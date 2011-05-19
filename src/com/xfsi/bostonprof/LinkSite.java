package com.xfsi.bostonprof;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class LinkSite extends Activity {
	private static final boolean LOG = true;
    private static final String TAG = "LinkSite";
    
    private WebView mWeblink;

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.linksite);
        
        Bundle extras = getIntent().getExtras();
		
        // retrieve title of the event
        String link = extras.getString(MyDBAdapter.KEY_ELINK);
		
        if (LOG) Log.i(TAG + "onCreate: link= ", link);
        
        // need start the link website
        
        
        
        mWeblink = (WebView) findViewById(R.id.weblink);

        WebSettings webSettings = mWeblink.getSettings();
       // webSettings.setSavePassword(false);
        //webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        
       // webSettings.setSupportZoom(false);

        //mWeblink.setWebChromeClient(new MyWebChromeClient());

       // mWeblink.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        mWeblink.loadUrl(link);

	}
}
