package com.golic.wycj.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationService;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.golic.wycj.LoginUser;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.model.MapLevelDictObject;

/**
 * 由于MapActivity中代码太多，将其中部分不重要的代码提取出来
 * 
 * @author luo
 * 
 */
public class MapActivityUtil
{
	public static void updateLocationServiceState(LocationService ls,
			final Activity context)
	{
		if (ls == null)
		{
			Toast.makeText(context, "定位服务开启失败", Toast.LENGTH_SHORT).show();
		}
		else if (ls.isStarted())
		{
			ls.stop();
			Toast.makeText(context, "定位服务关闭", Toast.LENGTH_SHORT).show();
			if (checkGps(context))
			{
				// 提示关闭gps设备
				NoticeUtil.showWarningDialog(context, "关闭GPS定位可以节省电量消耗！");
			}
		}
		else
		{
			ls.start();
			Toast.makeText(context, "定位服务开启", Toast.LENGTH_SHORT).show();
			if (!checkGps(context))
			{
				// 提示打开gps设备
				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("想要获取更准确的位置，请打开GPS")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int whichButton)
									{
										Intent myIntent = new Intent(
												Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										context.startActivity(myIntent);
									}
								}).setNegativeButton("忽略", null).show();
			}
		}
	}

	private static boolean checkGps(Context context)
	{
		LocationManager alm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return alm
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
	}

	public static ArrayList<MapLevelDictObject> filterLevelDicts(
			ArrayList<MapLevelDictObject> dicts, String filter)
	{
		ArrayList<MapLevelDictObject> result = new ArrayList<MapLevelDictObject>();
		for (MapLevelDictObject dic : dicts)
		{
			if (dic.getMC().contains(filter))
			{
				result.add(dic);
			}
		}
		return result;
	}

	public static void noticeAni(final GraphicsLayer aniGraphicsLayer)
	{
		if (aniGraphicsLayer != null && aniGraphicsLayer.isInitialized())
		{
			final ScheduledExecutorService timer = Executors
					.newSingleThreadScheduledExecutor();
			timer.scheduleAtFixedRate(new Runnable()
			{
				int time = 0;

				@Override
				public void run()
				{
					if (time < 4)
					{
						aniGraphicsLayer.setVisible(!aniGraphicsLayer
								.isVisible());
					}
					else
					{
						aniGraphicsLayer.setVisible(false);
						timer.shutdown();
					}
					time++;
				}
			}, 600, 600, TimeUnit.MILLISECONDS);
		}
	}

	public static void initGgsjSymbol(GraphicsLayer ggsjGraphicsLayer,
			GraphicsLayer textGraphicsLayer)
	{
		ggsjGraphicsLayer.removeAll();
		textGraphicsLayer.removeAll();
		for (Entry<Type, ArrayList<BaseAttrs>> entry : Source.ggsjs.entrySet())
		{
			Type type = entry.getKey();
			PictureMarkerSymbol symbol = Source.symbols.get(type);
			ArrayList<BaseAttrs> value = entry.getValue();
			// TODO 缩小内容
			List<BaseAttrs> subList = value;
			Collections.sort(subList);
			if (LoginUser.MARKER_NUM != 0
					&& value.size() > LoginUser.MARKER_NUM)
			{
				subList = value.subList(0, LoginUser.MARKER_NUM);
			}
			for (BaseAttrs attrs : subList)
			{
				Geometry geometry = MapUtil.getGeometryFromJson(attrs.X,
						attrs.Y);
				HashMap<String, Object> attribute = new HashMap<String, Object>();
				// if (attrs.bs > 0)
				// {
				// attribute.put("bs", attrs.bs);
				// }
				attribute.put("key", attrs.ID);
				// attribute.put("lx", attrs.LX);
				attribute.put("mc", attrs.MC);
				// attribute.put("dz", attrs.DZ);
				// attribute.put("djsj", attrs.DJSJ);
				attribute.put("type", attrs.type);
				int color = Color.BLACK;
				if ("网吧".equals(attrs.LX))
				{
					color = Color.RED;
				}
				else if (attrs.LX.startsWith("中式"))
				{
					color = Color.GREEN;
				}
				else if (attrs.LX.startsWith("宾馆")
						|| attrs.LX.startsWith("一般旅馆"))
				{
					color = Color.BLUE;
				}
				Graphic textGraphic = new Graphic(geometry, new TextSymbol(16,
						attrs.MC, color));
				// TODO 这一行有可能走不过去?
				int uid = textGraphicsLayer.addGraphic(textGraphic);
				attribute.put("uid", uid);
				Graphic graphic = new Graphic(geometry, symbol, attribute, null);
				ggsjGraphicsLayer.addGraphic(graphic);
			}
			// 原来的方式
			// for (BaseAttrs attrs : value)
			// {
			// Geometry geometry = MapUtil.getGeometryFromJson(attrs.X,
			// attrs.Y);
			// HashMap<String, Object> attribute = new HashMap<String,
			// Object>();
			// // if (attrs.bs > 0)
			// // {
			// // attribute.put("bs", attrs.bs);
			// // }
			// attribute.put("key", attrs.ID);
			// // attribute.put("lx", attrs.LX);
			// attribute.put("mc", attrs.MC);
			// // attribute.put("dz", attrs.DZ);
			// // attribute.put("djsj", attrs.DJSJ);
			// attribute.put("type", attrs.type);
			// int color = Color.BLACK;
			// if ("网吧".equals(attrs.LX))
			// {
			// color = Color.RED;
			// }
			// else if (attrs.LX.startsWith("中式"))
			// {
			// color = Color.GREEN;
			// }
			// else if (attrs.LX.startsWith("宾馆")
			// || attrs.LX.startsWith("一般旅馆"))
			// {
			// color = Color.BLUE;
			// }
			// Graphic textGraphic = new Graphic(geometry, new TextSymbol(16,
			// attrs.MC, color));
			// // TODO 这一行有可能走不过去?
			// int uid = textGraphicsLayer.addGraphic(textGraphic);
			// attribute.put("uid", uid);
			// Graphic graphic = new Graphic(geometry, symbol, attribute, null);
			// ggsjGraphicsLayer.addGraphic(graphic);
			// }
		}
	}

	public static void initBzdzSymbol(GraphicsLayer bzdzGraphicsLayer)
	{
		bzdzGraphicsLayer.removeAll();
		// 原来的方式
		// for (Building building : Source.buildings)
		// {
		// MPHM mphm = building.baseBuilding.mphm;
		// Geometry geometry = MapUtil.getGeometryFromJson(mphm.X, mphm.Y);
		// HashMap<String, Object> attribute = new HashMap<String, Object>();
		// attribute.put("key", building.baseBuilding.mphm.ID);
		// attribute.put("mlxz", building.baseBuilding.mphm.MLXZ);
		// if (building.baseBuilding.mphm.DZLX == 2)
		// {
		// attribute.put("dys", building.units.length);
		// }
		// attribute.put("djr", building.baseBuilding.mphm.DJR);
		// attribute.put("djsj", building.baseBuilding.mphm.DJSJ);
		// Graphic graphic = new Graphic(geometry, Source.bzdzSymbol,
		// attribute, null);
		// bzdzGraphicsLayer.addGraphic(graphic);
		// }
		// TODO 缩小范围
		Collections.sort(Source.buildings);
		List<Building> subList = Source.buildings;
		if (LoginUser.MARKER_NUM != 0 && subList.size() > LoginUser.MARKER_NUM)
		{
			subList = Source.buildings.subList(0, LoginUser.MARKER_NUM);
		}
		for (Building building : subList)
		{
			MPHM mphm = building.baseBuilding.mphm;
			Geometry geometry = MapUtil.getGeometryFromJson(mphm.X, mphm.Y);
			HashMap<String, Object> attribute = new HashMap<String, Object>();
			attribute.put("key", building.baseBuilding.mphm.ID);
			attribute.put("mlxz", building.baseBuilding.mphm.MLXZ);
			if (building.baseBuilding.mphm.DZLX == 2)
			{
//				attribute.put("dys", 0);
				attribute.put("dys", building.units.length);
			}
			attribute.put("djr", building.baseBuilding.mphm.DJR);
			attribute.put("djsj", building.baseBuilding.mphm.DJSJ);
			Graphic graphic = new Graphic(geometry, Source.bzdzSymbol,
					attribute, null);
			bzdzGraphicsLayer.addGraphic(graphic);
		}
	}

	public static String[] getGraphicsInfo(ArrayList<Graphic> graphics)
	{
		int size = graphics.size();
		String[] values = new String[graphics.size()];
		for (int i = 0; i < size; i++)
		{
			Graphic graphic = graphics.get(i);
			if (graphic == null)
			{
				continue;
			}
			Object mcAttribute = graphic.getAttributeValue("mc");
			if (mcAttribute == null)
			{
				values[i] = graphic.getAttributeValue("mlxz").toString();
			}
			else
			{
				values[i] = mcAttribute.toString();
			}
		}
		return values;
	}

	public static ArrayList<Graphic> clickAtGraphic(float screenX,
			float screenY, GraphicsLayer bzdzGraphicsLayer,
			GraphicsLayer ggsjGraphicsLayer, int OFF_SET)
	{
		ArrayList<Graphic> list = null;
		int[] bzdzIds = bzdzGraphicsLayer.getGraphicIDs(screenX, screenY,
				OFF_SET, 3);
		if (bzdzIds != null && bzdzIds.length > 0)
		{
			list = new ArrayList<Graphic>();
			for (int uid : bzdzIds)
			{
				Graphic graphic = bzdzGraphicsLayer.getGraphic(uid);
				if (graphic != null)
				{
					list.add(graphic);
				}
			}
		}
		int[] ggsjIds = ggsjGraphicsLayer.getGraphicIDs(screenX, screenY,
				OFF_SET, 3);
		if (ggsjIds != null && ggsjIds.length > 0)
		{
			if (list == null)
			{
				list = new ArrayList<Graphic>();
			}
			for (int uid : ggsjIds)
			{
				Graphic graphic = ggsjGraphicsLayer.getGraphic(uid);
				if (graphic != null)
				{
					list.add(graphic);
				}
			}
		}
		return list;
	}
}