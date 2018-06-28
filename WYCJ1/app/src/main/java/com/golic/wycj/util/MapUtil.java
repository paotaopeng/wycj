package com.golic.wycj.util;

import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import com.alibaba.fastjson.JSON;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.map.Graphic;
import com.golic.wycj.model.MapPoint;
import com.golic.wycj.model.MapPolygon;
import com.golic.wycj.model.SpatialReference;

public class MapUtil
{
	public static SpatialReference spatialReference = new SpatialReference(4326);

	public static Geometry getGeometryFromJson(String json)
	{
		Geometry geometry = null;
		try
		{
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createJsonParser(json);
			MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(parser);
			geometry = mapGeometry.getGeometry();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return geometry;
	}

	public static String getMapPointJson(double mapLongitude, double mapLatitude)
	{
		return JSON.toJSONString(new MapPoint(mapLongitude, mapLatitude,
				spatialReference));
	}

	public static String getMapPointJson(String mapLongitudeStr,
			String mapLatitudeStr)
	{
		if (mapLongitudeStr == null || "".equals(mapLongitudeStr))
		{
			return null;
		}
		if (mapLatitudeStr == null || "".equals(mapLatitudeStr))
		{
			return null;
		}
		double mapLongitude = Double.parseDouble(mapLongitudeStr);
		double mapLatitude = Double.parseDouble(mapLatitudeStr);
		return JSON.toJSONString(new MapPoint(mapLongitude, mapLatitude,
				spatialReference));
	}

	public static Geometry getGeometryFromJson(double mapLongitude,
			double mapLatitude)
	{
		return getGeometryFromJson(getMapPointJson(mapLongitude, mapLatitude));
	}

	public static Geometry getGeometryFromJson(String mapLongitude,
			String mapLatitude)
	{
		return getGeometryFromJson(getMapPointJson(mapLongitude, mapLatitude));
	}

	public static String getJsonFromGraphic(MapView map, Graphic graphic)
	{
		Geometry geometry = graphic.getGeometry();
		return GeometryEngine.geometryToJson(map.getSpatialReference(),
				geometry);
	}

	public static boolean contains(MapView map, String pointQueue,
			Graphic graphic)
	{
		Geometry geometry1 = getGeometryFromJson(formatPointQueue2Polygon(pointQueue));
		Geometry geometry2 = graphic.getGeometry();
		if (geometry1 == null || geometry2 == null)
		{
			return false;
		}

		return GeometryEngine.contains(geometry1, geometry2,
				map.getSpatialReference());
	}

	@SuppressWarnings("unchecked")
	public static String formatPointQueue2Polygon(String pointQueue)
	{
		ArrayList<ArrayList<double[]>> rings = JSON.parseObject(pointQueue,
				ArrayList.class);
		MapPolygon mapPolygon = new MapPolygon(rings, spatialReference);
		return mapPolygon.toString();
	}
}