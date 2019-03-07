package info;

import java.util.ArrayList;

public class RecipeInfo extends Info implements Comparable<RecipeInfo> {
	//RecipeInfo objects each store information of a recipe.
	public int recipeID;
	public int prepTime;
	public int cookTime;
	public ArrayList<String> ingredients;
	public ArrayList<String> instructions;
	
	public RecipeInfo(String name, double rating, int recipeID, int prepTime, int cookTime,
			ArrayList<String> ingredients, ArrayList<String> instructions) {
		this.name = name;
		this.rating = rating;
		this.recipeID = recipeID;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.ingredients = ingredients;
		this.instructions = instructions;
	}
	
	public int compareTo(RecipeInfo other) {
		return this.prepTime - other.prepTime;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this) return true;
		if(!(other instanceof RecipeInfo)) return false;
		RecipeInfo otherRecipeInfo = (RecipeInfo) other;
		return this.recipeID == otherRecipeInfo.recipeID;
	}
}