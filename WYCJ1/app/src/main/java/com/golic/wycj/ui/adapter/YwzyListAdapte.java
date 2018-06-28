package com.golic.wycj.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.golic.wycj.R;
import com.golic.wycj.domain.Ywzy;

public class YwzyListAdapte extends BaseAdapter
{

	private Context context;
	private List<Ywzy> dataList;

	public YwzyListAdapte(Context context, List<Ywzy> dataList)
	{
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		Ywzy ywzy = dataList.get(position);
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_single_choise, null);
			viewHolder.ywzyMc = (TextView) convertView
					.findViewById(R.id.zd_dialog_id);
			viewHolder.ywzyDz = (TextView) convertView
					.findViewById(R.id.zd_dialog_name);
			if ("".equals(ywzy.getMlxz()) || ywzy.getMlxz().length() == 0)
			{
				viewHolder.ywzyDz.setVisibility(View.GONE);
			}
			convertView.setTag(viewHolder);

		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.ywzyMc.setText(ywzy.getMc());
		viewHolder.ywzyDz.setText(ywzy.getMlxz());
		return convertView;
	}

	static class ViewHolder
	{
		TextView ywzyMc;
		TextView ywzyDz;
	}
}
