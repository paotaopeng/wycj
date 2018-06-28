package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 单位－企事业单位
 * 
 */
public class QSYDW extends BaseAttrs
{
	private static final long serialVersionUID = 1L;
	public QSYDW(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_QSYDW;
	}

	public QSYDW(BaseAttrs attrs, String zCZB, String fDDBR, String zCSJ,
			String zCDD)
	{
		this(attrs);
		ZCZB = zCZB;
		FDDBR = fDDBR;
		ZCSJ = zCSJ;
		ZCDD = zCDD;
	}

	/**
	 * 注册资本
	 */
	public String ZCZB;
	/**
	 * 法定代表人
	 */
	public String FDDBR;
	/**
	 * 注册时间
	 */
	public String ZCSJ;
	/**
	 * 注册地点
	 */
	public String ZCDD;
}