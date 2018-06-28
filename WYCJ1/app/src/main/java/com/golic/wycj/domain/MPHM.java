package com.golic.wycj.domain;

import java.io.Serializable;

public class MPHM implements Serializable
{
	private static final long serialVersionUID = 1L;

	public MPHM()
	{
		super();
	}

	/**
	 * 经度
	 */
	public String X;
	/**
	 * 纬度
	 */
	public String Y;
	public String DJR = "";
	/**
	 * 登记时间（格式为：yyyy年MM月dd日HH:mm:ss）
	 */
	public String DJSJ = "";
	/**
	 * 管辖单位代码（责任区）
	 */
	public String JWZRQ = "";

	public String XGR = "";
	/**
	 * 更新时间（格式为：yyyy-MM-dd）
	 */
	public String GXSJ = "";

	/**
	 * 备注
	 */
	public String BZ = "";

	public String ID;

	/**
	 * 所属区县12位(省市县字典,市政部门字典),数据库中存的是12位的实际需要的是6位的
	 */
	public String SSXQ = "";

	public String JWH = "";
	public String MLXZ = "";
	public String JLX = "";
	public String MPH = "";
	public String MPQZ = "";
	public String MPHZ = "1";
	public String FH = "";
	public String FHHZ = "";
	/**
	 * 门牌来源（1：悬挂门牌 2：推算 3：业主确认 4：证件确认 5：新建房屋(未分配) 6：无法采集）
	 */
	public String MPLY = "1";
	/**
	 * 地址类型（0：非住宅 1：私宅 2：小区楼栋 3：小区房间）
	 */
	public int DZLX = 0;

	// /**
	// * 采集标准地址时用
	// *
	// * @param attrs
	// */
	// public MPHM(String x, String y, String dJR, String dJSJ, String jWZRQ,
	// String xGR, String gXSJ, String bZ, String sSXQ, String jWH,
	// String mLXZ, String jLX, String mPH, String mPQZ, String mPHZ,
	// String fH, String fHHZ, String mPLY, int dZLX)
	// {
	// super();
	// X = x;
	// Y = y;
	// DJR = dJR;
	// DJSJ = dJSJ;
	// JWZRQ = jWZRQ;
	// XGR = xGR;
	// GXSJ = gXSJ;
	// BZ = bZ;
	// SSXQ = sSXQ;
	// JWH = jWH;
	// MLXZ = mLXZ;
	// JLX = jLX;
	// MPH = mPH;
	// MPQZ = mPQZ;
	// MPHZ = mPHZ;
	// FH = fH;
	// FHHZ = fHHZ;
	// MPLY = mPLY;
	// DZLX = dZLX;
	// }
	// private static final String[] bzdzTitle = new String[] { "ID",
	// "SSXQ",
	// "JLX", "MPQZ", "MPH", "MPHZ", "FH", "FHHZ", "ZLQZ", "ZLH", "ZLHZ",
	// "DYH", "SH", "SHHZ", "MLXZ", "JWZRQ", "JWH", "DZSX", "DJSJ",
	// "DMXZ", "GXSJ", "LCH", "LCHZ", "DJR", "XGR", "MPLY", "DZBH", "X",
	// "Y", "XQM", "BZ", "DZLX" };
	//
	// public MPHM(String id, String ssxq, String jlx, String mpqz, String mph,
	// String mphz, String fh, String fhhz, String zlqz, String zlh,
	// String zlhz, String dyh, String sh, String shhz, String mlxz,
	// String jwzrq, String jwh, String dzsx, String djsj, String dmxz,
	// String gxsj, String lch, String lchz, String djr, String xgr,
	// String mply, String dzbh, String x, String y, String xqm,
	// String bz, String dzlx)
	// {
	//
	// }

	/**
	 * 从数据查询出门牌号码时用
	 * 
	 */
	public MPHM(String x, String y, String dJR, String dJSJ, String jWZRQ,
			String xGR, String gXSJ, String bZ, String iD, String sSXQ,
			String jWH, String mLXZ, String jLX, String mPH, String mPQZ,
			String mPHZ, String fH, String fHHZ, String mPLY, int dZLX)
	{
		X = x;
		Y = y;
		DJR = dJR;
		DJSJ = dJSJ;
		JWZRQ = jWZRQ;
		XGR = xGR;
		GXSJ = gXSJ;
		BZ = bZ;
		ID = iD;
		SSXQ = sSXQ;
		JWH = jWH;
		MLXZ = mLXZ;
		JLX = jLX;
		MPH = mPH;
		MPQZ = mPQZ;
		MPHZ = mPHZ;
		FH = fH;
		FHHZ = fHHZ;
		MPLY = mPLY;
		DZLX = dZLX;
	}

	/**
	 * 采集时最初初始化用
	 */
	public MPHM(String x, String y, MPHM mphm)
	{
		super();
		X = x;
		Y = y;
		BZ = mphm.BZ;
		SSXQ = mphm.SSXQ;
		JLX = mphm.JLX;
		MPH = mphm.MPH;
		MPQZ = mphm.MPQZ;
		MPHZ = mphm.MPHZ;
		FH = mphm.FH;
		FHHZ = mphm.FHHZ;
		MPLY = mphm.MPLY;
		DZLX = mphm.DZLX;
	}

	@Override
	public String toString()
	{
		return "MPHM [X=" + X + ", Y=" + Y + ", DJR=" + DJR + ", DJSJ=" + DJSJ
				+ ", JWZRQ=" + JWZRQ + ", XGR=" + XGR + ", GXSJ=" + GXSJ
				+ ", BZ=" + BZ + ", ID=" + ID + ", SSXQ=" + SSXQ + ", JWH="
				+ JWH + ", MLXZ=" + MLXZ + ", JLX=" + JLX + ", MPH=" + MPH
				+ ", MPQZ=" + MPQZ + ", MPHZ=" + MPHZ + ", FH=" + FH
				+ ", FHHZ=" + FHHZ + ", MPLY=" + MPLY + ", DZLX=" + DZLX + "]";
	}

	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof MPHM)
		{
			MPHM b = (MPHM) o;
			if (MPQZ.equals(b.MPQZ) && SSXQ.equals(b.SSXQ) && JLX.equals(b.JLX)
					&& MPH.equals(b.MPH) && FH.equals(b.FH))
			{
				return true;
			}
		}
		return false;
	}
}