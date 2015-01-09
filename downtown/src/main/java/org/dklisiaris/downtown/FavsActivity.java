package org.dklisiaris.downtown;


import java.util.ArrayList;
import java.util.Set;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.adapters.MultiSelectionAdapter;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavsActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{
	
	protected static ArrayList<Company> favs;
	protected static ArrayList<String> favArray;
	protected static boolean actionState=false;
    SearchView searchView;
    MenuItem searchMenuItem;
    protected static SparseArray<String> mapHash;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Αγαπημένα");
        
        mapHash = ((GlobalData)getApplicationContext()).getCatsMap();
        FragmentManager fm = getSupportFragmentManager();        
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            FavsFragment list = new FavsFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fav_normal_menu, menu);
        
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //Create the search view
        //searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Τι,Ποιός,Περιοχή/Διεύθυνση,Τηλέφωνο,Περιγραφή");
        searchView.setOnQueryTextListener(this);
        searchView.setSuggestionsAdapter(new CustomSuggestionsAdapter(this, searchManager.getSearchableInfo(getComponentName()), searchView));
        //searchView.setIconified(true);
        
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setHintTextColor(getResources().getColor(R.color.white));
        searchMenuItem = menu.findItem(R.id.menu_search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                	MenuItemCompat.collapseActionView(searchMenuItem);
                    searchView.setQuery("", false);
                }
            }
        });
        return true;
    }
    
    @Override
    public boolean onQueryTextSubmit(String query) {
    	if (query.length()<3){
    		Toast t = Toast.makeText(this, "Απαιτούνται τουλαχιστον 3 χαρακτήρες", Toast.LENGTH_LONG);
    		t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
    		t.show();
    	}else{    	
	    	Intent sIntent = new Intent(this,SearchActivity.class);
	    	sIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	sIntent.putExtra("query",query);
	    	//searchView.setIconified(true);
	    	MenuItemCompat.collapseActionView(searchMenuItem);
	    	startActivity(sIntent);
    	}   	
        return true;
    }
 

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	if(getIntent().getStringExtra("caller")!=null){
            	Intent upIntent = new Intent(this,MainActivity.class);
            	upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(upIntent);
            	finish();
        	}
        	else onBackPressed();
            return true;
        case R.id.toMainHome:
        	Intent upIntent = new Intent(this,MainActivity.class);
        	upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(upIntent);
        	finish();
        	return true;          	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	public static class FavsFragment extends ListFragment {
	
	    private MultiSelectionAdapter adapter;
	    private ListView list;
	    private DBHandler db;	    
	    // if ActoinMode is null - assume we are in normal mode
	    private ActionMode actionMode;
	
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	    {
	        View v = inflater.inflate(R.layout.fav_list, null);
	        db = DBHandler.getInstance(getActivity());
	        favArray = db.getFavSeq();
	        String where="";
	        if(favArray.size()>0){	        
		        for(String s : favArray){
		        	if(where.equals(""))where=where+"com._id='"+s+"'";
		        	else where=where+" or com._id='"+s+"'";
		        }
	        }else where="com._id='0'";
	        
	        favs = db.getCompaniesWhere(where);
	        db.close();	        
	        setHasOptionsMenu(true);
	        this.list = (ListView) v.findViewById(android.R.id.list);	        
	        this.initListView();
	        return v;
	    }
	    
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    	//menu.clear();
	        //inflater.inflate(R.menu.fav_normal_menu, menu);
	        super.onCreateOptionsMenu(menu, inflater);
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.delete:
                if (actionMode != null) {
                    // if already in action mode - do nothing
                    return false;
                }
                ActionBarActivity activity=(ActionBarActivity)getActivity();                
                activity.startSupportActionMode(new ActionModeCallback());
                actionMode.invalidate();
	        	return true;          	
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	
	    @Override
	    public void onPause()
	    {
	        super.onPause();
	        if (this.actionMode != null) {
	            this.actionMode.finish();
	        }
	        if(db!=null){
	        	db.close();
	        }
	    }
	
	    @Override
	    public void onResume() {
	        super.onResume();
	        if(db==null){
	        	db = DBHandler.getInstance(getActivity());
	        }
	        updateData();
	    }
	
	    // update ListView
	    protected void updateData()
	    {
	        if (adapter == null) {
	            return;
	        }
	        adapter.clear();
	        // my kinda stuff :)
	        
	        favArray = db.getFavSeq();
	        String where="";
	        if(favArray.size()>0){
		        for(String s : favArray){
		        	if(where.equals(""))where=where+"com._id='"+s+"'";
		        	else where=where+" or com._id='"+s+"'";
		        }
	        }
	        else where="com._id='0'";
	        favs = db.getCompaniesWhere(where);
	        db.close();
	        if (favs != null) {
	            adapter.updateData(favs);
	            if (actionMode != null) {
	                actionMode.invalidate();
	            }
	        }
	        // if empty - finish action mode.
	        if (actionMode != null && (favs == null || favs.size() == 0)) {
	            actionMode.finish();
	        }
	    }
	
	    private void initListView()
	    {
	        this.adapter = new MultiSelectionAdapter(getActivity(), mapHash);
	        
	        this.list.setAdapter(adapter);	        
	    }
	
	
        @Override 
        public void onListItemClick(ListView l, View v, int position, long id) {
            if (actionMode != null) {
                // if action mode, toggle checked state of item            	
                adapter.toggleChecked(position);
                actionMode.invalidate();
            } else {
                // do whatever you should on item click
				TextView tv = (TextView)v.findViewById(R.id.name);
				String product = tv.getText().toString();				   
				// Launching new Activity on selecting single List Item
				((GlobalData)getActivity().getApplicationContext()).setSelected_companies(favs);
				Intent i = new Intent(getActivity(), SingleListItem.class);
				// sending data to new activity
				i.putExtra("product", product);
				i.putExtra("pos",position);
				startActivity(i);
            }
        	
        }
	    // all our ActionMode stuff here :)
	    private final class ActionModeCallback
	            implements ActionMode.Callback
	    {
	
	        // " selected" string resource to update ActionBar text
	        private String selected = getActivity().getString(
	                R.string.selected);
	
	        @Override
	        public boolean onCreateActionMode(ActionMode mode, Menu menu)
	        {
	            adapter.enterMultiMode();
	            // save global action mode
	            actionMode = mode;
	            return true;
	        }
	
	        @Override
	        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	        {
	            // remove previous items
	            menu.clear();
	            final int checked = adapter.getCheckedItemCount();
	            // update title with number of checked items
	            mode.setTitle(checked+" "+ this.selected);
	            switch (checked) {
	            default:
	                getActivity().getMenuInflater().inflate(
	                        R.menu.fav_menu, menu);
	                // remove rename option - because we have more than one selected
	                //menu.removeItem(R.id.library_context_rename);
	                return true;
	            }
	        }
	
	        @Override
	        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	        {
	            switch (item.getItemId()) {            
	            case R.id.deleteItem:
	                Set<Integer> checked = adapter.getCheckedItems();
	                
	                // iterate through selected items and delete them
	                for (Integer ci : checked) {
	                    Log.d("WOOW",adapter.getItem(ci.intValue()).getName());
	                    db.removeFav((Integer.toString(adapter.getItem(ci.intValue()).getId())));
	                }
	                db.close();
	                updateData();
	                return true;
	            default:
	                return false;
	            }
	        }
	
	        
	        @Override
	        public void onDestroyActionMode(ActionMode mode)
	        {
	            adapter.exitMultiMode();
	            // don't forget to remove it, because we are assuming that if it's not null we are in ActionMode
	            actionMode = null;
	        }
	
	    }
	
	}
}