package com.golic.wycj.domain;

import java.io.Serializable;

public class Ywzy implements Serializable
{
	private static final long serialVersionUID = -4315827545282376649L;

	/**
	 * 类型名称（如网吧、旅馆、机构）
	 */
	private String name;

	// private boolean isSelect = false;
	/**
	 * 业务专用数据的主键
	 */
	private String id;

	/**
	 * 公共数据的主键，这里作为外键关联
	 */
	private String ggsj_id;

	// /**
	// * 行政区划
	// */
	// private String xzqh;
	/**
	 * 警务责任区
	 */
	private String jwzrq;
	/**
	 * 街路巷
	 */
	private String jlx;
	/**
	 * 门牌号
	 */
	private String mph;
	/**
	 * 门楼祥址（详细地址）
	 */
	private String mlxz;
	/**
	 * 名称
	 */
	private String mc;
	/**
	 * 坐标（经度）
	 */
	private String x;
	/**
	 * 坐标（纬度）
	 */
	private String y;
	/**
	 * 业务标识（'0'代表未采,'1'代表已采,'2'代表注销即无法采集）
	 */
	private int bs;

	public String getGgsj_id()
	{
		return ggsj_id;
	}

	public void setGgsj_id(String ggsj_id)
	{
		this.ggsj_id = ggsj_id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	// public String getXzqh()
	// {
	// return xzqh;
	// }
	//
	// public void setXzqh(String xzqh)
	// {
	// this.xzqh = xzqh;
	// }

	public String getJwzrq()
	{
		return jwzrq;
	}

	public void setJwzrq(String jwzrq)
	{
		this.jwzrq = jwzrq;
	}

	public String getJlx()
	{
		return jlx;
	}

	public void setJlx(String jlx)
	{
		this.jlx = jlx;
	}

	public String getMph()
	{
		return mph;
	}

	public void setMph(String mph)
	{
		this.mph = mph;
	}

	public String getMlxz()
	{
		return mlxz;
	}

	public void setMlxz(String mlxz)
	{
		this.mlxz = mlxz;
	}

	public String getMc()
	{
		return mc;
	}

	public void setMc(String mc)
	{
		this.mc = mc;
	}

	public String getX()
	{
		return x;
	}

	public void setX(String x)
	{
		this.x = x;
	}

	public String getY()
	{
		return y;
	}

	public void setY(String y)
	{
		this.y = y;
	}

	public int getBs()
	{
		return bs;
	}

	public void setBs(int bs)
	{
		this.bs = bs;
	}

	@Override
	public String toString()
	{
		return "Ywzy [name=" + name + ", id=" + id + ", ggsj_id=" + ggsj_id
				+ ", jwzrq=" + jwzrq + ", jlx=" + jlx + ", mph=" + mph
				+ ", mlxz=" + mlxz + ", mc=" + mc + ", x=" + x + ", y=" + y
				+ ", bs=" + bs + "]";
	}
}