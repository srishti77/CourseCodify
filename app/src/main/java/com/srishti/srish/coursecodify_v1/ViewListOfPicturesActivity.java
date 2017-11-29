package com.srishti.srish.coursecodify_v1;

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
import android.widget.ListView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class ViewListOfPicturesActivity extends AppCompatActivity {

    ListSingleImageAdapter arrayAdapter;
    ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    ArrayList<String> nameOfFiles =  new ArrayList<String>();

    Drawable drawableImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_of_pictures);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ArrayList<String> imageName = new ArrayList<>();

        //File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCameraApp");
        for (File file : getCurrentFile().listFiles()) {
            if (file.isFile()) {
                drawableImage =Drawable.createFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/MyCameraApp/"+file.getName());
                drawables.add(drawableImage);

                nameOfFiles.add(file.getName());


                imageName.add(file.getName());
            }
        }

        final ListView listView = (ListView)  findViewById(R.id.listOfImage);
        arrayAdapter = new ListSingleImageAdapter(this, drawables, imageName);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Bitmap bitmap = ((BitmapDrawable) drawables.get(position)).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] buteArray = byteArrayOutputStream.toByteArray();

                Intent intent = new Intent(ViewListOfPicturesActivity.this, ViewImageFullScreenActivity.class);
                intent.putExtra("ImageView",buteArray);

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
                        if(id == R.id.delete){
                            Log.i("Delete Position", position+"");
                            Log.i("CountImageName Array",imageName.size()+"" );

                            File directory = getCurrentFile();
                            for(File file : directory.listFiles()){
                                if(imageName.get(position).compareTo(file.getName()) == 0){
                                    file.delete();
                                }
                            }
                            drawables.remove(position);
                            imageName.remove(position);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        if(id == R.id.share){
                            item = popupMenu.getMenu().findItem(id);
                            Log.i("Popup item", item+"");
                            ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                            Log.i("Check Shareaction ", shareActionProvider+"");
                            Bitmap bitmap = ((BitmapDrawable) drawables.get(position)).getBitmap();
                            String path = MediaStore.Images.Media.insertImage(ViewListOfPicturesActivity.this.getContentResolver(),
                                    bitmap, "Design", null);
                            Uri uri = Uri.parse(path);

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                            shareActionProvider.setShareIntent(shareIntent);
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
        if(item.getItemId() == R.id.home){
            this.finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    File getCurrentFile(){

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "MyCameraApp");
        return directory;
    }



}
