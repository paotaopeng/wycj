package com.golic.wycj.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.domain.Building;
import com.golic.wycj.util.DictUtil;

/**
 * 所有没有门牌号的幢楼
 * 
 * @author luo
 * 
 */
public class NonMphBuildingActivity extends Activity
{
	private View ll_top;
	private ListView lv_data_list;
	private View ll_mid;
	private View tv_bottom;
	ArrayList<Building> buildings;
	private DictUtil dictUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_non_mph_building);
		dictUtil = DictUtil.getInstance(getApplicationContext());
		buildings = new ArrayList<Building>();
		ll_top = findViewById(R.id.ll_top);
		ll_mid = findViewById(R.id.ll_mid);
		lv_data_list = (ListView) findViewById(R.id.lv_data_list);
		tv_bottom = findViewById(R.id.tv_bottom);
		new AsyncTask<Void, Void, Void>()
		{
			private MyAdapter adapter;

			@Override
			protected void onPreExecute()
			{
				ll_top.setVisibility(View.VISIBLE);
				tv_bottom.setVisibility(View.INVISIBLE);
				ll_mid.setVisibility(View.INVISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				// 查询所有无门牌号的幢楼
				for (Building b : Source.buildings)
				{
					if (TextUtils.isEmpty(b.baseBuilding.mphm.MPH))
					{
						buildings.add(b);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				ll_top.setVisibility(View.INVISIBLE);
				if (buildings.size() == 0)
				{
					tv_bottom.setVisibility(View.VISIBLE);
					ll_mid.setVisibility(View.INVISIBLE);
				}
				else
				{
					tv_bottom.setVisibility(View.INVISIBLE);
					ll_mid.setVisibility(View.VISIBLE);
					adapter = new MyAdapter();
					lv_data_list.setAdapter(adapter);
					lv_data_list
							.setOnItemClickListener(new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id)
								{
									// 跳转界面到地图
									Building building = buildings.get(position);
									Intent intent = new Intent(
											getApplicationContext(),
											MapActivity.class);
									intent.putExtra(
											"point",
											new Point(
													Double.parseDouble(building.baseBuilding.mphm.X),
													Double.parseDouble(building.baseBuilding.mphm.Y)));
									// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									finish();
								}
							});
					lv_data_list
							.setOnItemLongClickListener(new OnItemLongClickListener()
							{
								@Override
								public boolean onItemLongClick(
										AdapterView<?> parent, View view,
										final int position, long id)
								{
									new Builder(NonMphBuildingActivity.this)
											.setTitle("提示")
											.setMessage("您确定要删除该条数据吗？")
											.setPositiveButton("确定",
													new OnClickListener()
													{
														@Override
														public void onClick(
																DialogInterface dialog,
																int which)
														{
															Building building = buildings
																	.get(position);
															String ID = building.baseBuilding.mphm.ID;
															int delete = new BzdzDaoImpl(
																	getApplicationContext())
																	.deleteBuilding(ID);
															if (delete > 0)
															{
																Source.removeBuilding(ID);
																buildings
																		.remove(position);
																adapter.notifyDataSetChanged();
															}
														}
													})
											.setNegativeButton("取消", null).show();
									return false;
								}
							});
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	static class ViewHolder
	{
		TextView tv_dz;
		TextView tv_mply;
		TextView tv_bz;
		TextView tv_operation;
	}

	private class MyAdapter extends BaseAdapter
	{
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder = null;
			if (convertView == null)
			{
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_non_mph, null);
				holder = new ViewHolder();
				holder.tv_dz = (TextView) convertView.findViewById(R.id.tv_dz);
				holder.tv_mply = (TextView) convertView
						.findViewById(R.id.tv_mply);
				holder.tv_bz = (TextView) convertView.findViewById(R.id.tv_bz);
				holder.tv_operation = (TextView) convertView
						.findViewById(R.id.tv_operation);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			Building building = buildings.get(position);
			holder.tv_dz.setText(building.baseBuilding.mphm.MLXZ);
			holder.tv_mply.setText(dictUtil.getDictValue(DictUtil.DZ_MPLY,
					building.baseBuilding.mphm.MPLY));
			holder.tv_bz.setText(building.baseBuilding.mphm.BZ);
			holder.tv_operation.setText(building.baseBuilding.mphm.DJR + "\n"
					+ building.baseBuilding.mphm.DJSJ);
			return convertView;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public Object getItem(int position)
		{
			return buildings.get(position);
		}

		@Override
		public int getCount()
		{
			return buildings.size();
		}
	}
}