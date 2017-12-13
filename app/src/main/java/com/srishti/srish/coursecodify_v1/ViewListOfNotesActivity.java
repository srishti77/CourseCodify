package com.srishti.srish.coursecodify_v1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewListOfNotesActivity extends AppCompatActivity {

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> notesTitles = new ArrayList<String>();
    static ArrayList<String> notesBodies = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_of_notes);


        ListView listView = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, notesTitles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Position of ListItem", i+"");
                Intent intent = new Intent(ViewListOfNotesActivity.this, TakeNotesActivity.class);
                intent.putExtra("NoteId", i);
                startActivity(intent);
            }
        });




    }
}
