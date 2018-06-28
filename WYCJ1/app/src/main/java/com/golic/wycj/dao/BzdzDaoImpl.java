package com.golic.wycj.dao;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.golic.wycj.Source;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.Floor;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.Room;
import com.golic.wycj.domain.Unit;
import com.golic.wycj.model.WorkDay;
import com.golic.wycj.util.DbHelper;

public class BzdzDaoImpl
{
	private Context context;
	private static String TABLE_NAME = "DZ_ZZXX";

	public BzdzDaoImpl(Context context)
	{
		super();
		this.context = context;
	}

	public ArrayList<String> getKeys()
	{
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = database.query(TABLE_NAME, new String[] { "ID" }, null,
				null, null, null, null);
		while (cursor.moveToNext())
		{
			list.add(cursor.getString(cursor.getColumnIndex("ID")));
		}
		return list;
	}

	public void deleteWorkDay(WorkDay day, boolean complete)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		String sql = "GXSJ=? and XGR=?";
		if (!complete)
		{
			sql = "GXSJ=? and XGR=? and DZLX!=0";
		}
		database.delete(TABLE_NAME, sql, new String[] { day.gxsj, day.gxr });
	}

	public TreeSet<WorkDay> queryWorkday()
	{
		TreeSet<WorkDay> set = new TreeSet<WorkDay>();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = database.query(true, TABLE_NAME, new String[] { "GXSJ",
				"XGR" }, null, null, null, null, "GXSJ", null);
		while (cursor.moveToNext())
		{
			WorkDay day = new WorkDay();
			day.gxsj = cursor.getString(cursor.getColumnIndex("GXSJ"));
			day.gxr = cursor.getString(cursor.getColumnIndex("XGR"));
			day.type = "标准地址";
			set.add(day);
		}
		return set;
	}

	public int updateGgsjDz(MPHM mphm, ExtraDz extraDz)
	{
		// int result = -1;
		// mphm.ID = attrs.mphm.ID;
		// mphm.DJR = attrs.mphm.DJR;
		// mphm.JWH = attrs.mphm.JWH;
		// mphm.DJSJ = attrs.mphm.DJSJ;
		// mphm.JWZRQ = attrs.mphm.JWZRQ;
		// mphm.XGR = attrs.XGR;
		// mphm.GXSJ = attrs.GXSJ;
		ContentValues values = getContentValues(mphm, extraDz);
		SQLiteDatabase database = DbHelper.getDatabase(context);
		return database.update(TABLE_NAME, values, "ID=?",
				new String[] { mphm.ID });
		// if (update > 0)
		// {
		// // TODO 这里使用判断条件是公共数据ID不是用的标准地址ID
		// result = new GgsjDaoImpl(context).updateDz(attrs.type.toString(),
		// mphm.ID, mphm.MLXZ);
		// if (result > 0)
		// {
		// attrs.DZ = mphm.MLXZ;
		// }
		// }
		// return result;
	}

	// 修改私宅数据变更
	public int updateBuilding(MPHM mphm)
	{
		ContentValues values = getContentValues(mphm);
		SQLiteDatabase database = DbHelper.getDatabase(context);
		return database.update(TABLE_NAME, values, "ID=?",
				new String[] { mphm.ID });
	}

	/**
	 * 采用事物的方式处理插入整栋楼房间数据的操作
	 * 
	 * @param building
	 * @return
	 */
	public long insert(Building building)
	{
		long result = 0;
		long baseInsert = 0;
		boolean isXqzz = false;
		if (building.baseBuilding.mphm.DZLX == 2)
		{
			isXqzz = true;
		}
		SQLiteDatabase database = DbHelper.getDatabase(context);
		database.beginTransaction();
		try
		{
			ContentValues values = getContentValues(building);
			baseInsert = database.insert(TABLE_NAME, null, values);
			if (isXqzz && baseInsert > 0)
			{
				values.put("DZBH", building.baseBuilding.mphm.ID);
				for (Unit unit : building.units)
				{
					fillUnitValue(values, unit);
					for (Floor floor : unit.floors)
					{
						fillFloorValue(values, floor);
						for (Room room : floor.rooms)
						{
							fillRoomValue(values, room);
							long insert = database.insert(TABLE_NAME, null,
									values);
							if (insert > 0)
							{
								result++;
							}
						}
					}
				}
			}
			database.setTransactionSuccessful();
		}
		finally
		{
			database.endTransaction();
		}
		if (isXqzz)
		{
			if (result > 0)
			{
				Source.update = true;
			}
		}
		else
		{
			result = baseInsert;
			if (baseInsert > 0)
			{
				Source.update = true;
			}
		}
		return result;
	}

	public long updateBuilding(Building building)
	{
		long result = 0;
		SQLiteDatabase database = DbHelper.getDatabase(context);
		MPHM mphm = building.baseBuilding.mphm;
		database.beginTransaction();
		try
		{
			int delete = database.delete(TABLE_NAME, "ID=? or DZBH=? ",
					new String[] { mphm.ID, mphm.ID });
			if (delete > 0)
			{
				ContentValues values = getContentValues(building);
				if (mphm.DZLX == 1)
				{
					result = database.insert(TABLE_NAME, null, values);
				}
				else
				{
					long baseInsert = database.insert(TABLE_NAME, null, values);
					if (baseInsert > 0)
					{
						values.put("DZBH", mphm.ID);
						for (Unit unit : building.units)
						{
							fillUnitValue(values, unit);
							for (Floor floor : unit.floors)
							{
								fillFloorValue(values, floor);
								for (Room room : floor.rooms)
								{
									fillRoomValue(values, room);
									long insert = database.insert(TABLE_NAME,
											null, values);
									if (insert > 0)
									{
										result++;
									}
								}
							}
						}
					}
				}
			}
			database.setTransactionSuccessful();
		}
		finally
		{
			database.endTransaction();
		}
		if (result > 0)
		{
			Source.updateBuilding(building);
		}
		return result;
	}

	private static void fillUnitValue(ContentValues values, Unit unit)
	{
		values.put("DYH", unit.dyh);
	}

	private static void fillFloorValue(ContentValues values, Floor floor)
	{
		values.put("LCH", floor.lch);
		values.put("LCHZ", floor.sufLch);
	}

	private static void fillRoomValue(ContentValues values, Room room)
	{
		room.zzbh = UUID.randomUUID().toString();
		values.put("DZLX", 3);
		values.put("SH", room.sh);
		values.put("SHHZ", room.sufSh);
		values.put("ID", room.zzbh);
	}

	// private ContentValues getContentValues(BaseBzdz bzdz)
	// {
	// ContentValues values = new ContentValues();
	// values.put("ID", bzdz.getId());
	// values.put("SSXQ", bzdz.getSsxq());
	// values.put("JLX", bzdz.getJlx());
	// values.put("MPQZ", bzdz.getMpqz());
	// values.put("MPH", bzdz.getMph());
	// values.put("MPHZ", bzdz.getMphz());
	// values.put("FH", bzdz.getFh());
	// values.put("FHHZ", bzdz.getFhhz());
	// values.put("MLXZ", bzdz.getMlxz());
	// values.put("JWZRQ", bzdz.getJwzrq());
	// values.put("JWH", bzdz.getJwh());
	// values.put("X", bzdz.getX());
	// values.put("Y", bzdz.getY());
	// values.put("DJR", bzdz.getDjr());
	// values.put("XGR", bzdz.getXgr());
	// values.put("DJSJ", bzdz.getDjsj());
	// values.put("XGSJ", bzdz.getXgsj());
	// values.put("MPLY", bzdz.getMply());
	// values.put("ZXBS", bzdz.getZxbs());
	//
	// ExtraDz extraDz = bzdz.getExtraDz();
	// if (extraDz != null)
	// {
	// values.put("XQM", extraDz.getXqm());
	// values.put("ZLH", extraDz.getZlh());
	// values.put("ZLQZ", extraDz.getPreZlh());
	// values.put("ZLHZ", extraDz.getSufZlh());
	// values.put("DYH", extraDz.getDyh());
	// values.put("LCH", extraDz.getLch());
	// values.put("LCHZ", extraDz.getSufLch());
	// values.put("SH", extraDz.getSh());
	// values.put("SHHZ", extraDz.getSufSh());
	// values.put("BZ", extraDz.getBz());
	// }
	// else
	// {
	// values.put("XQM", "");
	// values.put("ZLH", "");
	// values.put("ZLQZ", "");
	// values.put("ZLHZ", "");
	// values.put("DYH", "");
	// values.put("LCH", "");
	// values.put("LCHZ", "");
	// values.put("SH", "");
	// values.put("SHHZ", "");
	// values.put("BZ", "");
	// }
	// return values;
	// }
	//

	private static ContentValues getContentValues(Building building)
	{
		ContentValues values = getContentValues(building.baseBuilding.mphm);
		if (building.baseBuilding.mphm.DZLX == 2)
		{
			values.put("XQM", building.baseBuilding.xqm);
			values.put("ZLQZ", building.baseBuilding.preZlh);
			values.put("ZLH", building.baseBuilding.zlh);
			values.put("ZLHZ", building.baseBuilding.sufZlh);
		}
		return values;
	}

	// 20个字段
	private static ContentValues getContentValues(MPHM mphm)
	{
		ContentValues values = new ContentValues();
		values.put("ID", mphm.ID);
		values.put("SSXQ", mphm.SSXQ);
		values.put("JLX", mphm.JLX);
		values.put("MPQZ", mphm.MPQZ);
		values.put("MPH", mphm.MPH);
		values.put("MPHZ", mphm.MPHZ);
		values.put("FH", mphm.FH);
		values.put("FHHZ", mphm.FHHZ);
		values.put("MLXZ", mphm.MLXZ);
		values.put("JWZRQ", mphm.JWZRQ);
		values.put("JWH", mphm.JWH);
		values.put("X", mphm.X);
		values.put("Y", mphm.Y);
		values.put("DJR", mphm.DJR);
		values.put("XGR", mphm.XGR);
		values.put("DJSJ", mphm.DJSJ);
		values.put("GXSJ", mphm.GXSJ);
		values.put("MPLY", mphm.MPLY);
		values.put("DZLX", mphm.DZLX);
		values.put("BZ", mphm.BZ);
		return values;
	}

	// 20个字段
	private static ContentValues getContentValues(MPHM mphm, ExtraDz extraDz)
	{
		ContentValues values = getContentValues(mphm);
		if (extraDz != null && !extraDz.isEmpty())
		{
			values.put("XQM", extraDz.xqm);
			values.put("ZLQZ", extraDz.preZlh);
			values.put("ZLH", extraDz.zlh);
			values.put("ZLHZ", extraDz.sufZlh);
			values.put("DYH", extraDz.dyh);
			values.put("LCH", extraDz.lch);
			values.put("LCHZ", extraDz.sufLch);
			values.put("SH", extraDz.sh);
			values.put("SHHZ", extraDz.sufSh);
		}
		return values;
	}

	// 20个字段
	private static MPHM getMPHMFromCursor(Cursor c)
	{
		String ID = c.getString(c.getColumnIndex("ID"));
		String JWZRQ = c.getString(c.getColumnIndex("JWZRQ"));
		String SSXQ = c.getString(c.getColumnIndex("SSXQ"));
		String JLX = c.getString(c.getColumnIndex("JLX"));
		String MPQZ = c.getString(c.getColumnIndex("MPQZ"));
		String MPH = c.getString(c.getColumnIndex("MPH"));
		String MPHZ = c.getString(c.getColumnIndex("MPHZ"));
		String FH = c.getString(c.getColumnIndex("FH"));
		String FHHZ = c.getString(c.getColumnIndex("FHHZ"));
		String MPLY = c.getString(c.getColumnIndex("MPLY"));
		String MLXZ = c.getString(c.getColumnIndex("MLXZ"));
		String JWH = c.getString(c.getColumnIndex("JWH"));
		String X = c.getString(c.getColumnIndex("X"));
		String Y = c.getString(c.getColumnIndex("Y"));
		String DJR = c.getString(c.getColumnIndex("DJR"));
		String XGR = c.getString(c.getColumnIndex("XGR"));
		String DJSJ = c.getString(c.getColumnIndex("DJSJ"));
		String GXSJ = c.getString(c.getColumnIndex("GXSJ"));
		String BZ = c.getString(c.getColumnIndex("BZ"));
		int DZLX = c.getInt(c.getColumnIndex("DZLX"));
		return new MPHM(X, Y, DJR, DJSJ, JWZRQ, XGR, GXSJ, BZ, ID, SSXQ, JWH,
				MLXZ, JLX, MPH, MPQZ, MPHZ, FH, FHHZ, MPLY, DZLX);
	}

	// 9个字段
	private static ExtraDz getExtraDzFromCursor(Cursor c)
	{
		String xqm = c.getString(c.getColumnIndex("XQM"));
		String preZlh = c.getString(c.getColumnIndex("ZLQZ"));
		String zlh = c.getString(c.getColumnIndex("ZLH"));
		String sufZlh = c.getString(c.getColumnIndex("ZLHZ"));
		String dyh = c.getString(c.getColumnIndex("DYH"));
		String lch = c.getString(c.getColumnIndex("LCH"));
		String sufLch = c.getString(c.getColumnIndex("LCHZ"));
		String sh = c.getString(c.getColumnIndex("SH"));
		String sufSh = c.getString(c.getColumnIndex("SHHZ"));
		ExtraDz extraDz = new ExtraDz(xqm, preZlh, zlh, sufZlh, dyh, lch,
				sufLch, sh, sufSh);
		if (extraDz.isEmpty())
		{
			return null;
		}
		return extraDz;
	}

	private static BaseBuilding getBaseBuildingFromCursor(Cursor cursor)
	{
		MPHM mphm = getMPHMFromCursor(cursor);
		String ZLH = cursor.getString(cursor.getColumnIndex("ZLH"));
		String ZLQZ = cursor.getString(cursor.getColumnIndex("ZLQZ"));
		String ZLHZ = cursor.getString(cursor.getColumnIndex("ZLHZ"));
		String XQM = cursor.getString(cursor.getColumnIndex("XQM"));
		return new BaseBuilding(mphm, ZLQZ, ZLH, ZLHZ, XQM);
	}

	/**
	 * 插入公共数据的地址
	 * 
	 * @param mphm
	 * @param extraDz
	 * @return
	 */
	public long insert(MPHM mphm, ExtraDz extraDz)
	{
		mphm.ID = UUID.randomUUID().toString();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = getContentValues(mphm, extraDz);
		long insert = database.insert(TABLE_NAME, null, values);
		return insert;
	}

	/**
	 * 插入EXCEL表格中的数据到数据库
	 * 
	 * @param mphm
	 * @param extraDz
	 * @return
	 */
	public long insert(MPHM mphm, ExtraDz extraDz, String dzbh)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = getContentValues(mphm, extraDz);
		if (!TextUtils.isEmpty(dzbh))
		{
			values.put("DZBH", dzbh);
		}
		long insert = database.insert(TABLE_NAME, null, values);
		return insert;
	}

	//
	// public long insert(BaseBuilding baseBuilding)
	// {
	// BaseBzdz bzdz = baseBuilding.getBzdz();
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// ContentValues values = getContentValues(bzdz);
	// values.put("ZLH", baseBuilding.getZlh());
	// values.put("ZLQZ", baseBuilding.getPreZlh());
	// values.put("ZLHZ", baseBuilding.getSufZlh());
	// long insert = database.insert(Constant.TABLE_BASE_BZDZ, null, values);
	// return insert;
	// }
	//
	// @Override
	// public int update(BaseBzdz bzdz)
	// {
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// int update = database.update(Constant.TABLE_BASE_BZDZ,
	// getContentValues(bzdz), "ID=?", new String[] { bzdz.getId() });
	// return update;
	// }
	//
	public ArrayList<Building> findAllBuilding()
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		// 首先查询所有的私宅
		Cursor szCursor = database.query(TABLE_NAME, null, "DZLX=1", null,
				null, null, null);
		ArrayList<Building> list = new ArrayList<Building>();
		while (szCursor.moveToNext())
		{
			MPHM mphm = getMPHMFromCursor(szCursor);
			Building building = new Building(new BaseBuilding(mphm));
			list.add(building);
		}
		szCursor.close();
		// 查询所有小区住宅
		Cursor cursor = database.query(TABLE_NAME, null, "DZLX=2", null, null,
				null, null);
		while (cursor.moveToNext())
		{
			BaseBuilding baseBuilding = getBaseBuildingFromCursor(cursor);
			ArrayList<Unit> units = findAllUnit(database, baseBuilding.mphm.ID);
			Building building = new Building(baseBuilding,
					units.toArray(new Unit[units.size()]));
			list.add(building);
		}
		cursor.close();
		return list;
	}

	/**
	 * 查询某栋楼下的所有单元
	 * 
	 * @param database
	 * @param ID
	 * @return
	 */
	private static ArrayList<Unit> findAllUnit(SQLiteDatabase database,
			String DZBH)
	{
		// 去重查询出某个ID（某个BaseBuilding）下的所有单元并排序
		Cursor unitCursor = database.query(true, TABLE_NAME,
				new String[] { "DYH" }, "DZBH=? ", new String[] { DZBH }, null,
				null, "DYH", null);
		ArrayList<Unit> units = new ArrayList<Unit>();
		while (unitCursor.moveToNext())
		{
			Unit unit = new Unit();
			String DYH = unitCursor.getString(unitCursor.getColumnIndex("DYH"));
			unit.dyh = DYH;
			ArrayList<Floor> floors = findAllFloor(database, DZBH, unit);
			unit.floors = floors.toArray(new Floor[floors.size()]);
			units.add(unit);
		}
		unitCursor.close();
		return units;
	}

	/**
	 * 查询某个单元下的所有楼层，并且跟新单元的起始楼层号
	 * 
	 * @param database
	 * @param ID
	 * @param unit
	 * @return
	 */
	private static ArrayList<Floor> findAllFloor(SQLiteDatabase database,
			String DZBH, Unit unit)
	{
		// 去重查询某个单元下的所有楼层（这里没有使用排序是因为：1、楼层号使用的不是数字而且起始号码不是‘01’而是‘1’，这样排序会出现‘2’比‘10’大的情况；
		// 2、楼层号可以是负数也不方便做字符串排序； 3、插入的时候是按顺序的可以保证取出的时候也正确）
		Cursor floorCursor = database.query(true, TABLE_NAME, new String[] {
				"LCH", "LCHZ" }, "DZBH=? and DYH=?", new String[] { DZBH,
				unit.dyh }, null, null, null, null);
		ArrayList<Floor> floors = new ArrayList<Floor>();
		for (int i = 0; floorCursor.moveToNext(); i++)
		{
			Floor floor = new Floor();
			String LCH = floorCursor.getString(floorCursor
					.getColumnIndex("LCH"));
			String LCHZ = floorCursor.getString(floorCursor
					.getColumnIndex("LCHZ"));
			if (i == 0)
			{
				int startNum = Integer.parseInt(LCH);
				unit.startNum = startNum;
			}
			floor.lch = LCH;
			floor.sufLch = LCHZ;
			ArrayList<Room> rooms = findAllRoom(database, DZBH, unit.dyh, LCH);
			floor.rooms = rooms.toArray(new Room[rooms.size()]);
			floors.add(floor);
		}
		floorCursor.close();
		return floors;
	}

	/**
	 * 查询一个楼层下的所有房间
	 * 
	 * @param database
	 * @param DZBH
	 * @param DYH
	 * @param LCH
	 * @return
	 */
	private static ArrayList<Room> findAllRoom(SQLiteDatabase database,
			String DZBH, String DYH, String LCH)
	{
		Cursor roomCursor = database.query(true, TABLE_NAME, new String[] {
				"SH", "SHHZ", "ID" }, "DZBH=? and DYH=? and LCH=?",
				new String[] { DZBH, DYH, LCH }, null, null, "SH", null);
		ArrayList<Room> rooms = new ArrayList<Room>();
		while (roomCursor.moveToNext())
		{
			Room room = new Room();
			String SH = roomCursor.getString(roomCursor.getColumnIndex("SH"));
			String SHHZ = roomCursor.getString(roomCursor
					.getColumnIndex("SHHZ"));
			String ZZBH = roomCursor.getString(roomCursor.getColumnIndex("ID"));
			room.sh = SH;
			room.sufSh = SHHZ;
			room.zzbh = ZZBH;
			rooms.add(room);
		}
		roomCursor.close();
		return rooms;
	}

	public static void fillBaseAttrs(SQLiteDatabase database, BaseAttrs attrs,
			String ID)
	{
		Cursor c = database.query(TABLE_NAME, null, "ID=?",
				new String[] { ID }, null, null, null);
		if (c.moveToNext())
		{
			attrs.mphm = getMPHMFromCursor(c);
			attrs.extraDz = getExtraDzFromCursor(c);
		}
	}

	public int deleteMphm(String iD)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		return database.delete(TABLE_NAME, "ID=?", new String[] { iD });
	}

	public int deleteBuilding(String iD)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		int delete = database.delete(TABLE_NAME, "ID=? or DZBH=?",
				new String[] { iD, iD });
		// 同时删除照片
		database.delete("BZDZ_ZP", "BZDZ_ID=?", new String[] { iD });
		return delete;
	}
}