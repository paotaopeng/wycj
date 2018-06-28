package com.golic.wycj.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.golic.wycj.LoginUser;
import com.golic.wycj.Type;
import com.golic.wycj.model.MapLevelDictObject;

public class BaseAttrs implements Serializable, Comparable<BaseAttrs>
{
	private static final long serialVersionUID = 1L;
	public String ID;
	/**
	 * 经度
	 */
	public String X;
	/**
	 * 纬度
	 */
	public String Y;
	public String DJR;
	/**
	 * 登记时间（格式为：yyyy年MM月dd日HH:mm:ss）
	 */
	public String DJSJ;
	/**
	 * 管辖单位代码（责任区）
	 */
	public String GXDWDM;
	/**
	 * 分类代码（含字母）
	 */
	public String FLDM;
	/**
	 * 国标代码（不含字母）
	 */
	public String GBDM;
	/**
	 * 类型（即小类名称）
	 */
	public String LX;
	public String XGR;
	/**
	 * 更新时间（格式为：yyyy-MM-dd）
	 */
	public String GXSJ;
	/**
	 * 图层映射
	 */
	public String YS;
	/**
	 * 备注
	 */
	public String BZ;
	/**
	 * 名称
	 */
	public String MC;
	/**
	 * 子名称
	 */
	public String ZMC;
	/**
	 * 处理数据的标识（0是正常状态，1表示被关联，大于1表示是临时数据）
	 */
	public int bs;
	public int level;
	public String comment;
	public String DZ;
	/**
	 * 门牌号码地址
	 */
	public MPHM mphm;
	/**
	 * 附加地址（用于精确到门牌号无法表达的地址，如有楼层号或房间号）
	 */
	public ExtraDz extraDz;
	/**
	 * 用作显示图标和图标中文
	 */
	public Type type;

	/**
	 * 用于采集时最初的初始化构造
	 */
	public BaseAttrs(String x, String y, String fLDM, String gBDM, String lX,
			String yS, Type type, MPHM mphm, ExtraDz extraDz)
	{
		super();
		X = x;
		Y = y;
		FLDM = fLDM;
		GBDM = gBDM;
		LX = lX;
		YS = yS;
		this.type = type;
		this.mphm = new MPHM(x, y, mphm);
		this.extraDz = extraDz;
	}

	// 用于‘公共设施’和‘交通设施’的构造方法（无地址）
	public BaseAttrs(String iD, String x, String y, String dJR, String dJSJ,
			String gXDWDM, String fLDM, String gBDM, String lX, String xGR,
			String gXSJ, String yS, String bZ, String mC, String zMC, int bs,
			int level, String comment)
	{
		super();
		ID = iD;
		X = x;
		Y = y;
		DJR = dJR;
		DJSJ = dJSJ;
		GXDWDM = gXDWDM;
		FLDM = fLDM;
		GBDM = gBDM;
		LX = lX;
		XGR = xGR;
		GXSJ = gXSJ;
		YS = yS;
		BZ = bZ;
		MC = mC;
		ZMC = zMC;
		this.bs = bs;
		this.level = level;
		this.comment = comment;
	}

	// 用于除‘公共设施’、‘交通设施’以外的公共数据的构造方法
	public BaseAttrs(String iD, String x, String y, String dJR, String dJSJ,
			String gXDWDM, String fLDM, String gBDM, String lX, String xGR,
			String gXSJ, String yS, String bZ, String mC, String zMC, int bs,
			int level, String comment, String dZ, MPHM mphm, ExtraDz extraDz)
	{
		super();
		ID = iD;
		X = x;
		Y = y;
		DJR = dJR;
		DJSJ = dJSJ;
		GXDWDM = gXDWDM;
		FLDM = fLDM;
		GBDM = gBDM;
		LX = lX;
		XGR = xGR;
		GXSJ = gXSJ;
		YS = yS;
		BZ = bZ;
		MC = mC;
		ZMC = zMC;
		this.bs = bs;
		this.level = level;
		this.comment = comment;
		this.DZ = dZ;
		this.mphm = mphm;
		this.extraDz = extraDz;
	}

	/**
	 * 子类重载的构造方法
	 * 
	 * @param attrs
	 */
	public BaseAttrs(BaseAttrs attrs)
	{
		this.ID = attrs.ID;
		this.bs = attrs.bs;
		this.BZ = attrs.BZ;
		this.comment = attrs.comment;
		this.DJR = attrs.DJR;
		this.DJSJ = attrs.DJSJ;
		this.FLDM = attrs.FLDM;
		this.GBDM = attrs.GBDM;
		this.GXDWDM = attrs.GXDWDM;
		this.GXSJ = attrs.GXSJ;
		this.level = attrs.level;
		this.LX = attrs.LX;
		this.MC = attrs.MC;
		this.ZMC = attrs.ZMC;
		this.X = attrs.X;
		this.XGR = attrs.XGR;
		this.Y = attrs.Y;
		this.YS = attrs.YS;
		this.DZ = attrs.DZ;
		this.mphm = attrs.mphm;
		this.extraDz = attrs.extraDz;
	}

	public void changeType(MapLevelDictObject dictObj)
	{
		this.FLDM = dictObj.getDM();
		this.GBDM = FLDM.substring(1);
		this.YS = dictObj.getMapping();
	}

	public void fillDjxx()
	{
		Date date = new Date();
		this.ID = UUID.randomUUID().toString();
		this.DJR = LoginUser.xm;
		this.DJSJ = LoginUser.DJSJ_TIME.format(date);
		this.GXDWDM = LoginUser.zrq;
		this.XGR = LoginUser.xm;
		this.GXSJ = LoginUser.XGSJ_DATE.format(date);
		if (mphm != null)
		{
			this.DZ = mphm.MLXZ;
		}
	}

	public void fillXgxx()
	{
		this.XGR = LoginUser.xm;
		this.GXSJ = LoginUser.XGSJ_DATE.format(new Date());
	}

	@Override
	public String toString()
	{
		return "BaseAttrs [ID=" + ID + ", X=" + X + ", Y=" + Y + ", DJR=" + DJR
				+ ", DJSJ=" + DJSJ + ", GXDWDM=" + GXDWDM + ", FLDM=" + FLDM
				+ ", GBDM=" + GBDM + ", LX=" + LX + ", XGR=" + XGR + ", GXSJ="
				+ GXSJ + ", YS=" + YS + ", BZ=" + BZ + ", MC=" + MC + ", ZMC="
				+ ZMC + ", bs=" + bs + ", level=" + level + ", comment="
				+ comment + ", DZ=" + DZ + ", mphm=" + mphm + ", extraDz="
				+ extraDz + ", type=" + type + "]";
	}

	//按从大到小的顺序排序
	@Override
	public int compareTo(BaseAttrs another)
	{
		return another.DJSJ.compareTo(DJSJ);
	}
}