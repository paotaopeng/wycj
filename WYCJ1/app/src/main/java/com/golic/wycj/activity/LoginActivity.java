package com.golic.wycj.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.golic.wycj.Constans;
import com.golic.wycj.LoginUser;
import com.golic.wycj.R;
import com.golic.wycj.dao.LoginEngine;

public class LoginActivity extends BaseActivity
{
	private EditText username_edit;
	private EditText password_edit;
	private String[] fileList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initData();
		initView();
	}

	private void initView()
	{
		username_edit = (EditText) findViewById(R.id.username_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		RadioGroup rg_config = (RadioGroup) findViewById(R.id.rg_config);
//		RadioButton rb_low = (RadioButton) findViewById(R.id.rb_low);
//		RadioButton rb_normal = (RadioButton) findViewById(R.id.rb_normal);
//		RadioButton rb_high = (RadioButton) findViewById(R.id.rb_high);
		rg_config.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch (checkedId)
				{
				case R.id.rb_low:
					LoginUser.MARKER_NUM = 10;
					break;
				case R.id.rb_normal:
					LoginUser.MARKER_NUM = 30;
					break;
				case R.id.rb_high:
					LoginUser.MARKER_NUM = 0;
					break;
				}
			}
		});
	}

	private void initData()
	{
		File file = new File(Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
				+ "map");
		fileList = file.list();
	}

	// 处理登陆验证的逻辑
	public void login(View view)
	{
		String userName = username_edit.getText().toString().trim();
		String password = password_edit.getText().toString().trim();
		LoginUser.userName = userName;
		boolean login = new LoginEngine(getApplicationContext()).login(
				userName, password);
		if (!login)
		{
			Toast.makeText(getApplicationContext(), "用户名或密码不正确！",
					Toast.LENGTH_LONG).show();
			return;

		}
		if (fileList.length == 1)
		{
			Constans.MAP_SOURCE = Constans.SOURCE_BASE_PATH
					+ Constans.SOURCE_PATH + "map/" + fileList[0] + "";
			pass();
		}
		else
		{
			chooseSource();
		}
	}

	// 兼容加载多个瓦片地图
	private void chooseSource()
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("检测到多个瓦片资源")
				.setSingleChoiceItems(fileList, -1,
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								Constans.MAP_SOURCE = Constans.SOURCE_BASE_PATH
										+ Constans.SOURCE_PATH + "map/"
										+ fileList[which];
								dialog.dismiss();
								pass();
							}
						}).create();
		dialog.setCancelable(false);
		dialog.show();
	}

	// // TODO 允许更换责任区
	// private void chooseZrq()
	// {
	// final DictUtil util = DictUtil.getInstance(this);
	// Builder builder = new AlertDialog.Builder(this).setTitle("选择责任区")
	// .setIcon(R.drawable.down_icon).setNegativeButton("取消", null);
	//
	// View view = LayoutInflater.from(this).inflate(
	// R.layout.dialog_list_layout, null);
	// final ListView dialogList = (ListView) view
	// .findViewById(R.id.dialog_list);
	// LinearLayout fliter = (LinearLayout) view
	// .findViewById(R.id.dialog_filter);
	// final ArrayList<? extends DictObject> dicts = util
	// .queryDict(DictUtil.TC_ZRQ);
	// @SuppressWarnings("unchecked")
	// final ArrayList<DictObject> showDatas = (ArrayList<DictObject>) dicts
	// .clone();
	// final DictListAdapterSingleChoose adapter = new
	// DictListAdapterSingleChoose(
	// this, showDatas);
	// dialogList.setAdapter(adapter);
	// // 控制过滤框弹出的条件
	// if (dicts.size() > 30)
	// {
	// fliter.setVisibility(View.VISIBLE);
	// Button fliterBtn = (Button) view
	// .findViewById(R.id.dialog_filter_btn);
	// final EditText fliterEt = (EditText) view
	// .findViewById(R.id.dialog_filter_et);
	// fliterBtn.setOnClickListener(new View.OnClickListener()
	// {
	// @Override
	// public void onClick(View v)
	// {
	// String fliter = fliterEt.getText().toString();
	// showDatas.clear();
	// showDatas.addAll(DictDialogUtil.filterLevelDicts(dicts,
	// fliter));
	// adapter.notifyDataSetChanged();
	// }
	// });
	// }
	// else
	// {
	// fliter.setVisibility(View.GONE);
	// }
	//
	// builder.setView(view);
	// final AlertDialog dialog = builder.create();
	// dialogList.setOnItemClickListener(new OnItemClickListener()
	// {
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id)
	// {
	// Object item = dialogList.getAdapter().getItem(position);
	// if (item != null)
	// {
	// DictObject dic = (DictObject) item;
	// LoginUser.zrq = dic.getDM();
	// LoginUser.zrqMc = dic.getMC();
	// SQLiteDatabase db = SQLiteDatabase.openDatabase(
	// LoginActivity.this
	// .getDatabasePath(Constans.DB_NAME)
	// .toString(), null,
	// SQLiteDatabase.OPEN_READWRITE);
	// Cursor cursor = db.query(DictUtil.TC_ZRQ,
	// new String[] { "FW" }, " DM=?",
	// new String[] { LoginUser.zrq }, null, null, null);
	// if (cursor.moveToNext())
	// {
	// LoginUser.zrqFw = cursor.getString(0);
	// }
	// cursor.close();
	// }
	// dialog.dismiss();
	// }
	// });
	// dialog.show();
	// dialog.setOnDismissListener(new OnDismissListener()
	// {
	// @Override
	// public void onDismiss(DialogInterface dialog)
	// {
	// Intent intent = new Intent(LoginActivity.this,
	// MapActivity.class);
	// startActivity(intent);
	// finish();
	// }
	// });
	// }

	// TODO 开启不同的耗能模式
	private void pass()
	{
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
		finish();
	}
}