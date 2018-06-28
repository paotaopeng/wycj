package com.golic.wycj.activity;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.core.geometry.Point;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.util.TypeUtil;

public class GgsjListActivity extends BaseActivity
{
	private MyAdapter adapter;
	private ArrayList<BaseAttrs> list;
	private Type type;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ggsj_list);
		initData();
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

	private void initData()
	{
		type = (Type) getIntent().getSerializableExtra("type");
		list = Source.ggsjs.get(type);
	}

	private void initView()
	{
		TextView tv_title_dz = (TextView) findViewById(R.id.tv_title_dz);
		if (type == Type.GGSJ_GGSS || type == Type.GGSJ_JTSS)
		{
			tv_title_dz.setVisibility(View.GONE);
		}
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(type.getName());
		ListView lv_ggsj = (ListView) findViewById(R.id.lv_ggsj);
		adapter = new MyAdapter();
		lv_ggsj.setAdapter(adapter);
		lv_ggsj.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				BaseAttrs attrs = list.get(position);
				TypeUtil.go2GgsjActivity(attrs, GgsjListActivity.this);
			}
		});
		registerForContextMenu(lv_ggsj);
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
		final BaseAttrs attrs = list.get(position);
		if (itemId == 0)
		{
			Point point = new Point(Double.parseDouble(attrs.X),
					Double.parseDouble(attrs.Y));
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
									int delete = new GgsjDaoImpl(
											getApplicationContext())
											.delete(attrs);
									if (delete > 0)
									{
										list.remove(position);
										Source.update = true;
										adapter.notifyDataSetChanged();
									}
								}
							}).setNegativeButton("取消", null).show();
		}
		return super.onContextItemSelected(item);
	};

	// private void go2GgsjActivity(Type type, BaseAttrs attrs)
	// {
	// switch (type)
	// {
	// case GGSJ_GGSS:
	// Intent ggssIntent = new Intent(getApplicationContext(),
	// GgssActivity.class);
	// ggssIntent.putExtra("attrs", new GGSS(attrs));
	// startActivity(ggssIntent);
	// break;
	// case GGSJ_JTSS:
	// Intent jtssIntent = new Intent(getApplicationContext(),
	// JtssActivity.class);
	// jtssIntent.putExtra("attrs", new JTSS(attrs));
	// startActivity(jtssIntent);
	// break;
	// default:
	// Intent intent = new Intent(getApplicationContext(),
	// FormBaseActivity.class);
	// intent.putExtra("attrs", attrs);
	// startActivity(intent);
	// }
	// }

	static class ViewHolder
	{
		TextView tv_name;
		TextView tv_dz;
		TextView tv_djr;
		TextView tv_djsj;
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
			BaseAttrs attrs = list.get(position);
			ViewHolder holder = null;
			if (convertView == null)
			{
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_ggsj, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.tv_dz = (TextView) convertView.findViewById(R.id.tv_dz);
				holder.tv_djr = (TextView) convertView
						.findViewById(R.id.tv_djr);
				holder.tv_djsj = (TextView) convertView
						.findViewById(R.id.tv_djsj);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			String mC = attrs.MC;
			String dZ = attrs.DZ;
			String dJR = attrs.DJR;
			String dJSJ = attrs.DJSJ;
			if (TextUtils.isEmpty(mC))
			{
				holder.tv_name.setVisibility(View.INVISIBLE);
			}
			else
			{
				holder.tv_name.setText(mC);
			}
			if (TextUtils.isEmpty(dZ))
			{
				holder.tv_dz.setVisibility(View.GONE);
			}
			else
			{
				holder.tv_dz.setText(dZ);
			}
			holder.tv_djr.setText(dJR);
			holder.tv_djsj.setText(dJSJ);
			return convertView;
		}
	}

	public void back(View view)
	{
		finish();
	}
}