package org.dklisiaris.downtown;


import java.util.ArrayList;
import java.util.List;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.adapters.AddressFilterAdapter;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.adapters.SubcatsAdapter;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.AccessAssets;
import org.dklisiaris.downtown.helper.ImageLoader;
import org.dklisiaris.downtown.helper.Utils;
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
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class SubcatsAndFilters extends ActionBarActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    MenuItem searchMenuItem;

    protected List<Category> menuItems;
    protected List<String> menuAreas;
    protected String targetItem;
    protected String targetID;
    protected Context ct;
    protected AccessAssets ast;
    protected Utils util;
    protected ListView list;
    protected ListView addressList;
    protected DBHandler db;
    protected SubcatsAdapter adapter;
    protected AddressFilterAdapter addressAdapter;
    //private static final String TAG = "SlidePanel";
    private ImageView arrowIcon;
    private boolean withWebsiteOnly = false;

    static final int IMG_CHANGE_DELAY = 2500;
    Handler imgChanger=null;
    ArrayList<String> imgFilenames=null;
    ImageLoader iLoader;
    int imgID=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.list_subcategories);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrowIcon = (ImageView)findViewById(R.id.collapse_icon);
        arrowIcon.setImageResource(R.drawable.ic_navigation_collapse);
        arrowIcon.setTag("up");

        SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        boolean isSplited = getApplicationContext().getResources().getBoolean(R.bool.split_action_bar);
        if(!isSplited){
            setMargins(layout,0,0,0,0);
        }
        //layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        layout.setAnchorPoint(0.7f);
        layout.setDragView(findViewById(R.id.panel_top));
        layout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //Log.i(TAG, "onPanelSlide, offset " + slideOffset);                
                if (slideOffset > 0.8) {
                    if (getSupportActionBar().isShowing()) {
                        getSupportActionBar().hide();
                    }
                } else {
                    if (!getSupportActionBar().isShowing()) {
                        getSupportActionBar().show();
                    }

                }
                if(slideOffset >= 0.7){
                    if (arrowIcon.getTag() == "up"){
                        arrowIcon.setImageResource(R.drawable.ic_navigation_expand);
                        arrowIcon.setTag("down");
                    }
                }
                else{
                    if (arrowIcon.getTag() == "down"){
                        arrowIcon.setImageResource(R.drawable.ic_navigation_collapse);
                        arrowIcon.setTag("up");
                    }
                }
                Log.i("Slide offset:", Float.toString(slideOffset));

            }

            @Override
            public void onPanelExpanded(View panel) {
                //Log.i(TAG, "onPanelExpanded");                
            }

            @Override
            public void onPanelCollapsed(View panel) {
                //Log.i(TAG, "onPanelCollapsed");                
            }

            @Override
            public void onPanelAnchored(View panel) {
                //Log.i(TAG, "onPanelAnchored");

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        ct = getApplicationContext();
        targetItem = ((GlobalData)ct).getCategory();
        targetID = ((GlobalData)ct).getCatID();

        menuItems = new ArrayList<Category>();
        menuAreas = new ArrayList<String>();

        util = new Utils(ct);
        db = DBHandler.getInstance(ct);
        iLoader = new ImageLoader(this);

        new SubcatsTask().execute();

    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onStop(){super.onStop(); if(imgChanger!=null)stopRepeatingTask();}
    @Override public void onResume(){super.onResume(); if(imgChanger!=null)startRepeatingTask();  }

    Runnable m_statusChecker = new Runnable()
    {
        @Override
        public void run() {
            //change the images in a circular array way
            imgID=((imgID+1)%imgFilenames.size());

            AspectRatioImageView imageView = (AspectRatioImageView) findViewById(R.id.banner);

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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.website_only:
                if (checked){
                    withWebsiteOnly = true;
                }
                break;
            case R.id.website_all:
                if (checked){
                    withWebsiteOnly = false;
                }
                break;
        }
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * Show a message giving the selected item captions
     */
    @SuppressWarnings("unused")
    private void showSelectedItems() {
        final StringBuffer sb = new StringBuffer("Selection: ");

        // Get an array that tells us for each position whether the item is
        // checked or not
        // --
        final SparseBooleanArray checkedItems = addressList.getCheckedItemPositions();
        if (checkedItems == null) {
            Toast.makeText(this, "No selection info available", Toast.LENGTH_LONG).show();
            return;
        }

        // For each element in the status array
        // --
        boolean isFirstSelected = true;
        final int checkedItemsCount = checkedItems.size();
        for (int i = 0; i < checkedItemsCount; ++i) {
            // This tells us the item position we are looking at
            // --
            final int position = checkedItems.keyAt(i);

            // This tells us the item status at the above position
            // --
            final boolean isChecked = checkedItems.valueAt(i);

            if (isChecked) {
                if (!isFirstSelected) {
                    sb.append(", ");
                }
                sb.append(menuAreas.get(position));
                isFirstSelected = false;
            }
        }

        // Show a message with the countries that are selected
        // --
        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
    }

    protected ArrayList<String> getSelectedItems(){
        ArrayList<String> selectedItems = new ArrayList<String>();
        // Get an array that tells us for each position whether the item is
        // checked or not
        // --
        final SparseBooleanArray checkedItems = addressList.getCheckedItemPositions();
        if (checkedItems == null) {
            selectedItems=null;;
            return selectedItems;
        }

        // For each element in the status array
        final int checkedItemsCount = checkedItems.size();
        for (int i = 0; i < checkedItemsCount; ++i) {
            // This tells us the item position we are looking at
            // --
            final int position = checkedItems.keyAt(i);

            // This tells us the item status at the above position
            // --
            final boolean isChecked = checkedItems.valueAt(i);

            if (isChecked && position!=0) {
                String[] splitArea = menuAreas.get(position).split("\\-",-1);
                selectedItems.add(splitArea[0].trim());
            }
            else if(isChecked && position==0){
                selectedItems=null;
                break;
            }
        }

        return selectedItems;
    }

    protected class SubcatsTask extends AsyncTask<String, Integer, List<Category>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setSupportProgressBarIndeterminateVisibility(true);
        }

        protected List<Category> doInBackground(String... query) {
            menuItems = db.getCategories("cat_parent_id = '"+targetID+"'");
            menuAreas.add("Όλες οι Περιοχές");
            menuAreas.addAll(db.getAreas("co_category = '"+targetID+"' and co_area != ''"));

            imgFilenames = db.getBanners(Integer.parseInt(targetID));
            imgID = imgFilenames.size()-1;

            db.close();

            return menuItems;
        }


        protected void onPostExecute(List<Category> cats) {

            list=(ListView)findViewById(R.id.list);
            addressList=(ListView)findViewById(R.id.list2);

            View header_view = View.inflate(ct, R.layout.tabs_header, null);
            TextView hv = ((TextView)header_view.findViewById(R.id.ctgr));
            hv.setText(((GlobalData)ct).getCategory());

            list.addHeaderView(header_view);

            if(adapter==null){
                adapter = new SubcatsAdapter(ct);
            }
            if(addressAdapter==null){
                addressAdapter = new AddressFilterAdapter(ct);
            }
            adapter.setData(cats);
            addressAdapter.setData(menuAreas);

            list.setAdapter(adapter);

            addressList.setItemsCanFocus(false);
            addressList.setAdapter(addressAdapter);
            addressList.setItemChecked(0, true);

            setSupportProgressBarIndeterminateVisibility(false);
	        
	        /* Starting the banner show if there are any banners */
            if(imgFilenames!=null && imgFilenames.size()>0){
                imgChanger = new Handler();
                startRepeatingTask();
            }
            else{
                imgChanger = null;
            }


            addressList.setOnItemClickListener(new OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View v, int position, long id){
					/* If click is on address uncheck all addresses */
                    if(position!=0 && addressList.isItemChecked(position) && addressList.isItemChecked(0) ){
                        addressList.setItemChecked(0, false);
                        addressAdapter.notifyDataSetChanged();
                    }
	        		/* If click is on all address uncheck the clicked addresses */
                    else if(position==0 && addressList.isItemChecked(0)){
                        SparseBooleanArray checkedItems = addressList.getCheckedItemPositions();
                        for(int i = 1; i<checkedItems.size(); i++){
                            if (checkedItems.valueAt(i) && checkedItems.keyAt(i)!=0) {
                                addressList.setItemChecked(checkedItems.keyAt(i), false);
                            }
                        }
                        addressAdapter.notifyDataSetChanged();
                    }
	        		/* If unclicked all addresses don't let happen. */
                    else if(position==0 && !addressList.isItemChecked(0)){
                        addressList.setItemChecked(0, true);
                        addressAdapter.notifyDataSetChanged();
                    }
	        		/* Normally click or unclick addresses, except if there is no clicked address in which case we click all addresses */
                    else{
                        boolean othersSelected = false;
                        SparseBooleanArray checkedItems = addressList.getCheckedItemPositions();
                        for(int i = 1; i<checkedItems.size(); i++){
                            if (checkedItems.valueAt(i) && checkedItems.keyAt(i)!=0) {
                                othersSelected = true;
                                break;
                            }
                        }
                        if(!othersSelected){
                            addressList.setItemChecked(0, true);
                        }
                        addressAdapter.notifyDataSetChanged();
                    }
                }
            });

            list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    // selected item
                    if((TextView)v.findViewById(R.id.category)!=null){
                        // selected item
                        TextView tv = (TextView)v.findViewById(R.id.category);
                        String product = tv.getText().toString();
                        String category = ((GlobalData)ct.getApplicationContext()).getCategory();
                        String cat_id = ((GlobalData)ct.getApplicationContext()).getCatID();
                        String subcatID = (String)v.getTag();
                        // Launching new Activity on selecting single List Item
                        Intent i = new Intent(ct, Products.class);
                        // sending data to new activity
                        i.putExtra("category", category);
                        i.putExtra("product", product);
                        i.putExtra("key", "subcategory");

                        i.putExtra("company",product);
                        i.putExtra("col", "co_subcategory");
                        i.putExtra("catID",cat_id);
                        i.putExtra("subcatID", subcatID);

                        i.putExtra("withWebsiteOnly", withWebsiteOnly);
                        i.putStringArrayListExtra("areas", getSelectedItems());
                        i.putExtra("source","subcats");
                        startActivity(i);
                    }
                }
            });
        }
    }
}
