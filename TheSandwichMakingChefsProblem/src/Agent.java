import java.util.ArrayList;

/**
 * The agent is responsible for putting 2 of 3 ingredients on the table every round
 * until the limit is reached.
 * @author dominikschmidtlein
 *
 */
public class Agent implements Runnable {

	private Table table;
	private int sandwichLimit;
	
	/**
	 * Creates an agent.
	 * @param sandwichLimit the number of sandwiches to be made in total
	 * @param table a reference to the table where ingredients are places
	 */
	public Agent(int sandwichLimit, Table table) {
		this.table = table;
		this.sandwichLimit = sandwichLimit;
	}
	
	/**
	 * The agent keeps placing ingredients on the table until the limit is reached. The agent
	 * also print what round and which ingredients were placed.
	 */
	@Override
	public void run() {
		for(int i = 0; i < sandwichLimit; i ++){
			ArrayList<Ingredient> ingredients = chooseIngredients();
			table.put(ingredients);
			System.out.println("Round " + (i + 1) + ", agent put " + ingredients.get(0) + " and " + ingredients.get(1) + ".");
		}
	}

	/**
	 * The agent randomly selects 2 ingredients to place on the table. All combinations have the same probability.
	 * @return an array of 2 ingredients which will be placed on the table
	 */
	private ArrayList<Ingredient> chooseIngredients() {
		ArrayList<Ingredient> ingredients = new ArrayList<>(2);
		double rand = Math.random();
		if(rand < (1.0/3.0)){
			ingredients.add(Ingredient.PEANUT_BUTTER);
			ingredients.add(Ingredient.BREAD);
		}	
		else if(rand < (2.0/3.0)){
			ingredients.add(Ingredient.PEANUT_BUTTER);
			ingredients.add(Ingredient.JAM);
		}
		else{
			ingredients.add(Ingredient.BREAD);
			ingredients.add(Ingredient.JAM);
		}
		
		return ingredients;
	}

}
