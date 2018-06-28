package com.golic.wycj.util;

import android.app.Activity;
import android.content.Intent;

import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.activity.CsActivity;
import com.golic.wycj.activity.FormBaseActivity;
import com.golic.wycj.activity.GgssActivity;
import com.golic.wycj.activity.JtssActivity;
import com.golic.wycj.activity.QsydwActivity;
import com.golic.wycj.activity.QtdwActivity;
import com.golic.wycj.activity.QtjtxxActivity;
import com.golic.wycj.activity.ZhjgActivity;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.CS;
import com.golic.wycj.domain.GGSS;
import com.golic.wycj.domain.JTSS;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.QSYDW;
import com.golic.wycj.domain.QTDW;
import com.golic.wycj.domain.QTJTXX;
import com.golic.wycj.domain.ZHJG;
import com.golic.wycj.model.MapLevelDictObject;

public class TypeUtil
{
	// public static String[] getTitles(Type type)
	// {
	// ArrayList<String> list = new ArrayList<String>();
	// for (Type t : Type.values())
	// {
	// if (t != type)
	// {
	// list.add(t.getName());
	// }
	// }
	// return list.toArray(new String[list.size()]);
	// }

	public static BaseAttrs getBean(BaseAttrs attrs)
	{
		Type type = attrs.type;
		BaseAttrs baseAttrs = null;
		switch (type)
		{
		case GGSJ_CS:
			baseAttrs = new CS(attrs);
			break;
		case GGSJ_QSYDW:
			baseAttrs = new QSYDW(attrs);
			break;
		case GGSJ_QTDW:
			baseAttrs = new QTDW(attrs);
			break;
		case GGSJ_QTJTXX:
			baseAttrs = new QTJTXX(attrs);
			break;
		case GGSJ_ZHJG:
			baseAttrs = new ZHJG(attrs);
			break;
		case GGSJ_GGSS:
			baseAttrs = new GGSS(attrs);
			break;
		case GGSJ_JTSS:
			baseAttrs = new JTSS(attrs);
			break;
		}
		return baseAttrs;
	}

	public static Class<?> getTargetClass(Type type)
	{
		Class<?> clazz = null;
		switch (type)
		{
		case GGSJ_CS:
			clazz = CsActivity.class;
			break;
		case GGSJ_QSYDW:
			clazz = QsydwActivity.class;
			break;
		case GGSJ_QTDW:
			clazz = QtdwActivity.class;
			break;
		case GGSJ_QTJTXX:
			clazz = QtjtxxActivity.class;
			break;
		case GGSJ_ZHJG:
			clazz = ZhjgActivity.class;
			break;
		case GGSJ_GGSS:
			clazz = GgssActivity.class;
			break;
		case GGSJ_JTSS:
			clazz = JtssActivity.class;
			break;
		}
		return clazz;
	}

