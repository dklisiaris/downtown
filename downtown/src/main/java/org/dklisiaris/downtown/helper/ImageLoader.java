package org.dklisiaris.downtown.helper;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Class for image loading
 * @author meec 
 */
public class ImageLoader {
	Context ctx;
	List<String> assetImgs;
	List<String> internalImgs;
	
	public ImageLoader(Context ctx) {
		super();
		this.ctx = ctx;
		assetImgs = new Utils(ctx).getAssetList("imgs");
		internalImgs = new Utils(ctx).getFilesList();
	}
	
	/**
	 * Creates a drawable image from a given filename
	 * Image can be in internal storage or in assets 
	 * @param imgName The filename of image
	 * @return a drawable to use with setImageDrawable
	 */
	public Drawable loadImage(String imgName){
		Drawable d=null;
		if(imgName==null) return null;
		
		if(internalImgs.contains(imgName)) {
			try{	 						
		        File filePath = ctx.getFileStreamPath(imgName);			        
		        //Bitmap myBitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());
		        d = Drawable.createFromPath(filePath.toString());
		     }catch(Exception e){e.printStackTrace();}
		}
		else if(assetImgs.contains(imgName)){
			try {					
		         // get input stream
		         InputStream ims = ctx.getAssets().open("imgs/"+imgName);
		         // load image as Drawable
		         d = Drawable.createFromStream(ims, null);			         
		     }catch(Exception e) {e.printStackTrace();}
		}
		return d;
	}	
	
}
