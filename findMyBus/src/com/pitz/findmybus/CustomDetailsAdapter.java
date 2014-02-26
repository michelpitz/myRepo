package com.pitz.findmybus;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

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
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.details_row, null);
		}
		
		final String childText = (String) getChild(groupPosition, childPosition);
		
		TextView name = (TextView) view.findViewById(R.id.details_row_name);
		name.setText(childText);

		return view;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View view, ViewGroup parent) 
	{
		if(view == null){
			LayoutInflater groupInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = groupInflater.inflate(R.layout.details_group_row, null);
		}

		TextView groupName = (TextView) view.findViewById(R.id.details_row_group_name);
		groupName.setText((String) getGroup(groupPosition));
		
		return view;
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
