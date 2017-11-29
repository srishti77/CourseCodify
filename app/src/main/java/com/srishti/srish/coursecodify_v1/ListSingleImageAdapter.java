package com.srishti.srish.coursecodify_v1;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 29/11/2017.
 */

public class ListSingleImageAdapter extends ArrayAdapter {

    ArrayList listOfFileName = new ArrayList();
    private final Activity context;
    ArrayList<Drawable> listOfImages = new ArrayList();
    public  ListSingleImageAdapter(Activity context, ArrayList listImage, ArrayList list){
        super(context, R.layout.for_each_image_listitem, list);
        this.context = context;
        listOfFileName = list;
        listOfImages = listImage;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        view = inflater.inflate(R.layout.for_each_image_listitem, null, true);

        Log.i("Position "+position,listOfFileName.get(position)+"");
        TextView textView =  view.findViewById(R.id.imageName);
        textView.setText(listOfFileName.get(position)+"");

        ImageView imageView =  view.findViewById(R.id.imageVIew);
        imageView.setImageDrawable(listOfImages.get(position));
        return view;
    }

}
