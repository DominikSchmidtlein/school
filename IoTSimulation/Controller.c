#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include "Structures.h"
#include <signal.h>
#include <sys/msg.h>
#include <sys/types.h>
#include <ctype.h>
#include <fcntl.h>
#include <limits.h>
#include <sys/stat.h>
#include <pthread.h>


/*
	child communicates with device processes
*/
static int msgid;
static int cloud_fifo_fd, parent_fifo_fd;
static long int parent_msg_to_receive;
static struct device parentDevice;
static struct cloud_data cloud_request;
static char parent_fifo[256];

void signalParent(struct device tempDev){
	tempDev.msg_type = (long int)getppid();
	if(msgsnd(msgid, (void *)&tempDev, MAX_TEXT, 0) == -1) {
		fprintf(stderr, "msgsnd failed\n");
		exit(EXIT_FAILURE);
	}
	printf("Signal parent about threshold crossing.\n");
	kill(getppid(), SIGALRM);
}

void keyboard_interrupt(int s){
	printf("got keyboard interrupt\n");
	close(cloud_fifo_fd);
    unlink(parent_fifo);
    if(msgctl(msgid, IPC_RMID, 0) == -1){
   			fprintf(stderr, "msgctl(IPC_RMID) failed\n");
   			exit(EXIT_FAILURE);
	}
    exit(EXIT_SUCCESS);
    
}

void signalListener(int sig){
	if(msgrcv(msgid, (void *)&parentDevice, BUFSIZ,
      	 parent_msg_to_receive, 0) == -1) {
		fprintf(stderr, "msgrcv failed with error: %d\n", errno);
		exit(EXIT_FAILURE);
	}
	/*printf("parent:name: %s\n", parentDevice.device_info.name);
	printf("parent:PID: %d\n",(long int)parentDevice.device_info.pid);
	printf("parent:Current value: %d\n", parentDevice.device_info.current_value);
	printf("parent:action: on\n");*/
	
}

int getDeviceByType(enum DeviceType type, int number, struct device *devicess){
	int i;
	for(i = 0; i < number; i ++){		
		if(devicess[i].device_info.device_type == type){
			return i;
		}
	}
	return -1;
}

void* forwardMessageToChild(void *arg){	
	int cloud_fifo_fd2, parent_fifo_fd2;
	struct cloud_data cloud_data2;
	struct device temp_device2;
	int read_res2;
	
	mkfifo(SERVER_FIFO_NAME2, 0777);
	cloud_fifo_fd2 = open(SERVER_FIFO_NAME2, O_RDONLY);
	if (cloud_fifo_fd2 == -1) {
		fprintf(stderr, "Server fifo failure\n");
		exit(EXIT_FAILURE);
    }
	while(1){
		read_res2 = read(cloud_fifo_fd2, &cloud_data2, sizeof(cloud_data2));
    	if(read_res2 > 0){
    		//printf("parent thread: command: %s\n", cloud_data2.device.device_info.name);
    		printf("Parent says: forwarding command to child.\n");
    		temp_device2 = cloud_data2.device;
    		temp_device2.msg_type = 1;
    		temp_device2.device_info.command = 1;
    		if(msgsnd(msgid, (void *)&temp_device2, MAX_TEXT, 0) == -1) {
				fprintf(stderr, "msgsnd failed\n");
				exit(EXIT_FAILURE);
			}
		}
    	
    }
    return NULL;
}

