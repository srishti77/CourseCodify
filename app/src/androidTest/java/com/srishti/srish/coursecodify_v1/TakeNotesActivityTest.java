package com.srishti.srish.coursecodify_v1;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by User on 28/01/2018.
 */
public class TakeNotesActivityTest {
    @Rule
    public ActivityTestRule<TakeNotesActivity> activityTestRule = new
            ActivityTestRule<TakeNotesActivity>(TakeNotesActivity.class);

    private TakeNotesActivity takeNotesActivity = null;


    //folder exist of not when the save is performed if the
    @Test
    public void save() throws Exception {

        takeNotesActivity = activityTestRule.getActivity();
        if(takeNotesActivity.currentEvent.size() > 0)
            assertNotNull(takeNotesActivity.selectedCurrentEvent);
    }

}