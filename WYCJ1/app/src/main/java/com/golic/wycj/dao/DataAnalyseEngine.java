package com.golic.wycj.dao;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.golic.wycj.Source;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.util.DbHelper;
import com.golic.wycj.util.NoticeUtil;

public class DataAnalyseEngine
{
	private Context context;
	private static final String TABLE_NAME = "DZ_ZZXX";

	public DataAnalyseEngine(Context context)
	{
		super();
		this.context = context;
	}

	public boolean hasSameDz(MPHM mphm, ExtraDz extraDz)
	{
		if (mphm == null || TextUtils.isEmpty(mphm.MPH))
		{
			return false;
		}
		SQLiteDatabase database = DbHelper.getDatabase(context);
		Cursor cursor = null;
		if (extraDz == null)
		{
			cursor = database
					.query(TABLE_NAME,
							new String[] { "ID" },
							" SSXQ=? and JLX=? and MPQZ=? and MPH=? and FH=? and DZLX=0 ",
							new String[] { mphm.SSXQ, mphm.JLX, mphm.MPQZ,
									mphm.MPH, mphm.FH }, null, null, null);
		}
		else
		{
			cursor = database
					.query(TABLE_NAME,
							new String[] { "ID" },
							" SSXQ=? and JLX=? and MPQZ=? and MPH=? and FH=? and XQM=? and ZLH=? and LCH=? and SH=? and DZLX=0 ",
							new String[] { mphm.SSXQ, mphm.JLX, mphm.MPQZ,
									mphm.MPH, mphm.FH, extraDz.xqm,
									extraDz.zlh, extraDz.lch, extraDz.sh },
							null, null, null);
		}
		if (cursor.moveToNext())
		{
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	public boolean hasSameDz(BaseBuilding baseBuilding)
	{
		if (baseBuilding == null || baseBuilding.mphm == null
				|| TextUtils.isEmpty(baseBuilding.mphm.MPH))
		{
			return false;
		}
		SQLiteDatabase database = DbHelper.getDatabase(context);
		MPHM mphm = baseBuilding.mphm;
		Cursor cursor;
		if (mphm.DZLX == 1)
		{
			cursor = database
					.query(TABLE_NAME,
							new String[] { "ID" },
							" SSXQ=? and JLX=? and MPQZ=? and MPH=? and FH=? and DZLX=1 ",
							new String[] { mphm.SSXQ, mphm.JLX, mphm.MPQZ,
									mphm.MPH, mphm.FH }, null, null, null);
		}
		else
		{
			cursor = database
					.query(TABLE_NAME,
							new String[] { "ID" },
							" SSXQ=? and JLX=? and MPQZ=? and MPH=? and FH=? and ZLH=? and DZLX=2 ",
							new String[] { mphm.SSXQ, mphm.JLX, mphm.MPQZ,
									mphm.MPH, mphm.FH, baseBuilding.zlh },
							null, null, null);
		}

		if (cursor.moveToNext())
		{
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}

	// 公共数据名称是否重复，不可忽略(先确定名称的唯一性：相同标准地址下的公共数据名称一致说明是重复的)
	// public boolean hasSameName(Ggsj ggsj)
	// {
	// String name = ggsj.getMc();
	// if (TextUtils.isEmpty(name))
	// {
	// return false;
	// }
	// String mlxz = null;
	// Bzdz bzdz = ggsj.getBzdz();
	// if (bzdz != null)
	// {
	// mlxz = bzdz.getMlxz();
	// }
	// if (TextUtils.isEmpty(mlxz))
	// {
	// return false;
	// }
	// String mc = ggsj.getMc();
	// TableName annotation = ggsj.getClass().getAnnotation(TableName.class);
	// if (annotation == null)
	// {
	// return false;
	// }
	// String tableName = annotation.value();
	// SQLiteDatabase database = DbHelper.getDatabase(context);
	// Cursor cursor = database.query(tableName, new String[] { "ID" },
	// " DZ=? and MC=? and ZXBS='0' ", new String[] { mlxz, mc },
	// null, null, null);
	// if (cursor.moveToNext())
	// {
	// cursor.close();
	// return true;
	// }
	// cursor.close();
	// return false;
	// }

	public static boolean dataFinishCheck(MPHM mphm, ExtraDz extraDz,
			Activity context)
	{
		String mply = mphm.MPLY;
		if (TextUtils.isEmpty(mphm.SSXQ))
		{
			NoticeUtil.showWarningDialog(context, "行政区划未填！");
			return false;
		}
		if (TextUtils.isEmpty(mphm.JLX))
		{
			NoticeUtil.showWarningDialog(context, "街路巷未填！");
			return false;
		}
		if (TextUtils.isEmpty(mply))
		{
			NoticeUtil.showWarningDialog(context, "门牌来源未填！");
			return false;
		}

		if (!TextUtils.isEmpty(mphm.MPH) && TextUtils.isEmpty(mphm.MPHZ))
		{
			NoticeUtil.showWarningDialog(context, "存在门牌号必须填写门牌后缀");
			return false;
		}
		if (!TextUtils.isEmpty(mphm.FH) && TextUtils.isEmpty(mphm.FHHZ))
		{
			NoticeUtil.showWarningDialog(context, "存在副号必须填写副号后缀");
			return false;
		}
		if (!TextUtils.isEmpty(mphm.FH) && TextUtils.isEmpty(mphm.MPH))
		{
			NoticeUtil.showWarningDialog(context, "存在副号必须填写门牌号");
			return false;
		}
		// 公共数据完整性判断
		if (TextUtils.isEmpty(mphm.MPH))
		{
			if (!"5".equals(mply) && !"6".equals(mply))
			{
				NoticeUtil.showWarningDialog(context, "门牌号未填！");
				return false;
			}
		}
		else
		{
			if ("5".equals(mply) || "6".equals(mply))
			{
				NoticeUtil.showWarningDialog(context, "门牌来源与实际输入值不符！");
				return false;
			}
		}
		// 如果用户填写了额外地址，还需要判断额外地址的完整性
		if (extraDz != null)
		{
			if (!TextUtils.isEmpty(extraDz.zlh)
					&& TextUtils.isEmpty(extraDz.sufZlh))
			{
				NoticeUtil.showWarningDialog(context, "存在幢楼号必须填写幢楼后缀");
				return false;
			}
			if (!TextUtils.isEmpty(extraDz.lch)
					&& TextUtils.isEmpty(extraDz.sufLch))
			{
				NoticeUtil.showWarningDialog(context, "存在楼层号必须填写楼层后缀");
				return false;
			}
			if (!TextUtils.isEmpty(extraDz.sh)
					&& TextUtils.isEmpty(extraDz.sufSh))
			{
				NoticeUtil.showWarningDialog(context, "存在室号必须填写室号后缀");
				return false;
			}
			if (!TextUtils.isEmpty(extraDz.sh)
					&& TextUtils.isEmpty(extraDz.lch))
			{
				NoticeUtil.showWarningDialog(context, "存在室号必须填写楼层号");
				return false;
			}
		}
		return true;
	}

	// 公共数据名称匹配，可以忽略
	public void nameMatching(BaseAttrs attrs)
	{
		int level = 0;
		// 用户填写的名称
		String name = attrs.MC;
		if (TextUtils.isEmpty(name))
		{
			attrs.level = level;
			attrs.comment = "";
		}
		String ys = attrs.YS;
		String matching = Source.nameMatching.get(ys);
		if (match(name, matching))
		{
			attrs.level = level;
			attrs.comment = "";
			return;
		}
		level++;
		ArrayList<String> comments = new ArrayList<String>();
		for (Map.Entry<String, String> entry : Source.nameMatching.entrySet())
		{
			if (match(name, entry.getValue()))
			{
				String key = entry.getKey();
				String value = Source.nameValues.get(key);
				comments.add(value);
				level++;
			}
		}
		if (level == 1)
		{
			attrs.level = level;
			attrs.comment = "名称无匹配项";
		}
		else
		{
			String kndNames = "";
			for (int i = 0; i < comments.size(); i++)
			{
				if (i == 0)
				{
					kndNames = comments.get(i);
				}
				else
				{
					kndNames += "," + comments.get(i);
				}
			}
			attrs.level = level;
			attrs.comment = "名称与所选分类不一致，可能的类型是：" + kndNames;
		}
	}

	// 判断用户输入的名称是否在自身的限定名称中，如果是返回true说明
	private static boolean match(String name, String matching)
	{
		if (TextUtils.isEmpty(matching))
		{
			return false;
		}
		for (String split : matching.split("、"))
		{
			if (name.endsWith(split))
			{
				return true;
			}
		}
		return false;
	}
}