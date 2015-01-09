package org.dklisiaris.downtown;


import java.util.ArrayList;
import java.util.Locale;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.adapters.CustomAdapter;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{

    SearchView searchView;
    MenuItem searchMenuItem;
    ListView list;
    private SparseArray<String> mapHash;
    CustomAdapter adapter; 
    ArrayList<Company> comps;
    private DBHandler db;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general, menu);        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));        
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));        
        searchView.setIconifiedByDefault(false);        
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setSuggestionsAdapter(new CustomSuggestionsAdapter(this, searchManager.getSearchableInfo(getComponentName()), searchView));

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.tab_list);
		
		db = DBHandler.getInstance(this);
		mapHash = ((GlobalData)getApplicationContext()).getCatsMap();
		if(mapHash == null){
			((GlobalData)getApplicationContext()).setCatsMap(db.getCatHash());
			mapHash = ((GlobalData)getApplicationContext()).getCatsMap();
		}
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    handleIntent(intent);
	    
	}
	   /*public void onNewIntent(Intent intent) { 
	      setIntent(intent); 
	      handleIntent(intent); 
	   } */
	
	   public void onListItemClick(ListView l, 
	      View v, int position, long id) { 
	      // call detail activity for clicked entry 
	   } 
	
	   private void handleIntent(Intent intent) { 
	      if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
	         String query = intent.getStringExtra(SearchManager.QUERY); 
	         doSearch(query);
	         //searchView.onActionViewCollapsed();
	         //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	      } 
	      else if (Intent.ACTION_VIEW.equals(intent.getAction())) { 
	          Uri keywordUri = intent.getData(); 
	          String id = keywordUri.getLastPathSegment(); 
	          Log.d("KeywordID",id);
	          Intent companiesIntent = new Intent(getApplicationContext(), Products.class); 
	          companiesIntent.putExtra("source", "suggestions"); 
	          companiesIntent.putExtra("keywordID", id); 
	          startActivity(companiesIntent); 
	          finish();	 	          
	       }  	 
	      
	      else{
	    	  Intent i = getIntent();
	    	  String query = i.getStringExtra("query");
	    	  doSearch(query);
	    	  //searchMenuItem.onActionViewCollapsed();
	    	  //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	      
	      }
	   }    
	
	   private void doSearch(String queryStr) { 

		   SearchInBg task = new SearchInBg();
		   task.execute(queryStr);// = new ArrayList<Company>();
	   } 

	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
            return true;
        case R.id.toMainHome:
        	Intent upIntent = new Intent(this,MainActivity.class);
        	upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(upIntent);
        	finish();
        	return true;
        case R.id.favourites:
        	Intent favIntent = new Intent(this,FavsActivity.class);
        	favIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(favIntent);
        	finish();
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
    public boolean onQueryTextSubmit(String query) {
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
        return true;
    }
 

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/**
	 * This class performs search operation in background thread
	 * @author MeeC
	 *
	 */
	 protected class SearchInBg extends AsyncTask<String, Integer, ArrayList<Company>> {
		 ArrayList<Company> cmps;
		@Override
		protected void onPreExecute() {
		    super.onPreExecute();	      
		    setSupportProgressBarIndeterminateVisibility(true);		   
		}
		 
		 protected ArrayList<Company> doInBackground(String... query) {
			 ArrayList<Company> match = new ArrayList<Company>();
			 String queryStr = query[0];
			 queryStr=queryStr.replace("ς","Σ");
			   int key = 0;
			   int cat_key=0;
			   String catKeyStr="0";
			   for(int i = 0; i < mapHash.size(); i++) {
			      key = mapHash.keyAt(i);
			      // get the object by the key.
			      String cat = mapHash.get(key);
			      cat = cat.toLowerCase();
			      cat=cat.replace('ά', 'α');
			      cat=cat.replace('έ', 'ε');
				   cat=cat.replace('ό', 'ο');
				   cat=cat.replace('ύ', 'υ');
				   cat=cat.replace('ί', 'ι');
				   cat=cat.replace('ή', 'η');
				   cat=cat.replace('ώ', 'ω'); 
				   cat=cat.replace('ς', 'σ'); 
				   if (cat.contains(queryStr.toLowerCase()) || queryStr.toLowerCase().contains(cat)){
					   cat_key = key;
					   catKeyStr=Integer.toString(cat_key);
					   break;
				   }				   
			   }
			  
			   
			   String q1 = queryStr.toUpperCase(new Locale("el_GR"));
			   q1=q1.replaceAll(" ","%");
			   q1=q1.replaceAll("ς","Σ");
			   //String q2 = queryStr.toLowerCase(new Locale("el_GR"));
			   //String q3 = queryStr.substring(0, 1).toUpperCase(new Locale("el_GR")) + queryStr.substring(1);
			   //Log.d("Searching",queryStr);
			   
			   /* String where = "co_description like '%"+q1+"%' or "+
					   "co_tel like '%"+q1+"%' or "+
					   "co_name like '%"+q1+"%'"; */
			   double t1,t2;
			   t1 = System.nanoTime();
			   if(!catKeyStr.equals("0")){
				   /*where = "co_description like '%"+q1+"%' or "+
						   "co_tel like '%"+q1+"%' or "+
						   "co_name like '%"+q1+"%' or "+
						   "co_subcategory like '"+catKeyStr+"' or co_subcategory like '%,"+catKeyStr+",%' or co_subcategory like '%,"+catKeyStr+"' or co_subcategory like '"+catKeyStr+",%' or "+
						   "co_category ='"+catKeyStr+"'";*/
				   match = db.performSearch(q1, catKeyStr);
			   }
			   else{
				   match = db.performSearch(q1, null);
			   }
			   t2 = (System.nanoTime() - t1)/1000000.0;
			   Log.d("---- Query completed in ----", Double.toString(t2));		
				   /*"co_description like '%"+q2+"%' or "+
				   "co_description like '%"+q3+"%' or "+
				   "co_area like '%"+q1+"%' or "+
				   "co_area like '%"+q2+"%' or "+
				   "co_area like '%"+q3+"%' or "+
				   "co_county like '%"+q1+"%' or "+
				   "co_county like '%"+q2+"%' or "+
				   "co_county like '%"+q3+"%' or "+
				   "co_address like '%"+q1+"%' or "+
				   "co_address like '%"+q2+"%' or "+
				   "co_address like '%"+q3+"%' or "+
				   "co_name like '%"+q1+"%' or "+
				   "co_name like '%"+q2+"%' or "+
				   "co_name like '%"+q3+"%'";*/
			   //match = db.getCompaniesWhere(where);		
		     return match;
		 }
		
		
		 protected void onPostExecute(ArrayList<Company> match) {
			 setSupportProgressBarIndeterminateVisibility(false);
			    comps= match;
		        ((GlobalData)getApplicationContext()).setSelected_companies(comps);
		                        
		        list=(ListView)findViewById(R.id.list);
		        View header_view = View.inflate(getApplicationContext(), R.layout.products_header, null);
		        TextView hv = ((TextView)header_view.findViewById(R.id.numProds));
		        hv.setText(Integer.toString(comps.size()));
		        list.addHeaderView(header_view);
		        
		        adapter = new CustomAdapter(getApplicationContext());
		        adapter.setData(comps);
		        list.setAdapter(adapter);
		        Log.d("Debug","gets Here");
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	               
						// selected item 
						if((TextView)view.findViewById(R.id.name)!=null){
							TextView tv = (TextView)view.findViewById(R.id.name);
							String product = tv.getText().toString();				   
							// Launching new Activity on selecting single List Item
							Intent i = new Intent(getApplicationContext(), SingleListItem.class);
							// sending data to new activity
							i.putExtra("product", product);
							i.putExtra("pos",position-1);
							finish();
							startActivity(i);
						}
					}
				});
		 }
		 

	 }	
}
