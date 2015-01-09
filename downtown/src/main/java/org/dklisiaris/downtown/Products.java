package org.dklisiaris.downtown;


import java.util.ArrayList;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.adapters.CustomAdapter;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.ImageLoader;
import org.dklisiaris.downtown.widgets.AspectRatioImageView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

public class Products extends ActionBarActivity implements SearchView.OnQueryTextListener{

    SearchView searchView;
    MenuItem searchMenuItem;

    ListView list;
    CustomAdapter adapter;
    DBHandler db;
    String catID,company,col,subcatID=null,keywordID;
    boolean withWebsiteOnly = false;
    ArrayList<String> areas;
    static final int CATEGORY_BASED = 0;
    static final int KEYWORD_BASED = 1;
    int queryMode = CATEGORY_BASED;

    static final int IMG_CHANGE_DELAY = 2500;
    Handler imgChanger=null;
    ArrayList<String> imgFilenames=null;
    ImageLoader iLoader;
    int imgID=0;


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
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.tab_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        String source = i.getStringExtra("source");
        if(source!=null && source.equals("suggestions")){
            keywordID = i.getStringExtra("keywordID");
            queryMode = KEYWORD_BASED;
        }
        else if(source!=null && source.equals("subcats")){
            // getting attached intent data

            subcatID = i.getStringExtra("subcatID");
            catID = i.getStringExtra("catID");
            company = i.getStringExtra("company");
            col = i.getStringExtra("col");
            withWebsiteOnly = i.getBooleanExtra("withWebsiteOnly", false);
            areas = i.getStringArrayListExtra("areas");
            queryMode = CATEGORY_BASED;
        }

        db = DBHandler.getInstance(this);
        iLoader = new ImageLoader(this);

        new CompaniesTask().execute();


    }

    @Override public void onStop(){super.onStop(); if(imgChanger!=null)stopRepeatingTask();}
    @Override public void onResume(){super.onResume(); if(imgChanger!=null)startRepeatingTask();  }

    Runnable m_statusChecker = new Runnable()
    {
        @Override
        public void run() {
            //change the images in a circular array way
            imgID=((imgID+1)%imgFilenames.size());

            AspectRatioImageView imageView = (AspectRatioImageView) findViewById(R.id.com_banner);

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

    protected class CompaniesTask extends AsyncTask<String, Integer, ArrayList<Company>> {
        ArrayList<Company> cmps;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setSupportProgressBarIndeterminateVisibility(true);

        }

        protected ArrayList<Company> doInBackground(String... query) {
            String where;
            ArrayList<Company> comps = null;
            switch(queryMode){
                case CATEGORY_BASED:
                    if(col.equals("co_subcategory") && subcatID != null){
                        //subCatID=db.getSubcatIDbyName(catID, company);
                        //where="co_category='"+catID+"' and co_subcategory like '"+subcatID+"' or co_subcategory like '%,"+subcatID+",%' or co_subcategory like '%,"+subcatID+"' or co_subcategory like '"+subcatID+",%'";
                        comps = db.getCompaniesFiltered(subcatID,withWebsiteOnly,areas);
						
						/* Getting the appropriate banners */
                        imgFilenames = db.getBanners(Integer.parseInt(subcatID));
                        imgID = imgFilenames.size()-1;
                    }
                    else{
                        where="co_category='"+catID+"' and "+col+"='"+company+"'";
                        comps = db.getCompaniesWhere(where);
                    }
                    break;
                case KEYWORD_BASED:
                    comps = db.getCompaniesByKeywordId(keywordID);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported Query Mode");
            }




            ((GlobalData)getApplicationContext()).setSelected_companies(comps);
            return comps;
        }


        protected void onPostExecute(ArrayList<Company> comps) {

            list=(ListView)findViewById(R.id.list);
            View header_view = View.inflate(getApplicationContext(), R.layout.products_header, null);
            TextView hv = ((TextView)header_view.findViewById(R.id.numProds));
            hv.setText(Integer.toString(comps.size()));
            list.addHeaderView(header_view);

            adapter = new CustomAdapter(getApplicationContext());
            adapter.setSubcatID(subcatID);
            adapter.setData(comps);
            list.setAdapter(adapter);
            //Log.d("Debug","gets Here");
            if(subcatID!=null)Log.d("--- This is subcatID ---",subcatID);
            setSupportProgressBarIndeterminateVisibility(false);
	        
	        /* Starting the banner show if there are any banners */
            if(imgFilenames!=null && imgFilenames.size()>0){
                imgChanger = new Handler();
                startRepeatingTask();
            }
            else{
                imgChanger = null;
            }

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
                        if(subcatID!=null)i.putExtra("subcatID", Integer.parseInt(subcatID));
                        i.putExtra("pos",position-1);
                        startActivity(i);
                    }
                }
            });
        }
    }
}
