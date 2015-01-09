package org.dklisiaris.downtown.adapters;

import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.helper.ImageLoader;


import java.io.File;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomStringAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Category> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
 
    public CustomStringAdapter(Activity a, ArrayList<Category> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        ImageView img = (ImageView)vi.findViewById(R.id.cat_img);
        String catID = Integer.toString(data.get(position).getCat_id());
        // Setting all values in listview
        title.setText(data.get(position).getCat_name());
        /*
        Resources res = img.getContext().getResources();
        String mDrawableName = "ic"+catID;
        int resID = res.getIdentifier(mDrawableName , "drawable", img.getContext().getPackageName());
        Drawable drawable = res.getDrawable(resID);
        */
        String imgUrl = data.get(position).getCat_icon();
        if(imgUrl==null || imgUrl.equals(""))imgUrl="ic"+Integer.toString(data.get(position).getCat_id())+".png";
        
        Drawable d = imageLoader.loadImage(imgUrl);
        if(d!=null)img.setImageDrawable(d);        
        /*
        Picasso.with(activity)        
        .load(activity.getFileStreamPath(data.get(position).getCat_icon()))
        .placeholder(imageLoader.loadImage(data.get(position).getCat_icon()))
        .into(img);
        */
        vi.setTag(catID);
        
        return vi;
    }
}
