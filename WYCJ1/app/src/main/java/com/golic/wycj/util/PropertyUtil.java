package com.golic.wycj.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.golic.wycj.Constans;

public class PropertyUtil
{
	private static Properties properties = new Properties();

	public static void init()
	{
		String path = Constans.SOURCE_BASE_PATH + Constans.SOURCE_PATH
				+ Constans.DATA_PATH + "golic.ini";
		FileInputStream in;
		try
		{
			in = new FileInputStream(path);
			properties.load(in);
			String centre_x = properties.getProperty("centre_x");
			String centre_y = properties.getProperty("centre_y");
			String xzqh_level = properties.getProperty("xzqh_level");
			String simple_select = properties.getProperty("simple_select");
			Constans.centreX = Double.valueOf(centre_x);
			Constans.centreY = Double.valueOf(centre_y);
			String[] split = xzqh_level.split("-");
			int[] arr = new int[split.length];
			for (int i = 0; i < split.length; i++)
			{
				arr[i] = Integer.valueOf(split[i]);
			}
			DictUtil.levelTable.put("TC_XZQH", arr);
			if ("true".equals(simple_select))
			{
				Constans.simpleSelect = true;
			}
			else
			{
				Constans.simpleSelect = false;
			}
			System.out.println("centre_x:" + centre_x);
			System.out.println("centre_y:" + centre_y);
			System.out.println("xzqh_level:" + xzqh_level);
			System.out.println("simple_select:" + simple_select);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}