#include "constants.h"

void *producer(void *arg);
void *consumer(void *arg);
void *balancer(void *arg);

int max(int a, int b);
int min(int a, int b);

int getSumOfQueues(struct Queues *queues);
struct task_struct * getHighestPriority(struct Q *queue);
struct task_struct * popFromQueue(struct Q *queue, int index);
void addToQueue(struct Q *queue, struct task_struct *task);


int consumer_index[NUM_CONSUMERS] = {0,1,2,3};

pthread_mutex_t queue_mutexes[NUM_CONSUMERS];


struct Queues consumers[NUM_CONSUMERS];

int main() {
	int res;
	int thread_num;
	
	pthread_t threads[NUM_CONSUMERS + 2];
	
	void *thread_result;
	
	for(thread_num = 0; thread_num < NUM_CONSUMERS; thread_num ++){
	
		if (pthread_mutex_init(&queue_mutexes[thread_num], NULL) != 0) {
			perror("Mutex initialization failed");
			exit(EXIT_FAILURE);
		}
		
	}
	
	
	for(thread_num = 0; thread_num < NUM_CONSUMERS + 2; thread_num ++) {
		if(thread_num == 0) {
			if(pthread_create(&(threads[thread_num]), NULL, producer, (void *)&thread_num) != 0) {
				perror("Thread creation failed");
				exit(EXIT_FAILURE);
			}
		} 
		else if(thread_num == NUM_CONSUMERS + 1){
			if(pthread_create(&(threads[thread_num]), NULL, balancer, (void *)&thread_num) != 0) {
				perror("Thread creation failed");
				exit(EXIT_FAILURE);
			}
		}
		else {
			if(pthread_create(&(threads[thread_num]), NULL, consumer, (void *)&consumer_index[thread_num - 1]) != 0) {
				perror("Thread creation failed");
				exit(EXIT_FAILURE);
			}
		}
	}
	
	for(thread_num = NUM_CONSUMERS + 1; thread_num >= 0; thread_num--) {
        res = pthread_join(threads[thread_num], &thread_result);
        if (res == 0) {
            printf("Picked up a thread\n");
        }
        else {
            perror("pthread_join failed");
        }
    }
    
    /*int i;
    int j;
    for(i = 0; i < NUM_CONSUMERS; i ++){
		printf("Number of processes in consumer%d is %d\n", i + 1, consumers[i].count);
		for(j = 0; j < consumers[i].RQ0.count; j ++){
			printf("RQ0: %d\n", consumers[i].RQ0.tasks[j]->sched_type);
		}
		for(j = 0; j < consumers[i].RQ1.count; j ++){
			printf("RQ1: %d\n", consumers[i].RQ1.tasks[j]->sched_type);
		}
		for(j = 0; j < consumers[i].RQ2.count; j ++){
			printf("RQ2: %d\n", consumers[i].RQ2.tasks[j]->sched_type);
		}
	}*/
	
	printf("Done\n");
	exit(EXIT_SUCCESS);
}

void *producer(void *arg) {
	
	int rr_count = 4;
	int fifo_count = 4;
	int normal_count = 12;
	
	int rand_int;
	int proc_type;
	int SP;
	
	struct timeval insert_time;
	
	int i;
	for(i = 0; i < NUM_PROCESSES; i++) {
		
		struct task_struct *task = (struct task_struct *) malloc(sizeof(struct task_struct));
		
		task->PID = i + 1000;
		
		rand_int = rand();
		proc_type = rand_int % (rr_count + fifo_count + normal_count);
		
		task->static_prio = rand_int % FIFO_RR_PRIOR_RANGE;
		
		task->exec_time = rand_int % (MAX_EXEC_TIME - MIN_EXEC_TIME) + MIN_EXEC_TIME;
		task->accu_time_slice = 0;
		gettimeofday(&insert_time, NULL);
		task->admit_time = insert_time.tv_sec * 1000000 + insert_time.tv_usec;
		
		if(proc_type < rr_count) {
			task->sched_type = SCHED_RR;
			
			rr_count--;
		}
		else if(proc_type < rr_count + fifo_count) {
			task->sched_type = SCHED_FIFO;
			
			fifo_count--;
		}
		else {
			task->sched_type = SCHED_NORMAL;
			task->static_prio = NORMAL_DEFAULT_PRIO;
			
			task->sleep_avg = 0;
			
			task->queue_insert_time = insert_time.tv_sec * 1000000 + insert_time.tv_usec;
			
			normal_count--;
		}
		
		
		SP = task->static_prio;
		task->time_slice = (SP < 120) ? (140 - SP) * 20 : (140 - SP) * 5;
		
		if(pthread_mutex_lock(&queue_mutexes[i % NUM_CONSUMERS]) != 0){
			perror("Mutex lock failed");
			exit(EXIT_FAILURE);
		}
		
		if(task->static_prio != NORMAL_DEFAULT_PRIO){
			consumers[i % NUM_CONSUMERS].RQ0.tasks[
					consumers[i % NUM_CONSUMERS].RQ0.count] = task;
			consumers[i % NUM_CONSUMERS].RQ0.count += 1;
		}
		else {
			consumers[i % NUM_CONSUMERS].RQ1.tasks[
					consumers[i % NUM_CONSUMERS].RQ1.count] = task;
			consumers[i % NUM_CONSUMERS].RQ1.count += 1;
		}
		
		if(pthread_mutex_unlock(&queue_mutexes[i % NUM_CONSUMERS]) != 0){
			perror("Mutex unlock failed");
			exit(EXIT_FAILURE);
		}
		
	}
	
	pthread_exit(NULL);
}

