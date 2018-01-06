package com.srishti.srish.coursecodify_v1;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewListOfPicturesActivity extends AppCompatActivity {

    ListSingleImageAdapter arrayAdapter;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    ArrayList<String> nameOfFiles = new ArrayList<String>();

    Drawable drawableImage;
    CreateDirectories createDirectories = new CreateDirectories();
    Spinner spinnerListOfEvents;
    final ArrayList<String> imageName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_of_pictures);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinnerListOfEvents = (Spinner) findViewById(R.id.images_listOfEvents);
        List listOfevents = new ArrayList<>();

        listOfevents.addAll(createDirectories.readAllDirectoryName(null, null));
        Log.i("Count of Events", listOfevents.size() + "");
        ArrayAdapter<String> arrayAdapterListOfEvents = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfevents);
        arrayAdapterListOfEvents.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerListOfEvents.setAdapter(arrayAdapterListOfEvents);


        Intent intent = getIntent();
        String event = intent.getStringExtra("CurrentCalendarEvent");

        if (event != null) {

            spinnerListOfEvents.setSelection(arrayAdapterListOfEvents.getPosition(event));
        }

        spinnerListOfEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                drawables.clear();
                nameOfFiles.clear();
                imageName.clear();
                arrayAdapter.clear();
                Log.i("Spinner", "spinner is changed");

                if (createDirectories.getCurrentFile("/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images").listFiles() != null) {

                    for (File file : createDirectories.getCurrentFile("/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images").listFiles()) {
                        if (file.isFile()) {

                            drawableImage = Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/CourseCodify/" + spinnerListOfEvents.getSelectedItem() + "/Images/" + file.getName());

                            drawables.add(drawableImage);

                            nameOfFiles.add(file.getName());
                            imageName.add(file.getName());


                        }
                    }


                }
                arrayAdapter.notifyDataSetChanged();

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final ListView listView = (ListView) findViewById(R.id.listOfImage);
        arrayAdapter = new ListSingleImageAdapter(this, drawables, imageName);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                byte[] byteArray = ViewAllMaterialActivity.getImageToSend(position, drawables);

                Intent intent = new Intent(ViewListOfPicturesActivity.this, ViewImageFullScreenActivity.class);
                intent.putExtra("ImageView", byteArray);

                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final PopupMenu popupMenu = new PopupMenu(ViewListOfPicturesActivity.this, listView, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0);

                popupMenu.getMenuInflater().inflate(R.menu.menu_shareordelete, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {

                            deleteImages(position,spinnerListOfEvents.getSelectedItem().toString(),drawables,imageName);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        if (id == R.id.share) {
                            item = popupMenu.getMenu().findItem(id);
                            shareImages(item, ViewListOfPicturesActivity.this.getContentResolver(), drawables.get(position));
                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            this.finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
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
           // Toast.makeText(getApplicationContext(), "File Could not be deleted", Toast.LENGTH_LONG).show();
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



}
