package com.pitz.findmybus;

import java.util.ArrayList;
import java.util.List;

import com.pitz.findmybus.model.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter {

	LayoutInflater inflater;
	int resource;
	Context context;

	public CustomAdapter(Context context, int resource, List objects) {
		super(context, resource, objects);
		this.resource = resource;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGroup) {

		Holder viewHolder;

		if (view == null) {
			view = inflater.inflate(R.layout.route_row, viewGroup, false);

			viewHolder = new Holder();
			viewHolder.id = (TextView) view.findViewById(R.id.route_id);
			viewHolder.text = (TextView) view.findViewById(R.id.route_name);

			view.setTag(viewHolder);
		} else {
			viewHolder = (Holder) view.getTag();
		}

		Route route = (Route)getItem(pos);
		viewHolder.id.setText(route.getShortName() + ".");
		viewHolder.text.setText(route.getLongName());

		return view;
	}

	private static class Holder {
		public TextView text;
		public TextView id;
	}

}
