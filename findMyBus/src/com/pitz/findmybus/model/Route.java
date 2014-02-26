package com.pitz.findmybus.model;

public class Route {

	String id = "";
	String agencyId = "";
	String shortName = "";
	String longName = "";
	String lastModifiedDate = "";
	
	public String getId() {
		return id;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public String getShortName() {
		return shortName + ". ";
	}

	public String getLongName() {
		return longName;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public String toString(){
		return " " + longName;
	}
}
