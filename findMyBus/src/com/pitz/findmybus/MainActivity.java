package com.pitz.findmybus;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.pitz.findmybus.controller.BaseDao;
import com.pitz.findmybus.model.Route;
import com.pitz.findmybus.model.RouteList;

public class MainActivity extends Activity {

	ListView listView;
	CustomAdapter customAdapter;
	ArrayList<Route> routesArray;
	ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		routesArray = new ArrayList<Route>();
		customAdapter = new CustomAdapter(getApplicationContext(), R.layout.route_row, routesArray); //R.layout.item, locationArray);
		
		listView = (ListView) findViewById(R.id.listViewRoutes);
		listView.setAdapter(customAdapter);
		
		initListeners();
	}
	
	private void initListeners()
	{
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {				
				getItemDetails(pos);
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
			Toast.makeText(getApplicationContext(), "Please, type at least 3 letters", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			RouteDao routeDao = new RouteDao(getApplicationContext(), "%" + streetName + "%");
			routeDao.execute(StaticConstants.URL_FIND_ROUTES);
		}
		catch (Exception e) {
			Log.d("DAO", "Connection error");
		}
	}

	private void getItemDetails(int position)
	{
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class RouteDao extends AsyncTask<String, Integer, RouteList>
	{
		Context context = null;
		Gson gson = new Gson();
		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
		private final String attribute = "stopName";
		
		public RouteDao(Context context, String value){
			this.context = context;
			this.params.add(new BasicNameValuePair(attribute, value));
			this.headers.add(new BasicNameValuePair("Authorization", "Basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA== X-AppGlu-Environment: staging"));
			this.headers.add(new BasicNameValuePair("X-AppGlu-Environment", "staging"));
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMax(100);
			progressDialog.setTitle("Please wait");
			progressDialog.setMessage("Fecthing results");
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
					
					for (NameValuePair h : headers) {
						dao.addHeader(h.getName(), h.getValue());
					}
					
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
				Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
				return;
			}
			
			for (int i = 0; i < list.getRouteList().size(); i++) 
			{
				rt = (Route)list.getRouteList().get(i);
				routesArray.add(rt);
				Log.d("RESULT", list.getRouteList().get(i).getLongName());
			}
			
			customAdapter.notifyDataSetChanged();
		}
	}

}
