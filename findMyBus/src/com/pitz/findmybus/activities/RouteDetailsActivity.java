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
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pitz.findmybus.R;
import com.pitz.findmybus.adapter.CustomDetailsAdapter;
import com.pitz.findmybus.dao.BaseDao;
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
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_details);
		
		setTitle(getString(R.string.app_name) + " > " + getString(R.string.title_activity_route_details));
		
		String routeName = getIntent().getStringExtra(Protocols.PARAM_ROUTE_NAME);
		String routeId = getIntent().getStringExtra(Protocols.PARAM_ROUTE_ID);
		((TextView)findViewById(R.id.routeDetails_name)).setText(routeName);
		
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId())
		{
			case R.id.action_back:
				setResult(RESULT_OK);
				finish();
				return true; 
	
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	class RouteDetailsDao extends AsyncTask<String, Integer, RouteDetailsList>
	{
		Context context = null;
		Gson gson = new Gson();
		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private final String paramKey = "routeId";
		final String weekdayStr = "WEEKDAY";
		final String saturdayStr = "SATURDAY";
		final String sundayStr = "SUNDAY";
		
		public RouteDetailsDao(Context context, String value)
		{
			this.context = context;
			this.params.add(new BasicNameValuePair(paramKey, value));
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(RouteDetailsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMax(100);
			progressDialog.setTitle(getString(R.string.message_please_wait));
			progressDialog.setMessage(getString(R.string.message_fetching_results));
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
				Toast.makeText(getApplicationContext(), getString(R.string.message_no_results), Toast.LENGTH_LONG).show();
				return;
			}
			
			createCollection(list);
			customAdapter.notifyDataSetChanged();
		}
		
		private void createCollection(RouteDetailsList list){
			List<String> weekDays = new ArrayList<String>();
			List<String> saturday = new ArrayList<String>();
			List<String> sunday = new ArrayList<String>();
			
			int count = list.getRouteList().size();
	
			
			for (int i = 0; i < count; i++) 
			{
				RouteTimetable rt = (RouteTimetable)list.getRouteList().get(i);
				
				if(!groupList.contains(rt.getCalendar())){
					groupList.add(rt.getCalendar());
				}
				if(rt.getCalendar().equals(weekdayStr)){
					weekDays.add(rt.getTime());
				}
				else if(rt.getCalendar().equals(saturdayStr)){
					saturday.add(rt.getTime());
				}
				else if(rt.getCalendar().equals(sundayStr)){
					sunday.add(rt.getTime());
				}
			}
	
			for (String groupName : groupList) {
				if(groupName.equals(weekdayStr))
					collection.put(groupName, weekDays);
				else 
					if(groupName.equals(saturdayStr))
						collection.put(groupName, saturday);
					else 
						if(groupName.equals(sundayStr))
							collection.put(groupName, sunday);
			}	
		}
	}
}
