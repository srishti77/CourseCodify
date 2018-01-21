package com.srishti.srish.coursecodify_v1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ViewImagesFragment extends Fragment {


    View view;

    ListSingleImageAdapter arrayAdapter;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    ArrayList<String> nameOfFiles = new ArrayList<String>();

    Drawable drawableImage;
    CreateDirectories createDirectories = new CreateDirectories();

    final ArrayList<String> imageName = new ArrayList<>();
    String selectedEvent;
    TextView imagesHeading;

    private OnFragmentInteractionListener mListener;


    public ViewImagesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        selectedEvent = bundle.getString("Folder");
        Log.i("SelectedEvent", selectedEvent+"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Inside ViewImage", "FRagment");
        view = inflater.inflate(R.layout.fragment_view_images, container, false);

        imagesHeading = (TextView) view.findViewById(R.id.fragment_image_heading);
        final ListView listView = (ListView) view.findViewById(R.id.listOfImages);
        getData(selectedEvent);

        arrayAdapter = new ListSingleImageAdapter(getActivity(), drawables, imageName);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                byte[] byteArray = getImageToSend(position, drawables);

                Intent intent = new Intent(getActivity(), ViewImageFullScreenActivity.class);
                intent.putExtra("ImageView", byteArray);

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
                        if (id == R.id.delete) {

                            deleteImages(position, selectedEvent ,drawables,imageName);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        if (id == R.id.share) {
                            item = popupMenu.getMenu().findItem(id);
                            shareImages(item, getActivity().getContentResolver(), drawables.get(position));
                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void deleteImages(int position, String event, ArrayList<Drawable> drawablesDelete, ArrayList<String> imageNameDelete){

        try{
            File directory = createDirectories.getCurrentFile("CourseCodify/" + event + "/Images");
            for (File file : directory.listFiles()) {
                if (imageNameDelete.get(position).compareTo(file.getName()) == 0) {
                    file.delete();
                }
            }
            drawablesDelete.remove(position);
            imageNameDelete.remove(position);
        }


        catch(Exception e){

            e.printStackTrace();
        }

    }

    public void shareImages(MenuItem item, ContentResolver contentResolver, Drawable drawable){


        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        String path = MediaStore.Images.Media.insertImage(contentResolver,
                bitmap, "Design", null);
        Uri uri = Uri.parse(path);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        shareActionProvider.setShareIntent(shareIntent);
    }


    void onSpinnerChanged(String selectedEvent) {
        this.selectedEvent = selectedEvent;
        Log.i("Inside Images","spinner");

        if(arrayAdapter != null){
            arrayAdapter.clear();
            Log.i("Spinner", "spinner is changed");

            getData(selectedEvent);
            arrayAdapter.notifyDataSetChanged();
            if(arrayAdapter.isEmpty())
                imagesHeading.setText("");
            else{

                imagesHeading.setText("List of Images");
            }
        }

    }

    public void getData(String event){

        drawables.clear();
        nameOfFiles.clear();
        imageName.clear();
        if (createDirectories.getCurrentFile("/CourseCodify/" + event + "/Images").listFiles() != null) {

            for (File file : createDirectories.getCurrentFile("/CourseCodify/" + event + "/Images").listFiles()) {
                if (file.isFile()) {

                    drawableImage = Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CourseCodify/" + event + "/Images/" + file.getName());

                    drawables.add(drawableImage);

                    nameOfFiles.add(file.getName());
                    imageName.add(file.getName());
                }
            }
        }
    }

    public static byte[] getImageToSend( int position,  ArrayList<Drawable> drawables1  ){
        Bitmap bitmap = ((BitmapDrawable) drawables1.get(position)).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("Images Fragment Attach", "called");
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("Image Fragment Dettach", "called");
        mListener = null;
    }

}
