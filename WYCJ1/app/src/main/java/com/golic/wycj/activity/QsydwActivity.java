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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.DataAnalyseEngine;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.QSYDW;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.MapLevelDictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.util.ChechUtil;
import com.golic.wycj.util.DictDialogUtil;
import com.golic.wycj.util.DictDialogUtil.MapDictCallback;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.NoticeUtil;
import com.golic.wycj.util.TypeUtil;

public class QsydwActivity extends BaseActivity implements OnClickListener
{
	private QSYDW attrs;
	private boolean isEdit = true;

	TextView tv_type;
	InputView input_name;
	InputView input_zname;
	InputView input_bz;
	InputView input_zczb;
	InputView input_frdb;
	InputView input_zcsj;
	InputView input_zcdd;

	private DataAnalyseEngine analyseEngine;
	private YwzyDaoImpl ywzyDaoImpl;
	private GgsjDaoImpl ggsjDaoImpl;
	private BzdzDaoImpl bzdzDaoImpl;
	// 添加公共数据照片
	private static final int TAKE_PHOTO = 58;
	private ArrayList<BzdzPhoto> deletePhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<BzdzPhoto> addPhotos = new ArrayList<BzdzPhoto>();
	private PhotoDaoImpl photoDaoImpl;
	private Button bt_take_photo;
	
	// *****************地址*********************
	private MPHM mphm;
	private ExtraDz extraDz;
	private DictUtil dict;
	LinearLayout ll_dz;
	InputView input_xzqh;
	InputView input_jlx;
	InputView input_mpqz;
	InputView input_mph;
	InputView input_mphz;
	InputView input_fh;
	InputView input_fhhz;
	InputView input_mply;
	InputView input_dzbz;

