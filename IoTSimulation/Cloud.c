#include <ctype.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <limits.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>
#include "Structures.h"
#include <pthread.h>

static int cloud_fifo_fd, parent_fifo_fd;
static char input[256];
pthread_t tid;

void keyboard_interrupt(int s){
	close(cloud_fifo_fd);
    unlink(SERVER_FIFO_NAME);
    exit(EXIT_SUCCESS);	
}

void* pollInput(void *arg){
	int cloud_fifo_fd2, parent_fifo_fd2;
	struct cloud_data cloud_data2;
	char parent_fifo2[256];
	sleep(0.5);
	cloud_fifo_fd2 = open(SERVER_FIFO_NAME2, O_WRONLY);
	if (cloud_fifo_fd2 == -1) {
	    fprintf(stderr, "Sorry, no server\n");
	    exit(EXIT_FAILURE);
	}
	cloud_data2.client_pid = getpid();
	sprintf(parent_fifo2, CLIENT_FIFO_NAME2, cloud_data2.client_pid);
	if (mkfifo(parent_fifo2, 0777) == -1) {
	    fprintf(stderr, "Sorry, can't make %s\n", parent_fifo2);
	    exit(EXIT_FAILURE);
	}

	while(1){
		printf("Enter command: ");
		fgets(input, BUFSIZ, stdin);
		strcpy(cloud_data2.device.device_info.name, input);
		write(cloud_fifo_fd2, &cloud_data2, sizeof(cloud_data2));		
	}
	return NULL;
}

int main(){
	signal(SIGINT, keyboard_interrupt);
	
	struct cloud_data cloud_data;
	int read_res;
	char parent_fifo[256];
    int err;
    
	
	mkfifo(SERVER_FIFO_NAME, 0777);
	cloud_fifo_fd = open(SERVER_FIFO_NAME, O_RDONLY);
	if (cloud_fifo_fd == -1) {
        fprintf(stderr, "Server fifo failure\n");
        exit(EXIT_FAILURE);
    }
    
    //strcpy(input, "");
    
//create fgets thread
	err = pthread_create(&tid, NULL, &pollInput, NULL);
	if(err != 0){
		printf("can't create thread\n");
	}else{
	}
    
    while(1){
    	read_res = read(cloud_fifo_fd, &cloud_data, sizeof(cloud_data));
    	if(read_res > 0){
		if(cloud_data.device.device_info.command == 1){
			printf("\nMost recent reading for %s.\n", cloud_data.device.device_info.name);
		}else{
	    		printf("\n%s was turned on.\n", cloud_data.device.device_info.name);
		}
		if(cloud_data.device.device_info.device_type == sensorHygrometer){
			printf("Type: Hygrometer\n");
		}else if(cloud_data.device.device_info.device_type == sensorThermometer){
			printf("Type: Thermometer\n");
		}
    		printf("Current value: %d\n", cloud_data.device.device_info.current_value);
    	}
	}
	
	
	close(cloud_fifo_fd);
    unlink(SERVER_FIFO_NAME);
    exit(EXIT_SUCCESS);	
}
