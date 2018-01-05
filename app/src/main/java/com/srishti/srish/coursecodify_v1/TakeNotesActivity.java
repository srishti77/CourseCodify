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
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class TakeNotesActivity extends AppCompatActivity {

    EditText notesTitle;
    EditText notesBody;
    ArrayList<String> notesTitles = new ArrayList<String>();
    ArrayList<String> notesBodies = new ArrayList<String>();

    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(TakeNotesActivity.this);
    CreateDirectories createDirectories = new CreateDirectories();
    int noteId;
    String notesContent;
    String currentEvent;
    boolean createdEvent = false;
    private static final int REQUEST_FileAccess_PERMISSION = 300;
    ViewListOfNotesActivity viewListOfNotesActivity = new ViewListOfNotesActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesTitle = (EditText) findViewById(R.id.titleNotes);
        notesBody = (EditText) findViewById(R.id.notesBody);


        Intent intent = getIntent();
        noteId = intent.getIntExtra("NoteId", -1);
        notesContent = intent.getStringExtra("NotesContent");
        Log.i("NoteId ", noteId + "");

        if (noteId != -1) {
            Log.i("Notes returned", notesContent);
            notesTitle.setText(ViewListOfNotesActivity.notesTitles.get(noteId).toString());
            notesBody.setText(notesContent);
        } else {
            noteId = 0;
            ViewListOfNotesActivity.notesTitles.add("");
            ViewListOfNotesActivity.notesBodies.add("");
        }


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(TakeNotesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FileAccess_PERMISSION);
            return;
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

            if(ViewListOfNotesActivity.notesTitles.get(noteId).contains(".txt")){

                if(viewListOfNotesActivity.selectedEvent() != null){


                    createDirectories.createCourseCodifyFile();

                    createDirectories.createEventFolder(viewListOfNotesActivity.selectedEvent());

                    createDirectories.createSubFolder(viewListOfNotesActivity.selectedEvent(), "Notes");
                    File newFile = createDirectories.saveMaterial(viewListOfNotesActivity.selectedEvent(), "Notes", notesTitle.getText().toString(), notesBody.getText().toString());
                    MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{newFile.toString()}, null, null);

                    Intent intent = new Intent(TakeNotesActivity.this, ViewListOfNotesActivity.class);
                   // intent.putExtra("CalendarEvent", viewListOfNotesActivity.selectedEvent());
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();

                }

                else{

                    Log.i("Selected Event", "is null");
                }
                //String fileFullPath = "/CourseCodify/"+
            }
              else{
                currentEvent = getCalendarDetails.getCurrentEvent();
//                Log.i("CurrentEvent", currentEvent);
                if (currentEvent == null) {

                    AlertDialogs alertDialogs = new AlertDialogs(TakeNotesActivity.this);
                    alertDialogs.askPermissionToGoToCalendar();
                    createdEvent= true;

                } else {
                    //Added on 20/12/2017 creating coursecodify if it doesnot exist

                    //if(notesTitle)
                    createDirectories.createCourseCodifyFile();

                    createDirectories.createEventFolder(currentEvent);

                    createDirectories.createSubFolder(currentEvent, "Notes");
                    File newFile = createDirectories.saveMaterial(currentEvent, "Notes", notesTitle.getText().toString()+".txt", notesBody.getText().toString() );
                    MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{newFile.toString()}, null, null);

                    Intent intent = new Intent(TakeNotesActivity.this, ViewListOfNotesActivity.class);
                    intent.putExtra("CalendarEvent", currentEvent);
                    Log.i("Selected ele", currentEvent);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();

                }

            }
            }
        }

        if(id == R.id.goToViewNotes){
            Intent intent = new Intent(TakeNotesActivity.this, ViewListOfNotesActivity.class);
            //intent.putExtra("CalendarEvent", viewListOfNotesActivity.selectedEvent());
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

        if(createdEvent) {
            currentEvent = getCalendarDetails.getCurrentEvent();
            if (!notesTitle.equals("") || !notesBody.equals("")) {
                if (currentEvent != null) {
                    createDirectories.createCourseCodifyFile();
                    createDirectories.createEventFolder(currentEvent);
                    createDirectories.createSubFolder(currentEvent, "Notes");
                    createDirectories.createNoMedia();
                    File notesName = createDirectories.saveMaterial(currentEvent, "Notes", notesTitle.getText().toString() + ".txt", notesBody.getText().toString());
                    MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{notesName.toString()}, null, null);


                }
            }
            createdEvent = false;
        }
    }




}
