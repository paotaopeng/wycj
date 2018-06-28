package com.golic.wycj.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.golic.wycj.Constans;
import com.golic.wycj.LoginUser;
import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.Type;
import com.golic.wycj.dao.BzdzDaoImpl;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.PointDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.BaseBuilding;
import com.golic.wycj.domain.Building;
import com.golic.wycj.domain.GGSS;
import com.golic.wycj.domain.JTSS;
import com.golic.wycj.domain.MPHM;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.MapLevelDictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.ui.adapter.DictListAdapterSingleChoose;
import com.golic.wycj.util.DictDialogUtil;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.MapActivityUtil;
import com.golic.wycj.util.MapUtil;
import com.golic.wycj.util.NoticeUtil;
import com.golic.wycj.util.TypeUtil;

public class MapActivity extends BaseActivity implements OnClickListener
{
	private MapView map;
	// **************配置参数************************
	// 默认显示的分辨率
	private static final double defaultRes = 0.000818;
	final static double SEARCH_RADIUS = 5;
	private static final int OFF_SET = 20;

	// **************控件****************************
	// 上层主体按钮
	private Button bt_new;
	private Button bt_add_jlx;
	private Button bt_work_search;
	private Button bt_layer_control;
	private Button bt_data_export;
	private Button bt_data_analyse;

	// 中层辅助按钮
	private View ll_mid_control_add;
	private View ll_mid_control_update;
	private Button bt_add;
	private Button bt_clear;
	private Button bt_update;
	private Button bt_cancle;
	private View ll_notice;
	private View im_type;
	// 底部辅助按钮
	private Button bt_location;
	private Button bt_help;
	// 地图漫游按钮
	private Button bt_zoomout;
	private Button bt_zoomfull;
	private Button bt_zoomin;

	// *******************辅助变量(不变动)****************************
	private GraphicsLayer bzdzGraphicsLayer;
	private GraphicsLayer ggsjGraphicsLayer;
	private GraphicsLayer textGraphicsLayer;
	private GraphicsLayer helpGraphicsLayer;
	private GraphicsLayer aniGraphicsLayer;
	private Geometry zrqGeometry;
	private LocationService ls;

	// *********************临时变量(经常变动)*******************************
	private GraphicsLayer tempGraphicsLayer;
	public static MapLevelDictObject mapDict;
	private static boolean[] check = new boolean[] { false, false, false };
	private boolean isMarking = false;
	private boolean moving = false;
	// 存储弹出气泡的地图元素
	private Graphic tempGraphic;
	public Point mapPoint;

