package org.dklisiaris.downtown.adapters;

import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.widgets.CheckableRelativeLayout;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AddressFilterAdapter  extends ArrayAdapter<String> {
	private Context context;
	private static LayoutInflater mInflater=null;
    
	public AddressFilterAdapter(Context c){
		super(c, R.layout.list_subcategories);
    	context = c;
    	mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  	
    	
	}
	
    public void setData(List<String> data) {
        clear();
        if (data != null) {
            for (String entry : data) {
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
        ViewHolder holder;
        
        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_address, parent, false);
            holder = new ViewHolder(view);
			view.setTag(R.id.holder, holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag(R.id.holder);
        }

        final String item = getItem(position);

		// Set some view properties
		holder.caption.setText(item);

		// Restore the checked state properly
		final ListView lv = (ListView) parent;
		boolean isChecked = lv.isItemChecked(position);

		holder.layout.setChecked(isChecked);
		if(isChecked)view.setBackgroundColor(0xffdddddd);
		else view.setBackgroundColor(0xffcccccc);
		        
        return view;
    }
    
	private static class ViewHolder {
		public TextView caption;
		public CheckableRelativeLayout layout;
		
		public ViewHolder(View root) {
			caption = (TextView) root.findViewById(R.id.address);
			layout = (CheckableRelativeLayout) root.findViewById(R.id.layout);
		}
	}
}
