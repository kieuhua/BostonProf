package com.xfsi.bostonprof;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class TitleDisplay extends Activity {
	
	private static final boolean LOG = true;
    private static final String TAG = "TitleDisplay";

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.titledisplay);
        
        Bundle extras = getIntent().getExtras();
		
        // retrieve title of the event
        String title = extras.getString(MyDBAdapter.KEY_ETITLE);
		
        if (LOG) Log.i(TAG + "onCreate: title= ", title);
        
        TextView titleTxtV = (TextView) findViewById(R.id.titlebody);
        
        titleTxtV.setText(title);
        ImageButton backbtn = (ImageButton) findViewById(R.id.titlebtn);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
