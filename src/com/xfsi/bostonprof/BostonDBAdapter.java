package com.xfsi.bostonprof;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * @author Kieu Hua
 *
 */
public class BostonDBAdapter implements BaseDB
{
	private static final String TAG="BostonDBAdapter";
	static final boolean LOG = false;

	public static final String KEY_ROWID = "_id";
	/**
	 * Database creation sql statement
	 */
	
	public static final String DATABASE_NAME = "data";
	public static final String DATABASE_TABLE = "events";
	public static final String DATABASE_CREATE = "create table events (_id integer primary key autoincrement, "
									+ "etype text not null, etitle text not null, edate text not null, " +
											"elink text not null, elocation text not null, edescription text not null);";
	
	public static final String DATABASE_DELETE = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
	
	private final Context mCtx;

	private DatabaseHelper mDbHelper;
	public static SQLiteDatabase mDb;
	private static final int DATABASE_VERSION = 2;
	
	public Cursor mEventsCursor;		// m denotes a member field 
	
public static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
			//Cursor mCursor = getDatabaseCursor(db);	
		}
		
		@Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (LOG) Log.w(TAG + DBTAG, ": DatabaseHlper: Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS events");
            onCreate(db);
        }
	}
    
    
    /**
	 * Construtor - takes the context to allow the database to be opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public BostonDBAdapter(Context ctx){
		//I need the context to create the DatabaseHelper in open()
		this.mCtx = ctx;	
	}
	
    /**
	 * Open the events database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to 
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an initialization
	 * 	call)
	 * 
	 * @throws SQLException if the database could be neither opened or created
	 */
	public SQLiteDatabase open() throws SQLException {
			
			// the application should create BELDatastore once; therefore this will be
			// called just once.
			mDbHelper = new DatabaseHelper(mCtx);
			if (LOG) Log.d(TAG + " : open()", "mDbHelper = " + mDbHelper);
	
			//Create or open the database for read+write. The database is cached, when it is opened.
			// it may fail to open or to write to database, but with retry it may success.
			mDb = mDbHelper.getWritableDatabase();
			if (LOG) Log.d(TAG + " :open()", "mDb = " + mDb);

		return mDb;
	}
	
	/**
	 * Create a new event using the title, location and time provided. If the event is 
	 * successfully create return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title 
	 * @param location
	 * @param time
	 * @return rowId or -1 if failed
	 */
	public long saveEvent(BostonEvent bostonEvent) {
	
		// this ContentValues class is used to store a set of values that Contentresolver can process.
		 ContentValues initialValues = new ContentValues();
		 initialValues.put(KEY_ETYPE, bostonEvent.eType);
		 initialValues.put(KEY_ETITLE, bostonEvent.eTitle);
		 initialValues.put(KEY_EDATE, bostonEvent.eDate);
		 initialValues.put(KEY_ELINK, bostonEvent.eLink);
		 initialValues.put(KEY_ELOCATION, bostonEvent.eLocation);
		 initialValues.put(KEY_EDESCRIPTION, bostonEvent.eDescription);
		 
		 return mDb.insert(DATABASE_TABLE, null, initialValues);
	 }
	
	public boolean deleteEvent(int rowId){
		
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;

	}
	
	public Cursor getDatabaseCursor(SQLiteDatabase mDb) {
		Cursor c =mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ETYPE,KEY_ETITLE, KEY_EDATE, KEY_ELINK, KEY_ELOCATION, KEY_EDESCRIPTION },null,null,null,null,null);
		mEventsCursor = c;
		return mEventsCursor;
	}
	
	public void deleteDatabase(SQLiteDatabase mDb) {
		// drop the database 'events'
		mDb.execSQL(DATABASE_DELETE);
		mDb.execSQL(DATABASE_CREATE);	
	}
	
	/**
	  * Return a Cursor over the list of all events in the database
	  * 
	  * @return Cursor over all events
	  * @throws SQLException if Event could not be found/retrieve
	  */
	 //public Cursor getAllStoredEvents() {
	public List<BostonEvent> getAllStoredEvents() throws SQLException {
		Cursor c =mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ETYPE,KEY_ETITLE, KEY_EDATE, KEY_ELINK, KEY_ELOCATION, KEY_EDESCRIPTION },null,null,null,null,null);
		
		//create the BELStoredEvent to store the result to return.
		List<BostonEvent> belStoredEventList = new ArrayList<BostonEvent>();
		
		// With the cursor get the count of events in the database.
		int eCount = c.getCount();
		
		// Move the cursor to the first event in the database.
		boolean bFirst = c.moveToFirst();
		if (bFirst != true) {
			if (LOG) Log.v(TAG, " Can't move the cursor to the first event");
			//make a toast
		}
		
		for (int i=0; i< eCount; i++) {	
		
			//create a new BELStoredEvent to store one Event from database
			BostonEvent oneEvent = new BostonEvent();
			
			// fill BELStoredEvent object with the content from the database
			// passing arguments are current position of cursor, and the index in the database.
			oneEvent = getBostonEvent(i);
			if (oneEvent ==null){
				if (LOG) Log.v(TAG, " Can't create a BELStoredEvent from database");
				//make a toast
			}
			
			// add the BELStoredEvent to the List<..>
			belStoredEventList.add(oneEvent);
			
			boolean	bNext = c.moveToNext();
			if (bNext != true) {
				if (LOG) Log.v(TAG, " Can't move the cursor to the next event");
				//make a toast
			}		
		}
		return belStoredEventList;	
	}
	
	/** 
	 * Retrieve content from database at cursor position, then create and return BELStoredEvent object
	 * @param Cursor c of database
	 * @param int rowId id of event to retrieve
	 * @return a BELStoredEvent object
	 */
	public BostonEvent getBostonEvent( int rowId) {
		BostonEvent belStoredEvent = new BostonEvent();
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ETYPE, KEY_ETITLE, KEY_EDATE,
			 KEY_ELINK, KEY_ELOCATION, KEY_EDESCRIPTION}, KEY_ROWID + "=" + rowId, null,
			 null, null, null, null);
		
		// need to move cursor to first
		mCursor.moveToFirst();
		
		if (LOG) Log.i("BELDatastoreImpl getBELstoredEvent(): ", "query event w/rowId successful; next w/ cursor all fields column Index");
		
		int etypeId = mCursor.getColumnIndex(KEY_ETYPE);
		int etitleId = mCursor.getColumnIndex(KEY_ETITLE);
		int edateId = mCursor.getColumnIndex(KEY_EDATE);
		int elinkId = mCursor.getColumnIndex(KEY_ELINK);
		int elocationId = mCursor.getColumnIndex(KEY_ELOCATION);
		int edescriptionId = mCursor.getColumnIndex(KEY_EDESCRIPTION);
		
		if (LOG) Log.i(TAG + " getBELstoredEvent(): ", "get all fields column Index successful; next w/ cursor all fields string");
		
		String etype = mCursor.getString(etypeId);
		String etitle = mCursor.getString(etitleId);
		String edate = mCursor.getString(edateId);
		String elink = mCursor.getString(elinkId);
		String elocation = mCursor.getString(elocationId);
		String edescription = mCursor.getString(edescriptionId);	
		
		if (LOG) Log.i(TAG + " getBELstoredEvent(): ", "get all fields string successful; next set all fields in BELStoredEvent object");

		belStoredEvent.seteType(etype);
		belStoredEvent.seteTitle(etitle);
		belStoredEvent.seteDate(edate);
		belStoredEvent.seteLink(elink);
		belStoredEvent.seteLocation(elocation);
		belStoredEvent.seteDescription(edescription);
		
		if (LOG) Log.i(TAG + " getBELstoredEvent(): ", "get all fields string successful; next set all fields in BELStoredEvent object");
			
		return belStoredEvent;
	}	
	
	public void closeDB() {
		mDb.close();
		return;
	}

}
