package com.xfsi.bostonprof;

/** This class contains the important fields of a Boston event with their setters and getters
 * Event type 
 * Event title
 * Event date
 * Event link
 * Event location
 * Event description
 * 
 * @author Kieu Hua
 */

public class BostonEvent 
{
	
	 String eType;
	 String eTitle;
	 String eDate;
	 String eLink;
	 String eLocation;
	 String eDescription;

	// getters for BELStoredEvent object.
	public String geteType(){
		return eType;
	}
	public String geteTitle(){
		return eTitle;
	}
	public String geteDate(){
		return eDate;
	}
	public String geteLink(){
		return eLink;
	}
	public String geteLocation(){
		return eLocation;
	}
	public String geteDescription(){
		return eDescription;
	}
	
	// setters for BELStoredEvent object.
	public void seteType(String etype){
		this.eType = etype;
	}
	public void seteTitle(String etitle){
		this.eTitle = etitle;
	}
	public void seteDate(String edate){
		this.eDate = edate;
	}
	public void seteLink(String elink){
		this.eLink = elink;
	}
	public void seteLocation(String elocation){
		this.eLocation = elocation;
	}
	public void seteDescription(String edescription) {
		this.eDescription = edescription;
	}

}
