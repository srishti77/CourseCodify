package com.srishti.srish.coursecodify_v1;

import android.support.test.rule.ActivityTestRule;
import android.widget.SeekBar;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
//import android.support.test.rule.ActivityTestRule;
import static org.junit.Assert.*;

/**
 * Created by User on 27/01/2018.
 * Check if the any one of the present calendar is saved or not.
 */
public class CalendarPreferenceSettingTest {

    @Rule
    public ActivityTestRule<NavigationDrawerActivity> activityTestRule = new
            ActivityTestRule<NavigationDrawerActivity>(NavigationDrawerActivity.class);

    private NavigationDrawerActivity nActivity = null;

    @Test
   public void testCalendarExistLaunch(){
        nActivity = activityTestRule.getActivity();
        assertNotNull(nActivity.calendarNamesList);

    }

    @After
    public void tearDown() throws Exception{
        nActivity = null;

    }
}