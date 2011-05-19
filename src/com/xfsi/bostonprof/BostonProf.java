package com.xfsi.bostonprof;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

public class BostonProf extends ListActivity {
	
	private static String TAG = "BostonProf";
    static final boolean LOG = false;
    
	public static SQLiteDatabase mDb1;
    public static BostonDBAdapter bostonDBAdapter1;
    public static EventBELFeed eventBELFeed1;
    
	static final int CURRENT_VIEW_ID = 0;
	static final int SAVED_VIEW_ID = 1;
    
    static LayoutInflater	mLayoutInflater = null;
    
    public enum EventField {
    	TYPE, TITLE, DATE, LINK, LOCATION, DESCRIPTION;
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        mLayoutInflater = LayoutInflater.from(this.getBaseContext());
        View layout = mLayoutInflater.inflate(R.layout.topbar_list, null);
        
        View topbar = layout.findViewById(R.id.topbar);
        topbar.setBackgroundResource(R.color.lawngreen);
        
    	// get list view 
    	ListView lv = getListView();
    	lv.setCacheColorHint(R.color.violetred3);
    	lv.setBackgroundResource(R.color.violetred3);
    	lv.setSelector(this.getResources().getDrawable(R.drawable.highlight));
    	lv.setDivider(this.getResources().getDrawable(R.drawable.divider));
    		
    	registerForContextMenu(lv);
    	
    	this.setContentView(layout);
    	ImageView viewing = (ImageView) findViewById(R.id.currentview);
    	viewing.setImageResource(R.drawable.currentevents);
    	
