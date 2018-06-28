package com.golic.wycj.domain;

import java.io.Serializable;

public class BzdzPhoto implements Serializable
{
	private static final long serialVersionUID = -2133267847735995249L;
	public String id;
	public String bzdzId;
	public String path;
	public String fileName;

	public BzdzPhoto()
	{
		super();
	}

	@Override
	public String toString()
	{
		return "BzdzPhoto [id=" + id + ", bzdzId=" + bzdzId + ", path=" + path
				+ ", fileName=" + fileName + "]";
	}

	public BzdzPhoto(String id, String bzdzId, String path, String fileName)
	{
		super();
		this.id = id;
		this.bzdzId = bzdzId;
		this.path = path;
		this.fileName = fileName;
	}
}