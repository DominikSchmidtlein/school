#Dominik Schmidtlein 100946295

running the program*********************
	1. run make file
	*** order important ***
	2. open terminal and run ./cloud
	3. open terminal and run ./controller
	4. enter a name into command prompt for controller
	for each desired sensor: (max 2)
		5. open terminal
		6. run "./device" with 3 arguments
			argument1 is name of device (no spaces)
			argument2 is type, either "Thermometer" or "Hygrometer"
			argument3 is threshold value (sensor readings are between 0 and 49)
			ex. "./device dev1 Thermometer 37"
	for each actuator: (max 2)
		7. open terminal
		8. run "./device" with 2 arguments
			arg1 is name
			arg2 = type, either "AC" or "Dehumidifier"
			ex. "./device dev2 AC"
program is now ready****

send commands through cloud ******************
	1. select cloud terminal
	for put:
		2. "put" and device type "AC" or "Dehumidifier"
		ex. "put AC"
		notice: the respective actuator will immediately turn on (see actuator terminal)
	for get:
		3. "get" and device type "Thermometer" or "Dehumidifier"
		ex. "get Thermometer"
		notice: the parent immediately displays "parent says: forwarding command to child"
		the child then sends the most recent reading to the parent and prints "Signal parent 			about threshold crossing"
		The cloud then displays the current measurements of the sensor. This is the only time 			measurements below the threshold will be displayed.
	
closing the program*******************
	each program is configured such that a keyboard interrupt will properly close all queues and fifos
	the controller will display an error because both the parent and the child are trying to close 		the same queue. This should not affect the performance for the next time.
