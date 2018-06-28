package com.golic.wycj.domain;

import java.io.Serializable;

import android.text.TextUtils;

public class ExtraDz implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String xqm;
	public String preZlh = "";
	public String zlh;
	public String sufZlh = "1";
	public String dyh;
	public String lch;
	public String sufLch = "9";
	public String sh;
	public String sufSh = "1";

	public ExtraDz()
	{
		super();
	}

	public ExtraDz(String xqm, String preZlh, String zlh, String sufZlh,
			String dyh, String lch, String sufLch, String sh, String sufSh)
	{
		super();
		this.xqm = xqm;
		this.preZlh = preZlh;
		this.zlh = zlh;
		this.sufZlh = sufZlh;
		this.dyh = dyh;
		this.lch = lch;
		this.sufLch = sufLch;
		this.sh = sh;
		this.sufSh = sufSh;
	}

	public boolean isEmpty()
	{
		return TextUtils.isEmpty(xqm) && TextUtils.isEmpty(zlh)
				&& TextUtils.isEmpty(dyh) && TextUtils.isEmpty(lch)
				&& TextUtils.isEmpty(sh);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ExtraDz [xqm=" + xqm + ", preZlh=" + preZlh + ", zlh=" + zlh
				+ ", sufZlh=" + sufZlh + ", dyh=" + dyh + ", lch=" + lch
				+ ", sufLch=" + sufLch + ", sh=" + sh + ", sufSh=" + sufSh
				+ "]";
	}

	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof ExtraDz)
		{
			ExtraDz e = (ExtraDz) o;
			boolean result = true;
			if (TextUtils.isEmpty(zlh))
			{
				if (!TextUtils.isEmpty(e.zlh))
				{
					return false;
				}
			}
			else
			{
				result = result && zlh.equals(e.zlh);
			}

			if (result)
			{
				if (TextUtils.isEmpty(dyh))
				{
					if (!TextUtils.isEmpty(e.dyh))
					{
						return false;
					}
				}
				else
				{
					result = result && dyh.equals(e.dyh);
				}
			}

			if (result)
			{
				if (TextUtils.isEmpty(lch))
				{
					if (!TextUtils.isEmpty(e.lch))
					{
						return false;
					}
				}
				else
				{
					result = result && lch.equals(e.lch);
				}
			}

			if (result)
			{
				if (TextUtils.isEmpty(sh))
				{
					if (!TextUtils.isEmpty(e.sh))
					{
						return false;
					}
				}
				else
				{
					result = result && sh.equals(e.sh);
				}
			}
			return result;
		}
		return false;
	}
}