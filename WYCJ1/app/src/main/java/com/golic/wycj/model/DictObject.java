package com.golic.wycj.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class DictObject implements Parcelable, Serializable,
		Comparable<DictObject>
{
	private static final long serialVersionUID = 1545427219987977528L;

	/** 代码 */
	protected final String DM;
	/** 名称 */
	protected final String MC;

	public DictObject(String dM, String mC)
	{
		if (dM == null || mC == null)
		{
			throw new NullPointerException("DM 或  MC 不能为null !!");
		}
		DM = dM;
		MC = mC;
	}

	public String getDM()
	{
		return DM;
	}

	public String getMC()
	{
		return MC;
	}

	@Override
	public String toString()
	{
		return "DictObject [DM=" + DM + "-->, MC=" + MC + "]";
	}

	public static final Creator<DictObject> CREATOR = new Creator<DictObject>()
	{
		@Override
		public DictObject createFromParcel(Parcel source)
		{
			return new DictObject(source.readString(), source.readString());
		}

		@Override
		public DictObject[] newArray(int size)
		{
			return new DictObject[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(DM);
		dest.writeString(MC);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (!(o instanceof DictObject))
			return false;

		boolean dmEquals = false;
		boolean mcEquals = false;

		if (DM == null)
			dmEquals = ((DictObject) o).getDM() == null ? true : false;
		else
			dmEquals = DM.equals(((DictObject) o).getDM());

		if (MC == null)
			mcEquals = ((DictObject) o).getMC() == null ? true : false;
		else
			mcEquals = MC.equals(((DictObject) o).getMC());

		return dmEquals && mcEquals;
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;

		hashCode += DM == null ? 0 : DM.hashCode();
		hashCode += MC == null ? 0 : MC.hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(DictObject another)
	{
		return DM.compareTo(another.getDM());
	}

}