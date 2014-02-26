package com.pitz.findmybus.model;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailsList {

	private int rowsAffected;
	private List<RouteTimetable> rows = new ArrayList<RouteTimetable>();
	
	public List<RouteTimetable> getRouteList(){
		return rows;
	}
	
	public int getRowsAffected(){
		return rowsAffected;
	}
}
