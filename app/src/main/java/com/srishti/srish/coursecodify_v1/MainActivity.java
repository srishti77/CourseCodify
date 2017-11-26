package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    DirectoryExpandableListAdapter directoryExpandableListAdapter;
    static ArrayList<String> listOfEvents_Today = new ArrayList<String>();
    static ArrayList<String> listOfSubDirectory = new ArrayList<String>();

    static List<String> calendarNamesList = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    CalendarPreference calendarPreference = new CalendarPreference();
    GetCalendarDetails getCalendarName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listOfSubDirectory.clear();
        listOfSubDirectory.add("Images");
        listOfSubDirectory.add("Notes");
        listOfSubDirectory.add("Recordings");



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        getCalendarName = new GetCalendarDetails(MainActivity.this);
        calendarNamesList.clear();
        calendarNamesList.addAll(getCalendarName.getAllCalendarName());
        listOfEvents_Today.clear();
        listOfEvents_Today.addAll(getCalendarName.getAllEventsFromPreferredCalendar(calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", 0)-1)));


        directoryExpandableListAdapter = new DirectoryExpandableListAdapter(MainActivity.this, listOfEvents_Today, listOfSubDirectory);
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_LV);

        expandableListView.setAdapter(directoryExpandableListAdapter);

        TextView textView = (TextView) findViewById(R.id.todayEvent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PERMISSION_GRANTED) {

                    Log.i("Permission Granted","Permission Granted");


                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR))
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("Read Contact Permission")
                                .setMessage("You need to grant read calendar Permission").show();
                    }
                    else {


                        new AlertDialog.Builder(this)
                                .setTitle("Read Contact Permission Denied")
                                .setMessage("You need to grant read calendar Permission Denied").show();
                    }

                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.refresh:
                Intent intent1 = getIntent();
                finish();
                startActivity(intent1);

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
