package org.dklisiaris.downtown.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class KeywordContract {

	/**
	 * The authority of the keywords provider.
	 */
	public static final String AUTHORITY = 
	      "org.dklisiaris.downtown.providers.keywords";
	
	/**
	 * The content URI for the top-level 
	 * keywords authority.
	 */
	public static final Uri CONTENT_URI = 
	      Uri.parse("content://" + AUTHORITY);
	
	/**
	 * A selection clause for ID based queries.
	 */
	public static final String SELECTION_ID_BASED = BaseColumns._ID + " = ? ";
	
	/**
	 * Constants for the Items table of the keywords provider.
	 */
	public static final class Keywords implements CommonColumns {
		/**
		 * The content URI for this table. 
		 */
		public static final Uri CONTENT_URI =  Uri.withAppendedPath(KeywordContract.CONTENT_URI, "keywords");
		/**
		 * The mime type of a directory of keywords.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.org.dklisiaris.downtown.keywords_keys";
		/**
		 * The mime type of a single keyword.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.org.dklisiaris.downtown.keywords_keys";
		/**
		 * A projection of all columns in the keywords table.
		 */
		public static final String[] PROJECTION_ALL = {_ID, NAME};
		/**
		 * The default sort order for queries containing NAME fields.
		 */
		public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
	}
	
	
   /**
    * This interface defines common columns found in multiple tables.
    */
	public static interface CommonColumns extends BaseColumns {
      /**
       * The name of the keyword.
       */
      public static final String NAME = "keyword";
	}
	
}
