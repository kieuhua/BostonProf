package com.xfsi.bostonprof;
import java.util.List;

/*
 * Implement this FeedParser to return a list of Message, i.e a list of event.
 */
public interface FeedParser {
	List<Message> parse();
}
