package org.dklisiaris.downtown.adapters;

import org.dklisiaris.downtown.R;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class CustomSuggestionsAdapter extends CursorAdapter
{
    private static final int QUERY_LIMIT = 50;

    private LayoutInflater inflater;
    private SearchView searchView;
    private SearchableInfo searchable;

    public CustomSuggestionsAdapter(Context context, SearchableInfo info, SearchView searchView)
    {
        super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.searchable = info;
        this.searchView = searchView;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View v, Context context, Cursor c)
    {
        String name = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        TextView namet = (TextView) v.findViewById(R.id.keyword_suggestion);
        namet.setText(name);
        
        String suggestion2=null;
        if(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2)!=-1)
        	suggestion2 = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
        if(suggestion2!=null){
            String name2 = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2));
            TextView namet2 = (TextView) v.findViewById(R.id.keyword_suggestion2);
            namet2.setText(name2);
        }
    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2)
    {
        return this.inflater.inflate(R.layout.row_suggestion, null);
    }

    /**
     * Use the search suggestions provider to obtain a live cursor.  This will be called
     * in a worker thread, so it's OK if the query is slow (e.g. round trip for suggestions).
     * The results will be processed in the UI thread and changeCursor() will be called.
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String query = (constraint == null) ? "" : constraint.toString();
        /**
         * for in app search we show the progress spinner until the cursor is returned with
         * the results.
         */
        Cursor cursor = null;
        if (searchView.getVisibility() != View.VISIBLE
                || searchView.getWindowVisibility() != View.VISIBLE) {
            return null;
        }
        try {
            cursor = getSuggestions(searchable, query, QUERY_LIMIT);
            // trigger fill window so the spinner stays up until the results are copied over and
            // closer to being ready
            if (cursor != null) {
                cursor.getCount();
                return cursor;
            }
        } catch (RuntimeException e) {
        }
        // If cursor is null or an exception was thrown, stop the spinner and return null.
        // changeCursor doesn't get called if cursor is null
        return null;
    }

    public Cursor getSuggestions(SearchableInfo searchable, String query, int limit) {

        if (searchable == null) {
            return null;
        }

        String authority = searchable.getSuggestAuthority();
        if (authority == null) {
            return null;
        }

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority)
                .query("") 
                .fragment(""); 

        // if content path provided, insert it now
        final String contentPath = searchable.getSuggestPath();
        if (contentPath != null) {
            uriBuilder.appendEncodedPath(contentPath);
        }

        // append standard suggestion query path
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        // get the query selection, may be null
        String selection = searchable.getSuggestSelection();
        // inject query, either as selection args or inline
        String[] selArgs = null;
        if (selection != null) {    // use selection if provided
            selArgs = new String[] { query };
        } else {                    // no selection, use REST pattern
            uriBuilder.appendPath(query);
        }

        if (limit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(limit));
        }

        Uri uri = uriBuilder.build();

        // finally, make the query
        return mContext.getContentResolver().query(uri, null, selection, selArgs, null);
    }

}