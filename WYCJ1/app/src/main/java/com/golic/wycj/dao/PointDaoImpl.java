package com.golic.wycj.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.golic.wycj.util.DbHelper;

public class PointDaoImpl
{
	public static int updateBuildingPoint(Context context, String id, String x,
			String y)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("X", x);
		values.put("Y", y);
		return database.update("DZ_ZZXX", values, "ID=? or DZBH=?",
				new String[] { id, id });
	}

	public static int updateGgsjPoint(Context context, String tableName,
			String id, String x, String y)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("X", x);
		values.put("Y", y);
		return database.update(tableName, values, "ID=?", new String[] { id });
	}
}
