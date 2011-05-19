package com.xfsi.bostonprof;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EventDisplay extends ListActivity {
	
	// static final boolean LOG = true;
	static final boolean LOG = false;
    private static String TAG = "EventDisplay";
	
	static final int CURRENT_VIEW_ID = 0;
	static final int SAVED_VIEW_ID = 1;
	static final int DELETE_REQUESTCODE = 10;
	
	static LayoutInflater	mLayoutInflater = null;
	
	static String mapAddress;
	
	public static final int DELETE_ITEM = 0;
	
	public static SQLiteDatabase mDbme;
    public static MyDBAdapter myDBAdapter;
    
    int doubleSave = 0;
    public static boolean deleteflag = false;
    
    public static boolean deleteflag2 = false;
    
    Long mRowId;
    
	TextView mEtypeText;
	TextView mEtitleText;
	TextView mEdateText;
	TextView mElinkText;
	TextView mElocationText;
	TextView mEdescriptionText;
	
	TextView mEtypeText1;
	TextView mEtitleText1;
	TextView mEdateText1;
	TextView mElinkText1;
	TextView mElocationText1;
	TextView mEdescriptionText1;
	
	String etype2;
	String etitle2;
	String edate2;
	String elink2;
	String elocation2;
	String edescription2;

	// use for extra row for delete
	int flagrowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		// if flagId has no value, then its default value is 2, which will list my db events.
		int flagId = extras.getInt("flagId", 2);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
        mLayoutInflater = LayoutInflater.from(this.getBaseContext());
        
        View layout =null; 
        View topbar = null;
        switch (flagId) {
        	case 1: 
        		layout = mLayoutInflater.inflate(R.layout.event_display, null);
        		topbar = layout.findViewById(R.id.topbar);
        		topbar.setBackgroundResource(R.color.magenta);
        		break;
        	case 2:
        		layout = mLayoutInflater.inflate(R.layout.myevents, null);
        		topbar = layout.findViewById(R.id.topbar);
                topbar.setBackgroundResource(R.color.deepskyblue);
        		break;
        	case 3:
        		layout = mLayoutInflater.inflate(R.layout.myevent_display, null);
        		topbar = layout.findViewById(R.id.topbar);
                topbar.setBackgroundResource(R.color.gold);
        		break;
        	case 4:
        		layout = mLayoutInflater.inflate(R.layout.myevents, null);
        		topbar = layout.findViewById(R.id.topbar);
                topbar.setBackgroundResource(R.color.deepskyblue);
                // now handle as same as flagId= 2, list my db
        		//finishActivity(RESULT_OK);
        		//finish();
        		break;
        	default:
        		layout = mLayoutInflater.inflate(R.layout.myevents, null); 
        		topbar = layout.findViewById(R.id.topbar);
                topbar.setBackgroundResource(R.color.deepskyblue);
        }
    
    	// get list view 
    	ListView lv = getListView();
    	lv.setCacheColorHint(R.color.white);
    	lv.setBackgroundResource(R.color.white);
    	lv.setSelector(this.getResources().getDrawable(R.drawable.highlight));
    	lv.setDivider(this.getResources().getDrawable(R.drawable.divider));
    	
    	registerForContextMenu(lv);
    	
    	// this is problem from flagId=4 ?? why, null pointer
    	this.setContentView(layout);
    	if (LOG) Log.v("EventDisplay:onCreate() after setContentView(layout)", "");	

		// Do real work
		mRowId = null;
		myDBAdapter = new MyDBAdapter(this);
	     
	     // open() will create DatabaseHelper class, so it should create db
	     mDbme = myDBAdapter.open();
		
	     // create TextView for all the fields - type, title, date,...
		mEtypeText = (TextView) findViewById(R.id.etype);
		mEtitleText = (TextView) findViewById(R.id.etitle);
		mEdateText = (TextView) findViewById(R.id.edate);
		mElinkText = (TextView) findViewById(R.id.elink);
		mElocationText = (TextView) findViewById(R.id.elocation);
		mEdescriptionText = (TextView) findViewById(R.id.edescription);
		
		// flagId==1 for event detail from Boston event list
		// or flagId==3 for event detail from my event DB
		
		if ((flagId ==1)||(flagId==3)) {
			etype2 = extras.getString(BostonDBAdapter.KEY_ETYPE);
			etitle2 = extras.getString(BostonDBAdapter.KEY_ETITLE);
			edate2 = extras.getString(BostonDBAdapter.KEY_EDATE);
			elink2 = extras.getString(BostonDBAdapter.KEY_ELINK);
			elocation2 = extras.getString(BostonDBAdapter.KEY_ELOCATION);
			mapAddress = elocation2;
			edescription2 = extras.getString(BostonDBAdapter.KEY_EDESCRIPTION);
		
			if (LOG) Log.v(TAG + " :onCreate", "get Bundle successful, next setText to all fields of event.");
			
			mEtypeText.setText(etype2);
			mEtitleText.setText(etitle2);
			mEdateText.setText(edate2);
			mElinkText.setText(elink2);
			mElocationText.setText(elocation2);
			mEdescriptionText.setText(edescription2);
	
			if (LOG) Log.i(TAG + ":onCreate", " setText to all fields successful.");
				
			ImageButton backbtn = (ImageButton) topbar.findViewById(R.id.backbtn);
			backbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDbme.close();
					finish();
				}
			});
			
			ImageButton mapbtn = (ImageButton) topbar.findViewById(R.id.mapbtn);
			mapbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (LOG) Log.i(TAG + ":onCreate mapbtn.setOnClickListner",elocation2);
					startMapActivity(elocation2);
				}
			});
			
			// This button1 displays the type(T),date(D) and location(L) of the event
			ImageButton expandbtn1 = (ImageButton) findViewById(R.id.expandbtn1);
			expandbtn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startTDLActivity();
				}
			});
			
			// This button2 displays the title of the event
			ImageButton expandbtn2 = (ImageButton) findViewById(R.id.expandbtn2);
			expandbtn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startTitleActivity();
				}
			});
			
			// This button3 displays the description of the event
			ImageButton expandbtn3 = (ImageButton) findViewById(R.id.expandbtn3);
			expandbtn3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startDescriptionActivity();
				}
			});

			// This button4 goes to the event link website
			ImageButton expandbtn4 = (ImageButton) findViewById(R.id.expandbtn4);
			expandbtn4.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startLinkSiteActivity();
				}
			});
						
			if (flagId ==1){
				deleteflag = false;
				ImageButton backbtn1 = (ImageButton) topbar.findViewById(R.id.backbtn);
				backbtn1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mDbme.close();
						finish();
					}
				});
				
				ImageView viewing = (ImageView) findViewById(R.id.currentview);
				viewing.setImageResource(R.drawable.curevent);
				
				ImageButton savebtn = (ImageButton) topbar.findViewById(R.id.savebtn);
				savebtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if ( doubleSave == 0) {
							if (LOG) Log.v(TAG + " :onCreate in savebtn setOnClickListner", "");
							BostonEvent myEvent = create_Event();
							if (LOG) Log.i(TAG + " :onMenuItemSelected", "After create_Event()");
							myDBAdapter.saveEvent(myEvent);
							
							toast("The event is saved.");
							doubleSave = 1;
						}else {
							toast("The event is already saved.");
						}
					}
				});
			}
			
			// display detail of the event in my DB
			if (flagId ==3){			
				ImageButton backbtn3 = (ImageButton) topbar.findViewById(R.id.backbtn);
				backbtn3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (deleteflag == true) {
							deleteflag = false;
						}
						mDbme.close();
						finish();
					}
				});
				
				ImageView viewing = (ImageView) findViewById(R.id.currentview);
				viewing.setImageResource(R.drawable.savedevent);
			
				flagrowId = extras.getInt(BostonDBAdapter.KEY_ROWID);
				ImageButton deletebtn = (ImageButton) topbar.findViewById(R.id.deletebtn);
				deletebtn.setOnClickListener(new OnClickListener() {
			
					@Override
					public void onClick(View v) {
						
						delEvent(flagrowId);
						deleteflag = true;
						mDbme.close();
						startMyDB();
					}
				});
			}
			
		} // end of flagId==1 detail from Boston list or flagId=3 => display from my db list
		
		// display the list of events from my events DB
		if (flagId == 2) {
			ImageButton backbtn2 = (ImageButton) topbar.findViewById(R.id.backbtn);
			backbtn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDbme.close();
					finish();
				}
			});
			display_myEvents();
		}
		
		// display the list of events form my events DB
		if (flagId == 4) {
			ImageButton backbtn4 = (ImageButton) topbar.findViewById(R.id.backbtn);
			backbtn4.setOnClickListener(new OnClickListener() {
			
				@Override
				public void onClick(View v) {
					mDbme.close();
					// reset the deleteflag
					deleteflag = false;
					finish();
					//finishActivity(DELETE_REQUESTCODE);
				}
			});	
			display_myEvents();
		}
	}	// end of onCreate()
	
	public void startTDLActivity() {
		Intent i = new Intent(this, TDLDisplay.class);
		i.putExtra(MyDBAdapter.KEY_ETYPE,etype2 );
		i.putExtra(MyDBAdapter.KEY_EDATE,edate2 );
		i.putExtra(MyDBAdapter.KEY_ELOCATION,elocation2 );
		
		if (LOG) Log.i(TAG + "EventDisplay: startTDLActivity ", "");
		this.startActivityForResult(i, RESULT_OK);
	}
	
	public void startTitleActivity() {
		Intent i = new Intent(this, TitleDisplay.class);
		i.putExtra(MyDBAdapter.KEY_ETITLE,etitle2 );
		
		if (LOG) Log.i(TAG + "EventDisplay: startTitleActivity ", "");
		this.startActivityForResult(i, RESULT_OK);
	}
	
	public void startDescriptionActivity() {
		Intent i = new Intent(this, DescriptionDisplay.class);
		i.putExtra(MyDBAdapter.KEY_EDESCRIPTION,edescription2 );
		
		if (LOG) Log.i(TAG + "EventDisplay: startDescriptionActivity ", "");
		this.startActivityForResult(i, RESULT_OK);
	}
	
	public void startLinkSiteActivity() {
		Intent i = new Intent(this, LinkSite.class);
		i.putExtra(MyDBAdapter.KEY_ELINK,elink2 );
		
		if (LOG) Log.i(TAG + "EventDisplay: startLinkSiteActivity ", "");
		this.startActivityForResult(i, RESULT_OK);
	}
	
	public void startMapActivity(String location) {
		
		String mapLocation = "geo:0,0?q=" + location;
		Intent i= new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapLocation));
		//this.startActivityForResult(i, RESULT_OK);
		startActivity(i);
	}
	public void startMyDB() {
    	// need to display my database
		Intent i2 = new Intent(this, EventDisplay.class);
		// add the flagId to indicate the selection of an event detail=1 or my db=2
		// flagId = 4, indicate that this activity is coming from delete my detail event.
		String flagId ="flagId";
		i2.putExtra(flagId, 4);
		this.startActivityForResult(i2, DELETE_REQUESTCODE);
		//this.startActivityFromChild(this,i2, RESULT_OK);
    }
	
	public void delEvent(int flagrowId){
		
		myDBAdapter.deleteEvent(flagrowId);
	}
	
	private BostonEvent create_Event(){
		
		BostonEvent myEvent = new BostonEvent();
		myEvent.seteType(etype2);
		myEvent.seteTitle(etitle2);
		myEvent.seteDate(edate2);
		myEvent.seteLink(elink2);
		myEvent.seteLocation(elocation2);
		myEvent.seteDescription(edescription2);
		
		return myEvent;	
	}
	
	public void display_myEvents() {
		
		Cursor c = mDbme.query(MyDBAdapter.MYDATABASE_TABLE, new String[] {MyDBAdapter.KEY_ROWID, MyDBAdapter.KEY_ETYPE,
    			MyDBAdapter.KEY_ETITLE, MyDBAdapter.KEY_EDATE, MyDBAdapter.KEY_ELINK, 
    		MyDBAdapter.KEY_ELOCATION, MyDBAdapter.KEY_EDESCRIPTION },null,null,null,null,null);
    	
    	c.moveToFirst();
    	int dbcnt = c.getCount();
    	if (LOG) Log.i(TAG +  " :display_myEvents() My database count dbcnt=", String.valueOf(dbcnt));
    	
    	//String[] from1 = new String[] { displayField} ;
    	String[] from1 = new String[] { MyDBAdapter.KEY_ETITLE} ;
    	
    	// and an array of the fields we want to bind those fields to 
    	int[] to1 = new int[] {R.id.text1};
    	  	
    	// Now create an array adapter and set it to display using our row
    	SimpleCursorAdapter events = new SimpleCursorAdapter(this, R.layout.events_row, c, from1, to1);
    	
    	ListView lv1 = getListView();
    	lv1.setCacheColorHint(R.color.aquamarine);
    	lv1.setBackgroundResource(R.color.aquamarine);
    	lv1.setSelector(this.getResources().getDrawable(R.drawable.highlight));
    	lv1.setDivider(this.getResources().getDrawable(R.drawable.divider));
    	setListAdapter(events); 	
	}
	
	// When you click on an event in my saved events list, it will retrieve that event 
	// from my events database, get all the fields, create a new Intent object, 
	// add all fields to the Intent object, and start activity to display my event.
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (LOG) Log.i(TAG + " :onListItemClick: before putExtra(..ROWID= )", String.valueOf(id));
		mDbme = myDBAdapter.open();
		
		// get all fields of the clicked event and send them to EventDisplay activity
		Cursor mCursor1 =
		mDbme.query(true, MyDBAdapter.MYDATABASE_TABLE, new String[] {MyDBAdapter.KEY_ROWID, MyDBAdapter.KEY_ETYPE, 
				 MyDBAdapter.KEY_ETITLE, MyDBAdapter.KEY_EDATE, MyDBAdapter.KEY_ELINK, MyDBAdapter.KEY_ELOCATION, 
			 MyDBAdapter.KEY_EDESCRIPTION}, MyDBAdapter.KEY_ROWID + "=" + id, null,
					 null, null, null, null);
		
		mCursor1.moveToFirst();
		if (LOG) Log.i(TAG + " :onListItemClick: ", "query event w/rowId successful; next w/ all fields column Index");
		
		int etypeId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_ETYPE);
		int etitleId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_ETITLE);
		int edateId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_EDATE);
		int elinkId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_ELINK);
		int elocationId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_ELOCATION);
		int edescriptionId1 = mCursor1.getColumnIndex(MyDBAdapter.KEY_EDESCRIPTION);
		
		if (LOG) Log.i(TAG + " :onListItemClick: ", "get all fields column Index successful; next w/ all fields string");
		
		String etype1 = mCursor1.getString(etypeId1);
		String etitle1 = mCursor1.getString(etitleId1);
		String edate1 = mCursor1.getString(edateId1);
		String elink1 = mCursor1.getString(elinkId1);
		String elocation1 = mCursor1.getString(elocationId1);
		String edescription1 = mCursor1.getString(edescriptionId1);
		
		//create another Activity MyEventDisplay
		
		Intent i = new Intent(this, EventDisplay.class);
		
		String flagId ="flagId";
		// flagId =3 is display an event in my db
		i.putExtra(flagId, 3);
		
		// need to put rowId to my event display, in case user want to delete it
		i.putExtra(MyDBAdapter.KEY_ROWID, (int)id);
		
		i.putExtra(MyDBAdapter.KEY_ETYPE,etype1 );
		i.putExtra(MyDBAdapter.KEY_ETITLE,etitle1 );
		i.putExtra(MyDBAdapter.KEY_EDATE,edate1 );
		i.putExtra(MyDBAdapter.KEY_ELINK,elink1 );
		i.putExtra(MyDBAdapter.KEY_ELOCATION,elocation1 );
		i.putExtra(MyDBAdapter.KEY_EDESCRIPTION,edescription1 );
		
		mDbme.close();
		if (LOG) Log.i(TAG + " :onListItemClick: ", "put all fields in Intent successful; next call startActivity()");
		this.startActivityForResult(i, DELETE_REQUESTCODE);
		//this.startActivityForResult(i, RESULT_OK);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//mDbme = belDatastoreImplme.open();
    	// I have to make this work.
    	//display_myEvents();
    	//if (requestCode != DELETE_REQUESTCODE){
    	//	finish();
    	//}
    	finish();
    }
    
    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
     }
}


