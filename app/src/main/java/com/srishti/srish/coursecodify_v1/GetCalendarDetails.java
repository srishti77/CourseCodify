package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.ContentResolver;
import android.provider.CalendarContract;
import android.util.Log;

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

                    //Saving data to the List elements so that it can be sent to the preference fragment


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

    public ArrayList<String> getAllEventsFromPreferredCalendar(String CalendarName) {
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
        String selection = "(" + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + "= ?)";
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
}
