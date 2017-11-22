import java.util.ArrayList;

/**
 * Main class is responsible for creating other classes and shutting down.
 * @author dominikschmidtlein
 *
 */
public class Main {
	
	public static final int SANDWICH_LIMIT = 20;
	
	
	/**
	 * Creates a new table and gives every thread a reference. Creates 4 threads, 3 chefs
	 * and 1 agent. Starts them all, then waits for the threads to join. Prints Done.
	 * @param args irrelevant
	 */
	public static void main(String[] args) {
		Table table = new Table(SANDWICH_LIMIT);
		
		//array for storing threads (agent and chef)
		ArrayList<Thread> threads = new ArrayList<>(Ingredient.values().length + 1);
		
		//new thread for the agent
		threads.add(new Thread(new Agent(SANDWICH_LIMIT, table)));
		
		//Create a new thread for each chef
		for(Ingredient ingredient : Ingredient.values())
			threads.add(new Thread(new ChefCurry(table, ingredient, 20)));
		
		//Start all 4 threads
		for(Thread thread : threads)
			thread.start();
		
		//Wait for all 4 threads to finish
		for(Thread thread : threads){
			try {
				thread.join();
			} catch (Exception e) {

			}
		}
		
		System.out.println("\nDone");
	}
}
