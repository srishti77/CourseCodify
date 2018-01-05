package com.srishti.srish.coursecodify_v1;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RecordingActivity extends AppCompatActivity {

    MediaRecorder mediaRecorder;
    CreateDirectories createDirectories = new CreateDirectories();
    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(RecordingActivity.this);
    String currentEvent = null;
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
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        currentEvent = getCalendarDetails.getCurrentEvent();
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        microphoneRecordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                  Log.i("Current Drawable", microphoneRecordImage.getDrawable()+"");
                    if(currentEvent == null){

                        AlertDialogs alertDialogs = new AlertDialogs(RecordingActivity.this);
                        alertDialogs.askPermissionToGoToCalendar();
                        createdEvent= true;
                    }
                    else{
                        if(onRecordStart){
                            microphoneRecordImage.setImageResource(R.mipmap.pause);
                            onRecordStart = false;

                            startTime = SystemClock.uptimeMillis();
                            customHandler.postDelayed(updateTimerThread, 0);

                            createDirectories.createCourseCodifyFile();
                            createDirectories.createEventFolder(currentEvent);
                            createDirectories.createSubFolder(currentEvent, "Recordings");
                            String outputFile= Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/CourseCodify/" +currentEvent+"/Recordings/"+ "record"+ timeStamp+".3gp";

                            mediaRecorder.setOutputFile(outputFile);
                            mediaRecorder.prepare();
                            mediaRecorder.start();
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

                } catch (IOException e) {
                    e.printStackTrace();
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
                Intent intent = new Intent(RecordingActivity.this, ViewListOfRecordingsActivity.class);
                intent.putExtra("CalendarEvent", currentEvent);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
