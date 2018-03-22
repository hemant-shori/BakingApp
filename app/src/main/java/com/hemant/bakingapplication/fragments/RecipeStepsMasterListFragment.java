package com.hemant.bakingapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.activities.RecipeStepsActivity;
import com.hemant.bakingapplication.adapters.RecipeMasterListAdapter;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.models.RecipeStep;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

import static com.hemant.bakingapplication.activities.IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS;
import static com.hemant.bakingapplication.fragments.RecipeStepsFragment.RECIPE_STEP_CURRENT_COUNT_KEY;

public class RecipeStepsMasterListFragment extends Fragment implements RecipeMasterListAdapter.OnRecyclerViewItemClickListener {
    public interface OnMasterListItemClickListener {
        void onItemClicked(int position);
    }

    private OnMasterListItemClickListener onMasterListItemClickListener;
    private Recipe recipe;
    private ArrayList<RecipeStep> recipeStepArrayList;
    private int currentStepCount = 1;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onMasterListItemClickListener = (OnMasterListItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMasterListItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_steps_mater_list, container, false);
        if (getArguments() == null || !getArguments().containsKey(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            showErrorUI();
        } else {
            try {
                recipe = getArguments().getParcelable(SELECTED_RECIPE_DETAILS);

                if (getArguments().containsKey(RECIPE_STEP_CURRENT_COUNT_KEY))
                    currentStepCount = getArguments().getInt(RECIPE_STEP_CURRENT_COUNT_KEY);
                assert recipe != null;
                recipeStepArrayList = RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());

                RecyclerView masterListRecyclerView = rootView.findViewById(R.id.rv_recipe_steps_master_list);
                masterListRecyclerView.setLayoutManager(new

                        LinearLayoutManager(getContext()));
                masterListRecyclerView.setHasFixedSize(true);
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(masterListRecyclerView.getContext(),
                        LinearLayoutManager.VERTICAL);
                masterListRecyclerView.addItemDecoration(mDividerItemDecoration);
                masterListRecyclerView.setAdapter(new RecipeMasterListAdapter(recipeStepArrayList, this));
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
                showErrorUI();
            }
        }

        return rootView;
    }

    private void showErrorUI() {

    }

    @Override
    public void onItemClicked(int position) {
        onMasterListItemClickListener.onItemClicked(position);
    }
}
