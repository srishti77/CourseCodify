package com.srishti.srish.coursecodify_v1;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AllListActivity extends AppCompatActivity {

    String selectedEvent,material;

    ViewImagesFragment viewImagesFragment;
    NotesFragment notesFragment;
    AllMaterialFragment allMaterialFragment;
    RecordingFragment recordingsFragment;
    static Spinner spinnerListOfEvents;
    CreateDirectories createDirectories = new CreateDirectories();
    boolean firsttime = true;
    FragmentTransaction transaction;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);

        Intent intent = getIntent();
        selectedEvent = intent.getStringExtra("CalendarEvent");
        material = intent.getStringExtra("Material");

        viewImagesFragment = new ViewImagesFragment();
        notesFragment = new NotesFragment();

        recordingsFragment = new RecordingFragment();

        allMaterialFragment = new AllMaterialFragment();

        List listOfevents = new ArrayList<>();

        spinnerListOfEvents = (Spinner) findViewById(R.id.spinner);

        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));
        Log.i("Count of Events", listOfevents.size()+ "");

        ArrayAdapter<String> arrayAdapterListOfEvents = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listOfevents );
        arrayAdapterListOfEvents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListOfEvents.setAdapter(arrayAdapterListOfEvents);

        if(selectedEvent != null){
            Log.i("All List Activity", selectedEvent);
            if(listOfevents.contains(selectedEvent))
            spinnerListOfEvents.setSelection(arrayAdapterListOfEvents.getPosition(selectedEvent));
        }

        bundle = new Bundle();
       
        bundle.putString("Folder", spinnerListOfEvents.getSelectedItem() +"" );
        Log.i("Spinner", spinnerListOfEvents.getSelectedItem()+"");

        transaction =  getSupportFragmentManager().beginTransaction();


        if(material.equals("Images")){

            transaction.replace(R.id.frameImages1, viewImagesFragment);
            viewImagesFragment.setArguments(bundle);
        }

        else if(material.equals("Notes")){
           transaction.add(R.id.frameNotes1, notesFragment);
            notesFragment.setArguments(bundle);
        }

        else if(material.equals("Recordings")){
            transaction.replace(R.id.frameRecordings1, recordingsFragment);
            recordingsFragment.setArguments(bundle);
        }

        else if(material.equals("All Materials")){
           /* transaction.replace(R.id.frameImages1, viewImagesFragment);
            viewImagesFragment.setArguments(bundle);
            transaction.replace(R.id.frameNotes1, notesFragment);

            transaction.replace(R.id.frameRecordings1,recordingsFragment);
            */

            transaction.replace(R.id.frameImages1, allMaterialFragment);

            allMaterialFragment.setArguments(bundle);

            /*viewImagesFragment.setArguments(bundle);
            notesFragment.setArguments(bundle);
            recordingsFragment.setArguments(bundle);
            */
        }

        transaction.commit();

            spinnerListOfEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    String event = spinnerListOfEvents.getSelectedItem().toString();
                  if(material.equals("Images")){
                      viewImagesFragment.onSpinnerChanged(event);

                  }

                  else if(material.equals("Notes")){
                          notesFragment.onSpinnerChanged(event);
                  }

                  else if(material.equals("Recordings")){
                      recordingsFragment.onSpinnerChanged(event);
                  }

                  else if(material.equals("All Materials")){
                      Log.i("spinner change", "called");
                      allMaterialFragment.onSpinnerChanged(event);

                  }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
}
