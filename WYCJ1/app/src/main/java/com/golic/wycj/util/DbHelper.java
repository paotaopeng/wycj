package com.golic.wycj.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.golic.wycj.Constans;

public class DbHelper
{
	private static SQLiteDatabase database;

	public static SQLiteDatabase getDatabase(Context context)
	{
		if (database == null)
		{
			database = SQLiteDatabase.openDatabase(
					context.getDatabasePath(Constans.DB_NAME).toString(), null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}
}