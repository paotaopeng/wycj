package com.golic.wycj.dao;

import java.util.ArrayList;

import com.golic.wycj.Source;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.util.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhotoDaoImpl
{
	private Context context;
	private SQLiteDatabase database;
	private static final String TABLE_NAME = "BZDZ_ZP";

	public PhotoDaoImpl(Context context)
	{
		super();
		this.context = context;
	}

	public void updatePhotos(ArrayList<BzdzPhoto> deletePhotos,
			ArrayList<BzdzPhoto> addPhotos)
	{
		for (BzdzPhoto photo : deletePhotos)
		{
			deletePhoto(photo);
		}
		for (BzdzPhoto photo : addPhotos)
		{
			insertPhoto(photo);
		}
	}

	/**
	 * 查询某个某个标准地址下的所有照片
	 * 
	 * @param bzdzId
	 *            标准地址主键
	 * @return
	 */
	public ArrayList<BzdzPhoto> findPhotos(String bzdzId)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);

		Cursor cursor = database.query(TABLE_NAME, null, "BZDZ_ID=?",
				new String[] { bzdzId }, null, null, null);
		ArrayList<BzdzPhoto> photos = new ArrayList<BzdzPhoto>();
		while (cursor.moveToNext())
		{
			String id = cursor.getString(cursor.getColumnIndex("ID"));
			String path = cursor.getString(cursor.getColumnIndex("PATH"));
			String fileName = cursor.getString(cursor
					.getColumnIndex("FILE_NAME"));
			photos.add(new BzdzPhoto(id, bzdzId, path, fileName));
		}
		cursor.close();
		return photos;
	}
	
	/**
	 * @return　返回所有的照片
	 */
	public ArrayList<BzdzPhoto> findAllPhotos()
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);

		Cursor cursor = database.query(TABLE_NAME, null, null,
				null, null, null, null);
		ArrayList<BzdzPhoto> photos = new ArrayList<BzdzPhoto>();
		while (cursor.moveToNext())
		{
			String id = cursor.getString(cursor.getColumnIndex("ID"));
			String bzdzId = cursor.getString(cursor.getColumnIndex("BZDZ_ID"));
			String path = cursor.getString(cursor.getColumnIndex("PATH"));
			String fileName = cursor.getString(cursor
					.getColumnIndex("FILE_NAME"));
			photos.add(new BzdzPhoto(id, bzdzId, path, fileName));
		}
		cursor.close();
		return photos;
	}
	
	

	public int deletePhoto(BzdzPhoto photo)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		int delete = database.delete(TABLE_NAME, "ID=?",
				new String[] { photo.id });
		return delete;
	}

	public long insertPhoto(BzdzPhoto photo)
	{
		SQLiteDatabase database = DbHelper.getDatabase(context);
		ContentValues values = new ContentValues();
		values.put("ID", photo.id);
		values.put("BZDZ_ID", photo.bzdzId);
		values.put("PATH", photo.path);
		values.put("FILE_NAME", photo.fileName);
		long insert = database.insert(TABLE_NAME, null, values);
		return insert;
	}

}
