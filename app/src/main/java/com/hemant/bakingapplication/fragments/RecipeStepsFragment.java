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

import com.google.android.exoplayer2.C;
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
    private static final String SELECTED_PLAYER_POSITION_KEY = "SELECTED_PLAYER_POSITION_KEY";
    private static final String SELECTED_PLAYER_RESUME_WINDOW_KEY = "SELECTED_PLAYER_RESUME_WINDOW_KEY";
    private boolean twoPaneLayout = false;
    private ArrayList<RecipeStep> recipeStepArrayList;
    private int currentStepCount = 1;
    private RecipeStepsFragmentBinding recipeStepsFragmentBinding;
    private SimpleExoPlayer simpleExoPlayer;
    private long playerPosition = C.TIME_UNSET;
    private int resumeWindow = C.INDEX_UNSET;

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
                assert recipe != null;
                recipeStepArrayList = RecipesJsonUtils.getRecipeStepsDetails(recipe.getSteps());
                recipeStepsFragmentBinding.fabStepsNext.setOnClickListener(this);
                recipeStepsFragmentBinding.fabStepsPrevious.setOnClickListener(this);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.UnableToGetTheMovieData), Toast.LENGTH_SHORT).show();
                showErrorUI();
            }
        }
        return recipeStepsFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_PLAYER_POSITION_KEY)) {
                playerPosition = savedInstanceState.getLong(SELECTED_PLAYER_POSITION_KEY);
            }
            if (savedInstanceState.containsKey(SELECTED_PLAYER_RESUME_WINDOW_KEY)) {
                resumeWindow = savedInstanceState.getInt(SELECTED_PLAYER_RESUME_WINDOW_KEY);
            }
        }
    }

    private void changeRecipeStepMedia(Uri mediaUri) {
        if (recipeStepsFragmentBinding.recipeStepsExoPlayerView != null && simpleExoPlayer != null) {
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            assert getContext() != null;
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(getContext(),
                    userAgent), new DefaultExtractorsFactory(), null, null);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                simpleExoPlayer.seekTo(resumeWindow, playerPosition);
            }
            simpleExoPlayer.prepare(mediaSource, !haveResumePosition, false);
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
            playerPosition = simpleExoPlayer.getCurrentPosition();
            resumeWindow = simpleExoPlayer.getCurrentWindowIndex();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        assert getArguments() != null;
        if (getArguments().containsKey(IS_TWO_PANE_LAYOUT_ENABLED_KEY))
            twoPaneLayout = getArguments().getBoolean(IS_TWO_PANE_LAYOUT_ENABLED_KEY);
        if (getArguments().containsKey(RECIPE_STEP_CURRENT_COUNT_KEY))
            currentStepCount = getArguments().getInt(RECIPE_STEP_CURRENT_COUNT_KEY);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (playerPosition != C.TIME_UNSET) {
            outState.putLong(SELECTED_PLAYER_POSITION_KEY, playerPosition);
        }
        if (resumeWindow != C.INDEX_UNSET) {
            outState.putInt(SELECTED_PLAYER_RESUME_WINDOW_KEY, resumeWindow);
        }
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
        clearResumePosition();
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
        clearResumePosition();
        updateCurrentStepUI();
        recipeStepsFragmentBinding.fabStepsNext.setVisibility(View.VISIBLE);
    }

    public void updateRecipeStep(int position) {
        currentStepCount = position + 1;
        clearResumePosition();
        updateCurrentStepUI();
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        playerPosition = C.TIME_UNSET;
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }
}
