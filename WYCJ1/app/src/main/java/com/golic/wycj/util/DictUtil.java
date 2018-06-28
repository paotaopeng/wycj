package com.golic.wycj.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.LruCache;

import com.golic.wycj.Constans;
import com.golic.wycj.LoginUser;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.MapLevelDictObject;

public class DictUtil
{
	private final SQLiteDatabase db;
	// private static final String DATABASE_NAME = "map_dict.db";
	private static DictUtil dictUtil;
	private static final ArrayList<String> DICT_TABLE_NAME = new ArrayList<String>();
	public static final HashMap<String, int[]> levelTable = new HashMap<String, int[]>();
	private static final ArrayList<String> MAP_LEVEL_TABLE_NAME = new ArrayList<String>();
	private static ArrayList<DictObject> xzqhDicts;

	private DictUtil(Context context)
	{
		db = DbHelper.getDatabase(context);
		// db =
		// SQLiteDatabase.openDatabase(context.getDatabasePath(Constans.DB_NAME)
		// .toString(), null, SQLiteDatabase.OPEN_READWRITE);
		dictCache = new LruCache<String, ArrayList<? extends DictObject>>(10);
	}

	public static DictUtil getInstance(Context context)
	{
		if (dictUtil == null)
			dictUtil = new DictUtil(context.getApplicationContext());
		return dictUtil;
	}

	private final LruCache<String, ArrayList<? extends DictObject>> dictCache;

	public void cleanCache()
	{
		dictCache.evictAll();
	}

	private String parseZrq(String zrq)
	{
		return zrq.replaceAll("(0+)$", "%");
	}

