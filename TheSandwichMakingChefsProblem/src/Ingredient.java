
public enum Ingredient {
	PEANUT_BUTTER("peanut butter"), JAM("jam"), BREAD("bread");
	
	private final String text;
	
	private Ingredient(String text){
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