void *consumer(void *arg) {
	int my_index = *(int *)arg;
	
	int service_time;
	int blocking_time;
	int remaining_exec_time;
	
	struct task_struct *task;
	struct timeval current_time;
	
	printf("my index: %d\n", my_index);
	
	while(1){
		while(consumers[my_index].RQ0.count + 
			consumers[my_index].RQ1.count + 
			consumers[my_index].RQ2.count == 0);
		
		//select highest priority process in my queues
		if(pthread_mutex_lock(&queue_mutexes[my_index]) != 0){
			perror("Mutex lock failed");
			exit(EXIT_FAILURE);
		}
		
		task = getHighestPriority(&consumers[my_index].RQ0);
		if(task == NULL)
			task = getHighestPriority(&consumers[my_index].RQ1);
		if(task == NULL)
			task = getHighestPriority(&consumers[my_index].RQ2);
		if(task == NULL)
			continue;
			
		if(pthread_mutex_unlock(&queue_mutexes[my_index]) != 0){
			perror("Mutex unlock failed");
			exit(EXIT_FAILURE);
		}
		
		//printf("Consumer: %d, Priority: %d\n", my_index, task->static_prio);
		remaining_exec_time = task->exec_time - task->accu_time_slice;
		
		blocking_time = 0;
		if(task->sched_type == SCHED_FIFO){
			
			service_time = task->exec_time;
			
		}
		else if(task->sched_type == SCHED_RR){
			
			service_time = task->time_slice;
			
		}
		else if(task->sched_type == SCHED_NORMAL){
			//update sleep_avg
			gettimeofday(&current_time, NULL);
			task->sleep_avg += (current_time.tv_sec * 1000000 + 
				current_time.tv_usec - task->queue_insert_time) / 1000 / TICKS_PER_MSEC;
			if(task->sleep_avg > MAX_SLEEP_AVERAGE)
				task->sleep_avg = MAX_SLEEP_AVERAGE;
			
			//consider possible IO block
			service_time = task->time_slice;
			if(service_time > 10 && rand() % 2) {
				service_time = 10; 
				if(service_time < remaining_exec_time)
					blocking_time = 1200;
			}
		}		
		
		service_time = (service_time > remaining_exec_time) ?
			remaining_exec_time : service_time;
			
		
		usleep((service_time + blocking_time) * 1000);
		
		printf("\nPID: %d\n", task->PID);
		printf("Priority: %d\n", task->static_prio);
		printf("Init Execution Time: %d\n", task->exec_time);
		printf("Accumulated Time: %d\n", task->accu_time_slice);
		printf("Service Time: %d\n", service_time);
		
		gettimeofday(&current_time, NULL);
		
		if(task->sched_type == SCHED_NORMAL){
			//update sleep_avg
			task->sleep_avg -= service_time/TICKS_PER_MSEC;
			if(task->sleep_avg < 0)
				task->sleep_avg = 0;
			//calculate dynamic priority
			task->static_prio = max(100, min(task->static_prio 
				- task->sleep_avg + 5, 139));
			//calculate time_slice
			task->time_slice = (task->static_prio < 120) ? 
				(140 - task->static_prio) * 20 : (140 - task->static_prio) * 5;
			//update queue_insert_time			
			task->queue_insert_time = current_time.tv_sec * 1000000 + current_time.tv_usec;
		}
		
		task->accu_time_slice += service_time;
		
		if(task->accu_time_slice < task->exec_time){
			if(pthread_mutex_lock(&queue_mutexes[my_index]) != 0){
				perror("Mutex lock failed");
				exit(EXIT_FAILURE);
			}
			
			if(task->static_prio < 100){
				addToQueue(&consumers[my_index].RQ0, task);
			}
			else if(task->static_prio < 130){
				addToQueue(&consumers[my_index].RQ1, task);
			}
			else {
				addToQueue(&consumers[my_index].RQ2, task);
			}
			
			if(pthread_mutex_unlock(&queue_mutexes[my_index]) != 0){
				perror("Mutex unlock failed");
				exit(EXIT_FAILURE);
			}
		}
		else {
			printf("TAT: %f\n", (current_time.tv_sec * 1000000 + current_time.tv_usec - task->admit_time)/1000000.0);
			printf("							Done, processor: %d, count: %d\n", my_index, getSumOfQueues(&consumers[my_index]));
		}
		
	}
	
	pthread_exit(NULL);
}