	InputView input_xqm;
	InputView input_zlqz;
	InputView input_zlh;
	InputView input_zlhz;
	InputView input_dyh;
	InputView input_lch;
	InputView input_lchz;
	InputView input_sh;
	InputView input_shhz;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qsydw);
		initData();
		initView();
	}

	private void initView()
	{
		tv_type = (TextView) findViewById(R.id.tv_type);
		input_name = (InputView) findViewById(R.id.input_name);
		input_zname = (InputView) findViewById(R.id.input_zname);
		input_bz = (InputView) findViewById(R.id.input_bz);
		input_zczb = (InputView) findViewById(R.id.input_zczb);
		input_frdb = (InputView) findViewById(R.id.input_frdb);
		input_zcsj = (InputView) findViewById(R.id.input_zcsj);
		input_zcdd = (InputView) findViewById(R.id.input_zcdd);

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
		input_zczb.getShowView().setText(attrs.ZCZB);
		input_frdb.getShowView().setText(attrs.FDDBR);
		input_zcsj.getShowView().setText(attrs.ZCSJ);
		input_zcdd.getShowView().setText(attrs.ZCDD);

		MPHM mphm = attrs.mphm;
		ExtraDz extraDz = attrs.extraDz;
		if (isEdit)
		{
			bt_take_photo.setText("照片");
			ll_dz = (LinearLayout) findViewById(R.id.ll_dz);
			ll_dz.setVisibility(View.VISIBLE);
			input_xzqh = (InputView) findViewById(R.id.input_xzqh);
			input_jlx = (InputView) findViewById(R.id.input_jlx);
			input_mpqz = (InputView) findViewById(R.id.input_mpqz);
			input_mph = (InputView) findViewById(R.id.input_mph);
			input_mphz = (InputView) findViewById(R.id.input_mphz);
			input_fh = (InputView) findViewById(R.id.input_fh);
			input_fhhz = (InputView) findViewById(R.id.input_fhhz);
			input_mply = (InputView) findViewById(R.id.input_mply);
			input_dzbz = (InputView) findViewById(R.id.input_dzbz);

			input_xqm = (InputView) findViewById(R.id.input_xqm);
			input_zlqz = (InputView) findViewById(R.id.input_zlqz);
			input_zlh = (InputView) findViewById(R.id.input_zlh);
			input_zlhz = (InputView) findViewById(R.id.input_zlhz);
			input_dyh = (InputView) findViewById(R.id.input_dyh);
			input_lch = (InputView) findViewById(R.id.input_lch);
			input_lchz = (InputView) findViewById(R.id.input_lchz);
			input_sh = (InputView) findViewById(R.id.input_sh);
			input_shhz = (InputView) findViewById(R.id.input_shhz);

			input_xzqh.fillDictInputView(dict, mphm.SSXQ);
			input_jlx.fillDictInputView(dict, mphm.JLX);
			input_mpqz.fillDictInputView(dict, mphm.MPQZ);
			input_mphz.fillDictInputView(dict, mphm.MPHZ);
			input_mph.getShowView().setText(mphm.MPH);
			input_fh.getShowView().setText(mphm.FH);
			input_fhhz.fillDictInputView(dict, mphm.FHHZ);
			input_mply.fillDictInputView(dict, mphm.MPLY);
			input_dzbz.getShowView().setText(mphm.BZ);
			if (extraDz != null)
			{
				input_xqm.getShowView().setText(extraDz.xqm);
				input_zlqz.fillDictInputView(dict, extraDz.preZlh);
				input_zlh.getShowView().setText(extraDz.zlh);
				input_zlhz.fillDictInputView(dict, extraDz.sufZlh);
				input_dyh.fillDictInputView(dict, extraDz.dyh);
				input_lch.getShowView().setText(extraDz.lch);
				input_lchz.fillDictInputView(dict, extraDz.sufLch);
				input_sh.getShowView().setText(extraDz.sh);
				input_shhz.fillDictInputView(dict, extraDz.sufSh);
			}
			else
			{
				input_zlhz.fillDictInputView(dict, "1");
				input_lchz.fillDictInputView(dict, "9");
				input_shhz.fillDictInputView(dict, "1");
			}
		}
	}

	private void initData()
	{
		mphm = new MPHM();
		// 添加拍照功能
		photoDaoImpl = new PhotoDaoImpl(getApplicationContext());
		dict = DictUtil.getInstance(getApplicationContext());
		bzdzDaoImpl = new BzdzDaoImpl(getApplicationContext());
		analyseEngine = new DataAnalyseEngine(getApplicationContext());
		ywzyDaoImpl = new YwzyDaoImpl(getApplicationContext());
		ggsjDaoImpl = new GgsjDaoImpl(getApplicationContext());
		attrs = (QSYDW) getIntent().getSerializableExtra("attrs");
		if (attrs.ID == null)
		{
			isEdit = false;
		}
		mphm.X=attrs.X;
		mphm.Y=attrs.Y;
	}

	private void fillBean(String name, String zname, String bz, String zczb,
			String frdb, String zcsj, String zcdd)
	{
		attrs.MC = name;
		attrs.ZMC = zname;
		attrs.BZ = bz;
		attrs.ZCZB = zczb;
		attrs.FDDBR = frdb;
		attrs.ZCSJ = zcsj;
		attrs.ZCDD = zcdd;
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
												QsydwActivity.this,
												new MapDictCallback()
												{
													@Override
													public void call(
															MapLevelDictObject mapLevelDictObject)
													{
														TypeUtil.changeType(
																mapLevelDictObject,
																attrs,
																QsydwActivity.this,
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
								QsydwActivity.this, WAIT_FOR_RESULT);
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
				String zczb = input_zczb.getShowView().getText().toString();
				String frdb = input_frdb.getShowView().getText().toString()
						.trim();
				String zcsj = input_zcsj.getShowView().getText().toString();
				String zcdd = input_zcdd.getShowView().getText().toString()
						.trim();

				boolean same = name.equals(attrs.MC) && zname.equals(attrs.ZMC)
						&& bz.equals(attrs.BZ) && zczb.equals(attrs.ZCZB)
						&& frdb.equals(attrs.FDDBR) && zcsj.equals(attrs.ZCSJ)
						&& zcdd.equals(attrs.ZCDD);
				if (isEdit)
				{
					// 将表单中的数据全部存储起来
					fillData();
					// 根据表单中的数据填充地址
					fillMlxz();

					if (!DataAnalyseEngine.dataFinishCheck(mphm, extraDz, this))
					{
						return;
					}

					// 判断地址数据是否发生修改
					boolean sameDz = mphm.MLXZ.equals(attrs.mphm.MLXZ);
					if (!sameDz)
					{
						if (attrs.mphm.equals(mphm))
						{
							if (attrs.extraDz == null)
							{
								if (extraDz != null)
								{
									if (analyseEngine.hasSameDz(mphm, extraDz))
									{
										NoticeUtil.showWarningDialog(this,
												"您采集的地址已存在,请检查输入,或者到工作查询中修改");
										return;
									}
								}
							}
							else
							{
								if (!attrs.extraDz.equals(extraDz))
								{
									if (analyseEngine.hasSameDz(mphm, extraDz))
									{
										NoticeUtil.showWarningDialog(this,
												"您采集的地址已存在,请检查输入,或者到工作查询中修改");
										return;
									}
								}
							}
						}
						else
						{
							if (analyseEngine.hasSameDz(mphm, extraDz))
							{
								NoticeUtil.showWarningDialog(this,
										"您采集的地址已存在,请检查输入,或者到工作查询中修改");
								return;
							}
						}
					}
					if (same && sameDz)
					{
						// 地址相同，数据相同
						//处理照片更新
						updatePhoto();
						setResult(RESULT_OK);
						finish();
					}
					else
					{
						// 只要有一个发生了修改，为了同步公共数据和自己的地址，他们的跟新时间需要保持一致，因此到这里公共数据和标准地址不论谁发生修改另外一个也需要跟新（至少是时间和修改人更新）
						attrs.fillXgxx();
						mphm.ID = attrs.mphm.ID;
						mphm.DJR = attrs.mphm.DJR;
						mphm.JWH = attrs.mphm.JWH;
						mphm.DJSJ = attrs.mphm.DJSJ;
						mphm.JWZRQ = attrs.mphm.JWZRQ;
						mphm.XGR = attrs.XGR;
						mphm.GXSJ = attrs.GXSJ;
						attrs.mphm = mphm;
						attrs.DZ = mphm.MLXZ;
						attrs.extraDz = extraDz;
						fillBean(name, zname, bz, zczb, frdb, zcsj, zcdd);
						// 到此数据在内存中跟新完成，同步到数据库中即可
						// 更新地址
						bzdzDaoImpl.updateGgsjDz(mphm, extraDz);
						// 唯一的区别在此，如果公共数据内容发生变化，需要重新检查类型和匹配业务专用数据
						if (!same)
						{
							// 数据不同，需要检查分类、匹配业务专用数据
							ChechUtil.editcheck(this, attrs, analyseEngine,
									ywzyDaoImpl, ggsjDaoImpl,photoDaoImpl,addPhotos,deletePhotos);
						}
						else
						{
							// 数据相同，直接更新
							int update = ggsjDaoImpl.updateGgsj(attrs);
							if (update > 0)
							{
								Source.updateGgsj(attrs);
							}
							updatePhoto();
							setResult(RESULT_OK);
							finish();
						}
					}
				}
				else
				{
					// 添加操作
					attrs.fillDjxx();
					fillBean(name, zname, bz, zczb, frdb, zcsj, zcdd);
					ChechUtil.addcheck(this, attrs, analyseEngine, ywzyDaoImpl,
							ggsjDaoImpl,photoDaoImpl,addPhotos,deletePhotos);
				}
			}
			break;
		}
	}

	

	/**
	 * 为表单数据mphm填充地址数据
	 */
	private void fillMlxz()
	{
		String xzqh = input_xzqh.getShowView().getText().toString();
		String jlx = input_jlx.getShowView().getText().toString();
		String mpqz = input_mpqz.getShowView().getText().toString();
		String mph = input_mph.getShowView().getText().toString();
		String mphz = input_mphz.getShowView().getText().toString();
		String fh = input_fh.getShowView().getText().toString();
		String fhhz = input_fhhz.getShowView().getText().toString();
		String mlxz = xzqh + jlx;
		if (!"".equals(mph))
		{
			mlxz += mpqz + mph + mphz;
			if (!"".equals(fh))
			{
				mlxz += fh + fhhz;
			}
		}
		if (extraDz != null)
		{
			String xqm = input_xqm.getShowView().getText().toString().trim();
			String zlh = input_zlh.getShowView().getText().toString();
			String lch = input_lch.getShowView().getText().toString();
			String sh = input_sh.getShowView().getText().toString();
			String zlqz = input_zlqz.getShowView().getText().toString();
			String zlhz = input_zlhz.getShowView().getText().toString();
			String dyh = input_dyh.getShowView().getText().toString();
			String lchz = input_lchz.getShowView().getText().toString();
			String shhz = input_shhz.getShowView().getText().toString();
			if (!TextUtils.isEmpty(xqm))
			{
				mlxz += xqm;
			}
			if (!TextUtils.isEmpty(zlh))
			{
				mlxz += zlqz + zlh + zlhz;
			}
			mlxz += dyh;
			if (!TextUtils.isEmpty(lch))
			{
				mlxz += lch + lchz;
			}
			if (!TextUtils.isEmpty(sh))
			{
				mlxz += sh + shhz;
			}
		}
		mphm.MLXZ = mlxz;
	}

	private void fillData()
	{
		DictObject xzqhDict = (DictObject) input_xzqh.getTag();
		DictObject jlxDict = (DictObject) input_jlx.getTag();
		DictObject mpqzDict = (DictObject) input_mpqz.getTag();
		DictObject mphzDict = (DictObject) input_mphz.getTag();
		DictObject fhhzDict = (DictObject) input_fhhz.getTag();
		DictObject mplyDict = (DictObject) input_mply.getTag();
		String mph = input_mph.getShowView().getText().toString();
		String fh = input_fh.getShowView().getText().toString();
		String bz = input_dzbz.getShowView().getText().toString().trim();
		if (xzqhDict != null)
		{
			mphm.SSXQ = xzqhDict.getDM();
		}
		if (jlxDict != null)
		{
			mphm.JLX = jlxDict.getDM();
		}
		if (mpqzDict != null)
		{
			mphm.MPQZ = mpqzDict.getDM();
		}
		if (mphzDict != null)
		{
			mphm.MPHZ = mphzDict.getDM();
		}
		if (fhhzDict != null)
		{
			mphm.FHHZ = fhhzDict.getDM();
		}
		if (mplyDict != null)
		{
			mphm.MPLY = mplyDict.getDM();
		}
		mphm.MPH = mph;
		mphm.FH = fh;
		mphm.BZ = bz;
		// 是否有额外地址
		String xqm = input_xqm.getShowView().getText().toString().trim();
		String zlh = input_zlh.getShowView().getText().toString();
		String lch = input_lch.getShowView().getText().toString();
		String sh = input_sh.getShowView().getText().toString();
		DictObject zlqzDict = (DictObject) input_zlqz.getTag();
		DictObject zlhzDict = (DictObject) input_zlhz.getTag();
		DictObject dyhDict = (DictObject) input_dyh.getTag();
		DictObject lchzDict = (DictObject) input_lchz.getTag();
		DictObject shhzDict = (DictObject) input_shhz.getTag();
		ExtraDz extraDz = new ExtraDz();
		extraDz.xqm = xqm;
		extraDz.zlh = zlh;
		extraDz.lch = lch;
		extraDz.sh = sh;
		if (zlqzDict != null)
		{
			extraDz.preZlh = zlqzDict.getDM();
		}
		if (zlhzDict != null)
		{
			extraDz.sufZlh = zlhzDict.getDM();
		}
		if (dyhDict != null)
		{
			extraDz.dyh = dyhDict.getDM();
		}
		if (lchzDict != null)
		{
			extraDz.sufLch = lchzDict.getDM();
		}
		if (shhzDict != null)
		{
			extraDz.sufSh = shhzDict.getDM();
		}
		if (!extraDz.isEmpty())
		{
			this.extraDz = extraDz;
		}
	}
	
	private void updatePhoto() {
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
				return null;
			}
		}.execute();
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
