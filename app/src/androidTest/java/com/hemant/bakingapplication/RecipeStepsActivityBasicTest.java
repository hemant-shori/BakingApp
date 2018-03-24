package com.hemant.bakingapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.hemant.bakingapplication.activities.IngredientsDetailsActivity;
import com.hemant.bakingapplication.activities.RecipeStepsActivity;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.NetworkUtils;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeStepsActivityBasicTest {

    @SuppressWarnings("SameParameterValue")
    private IdlingResource registerIdlingResourceWithWaitTime(int time) {
        long waitingTime = DateUtils.SECOND_IN_MILLIS * time;
        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
        // Now we wait
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        IdlingRegistry.getInstance().register(idlingResource);
        return idlingResource;
    }

    @Rule
    public final ActivityTestRule<RecipeStepsActivity> recipeStepsActivityTestRule = new ActivityTestRule<>(RecipeStepsActivity.class, true, false);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(15, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(8, TimeUnit.SECONDS);
    }

    @Test
    public void portraitModeTest_CheckShortDescriptionIsDisplayed_ClickOnNextFAB_ClickOnPreviousFAB() {
        launchActivity();
        //check Recycler View is displayed
        recipeStepsActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        IdlingResource idlingResource = registerIdlingResourceWithWaitTime(3);
        //if two pane disabled the check for fab
        if (!recipeStepsActivityTestRule.getActivity().twoPaneLayoutEnabled()) {
            onView(withId(R.id.cv_recipe_steps_short_description)).check(matches(isDisplayed()));
            onView(withId(R.id.fab_steps_next)).check(matches(isDisplayed()));
            onView(withId(R.id.fab_steps_next)).perform(click());
            onView(withId(R.id.fab_steps_previous)).check(matches(isDisplayed()));
            onView(withId(R.id.fab_steps_previous)).perform(click());
        }
        // Clean up
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void landScapeMode_CheckMasterStepListVisible_PerformClickAndChangeStep() {
        launchActivity();
        recipeStepsActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        IdlingResource idlingResource = registerIdlingResourceWithWaitTime(3);

        if (recipeStepsActivityTestRule.getActivity().twoPaneLayoutEnabled()) {
            onView(withId(R.id.fab_steps_next)).check(doesNotExist());
            onView(withId(R.id.fab_steps_previous)).check(doesNotExist());
            onView(withId(R.id.rv_recipe_steps_master_list)).check(matches(isDisplayed()));
            String matcher = "Recipe Introduction";
            onView(withId(R.id.cv_recipe_steps_short_description)).check(matches(withText(matcher)));
            onView(withId(R.id.rv_recipe_steps_master_list)).perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));
            //check after selecting last step
            matcher = "Finishing Steps";
            onView(withId(R.id.cv_recipe_steps_short_description)).check(matches(withText(matcher)));

        }
        // Clean up
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    private void launchActivity() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, RecipeStepsActivity.class);
        try {
            String s = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
            ArrayList<Recipe> recipeArrayList = RecipesJsonUtils.getRecipeDetailsFromJson(s);
            intent.putExtra(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS, recipeArrayList.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recipeStepsActivityTestRule.launchActivity(intent);
    }
}
