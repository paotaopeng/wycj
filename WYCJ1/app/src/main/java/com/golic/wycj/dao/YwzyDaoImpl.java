package com.golic.wycj.dao;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.golic.wycj.Source;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.util.DbHelper;

public class YwzyDaoImpl
{
	private Context context;
	private static final String[] YWZY_TABLES = new String[] { "YWZY_WB",
			"YWZY_LG", "YWZY_JG" };
	public static final String[] YWZY_NAMES = new String[] { "网吧", "旅馆", "机构" };

	public YwzyDaoImpl(Context context)
	{
		super();
		this.context = context;
	}

	private Ywzy getBeanFromCursor(Cursor cursor)
	{
		Ywzy ywzy = new Ywzy();
		String id = cursor.getString(cursor.getColumnIndex("ID"));
		// String xzqh = cursor.getString(cursor.getColumnIndex("XZQH"));
		String jwzrq = cursor.getString(cursor.getColumnIndex("JWZRQ"));
		String jlx = cursor.getString(cursor.getColumnIndex("JLX"));
		String mph = cursor.getString(cursor.getColumnIndex("MPH"));
		String mlxz = cursor.getString(cursor.getColumnIndex("MLXZ"));
		String mc = cursor.getString(cursor.getColumnIndex("MC"));
		String x = cursor.getString(cursor.getColumnIndex("X"));
		String y = cursor.getString(cursor.getColumnIndex("Y"));
		int bs = cursor.getInt(cursor.getColumnIndex("BS"));
		String ggsjId = cursor.getString(cursor.getColumnIndex("GGSJ_ID"));
		ywzy.setGgsj_id(ggsjId);
		ywzy.setId(id);
		// ywzy.setXzqh(xzqh);
		ywzy.setJwzrq(jwzrq);
		ywzy.setJlx(jlx);
		ywzy.setMph(mph);
		ywzy.setMlxz(mlxz);
		ywzy.setMc(mc);
		ywzy.setX(x);
		ywzy.setY(y);
		ywzy.setBs(bs);
		return ywzy;
	}

