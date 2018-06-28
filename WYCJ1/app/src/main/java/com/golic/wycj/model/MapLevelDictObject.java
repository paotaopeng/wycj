package com.golic.wycj.model;

import java.util.ArrayList;

import com.golic.wycj.model.base.LevelRelation;
import com.golic.wycj.util.DictUtil;

public class MapLevelDictObject extends DictObject implements LevelRelation
{
	private static final long serialVersionUID = -2822409215056550503L;
	private String mapping;
	private String parentDm;
	private String ywbm;

	public MapLevelDictObject(String dM, String mC, String mapping,
			String parentDm, String ywbm)
	{
		super(dM, mC);
		this.mapping = mapping;
		this.parentDm = parentDm;
		this.ywbm = ywbm;
	}

	public MapLevelDictObject(String dM, String mC, String mapping,
			String parentDm)
	{
		super(dM, mC);
		this.mapping = mapping;
		this.parentDm = parentDm;
	}

	public String getYwbm()
	{
		return ywbm;
	}

	@Override
	public String getParentDm()
	{
		return parentDm;
	}

	public String getMapping()
	{
		return mapping;
	}

	public void setMapping(String mapping)
	{
		this.mapping = mapping;
	}

	@Override
	public ArrayList<LevelRelation> getChildren(DictUtil util)
	{
		util.getMapLevelChildren(this, DictUtil.GGSJ_DICT);
		return null;
	}

	@Override
	public String toString()
	{
		return "MapLevelDictObject [mapping=" + mapping + ", parentDm="
				+ parentDm + ", ywbm=" + ywbm + ", DM=" + DM + ", MC=" + MC
				+ "]";
	}
}