int main()
{   
	signal(SIGINT, keyboard_interrupt);
    char name[BUFSIZ];
	printf("Enter a name for controller: ");
	fgets(name, BUFSIZ, stdin);
	
	msgid = msgget((key_t)1236, 0666 | IPC_CREAT);
	if (msgid == -1) {
	    fprintf(stderr, "msgget failed with error: %d\n", errno);
	    exit(EXIT_FAILURE);
	}
	
	pid_t new_pid;
	new_pid = fork();
	
	if(new_pid == 0){
		//child
		
		long int msg_to_receive = 1;
		struct device *devices = (struct device *)malloc(4*sizeof(struct device));
		int numberOfDevices = 0;
    	struct device device;
    	struct device ackMessage; //(struct device *)malloc(sizeof(struct device));
		char temp[256];
		//create msg queue
		
		while(1){
			//printf("ID: %d\n", msgid);
			if(msgrcv(msgid, (void *)&device, BUFSIZ,
              	 msg_to_receive, 0) == -1) {
        		fprintf(stderr, "msgrcv failed with error: %d\n", errno);
        		exit(EXIT_FAILURE);
    		}
//is message a command from cloud
    		if(device.device_info.command == 1){
				strcpy(name,device.device_info.name);
				if(!strncmp(name,"put AC\0",6)){
					int index = getDeviceByType(actuatorAC, numberOfDevices, devices);
					if(index != -1){
						ackMessage.msg_type = (long int)devices[index].device_info.pid;
						ackMessage.device_info.state = 1;
						if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
							fprintf(stderr, "msgsnd failed\n");
							exit(EXIT_FAILURE);
						}
					}
				}else if(!strncmp(name,"put Dehumidifier\0",16)){
					int index = getDeviceByType(actuatorDehumidifier, numberOfDevices, devices);
					if(index != -1){
						ackMessage.msg_type = (long int)devices[index].device_info.pid;
						ackMessage.device_info.state = 1;
						if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
							fprintf(stderr, "msgsnd failed\n");
							exit(EXIT_FAILURE);
						}
					}
				}else if(!strncmp(name,"get Thermometer\0",15)){
					int index = getDeviceByType(sensorThermometer, numberOfDevices, devices);
					if(index != -1){
						signalParent(devices[index]);
					}					
				}else if(!strncmp(name,"get Hygrometer\0",14)){
					int index = getDeviceByType(sensorHygrometer, numberOfDevices, devices);
					if(index != -1){
						signalParent(devices[index]);
					}
				}
				continue;
    		}
    		    		
//check if device is known and add new devices to array
			int i;
			int contains = 0;
			
			//printf("number of devices: %d\n", numberOfDevices);
			for(i = 0; i < numberOfDevices; i ++){
				//printf("array pid: %d\n",(long int)devices[i].device_info.pid);
				//printf("pid of messenger: %d\n",(long int)device.device_info.pid);			
				if(devices[i].device_info.pid == device.device_info.pid){
					contains = 1;
					devices[i].device_info.current_value = device.device_info.current_value;
					//printf("already know the device\n");
					break;
				}
			}
			if(!contains){
				devices[numberOfDevices] = device;
				numberOfDevices +=1;
				//printf("new device\n");
/* send ACK*/		
				//printf("prep set msg type\n");		
				ackMessage.msg_type = (long int)device.device_info.pid;
				//printf("set msg type\n");
				if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
					fprintf(stderr, "msgsnd failed\n");
					exit(EXIT_FAILURE);
				}	
				//printf("sent ack\n");
				continue;
			}
//////////////////////////////////////////////////////	
			
			if(device.device_info.device_type == sensorHygrometer || device.device_info.device_type == sensorThermometer){
//readings below thresh
				if(device.device_info.current_value <= device.device_info.threshold){
					//printf("Sensor reading is ok, turning off actuator.\n");
					if(device.device_info.device_type == sensorHygrometer){
				//turn off dehumidifier
						for(i = 0; i < numberOfDevices; i ++){
							if(devices[i].device_info.device_type == actuatorDehumidifier){
								//if(!devices[i].device_info.state){
								//	break;
								//}
								//devices[i].device_info.state = 0;
								ackMessage.msg_type = (long int)devices[i].device_info.pid;
								ackMessage.device_info.state = 0;
								if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
									fprintf(stderr, "msgsnd failed\n");
									exit(EXIT_FAILURE);
								}
								printf("PID: %d -> Sensor reading acceptable, turning off dehumidifier.\n",(long int)device.device_info.pid);
								break;
							}
						}
					
					}else{
					//turn off AC
						for(i = 0; i < numberOfDevices; i ++){
							if(devices[i].device_info.device_type == actuatorAC){
								//if(!devices[i].device_info.state){
								//	break;
								//}
								//devices[i].device_info.state = 0;
								ackMessage.msg_type = (long int)devices[i].device_info.pid;
								ackMessage.device_info.state = 0;
								if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
									fprintf(stderr, "msgsnd failed\n");
									exit(EXIT_FAILURE);
								}
								printf("PID: %d -> Sensor reading acceptable, turning off dehumidifier.\n",(long int)device.device_info.pid);
								break;
							}
						}
					}
					continue; //no problem					
				}
