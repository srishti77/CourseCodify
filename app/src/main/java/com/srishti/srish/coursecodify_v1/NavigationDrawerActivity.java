package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/*
This is our main class.
It consist of calendar and the list of today's event
 */
public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Lets try

    DirectoryExpandableListAdapter directoryExpandableListAdapter;
    static ArrayList<String> listOfEvents_Today = new ArrayList<String>();
    static ArrayList<String> listOfSubDirectory = new ArrayList<String>();

    static List<String> calendarNamesList = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    SharedPreferences sharedPreferences;
    boolean firstInstallation = true;

    CreateDirectories create = new CreateDirectories();
    final GetCalendarDetails getCalendarName = new GetCalendarDetails(NavigationDrawerActivity.this);

    static boolean calendarPermission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



            //Here we add Man Activity

        listOfSubDirectory.clear();
        listOfSubDirectory.add("Images");
        listOfSubDirectory.add("Notes");
        listOfSubDirectory.add("Recordings");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }





            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NavigationDrawerActivity.this);
            firstInstallation = sharedPreferences.getBoolean("FIRST_RUN", false);

            if (!firstInstallation) {
                sharedPreferences.edit().putBoolean("FIRST_RUN", true).commit();
                create.createCourseCodifyFile();

                AlertDialog.Builder goToSettings = new AlertDialog.Builder(NavigationDrawerActivity.this);
                goToSettings.setMessage("Go to Settings to choose Calendar?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                calendarNamesList.clear();
                                calendarNamesList.addAll(getCalendarName.getAllCalendarName());

                                Log.i("Length of Calendarnames", calendarNamesList.size() + "");
                                Intent settingsIntent = new Intent(NavigationDrawerActivity.this, SettingActivity.class);
                                startActivity(settingsIntent);
                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alertForSettingsConfig = goToSettings.create();
                alertForSettingsConfig.show();
            }
            // getCalendarName = new GetCalendarDetails(NavigationDrawerActivity.this);


        /*
        when no calendar is selected we want user to select the calendar
         */
        calendarNamesList.clear();
        calendarNamesList.addAll(getCalendarName.getAllCalendarName());
            if (!getCalendarName.getAllCalendarName().isEmpty()) {
                Log.i("sharedP selectedIndex", (sharedPreferences.getInt("SelectedIndex", -1) - 1)+"");
                listOfEvents_Today.clear();
                listOfEvents_Today.addAll(getCalendarName.getevents(calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", -1) - 1)));
                directoryExpandableListAdapter = new DirectoryExpandableListAdapter(NavigationDrawerActivity.this, listOfEvents_Today, listOfSubDirectory);

            }

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_LV);

        expandableListView.setAdapter(directoryExpandableListAdapter);

        TextView textView = (TextView) findViewById(R.id.todayEvent);

        textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //getCalendarName.getevents(calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", -1)-1));
                    getCalendarName.getCurrentEvent();
                }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(NavigationDrawerActivity.this, SettingActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.nav_camera) {

            Intent intent = new Intent(NavigationDrawerActivity.this, TakeImagesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notes) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_Forum) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    whenPermissionIsGranted();

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

    public void whenPermissionIsGranted(){


    }

}