	public static void changeType(MapLevelDictObject mapLevelDictObject,
			BaseAttrs attrs, Activity context, int request)
	{
		String ywbm = mapLevelDictObject.getYwbm();
		if (ywbm.equals(attrs.type.toString()))
		{
			String lx = mapLevelDictObject.getMC();
			if (lx.equals(attrs.LX))
			{
				NoticeUtil.showWarningDialog(context, "已经是" + lx + "无需切换");
			}
			else
			{
				String dm = mapLevelDictObject.getDM();
				String ys = mapLevelDictObject.getMapping();
				attrs.FLDM = dm;
				attrs.GBDM = dm.substring(1);
				attrs.LX = lx;
				attrs.YS = ys;
				Source.updateGgsj(attrs);
				new GgsjDaoImpl(context).updateGgsj(attrs);
			}
			return;
		}
		// 到此为止所有未发生更改的类型切换已结束，以下全是发生类型变化的更改
		// 如果是修改则需要先删除原来的数据
		if (attrs.ID != null)
		{
			int delete = new GgsjDaoImpl(context).delete(attrs);
			if (delete > 0)
			{
				Source.removeGgsj(attrs);
				attrs.ID = null;
				if (attrs.mphm != null)
				{
					attrs.mphm.ID = null;
				}
			}
			else
			{
				NoticeUtil.showWarningDialog(context, "切换类型失败");
				return;
			}
		}
		if (attrs.mphm == null)
		{
			// 说明是从无地址的公共数据切换到有地址的公共数据
			if ("GGSJ_MPHM".equals(ywbm))
			{
				// 从无地址数据切换到住宅
				Intent intent = new Intent(context, FormBaseActivity.class);
				Building building = new Building(new BaseBuilding(new MPHM(
						attrs.X, attrs.Y, Source.mphm)));
				intent.putExtra("building", building);
				context.startActivityForResult(intent, request);
			}
			else
			{
				// 从无地址公共数据切换到有地址公共数据
				Type type = Type.valueOf(ywbm);
				String fldm = mapLevelDictObject.getDM();
				String gbdm = fldm.substring(1);
				BaseAttrs baseAttrs = new BaseAttrs(attrs.X, attrs.Y, fldm,
						gbdm, mapLevelDictObject.getMC(),
						mapLevelDictObject.getMapping(), type, Source.mphm,
						Source.extraDz);
				switch (type)
				{
				case GGSJ_GGSS:
					// 交通设施切换到--公共设施
					Intent ggssIntent = new Intent(context, GgssActivity.class);
					ggssIntent.putExtra("attrs", new GGSS(baseAttrs));
					context.startActivityForResult(ggssIntent, request);
					break;
				case GGSJ_JTSS:
					// 公共设施切换到--交通设施
					Intent jtssIntent = new Intent(context, JtssActivity.class);
					jtssIntent.putExtra("attrs", new JTSS(baseAttrs));
					context.startActivityForResult(jtssIntent, request);
					break;
				default:
					// 公共设施或者是交通设施切换到--其它有地址类别
					Intent intent = new Intent(context, FormBaseActivity.class);
					intent.putExtra("attrs", baseAttrs);
					context.startActivityForResult(intent, request);
				}
			}
			return;
		}
		// 以下的都说明最开始的公共数据是一个有地址的
		if ("GGSJ_MPHM".equals(ywbm))
		{
			Intent intent = new Intent(context, FormBaseActivity.class);
			Building building = new Building(new BaseBuilding(attrs.mphm));
			intent.putExtra("building", building);
			context.startActivityForResult(intent, request);
			return;
		}
		Type tempType = Type.valueOf(ywbm);
		String dm = mapLevelDictObject.getDM();
		String lx = mapLevelDictObject.getMC();
		String ys = mapLevelDictObject.getMapping();
		attrs.FLDM = dm;
		attrs.GBDM = dm.substring(1);
		attrs.LX = lx;
		attrs.YS = ys;
		if (tempType != null)
		{
			BaseAttrs baseAttrs = null;
			Class<?> clazz = null;
			Intent intent;
			switch (tempType)
			{
			case GGSJ_CS:
				baseAttrs = new CS(attrs);
				clazz = CsActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_QSYDW:
				baseAttrs = new QSYDW(attrs);
				clazz = QsydwActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_QTDW:
				baseAttrs = new QTDW(attrs);
				clazz = QtdwActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_QTJTXX:
				baseAttrs = new QTJTXX(attrs);
				clazz = QtjtxxActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_ZHJG:
				baseAttrs = new ZHJG(attrs);
				clazz = ZhjgActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_GGSS:
				baseAttrs = new GGSS(attrs);
				// baseAttrs.mphm = null;
				// baseAttrs.extraDz = null;
				clazz = GgssActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			case GGSJ_JTSS:
				baseAttrs = new JTSS(attrs);
				// baseAttrs.mphm = null;
				// baseAttrs.extraDz = null;
				clazz = JtssActivity.class;
				intent = new Intent(context, clazz);
				intent.putExtra("attrs", baseAttrs);
				context.startActivityForResult(intent, request);
				break;
			}
		}
	}

	public static void go2GgsjActivity(BaseAttrs attrs, Activity context)
	{
		Type type = attrs.type;
		// BaseAttrs baseAttrs = null;
		Intent intent;
		switch (type)
		{
		case GGSJ_CS:
			// baseAttrs = new CS(attrs);
			intent = new Intent(context, CsActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_QSYDW:
			// baseAttrs = new QSYDW(attrs);
			intent = new Intent(context, QsydwActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_QTDW:
			// baseAttrs = new QTDW(attrs);
			intent = new Intent(context, QtdwActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_QTJTXX:
			// baseAttrs = new QTJTXX(attrs);
			intent = new Intent(context, QtjtxxActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_ZHJG:
			// baseAttrs = new ZHJG(attrs);
			intent = new Intent(context, ZhjgActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_GGSS:
			// baseAttrs = new GGSS(attrs);
			// baseAttrs.mphm = null;
			// baseAttrs.extraDz = null;
			intent = new Intent(context, GgssActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		case GGSJ_JTSS:
			// baseAttrs = new JTSS(attrs);
			// baseAttrs.mphm = null;
			// baseAttrs.extraDz = null;
			intent = new Intent(context, JtssActivity.class);
			intent.putExtra("attrs", attrs);
			context.startActivity(intent);
			break;
		}
	}

	// public static void go2GgsjActivityForResult(BaseAttrs attrs,
	// Activity context, int requestCode)
	// {
	// Type type = attrs.type;
	// // BaseAttrs baseAttrs = null;
	// Intent intent;
	// switch (type)
	// {
	// case GGSJ_CS:
	// // baseAttrs = new CS(attrs);
	// intent = new Intent(context, CsActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_QSYDW:
	// // baseAttrs = new QSYDW(attrs);
	// intent = new Intent(context, QsydwActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_QTDW:
	// // baseAttrs = new QTDW(attrs);
	// intent = new Intent(context, QtdwActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_QTJTXX:
	// // baseAttrs = new QTJTXX(attrs);
	// intent = new Intent(context, QtjtxxActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_ZHJG:
	// // baseAttrs = new ZHJG(attrs);
	// intent = new Intent(context, ZhjgActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_GGSS:
	// // baseAttrs = new GGSS(attrs);
	// // baseAttrs.mphm = null;
	// // baseAttrs.extraDz = null;
	// intent = new Intent(context, GgssActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// case GGSJ_JTSS:
	// // baseAttrs = new JTSS(attrs);
	// // baseAttrs.mphm = null;
	// // baseAttrs.extraDz = null;
	// intent = new Intent(context, JtssActivity.class);
	// intent.putExtra("attrs", attrs);
	// context.startActivityForResult(intent, requestCode);
	// break;
	// }
	// }
}