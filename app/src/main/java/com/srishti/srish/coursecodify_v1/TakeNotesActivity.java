package com.srishti.srish.coursecodify_v1;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class TakeNotesActivity extends AppCompatActivity {

    EditText notesTitle;
    EditText notesBody;
    ArrayList<String> notesTitles = new ArrayList<String>();
    ArrayList<String> notesBodies = new ArrayList<String>();

    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(TakeNotesActivity.this);
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notesTitle = (EditText) findViewById(R.id.titleNotes);
        notesBody = (EditText) findViewById(R.id.notesBody);


        Intent intent = getIntent();
        noteId = intent.getIntExtra("NoteId", -1);
        Log.i("NoteId ", noteId + "");

        if (noteId != -1) {
            notesTitle.setText(ViewListOfNotesActivity.notesTitles.get(noteId).toString());
            notesBody.setText(ViewListOfNotesActivity.notesBodies.get(noteId).toString());
        }


        else{
            noteId= 0;
            ViewListOfNotesActivity.notesTitles.add("Text0");
            ViewListOfNotesActivity.notesBodies.add(" ");
        }



       try {
            TextWatcher textWatcherTitle = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(s == ""){
                        ViewListOfNotesActivity.notesTitles.set(noteId, "Text"+noteId);
                    }
                    else{
                        //ViewListOfNotesActivity.notesTitles.add("Text0");
                        ViewListOfNotesActivity.notesTitles.set(noteId,s+ "");
                    }

                  if(noteId > 0){
                        ViewListOfNotesActivity.arrayAdapter.notifyDataSetChanged();
                  }

                }


                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            notesTitle.addTextChangedListener(textWatcherTitle);
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "The title is blank", Toast.LENGTH_LONG).show();

        }

        TextWatcher notesBodytextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(charSequence == ""){
                    ViewListOfNotesActivity.notesBodies.set(noteId, "Text"+noteId);
                }

                else{
                    //ViewListOfNotesActivity.notesTitles.add("Text0");
                    ViewListOfNotesActivity.notesBodies.set(noteId,charSequence+ "");
                }

                if(noteId  > 0) {
                    ViewListOfNotesActivity.arrayAdapter.notifyDataSetChanged();

                }
            }

             @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        notesBody.addTextChangedListener(notesBodytextWatcher);
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

            String currentEvent = getCalendarDetails.getCurrentEvent();
            /*
            Ask Permission to go to the calendar App
             */
            if (currentEvent == null) {

                AlertDialogs alertDialogs = new AlertDialogs(TakeNotesActivity.this);
                alertDialogs.askPermissionToGoToCalendar();

            } else {
                Intent intent = new Intent(TakeNotesActivity.this, ViewListOfNotesActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Saved here..", Toast.LENGTH_LONG).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }


}
