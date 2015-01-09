package org.dklisiaris.downtown;

//import gr.futurearts.greekguide.actionbar.ActionBar;

import java.util.ArrayList;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.adapters.CustomStringAdapter;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.ImageLoader;
import org.dklisiaris.downtown.maps.Nearby;
import org.dklisiaris.downtown.widgets.AspectRatioImageView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{
	
    static final int IMG_CHANGE_DELAY = 2300;
    SearchView searchView;
    MenuItem searchMenuItem;
    ListView list;
    CustomStringAdapter adapter;
    Handler imgChanger;
    ArrayList<String> imgFilenames;
    ImageLoader iLoader;
    int imgID=0;
    
    private DBHandler db;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general, menu);
        
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //Create the search view
        //searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint(getString(R.string.query_hint));
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
        //searchView.setSubmitButtonEnabled(true);
		//SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		//SearchableInfo info = searchManager.getSearchableInfo(getComponentName());

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.toMainHome:
        	Toast.makeText(getApplicationContext(),getString(R.string.this_is_home), Toast.LENGTH_SHORT).show();
        	return true;
        case R.id.favourites:
        	Intent favIntent = new Intent(this,FavsActivity.class);
        	favIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	favIntent.putExtra("caller","Main");
        	startActivity(favIntent);
        	//finish();
        	return true;  
        case R.id.FreeEntry:
        	Intent i = new Intent(this,MoreActivity.class);
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	i.putExtra("title","entry");
        	startActivity(i);
        	//finish();
        	return true; 
        case R.id.MoreInfo:
        	Intent mi = new Intent(this,MoreActivity.class);
        	mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	mi.putExtra("title","info");
        	startActivity(mi);
        	return true;
        case R.id.Contact:
        	Intent ci = new Intent(this,MoreActivity.class);
        	ci.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	ci.putExtra("title","contact");
        	startActivity(ci);
        	return true;   
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false); 		
       
        final ArrayList<Category> listCategs;
        
        db = DBHandler.getInstance(this);
               
        imgFilenames = db.getBanners(0);
        iLoader = new ImageLoader(this);
        
        /* imgID is used by runnable thread to show the next banner
         * runnable uses a mod opearation to access the array in circular way
         * so we start with the last id in order to show the first banner in first increment
         */       
        imgID = imgFilenames.size()-1;
               
        listCategs = db.getCategories("cat_parent_id is null");
        View header_view = View.inflate(this, R.layout.img_header, null);
        Button btn_nearby = (Button)header_view.findViewById(R.id.btn_nearby);
        
        btn_nearby.setOnClickListener(new OnClickListener() {           
        	public void onClick(View v){
        		if(isNetworkAvailable()){
        			Intent i = new Intent(getApplicationContext(), Nearby.class);
        			//Intent i = new Intent(getApplicationContext(), TestActivity.class);        			
            		startActivity(i);
        		}
        		else Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        		
        	}    
		});
        
        
        list=(ListView)findViewById(R.id.list);

        list.addHeaderView(header_view);
        // Getting adapter by passing xml data ArrayList
        adapter=new CustomStringAdapter(this, listCategs);
        list.setAdapter(adapter);
        
        imgChanger = new Handler();
        //startRepeatingTask();
        
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!(view.findViewById(R.id.title) == null)) {				
				  // selected item 				
				  TextView tv = (TextView)view.findViewById(R.id.title);
				  String product = tv.getText().toString();
				  String catID=(String)view.getTag(); 
				  // Launching new Activity on selecting single List Item
				  
				  //Intent i = new Intent(getApplicationContext(), Tabs.class);
				  Intent i = new Intent(getApplicationContext(), SubcatsAndFilters.class);
				  
				  GlobalData state = ((GlobalData)getApplicationContext());
				  state.setCategory(product);
				  state.setCatID(catID);
				  // sending data to new activity
				  //i.putExtra("product", product);
				  startActivity(i);
				}
			}
		});				
 
	}
	
	@Override public void onStop(){super.onStop(); stopRepeatingTask();}
	@Override public void onResume(){super.onResume(); stopRepeatingTask(); startRepeatingTask();  }
	
	Runnable m_statusChecker = new Runnable()
	{
	     @Override 
	     public void run() {
	          //change the images in a circular array way	         
	         imgID=((imgID+1)%imgFilenames.size());
	         
	         AspectRatioImageView imageView = (AspectRatioImageView) findViewById(R.id.imageHead);

	        // load image
	        Drawable d = iLoader.loadImage(imgFilenames.get(imgID));
	        if(d!=null)imageView.setImageDrawable(d);
			
			imgChanger.postDelayed(m_statusChecker, IMG_CHANGE_DELAY);
	     }
	};

	void startRepeatingTask()
	{
	    m_statusChecker.run(); 
	}

	void stopRepeatingTask()
	{
		imgChanger.removeCallbacks(m_statusChecker);
	}

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Toast.makeText(this, "You searched for: " + query, Toast.LENGTH_LONG).show();
    	if (query.length()<3){
    		Toast t = Toast.makeText(this, getString(R.string.char_limit), Toast.LENGTH_LONG);
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
    	//finish();    	
        return true;
    }
 

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
	
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
}
