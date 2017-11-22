#define MAX_TEXT 512
#define SERVER_FIFO_NAME "/tmp/serv_fifo"
#define CLIENT_FIFO_NAME "/tmp/cli_%d_fifo"
#define SERVER_FIFO_NAME2 "/tmp/serv_fifo2"
#define CLIENT_FIFO_NAME2 "/tmp/cli_%d_fifo2"

enum DeviceType{
	actuatorAC = 7,
	actuatorDehumidifier = 8,
	sensorThermometer = 9,
	sensorHygrometer = 10
}deviceType;

struct device {
	long int msg_type;
	struct device_info {
		pid_t pid;
		char name [25];
		enum DeviceType device_type;
		int threshold;
		int current_value;
		int state;
		int command;
	}device_info;
};

struct cloud_data {
	pid_t client_pid;
	struct device device;
};
