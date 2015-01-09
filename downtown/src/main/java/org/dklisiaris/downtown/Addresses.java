package org.dklisiaris.downtown;


import java.util.ArrayList;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.AccessAssets;
import org.dklisiaris.downtown.helper.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Deprecated
 * It was used for the address list
 * @author MeeC
 *
 */
public class Addresses extends ActionBarActivity {

    // XML node keys
    static final String KEY_CATEGORY = "category"; // parent node
    static final String KEY_AREA = "brandname";
    static final String KEY_NAME = "name";
    static final String KEY_SUBCATEGORY = "subcategory";
    static final String KEY_CATEGORIES = "categories";
    static final String KEY_PRODUCT = "product";
    protected static ArrayList<String> menuItems; 
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AddressesFragment addrs = new AddressesFragment();
            fm.beginTransaction().add(android.R.id.content, addrs).commit();
        }
	}

	 /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class ItemLoader extends AsyncTaskLoader<ArrayList<String>> {

        ArrayList<String> mItems;
        String targetItem;
        String targetID;
        Context ct;
        AccessAssets ast;
        Utils util;
        String xml;
        DBHandler db;

        public ItemLoader(Context context) {
            super(context);
            targetItem = ((GlobalData)getContext()).getCategory();
            targetID = ((GlobalData)getContext()).getCatID();
            ct = context;
        	//Log.d("Subs for: ",targetItem); 
    		ast = new AccessAssets(ct);
    		//Log.d("Created ","ast"); 
            menuItems = new ArrayList<String>();
            //xml = ast.readAssetFile("categories.xml");
            util = new Utils(ct);
            xml = util.getStrFromInternalStorage("products.xml");
            //Log.d("XML--->: ",xml);
            db = DBHandler.getInstance(getContext());

        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public ArrayList<String> loadInBackground() {
            
            /*XMLParser parser = new XMLParser();    		
            Document doc = parser.getDomElement(xml); // getting DOM element
    		
            NodeList nl = doc.getElementsByTagName(KEY_PRODUCT);
            
            // looping through all item nodes <category>
            for (int k = 0; k < nl.getLength();k++) {
                Element e = (Element) nl.item(k);
                if (parser.getValue(e, KEY_CATEGORY).equals(targetItem) && !(menuItems.contains(parser.getValue(e, KEY_AREA)))){           	
                		menuItems.add(parser.getValue(e, KEY_AREA));            	
                }
            }

            Collections.sort(menuItems);*/
        	
        	menuItems=db.getAreas("co_category = '"+targetID+"' and co_area != ''");
            // Done!
            return menuItems;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(ArrayList<String> items) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (items != null) {
                    onReleaseResources(items);
                }
            }
            ArrayList<String> olditems = items;
            mItems = items;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(items);
            }

            // At this point we can release the resources associated with
            // 'olditems' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (olditems != null) {
                onReleaseResources(olditems);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mItems != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mItems);
            }
            else{
            	forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(ArrayList<String> items) {
            super.onCanceled(items);

            // At this point we can release the resources associated with 'items'
            // if needed.
            onReleaseResources(items);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'items'
            // if needed.
            if (mItems != null) {
                onReleaseResources(mItems);
                mItems = null;
            }

        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(ArrayList<String> items) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
	
    public static class CustomAdapter extends ArrayAdapter<String> {
        private final LayoutInflater mInflater;

        public CustomAdapter(Context context) {
            super(context, R.layout.tab_list);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(ArrayList<String> data) {
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

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item, parent, false);
            } else {
                view = convertView;
            }

            String item = getItem(position);
            ((TextView)view.findViewById(R.id.category)).setText(item);

            return view;
        }
    }
    
    public static class AddressesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<String>> {
        // This is the Adapter being used to display the list's data.
        CustomAdapter mAdapter;
        // If non-null, this is the current filter the user has provided.
        String mCurFilter;
        OnQueryTextListenerCompat mOnQueryTextListenerCompat;
        
        @Override 
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
 
            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);
            setListAdapter(null);
            // Create an empty adapter we will use to display the loaded data.
            View header_view = View.inflate(getActivity(), R.layout.tabs_header, null);
            TextView hv = ((TextView)header_view.findViewById(R.id.ctgr));
            hv.setText(((GlobalData)getActivity().getApplicationContext()).getCategory());
            getListView().addHeaderView(header_view);
            
            // Create an empty adapter we will use to display the loaded data.
            if (mAdapter == null) {
                mAdapter = new CustomAdapter(getActivity());
            }
            setListAdapter(mAdapter);

            // Start out with a progress indicator.
            setListShown(false);
            
            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);

    		
    	    //Intent i = getParent().getIntent();
    	    // getting attached intent data

        }
/*
        @Override 
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Place an action bar item for searching.
            MenuItem item = menu.add("Search");
            item.setIcon(android.R.drawable.ic_menu_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            View searchView = SearchViewCompat.newSearchView(getActivity());
            if (searchView != null) {
                SearchViewCompat.setOnQueryTextListener(searchView,
                        new OnQueryTextListenerCompat() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Called when the action bar search text has changed.  Since this
                        // is a simple array adapter, we can just have it do the filtering.
                        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
                        mAdapter.getFilter().filter(mCurFilter);
                        return true;
                    }
                });
                item.setActionView(searchView);
            }
        }*/

        @Override 
        public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            //Log.d("Subcats..", "Item clicked: " + id);
			  // selected item 
        	if((TextView)v.findViewById(R.id.category)!=null){
			  TextView tv = (TextView)v.findViewById(R.id.category);
			  String product = tv.getText().toString();		
			  String[] array = product.split("\\-",-1);
			  product = array[0].trim();
			  String category = ((GlobalData)getActivity().getApplicationContext()).getCategory();
			  String cat_id = ((GlobalData)getActivity().getApplicationContext()).getCatID();
			  // Launching new Activity on selecting single List Item
			  Intent i = new Intent(getActivity(), Products.class);
			  // sending data to new activity
			  i.putExtra("category", category);
			  i.putExtra("product", product);
			  i.putExtra("key", KEY_AREA);
			  
			  //i.putExtra
			  i.putExtra("company",product);
			  i.putExtra("col", "co_area");
			  i.putExtra("catID",cat_id);
			  startActivity(i);
        	}
        }
        
        @Override public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return new ItemLoader(getActivity());
        }

        @Override public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            // Set the new data in the adapter.
            mAdapter.setData(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override public void onLoaderReset(Loader<ArrayList<String>> loader) {
            // Clear the data in the adapter.
            mAdapter.setData(null);
        }

    }
	
}