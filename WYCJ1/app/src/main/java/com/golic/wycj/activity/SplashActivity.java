package com.golic.wycj.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.esri.core.symbol.PictureMarkerSymbol;
import com.golic.wycj.Constans;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.util.DbHelper;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.NoticeUtil;
import com.golic.wycj.util.PropertyUtil;
import com.golic.wycj.util.TransferUtil;

@SuppressLint("HandlerLeak")
public class SplashActivity extends BaseActivity
{
	private long startAppTime;
	private SharedPreferences sp;
	private static final int LOGIN_UI = 11;
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == LOGIN_UI)
			{
				toNextUi();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		startAppTime = System.currentTimeMillis();
		initEnvironment();
	}

	// 处理适配平板多个sdcard的情况
	private void initEnvironment()
	{
		File file = new File(Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
				+ "map");
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
		{
			NoticeUtil.showErrorDialog(this, "设备缺少sd卡无法运行,请先安装sd卡");
		}
		else
		{
			if (!file.exists())
			{
				file = new File(Constans.SOURCE_BASE_PATH + "2"
						+ Constans.SOURCE_PATH + "map");
				if (!file.exists())
				{
					NoticeUtil.showErrorDialog(this, "资源目录不存在！");
				}
				else
				{
					Constans.SOURCE_BASE_PATH += "2";
				}
			}
			String[] list = file.list();
			if (list != null && list.length > 0)
			{
				prepareDict();
			}
			else
			{
				NoticeUtil.showErrorDialog(this,
						"瓦片地图不存在,请将瓦片地图拷贝到‘golic/map’下面");
			}
		}
	}

	public void prepareDict()
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected void onPreExecute()
			{
				sp = getSharedPreferences("config", MODE_PRIVATE);
				Editor edit = sp.edit();
				boolean hasInitDict = sp.getBoolean("has_init_dict", false);
				if (!hasInitDict)
				{
					if (copyDict(Constans.DB_NAME))
					{
						edit.putBoolean("has_init_dict", true);
						edit.commit();
					}
					else
					{
						NoticeUtil.showErrorDialog(SplashActivity.this,
								"程序资源文件损坏或缺失");
					}
				}
			}

			@Override
			protected void onPostExecute(Void result)
			{
				if ((System.currentTimeMillis() - startAppTime) < 3000)
				{
					handler.sendEmptyMessageDelayed(LOGIN_UI, 3000
							+ startAppTime - System.currentTimeMillis());
				}
				else
				{
					handler.sendEmptyMessage(LOGIN_UI);
				}
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				initConfig();
				DictUtil.getInstance(getApplicationContext()).queryDict(
						DictUtil.TC_XZQH);
				prepareSource();
				return null;
			}

		}.execute();
	}

	protected void initConfig()
	{
		PropertyUtil.init();
	}

	/**
	 * 准备资源（初始化程序的数据和图片）
	 */
	private void prepareSource()
	{
		Resources resources = getResources();
		// 图片资源
		Source.bzdzDrawable = resources.getDrawable(R.drawable.zz);
		Source.bzdzSymbol = new PictureMarkerSymbol(Source.bzdzDrawable);
		Type[] values = Type.values();
		for (Type type : values)
		{
			Drawable drawable = resources.getDrawable(type.getSource());
			Source.ggsjDrawables.put(type, drawable);
			Source.symbols.put(type, new PictureMarkerSymbol(drawable));
		}
		Source.ywzyDrawables.put("网吧",
				resources.getDrawable(R.drawable.ywzy_wb));
		Source.ywzyDrawables.put("旅馆",
				resources.getDrawable(R.drawable.ywzy_lg));
		Source.ywzyDrawables.put("机构",
				resources.getDrawable(R.drawable.ywzy_jg));
		// 所有住宅
		Source.buildings = new BzdzDaoImpl(getApplicationContext())
				.findAllBuilding();
		// 所有公共数据
		new GgsjDaoImpl(getApplicationContext()).findAllGgsj();
		// 所有业务专用数据
		new YwzyDaoImpl(getApplicationContext()).findAll();
		// 所有名称匹配规则
		SQLiteDatabase database = DbHelper.getDatabase(getApplicationContext());
		Cursor cursor = database.query(DictUtil.NAME_MATCHING, null, null,
				null, null, null, null);
		while (cursor.moveToNext())
		{
			String ys = cursor.getString(cursor.getColumnIndex("YS"));
			String mc = cursor.getString(cursor.getColumnIndex("MC"));
			String matching = cursor.getString(cursor
					.getColumnIndex("MATCHING"));
			Source.nameMatching.put(ys, matching);
			Source.nameValues.put(ys, mc);
		}
		cursor.close();
	}

	private boolean copyDict(String dictName)
	{
		File target = SplashActivity.this.getDatabasePath(dictName);
		target.getParentFile().mkdirs();
		try
		{
			InputStream source = SplashActivity.this.getAssets().open(dictName);
			return TransferUtil.transfer(source, target);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	protected void toNextUi()
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}