//reading above thresh
				signalParent(device);
				if(device.device_info.device_type == sensorHygrometer){
				//turn on dehumidifier
					printf("PID: %d -> Threshold (%d) crossed, turning on Dehumidifier. Current: %d\n",(long int)device.device_info.pid,device.device_info.threshold,device.device_info.current_value);
					for(i = 0; i < numberOfDevices; i ++){
						if(devices[i].device_info.device_type == actuatorDehumidifier){
							ackMessage.msg_type = (long int)devices[i].device_info.pid;
							ackMessage.device_info.state = 1;
							if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
								fprintf(stderr, "msgsnd failed\n");
								exit(EXIT_FAILURE);
							}
							break;
						}
					}
					
				}else{
				//turn on AC
					printf("PID: %d -> Threshold (%d) crossed, turning on Dehumidifier. Current: %d\n", (long int)device.device_info.pid, device.device_info.threshold, device.device_info.current_value);
					for(i = 0; i < numberOfDevices; i ++){
						if(devices[i].device_info.device_type == actuatorAC){
							ackMessage.msg_type = (long int)devices[i].device_info.pid;
							ackMessage.device_info.state = 1;
							if(msgsnd(msgid, (void *)&ackMessage, MAX_TEXT, 0) == -1) {
								fprintf(stderr, "msgsnd failed\n");
								exit(EXIT_FAILURE);
							}
							break;
						}
					}
				}
			}
			else if(device.device_info.device_type == actuatorAC || device.device_info.device_type == actuatorDehumidifier){
				//printf("actuator ack message\n");
			}
			else{
				fprintf(stderr, "invalid device type\n");
			}		
			
			//int j;
			//for(j = 0; j < numberOfDevices; j++){
			//	printf("PID: %d\n",(long int)devices[j].device_info.pid);
    		//	printf("Name: %s\n", devices[j].device_info.name);
			//}
    		
    		//printf("Name: %s\n", devices[0].device_info.name);
	   		//printf("Type: %c\n", devices[0].device_info.device_type);
    		
    		/*int contains = 0;
    		int i;
    		for(i = 0; i < numberOfDevices; i++){
    			if(devices[i].device_info.pid == device.device_info.pid){
    				contains = 1;
    				break;
    			}
    		}*/ 

			  		
		}
		if(msgctl(msgid, IPC_RMID, 0) == -1){
	   			fprintf(stderr, "msgctl(IPC_RMID) failed\n");
	   			exit(EXIT_FAILURE);
		}
		
	}
	else{
		//parent
		//printf("%d\n", new_pid);
		
    	struct cloud_data cloud_data;
    	
    	pthread_t tid;
    	
    	cloud_fifo_fd = open(SERVER_FIFO_NAME, O_WRONLY);
    	if (cloud_fifo_fd == -1) {
		    fprintf(stderr, "Sorry, no server\n");
		    exit(EXIT_FAILURE);
		}
		cloud_data.client_pid = getpid();
    	sprintf(parent_fifo, CLIENT_FIFO_NAME, cloud_data.client_pid);
    	if (mkfifo(parent_fifo, 0777) == -1) {
		    fprintf(stderr, "Sorry, can't make %s\n", parent_fifo);
		    exit(EXIT_FAILURE);
		}
		
		parent_msg_to_receive = (long int)getpid();
		
//set up thread for forwarding commands from cloud to child
		if(pthread_create(&tid, NULL, &forwardMessageToChild, NULL) != 0){
			printf("can't create thread\n");
			exit(EXIT_FAILURE);
		}
		//printf("thread created\n");
//subscribe to signal and set 		
		(void) signal(SIGALRM, signalListener);

		
		while(1){
			//printf("wait for signal\n");
			pause();
			//printf("got signal\n");
			cloud_data.device = parentDevice;
			write(cloud_fifo_fd, &cloud_data, sizeof(cloud_data));
		}
	}
	
    exit(EXIT_SUCCESS);
/* Get name and threshold of device */    
	/*if (msgrcv(msgid, (void *)&some_data, BUFSIZ,
               msg_to_receive, 0) == -1) {
        fprintf(stderr, "msgrcv failed with error: %d\n", errno);
        exit(EXIT_FAILURE);
    }
    printf("%s: %d",some_data.info.threshold, some_data.info.name);

	exit(EXIT_SUCCESS);
	
    while(running) {
        printf("Enter some text: ");
        fgets(buffer, BUFSIZ, stdin);
        some_data.my_msg_type = 1;
        strcpy(some_data.some_text, buffer);

        if (msgsnd(msgid, (void *)&some_data, MAX_TEXT, 0) == -1) {
            fprintf(stderr, "msgsnd failed\n");
            exit(EXIT_FAILURE);
        }
        if (strncmp(buffer, "end", 3) == 0) {
            running = 0;
        }
    }

    exit(EXIT_SUCCESS);*/
}




