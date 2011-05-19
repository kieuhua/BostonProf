package com.xfsi.bostonprof;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DescriptionDisplay extends Activity {
	
	private static final boolean LOG = true;
    private static final String TAG = "DescriptionDisplay";

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
       
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.descriptiondisplay);
        
        Bundle extras = getIntent().getExtras();
		
        // retrieve description of the event
        String description = extras.getString(MyDBAdapter.KEY_EDESCRIPTION);
		
        if (LOG) Log.i(TAG + "onCreate: description= ", description);
        
        TextView descriptionTxtV = (TextView) findViewById(R.id.descriptionbody);
        
        descriptionTxtV.setText(description);
        
        ImageButton backbtn = (ImageButton) findViewById(R.id.descriptionbtn);
		backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
