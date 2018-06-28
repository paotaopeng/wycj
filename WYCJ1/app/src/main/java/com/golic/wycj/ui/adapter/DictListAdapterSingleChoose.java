package com.golic.wycj.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.golic.wycj.R;
import com.golic.wycj.model.DictObject;


public class DictListAdapterSingleChoose extends BaseAdapter {

	private Context context;
	private List<? extends DictObject> dataList;

	public DictListAdapterSingleChoose(Context context, List<? extends DictObject> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_single_choise, null);
			viewHolder.idText = (TextView) convertView.findViewById(R.id.zd_dialog_id);
			if (dataList.get(position).getDM().length() > 10) {
				viewHolder.idText.setVisibility(View.GONE);
			}
			viewHolder.nameText = (TextView) convertView.findViewById(R.id.zd_dialog_name);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.idText.setText(dataList.get(position).getDM());
		viewHolder.nameText.setText(dataList.get(position).getMC());
		return convertView;
	}

	static class ViewHolder {
		TextView idText;
		TextView nameText;
	}
}