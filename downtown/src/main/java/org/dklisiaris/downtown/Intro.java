package org.dklisiaris.downtown;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.dklisiaris.downtown.R;
import org.dklisiaris.downtown.db.Banner;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.db.Image;
import org.dklisiaris.downtown.db.InitData;
import org.dklisiaris.downtown.db.Keyword;
import org.dklisiaris.downtown.db.Mapping;
import org.dklisiaris.downtown.downloader.DownloadTask;
import org.dklisiaris.downtown.helper.ConnectionDetector;
import org.dklisiaris.downtown.helper.XMLParser;
import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
//import gr.futurearts.greekguide.actionbar.ActionBar.Action;

public class Intro extends ActionBarActivity{
	DBHandler db;
	// Progress Dialog
    private ProgressDialog pDialog;
    NotificationManager mNotificationManager = null;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0; 
    String update;
	long start,start2;
	long elapsedTime,elapsedTime2;
	ArrayList<String> imgs2download=null;
     
	//File url to download
	//Depricated - this website does not exist anymore
    private String[] debug_urls = {
		"http://the4seasonstravel.gr/downtown/xml/update/",
		"http://the4seasonstravel.gr/downtown/xml/categories/",
		"http://the4seasonstravel.gr/downtown/xml/companies/",
		"http://the4seasonstravel.gr/downtown/xml/keywords/",
		"http://the4seasonstravel.gr/downtown/xml/images/",
		"http://the4seasonstravel.gr/downtown/images/",
		"http://the4seasonstravel.gr/downtown/images/banners/"
    };