		ImageButton reloadbtn = (ImageButton) topbar.findViewById(R.id.reloadbtn);
		reloadbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				toast("Reloading The Boston Professional Event List.");
				reloadBEL();
			}
		});
		
		ImageButton forwardbtn = (ImageButton) topbar.findViewById(R.id.forwardbtn);
		forwardbtn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				displayMyDB();
			}
		});
    	
    	// START DO REAL WORKS
           
       bostonDBAdapter1 = new BostonDBAdapter(this);
       // open() will create DatabaseHelper class, so it should create db
      mDb1 = bostonDBAdapter1.open();
      bostonDBAdapter1.deleteDatabase(mDb1);
       
       eventBELFeed1 = new EventBELFeed(this,bostonDBAdapter1);
       
       Cursor c = mDb1.query(BostonDBAdapter.DATABASE_TABLE, new String[] {BostonDBAdapter.KEY_ROWID, BostonDBAdapter.KEY_ETYPE,
   			BostonDBAdapter.KEY_ETITLE, BostonDBAdapter.KEY_EDATE, BostonDBAdapter.KEY_ELINK, 
   			BostonDBAdapter.KEY_ELOCATION, BostonDBAdapter.KEY_EDESCRIPTION },null,null,null,null,null);
   	
   		c.moveToFirst();
           
        if (LOG) Log.v(TAG +"onCreate", "new BELDatastoreImpl1() successful; next fillData()");
        
        fillData(BostonDBAdapter.KEY_ETITLE);
        if (LOG) Log.v(TAG+"onCreate", "fillData() successful; end of onCreate");
        
    } // end of onCreate()
    
    public void reloadBEL() {
    	
    	/** at the moment, if the database is not empty, then don't need to reload, 
    	 * because, there is risk in reload, if you don't need to reload.
    	 * Everytime you reload you delete the Boston Events database, and
    	 * if you don't get a network connection to BostonEventlist, then you have 
    	 * no entry in the Boston Events database.
    	 * This feature can be changed in the future.
    	 */
    	if (mDb1 == null ) {
    		new EventBELFeed(this,bostonDBAdapter1);
    		fillData(BostonDBAdapter.KEY_ETITLE);	
    	}
    	return;
    }
    
    public void displayMyDB() {
    	
		Intent i2 = new Intent(this, EventDisplay.class);
		// add the flagId to indicate the selection of an event detail=1 or my db=2
		String flagId ="flagId";
		i2.putExtra(flagId, 2);
		this.startActivityForResult(i2, RESULT_OK);
    }
	
	/*
	 * Need to write onListItemClick to display all fields of specified event
	 * 
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Cursor mCursor1 =
		mDb1.query(true, BostonDBAdapter.DATABASE_TABLE, new String[] {BostonDBAdapter.KEY_ROWID, BostonDBAdapter.KEY_ETYPE, 
				 BostonDBAdapter.KEY_ETITLE, BostonDBAdapter.KEY_EDATE, BostonDBAdapter.KEY_ELINK, BostonDBAdapter.KEY_ELOCATION, 
			 BostonDBAdapter.KEY_EDESCRIPTION}, BostonDBAdapter.KEY_ROWID + "=" + id, null,
					 null, null, null, null);
		
		mCursor1.moveToFirst();
		
		int etypeId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_ETYPE);
		int etitleId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_ETITLE);
		int edateId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_EDATE);
		int elinkId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_ELINK);
		int elocationId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_ELOCATION);
		int edescriptionId1 = mCursor1.getColumnIndex(BostonDBAdapter.KEY_EDESCRIPTION);
		
		String etype1 = mCursor1.getString(etypeId1);
		String etitle1 = mCursor1.getString(etitleId1);
		String edate1 = mCursor1.getString(edateId1);
		String elink1 = mCursor1.getString(elinkId1);
		String elocation1 = mCursor1.getString(elocationId1);
		String edescription1 = mCursor1.getString(edescriptionId1);
			
		if (LOG) Log.i(TAG + "onListItemClick: ", "get all fields string successful; next put all fields in Intent");
		
		Intent i = new Intent(this, EventDisplay.class);
		// add the flagId to indicate the selection of an event detail=1 or my db=2
		String flagId ="flagId";
		i.putExtra(flagId, 1);
		i.putExtra(BostonDBAdapter.KEY_ETYPE,etype1 );
		i.putExtra(BostonDBAdapter.KEY_ETITLE,etitle1 );
		i.putExtra(BostonDBAdapter.KEY_EDATE,edate1 );
		i.putExtra(BostonDBAdapter.KEY_ELINK,elink1 );
		i.putExtra(BostonDBAdapter.KEY_ELOCATION,elocation1 );
		i.putExtra(BostonDBAdapter.KEY_EDESCRIPTION,edescription1 );
		
		if (LOG) Log.i(TAG + "onListItemClick: ", "put all fields in Intent successful; next call startActivity()");
		this.startActivityForResult(i, RESULT_OK);
	}
    
    /*
     * fillData(String displayField) 
     * displayFields - etype, etile, edate, elink, elocation, edescription
     */
    private void fillData(String displayField) {
    	
    	if (mDb1== null){
    		// What should I do
    		if (LOG) Log.i(TAG + "fillData: ", "mDb1 is null.");
    	}
    	
    	Cursor c = mDb1.query(BostonDBAdapter.DATABASE_TABLE, new String[] {BostonDBAdapter.KEY_ROWID, BostonDBAdapter.KEY_ETYPE,
    			BostonDBAdapter.KEY_ETITLE, BostonDBAdapter.KEY_EDATE, BostonDBAdapter.KEY_ELINK, 
    			BostonDBAdapter.KEY_ELOCATION, BostonDBAdapter.KEY_EDESCRIPTION },null,null,null,null,null);
    	
    	c.moveToFirst();	
    	int dbcnt = c.getCount();
    	if (LOG) Log.i(TAG + "fillData() get db count dbcnt=", String.valueOf(dbcnt));
    	
    	String[] from1 = new String[] { displayField} ;
    	
    	// and an array of the fields we want to bind those fields to 
    	int[] to1 = new int[] {R.id.text1};
    	  	
    	// Now create an array adapter and set it to display using our row
    	SimpleCursorAdapter events = new SimpleCursorAdapter(this, R.layout.events_row, c, from1, to1);
    	
    	ListView lv1 = getListView();
    	lv1.setCacheColorHint(R.color.violetred3);
    	lv1.setBackgroundResource(R.color.violetred3);
    	lv1.setSelector(this.getResources().getDrawable(R.drawable.highlight));
    	lv1.setDivider(this.getResources().getDrawable(R.drawable.divider));
    	
    	setListAdapter(events);
    	//c.close();	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (LOG) Log.i(TAG + "onActivityResult: before setImageResource()","");
    
    	View layout1 = mLayoutInflater.inflate(R.layout.topbar_list, null);
    	//ImageView viewing1 = (ImageView) findViewById(R.id.currentview);
    	//viewing1.setImageResource(R.drawable.currentevents);
    	this.setContentView(layout1);
    	
    	if (LOG) Log.i(TAG + "onActivityResult: before fillData() ", "");
    	fillData(BostonDBAdapter.KEY_ETITLE);
    }
    
   @Override
   public void finish() {
    // this.belDatastoreImpl1.closeDB(); why not???
      super.finish();
   }
   
   private void toast(String s) {
       Toast.makeText(this, s, Toast.LENGTH_LONG).show();
     }
    
}
