package com.hemant.bakingapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.adapters.IngredientsListAdapter;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.BitmapBytesUtility;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;


public class IngredientsDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static String SELECTED_RECIPE_DETAILS = "SELECTED_RECIPE_DETAILS";
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_details_activity);
        if (!getIntent().hasExtra(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getApplicationContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        recipe = getIntent().getParcelableExtra(SELECTED_RECIPE_DETAILS);
        ImageView imageViewRecipePoster = findViewById(R.id.iv_ingredient_recipe_poster);
        if (recipe.getRecipePoster() != null && recipe.getRecipePoster().length > 0) {
            imageViewRecipePoster.setImageBitmap(BitmapBytesUtility.getImage(recipe.getRecipePoster()));
        } else {
            Glide.with(IngredientsDetailsActivity.this)
                    .load(Uri.parse(recipe.getRecipePosterURL()))
                    .into(imageViewRecipePoster);
        }
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_ingredient_select_for_widget);
        Toolbar toolbar = findViewById(R.id.my_ingredients_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(recipe.getName());

        RecyclerView ingredientsRecyclerView = findViewById(R.id.rv_ingredients);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setHasFixedSize(true);

        try {
            ingredientsRecyclerView.setAdapter(new IngredientsListAdapter(RecipesJsonUtils.getIngredientsList(recipe.getIngredients())));
        } catch (JSONException e) {
            e.printStackTrace();
            showNoIngredientFoundUI();
        }
        floatingActionButton.setOnClickListener(this);
    }

    private void showNoIngredientFoundUI() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ingredients_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_ingredient_select_for_widget:
                Intent intent = new Intent(getApplicationContext(), RecipeStepsActivity.class);
                intent.putExtra(SELECTED_RECIPE_DETAILS, recipe);
                startActivity(intent);
                break;
        }
    }
}
