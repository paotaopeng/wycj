package com.golic.wycj.model.base;

import java.util.ArrayList;

import com.golic.wycj.util.DictUtil;

public interface LevelRelation
{
	String getParentDm();
	ArrayList<LevelRelation> getChildren(DictUtil util);
}