package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.model.WorkDay;
import com.golic.wycj.util.DbHelper;
import com.golic.wycj.util.NoticeUtil;

public class DataDeleteActivity extends BaseActivity
{
	// private TreeSet<WorkDay> bzdzWorkDays;
	// private TreeSet<WorkDay> ggsjWorkDays;
	private BzdzDaoImpl bzdzDaoImpl;
	private GgsjDaoImpl ggsjDaoImpl;
	private ArrayList<WorkDay> list;
	private ListView lv_work_day;
	private com.golic.wycj.activity.DataDeleteActivity.MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_delete);
		initData();
		initView();
	}

	private void initView()
	{
		lv_work_day = (ListView) findViewById(R.id.lv_work_day);
		adapter = new MyAdapter();
		lv_work_day.setAdapter(adapter);
		lv_work_day.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				WorkDay day = list.get(position);
				day.select = !day.select;
				adapter.notifyDataSetChanged();
			}
		});
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
						R.layout.item_work_day, null);
				holder = new ViewHolder();
				holder.tv_gxsj = (TextView) convertView
						.findViewById(R.id.tv_gxsj);
				holder.tv_gxr = (TextView) convertView
						.findViewById(R.id.tv_gxr);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.tv_type);
				holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			final WorkDay day = list.get(position);
			holder.tv_gxsj.setText(day.gxsj);
			holder.tv_gxr.setText(day.gxr);
			holder.tv_type.setText(day.type);
			if ("标准地址".equals(day.type))
			{
				holder.tv_type.setTextColor(Color.GREEN);
			}
			else
			{
				holder.tv_type.setTextColor(Color.BLUE);
			}
			holder.cb.setChecked(day.select);
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

	static class ViewHolder
	{
		TextView tv_gxsj;
		TextView tv_gxr;
		TextView tv_type;
		CheckBox cb;
	}

	private void initData()
	{
		bzdzDaoImpl = new BzdzDaoImpl(getApplicationContext());
		ggsjDaoImpl = new GgsjDaoImpl(getApplicationContext());
		TreeSet<WorkDay> bzdzWorkDays = bzdzDaoImpl.queryWorkday();
		TreeSet<WorkDay> ggsjWorkDays = ggsjDaoImpl.queryWorkday();
		ggsjWorkDays.addAll(bzdzWorkDays);
		list = new ArrayList<WorkDay>(ggsjWorkDays);
	}

	public void delete(WorkDay firstDayWork, TreeSet<WorkDay> set)
	{
		if (firstDayWork == null)
		{
			return;
		}
		// WorkDay day = set.pollFirst();
		if ("标准地址".equals(firstDayWork.type))
		{
			deleteSingleBzdz(firstDayWork);
			// deleteSingleGgsj(firstDayWork);
			delete(set.pollFirst(), set);
		}
		else
		{
			WorkDay day = set.pollFirst();
			if (day == null)
			{
				deleteSingleGgsj(firstDayWork);
				// deleteSingleBzdz(firstDayWork);
				return;
			}
			// 两个都不是空
			if ("公共数据".equals(day.type))
			{
				deleteSingleGgsj(firstDayWork);
				// deleteSingleBzdz(firstDayWork);
				delete(day, set);
			}
			else
			{
				// 都不是空，并且前面是公共数据，后面是标准地址
				if (firstDayWork.gxsj.equals(day.gxsj)
						&& firstDayWork.gxr.equals(day.gxr))
				{
					deleteDouble(firstDayWork, day);
				}
				else
				{
					deleteSingleGgsj(firstDayWork);
					deleteSingleBzdz(day);
					// deleteSingleBzdz(firstDayWork);
					// deleteSingleBzdz(day);
				}
				delete(set.pollFirst(), set);
			}
		}
	}

	private void deleteSingleBzdz(WorkDay bzdzWorkDay)
	{
		boolean complete = true;
		for (WorkDay day : list)
		{
			// 如果同一天存在没有删除的公共数据，那么它的地址也不能删除
			if ("公共数据".equals(day.type) && day.gxsj.equals(bzdzWorkDay.gxsj)
					&& day.gxr.equals(bzdzWorkDay.gxr))
			{
				complete = false;
				break;
			}
		}
		bzdzDaoImpl.deleteWorkDay(bzdzWorkDay, complete);
	}

	private void deleteSingleGgsj(WorkDay ggsjWorkDay)
	{
		ggsjDaoImpl.deleteWorkDay(ggsjWorkDay);
	}

	private void deleteDouble(WorkDay bzdzWorkDay, WorkDay ggsjWorkDay)
	{
		SQLiteDatabase database = DbHelper.getDatabase(getApplicationContext());
		database.beginTransaction();
		try
		{
			ggsjDaoImpl.deleteWorkDay(ggsjWorkDay);
			bzdzDaoImpl.deleteWorkDay(bzdzWorkDay, true);
			database.setTransactionSuccessful();
		}
		finally
		{
			database.endTransaction();
		}
	}

	public void delete(View view)
	{
		new Builder(this).setTitle("提醒").setMessage("您确定删除已选数据？")
				.setPositiveButton("确定", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						final TreeSet<WorkDay> set = new TreeSet<WorkDay>();

						Iterator<WorkDay> it = list.iterator();

						while (it.hasNext())
						{
							WorkDay day = it.next();
							if (day.select)
							{
								set.add(day);
								it.remove();
							}
						}
						// for (int i = 0; i < list.size(); i++)
						// {
						// WorkDay day = list.get(i);
						// if (day.select)
						// {
						// set.add(list.remove(i));
						// }
						// }
						if (set.size() == 0)
						{
							Toast.makeText(getApplicationContext(),
									"请至少选择一条记录", Toast.LENGTH_LONG).show();
							return;
						}
						adapter.notifyDataSetChanged();
						new AsyncTask<Void, Void, Void>()
						{
							ProgressDialog dialog;

							@Override
							protected void onPreExecute()
							{
								dialog = ProgressDialog.show(
										DataDeleteActivity.this, "请稍候...",
										"正在删除数据");
							}

							@Override
							protected Void doInBackground(Void... params)
							{
								delete(set.pollFirst(), set);
								return null;
							}

							@Override
							protected void onPostExecute(Void result)
							{
								dialog.dismiss();
								Source.update = true;
								NoticeUtil.showWarningDialog(
										DataDeleteActivity.this,
										"数据删除完成，将在下次登录时生效！");
							}
						}.execute();
					}
				}).setNegativeButton("取消", null).show();
	}

	public void back(View view)
	{
		finish();
	}
}