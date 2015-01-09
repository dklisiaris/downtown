package org.dklisiaris.downtown.downloader;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.dklisiaris.downtown.helper.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, Void>{
    private NotificationHelper mNotificationHelper;

    //private static final String PEFERENCE_FILE = "preference";
    //private static final String ISDOWNLOADED = "isdownloaded";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;
    int total = 0, numImgs=0;
    
    public DownloadTask(Context context){
        this.context = context;
      //Create the notification object from NotificationHelper class
        mNotificationHelper = new NotificationHelper(context);
    }

    protected void onPreExecute(){
        //Create the notification in the statusbar
        mNotificationHelper.createNotification();
    }

    @Override
    protected Void doInBackground(String... aurl) {
        //This is where we would do the actual download stuff
        //for now I'm just going to loop for 10 seconds
        // publishing progress every second
    	int tryCounter=0;
    	Utils util = new Utils(context);
    	List<String> storedImgs = util.getAssetList("imgs");
    	storedImgs.addAll(util.getFilesList());
    	numImgs = aurl.length;
    	Log.d("--- Imgs 2 DL ---",""+(int)numImgs);
		for (String f : aurl){
			String fname = f.substring( f.lastIndexOf('/')+1, f.length() );        			
			//storedImgs.contains(fname)
			if(storedImgs.contains(fname)){        				
				total++;
				publishProgress((total*100)/numImgs);
			}
			else{
				//Log.d("DOWNLOADING",f);
				try{
					downloadImages(f);
				}
				catch (Exception e) {
					tryCounter++;            
		        }
				finally{
					if(tryCounter>=3)break;
				}
			}        			
			//Log.d("%Percentage%",""+(int)((total*100)/numImgs));
			//download(f,"images");
		}
    	
    	/*
        int count;

        try {


        URL url = new URL(aurl[0]);
        URLConnection conexion = url.openConnection();
        conexion.connect();
        
        File root = android.os.Environment.getExternalStorageDirectory();

        int lenghtOfFile = conexion.getContentLength();
        Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

        InputStream input = new BufferedInputStream(url.openStream());
        //OutputStream output = new FileOutputStream("/sdcard/foldername/temp.zip");
        String a = root.getAbsolutePath() + "/Pictures/" + aurl[0].substring(aurl[0].lastIndexOf('/')+1);
        OutputStream output = new FileOutputStream(a);
        byte data[] = new byte[2048];

        long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                //publishProgress(""+(int)((total*100)/lenghtOfFile));
                Log.d("%Percentage%",""+(int)((total*100)/lenghtOfFile));
                onProgressUpdate((int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
    	*/

        return null;
    }
    protected void onProgressUpdate(Integer... progress) {
        //This method runs on the UI thread, it receives progress updates
        //from the background thread and publishes them to the status bar
        mNotificationHelper.progressUpdate(progress[0]);
    }
    protected void onPostExecute(Void result)    {
        //The task is complete, tell the status bar about it
        mNotificationHelper.completed();
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
            total++;
            publishProgress((total*100)/numImgs);
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
