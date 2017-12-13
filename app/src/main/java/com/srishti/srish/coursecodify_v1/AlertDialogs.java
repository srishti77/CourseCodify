package com.srishti.srish.coursecodify_v1;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by User on 13/12/2017.
 */

public class AlertDialogs {

    Context context;

    AlertDialogs(Context context){
        this.context = context;

    }
    GetCalendarDetails getCalendarDetails = new GetCalendarDetails(context);

    public  void askPermissionToGoToCalendar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("There is no event at this moment. Create event to save your material")
                .setPositiveButton("Create Event", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("OnClick","Create Event is pressed");
                        //Log.i("OnClick",getByteOfImage().length+"");

                        goToCalendar();
                        Toast.makeText(context, "Save Pressed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(context, "Create Event Pressed", Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

 

    public void goToCalendar(){

        long startMills = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMills);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        context.startActivity(intent);


    }

}
