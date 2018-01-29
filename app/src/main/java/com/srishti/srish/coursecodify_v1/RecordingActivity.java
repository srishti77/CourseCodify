package com.srishti.srish.coursecodify_v1;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecordingActivity extends CourseCodifyHelper {

    MediaRecorder mediaRecorder;
    CreateDirectories createDirectories = new CreateDirectories();
    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(RecordingActivity.this);
    ArrayList<String> currentEvent = new ArrayList<String>();
    static String selectedCurrentEvent;
    boolean createdEvent = false;
    boolean onRecordStart = true;
    ImageView microphoneRecordImage;
    TextView timerValue;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    final Handler customHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        timerValue = (TextView) findViewById(R.id.timerValue);
        microphoneRecordImage = (ImageView) findViewById(R.id.playOrpause);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        currentEvent = getCalendarDetails.getCurrentEvent();

        microphoneRecordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  Log.i("Current Drawable", microphoneRecordImage.getDrawable()+"");
                    if(currentEvent == null){


                        askPermissionToGoToCalendar();
                        createdEvent= true;
                    }
                    else{
                        if(onRecordStart){
                            if(currentEvent.size()>1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RecordingActivity.this);
                                builder.setTitle("Choose the event To Save Images");
                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RecordingActivity.this,
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
                                        recording(selectedCurrentEvent);
                                    }
                                });
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }
                            else{
                                selectedCurrentEvent = currentEvent.get(0);
                                recording(selectedCurrentEvent);
                            }

                            Toast.makeText(getApplicationContext(), "Recording start", Toast.LENGTH_LONG).show();
                        }
                        else{
                            microphoneRecordImage.setImageResource(R.mipmap.microphone);
                            onRecordStart = true;
                            timeSwapBuff += timeInMilliseconds;
                            customHandler.removeCallbacks(updateTimerThread);
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            MediaScannerConnection.scanFile(RecordingActivity.this, new String[]{}, null, null);

                            Toast.makeText(getApplicationContext(), "Audio Recordering successfully done", Toast.LENGTH_LONG).show();

                        }
                        Log.i("Record is pressed", "yayyy");

                    }

            }
        });

    }

    Runnable updateTimerThread = new Runnable(){

        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime/1000);
            int mins = secs/60;
            secs = secs % 60;
            int hours = mins/60;

            timerValue.setText(""+String.format("%02d", hours)+":"
                    + String.format("%02d", mins)
                    +":"
                    +String.format("%02d", secs)
            );
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if(createdEvent){
            Toast.makeText(getApplicationContext(),"You can start Recording by pressing Record button", Toast.LENGTH_LONG ).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.view_list_of_images,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.viewlistOfPicture:
                Log.i("ViewRecordingActivity", "Called");
                Intent intent = new Intent(RecordingActivity.this, AllListActivity.class);
                intent.putExtra("Material", "Recordings");
                intent.putExtra("CalendarEvent", selectedCurrentEvent);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    void recording(String event){
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        try{

            microphoneRecordImage.setImageResource(R.mipmap.pause);
            onRecordStart = false;

            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);

            createDirectories.createCourseCodifyFile();
            createDirectories.createEventFolder(event);
            createDirectories.createSubFolder(event, "Recordings");
            String outputFile= Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/CourseCodify/" +event+"/Recordings/"+ "Rec_"+ timeStamp+".m4a";

            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        catch (Exception e){
            Log.i("Recording", "statred");
            e.printStackTrace();
        }
    }

}
