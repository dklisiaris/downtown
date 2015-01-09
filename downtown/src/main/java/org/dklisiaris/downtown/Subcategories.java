package org.dklisiaris.downtown;


import java.util.ArrayList;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.AccessAssets;
import org.dklisiaris.downtown.helper.ImageLoader;
import org.dklisiaris.downtown.helper.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Deprecated
 * It was user for the subcategories list
 * @author MeeC
 *
 */
public class Subcategories extends ActionBarActivity {

    // XML node keys
    static final String KEY_CATEGORY = "category"; // parent node
    static final String KEY_SUBTITLE = "subtitle";
    static final String KEY_NAME = "name";
    static final String KEY_SUBCATEGORY = "subcategory";
    static final String KEY_CATEGORIES = "categories";
    static final String KEY_CONFIGURATION = "configuration";
    
    protected static ArrayList<Category> menuItems;    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            SubcategoriesFragment subcats = new SubcategoriesFragment();
            fm.beginTransaction().add(android.R.id.content, subcats).commit();
        }
	}

	
    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class ItemLoader extends AsyncTaskLoader<ArrayList<Category>> {

        ArrayList<Category> mItems;
        String targetItem;
        String targetID;
        Context ct;
        AccessAssets ast;
        Utils util;
        DBHandler db;
        
        public ItemLoader(Context context) {
            super(context);
            targetItem = ((GlobalData)getContext()).getCategory();
            targetID = ((GlobalData)getContext()).getCatID();
            ct = context;
    		 
            menuItems = new ArrayList<Category>();            
            util = new Utils(ct);            
            db = DBHandler.getInstance(getContext());
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public ArrayList<Category> loadInBackground() {
        	
        	menuItems = db.getCategories("cat_parent_id = '"+targetID+"'");
        	db.close();
            // Done!
            return menuItems;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(ArrayList<Category> items) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (items != null) {
                    onReleaseResources(items);
                }
            }
            ArrayList<Category> olditems = items;
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
        @Override public void onCanceled(ArrayList<Category> items) {
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
        protected void onReleaseResources(ArrayList<Category> items) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
	
    public static class CustomAdapter extends ArrayAdapter<Category> {
        private final LayoutInflater mInflater;
        private Context ctx;
        private ImageLoader iLoader;

        public CustomAdapter(Context context) {
            super(context, R.layout.tab_list);
            ctx=context;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            iLoader = new ImageLoader(context);
        }

        public void setData(ArrayList<Category> data) {
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
    
    public static class SubcategoriesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<Category>> {
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
        public void onDestroy(){
        	super.onDestroy();
        	setListAdapter(null);
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
        	if((TextView)v.findViewById(R.id.category)!=null){
			  // selected item 
			  TextView tv = (TextView)v.findViewById(R.id.category);
			  String product = tv.getText().toString();
			  String category = ((GlobalData)getActivity().getApplicationContext()).getCategory();
			  String cat_id = ((GlobalData)getActivity().getApplicationContext()).getCatID();
			  String subcatID = (String)v.getTag();  
			  // Launching new Activity on selecting single List Item
			  Intent i = new Intent(getActivity(), Products.class);
			  // sending data to new activity
			  i.putExtra("category", category);
			  i.putExtra("product", product);
			  i.putExtra("key", KEY_SUBCATEGORY);
			  
			  i.putExtra("company",product);
			  i.putExtra("col", "co_subcategory");
			  i.putExtra("catID",cat_id);
			  i.putExtra("subcatID", subcatID);
			  startActivity(i);
        	}
        }
        
        @Override public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // sample only has one Loader with no arguments, so it is simple.
            return new ItemLoader(getActivity());
        }

        @Override public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
            // Set the new data in the adapter.
            mAdapter.setData(data);

            // The list should now be shown.
            if (isResumed()) {
                setListShown(true);
            } else {
                setListShownNoAnimation(true);
            }
        }

        @Override public void onLoaderReset(Loader<ArrayList<Category>> loader) {
            // Clear the data in the adapter.
            mAdapter.setData(null);
        }

    }
}
