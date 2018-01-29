package com.srishti.srish.coursecodify_v1;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by User on 29/01/2018.
 * Check if the material type is given in order to render proper fragment
 * If Material type is notes then NotesFragment should render
 * If Material type is Images then ViewImageFragment should render
 */
public class AllListActivityTest {
    @Rule
    public ActivityTestRule<AllListActivity> activityTestRule = new
            ActivityTestRule<AllListActivity>(AllListActivity.class);

    private AllListActivity allListActivity = null;

    @Test
    public void onCreate() throws Exception {
       allListActivity  = activityTestRule.getActivity();
        if(allListActivity.material != null)
        assertThat(allListActivity.material, anyOf(is("Notes"), is("Images"), is("Recordings"), is("All Materials")));
    }

}