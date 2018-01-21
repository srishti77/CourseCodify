package com.srishti.srish.coursecodify_v1;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.ContentResolver;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.text.format.Time;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 26/11/2017.
 * get all the calendar Names present in the google calendar
 */

public class GetCalendarDetails {

    private Context context;
    String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND

    };
    ArrayList<String> currentEventName = new ArrayList<String>();

    ArrayList<String> eventNames = new ArrayList<String>();
    ArrayList<String> calendarNames = new ArrayList<String>();

    public GetCalendarDetails(Context context){
        this.context = context;
    }
//    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    public ArrayList<String> getAllCalendarName(){
       try

        {

            SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("MyCalendarDb", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS New_MyCalendar_Table (" + "CalendarName VARCHAR," + "CalendarId VARCHAR)");

            //for getting all the calendar names
            Cursor cursor;

            if (android.os.Build.VERSION.SDK_INT <= 7) {
                cursor = context.getContentResolver().query(Uri.parse("content://calendar/calendars"), new String[]{"_id", "displayName"}, null, null, null);
            } else if (android.os.Build.VERSION.SDK_INT <= 14) {
                cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[]{"_id", "displayName"}, null, null, null);
            } else {
                cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), new String[]{"_id", "calendar_displayName"}, null, null, null);
            }

            Log.i("No of calendars", cursor.getCount()+"");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                calendarNames.clear();
                for (int i = 0; i < cursor.getCount(); i++) {

                    sqLiteDatabase.execSQL("INSERT INTO New_MyCalendar_Table(CalendarName,CalendarId) Values('" + cursor.getString(1) + "', '" + cursor.getInt(0) + "')");
                    calendarNames.add(cursor.getString(1));
                    cursor.moveToNext();

                }

            }

            cursor.close();
            sqLiteDatabase.close();
           //

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return calendarNames;
    }


    public ArrayList<String> getevents( String CalendarName, Time time){

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        if(time == null){
            time = new Time();
            time.setToNow();
            Log.i("Time is", time+"");
            time.set(00, 00, 01, time.monthDay, time.month, time.year);
        }


        time.set(00, 00, 01, time.monthDay, time.month, time.year);

        long dtStart = time.toMillis(false);

        time.set(59, 59, 23, time.monthDay, time.month, time.year);
        long dtEnd = time.toMillis(false);

        Uri.Builder uri = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(uri, dtStart);
        ContentUris.appendId(uri, dtEnd);
        String selection = "(" + CalendarContract.Instances.CALENDAR_DISPLAY_NAME + "= ?)";
        if(CalendarName == null)
            CalendarName = "srishti.kristi12@gmail.com";
        String[] selectionArgs = new String[]{CalendarName};

        Uri eventsUri = uri.build();

        cur = cr.query(eventsUri, null,selection, selectionArgs, CalendarContract.Instances.DTSTART + " ASC");


        Log.i("Cursor Count", cur.getCount()+"");

        cur.moveToFirst();

        eventNames.clear();
        for(int i =0; i< cur.getCount();i++){
            Log.i("cursor value", cur.getString(cur.getColumnIndex("title")));
            eventNames.add(cur.getString(cur.getColumnIndex("title")));
            cur.moveToNext();
        }

        cur.close();
        return eventNames;
    }

    public ArrayList<String> getCurrentEvent(){
        Log.i("Inside", "getCurrentEvent");

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        Time t = new Time();
        t.setToNow();

        long dtStart = t.toMillis(false);
        t.set(59, 59, 23, t.monthDay, t.month, t.year);
        long dtEnd = t.toMillis(false);

        Uri.Builder uri = CalendarContract.Instances.CONTENT_URI.buildUpon();

        ContentUris.appendId(uri, dtStart);
        ContentUris.appendId(uri, dtEnd);
        String selection = "(" + CalendarContract.Instances.CALENDAR_DISPLAY_NAME + "= ?) AND (" + CalendarContract.Instances.BEGIN + " <= ? ) AND (" + CalendarContract.Instances.END + " >= ? )";


        String[] selectionArgs = new String[]{NavigationDrawerActivity.currentSelectedEvent, dtStart+"", dtStart+""};

        Uri eventsUri = uri.build();

        cur = cr.query(eventsUri, null,selection, selectionArgs, CalendarContract.Instances.DTSTART + " ASC");

        cur.moveToFirst();




        Log.i("Current time", dtStart+"");
        int colIndex = cur.getColumnIndex("title");
        for(int i = 0; i< cur.getCount(); i++){
            Log.i("Event Name:::", cur.getString(colIndex) );
            currentEventName.add(cur.getString(colIndex).toString());
            Log.i("Events added", "yayyy");
            cur.moveToNext();
        }


        Log.i("ListOf Current Event", currentEventName.size()+"");
        cur.close();
        return  currentEventName;

    }

    public void goToCalendar(Context context){

        long startMills = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMills);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        context.startActivity(intent);
    }

}
