package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;

/**
 * Created by User on 10/01/2018.
 */

public class SharingImplementation extends Fragment
    {

    public  void shareRecordings( String event, String recordingsName, Context context, MenuItem item){

        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CourseCodify/"+event+"/Recordings/"+recordingsName);
        Intent myShareIntent = new Intent();
        myShareIntent.setAction(Intent.ACTION_SEND);


        Uri fileURI = FileProvider.getUriForFile(context ,"com.srishti.srish.coursecodify_v1", file);
        Log.i("The path..", fileURI+"");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI );
        myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myShareIntent.setType("audio/*");
        //startActivity(Intent.createChooser(myShareIntent, "Share"));

        shareActionProvider.setShareIntent(myShareIntent);
    }
}
