package com.pitz.findmybus;

import java.util.ArrayList;

import com.pitz.findmybus.model.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{

	LayoutInflater inflater;
	ArrayList<Route> routeList = new ArrayList<Route>();
	
	public CustomAdapter(Context context){
		 inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setItem(ArrayList<Route> list){
		this.routeList = list;
	}
	
	@Override
	public int getCount() {
		return routeList.size();
	}

	@Override
	public Object getItem(int index) {
		return routeList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGroup) {
		
		ViewHolder viewHolder;
		
		if(view == null){
			view = inflater.inflate(R.layout.route_row, viewGroup, false);
			
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.route_description);
			
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.text.setText("teste" + pos);
		
		return view;
	}
	
	private static class ViewHolder{
		public TextView text;
	}

}
