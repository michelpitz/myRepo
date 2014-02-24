package com.pitz.findmybus.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteList {

	private int rowsAffected;
	private List<Route> rows = new ArrayList<Route>();
	
	public List<Route> getRouteList(){
		return rows;
	}
	
	public int getRowsAffected(){
		return rowsAffected;
	}
}
