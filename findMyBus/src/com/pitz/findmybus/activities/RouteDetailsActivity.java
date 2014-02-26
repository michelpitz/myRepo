package com.pitz.findmybus.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pitz.findmybus.CustomDetailsAdapter;
import com.pitz.findmybus.R;
import com.pitz.findmybus.StaticConstants;
import com.pitz.findmybus.controller.BaseDao;
import com.pitz.findmybus.model.RouteDetailsList;
import com.pitz.findmybus.model.RouteTimetable;

public class RouteDetailsActivity extends Activity 
{
	final String URL_ROUTE_DETAILS = "https://dashboard.appglu.com/v1/queries/findDeparturesByRouteId/run";
	
	CustomDetailsAdapter customAdapter;
	ExpandableListView listView;
	ProgressDialog progressDialog;
	List<String> groupList = new ArrayList<String>();
	HashMap<String, List<String>> collection = new HashMap<String, List<String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_details);

		
		String routeName = getIntent().getStringExtra("routeName");
		String routeId = getIntent().getStringExtra("routeId");
		((TextView)findViewById(R.id.routeDetails_name)).setText("Route: " + routeName);
		
		
		createAdapter();
		getDetails(routeId);
	}
	
	private void createAdapter()
	{
		listView = (ExpandableListView) findViewById(R.id.routeDetails_list);
		customAdapter = new CustomDetailsAdapter(getApplicationContext(), groupList, collection); 
		listView.setAdapter(customAdapter);
	}
	
	private void getDetails(String routeId)
	{
		try {
			RouteDetailsDao routeDetailsDao = new RouteDetailsDao(getApplicationContext(), routeId);
			routeDetailsDao.execute(this.URL_ROUTE_DETAILS);
		}
		catch (Exception e) {
			Log.d("DAO", "Connection error");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.route_details, menu);
		return true;
	}

	class RouteDetailsDao extends AsyncTask<String, Integer, RouteDetailsList>
	{
		Context context = null;
		Gson gson = new Gson();
		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
		private final String paramKey = "routeId";
		
		public RouteDetailsDao(Context context, String value){
			this.context = context;
			this.params.add(new BasicNameValuePair(paramKey, value));
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(RouteDetailsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMax(100);
			progressDialog.setTitle("Please wait");
			progressDialog.setMessage("Fetching results");
			progressDialog.show();
		}
		
		@Override
		protected RouteDetailsList doInBackground(String... urls) 
		{
			Gson gson = new Gson();
			RouteDetailsList list = null;
			int count = urls.length;
			
			for (int i = 0; i < count; i++)
			{
				try {
					BaseDao dao = new BaseDao(urls[i]);
					
					for (NameValuePair p : params) {
						dao.addParam(p.getName(), p.getValue());
					}

					try {
						dao.executePost();
					} catch (Exception e) {
						Log.d("ExecutePost fault: ", e.getMessage());
					}
					
					String json = dao.getResponse();
					Log.d("RESULT", json);
					
					list = gson.fromJson(json, RouteDetailsList.class);

				} catch (Exception e) {
				}
			}
			
			return list;
		}
		
		@Override
		protected void onPostExecute(RouteDetailsList list) 
		{
			progressDialog.dismiss();

			
			if(list.getRouteList() != null && list.getRouteList().size() == 0){
				Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
				return;
			}
			
			int count = list.getRouteList().size();
			List<String> weekDays = new ArrayList<String>();
			List<String> saturday = new ArrayList<String>();
			List<String> sunday = new ArrayList<String>();
			
			for (int i = 0; i < count; i++) 
			{
				RouteTimetable rt = (RouteTimetable)list.getRouteList().get(i);
				
				if(!groupList.contains(rt.getCalendar())){
					groupList.add(rt.getCalendar());
				}
				if(rt.getCalendar().equals("WEEKDAY")){
					weekDays.add(rt.getTime());
				}
				else if(rt.getCalendar().equals("SATURDAY")){
					saturday.add(rt.getTime());
				}
				else if(rt.getCalendar().equals("SUNDAY")){
					sunday.add(rt.getTime());
				}
			}

			for (String groupName : groupList) {
				if(groupName.equals("WEEKDAY"))
					collection.put(groupName, weekDays);
				else 
					if(groupName.equals("SATURDAY"))
						collection.put(groupName, saturday);
					else 
						if(groupName.equals("SUNDAY"))
							collection.put(groupName, sunday);
			}	
			
			customAdapter.notifyDataSetChanged();
		}
	}
}
