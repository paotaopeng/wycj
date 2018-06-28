package com.golic.wycj;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.Drawable;

import com.esri.core.symbol.PictureMarkerSymbol;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.ExtraDz;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.Ywzy;

public class Source
{
	public static boolean update = false;
	public static HashMap<Type, Drawable> ggsjDrawables = new HashMap<Type, Drawable>();
	public static HashMap<String, Drawable> ywzyDrawables = new HashMap<String, Drawable>();
	public static HashMap<Type, PictureMarkerSymbol> symbols = new HashMap<Type, PictureMarkerSymbol>();
	public static ArrayList<Building> buildings = new ArrayList<Building>();
	public static HashMap<Type, ArrayList<BaseAttrs>> ggsjs = new HashMap<Type, ArrayList<BaseAttrs>>();
	public static Drawable bzdzDrawable;
	public static PictureMarkerSymbol bzdzSymbol;
	public static HashMap<String, ArrayList<Ywzy>> ywzys = new HashMap<String, ArrayList<Ywzy>>();
	public static String mpqzsx = "";
	/*
	 * 记录上次添加的地址
	 */
	public static MPHM mphm = new MPHM();
	/**
	 * 记录上次添加的额外地址
	 */
	public static ExtraDz extraDz;

	// 图层映射与图层信息描述的匹配关系
	public static HashMap<String, String> nameMatching = new HashMap<String, String>();
	// 图层映射和图层名称之间的关系
	public static HashMap<String, String> nameValues = new HashMap<String, String>();

	public static void updateBuilding(Building building)
	{
		int index = findBuildingIndex(building.baseBuilding.mphm.ID);
		buildings.set(index, building);
		update = true;
	}

	public static BaseAttrs findGgsj(Type type, String ID)
	{
		ArrayList<BaseAttrs> list = ggsjs.get(type);
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			BaseAttrs baseAttrs = list.get(i);
			if (baseAttrs.ID.equals(ID))
			{
				return baseAttrs;
			}
		}
		return null;
	}

	public static Building findBuilding(String ID)
	{
		for (Building building : buildings)
		{
			if (building.baseBuilding.mphm.ID.equals(ID))
			{
				return building;
			}
		}
		return null;
	}

	public static void removeGgsj(BaseAttrs attrs)
	{
		int index = findGgsjIndex(attrs);
		if (index >= 0)
		{
			ggsjs.get(attrs.type).remove(index);
			update = true;
		}
	}

	public static void removeBuilding(String ID)
	{
		int index = findBuildingIndex(ID);
		if (index >= 0)
		{
			buildings.remove(index);
			update = true;
		}
	}

	private static int findBuildingIndex(String ID)
	{
		int index = -1;
		int size = buildings.size();
		for (int i = 0; i < size; i++)
		{
			Building building = buildings.get(i);
			if (building.baseBuilding.mphm.ID.equals(ID))
			{
				return i;
			}
		}
		return index;
	}

	private static int findGgsjIndex(BaseAttrs attrs)
	{
		String iD = attrs.ID;
		Type type = attrs.type;
		int index = -1;
		ArrayList<BaseAttrs> list = ggsjs.get(type);
		int size = list.size();
		for (int i = 0; i < size; i++)
		{
			BaseAttrs baseAttrs = list.get(i);
			if (baseAttrs.ID.equals(iD))
			{
				return i;
			}
		}
		return index;
	}

	public static void updateGgsj(BaseAttrs attrs)
	{
		Type type = attrs.type;
		ArrayList<BaseAttrs> list = ggsjs.get(type);
		int index = findGgsjIndex(attrs);
		list.set(index, attrs);
		update = true;
	}

	public static void clear()
	{
		update = false;
		ggsjDrawables.clear();
		ywzyDrawables.clear();
		symbols.clear();
		if (buildings != null)
		{
			buildings.clear();
		}
		ggsjs.clear();
		// bzdzDrawable = null;
		// bzdzSymbol = null;
		ywzys.clear();
		extraDz = null;
		nameMatching.clear();
		nameValues.clear();
		System.gc();
	}
}