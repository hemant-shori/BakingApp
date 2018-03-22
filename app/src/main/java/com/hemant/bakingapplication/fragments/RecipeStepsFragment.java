package com.hemant.bakingapplication.fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.models.RecipeStep;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

import static com.hemant.bakingapplication.activities.IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS;


public class RecipeStepsFragment extends Fragment implements View.OnClickListener {
    public static final String RECIPE_STEP_CURRENT_COUNT_KEY = "RECIPE_STEP_CURRENT_COUNT_KEY";
    public static String IS_TWO_PANE_LAYOUT_ENABLED_KEY = "IS_TWO_PANE_LAYOUT_ENABLED_KEY";
    private boolean twoPaneLayout = false;
    SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    Recipe recipe;
    private ArrayList<RecipeStep> recipeStepArrayList;
    private int currentStepCount = 1;
    private FloatingActionButton nextStepFAB, previousStepFAB;
    private TextView shortDescriptionTextView, descriptionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recepe_steps_fragment, container, false);
        simpleExoPlayerView = rootView.findViewById(R.id.recipe_steps_exo_player_view);
        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_holder));
        if (getArguments() == null || !getArguments().containsKey(SELECTED_RECIPE_DETAILS)) {
            Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
            showErrorUI();
        } else {
            try {
                initializePlayer();
                recipe = getArguments().getParcelable(SELECTED_RECIPE_DETAILS);
                if (getArguments().containsKey(IS_TWO_PANE_LAYOUT_ENABLED_KEY))
                    twoPaneLayout = getArguments().getBoolean(IS_TWO_PANE_LAYOUT_ENABLED_KEY);
                if (getArguments().containsKey(RECIPE_STEP_CURRENT_COUNT_KEY))
                    currentStepCount = getArguments().getInt(RECIPE_STEP_CURRENT_COUNT_KEY);
                assert recipe != null;
                recipeStepArrayList = RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());
                nextStepFAB = rootView.findViewById(R.id.fab_steps_next);
                nextStepFAB.setOnClickListener(this);
                previousStepFAB = rootView.findViewById(R.id.fab_steps_previous);
                previousStepFAB.setOnClickListener(this);
                shortDescriptionTextView = rootView.findViewById(R.id.tv_recipe_steps_short_description);
                descriptionTextView = rootView.findViewById(R.id.tv_recipe_steps_description);
                if (twoPaneLayout) {
                    nextStepFAB.setVisibility(View.GONE);
                    previousStepFAB.setVisibility(View.GONE);
                } else {
                    if (currentStepCount == 1) {
                        previousStepFAB.setVisibility(View.GONE);
                        nextStepFAB.setVisibility(View.VISIBLE);
                    } else if (currentStepCount == recipeStepArrayList.size()) {
                        nextStepFAB.setVisibility(View.GONE);
                        previousStepFAB.setVisibility(View.VISIBLE);
                    }
                }
                updateCurrentStepUI();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
                showErrorUI();
            }
        }
        return rootView;
    }

    private void changeRecipeStepMedia(Uri mediaUri) {
        if (simpleExoPlayerView != null && simpleExoPlayer != null) {
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            assert getContext() != null;
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    private void showErrorUI() {
        //TODO: show error UI
    }

    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector,
                    loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
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
            nextStepFAB.setVisibility(View.GONE);
        }
        currentStepCount++;
        updateCurrentStepUI();
        previousStepFAB.setVisibility(View.VISIBLE);
    }

    private void updateCurrentStepUI() {
        RecipeStep recipeStep = recipeStepArrayList.get(currentStepCount - 1);
        shortDescriptionTextView.setText(recipeStep.getShortDescription());
        descriptionTextView.setText(recipeStep.getDescription());
        changeRecipeStepMedia(Uri.parse(recipeStep.getVideoUrl()));
    }

    private void goToPreviewStep() {
        if (currentStepCount - 1 == 1) {
            previousStepFAB.setVisibility(View.GONE);
        }
        currentStepCount--;
        updateCurrentStepUI();
        nextStepFAB.setVisibility(View.VISIBLE);
    }

    public void updateRecipeStep(int position) {
        currentStepCount = position + 1;
        updateCurrentStepUI();
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }
}
