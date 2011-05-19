package com.xfsi.bostonprof;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.content.Context;
import android.util.Log;

public class EventBELFeed {
	
	static final boolean LOG = false;
	private static String TAG = "EventBELFeed";
	
	static String feedUrl = "http://www.bostoneventslist.com/rss.xml";
	
    // this is List of Message from Boston event list
	private List<Message> messages;
	
	static BostonDBAdapter bostonDBAdapter;
	
	public EventBELFeed(Context context, BostonDBAdapter bDBA) {
		
       bostonDBAdapter = bDBA;
        
        loadFeed();
	}
	
	public void loadFeed(){
    	try{
    		// this is where the feedname pass to BaseFeedParser
    		if (LOG) Log.i(TAG +  " loadFeed()",feedUrl);
    		FeedParser parser = new FeedParserAdapter(feedUrl);
	    	long start = System.currentTimeMillis();
	    	messages = parser.parse();
	    	long duration = System.currentTimeMillis() - start;
	    	if (LOG) Log.i(TAG + " loadFeed()" + " Parser duration=", String.valueOf(duration));
	    	
	    	if (LOG) Log.i(TAG + " loadFeed(): The size of messages", String.valueOf(messages.size()));
	    
	    	// put messages into database
	    	writeToDatabase();
	    	
    	} catch (Throwable t){
    		Log.e(TAG + " loadFeed()"+ "Exception",t.getMessage(),t);
    	}
    	
    }
	
	private void writeToDatabase() {
		
		try {
			// for each message, I need to get eType, eTitle, eDate, eLink, eLocation, eDescription
			for (Message msg: messages){	
				
				// I need to parse this edescription to get all the event fields
				String edescription = msg.getDescription();
				
				if (LOG) Log.i(TAG + " writeToDatabase() after msg.getDescription edescription= ",edescription);
			
				// get most of the event fields and return a Event object
				BostonEvent eBostonEvent = parseHtmledescription(edescription);
				
				// This etitle is eTitle for the event
				String etitle = msg.getTitle();
				eBostonEvent.eTitle = etitle;
				// I am using the link in the message, not the link in the description body.
				URL link = msg.getLink();
				eBostonEvent.eLink = link.toString();
            
				//long okInsertEvent mDbHelper.saveEvent(eBELStoredEvent);
				long okInsertEvent = bostonDBAdapter.saveEvent(eBostonEvent);
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 	
	}

	public BostonEvent parseHtmledescription(String edescription)
	throws XPatherException, IOException {

		BostonEvent cBELStoredEvent = new BostonEvent();
	
		String EDATE_XPATH = "(//div[1])";
	
		String ETYPE_XPATH = "(//div[3]/div)";
		
		// I am using the link from the message.link, not the link in description body
		//String ELINK_XPATH = "(//a[@href])";
		String ELINK_XPATH = "(//[a !=''] )";
		
		// skip to 5 div element, go into child /div, take the value of the grand child /div	
		String ELOCATION_XPATH = "(//div[5]/div/div)";
		
		String EDESCRIPTION_XPATH = "//p";
	
		TagNode node;
	
		// create HtmlCleaner object and set CleanerProperties
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
	
		node = cleaner.clean(edescription);
		// if (LOG) Log.i(TAG+ " : after cleaner.clean() edescription", edescription);
	
		Object[] eDate_nodes = node.evaluateXPath(EDATE_XPATH);  
		Object[] eType_nodes = node.evaluateXPath(ETYPE_XPATH);
		Object[] eLink_nodes = node.evaluateXPath(ELINK_XPATH);
		Object[] eLocation_nodes = node.evaluateXPath(ELOCATION_XPATH);
		Object[] eDescription_nodes = node.evaluateXPath(EDESCRIPTION_XPATH);
		
		String date;
		String type;
		String title;
		String link;
		String location;
		String description;
		
		if (eDate_nodes.length == 0) {
			date = "No date";
		}else {
			TagNode tag2 = (TagNode)eDate_nodes[0];
			date = tag2.getChildren().iterator().next().toString().trim();
		}
	
		if (eType_nodes.length == 0){
			type = "No type";
		}else {
			TagNode tag3 = (TagNode)eType_nodes[0];
			type = tag3.getChildren().iterator().next().toString().trim();
		}
	
		if (eLink_nodes.length == 0) {
			link = "No Link";
		}else {
			TagNode tag4 = (TagNode)eLink_nodes[0];
			link = tag4.getChildren().iterator().next().toString().trim();
			//link ="No Link";
		}
	
		if (eLocation_nodes.length == 0) {
			location = "No location";
		}else {
			TagNode tag5 = (TagNode)eLocation_nodes[0];
			location = tag5.getChildren().iterator().next().toString().trim();
			//location = "No Location";
		}
		
		if (eDescription_nodes.length == 0 ){
			description = "No description";
		}else{
			TagNode tag = (TagNode)eDescription_nodes[0];
			description = tag.getChildren().iterator().next().toString().trim();
		}
	
		// Now set BELStoredEvent object
		cBELStoredEvent.seteDate(date);
		cBELStoredEvent.seteType(type);
		cBELStoredEvent.seteLink(link);
		cBELStoredEvent.seteLocation(location);
		cBELStoredEvent.seteDescription(description);
	
		return cBELStoredEvent;
	}
	

}
