package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.ui.BadgeView;

public class WorkSearchActivity extends BaseActivity
{
	private GridView gv_ywzy;
	private FunctionAdapter adapter;
	private Animation ani;
	private int ggsjSize;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private Type[] values;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_search);
		initData();
		initView();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (Source.update && adapter != null)
		{
			updateNum();
			adapter.notifyDataSetChanged();
		}
	}

	class Task
	{
		String name;
		Drawable drawable;
		int num;

		public Task(String name, Drawable drawable, int num)
		{
			super();
			this.name = name;
			this.drawable = drawable;
			this.num = num;
		}
	}

	private void initView()
	{
		gv_ywzy = (GridView) findViewById(R.id.gv_gzcx);
		gv_ywzy.setAdapter(adapter);
		gv_ywzy.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Task task = tasks.get(position);
				if (task.num == 0)
				{
					view.startAnimation(ani);
					Toast.makeText(getApplicationContext(), "没有数据",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (position < ggsjSize)
				{
					Intent intent = new Intent(getApplicationContext(),
							GgsjListActivity.class);
					intent.putExtra("type", values[position]);
					startActivity(intent);
				}
				else if (position == ggsjSize)
				{
					Intent intent = new Intent(getApplicationContext(),
							ZzListActivity.class);
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent(getApplicationContext(),
							YwzyListActivity.class);
					intent.putExtra("name", task.name);
					startActivity(intent);
				}
			}
		});
	}

	public void back(View view)
	{
		finish();
	}

	private void initData()
	{
		tasks = new ArrayList<Task>();
		values = Type.values();
		for (Type t : values)
		{
			String name = t.getName();
			Drawable drawable = Source.ggsjDrawables.get(t);
			// System.out.println("source.ggsjs:"+Source.ggsjs);
			// System.out.println("Source.ggsjs.get(t):"+Source.ggsjs.get(t));
			int num = Source.ggsjs.get(t).size();
			tasks.add(new Task(name, drawable, num));
		}
		ggsjSize = values.length;
		tasks.add(new Task("住宅", Source.bzdzDrawable, Source.buildings.size()));
		for (Map.Entry<String, ArrayList<Ywzy>> entry : Source.ywzys.entrySet())
		{
			String name = entry.getKey();
			Drawable drawable = Source.ywzyDrawables.get(name);
			int num = entry.getValue().size();
			tasks.add(new Task(name, drawable, num));
		}
		ani = new TranslateAnimation(0, 0, 0, -10);
		CycleInterpolator inter = new CycleInterpolator(0.5f);
		ani.setInterpolator(inter);
		ani.setDuration(200);
		adapter = new FunctionAdapter();
	}

	private void updateNum()
	{
		for (int i = 0; i < ggsjSize; i++)
		{
			tasks.get(i).num = Source.ggsjs.get(values[i]).size();
		}
		tasks.get(ggsjSize).num = Source.buildings.size();
		for (int i = 0; i < Source.ywzys.size(); i++)
		{
			Task task = tasks.get(ggsjSize + i + 1);
			task.num = Source.ywzys.get(task.name).size();
		}
	}

	private class FunctionAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return tasks.size();
		}

		@Override
		public Object getItem(int position)
		{
			return tasks.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			Task task = tasks.get(position);
			if (convertView == null)
			{
				View view = View.inflate(getApplicationContext(),
						R.layout.item_task, null);
				TextView tv = (TextView) view.findViewById(R.id.tv);
				BadgeView badge = new BadgeView(WorkSearchActivity.this, tv);
				badge.setTextSize(14);
				badge.setBadgeBackgroundColor(Color.RED);
				badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				tv.setTag(badge);
				convertView = view;
			}

			TextView tv = (TextView) convertView.findViewById(R.id.tv);
			// 公共数据
			tv.setText(task.name);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, task.drawable,
					null, null);
			BadgeView badge = (BadgeView) tv.getTag();
			if (task.num > 0)
			{
				badge.setText("" + task.num);
				badge.show();
			}
			else
			{
				badge.hide();
			}
			return convertView;
		}
	}
}