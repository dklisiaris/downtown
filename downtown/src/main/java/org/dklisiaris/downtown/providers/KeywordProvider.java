package org.dklisiaris.downtown.providers;


import java.util.HashMap;

import org.dklisiaris.downtown.db.DBHandler;
import org.dklisiaris.downtown.db.DBInterface;
import org.dklisiaris.downtown.providers.KeywordContract.Keywords;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

public class KeywordProvider extends ContentProvider {
	private DBHandler dbHandler;
	private Context ctx;
	
	// helper constants for use with the UriMatcher
	private static final int KEYWORD_LIST = 1;
	private static final int KEYWORD_ID = 2;
	private static final int SEARCH_SUGGEST = 3;
	private static final UriMatcher URI_MATCHER;
	public static final int TYPE_NONE = 0;
	public static final int TYPE_CATEGORY = 1;
	public static final int TYPE_COMPANY = 2;
	public static final int TYPE_BOTH = 3;
	private SparseArray<String> catKeyIndex = null;
	private SparseArray<String> comKeyIndex = null;
	
	
	// prepare the UriMatcher
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(KeywordContract.AUTHORITY, "keywords", KEYWORD_LIST);
		URI_MATCHER.addURI(KeywordContract.AUTHORITY, "keywords/#", KEYWORD_ID);	
		// to get suggestions...
		URI_MATCHER.addURI(KeywordContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		URI_MATCHER.addURI(KeywordContract.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
	}
	
	@Override
	public boolean onCreate() {
		ctx = getContext();
		dbHandler = DBHandler.getInstance(ctx);
		catKeyIndex = dbHandler.getCategoryKeywordsSparse();
		comKeyIndex = dbHandler.getCompanyKeywordsSparse();
		
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
				
		switch (URI_MATCHER.match(uri)) {
	        case SEARCH_SUGGEST:
	            if (selectionArgs == null) {
	              throw new IllegalArgumentException(
	                  "selectionArgs must be provided for the Uri: " + uri);
	            }
	            return getSuggestions(selectionArgs[0]);
			case KEYWORD_LIST:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                        "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
			case KEYWORD_ID:
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
				return null;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
			case SEARCH_SUGGEST:
	            return SearchManager.SUGGEST_MIME_TYPE;
			case KEYWORD_LIST:
				return Keywords.CONTENT_TYPE;
			case KEYWORD_ID:
				return Keywords.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
    private Cursor getSuggestions(String query) {
        query = prepareQuery(query);
        //Log.d("PROVIDER QUERY: ",query);
        String[] qTokens = query.split(" ");
        HashMap<String,Integer> queryHash = new HashMap<String,Integer>();
        if(qTokens.length>0 && qTokens[0].length()>1){        	
            for(String q : qTokens){
            	int type = evaluateQuery(q);
            	//Log.d("qTOKEN",q+": "+type);
            	queryHash.put(q, type);
            }        	
        }

        String[] columns = new String[] {
    		DBInterface.KEY_ID,
    		SearchManager.SUGGEST_COLUMN_TEXT_1,    
            //SearchManager.SUGGEST_COLUMN_TEXT_2,
         /* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
                          (only if you want to refresh shortcuts)*/
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return dbHandler.getSuggestionsMatches(query, columns, queryHash);
      }
	
	/**
	 * The following method should not be used anywhere in the app
	 * since this provider is only for search suggestions queries.
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	/**
	 * The following method should not be used anywhere in the app
	 * since this provider is only for search suggestions queries.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * The following method should not be used anywhere in the app
	 * since this provider is only for search suggestions queries.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	private String prepareQuery(String query){
		String q = query;
		if(q.length()>48)
    		q = q.substring(0,47); 
		q=q.trim();
		q = q.toLowerCase();
        q=q.replace('ά', 'α');
        q=q.replace('έ', 'ε');
        q=q.replace('ό', 'ο');
        q=q.replace('ύ', 'υ');
        q=q.replace('ί', 'ι');
        q=q.replace('ή', 'η');
        q=q.replace('ώ', 'ω');
        q=q.replace('ς', 'σ');
		q=q.toUpperCase();
		
		return q;
	}
	
	private int evaluateQuery(String word){
		boolean isCatKey = isCategoryKeyword(word);
		boolean isComKey = isCompanyKeyword(word);
		
		if(isCatKey && isComKey){
			return TYPE_BOTH;
		}
		else if(isCatKey){
			return TYPE_CATEGORY;
		}
		else if(isComKey){
			return TYPE_COMPANY;
		}
		else{
			return TYPE_NONE;
		}

	}
	
	private boolean isCategoryKeyword(String word){
		if(catKeyIndex == null){
			catKeyIndex = dbHandler.getCategoryKeywordsSparse();
		}
		
		for(int i = 0; i < catKeyIndex.size(); i++) {
			int id = catKeyIndex.keyAt(i);		      
			String kword = catKeyIndex.get(id);
			if (kword.contains(word)){
				return true;
			}				   
		}
		return false;
	}
	
	private boolean isCompanyKeyword(String word){
		if(comKeyIndex == null){
			comKeyIndex = dbHandler.getCompanyKeywordsSparse();
		}
		
		for(int i = 0; i < comKeyIndex.size(); i++) {
			int id = comKeyIndex.keyAt(i);		      
			String kword = comKeyIndex.get(id);
			if (kword.contains(word)){
				Log.d("this is com key", kword+" "+word);
				return true;
			}				   
		}
		return false;
	}

}
