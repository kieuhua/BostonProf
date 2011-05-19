/**
 * @author Kieu Hua
 */
package com.xfsi.bostonprof;

import java.util.List;

public interface BaseDB
{
	public static final String KEY_ETYPE = "etype";
	public static final String KEY_ETITLE = "etitle";
	public static final String KEY_EDATE = "edate";
	public static final String KEY_ELINK = "elink";
	public static final String KEY_ELOCATION = "elocation";
	public static final String KEY_EDESCRIPTION = "edescription";
	
	public static final String DBTAG ="SQLiteDatabase";
	
	
    /**
     * Save event to database
     */
    public long saveEvent( BostonEvent event );
    
    /**
     * Retrieves all saved events
     */
    public List<BostonEvent> getAllStoredEvents();
    
	
}
