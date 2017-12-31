package com.srishti.srish.coursecodify_v1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

public class ViewImageFullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_full_screen);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Bitmap bitmap;
        byte[] byteArray = getIntent().getByteArrayExtra("ImageView");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.i("I am in ViewImage","Class");
        ImageView imageFullScreen = (ImageView) findViewById(R.id.imageFullScreen);
        imageFullScreen.setImageBitmap(bitmap);

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
}
