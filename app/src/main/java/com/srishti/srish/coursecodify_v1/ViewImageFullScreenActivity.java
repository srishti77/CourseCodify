package com.srishti.srish.coursecodify_v1;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewImageFullScreenActivity extends AppCompatActivity {

    ViewImagesFragment viewImagesFragment = new ViewImagesFragment();
    int position;
    String imageNameFull;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_full_screen);


        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("ImageView");

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        position = intent.getIntExtra("Position",-1);
        final String currentEvent = intent.getStringExtra("Selected Event");
        imageNameFull = intent.getStringExtra("ImageName");

        Log.i("ImageName opened", imageNameFull);
        ImageView imageFullScreen = (ImageView) findViewById(R.id.imageFullScreen);
        imageFullScreen.setImageBitmap(bitmap);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.delete:
                        viewImagesFragment.deleteImages(position, currentEvent, ViewImagesFragment.drawables, ViewImagesFragment.imageName);
                        ViewImagesFragment.arrayAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share:
                        shareImages(getContentResolver(), bitmap);
                        break;
                }
                return true;
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void shareImages(ContentResolver contentResolver, Bitmap bitmap){


        String path = MediaStore.Images.Media.insertImage(contentResolver,
                bitmap, "Design", null);
        Uri uri = Uri.parse(path);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(shareIntent);

    }
}
