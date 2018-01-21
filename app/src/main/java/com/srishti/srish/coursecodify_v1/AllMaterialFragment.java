package com.srishti.srish.coursecodify_v1;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class AllMaterialFragment extends Fragment {


    private  FragmentActivity myContext;
    View view;
    String selectedEvent;
    static Fragment selectedFragment = null;
    ViewImagesFragment viewImagesFragment;
    NotesFragment notesFragment;
    RecordingFragment recordingFragment;
     Bundle bundle = new Bundle();

    public AllMaterialFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle !=null){
            Log.i("Bundle Not null", "----");
            selectedEvent = bundle.getString("Folder");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_all_material, container, false);

        Log.i("selectedEvent--", selectedEvent);


        bundle.putString("Folder", selectedEvent);

        BottomNavigationView bottomNavigationItemView = (BottomNavigationView) view.findViewById(R.id.navigation);
        bottomNavigationItemView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();

                switch(item.getItemId()){

                    case R.id.images:
                        viewImagesFragment = new ViewImagesFragment();
                        transaction.replace(R.id.frame_layout, viewImagesFragment, "ImageFragment");
                        viewImagesFragment.setArguments(bundle);
                       // viewImagesFragment.onSpinnerChanged(selectedEvent);
                        break;

                    case R.id.notes:
                        Log.i("Notes Button", "called");
                        //notesFragment = new NotesFragment();
                        transaction.replace(R.id.frame_layout, notesFragment, "NotesFragment");
                        notesFragment.setArguments(bundle);
                       // notesFragment.onSpinnerChanged(selectedEvent);
                        break;

                    case R.id.recordings:
                        recordingFragment = new RecordingFragment();
                        transaction.replace(R.id.frame_layout, recordingFragment, "RecordingFragment");
                        recordingFragment.setArguments(bundle);
                       // recordingFragment.onSpinnerChanged(selectedEvent);
                        break;
                }


                transaction.commit();

                return true;

            }
        });
        FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();
        notesFragment = new NotesFragment();
        transaction.replace(R.id.frame_layout, notesFragment);
        notesFragment.setArguments(bundle);
        onSpinnerChanged(selectedEvent);
        transaction.commit();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void onSpinnerChanged(String event){
        selectedEvent = event;
        bundle.putString("Folder", selectedEvent);

        if(viewImagesFragment != null && viewImagesFragment.isVisible()){
            Log.i("ViewImages---", "called");
            viewImagesFragment.onSpinnerChanged(event);
        }


        if(notesFragment != null && notesFragment.isVisible()){
            Log.i("NotesFragments---", "called");
            notesFragment.onSpinnerChanged(event);
        }


        if(recordingFragment != null && recordingFragment.isVisible()){
            Log.i("Recordings Fragments---", "called");
            recordingFragment.onSpinnerChanged(event);
        }

    }

}
