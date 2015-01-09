package org.dklisiaris.downtown.helper;


import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.dklisiaris.downtown.db.Banner;
import org.dklisiaris.downtown.db.Category;
import org.dklisiaris.downtown.db.Company;
import org.dklisiaris.downtown.db.Image;
import org.dklisiaris.downtown.db.InitData;
import org.dklisiaris.downtown.db.Keyword;
import org.dklisiaris.downtown.db.Mapping;
import org.dklisiaris.downtown.db.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.text.TextUtils;
import android.util.Log;

public class XMLParser {	
	static final String KEY_PRODUCT = "product";
	static final String KEY_NAME = "name";
	static final String KEY_DEL = "delete";
	static final String KEY_DELS = "deleted";
	
    static final String KEY_DESC = "description";
    static final String KEY_IMAGE = "image";
    static final String KEY_DEL_IMAGE = "del_image";
    static final String KEY_CATEGORY = "category";
    static final String KEY_SUBCATEGORY = "subcategory";
    static final String KEY_AVAIL = "availability";
   
    static final String KEY_LANG= "latitude";
    static final String KEY_LONG = "longitude";
    static final String KEY_ID = "id";
    static final String KEY_ADDRESS = "address";
    static final String KEY_AREA = "area";
    static final String KEY_COUNTY = "county";
    static final String KEY_WEBSITE = "website";
    static final String KEY_TELS = "telephones";
    static final String KEY_TEL = "tel";
    static final String KEY_FAX = "fax";
    static final String KEY_TK = "tk";
    static final String KEY_LEVEL = "level";
    static final String KEY_CATEGORIES = "categories";
    static final String KEY_SUBCATEGORIES = "subcategories";    
    static final String KEY_INIT_IMG = "initial_images";
    static final String KEY_INIT_IMAGE = "init_image";
    static final String KEY_DEL_INIT_IMAGE = "del_init_image";
    
    static final String ATTR_CATEGORY_ID = "cat_id";
    static final String ATTR_ID = "id";
    static final String ATTR_UPDATE = "update-time";
        
    static final String KEY_UPDATES = "Downtown_Updates";
    static final String KEY_LICENSE = "license";
	static final String KEY_AVAIL_UPDATES = "available-updates";
	
	static final String KEY_CO_CAT = "co_cat";
	static final String KEY_DEL_CO_CAT = "del_co_cat";
	static final String KEY_KEY = "key";
	static final String KEY_DEL_KEY = "del_key";
	static final String KEY_CO_KEY = "co_key";
	static final String KEY_DEL_CO_KEY = "del_co_key";
	static final String KEY_CAT_KEY = "cat_key";
	static final String KEY_DEL_CAT_KEY = "del_cat_key";
	
	static final String ATTR_CO_ID = "co_id";
    static final String ATTR_CAT_ID = "cat_id";
    static final String ATTR_KEY_ID = "key_id";
        
    static final String KEY_BANNER = "banner";
    static final String KEY_DEL_BANNER = "del_banner";
    static final String KEY_ICON = "icon";
    static final String KEY_DEL_ICON = "del_icon";
    static final String KEY_BLANK = "blankImg";
    static final String ATTR_WEIGHT = "w";
	
	public XMLParser(){}
	
	public String getXmlFromUrl(String url) {
        String xml = null;
 
        try {
            // defaultHttpClient
            /*DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);*/
            
        	HttpGet httpGet = new HttpGet(url);
		    HttpParams httpParameters = new BasicHttpParams();
		    // Set the timeout in milliseconds until a connection is established.
		    // The default value is zero, that means the timeout is not used. 
		    int timeoutConnection = 4000;
		    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		    // Set the default socket timeout (SO_TIMEOUT) 
		    // in milliseconds which is the timeout for waiting for data.
		    int timeoutSocket = 6000;
		    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		    HttpResponse response = httpClient.execute(httpGet);
		    
		    HttpEntity httpEntity = response.getEntity();
            xml = EntityUtils.toString(httpEntity);
        }catch (ConnectTimeoutException e) { 
        	xml = null;
        	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	xml = null;
            e.printStackTrace();
        } catch (ClientProtocolException e) {
        	xml = null;
            e.printStackTrace();
        } catch (IOException e) {
        	xml = null;
            e.printStackTrace();
        } 
        // return XML
        return xml;
    }
	
	public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
	
	public String getValue(Element item, String str) {      
	    NodeList n = item.getElementsByTagName(str);        
	    return this.getElementValue(n.item(0));
	}
	 
