package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 场所
 * 
 */
public class CS extends BaseAttrs
{
	private static final long serialVersionUID = 1L;

	public CS(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_CS;
	}

	public CS(BaseAttrs attrs, String lXR, String lXDH)
	{
		this(attrs);
		LXR = lXR;
		LXDH = lXDH;
	}

	/**
	 * 联系人
	 */
	public String LXR;
	/**
	 * 联系电话
	 */
	public String LXDH;

	@Override
	public String toString()
	{
		return "CS [LXR=" + LXR + ", LXDH=" + LXDH + ", ID=" + ID + ", X=" + X
				+ ", Y=" + Y + ", DJR=" + DJR + ", DJSJ=" + DJSJ + ", GXDWDM="
				+ GXDWDM + ", FLDM=" + FLDM + ", GBDM=" + GBDM + ", LX=" + LX
				+ ", XGR=" + XGR + ", GXSJ=" + GXSJ + ", YS=" + YS + ", BZ="
				+ BZ + ", MC=" + MC + ", ZMC=" + ZMC + ", bs=" + bs
				+ ", level=" + level + ", comment=" + comment + ", DZ=" + DZ
				+ ", mphm=" + mphm + ", extraDz=" + extraDz + ", type=" + type
				+ "]";
	}
}