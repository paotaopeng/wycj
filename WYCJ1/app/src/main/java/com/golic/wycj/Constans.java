package com.golic.wycj;

public class Constans
{
	public static final String FINISH_ACTION = "finish";
	/**
	 * 资源文件的根目录，该目录由于不同的设备可能有所变动。资源的最终路径应该是：SOURCE_BASE_PATH+SOURCE_PATH+'相应类型'。
	 */
	public static String SOURCE_BASE_PATH = "/mnt/sdcard";
	public static final String SOURCE_PATH = "/golic/";
	/**
	 * 存储系统的所有数据
	 */
	public static final String DATA_PATH = "data/";
	public static String MAP_SOURCE = "";
	public static final String DB_NAME = "map_dict.db";
	
	// 是否允许替换责任区
	// public static final boolean CHANGE_ZRQ = true;
	// 是否存在街路巷、责任区关系字典
	public static final boolean EXIST_JLX_ZRQ_TABLE = false;

	public static double centreX = 113.38;
	public static double centreY = 31.72;
	public static boolean simpleSelect = false;
	public static final String PROJECT_FOLDER_PATH = SOURCE_BASE_PATH+SOURCE_PATH+"photo/";
	public static final int PHOTO_NUM_MIN=0;
	public static final int PHOTO_NUM_MAX=5;
}