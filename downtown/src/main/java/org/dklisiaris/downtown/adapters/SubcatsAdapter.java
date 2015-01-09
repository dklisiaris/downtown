package org.dklisiaris.downtown.adapters;

import java.util.ArrayList;
import java.util.List;

import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.helper.ImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubcatsAdapter  extends ArrayAdapter<Category>{
	private Context context;
	private static LayoutInflater mInflater=null;
    private ImageLoader iLoader;
    
	public SubcatsAdapter(Context c){
		super(c, R.layout.list_subcategories);
    	context = c;
    	mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iLoader = new ImageLoader(context);
	}
	
    public void setData(List<Category> data) {
        clear();
        if (data != null) {
            for (Category entry : data) {
                add(entry);
                //Log.d("Added by cAdapter",entry);
            }
        }
    }
    
    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item, parent, false);
        } else {
            view = convertView;
        }

        Category item = getItem(position);
        ((TextView)view.findViewById(R.id.category)).setText(item.getCat_name()); 
        //load icon for this subcategory
        Drawable d = iLoader.loadImage(item.getCat_icon());  
        //if subcategory has no icon, we use parents' category icon
        if(d==null) d = iLoader.loadImage("ic"+Integer.toString(item.getCat_parent())+".png"); 
        //finally if there is an icon we use it
        if(d!=null){
        	((ImageView)view.findViewById(R.id.cat_icon)).setImageDrawable(d);
        	((ImageView)view.findViewById(R.id.cat_icon)).setVisibility(View.VISIBLE);
        }
        view.setTag(Integer.toString(item.getCat_id()));	

        return view;
    }
	
}
