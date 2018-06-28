package com.golic.wycj;

public enum Type
{
	GGSJ_CS("场所", R.drawable.cs), GGSJ_JTSS("交通设施", R.drawable.jtss), GGSJ_GGSS(
			"公共设施", R.drawable.ggss), GGSJ_QSYDW("企事业单位", R.drawable.qsydw), GGSJ_QTDW(
			"其他单位", R.drawable.qtdw), GGSJ_QTJTXX("其他交通信息", R.drawable.qtjtxx), GGSJ_ZHJG(
			"驻华机构", R.drawable.zhjg);
	private String name;
	private int source;

	private Type(String name, int source)
	{
		this.name = name;
		this.source = source;
	}

	public String getName()
	{
		return name;
	}

	public int getSource()
	{
		return source;
	}
}