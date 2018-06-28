package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.dao.DataAnalyseEngine;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.JTSS;
import com.golic.wycj.model.MapLevelDictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.util.ChechUtil;
import com.golic.wycj.util.DictDialogUtil;
import com.golic.wycj.util.DictDialogUtil.MapDictCallback;
import com.golic.wycj.util.TypeUtil;

public class JtssActivity extends BaseActivity implements OnClickListener
{
	private JTSS attrs;
	private boolean isEdit = true;

	TextView tv_type;
	InputView input_name;
	InputView input_zname;
	InputView input_bz;
	private DataAnalyseEngine analyseEngine;
	private YwzyDaoImpl ywzyDaoImpl;
	private GgsjDaoImpl ggsjDaoImpl;
	
	// 添加公共数据照片
	private static final int TAKE_PHOTO = 58;
	private ArrayList<BzdzPhoto> deletePhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<BzdzPhoto> addPhotos = new ArrayList<BzdzPhoto>();
	private PhotoDaoImpl photoDaoImpl;
	private Button bt_take_photo;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jtss);
		initData();
		initView();
	}

	// TODO 8月25
	private void initView()
	{
		tv_type = (TextView) findViewById(R.id.tv_type);
		input_name = (InputView) findViewById(R.id.input_name);
		input_zname = (InputView) findViewById(R.id.input_zname);
		input_bz = (InputView) findViewById(R.id.input_bz);

		Button bt_back = (Button) findViewById(R.id.bt_back);
		Button bt_save = (Button) findViewById(R.id.bt_save);
		
		// 添加照片按钮
		bt_take_photo = (Button) findViewById(R.id.bt_take_photo);
		bt_take_photo.setOnClickListener(this);
		tv_type.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_save.setOnClickListener(this);
		// 初始化数据
		input_name.getShowView().setText(attrs.MC);
		input_zname.getShowView().setText(attrs.ZMC);
		input_bz.getShowView().setText(attrs.BZ);
		if(isEdit){
			bt_take_photo.setText("照片");
		}
	}

	private void initData()
	{
		//添加拍照
		photoDaoImpl = new PhotoDaoImpl(getApplicationContext());
		analyseEngine = new DataAnalyseEngine(getApplicationContext());
		ywzyDaoImpl = new YwzyDaoImpl(getApplicationContext());
		ggsjDaoImpl = new GgsjDaoImpl(getApplicationContext());
		attrs = (JTSS) getIntent().getSerializableExtra("attrs");
		if (attrs.ID == null)
		{
			isEdit = false;
		}
	}

	private void fillBean(String name, String zname, String bz)
	{
		attrs.MC = name;
		attrs.ZMC = zname;
		attrs.BZ = bz;
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.tv_type:
			if (isEdit)
			{
				// 修改数据类型会将原来的数据删除
				new Builder(this)
						.setTitle("提示")
						.setMessage("修改数据类型会将原来的数据删除")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int which)
									{
										DictDialogUtil.changeType(
												JtssActivity.this,
												new MapDictCallback()
												{
													@Override
													public void call(
															MapLevelDictObject mapLevelDictObject)
													{
														TypeUtil.changeType(
																mapLevelDictObject,
																attrs,
																JtssActivity.this,
																WAIT_FOR_RESULT);
													}
												});
									}
								}).setNegativeButton("取消", null).show();
			}
			else
			{
				DictDialogUtil.changeType(this, new MapDictCallback()
				{
					@Override
					public void call(MapLevelDictObject mapLevelDictObject)
					{
						TypeUtil.changeType(mapLevelDictObject, attrs,
								JtssActivity.this, WAIT_FOR_RESULT);
					}
				});
			}
			break;
		case R.id.bt_back:
			finish();
			break;
		case R.id.bt_take_photo:
			Intent takePhotoIntent = new Intent(getApplicationContext(),
					TakePhotoActivity.class);
			if (isEdit)
			{
				takePhotoIntent.putExtra("ywid", attrs.ID);
				takePhotoIntent.putExtra("isEdit", true);
			}
			startActivityForResult(takePhotoIntent, TAKE_PHOTO);
			break;
		case R.id.bt_save:
			if (TextUtils.isEmpty(input_name.getShowView().getText().toString()
					.trim()))
			{
				Toast.makeText(getApplicationContext(), "名称未填写",
						Toast.LENGTH_LONG).show();
			}
			else
			{
				String name = input_name.getShowView().getText().toString()
						.trim();
				String zname = input_zname.getShowView().getText().toString()
						.trim();
				String bz = input_bz.getShowView().getText().toString().trim();
				boolean same = name.equals(attrs.MC) && zname.equals(attrs.ZMC)
						&& bz.equals(attrs.BZ);

				if (isEdit)
				{
					// boolean sameDz = analyseEngine.hasSameDz(attrs.mphm,
					// attrs.extraDz);
					// if (sameDz)
					// {
					// NoticeUtil.showWarningDialog(this,
					// "您采集的地址已存在，请检查输入,或者到工作查询中修改");
					// return;
					// }
					if (!same)
					{
						attrs.fillXgxx();
						fillBean(name, zname, bz);
						ChechUtil.editcheck(this, attrs, analyseEngine,
								ywzyDaoImpl, ggsjDaoImpl,photoDaoImpl,addPhotos,deletePhotos);
					}
					else
					{
						//处理照片更新
						new AsyncTask<Void, Void, Void>()
						{
							@Override
							protected Void doInBackground(Void... params)
							{
								photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
								return null;
							}
						}.execute();
						setResult(RESULT_OK);
						finish();
					}
				}
				else
				{
					// 添加操作
					attrs.mphm = null;
					attrs.DZ = null;
					attrs.fillDjxx();
					fillBean(name, zname, bz);
					ChechUtil.addcheck(this, attrs, analyseEngine, ywzyDaoImpl,
							ggsjDaoImpl,photoDaoImpl,addPhotos,deletePhotos);
				}
			}
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK)
		{
			// 将拍照数据保存
			deletePhotos.clear();
			deletePhotos.addAll((Collection<? extends BzdzPhoto>) data
					.getSerializableExtra("delete"));
			addPhotos.clear();
			addPhotos.addAll((Collection<? extends BzdzPhoto>) data
					.getSerializableExtra("add"));
		}
	}
}