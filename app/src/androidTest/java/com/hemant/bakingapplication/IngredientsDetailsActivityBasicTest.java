package com.hemant.bakingapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;

import com.hemant.bakingapplication.activities.IngredientsDetailsActivity;
import com.hemant.bakingapplication.activities.RecipeStepsActivity;
import com.hemant.bakingapplication.models.Recipe;
import com.hemant.bakingapplication.utils.NetworkUtils;
import com.hemant.bakingapplication.utils.RecipesJsonUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class IngredientsDetailsActivityBasicTest {

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
    public final IntentsTestRule<IngredientsDetailsActivity> ingredientsDetailsActivityIntentsTestRule = new IntentsTestRule<>(IngredientsDetailsActivity.class, true, false);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(15, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(8, TimeUnit.SECONDS);
    }


    @Test
    public void CheckRecipeRecyclerViewIsDisplayed_ClickOnFirstRecipe_MatchIntentHasExtras() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, IngredientsDetailsActivity.class);
        try {
            String s = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
            ArrayList<Recipe> recipeArrayList = RecipesJsonUtils.getRecipeDetailsFromJson(s);
            intent.putExtra(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS, recipeArrayList.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ingredientsDetailsActivityIntentsTestRule.launchActivity(intent);
        //check Recycler View is displayed
        IdlingResource idlingResource = registerIdlingResourceWithWaitTime(3);
        onView(withId(R.id.rv_ingredients)).check(matches(isDisplayed()));
        onView(withId(R.id.fab_ingredient_open_recipe_steps)).perform(click());
        //Match ToolBar Title
        matchToolbarTitle("Nutella Pie");
        //check the intent Details after the click on steps Button It should Open RecipeStepsActivity
        intended(allOf(
                hasComponent(RecipeStepsActivity.class.getName()),
                hasExtraWithKey(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS),
                toPackage(ingredientsDetailsActivityIntentsTestRule.getActivity().getPackageName())));

        // Clean up
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}
