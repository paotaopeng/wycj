package com.golic.wycj.domain;

import java.io.Serializable;

public class Room implements Serializable
{
	private static final long serialVersionUID = 7114434181201767075L;
	public String sh;
	public String sufSh;
	public String zzbh;

	@Override
	public String toString()
	{
		return "Room [" + sh + "(" + sufSh + ")+zzbh： " + zzbh + "]";
	}
}