package com.hemant.bakingapplication.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.adapters.BakingRecipesAdapter;
import com.hemant.bakingapplication.databinding.ActivityMainBinding;
import com.hemant.bakingapplication.interfaces.AsyncTaskLoaderInterface;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.network.FetchRecipesAsyncTaskLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncTaskLoaderInterface, BakingRecipesAdapter.OnRecipesClickListener, LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {
    private BakingRecipesAdapter bakingRecipesAdapter;
    private static final int RECIPES_LIST_LOADER = 100;
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        int columnCount = getColumnAccordingToScreenSize();
        if (columnCount == 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            activityMainBinding.recipesRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount);
            activityMainBinding.recipesRecyclerView.setLayoutManager(gridLayoutManager);
        }
        activityMainBinding.recipesRecyclerView.setHasFixedSize(true);
        bakingRecipesAdapter = new BakingRecipesAdapter(this);
        activityMainBinding.recipesRecyclerView.setAdapter(bakingRecipesAdapter);
    }

    private int getColumnAccordingToScreenSize() {
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        //int smallestWidth = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier.
        if (screenWidthDp > getResources().getInteger(R.integer.smallestWidth700)) {
            return getResources().getInteger(R.integer.columnCount3);
        } else if (screenWidthDp > getResources().getInteger(R.integer.smallestWidth600)) {
            return getResources().getInteger(R.integer.columnCount2);
        } else {
            return getResources().getInteger(R.integer.columnCount1);
        }
    }

    @Override
    public void onItemClicked(Recipe recipe) {
        Intent intent = new Intent(MainActivity.this, IngredientsDetailsActivity.class);
        intent.putExtra(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS, recipe);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RECIPES_LIST_LOADER:
                return new FetchRecipesAsyncTaskLoader(this, this);
            default:
                throw new UnsupportedOperationException("Unknown Loader Executed : " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
        if (data != null) {
            bakingRecipesAdapter.swapRecipes(data);
            showRecipeUI();
        } else {
            showNoRecipesFoundUI();
        }
    }

    private void showNoRecipesFoundUI() {
        activityMainBinding.loadRecipesProgressBar.setVisibility(View.GONE);
        activityMainBinding.recipesRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.error_loading_recipes_layout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_recipes_network_response)).setText(R.string.no_recipes_found);
    }

    private void showRecipeUI() {
        activityMainBinding.loadRecipesProgressBar.setVisibility(View.GONE);
        activityMainBinding.recipesRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.error_loading_recipes_layout).setVisibility(View.GONE);
    }


    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Recipe>> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(RECIPES_LIST_LOADER, null, this);

    }

    @Override
    public void onStartLoading() {
        activityMainBinding.loadRecipesProgressBar.setVisibility(View.VISIBLE);
        activityMainBinding.recipesRecyclerView.setVisibility(View.GONE);
        findViewById(R.id.error_loading_recipes_layout).setVisibility(View.GONE);
        bakingRecipesAdapter.swapRecipes(null);
    }
}