void *balancer(void *arg){
	
	struct Q * max_queue;
	struct Queues * min_queues;
	int max;
	int min;
	int count;
	int i;
	
	while(1){
		max = -1;
		min = NUM_PROCESSES;
		//find max and min queues
		for(i = 0; i < NUM_CONSUMERS; i ++){
			if(pthread_mutex_lock(&queue_mutexes[i]) != 0){
				perror("Mutex lock failed");
				exit(EXIT_FAILURE);
			}
			
			count = getSumOfQueues(&consumers[i]);
			
			if(count > max){
				if(consumers[i].RQ0.count > 0){
					max_queue = &consumers[i].RQ0;
				}
				else if(consumers[i].RQ1.count > 0) {
					max_queue = &consumers[i].RQ1;
				}
				else if(consumers[i].RQ2.count > 0){
					max_queue = &consumers[i].RQ2;
				}
				max = count;
			}
			
			if(count < min){
				min_queues = &consumers[i];
				min = count;
			}
			
			if(pthread_mutex_unlock(&queue_mutexes[i]) != 0){
				perror("Mutex unlock failed");
				exit(EXIT_FAILURE);
			}
			
		}
		//printf("%d\n", abs(max - min));
		if(max - min < 2){
			usleep(200000);
			continue;
		}
		
		for(i = 0; i < NUM_CONSUMERS; i ++){
			if(pthread_mutex_lock(&queue_mutexes[i]) != 0){
				perror("Mutex lock failed");
				exit(EXIT_FAILURE);
			}
		}
		
		if(max_queue->tasks[max_queue->count - 1]->static_prio < 100){
			min_queues->RQ0.tasks[min_queues->RQ0.count] = 
				max_queue->tasks[max_queue->count - 1];
				
			min_queues->RQ0.count ++;
		}
		else if(max_queue->tasks[max_queue->count - 1]->static_prio < 130){
			min_queues->RQ1.tasks[min_queues->RQ1.count] = 
				max_queue->tasks[max_queue->count - 1];
				
			min_queues->RQ1.count ++;
		}
		else {
			min_queues->RQ2.tasks[min_queues->RQ2.count] = 
				max_queue->tasks[max_queue->count - 1];
				
			min_queues->RQ2.count ++;
		}
		
		max_queue->tasks[max_queue->count - 1] = NULL;
		max_queue->count --;
		
		for(i = 0; i < NUM_CONSUMERS; i ++){
			if(pthread_mutex_unlock(&queue_mutexes[i]) != 0){
				perror("Mutex unlock failed");
				exit(EXIT_FAILURE);
			}
		}
		
		printf("Balancing...\n");
	
	}
	
	pthread_exit(NULL);
}

int getSumOfQueues(struct Queues *queues){
	return queues->RQ0.count + queues->RQ1.count + queues->RQ2.count;
}

struct task_struct * getHighestPriority(struct Q *queue){
	if(queue->count == 0)
		return NULL;
	
	int index = 0;
	int i;
	
	for(i = 1; i < queue->count; i++)
		if(queue->tasks[i]->static_prio < queue->tasks[index]->static_prio)
			index = i;
	
	return popFromQueue(queue, index);
}

struct task_struct * popFromQueue(struct Q *queue, int index){
	struct task_struct *task = queue->tasks[index];
	queue->tasks[index] = queue->tasks[queue->count-1];
	queue->tasks[queue->count-1] = NULL;
	queue->count --;
	return task;
}

void addToQueue(struct Q *queue, struct task_struct *task){
	queue->tasks[queue->count] = task;
	queue->count ++;
}

int max(int a, int b){
	return (a > b) ? a : b;
}

int min(int a, int b) {
	return (a < b) ? a : b;
}



