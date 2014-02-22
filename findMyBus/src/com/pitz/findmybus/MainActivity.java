package com.pitz.findmybus;

import java.util.ArrayList;

import com.pitz.findmybus.model.Route;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	CustomAdapter customAdapter;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Route route = new Route();
		route.setDescription("uma");
		ArrayList<Route> list = new ArrayList<Route>();
		list.add(route);
		list.add(route);
		list.add(route);
		list.add(route);
		list.add(route);
		
		customAdapter = new CustomAdapter(getApplicationContext());
		customAdapter.setItem(list);
		
		listView = (ListView) findViewById(R.id.listViewRoutes);
		listView.setAdapter(customAdapter);
		
		
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
	
	private void executeSearch(View v){
		
	}

	private void getItemDetails(int position){
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
