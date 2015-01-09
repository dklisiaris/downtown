package org.dklisiaris.downtown;

import java.util.ArrayList;
import org.dklisiaris.downtown.R;

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

public class Websites extends ActionBarActivity{

    // XML node keys
    static final String KEY_CATEGORY = "category"; // parent node
    static final String KEY_SUBTITLE = "subtitle";
    static final String KEY_NAME = "name";
    static final String KEY_SUBCATEGORY = "subcategory";
    static final String KEY_CATEGORIES = "categories";    
    static final String KEY_SITE = "eshop";
    static final String KEY_AVAIL = "availability";
    protected static ArrayList<String> menuItems = new ArrayList<String>();  
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            WebsitesFragment sites = new WebsitesFragment();
            fm.beginTransaction().add(android.R.id.content, sites).commit();
        }
	}

    public static class ItemLoader extends AsyncTaskLoader<ArrayList<String>> {

        ArrayList<String> mItems;
        Context ct;

        public ItemLoader(Context context) {
            super(context);
            ct = context;
            menuItems = new ArrayList<String>();
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public ArrayList<String> loadInBackground() {
            
        	menuItems.add("Διαθέσιμα");
        	menuItems.add("Μη Διαθέσιμα");
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
    
    public static class WebsitesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<String>> {
        // This is the Adapter being used to display the list's data.
        CustomAdapter mAdapter;
        // If non-null, this is the current filter the user has provided.
        String mCurFilter;
        OnQueryTextListenerCompat mOnQueryTextListenerCompat;
        
        @Override 
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Give some text to display if there is no data.
            //setEmptyText("Δεν βρέθηκαν αποτελέσματα...");
 
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

        @Override 
        public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            //Log.d("Subcats..", "Item clicked: " + id);
			  // selected item 
        	if((TextView)v.findViewById(R.id.category)!=null){
			  TextView tv = (TextView)v.findViewById(R.id.category);
			  String product = (tv.getText().toString()).equals("Διαθέσιμα")? "yes" : "no";
			  String avail = (tv.getText().toString()).equals("Διαθέσιμα")? "1" : "0";
			  String category = ((GlobalData)getActivity().getApplicationContext()).getCategory();
			  String cat_id = ((GlobalData)getActivity().getApplicationContext()).getCatID();
			  // Launching new Activity on selecting single List Item
			  Intent i = new Intent(getActivity(), Products.class);
			  // sending data to new activity
			  i.putExtra("category", category);
			  i.putExtra("product", product);
			  i.putExtra("key", KEY_AVAIL);
			  
			  i.putExtra("company", avail);
			  i.putExtra("catID",cat_id);
			  i.putExtra("col", "co_availability");
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
