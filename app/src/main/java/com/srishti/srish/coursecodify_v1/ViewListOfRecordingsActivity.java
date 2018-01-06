package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfRecordingsActivity extends AppCompatActivity {

    CreateDirectories createDirectories = new CreateDirectories();
    static Spinner spinnerListOfEvents;
    static ArrayList<String> recordings = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static boolean onPlayStart = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_of_recordings);

        spinnerListOfEvents = (Spinner) findViewById(R.id.recording_listOfEvents);
        List listOfevents = new ArrayList<>();

        Intent intent = getIntent();
        String event = intent.getStringExtra("CalendarEvent");

        listOfevents.clear();
        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));

        Log.i("Count of Events--", listOfevents.size()+"");
        ArrayAdapter<String> arrayAdapterListOfEvents = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listOfevents );
        arrayAdapterListOfEvents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListOfEvents.setAdapter(arrayAdapterListOfEvents);


        if(event != null){
            Log.i("ViewListOfNotesActivity", event);
            Log.i("Position of newyear", listOfevents.indexOf(event)+"");
            spinnerListOfEvents.setSelection(arrayAdapterListOfEvents.getPosition(event));
        }

        final ListView listView = (ListView) findViewById(R.id.listOfRecordings);
        recordings.clear();;
        //recordings.addAll(createDirectories.readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Recordings"));
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recordings);
        listView.setAdapter(arrayAdapter);

        spinnerListOfEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("SpinnerOnChange ", "Listener called");
                recordings.clear();
                Log.i("selectedItem from list", spinnerListOfEvents.getSelectedItem()+ "");
                recordings.addAll(createDirectories.readAllDirectoryName(spinnerListOfEvents.getSelectedItem()+ "", "Recordings"));

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


             letsPlay(view, i, recordings.get(i));

         }

     });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                final PopupMenu popupMenu = new PopupMenu(ViewListOfRecordingsActivity.this, listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

                popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.delete){
                            deleteRecordings( position, spinnerListOfEvents.getSelectedItem().toString(), recordings );

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            shareRecordings(spinnerListOfEvents.getSelectedItem().toString(), recordings.get(position), ViewListOfRecordingsActivity.this);

                        }
                        return true;
                    }
                });

                popupMenu.show();

                return true;
            }
        });

    }


    public void letsPlay(View view, int i, String recording ){


        view = getLayoutInflater().inflate(R.layout.bottom_sheet_recordings_play_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewListOfRecordingsActivity.this);
        bottomSheetDialog.setContentView(view);

        TextView recordingName = (TextView) view.findViewById(R.id.Name_Of_Recordings);
        recordingName.setText(recording);

        final ImageView playOrpause = (ImageView) view.findViewById(R.id.play_pause_recording);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        final TextView timeOfSeekBar = (TextView) view.findViewById(R.id.timeOfSeekBar);
        bottomSheetDialog.show();
        final MediaPlayer mediaPlayer = new MediaPlayer();
        try{

            String outputFile = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/CourseCodify/" + spinnerListOfEvents.getSelectedItem()
                    + "/Recordings/" + recording;

            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (onPlayStart) {
                    playOrpause.setImageResource(R.mipmap.pause);
                    onPlayStart = false;

                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration() / 1000);
                    final Handler handler = new Handler();

                    Runnable updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekBar.setProgress(currentPosition);
                                timeOfSeekBar.setText(currentPosition + "/" + (mediaPlayer.getDuration() / 1000));
                            }
                            handler.postDelayed(this, 0);
                        }
                    };
                    handler.postDelayed(updateSeekBar, 0);

                }
                else {

                    mediaPlayer.pause();

                    playOrpause.setImageResource(R.mipmap.play);
                    onPlayStart = true;
                }

            }
        });

    }

    public void deleteRecordings(int position, String event, ArrayList<String> filename ){

        File directory = createDirectories.getCurrentFile("CourseCodify/"+event+"/Recordings");
        for(File file : directory.listFiles()){

            if(filename.get(position).compareTo(file.getName()) == 0){
                file.delete();
                filename.remove(position);
            }
        }


    }

    public void shareRecordings( String event, String recordingsName, Context context){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CourseCodify/"+event+"/Recordings/"+recordingsName);
        Intent myShareIntent = new Intent();
        myShareIntent.setAction(Intent.ACTION_SEND);


        Uri fileURI = FileProvider.getUriForFile(context ,"com.srishti.srish.coursecodify_v1", file);
        Log.i("The path..", fileURI+"");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI );
        myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myShareIntent.setType("audio/*");
        startActivity(Intent.createChooser(myShareIntent, "Share"));

    }

}
