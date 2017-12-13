package com.srishti.srish.coursecodify_v1;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by User on 05/12/2017.
 */

public class CreateDirectories {

    public void createNoMedia(){

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "MyCameraApp" + "/.nomedia");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp .nomedia", "failed to create directory");

            }
        }
    }

    public String createCourseCodifyFile(){
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CourseCodify");

        if(!mediaStorageDir.exists()){

            mediaStorageDir.mkdirs();
            if(!mediaStorageDir.mkdirs()){
                Log.i("mainFile is created","CourseCodify");
            }
        }
        Log.i("MainDirectory","CourseCodify");
        return mediaStorageDir.getName();
    }



}
