package com.srishti.srish.coursecodify_v1;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.ContentResolver;
import android.provider.CalendarContract;
import android.util.Log;

import java.net.URI;
import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    String currentEventName;

    ArrayList<String> eventNames = new ArrayList<String>();
    ArrayList<String> calendarNames = new ArrayList<String>();

    public GetCalendarDetails(Context context){
        this.context = context;
    }
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

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return calendarNames;
    }

   /* public ArrayList<String> getAllEventsFromPreferredCalendar(String CalendarName) {
       // String selectedCalendar= "1";
        Cursor cursorForEvent = null;
        ContentResolver contentResolver = context.getContentResolver();
        try{
            SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("MyCalendarDb", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS New_MyCalendar_Table (" + "CalendarName VARCHAR," + "CalendarId VARCHAR)");

            Cursor cursor = sqLiteDatabase.rawQuery("select * from New_MyCalendar_Table",null);
        }
        catch(Exception e){

        }
        Uri uri = Uri.parse("content://com.android.calendar/events");
        String selection = "(" + CalendarContract.Instances.CALENDAR_DISPLAY_NAME + "= ?)";
        Log.i("Here we are in ", "getting events");
        String[] selectionArgs = new String[]{CalendarName};
        cursorForEvent = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        Log.i("Lets see events", cursorForEvent.getCount() + "");
        eventNames.clear();
        if (cursorForEvent.getCount() > 0) {
            cursorForEvent.moveToFirst();

            for (int i = 0; i < cursorForEvent.getCount(); i++) {

               // Log.i("", getDate(Long.parseLong(cursorForEvent.getString(1))));

                if ((cursorForEvent.getString(0)) != null && cursorForEvent.getString(1) != null && cursorForEvent.getString(2) != null
                        &&  getDate(Long.parseLong(cursorForEvent.getString(1))).compareTo(getDate()) == 0
                        ) {

                    eventNames.add(cursorForEvent.getString(0));

                    Log.i("Event Name", "" + cursorForEvent.getString(0));
                    Log.i("Start Time", "" + getDate(Long.parseLong(cursorForEvent.getString(1))));
                   // Log.i("End Time", "" + getDate(Long.parseLong(cursorForEvent.getString(2))));


                }

                cursorForEvent.moveToNext();
            }



        }

 return eventNames;
}

    public static String getDate(long milliSeconds) {
        //Change the format to dd/mm/yyy
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Log.i("Date from Calendar", formatter.format(calendar.getTime())+"");
        return formatter.format(calendar.getTime());
    }

    public static String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy");
        Date date = new Date();
        Log.i("Current time", formatter.format(date)+"");
      return formatter.format(date);
    }

    public static String getDate1(){
        SimpleDateFormat formatter = new SimpleDateFormat( "dd-MM-yy:HH:mm:SS Z");
        Date date = new Date();
        Log.i("Current time", formatter.format(date)+"");
        return formatter.format(date);
    }

    */

    public ArrayList<String> getevents( String CalendarName){

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        Time t = new Time();
        t.setToNow();
        t.set(00, 00, 01, t.monthDay, t.month, t.year);
        Log.i("Current time", t+"");
        long dtStart = t.toMillis(false);
        Log.i("Current time", dtStart+"");
        t.set(59, 59, 23, t.monthDay, t.month, t.year);
        long dtEnd = t.toMillis(false);
        Log.i("Endday", Long.toString(dtEnd));

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

        return eventNames;
    }

    public String getCurrentEvent(){
        Log.i("Inside", "getCurrentEvent");
        currentEventName = null;
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


        String[] selectionArgs = new String[]{"srishti.kristi12@gmail.com", dtStart+"", dtStart+""};

        Uri eventsUri = uri.build();

        cur = cr.query(eventsUri, null,selection, selectionArgs, CalendarContract.Instances.DTSTART + " ASC");

        cur.moveToFirst();




        Log.i("Current time", dtStart+"");
        for(int i = 0; i< cur.getCount(); i++){
            Log.i("Event Name", cur.getString(cur.getColumnIndex("title")) );
            currentEventName = cur.getString(cur.getColumnIndex("title"));
            Log.i("Start time", cur.getString(cur.getColumnIndex("begin")));
            Log.i("End time", cur.getString(cur.getColumnIndex("end")));
        }

        return  currentEventName;

    }


}
