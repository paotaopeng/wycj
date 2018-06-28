package com.golic.wycj.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.CS;
import com.golic.wycj.domain.GGSS;
import com.golic.wycj.domain.JTSS;
import com.golic.wycj.domain.QSYDW;
import com.golic.wycj.domain.QTDW;
import com.golic.wycj.domain.QTJTXX;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.domain.ZHJG;
import com.golic.wycj.model.WorkDay;
import com.golic.wycj.util.DbHelper;

public class GgsjDaoImpl
{
	private Context context;
	private SQLiteDatabase database;

	public GgsjDaoImpl(Context context)
	{
		super();
		this.context = context;
	}

	/**
	 * 删除一个工作日的公共数据（ //TODO 附带删除业务专用数据）
	 */
	public void deleteWorkDay(WorkDay day)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Type[] values = Type.values();
		for (Type type : values)
		{
			// if (delete > 0)
			// {
			// 查看是否关联过业务专用
			Cursor cursor = database.query(type.toString(),
					new String[] { "ID" }, "BS>0", null, null, null, null);
			ArrayList<String> keys = new ArrayList<String>();
			while (cursor.moveToNext())
			{
				keys.add(cursor.getString(cursor.getColumnIndex("ID")));
			}
			if (keys.size() > 0)
			{
				// 删除关联的业务专用
				for (Map.Entry<String, ArrayList<Ywzy>> entry : Source.ywzys
						.entrySet())
				{
					ArrayList<Ywzy> list = entry.getValue();
					Iterator<Ywzy> it = list.iterator();
					while (it.hasNext())
					{
						Ywzy ywzy = it.next();
						if (keys.contains(ywzy.getGgsj_id()))
						{
							// TODO 一个公共数据关联多个业务专用数据，所以暂时不从keys中删除
							// keys.remove(ywzy.getGgsj_id());
							ywzy.setBs(0);
							int update = new YwzyDaoImpl(context).update(ywzy);
							if (update > 0)
							{
								// 更新内存中的数据（从内存中移除被关联过的业务专用数据）
								it.remove();
							}
						}
					}
				}
			}
			// }
			// int delete =
			database.delete(type.toString(), "GXSJ=? and XGR=?", new String[] {
					day.gxsj, day.gxr });
		}
	}

	public TreeSet<WorkDay> queryWorkday()
	{
		TreeSet<WorkDay> set = new TreeSet<WorkDay>();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Type[] values = Type.values();
		for (Type type : values)
		{
			Cursor cursor = database.query(true, type.toString(), new String[] {
					"GXSJ", "XGR" }, null, null, null, null, null, null);
			while (cursor.moveToNext())
			{
				WorkDay day = new WorkDay();
				day.gxsj = cursor.getString(cursor.getColumnIndex("GXSJ"));
				day.gxr = cursor.getString(cursor.getColumnIndex("XGR"));
				day.type = "公共数据";
				set.add(day);
			}
		}
		return set;
	}

	private static BaseAttrs getBaseAttrsFromCursor(Cursor c)
	{
		String ID = c.getString(c.getColumnIndex("ID"));
		String X = c.getString(c.getColumnIndex("X"));
		String Y = c.getString(c.getColumnIndex("Y"));
		String DJR = c.getString(c.getColumnIndex("DJR"));
		String DJSJ = c.getString(c.getColumnIndex("DJSJ"));
		String GXDWDM = c.getString(c.getColumnIndex("GXDWDM"));

		String FLDM = c.getString(c.getColumnIndex("FLDM"));
		String GBDM = c.getString(c.getColumnIndex("GBDM"));
		String LX = c.getString(c.getColumnIndex("LX"));

		String XGR = c.getString(c.getColumnIndex("XGR"));
		String GXSJ = c.getString(c.getColumnIndex("GXSJ"));

		String YS = c.getString(c.getColumnIndex("YS"));
		String BZ = c.getString(c.getColumnIndex("BZ"));
		String MC = c.getString(c.getColumnIndex("MC"));
		String ZMC = c.getString(c.getColumnIndex("ZMC"));
		int BS = c.getInt(c.getColumnIndex("BS"));
		int LEVEL = c.getInt(c.getColumnIndex("LEVEL"));
		String COMMENT = c.getString(c.getColumnIndex("LEVEL_COMMENT"));
		return new BaseAttrs(ID, X, Y, DJR, DJSJ, GXDWDM, FLDM, GBDM, LX, XGR,
				GXSJ, YS, BZ, MC, ZMC, BS, LEVEL, COMMENT);
	}

	private static ContentValues getContentValues(BaseAttrs attrs)
	{
		ContentValues values = new ContentValues();
		values.put("ID", attrs.ID);
		values.put("X", attrs.X);
		values.put("Y", attrs.Y);
		values.put("DJR", attrs.DJR);
		values.put("XGR", attrs.XGR);
		values.put("DJSJ", attrs.DJSJ);
		values.put("GXSJ", attrs.GXSJ);
		values.put("GXDWDM", attrs.GXDWDM);
		values.put("FLDM", attrs.FLDM);
		values.put("GBDM", attrs.GBDM);
		values.put("LX", attrs.LX);
		values.put("YS", attrs.YS);
		values.put("BZ", attrs.BZ);
		values.put("MC", attrs.MC);
		values.put("ZMC", attrs.ZMC);
		values.put("BS", attrs.bs);
		values.put("LEVEL", attrs.level);
		values.put("LEVEL_COMMENT", attrs.comment);
		Type type = attrs.type;
		switch (type)
		{
		case GGSJ_CS:
			CS cs = (CS) attrs;
			values.put("LXR", cs.LXR);
			values.put("LXDH", cs.LXDH);
			break;
		case GGSJ_QSYDW:
			QSYDW qsydw = (QSYDW) attrs;
			values.put("ZCZB", qsydw.ZCZB);
			values.put("FDDBR", qsydw.FDDBR);
			values.put("ZCSJ", qsydw.ZCSJ);
			values.put("ZCDD", qsydw.ZCDD);
			break;
		case GGSJ_QTDW:
			QTDW qtdw = (QTDW) attrs;
			values.put("LXR", qtdw.LXR);
			values.put("LXDH", qtdw.LXDH);
			break;
		// 无地址
		case GGSJ_GGSS:
			GGSS ggss = (GGSS) attrs;
			values.put("LXR", ggss.LXR);
			values.put("LXDH", ggss.LXDH);
			break;
		// 无地址
		case GGSJ_JTSS:
			break;
		case GGSJ_QTJTXX:
			QTJTXX qtjtxx = (QTJTXX) attrs;
			values.put("SZWZ", qtjtxx.SZWZ);
			break;
		case GGSJ_ZHJG:
			ZHJG zhjg = (ZHJG) attrs;
			values.put("LXR", zhjg.LXR);
			values.put("LXDH", zhjg.LXDH);
			values.put("GJ", zhjg.GJ);
			break;
		}
		if (attrs.mphm != null)
		{
			values.put("BZDZ_ID", attrs.mphm.ID);
			values.put("DZ", attrs.mphm.MLXZ);
		}
		return values;
	}

	/**
	 * 插入EXCEL中的数据到数据库
	 */
	public long insert(String tableName, ContentValues values)
	{
		database = DbHelper.getDatabase(context);
		long insert = database.insert(tableName, null, values);
		if (insert > 0)
		{
			Source.update = true;
		}
		return insert;
	}

	public long insert(BaseAttrs attrs)
	{
		if (attrs.mphm != null)
		{
			// 需要首先插入一条标准地址
			new BzdzDaoImpl(context).insert(attrs.mphm, attrs.extraDz);
		}
		database = DbHelper.getDatabase(context);
		Type type = attrs.type;
		ContentValues values = getContentValues(attrs);
		long insert = database.insert(type.toString(), null, values);
		if (insert > 0)
		{
			Source.update = true;
		}
		return insert;
	}

	/**
	 * 这里的修改只关心公共数据的部分，如果是标准地址发生更改在逻辑层处理
	 * 
	 * @param attrs
	 */
	public int updateGgsj(BaseAttrs attrs)
	{
		database = DbHelper.getDatabase(context);
		Type type = attrs.type;
		ContentValues values = getContentValues(attrs);
		return database.update(type.toString(), values, "ID=?",
				new String[] { attrs.ID });
	}

	public int updateDz(String tableName, String ID, String DZ)
	{
		database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("DZ", DZ);
		return database.update(tableName, values, "ID=?", new String[] { ID });
	}

	public int delete(BaseAttrs attrs)
	{
		database = DbHelper.getDatabase(context);
		int delete = database.delete(attrs.type.toString(), "ID=?",
				new String[] { attrs.ID });
		if (delete > 0)
		{
			// 同时删除照片
			database.delete("BZDZ_ZP", "BZDZ_ID=?", new String[] { attrs.ID });
			if (attrs.mphm != null)
			{
				new BzdzDaoImpl(context).deleteMphm(attrs.mphm.ID);
			}
			if (attrs.bs > 0)
			{
				// 说明有关联业务专用数据
				new YwzyDaoImpl(context).clearYwzyWhitGgsj(attrs);
			}
		}
		return delete;
	}

	// 其他交通信息查询的时候不需要关心SZWZ（所在位置）字段
	public void findAllGgsj()
	{
		database = DbHelper.getDatabase(context);
		Type[] values = Type.values();
		for (Type type : values)
		{
			switch (type)
			{
			case GGSJ_CS:
				Cursor csCursor = database.query(type.toString(), null, null,
						null, null, null, null);
				ArrayList<BaseAttrs> csList = new ArrayList<BaseAttrs>();
				while (csCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(csCursor);
					csList.add(fillCs(csCursor, attrs));
				}
				csCursor.close();
				Source.ggsjs.put(type, csList);
				break;
			case GGSJ_QSYDW:
				Cursor qsydwCursor = database.query(type.toString(), null,
						null, null, null, null, null);
				ArrayList<BaseAttrs> qsydwList = new ArrayList<BaseAttrs>();
				while (qsydwCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(qsydwCursor);
					qsydwList.add(fillQsydw(qsydwCursor, attrs));
				}
				qsydwCursor.close();
				Source.ggsjs.put(type, qsydwList);
				break;
			case GGSJ_QTDW:
				Cursor qtdwCursor = database.query(type.toString(), null, null,
						null, null, null, null);
				ArrayList<BaseAttrs> qtdwList = new ArrayList<BaseAttrs>();
				while (qtdwCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(qtdwCursor);
					qtdwList.add(fillQtdw(qtdwCursor, attrs));
				}
				qtdwCursor.close();
				Source.ggsjs.put(type, qtdwList);
				break;
			// 无地址
			case GGSJ_GGSS:
				Cursor ggssCursor = database.query(type.toString(), null, null,
						null, null, null, null);
				ArrayList<BaseAttrs> ggssList = new ArrayList<BaseAttrs>();
				while (ggssCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(ggssCursor);
					ggssList.add(fillGgss(ggssCursor, attrs));
				}
				ggssCursor.close();
				Source.ggsjs.put(type, ggssList);
				break;
			// 无地址
			case GGSJ_JTSS:
				Cursor cursor = database.query(type.toString(), null, null,
						null, null, null, null);
				ArrayList<BaseAttrs> list = new ArrayList<BaseAttrs>();
				while (cursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(cursor);
					list.add(new JTSS(attrs));
				}
				cursor.close();
				Source.ggsjs.put(type, list);
				break;
			case GGSJ_QTJTXX:
				Cursor qtjtxxCursor = database.query(type.toString(), null,
						null, null, null, null, null);
				ArrayList<BaseAttrs> qtjtxxList = new ArrayList<BaseAttrs>();
				while (qtjtxxCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(qtjtxxCursor);
					attrs.DZ = qtjtxxCursor.getString(qtjtxxCursor
							.getColumnIndex("DZ"));
					String BZDZ_ID = qtjtxxCursor.getString(qtjtxxCursor
							.getColumnIndex("BZDZ_ID"));
					BzdzDaoImpl.fillBaseAttrs(database, attrs, BZDZ_ID);
					qtjtxxList.add(new QTJTXX(attrs));
				}
				qtjtxxCursor.close();
				Source.ggsjs.put(type, qtjtxxList);
				break;
			case GGSJ_ZHJG:
				Cursor zhjgCursor = database.query(type.toString(), null, null,
						null, null, null, null);
				ArrayList<BaseAttrs> zhjgList = new ArrayList<BaseAttrs>();
				while (zhjgCursor.moveToNext())
				{
					BaseAttrs attrs = getBaseAttrsFromCursor(zhjgCursor);
					zhjgList.add(fillZhjg(zhjgCursor, attrs));
				}
				zhjgCursor.close();
				Source.ggsjs.put(type, zhjgList);
				break;
			}
		}
	}

	private CS fillCs(Cursor c, BaseAttrs attrs)
	{
		attrs.DZ = c.getString(c.getColumnIndex("DZ"));
		String LXR = c.getString(c.getColumnIndex("LXR"));
		String LXDH = c.getString(c.getColumnIndex("LXDH"));
		String BZDZ_ID = c.getString(c.getColumnIndex("BZDZ_ID"));
		BzdzDaoImpl.fillBaseAttrs(database, attrs, BZDZ_ID);
		return new CS(attrs, LXR, LXDH);
	}

	private GGSS fillGgss(Cursor c, BaseAttrs attrs)
	{
		String LXR = c.getString(c.getColumnIndex("LXR"));
		String LXDH = c.getString(c.getColumnIndex("LXDH"));
		return new GGSS(attrs, LXR, LXDH);
	}

	private QSYDW fillQsydw(Cursor c, BaseAttrs attrs)
	{
		attrs.DZ = c.getString(c.getColumnIndex("DZ"));
		String ZCZB = c.getString(c.getColumnIndex("ZCZB"));
		String FDDBR = c.getString(c.getColumnIndex("FDDBR"));
		String ZCSJ = c.getString(c.getColumnIndex("ZCSJ"));
		String ZCDD = c.getString(c.getColumnIndex("ZCDD"));
		String BZDZ_ID = c.getString(c.getColumnIndex("BZDZ_ID"));
		BzdzDaoImpl.fillBaseAttrs(database, attrs, BZDZ_ID);
		return new QSYDW(attrs, ZCZB, FDDBR, ZCSJ, ZCDD);
	}

	private QTDW fillQtdw(Cursor c, BaseAttrs attrs)
	{
		attrs.DZ = c.getString(c.getColumnIndex("DZ"));
		String LXR = c.getString(c.getColumnIndex("LXR"));
		String LXDH = c.getString(c.getColumnIndex("LXDH"));
		String BZDZ_ID = c.getString(c.getColumnIndex("BZDZ_ID"));
		BzdzDaoImpl.fillBaseAttrs(database, attrs, BZDZ_ID);
		return new QTDW(attrs, LXR, LXDH);
	}

	private ZHJG fillZhjg(Cursor c, BaseAttrs attrs)
	{
		attrs.DZ = c.getString(c.getColumnIndex("DZ"));
		String LXR = c.getString(c.getColumnIndex("LXR"));
		String LXDH = c.getString(c.getColumnIndex("LXDH"));
		String GJ = c.getString(c.getColumnIndex("GJ"));
		String BZDZ_ID = c.getString(c.getColumnIndex("BZDZ_ID"));
		BzdzDaoImpl.fillBaseAttrs(database, attrs, BZDZ_ID);
		return new ZHJG(attrs, LXR, LXDH, GJ);
	}
}