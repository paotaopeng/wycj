package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.golic.wycj.LoginUser;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.DataAnalyseEngine;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.NoticeUtil;
import com.golic.wycj.util.TypeUtil;

/**
 * 只处理添加业务
 * 
 * @author luo
 * 
 */
public class FormBaseActivity extends BaseActivity implements OnClickListener
{
	private boolean isBuilding = false;
	private DictUtil dict = null;
	// private static final int WAIT_FOR_RESULT = 11;

	// *******************共有控件***********************
	InputView input_xzqh;
	InputView input_jlx;
	InputView input_mpqzsx;
	InputView input_mpqz;
	InputView input_mph;
	InputView input_mphz;
	InputView input_fh;
	InputView input_fhhz;
	InputView input_mply;
	InputView input_bz;
	private Button bt_next;
	private Button bt_take_photo;
	private Button bt_back;

	// ***********************公共数据地址***************************
	private BaseAttrs attrs;
	LinearLayout ll_more;
	LinearLayout ll_more_image;
	LinearLayout ll_extra_dz;
	InputView input_xqm;
	InputView input_zlqz;
	InputView input_zlh;
	InputView input_zlhz;
	InputView input_dyh;
	InputView input_lch;
	InputView input_lchz;
	InputView input_sh;
	InputView input_shhz;
	private boolean hasExtraDz;

	// ************************住宅******************************
	LinearLayout ll_zz;
	CheckBox cb_sz;
	LinearLayout ll_zz_xq;
	InputView input_building_xqm;
	InputView input_building_zlqz;
	InputView input_building_zlh;
	InputView input_building_zlhz;

