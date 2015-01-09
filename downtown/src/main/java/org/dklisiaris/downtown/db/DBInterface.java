package org.dklisiaris.downtown.db;

/**
 * A public interface with database constants.
 * It should be used wherever column names are needed instead 
 * of direct access.
 * @author MeeC
 *
 */
public interface DBInterface {
	final String DATABASE_NAME = "hellasguideDB";
	
	// Companies Table Columns names
	static final String KEY_ID = "_id";
	
	static final String TB_COMPANY = "company";
	static final String KEY_CO_NAME = "co_name";
    static final String KEY_ADDRESS = "co_address";
    static final String KEY_TEL = "co_tel";
    static final String KEY_FAX = "co_fax";
    static final String KEY_DESC = "co_description";
    static final String KEY_IMAGE = "co_image";
    static final String KEY_CATEGORY = "co_category";
    static final String KEY_SUBCATEGORY = "co_subcategory";
	static final String KEY_AREA = "co_area";
    static final String KEY_AVAIL = "co_availability";
    static final String KEY_SITE = "co_website";
    static final String KEY_LANG= "co_latitude";
    static final String KEY_LONG = "co_longitude";
    static final String KEY_LEVEL = "co_level";
    static final String KEY_COUNTY = "co_county";
    static final String KEY_TK = "co_tk";
    
    static final String TB_CATEGORIES = "categories";
    static final String KEY_CAT_ID = "_id";
    static final String KEY_CAT_NAME = "cat_name";
    static final String KEY_CAT_PARENT = "cat_parent_id";
    static final String KEY_CAT_ICON = "cat_icon";
    
    static final String TB_HG_DATA = "hg_data";
    static final String KEY_HG_UPDATE = "hg_update";
    static final String KEY_HG_IMG = "hg_images";
    static final String KEY_HG_FAVS = "hg_favs";
    
    static final String TB_COCAT = "company_category_map";
    static final String TB_COKEY = "company_keyword_map";
    static final String TB_CATKEY = "category_keyword_map";
    
    static final String KEY_CATEGORY_ID = "category_id";
    static final String KEY_COMPANY_ID = "company_id";
    static final String KEY_KEYWORD_ID = "keyword_id";
    
    static final String TB_KEYWORDS = "keywords";
    static final String KEY_KEYWORD = "keyword";
    
    static final String TB_IMAGES = "images";
    static final String TB_BANNERS = "banners";
    static final String KEY_NAME = "name";
    static final String KEY_WEIGHT = "weight";
	
	
}
