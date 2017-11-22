#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <sys/types.h>
#include <sys/stat.h>


#define NUMBER_OF_BUFFERS 100
#define SHARED_BUFFER_SIZE 128
#define SEM_KEY_S 5555
#define SEM_KEY_N 6666
#define SEM_KEY_E 7777
#define SHARE_MEM_KEY 1237
#define SEMAPHORE_S 1

#define INPUT_FILE "input.txt"
#define OUTPUT_FILE "output.txt"


struct buffer_instance {
	int count;
	char str[SHARED_BUFFER_SIZE];
};

struct shared_memory_st {
	int ready;
	struct buffer_instance buffers[NUMBER_OF_BUFFERS];
};

union semun {
	int val;                    /* value for SETVAL */
	struct semid_ds *buf;       /* buffer for IPC_STAT, IPC_SET */
	unsigned short int *array;  /* array for GETALL, SETALL */
	struct seminfo *__buf;      /* buffer for IPC_INFO */
};
