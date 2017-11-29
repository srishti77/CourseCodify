package com.srishti.srish.coursecodify_v1; /**
 * Created by User on 26/11/2017.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.srishti.srish.coursecodify_v1.R;

public class CalendarPreferenceSetting extends PreferenceFragment {


    CharSequence[] calendarNamesArray = NavigationDrawerActivity.calendarNamesList.
            toArray(new CharSequence[NavigationDrawerActivity.calendarNamesList.size()]);

    //String selectedCalendar= "1";
    CharSequence[] calendarIdsArray= new CharSequence[calendarNamesArray.length];

     String selectedCalendar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.calendar_preferences);

        //for storing the selected calendar in shared Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        final ListPreference listCalendarPreference = new ListPreference(preferenceScreen.getContext());
        preferenceCategory.setTitle("Settings");
        preferenceScreen.addPreference(preferenceCategory);
        listCalendarPreference.setTitle("Sync Calendar");

        listCalendarPreference.setEntries(calendarNamesArray);
        listCalendarPreference.setEntryValues(calendarIdsArray);
        listCalendarPreference.setValue(calendarIdsArray + "");
        preferenceScreen.addPreference(listCalendarPreference);

        /* Assigning the id to each of the calendar populated*/
        for(int i=0;i<calendarNamesArray.length;i++)
            calendarIdsArray[i] = (i+1)+"";

        Log.i("Print selected Index",sharedPreferences.getInt("SelectedIndex",0)+"" );

        if(sharedPreferences.getInt("SelectedIndex",0) != 0 && calendarNamesArray.length>0){
            Log.i("Inside", "if statement");
            try{
                listCalendarPreference.setValueIndex(sharedPreferences.getInt("SelectedIndex",0)-1);
                selectedCalendar = listCalendarPreference.getEntry()+"";

               // Log.i("selected Calendar", selectedCalendar+"");

            }
            catch(Exception e){
                Toast.makeText(getActivity().getApplicationContext(), "Problem with launching. Please try reinstalling the app", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        //Save the state if changed
        Preference.OnPreferenceChangeListener listCalendarPreference1 = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String getSelectedIndex = newValue.toString();
                Log.i("getSelectedIndex", getSelectedIndex);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sharedPreferences.edit().putInt("SelectedIndex", Integer.parseInt(getSelectedIndex)).commit();



                return true;
            }
        };

        listCalendarPreference.setOnPreferenceChangeListener(listCalendarPreference1);





    }

    public String getSelectedCalendar(){
        return selectedCalendar;
    }
}
