package com.golic.wycj.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.content.ContentValues;

import com.golic.wycj.Constans;
import com.golic.wycj.LoginUser;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.PhotoDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.BzdzPhoto;
import com.golic.wycj.domain.CS;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.Floor;
import com.golic.wycj.domain.GGSS;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.QSYDW;
import com.golic.wycj.domain.QTDW;
import com.golic.wycj.domain.QTJTXX;
import com.golic.wycj.domain.Room;
import com.golic.wycj.domain.Unit;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.domain.ZHJG;

public class ExcelUtil
{
	private static void deleteFile(File file)
	{
		if (file.exists())
		{
			if (file.isFile())
			{
				file.delete();
			}
			else
			{
				File[] listFiles = file.listFiles();
				if (listFiles.length == 0)
				{
					file.delete();
				}
				else
				{
					for (File f : listFiles)
					{
						deleteFile(f);
					}
					if (file.listFiles().length == 0)
					{
						file.delete();
					}
				}
			}
		}
	}

	public static void importFile(File file, YwzyDaoImpl ywzyDaoImpl,
			BzdzDaoImpl bzdzDaoImpl, GgsjDaoImpl ggsjDaoImpl)
	{
		String path = file.getPath();
		File ywzyFile = new File(path, "业务专用");
		File bzdzFile = new File(path, "标准地址");
		File ggsjFile = new File(path, "公共数据");

		if (bzdzFile.exists())
		{
			importBzdzs(bzdzFile, bzdzDaoImpl);
		}
		ArrayList<String> list = bzdzDaoImpl.getKeys();
		if (ggsjFile.exists())
		{
			importGgsjs(ggsjFile, ggsjDaoImpl, list);
		}
		if (ywzyFile.exists())
		{
			importYwzys(ywzyFile, ywzyDaoImpl);
		}
	}

