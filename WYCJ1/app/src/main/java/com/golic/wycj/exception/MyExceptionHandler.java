package com.golic.wycj.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.os.Build;

import com.golic.wycj.util.SdCardUtil;

public class MyExceptionHandler implements UncaughtExceptionHandler
{
	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		Field[] fields = Build.class.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		for (Field f : fields)
		{
			try
			{
				f.setAccessible(true);
				sb.append(f.getName());
				sb.append(":");
				sb.append(f.get(new Build()));
				sb.append("\r\n");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// 收集用户的信息到sd卡文件中
		SdCardUtil.saveMobileConfigLog(sb.toString());
		// 收集错误日志,堆栈信息没有收集
		SdCardUtil.saveErrorLog(ex);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}