	public long addJlx(String dm, String mc)
	{
		dictCache.remove(DZ_JLX);
		ContentValues values = new ContentValues();
		values.put("DM", dm);
		values.put("MC", mc);
		long insert = db.insert(DZ_JLX, null, values);
		return insert;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<? extends DictObject> queryDict(String dictName)
	{
		if (!hasSuchDict(dictName))
			throw new IllegalArgumentException("不存在表：" + dictName);

		if (TC_XZQH.equals(dictName))
		{
			if (xzqhDicts == null)
			{
				getDictObjects(dictName);
				return (ArrayList<? extends DictObject>) xzqhDicts.clone();
			}
			else
			{
				return (ArrayList<? extends DictObject>) xzqhDicts.clone();
			}
		}
		ArrayList<? extends DictObject> resultList = dictCache.get(dictName);
		if (resultList == null)
		{
			if (MAP_LEVEL_TABLE_NAME.contains(dictName))
			{
				resultList = getMapLevelDictObjects(dictName);
			}
			else
			{
				resultList = getDictObjects(dictName);
			}
		}
		// dictCache.put(dictName, resultList);
		return (ArrayList<? extends DictObject>) resultList.clone(); // 返回克隆结果,避免在界面中被修改.
		// return resultList;
	}

	@SuppressWarnings("unused")
	public ArrayList<DictObject> getDictObjects(String dictName)
	{
		Cursor cursor = null;
		// 根据责任区特意获取街路巷,减少选择项
		if (DZ_JLX.equals(dictName) && Constans.EXIST_JLX_ZRQ_TABLE)
		{
			cursor = db
					.rawQuery(
							"select distinct DZ_JLX.DM , DZ_JLX.MC from DZ_JLX  , DZ_JLX_ZRQ where DZ_JLX_ZRQ.JWZRQ like '"
									+ parseZrq(LoginUser.zrq)
									+ "' and  DZ_JLX.DM = DZ_JLX_ZRQ.JLX;",
							null);
		}
		else
		{
			cursor = db
					.rawQuery("select DM , MC from " + dictName + " ;", null);
		}
		// cursor = db.rawQuery("select DM , MC from " + dictName + " ;", null);
		ArrayList<DictObject> resultList = new ArrayList<DictObject>();
		while (cursor.moveToNext())
		{
			resultList.add(new DictObject(cursor.getString(cursor
					.getColumnIndex("DM")), cursor.getString(cursor
					.getColumnIndex("MC"))));
		}
		cursor.close();
		if (TC_XZQH.equals(dictName))
		{
			xzqhDicts = resultList;
		}
		else
		{
			dictCache.put(dictName, resultList);
		}
		return resultList;
	}

	private ArrayList<MapLevelDictObject> getMapLevelDictObjects(String dictName)
	{
		Cursor cursor = null;
		cursor = db.rawQuery("select DM , MC, YS, PARENT_DM, YWBM from "
				+ dictName + " ;", null);

		ArrayList<MapLevelDictObject> resultList = new ArrayList<MapLevelDictObject>();
		while (cursor.moveToNext())
		{
			resultList.add(new MapLevelDictObject(cursor.getString(cursor
					.getColumnIndex("DM")), cursor.getString(cursor
					.getColumnIndex("MC")), cursor.getString(cursor
					.getColumnIndex("YS")), cursor.getString(cursor
					.getColumnIndex("PARENT_DM")), cursor.getString(cursor
					.getColumnIndex("YWBM"))));
		}
		cursor.close();
		dictCache.put(dictName, resultList);
		return resultList;
	}

	// 根据某个层级字典项查询其所有下一级字典（如果指定的字典为null则默认查询第一级别的字典集，若指定字典为最后一个级别则返回null）
	public ArrayList<DictObject> getIntLevelDictObjects(String dictName,
			String dm)
	{
		ArrayList<DictObject> resultList = new ArrayList<DictObject>();
		// TODO 这里需要注意，如果行政区划编号与责任区一致则注释内代码可以使用，否则不应该使用
		if (TextUtils.isEmpty(dm) && Constans.simpleSelect)
		{
			dm = LoginUser.zrq;
		}
		// if ((dm == null) && TC_XZQH.equals(dictName))
		// {
		// // 显示行政区划根据用户的实际责任区,减少选择项
		// dm = LoginUser.zrq;
		// }
		System.out.println("dm:" + dm);
		String patternZeroStr = getPatternZeroStr(dictName, dm);
		System.out.println("patternZeroStr:" + patternZeroStr);
		if (patternZeroStr == null)
		{
			return resultList;
		}
		Cursor cursor = null;
		// String sql = "select DM , MC from " + dictName +
		// " where DM!="+dm+" and DM like '"
		// + patternZeroStr + "' ;";
		String sql = "select DM , MC from " + dictName + " where DM like '"
				+ patternZeroStr + "' ;";
		if (TextUtils.isEmpty(dm))
		{
			sql = "select DM , MC from " + dictName
					+ " where substr(DM, (length(DM)-"
					+ (patternZeroStr.length() - 1) + "),length(DM))= '"
					+ patternZeroStr + "';";
		}
		System.out.println("sql:" + sql);
		cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext())
		{
			resultList.add(new DictObject(cursor.getString(cursor
					.getColumnIndex("DM")), cursor.getString(cursor
					.getColumnIndex("MC"))));
		}
		cursor.close();
		// dictCache.put(dictName, resultList);
		return resultList;
	}

	private String getPatternZeroStr(String dictName, String dm)
	{
		int[] arr = levelTable.get(dictName);
		if (arr == null)
		{
			throw new RuntimeException("不是一个层级字典！");
		}

		if (!TextUtils.isEmpty(dm))
		{
			int skipStep = 0;
			// 如果dm不为null表示采用like“dm0000%”的规则匹配，其中的关键是确定代码后面0的个数，比如说最后双位是10，不能写成1%而应该是10%
			int zeroNum = getEndingZeroNum(dm);
			// int availLen = len - zeroNum;
			int stepLen = arr[arr.length - 1];
			int subLen = 0;
			for (int i = 0; stepLen <= zeroNum; i++)
			{
				skipStep++;
				subLen = stepLen;
				stepLen += arr[arr.length - i - 2];
			}
			if (skipStep == 0)
			{
				// 如果末尾不含0表示dm已经是最后一个级别不能在查找下一级别了
				return null;
			}
			else
			{
				String clearStr = dm.substring(0, dm.length() - subLen);
				String patternStr = getSameCharStr('_', arr[arr.length
						- skipStep]);
				String zeroStr = "";
				if (clearStr.length() + patternStr.length() < dm.length())
				{
					zeroStr = getSameCharStr(
							'0',
							dm.length() - clearStr.length()
									- patternStr.length());
				}
				return clearStr + patternStr + zeroStr;
			}
		}
		else
		{
			int len = 0;
			for (int i = 0; i < arr.length; i++)
			{
				len += arr[i];
			}
			// 如果dm为null表示是第一级别的字典，应该使用“%0000”的规则匹配
			int zeroLen = len - arr[0];
			return getSameCharStr('0', zeroLen);
		}
	}

