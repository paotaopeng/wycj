package com.golic.wycj.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.golic.wycj.Constans;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.util.ExcelUtil;
import com.golic.wycj.util.NoticeUtil;

public class DataImportActivity extends BaseActivity
{
	private BzdzDaoImpl bzdzDaoImpl;
	private GgsjDaoImpl ggsjDaoImpl;
	private ListView lv_file;
	private MyAdapter adapter;
	private List<File> list;
	private YwzyDaoImpl ywzyDaoImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_import);
		initData();
		initView();
	}

	private void initView()
	{
		lv_file = (ListView) findViewById(R.id.lv_file);
		adapter = new MyAdapter();
		lv_file.setAdapter(adapter);
	}

	private class MyAdapter extends BaseAdapter
	{
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			File file = list.get(position);
			if (convertView == null)
			{
				TextView textView = new TextView(getApplicationContext());
				textView.setLayoutParams(new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT, 100));
				textView.setTextSize(20);
				textView.setTextColor(Color.BLACK);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				convertView = textView;
			}
			TextView tv = (TextView) convertView;
			tv.setText(file.getName());
			return tv;
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

	private void initData()
	{
		ywzyDaoImpl = new YwzyDaoImpl(getApplicationContext());
		bzdzDaoImpl = new BzdzDaoImpl(getApplicationContext());
		ggsjDaoImpl = new GgsjDaoImpl(getApplicationContext());
		String baseDir = Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
				+ Constans.DATA_PATH + "import";
		File dirfile = new File(baseDir);
		if (!dirfile.exists())
		{
			dirfile.mkdir();
		}
		File[] files = dirfile.listFiles();
		if(files==null){
			list=new ArrayList<File>();
		}else{
			list = Arrays.asList(files);
		}
		if (list.size() == 0)
		{
			NoticeUtil.showWarningDialog(this,
					"请将需要导入的文件放到‘golic/data/import’目录下");
		}
	}

	public void importData(View view)
	{
		if (list.size() == 0)
		{
			NoticeUtil.showWarningDialog(this,
					"请将需要导入的文件放到‘golic/data/import’目录下");
			return;
		}
		new AsyncTask<Void, Void, Void>()
		{
			ProgressDialog dialog;

			@Override
			protected void onPreExecute()
			{
				dialog = ProgressDialog.show(DataImportActivity.this, "请稍候...",
						"数据正在导入中");
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				for (File file : list)
				{
					ExcelUtil.importFile(file, ywzyDaoImpl, bzdzDaoImpl,
							ggsjDaoImpl);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				dialog.dismiss();
				Source.update = true;
				NoticeUtil.showWarningDialog(DataImportActivity.this,
						"数据导入完成，将在下次登录时生效！");
			}
		}.execute();
	}

	public void back(View view)
	{
		finish();
	}
}