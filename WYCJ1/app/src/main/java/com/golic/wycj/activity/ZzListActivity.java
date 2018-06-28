package com.golic.wycj.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.domain.Building;

public class ZzListActivity extends BaseActivity
{
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zzlist);
		initView();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (Source.update && adapter != null)
		{
			adapter.notifyDataSetChanged();
		}
	}

	private void initView()
	{
		ListView lv_zzlist = (ListView) findViewById(R.id.lv_zzlist);
		adapter = new MyAdapter();
		lv_zzlist.setAdapter(adapter);
		lv_zzlist.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Building building = Source.buildings.get(position);
				Intent intent = new Intent(ZzListActivity.this,
						FormBaseActivity.class);
				intent.putExtra("building", building);
				startActivity(intent);
			}
		});
		registerForContextMenu(lv_zzlist);
	}

	private class MyAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return Source.buildings.size();
		}

		@Override
		public Object getItem(int position)
		{
			return Source.buildings.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_zz, null);
				holder = new ViewHolder();
				holder.tv_dz = (TextView) convertView.findViewById(R.id.tv_dz);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.tv_type);
				holder.tv_max_floor = (TextView) convertView
						.findViewById(R.id.tv_max_floor);
				holder.ll_max_room = (LinearLayout) convertView
						.findViewById(R.id.ll_max_room);
				holder.tv_max_room = (TextView) convertView
						.findViewById(R.id.tv_max_room);
				holder.tv_opearation = (TextView) convertView
						.findViewById(R.id.tv_opearation);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			Building building = Source.buildings.get(position);
			holder.tv_dz.setText(building.baseBuilding.mphm.MLXZ);
			if (building.baseBuilding.mphm.DZLX == 1)
			{
				holder.tv_type.setText("房屋性质：");
				holder.tv_max_floor.setText("私宅");
				holder.ll_max_room.setVisibility(View.GONE);
			}
			else
			{
				holder.tv_type.setText("最大楼层数：");
				holder.tv_max_floor.setText("" + building.getMaxFloorSize());
				holder.ll_max_room.setVisibility(View.VISIBLE);
				holder.tv_max_room.setText("" + building.getMaxRoomSize());
			}
			holder.tv_opearation.setText(building.baseBuilding.mphm.DJR + "于"
					+ building.baseBuilding.mphm.DJSJ + "采集");
			return convertView;
		}
	}

	static class ViewHolder
	{
		TextView tv_dz;
		TextView tv_type;
		TextView tv_max_floor;
		LinearLayout ll_max_room;
		TextView tv_max_room;
		TextView tv_opearation;
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
		final Building building = Source.buildings.get(position);
		if (itemId == 0)
		{
			Point point = new Point(
					Double.parseDouble(building.baseBuilding.mphm.X),
					Double.parseDouble(building.baseBuilding.mphm.Y));
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
									int delete = new BzdzDaoImpl(
											getApplicationContext())
											.deleteBuilding(building.baseBuilding.mphm.ID);
									if (delete > 0)
									{
										Source.buildings.remove(position);
										Source.update = true;
										// Source.removeBuilding(building.baseBuilding.mphm.ID);
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