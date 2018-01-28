package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;


/**
 * Created by User on 13/01/2018.
 */

public class CourseCodifyHelper  extends AppCompatActivity {

    CreateDirectories createDirectories = new CreateDirectories();
    String selectedCurrentEvent;

    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(CourseCodifyHelper.this);
    File saveImagesIntoFolder(String selectedCurrentEvent){

        Log.i("SaveImages...","called");
        File mediaStorageDir = null;
        String directory =  Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+
                "/"+createDirectories.createCourseCodifyFile() + "/" + selectedCurrentEvent;

        createDirectories.createCourseCodifyFile();
        createDirectories.createEventFolder(selectedCurrentEvent);
        createDirectories.createNoMedia( directory);

        mediaStorageDir = new File(directory+"/Images");

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();

        }

        return mediaStorageDir;
    }

    public  void askPermissionToGoToCalendar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(CourseCodifyHelper.this);
        builder.setMessage("There is no event at this moment. Create event to save your material")
                .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("OnClick","Create Event is pressed");
                        //Log.i("OnClick",getByteOfImage().length+"");

                        getCalendarDetails.goToCalendar(CourseCodifyHelper.this);
                       // Toast.makeText(context, "Save Pressed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "Not Now Pressed", Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
