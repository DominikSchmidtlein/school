#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include "Structures.h"

#include <sys/msg.h>

void checkForStopMessage(int msgQid){
	struct device stopMessage;
	if(msgrcv(msgQid, (void *)&stopMessage, BUFSIZ, (long int)getpid(), IPC_NOWAIT) == -1) {
		//fprintf(stderr, "msgrcv failed with error: %d\n", errno);
		//exit(EXIT_FAILURE);
		return;
	}else if(!strcmp(stopMessage.device_info.name, "stop")){
		printf("stop\n");
		exit(EXIT_SUCCESS);
	}
}

int main(int argc, char *argv[])
{
	if(argc < 3){
		exit(EXIT_FAILURE);
	}
	char *device_name = argv[1];
	char *type = argv[2];
	int threshold;
	if(!strcmp(type, "Thermometer") || !strcmp(type, "Hygrometer")){
		threshold = (int)strtol(argv[3],NULL,10);
	}
	long int msg_to_receive = (long int)getpid();
    static int msgid;
	char buffer[BUFSIZ];
	int running = 1;

	key_t key = 1234;
	
	struct device ackMessage;
    struct device device; 
	device.msg_type = 1;   
	device.device_info.pid = getpid();
	//printf("pid: %d\n",(int)getpid());
	//printf("pid: %d\n",(int)device.device_info.pid);
	strcpy(device.device_info.name, device_name);
	device.device_info.threshold = threshold;
	device.device_info.current_value = 5;
	device.device_info.state = 0;
	device.device_info.command = 0;
	if(!strcmp(type, "AC")){
		device.device_info.device_type = actuatorAC;
		//printf("AC\n");
	}else if(!strcmp(type, "Dehumidifier")){
		device.device_info.device_type = actuatorDehumidifier;
		//printf("dehum\n");
	}else if(!strcmp(type, "Thermometer")){
		device.device_info.device_type = sensorThermometer;
		//printf("thermo\n");
	}else if(!strcmp(type, "Hygrometer")){
		device.device_info.device_type = sensorHygrometer;
		//printf("hygro\n");
	}else{
		printf("no valid device type\n");
		exit(EXIT_FAILURE);
	}

    

/* First, we set up the message queue. */

    msgid = msgget((key_t)1236, 0666);
	//printf("%d\n",msgid);
    if (msgid == -1) {
        fprintf(stderr, "msgget failed with error: %d\n", errno);
        exit(EXIT_FAILURE);
    }
    //printf("setup queue\n");
    //fgets(buffer, BUFSIZ, stdin);
/* Send device name and threshold value. */
	if (msgsnd(msgid, (void *)&device, MAX_TEXT, 0) == -1) {
	    fprintf(stderr, "msgsnd failed\n");
	    exit(EXIT_FAILURE);
	}
	//fgets(buffer, BUFSIZ, stdin);
/*wait for ACK*/    
 	if(msgrcv(msgid, (void *)&ackMessage, BUFSIZ, msg_to_receive, 0) == -1) {
		fprintf(stderr, "msgrcv failed with error: %d\n", errno);
		exit(EXIT_FAILURE);
	}
	printf("got ACK\n");

	if(device.device_info.device_type == sensorThermometer || device.device_info.device_type == sensorHygrometer){
		while(running) {		
			checkForStopMessage(msgid);
			//printf("Enter some text: ");
			//fgets(buffer, BUFSIZ, stdin);
			//if (strncmp(buffer, "stop", 3) == 0) {
			//	running = 0;
			//	break;
			//}
			//device.device_info.current_value = (int)strtol(buffer,NULL,10);
			device.device_info.current_value = rand() % 50;
			printf("Generated current value: %d\n", device.device_info.current_value);
			if (msgsnd(msgid, (void *)&device, MAX_TEXT, 0) == -1) {
				fprintf(stderr, "msgsnd failed\n");
				exit(EXIT_FAILURE);
			}
			sleep(2);
		}
	}else{
		while(running){
			if(msgrcv(msgid, (void *)&ackMessage, BUFSIZ, msg_to_receive, 0) == -1) {
				fprintf(stderr, "msgrcv failed with error: %d\n", errno);
				exit(EXIT_FAILURE);
			}
			if(!strcmp(ackMessage.device_info.name, "stop")){
				exit(EXIT_SUCCESS);
			}
			if(ackMessage.device_info.state){
				//device.device_info.state = 1;
				if(device.device_info.device_type == actuatorAC){					
					printf("****************AC on\n");
				}else{
					printf("Dehumidifier on\n");
				}
			}else{
				//if(device.device_info.state){
					//device.device_info.state = 0;
					if(device.device_info.device_type == actuatorAC){					
						printf("AC off\n");
					}else{
						printf("Dehumidifier off\n");
					}
				//}
			}
			//send ack
			//printf("msgid: %d\n", msgid);
			//printf("msg to receive: %d\n", msg_to_receive);
			if (msgsnd(msgid, (void *)&device, MAX_TEXT, 0) == -1) {
				fprintf(stderr, "msgsnd failed\n");
				exit(EXIT_FAILURE);
			}
			//printf("sent ack\n");
		}
	}
	
	//printf("sent message\n");
	
	/*char mess[BUFSIZ];
	printf("Enter a message: ");
	fgets(mess, BUFSIZ, stdin);
	
	strcpy(device.device_info.name, mess);*/

	exit(EXIT_SUCCESS);

/* Then the messages are retrieved from the queue, until an end message is encountered.
 Lastly, the message queue is deleted. */

    /*
    while(running) {
        if (msgrcv(msgid, (void *)&some_data, BUFSIZ,
                   msg_to_receive, 0) == -1) {
            fprintf(stderr, "msgrcv failed with error: %d\n", errno);
            exit(EXIT_FAILURE);
        }
        printf("You wrote: %s", some_data.some_text);
        if (strncmp(some_data.some_text, "stop", 3) == 0) {
            running = 0;
        }
    }

    if (msgctl(msgid, IPC_RMID, 0) == -1) {
        fprintf(stderr, "msgctl(IPC_RMID) failed\n");
        exit(EXIT_FAILURE);
    }

    exit(EXIT_SUCCESS);*/
}


