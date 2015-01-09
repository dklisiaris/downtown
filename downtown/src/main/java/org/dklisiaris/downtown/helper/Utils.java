package org.dklisiaris.downtown.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;

public class Utils {
	private static final int BUFFER_SIZE=16384;
	Context mCt;
	AssetManager am;
	List<String> mapList = null;
	
	public Utils(Context ct){ mCt=ct; }

	/**
	 * Gets content of a file in internal storage
	 * @param filename The name of the file
	 * @return String - content of filename
	 */
    public String getStrFromInternalStorage(String filename){
        String strText="0";
        if(fileExistance(filename)){
	        try {
	            BufferedReader inputReader = new BufferedReader(new InputStreamReader(new ContextWrapper(mCt).openFileInput(filename)));
	            String inputString;
	            StringBuffer stringBuffer = new StringBuffer();                
	            while ((inputString = inputReader.readLine()) != null) {
	                stringBuffer.append(inputString + "\n");
	            }
	            strText = stringBuffer.toString();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }	         
    	return strText;	    	
    }

	/**
	 * Checks if file existist in internal storage
	 * @param fname The filename
	 * @return boolean true if file exists in storage, false otherwise
	 */
	public boolean fileExistance(String fname){
	    File file = mCt.getFileStreamPath(fname);
	    if(file.exists()){
	        return true;
	    }
	    else{
	        return false;
	    }   
	}
	
	/**
	 * Gets a list of file names in internal storage
	 * @return A List<String> with file names 
	 */
	public List<String> getFilesList(){
		List<String> fnames = new ArrayList<String>();
		for (String f : mCt.fileList()){
		   fnames.add(f);       
		}
		return fnames;
	}
	
	/**
	 * Gets a list of file names in some asset folder
	 * @param folderName The name of the folder to check for assets
	 * @return A List<String> with file names 
	 */
	public List<String> getAssetList(String folderName){
		if (mapList == null) {
			mapList = new ArrayList<String>();
	        am = mCt.getAssets();
	        try {
	            mapList.addAll(Arrays.asList(am.list(folderName)));
	        } catch (IOException e) {
	        }
	    }
	    return mapList;
	}
	

	/**
	 * Checks if an asset exists.
	 *
	 * @param assetName
	 * @return boolean - true if there is an asset with that name.
	 */
	public boolean checkIfInAssets(String assetName,String folderName) {
	    if (mapList == null) {
	        am = mCt.getAssets();
	        try {
	            mapList = Arrays.asList(am.list(folderName));
	        } catch (IOException e) {
	        }
	    }
	    return mapList.contains(assetName);
	}	

}