	// ***********************全局唯一变量********************************
	private static PictureMarkerSymbol tempSymbol;
	private Callout callout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		initMap();
	}

	@Override
	public void finish()
	{
		super.finish();
		sendBroadcast(new Intent(Constans.FINISH_ACTION));
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		if (Source.update)
		{
			Source.update = false;
			tempGraphicsLayer.removeAll();
			bt_add.setEnabled(false);
			bt_clear.setEnabled(false);

			updateData(new CallBack()
			{
				@Override
				public void call()
				{
					MapActivityUtil.initBzdzSymbol(bzdzGraphicsLayer);
					MapActivityUtil.initGgsjSymbol(ggsjGraphicsLayer,
							textGraphicsLayer);
				}

				@Override
				public void post()
				{
					Point point = (Point) intent.getSerializableExtra("point");
					showPopwAtPoint(point);
				}
			});
		}
		else
		{
			Point point = (Point) intent.getSerializableExtra("point");
			showPopwAtPoint(point);
		}
		// System.out.println("onnewIntent!!!");
	}

	private void showPopwAtPoint(Point point)
	{
		clear();
		map.centerAt(point, false);
		int[] ggsjGraphicIDs = ggsjGraphicsLayer.getGraphicIDs();
		if (ggsjGraphicIDs != null && ggsjGraphicIDs.length > 0)
		{
			for (int id : ggsjGraphicIDs)
			{
				Graphic graphic = ggsjGraphicsLayer.getGraphic(id);
				Geometry geometry = graphic.getGeometry();
				if (geometry != null && geometry instanceof Point)
				{
					Point p = (Point) geometry;
					if ((point.getX() == p.getX())
							&& (point.getY() == p.getY()))
					{
						tempGraphic = graphic;
						ggsjGraphicsLayer.setVisible(true);
						check[1] = true;
						if (check[2])
						{
							textGraphicsLayer.setVisible(true);
						}
						showGgsjPopw();
						return;
					}
				}
			}
		}
		int[] bzdzGraphicIDs = bzdzGraphicsLayer.getGraphicIDs();
		if (bzdzGraphicIDs != null && bzdzGraphicIDs.length > 0)
		{
			for (int id : bzdzGraphicIDs)
			{
				Graphic graphic = bzdzGraphicsLayer.getGraphic(id);
				Geometry geometry = graphic.getGeometry();
				if (geometry != null && geometry instanceof Point)
				{
					Point p = (Point) geometry;
					if ((point.getX() == p.getX())
							&& (point.getY() == p.getY()))
					{
						tempGraphic = graphic;
						bzdzGraphicsLayer.setVisible(true);
						check[0] = true;
						showBzdzPopw();
						return;
					}
				}
			}
		}
		Toast.makeText(getApplicationContext(), "图标定位失败！", Toast.LENGTH_LONG)
				.show();
	}

	private void initMap()
	{
		// map = (MapView) findViewById(R.id.map);
		// MyTouchListener myListener = new MyTouchListener(
		// getApplicationContext(), map);
		// map.setOnTouchListener(myListener);
		// ArcGISLocalTiledLayer local = null;
		// try
		// {
		// local = new ArcGISLocalTiledLayer("file://" + Constans.MAP_SOURCE
		// + "/Layers");
		// }
		// catch (Exception e)
		// {
		// NoticeUtil.showErrorDialog(this, "地图资源文件损坏！");
		// return;
		// }
		// if (local != null)
		// {
		// map.addLayer(local);
		// }
		callout = map.getCallout();
		callout.setStyle(R.xml.callout_style);
		// map.addLayer(tempGraphicsLayer);
		// map.addLayer(bzdzGraphicsLayer);
		// map.addLayer(ggsjGraphicsLayer);
		// map.addLayer(textGraphicsLayer);
		//
		// if (zrqGeometry != null)
		// {
		// map.addLayer(aniGraphicsLayer);
		// map.addLayer(helpGraphicsLayer);
		// }
		map.setOnStatusChangedListener(new OnStatusChangedListener()
		{
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status)
			{
				if (source == map && status == STATUS.INITIALIZED)
				{
					ls = map.getLocationService();
					ls.setAutoPan(false);
					ls.start();

					map.setOnLongPressListener(new OnLongPressListener()
					{
						private static final long serialVersionUID = 1L;

						@Override
						public void onLongPress(float x, float y)
						{
							if (!isMarking && (!moving))
							{
								final ArrayList<Graphic> graphics = MapActivityUtil
										.clickAtGraphic(x, y,
												bzdzGraphicsLayer,
												ggsjGraphicsLayer, OFF_SET);
								if (graphics == null)
								{
									callout.hide();
									return;
								}
								if (graphics.size() == 1)
								{
									tempGraphic = graphics.get(0);
									showPopw();
								}
								else
								{
									String[] info = MapActivityUtil
											.getGraphicsInfo(graphics);
									if (info.length == 0)
									{
										// TODO 有时候会出现graphics对象为null的bug
										// updateData(new CallBack()
										// {
										// @Override
										// public void call()
										// {
										// MapActivityUtil
										// .initBzdzSymbol(bzdzGraphicsLayer);
										// MapActivityUtil.initGgsjSymbol(
										// ggsjGraphicsLayer,
										// textGraphicsLayer);
										// }
										// });
										return;
									}
									new AlertDialog.Builder(MapActivity.this)
											.setTitle("搜索到多条数据")
											.setSingleChoiceItems(
													info,
													-1,
													new DialogInterface.OnClickListener()
													{
														@Override
														public void onClick(
																DialogInterface dialog,
																int which)
														{
															tempGraphic = graphics
																	.get(which);
															showPopw();
															dialog.dismiss();
														}
													}).show();
								}
							}
						}
					});
				}
			}
		});
		loadFullMap();
	}

	private void showPopw()
	{
		Object typeAttribute = tempGraphic.getAttributeValue("type");
		if (typeAttribute == null)
		{
			showBzdzPopw();
		}
		else
		{
			showGgsjPopw();
		}
	}

	private void initView()
	{
		// 上层操作控制按钮
		bt_new = (Button) findViewById(R.id.bt_new);
		bt_add_jlx = (Button) findViewById(R.id.bt_add_jlx);
		bt_work_search = (Button) findViewById(R.id.bt_work_search);
		bt_data_export = (Button) findViewById(R.id.bt_data_export);
		bt_layer_control = (Button) findViewById(R.id.bt_layer_control);
		bt_data_analyse = (Button) findViewById(R.id.bt_data_analyse);

		// 中层辅助按钮
		ll_mid_control_add = findViewById(R.id.ll_mid_control_add);
		ll_mid_control_update = findViewById(R.id.ll_mid_control_update);
		bt_add = (Button) findViewById(R.id.bt_add);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_update = (Button) findViewById(R.id.bt_update);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);

		ll_notice = findViewById(R.id.ll_notice);
		im_type = findViewById(R.id.im_type);

		// 下层操作按钮
		bt_location = (Button) findViewById(R.id.bt_location);
		bt_help = (Button) findViewById(R.id.bt_help);
		// 地图导航栏
		bt_zoomout = (Button) findViewById(R.id.bt_zoomout);
		bt_zoomfull = (Button) findViewById(R.id.bt_zoomfull);
		bt_zoomin = (Button) findViewById(R.id.bt_zoomin);

		bt_new.setOnClickListener(this);
		bt_add_jlx.setOnClickListener(this);
		bt_work_search.setOnClickListener(this);
		bt_data_analyse.setOnClickListener(this);
		bt_data_export.setOnClickListener(this);
		bt_layer_control.setOnClickListener(this);

		im_type.setOnClickListener(this);
		bt_add.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_update.setOnClickListener(this);
		bt_cancle.setOnClickListener(this);

		bt_location.setOnClickListener(this);
		bt_help.setOnClickListener(this);

		bt_zoomout.setOnClickListener(this);
		bt_zoomfull.setOnClickListener(this);
		bt_zoomin.setOnClickListener(this);
		if (LoginUser.isAdministrator)
		{
			bt_new.setVisibility(View.GONE);
			Button bt_user = (Button) findViewById(R.id.bt_user);
			LinearLayout ll_administrator = (LinearLayout) findViewById(R.id.ll_administrator);
			Button bt_data_delete = (Button) findViewById(R.id.bt_data_delete);
			Button bt_data_import = (Button) findViewById(R.id.bt_data_import);
			bt_user.setVisibility(View.VISIBLE);
			ll_administrator.setVisibility(View.VISIBLE);
			bt_user.setOnClickListener(this);
			bt_data_delete.setOnClickListener(this);
			bt_data_import.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_new:
			if (isMarking)
			{
				clear();
			}
			else
			{
				// int currentTextColor = bt_new.getCurrentTextColor();
				// if (currentTextColor == Color.RED)
				// {
				// bt_new.setTextColor(Color.WHITE);
				// }
				// else
				// {
				isMarking = true;
				bt_new.setTextColor(Color.RED);
				changeType();
				// }
			}
			break;
		case R.id.bt_add_jlx:
			// TODO 添加街路巷
			showAddJlxDialog();
			break;
		case R.id.bt_work_search:
			clear();
			Intent workSearchIntent = new Intent(this, WorkSearchActivity.class);
			startActivity(workSearchIntent);
			break;
		case R.id.bt_data_analyse:
			clear();
			Intent dataAnalyseIntent = new Intent(this,
					DataAnalyseActivity.class);
			startActivity(dataAnalyseIntent);
			break;
		case R.id.bt_data_export:
			Intent exportIntent = new Intent(this, DataExportActivity.class);
			startActivity(exportIntent);
			break;
		case R.id.bt_layer_control:
			showLayerDialog();
			break;
		case R.id.im_type:
			changeType();
			break;
		case R.id.bt_add:
			if (mapDict != null)
			{
				if ("GGSJ_MPHM".equals(mapDict.getYwbm()))
				{
					// 此乃标准地址-住宅
					Intent intent = new Intent(this, FormBaseActivity.class);
					Building building = new Building(new BaseBuilding(new MPHM(
							"" + mapPoint.getX(), "" + mapPoint.getY(),
							Source.mphm)));
					intent.putExtra("building", building);
					startActivity(intent);
				}
				else
				{
					// 公共数据
					BaseAttrs attrs = fillGgsj();
					// Type type = Type.valueOf(mapDict.getYwbm());
					go2GgsjActivity(attrs.type, attrs);
				}
			}
			break;
		case R.id.bt_clear:
			tempGraphicsLayer.removeAll();
			changeType();
			bt_add.setEnabled(false);
			bt_clear.setEnabled(false);
			break;
		case R.id.bt_update:
			bt_new.setEnabled(true);
			tempGraphicsLayer.removeAll();
			moving = false;
			ll_mid_control_update.setVisibility(View.INVISIBLE);
			Object attributeValue = tempGraphic.getAttributeValue("type");
			Map<String, Object> attributes = tempGraphic.getAttributes();
			String ID = tempGraphic.getAttributeValue("key").toString();
			if (attributeValue == null)
			{
				// 住宅
				bzdzGraphicsLayer.addGraphic(new Graphic(mapPoint,
						Source.bzdzSymbol, attributes, null));
				PointDaoImpl.updateBuildingPoint(getApplicationContext(), ID,
						"" + mapPoint.getX(), "" + mapPoint.getY());
				Building building = Source.findBuilding(ID);
				if (building != null)
				{
					building.baseBuilding.mphm.X = "" + mapPoint.getX();
					building.baseBuilding.mphm.Y = "" + mapPoint.getY();
				}
			}
			else
			{
				// 公共数据
				String uidStr = tempGraphic.getAttributeValue("uid").toString();
				int uid = Integer.parseInt(uidStr);
				textGraphicsLayer.removeGraphic(uid);
				// String lx = attributes.get("lx").toString();
				// int color = Color.BLACK;
				// if ("网吧".equals(lx))
				// {
				// color = Color.RED;
				// }
				// else if (lx.startsWith("中式"))
				// {
				// color = Color.GREEN;
				// }
				// else if (lx.startsWith("宾馆") || lx.startsWith("一般旅馆"))
				// {
				// color = Color.BLUE;
				// }
				Type type = Type.valueOf(attributes.get("type").toString());
				BaseAttrs attrs = Source.findGgsj(type, ID);
				if (attrs != null)
				{
					attrs.X = "" + mapPoint.getX();
					attrs.Y = "" + mapPoint.getY();
				}
				int color = Color.BLACK;
				if ("网吧".equals(attrs.LX))
				{
					color = Color.RED;
				}
				else if (attrs.LX.startsWith("中式"))
				{
					color = Color.GREEN;
				}
				else if (attrs.LX.startsWith("宾馆")
						|| attrs.LX.startsWith("一般旅馆"))
				{
					color = Color.BLUE;
				}
				String mc = "";
				if (attrs != null)
				{
					mc = attrs.MC;
				}
				Graphic textGraphic = new Graphic(mapPoint, new TextSymbol(16,
						mc, color));
				int newUid = textGraphicsLayer.addGraphic(textGraphic);
				attributes.put("uid", newUid);
				ggsjGraphicsLayer.addGraphic(new Graphic(mapPoint,
						Source.symbols.get(type), attributes, null));
				PointDaoImpl.updateGgsjPoint(getApplicationContext(),
						type.toString(), ID, "" + mapPoint.getX(), ""
								+ mapPoint.getY());
			}
			break;
		case R.id.bt_cancle:
			bt_new.setEnabled(true);
			tempGraphicsLayer.removeAll();
			moving = false;
			ll_mid_control_update.setVisibility(View.INVISIBLE);
			Object typeAttr = tempGraphic.getAttributeValue("type");
			if (typeAttr == null)
			{
				// 住宅
				bzdzGraphicsLayer.addGraphic(tempGraphic);
			}
			else
			{
				// 公共数据
				// String uidStr =
				// tempGraphic.getAttributeValue("uid").toString();
				ggsjGraphicsLayer.addGraphic(tempGraphic);
			}
			break;
		case R.id.bt_location:
			MapActivityUtil.updateLocationServiceState(ls, this);
			break;
		case R.id.bt_help:
			if (zrqGeometry == null)
			{
				Toast.makeText(this, "暂时没有责任区范围", Toast.LENGTH_LONG).show();
			}
			else
			{
				helpGraphicsLayer.setVisible(!helpGraphicsLayer.isVisible());
			}
			break;
		case R.id.bt_zoomout:
			map.zoomout();
			break;
		case R.id.bt_zoomfull:
			loadFullMap();
			break;
		case R.id.bt_zoomin:
			map.zoomin();
			break;
		case R.id.bt_user:
			// 用户管理界面
			Intent userIntent = new Intent(getApplicationContext(),
					UserManagerActivity.class);
			startActivity(userIntent);
			break;
		case R.id.bt_data_delete:
			// 数据批量删除界面
			Intent deleteIntent = new Intent(getApplicationContext(),
					DataDeleteActivity.class);
			startActivity(deleteIntent);
			break;
		case R.id.bt_data_import:
			// 数据导入界面
			Intent importIntent = new Intent(getApplicationContext(),
					DataImportActivity.class);
			startActivity(importIntent);
			break;
		}
	}

	private void go2GgsjActivity(Type type, BaseAttrs attrs)
	{
		switch (type)
		{
		case GGSJ_GGSS:
			Intent ggssIntent = new Intent(getApplicationContext(),
					GgssActivity.class);
			attrs.mphm = null;
			ggssIntent.putExtra("attrs", new GGSS(attrs));
			startActivity(ggssIntent);
			break;
		case GGSJ_JTSS:
			Intent jtssIntent = new Intent(getApplicationContext(),
					JtssActivity.class);
			attrs.mphm = null;
			jtssIntent.putExtra("attrs", new JTSS(attrs));
			startActivity(jtssIntent);
			break;
		default:
			Intent intent = new Intent(getApplicationContext(),
					FormBaseActivity.class);
			intent.putExtra("attrs", attrs);
			startActivity(intent);
		}
	}

	private BaseAttrs fillGgsj()
	{
		Type type = Type.valueOf(mapDict.getYwbm());
		String fldm = mapDict.getDM();
		String gbdm = fldm.substring(1);
		return new BaseAttrs("" + mapPoint.getX(), "" + mapPoint.getY(), fldm,
				gbdm, mapDict.getMC(), mapDict.getMapping(), type, Source.mphm,
				Source.extraDz);
	}

	private void showLayerDialog()
	{
		final boolean[] tempCheck = new boolean[check.length];
		for (int i = 0; i < tempCheck.length; i++)
		{
			tempCheck[i] = check[i];
		}

		new AlertDialog.Builder(this)
				.setTitle("显示图层")
				.setMultiChoiceItems(new String[] { "标准数据", "公共数据", "图标文字" },
						tempCheck, new OnMultiChoiceClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked)
							{
							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						check = tempCheck;
						updatelayer();
					}
				}).setNegativeButton("取消", null).show();
	}

	// 擦除所有的临时变量，将所有状态重置
	private void clear()
	{
		tempGraphicsLayer.removeAll();
		callout.hide();
		tempGraphic = null;
		mapDict = null;
		moving = false;
		isMarking = false;
		bt_new.setEnabled(true);
		ll_mid_control_add.setVisibility(View.INVISIBLE);
		ll_mid_control_update.setVisibility(View.INVISIBLE);
		ll_notice.setVisibility(View.INVISIBLE);
		bt_new.setTextColor(Color.WHITE);
	}

	private void showGgsjPopw()
	{
		if (tempGraphic != null)
		{
			String typeStr = tempGraphic.getAttributeValue("type").toString();
			final Type type = Type.valueOf(typeStr);
			String ID = tempGraphic.getAttributeValue("key").toString();
			final BaseAttrs attrs = Source.findGgsj(type, ID);
			if (attrs == null)
			{
				return;
			}
			// String lx = tempGraphic.getAttributeValue("lx").toString();
			// String mc = tempGraphic.getAttributeValue("mc").toString();
			// Object dzAttr = tempGraphic.getAttributeValue("dz");
			// String dz = null;
			// if (dzAttr != null)
			// {
			// dz = dzAttr.toString();
			// }
			// String djsj = tempGraphic.getAttributeValue("djsj").toString();
			// Object bsValue = tempGraphic.getAttributeValue("bs");
			View view = View.inflate(getApplicationContext(),
					R.layout.ggsj_callout, null);
			View bind = view.findViewById(R.id.bt_bind);
			if (attrs.bs > 0)
			{
				bind.setVisibility(View.VISIBLE);
			}
			TextView tv_lx = (TextView) view.findViewById(R.id.tv_lx);
			tv_lx.setText(attrs.LX);
			View ll_mc = view.findViewById(R.id.ll_mc);
			View ll_dz = view.findViewById(R.id.ll_dz);
			// View ll_cjsj = view.findViewById(R.id.ll_cjsj);
			if (TextUtils.isEmpty(attrs.MC))
			{
				ll_mc.setVisibility(View.GONE);
			}
			else
			{
				TextView tv_mc = (TextView) view.findViewById(R.id.tv_mc);
				tv_mc.setText(attrs.MC);
			}
			if (TextUtils.isEmpty(attrs.DZ))
			{
				ll_dz.setVisibility(View.GONE);
			}
			else
			{
				TextView tv_dz = (TextView) view.findViewById(R.id.tv_dz);
				tv_dz.setText(attrs.DZ);
			}
			// if (TextUtils.isEmpty(djsj))
			// {
			// ll_cjsj.setVisibility(View.GONE);
			// }
			// else
			// {
			TextView tv_cjsj = (TextView) view.findViewById(R.id.tv_cjsj);
			tv_cjsj.setText(attrs.DJSJ);
			// }
			bind.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					showBindDialog(type);
				}
			});
			view.findViewById(R.id.bt_move).setOnClickListener(
					new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							callout.hide();
							moving = true;
							bt_new.setEnabled(false);
							ll_mid_control_update.setVisibility(View.VISIBLE);
							ggsjGraphicsLayer.removeGraphic(tempGraphic
									.getUid());
							// String uidStr = tempGraphic
							// .getAttributeValue("uid").toString();
							// int uid = Integer.parseInt(uidStr);
							// textGraphicsLayer.removeGraphic(uid);
						}
					});
			view.findViewById(R.id.tv_lx).setOnClickListener(
					new OnClickListener()
					{
						@Override
						public void onClick(View arg0)
						{
							callout.hide();
							TypeUtil.go2GgsjActivity(attrs, MapActivity.this);
							// go2GgsjActivity(type, attrs);
						}
					});

			view.findViewById(R.id.tv_lx).setOnLongClickListener(
					new OnLongClickListener()
					{
						@Override
						public boolean onLongClick(View v)
						{
							showDeleteDialog();
							return false;
						}
					});
			callout.setMaxHeight(400);
			callout.setMaxWidth(600);
			callout.setOffset(8, 20);
			Geometry geometry = tempGraphic.getGeometry();
			callout.show((Point) geometry, view);
			map.postInvalidate();
		}
	}

	private void showBindDialog(Type type)
	{
		String ID = tempGraphic.getAttributeValue("key").toString();
		// final Object lxAttribute = tempGraphic.getAttributeValue("lx");
		ArrayList<Ywzy> list = new ArrayList<Ywzy>();
		for (Map.Entry<String, ArrayList<Ywzy>> entry : Source.ywzys.entrySet())
		{
			ArrayList<Ywzy> value = entry.getValue();
			for (Ywzy ywzy : value)
			{
				// System.out.println("ywzy:" + ywzy);
				if (ywzy.getGgsj_id().equals(ID))
				{
					list.add(ywzy);
				}
			}
		}
		BaseAttrs attrs = Source.findGgsj(type, ID);
		// 到此为止找到所有绑定在该条公共数据上的业务专用数据
		if (list.size() == 0)
		{
			// 说明提示图标有误
			NoticeUtil.showWarningDialog(this, "未找到关联的业务专用数据");
			attrs.bs = 0;
			new GgsjDaoImpl(getApplicationContext()).updateGgsj(attrs);
		}
		else
		{
			DictDialogUtil.clearYwzyChoose(this, list, attrs);
		}
	}

	private void showBzdzPopw()
	{
		if (tempGraphic != null)
		{
			View view = View.inflate(getApplicationContext(),
					R.layout.bzdz_callout, null);
			TextView tv_building_dz = (TextView) view
					.findViewById(R.id.tv_building_dz);
			TextView tv_unit_num = (TextView) view
					.findViewById(R.id.tv_unit_num);
			TextView tv_djr = (TextView) view.findViewById(R.id.tv_djr);
			TextView tv_djsj = (TextView) view.findViewById(R.id.tv_djsj);
			String mlxz = tempGraphic.getAttributeValue("mlxz").toString();
			// 单元数如果是私宅的话没有
			Object dysObj = tempGraphic.getAttributeValue("dys");
			String djr = tempGraphic.getAttributeValue("djr").toString();
			String djsj = tempGraphic.getAttributeValue("djsj").toString();
			if (dysObj == null)
			{
				TextView tv_sx = (TextView) view.findViewById(R.id.tv_sx);
				tv_sx.setText("房屋类型:");
				tv_unit_num.setText("私宅");
			}
			else
			{
				tv_unit_num.setText(dysObj.toString());
			}
			tv_building_dz.setText(mlxz);
			tv_djr.setText(djr);
			tv_djsj.setText(djsj);
			TextView tv_move = (TextView) view.findViewById(R.id.tv_move);
			tv_move.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			tv_move.setText("移动图标");
			tv_move.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					callout.hide();
					moving = true;
					bt_new.setEnabled(false);
					ll_mid_control_update.setVisibility(View.VISIBLE);
					bzdzGraphicsLayer.removeGraphic(tempGraphic.getUid());
				}
			});
			tv_building_dz.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					callout.hide();
					Intent intent = new Intent();
					String ID = tempGraphic.getAttributeValue("key").toString();
					intent.setClass(getApplicationContext(),
							FormBaseActivity.class);
					Building building = Source.findBuilding(ID);
					if (building != null)
					{
						intent.putExtra("building", building);
						startActivity(intent);
					}
				}
			});
			tv_building_dz.setOnLongClickListener(new OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					showDeleteDialog();
					return false;
				}
			});
			callout.setMaxHeight(400);
			callout.setMaxWidth(600);
			callout.setOffset(8, 20);
			Geometry geometry = tempGraphic.getGeometry();
			callout.show((Point) geometry, view);
			map.postInvalidate();
		}
	}

	private void showDeleteDialog()
	{
		final Object typeAttribute = tempGraphic.getAttributeValue("type");
		new Builder(this).setTitle("提示").setMessage("您是否要删除该条数据？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (typeAttribute == null)
						{
							// 住宅
							final String ID = tempGraphic.getAttributeValue(
									"key").toString();
							int delete = new BzdzDaoImpl(
									getApplicationContext()).deleteBuilding(ID);
							if (delete > 0)
							{
								bzdzGraphicsLayer.removeGraphic(tempGraphic
										.getUid());
								Source.removeBuilding(ID);
							}
						}
						else
						{
							// 公共数据
							String uidStr = tempGraphic
									.getAttributeValue("uid").toString();
							int uid = Integer.parseInt(uidStr);
							String ID = tempGraphic.getAttributeValue("key")
									.toString();
							Type type = Type.valueOf(tempGraphic
									.getAttributeValue("type").toString());
							BaseAttrs attrs = Source.findGgsj(type, ID);
							if (attrs != null)
							{
								int delete = new GgsjDaoImpl(
										getApplicationContext()).delete(attrs);
								if (delete > 0)
								{
									textGraphicsLayer.removeGraphic(uid);
									ggsjGraphicsLayer.removeGraphic(tempGraphic
											.getUid());
									Source.removeGgsj(attrs);
								}
							}
						}
						// 在这里及时把标记置为false，因为修改已经处理完了
						Source.update = false;
						callout.hide();
					}
				}).setNegativeButton("取消", null).show();
	}

	private void initData()
	{
		// 初始化图层选中状态
		check[0] = false;
		check[1] = false;
		check[2] = false;
		tempSymbol = new PictureMarkerSymbol(getResources().getDrawable(
				R.drawable.map_location));
		// 初始化所有图层（交互、住宅、公共数据、文本、责任区范围、越界提示）
		tempGraphicsLayer = new GraphicsLayer();
		bzdzGraphicsLayer = new GraphicsLayer();
		ggsjGraphicsLayer = new GraphicsLayer();
		textGraphicsLayer = new GraphicsLayer();
		helpGraphicsLayer = new GraphicsLayer();
		aniGraphicsLayer = new GraphicsLayer();
		// 这里获取责任区数据并且生成责任区范围
		initZrq();
		bzdzGraphicsLayer.setVisible(false);
		ggsjGraphicsLayer.setVisible(false);
		textGraphicsLayer.setVisible(false);
		helpGraphicsLayer.setVisible(false);
		aniGraphicsLayer.setVisible(false);
		if (hasError)
		{
			return;
		}
		map = (MapView) findViewById(R.id.map);
		MyTouchListener myListener = new MyTouchListener(
				getApplicationContext(), map);
		map.setOnTouchListener(myListener);
		ArcGISLocalTiledLayer local = null;
		try
		{
			local = new ArcGISLocalTiledLayer("file://" + Constans.MAP_SOURCE
					+ "/Layers");
		}
		catch (Exception e)
		{
			NoticeUtil.showErrorDialog(this, "地图资源文件损坏！");
			return;
		}
		if (local != null)
		{
			map.addLayer(local);
		}
		map.addLayer(tempGraphicsLayer);
		map.addLayer(bzdzGraphicsLayer);
		map.addLayer(ggsjGraphicsLayer);
		map.addLayer(textGraphicsLayer);

		if (zrqGeometry != null)
		{
			map.addLayer(aniGraphicsLayer);
			map.addLayer(helpGraphicsLayer);
		}

		updateData(new CallBack()
		{
			@Override
			public void call()
			{
				MapActivityUtil.initBzdzSymbol(bzdzGraphicsLayer);
				MapActivityUtil.initGgsjSymbol(ggsjGraphicsLayer,
						textGraphicsLayer);
			}

			@Override
			public void post()
			{

			}
		});
	}

	private abstract class CallBack
	{
		public abstract void call();

		public abstract void post();
	}

	private void updateData(final CallBack callBack)
	{

		new AsyncTask<Void, Void, Void>()
		{
			ProgressDialog dialog;

			@Override
			protected void onPreExecute()
			{
				dialog = ProgressDialog.show(MapActivity.this, "请稍候...",
						"正在努力为您加载图层数据");
			}

			@Override
			protected Void doInBackground(Void... params)
			{
				callBack.call();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				dialog.dismiss();
				callBack.post();
			}
		}.execute();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (Source.update)
		{
			Source.update = false;
			tempGraphicsLayer.removeAll();
			bt_add.setEnabled(false);
			bt_clear.setEnabled(false);

			updateData(new CallBack()
			{
				@Override
				public void call()
				{
					MapActivityUtil.initBzdzSymbol(bzdzGraphicsLayer);
					MapActivityUtil.initGgsjSymbol(ggsjGraphicsLayer,
							textGraphicsLayer);
				}

				@Override
				public void post()
				{

				}
			});
		}
	}

	// private void showPopwAtPoint(Serializable pointExtra)
	// {
	// if (pointExtra != null && pointExtra instanceof Point)
	// {
	// callout.hide();
	// clear();
	// Point point = (Point) pointExtra;
	// Graphic graphicAtPoint = getGraphicAtPoint(point);
	// String type = "";
	// if (graphicAtPoint != null)
	// {
	// type = graphicAtPoint.getAttributeValue("type").toString();
	// }
	// else
	// {
	// Toast.makeText(this, "图标定位失败！", 1).show();
	// }
	// if ("bzdz".equals(type))
	// {
	// initPopwindow(point, graphicAtPoint);
	// }
	// else
	// {
	// Graphic textGraphic = getGraphicAtPoint(textGraphicsLayer,
	// point);
	// initPopwindow(point, graphicAtPoint, textGraphic);
	// }
	// map.centerAt(point, false);
	// map.postInvalidate();
	// }
	// }

	private void loadFullMap()
	{
		map.zoomToResolution(map.getCenter(), defaultRes);
		map.centerAt(new Point(Constans.centreX, Constans.centreY), true);
	}

	class MyTouchListener extends MapOnTouchListener
	{
		public MyTouchListener(Context arg0, MapView arg1)
		{
			super(arg0, arg1);
		}

		public boolean onSingleTap(MotionEvent e)
		{
			mapPoint = map.toMapPoint(e.getX(), e.getY());
			System.out.println("点击地图上的点，X：" + mapPoint.getX() + " , Y:"
					+ mapPoint.getY());
			if (!isMarking)
			{
				if (!moving)
				{
					// 表明该操作没有任何状态，只是普通的单机地图
					callout.hide();
					return false;
				}
				// 表明是moving状态
				if (checkRole(mapPoint))
				{
					bt_update.setEnabled(true);
					tempGraphicsLayer.removeAll();
					Point mp = map.toMapPoint(e.getX(), e.getY());
					Graphic graphic = new Graphic(mp, tempSymbol);
					tempGraphicsLayer.addGraphic(graphic);
				}
				else
				{
					zrqRoleErrorNotice();
				}
				return true;
			}
			addPoint(e.getX(), e.getY());
			return true;
		}

		public boolean onDragPointerMove(MotionEvent from, MotionEvent to)
		{
			return super.onDragPointerMove(from, to);
		}

		@Override
		public boolean onDragPointerUp(MotionEvent from, MotionEvent to)
		{
			return super.onDragPointerUp(from, to);
		}
	}

	private void updatelayer()
	{
		callout.hide();
		bzdzGraphicsLayer.setVisible(check[0]);
		ggsjGraphicsLayer.setVisible(check[1]);
		if (check[1])
		{
			textGraphicsLayer.setVisible(check[2]);
		}
		else
		{
			textGraphicsLayer.setVisible(false);
		}
	}

	// TODO 利用回调接口处理改变公共数据类型
	private void changeType()
	{
		// DictDialogUtil.changeType(this, new MapDictCallback()
		// {
		// @Override
		// public void call(MapLevelDictObject mapLevelDictObject)
		// {
		// mapDict = mapLevelDictObject;
		// if (mapDict != null)
		// {
		// ll_notice.setVisibility(View.VISIBLE);
		// String ywbm = mapDict.getYwbm();
		// if ("GGSJ_MPHM".equals(ywbm))
		// {
		// im_type.setBackgroundResource(R.drawable.zz);
		// NoticeUtil.showNoticeAni(ll_notice);
		// }
		// else
		// {
		// Type type = Type.valueOf(ywbm);
		// im_type.setBackgroundResource(type.getSource());
		// NoticeUtil.showNoticeAni(ll_notice);
		// }
		// }
		// }
		// });
		final DictUtil instance = DictUtil.getInstance(this);
		final ArrayList<MapLevelDictObject> dicts = instance
				.getMapLevelChildren(null, DictUtil.GGSJ_DICT);

		View view = View.inflate(this, R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);
		LinearLayout fliter = (LinearLayout) view
				.findViewById(R.id.dialog_filter);
		@SuppressWarnings("unchecked")
		final ArrayList<MapLevelDictObject> showDatas = (ArrayList<MapLevelDictObject>) dicts
				.clone();
		final DictListAdapterSingleChoose adapter = new DictListAdapterSingleChoose(
				this, showDatas);
		Builder builder = new AlertDialog.Builder(this)
				.setTitle("地理信息类别")
				.setIcon(R.drawable.down_icon)
				.setPositiveButton("上一步", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						try
						{
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						MapLevelDictObject mapLevelDictObject = showDatas
								.get(0);
						showDatas.clear();
						showDatas.addAll(instance
								.getUpLevelDicts(mapLevelDictObject));
						adapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						clear();
						try
						{
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
		if (dicts.size() > 30)
		{
			fliter.setVisibility(View.VISIBLE);
			Button fliterBtn = (Button) view
					.findViewById(R.id.dialog_filter_btn);
			final EditText fliterEt = (EditText) view
					.findViewById(R.id.dialog_filter_et);
			fliterBtn.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String filter = fliterEt.getText().toString();
					showDatas.clear();
					showDatas
							.addAll((ArrayList<MapLevelDictObject>) MapActivityUtil
									.filterLevelDicts(dicts, filter));
					adapter.notifyDataSetChanged();
				}
			});
		}
		else
		{
			fliter.setVisibility(View.GONE);
		}
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		dialogList.setAdapter(adapter);
		dialogList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				MapLevelDictObject mapLevelDictObject = showDatas.get(position);
				ArrayList<MapLevelDictObject> levelChildren = instance
						.getMapLevelChildren(mapLevelDictObject,
								DictUtil.GGSJ_DICT);
				if (levelChildren.size() == 0)
				{
					try
					{
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					mapDict = mapLevelDictObject;
					if (mapDict != null)
					{
						ll_notice.setVisibility(View.VISIBLE);
						String ywbm = mapDict.getYwbm();
						if ("GGSJ_MPHM".equals(ywbm))
						{
							im_type.setBackgroundResource(R.drawable.zz);
							NoticeUtil.showNoticeAni(ll_notice);
						}
						else
						{
							Type type = Type.valueOf(ywbm);
							im_type.setBackgroundResource(type.getSource());
							NoticeUtil.showNoticeAni(ll_notice);
						}
					}
					dialog.dismiss();
				}
				else
				{
					showDatas.clear();
					showDatas.addAll(instance.getMapLevelChildren(
							mapLevelDictObject, DictUtil.GGSJ_DICT));
					adapter.notifyDataSetChanged();
				}
			}
		});
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * marking状态点击了地图
	 */
	private void addPoint(float x, float y)
	{
		if (checkRole(mapPoint))
		{
			tempGraphicsLayer.removeAll();
			Point mp = map.toMapPoint(x, y);
			Graphic graphic = new Graphic(mp, tempSymbol);
			tempGraphicsLayer.addGraphic(graphic);
			ll_mid_control_add.setVisibility(View.VISIBLE);
			// bt_add.setText("添加");
			bt_add.setEnabled(true);
			bt_clear.setEnabled(true);
		}
		else
		{
			zrqRoleErrorNotice();
		}
	}

	// private Graphic getGraphicAtPoint(Point mapPoint)
	// {
	// int[] bzdzGraphicIDs = bzdzGraphicsLayer.getGraphicIDs();
	// Graphic result = null;
	// if (bzdzGraphicIDs != null && bzdzGraphicIDs.length > 0)
	// {
	// for (int id : bzdzGraphicIDs)
	// {
	// Graphic graphic = bzdzGraphicsLayer.getGraphic(id);
	// Geometry geometry = graphic.getGeometry();
	// if (geometry != null && geometry instanceof Point)
	// {
	// Point point = (Point) geometry;
	// if ((mapPoint.getX() == point.getX())
	// && (mapPoint.getY() == point.getY()))
	// {
	// result = graphic;
	// bzdzGraphicsLayer.setVisible(true);
	// check[0] = true;
	// break;
	// }
	// }
	// }
	// }
	// if (result == null)
	// {
	// int[] ggsjGraphicIDs = ggsjGraphicsLayer.getGraphicIDs();
	// if (ggsjGraphicIDs != null && ggsjGraphicIDs.length > 0)
	// {
	// for (int id : ggsjGraphicIDs)
	// {
	// Graphic graphic = ggsjGraphicsLayer.getGraphic(id);
	// Geometry geometry = graphic.getGeometry();
	// if (geometry != null && geometry instanceof Point)
	// {
	// Point point = (Point) geometry;
	// if ((mapPoint.getX() == point.getX())
	// && (mapPoint.getY() == point.getY()))
	// {
	// result = graphic;
	// ggsjGraphicsLayer.setVisible(true);
	// check[1] = true;
	// if (check[2])
	// {
	// textGraphicsLayer.setVisible(true);
	// }
	// break;
	// }
	// }
	// }
	// }
	// }
	// return result;
	// }

	// private Graphic getGraphicAtPoint(GraphicsLayer layer, Point mapPoint)
	// {
	// int[] graphicIDs = layer.getGraphicIDs();
	// Graphic result = null;
	// if (graphicIDs != null && graphicIDs.length > 0)
	// {
	// for (int id : graphicIDs)
	// {
	// Graphic graphic = layer.getGraphic(id);
	// Geometry geometry = graphic.getGeometry();
	// if (geometry != null && geometry instanceof Point)
	// {
	// Point point = (Point) geometry;
	// if (mapPoint.getX() == point.getX())
	// {
	// result = graphic;
	// break;
	// }
	// }
	// }
	// }
	// return result;
	// }

	// 初始化责任区
	private void initZrq()
	{
		// zrqGeometry = MapUtil
		// .getGeometryFromJson(MapUtil
		// .formatPointQueue2Polygon("[[[115.17773,29.86826],[115.17773,29.86813],[115.18362,29.8683],[115.18761,29.86838],[115.18942,29.86817],[115.1911,29.86771],[115.19241,29.86725],[115.19337,29.86653],[115.19497,29.8654],[115.19584,29.86456],[115.19573,29.86448],[115.1957,29.86444],[115.19564,29.86439],[115.1956,29.86435],[115.1955,29.8641],[115.19511,29.86306],[115.19486,29.86256],[115.19477,29.86233],[115.19477,29.86213],[115.19477,29.86187],[115.1949,29.86127],[115.19505,29.86088],[115.1951,29.86067],[115.19514,29.86054],[115.19514,29.86045],[115.19512,29.86029],[115.19507,29.86001],[115.19495,29.85983],[115.19468,29.85963],[115.19425,29.85917],[115.19409,29.85864],[115.19392,29.85805],[115.19388,29.85787],[115.19393,29.85764],[115.19408,29.85717],[115.19399,29.85698],[115.19373,29.85661],[115.19358,29.85647],[115.19356,29.85637],[115.19356,29.85625],[115.19359,29.85608],[115.19351,29.85597],[115.19342,29.85586],[115.19341,29.85577],[115.19343,29.8557],[115.19345,29.85566],[115.19359,29.85561],[115.19389,29.85553],[115.19395,29.85546],[115.19398,29.8553],[115.19398,29.85516],[115.19395,29.85501],[115.19362,29.85487],[115.19344,29.85483],[115.19331,29.85477],[115.1933,29.85467],[115.19334,29.85457],[115.19421,29.85368],[115.19236,29.85305],[115.188,29.85154],[115.1873,29.85239],[115.18481,29.85538],[115.18249,29.85701],[115.17992,29.85847],[115.17745,29.85967],[115.17116,29.86354],[115.16549,29.86695],[115.16482,29.8673],[115.16434,29.86771],[115.16505,29.86784],[115.17773,29.86826]]]"));
		boolean empty = TextUtils.isEmpty(LoginUser.zrqFw);
		if (!empty)
		{
			zrqGeometry = MapUtil.getGeometryFromJson(MapUtil
					.formatPointQueue2Polygon(LoginUser.zrqFw));
			if (zrqGeometry != null)
			{
				helpGraphicsLayer.addGraphic(new Graphic(zrqGeometry,
						new SimpleLineSymbol(Color.RED, 5)));
				aniGraphicsLayer.addGraphic(new Graphic(zrqGeometry,
						new SimpleFillSymbol(Color.BLUE)));
			}
		}
	}

	private void zrqRoleErrorNotice()
	{
		if (zrqGeometry != null)
		{
			map.setExtent(zrqGeometry);
		}
		else
		{
			loadFullMap();
		}
		MapActivityUtil.noticeAni(aniGraphicsLayer);
		Toast.makeText(getApplicationContext(), "采集点不在责任区", Toast.LENGTH_LONG)
				.show();
	}

	private boolean checkRole(Point point)
	{
		if (zrqGeometry == null)
		{
			return true;
		}
		if (point == null)
		{
			return false;
		}
		if (GeometryEngine.contains(zrqGeometry, point,
				map.getSpatialReference()))
		{
			return true;
		}
		return false;
	}

	/**
	 * 添加街路巷字典
	 */
	public void showAddJlxDialog()
	{
		final DictUtil dictUtil = DictUtil.getInstance(this);
		View view = View.inflate(this, R.layout.dialog_add_jlx, null);
		final InputView input_jlx_dm = (InputView) view
				.findViewById(R.id.input_jlx_dm);
		final InputView input_jlx_mc = (InputView) view
				.findViewById(R.id.input_jlx_mc);

		Button bt_save = (Button) view.findViewById(R.id.bt_save);
		Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		final AlertDialog dialog = new Builder(this).setTitle("新增街路巷")
				.setView(view).show();
		bt_cancle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		bt_save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String dm = input_jlx_dm.getShowView().getText().toString()
						.trim();
				String mc = input_jlx_mc.getShowView().getText().toString();
				if (TextUtils.isEmpty(dm))
				{
					NoticeUtil.showWarningDialog(MapActivity.this, "街路巷代码未填写！");
					return;
				}
				if (TextUtils.isEmpty(mc))
				{
					NoticeUtil.showWarningDialog(MapActivity.this, "街路巷名称未填写！");
					return;
				}
				if (!dm.startsWith("00"))
				{
					NoticeUtil.showWarningDialog(MapActivity.this,
							"手动添加的街路巷代码必须是'00'开头");
					return;
				}
				ArrayList<? extends DictObject> dicts = dictUtil
						.queryDict(DictUtil.DZ_JLX);

				if (dicts != null && dicts.size() > 0)
				{
					for (DictObject dict : dicts)
					{
						if (dict.getMC().equals(mc))
						{
							NoticeUtil.showWarningDialog(MapActivity.this,
									"街路巷名称已存在，请仔细检查！");
							return;
						}
					}
				}
				// 新增街路巷
				long insert = dictUtil.addJlx(dm, mc);
				if (insert > 0)
				{
					Toast.makeText(getApplicationContext(), "新增成功",
							Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
	}
}