package com.pitz.findmybus.activities;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pitz.findmybus.R;
import com.pitz.findmybus.adapter.CustomAdapter;
import com.pitz.findmybus.dao.BaseDao;
import com.pitz.findmybus.model.Route;
import com.pitz.findmybus.model.RouteList;


public class MainActivity extends Activity 
{
	final String URL_FIND_ROUTES = "https://dashboard.appglu.com/v1/queries/findRoutesByStopName/run";
	
	ListView listView;
	CustomAdapter customAdapter;
	ArrayList<Route> routesArray = new ArrayList<Route>();
	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		createAdapter();
		initListeners();
	}
	
	private void createAdapter(){
		customAdapter = new CustomAdapter(getApplicationContext(), R.layout.route_row, routesArray);
		
		listView = (ListView) findViewById(R.id.listViewRoutes);
		listView.setAdapter(customAdapter);
	}
	
	private void initListeners()
	{
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view, int pos, long arg3) {				
				getItemDetails(list, pos);
			}
		});
		
		
		Button searchButton = (Button) findViewById(R.id.btSearch);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				executeSearch(v);
			}
		});
	}
	
	private void executeSearch(View v)
	{
		routesArray.clear();
		
		String streetName = ((EditText) findViewById(R.id.etSearchInput)).getText().toString();
		
		if(streetName.length() < 3){
			Toast.makeText(getApplicationContext(), getString(R.string.message_type_at_least_three), Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			RouteDao routeDao = new RouteDao(getApplicationContext(), "%" + streetName + "%");
			routeDao.execute(this.URL_FIND_ROUTES);
		}
		catch (Exception e) {
			Log.d("RouteDao", "Connection error");
		}
	}

	private void getItemDetails(AdapterView<?> list, int position)
	{
		Route selectedRoute = (Route) list.getAdapter().getItem(position);
		
		Intent it = new Intent(getApplicationContext(), RouteDetailsActivity.class);
		it.putExtra(Protocols.PARAM_ROUTE_NAME, selectedRoute.toString());
		it.putExtra(Protocols.PARAM_ROUTE_ID, selectedRoute.getId());
		
		startActivity(it);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class RouteDao extends AsyncTask<String, Integer, RouteList>
	{
		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private final String paramKey = "stopName";

		Context context = null;
		Gson gson = new Gson();
		
		public RouteDao(Context context, String value)
		{
			this.context = context;
			this.params.add(new BasicNameValuePair(paramKey, value));
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMax(100);
			progressDialog.setTitle(getString(R.string.message_please_wait));
			progressDialog.setMessage(getString(R.string.message_fetching_results));
			progressDialog.show();
			
			((Button) findViewById(R.id.btSearch)).setEnabled(false);
		}
		
		@Override
		protected RouteList doInBackground(String... urls) 
		{
			Gson gson = new Gson();
			RouteList list = null;
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
					list = gson.fromJson(json, RouteList.class);

				} catch (Exception e) {
				}
			}
			
			return list;
		}
		
		@Override
		protected void onPostExecute(RouteList list) 
		{
			progressDialog.dismiss();
			((Button) findViewById(R.id.btSearch)).setEnabled(true);
			
			Route rt = new Route();
			
			if(list.getRouteList() != null && list.getRouteList().size() == 0){
				Toast.makeText(getApplicationContext(), getString(R.string.message_no_results), Toast.LENGTH_LONG).show();
				return;
			}
			
			for (int i = 0; i < list.getRouteList().size(); i++) 
			{
				rt = (Route)list.getRouteList().get(i);
				routesArray.add(rt);
			}
			
			customAdapter.notifyDataSetChanged();
		}
	}
}
