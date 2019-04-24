package info;

import java.util.ArrayList;

public class RecipeInfo extends Info implements Comparable<RecipeInfo> {
	//RecipeInfo objects each store information of a recipe.
	public int recipeID;
	public int prepTime;
	public int cookTime;
	public ArrayList<String> ingredients;
	public ArrayList<Boolean> checked;
	public ArrayList<String> instructions;
	public String imageURL;

	public RecipeInfo(String name, int rating, int recipeID, int prepTime, int cookTime,
					  ArrayList<String> ingredients, ArrayList<String> instructions, String imageURL, int pos) {
		this.name = name;
		this.rating = rating;
		this.recipeID = recipeID;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.ingredients = ingredients;
		this.instructions = instructions;
		this.imageURL = imageURL;
		this.pos = pos;
		checked = new ArrayList<>();
	}

	public RecipeInfo(String name, int rating, int recipeID, int prepTime, int cookTime,
					  ArrayList<String> ingredients, ArrayList<String> instructions, String imageURL, int dbid, ArrayList<Boolean> checked) {
		this.name = name;
		this.rating = rating;
		this.recipeID = recipeID;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.ingredients = ingredients;
		this.instructions = instructions;
		this.imageURL = imageURL;
		this.dbid = dbid;
		this.checked = checked;
		pos = 0;
	}

	public RecipeInfo(String name, int rating, int recipeID, int prepTime, int cookTime,
					  ArrayList<String> ingredients, ArrayList<String> instructions, String imageURL, int dbid, int pos) {
		this.name = name;
		this.rating = rating;
		this.recipeID = recipeID;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.ingredients = ingredients;
		this.instructions = instructions;
		this.imageURL = imageURL;
		this.dbid = dbid;
		checked = new ArrayList<>();
		this.pos = pos;
	}

	//used for sorting in ascending order based on prepTime.
	public int compareTo(RecipeInfo other) {
		return this.prepTime - other.prepTime;
	}
	
	//two Spoonacular recipes must be the same if they have identical Spoonacular recipe IDs.
	@Override
	public boolean equals(Object other) {
		if(other == this) return true;
		if(!(other instanceof RecipeInfo)) return false;
		RecipeInfo otherRecipeInfo = (RecipeInfo) other;
		return this.recipeID == otherRecipeInfo.recipeID;
	}
}
