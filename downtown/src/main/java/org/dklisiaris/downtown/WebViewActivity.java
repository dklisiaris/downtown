package org.dklisiaris.downtown;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.dklisiaris.downtown.R;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends ActionBarActivity {
	private WebView webView;
	private String website;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.webview);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
        // getting attached intent data
        website = i.getStringExtra("website");
        
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		final ActionBarActivity activity = this;
		 webView.setWebChromeClient(new WebChromeClient() {
		   public void onProgressChanged(WebView view, int progress) {
		     // Activities and WebViews measure progress with different scales.
		     // The progress meter will automatically disappear when we reach 100%
		     activity.setProgress(progress * 1000);
		   }
		 });
		 webView.setWebViewClient(new WebViewClient() {
		   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		     Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
		   }
		 });

		 webView.loadUrl(website);		
 
	}
	
	@Override
	public void onBackPressed() {
	    if (webView.canGoBack()) {
	        webView.goBack();
	        return;
	    }
	    // Otherwise defer to system default behavior.
	    super.onBackPressed();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
            
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    } 
}
