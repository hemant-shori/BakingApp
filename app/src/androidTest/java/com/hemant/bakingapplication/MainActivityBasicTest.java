package com.hemant.bakingapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import com.hemant.bakingapplication.activities.IngredientsDetailsActivity;
import com.hemant.bakingapplication.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

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
    public final IntentsTestRule<MainActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(15, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(8, TimeUnit.SECONDS);
    }

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void CheckRecipeRecyclerViewIsDisplayed_ClickOnFirstRecipe_MatchIntentHasExtras() {
        IdlingResource idlingResource = registerIdlingResourceWithWaitTime(5);
        //check Recycler View is displayed
        onView(withId(R.id.recipes_recycler_view)).check(matches(isDisplayed()));
        //click on 1st item of recycler View
        onView(withId(R.id.recipes_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Verify that an intent to the dialer was sent with the correct action, phone
        // number and package. Think of Intents intended API as the equivalent to Mockito's verify.
        intended(allOf(
                hasExtraWithKey(IngredientsDetailsActivity.SELECTED_RECIPE_DETAILS),
                toPackage(mainActivityIntentsTestRule.getActivity().getPackageName())));

        // Clean up
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