	public final String getElementValue( Node elem ) {
	         Node child;
	         if( elem != null){
	             if (elem.hasChildNodes()){
	                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  }
	
	public String getAttributeFromNode(Element item, String key, String attr){
    	NodeList nl = item.getElementsByTagName(key);
    	Element elem = (Element)nl.item(0);
    	return elem.getAttribute(attr);
		
	}
	
	/**
	 * Getting if we should update from update.xml
	 */
	public boolean getUpdate(Document doc){		
		Element e = (Element) doc.getElementsByTagName(KEY_UPDATES).item(0);
		String license = getValue(e,KEY_LICENSE);
		String isUpdate = getValue(e,KEY_AVAIL_UPDATES);		
		if(license.equals("true") && isUpdate.equals("yes"))
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Parsing all company-category mappings from companies.xml
	 * @param isDeleted : true if we want deleted mappings, false otherwise 
	 */
	public ArrayList<Mapping> parseCoCatMaps(Document doc, boolean isDeleted){
		ArrayList<Mapping> cocats = new ArrayList<Mapping>();	
		String tag = (isDeleted)? KEY_DEL_CO_CAT : KEY_CO_CAT;		
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int co_id = Integer.parseInt(e.getAttribute(ATTR_CO_ID)); 
            int cat_id = Integer.parseInt(e.getAttribute(ATTR_CAT_ID)); 
            Mapping ccm = new Mapping(co_id,cat_id);
            cocats.add(ccm);
		}		
		return cocats;
	}
	
	
	/**
	 * Parsing all keywords from keywords.xml
	 */
	public ArrayList<Keyword> parseKeywords(Document doc){
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();					
		NodeList nl = doc.getElementsByTagName(KEY_KEY);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int key_id = Integer.parseInt(e.getAttribute(ATTR_KEY_ID)); 
            String key_name = getElementValue(e);
            Keyword key = new Keyword(key_id,key_name);
            keywords.add(key);
		}		
		return keywords;
	}
	
	/**
	 * Parsing all deleted keywords from keywords.xml
	 */
	public ArrayList<Integer> parseDeletedKeywords(Document doc){
		ArrayList<Integer> keyIDs = new ArrayList<Integer>();					
		NodeList nl = doc.getElementsByTagName(KEY_DEL_KEY);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);            
            int key_id = Integer.parseInt(getElementValue(e));            
            keyIDs.add(key_id);
		}		
		return keyIDs;
	}
	
	
	/**
	 * Parsing all company-keyword mappings from keywords.xml
	 * @param isDeleted : true if we want deleted mappings, false otherwise 
	 */
	public ArrayList<Mapping> parseCoKeyMaps(Document doc, boolean isDeleted){
		ArrayList<Mapping> cokeys = new ArrayList<Mapping>();	
		String tag = (isDeleted)? KEY_DEL_CO_KEY : KEY_CO_KEY;		
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int co_id = Integer.parseInt(e.getAttribute(ATTR_CO_ID)); 
            int key_id = Integer.parseInt(e.getAttribute(ATTR_KEY_ID)); 
            Mapping ckm = new Mapping(co_id,key_id);
            cokeys.add(ckm);
		}		
		return cokeys;
	}
	
	/**
	 * Parsing all category-keyword mappings from keywords.xml
	 * @param isDeleted : true if we want deleted mappings, false otherwise 
	 */
	public ArrayList<Mapping> parseCatKeyMaps(Document doc, boolean isDeleted){
		ArrayList<Mapping> catkeys = new ArrayList<Mapping>();	
		String tag = (isDeleted)? KEY_DEL_CAT_KEY : KEY_CAT_KEY;		
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int cat_id = Integer.parseInt(e.getAttribute(ATTR_CAT_ID)); 
            int key_id = Integer.parseInt(e.getAttribute(ATTR_KEY_ID)); 
            Mapping ckm = new Mapping(cat_id,key_id);
            catkeys.add(ckm);
		}		
		return catkeys;
	}
	
	/**
	 * Parsing all images from images.xml
	 * @param isDeleted : true if we want deleted images, false otherwise
	 */
	public ArrayList<Image> parseImages(Document doc, boolean isDeleted){
		ArrayList<Image> imgs = new ArrayList<Image>();		
		String tag = (isDeleted)? KEY_DEL_IMAGE : KEY_IMAGE;
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int co_id = Integer.parseInt(e.getAttribute(ATTR_CO_ID)); 
            int weight = (isDeleted)? 0 : Integer.parseInt(e.getAttribute(ATTR_WEIGHT));
            String img_url = getElementValue(e);
            Image img = new Image(co_id,weight,img_url);
            imgs.add(img);
		}
		return imgs;
	}
	
	/**
	 * Parsing all banners or icons from images.xml
	 * @param isIcon : true if we want icons, false if banners 
	 * @param isDeleted : true if we want deleted images, false otherwise
	 */
	public ArrayList<Banner> parseBanners(Document doc,boolean isIcon, boolean isDeleted){
		ArrayList<Banner> imgs = new ArrayList<Banner>();		
		String tag = (isIcon)? KEY_ICON : KEY_BANNER;
		if(isDeleted) tag = "del_"+tag;		
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int cat_id = Integer.parseInt(e.getAttribute(ATTR_CAT_ID)); 
            int weight = (!isDeleted && !isIcon)? Integer.parseInt(e.getAttribute(ATTR_WEIGHT)) : 0;
            String img_url = getElementValue(e);
            Banner img = new Banner(cat_id,weight,img_url,isIcon);
            imgs.add(img);
		}
		return imgs;
	}
	
	/**
	 * Parsing initial images (start Banners) from images.xml
	 * @param isDeleted : true if we want deleted banners, false otherwise
	 */
	public ArrayList<Banner> parseStartBanners(Document doc, boolean isDeleted){
		ArrayList<Banner> imgs = new ArrayList<Banner>();		
		String tag = (isDeleted)? KEY_DEL_INIT_IMAGE : KEY_INIT_IMAGE;
		NodeList nl = doc.getElementsByTagName(tag);
		for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);            
            int weight = Integer.parseInt(e.getAttribute(ATTR_WEIGHT));
            String img_url = getElementValue(e);
            Banner img = new Banner(0,weight,img_url,false);
            imgs.add(img);
		}
		return imgs;
	}
	
	/**
	 * Check if there is a blank image to dl - from images.xml
	 */
	public boolean getBlankImg(Document doc){		
		Element e = (Element) doc.getElementsByTagName(KEY_BLANK).item(0);
		String blank = getElementValue(e);				
		if(blank!=null && blank.equals("Blankside.jpg"))
			return true;
		else 
			return false;
	}
	
	/**
	 * Parsing all categories from categories.xml
	 */
	public ArrayList<Category> getAllCategories(Document doc){
		ArrayList<Category> cats = new ArrayList<Category>();		
		
		//Document doc = getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_CATEGORY);
        
        // looping through all item nodes <category>
        for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
        	String name = getValue(e,KEY_NAME);
        	int id=Integer.parseInt(e.getAttribute(ATTR_ID));        	
        	NodeList subItems = e.getElementsByTagName(KEY_SUBCATEGORY);
            for(int j=0; j<subItems.getLength(); j++){
            	Element subItem = (Element) subItems.item(j);
	            String subcat_name = getElementValue(subItem);
	            int subcat_id = Integer.parseInt(subItem.getAttribute(ATTR_ID));
	            int subcat_parent=id;
	            Category subcat = new Category(subcat_id,subcat_name,subcat_parent);
	            cats.add(subcat);
            	//Log.d("Subcat",subcat.getCat_name());
            }
        	Category cat = new Category(id,name,0);
        	cats.add(cat);
            
        }
                
		return cats;
		
	}
	
	public InitData getInitData(Document doc){			
		//Document doc = getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_INIT_IMG);
        
        Element e = (Element) nl.item(0);
    	String update = e.getAttribute(ATTR_UPDATE);
    	//Log.d("Update time:",update);
    	ArrayList<String> imgs = getImgUrlsFromElem(e,KEY_IMAGE);
    	InitData ini = new InitData(update,imgs);

		return ini;		
	}
	
	public ArrayList<String> getDeleted(Document doc){
		ArrayList<String> dels = new ArrayList<String>();		
		
		//Document doc = getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_DEL);
        
        // looping through all item nodes <category>
        for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
        	String del_id = getElementValue(e);        	
        	dels.add(del_id);
        }
        return dels;
	}

	public ArrayList<Company> getAllCompanies(Document doc){
		ArrayList<Company> companies = new ArrayList<Company>();
		
		//Document doc = getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_PRODUCT);
        
        // looping through all item nodes <category>
        for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            int co_id = Integer.parseInt(getValue(e,KEY_ID));
        	int avail = getValue(e,KEY_AVAIL).equals("yes") ? 1 : 0;
        	int level = Integer.parseInt(getValue(e,KEY_LEVEL));
        	int cat_id = Integer.parseInt(getAttributeFromNode(e,KEY_CATEGORY,ATTR_CATEGORY_ID));
        	String website=null;
        	if(avail==1)website=getValue(e,KEY_WEBSITE);        	
        	double langi=0.0,longi=0.0;
        	ArrayList<String> imgs = getImgUrlsFromElem(e,KEY_IMAGE);
        	ArrayList<String> tels = getMultiValuesFromElem(e,KEY_TEL);
        	ArrayList<String> subcats = new ArrayList<String>();
        	NodeList subItems = e.getElementsByTagName(KEY_SUBCATEGORY);
            for(int j=0; j<subItems.getLength(); j++){
            	Element subItem = (Element) subItems.item(j);	                        
	            subcats.add(subItem.getAttribute(ATTR_CATEGORY_ID));            	
            }
        	String county = getValue(e,KEY_COUNTY);
        	String tk = getValue(e,KEY_TK);
        	String fax = getValue(e,KEY_FAX);
        	String strLang=getValue(e,KEY_LANG), strLong=getValue(e,KEY_LONG);
        	//Log.d("Coords: ",strLang+" - "+strLong);
        	if (exists(strLang) && exists(strLong)){
            	try{
            		langi = Double.parseDouble(getValue(e,KEY_LANG));
            		longi = Double.parseDouble(getValue(e,KEY_LONG));
            	}catch(NumberFormatException nfe){nfe.printStackTrace();}
        	}
        	Company com =new Company(co_id,getValue(e,KEY_NAME),getValue(e,KEY_ADDRESS),getValue(e,KEY_DESC),
        			imgs,tels,subcats,cat_id,getValue(e,KEY_AREA),county,tk,level,avail,
        			website,fax,langi,longi);
        	//Log.d("Namexml",pr.getName());
        	companies.add(com);            	
            
        }        
		return companies;
		
	}
	
	
	
	public ArrayList<String> getAllValuesWithTag(Document doc, String itemTag){
		ArrayList<String> items = new ArrayList<String>();
		//Document doc = getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(itemTag);
        // looping through all item nodes <category>
        for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            String value = getElementValue(e);
            if (!items.contains(value) && value != null && !TextUtils.isEmpty(value)){           	
            		items.add(value); 
            		//Log.d("Item added:",value);
            }
        }	

		return items;
	}
	
	public ArrayList<String> getMultiValuesFromElem(Element elem , String multiTag){
		ArrayList<String> multi = new ArrayList<String>();
		NodeList subItems = elem.getElementsByTagName(multiTag);	
        for(int j=0; j<subItems.getLength(); j++){
        	Element subItem = (Element) subItems.item(j);
            String value = getElementValue(subItem);
            if (!multi.contains(value)){
            	//Log.d(getValue(elem,KEY_NAME),value);
            		multi.add(value); 
            		//Log.d("Tel",value);
            }
        }
		return multi;
	}
	
	public ArrayList<String> getImgUrlsFromElem(Element elem , String multiTag){
		ArrayList<String> multi = new ArrayList<String>();
		NodeList subItems = elem.getElementsByTagName(multiTag);	
        for(int j=0; j<subItems.getLength(); j++){
        	Element subItem = (Element) subItems.item(j);
            String value = getElementValue(subItem);
            if (!multi.contains(value) && isValidImageName(value)){
            	//Log.d(getValue(elem,KEY_NAME),value);
            		multi.add(value); 
            }
        }
		return multi;
	}
	
	public boolean isValidImageName(String imgName){
		if (imgName.endsWith(".jpg") || imgName.endsWith(".jpeg") || imgName.endsWith(".png") || imgName.endsWith(".bmp")){
			if(!imgName.startsWith("Blankside") && !imgName.startsWith("1Blankside") && imgName!=null && !TextUtils.isEmpty(imgName)){
				return true;
			}else return false;	
		} 
		else return false;
	}
	
	public ArrayList<String> getAllSubTagItems(Document doc, String elemTag, String itemTag, boolean isMulti){
		ArrayList<String> items = new ArrayList<String>();
		//Document doc = getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(elemTag);
        // looping through all item nodes <category>
        for (int k = 0; k < nl.getLength();k++) {
            Element e = (Element) nl.item(k);
            if(isMulti){
	            NodeList subItems = e.getElementsByTagName(itemTag);	
	            for(int j=0; j<subItems.getLength(); j++){
	            	Element subItem = (Element) subItems.item(j);
		            String value = getElementValue(subItem);
		            if (!items.contains(value)){           	
		            		items.add(value); 
		            		//Log.d("Item added:",value);
		            }
	            	//Log.d("Subcat",parser.getElementValue(subcat));
	            }
            }else{
	            String value = getValue(e, itemTag);
	            if (!items.contains(value)){           	
	            		items.add(value); 
	            		//Log.d("Item added:",value);
	            }
            }
        }	

		return items;
	}
	
	
	public static boolean exists(final String string)  
	{  
	   return string != null && !TextUtils.isEmpty(string) && !TextUtils.isEmpty(string.trim());  
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
}
