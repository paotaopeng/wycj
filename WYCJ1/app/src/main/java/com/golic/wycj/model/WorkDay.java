package com.golic.wycj.model;

public class WorkDay implements Comparable<WorkDay>
{
	public String gxsj;
	public String gxr;
	public String type;
	public boolean select;

	@Override
	public String toString()
	{
		return "WorkDay [gxsj=" + gxsj + ", gxr=" + gxr + ", type=" + type
				+ ", select=" + select + "]";
	}

	@Override
	public int compareTo(WorkDay o)
	{
		int compare = Integer.parseInt(gxsj) - Integer.parseInt(o.gxsj);
		if (compare == 0)
		{
			compare = gxr.compareTo(o.gxr);
			if (compare == 0)
			{
				compare = type.compareTo(o.type);
			}
		}
		return compare;
	}
}