package com.srishti.srish.coursecodify_v1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class NotesFragment extends Fragment {

    View view;

    private OnFragmentInteractionListener mListener;

    static ArrayAdapter arrayAdapter;
    static ArrayList<String> notesTitles = new ArrayList<String>();

    static ArrayList<String> notesBodies = new ArrayList<String>();
    CreateDirectories createDirectories = new CreateDirectories();

    String selectedEvent;
    TextView notesHeading;
    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        selectedEvent = bundle.getString("Folder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("Inside", "View Notes Fragment");
        if(view == null)
            view = inflater.inflate(R.layout.fragment_notes, container, false);

        notesHeading = (TextView) view.findViewById(R.id.fragment_heading);
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        notesTitles.clear();
        notesTitles.addAll(createDirectories.readAllDirectoryName(selectedEvent+ "", "Notes"));
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, notesTitles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Position of ListItem", i+"");
                Intent intent = new Intent(getActivity(), TakeNotesActivity.class);
                Log.i("File Name:", notesTitles.get(i));

                String notesContent = createDirectories.readContentOfNotesFile(selectedEvent+"/Notes/"+ notesTitles.get(i)).toString();
                intent.putExtra("NoteName", notesTitles.get(i)+"");
                intent.putExtra("NotesContent", notesContent);
                intent.putExtra("CurrentSelected",selectedEvent);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final PopupMenu popupMenu = new PopupMenu(getActivity(), listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

                popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.delete){
                            deleteNotes( position, selectedEvent, notesTitles.get(position).toString(),notesTitles );

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            shareNotes(selectedEvent, notesTitles.get(position), getActivity(), item);

                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });
        return view;
    }

    public void deleteNotes(int position, String event, String material, ArrayList<String> materials ){

        File directory = createDirectories.getCurrentFile("CourseCodify/"+event+"/Notes");
        for(File file : directory.listFiles()){

            if(material.compareTo(file.getName()) == 0){
                Log.i("Delete Notes", "gfs");
                file.delete();
                materials.remove(position);
            }
        }


    }

    public void shareNotes( String event, String notesName, Context context, MenuItem item){

        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CourseCodify/"+event+"/Notes/"+notesName);
        Intent myShareIntent = new Intent();
        myShareIntent.setAction(Intent.ACTION_SEND);
        Uri fileURI = FileProvider.getUriForFile(context ,"com.srishti.srish.coursecodify_v1", file);
        Log.i("The path..", fileURI+"");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, fileURI );
        myShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        myShareIntent.setType("text/*");

        shareActionProvider.setShareIntent(myShareIntent);

    }


    void onSpinnerChanged(String event){
        selectedEvent = event;
        notesTitles.clear();

        if(arrayAdapter != null){
            arrayAdapter.clear();
            notesTitles.addAll(createDirectories.readAllDirectoryName(event, "Notes"));
            arrayAdapter.notifyDataSetChanged();
            if(arrayAdapter.isEmpty())
                notesHeading.setText("");
            else if(!arrayAdapter.isEmpty())
                notesHeading.setText("List of Notes");
        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("Notes Fragment Attach", "called");
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("Notes Fragment Dettach", "called");
        mListener = null;
    }

}
