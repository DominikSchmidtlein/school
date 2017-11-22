Dominik Schmidtlein 100946295

Files:
ChefCurry - This is the sandwich maker class. There are three chefs that run concurrently. All of them have one of the three necessary ingredients for a sandwich and
they all need two more. The chefs try and get their required ingredients, and if they are, then the chef makes a sandwich. If the ingredients are not available, the
chef will wait until another thread notifies. This could be triggered by another chef, in which case the first chef would return into the wait set. If the agent calls
wait, there is a 1/3 chance that the ingredients will be the right ones. The chef prints that it made a sandwich once it has gathered all necessary ingredients.

Agent - The agent goes through 20 rounds of choosing 2 ingredients and putting them on the table. The agent prints that it has put ingredients on the table after the put
function returns. When the agent puts the 20th set of ingredients on the table, it shuts down.

Table - The table is the object which has a lock. This ensures that only 1 method can place ingredients or take ingredients from the table at any time. The table never 
prints about its actions because the agent and chef already specify the actions.

Main - The main class is nothing but a static main method. The method creates a table, 3 chefs and an agent all as threads. The chefs and the agent all get a reference to the table. The 
main method then starts all 4 threads. The main method then waits for the threads to join, and prints done when all threads have joined.

Setup Instructions:
In order to run the simulation, import the java project in Eclipse. Select the main file and click run. Look for the output on the Eclipse Console.