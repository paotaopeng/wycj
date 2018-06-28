package com.golic.wycj.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.os.StatFs;

import com.golic.wycj.Constans;

public class SdCardUtil
{
	/**
	 * 将目录分隔符替换成与平台无关的
	 * 
	 * @param dir
	 *            路径
	 * @return 返回替换后的结果
	 */
	private static String replaceFileSeparator(String dir)
	{
		// 先判断是否存在多级目录如果是先替换成平台无关的文件路径分隔符
		if (dir.contains("\\") || dir.contains("/"))
		{
			dir = dir.replace("\\", File.separator);
			dir = dir.replace("/", File.separator);
		}
		return dir;
	}

	/**
	 * 判断sd卡是否存在并且可读写
	 * 
	 * @return
	 */
	public static boolean available()
	{
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 判断sd卡是否可以写得下一个流的内容
	 * 
	 * @param in
	 * @return
	 */
	public static boolean isWritable(InputStream in)
	{
		try
		{
			return available()
					&& (in.available() < getTotalExternalMemorySize());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 得到sd卡的可用空间大小
	 * 
	 * @return
	 */
	public static long getTotalExternalMemorySize()
	{
		if (available())
		{
			File path = Environment.getExternalStorageDirectory();// 获取外部存储目录即
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		}
		return 0;
	}

	/**
	 * 将流中的数据写入到sd卡指定的目录中
	 * 
	 * @param in
	 *            源流
	 * @param dir
	 *            sd卡的目录,如果是null就默认是根目录
	 * @param fileName
	 *            目录下的文件
	 * @return
	 */
	public static boolean write2Sd(InputStream in, String dir, String fileName)
	{
		if (in == null || fileName == null || "".equals(fileName))
		{
			return false;
		}
		if (isWritable(in))
		{
			FileOutputStream out = getSdFileOutputStream(dir, fileName);
			if (out != null)
			{
				return transfer(in, out);
			}
			return false;
		}
		return false;
	}

	private static FileOutputStream getSdFileOutputStream(String dir,
			String fileName)
	{
		try
		{
			File sdDir = Environment.getExternalStorageDirectory();
			// 指定了目录就创建目录
			if (dir != null && !"".equals(dir))
			{
				File fileDir = new File(sdDir, replaceFileSeparator(dir));
				// 目录不存在就创建
				if (!fileDir.exists())
				{
					fileDir.mkdirs();
				}
				return new FileOutputStream(new File(fileDir, fileName));
			}
			else
			{
				// 没有指定目录就用sd根目录
				return new FileOutputStream(new File(sdDir, fileName));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static boolean transfer(InputStream in, OutputStream out)
	{
		try
		{
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = in.read(buf)) != -1)
			{
				out.write(buf, 0, len);
			}
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// public static void saveErrorLog(String info)
	// {
	// boolean available = SdCardUtil.available();
	// if (available)
	// {
	// // File sdDir = Environment.getExternalStorageDirectory();
	// // File fileDir = new File(sdDir, "log");
	// File fileDir = new File(Constant.LOG_PATH);
	// File logFile = new File(fileDir, "error.txt");
	// try
	// {
	// FileWriter writer = new FileWriter(logFile, true);
	// writer.write(info);
	// writer.write("\r\n");
	// writer.flush();
	// writer.close();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// }

	// public static void saveErrorLog(StackTraceElement[] elements)
	// {
	// boolean available = SdCardUtil.available();
	// if (available)
	// {
	// // File sdDir = Environment.getExternalStorageDirectory();
	// // File fileDir = new File(sdDir, "log");
	// File fileDir = new File(Constant.LOG_PATH);
	// File logFile = new File(fileDir, "error.txt");
	// try
	// {
	// FileWriter writer = new FileWriter(logFile, true);
	// PrintWriter pw = new PrintWriter(writer, true);
	// pw.println();
	// for (StackTraceElement e : elements)
	// {
	// pw.println(formatTime(System.currentTimeMillis()) + "  "
	// + e);
	// }
	// pw.println("-----------------------");
	// pw.println();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// }

	public static void saveErrorLog(Throwable t)
	{
		boolean available = SdCardUtil.available();
		if (available)
		{
			File fileDir = new File(Constans.SOURCE_BASE_PATH
					+ Constans.SOURCE_PATH + "log");
			File logFile = new File(fileDir, "错误日志.txt");
//			if (logFile.exists())
//			{
//				return;
//			}
			try
			{
				FileWriter writer = new FileWriter(logFile, true);
				PrintWriter pw = new PrintWriter(writer, true);
				pw.println("错误发生时间：" + formatTime(System.currentTimeMillis()));
				t.printStackTrace(pw);
				pw.println("-------------------------");
				pw.println();
				pw.flush();
				pw.close();
			}
			catch (IOException e)
			{
				System.out.println("写入出错了");
				e.printStackTrace();
			}
		}
	}

	private static String formatTime(long installTime)
	{
		Date date = new Date(installTime);
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSSZ", Locale.CHINA);
		String time = format.format(date);
		return time;
	}

	public static void saveMobileConfigLog(String info)
	{
		boolean available = SdCardUtil.available();
		if (available)
		{
			File fileDir = new File(Constans.SOURCE_BASE_PATH
					+ Constans.SOURCE_PATH + "log");
			if(!fileDir.exists())
			{
				fileDir.mkdir();
			}
			File logFile = new File(fileDir, "设备信息.txt");
			try
			{
				FileWriter writer = new FileWriter(logFile);
				writer.write(info);
				writer.flush();
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// public static String readLogInfo()
	// {
	// // File sdDir = Environment.getExternalStorageDirectory();
	// // File fileDir = new File(sdDir, "log");
	// File fileDir = new File(Constant.LOG_PATH);
	// File logFile = new File(fileDir, "error.txt");
	// StringBuilder sb = new StringBuilder();
	// try
	// {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// new FileInputStream(logFile)));
	// String len;
	// while ((len = reader.readLine()) != null)
	// {
	// sb.append(len);
	// }
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// return sb.toString();
	// }
}
