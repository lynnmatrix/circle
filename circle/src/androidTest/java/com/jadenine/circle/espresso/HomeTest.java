package com.jadenine.circle.espresso;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jadenine.circle.R;
import com.jadenine.circle.ui.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by linym on 6/3/15.
 */
@RunWith(AndroidJUnit4.class)
public class HomeTest {
    @Rule
    public ActivityTestRule<HomeActivity> homeActivityActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testDrawer() {
        openDrawer(R.id.nav_drawer);

        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));

        closeDrawer(R.id.nav_drawer);

        onView(withId(R.id.nav_view)).check(matches(not(isDisplayed())));
    }
}
