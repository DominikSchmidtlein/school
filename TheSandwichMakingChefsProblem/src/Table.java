import java.util.ArrayList;

/**
 * The table has a lock which limits 1 thread to access it at a time. The table
 * is where ingredients are placed by the agent and then used by the chefs.
 * @author dominikschmidtlein
 *
 */
public class Table {

	/**
	 * The ingredients currently on the table.
	 */
	private ArrayList<Ingredient> ingredients;
	/**
	 * The sum of all the sandwiches made so far.
	 */
	private int numberOfSandwichesMade;
	
	/**
	 * The maximum number of sandwiches to be made.
	 */
	private int sandwichLimit;

	/**
	 * Constructs an empty table with no sandwiches made.
	 */
	public Table(int sandwichLimit) {
		this.ingredients = new ArrayList<>();
		this.numberOfSandwichesMade = 0;
		this.sandwichLimit = sandwichLimit;
	}

	/**
	 * Waits until the table has no ingredients on it, then places new ones on it.
	 * At the end, all threads in the wait set are notified because the ingredients
	 * on the table have changed.
	 * @param ingredients the new ingredients to be put on the table
	 */
	public synchronized void put(ArrayList<Ingredient> ingredients) {
		while (!this.ingredients.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		this.ingredients.addAll(ingredients);
		notifyAll();
	}

	/**
	 * Waits until the ingredients on the table match the desired ingredients.
	 * Then the ingredients are taken off of the table. The 
	 * @param ingredients
	 * @return
	 */
	public synchronized ArrayList<Ingredient> get(ArrayList<Ingredient> ingredients) {
		while (!this.ingredients.containsAll(ingredients) && numberOfSandwichesMade < sandwichLimit) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}
		if(numberOfSandwichesMade >= sandwichLimit)
			return null;
					
		this.ingredients.removeAll(ingredients);
		incrementNumberOfSandwichesMade();
		notifyAll();
		return ingredients;
	}

	/**
	 * Increments the number of sandwiches that have been made by one.
	 */
	public synchronized void incrementNumberOfSandwichesMade() {
		numberOfSandwichesMade++;
	}
	
	/**
	 * Returns the number of sandwiches that have been made so far.
	 * @return number of sandwiches made
	 */
	public synchronized int getNumberOfSandwichesMade(){
		return numberOfSandwichesMade;
	}

}