	private static void importGgsjs(File file, GgsjDaoImpl ggsjDaoImpl,
			ArrayList<String> bzdzKeys)
	{

		File[] listFiles = file.listFiles();
		for (File f : listFiles)
		{
			String name = f.getName();
			if ("场所.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_CS", f, csTitle, bzdzKeys);
			}
			else if ("交通设施.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_JTSS", f, null, null);
			}
			else if ("公共设施.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_GGSS", f, ggssTitle, null);
			}
			else if ("企事业单位.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_QSYDW", f, qsydwTitle,
						bzdzKeys);
			}
			else if ("其他单位.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_QTDW", f, qtdwTitle, bzdzKeys);
			}
			else if ("其他交通信息.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_QTJTXX", f, qtjtxxTitle,
						bzdzKeys);
			}
			else if ("驻华机构.xls".equals(name))
			{
				importGgsjItem(ggsjDaoImpl, "GGSJ_ZHJG", f, zhjgTitle, bzdzKeys);
			}
		}
	}

	private static void importGgsjItem(GgsjDaoImpl ggsjDaoImpl,
			String tableName, File file, String[] title,
			ArrayList<String> bzdzKeys)
	{
		try
		{
			Workbook book = Workbook.getWorkbook(file);
			Sheet sheet = book.getSheet(0);
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++)
			{
				ContentValues values = new ContentValues();
				int len = ggsjTitle.length;
				for (int j = 0; j < len; j++)
				{
					values.put(ggsjTitle[j], sheet.getCell(j, i).getContents());
				}
				if (title != null)
				{
					for (int k = 0; k < title.length; k++)
					{
						values.put(title[k], sheet.getCell(k + len, i)
								.getContents());
					}
				}
				if (bzdzKeys != null)
				{
					// 有地址的要先判断地址是否已经插入，否则跳过
					String bzdzId = values.getAsString("BZDZ_ID");
					if (bzdzKeys.contains(bzdzId))
					{
						ggsjDaoImpl.insert(tableName, values);
					}
				}
				else
				{
					ggsjDaoImpl.insert(tableName, values);
				}
			}
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// private static ContentValues getBaseAttrs(Sheet sheet, int RowNum)
	// {
	// ContentValues values = new ContentValues();
	// String id = sheet.getCell(0, RowNum).getContents();
	// String x = sheet.getCell(1, RowNum).getContents();
	// String y = sheet.getCell(2, RowNum).getContents();
	// String djr = sheet.getCell(3, RowNum).getContents();
	// String djsj = sheet.getCell(4, RowNum).getContents();
	// String gxdwdm = sheet.getCell(5, RowNum).getContents();
	// String fldm = sheet.getCell(6, RowNum).getContents();
	// String gbdm = sheet.getCell(7, RowNum).getContents();
	// String lx = sheet.getCell(8, RowNum).getContents();
	// String xgr = sheet.getCell(9, RowNum).getContents();
	// String gxsj = sheet.getCell(10, RowNum).getContents();
	// String ys = sheet.getCell(11, RowNum).getContents();
	// String bz = sheet.getCell(12, RowNum).getContents();
	// String mc = sheet.getCell(13, RowNum).getContents();
	// String zmc = sheet.getCell(14, RowNum).getContents();
	// String bsStr = sheet.getCell(15, RowNum).getContents();
	// String levelStr = sheet.getCell(16, RowNum).getContents();
	// String comment = sheet.getCell(17, RowNum).getContents();
	// int bs = Integer.parseInt(bsStr);
	// int level = Integer.parseInt(levelStr);
	// values.put("ID", id);
	// values.put("X", x);
	// values.put("Y", y);
	// values.put("DJR", djr);
	// values.put("XGR", xgr);
	// values.put("DJSJ", djsj);
	// values.put("GXSJ", gxsj);
	// values.put("GXDWDM", gxdwdm);
	// values.put("FLDM", fldm);
	// values.put("GBDM", gbdm);
	// values.put("LX", lx);
	// values.put("YS", ys);
	// values.put("BZ", bz);
	// values.put("MC", mc);
	// values.put("ZMC", zmc);
	// values.put("BS", bs);
	// values.put("LEVEL", level);
	// values.put("LEVEL_COMMENT", comment);
	// return values;
	// }

	private static void importBzdzs(File bzdzFile, BzdzDaoImpl impl)
	{
		File file = new File(bzdzFile.getPath(), "标准地址.xls");
		if (file.exists())
		{
			try
			{
				// 首先导入业务专用数据
				Workbook book = Workbook.getWorkbook(file);
				int itemNum = 2;
				for (int index = 0; index < itemNum; index++)
				{
					Sheet sheet = book.getSheet(index);
					int rows = sheet.getRows();
					for (int i = 1; i < rows; i++)
					{
						String id = sheet.getCell(0, i).getContents();
						String ssxq = sheet.getCell(1, i).getContents();
						String jlx = sheet.getCell(2, i).getContents();
						String mpqz = sheet.getCell(3, i).getContents();
						String mph = sheet.getCell(4, i).getContents();
						String mphz = sheet.getCell(5, i).getContents();
						String fh = sheet.getCell(6, i).getContents();
						String fhhz = sheet.getCell(7, i).getContents();
						String zlqz = sheet.getCell(8, i).getContents();
						String zlh = sheet.getCell(9, i).getContents();
						String zlhz = sheet.getCell(10, i).getContents();
						String dyh = sheet.getCell(11, i).getContents();
						String sh = sheet.getCell(12, i).getContents();
						String shhz = sheet.getCell(13, i).getContents();
						String mlxz = sheet.getCell(14, i).getContents();
						String jwzrq = sheet.getCell(15, i).getContents();
						String jwh = sheet.getCell(16, i).getContents();
						// String dzsx = ggsjDzsheet.getCell(17,
						// i).getContents();
						String djsj = sheet.getCell(18, i).getContents();
						// String dmxz = ggsjDzsheet.getCell(19,
						// i).getContents();
						String gxsj = sheet.getCell(20, i).getContents();
						String lch = sheet.getCell(21, i).getContents();
						String lchz = sheet.getCell(22, i).getContents();
						String djr = sheet.getCell(23, i).getContents();
						String xgr = sheet.getCell(24, i).getContents();
						String mply = sheet.getCell(25, i).getContents();
						String dzbh = sheet.getCell(26, i).getContents();
						String x = sheet.getCell(27, i).getContents();
						String y = sheet.getCell(28, i).getContents();
						String xqm = sheet.getCell(29, i).getContents();
						String bz = sheet.getCell(30, i).getContents();
						String dzlxStr = sheet.getCell(31, i).getContents();
						int dzlx = Integer.parseInt(dzlxStr);
						MPHM mphm = new MPHM(x, y, djr, djsj, jwzrq, xgr, gxsj,
								bz, id, ssxq, jwh, mlxz, jlx, mph, mpqz, mphz,
								fh, fhhz, mply, dzlx);
						ExtraDz extraDz = new ExtraDz(xqm, zlqz, zlh, zlhz,
								dyh, lch, lchz, sh, shhz);
						impl.insert(mphm, extraDz, dzbh);
					}
				}
				book.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void importYwzys(File file, YwzyDaoImpl ywzyDaoImpl)
	{
		File[] listFiles = file.listFiles();
		for (File f : listFiles)
		{
			String name = f.getName();
			if ("机构.xls".equals(name))
			{
				importYwzyItem(f, ywzyDaoImpl, "YWZY_JG");
			}
			else if ("旅馆.xls".equals(name))
			{
				importYwzyItem(f, ywzyDaoImpl, "YWZY_LG");
			}
			else if ("网吧.xls".equals(name))
			{
				importYwzyItem(f, ywzyDaoImpl, "YWZY_WB");
			}
		}
	}

	private static void importYwzyItem(File file, YwzyDaoImpl ywzyDaoImpl,
			String tableName)
	{
		try
		{
			Workbook book = Workbook.getWorkbook(file);
			Sheet sheet = book.getSheet(0);
			// int columns = sheet.getColumns();
			int rows = sheet.getRows();
			int idColumn = 0;
			int xColumn = 6;
			int yColumn = 7;
			// int bsColumn = 8;
			int ggsjIdColumn = 9;
			for (int i = 1; i < rows; i++)
			{
				String id = sheet.getCell(idColumn, i).getContents();
				String x = sheet.getCell(xColumn, i).getContents();
				String y = sheet.getCell(yColumn, i).getContents();
				String ggsjId = sheet.getCell(ggsjIdColumn, i).getContents();
				ywzyDaoImpl.update(tableName, id, x, y, ggsjId);
			}
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(
			"yyyyMMdd HH时mm分ss秒", Locale.getDefault());

	/**
	 * 导出所有的数据
	 */
	public static String exportAll(boolean[] checked,PhotoDaoImpl photoImpl)
	{
		StringBuilder sb = new StringBuilder();
		mphmAttrs.clear();
		String time = TIME_FORMAT.format(new Date());
		String fileName = time + " " + LoginUser.xm;
		String baseDir = Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
				+ Constans.DATA_PATH + "export/";
		// 首先创建文件夹,路径为："/golic/data/export/20140828 15时30分30秒 罗会涛"
		// File baseFile = new File(Constans.SOURCE_BASE_PATH
		// + Constans.SOURCE_PATH + Constans.DATA_PATH + "export");
		// if (!baseFile.exists())
		// {
		// baseFile.mkdir();
		// }
		File dirfile = new File(baseDir + fileName);
		if (!dirfile.exists())
		{
			dirfile.mkdir();
		}
		String dirName = dirfile.getPath();
		if (checked[0])
		{
			File file = new File(dirName + "/公共数据");
			if (!file.exists())
			{
				file.mkdir();
			}
			sb.append(exportGgsj(file.getPath()));
		}
		if (checked[1])
		{
			File file = new File(dirName + "/标准地址");
			if (!file.exists())
			{
				file.mkdir();
			}
			String bzdzStr = exportBzdz(file.getPath());
			if (!"".equals(bzdzStr))
			{
				sb.append(bzdzStr).append("\r\n");
			}
		}
		if (checked[2])
		{
			File file = new File(dirName + "/业务专用");
			if (!file.exists())
			{
				file.mkdir();
			}
			String ywzyStr = exportYwzy(file.getPath());
			if (!"".equals(ywzyStr))
			{
				sb.append(ywzyStr);
			}
		}
		//导出照片
		if(checked[0]||checked[1]){
			File file = new File(dirName + "/照片");
			if (!file.exists())
			{
				file.mkdir();
			}
			String xpStr = exportXp(file.getPath(),photoImpl);
			if (!"".equals(xpStr))
			{
				sb.append(xpStr);
			}
		}
		String result = sb.toString();
		if ("".equals(result))
		{
			// 到此所有数据全部顺利导出，现在要判断文件日期是否重复，如有重复删除前一个
			File file = new File(baseDir);
			File[] files = file.listFiles();
			String[] split = time.split(" ");
			for (File f : files)
			{
				String name = f.getName();
				if (!name.equals(fileName))
				{
					// 发现有重复的数据
					if (name.startsWith(split[0])
							&& name.endsWith(LoginUser.xm))
					{
						// 删除文件夹f
						deleteFile(f);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 导出所有的公共数据
	 */
	private static String exportGgsj(String dirName)
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Type, ArrayList<BaseAttrs>> entry : Source.ggsjs
				.entrySet())
		{
			Type type = entry.getKey();
			ArrayList<BaseAttrs> list = entry.getValue();
			if (list.size() > 0)
			{
				String result = exportGgsj(dirName, type, list);
				if (!"".equals(result))
				{
					sb.append(result + "导出失败！").append("\r\n");
				}
			}
		}
		return sb.toString();
	}

	private static String exportXp(String dirName,PhotoDaoImpl photoImpl){
		File file = new File(dirName, "照片.xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheetOne = book.createSheet("照片", 0);
			fillTitle(sheetOne, xpTitle);
			ArrayList<BzdzPhoto> xpAttrs=photoImpl.findAllPhotos();
			int fzzSize = xpAttrs.size();
			for (int i = 0; i < fzzSize; i++)
			{
				BzdzPhoto attrs = xpAttrs.get(i);
				String[] values = getBzdzXpValue(attrs);
				int length = values.length;
				for (int j = 0; j < length; j++)
				{
					Label lable = new Label(j, i + 1, values[j]);
					sheetOne.addCell(lable);
				}
			}
			
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "照片导出失败";
		}
		return "";
	}
	
	
	/**
	 * 导出所有的标准地址（ 非住宅地址导出依据的是公共数据外键，如果公共数据被删除，非住宅数据不会被导出）
	 */
	private static String exportBzdz(String dirName)
	{
		File file = new File(dirName, "标准地址.xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheetOne = book.createSheet("非住宅", 0);
			fillTitle(sheetOne, bzdzTitle);
			int fzzSize = mphmAttrs.size();
			for (int i = 0; i < fzzSize; i++)
			{
				BaseAttrs attrs = mphmAttrs.get(i);
				String[] values = getGgsjBzdzValue(attrs);
				int length = values.length;
				for (int j = 0; j < length; j++)
				{
					Label lable = new Label(j, i + 1, values[j]);
					sheetOne.addCell(lable);
				}
			}
			// 到此已经导出所有公共数据的标准地址，接下来导出所有住宅的标准地址
			
			// 首先导出幢楼数据
			WritableSheet sheetTwo = book.createSheet("住宅", 1);
			fillTitle(sheetTwo, bzdzTitle);
			int zzSize = Source.buildings.size();
			for (int i = 0; i < zzSize; i++)
			{
				Building building = Source.buildings.get(i);
				String[] values = getBuildingValue(building);
				int length = values.length;
				for (int j = 0; j < length; j++)
				{
					Label lable = new Label(j, i + 1, values[j]);
					sheetTwo.addCell(lable);
				}
			}
			// 接着导出房间数据
			int index = zzSize;
			for (int i = 0; i < zzSize; i++)
			{
				Building building = Source.buildings.get(i);
				BaseBuilding baseBuilding = building.baseBuilding;
				MPHM mphm = baseBuilding.mphm;
				if (mphm.DZLX == 2)
				{
					Unit[] units = building.units;
					for (Unit u : units)
					{
						String dyh = u.dyh;
						Floor[] floors = u.floors;
						for (Floor f : floors)
						{
							String lch = f.lch;
							String sufLch = f.sufLch;
							Room[] rooms = f.rooms;
							for (Room r : rooms)
							{
								String[] values = getRoomValue(r, mphm,
										baseBuilding, dyh, lch, sufLch);
								int len = values.length;
								index++;
								for (int j = 0; j < len; j++)
								{
									Label lable = new Label(j, index, values[j]);
									sheetTwo.addCell(lable);
								}
							}
						}
					}
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "标准地址导出失败";
		}
		return "";
	}

	/**
	 * 导出所有的业务专用数据
	 */
	private static String exportYwzy(String dirName)
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, ArrayList<Ywzy>> entry : Source.ywzys.entrySet())
		{
			String key = entry.getKey();
			ArrayList<Ywzy> list = entry.getValue();
			File file = new File(dirName, key + ".xls");
			try
			{
				WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
				WritableSheet sheet = book.createSheet(key, 0);
				fillTitle(sheet, ywzyTitle);
				int size = list.size();
				for (int i = 0; i < size; i++)
				{
					Ywzy ywzy = list.get(i);
					String[] values = getYwzyValue(ywzy);
					int length = values.length;
					for (int j = 0; j < length; j++)
					{
						Label lable = new Label(j, i + 1, values[j]);
						sheet.addCell(lable);
					}
				}
				// 到此已经导出所有公共数据的标准地址，接下来导出所有住宅的标准地址
				// 首先导出幢楼数据
				book.write();
				book.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				sb.append(key + "导出失败").append("\r\n");
			}
		}
		return sb.toString();
	}

	private static String exportGgsj(String dirName, Type type,
			ArrayList<BaseAttrs> list)
	{
		String result = "";
		switch (type)
		{
		case GGSJ_CS:
			ArrayList<CS> csList = new ArrayList<CS>();
			for (BaseAttrs attrs : list)
			{
				csList.add((CS) attrs);
			}
			result = exportCs(type, dirName, csList);
			break;
		case GGSJ_QSYDW:
			ArrayList<QSYDW> qsydwList = new ArrayList<QSYDW>();
			for (BaseAttrs attrs : list)
			{
				qsydwList.add((QSYDW) attrs);
			}
			result = exportQsydw(type, dirName, qsydwList);
			break;
		case GGSJ_QTDW:
			ArrayList<QTDW> qtdwList = new ArrayList<QTDW>();
			for (BaseAttrs attrs : list)
			{
				qtdwList.add((QTDW) attrs);
			}
			result = exportQtdw(type, dirName, qtdwList);
			break;
		case GGSJ_QTJTXX:
			ArrayList<QTJTXX> qtjtxxList = new ArrayList<QTJTXX>();
			for (BaseAttrs attrs : list)
			{
				qtjtxxList.add((QTJTXX) attrs);
			}
			result = exportQtjtxx(type, dirName, qtjtxxList);
			break;
		case GGSJ_ZHJG:
			ArrayList<ZHJG> zhjgList = new ArrayList<ZHJG>();
			for (BaseAttrs attrs : list)
			{
				zhjgList.add((ZHJG) attrs);
			}
			result = exportZhjg(type, dirName, zhjgList);
			break;
		case GGSJ_GGSS:
			ArrayList<GGSS> ggssList = new ArrayList<GGSS>();
			for (BaseAttrs attrs : list)
			{
				ggssList.add((GGSS) attrs);
			}
			result = exportGgss(type, dirName, ggssList);
			break;
		case GGSJ_JTSS:
			result = exportJtss(type, dirName, list);
			break;
		}
		return result;
	}

	private static final String[] ywzyTitle = new String[] { "ID", "JWZRQ",
			"JLX", "MPH", "MLXZ", "MC", "X", "Y", "BS", "GGSJ_ID" };

	private static final String[] bzdzTitle = new String[] { "ID", "SSXQ",
			"JLX", "MPQZ", "MPH", "MPHZ", "FH", "FHHZ", "ZLQZ", "ZLH", "ZLHZ",
			"DYH", "SH", "SHHZ", "MLXZ", "JWZRQ", "JWH", "DZSX", "DJSJ",
			"DMXZ", "GXSJ", "LCH", "LCHZ", "DJR", "XGR", "MPLY", "DZBH", "X",
			"Y", "XQM", "BZ", "DZLX" };
	
	private static final String[] xpTitle = new String[] { "ID", "BZDZ_ID",
		"PATH", "FILE_NAME"};

	private static final String[] ggsjTitle = new String[] { "ID", "X", "Y",
			"DJR", "DJSJ", "GXDWDM", "FLDM", "GBDM", "LX", "XGR", "GXSJ", "YS",
			"BZ", "MC", "ZMC", "BS", "LEVEL", "LEVEL_COMMENT", "BZDZ_ID", "DZ" };
	private static final String[] csTitle = new String[] { "DLBM", "LXR",
			"LXDH", "SJZGDW" };
	private static final String[] ggssTitle = new String[] { "LXR", "LXDH" };
	private static final String[] qsydwTitle = new String[] { "ZZJGDM", "JYFW",
			"QYXZ", "ZCZB", "FDDBR", "ZCSJ", "ZCDD", "SJZGDW" };
	private static final String[] qtdwTitle = new String[] { "ZZJGDM", "LXR",
			"LXDH", "SJZGDW" };
	private static final String[] qtjtxxTitle = new String[] { "BH", "SZWZ" };
	private static final String[] zhjgTitle = new String[] { "ZZJGDM", "LXR",
			"LXDH", "GJ" };

	private static void fillGgsjBaseTitle(WritableSheet sheet)
			throws RowsExceededException, WriteException
	{
		for (int i = 0; i < ggsjTitle.length; i++)
		{
			Label lable = new Label(i, 0, ggsjTitle[i]);
			sheet.addCell(lable);
		}
	}

	private static void fillGgsjExtraTitle(WritableSheet sheet,
			String[] extraTitle) throws RowsExceededException, WriteException
	{
		int len = ggsjTitle.length;
		for (int i = len; i < len + extraTitle.length; i++)
		{
			Label lable = new Label(i, 0, extraTitle[i - len]);
			sheet.addCell(lable);
		}
	}

	private static ArrayList<BaseAttrs> mphmAttrs = new ArrayList<BaseAttrs>();

	private static void fillTitle(WritableSheet sheet, String[] title)
			throws RowsExceededException, WriteException
	{
		for (int i = 0; i < title.length; i++)
		{
			Label lable = new Label(i, 0, title[i]);
			sheet.addCell(lable);
		}
	}

	private static String[] getYwzyValue(Ywzy ywzy)
	{
		return new String[] { ywzy.getId(), ywzy.getJwzrq(), ywzy.getJlx(),
				ywzy.getMph(), ywzy.getMlxz(), ywzy.getMc(), ywzy.getX(),
				ywzy.getY(), "" + ywzy.getBs(), ywzy.getGgsj_id() };
	}

	private static String[] getGgsjBzdzValue(BaseAttrs attrs)
	{
		MPHM mphm = attrs.mphm;
		ExtraDz extraDz = attrs.extraDz;
		if (extraDz == null)
		{
			return new String[] { mphm.ID, mphm.SSXQ, mphm.JLX, mphm.MPQZ,
					mphm.MPH, mphm.MPHZ, mphm.FH, mphm.FHHZ, null, null, null,
					null, null, null, mphm.MLXZ, mphm.JWZRQ, mphm.JWH, null,
					mphm.DJSJ, null, mphm.GXSJ, null, null, mphm.DJR, mphm.XGR,
					mphm.MPLY, null, mphm.X, mphm.Y, null, mphm.BZ,
					"" + mphm.DZLX };
		}
		else
		{
			return new String[] { mphm.ID, mphm.SSXQ, mphm.JLX, mphm.MPQZ,
					mphm.MPH, mphm.MPHZ, mphm.FH, mphm.FHHZ, extraDz.preZlh,
					extraDz.zlh, extraDz.sufZlh, extraDz.dyh, extraDz.sh,
					extraDz.sufSh, mphm.MLXZ, mphm.JWZRQ, mphm.JWH, null,
					mphm.DJSJ, null, mphm.GXSJ, extraDz.lch, extraDz.sufLch,
					mphm.DJR, mphm.XGR, mphm.MPLY, null, mphm.X, mphm.Y,
					extraDz.xqm, mphm.BZ, "" + mphm.DZLX };
		}
	}
	
	private static String[] getBzdzXpValue(BzdzPhoto bzdzPhoto)
	{
			return new String[] { bzdzPhoto.id, bzdzPhoto.bzdzId, bzdzPhoto.path, bzdzPhoto.fileName
					};
	}

	private static String[] getBuildingValue(Building building)
	{
		BaseBuilding baseBuilding = building.baseBuilding;
		MPHM mphm = baseBuilding.mphm;
		return new String[] { mphm.ID, mphm.SSXQ, mphm.JLX, mphm.MPQZ,
				mphm.MPH, mphm.MPHZ, mphm.FH, mphm.FHHZ, baseBuilding.preZlh,
				baseBuilding.zlh, baseBuilding.sufZlh, null, null, null,
				mphm.MLXZ, mphm.JWZRQ, mphm.JWH, null, mphm.DJSJ, null,
				mphm.GXSJ, null, null, mphm.DJR, mphm.XGR, mphm.MPLY, null,
				mphm.X, mphm.Y, baseBuilding.xqm, mphm.BZ, "" + mphm.DZLX };
	}

	private static String[] getRoomValue(Room room, MPHM mphm,
			BaseBuilding baseBuilding, String dyh, String lch, String sufLch)
	{
		return new String[] { room.zzbh, mphm.SSXQ, mphm.JLX, mphm.MPQZ,
				mphm.MPH, mphm.MPHZ, mphm.FH, mphm.FHHZ, baseBuilding.preZlh,
				baseBuilding.zlh, baseBuilding.sufZlh, dyh, room.sh,
				room.sufSh, mphm.MLXZ, mphm.JWZRQ, mphm.JWH, null, mphm.DJSJ,
				null, mphm.GXSJ, lch, sufLch, mphm.DJR, mphm.XGR, mphm.MPLY,
				mphm.ID, mphm.X, mphm.Y, baseBuilding.xqm, mphm.BZ, "3" };
	}

	private static String[] getGgsjBaseValue(BaseAttrs attrs)
	{
		String BZDZ_ID = "";
		String DZ = "";
		if (attrs.mphm != null)
		{
			mphmAttrs.add(attrs);
			BZDZ_ID = attrs.mphm.ID;
			DZ = attrs.DZ;
		}
		return new String[] { attrs.ID, attrs.X, attrs.Y, attrs.DJR,
				attrs.DJSJ, attrs.GXDWDM, attrs.FLDM, attrs.GBDM, attrs.LX,
				attrs.XGR, attrs.GXSJ, attrs.YS, attrs.BZ, attrs.MC, attrs.ZMC,
				"" + attrs.bs, "" + attrs.level, attrs.comment, BZDZ_ID, DZ };
	}

	private static String[] getCsValue(CS cs)
	{
		return new String[] { null, cs.LXR, cs.LXDH, null };
	}

	private static String[] getGgssValue(GGSS ggss)
	{
		return new String[] { ggss.LXR, ggss.LXDH };
	}

	private static String[] getQsydwValue(QSYDW qsydw)
	{
		return new String[] { null, null, null, qsydw.ZCZB, qsydw.FDDBR,
				qsydw.ZCSJ, qsydw.ZCDD, null };
	}

	private static String[] getQtdwValue(QTDW qtdw)
	{
		return new String[] { null, qtdw.LXR, qtdw.LXDH, null };
	}

	private static String[] getQtjtxxValue(QTJTXX qtjtxx)
	{
		return new String[] { null, qtjtxx.SZWZ };
	}

	private static String[] getZhjgValue(ZHJG zhjg)
	{
		return new String[] { null, zhjg.LXR, zhjg.LXDH, zhjg.GJ };
	}

	private static void fillGgsjBaseValue(int rowNum, BaseAttrs attrs,
			WritableSheet sheet) throws RowsExceededException, WriteException
	{
		String[] values = getGgsjBaseValue(attrs);
		for (int i = 0; i < ggsjTitle.length; i++)
		{
			Label lable = new Label(i, rowNum, values[i]);
			sheet.addCell(lable);
		}
	}

	private static String exportCs(Type type, String dirName, ArrayList<CS> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, csTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				CS attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getCsValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportGgss(Type type, String dirName,
			ArrayList<GGSS> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, ggssTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				GGSS attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getGgssValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportJtss(Type type, String dirName,
			ArrayList<BaseAttrs> list)
	{
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				BaseAttrs attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportQsydw(Type type, String dirName,
			ArrayList<QSYDW> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, qsydwTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				QSYDW attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getQsydwValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportQtdw(Type type, String dirName,
			ArrayList<QTDW> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, qtdwTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				QTDW attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getQtdwValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportQtjtxx(Type type, String dirName,
			ArrayList<QTJTXX> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, qtjtxxTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				QTJTXX attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getQtjtxxValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	private static String exportZhjg(Type type, String dirName,
			ArrayList<ZHJG> list)
	{
		int len = ggsjTitle.length;
		File file = new File(dirName, type.getName() + ".xls");
		try
		{
			WritableWorkbook book = Workbook.createWorkbook(file); // 根据book创建一个操作对象
			WritableSheet sheet = book.createSheet(type.getName(), 0);
			fillGgsjBaseTitle(sheet);
			fillGgsjExtraTitle(sheet, zhjgTitle);
			int size = list.size();
			for (int i = 0; i < size; i++)
			{
				ZHJG attrs = list.get(i);
				fillGgsjBaseValue(i + 1, attrs, sheet);
				String[] values = getZhjgValue(attrs);
				for (int j = len; j < len + values.length; j++)
				{
					Label lable = new Label(j, i + 1, values[j - len]);
					sheet.addCell(lable);
				}
			}
			book.write();
			book.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return type.getName();
		}
		return "";
	}

	// public static boolean exportDataAnalyse(ArrayList<Ggsj> mcList,
	// ArrayList<BaseBzdz> mphList, ArrayList<Building> zlhList)
	// {
	// File file = Constant.zjbgFile;
	// try
	// {
	// WritableWorkbook workBook = Workbook.createWorkbook(file); //
	// 根据book创建一个操作对象
	// if (!mcList.isEmpty())
	// {
	// WritableSheet mcSheet = workBook.createSheet("名称", 0);
	// String[] mcLabels = new String[] { "主键", "类型", "名称", "错误级别",
	// "错误原因", "门楼详址", "操作人", "操作时间" };
	// addHead(mcLabels, mcSheet);
	// for (int i = 0; i < mcList.size(); i++)
	// {
	// Ggsj ggsj = mcList.get(i);
	// String[] values = getMcFieldValues(ggsj);
	// for (int j = 0; j < values.length; j++)
	// {
	// Label lable = new Label(j, i + 1, values[j]);
	// mcSheet.addCell(lable);
	// }
	// }
	// }
	// if (!mphList.isEmpty())
	// {
	// System.out.println("mphList:" + mphList);
	// WritableSheet mphSheet = workBook.createSheet("门牌号", 1);
	// String[] mphLabels = new String[] { "主键", "门牌号", "门牌来源",
	// "门楼详址", "操作人", "操作时间" };
	// addHead(mphLabels, mphSheet);
	// for (int i = 0; i < mphList.size(); i++)
	// {
	// BaseBzdz baseBzdz = mphList.get(i);
	// String[] values = getMphFieldValues(baseBzdz);
	// for (int j = 0; j < values.length; j++)
	// {
	// Label lable = new Label(j, i + 1, values[j]);
	// mphSheet.addCell(lable);
	// }
	// }
	// }
	// if (!zlhList.isEmpty())
	// {
	// WritableSheet zlhSheet = workBook.createSheet("幢楼号", 2);
	// String[] zlhLabels = new String[] { "主键", "门牌号", "幢楼号", "门楼详址",
	// "操作人", "操作时间" };
	// addHead(zlhLabels, zlhSheet);
	// // 向sheet中添加内容
	// for (int i = 0; i < zlhList.size(); i++)
	// {
	// Building building = zlhList.get(i);
	// String[] values = getZlhFieldValues(building);
	// for (int j = 0; j < values.length; j++)
	// {
	// Label lable = new Label(j, i + 1, values[j]);
	// zlhSheet.addCell(lable);
	// }
	// }
	// }
	//
	// workBook.write();
	// workBook.close();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }
}