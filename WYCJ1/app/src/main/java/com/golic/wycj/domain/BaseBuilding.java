package com.golic.wycj.domain;

import java.io.Serializable;

public class BaseBuilding implements Serializable
{
	private static final long serialVersionUID = 1L;
	public MPHM mphm;
	public String preZlh;
	public String zlh;
	public String sufZlh = "1";
	public String xqm;

	public BaseBuilding()
	{
		super();
	}

	public BaseBuilding(MPHM mphm)
	{
		super();
		this.mphm = mphm;
	}

	public BaseBuilding(MPHM mphm, String preZlh, String zlh, String sufZlh,
			String xqm)
	{
		super();
		this.mphm = mphm;
		this.preZlh = preZlh;
		this.zlh = zlh;
		this.sufZlh = sufZlh;
		this.xqm = xqm;
	}

	@Override
	public String toString()
	{
		return "BaseBuilding [mphm=" + mphm + ", preZlh=" + preZlh + ", zlh="
				+ zlh + ", sufZlh=" + sufZlh + ", xqm=" + xqm + "]";
	}

	// @Override
	// public String toString()
	// {
	// return JSON.toJSONString(this);
	// }

	// public void fillMlxz(Context context)
	// {
	// String dz = "";
	// DictUtil util = DictUtil.getInstance(context);
	// String xzqhValue = util.getDictValue(DictUtil.TC_XZQH, bzdz.getSsxq());//
	// 省市区县(行政区划)
	// String jlxValue = util.getDictValue(DictUtil.DZ_JLX, bzdz.getJlx());//
	// 街路巷
	// String mpqzValue = util.getDictValue(DictUtil.DZ_MPQZ, bzdz.getMpqz());//
	// 门牌前缀
	// String mphzValue = util.getDictValue(DictUtil.DZ_MPHZ, bzdz.getMphz());//
	// 门牌后缀
	// String fhhzValue = util.getDictValue(DictUtil.DZ_FHHZ, bzdz.getFhhz());//
	// 副号后缀
	// dz += xzqhValue + jlxValue;
	// // 门牌号通过"前缀+门牌号+后缀"的规则拼接
	// String mphValue = "";
	// if (!TextUtils.isEmpty(bzdz.getMph()))
	// {
	// mphValue += mpqzValue + bzdz.getMph() + mphzValue;
	// }
	//
	// dz += mphValue;
	//
	// if (!TextUtils.isEmpty(bzdz.getFh()))
	// {
	// dz += bzdz.getFh() + fhhzValue;
	// }
	// bzdz.setMlxz(dz);
	// String zlqhValue = util.getDictValue(DictUtil.DZ_ZLQZ, preZlh);// 幢楼前缀
	// String zlhzValue = util.getDictValue(DictUtil.DZ_ZLHZ, sufZlh);// 幢楼后缀
	// if (!TextUtils.isEmpty(xqm))
	// {
	// dz += xqm;
	// }
	// if (!TextUtils.isEmpty(zlh))
	// {
	// dz += zlqhValue + zlh + zlhzValue;
	// }
	// setMlxz(dz);
	// }
	//
	// @Override
	// public boolean equals(Object o)
	// {
	// if (o != null && o instanceof BaseBuilding)
	// {
	// BaseBuilding b = (BaseBuilding) o;
	// if (bzdz.equals(b.getBzdz()))
	// {
	// if (zlh.equals(b.getZlh()))
	// {
	// return true;
	// }
	// }
	// }
	// return false;
	// }
}