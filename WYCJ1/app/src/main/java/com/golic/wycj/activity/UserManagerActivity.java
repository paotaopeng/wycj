package com.golic.wycj.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.dao.LoginEngine;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.User;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.NoticeUtil;

public class UserManagerActivity extends BaseActivity
{
	private LoginEngine loginEngine;
	private ArrayList<User> list;
	private ListView lv_user;
	private BaseAdapter adapter;
	private DictUtil dictUtil;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_manager);
		initData();
		initView();
	}

	private void initView()
	{
		lv_user = (ListView) findViewById(R.id.lv_user);
		adapter = new MyAdapter();
		lv_user.setAdapter(adapter);
		lv_user.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// 修改操作
				User user = list.get(position);
				showEditDialog("修改用户", user);
			}
		});
		lv_user.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id)
			{
				final User user = list.get(position);
				new Builder(UserManagerActivity.this)
						.setTitle("提示")
						.setMessage("您是否要删除" + user.xm + "?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{
										int delete = loginEngine.delete(user);
										if (delete > 0)
										{
											list.remove(position);
											adapter.notifyDataSetChanged();
										}
									}
								}).setNegativeButton("取消", null).show();
				return false;
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
						R.layout.item_user, null);
				holder = new ViewHolder();
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_password = (TextView) convertView
						.findViewById(R.id.tv_password);
				holder.tv_xm = (TextView) convertView.findViewById(R.id.tv_xm);
				holder.tv_zrq = (TextView) convertView
						.findViewById(R.id.tv_zrq);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			User user = list.get(position);
			holder.tv_user_name.setText(user.userName);
			holder.tv_password.setText(user.password);
			holder.tv_xm.setText(user.xm);
			holder.tv_zrq.setText(dictUtil.getDictValue(DictUtil.TC_ZRQ,
					user.zrq));
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
		TextView tv_user_name;
		TextView tv_password;
		TextView tv_xm;
		TextView tv_zrq;
	}

	// 获取当前的所有用户
	private void initData()
	{
		dictUtil = DictUtil.getInstance(getApplicationContext());
		loginEngine = new LoginEngine(getApplicationContext());
		list = loginEngine.findAll();
	}

	public void showEditDialog(String title, final User user)
	{
		View view = View.inflate(this, R.layout.dialog_user_edit, null);
		final InputView input_user_name = (InputView) view
				.findViewById(R.id.input_user_name);
		final InputView input_password = (InputView) view
				.findViewById(R.id.input_password);
		final InputView input_xm = (InputView) view.findViewById(R.id.input_xm);
		final InputView input_zrq = (InputView) view
				.findViewById(R.id.input_zrq);

		if (user.id != null)
		{
			input_user_name.getShowView().setText(user.userName);
			input_password.getShowView().setText(user.password);
			input_xm.getShowView().setText(user.xm);
			input_zrq.fillDictInputView(dictUtil, user.zrq);
		}
		Button bt_save = (Button) view.findViewById(R.id.bt_save);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		final AlertDialog dialog = new Builder(this).setTitle(title)
				.setView(view).show();
		bt_cancle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		bt_save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String userName = input_user_name.getShowView().getText()
						.toString().trim();
				String password = input_password.getShowView().getText()
						.toString();
				String xm = input_xm.getShowView().getText().toString().trim();
				String zrqMc = input_zrq.getShowView().getText().toString()
						.trim();
				if (TextUtils.isEmpty(userName))
				{
					NoticeUtil.showWarningDialog(UserManagerActivity.this,
							"帐号未填写！");
					return;
				}
				if (TextUtils.isEmpty(password))
				{
					NoticeUtil.showWarningDialog(UserManagerActivity.this,
							"密码未填写！");
					return;
				}
				if (TextUtils.isEmpty(xm))
				{
					NoticeUtil.showWarningDialog(UserManagerActivity.this,
							"姓名未填写！");
					return;
				}
				if (TextUtils.isEmpty(zrqMc))
				{
					NoticeUtil.showWarningDialog(UserManagerActivity.this,
							"责任区未指定！");
					return;
				}
				DictObject zrqDict = (DictObject) input_zrq.getTag();
				user.userName = userName;
				user.password = password;
				user.xm = xm;
				user.zrq = zrqDict.getDM();
				if (user.id != null)
				{
					// 修改用户
					loginEngine.update(user);
					dialog.dismiss();
				}
				else
				{
					// 添加用户
					long add = loginEngine.add(user);
					if (add > 0)
					{
						list.add(user);
						Toast.makeText(getApplicationContext(), "新增成功",
								Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void add(View view)
	{
		showEditDialog("添加用户", new User());
	}

	public void back(View view)
	{
		finish();
	}
}