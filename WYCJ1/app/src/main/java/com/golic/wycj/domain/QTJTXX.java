package com.golic.wycj.domain;

import com.golic.wycj.Type;

/**
 * 交通－其他交通信息
 * 
 */
public class QTJTXX extends BaseAttrs
{
	private static final long serialVersionUID = 1L;
	public QTJTXX(BaseAttrs attrs)
	{
		super(attrs);
		this.type = Type.GGSJ_QTJTXX;
		this.SZWZ=attrs.DZ;
	}
	/**
	 * 所在位置
	 */
	public String SZWZ;
}