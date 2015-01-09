package org.dklisiaris.downtown;

//import gr.futurearts.greekguide.actionbar.ActionBar;
//import org.dklisiaris.downtown.db.DatabaseHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.helper.ConnectionDetector;
import org.dklisiaris.downtown.helper.ImageLoader;
import org.dklisiaris.downtown.helper.InfoHelper;
import org.dklisiaris.downtown.helper.ShareHelper;
import org.dklisiaris.downtown.helper.Utils;
import org.dklisiaris.downtown.widgets.FlipAnimator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SingleListItem extends ActionBarActivity{
    private Company selected;
    ArrayList<Company> comps;
    private Typeface robo;
    private List<String> img_urls = new ArrayList<String>();
    private ArrayList<String> allImgUrls = new ArrayList<String>();
    protected String[] imgsToDownload = null;
    protected List<String> storedImgs;
    private SparseArray<String> mapHash;
    private int img_counter=0,pos=0,subcatID=0;
    //private Dialog mDialog;
    private ImageButton btnLeft,btnRight;
    private static int swipeMinDistance = 10;
    private static int swipeMaxOffPath = 150;
    private static int swipeThresholdVelocity = 20;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    boolean first_run;
    //DatabaseHandler db;
    DBHandler db;
    boolean isFav=false;
    //private boolean hasImages=false;
    static final int IMG_CHANGE_DELAY = 3500;

    Handler imgChanger;
    private ImageLoader iLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.single_list_item_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iLoader = new ImageLoader(this);
        //final ViewConfiguration vc = ViewConfiguration.get(getApplicationContext());
        //swipeMinDistance = vc.getScaledPagingTouchSlop();
        //swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
        //swipeMaxOffPath = vc.getScaledTouchSlop();
        first_run=true;

        Intent i = getIntent();
        pos = i.getIntExtra("pos",0);
        subcatID = i.getIntExtra("subcatID",0);
        mapHash = ((GlobalData)getApplicationContext()).getCatsMap();
        comps = ((GlobalData)getApplicationContext()).getSelected_companies();
        //Log.d("Position Selected / data size",Integer.toString(pos)+" / "+ comps.size());
        selected = comps.get(pos);
        db = DBHandler.getInstance(this);
        allImgUrls = db.getImagesById(selected.getId());

        if(allImgUrls!=null && allImgUrls.size()>1){
            Utils util = new Utils(this);
            storedImgs = util.getAssetList("imgs");
            storedImgs.addAll(util.getFilesList());

            ArrayList<String> tempImgsToDownload = new ArrayList<String>();
            for(String img : allImgUrls){
                if(!storedImgs.contains(img)){
                    tempImgsToDownload.add(img);
                }
            }
            imgsToDownload = tempImgsToDownload.toArray(new String[tempImgsToDownload.size()]);
        }

        // Loading Font Face
        robo = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        if(db.isFav(Integer.toString(selected.getId())))isFav=true;

        // Gesture detection
        gestureDetector = new GestureDetector(this, new CustomGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        btnLeft = (ImageButton)findViewById(R.id.btn_left);
        btnRight = (ImageButton)findViewById(R.id.btn_right);
        viewChanger(this.findViewById(android.R.id.content),this,selected);
        
        /*
         * Website layout-button. It opens a Web View with company's website.
         */
        RelativeLayout siteL = (RelativeLayout) findViewById(R.id.site_layout);
        siteL.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                if (selected.getWebsite()!=null
                        && (selected.getWebsite().startsWith("http") || selected.getWebsite().startsWith("www"))
                        && cd.isConnectingToInternet() ){
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                    intent.putExtra("website",selected.getWebsite());
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),"Μη διαθέσιμη ιστοσελίδα", Toast.LENGTH_SHORT).show();
            }
        });
        
        /*
         * Address layout-button. It opens a Map Fragment with address information and markers.
         */
        RelativeLayout addrL = (RelativeLayout) findViewById(R.id.addr_layout);
        addrL.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                //if (selected.getLatitude()>0 && selected.getLongitude()>0 && cd.isConnectingToInternet() ){
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("lat",selected.getLatitude());
                intent.putExtra("lon",selected.getLongitude());
                intent.putExtra("name",selected.getName());
                intent.putExtra("address",selected.getAddress());
                intent.putExtra("area",selected.getArea());
                intent.putExtra("county",selected.getCounty());
                intent.putExtra("tk",selected.getTk());
                if(selected.getTels().get(0)!=null)
                    intent.putExtra("tel",selected.getTels().get(0));
                else intent.putExtra("tel","");

                startActivity(intent);
                //}
                //else if(cd.isConnectingToInternet()) Toast.makeText(getApplicationContext(),"Μη διαθέσιμος χάρτης", Toast.LENGTH_SHORT).show();
                //else Toast.makeText(getApplicationContext(),"Δεν υπάρχει σύνδεση internet", Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * Image layout-button. When image pressed next image is shown.
         */
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.infos);
        layout.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNextImage(true);
            }
        });
        layout.setOnTouchListener(gestureListener);
        
        /*
         * Left image button. Shows previous image.
         */
        ImageButton left_btn = (ImageButton) findViewById(R.id.left_arrow);
        left_btn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNextImage(false);
                stopRepeatingTask();
                first_run=true;
                startRepeatingTask();
            }
        });
        
        /*
         * Right image button. Shows next image.
         */
        ImageButton right_btn = (ImageButton) findViewById(R.id.right_arrow);
        right_btn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNextImage(true);
                stopRepeatingTask();
                first_run=true;
                startRepeatingTask();
            }
        });
        
        /*
         * Description layout-button. Opens a dialog with company description.
         */
        RelativeLayout descL = (RelativeLayout) findViewById(R.id.desc_layout);
        descL.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog("desc");
            }
        });
        
        /*
         * Telephones layout-button. Opens a dialog with company phones.
         */
        RelativeLayout telL = (RelativeLayout) findViewById(R.id.tel_layout);
        telL.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog("tels");
            }
        });

        final RelativeLayout moreL = (RelativeLayout) findViewById(R.id.more_imgs_layout);
        if(selected.getFirst_img()!=null && allImgUrls.size()>1 && imgsToDownload!=null && imgsToDownload.length>=1){
            moreL.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //allImgUrls.remove(0);
                    //img_urls.addAll(allImgUrls);
                    ImageDownloader imgDl = new ImageDownloader(getApplicationContext());
                    imgDl.execute(imgsToDownload);
                    moreL.setEnabled(false);
                    //moreL.setVisibility(View.GONE);
                    //findViewById(R.id.horizon_line_4).setVisibility(View.GONE);
                }
            });
        }
        else{
            findViewById(R.id.horizon_line_4).setVisibility(View.GONE);
            moreL.setVisibility(View.GONE);
        }
        
        /*
         * Left button. Shows previous company.
         */
        btnLeft.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur=pos;
                if(cur>0)cur--;
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                i.putExtra("pos",cur);
                startActivity(i);
                SingleListItem.this.finish();
            }
        });
        
        /*
         * Right button. Shows next company.
         */
        btnRight.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size=comps.size();
                int cur=pos;
                if(cur<size-1)cur++;
                Intent i = new Intent(getApplicationContext(), SingleListItem.class);
                i.putExtra("pos",cur);
                startActivity(i);
                SingleListItem.this.finish();
            }
        });

        imgChanger = new Handler();
    }

    /**
     * Creates a dialog with more information about company.
     * @param mode Two modes {desc,tels} for description and telephones respectively.
     */
    public void makeDialog(String mode){
        if(mode.equals("desc"))
            new InfoHelper(this).show(selected.getDescription());
        else if(mode.equals("tels"))
            new InfoHelper(this).show(selected.getTels(),selected.getFax());
    }

    @Override public void onStop(){super.onStop(); stopRepeatingTask();}
    @Override public void onResume(){super.onResume(); startRepeatingTask();  }

    /*
     * Background thread which changes the image every N seconds.
     */
    Runnable m_statusChecker = new Runnable()
    {
        @Override
        public void run() {
            if(!first_run)showNextImage(true);
            else first_run=false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (isFav)
            menu.findItem(R.id.favourites).setIcon(R.drawable.ic_rating_important);
        else
            menu.findItem(R.id.favourites).setIcon(R.drawable.ic_rating_not_important);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favourites:
                saveFavourite(selected);
                return true;
            case R.id.share_btn:
                createShareIntent();
                return true;
            case R.id.toMainHome:
                Intent upIntent = new Intent(this,MainActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upIntent);
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

    /**
     * Instead of an adapter, here we use this method to change
     * evething we want in view
     * @param vi The view
     * @param a The activity
     * @param pr The company
     */
    public void viewChanger(View vi, Activity a, Company pr){
        TextView name = (TextView)vi.findViewById(R.id.name);
        TextView subcat = (TextView)vi.findViewById(R.id.subcateg);
        TextView categ = (TextView)vi.findViewById(R.id.categ);
        ImageView img1 = (ImageView)vi.findViewById(R.id.imageView1);

        // Setting all values in listview
        name.setText(pr.getName());
        name.setTypeface(robo);

        if(subcatID!=0)subcat.setText(mapHash.get(subcatID));
        else subcat.setText(mapHash.get(Integer.parseInt(pr.getSubcategories().get(0))));

        categ.setText(mapHash.get(pr.getCategory()));

        int size=comps.size();
        int cur=pos;
        if(cur==size-1){
            btnRight.setEnabled(false);
            btnRight.setVisibility(View.INVISIBLE);
        }
        if(cur==0){
            btnLeft.setEnabled(false);
            btnLeft.setVisibility(View.INVISIBLE);
        }

        Drawable d=null;
        try{
            if(pr.getFirst_img()!=null){
                // load image into drawable
                d = iLoader.loadImage(pr.getFirst_img());
                img_urls.add(pr.getFirst_img());
                if(allImgUrls!=null && allImgUrls.size()>1){
                    for(String img : allImgUrls){
                        if(!img.equals(pr.getFirst_img()) && storedImgs.contains(img)){
                            img_urls.add(img);
                        }
                    }
                }

            }
        }catch(Exception e){e.printStackTrace();}

        if(d==null){
            d = iLoader.loadImage("Blankside.jpg");
            img_urls.add("Blankside.jpg");
        }
        img1.setImageDrawable(d);

    }

    /**
     * Saves a company into favourites
     * @param p The company to save
     */
    @SuppressLint("NewApi")
    public void saveFavourite(Company p){
        if(!isFav){
            db.setFav(Integer.toString(selected.getId()));
            Toast.makeText(getApplicationContext(), "Αποθηκεύτηκε στα αγαπημένα!", Toast.LENGTH_SHORT).show();
        }
        else{
            db.removeFav(Integer.toString(selected.getId()));
            Toast.makeText(getApplicationContext(), "Αφαιρέθηκε από τα αγαπημένα...", Toast.LENGTH_SHORT).show();
        }
        //toggle isFav bool
        isFav=!isFav;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            invalidateOptionsMenu();
        }
        else{
            supportInvalidateOptionsMenu();
        }
    }

    /**
     * Changes images. Based on the images array, makes the imageview 
     * to change its image source.
     * @param next True if we want next image, false if previous
     */
    protected void showNextImage(boolean next){
        ImageView img = (ImageView) findViewById(R.id.imageView1);
        ImageView img2 = (ImageView) findViewById(R.id.imageView2);
        ImageView target,current;

        if (img.getVisibility() == View.GONE){
            target=img;
            current = img2;
        }
        else{
            target = img2;
            current = img;
        }

        String imgName;
        if(!next)imgName=getPreviousImgUrl();
        else imgName=getNextImgUrl();

        Drawable d=null;
        try{
            if(imgName!=null){
                // load image into drawable
                d = iLoader.loadImage(imgName);

            }
        }catch(Exception e){e.printStackTrace();}
        
        /*
         * If image is stored and loaded set it, else load it from net 
         */
        if(d!=null)target.setImageDrawable(d);
        /*
        else {
            //Picasso.with(this)        
            //.load(DBHandler.URL_IMAGES+imgName)            
        	//.into(target);
        	d = iLoader.loadImage("Blankside.jpg");
        	target.setImageDrawable(d);
        }*/

        /*
         * Animate the image change flip effect
         */
        FlipAnimator animator;
        if(!next){
            animator= new FlipAnimator(target, current, current.getWidth() / 2, current.getHeight() / 2);
            animator.reverse();
        }
        else animator= new FlipAnimator(current, target, current.getWidth() / 2, current.getHeight() / 2);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.infos);
        layout.startAnimation(animator);
    }


    /**
     * Starts a share dialog and converts text into acceptable form to be shared in
     * different services such as facebook(only link), twitter(136 chars max) etc.
     */
    private void createShareIntent() {
        String name,address,tel,site;
        name=selected.getName();
        address=selected.getAddress();
        //desc=selected.getDescription();
        tel=selected.getTels().get(0);
        if (selected.getWebsite() != null) site = selected.getWebsite();
        else site = " - ";

        String share_text = "<b>Επωνυμία: </b>"+name+
                "<br/><b>Διεύθυνση: </b>"+address+
                "<br/><b>Τηλέφωνο: </b>"+tel+
                "<br/><b>Ιστοσελίδα: </b>"+site+
                "<br/><br/>Sent from HellasGuide App";
        String plain_text = "Επωνυμία: "+name+
                "Διεύθυνση: "+address+
                "Τηλέφωνο: "+tel+
                "Ιστοσελίδα: "+site+
                "Sent from HellasGuide App";

        String twi_text = "Επωνυμία: "+name+
                "Διεύθυνση: "+address+
                "Τηλέφωνο: "+tel+
                "Ιστοσελίδα: "+site+
                "Sent from HellasGuide App";
        //Spanned text = Html.fromHtml(share_text);
        String twitter;
        if(twi_text.length()>138)
            twitter = twi_text.substring(0,136)+"...";
        else twitter=twi_text;
        String fcb = site;

        new ShareHelper(this, name, plain_text, Html.fromHtml(share_text), twitter, fcb).share();

    }

    /**
     * Makes a telephone call
     * @param phone The number to call
     */
    public void call(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phone.replaceAll("\\s","")));
        startActivity(callIntent);
    }

    /**
     * Gets the name of next image to show.
     * @return String name of next image
     */
    public String getNextImgUrl(){
        img_counter = (img_counter+1) % img_urls.size();
        return img_urls.get(img_counter);
    }

    /**
     * Gets the name of previous image to show.
     * @return String name of previous image
     */
    public String getPreviousImgUrl(){
        img_counter = (img_counter - 1 + img_urls.size()) % img_urls.size();
        return img_urls.get(img_counter);
    }

    /**
     * Handles touch gestures in order to make gesture based image navigation 
     * @author meec
     */
    class CustomGestureDetector extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > swipeMaxOffPath)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
                    //Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show(); 
                    showNextImage(false);
                    stopRepeatingTask();
                    first_run=true;
                    startRepeatingTask();
                }  else if (e2.getX() - e1.getX() >swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
                    //Toast.makeText(getApplicationContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                    showNextImage(true);
                    stopRepeatingTask();
                    first_run=true;
                    startRepeatingTask();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

    }

    protected class ImageDownloader extends AsyncTask<String, Void, ArrayList<String>>{
        Context context;

        public ImageDownloader(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setSupportProgressBarIndeterminateVisibility(true);

        }

        @Override
        protected ArrayList<String> doInBackground(String... aurl) {
            int tryCounter=0;
            ArrayList<String> downloaded = new ArrayList<String>();

            for (String f : aurl){
                //String fname = f.substring( f.lastIndexOf('/')+1, f.length() );
                if(storedImgs.contains(f)){
                    downloaded.add(f);
                }
                else{
                    //Log.d("DOWNLOADING",f);
                    try{
                        downloadImages(DBHandler.URL_IMAGES+f);
                        downloaded.add(f);

                    }
                    catch (Exception e) {
                        tryCounter++;
                    }
                    finally{
                        if(tryCounter>=2)break;
                    }
                }
            }
            return downloaded;
        }

        @Override
        protected void onPostExecute(ArrayList<String> downloaded) {
            setSupportProgressBarIndeterminateVisibility(false);
            iLoader = new ImageLoader(context);
            img_urls.addAll(downloaded);
        }

        /**
         * Downloads and saves an image in internal storage
         * @param urlf The full url of image we want to download
         */
        protected void downloadImages(String urlf){
            int count;
            int connectTimeout = 10000;
            int readTimeout = 3000;

            try {
                URL url = new URL(urlf);
                String f_name = urlf.substring( urlf.lastIndexOf('/')+1, urlf.length() );

                URLConnection conection = url.openConnection();
                conection.setConnectTimeout(connectTimeout);
                conection.setReadTimeout(readTimeout);
                conection.connect();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File appDir = context.getFilesDir();
                File filename = new File(appDir, f_name);
                FileOutputStream fos = new FileOutputStream(filename);

                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    //total += count;
                    // writing data to file
                    fos.write(data, 0, count );
                }
                // flushing output
                fos.flush();

                // closing streams
                input.close();
                fos.close();

            } catch (Exception e) {
                //Log.e("Download Error: ", e.getMessage());
                e.printStackTrace();
                throw new IllegalArgumentException("Error downloading file...");
            }
        }

    }
}
