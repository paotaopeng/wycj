package com.golic.wycj.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.util.ExcelUtil;

public class DataExportActivity extends BaseActivity implements OnClickListener
{
	boolean[] checked = new boolean[] { false, false, false };
	private CheckBox cb_ggsj;
	private CheckBox cb_bzdz;
	private CheckBox cb_ywzy;
	private Button bt_back;
	private Button bt_all;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_export);
		initView();
	}

	private void initView()
	{
		cb_ggsj = (CheckBox) findViewById(R.id.cb_ggsj);
		cb_bzdz = (CheckBox) findViewById(R.id.cb_bzdz);
		cb_ywzy = (CheckBox) findViewById(R.id.cb_ywzy);
		bt_all = (Button) findViewById(R.id.bt_all);
		bt_back = (Button) findViewById(R.id.bt_back);

		cb_ggsj.setOnClickListener(this);
		cb_bzdz.setOnClickListener(this);
		cb_ywzy.setOnClickListener(this);
		bt_all.setOnClickListener(this);
		bt_back.setOnClickListener(this);
	}

	/**
	 * 数据导出
	 */
	public void export(View view)
	{
		// 首先判断哪些数据被选择了，如果全部没有选择弹出提示，否则依次导出
		if (hasCancleAll())
		{
			Toast.makeText(getApplicationContext(), "至少需要选择一种数据类型",
					Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog dialog = ProgressDialog.show(this, "数据导出",
				"数据正在导出请稍候！");
		dialog.setCanceledOnTouchOutside(false);
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected void onPostExecute(String result)
			{
				super.onPostExecute(result);
				dialog.dismiss();
				if (!"".equals(result))
				{
					new AlertDialog.Builder(DataExportActivity.this)
							.setTitle("导出失败").setMessage(result)
							.setPositiveButton("确定", null).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "数据导出成功！",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			protected String doInBackground(Void... params)
			{
				return ExcelUtil.exportAll(checked,new PhotoDaoImpl(getApplicationContext()));
			}
		}.execute();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.cb_ggsj:
			checked[0] = cb_ggsj.isChecked();
			// cb_ggsj.setChecked(!c0);
			// checked[0] = !c0;
			break;
		case R.id.cb_bzdz:
			checked[1] = cb_bzdz.isChecked();
			// cb_bzdz.setChecked(!c1);
			// checked[1] = !c1;
			break;
		case R.id.cb_ywzy:
			checked[2] = cb_ywzy.isChecked();
			// cb_ywzy.setChecked(!c2);
			// checked[2] = !c2;
			break;
		case R.id.bt_all:
			if (hasSelectAll())
			{
				// 反选
				checked[0] = false;
				checked[1] = false;
				checked[2] = false;
				cb_ggsj.setChecked(false);
				cb_bzdz.setChecked(false);
				cb_ywzy.setChecked(false);
				bt_all.setText("全选");
			}
			else
			{
				// 全选
				checked[0] = true;
				checked[1] = true;
				checked[2] = true;
				cb_ggsj.setChecked(true);
				cb_bzdz.setChecked(true);
				cb_ywzy.setChecked(true);
				bt_all.setText("反选");
			}
			break;
		case R.id.bt_back:
			finish();
			break;
		}
	}

	private boolean hasSelectAll()
	{
		for (boolean b : checked)
		{
			if (!b)
			{
				return false;
			}
		}
		return true;
	}

	private boolean hasCancleAll()
	{
		for (boolean b : checked)
		{
			if (b)
			{
				return false;
			}
		}
		return true;
	}
}