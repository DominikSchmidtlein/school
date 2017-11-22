Producer-Consumer
The producer reads from a file and writes to a circular array of buffers in shared memory. The consumer simultaneously reads from the buffers in shared memory and writes to an output file. 
The challenge is to synchronize the processes such that:
	1. the producer and consumer do not try and access the shared memory at the same time, creates unpredictable behavior
	2. the producer does not overlap the consumer, otherwise the producer will start overwriting data that the consumer has not yet copied
	3. the consumer does not pass the producer, otherwise the consumer will read old or meaningless data

running the file:
	$make
*******order is crucial******
in one terminal
	./consumer
in another terminal
	./producer

the number of bytes written and read are displayed on the two terminals

the input file is called "input.txt"
the output file is called "output.txt"
	the output file must be already made when the program is run

FOR BONUS****

see README.md inside "performance-testing" directory
