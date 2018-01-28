package com.srishti.srish.coursecodify_v1;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecordingFragment extends SharingImplementation {

    View view;
    CreateDirectories createDirectories = new CreateDirectories();
    static ArrayList<String> recordings = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;

    String folder;
    boolean onPlayStart = true;
    TextView recordingsHeading;

    private OnFragmentInteractionListener mListener;


    public RecordingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Inside View Recording", "Fragment");
        view = inflater.inflate(R.layout.fragment_recording, container, false);
        Bundle bundle = getArguments();

        folder = bundle.getString("Folder");
        List listOfevents = new ArrayList<>();



        listOfevents.clear();
        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));

        recordingsHeading = (TextView) view.findViewById(R.id.fragment_recordings_heading);
        final ListView listView = (ListView) view.findViewById(R.id.listOfRecordings);
        recordings.clear();;
        recordings.addAll(createDirectories.readAllDirectoryName(folder+ "", "Recordings"));
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, recordings);
        listView.setAdapter(arrayAdapter);

        if(arrayAdapter.isEmpty())
            recordingsHeading.setText("");
        else
            recordingsHeading.setText("List of Recordings");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                onPlayStart = true;

                letsPlay(view, i, recordings.get(i));

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                final PopupMenu popupMenu = new PopupMenu(getActivity(), listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

                popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.delete){
                            deleteRecordings( position, folder, recordings );

                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            shareRecordings(folder, recordings.get(position), getActivity(),item);

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

    public void letsPlay(View view, int i, String recording ){


        view = getLayoutInflater().inflate(R.layout.bottom_sheet_recordings_play_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(view);

        TextView recordingName = (TextView) view.findViewById(R.id.Name_Of_Recordings);
        recordingName.setText(recording);

        final ImageView playOrpause = (ImageView) view.findViewById(R.id.play_pause_recording);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        final TextView timeOfSeekBar = (TextView) view.findViewById(R.id.timeOfSeekBar);
        bottomSheetDialog.show();
        final MediaPlayer mediaPlayer = new MediaPlayer();
        try{

            String outputFile = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/CourseCodify/" + folder
                    + "/Recordings/" + recording;

            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (onPlayStart) {
                    playOrpause.setImageResource(R.mipmap.pause);
                    onPlayStart = false;

                    mediaPlayer.start();
                    try{
                        seekBar.setMax(mediaPlayer.getDuration() / 1000);
                    }
                  catch(Exception e){
                      Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                  }
                    final Handler handler = new Handler();

                    Runnable updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekBar.setProgress(currentPosition);
                                timeOfSeekBar.setText(currentPosition + "/" + (mediaPlayer.getDuration() / 1000));
                            }
                            handler.postDelayed(this, 0);
                        }
                    };
                    handler.postDelayed(updateSeekBar, 0);

                }
                else {

                    mediaPlayer.pause();

                    playOrpause.setImageResource(R.mipmap.play);
                    onPlayStart = true;
                }

            }
        });



    }

    public void deleteRecordings(int position, String event, ArrayList<String> filename ){

        File directory = createDirectories.getCurrentFile("CourseCodify/"+event+"/Recordings");
        for(File file : directory.listFiles()){

            if(filename.get(position).compareTo(file.getName()) == 0){
                file.delete();
                filename.remove(position);
            }
        }
     }


    public void onSpinnerChanged(String event){
        folder = event;

        recordings.clear();

        if(arrayAdapter != null){
            arrayAdapter.clear();
            recordings.addAll(createDirectories.readAllDirectoryName(event, "Recordings"));
            arrayAdapter.notifyDataSetChanged();
            Log.i("Recadapter count", arrayAdapter.getCount()+"");
            if(arrayAdapter.isEmpty())
                recordingsHeading.setText("");
            else{
                recordingsHeading.setText("List of Recordings");
            }
        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("Record Fragment Attach", "called");


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("Record Fragment Dettach", "called");
        mListener = null;
    }
}
