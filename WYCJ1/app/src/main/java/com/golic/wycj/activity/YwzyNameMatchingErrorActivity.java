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
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.Ywzy;

public class YwzyNameMatchingErrorActivity extends Activity
{
	private View ll_top;
	private ListView lv_data_list;
	private View ll_mid;
	private View tv_bottom;
	ArrayList<ItemData> list;

	class ItemData
	{
		Ywzy ywzy;
		// String ywzyMc;
		// String ywzyDz;
		BaseAttrs attrs;

		public ItemData(Ywzy ywzy, BaseAttrs attrs)
		{
			super();
			this.ywzy = ywzy;
			this.attrs = attrs;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ywzy_name_matching_error);
		list = new ArrayList<ItemData>();
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
				// 查询出所有名称不一致的业务专用数据
				for (Map.Entry<String, ArrayList<Ywzy>> entry : Source.ywzys
						.entrySet())
				{
					String name = entry.getKey();
					if (YwzyDaoImpl.YWZY_NAMES[2].equals(name))
					{
						// 机构,依次查找场所--企事业单位--其他单位--驻华机构
						for (Ywzy ywzy : entry.getValue())
						{
							String id = ywzy.getGgsj_id();
							BaseAttrs attrs = findGgsj(id);
							if (attrs != null)
							{
								if (!ywzy.getMc().contains(attrs.MC))
								{
									list.add(new ItemData(ywzy, attrs));
								}
							}
						}
					}
					else
					{
						// 场所
						for (Ywzy ywzy : entry.getValue())
						{
							BaseAttrs attrs = Source.findGgsj(Type.GGSJ_CS,
									ywzy.getGgsj_id());
							if (attrs != null)
							{
								if (!ywzy.getMc().contains(attrs.MC))
								{
									list.add(new ItemData(ywzy, attrs));
								}
							}
						}
					}
				}
				//
				// for (Map.Entry<Type, ArrayList<BaseAttrs>> entry :
				// Source.ggsjs
				// .entrySet())
				// {
				// ArrayList<BaseAttrs> value = entry.getValue();
				// for (BaseAttrs attrs : value)
				// {
				// if (attrs.bs > 1)
				// {
				// // 说明是名称匹配错误数据
				// mutiYwzyAttrs.add(attrs);
				// }
				// }
				// }
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				ll_top.setVisibility(View.INVISIBLE);
				if (list.size() == 0)
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
									BaseAttrs attrs = list.get(position).attrs;
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
											YwzyNameMatchingErrorActivity.this)
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
															ItemData itemData = list
																	.get(position);
															Ywzy ywzy = itemData.ywzy;
															// 删除业务专用数据
															Source.ywzys
																	.get(ywzy
																			.getName())
																	.remove(itemData.ywzy);
															ywzy.setBs(0);
															int update = new YwzyDaoImpl(
																	getApplicationContext())
																	.update(ywzy);
															// 更新公共数据
															BaseAttrs attrs = itemData.attrs;
															attrs.bs--;
															int updateGgsj = new GgsjDaoImpl(
																	getApplicationContext())
																	.updateGgsj(attrs);
															if (updateGgsj > 0)
															{
																Source.updateGgsj(attrs);
															}
															// 最后更新界面
															if (update > 0)
															{
																list.remove(position);
																adapter.notifyDataSetChanged();
															}
														}
													})
											.setNegativeButton("取消", null)
											.show();
									return false;
								}
							});
				}
				super.onPostExecute(result);
			}
		}.execute();
	}

	private BaseAttrs findGgsj(String id)
	{
		BaseAttrs attrs = Source.findGgsj(Type.GGSJ_CS, id);
		if (attrs == null)
		{
			attrs = Source.findGgsj(Type.GGSJ_QSYDW, id);
		}
		if (attrs == null)
		{
			attrs = Source.findGgsj(Type.GGSJ_QTDW, id);
		}
		if (attrs == null)
		{
			attrs = Source.findGgsj(Type.GGSJ_ZHJG, id);
		}
		return attrs;
	}

	static class ViewHolder
	{
		TextView tv_ywzy_mc;
		TextView tv_ywzy_dz;
		TextView tv_ggsj_mc;
		TextView tv_ggsj_dz;
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
						R.layout.item_ywzy_name_matching_error, null);
				holder = new ViewHolder();
				holder.tv_ywzy_mc = (TextView) convertView
						.findViewById(R.id.tv_ywzy_mc);
				holder.tv_ywzy_dz = (TextView) convertView
						.findViewById(R.id.tv_ywzy_dz);
				holder.tv_ggsj_mc = (TextView) convertView
						.findViewById(R.id.tv_ggsj_mc);
				holder.tv_ggsj_dz = (TextView) convertView
						.findViewById(R.id.tv_ggsj_dz);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			ItemData itemData = list.get(position);
			holder.tv_ywzy_mc.setText(itemData.ywzy.getMc());
			holder.tv_ywzy_dz.setText(itemData.ywzy.getMlxz());
			holder.tv_ggsj_mc.setText(itemData.attrs.MC);
			holder.tv_ggsj_dz.setText(itemData.attrs.DZ);
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
			return list.get(position);
		}

		@Override
		public int getCount()
		{
			return list.size();
		}
	}
}