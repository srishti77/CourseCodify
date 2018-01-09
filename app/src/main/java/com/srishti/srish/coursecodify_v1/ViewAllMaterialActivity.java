package com.srishti.srish.coursecodify_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
    ArrayList notesList = new ArrayList<String>();
    ArrayList recordingsList = new ArrayList<String>();

    ArrayAdapter notesArrayAdapter;

    ArrayAdapter recordingsArrayAdapter;

    ListView listViewImages, listViewNotes, listViewRecordings;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    ArrayList<String> imageName = new ArrayList<>();
    Drawable drawableImage;
    ListSingleImageAdapter arrayAdapter;
    TextView recordingsTitle, notesTitle, imagesTitle;

    ViewListOfNotesActivity viewListOfNotesActivity = new ViewListOfNotesActivity();
    ViewListOfPicturesActivity viewListOfPicturesActivity = new ViewListOfPicturesActivity();
    ViewListOfRecordingsActivity viewListOfRecordingsActivity = new ViewListOfRecordingsActivity();
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
        recordingsTitle = (TextView) findViewById(R.id.RecordingsTitle);
        notesTitle = (TextView) findViewById(R.id.NotesTitle);
        imagesTitle = (TextView) findViewById(R.id.ImagesTitle);

        notesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList);
        listViewNotes.setAdapter(notesArrayAdapter);

        recordingsArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recordingsList);
        listViewRecordings.setAdapter(recordingsArrayAdapter);

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

                drawables.clear();
                imageName.clear();
                if(createDirectories.getCurrentFile("/CourseCodify/"+spinnerListOfEvents.getSelectedItem()+"/Images").listFiles() != null) {

                    for (File file : createDirectories.getCurrentFile("/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images").listFiles()) {
                        if (file.isFile()) {

                            drawableImage = Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images/" + file.getName());

                            drawables.add(drawableImage);

                            imageName.add(file.getName());

                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                hide();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ViewAllMaterialActivity.this, TakeNotesActivity.class);
                String notesContent = createDirectories.readContentOfNotesFile(spinnerListOfEvents.getSelectedItem()+"/Notes/"+ notesList.get(i)).toString();
                intent.putExtra("NoteName", notesList.get(i)+"");
                intent.putExtra("NotesContent", notesContent);
                startActivity(intent);
            }
        });

        listViewNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final PopupMenu popupMenu = popupMenu(listViewNotes);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        String event = spinnerListOfEvents.getSelectedItem()+"";
                        if(id == R.id.delete){
                            viewListOfNotesActivity.deleteNotes( position, event, notesList);

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            //viewListOfNotesActivity.shareNotes(spinnerListOfEvents.getSelectedItem().toString(),notesList.get(position).toString(),ViewAllMaterialActivity.this );
                           /* File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CourseCodify/"+spinnerListOfEvents.getSelectedItem()
                                    +"/Notes/"+notesList.get(position));
                            Intent myShareIntent = new Intent();
                            myShareIntent.setAction(Intent.ACTION_SEND);


                            Uri fileURI = FileProvider.getUriForFile(ViewAllMaterialActivity.this ,"com.srishti.srish.coursecodify_v1", file);
                            Log.i("The path..", fileURI+"");
                            myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI );
                            myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            myShareIntent.setType("text/*");
                            startActivity(Intent.createChooser(myShareIntent, "Share"));
                            */

                           viewListOfNotesActivity.shareNotes(event, notesList.get(position)+"", ViewAllMaterialActivity.this, item);
                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });




        listViewRecordings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

               // viewListOfRecordingsActivity.letsPlay(view, i, recordingsList.get(i)+"");
                Intent intent = new Intent(ViewAllMaterialActivity.this, RecordingsPlay.class);
                intent.putExtra("SelectedEvent", spinnerListOfEvents.getSelectedItem()+"");
                intent.putExtra("recording", recordingsList.get(position)+"");

                startActivity(intent);
            }

        });


        listViewRecordings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                final PopupMenu popupMenu = popupMenu(listViewNotes);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {
                            viewListOfRecordingsActivity.deleteRecordings(position, spinnerListOfEvents.getSelectedItem() + "", recordingsList);

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if (id == R.id.share) {
                            item = popupMenu.getMenu().findItem(id);
                          File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CourseCodify/" + spinnerListOfEvents.getSelectedItem()
                                    + "/Recordings/" + recordingsList.get(position));
                            Intent myShareIntent = new Intent();
                            myShareIntent.setAction(Intent.ACTION_SEND);


                            Uri fileURI = FileProvider.getUriForFile(ViewAllMaterialActivity.this, "com.srishti.srish.coursecodify_v1", file);
                            Log.i("The path..", fileURI + "");
                            myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
                            myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            myShareIntent.setType("audio/*");
                            startActivity(Intent.createChooser(myShareIntent, "Share"));
                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });


        listViewImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                byte[] byteArray = getImageToSend(position, drawables);
                Intent intent = new Intent(ViewAllMaterialActivity.this, ViewImageFullScreenActivity.class);
                intent.putExtra("ImageView",byteArray);
                startActivity(intent);
            }
        });

        listViewImages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final PopupMenu popupMenu = popupMenu(listViewImages);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {

                            viewListOfPicturesActivity.deleteImages(position,spinnerListOfEvents.getSelectedItem().toString(),drawables,imageName);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        if (id == R.id.share) {
                            item = popupMenu.getMenu().findItem(id);
                            viewListOfPicturesActivity.shareImages(item, ViewAllMaterialActivity.this.getContentResolver(),drawables.get(position));
                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });

    }

    public void hide(){
        if(recordingsList.size() != 0)
            recordingsTitle.setText("Recordings");
        else
            recordingsTitle.setText("");

        if(notesList.size() != 0)
            notesTitle.setText("Notes");
        else
            notesTitle.setText("");

        if(drawables.size() != 0)
            imagesTitle.setText("Images");
        else
            imagesTitle.setText("");
    }

    public static byte[] getImageToSend( int position,  ArrayList<Drawable> drawables1  ){
        Bitmap bitmap = ((BitmapDrawable) drawables1.get(position)).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }

    public PopupMenu  popupMenu(ListView listView){

        final PopupMenu popupMenu = new PopupMenu(ViewAllMaterialActivity.this, listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

        popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

        return popupMenu;
    }

}
