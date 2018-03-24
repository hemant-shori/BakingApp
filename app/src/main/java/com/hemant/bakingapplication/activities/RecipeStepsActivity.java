package com.hemant.bakingapplication.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.fragments.RecipeStepsFragment;
import com.hemant.bakingapplication.fragments.RecipeStepsMasterListFragment;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;

import static com.hemant.bakingapplication.activities.IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS;
import static com.hemant.bakingapplication.fragments.RecipeStepsFragment.IS_TWO_PANE_LAYOUT_ENABLED_KEY;
import static com.hemant.bakingapplication.fragments.RecipeStepsFragment.RECIPE_STEP_CURRENT_COUNT_KEY;


public class RecipeStepsActivity extends AppCompatActivity implements RecipeStepsMasterListFragment.OnMasterListItemClickListener {
    private final String SAVE_INSTANCE_RECIPE_STEP_CURRENT_COUNT_KEY = "SAVE_INSTANCE_RECIPE_STEP_CURRENT_COUNT_KEY";
    private RecipeStepsFragment recipeStepsFragment;
    private boolean twoPaneLayout;
    private int currentStepCount = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_steps_activity);
        if (!getIntent().hasExtra(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getApplicationContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Recipe recipe = getIntent().getParcelableExtra(SELECTED_RECIPE_DETAILS);
        try {
            if (findViewById(R.id.recipe_steps_master_list_container) != null) {
                twoPaneLayout = true;
            }
            if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_INSTANCE_RECIPE_STEP_CURRENT_COUNT_KEY)) {
                currentStepCount = savedInstanceState.getInt(SAVE_INSTANCE_RECIPE_STEP_CURRENT_COUNT_KEY);
            }
            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(recipe.getName());
            RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS, recipe);
        bundle.putBoolean(IS_TWO_PANE_LAYOUT_ENABLED_KEY, twoPaneLayout);
        bundle.putInt(RECIPE_STEP_CURRENT_COUNT_KEY, currentStepCount);

        if (savedInstanceState != null) {
            recipeStepsFragment = (RecipeStepsFragment) getSupportFragmentManager().getFragment(savedInstanceState, RecipeStepsFragment.class.getName());
            recipeStepsFragment.setArguments(bundle);
        } else {
            recipeStepsFragment = new RecipeStepsFragment();
            recipeStepsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_steps_container, recipeStepsFragment)
                    .commit();
        }
        if (twoPaneLayout) {
            RecipeStepsMasterListFragment recipeStepsMasterListFragment = new RecipeStepsMasterListFragment();
            recipeStepsMasterListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_steps_master_list_container, recipeStepsMasterListFragment)
                    .commit();
        }
    }

    @Override
    public void onItemClicked(int position) {
        recipeStepsFragment.updateRecipeStep(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_INSTANCE_RECIPE_STEP_CURRENT_COUNT_KEY, recipeStepsFragment.getCurrentStepCount());
        getSupportFragmentManager().putFragment(outState, RecipeStepsFragment.class.getName(), recipeStepsFragment);
    }

    public boolean twoPaneLayoutEnabled() {
        return twoPaneLayout;
    }

}
