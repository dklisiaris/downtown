package org.dklisiaris.downtown.maps;

import org.dklisiaris.downtown.FavsActivity;
import org.dklisiaris.downtown.GlobalData;
import org.dklisiaris.downtown.MainActivity;
import org.dklisiaris.downtown.MoreActivity;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.SearchActivity;
import org.dklisiaris.downtown.SingleListItem;
import org.dklisiaris.downtown.adapters.CustomSuggestionsAdapter;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.widgets.MultiSpinner;
import org.dklisiaris.downtown.widgets.MultiSpinner.MultiSpinnerListener;


import java.util.ArrayList;
import java.util.Locale;

import org.w3c.dom.Document;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

public class Nearby extends AbstractMapActivity implements
        OnNavigationListener, OnInfoWindowClickListener, LocationSource, LocationListener,
        ClusterManager.OnClusterClickListener<CompanyMarker>,
        ClusterManager.OnClusterInfoWindowClickListener<CompanyMarker>,
        ClusterManager.OnClusterItemClickListener<CompanyMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<CompanyMarker>,MultiSpinnerListener,
        SearchView.OnQueryTextListener{

    private static final String STATE_NAV="nav";
    /*
    private static final int[] MAP_TYPE_NAMES= { R.string.normal,
      R.string.hybrid, R.string.satellite, R.string.terrain };
    private static final int[] MAP_TYPES= { GoogleMap.MAP_TYPE_NORMAL,
      GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
      GoogleMap.MAP_TYPE_TERRAIN };
    */
    private static final String[] DIRECTION_MODE_NAMES= {"Αυτοκίνητο","Πεζός","Λεωφορείο"};
    private static final String[] DIRECTION_MODES_TYPES= {GMapV2Direction.MODE_DRIVING,
            GMapV2Direction.MODE_WALKING,GMapV2Direction.MODE_TRANSIT};

    SearchView searchView;
    MenuItem searchMenuItem;

    protected String directionMode = GMapV2Direction.MODE_DRIVING;
    private GoogleMap map=null;
    private OnLocationChangedListener mapLocationListener=null;
    private LocationManager locMgr=null;
    private Criteria crit=new Criteria();
    private AlertDialog alert=null;
    Location currentLocation=null;
    protected ClusterManager<CompanyMarker> mClusterManager;
    protected ArrayList<Company> nearComps=null;
    protected Polyline currentPolyline=null;
    protected Marker selectedMarker=null;
    protected SparseArray<String> sparse=null;
    protected ArrayList<String>  selectedCategories = null;
    int selectedID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (readyToGo()) {
            setContentView(R.layout.map_fragment);

            SupportMapFragment mapFrag=
                    (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

            initListNav();

            initCategorySpinner();

            map=mapFrag.getMap();

            if (savedInstanceState == null) {
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(37.9928920,
                                23.6772921));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                map.moveCamera(center);
                map.animateCamera(zoom);
            }

            //map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
            //map.setOnInfoWindowClickListener(this);

            locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
            crit.setAccuracy(Criteria.ACCURACY_FINE);

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            mClusterManager = new ClusterManager<CompanyMarker>(this, map);
            map.setOnCameraChangeListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            map.setOnInfoWindowClickListener(this);
            mClusterManager.setOnClusterClickListener(this);
            mClusterManager.setOnClusterInfoWindowClickListener(this);
            mClusterManager.setOnClusterItemClickListener(this);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);

            if ( !locMgr.isProviderEnabled( LocationManager.GPS_PROVIDER ) &&
                    !locMgr.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }
        }
        else{
            Log.e("NEARBY", "Error! Something is missing!");
            Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        String provider=null;
        for(String s : locMgr.getProviders(true)){
            Log.d("PROVIDERS",s);
        }

        if(locMgr != null)
        {
            boolean networkIsEnabled = locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean gpsIsEnabled = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean passiveIsEnabled = locMgr.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if(networkIsEnabled)
            {
                locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60*1000L, 10.0F, this, null);
                provider=LocationManager.NETWORK_PROVIDER;
            }
            else if(gpsIsEnabled)
            {
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000L, 10.0F, this, null);
                provider=LocationManager.GPS_PROVIDER;
            }
            else if(passiveIsEnabled)
            {
                locMgr.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 60*1000L, 10.0F, this, null);
                provider=LocationManager.PASSIVE_PROVIDER;
            }
            else
            {
                Toast.makeText(this, "Δεν υπάρχει ενεργοποιημένη υπηρεσία εύρεσης τοποθεσίας.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, "Δεν υπάρχει υπηρεσία εύρεσης τοποθεσίας στη συσκευή σας.", Toast.LENGTH_LONG).show();
        }
		
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			locMgr.requestLocationUpdates(0L, 0.0f, crit, this, null);
		}else{ 
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0L, 0.0f, this, null);
		}
		*/
        map.setLocationSource(this);
        if(provider!=null)
            currentLocation = locMgr.getLastKnownLocation(provider);


        if(currentLocation!=null)map.getUiSettings().setMyLocationButtonEnabled(true);
        //drawCircle();
        //Toast.makeText(this, "Lat: "+currentLocation.getLatitude()+" Long: "+currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

        if(currentLocation!=null){
            new MarkerLoader().execute(currentLocation);
        }
        else Toast.makeText(this, "Η τοποθεσία σας δεν βρέθηκε. Ελέγξτε τις ρυθμίσεις gps.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        map.setMyLocationEnabled(false);
        map.setLocationSource(null);
        locMgr.removeUpdates(this);

        if(alert != null) { alert.dismiss(); }

        super.onPause();
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
        //searchView.setSubmitButtonEnabled(true);
        //SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        //SearchableInfo info = searchManager.getSearchableInfo(getComponentName());

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
    public void onItemsSelected(boolean[] selected) {
        selectedCategories = new ArrayList<String>();
		/* If -all categories- is selected we set selectedCategories null. this value will be used in query building. */
        if(selected[0]){
            selectedCategories=null;
        }
		/* Else we add the ids of selected categories to array list */
        else{
            for(int i=1;i<selected.length;i++){
                if(selected[i]){
                    selectedCategories.add(sparse.get(i));
                }
            }
        }
        //Toast.makeText(this, "Activated: "+activated, Toast.LENGTH_LONG).show();
        mClusterManager.clearItems();
        //new MarkerLoader().execute(currentLocation);
        onResume();
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //map.setMapType(MAP_TYPES[itemPosition]);
        directionMode = DIRECTION_MODES_TYPES[itemPosition];
        return(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(STATE_NAV,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_NAV));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int selectedPos=0;
		/* Finding position of selected company in nearby companies collection*/
        for(int i=0 ; i<nearComps.size() ; i++){
            if(nearComps.get(i).getId()==selectedID){
                selectedPos=i;
                break;
            }
        }

        ((GlobalData)getApplicationContext()).setSelected_companies(nearComps);
        Intent i = new Intent(getApplicationContext(), SingleListItem.class);
        // sending data to new activity
        i.putExtra("pos",selectedPos);
        startActivity(i);

        //Toast.makeText(this, "go there "+marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        this.mapLocationListener=listener;
    }

    @Override
    public void deactivate() {
        this.mapLocationListener=null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOC CHANGED!","");
        if (mapLocationListener != null) {
            mapLocationListener.onLocationChanged(location);

            LatLng latlng=
                    new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cu=CameraUpdateFactory.newLatLng(latlng);

            map.animateCamera(cu);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // unused
    }

    @Override
    public void onProviderEnabled(String provider) {
        // unused
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // unused
    }

    private void initCategorySpinner(){
        DBHandler db = DBHandler.getInstance(this);
        ArrayList<Category> cats = db.getCategories("cat_parent_id is not null");
        ArrayList<String> items=new ArrayList<String>();

        if(sparse==null)
            sparse = new SparseArray<String>();
		
		/* We add an extra item which represents ALL categories */
        items.add("Όλες οι κατηγορίες");
		/* We also put it in first (zero) position in sparse array*/
        sparse.put(0, "0");
		
		/* 
		 * BE CAREFUL! We count from 0 so every category gets added to items 
		 * but we put them in sparse shifted by one because zero position is taken by all categories item.
		 * */
        for (int i=0; i<cats.size(); i++) {
            items.add(cats.get(i).getCat_name());
            sparse.put(i+1,Integer.toString(cats.get(i).getCat_id()));
        }
        MultiSpinner multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
        multiSpinner.setItems(items,"Όλες οι κατηγορίες",this);
    }

    private void initListNav() {
        ArrayList<String> items=new ArrayList<String>();
        ArrayAdapter<String> nav=null;
        ActionBar bar=getSupportActionBar();
		
		/*
		for (int type : MAP_TYPE_NAMES) {
		  items.add(getString(type));
		}*/

        for (String m : DIRECTION_MODE_NAMES) {
            items.add(m);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            nav=
                    new ArrayAdapter<String>(
                            bar.getThemedContext(),
                            android.R.layout.simple_spinner_item,
                            items);
        }
        else {
            nav=
                    new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_spinner_item,
                            items);
        }

        nav.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        bar.setListNavigationCallbacks(nav, this);
    }

    @SuppressWarnings("unused")
    private void addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(getString(title))
                .snippet(getString(snippet)));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ενεργοποίηση GPS")
                .setMessage("Το GPS της συσκευής σας φαίνεται να είναι απενεργοποιημένο. Θέλετε να το ενεργοποιήσετε?")
                .setCancelable(false)
                .setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Όχι", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @SuppressLint("NewApi")
    private void drawCircle(){
        final Circle circle = map.addCircle(new CircleOptions()
                .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .strokeColor(Color.BLUE).radius(100));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator vAnimator = new ValueAnimator();
            vAnimator.setRepeatCount(0);
            vAnimator.setRepeatMode(ValueAnimator.RESTART);  /* PULSE */
            vAnimator.setIntValues(0, 100);
            vAnimator.setDuration(1000);
            vAnimator.setEvaluator(new IntEvaluator());
            vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    // Log.e("", "" + animatedFraction);
                    circle.setRadius(animatedFraction * 100);
                }
            });
            vAnimator.start();
        }
    }

    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point Point of origin
     * @param range Range in meters
     * @param bearing Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    public static PointF calculateDerivedPosition(PointF point, double range, double bearing)
    {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;

    }

    protected class MarkerLoader extends AsyncTask<Location,CompanyMarker,ArrayList<CompanyMarker>>{
        DBHandler db;
        ArrayList<CompanyMarker> cms = new ArrayList<CompanyMarker>();
        double t1,t2;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setSupportProgressBarIndeterminateVisibility(true);
            t1 = System.nanoTime();
        }

        @Override
        protected ArrayList<CompanyMarker> doInBackground(Location... loc) {
            double lat = loc[0].getLatitude();
            double lon = loc[0].getLongitude();
            PointF center = new PointF((float) lat, (float) lon);
            final double mult = 1; // mult = 1.1; is more reliable
            int range = 1*1000; //1km around center
            PointF p1 = calculateDerivedPosition(center, mult * range, 0);
            PointF p2 = calculateDerivedPosition(center, mult * range, 90);
            PointF p3 = calculateDerivedPosition(center, mult * range, 180);
            PointF p4 = calculateDerivedPosition(center, mult * range, 270);

            String whereClause =
                    "co_latitude > " + String.valueOf(p3.x) + " AND "
                            + "co_latitude < " + String.valueOf(p1.x) + " AND "
                            + "co_longitude < " + String.valueOf(p2.y) + " AND "
                            + "co_longitude > " + String.valueOf(p4.y);

            String whereCat = "";
            if(selectedCategories!=null){
                boolean isFirst=true;
                for(String sc : selectedCategories){
                    if(isFirst){
                        whereCat += "( cc.category_id='"+sc+"'";
                        isFirst=false;
                    }
                    else{
                        whereCat += " OR cc.category_id='"+sc+"'";
                    }
                }
                whereCat += " ) AND ";
            }

            whereClause = whereCat + whereClause;
            Log.d("--- Query ---", whereClause);
            //clause to get companies in a 20km radius away from user - NOT WORKING in sqlite
			/*
			String whereClause = "ACOS( "+
					"SIN( RADIANS( `latitude` ) ) * SIN( RADIANS( "+lat+" ) ) + COS( RADIANS( `latitude` ) ) * "+
					"COS( RADIANS( "+lat+" )) * COS( RADIANS( `longitude` ) - RADIANS( "+lon+" )) ) * 6380 < 20";
			*/
            try{
                db = DBHandler.getInstance(getApplicationContext());
                if(selectedCategories!=null){
                    nearComps = db.getCompaniesJoinCCWhere(whereClause);
                }else{
                    nearComps = db.getCompaniesWhere(whereClause);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                db.close();
            }

            //List<Marker> markers = new ArrayList<Marker>();

            if(nearComps!=null && nearComps.size()>0){
                for(Company c : nearComps){
                    CompanyMarker m = new CompanyMarker(c.getId(), c.getName(),
                            c.getAddress(),
                            c.getLatitude(),
                            c.getLongitude());
                    //mClusterManager.addItem(m);
                    //publishProgress(m);
                    cms.add(m);
					/*
					Marker m = map.addMarker(new MarkerOptions()
						.position(new LatLng(c.getLatitude(),c.getLongitude()))
						.title(c.getName())
						.snippet(c.getAddress() +", "+c.getArea()));
					markers.add(m);
					*/
                }
            }
            //Log.d("Num of markers",""+nearComps.size());

            return cms;
        }

        protected void onProgressUpdate(CompanyMarker... companyMarkers){
            //mClusterManager.addItem(companyMarkers[0]);
			/*
			map.addMarker(new MarkerOptions()
				.position(companyMarkers[0].getPosition())
				.title(companyMarkers[0].getTitle())
				.snippet(companyMarkers[0].getSnippet()));
			*/
        }

        protected void onPostExecute(ArrayList<CompanyMarker> cms){
            if(cms!=null && cms.size()>0){
                for(CompanyMarker cm : cms){
                    mClusterManager.addItem(cm);
                }
            }
            setSupportProgressBarIndeterminateVisibility(false);
            mClusterManager.cluster();
            t2 = (System.nanoTime() - t1)/1000000.0;
            //Log.d("---- Marker Loader completed in ----", Double.toString(t2));
        }



    }


    @Override
    public void onClusterItemInfoWindowClick(CompanyMarker item) {

    }

    @Override
    public boolean onClusterItemClick(CompanyMarker item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
        //IconGenerator tc = new IconGenerator(this);
        //Bitmap bmp = tc.makeIcon("hello"); // pass the text you want.
        if(selectedMarker != null)
            selectedMarker.remove();
        selectedMarker = map.addMarker(new MarkerOptions()
                .position(item.getPosition())
                .title(item.getTitle())
                .snippet(item.getSnippet()));
        selectedMarker.showInfoWindow();
        selectedID=item.getId();

        LatLng from = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        LatLng[] fromTo = {from, item.getPosition()};
        new DirectionsLoader().execute(fromTo);

        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<CompanyMarker> cluster) {

    }

    @Override
    public boolean onClusterClick(Cluster<CompanyMarker> cluster) {
        return false;
    }

    protected class DirectionsLoader extends AsyncTask<LatLng,Void,DirectionsInfo>{

        @Override
        protected DirectionsInfo doInBackground(LatLng... params) {
            GMapV2Direction md = new GMapV2Direction();

            Document doc = md.getDocument(params[0], params[1], directionMode);
            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

            for(int i = 0 ; i < directionPoint.size() ; i++) {
                rectLine.add(directionPoint.get(i));
            }

            DirectionsInfo directionsInfo = new DirectionsInfo(
                    null,md.getDurationValue(doc),
                    null,md.getDistanceValue(doc),
                    rectLine);
            return directionsInfo;
        }

        protected void onPostExecute(DirectionsInfo directionsInfo){
            if(currentPolyline!=null){
                currentPolyline.remove();
            }
            currentPolyline = map.addPolyline(directionsInfo.getPolylineOptions());

            double dist = (double)directionsInfo.getDistanceValue() * 1.0/1000.0;
            int durat = (int)Math.round(directionsInfo.getDurationValue() * 1.0/60.0);

            if(selectedMarker!=null){
                selectedMarker.hideInfoWindow();
                String snip = selectedMarker.getSnippet();

                selectedMarker.setSnippet(snip+ ", Απόσταση: "+String.format(Locale.ENGLISH, "%.1f", dist)+" χλμ. Χρόνος Άφιξης: "+
                        durat+" λεπτα");
                selectedMarker.showInfoWindow();
            }
        }

    }

}
