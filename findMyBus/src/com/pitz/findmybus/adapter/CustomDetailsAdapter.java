package com.pitz.findmybus.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pitz.findmybus.R;

public class CustomDetailsAdapter extends BaseExpandableListAdapter 
{
	Context context;
	private HashMap<String, List<String>> collection;
	private List<String> list;
	
	public CustomDetailsAdapter(Context context, List<String> objects, HashMap<String, List<String>> collection) {
		this.list = objects;
		this.collection = collection;
		this.context = context;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) 
	{
		Holder viewHolder;
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.details_row, null);
			
			viewHolder = new Holder();
			viewHolder.name = (TextView) view.findViewById(R.id.details_row_name);
			
			view.setTag(viewHolder);
		}
		else{
			viewHolder = (Holder)view.getTag();
		}
		
		final String childText = (String) getChild(groupPosition, childPosition);
		viewHolder.name.setText(childText);

		return view;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View view, ViewGroup parent) 
	{
		Holder viewHolder;
		
		if(view == null){
			LayoutInflater groupInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = groupInflater.inflate(R.layout.details_group_row, null);
			
			viewHolder = new Holder();
			viewHolder.groupName = (TextView) view.findViewById(R.id.details_row_group_name);
			
			view.setTag(viewHolder);
		}
		else {
			viewHolder = (Holder)view.getTag();
		}
		
		final String headerText = (String) getGroup(groupPosition);
		viewHolder.groupName.setText(headerText);
		return view;
	}
	
	public static class Holder{
		public TextView name;
		public TextView groupName;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	@Override
	public String getChild(int groupPos, int childPos) {
		return this.collection.get(this.list.get(groupPos)).get(childPos);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.collection.get(this.list.get(groupPosition)).size();
	}

	@Override
	public String getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

}
