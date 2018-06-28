package com.golic.wycj.model;

import com.alibaba.fastjson.JSON;

public class MapPoint
{
	public double x;
	public double y;
	public SpatialReference spatialReference;

	public MapPoint(double x, double y, SpatialReference spatialReference)
	{
		super();
		this.x = x;
		this.y = y;
		this.spatialReference = spatialReference;
	}

	public MapPoint()
	{
		super();
	}

	@Override
	public String toString()
	{
		return JSON.toJSONString(this);
	}
}
