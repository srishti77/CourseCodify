package com.srishti.srish.coursecodify_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewAllMaterialActivity extends AppCompatActivity {

    CreateDirectories createDirectories = new CreateDirectories();
    static Spinner spinnerListOfEvents;
    List notesList = new ArrayList<String>();
    List recordingsList = new ArrayList<String>();

    ArrayAdapter notesArrayAdapter;

    ArrayAdapter recordingsArrayAdapter;

    ListView listViewImages, listViewNotes, listViewRecordings;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    ArrayList<String> imageName = new ArrayList<>();
    Drawable drawableImage;
    ListSingleImageAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_material);

        spinnerListOfEvents = (Spinner) findViewById(R.id.listOfAllEvents);
        List listOfevents = new ArrayList<>();

        Intent intent = getIntent();
        String event = intent.getStringExtra("CalendarEvent");
        final String materialType = intent.getStringExtra("MaterialType");

        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));
        Log.i("Count of Events", listOfevents.size()+ "");
        ArrayAdapter<String> arrayAdapterListOfEvents = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listOfevents );
        arrayAdapterListOfEvents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListOfEvents.setAdapter(arrayAdapterListOfEvents);


        if(event != null){
            Log.i("Inside ViewAllMaterial", event+"");
            spinnerListOfEvents.setSelection(arrayAdapterListOfEvents.getPosition(event));
        }

        listViewImages = (ListView) findViewById(R.id.listOfImages);
        listViewNotes = (ListView) findViewById(R.id.listOfNotes);
        listViewRecordings = (ListView) findViewById(R.id.listOfRecordings);

        recordingsList.clear();
        recordingsList.addAll(createDirectories
                    .readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Recordings"));
        Log.i("Recordings count",recordingsList.size()+"");
        notesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recordingsList);
        listViewRecordings.setAdapter(notesArrayAdapter);


        notesList.clear();
        notesList.addAll(createDirectories
                .readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Notes"));
        Log.i("Recordings count",notesList.size()+"");
        recordingsArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList);
        listViewNotes.setAdapter(recordingsArrayAdapter);




        arrayAdapter = new ListSingleImageAdapter(this, drawables, imageName);
        listViewImages.setAdapter(arrayAdapter);

        spinnerListOfEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("OnChange Listener", "called");
                recordingsList.clear();
                recordingsList.addAll(createDirectories
                        .readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Recordings"));

                  recordingsArrayAdapter.notifyDataSetChanged();

                notesList.clear();
                notesList.addAll(createDirectories
                        .readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Notes"));

                notesArrayAdapter.notifyDataSetChanged();


                if(createDirectories.getCurrentFile("/CourseCodify/"+spinnerListOfEvents.getSelectedItem()+"/Images").listFiles() != null) {

                    for (File file : createDirectories.getCurrentFile("/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images").listFiles()) {
                        if (file.isFile()) {

                            drawableImage = Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images/" + file.getName());

                            drawables.add(drawableImage);

                            imageName.add(file.getName());
                            arrayAdapter.notifyDataSetChanged();

                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Position of ListItem", i+"");
                Intent intent = new Intent(ViewAllMaterialActivity.this, TakeNotesActivity.class);
                Log.i("File Name:", ViewListOfNotesActivity.notesTitles.get(i));

                String notesContent = createDirectories.readContentOfNotesFile(spinnerListOfEvents.getSelectedItem()+"/Notes/"+ViewListOfNotesActivity.notesTitles.get(i)).toString();
                intent.putExtra("NoteId", i);
                intent.putExtra("NotesContent", notesContent);
                startActivity(intent);
            }
        });

        listViewRecordings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                Intent intent = new Intent(ViewAllMaterialActivity.this, RecordingActivity.class);
                startActivity(intent);
            }

        });

        listViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                Intent intent = new Intent(ViewAllMaterialActivity.this, ViewListOfPicturesActivity.class);
                startActivity(intent);
            }

        });

    }

}