	/**
	 * 记录修改之前的原值
	 */
	private Building oldBuilding;
	/**
	 * 记录页面上操作的mphm变量（只与页面上的控件值有关）
	 */
	private MPHM mphm;
	private ExtraDz extraDz;
	private BaseBuilding baseBuilding;
	private static final int TAKE_PHOTO = 58;
	private ArrayList<BzdzPhoto> deletePhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<BzdzPhoto> addPhotos = new ArrayList<BzdzPhoto>();
	private PhotoDaoImpl photoDaoImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_base);
		initData();
		initView();
	}

	private void initView()
	{
		// *******************共有控件***********************
		input_xzqh = (InputView) findViewById(R.id.input_xzqh);
		input_jlx = (InputView) findViewById(R.id.input_jlx);
		input_mpqzsx = (InputView) findViewById(R.id.input_mpqzsx);
		input_mpqz = (InputView) findViewById(R.id.input_mpqz);
		input_mph = (InputView) findViewById(R.id.input_mph);
		input_mphz = (InputView) findViewById(R.id.input_mphz);
		input_fh = (InputView) findViewById(R.id.input_fh);
		input_fhhz = (InputView) findViewById(R.id.input_fhhz);
		input_mply = (InputView) findViewById(R.id.input_mply);
		input_bz = (InputView) findViewById(R.id.input_bz);
		bt_next = (Button) findViewById(R.id.bt_next);
		bt_take_photo = (Button) findViewById(R.id.bt_take_photo);
		bt_back = (Button) findViewById(R.id.bt_back);

		ll_more = (LinearLayout) findViewById(R.id.ll_more);

		bt_back.setOnClickListener(this);
		bt_take_photo.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		// TODO
		input_mphz.getShowView().addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.toString().endsWith("-"))
				{
					// 更改副号和副号后缀
					input_fh.getShowView().setText("1");
					input_fhhz.fillDictInputView(dict, "1");
				}
			}
		});
		input_mply.getShowView().addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.toString().startsWith("新")
						|| s.toString().startsWith("无"))
				{
					// 清空门牌号、副号
					input_mph.getShowView().setText("");
					input_fh.getShowView().setText("");
				}
			}
		});
		if (isBuilding)
		{
			mphm.DZLX = oldBuilding.baseBuilding.mphm.DZLX;
			// 住宅
			ll_zz = (LinearLayout) findViewById(R.id.ll_zz);
			cb_sz = (CheckBox) findViewById(R.id.cb_sz);
			ll_zz_xq = (LinearLayout) findViewById(R.id.ll_zz_xq);
			input_building_xqm = (InputView) findViewById(R.id.input_building_xqm);
			input_building_zlqz = (InputView) findViewById(R.id.input_building_zlqz);
			input_building_zlh = (InputView) findViewById(R.id.input_building_zlh);
			input_building_zlhz = (InputView) findViewById(R.id.input_building_zlhz);
			cb_sz.setOnClickListener(this);
			ll_more.setVisibility(View.GONE);
			ll_zz.setVisibility(View.VISIBLE);
			// 住宅修改的话填充控件，否则控件设置默认值
			if (oldBuilding.baseBuilding.mphm.ID != null)
			{
				// 住宅修改
				fillMphmView(oldBuilding.baseBuilding.mphm);
				if (oldBuilding.baseBuilding.mphm.DZLX == 2)
				{
					// 到这里说明一定是小区住宅修改
					input_building_xqm.getShowView().setText(
							oldBuilding.baseBuilding.xqm);
					input_building_zlh.getShowView().setText(
							oldBuilding.baseBuilding.zlh);
					input_building_zlqz.fillDictInputView(dict,
							oldBuilding.baseBuilding.preZlh);
					input_building_zlhz.fillDictInputView(dict,
							oldBuilding.baseBuilding.sufZlh);
				}
				else
				{
					bt_take_photo.setText("照片");
				}
			}
			else
			{
				// 住宅添加
				fillMphmView(mphm);
				if (Source.extraDz != null
						&& !TextUtils.isEmpty(Source.extraDz.xqm))
				{
					input_building_xqm.getShowView()
							.setText(Source.extraDz.xqm);
				}
				input_building_zlhz
						.fillDictInputView(dict, baseBuilding.sufZlh);
			}
		}
		else
		{
			// 公共数据(默认显示)
			ll_more_image = (LinearLayout) findViewById(R.id.ll_more_image);
			ll_extra_dz = (LinearLayout) findViewById(R.id.ll_extra_dz);
			input_xqm = (InputView) findViewById(R.id.input_xqm);
			input_zlqz = (InputView) findViewById(R.id.input_zlqz);
			input_zlh = (InputView) findViewById(R.id.input_zlh);
			input_zlhz = (InputView) findViewById(R.id.input_zlhz);
			input_dyh = (InputView) findViewById(R.id.input_dyh);
			input_lch = (InputView) findViewById(R.id.input_lch);
			input_lchz = (InputView) findViewById(R.id.input_lchz);
			input_sh = (InputView) findViewById(R.id.input_sh);
			input_shhz = (InputView) findViewById(R.id.input_shhz);
			ll_more_image.setOnClickListener(this);
			// 公共数据添加添加默认值
			fillMphmView(mphm);
			if (Source.extraDz != null)
			{
				ExtraDz extraDz = Source.extraDz;
				ll_more_image.setVisibility(View.GONE);
				ll_extra_dz.setVisibility(View.VISIBLE);
				hasExtraDz = true;
				input_xqm.getShowView().setText(extraDz.xqm);
				input_zlh.getShowView().setText(extraDz.zlh);
				input_lch.getShowView().setText(extraDz.lch);
				input_sh.getShowView().setText(extraDz.sh);
				input_zlqz.fillDictInputView(dict, extraDz.preZlh);
				input_zlhz.fillDictInputView(dict, extraDz.sufZlh);
				input_dyh.fillDictInputView(dict, extraDz.dyh);
				input_lchz.fillDictInputView(dict, extraDz.sufLch);
				input_shhz.fillDictInputView(dict, extraDz.sufSh);
			}
		}
	}

	/**
	 * 填充表单，
	 */
	private void fillMphmView(MPHM mphm)
	{
		input_xzqh.fillDictInputView(dict, mphm.SSXQ);
		input_jlx.fillDictInputView(dict, mphm.JLX);
		input_mpqz.fillDictInputView(dict, mphm.MPQZ);
		input_mphz.fillDictInputView(dict, mphm.MPHZ);
		input_fhhz.fillDictInputView(dict, mphm.FHHZ);
		input_mply.fillDictInputView(dict, mphm.MPLY);
		input_mpqzsx.getShowView().setText(Source.mpqzsx);
		input_mph.getShowView().setText(mphm.MPH);
		input_fh.getShowView().setText(mphm.FH);
		input_bz.getShowView().setText(mphm.BZ);
		if (isBuilding)
		{
			if (mphm.DZLX == 1)
			{
				bt_next.setText("保存");
				bt_take_photo.setVisibility(View.VISIBLE);
				cb_sz.setChecked(true);
				ll_zz_xq.setVisibility(View.GONE);
			}
		}
	}

	private void fillData()
	{
		DictObject xzqhDict = (DictObject) input_xzqh.getTag();
		DictObject jlxDict = (DictObject) input_jlx.getTag();
		DictObject mpqzDict = (DictObject) input_mpqz.getTag();
		DictObject mphzDict = (DictObject) input_mphz.getTag();
		DictObject fhhzDict = (DictObject) input_fhhz.getTag();
		DictObject mplyDict = (DictObject) input_mply.getTag();
		Source.mpqzsx = input_mpqzsx.getShowView().getText().toString();
		String mph = input_mph.getShowView().getText().toString();
		String fh = input_fh.getShowView().getText().toString();
		String bz = input_bz.getShowView().getText().toString().trim();
		if (xzqhDict != null)
		{
			mphm.SSXQ = xzqhDict.getDM();
		}
		else
		{
			mphm.SSXQ = "";
		}
		if (jlxDict != null)
		{
			mphm.JLX = jlxDict.getDM();
		}
		else
		{
			mphm.JLX = "";
		}
		if (mpqzDict != null)
		{
			mphm.MPQZ = mpqzDict.getDM();
		}
		else
		{
			mphm.MPQZ = "";
		}
		if (mphzDict != null)
		{
			mphm.MPHZ = mphzDict.getDM();
		}
		else
		{
			mphm.MPHZ = "";
		}
		if (fhhzDict != null)
		{
			mphm.FHHZ = fhhzDict.getDM();
		}
		else
		{
			mphm.FHHZ = "";
		}
		if (mplyDict != null)
		{
			mphm.MPLY = mplyDict.getDM();
		}
		else
		{
			mphm.MPLY = "";
		}
		mphm.MPH = mph;
		mphm.FH = fh;
		if ((!"".equals(Source.mpqzsx)) && (!bz.startsWith("(")))
		{
			bz = "(" + Source.mpqzsx + ")" + bz;
		}
		mphm.BZ = bz;
		if (isBuilding)
		{
			// 私宅
			if (cb_sz.isChecked())
			{
				mphm.DZLX = 1;
			}
			else
			{
				mphm.DZLX = 2;
				DictObject zlqzDict = (DictObject) input_building_zlqz.getTag();
				DictObject zlhzDict = (DictObject) input_building_zlhz.getTag();
				String xqm = input_building_xqm.getShowView().getText()
						.toString().trim();
				String zlh = input_building_zlh.getShowView().getText()
						.toString();

				if (zlqzDict != null)
				{
					baseBuilding.preZlh = zlqzDict.getDM();
				}
				else
				{
					baseBuilding.preZlh = "";
				}
				if (zlhzDict != null)
				{
					baseBuilding.sufZlh = zlhzDict.getDM();
				}
				else
				{
					baseBuilding.sufZlh = "";
				}
				baseBuilding.xqm = xqm;
				baseBuilding.zlh = zlh;
				if (!TextUtils.isEmpty(xqm))
				{
					this.extraDz = new ExtraDz();
					this.extraDz.xqm = xqm;
				}
			}
		}
		else
		{
			// 是否有额外地址
			if (hasExtraDz)
			{
				String xqm = input_xqm.getShowView().getText().toString()
						.trim();
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
				else
				{
					extraDz.preZlh = "";
				}
				if (zlhzDict != null)
				{
					extraDz.sufZlh = zlhzDict.getDM();
				}
				else
				{
					extraDz.sufZlh = "";
				}
				if (dyhDict != null)
				{
					extraDz.dyh = dyhDict.getDM();
				}
				else
				{
					extraDz.dyh = "";
				}
				if (lchzDict != null)
				{
					extraDz.sufLch = lchzDict.getDM();
				}
				else
				{
					extraDz.sufLch = "";
				}
				if (shhzDict != null)
				{
					extraDz.sufSh = shhzDict.getDM();
				}
				else
				{
					extraDz.sufSh = "";
				}
				if (!extraDz.isEmpty())
				{
					this.extraDz = extraDz;
				}
			}
		}
		// 只用Source.mphm中的值，并不直接使用Source.mphm对象,除了保存的时候其余时候不修改Source.mphm的值
		Source.mphm = mphm;
		Source.extraDz = this.extraDz;
	}

	// 数据完整性检查
	private boolean dataFinishCheck()
	{
		String mply = mphm.MPLY;
		if (TextUtils.isEmpty(mphm.SSXQ))
		{
			NoticeUtil.showWarningDialog(this, "行政区划未填！");
			return false;
		}
		if (TextUtils.isEmpty(mphm.JLX))
		{
			NoticeUtil.showWarningDialog(this, "街路巷未填！");
			return false;
		}
		if (TextUtils.isEmpty(mply))
		{
			NoticeUtil.showWarningDialog(this, "门牌来源未填！");
			return false;
		}

		if (!TextUtils.isEmpty(mphm.MPH) && TextUtils.isEmpty(mphm.MPHZ))
		{
			NoticeUtil.showWarningDialog(this, "存在门牌号必须填写门牌后缀");
			return false;
		}
		if (!TextUtils.isEmpty(mphm.FH) && TextUtils.isEmpty(mphm.FHHZ))
		{
			NoticeUtil.showWarningDialog(this, "存在副号必须填写副号后缀");
			return false;
		}
		if (!TextUtils.isEmpty(mphm.FH) && TextUtils.isEmpty(mphm.MPH))
		{
			NoticeUtil.showWarningDialog(this, "存在副号必须填写门牌号");
			return false;
		}

		if (isBuilding)
		{
			if (!TextUtils.isEmpty(baseBuilding.zlh)
					&& TextUtils.isEmpty(baseBuilding.sufZlh))
			{
				NoticeUtil.showWarningDialog(this, "存在幢楼号必须填写幢楼后缀");
				return false;
			}

			if (TextUtils.isEmpty(mphm.MPH))
			{
				if ("5".equals(mply) || "6".equals(mply))
				{
					if (TextUtils.isEmpty(mphm.BZ))
					{
						NoticeUtil.showWarningDialog(this,
								"采集住宅建议确认门牌号,如果确实无法获取,请在备注中详细说明");
						return false;
					}
				}
				else
				{
					NoticeUtil.showWarningDialog(this, "门牌号未填！");
					return false;
				}
			}
			else
			{
				if ("5".equals(mply) || "6".equals(mply))
				{
					NoticeUtil.showWarningDialog(this, "门牌来源与实际输入值不符！");
					return false;
				}
			}

			// 非私宅要确定幢楼信息是否完整
			if (mphm.DZLX == 2)
			{
				if (TextUtils.isEmpty(baseBuilding.zlh))
				{
					NoticeUtil.showWarningDialog(this, "幢楼号未填！");
					return false;
				}
			}
		}
		else
		{
			// 公共数据完整性判断
			if (TextUtils.isEmpty(mphm.MPH))
			{
				if (!"5".equals(mply) && !"6".equals(mply))
				{
					NoticeUtil.showWarningDialog(this, "门牌号未填！");
					return false;
				}
			}
			else
			{
				if ("5".equals(mply) || "6".equals(mply))
				{
					NoticeUtil.showWarningDialog(this, "门牌来源与实际输入值不符！");
					return false;
				}
			}

			// 如果用户填写了额外地址，还需要判断额外地址的完整性
			if (extraDz != null)
			{
				if (!TextUtils.isEmpty(extraDz.zlh)
						&& TextUtils.isEmpty(extraDz.sufZlh))
				{
					NoticeUtil.showWarningDialog(this, "存在幢楼号必须填写幢楼后缀");
					return false;
				}
				if (!TextUtils.isEmpty(extraDz.lch)
						&& TextUtils.isEmpty(extraDz.sufLch))
				{
					NoticeUtil.showWarningDialog(this, "存在楼层号必须填写楼层后缀");
					return false;
				}
				if (!TextUtils.isEmpty(extraDz.sh)
						&& TextUtils.isEmpty(extraDz.sufSh))
				{
					NoticeUtil.showWarningDialog(this, "存在室号必须填写室号后缀");
					return false;
				}
				if (!TextUtils.isEmpty(extraDz.sh)
						&& TextUtils.isEmpty(extraDz.lch))
				{
					NoticeUtil.showWarningDialog(this, "存在室号必须填写楼层号");
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasSameDz()
	{
		boolean hasSameDz = false;
		if (isBuilding)
		{
			MPHM oldMphm = oldBuilding.baseBuilding.mphm;
			if (oldBuilding.baseBuilding.mphm.ID != null
					&& oldMphm.DZLX == mphm.DZLX)
			{
				// 如果是修改，重复性验证只针对必填字段
				if (oldMphm.equals(mphm))
				{
					if (oldMphm.DZLX == 2)
					{
						if (!oldBuilding.baseBuilding.zlh
								.equals(baseBuilding.zlh))
						{
							hasSameDz = new DataAnalyseEngine(
									getApplicationContext())
									.hasSameDz(baseBuilding);
						}
					}
				}
				else
				{

					hasSameDz = new DataAnalyseEngine(getApplicationContext())
							.hasSameDz(baseBuilding);
				}
			}
			else
			{
				hasSameDz = new DataAnalyseEngine(getApplicationContext())
						.hasSameDz(baseBuilding);
			}
		}
		else
		{
			hasSameDz = new DataAnalyseEngine(getApplicationContext())
					.hasSameDz(mphm, extraDz);
		}
		return hasSameDz;
	}

	// 这里要做的流程是：获取BaseAttrs或者building来判断现在在做那种业务
	private void initData()
	{
		photoDaoImpl = new PhotoDaoImpl(getApplicationContext());
		dict = DictUtil.getInstance(getApplicationContext());
		Intent intent = getIntent();
		attrs = (BaseAttrs) intent.getSerializableExtra("attrs");
		if (attrs == null)
		{
			isBuilding = true;
			oldBuilding = (Building) intent.getSerializableExtra("building");
			mphm = new MPHM(oldBuilding.baseBuilding.mphm.X,
					oldBuilding.baseBuilding.mphm.Y, Source.mphm);
			// 如果上次保存的是公共数据，这里要将地址类型初始化为小区住宅
			if (mphm.DZLX == 0)
			{
				mphm.DZLX = 2;
			}
			baseBuilding = new BaseBuilding(mphm);
		}
		else
		{
			mphm = new MPHM(attrs.X, attrs.Y, Source.mphm);
			mphm.DZLX = 0;
		}
	}

	@Override
	public void onClick(final View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			finish();
			break;
		case R.id.bt_take_photo:
			Intent takePhotoIntent = new Intent(getApplicationContext(),
					TakePhotoActivity.class);
			takePhotoIntent.putExtra("ywid", oldBuilding.baseBuilding.mphm.ID);
			if (isBuilding && oldBuilding.baseBuilding.mphm.ID != null)
			{
				takePhotoIntent.putExtra("isEdit", true);
			}
			startActivityForResult(takePhotoIntent, TAKE_PHOTO);
			break;
		case R.id.bt_next:
			// TODO
			fillData();
			if (dataFinishCheck())
			{
				if (hasSameDz())
				{
					NoticeUtil.showWarningDialog(this,
							"该地址已经采集过,请检查输入内容或到工作查询中修改");
				}
				else
				{
					// 全部验证通过
					fillMlxz();
					if (isBuilding && oldBuilding.baseBuilding.mphm.ID != null)
					{
						// 修改操作：首先判断是否发生了更新
						MPHM oldMphm = oldBuilding.baseBuilding.mphm;
						if (mphm.DZLX == 1)
						{
							// 私宅-->私宅
							if (oldMphm.DZLX == 1)
							{
								if (!oldMphm.MLXZ.equals(mphm.MLXZ)
										|| !oldMphm.BZ.equals(mphm.BZ)
										|| !oldMphm.MPLY.equals(mphm.MPLY))
								{
									// 有修改私宅
									fillXgxx(oldMphm);
									int update = new BzdzDaoImpl(
											getApplicationContext())
											.updateBuilding(mphm);
									if (update > 0)
									{
										oldBuilding.baseBuilding.mphm = mphm;
										Source.updateBuilding(oldBuilding);
									}
								}
							}
							else
							{
								// 小区-->私宅
								fillXgxx(oldMphm);
								oldBuilding = new Building(new BaseBuilding(
										mphm));
								new BzdzDaoImpl(getApplicationContext())
										.updateBuilding(oldBuilding);
							}
							//处理照片更新
							new AsyncTask<Void, Void, Void>()
							{
								@Override
								protected Void doInBackground(Void... params)
								{
									photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
									return null;
								}

								@Override
								protected void onPostExecute(Void result)
								{
									finish();
								}
							}.execute();
						}
						else
						{
							// 私宅-->小区
							// 小区-->小区
							Intent intent = new Intent(getApplicationContext(),
									BuildingActivity.class);
							if (oldMphm.MLXZ.equals(mphm.MLXZ)
									&& oldMphm.BZ.equals(mphm.BZ)
									&& oldMphm.MPLY.equals(mphm.MPLY))
							{
								// 无修改
								intent.putExtra("building", oldBuilding);
								intent.putExtra("update", false);
								finish();
							}
							else
							{
								// 有修改
								fillXgxx(oldMphm);
								oldBuilding.baseBuilding.preZlh = baseBuilding.preZlh;
								oldBuilding.baseBuilding.zlh = baseBuilding.zlh;
								oldBuilding.baseBuilding.sufZlh = baseBuilding.sufZlh;
								oldBuilding.baseBuilding.xqm = baseBuilding.xqm;
								if (oldBuilding.baseBuilding.mphm.DZLX == 1)
								{
									// 私宅变小区
									intent.putExtra("new", true);
								}
								oldBuilding.baseBuilding.mphm = mphm;
								intent.putExtra("building", oldBuilding);
								intent.putExtra("update", true);
//								new BzdzDaoImpl(getApplicationContext())
//										.updateBuilding(oldBuilding);
								finish();
							}
							startActivityForResult(intent, WAIT_FOR_RESULT);
						}
					}
					else
					{
						// 添加操作,只需要给mphm添加操作信息数据，然后传递给下一个界面
						fillDjxx();
						if (isBuilding)
						{
							if (mphm.DZLX == 1)
							{
								mphm.ID = UUID.randomUUID().toString();
								oldBuilding = new Building(new BaseBuilding(
										mphm));
								// 添加的是一个私宅
								new BzdzDaoImpl(getApplicationContext())
										.insert(oldBuilding);
								Source.buildings.add(oldBuilding);
								System.out.println("处理的addPhotos：" + addPhotos);
								for (BzdzPhoto photo : addPhotos)
								{
									photo.bzdzId = mphm.ID;
								}
								System.out.println("最终插入的数据：" + addPhotos);
								photoDaoImpl.updatePhotos(deletePhotos,
										addPhotos);
								setResult(RESULT_OK);
								finish();
							}
							else
							{
								oldBuilding = new Building(baseBuilding);
								Intent intent = new Intent(
										getApplicationContext(),
										BuildingActivity.class);
								intent.putExtra("building", oldBuilding);
								startActivityForResult(intent, WAIT_FOR_RESULT);
							}
						}
						else
						{
							// 如果是公共数据
							Type type = attrs.type;
							attrs.mphm = mphm;
							attrs.extraDz = extraDz;
							// TypeUtil.go2GgsjActivity(attrs, this);
							Intent intent = new Intent(getApplicationContext(),
									TypeUtil.getTargetClass(type));
							intent.putExtra("attrs", TypeUtil.getBean(attrs));
							startActivityForResult(intent, WAIT_FOR_RESULT);
						}
					}
				}
			}
			break;
		case R.id.cb_sz:
			// checked为checkbox状态改变之后的状态值
			boolean checked = cb_sz.isChecked();
			// cb_sz.setChecked(!checked);
			if (checked)
			{
				// 小区变私宅
				ll_zz_xq.setVisibility(View.GONE);
				mphm.DZLX = 1;
				bt_next.setText("保存");
				bt_take_photo.setVisibility(View.VISIBLE);
			}
			else
			{
				// 私宅变成小区
				ll_zz_xq.setVisibility(View.VISIBLE);
				mphm.DZLX = 2;
				bt_next.setText("下一步");
				bt_take_photo.setVisibility(View.GONE);
			}
			break;
		case R.id.ll_more_image:
			ll_more_image.setVisibility(View.GONE);
			ll_extra_dz.setVisibility(View.VISIBLE);
			hasExtraDz = true;
			input_zlhz.fillDictInputView(dict, "1");
			input_lchz.fillDictInputView(dict, "9");
			input_shhz.fillDictInputView(dict, "1");
			break;
		}
	}

	/**
	 * 填充修改信息
	 * 
	 * @param oldMphm
	 */
	private void fillXgxx(MPHM oldMphm)
	{
		mphm.ID = oldMphm.ID;
		mphm.DJR = oldMphm.DJR;
		mphm.JWH = oldMphm.JWH;
		mphm.DJSJ = oldMphm.DJSJ;
		mphm.JWZRQ = oldMphm.JWZRQ;
		mphm.XGR = LoginUser.xm;
		mphm.GXSJ = LoginUser.XGSJ_DATE.format(new Date());
	}

	/**
	 * 填充更新信息
	 */
	private void fillDjxx()
	{
		mphm.JWH = LoginUser.userName;
		mphm.JWZRQ = LoginUser.zrq;
		mphm.DJR = LoginUser.xm;
		mphm.DJSJ = LoginUser.DJSJ_TIME.format(new Date());
		mphm.XGR = LoginUser.xm;
		mphm.GXSJ = LoginUser.XGSJ_DATE.format(new Date());
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
			mlxz += Source.mpqzsx + mpqz + mph + mphz;
			if (!"".equals(fh))
			{
				mlxz += fh + fhhz;
			}
		}
		// 如果是添加修改小区幢楼信息，地址要精确到幢楼号
		if (isBuilding && mphm.DZLX == 2)
		{
			String xqm = input_building_xqm.getShowView().getText().toString()
					.trim();
			String zlqz = input_building_zlqz.getShowView().getText()
					.toString();
			String zlh = input_building_zlh.getShowView().getText().toString();
			String zlhz = input_building_zlhz.getShowView().getText()
					.toString();
			mlxz += xqm;
			if (!"".equals(zlh))
			{
				mlxz += zlqz + zlh + zlhz;
			}
		}
		if (!isBuilding && extraDz != null)
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