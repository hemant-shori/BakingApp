package com.hemant.bakingapplication.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.adapters.RecipeMasterListAdapter;
import com.hemant.bakingapplication.databinding.RecipeStepsMasterListBinding;
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
        RecipeStepsMasterListBinding recipeStepsMasterListBinding = DataBindingUtil.inflate(inflater,
                R.layout.recipe_steps_master_list, container, false);
        if (getArguments() == null || !getArguments().containsKey(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            showErrorUI();
        } else {
            try {
                Recipe recipe = getArguments().getParcelable(SELECTED_RECIPE_DETAILS);

                if (getArguments().containsKey(RECIPE_STEP_CURRENT_COUNT_KEY))
                    currentStepCount = getArguments().getInt(RECIPE_STEP_CURRENT_COUNT_KEY);
                assert recipe != null;
                ArrayList<RecipeStep> recipeStepArrayList = RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());

                recipeStepsMasterListBinding.rvRecipeStepsMasterList.setLayoutManager(new

                        LinearLayoutManager(getContext()));
                recipeStepsMasterListBinding.rvRecipeStepsMasterList.setHasFixedSize(true);
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recipeStepsMasterListBinding.rvRecipeStepsMasterList.getContext(),
                        LinearLayoutManager.VERTICAL);
                recipeStepsMasterListBinding.rvRecipeStepsMasterList.addItemDecoration(mDividerItemDecoration);
                recipeStepsMasterListBinding.rvRecipeStepsMasterList.setAdapter(new RecipeMasterListAdapter(recipeStepArrayList, this, currentStepCount - 1));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
                showErrorUI();
            }
        }

        return recipeStepsMasterListBinding.getRoot();
    }

    private void showErrorUI() {
        assert getContext() != null;
        new AlertDialog.Builder(getContext(), R.style.MyAlertDialog)
                .setTitle(R.string.UnableToConnect)
                .setMessage(R.string.UnableToFetchRecipeSteps)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null)
                            getActivity().finish();
                    }
                }).setCancelable(false)
                .create().show();
    }

    @Override
    public void onItemClicked(int position) {
        onMasterListItemClickListener.onItemClicked(position);
    }

}
