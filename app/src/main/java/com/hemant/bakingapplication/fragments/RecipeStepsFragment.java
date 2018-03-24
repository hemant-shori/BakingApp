package com.hemant.bakingapplication.fragments;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.databinding.RecipeStepsFragmentBinding;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.models.RecipeStep;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

import static com.hemant.bakingapplication.activities.IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS;


public class RecipeStepsFragment extends Fragment implements View.OnClickListener {
    public static final String RECIPE_STEP_CURRENT_COUNT_KEY = "RECIPE_STEP_CURRENT_COUNT_KEY";
    public static final String IS_TWO_PANE_LAYOUT_ENABLED_KEY = "IS_TWO_PANE_LAYOUT_ENABLED_KEY";
    private boolean twoPaneLayout = false;
    private ArrayList<RecipeStep> recipeStepArrayList;
    private int currentStepCount = 1;
    private RecipeStepsFragmentBinding recipeStepsFragmentBinding;
    private SimpleExoPlayer simpleExoPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recipeStepsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_steps_fragment, container, false);
        recipeStepsFragmentBinding.recipeStepsExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_holder));
        if (getArguments() == null || !getArguments().containsKey(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            showErrorUI();
        } else {
            try {
                initializePlayer();
                Recipe recipe = getArguments().getParcelable(SELECTED_RECIPE_DETAILS);
                if (getArguments().containsKey(IS_TWO_PANE_LAYOUT_ENABLED_KEY))
                    twoPaneLayout = getArguments().getBoolean(IS_TWO_PANE_LAYOUT_ENABLED_KEY);
                if (getArguments().containsKey(RECIPE_STEP_CURRENT_COUNT_KEY))
                    currentStepCount = getArguments().getInt(RECIPE_STEP_CURRENT_COUNT_KEY);
                assert recipe != null;
                recipeStepArrayList = RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());
                recipeStepsFragmentBinding.fabStepsNext.setOnClickListener(this);
                recipeStepsFragmentBinding.fabStepsPrevious.setOnClickListener(this);
                if (twoPaneLayout) {
                    recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.GONE);
                    recipeStepsFragmentBinding.fabStepsPrevious.setVisibility(View.GONE);
                } else {
                    if (currentStepCount == 1) {
                        recipeStepsFragmentBinding.fabStepsPrevious.setVisibility(View.GONE);
                        recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.VISIBLE);
                    } else if (currentStepCount == recipeStepArrayList.size()) {
                        recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.GONE);
                        recipeStepsFragmentBinding.fabStepsPrevious.setVisibility(View.VISIBLE);
                    }
                }
                updateCurrentStepUI();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
                showErrorUI();
            }
        }
        return recipeStepsFragmentBinding.getRoot();
    }

    private void changeRecipeStepMedia(Uri mediaUri) {
        if (recipeStepsFragmentBinding.recipeStepsExoPlayerView != null && simpleExoPlayer != null) {
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            assert getContext() != null;
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
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

    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector,
                    loadControl);
            recipeStepsFragmentBinding.recipeStepsExoPlayerView.setPlayer(simpleExoPlayer);
        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_steps_previous:
                goToPreviewStep();
                break;
            case R.id.fab_steps_next:
                gotoNextStep();
                break;
        }
    }

    private void gotoNextStep() {
        if (currentStepCount + 1 == recipeStepArrayList.size()) {
            recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.GONE);
        }
        currentStepCount++;
        updateCurrentStepUI();
        recipeStepsFragmentBinding.fabStepsPrevious.setVisibility(View.VISIBLE);
    }

    private void updateCurrentStepUI() {
        RecipeStep recipeStep = recipeStepArrayList.get(currentStepCount - 1);
        recipeStepsFragmentBinding.tvRecipeStepsShortDescription.setText(recipeStep.getShortDescription());
        recipeStepsFragmentBinding.tvRecipeStepsDescription.setText(recipeStep.getDescription());
        changeRecipeStepMedia(Uri.parse(recipeStep.getVideoUrl()));
    }

    private void goToPreviewStep() {
        if (currentStepCount - 1 == 1) {
            recipeStepsFragmentBinding.fabStepsPrevious.setVisibility(View.GONE);
        }
        currentStepCount--;
        updateCurrentStepUI();
        recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.VISIBLE);
    }

    public void updateRecipeStep(int position) {
        currentStepCount = position + 1;
        updateCurrentStepUI();
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }
}
