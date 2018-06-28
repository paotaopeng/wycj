package com.golic.wycj;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LoginUser
{
	// TODO 每次发布需要修改
	public static String zrq = "420203630104";
	public static String zrqMc = "大智路警务室";
	public static int MARKER_NUM = 10;
	/**
	 * 警号（或者登录用户名）
	 */
	public static boolean isAdministrator = false;
	// public static String userId = "00001";
	public static String userName = "administrator";
	public static String xm = "administrator";
	// public static final String xzqh = "420111080000";
	public static String zrqFw = "";
	public static final SimpleDateFormat DJSJ_TIME = new SimpleDateFormat(
			"yyyy年MM月dd日HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat XGSJ_DATE = new SimpleDateFormat(
			"yyyyMMdd", Locale.getDefault());
}