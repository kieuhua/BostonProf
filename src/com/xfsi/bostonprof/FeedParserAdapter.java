package com.xfsi.bostonprof;

import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

public class FeedParserAdapter extends BaseFeedParser {
	static final boolean LOG = false;
	private static String TAG = "FeedparserAdapter: ";	

	static final String RSS = "rss";
	//static final String RSS = "xml";
	public FeedParserAdapter(String feedUrl) {
		super(feedUrl);
	}

	public List<Message> parse() {
		final Message currentMessage = new Message();
		RootElement root = new RootElement(RSS);
		final List<Message> messages = new ArrayList<Message>();
		if (LOG) Log.i(TAG +  " parse() ","before root.getChild");
		
		Element channel = root.getChild(CHANNEL);
		Element item = channel.getChild(ITEM);
	
		item.setEndElementListener(new EndElementListener(){
			public void end() {
				// not sure you need to make a copy before add it to messages??
				// answer is because currentMessage object will be destroy
				// once it leave parse(); however, copy() return Message object
				// it will be around, therefore, you can add it to messages.
				messages.add(currentMessage.copy());
				if (LOG) Log.i(TAG +  " parse() ","after messages.add()");
			}
		});
		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setTitle(body);
				if (LOG) Log.i(TAG +  " parse() ","inside getChild(TITLE)" + body);
			}
		});
		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setLink(body);
				if (LOG) Log.i(TAG +  " parse() ","inside getChild(LINK)" + body);
			}
		});
		item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setDescription(body);
				if (LOG) Log.i(TAG +  " parse() ","inside getChild(DESCRIPTION)" + body) ;
			}
		});
		item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				currentMessage.setDate(body);
			}
		});
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return messages;
	}
}
