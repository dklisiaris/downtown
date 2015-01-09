package org.dklisiaris.downtown.db;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dklisiaris.downtown.helper.Utils;
import org.dklisiaris.downtown.providers.KeywordProvider;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.util.SparseArray;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DBHandler extends SQLiteAssetHelper {    
    private static final int DATABASE_VERSION = 4;
    
    private static DBHandler mInstance = null;

    public static final String URL_IMAGES = "http://the4seasonstravel.gr/downtown/images/";
    public static final String URL_BANNERS = "http://the4seasonstravel.gr/downtown/images/banners/";
    Context ctx;
    
    /* Some strings commonly used in company queries*/
	String select = "SELECT  com.*,img.name,MAX(img.weight)  FROM " + DBInterface.TB_COMPANY+" com ";
	String cat_join = "INNER JOIN "+DBInterface.TB_COCAT+" cc ON com._id=cc.company_id ";
	String img_join = "LEFT JOIN "+DBInterface.TB_IMAGES+" img  ON img.company_id=com._id ";
	String cmk_join = "LEFT JOIN company_keyword_map cmk ON cmk.company_id=com._id ";
	String ctk_join = "LEFT JOIN category_keyword_map ctk ON ctk.category_id=cc.category_id ";
	String key_join = "LEFT JOIN keywords k ON ( k._id=ctk.keyword_id OR k._id=cmk.keyword_id ) ";
	String group_by = "GROUP BY com._id ";
	String order_by = "ORDER BY com.co_level desc, com.co_name asc ";
        
    public static DBHandler getInstance(Context context){
    	// Use the application context, which will ensure that you 
        // don't accidentally leak an Activity's context.
        if (mInstance == null) {
            mInstance = new DBHandler(context.getApplicationContext());
          }
          return mInstance;
    }
    
    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DBHandler(Context context) {
        super(context, DBInterface.DATABASE_NAME, null, DATABASE_VERSION);
        ctx=context;
        setForcedUpgrade(4);
    }
    
    /**
     * Gets All Companies by where clause.
     * @param where The where clause.
     * @return
     */
    public ArrayList<Company> getCompaniesWhere(String where) {		
		return getCompanies(where, null);
    }
    
    /**
     * Gets All Companies by subcatID.
     * @param subcatID The Id of the subcategory in which the target companies belong to.
     * @return
     */
    public ArrayList<Company> getCompaniesBySubcatId(String subcatID) {		
		return getCompanies(null, subcatID);
    }
    
    /**
     * Gets All Companies by subcatID and filters
     * @param subcatID The Id of the subcategory in which the target companies belong to.
     * @param withWebsitesOnly True if we want only companies with websites, false otherwise. 
     * @param areas An ArrayList of type String with the specific areas we want companies to be in or null for all companies. 
     * @return An ArrayList of type Company with the target companies.
     */
    public ArrayList<Company> getCompaniesFiltered(String subcatID, boolean withWebsitesOnly, ArrayList<String> areas) {
    	
    	String where="";
    	if(withWebsitesOnly)where += "AND co_availability=1 " ;
    	if(areas!=null){
    		boolean first=true;
	    	for(String a : areas){
	    		if(first){
	    			where += "AND ( co_area='"+a+"' ";
	    			first=false;
	    		}
	    		else{
	    			where += "OR co_area='"+a+"' ";	    		
	    		}
	    	}
	    	where += ") ";
    	}
    	
		return getCompanies(where, subcatID);
    }
    
    /**
     * Gets Companies based on the provided keyword id.
     * @param keywordId The id of of the selected keyword.
     * @return
     */
    public ArrayList<Company> getCompaniesByKeywordId(String keywordId){
    	String selectQuery = "";
    	String where = "";
		//  Make the Query    	
    	if (keywordId.contains("-")) {
    		String[] ids = keywordId.split("-");
    		where = "WHERE ctk.keyword_id='"+ids[0]+"' AND cmk.keyword_id='"+ids[1]+"' ";
    	} else {
    		where = "WHERE ctk.keyword_id='"+keywordId+"' OR cmk.keyword_id='"+keywordId+"' ";
    	}		
		
		selectQuery = select+
				cat_join+
				img_join+
				cmk_join+
				ctk_join+
				where+
				group_by+
				order_by;
    	
    	return getCompaniesWithRawQuery(selectQuery);
    } 
    
    
    public ArrayList<Company> performSearch(String searchQ, String suspectedCatID){
    	/*
    	String keyCondition="";
    	List<String> matchedKeys = getMatchedKeywordIDs(searchQ);
    	if(matchedKeys != null && matchedKeys.size()>0){
    		for(String k: matchedKeys){
        		keyCondition += "ctk.keyword_id='"+k+"' OR cmk.keyword_id='"+k+"' or ";
        	}
    	}
    	*/
    	String selectQuery = "";
    	String where = "";
    	
    	if(suspectedCatID!=null){
    		 where = "WHERE com.co_name like '%"+searchQ+"%' or "+	    				 
					 "com.co_tel like '%"+searchQ+"%' or "+					   				   
					 "com.co_category ='"+suspectedCatID+"' or "+
					 "cc.category_id='"+suspectedCatID+"' or "+
					 //keyCondition+
					 //"k.keyword like '%"+searchQ+"%' or "+	
					 "com.co_description like '%"+searchQ+"%' ";
    	}
    	else{
    		 where = "WHERE com.co_name like '%"+searchQ+"%' or "+
    				 "com.co_description like '%"+searchQ+"%' or "+
    				 "com.co_tel like '%"+searchQ+"%' or "+
    				 //keyCondition+
    				 //"k.keyword like '%"+searchQ+"%' or "+
    				 "com.co_description like '%"+searchQ+"%' ";
    	}
    	
		selectQuery = select+
				cat_join+
				img_join+
				//cmk_join+
				//ctk_join+
				//key_join+
				where+
				group_by+
				order_by;
    	
		Log.d("SEARCH_QUERY",selectQuery);
    	return getCompaniesWithRawQuery(selectQuery);
    }
    
    /**
     * Gets All Companies by where clause or/and by subcatID.
     * @param where The where statement
     * @param subcatID The id of subCategory
     * @return An arrayList of Companies
     */
	private ArrayList<Company> getCompanies(String where, String subcatID) {		
		//  Make the Query		
		String selectQuery = "";
		if(where!=null && subcatID!=null){
			String clause="WHERE cc.category_id='"+subcatID+"' "+where+" ";
			selectQuery = select+cat_join+img_join+clause+group_by+order_by;
		}
		else if(where!=null && subcatID==null){
			String clause="WHERE "+where+" ";
			selectQuery = select+img_join+clause+group_by+order_by;
		}
		else if(subcatID!=null && where==null){
			String clause="WHERE cc.category_id='"+subcatID+"' ";
			selectQuery = select+cat_join+img_join+clause+group_by+order_by;
		}
		else{
			throw new UnsupportedOperationException();			
		}
		
		return getCompaniesWithRawQuery(selectQuery);
	}
	
	public ArrayList<Company> getCompaniesJoinCCWhere(String where) {
		String selectQuery = "";
		if(where!=null){
			String clause="WHERE "+where+" ";
			selectQuery = select+cat_join+img_join+clause+group_by+order_by;
			return getCompaniesWithRawQuery(selectQuery);
		}
		else{
			throw new UnsupportedOperationException();	
		}

		

	}
	
	/**
	 * Returns an ArrayList of type Company with all companies selected based on the provided raw select query.
	 * This method is the method which actually performs query on database.
	 * @param rawQuery The select clause. It should be complete and carefully prepared.
	 * @return ArrayList of type Company with all companies selected
	 */
	private ArrayList<Company> getCompaniesWithRawQuery(String rawQuery){
		ArrayList<Company> coms = new ArrayList<Company>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(rawQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Company com = new Company();
				com.setId(Integer.parseInt(cursor.getString(0)));
				com.setName(cursor.getString(1));				
				com.setAddress((cursor.getString(2)!=null)? cursor.getString(2) : "-" );
				com.setTels((cursor.getString(3)!=null && !cursor.getString(3).equals(""))? convertStringToArray(cursor.getString(3)) : convertStringToArray("-") );
				com.setDescription((cursor.getString(4)!=null)? cursor.getString(4) : "-" );
				com.setImage_url((cursor.getString(5)!=null && !cursor.getString(5).equals(""))? convertStringToArray(cursor.getString(5)) : convertStringToArray("Blankside.jpg") );
				com.setCategory(Integer.parseInt(cursor.getString(6)));
				com.setSubcategories(convertStringToArray(cursor.getString(7)));
				com.setArea(cursor.getString(8));
				com.setAvail((cursor.getInt(9))==1? 1:0);
				com.setWebsite(cursor.getString(10));
				com.setLatitude(cursor.getDouble(11));
				com.setLongitude(cursor.getDouble(12));
				com.setLevel(Integer.parseInt(cursor.getString(13)));
				com.setCounty((cursor.getString(14)!=null)? cursor.getString(14) : "-" );
				com.setTk((cursor.getString(15)!=null)? cursor.getString(15) : "-" );
				com.setFax((cursor.getString(16)!=null)? cursor.getString(16) : "-" );
				com.setFirst_img(cursor.getString(17));
				
				// Adding com to list
				coms.add(com);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		// return com list
		return coms;
	}
	
	public ArrayList<String> getAllFirstImages(){
		double t1,t2;
		Utils util = new Utils(ctx);
		t1 = System.nanoTime();
		ArrayList<String> imgs = new ArrayList<String>();				
		
		String selectQuery = 
				"SELECT name,company_id,MAX(weight) "+
				"FROM (select name,company_id,weight from images order by name asc) "+
				"group by company_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		List<String> fnames = util.getFilesList();				
		fnames.addAll(util.getAssetList("imgs"));

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String imgName = cursor.getString(0);				
				if(!fnames.contains(imgName) && !imgs.contains(imgName)){					
					imgs.add(URL_IMAGES+imgName);
				}					
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();	
		t2 = (System.nanoTime() - t1)/1000000.0;
		Log.d("---- Query completed in ----", Double.toString(t2));		
		return imgs;
	}
		
	public ArrayList<String> getImagesById(int id){
		ArrayList<String> imgs = new ArrayList<String>();
		String selectQuery = 
				"SELECT name "+
				"FROM images "+
				"WHERE company_id = '"+Integer.toString(id)+"' "+
				"GROUP BY name "+
				"ORDER BY weight desc, name asc";
						
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {											
				imgs.add(cursor.getString(0));									
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();			
		return imgs;
	}
	
	// Updating single Company
	public void updateOrReplaceCompany(ArrayList<Company> companies) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(Company co : companies){
			ContentValues values = new ContentValues();
			values.put(DBInterface.KEY_ID, co.getId());
			values.put(DBInterface.KEY_CO_NAME, co.getName()); // Company Name
			values.put(DBInterface.KEY_ADDRESS, co.getAddress()); // Company Phone
			values.put(DBInterface.KEY_TEL, convertArrayToString(co.getTels()));
			values.put(DBInterface.KEY_DESC, co.getDescription());
			values.put(DBInterface.KEY_IMAGE, convertArrayToString(co.getImage_url()));
			values.put(DBInterface.KEY_CATEGORY, co.getCategory());
			values.put(DBInterface.KEY_SUBCATEGORY, convertArrayToString(co.getSubcategories()));
			values.put(DBInterface.KEY_AREA, co.getArea());
			values.put(DBInterface.KEY_AVAIL, co.getAvail());
			if(co.getWebsite()!=null)
				values.put(DBInterface.KEY_SITE, co.getWebsite());
			else
				values.putNull(DBInterface.KEY_SITE);
			values.put(DBInterface.KEY_LANG, co.getLatitude());
			values.put(DBInterface.KEY_LONG, co.getLongitude());
			values.put(DBInterface.KEY_LEVEL, co.getLevel());
			values.put(DBInterface.KEY_COUNTY, co.getCounty());
			values.put(DBInterface.KEY_TK, co.getTk());
			values.put(DBInterface.KEY_FAX, co.getFax());
	
			// updating row
			db.replace(DBInterface.TB_COMPANY,null, values);
		}
		db.close();
	}

	// Deleting companies
	public void deleteCompanies(ArrayList<String> deletedIDs) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(String del : deletedIDs){
			db.delete(DBInterface.TB_COMPANY, DBInterface.KEY_ID + " = ?", new String[] { del });	
			db.delete(DBInterface.TB_COCAT, DBInterface.KEY_COMPANY_ID + " = ?", new String[] { del });	
			db.delete(DBInterface.TB_COKEY, DBInterface.KEY_COMPANY_ID + " = ?", new String[] { del });	
			db.delete(DBInterface.TB_IMAGES, DBInterface.KEY_COMPANY_ID + " = ?", new String[] { del });	
		}
		
		db.close();
	}
	
	// Getting All Areas
	public ArrayList<String> getAreas(String where) {
		ArrayList<String> areas = new ArrayList<String>();
		String clause="";
		if(where!=null)clause=" WHERE "+where;
		String extra = " group by co_area order by co_area";
		
		String selectQuery = "SELECT co_area,co_county FROM " + DBInterface.TB_COMPANY+clause+extra;
		

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String area = "";
				String a=cursor.getString(0);
				String b=cursor.getString(1);
				if(a!=null)
					area = area+a;
				if(b!=null)
					area = area+" - "+b;
				// Adding com to list
				if (area!="")
					areas.add(area);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return areas;
	}
	
	// Getting Init Data - Obsolete
	public InitData getInitData() {
		InitData ini = new InitData();
		
		String selectQuery = "SELECT  * FROM " + DBInterface.TB_HG_DATA +" limit 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {					
			String update = cursor.getString(0);
			String urls = cursor.getString(1);
			String favs = cursor.getString(2);
					
			ini.setLastUpdate(update);
			if(urls!=null)
				ini.setInit_images(convertStringToArray(urls));
			if(favs!=null)
				ini.setFavs(convertStringToArray(favs));

		}

		cursor.close();
		db.close();
		return ini;
	}
	
	// Getting Subcategory's ID
	public String getSubcatIDbyName(String parent, String name) {
		String id = "";
		
		String selectQuery = "SELECT _id FROM " + DBInterface.TB_CATEGORIES +" where cat_parent_id='"+parent+"' and cat_name='"+name+"' limit 1";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {					
			id = cursor.getString(0);
		}

		cursor.close();	
		db.close();
		return id;
	}
	
	public SparseArray<String> getCatHash(){
		SparseArray<String> map = new SparseArray<String>();
		String selectQuery = "SELECT _id,cat_name FROM " + DBInterface.TB_CATEGORIES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {			
				map.append(Integer.parseInt(cursor.getString(0)),cursor.getString(1) );				
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return map;
	}
	
	// Getting Categories
	public ArrayList<Category> getCategories(String where) {
		ArrayList<Category> cats = new ArrayList<Category>();
		//  Make the Query
		String clause="";
		if(where!=null)clause=" WHERE "+where;
		
		String selectQuery = "SELECT * FROM " + DBInterface.TB_CATEGORIES +clause+" ORDER BY "+DBInterface.KEY_CAT_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Category cat = new Category();				
				cat.setCat_id(Integer.parseInt(cursor.getString(0)));				
				cat.setCat_name(cursor.getString(1));
				String parent=cursor.getString(2);
				if (parent != null)
					cat.setCat_parent(Integer.parseInt(parent));					
				else
					cat.setCat_parent(0);	
				String icon = cursor.getString(3);				
				cat.setCat_icon(icon);
				// Adding category to list
				cats.add(cat);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return cats;
	}
    
	// Updating Categories
	public void updateOrReplaceCategory(ArrayList<Category> categories) {
		SQLiteDatabase db = this.getWritableDatabase();

		for(Category cat : categories){
			//Log.d("Before update",cat.getCat_id()+" "+cat.getCat_name()+" "+cat.getCat_parent());
			ContentValues values = new ContentValues();
			values.put(DBInterface.KEY_CAT_ID, cat.getCat_id());
			values.put(DBInterface.KEY_CAT_NAME, cat.getCat_name()); // Company Name
			values.put(DBInterface.KEY_CAT_PARENT, (cat.getCat_parent()==0)? null : cat.getCat_parent()); // Company Phone
			values.put(DBInterface.KEY_CAT_ICON, "ic"+Integer.toString(cat.getCat_id())+".png");
			
			// updating row
			db.replace(DBInterface.TB_CATEGORIES,null, values);			
		}
		db.close();
	}
	
	// Deleting companies
	public void deleteCategories(ArrayList<String> deletedIDs) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(String del : deletedIDs){
			db.delete(DBInterface.TB_CATEGORIES, DBInterface.KEY_CAT_ID + " = ?", new String[] { del });	
			db.delete(DBInterface.TB_COCAT, DBInterface.KEY_CATEGORY_ID + " = ?", new String[] { del });
			db.delete(DBInterface.TB_CATKEY, DBInterface.KEY_CATEGORY_ID + " = ?", new String[] { del });
			db.delete(DBInterface.TB_BANNERS, DBInterface.KEY_CATEGORY_ID + " = ?", new String[] { del });
		}
		
		db.close();
	}
	
	/**
	 * Add Mappings to DB
	 * @param map The ArrayList of fk1,fk2 Mappings to save
	 */
	public void addCompanyCategoryMaps(ArrayList<Mapping> map){
		addMappings(map,DBInterface.TB_COCAT,DBInterface.KEY_COMPANY_ID,DBInterface.KEY_CATEGORY_ID);
	}
	public void addCompanyKeywordMaps(ArrayList<Mapping> map){
		addMappings(map,DBInterface.TB_COKEY,DBInterface.KEY_COMPANY_ID,DBInterface.KEY_KEYWORD_ID);
	}
	public void addCategoryKeywordMaps(ArrayList<Mapping> map){
		addMappings(map,DBInterface.TB_CATKEY,DBInterface.KEY_CATEGORY_ID,DBInterface.KEY_KEYWORD_ID);
	}
	
	/**
	 * Remove Mappings from DB
	 * @param map The ArrayList of fk1,fk2 Mappings to remove
	 */
	public void deleteCompanyCategoryMaps(ArrayList<Mapping> map){
		removeMappings(map,DBInterface.TB_COCAT,DBInterface.KEY_COMPANY_ID,DBInterface.KEY_CATEGORY_ID);
	}
	public void deleteCompanyKeywordMaps(ArrayList<Mapping> map){
		removeMappings(map,DBInterface.TB_COKEY,DBInterface.KEY_COMPANY_ID,DBInterface.KEY_KEYWORD_ID);
	}
	public void deleteCategoryKeywordMaps(ArrayList<Mapping> map){
		removeMappings(map,DBInterface.TB_CATKEY,DBInterface.KEY_CATEGORY_ID,DBInterface.KEY_KEYWORD_ID);
	}
	
	
	/**
	 * Generic function which saves a list of mapping in DB.
	 * Cannot accessed directly from activities. 
	 * It should accessed from other function where specifics are defined
	 * @param map ArrayList of Mapping objects
	 * @param table Name of table where queries will execute
	 * @param col1 Name of first column in map table
	 * @param col2 Name of second column in map table
	 */
	protected void addMappings(ArrayList<Mapping> map, String table, String col1, String col2){
		SQLiteDatabase db = this.getWritableDatabase();
		
		for(Mapping m : map){
			ContentValues values = new ContentValues();
			values.put(col1, m.getId1());
			values.put(col2, m.getId2());
			
			// updating row
			db.insert(table,null, values);			
		}
		db.close();
	}
	
	/**
	 * Generic function which deleted a list of mapping in DB.
	 * Cannot accessed directly from activities. 
	 * It should accessed from other function where specifics are defined
	 * @param map ArrayList of Mapping objects
	 * @param table Name of table where queries will execute
	 * @param col1 Name of first column in map table
	 * @param col2 Name of second column in map table
	 */
	protected void removeMappings(ArrayList<Mapping> map, String table, String col1, String col2){
		SQLiteDatabase db = this.getWritableDatabase();
		
		for(Mapping m : map){
			db.delete(table, 
				col1 + " = ? AND " +
				col2 + " = ?", 
				new String[] { 
					Integer.toString(m.getId1()),
					Integer.toString(m.getId2()) 
				});			
		}
		db.close();
	}
	
	/**
	 * Adds new keywords in DB
	 * @param keys An arraylist of keywords
	 */
	public void addKeywords(ArrayList<Keyword> keys){
		SQLiteDatabase db = this.getWritableDatabase();
		for(Keyword k : keys){
			ContentValues values = new ContentValues();
			values.put(DBInterface.KEY_ID, k.getId());
			values.put(DBInterface.KEY_KEYWORD, k.getName());
			
			// updating row
			db.replace(DBInterface.TB_KEYWORDS,null, values);	
		}
		db.close();
	}
	
	/**
	 * Removes keywords from DB
	 * @param del_keys An integer arrayList with IDs of keywords to be removed 
	 */
	public void removeKeywords(ArrayList<Integer> del_keys){
		SQLiteDatabase db = this.getWritableDatabase();
		for(Integer k : del_keys){
			db.delete(DBInterface.TB_KEYWORDS, DBInterface.KEY_ID + " = ?", new String[]{Integer.toString(k)});
			db.delete(DBInterface.TB_CATKEY, DBInterface.KEY_KEYWORD_ID + " = ?", new String[]{Integer.toString(k)});
			db.delete(DBInterface.TB_COKEY, DBInterface.KEY_KEYWORD_ID + " = ?", new String[]{Integer.toString(k)});
		}
		db.close();
	}
	
	/**
	 * Adds images to DB
	 * @param imgs An arrayList of images to be added
	 */
	public void addImages(ArrayList<Image> imgs){
		SQLiteDatabase db = this.getWritableDatabase();
		for(Image i : imgs){
			ContentValues values = new ContentValues();
			values.put(DBInterface.KEY_NAME, i.getName());
			values.put(DBInterface.KEY_WEIGHT, i.getWeight());
			values.put(DBInterface.KEY_COMPANY_ID, i.getCo_id());
			
			// updating row
			int nRowsEffected = db.update(DBInterface.TB_IMAGES,values,
					DBInterface.KEY_NAME+"='"+i.getName()+"' AND "+DBInterface.KEY_COMPANY_ID+"='"+i.getCo_id()+"'",null);
			if(nRowsEffected==0)
				db.insert(DBInterface.TB_IMAGES, null, values);
		}
		db.close();
	}
	
	/**
	 * Removes images from DB
	 * @param imgs An arrayList of images to be removed
	 */
	public void removeImages(ArrayList<Image> imgs){
		SQLiteDatabase db = this.getWritableDatabase();
		for(Image i : imgs){
			db.delete(DBInterface.TB_IMAGES, 
					DBInterface.KEY_NAME + " = ? AND " +
					DBInterface.KEY_COMPANY_ID + " = ?", 
					new String[] { 
						i.getName(),
						Integer.toString(i.getCo_id()) 
					});
		}
		db.close();
	}
	
	/**
	 * Adds banners to DB
	 * @param banners An arrayList of banners to be added
	 */
	public void addBanners(ArrayList<Banner> banners){
		SQLiteDatabase db = this.getWritableDatabase();
		for(Banner b :banners){
			ContentValues values = new ContentValues();
			values.put(DBInterface.KEY_NAME, b.getName());
			values.put(DBInterface.KEY_WEIGHT, b.getWeight());
			values.put(DBInterface.KEY_CATEGORY_ID, b.getCat_id());
			// updating row
			int nRowsEffected = db.update(DBInterface.TB_BANNERS,values,
					DBInterface.KEY_NAME+"='"+b.getName()+"' AND "+DBInterface.KEY_CATEGORY_ID+"='"+b.getCat_id()+"'",null);
			if(nRowsEffected==0)
				db.insert(DBInterface.TB_BANNERS,null, values);
		}
		db.close();
	}
	
	/**
	 * Removes banners from DB
	 * @param del_banners An arrayList of banners to be removed
	 */
	public void removeBanners(ArrayList<Banner> del_banners){		
		SQLiteDatabase db = this.getWritableDatabase();
		for(Banner b  : del_banners){
			db.delete(DBInterface.TB_BANNERS, 
					DBInterface.KEY_NAME + " = ? AND " +
					DBInterface.KEY_CATEGORY_ID + " = ?", 
					new String[] { 
						b.getName(),
						Integer.toString(b.getCat_id()) 
					});
		}
		db.close();
	}
	
	/**
	 * Adds icons to DB
	 * @param icons An ArrayList of icons to be added
	 */
	public void addIcons(ArrayList<Banner> icons){		
		SQLiteDatabase db = this.getWritableDatabase();
		for(Banner b :icons){
			ContentValues values = new ContentValues();					
			values.put(DBInterface.KEY_CAT_ICON, b.getName());
			// updating row
			db.update(DBInterface.TB_CATEGORIES, values,
				DBInterface.KEY_CAT_ID + " = ?", 
				new String[] {Integer.toString(b.getCat_id()) 
			});
		}
		db.close();
	}
	
	/**
	 * Removes icons from DB
	 * @param icons An ArrayList of icons to be deleted
	 */
	public void removeIcons(ArrayList<Banner> icons){		
		SQLiteDatabase db = this.getWritableDatabase();
		for(Banner b :icons){
			ContentValues values = new ContentValues();					
			values.putNull(DBInterface.KEY_CAT_ICON);
			// updating row
			db.update(DBInterface.TB_CATEGORIES, values,
				DBInterface.KEY_CAT_ID + " = ?", 
				new String[] {Integer.toString(b.getCat_id()) 
			});
		}
		db.close();
	}
	
	/**
	 * Get banners from DB based on category
	 * @param category_id The ID of the category these banners belong
	 * @return An arrayList with string values of banner names
	 */
	public ArrayList<String> getBanners(int category_id){
		ArrayList<String> banners = new ArrayList<String>();
		String selectQuery = "SELECT name FROM "+ DBInterface.TB_BANNERS+" "+
				"WHERE category_id = '"+Integer.toString(category_id)+"' "+
				"GROUP BY name "+
				"ORDER BY weight DESC, name ASC";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				banners.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return banners;
	}
	
	/**
	 * Get all banners from DB
	 * @return An arrayList with string values of banner names
	 */
	public ArrayList<String> getBanners(){
		Utils util = new Utils(ctx);
		ArrayList<String> banners = new ArrayList<String>();
		String selectQuery = "SELECT name FROM "+ DBInterface.TB_BANNERS+" "+				
				"GROUP BY name ";
		List<String> fnames = util.getFilesList();				
		fnames.addAll(util.getAssetList("imgs"));		
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String imgName = cursor.getString(0);				
				if(!fnames.contains(imgName)){
					banners.add(URL_BANNERS+imgName);
				}				
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		return banners;
	}
	
	
    /**
     * Returns a Cursor over all keywords that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getSuggestionsMatches(String query, String[] columns, HashMap<String,Integer> hash) {
    	query = query.replace(" ", "%");
        String selection = DBInterface.KEY_KEYWORD + " LIKE ?";
        String[] selectionArgs = new String[] {"%"+query+"%"};
        
        String rawWhere = "";
        if(hash.size()>1){	        
	        boolean isFirst = true;
	        for(Map.Entry<String, Integer> entry: hash.entrySet()) {
	            System.out.println(entry.getKey() + " : " + entry.getValue());	            
	            switch (entry.getValue()){
	            case KeywordProvider.TYPE_BOTH:
	            	rawWhere += (isFirst) ? "WHERE " : "AND ";
	            	rawWhere += "( k1.keyword LIKE '%"+entry.getKey()+"%' or k2.keyword LIKE '%"+entry.getKey()+"%' ) ";
	            	isFirst = false;
	            	break;
	            case KeywordProvider.TYPE_CATEGORY:
	            	rawWhere += (isFirst) ? "WHERE " : "AND ";
	            	rawWhere += "k1.keyword LIKE '%"+entry.getKey()+"%' ";
	            	isFirst = false;
	            	break;
	            case KeywordProvider.TYPE_COMPANY:
	            	rawWhere += (isFirst) ? "WHERE " : "AND ";
	            	rawWhere += "k2.keyword LIKE '%"+entry.getKey()+"%' ";
	            	isFirst = false;
	            	break;
	            default:
	            	break;	            
	            };
	        }
        }
        
        return doSuggestionsQuery(selection, selectionArgs, columns, rawWhere);

    }
	
    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
	public Cursor doSuggestionsQuery(String selection, String[] selectionArgs, String[] columns, String rawWhere){
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		Cursor cursor = null;
		
		if(rawWhere.equals("")){
			Map<String, String> projectionMap = new HashMap<String, String>(); 
			projectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, DBInterface.KEY_KEYWORD + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1); 
			projectionMap.put(DBInterface.KEY_ID, DBInterface.KEY_ID); 
			projectionMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, DBInterface.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);		
			
			builder.setTables(DBInterface.TB_KEYWORDS);
	        builder.setProjectionMap(projectionMap);

	        cursor = builder.query(db, columns, selection, selectionArgs, null, null, DBInterface.KEY_KEYWORD+" ASC");
		}
		else{
			String rawQ = "SELECT k1._id AS _id, k1._id || '-' || k2._id AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID+", "+
					"k1.keyword AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", k2.keyword AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+" "+
					"FROM keywords k1 "+
					"INNER JOIN category_keyword_map ctk ON ctk.keyword_id=k1._id "+
					"INNER JOIN company_category_map cc ON cc.category_id=ctk.category_id "+
					"INNER JOIN company_keyword_map cmk ON cmk.company_id=cc.company_id "+
					"INNER JOIN keywords k2 ON k2._id=cmk.keyword_id "+
					rawWhere;
			//Log.d("MULTIWORDS",rawQ);
			cursor = db.rawQuery(rawQ, null);
		}

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
	}
	
	public ArrayList<String> getMatchedKeywordIDs(String query){
		ArrayList<String> ids = new ArrayList<String>();
		String selectQ = "SELECT "+DBInterface.KEY_ID+
				" FROM "+DBInterface.TB_KEYWORDS+
				" WHERE "+DBInterface.KEY_KEYWORD+" LIKE '%"+query+"%'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQ, null);
		
		if (cursor.moveToFirst()) {
			do {			
				ids.add(cursor.getString(0));				
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		
		return ids;
	}
	
	public SparseArray<String> getCategoryKeywordsSparse(){
		SparseArray<String> catKeys = new SparseArray<String>();
		String selectQ = "SELECT k.* FROM keywords k "+
				"INNER JOIN category_keyword_map ctk ON ctk.keyword_id=k._id "+
				"GROUP BY k._id";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQ, null);
		
		if (cursor.moveToFirst()) {
			do {			
				catKeys.append(Integer.parseInt(cursor.getString(0)),cursor.getString(1) );					
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		
		return catKeys;
	}
	
	public SparseArray<String> getCompanyKeywordsSparse(){
		SparseArray<String> comKeys = new SparseArray<String>();
		String selectQ = "SELECT k.* FROM keywords k "+
				"INNER JOIN company_keyword_map cmk ON cmk.keyword_id=k._id "+
				"GROUP BY k._id";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQ, null);
		
		if (cursor.moveToFirst()) {
			do {			
				comKeys.append(Integer.parseInt(cursor.getString(0)),cursor.getString(1) );					
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		
		return comKeys;
	}
	
	public boolean isFav(String coID){
		boolean is_fav=false;
		ArrayList<String> favs = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "+DBInterface.KEY_HG_FAVS+" from "+DBInterface.TB_HG_DATA+" limit 1",null);		
		if (cursor.moveToFirst()) {					
			String f = cursor.getString(0);
			if(f != null && !f.equals("")) favs = convertStringToArray(f);
		}
		cursor.close();
		db.close();
		
		if(favs.size()>0){
			if (favs.contains(coID))is_fav=true;
		}
		return is_fav;
	}

	public void setUpdate(String update){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();		
		args.put(DBInterface.KEY_HG_UPDATE, update);
		db.update(DBInterface.TB_HG_DATA, args, null, null);
		db.close();
	}
	
	public String getUpdate(){		
		String update = "";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "+DBInterface.KEY_HG_UPDATE+" from "+DBInterface.TB_HG_DATA+" limit 1",null);		
		if (cursor.moveToFirst()) {					
			update = cursor.getString(0);			
		}
		cursor.close();
		db.close();
		return update;
	}
	
	public void setIniImgSeq(ArrayList<String> imgs){
		if (imgs.size()>0){
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues args = new ContentValues();
			String sq = convertArrayToString(imgs);
			args.put(DBInterface.KEY_HG_IMG, sq);
			db.update(DBInterface.TB_HG_DATA, args, null, null);
			db.close();
		}
	}
	
	public ArrayList<String> getFavSeq(){		
		ArrayList<String> favs = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select "+DBInterface.KEY_HG_FAVS+" from "+DBInterface.TB_HG_DATA+" limit 1",null);		
		if (cursor.moveToFirst()) {					
			String f = cursor.getString(0);
			if(f != null && !f.equals("")) favs = convertStringToArray(f);
		}
		cursor.close();
		db.close();
		return favs;
	}
	
	public void setFavSeq(ArrayList<String> favs){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		String sq = convertArrayToString(favs);
		args.put(DBInterface.KEY_HG_FAVS, sq);
		db.update(DBInterface.TB_HG_DATA, args, null, null);
		db.close();
	}
	
	public void setFav(String coID){
		if(!isFav(coID)){
			ArrayList<String> favs = getFavSeq();
			favs.add(coID);
			setFavSeq(favs);
		}
	}
	
	public void removeFav(String coID){
		if(isFav(coID)){
			ArrayList<String> favs = getFavSeq();
			favs.remove(coID);
			setFavSeq(favs);
		}
	}
	
	// Getting companys Count
	public int getCompaniesCount() {
		String countQuery = "SELECT  * FROM " + DBInterface.TB_COMPANY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		// return count
		return count;
	}
	
	public boolean Exists(String key , String value) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select 1 from "+DBInterface.TB_COMPANY+" where "+key+" = ?", new String[] { value });
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		db.close();
		return exists;
	}
	
	public static String convertArrayToString(ArrayList<String> array){
	    String str = "";
	    if(array!=null && array.size()>0){
		    for (int i = 0;i<array.size(); i++) {
		        str = str+array.get(i);
		        // Do not append comma at the end of last element
		        if(i<array.size()-1){
		            str = str+",";
		        }
		    }
	    }
	    return str;
	}
	public static ArrayList<String> convertStringToArray(String str){
		if(str.length()>0){
			ArrayList<String> arr = new ArrayList<String>(Arrays.asList(str.split(",")));	    
	    	return arr;
		}
		else return null;
	}
	
	public void close(){
		super.close();
	}
}
