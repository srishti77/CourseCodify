package com.srishti.srish.coursecodify_v1;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;


/**
 * Created by User on 13/01/2018.
 */

public class CourseCodifyHelper  extends AppCompatActivity {

    CreateDirectories createDirectories = new CreateDirectories();
    String selectedCurrentEvent;

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
}