	/**
	 * 查询出所有的业务专用数据
	 * 
	 * @return
	 */
	public void findAll()
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		for (int i = 0; i < YWZY_TABLES.length; i++)
		{
			String tableName = YWZY_TABLES[i];
			Cursor cursor = database.query(tableName, null, "BS=1", null, null,
					null, null);
			ArrayList<Ywzy> ywzys = new ArrayList<Ywzy>();
			while (cursor.moveToNext())
			{
				Ywzy ywzy = getBeanFromCursor(cursor);
				ywzy.setName(YWZY_NAMES[i]);
				ywzys.add(ywzy);
			}
			Source.ywzys.put(YWZY_NAMES[i], ywzys);
			cursor.close();
		}
	}

	// public void clearAll()
	// {
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// for (int i = 0; i < YWZY_TABLES.length; i++)
	// {
	// String tableName = YWZY_TABLES[i];
	// database.delete(tableName, " BS=1 or BS=2 ", null);
	// }
	// }

	// public int updatePointWhitGgsj(Ggsj ggsj)
	// {
	// String fldm = ggsj.getFldm();
	// String id = ggsj.getId();
	// String table = "";
	// if ("B031001".equals(fldm))
	// {
	// // 说明该公共数据是一个网吧
	// table = YWZY_TABLES[0];
	// }
	// else if (fldm.startsWith("B0311"))
	// {
	// // 说明可能是一个旅馆
	//
	// table = YWZY_TABLES[2];
	// }
	// else if (fldm.startsWith("B03") || fldm.startsWith("B07"))
	// {
	// // 说明可能是一个机构
	// table = YWZY_TABLES[3];
	// }
	// else
	// {
	// return -1;
	// }
	// ContentValues values = new ContentValues();
	// values.put("X", ggsj.getX());
	// values.put("Y", ggsj.getY());
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// int update = database.update(table, values, " GGSJ_ID=? ",
	// new String[] { id });
	// return update;
	// }

	/**
	 * 检查某个公共数据是否被关联过，关联过返回true
	 * 
	 * @param ggsjId
	 * @param name
	 * @return
	 */
	public boolean relationCheck(String ggsjId, String name)
	{
		int index = -1;
		for (int i = 0; i < YWZY_NAMES.length; i++)
		{
			if (YWZY_NAMES[i].equals(name))
			{
				index = i;
				break;
			}
		}
		if (index < 0)
		{
			throw new RuntimeException("所要匹配的业务专用数据类型不存在");
		}
		String tableName = YWZY_TABLES[index];

		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = database.query(tableName, new String[] { "GGSJ_ID" },
				" GGSJ_ID=? ", new String[] { ggsjId }, null, null, null);
		if (cursor.moveToNext())
		{
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	// public int clearYwzyWhitGgsjId(String ggsjId, String name)
	// {
	// int index = -1;
	// for (int i = 0; i < YWZY_NAMES.length; i++)
	// {
	// if (YWZY_NAMES[i].equals(name))
	// {
	// index = i;
	// break;
	// }
	// }
	// if (index < 0)
	// {
	// throw new RuntimeException("所要匹配的业务专用数据类型不存在");
	// }
	// String tableName = YWZY_TABLES[index];
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// ContentValues values = new ContentValues();
	// values.put("X", "");
	// values.put("Y", "");
	// values.put("GGSJ_ID", "");
	// values.put("BS", 0);
	// int update = database.update(tableName, values, "GGSJ_ID=?",
	// new String[] { ggsjId });
	// return update;
	// }

	public int clearYwzyWhitGgsj(BaseAttrs attrs)
	{
		String fldm = attrs.FLDM;
		String tableName = null;
		int index = -1;
		if ("B031001".equals(fldm))
		{
			// 说明该公共数据是一个网吧
			// tableName = YWZY_TABLES[0];
			index = 0;
		}
		else if (fldm.startsWith("B0311"))
		{
			// 说明可能是一个旅馆
			// tableName = YWZY_TABLES[1];
			index = 1;
		}
		else if (fldm.startsWith("B03") || fldm.startsWith("B07"))
		{
			// 说明可能是一个机构
			// tableName = YWZY_TABLES[2];
			index = 2;
		}
		if (index < 0)
		{
			return 0;
		}
		tableName = YWZY_TABLES[index];
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("X", "");
		values.put("Y", "");
		values.put("GGSJ_ID", "");
		values.put("BS", 0);
		int update = database.update(tableName, values, "GGSJ_ID=?",
				new String[] { attrs.ID });
		if (update > 0)
		{
			// 清除内存中的业务专用数据
			ArrayList<Ywzy> arrayList = Source.ywzys.get(YWZY_NAMES[index]);
			Iterator<Ywzy> it = arrayList.iterator();
			while (it.hasNext())
			{
				Ywzy y = it.next();
				if (y.getGgsj_id().equals(attrs.ID))
				{
					it.remove();
				}
			}
		}
		return update;
	}

	public ArrayList<Ywzy> getMatchYwzys(BaseAttrs attrs)
	{
		ArrayList<Ywzy> list = new ArrayList<Ywzy>();
		String fldm = attrs.FLDM;
		String tableName = null;
		if ("B031001".equals(fldm))
		{
			// 说明该公共数据是一个网吧
			tableName = YWZY_TABLES[0];
		}
		else if (fldm.startsWith("B0311"))
		{
			// 说明可能是一个旅馆
			tableName = YWZY_TABLES[1];
		}
		else if (fldm.startsWith("B03") || fldm.startsWith("B07"))
		{
			// 说明可能是一个机构
			tableName = YWZY_TABLES[2];
		}
		if (tableName == null)
		{
			return list;
		}
		SQLiteDatabase database = DbHelper.getDatabase(context);

		Cursor cursor = database.query(tableName, null, "GGSJ_ID=?",
				new String[] { attrs.ID }, null, null, null);
		Ywzy ywzy = getBeanFromCursor(cursor);
		list.add(ywzy);
		return list;
	}

	/**
	 * 用于数据导入的时候更新数据表
	 */
	public void update(String tableName, String id, String x, String y,
			String ggsjId)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("X", x);
		values.put("Y", y);
		values.put("BS", 1);
		values.put("GGSJ_ID", ggsjId);
		database.update(tableName, values, "ID=?", new String[] { id });
	}

	public int update(Ywzy ywzy)
	{
		String tableName = getTableName(ywzy);
		if (tableName == null)
		{
			return -1;
		}
		SQLiteDatabase database = DbHelper.getDatabase(context);
		String id = ywzy.getId();
		int bs = ywzy.getBs();
		ContentValues values = new ContentValues();
		values.put("BS", bs);
		switch (bs)
		{
		case 1:
			// 转换成已采
			values.put("X", ywzy.getX());
			values.put("Y", ywzy.getY());
			// if (!"YWZY_TXJK".equals(ywzy.getName()))
			// {
			values.put("GGSJ_ID", ywzy.getGgsj_id());
			// }
			break;
		case 0:
			// 转换成未采
			// TODO 将坐标置为null是否有问题？
		case 2:
			// 转换成无法采集
			values.put("X", "");
			values.put("Y", "");
			values.put("GGSJ_ID", "");
			break;
		}
		int update = database.update(tableName, values, "ID=?",
				new String[] { id });
		return update;
	}

	public String getTableName(Ywzy ywzy)
	{
		String name = ywzy.getName();
		for (int i = 0; i < YWZY_NAMES.length; i++)
		{
			if (YWZY_NAMES[i].equals(name))
			{
				return YWZY_TABLES[i];
			}
		}
		return null;
	}

	/**
	 * 匹配网吧业务专用数据(特指：场所--互联网上网服务营业场所--网吧（B031001）)
	 * 匹配旅馆的业务专用数据(特指：场所--住宿服务场所（B031100）)
	 * 匹配机构的业务专用数据(特指：除去上两类的其它场所和所有单位(B030000、B070000))
	 * 匹配图像监控的业务专用数据(特指：基础设施--图像监控资源--监控头(B090500))
	 */
	public ArrayList<Ywzy> matchYwzy(BaseAttrs attrs)
	{
		MPHM mphm = attrs.mphm;
		ArrayList<Ywzy> list = new ArrayList<Ywzy>();
		if (mphm == null)
		{
			return list;
		}
		String fldm = attrs.FLDM;
		String mc = attrs.MC;

		if ("B031001".equals(fldm))
		{
			// 说明该公共数据是一个网吧
			list = getMatchingData(mphm, mc, YWZY_TABLES[0], YWZY_NAMES[0]);
		}
		// else if ("B090500".equals(fldm))
		// {
		// // 说明该公共数据是一个摄像头
		// list = getMatchingData(null, mc, YWZY_TABLES[1], YWZY_NAMES[1]);
		// }
		else if (fldm.startsWith("B0311"))
		{
			// 说明可能是一个旅馆
			list = getMatchingData(mphm, mc, YWZY_TABLES[1], YWZY_NAMES[1]);
		}
		else
		{
			if (fldm.startsWith("B03") || fldm.startsWith("B07"))
			{
				// 说明可能是一个机构
				list = getMatchingData(mphm, mc, YWZY_TABLES[2], YWZY_NAMES[2]);
			}
		}
		return list;
	}

	/**
	 * 根据公共数据的地址和名称查找相关的所有业务专用数据
	 * 
	 * @param mphm
	 * @param mc
	 *            用户填写名称
	 * @param tableName
	 * @param name
	 *            业务专用数据类型
	 * @return
	 */
	private ArrayList<Ywzy> getMatchingData(MPHM mphm, String mc,
			String tableName, String name)
	{
		ArrayList<Ywzy> list = new ArrayList<Ywzy>();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor;
		if (mphm != null)
		{
			String jlx = mphm.JLX;
			String mph = mphm.MPH;
			cursor = database
					.query(tableName, null,
							" (MC like ? or (JLX=? and MPH=?)) and BS=0 ",
							new String[] { "%" + mc + "%", jlx, mph }, null,
							null, null);
		}
		else
		{
			cursor = database.query(tableName, null, " MC like ?  and BS=0 ",
					new String[] { "%" + mc + "%" }, null, null, null);
		}
		while (cursor.moveToNext())
		{
			Ywzy ywzy = getBeanFromCursor(cursor);
			ywzy.setName(name);
			list.add(ywzy);
		}
		return list;
	}
}