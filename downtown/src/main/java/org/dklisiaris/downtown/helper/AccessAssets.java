package org.dklisiaris.downtown.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AccessAssets {
	private Context ct;

	public AccessAssets(Context c){ct = c;}

	public String readAssetFile(String filename) {
        //read from assets      
		AssetManager am = ct.getAssets();
		String out="";
		try{
            InputStream is = am.open(filename);

            //Log.d("Success", "Read");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            //Log.d("FileContent",total.toString());
            out=total.toString();
	    }catch (IOException e){
            Log.e("Failed", "Could not load '" + e.getMessage()+ "'!");
	    }
        return out;
    }
}
