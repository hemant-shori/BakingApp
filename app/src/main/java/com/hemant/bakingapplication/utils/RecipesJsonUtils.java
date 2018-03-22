package com.hemant.bakingapplication.utils;

import com.hemant.bakingapplication.models.Ingredient;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.models.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class RecipesJsonUtils {


    public static ArrayList<Recipe> getRecipeDetailsFromJson(String movieDetailsString)
            throws JSONException {

        final String NAME = "name";
        final String INGREDIENTS = "ingredients";
        final String STEPS = "steps";
        final String SERVINGS = "servings";


        final String SHORT_DESCRIPTION = "shortDescription";
        final String DESCRIPTION = "description";
        final String VIDEO_URL = "videoURL";
        final String THUMBNAIL_URL = "thumbnailURL";

        JSONArray outerJsonArray = new JSONArray(movieDetailsString);
        ArrayList<Recipe> recipeArrayList = new ArrayList<>(outerJsonArray.length());

        for (int i = 0; i < outerJsonArray.length(); i++) {
            JSONObject recipesDetailsJsonObject = outerJsonArray.getJSONObject(i);
            Recipe recipe = new Recipe();
            recipe.setName(recipesDetailsJsonObject.getString(NAME));
            recipe.setIngredients(recipesDetailsJsonObject.getJSONArray(INGREDIENTS).toString());

            JSONArray recipeSteps = recipesDetailsJsonObject.getJSONArray(STEPS);
            String posterUrl = recipeSteps.getJSONObject(recipeSteps.length() - 1).getString(VIDEO_URL);
            recipe.setRecipePosterURL(posterUrl);
            recipe.setSteps(recipeSteps.toString());
            recipe.setServing(String.valueOf(recipesDetailsJsonObject.getInt(SERVINGS)));
            recipeArrayList.add(recipe);
        }

        return recipeArrayList;
    }

    public static ArrayList<Ingredient> getIngredientsList(String ingredientsString) throws JSONException {

        final String QUANTITY = "quantity";
        final String MEASURE = "measure";
        final String INGREDIENT = "ingredient";

        JSONArray ingredientsJsonArray = new JSONArray(ingredientsString);
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>(ingredientsJsonArray.length());
        for (int i = 0; i < ingredientsJsonArray.length(); i++) {
            JSONObject ingredientJsonObject = ingredientsJsonArray.getJSONObject(i);
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientName(ingredientJsonObject.getString(INGREDIENT));
            ingredient.setMeasure(ingredientJsonObject.getString(MEASURE));
            ingredient.setQuantity(ingredientJsonObject.getString(QUANTITY));
            ingredientArrayList.add(ingredient);
        }
        return ingredientArrayList;
    }

    public static ArrayList<RecipeStep> getRecipeStepsDetails(String steps) throws JSONException {

        final String SHORT_DESCRIPTION = "shortDescription";
        final String DESCRIPTION = "description";
        final String VIDEO_URL = "videoURL";
        final String THUMBNAIL_URL = "thumbnailURL";
        JSONArray recipeStepsJsonArray = new JSONArray(steps);
        ArrayList<RecipeStep> recipeStepArrayList = new ArrayList<>(recipeStepsJsonArray.length());
        for (int i = 0; i < recipeStepsJsonArray.length(); i++) {
            JSONObject recipeStepsJsonObject = recipeStepsJsonArray.getJSONObject(i);
            RecipeStep recipeStep = new RecipeStep();
            recipeStep.setShortDescription(recipeStepsJsonObject.getString(SHORT_DESCRIPTION));
            recipeStep.setDescription(recipeStepsJsonObject.getString(DESCRIPTION));
            String videoUrl = recipeStepsJsonObject.getString(VIDEO_URL);
            if (videoUrl.equals("")) {
                videoUrl = recipeStepsJsonObject.getString(THUMBNAIL_URL);
            }
            recipeStep.setVideoUrl(videoUrl);
            recipeStepArrayList.add(recipeStep);
        }
        return recipeStepArrayList;
    }
}