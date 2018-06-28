package com.golic.wycj.dao;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.golic.wycj.LoginUser;
import com.golic.wycj.model.User;
import com.golic.wycj.util.DbHelper;
import com.golic.wycj.util.DictUtil;

public class LoginEngine
{
	private Context context;
	private static final String TABLE_NAME = "USER";

	public LoginEngine(Context context)
	{
		super();
		this.context = context;
	}

	public void update(User user)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = getContentValues(user);
		database.update(TABLE_NAME, values, "USER_ID=?",
				new String[] { user.id });
	}

	public long add(User user)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		user.id = UUID.randomUUID().toString();
		ContentValues values = getContentValues(user);
		return database.insert(TABLE_NAME, null, values);
	}

	private static ContentValues getContentValues(User user)
	{
		ContentValues values = new ContentValues();
		values.put("USER_ID", user.id);
		values.put("USER_NAME", user.userName);
		values.put("PASSWORD", user.password);
		values.put("XM", user.xm);
		values.put("ZRQ", user.zrq);
		return values;
	}

	public int delete(User user)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		return database.delete(TABLE_NAME, "USER_ID=?",
				new String[] { user.id });
	}

	public ArrayList<User> findAll()
	{
		ArrayList<User> list = new ArrayList<User>();
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = database.query(TABLE_NAME, null, "USER_NAME!='golic'",
				null, null, null, null);
		while (cursor.moveToNext())
		{
			User user = new User();
			user.id = cursor.getString(cursor.getColumnIndex("USER_ID"));
			user.userName = cursor
					.getString(cursor.getColumnIndex("USER_NAME"));
			user.password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
			user.xm = cursor.getString(cursor.getColumnIndex("XM"));
			user.zrq = cursor.getString(cursor.getColumnIndex("ZRQ"));
			list.add(user);
		}
		return list;
	}

	public boolean login(String userName, String password)
	{
		boolean accept = false;
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = database.query(TABLE_NAME,
				new String[] { "XM", "ZRQ" }, " USER_NAME=? and PASSWORD=? ",
				new String[] { userName, password }, null, null, null);
		if (cursor.moveToNext())
		{
			String xm = cursor.getString(cursor.getColumnIndex("XM"));
			accept = true;
			String zrq = cursor.getString(cursor.getColumnIndex("ZRQ"));
			LoginUser.xm = xm;
			LoginUser.zrq = zrq;
			if ("golic".equals(userName))
			{
				LoginUser.isAdministrator = true;
			}
			else
			{
				LoginUser.isAdministrator = false;
			}
		}
		cursor.close();
		if (accept)
		{
			Cursor zrqCursor = database.query(DictUtil.TC_ZRQ, null, " DM=? ",
					new String[] { LoginUser.zrq }, null, null, null);
			if (zrqCursor.moveToNext())
			{
				LoginUser.zrqMc = zrqCursor.getString(zrqCursor
						.getColumnIndex("MC"));
				LoginUser.zrqFw = zrqCursor.getString(zrqCursor
						.getColumnIndex("FW"));
			}
			zrqCursor.close();
		}
		return accept;
	}
}