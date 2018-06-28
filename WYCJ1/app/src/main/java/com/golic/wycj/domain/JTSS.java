package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 交通-交通设施(无地址)
 * 
 */
public class JTSS extends BaseAttrs
{
	private static final long serialVersionUID = 1L;

	public JTSS(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_JTSS;
	}
}