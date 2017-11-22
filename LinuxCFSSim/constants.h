#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <sys/time.h>
#include <time.h>

#define SCHED_FIFO 1
#define SCHED_RR 2
#define SCHED_NORMAL 3

#define NUM_PROCESSES 20
#define NUM_CONSUMERS 4

#define FIFO_RATIO 1
#define RR_RATIO 1
#define NORMAL_RATIO 3
#define RATIO_SUM FIFO_RATIO + RR_RATIO + NORMAL_RATIO

#define RR_PROCESS_COUNT NUM_PROCESSES * RR_RATIO / RATIO_SUM
#define FIFO_PROCESS_COUNT NUM_PROCESSES * FIFO_RATIO / RATIO_SUM
#define NORMAL_PROCESS_COUNT NUM_PROCESSES * NORMAL_RATIO / RATIO_SUM

#define FIFO_RR_PRIOR_RANGE 100
#define NORMAL_DEFAULT_PRIO 120

#define MAX_EXEC_TIME 3000
#define MIN_EXEC_TIME 300

#define TICKS_PER_MSEC 100
#define MAX_SLEEP_AVERAGE 10

struct task_struct {
	
	int PID;
	
	int static_prio;
	//int dynamic_prio;
	
	int exec_time;
	int accu_time_slice;
	int time_slice;
	
	int sleep_avg;
	
	int sched_type;
	
	int queue_insert_time;
	int admit_time;
	
};


struct Q {
	
	int count;
	struct task_struct *tasks[NUM_PROCESSES * 3 / 5 + 1];
	
};

struct Queues {
	
	struct Q RQ0;
	struct Q RQ1;
	struct Q RQ2;
	
};
