package com.golic.wycj.activity;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.Ywzy;

public class YwzyListActivity extends BaseActivity
{
	private MyAdapter adapter;
	private String name;
	private ArrayList<Ywzy> list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ywzy_list);
		initData();
		initView();
	}

	private void initData()
	{
		name = getIntent().getStringExtra("name");
		list = Source.ywzys.get(name);
	}

	private void initView()
	{
		ListView lv_ywzy_list = (ListView) findViewById(R.id.lv_ywzy_list);
		adapter = new MyAdapter();
		lv_ywzy_list.setAdapter(adapter);
		registerForContextMenu(lv_ywzy_list);
	}

	private class MyAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Ywzy ywzy = list.get(position);
			ViewHolder holder = null;
			if (convertView == null)
			{
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_ywzy, null);
				holder = new ViewHolder();
				holder.tv_dz = (TextView) convertView.findViewById(R.id.tv_dz);
				holder.tv_mc = (TextView) convertView.findViewById(R.id.tv_mc);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_dz.setText(ywzy.getMlxz());
			holder.tv_mc.setText(ywzy.getMc());
			return convertView;
		}
	}

	static class ViewHolder
	{
		TextView tv_dz;
		TextView tv_mc;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderIcon(R.drawable.down_icon);
		menu.setHeaderTitle("菜单");
		menu.add(1, 0, 0, "定位");
		menu.add(1, 1, 1, "删除");
	};

	public boolean onContextItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final int position = menuInfo.position;
		final Ywzy ywzy = list.get(position);
		if (itemId == 0)
		{
			Point point = new Point(Double.parseDouble(ywzy.getX()),
					Double.parseDouble(ywzy.getY()));
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("point", point);
			// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
		}
		else if (itemId == 1)
		{
			new Builder(this)
					.setTitle("提示")
					.setMessage("您是否要删除该条数据？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									ywzy.setBs(0);
									int update = new YwzyDaoImpl(
											getApplicationContext())
											.update(ywzy);
									if (update > 0)
									{
										list.remove(position);
//										Source.ywzys.get(name).remove(position);
										Source.update = true;
										adapter.notifyDataSetChanged();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		return super.onContextItemSelected(item);
	};

	public void back(View view)
	{
		finish();
	}
}