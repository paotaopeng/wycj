package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.golic.wycj.Type;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.domain.BaseAttrs;

public class NameMatchingErrorListActivity extends Activity
{
	private View ll_top;
	private ListView lv_data_list;
	private View ll_mid;
	private View tv_bottom;
	ArrayList<BaseAttrs> errorGgsjs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name_error_data_list);
		errorGgsjs = new ArrayList<BaseAttrs>();
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
				// 查询出所有的名称不匹配的公共数据
				for (Map.Entry<Type, ArrayList<BaseAttrs>> entry : Source.ggsjs
						.entrySet())
				{
					ArrayList<BaseAttrs> value = entry.getValue();
					for (BaseAttrs attrs : value)
					{
						if (attrs.level > 1)
						{
							// 说明是名称匹配错误数据
							errorGgsjs.add(attrs);
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				ll_top.setVisibility(View.INVISIBLE);
				if (errorGgsjs.size() == 0)
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
									BaseAttrs attrs = errorGgsjs.get(position);
									Intent intent = new Intent(
											getApplicationContext(),
											MapActivity.class);
									intent.putExtra(
											"point",
											new Point(Double
													.parseDouble(attrs.X),
													Double.parseDouble(attrs.Y)));
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
									new Builder(
											NameMatchingErrorListActivity.this)
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
															BaseAttrs attrs = errorGgsjs
																	.get(position);
															int delete = new GgsjDaoImpl(
																	getApplicationContext())
																	.delete(attrs);
															if (delete > 0)
															{
																Source.removeGgsj(attrs);
																errorGgsjs
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
		TextView tv_flmc;
		TextView tv_sjmc;
		TextView tv_level_comment;
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
						R.layout.item_name_error, null);
				holder = new ViewHolder();
				holder.tv_flmc = (TextView) convertView
						.findViewById(R.id.tv_flmc);
				holder.tv_sjmc = (TextView) convertView
						.findViewById(R.id.tv_sjmc);
				holder.tv_level_comment = (TextView) convertView
						.findViewById(R.id.tv_level_comment);
				holder.tv_operation = (TextView) convertView
						.findViewById(R.id.tv_operation);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			BaseAttrs attrs = errorGgsjs.get(position);
			holder.tv_flmc.setText(attrs.LX);
			holder.tv_sjmc.setText(attrs.MC);
			holder.tv_level_comment.setText(attrs.comment);
			holder.tv_operation.setText(attrs.DJR + "\n" + attrs.DJSJ);
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
			return errorGgsjs.get(position);
		}

		@Override
		public int getCount()
		{
			return errorGgsjs.size();
		}
	}
}