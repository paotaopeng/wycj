package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 单位－驻华机构
 * 
 */
public class ZHJG extends BaseAttrs
{
	private static final long serialVersionUID = 1L;

	public ZHJG(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_ZHJG;
	}

	public ZHJG(BaseAttrs attrs, String lXR, String lXDH, String gJ)
	{
		this(attrs);
		LXR = lXR;
		LXDH = lXDH;
		GJ = gJ;
	}

	/**
	 * 联系人
	 */
	public String LXR;
	/**
	 * 联系电话
	 */
	public String LXDH;
	/**
	 * 国籍
	 */
	public String GJ;
}