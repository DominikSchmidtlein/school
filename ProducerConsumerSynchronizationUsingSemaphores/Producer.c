#include "DomStructures.h"

static int set_semvalue(int,int);
static void del_semvalue(int);
static int wait(int);
static int signal(int);

static int sem_id_S;
static int sem_id_E;
static int sem_id_N;

int main(){
	void *shared_memory = (void *)0;
	int shmid;
	struct shared_memory_st *shared_memory_inst;
	int input_file;
	int len;
	char buf[BUFSIZ + 1];
	char share_buffer[SHARED_BUFFER_SIZE];
	int i = 0;
	int j;
	int share_len;
	int bytes_read = 0;
	
	
	
//setup semaphores
	sem_id_E = semget((key_t)SEM_KEY_E, 1, 0666);
	sem_id_N = semget((key_t)SEM_KEY_N, 1, 0666);
	sem_id_S = semget((key_t)SEM_KEY_S, 1, 0666);
//finished semaphores setup

//setup shared memory
	shmid = shmget((key_t)SHARE_MEM_KEY, sizeof(struct shared_memory_st), 0666);
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

	if((input_file = open(INPUT_FILE, O_RDONLY)) < 0){
		fprintf(stderr, "open file failed\n");
		exit(EXIT_FAILURE);
	}
	
	shared_memory_inst = (struct shared_memory_st *)shared_memory;
	shared_memory_inst->ready = 1;
	
	do{
		if((len = read(input_file, buf, BUFSIZ)) < 0){
			fprintf(stderr, "read file failed\n");
			exit(EXIT_FAILURE);
		}
		buf[BUFSIZ] = '\0';
		bytes_read += len;
		for(j = len; j > 0; j -= SHARED_BUFFER_SIZE){
			share_len = (j < SHARED_BUFFER_SIZE) ? j : SHARED_BUFFER_SIZE;
			strncpy(share_buffer, (char *) &buf[len - j], share_len);
		
			if(!wait(sem_id_E)) exit(EXIT_FAILURE);
			if(SEMAPHORE_S)
				if(!wait(sem_id_S)) exit(EXIT_FAILURE);
			
			strncpy(shared_memory_inst->buffers[i].str, share_buffer, share_len);
			shared_memory_inst->buffers[i].count = share_len;
			i = (i + 1) % NUMBER_OF_BUFFERS;
			if(SEMAPHORE_S)
				if(!signal(sem_id_S)) exit(EXIT_FAILURE);
			if(!signal(sem_id_N)) exit(EXIT_FAILURE);
		}
	}while(len == BUFSIZ);
	
	printf("Bytes written: %db\n", bytes_read);
	
	
	if (shmdt(shared_memory) == -1) {
		fprintf(stderr, "shmdt failed\n");
		exit(EXIT_FAILURE);
	}
	
	
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
