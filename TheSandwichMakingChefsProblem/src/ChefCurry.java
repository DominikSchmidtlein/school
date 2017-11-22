import java.util.ArrayList;

/**
 * The chefs take ingredients off the table if it would give them all three ingredients.
 * Otherwise they wait in the table's wait or blocked set.
 * @author dominikschmidtlein
 *
 */
public class ChefCurry implements Runnable {

	private String name;
	private ArrayList<Ingredient> neededIngredients;
	private Table table;
	private int sandwichLimit;
	
	/**
	 * Creates a new chef who has a reference to the table, an ingredient in infinite supply
	 * and a sandwich work order. Sets neededIngredients to the 2 ingredients that the chef
	 * does not have.
	 * @param table a reference to the table from which the chef gets the remaining ingredients
	 * @param ingredient the ingredient that the chef has an infinite supply of
	 * @param sandwichLimit the total number of sandwiches to be made by all chefs
	 */
	public ChefCurry(Table table, Ingredient ingredient, int sandwichLimit) {		
		this.name = "CHEF " + ingredient.toString();
		this.table = table;
		this.sandwichLimit = sandwichLimit;
		neededIngredients = new ArrayList<>(3);
		
		neededIngredients.add(Ingredient.PEANUT_BUTTER);
		neededIngredients.add(Ingredient.JAM);
		neededIngredients.add(Ingredient.BREAD);
		
		neededIngredients.remove(ingredient);		
	}
	
	/**
	 * Until all sandwiches are made, grab the necessary ingredients and make a sandwich.
	 */
	@Override
	public void run() {
		while(table.getNumberOfSandwichesMade() < sandwichLimit){
			if(table.get(neededIngredients) == null)
				break;
			this.withThePot();
		}
	}
	
	/**
	 * Analogous to making a sandwich. The chef prints his name and that he made a sandwich.
	 */
	private void withThePot(){
		System.out.println(name + " made a sandwich.");
	}
	
}
