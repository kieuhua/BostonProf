package com.xfsi.bostonprof;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TDLDisplay extends Activity {
	private static final boolean LOG = true;
    private static final String TAG = "TitleDisplay";

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.tdldisplay);
        
        Bundle extras = getIntent().getExtras();
		
        // retrieve type, date and location of the event
        String type = extras.getString(MyDBAdapter.KEY_ETYPE);
        String date = extras.getString(MyDBAdapter.KEY_EDATE);
        String location = extras.getString(MyDBAdapter.KEY_ELOCATION);
		
        String tdl = type+date+location;
        if (LOG) Log.i(TAG + "onCreate: tdl= ", tdl);
        
       TextView typeTxtV = (TextView) findViewById(R.id.typebody);
        TextView dateTxtV = (TextView) findViewById(R.id.datebody);
        TextView locationTxtV = (TextView) findViewById(R.id.locationbody);
        
        typeTxtV.setText(type);
        dateTxtV.setText(date);
        locationTxtV.setText(location);
        
        ImageButton backbtn = (ImageButton) findViewById(R.id.tdlbtn);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}	

}
