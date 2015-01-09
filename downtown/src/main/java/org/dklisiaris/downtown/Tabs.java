package org.dklisiaris.downtown;

//import gr.futurearts.greekguide.actionbar.ActionBar;
import java.util.ArrayList;
import org.dklisiaris.downtown.R;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;
 
/**
 * Deprecated
 * It was used for the 3 tabs list menu
 * @author MeeC
 *
 */
public class Tabs extends ActionBarActivity implements SearchView.OnQueryTextListener{
    // TabSpec Names
    private static final String SUBCATEGORY_SPEC = "Κατηγορία";
    private static final String AREA_SPEC = "Περιοχή";
    private static final String WEBSITE_SPEC = "Website";
    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    SearchView searchView;
    MenuItem searchMenuItem;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        
        
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec(SUBCATEGORY_SPEC).setIndicator(SUBCATEGORY_SPEC),
                Subcategories.SubcategoriesFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec(AREA_SPEC).setIndicator(AREA_SPEC),
                Addresses.AddressesFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec(WEBSITE_SPEC).setIndicator(WEBSITE_SPEC),
                Websites.WebsitesFragment.class, null);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }
    
    @Override
    public boolean onQueryTextSubmit(String query) {
    	if (query.length()<4){
    		Toast t = Toast.makeText(this, "Απαιτούνται τουλαχιστον 4 χαρακτήρες", Toast.LENGTH_LONG);
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
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
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
        searchView.setQueryHint("Τι,Ποιός,Περιοχή/Διεύθυνση,Τηλέφωνο,Περιγραφή");
        searchView.setOnQueryTextListener(this);
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
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
