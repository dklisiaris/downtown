package org.dklisiaris.downtown.adapters;

import org.dklisiaris.downtown.GlobalData;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.db.Product;
import org.dklisiaris.downtown.helper.ImageLoader;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomAdapter extends ArrayAdapter<Company> {
 
    private Context context;
    private SparseArray<String> mapHash;
    private static LayoutInflater inflater=null;
    private ImageLoader iLoader; 
    private String subcatID = null;
 
    public CustomAdapter(Context c) {
    	super(c, R.layout.tab_list);
    	context = c;
    	mapHash = ((GlobalData)context).getCatsMap();
    	if(mapHash == null){
    		DBHandler db = DBHandler.getInstance(context);
			((GlobalData)context).setCatsMap(db.getCatHash());
			mapHash = ((GlobalData)context).getCatsMap();
		}
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iLoader = new ImageLoader(c);
    }
 
    public void setData(ArrayList<Company> data) {
        clear();
        if (data != null) {
            for (Company co : data) {
                add(co);
            }
        }
    }
         
    public void setSubcatID(String subcatID) {
		this.subcatID = subcatID;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.products_row, null);
 
        TextView name = (TextView)vi.findViewById(R.id.name); 
        TextView address = (TextView)vi.findViewById(R.id.address); 
        TextView area = (TextView)vi.findViewById(R.id.area); 
        TextView tel = (TextView)vi.findViewById(R.id.tel);
        TextView categ = (TextView)vi.findViewById(R.id.categ); 
        TextView subcat = (TextView)vi.findViewById(R.id.subcateg); 
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); 
                      
        Company  co = getItem(position);
        // Setting all values in listview
        name.setText(co.getName());        
        address.setText(co.getAddress());
        if(co.getArea() != null && co.getArea() != "")
        	area.setText(co.getArea());        
        if(co.getTels() != null && co.getTels().size()>0){
        	if(co.getTels().get(0) != null && co.getTels().get(0) != ""){
        		tel.setText(co.getTels().get(0));
        	}
        }
        else tel.setText("-");
        
        if(subcatID!=null)subcat.setText(mapHash.get(Integer.parseInt(subcatID)));      	        
        else subcat.setText(mapHash.get(Integer.parseInt(co.getSubcategories().get(0))));
        
        categ.setText(mapHash.get(co.getCategory()));
        Drawable d=null;
        try{
        	if(co.getFirst_img()!=null){
	        	 // load image into drawable
        		d = iLoader.loadImage(co.getFirst_img());
        	}
         }catch(Exception e){e.printStackTrace();}
        
        if(d==null)d = iLoader.loadImage("Blankside.jpg");
        thumb_image.setImageDrawable(d);
        return vi;
    }
}