	private String getSameCharStr(char c, int len)
	{
		if (len <= 0)
		{
			return null;
		}
		char[] arr = new char[len];
		for (int i = 0; i < len; i++)
		{
			arr[i] = c;
		}
		return new String(arr);
	}

	private int getEndingZeroNum(String str)
	{
		int zeroNum = 0;
		char[] charArray = str.toCharArray();
		for (int i = charArray.length - 1; i >= 0; i--)
		{
			if (charArray[i] != '0')
			{
				break;
			}
			zeroNum++;
		}
		return zeroNum;
	}

	public DictObject getDictObject(String tableName, String key)
	{
		if (key == null || "".equals(key))
		{
			return null;
		}
		key = key.trim();
		ArrayList<? extends DictObject> dicts = queryDict(tableName);
		for (DictObject obj : dicts)
		{
			if (obj.getDM().equals(key))
			{
				return obj;
			}
		}
		return null;
	}

	public DictObject getLevelDictObject(String levelTableName, String key)
	{
		if (key != null)
		{
			key = key.trim();
		}
		ArrayList<? extends DictObject> dicts = queryDict(levelTableName);
		for (DictObject dic : dicts)
		{
			if (dic.getDM().equals(key))
			{
				return dic;
			}
		}
		return null;
	}

	public String getDictValue(String tableName, String key)
	{
		DictObject obj = getDictObject(tableName, key);
		if (obj == null)
		{
			if (key != null)
			{
				return key;
			}
			return "";
		}
		else
		{
			return obj.getMC();
		}
	}

