#include <time.h>
#include <sys/time.h>
#include "DomStructures.h"

static int set_semvalue(int,int);
static void del_semvalue(int);
static int wait(int);
static int signal(int);


static int sem_id_S;
static int sem_id_E;
static int sem_id_N;

int main(int argc, char *argv[]){
	void *shared_memory = (void *)0;
	int shmid;
	struct shared_memory_st *shared_memory_inst;
	int output_file;
	int i = 0;
	int read_len;
	int write_count = 0;
	int NUMBER_OF_BUFFERS;
	int SHARED_BUFFER_SIZE;
	int SEMAPHORE_S;
	struct timeval t0;
	struct timeval t1;
	
	NUMBER_OF_BUFFERS = strtol(argv[1], NULL, 0);
	SHARED_BUFFER_SIZE = strtol(argv[2], NULL, 0);
	SEMAPHORE_S = strtol(argv[3], NULL, 0);
	char read_buffer[SHARED_BUFFER_SIZE];
	
//setup semaphore
	sem_id_N = semget((key_t)SEM_KEY_N, 1, 0666 | IPC_CREAT);
	if(!set_semvalue(sem_id_N, 0)) exit(EXIT_FAILURE);
	
	sem_id_S = semget((key_t)SEM_KEY_S, 1, 0666 | IPC_CREAT);
	if(!set_semvalue(sem_id_S, 1)) exit(EXIT_FAILURE);
	
	sem_id_E = semget((key_t)SEM_KEY_E, 1, 0666 | IPC_CREAT);
	if(!set_semvalue(sem_id_E, NUMBER_OF_BUFFERS)) exit(EXIT_FAILURE);
//finished semaphore setup

//setup shared memory	
	shmid = shmget((key_t)SHARE_MEM_KEY, sizeof(struct shared_memory_st), 0666 | IPC_CREAT);
	if (shmid == -1) {
		fprintf(stderr, "shmget failed\n");
		exit(EXIT_FAILURE);
	}
	
	shared_memory = shmat(shmid, (void *)0, 0);
	if (shared_memory == (void *)-1) {
		fprintf(stderr, "shmat failed\n");
		exit(EXIT_FAILURE);
	}
//finished shared mem setup

	if((output_file = open(OUTPUT_FILE, O_WRONLY | O_TRUNC)) < 0){
		fprintf(stderr, "open file failed\n");
		exit(EXIT_FAILURE);
	}

	shared_memory_inst = (struct shared_memory_st *)shared_memory;
	
	
	shared_memory_inst->num_bufs = NUMBER_OF_BUFFERS;
	shared_memory_inst->buf_size = SHARED_BUFFER_SIZE;
	shared_memory_inst->sem_s = SEMAPHORE_S;
	
	shared_memory_inst->ready = 0;
	while(!shared_memory_inst->ready);
	gettimeofday(&t0, NULL);
	
	do{
		if(!wait(sem_id_N)) exit(EXIT_FAILURE);
		if(SEMAPHORE_S)
			if(!wait(sem_id_S)) exit(EXIT_FAILURE);
		read_len = shared_memory_inst->buffers[i].count;
		strncpy(read_buffer, shared_memory_inst->buffers[i].str, read_len);	
		i = (i + 1) % NUMBER_OF_BUFFERS;
		if(SEMAPHORE_S)
			if(!signal(sem_id_S)) exit(EXIT_FAILURE);
		if(!signal(sem_id_E)) exit(EXIT_FAILURE);
		
		if(write(output_file, read_buffer, read_len) != read_len){
			fprintf(stderr, "write file failed\n");
			exit(EXIT_FAILURE);
		}
		write_count += read_len;
		
	}while(read_len == SHARED_BUFFER_SIZE);
	
	gettimeofday(&t1, NULL);
	printf("\n%d, %d, %d, Bytes: %db\n", NUMBER_OF_BUFFERS, SHARED_BUFFER_SIZE, 
			SEMAPHORE_S, write_count);
	printf("Time: %dus\n", t1.tv_usec - t0.tv_usec);
	
	if (shmdt(shared_memory) == -1) {
		fprintf(stderr, "shmdt failed\n");
		exit(EXIT_FAILURE);
	}
	if (shmctl(shmid, IPC_RMID, 0) == -1) {
		fprintf(stderr, "shmctl(IPC_RMID) failed\n");
		exit(EXIT_FAILURE);
	}
	
	del_semvalue(sem_id_N);
	del_semvalue(sem_id_E);
	if(SEMAPHORE_S)
		del_semvalue(sem_id_S);
		
	exit(EXIT_SUCCESS);
}

static int set_semvalue(int sem_id, int count)
{
    union semun sem_union;

    sem_union.val = count;
    if (semctl(sem_id, 0, SETVAL, sem_union) == -1) return(0);
    return(1);
}

static void del_semvalue(int sem_id)
{
    union semun sem_union;
    
    if (semctl(sem_id, 0, IPC_RMID) == -1)
        fprintf(stderr, "Failed to delete semaphore\n");
}

static int wait(int sem_id)
{
    struct sembuf sem_b;
    
    sem_b.sem_num = 0;
    sem_b.sem_op = -1; /* P() */
    sem_b.sem_flg = 0;
    if (semop(sem_id, &sem_b, 1) == -1) {
        fprintf(stderr, "semaphore_p failed\n");
        return(0);
    }
    return(1);
}

static int signal(int sem_id)
{
    struct sembuf sem_b;
    
    sem_b.sem_num = 0;
    sem_b.sem_op = 1; /* V() */
    sem_b.sem_flg = 0;
    if (semop(sem_id, &sem_b, 1) == -1) {
        fprintf(stderr, "semaphore_v failed\n");
        return(0);
    }
    return(1);
}
