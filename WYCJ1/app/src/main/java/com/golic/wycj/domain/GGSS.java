package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 公共设施(无地址)
 * 
 */
public class GGSS extends BaseAttrs
{
	private static final long serialVersionUID = 1L;

	public GGSS(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_GGSS;
	}

	public GGSS(BaseAttrs attrs, String lXR, String lXDH)
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
}