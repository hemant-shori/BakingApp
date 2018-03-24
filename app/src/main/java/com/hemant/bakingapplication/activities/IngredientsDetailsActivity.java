package com.hemant.bakingapplication.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.adapters.IngredientsListAdapter;
import com.hemant.bakingapplication.databinding.IngredientsDetailsActivityBinding;
import com.hemant.bakingapplication.models.Ingredient;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.BitmapBytesUtility;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;
import com.hemant.bakingapplication.widget.FavoriteRecipeWidgetProvider;

import org.json.JSONException;

import java.util.ArrayList;

import static com.hemant.bakingapplication.widget.FavoriteRecipeWidgetProvider.WIDGET_INGREDIENTS_LIST_KEY;
import static com.hemant.bakingapplication.widget.FavoriteRecipeWidgetProvider.WIDGET_INGREDIENT_NAME_KEY;


public class IngredientsDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SELECTED_RECIPE_DETAILS = "SELECTED_RECIPE_DETAILS";
    private Recipe recipe;
    private String[] ingredients;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IngredientsDetailsActivityBinding ingredientsDetailsActivityBinding = DataBindingUtil.setContentView(this, R.layout.ingredients_details_activity);
        if (!getIntent().hasExtra(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getApplicationContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recipe = getIntent().getParcelableExtra(SELECTED_RECIPE_DETAILS);
        if (recipe.getRecipePoster() != null && recipe.getRecipePoster().length > 0) {
            ingredientsDetailsActivityBinding.ivIngredientRecipePoster.setImageBitmap(BitmapBytesUtility.getImage(recipe.getRecipePoster()));
        } else {
            Glide.with(IngredientsDetailsActivity.this)
                    .load(Uri.parse(recipe.getRecipePosterURL()))
                    .into(ingredientsDetailsActivityBinding.ivIngredientRecipePoster);
        }
        setSupportActionBar(ingredientsDetailsActivityBinding.myIngredientsToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ingredientsDetailsActivityBinding.collapsingToolbar.setTitle(recipe.getName());

        ingredientsDetailsActivityBinding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        ingredientsDetailsActivityBinding.rvIngredients.setHasFixedSize(true);

        try {
            ArrayList<Ingredient> ingredientArrayList = RecipesJsonUtils.getIngredientsList(recipe.getIngredients());
            populateIngredientsForWidget(ingredientArrayList);
            ingredientsDetailsActivityBinding.rvIngredients.setAdapter(new IngredientsListAdapter(ingredientArrayList));
        } catch (JSONException e) {
            e.printStackTrace();
            showNoIngredientFoundUI();
        }
        ingredientsDetailsActivityBinding.fabIngredientOpenRecipeSteps.setOnClickListener(this);
    }

    private void showNoIngredientFoundUI() {
        new AlertDialog.Builder(getApplicationContext(), R.style.MyAlertDialog)
                .setTitle(R.string.UnableToConnect)
                .setMessage(R.string.UnableToFetchIngredients)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false)
                .create().show();
    }

    private void populateIngredientsForWidget(ArrayList<Ingredient> ingredientArrayList) {
        ingredients = new String[ingredientArrayList.size()];
        for (int i = 0; i < ingredientArrayList.size(); i++) {
            ingredients[i] = String.format("%s %s of %s", ingredientArrayList.get(i).getQuantity(), ingredientArrayList.get(i).getMeasure(), ingredientArrayList.get(i).getIngredientName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.ingredients_menu, menu);
        String recipeName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(WIDGET_INGREDIENT_NAME_KEY, "");
        if (TextUtils.equals(recipeName, recipe.getName()))
            menu.findItem(R.id.action_add_to_widget).setVisible(false);
        else
            menu.findItem(R.id.action_add_to_widget).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_to_widget:
                String result;
                if (ingredients.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : ingredients) {
                        sb.append(s).append("$");
                    }
                    result = sb.deleteCharAt(sb.length() - 1).toString();
                    menu.findItem(R.id.action_add_to_widget).setVisible(false);
                    invalidateOptionsMenu();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString(WIDGET_INGREDIENTS_LIST_KEY, result)
                            .putString(WIDGET_INGREDIENT_NAME_KEY, recipe.getName())
                            .apply();
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FavoriteRecipeWidgetProvider.class));
                    //Now update all widgets
                    FavoriteRecipeWidgetProvider.forceUpdateWidgets(getApplicationContext(), appWidgetManager, appWidgetIds);
                }

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_ingredient_open_recipe_steps:
                Intent intent = new Intent(getApplicationContext(), RecipeStepsActivity.class);
                intent.putExtra(SELECTED_RECIPE_DETAILS, recipe);
                startActivity(intent);
                break;
        }
    }
}
