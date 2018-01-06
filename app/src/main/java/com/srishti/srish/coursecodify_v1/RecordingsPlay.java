package com.srishti.srish.coursecodify_v1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class RecordingsPlay extends AppCompatActivity {

    static boolean onPlayStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_recordings_play_layout);

        Intent intent = getIntent();
        String selectedEvent = intent.getStringExtra("SelectedEvent");
        String recording = intent.getStringExtra("recording");

        TextView recordingName = (TextView) findViewById(R.id.Name_Of_Recordings);
        recordingName.setText(recording);

        final ImageView playOrpause = (ImageView) findViewById(R.id.play_pause_recording);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        final TextView timeOfSeekBar = (TextView) findViewById(R.id.timeOfSeekBar);

        final MediaPlayer mediaPlayer = new MediaPlayer();
        try{

            String outputFile = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/CourseCodify/" + selectedEvent
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
}
