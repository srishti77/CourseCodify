package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class TakeNotesActivity extends CourseCodifyHelper {

    EditText notesTitle;
    EditText notesBody;
    ArrayList<String> notesTitles = new ArrayList<String>();
    ArrayList<String> notesBodies = new ArrayList<String>();

    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(TakeNotesActivity.this);
    CreateDirectories createDirectories = new CreateDirectories();
    String noteName;
    String notesContent;
    String currentSelected, selectedCurrentEvent;

    ArrayList<String> currentEvent = new ArrayList<String>();
    boolean createdEvent = false;
    private static final int REQUEST_FileAccess_PERMISSION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesTitle = (EditText) findViewById(R.id.titleNotes);
        notesBody = (EditText) findViewById(R.id.notesBody);

        Intent intent = getIntent();
        noteName = intent.getStringExtra("NoteName");
        notesContent = intent.getStringExtra("NotesContent");
        currentSelected = intent.getStringExtra("CurrentSelected");

        Log.i("NoteId ", noteName + "");

        if (noteName != null) {
            Log.i("Notes returned", notesContent);
            notesTitle.setText(noteName);
            notesBody.setText(notesContent);
        } else {
            noteName = "";
            notesTitles.add("");
            notesBodies.add("");
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_take_notes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.home)
        {
            this.finish();
            return true;
        }
        if(id == R.id.saveNotes) {

            if(notesTitle.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Please give the notes Title", Toast.LENGTH_LONG).show();
            }
            else {

            /*
            Ask Permission to go to the calendar App
             */

            if(noteName.contains(".txt")){

                if(currentSelected != null){


                    createDirectories.createCourseCodifyFile();
                    createDirectories.createEventFolder(currentSelected);
                    createDirectories.createSubFolder(currentSelected, "Notes");
                    File newFile = createDirectories.saveMaterial(currentSelected, "Notes", notesTitle.getText().toString(), notesBody.getText().toString());
                    MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{newFile.toString()}, null, null);
                    Intent intent = new Intent(TakeNotesActivity.this, AllListActivity.class);
                    intent.putExtra("Material","Notes");
                    intent.putExtra("CalendarEvent", currentSelected);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();

                }

                else{

                    Log.i("Selected Event", "is null");
                }

            }
              else{
                currentEvent = getCalendarDetails.getCurrentEvent();
                if (currentEvent.isEmpty()) {

                    AlertDialogs alertDialogs = new AlertDialogs(TakeNotesActivity.this);
                    alertDialogs.askPermissionToGoToCalendar();
                    createdEvent= true;

                } else {
                    if(currentEvent.size() > 1){

                        AlertDialog.Builder builder = new AlertDialog.Builder(TakeNotesActivity.this);
                        builder.setTitle("Choose the event To Save Images");
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                this,
                                android.R.layout.simple_list_item_1,
                                currentEvent );


                        builder.setSingleChoiceItems(arrayAdapter, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        selectedCurrentEvent = currentEvent.get(i);
                                        Toast.makeText(getApplicationContext(), "Selected Event "+ selectedCurrentEvent,Toast.LENGTH_LONG).show();

                                    }
                                });

                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                save();
                                Intent intent = new Intent(TakeNotesActivity.this, AllListActivity.class);
                                intent.putExtra("Material", "Notes");
                                intent.putExtra("CalendarEvent", selectedCurrentEvent);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();
                            }
                        });
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }

                    else{

                        selectedCurrentEvent = currentEvent.get(0);

                        save();
                        Intent intent = new Intent(TakeNotesActivity.this, AllListActivity.class);
                        intent.putExtra("Material", "Notes");
                        intent.putExtra("CalendarEvent", selectedCurrentEvent);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();
                    }



                }

            }
            }
        }

        if(id == R.id.goToViewNotes){
            Intent intent = new Intent(TakeNotesActivity.this, AllListActivity.class);
            intent.putExtra("Material","Notes");
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FileAccess_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(TakeNotesActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void save(){
        createDirectories.createCourseCodifyFile();
        createDirectories.createEventFolder(selectedCurrentEvent);
        createDirectories.createSubFolder(selectedCurrentEvent, "Notes");
        File notesName = createDirectories.saveMaterial(selectedCurrentEvent, "Notes", notesTitle.getText().toString() + ".txt", notesBody.getText().toString());
        MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{notesName.toString()}, null, null);
    }

}
