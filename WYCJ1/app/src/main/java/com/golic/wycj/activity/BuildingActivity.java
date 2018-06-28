package com.golic.wycj.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.golic.wycj.LoginUser;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.Floor;
import com.golic.wycj.domain.Room;
import com.golic.wycj.domain.Unit;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.util.DictUtil;

public class BuildingActivity extends BaseActivity implements OnClickListener
{
	private Building building;
	private LinearLayout ll_building;
	// 基本初始化数据控件
	private InputView iv_start_entrance_suf;// 开始单元后缀（后面的单元自动累加）
	private InputView iv_floor_suf;// 楼层后缀
	private InputView iv_start_floor_num;// 开始楼层号
	private InputView iv_room_suf;// 房间后缀

	private EditText et_entrance_num;// 单元数
	private EditText et_entrance_num_update;// 更新单元数
	private EditText et_floor_num;// 楼层数
	private EditText et_room_num;// 房间数

	private Button bt_init_building;
	private Button bt_update_building;
	private Button bt_back;
	private Button bt_take_photo;
	private Button bt_next;

	private boolean isEdit = false;
	/**
	 * 表示整个工作查询界面是否发生过更改
	 */
	private boolean update;
	private DictUtil dictUtil;
	private boolean hasInit;
	private Building cloneBuilding;
	/**
	 * 表示是否是私宅变成小区住宅（这种情况下主键ID不是null）
	 */
	private boolean isNew;
	private static final int TAKE_PHOTO = 58;
	private ArrayList<BzdzPhoto> deletePhotos = new ArrayList<BzdzPhoto>();
	private ArrayList<BzdzPhoto> addPhotos = new ArrayList<BzdzPhoto>();
	private PhotoDaoImpl photoDaoImpl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_building);
		initData();
		initView();
	}

	private void initView()
	{
		View ll_add = findViewById(R.id.ll_add);
		View ll_update = findViewById(R.id.ll_update);
		if (isEdit)
		{
			ll_add.setVisibility(View.GONE);
			ll_update.setVisibility(View.VISIBLE);
		}

		ll_building = (LinearLayout) findViewById(R.id.ll_building);

		iv_start_entrance_suf = (InputView) findViewById(R.id.iv_start_entrance_suf);
		iv_floor_suf = (InputView) findViewById(R.id.iv_floor_suf);
		iv_start_floor_num = (InputView) findViewById(R.id.iv_start_floor_num);
		iv_room_suf = (InputView) findViewById(R.id.iv_room_suf);

		et_entrance_num = (EditText) findViewById(R.id.et_entrance_num);
		et_entrance_num_update = (EditText) findViewById(R.id.et_entrance_num_update);
		et_floor_num = (EditText) findViewById(R.id.et_floor_num);
		et_room_num = (EditText) findViewById(R.id.et_room_num);

		bt_init_building = (Button) findViewById(R.id.bt_init_building);
		bt_update_building = (Button) findViewById(R.id.bt_update_building);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_take_photo = (Button) findViewById(R.id.bt_take_photo);
		bt_next = (Button) findViewById(R.id.bt_next);

		// 设置默认值
		if (isEdit)
		{
			bt_take_photo.setText("照片");
			bt_init_building.setText("更新");
			Unit unit = building.units[0];
			Floor floor = unit.floors[0];
			Room room = floor.rooms[0];
			iv_start_entrance_suf.fillDictInputView(dictUtil, unit.dyh);
			iv_floor_suf.fillDictInputView(dictUtil, floor.sufLch);
			iv_start_floor_num.getShowView().setText("" + unit.startNum);
			iv_room_suf.fillDictInputView(dictUtil, room.sufSh);
			building.drawForm(ll_building);
		}
		else
		{
			iv_start_entrance_suf.setTag(new DictObject("01", "一单元"));
			iv_start_entrance_suf.getShowView().setText("一单元");
			iv_floor_suf.setTag(new DictObject("9", "楼"));
			iv_floor_suf.getShowView().setText("楼");
			iv_start_floor_num.getShowView().setText("1");
			iv_room_suf.setTag(new DictObject("1", "室"));
			iv_room_suf.getShowView().setText("室");
		}
		bt_init_building.setOnClickListener(this);
		bt_update_building.setOnClickListener(this);
		bt_back.setOnClickListener(this);
		bt_take_photo.setOnClickListener(this);
		bt_next.setOnClickListener(this);
	}

	private void initData()
	{
		photoDaoImpl = new PhotoDaoImpl(getApplicationContext());
		dictUtil = DictUtil.getInstance(getApplicationContext());
		building = (Building) getIntent().getSerializableExtra("building");
		isNew = getIntent().getBooleanExtra("new", false);
		if (building.baseBuilding.mphm.ID != null && !isNew)
		{
			isEdit = true;
			update = getIntent().getBooleanExtra("update", false);
			cloneBuilding = JSON.parseObject(JSON.toJSONString(building),
					Building.class);
		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_init_building:
		case R.id.bt_update_building:
			initBuildingGraphic();
			break;
		case R.id.bt_back:
			finish();
			break;
		case R.id.bt_take_photo:
			Intent takePhotoIntent = new Intent(getApplicationContext(),
					TakePhotoActivity.class);
			takePhotoIntent.putExtra("ywid", building.baseBuilding.mphm.ID);
			takePhotoIntent.putExtra("isEdit", isEdit);
			startActivityForResult(takePhotoIntent, TAKE_PHOTO);
			break;
		case R.id.bt_next:
			if (isEdit)
			{
				if (!update)
				{
					if (!cloneBuilding.equals(building))
					{
						update = true;
					}
				}
				if (update)
				{
					building.baseBuilding.mphm.XGR = LoginUser.xm;
					building.baseBuilding.mphm.GXSJ = LoginUser.XGSJ_DATE
							.format(new Date());
					updateDate();
					Source.updateBuilding(building);
					setResult(RESULT_OK);
					finish();
				}
				else
				{
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
							setResult(RESULT_OK);
							finish();
						}
					}.execute();
					// photoDaoImpl.updatePhotos(deletePhotos,
					// addPhotos);
					// setResult(RESULT_OK);
					// finish();
				}
			}
			else
			{
				if (hasInit)
				{
					Date date = new Date();
					building.baseBuilding.mphm.JWZRQ = LoginUser.zrq;
					building.baseBuilding.mphm.DJR = LoginUser.xm;
					building.baseBuilding.mphm.DJSJ = LoginUser.DJSJ_TIME
							.format(date);
					building.baseBuilding.mphm.JWH = LoginUser.userName;

					if (building.baseBuilding.mphm.ID == null)
					{
						building.baseBuilding.mphm.ID = UUID.randomUUID()
								.toString();
					}
					building.baseBuilding.mphm.XGR = LoginUser.xm;
					building.baseBuilding.mphm.GXSJ = LoginUser.XGSJ_DATE
							.format(date);

					saveData();
					// Source.buildings.add(building);
					// setResult(RESULT_OK);
					// finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "请先初始化楼盘结构",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void updateDate()
	{
		new AsyncTask<Void, Void, Void>()
		{
			long updateNum = 0;

			@Override
			protected Void doInBackground(Void... params)
			{
				updateNum = new BzdzDaoImpl(getApplicationContext())
						.updateBuilding(building);
				photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				if (updateNum > 0)
				{
					Toast.makeText(getApplicationContext(), "更新成功",
							Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "更新数据失败！",
							Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}

	private void saveData()
	{
		new AsyncTask<Void, Void, Void>()
		{
			long insert = 0;

			@Override
			protected Void doInBackground(Void... params)
			{
				BzdzDaoImpl bzdzDaoImpl = new BzdzDaoImpl(
						getApplicationContext());
				if (isNew)
				{
					int delete = bzdzDaoImpl
							.deleteBuilding(building.baseBuilding.mphm.ID);
					if (delete > 0)
					{
						Source.removeBuilding(building.baseBuilding.mphm.ID);
						insert = new BzdzDaoImpl(getApplicationContext())
								.insert(building);
					}
				}
				else
				{
					insert = new BzdzDaoImpl(getApplicationContext())
							.insert(building);
				}
				System.out.println("插入的数据是：" + addPhotos);
				for(BzdzPhoto photo:addPhotos)
				{
					photo.bzdzId=building.baseBuilding.mphm.ID;
				}
				photoDaoImpl.updatePhotos(deletePhotos, addPhotos);
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				if (insert > 0)
				{
					Toast.makeText(getApplicationContext(), "插入成功！",
							Toast.LENGTH_LONG).show();
					Source.buildings.add(building);
					// TODO 是否改为再次控制update的值
					setResult(RESULT_OK);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "更新保存失败！",
							Toast.LENGTH_LONG).show();
					setResult(RESULT_OK);
					finish();
				}
			}
		}.execute();
	}

	private boolean checkInfo()
	{
		String dyh = iv_start_entrance_suf.getShowView().getText().toString();
		String lchz = iv_floor_suf.getShowView().getText().toString();
		String shhz = iv_room_suf.getShowView().getText().toString();
		String startFloorNum = iv_start_floor_num.getShowView().getText()
				.toString();

		if (startFloorNum.startsWith("0"))
		{
			Toast.makeText(this, "起始楼层号不能是0!", Toast.LENGTH_LONG).show();
			return false;
		}
		else if ("-".equals(startFloorNum))
		{
			Toast.makeText(this, "起始楼层号不能是-!", Toast.LENGTH_LONG).show();
			return false;
		}
		else if ("-0".equals(startFloorNum))
		{
			Toast.makeText(this, "起始楼层号不能是-0!", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!isEdit)
		{
			if (TextUtils.isEmpty(dyh))
			{
				Toast.makeText(this, "请填写开始单元后缀", Toast.LENGTH_LONG).show();
				return false;
			}
			if (TextUtils.isEmpty(lchz))
			{
				Toast.makeText(this, "请填写楼层后缀", Toast.LENGTH_LONG).show();
				return false;
			}
			if (TextUtils.isEmpty(shhz))
			{
				Toast.makeText(this, "请填写房间后缀", Toast.LENGTH_LONG).show();
				return false;
			}
			if (TextUtils.isEmpty(startFloorNum))
			{
				Toast.makeText(this, "请填写起始楼层号", Toast.LENGTH_LONG).show();
				return false;
			}
			String dys = et_entrance_num.getText().toString();
			String lcs = et_floor_num.getText().toString();
			String fjs = et_room_num.getText().toString();
			if (TextUtils.isEmpty(dys) || dys.startsWith("0"))
			{
				Toast.makeText(this, "请填写单元数", Toast.LENGTH_LONG).show();
				return false;
			}
			else if (TextUtils.isEmpty(lcs) || lcs.startsWith("0"))
			{
				Toast.makeText(this, "请填写楼层数", Toast.LENGTH_LONG).show();
				return false;
			}
			else if (TextUtils.isEmpty(fjs) || fjs.startsWith("0"))
			{
				Toast.makeText(this, "请填写房间数", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else
		{
			String entranceUpdateNumStr = et_entrance_num_update.getText()
					.toString();
			if (entranceUpdateNumStr.startsWith("0"))
			{
				Toast.makeText(this, "单元数不能是0！", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	private void initBuildingGraphic()
	{
		if (checkInfo())
		{
			if (!isEdit)
			{
				String entranceNumStr = et_entrance_num.getText().toString()
						.trim();
				String floorNumStr = et_floor_num.getText().toString().trim();
				String roomNumStr = et_room_num.getText().toString().trim();
				createBuilding(entranceNumStr, floorNumStr, roomNumStr);
				building.drawForm(ll_building);
				hasInit = true;
			}
			else
			{
				// 修改,如果发生变动则重新绘制否则不变
				DictObject entranceDict = (DictObject) iv_start_entrance_suf
						.getTag();
				DictObject floorDict = (DictObject) iv_floor_suf.getTag();
				DictObject roomDict = (DictObject) iv_room_suf.getTag();
				String startFloorNum = iv_start_floor_num.getShowView()
						.getText().toString();
				String entranceUpdateNumStr = et_entrance_num_update.getText()
						.toString();

				boolean entranceNumChanged = false;
				if (!"".equals(entranceUpdateNumStr))
				{
					entranceNumChanged = building.updateEntranceNum(Integer
							.parseInt(entranceUpdateNumStr));
				}
				boolean dyhChanged = building.updateEntranceSuf(entranceDict);
				boolean sufLchChanged = building.updateFloorSuf(floorDict);
				boolean floorStartNumChanged = building
						.updateFloorStartNum(Integer.parseInt(startFloorNum));
				boolean sufShChanged = building.updateRoomSuf(roomDict);

				if (entranceNumChanged || dyhChanged || sufLchChanged
						|| floorStartNumChanged || sufShChanged)
				{
					update = true;
					building.drawForm(ll_building);
				}
				else
				{
					Toast.makeText(this, "未发生更改", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void createBuilding(String entranceNumStr, String floorNumStr,
			String roomNumStr)
	{
		int entranceNum = Integer.parseInt(entranceNumStr);
		int floorNum = Integer.parseInt(floorNumStr);
		int roomNum = Integer.parseInt(roomNumStr);
		building.units = new Unit[entranceNum];
		Unit[] units = building.units;

		DictObject entranceDict = (DictObject) iv_start_entrance_suf.getTag();
		DictObject floorDict = (DictObject) iv_floor_suf.getTag();
		DictObject roomDict = (DictObject) iv_room_suf.getTag();
		String text = iv_start_floor_num.getShowView().getText().toString();
		int floorStartNum = Integer.parseInt(text);
		for (int i = 0; i < entranceNum; i++)
		{
			units[i] = new Unit();
			units[i].dyh = DictUtil.nextDm(entranceDict.getDM(), i);
			Floor[] floors = new Floor[floorNum];
			units[i].floors = floors;
			units[i].startNum = floorStartNum;
			for (int j = 0; j < floorNum; j++)
			{
				floors[j] = new Floor();
				int lc = floorStartNum + j;

				if (floorStartNum < 0 && lc >= 0)
				{
					lc += 1;
				}
				floors[j].lch = ("" + lc);
				floors[j].sufLch = floorDict.getDM();
				Room[] rooms = new Room[roomNum];
				floors[j].rooms = rooms;
				for (int k = 0; k < roomNum; k++)
				{
					rooms[k] = new Room();
					rooms[k].sh = floors[j].lch + DictUtil.nextDm("01", k);
					rooms[k].sufSh = roomDict.getDM();
					// TODO 设置房间UUID
				}
			}
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