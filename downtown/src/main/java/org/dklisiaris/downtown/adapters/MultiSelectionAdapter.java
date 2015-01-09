package org.dklisiaris.downtown.adapters;

import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.helper.ImageLoader;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public final class MultiSelectionAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private ArrayList<Company> items;
	private Context context;
	private SparseArray<String> mapHash;
	// all our checked indexes go here
	private HashSet<Integer> checkedItems;
	private ImageLoader iLoader;
	
	
	// multi selection mode flag
	private boolean multiMode;
	
	public MultiSelectionAdapter(Context context, ArrayList<Company> items, SparseArray<String> hash)
	{
		this.inflater = LayoutInflater.from(context);
		this.items = items;
		this.checkedItems = new HashSet<Integer>();
		this.context=context;
		mapHash=hash;
		iLoader = new ImageLoader(context);
	}
	
	public MultiSelectionAdapter(Context context, SparseArray<String> hash)	
	{		
		this(context, new ArrayList<Company>(), hash);		
	}
	
	public void enterMultiMode()
	{
		this.multiMode = true;
		this.notifyDataSetChanged();
	}
	
	public void exitMultiMode()
	{
		this.checkedItems.clear();
		this.multiMode = false;
		this.notifyDataSetChanged();
	}
	
	public void setChecked(int pos, boolean checked)
	{
	if (checked) {
	    this.checkedItems.add(Integer.valueOf(pos));
	} else {
	    this.checkedItems.remove(Integer.valueOf(pos));
	}
	if (this.multiMode) {
	    this.notifyDataSetChanged();
	}
	}
	
	public boolean isChecked(int pos)
	{
		return this.checkedItems.contains(Integer.valueOf(pos));
	}
	
	public void toggleChecked(int pos)
	{
		final Integer v = Integer.valueOf(pos);
		if (this.checkedItems.contains(v)) {
			this.checkedItems.remove(v);			
		} else {
			this.checkedItems.add(v);
		}
		this.notifyDataSetChanged();
	}
	
	public int getCheckedItemCount()
	{
		return this.checkedItems.size();
	}
	
	// we use this convinience method for rename thingie.
	public Company getFirstCheckedItem()
	{
		for (Integer i : this.checkedItems) {
		    return this.items.get(i.intValue());
	}
	return null;
	}
	
	public Set<Integer> getCheckedItems()
	{
		return this.checkedItems;
	}
	
	public void clear()
	{
		this.items.clear();
	}
	
	public void updateData(ArrayList<Company> data)
	{
		for (Company p : data) {
		    this.items.add(p);
		}
		this.checkedItems.clear();
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		return this.items.size();
	}
	
	@Override
	public Company getItem(int position)
	{
		return this.items.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	@Override
	public boolean hasStableIds()
	{
		return true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null) {
		    convertView = inflater.inflate(R.layout.products_row, null);
		    // convert view stuff cleared, to much code - do the usual Holder stuff here
		
		}
		
        TextView name = (TextView)convertView.findViewById(R.id.name); 
        TextView address = (TextView)convertView.findViewById(R.id.address); 
        TextView area = (TextView)convertView.findViewById(R.id.area); 
        TextView tel = (TextView)convertView.findViewById(R.id.tel);
        TextView categ = (TextView)convertView.findViewById(R.id.categ); 
        TextView subcat = (TextView)convertView.findViewById(R.id.subcateg); 
        ImageView thumb_image=(ImageView)convertView.findViewById(R.id.list_image); 
                      
        Company pr = getItem(position);
        // Setting all values in listview
        name.setText(pr.getName());
        address.setText(pr.getAddress());
        area.setText(pr.getArea());
        if(pr.getTels().get(0) != null && pr.getTels().get(0) != "")
        	tel.setText(pr.getTels().get(0));
        subcat.setText(mapHash.get(Integer.parseInt(pr.getSubcategories().get(0))));
        categ.setText(mapHash.get(pr.getCategory()));
        
        Drawable d=null;
        try{
        	if(pr.getFirst_img()!=null){
	        	 // load image into drawable
        		d = iLoader.loadImage(pr.getFirst_img());
        	}
         }catch(Exception e){e.printStackTrace();}
        
        if(d==null)d = iLoader.loadImage("Blankside.jpg");
        thumb_image.setImageDrawable(d);
                	
		// the 4 state change problem described above. We use a second selector with no pressed state color if in multi mode
		convertView
		        .setBackgroundResource(this.multiMode ? R.drawable.fav_list_multimode
		                : R.drawable.tablist_selector);
		
		if (checkedItems.contains(Integer.valueOf(position))) {
		    // if this item is checked - set checked state
		    convertView.getBackground().setState(
		            new int[] { android.R.attr.state_checked });
		} else {
		    // if this item is unchecked - set unchecked state (notice the minus)
		    convertView.getBackground().setState(
		            new int[] { -android.R.attr.state_checked });
		}
		
		
		return convertView;
	}

}