running the project ***********
$make
$./runfile

the command line arguments for the consumer are as follows: (inside runfile)
	$./consumer NUMBER_OF_BUFFERS SHARED_BUFFER_SIZE USE_SEMAPHORE_S

NUMBER_OF_BUFFERS
	the test used values of 10, 20, 50, 100, 150 buffers in shared memory

SHARED_BUFFER_SIZE
	the test used value of 128b, 256b, 512b for the shared buffer size in shared memory

USE_SEMAPHORE_S
	this argument determines whether semaphore S is being used, 1 = YES/ON, 0 = NO/OFF

changing the input file size:
	simply change the contents of "input.txt"

The performance results are available in "performance testing.docx",
and the raw data is available in "performance testing graphs and tables.xlsx" and "stats.txt"
