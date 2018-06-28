package com.golic.wycj.model;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

public class MapPolygon
{
	public ArrayList<ArrayList<double[]>> rings;
	public SpatialReference spatialReference;

	public MapPolygon(ArrayList<ArrayList<double[]>> rings,
			SpatialReference spatialReference)
	{
		super();
		this.rings = rings;
		this.spatialReference = spatialReference;
	}

	@Override
	public String toString()
	{
		return JSON.toJSONString(this);
	}
}