	public static String genValue(List<DictObject> datas)
	{
		if (datas == null || datas.size() == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (DictObject d : datas)
		{
			if (d == null)
			{
				continue;
			}
			sb.append(d.getMC() + ",");
		}
		if (sb.length() > 0)
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String genKey(List<DictObject> datas)
	{
		if (datas == null || datas.size() == 0)
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (DictObject d : datas)
		{
			sb.append(d.getDM() + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/** 单元号 */
	public static final String DZ_DYH = "DZ_DYH";
	/** 副号后缀 */
	public static final String DZ_FHHZ = "DZ_FHHZ";
	/** 楼层后缀 */
	public static final String DZ_LCHZ = "DZ_LCHZ";
	/** 门牌后缀 */
	public static final String DZ_MPHZ = "DZ_MPHZ";
	/** 门牌前缀 */
	public static final String DZ_MPQZ = "DZ_MPQZ";
	/** 室号后缀 */
	public static final String DZ_SHHZ = "DZ_SHHZ";
	/** 幢楼后缀 */
	public static final String DZ_ZLHZ = "DZ_ZLHZ";
	/** 幢楼前缀 */
	public static final String DZ_ZLQZ = "DZ_ZLQZ";
	/** 街路巷 */
	public static final String DZ_JLX = "DZ_JLX";
	/** 民族 */
	public static final String RY_MZ = "RY_MZ";
	/** 性别 */
	public static final String RY_XB = "RY_XB";
	/** 国家地区 */
	public static final String TC_GJDQ = "TC_GJDQ";
	/** 行政区划 */
	public static final String TC_XZQH = "TC_XZQH";
	/** 责任区 */
	public static final String TC_ZRQ = "TC_ZRQ";
	/** 地理信息分类 */
	public static final String GGSJ_DICT = "GGSJ_DICT";
	/** 门牌号来源 */
	public static final String DZ_MPLY = "DZ_MPLY";
	/**
	 * 名称匹配集
	 */
	public static final String NAME_MATCHING = "NAME_MATCHING";
	//
	// public static final String USER = "USER";

	// public static final String USER_ZRQ="USER_ZRQ";

	static
	{
		MAP_LEVEL_TABLE_NAME.add(GGSJ_DICT);
		Field[] fields = DictUtil.class.getFields();
		int staticFinalModifier = Modifier.FINAL | Modifier.STATIC
				| Modifier.PUBLIC;
		for (Field field : fields)
		{
			if ((field.getModifiers() & staticFinalModifier) == staticFinalModifier
					&& field.getType() == String.class)
			{
				try
				{
					DICT_TABLE_NAME.add((String) field.get(null));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		/**
		 * 关于TC_XZQH字典具体使用哪种层级分类取决于具体的情况，一般的使用4-2-3-3（省市县区），但如果是省辖市如“天门市”这种，
		 * 就要使用6-3-3
		 */
		levelTable.put(TC_XZQH, new int[] { 4, 2, 3, 3 });
		// levelTable.put(TC_XZQH, new int[] { 6, 3, 3 });
		levelTable.put(TC_ZRQ, new int[] { 6, 2, 2, 2 });
	}

	private boolean hasSuchDict(String dictName)
	{
		return DICT_TABLE_NAME.contains(dictName);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MapLevelDictObject> getMapLevelChildren(
			MapLevelDictObject mapLevelDictObject, String dictName)
	{
		if (!MAP_LEVEL_TABLE_NAME.contains(dictName))
		{
			throw new RuntimeException("不是层级字典元素");
		}
		ArrayList<? extends DictObject> chacheList = dictCache.get(dictName);

		if (chacheList == null)
		{
			chacheList = getMapLevelDictObjects(dictName);
		}

		ArrayList<MapLevelDictObject> levelResults = (ArrayList<MapLevelDictObject>) chacheList;
		ArrayList<MapLevelDictObject> resultList = new ArrayList<MapLevelDictObject>();

		if (mapLevelDictObject != null)
		{
			for (MapLevelDictObject obj : levelResults)
			{
				String parentDm = mapLevelDictObject.getDM();
				if (parentDm.equals(obj.getParentDm()))
				{
					String mapping = mapLevelDictObject.getMapping();
					// 说明这个节点是该节点的子节点
					if (mapping != null && (!"".equals(mapping)))
					{
						obj.setMapping(mapping);
					}
					resultList.add(obj);
				}
			}
		}
		else
		{
			for (MapLevelDictObject obj : levelResults)
			{
				String mparentDm = obj.getParentDm();
				if (mparentDm == null || "".equals(mparentDm))
				{
					resultList.add(obj);
				}
			}
		}
		return resultList;
	}

	public static String nextDm(String dm, int nextIndex)
	{
		if (TextUtils.isEmpty(dm))
		{
			return dm;
		}
		else
		{
			int parseInt = Integer.parseInt(dm);
			parseInt += nextIndex;
			if (dm.startsWith("0"))
			{
				return String.format("%0" + dm.length() + "d", parseInt);
			}
			else
			{
				return parseInt + "";
			}
		}
	}

	public ArrayList<MapLevelDictObject> getUpLevelDicts(
			MapLevelDictObject mapLevelDictObject)
	{
		MapLevelDictObject grapDic = null;
		String parentDm = mapLevelDictObject.getParentDm();
		if (parentDm != null)
		{
			MapLevelDictObject parentDic = (MapLevelDictObject) getDictObject(
					DictUtil.GGSJ_DICT, parentDm);
			if (parentDic.getParentDm() != null)
			{
				grapDic = (MapLevelDictObject) getDictObject(
						DictUtil.GGSJ_DICT, parentDic.getParentDm());
			}
		}
		return getMapLevelChildren(grapDic, DictUtil.GGSJ_DICT);
	}
}