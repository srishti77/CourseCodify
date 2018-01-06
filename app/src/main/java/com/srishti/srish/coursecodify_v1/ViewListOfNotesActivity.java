package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfNotesActivity extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> notesTitles = new ArrayList<String>();

    static ArrayList<String> notesBodies = new ArrayList<String>();
    CreateDirectories createDirectories = new CreateDirectories();
    static Spinner spinnerListOfEvents;
    String selectedEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_of_notes);

        spinnerListOfEvents = (Spinner) findViewById(R.id.notes_listOfEvents);
        List listOfevents = new ArrayList<>();

        Intent intent = getIntent();
        String event = intent.getStringExtra("CalendarEvent");

        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));
        Log.i("Count of Events", listOfevents.size()+ "");
        ArrayAdapter<String> arrayAdapterListOfEvents = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listOfevents );
        arrayAdapterListOfEvents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListOfEvents.setAdapter(arrayAdapterListOfEvents);

        Log.i("Selected Item",spinnerListOfEvents.getSelectedItem()+ "");

        if(event != null){
            Log.i("ViewListOfNotesActivity", event);
            Log.i("Position of newyear", listOfevents.indexOf(event)+"");
            spinnerListOfEvents.setSelection(arrayAdapterListOfEvents.getPosition(event));
        }
        //ListView -- Lists all the notes

        final ListView listView = (ListView) findViewById(R.id.listView);
        notesTitles.clear();
        notesTitles.addAll(createDirectories.readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Notes"));
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesTitles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Position of ListItem", i+"");
                Intent intent = new Intent(ViewListOfNotesActivity.this, TakeNotesActivity.class);
                Log.i("File Name:", ViewListOfNotesActivity.notesTitles.get(i));

                String notesContent = createDirectories.readContentOfNotesFile(spinnerListOfEvents.getSelectedItem()+"/Notes/"+ViewListOfNotesActivity.notesTitles.get(i)).toString();
                intent.putExtra("NoteName", ViewListOfNotesActivity.notesTitles.get(i)+"");
                intent.putExtra("NotesContent", notesContent);
                startActivity(intent);
            }
        });



        spinnerListOfEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                notesTitles.clear();
                notesTitles.addAll(createDirectories.readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Notes"));

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final PopupMenu popupMenu = new PopupMenu(ViewListOfNotesActivity.this, listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

                popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.delete){
                            deleteNotes( position, selectedEvent(), notesTitles );

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            shareNotes(selectedEvent(), notesTitles.get(position), ViewListOfNotesActivity.this);

                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });

    }

    public String selectedEvent(){
        selectedEvent = (String)spinnerListOfEvents.getSelectedItem();
        return selectedEvent;
    }

    public void deleteNotes(int position, String event, ArrayList<String> material ){

        File directory = createDirectories.getCurrentFile("CourseCodify/"+event+"/Notes");
        for(File file : directory.listFiles()){

            if(material.get(position).compareTo(file.getName()) == 0){
                file.delete();
                material.remove(position);
            }
        }


    }

    public void shareNotes( String event, String notesName, Context context){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CourseCodify/"+event+"/Notes/"+notesName);
        Intent myShareIntent = new Intent();
        myShareIntent.setAction(Intent.ACTION_SEND);


        Uri fileURI = FileProvider.getUriForFile(context ,"com.srishti.srish.coursecodify_v1", file);
        Log.i("The path..", fileURI+"");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI );
        myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myShareIntent.setType("text/*");
        startActivity(Intent.createChooser(myShareIntent, "Share"));

    }

}
