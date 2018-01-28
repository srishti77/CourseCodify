package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuInflater;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DirectoryExpandableListAdapter directoryExpandableListAdapter;
    static ArrayList<String> listOfEvents_Today = new ArrayList<String>();
    static ArrayList<String> listOfSubDirectory = new ArrayList<String>();
    static List<String> calendarNamesList = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 17;
    SharedPreferences sharedPreferences;
    boolean firstInstallation = true;
    static String currentSelectedEvent;
    CreateDirectories create = new CreateDirectories();
    final GetCalendarDetails getCalendarName = new GetCalendarDetails(NavigationDrawerActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listOfSubDirectory.clear();
        listOfSubDirectory.add("All Materials");
        listOfSubDirectory.add("Images");
        listOfSubDirectory.add("Notes");
        listOfSubDirectory.add("Recordings");

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED)||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED))

        {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_CALENDAR,

                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA },  MY_PERMISSIONS_REQUEST_READ_CONTACTS);

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

        calendarNamesList.clear();
        calendarNamesList.addAll(getCalendarName.getAllCalendarName());
        if (!getCalendarName.getAllCalendarName().isEmpty()) {
                Log.i("sharedP selectedIndex", (sharedPreferences.getInt("SelectedIndex", -1) - 1)+"");
                listOfEvents_Today.clear();
                listOfEvents_Today.addAll(getCalendarName.getevents(calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", -1) - 1), null));
                directoryExpandableListAdapter = new DirectoryExpandableListAdapter(NavigationDrawerActivity.this, listOfEvents_Today, listOfSubDirectory);

        }

        View hView = navigationView.getHeaderView(0);
        TextView textView1 = (TextView) hView.findViewById(R.id.CalendarName);


        if(!calendarNamesList.isEmpty()){
            currentSelectedEvent = calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", -1) - 1);
            textView1.setText(currentSelectedEvent);
        }


        final ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_LV);

        expandableListView.setAdapter(directoryExpandableListAdapter);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        calendarView.setDate(Calendar.getInstance().getTimeInMillis(),true,true);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Log.i("Date change", "Listener is called");
                Log.i(" selected date"+i+" ", i1+" "+ i2);
                Time time = new Time();
                time.set(00,00,01,i2,i1,i);


                listOfEvents_Today.clear();
                listOfEvents_Today.addAll(getCalendarName.getevents(calendarNamesList.get(sharedPreferences.getInt("SelectedIndex", -1) - 1), time));
                directoryExpandableListAdapter = new DirectoryExpandableListAdapter(NavigationDrawerActivity.this, listOfEvents_Today, listOfSubDirectory);
                expandableListView.setAdapter(directoryExpandableListAdapter);

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

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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
          Intent intent = new Intent(NavigationDrawerActivity.this, TakeNotesActivity.class);
          startActivity(intent);

        } else if (id == R.id.nav_audioRecord) {
          Intent intent = new Intent(NavigationDrawerActivity.this, RecordingActivity.class);
          startActivity(intent);

        } else if (id == R.id.nav_setting) {
          Intent intent = new Intent(NavigationDrawerActivity.this, SettingActivity.class);
          startActivity(intent);

        }
      else if (id == R.id.nav_material) {
          Intent intent = new Intent(NavigationDrawerActivity.this, AllListActivity.class);
          intent.putExtra("Material", "All Materials");
          startActivity(intent);

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

            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0
                        && grantResults[0] == PERMISSION_GRANTED) {

                    Log.i("Permission Granted","Permission Granted");

                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("RECORD Permission")
                                .setMessage("You need to grant RECORD Permission").show();
                    }
                    else {


                        new AlertDialog.Builder(this)
                                .setTitle("RECORD Permission Denied")
                                .setMessage("You need to grant RECORD Permission Denied").show();
                    }

                }
                break;

        }
    }


    @Override
    protected void onResume() {

        super.onResume();
    }
}