    boolean control = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);        
        this.setContentView(R.layout.intro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setSupportProgressBarIndeterminateVisibility(true);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        new InitializeApp().execute();

    }

    private class InitializeApp extends AsyncTask<String, Void, String> {
    	Boolean isInternetPresent;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();	                  
        }
    	
		@Override
		protected String doInBackground(String... urls) {			
			db = DBHandler.getInstance(getApplicationContext());
	        update = db.getUpdate();
		                
	        ((GlobalData)getApplicationContext()).setCatsMap(db.getCatHash());
	        
	        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
	        isInternetPresent = cd.isConnectingToInternet(); // true or false
			return null;
		}

		@Override
		protected void onPostExecute(String result) {		
			setSupportProgressBarIndeterminateVisibility(false);
			if(isInternetPresent && control){
				//Toast.makeText(getApplicationContext(),"Online", Toast.LENGTH_SHORT).show();        	
			//new DownloadFileFromURL().execute(debug_urls);        	
				new CheckForUpdates().execute(debug_urls);
			}else{ 
				
			    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
			    startActivity(intent);
			    Intro.this.finish();
			    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);			
			}
		}
	}
    

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case progress_bar_type: // we set this to 0
            pDialog = new ProgressDialog(this);            
            pDialog.setMessage("Έλεγχος για ενημερώσεις...");
            pDialog.setIndeterminate(true);
            //pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
            return pDialog;
        default:
            return null;
        }
    }
          
    /* --- Obsolete ---
    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        if(file.exists()){        	
            return true;
        }
        else{
            return false;
        }    
    }
    
    public String getStrFromInternalStorage(String filename){
        String strText="0";
        if(fileExistance(filename)){
	        try {
	            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
	                    openFileInput(filename)));
	            String inputString;
	            StringBuffer stringBuffer = new StringBuffer();                
	            while ((inputString = inputReader.readLine()) != null) {
	                stringBuffer.append(inputString + "\n");
	            }
	            strText = stringBuffer.toString();
	            //Log.d(filename+" from data",stringBuffer.toString());
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }	         
    	return strText;	    	
    }
    
    public int getCurrentVersion(String version_file){
    	int version=0;
		try {
	        String temp = getStrFromInternalStorage(version_file);
	        //remove non digit characters
	        temp = temp.replaceAll("[^\\d]", "");
	        version = Integer.parseInt(temp);
	        if(version>0)System.out.println(version);
		} catch(NumberFormatException nfe) {
		   System.out.println("Could not parse " + nfe);
		} 
    	return version;
    }
    
    public boolean getLicense(String license_file){
    	boolean license=false;
		try {
	        String temp = getStrFromInternalStorage(license_file);
	        //remove non digit characters
	        temp = temp.replaceAll("[^a-zA-Z]", "");
	        
	        if(temp.equals("true")) license = true;
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return license;
    }
    */
	public void showUpdateDialog(){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Ενημερώσεις");
        myAlertDialog.setMessage("Βρέθηκαν νέες καταχωρήσεις. Θέλετε να γίνει ενημέρωση;");
        
        myAlertDialog.setNegativeButton("Ενημέρωση Αργότερα", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface arg0, int arg1) {
				 Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				 startActivity(intent);
				 Intro.this.finish();
				 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			 }});
        
        myAlertDialog.setPositiveButton("Ενημέρωση Τώρα", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	new DownloadFileFromURL().execute(debug_urls);
            }});
        myAlertDialog.show();
	}
    
    private class CheckForUpdates extends AsyncTask<String, Void, String> {
    	boolean shouldUpdate;
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();	      
            setSupportProgressBarIndeterminateVisibility(true);
        }
    	
		@Override
		protected String doInBackground(String... urls) {			
			XMLParser xmlu = new XMLParser();
			String xml_update = xmlu.getXmlFromUrl(urls[0]+update);	
			if(xml_update!=null){
				Document doc_update = xmlu.getDomElement(xml_update);
				shouldUpdate = xmlu.getUpdate(doc_update);	
				imgs2download = db.getAllFirstImages();
				imgs2download.addAll(db.getBanners());
		        Log.d("--- #of Imgs 2 dl ---",Integer.toString(imgs2download.size()));
			}
			else{
				shouldUpdate=false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {		
			setSupportProgressBarIndeterminateVisibility(false);
			if(shouldUpdate || imgs2download!=null && imgs2download.size()>0)
				showUpdateDialog();
			else {
				Intent intent = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent);
				Intro.this.finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		}
	}
    
 
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
 
    	private int totalFilesLength=0;
    	private long total = 0;
    	private String[] imgsToDownload;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();	      
            setSupportProgressBarIndeterminateVisibility(true);
        }
 
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            try {            	 
            	publishProgress("update"); 
            	XMLParser xmlp = new XMLParser();
            	
            	publishProgress("msg","Ενημερώση Κατηγοριών");
            	//Getting Xmls and creating DOMs.
            	Document xml_cat = xmlp.getDomElement(xmlp.getXmlFromUrl(f_url[1]+update));
            	publishProgress("msg","Ενημερώση Καταχωρήσεων");
            	Document xml_comp = xmlp.getDomElement(xmlp.getXmlFromUrl(f_url[2]+update));
            	publishProgress("msg","Ενημερώση Keywords");
            	Document xml_keywords = xmlp.getDomElement(xmlp.getXmlFromUrl(f_url[3]+update));
            	publishProgress("msg","Ενημερώση Εικόνων");
            	Document xml_images = xmlp.getDomElement(xmlp.getXmlFromUrl(f_url[4]+update));
            	
            	publishProgress("msg","Επεξεργασία Νέων Δεδομένων");
            	InitData ini = xmlp.getInitData(xml_cat);
            	
            	ArrayList<Category> allCats = xmlp.getAllCategories(xml_cat);
            	ArrayList<String> catDels = xmlp.getDeleted(xml_cat);
            	
            	//ArrayList<Category> cat = new XMLParser().getAllCategories(xml_cat);
            	ArrayList<Company> com = xmlp.getAllCompanies(xml_comp);
            	ArrayList<String> dels = xmlp.getDeleted(xml_comp);
            	
            	ArrayList<Mapping> cocats = xmlp.parseCoCatMaps(xml_comp, false);
            	ArrayList<Mapping> del_cocats = xmlp.parseCoCatMaps(xml_comp, true);
            	      	
            	ArrayList<Keyword> keywords = xmlp.parseKeywords(xml_keywords);
            	
            	ArrayList<Mapping> cokeys = xmlp.parseCoKeyMaps(xml_keywords, false);            	            	
            	ArrayList<Mapping> del_cokeys = xmlp.parseCoKeyMaps(xml_keywords, true);
            	ArrayList<Mapping> catkeys = xmlp.parseCatKeyMaps(xml_keywords, false);
            	ArrayList<Mapping> del_catkeys = xmlp.parseCatKeyMaps(xml_keywords, true);
            	
            	ArrayList<Integer> del_keywords = xmlp.parseDeletedKeywords(xml_keywords);
            	
            	ArrayList<Image> imgs = xmlp.parseImages(xml_images, false);            	
            	ArrayList<Image> del_imgs = xmlp.parseImages(xml_images, true);
            	
            	ArrayList<Banner> banners = xmlp.parseBanners(xml_images, false, false);            	
            	ArrayList<Banner> del_banners = xmlp.parseBanners(xml_images, false, true);  
            	banners.addAll(xmlp.parseStartBanners(xml_images, false));
            	del_banners.addAll(xmlp.parseStartBanners(xml_images, true));
            	
            	ArrayList<Banner> icons = xmlp.parseBanners(xml_images, true, false);            	
            	ArrayList<Banner> del_icons = xmlp.parseBanners(xml_images, true, true);            	
            	//ArrayList<Banner> sBanners = xmlp.parseStartBanners(xml_images, false);
            	//ArrayList<Banner> del_sBanners = xmlp.parseStartBanners(xml_images, true);

            	boolean isNewBlank = xmlp.getBlankImg(xml_images);
            	
            	publishProgress("msg","Κατεβασμα Εικόνων");
            	
        		ArrayList<String> image_urls = new ArrayList<String>();
        		ArrayList<String> icon_urls = new ArrayList<String>();
        		/*
        		for (Company p : com){
        			if(p.getImage_url()!=null){
        				for(String i : p.getImage_url()){
        					if (!image_urls.contains(i)){
        						image_urls.add(i);
        						//Log.d("img", i);
        					}
        				}        				
        			}
        		}*/
        		for (Image i : imgs){
        			String url = f_url[5]+i.getName();
        			if (!image_urls.contains(url)){
						image_urls.add(url);						
					}
        		}
        		for (Banner i : banners){
        			String url = f_url[6]+i.getName();
        			if (!image_urls.contains(url)){
						image_urls.add(url);						
					}
        		}
        		if(imgs2download!=null)image_urls.addAll(imgs2download);        		
        		
        		for (Banner i : icons){
        			String url = f_url[6]+i.getName();
        			if (!icon_urls.contains(url)){
						icon_urls.add(url);						
					}
        		}
        		publishProgress("calculate");
        		
        		//ArrayList<String> head_imgs=ini.getInit_images();
        		
        		/*
        		if(head_imgs.size()>0){
        			image_urls.addAll(head_imgs);
        			image_urls.add("Blankside.jpg");
        		}
        		if(head_imgs.size()>0){
        			db.setIniImgSeq(head_imgs);
        		}
        		*/
        		if(isNewBlank)icon_urls.add(f_url[6]+"Blankside.jpg");
        		totalFilesLength += (image_urls.size()+icon_urls.size());
        		
   
        		publishProgress("download",Integer.toString(totalFilesLength));
        		
        		        		
        		String updated = ini.getLastUpdate();              		
        		try{
        			db.updateOrReplaceCategory(allCats);
        			db.updateOrReplaceCompany(com);
        			db.deleteCategories(catDels);
        			db.addCompanyCategoryMaps(cocats);
        			db.deleteCompanyCategoryMaps(del_cocats);
        			db.deleteCompanies(dels);
        			db.addKeywords(keywords);
        			db.addCategoryKeywordMaps(catkeys);
        			db.addCompanyKeywordMaps(cokeys);
        			db.deleteCategoryKeywordMaps(del_catkeys);
        			db.deleteCompanyKeywordMaps(del_cokeys);
        			db.removeKeywords(del_keywords);
        			db.addImages(imgs);
        			db.removeImages(del_imgs);
        			db.addBanners(banners);
        			db.removeBanners(del_banners);
        			db.addIcons(icons);
        			db.removeIcons(del_icons);
        			
        			db.setUpdate(updated);
        		}catch (Exception e) {
                	Log.e("Update Error...","Something went Wrong");
                }finally{
                	db.close();                	
                }
        		       
        		/*
        		Utils util = new Utils(getApplicationContext());
            	List<String> storedImgs = util.getAssetList("imgs");
            	storedImgs.addAll(util.getFilesList());
        		for (String f : image_urls){
        			String fname = f.substring( f.lastIndexOf('/')+1, f.length() );        			
        			//storedImgs.contains(fname)
        			if(storedImgs.contains(fname)){        				
        				total++;
        				publishProgress(""+(int)(total));
        			}
        			else{
        				download(f);
        			}        			
        			//download(f,"images");
        		}
        		*/
        		for(String f : icon_urls){
        			download(f);
        		}
        		
        		imgsToDownload = image_urls.toArray(new String[image_urls.size()]);
        		

            } catch (Exception e) {
            	
            }
 
            return null;
        }
 
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
        	if(progress[0].equals("update")){
        		showDialog(progress_bar_type);
        		pDialog.setMessage("Έλεγχος για ενημερώσεις...");
    		}
        	else if(progress[0].equals("calculate")){
        		pDialog.setMessage("Υπολογισμός Μεγέθους Αρχείων...");
    		}
        	else if(progress[0].equals("msg")){
        		pDialog.setMessage(progress[1]);
        	}
        	else if(progress[0].equals("download")){
        		//change mode to determinate
        		pDialog.setMax(Integer.parseInt(progress[1]));
        		pDialog.setMessage("Αποθήκευση νέων δεδομένων...");
    			pDialog.setIndeterminate(false);
    			pDialog.setProgress(0);
    		}
        	else{
        		// setting progress percentage
        		if(pDialog!=null && pDialog.isShowing()){pDialog.incrementProgressBy(1);}
            }
       }
 
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @SuppressLint("NewApi")
		@Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
        	((GlobalData)getApplicationContext()).setCatsMap(db.getCatHash());
        	setSupportProgressBarIndeterminateVisibility(false);
        	if(pDialog!=null && pDialog.isShowing()){dismissDialog(progress_bar_type);}
        	elapsedTime = System.nanoTime() - start;
            double seconds = (double)elapsedTime / 1000000000.0;
            Log.d("Intro completed in:",Double.toString(seconds));  
            
            mNotificationManager.cancel(1);
            DownloadTask dt = new DownloadTask(getApplicationContext());
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
        		dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,imgsToDownload);
        	}
        	else {
        		dt.execute(imgsToDownload);
        	}
    		//new DownloadTask(getApplicationContext()).execute(imgsToDownload);
            
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            Intro.this.finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
   
        }
        
        /**
         * Downloads and saves an image in internal storage
         * It is NOT used since background notification serviced image downloads was introduced
         * @param urlf The url of image to be downloaded
         */
        protected void download(String urlf){
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
                File appDir = getBaseContext().getFilesDir();             
                File filename = new File(appDir, f_name);               
                FileOutputStream fos = new FileOutputStream(filename);
                
                byte data[] = new byte[1024];                
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // writing data to file
                    fos.write(data, 0, count );
                }
                publishProgress(""+(int)(total));
                // flushing output	
                fos.flush();
 
                // closing streams
                input.close();
                fos.close();
 
            } catch (Exception e) {
                //Log.e("Download Error: ", e.getMessage());
                e.printStackTrace();
            }
        }
               
    }
}
