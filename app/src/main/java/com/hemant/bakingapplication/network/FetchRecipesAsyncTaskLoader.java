package com.hemant.bakingapplication.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.hemant.bakingapplication.interfaces.AsyncTaskLoaderInterface;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.NetworkUtils;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class FetchRecipesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private AsyncTaskLoaderInterface asyncTaskLoaderInterface;
    private ArrayList<Recipe> recipeArrayList;

    public FetchRecipesAsyncTaskLoader(@NonNull Context context, AsyncTaskLoaderInterface asyncTaskLoaderInterface) {
        super(context);
        this.asyncTaskLoaderInterface = asyncTaskLoaderInterface;
    }

    @Override
    public void deliverResult(@Nullable ArrayList<Recipe> data) {
        super.deliverResult(data);
        recipeArrayList = data;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        asyncTaskLoaderInterface.onStartLoading();
        if (recipeArrayList != null)
            deliverResult(recipeArrayList);
        else
            forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Recipe> loadInBackground() {
        try {
            String recipesString = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
            recipeArrayList = RecipesJsonUtils.getRecipeDetailsFromJson(recipesString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipeArrayList;
    }
}
