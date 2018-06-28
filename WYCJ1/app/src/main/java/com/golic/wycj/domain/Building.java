package com.golic.wycj.domain;

import java.io.Serializable;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.golic.wycj.R;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.base.GraphicForm;
import com.golic.wycj.util.DictUtil;

/**
 * 幢楼
 * 
 * @author luo
 * 
 */
public class Building implements GraphicForm, Serializable,
		Comparable<Building>
{
	private static final long serialVersionUID = 8051711379317205282L;
	public BaseBuilding baseBuilding;
	public Unit[] units;

	public Building()
	{
		super();
	}

	public Building(BaseBuilding baseBuilding)
	{
		super();
		this.baseBuilding = baseBuilding;
	}

	public Building(BaseBuilding baseBuilding, Unit[] units)
	{
		super();
		this.baseBuilding = baseBuilding;
		this.units = units;
	}

	public int getMaxFloorSize()
	{
		int size = 0;
		if (units != null)
		{
			for (Unit unit : units)
			{
				if (size < unit.floors.length)
				{
					size = unit.floors.length;
				}
			}
		}
		return size;
	}

	public int getMaxRoomSize()
	{
		int size = 0;
		if (units != null)
		{
			for (Unit unit : units)
			{
				Floor[] floors = unit.floors;
				if (floors != null)
				{
					for (Floor floor : floors)
					{
						if (size < floor.rooms.length)
						{
							size = floor.rooms.length;
						}
					}
				}
			}
		}
		return size;
	}

//	public int getRoomNum()
//	{
//		int size = 0;
//		for (Unit unit : units)
//		{
//			Floor[] floors = unit.floors;
//			for (Floor floor : floors)
//			{
//				size += floor.rooms.length;
//
//			}
//		}
//		return size;
//	}

	// /**
	// * 绘制缩略图
	// *
	// * @param contentView
	// * 需要显示的控件
	// */
	// public void drawThumbnail(LinearLayout contentView)
	// {
	// contentView.removeAllViews();
	// Context context = contentView.getContext();
	// DictUtil instance = DictUtil.getInstance(context);
	// int maxFloorSize = getMaxFloorSize();
	// for (int i = 0; i < units.length; i++)
	// {
	// if (units.length > 5 && i > 1 && i != units.length - 1)
	// {
	// if (i == 3)
	// {
	// TextView midUnitTextView = new TextView(context);
	// midUnitTextView.setGravity(Gravity.BOTTOM);
	// midUnitTextView.setText(instance.getDictValue(
	// DictUtil.DZ_DYH, units[i].getDyh())
	// + "~~"
	// + instance.getDictValue(DictUtil.DZ_DYH,
	// units[units.length - 2].getDyh()));
	// midUnitTextView.setTextColor(Color.BLACK);
	// LayoutParams params = new LayoutParams(0,
	// LayoutParams.MATCH_PARENT, 2);
	// params.bottomMargin = 20;
	// contentView.addView(midUnitTextView, params);
	// }
	// continue;
	// }
	// Unit unit = units[i];
	// LinearLayout unitView = new LinearLayout(context);
	// unitView.setOrientation(LinearLayout.VERTICAL);
	// LayoutParams unitParams = new LayoutParams(0,
	// LayoutParams.MATCH_PARENT, 1);
	// TextView floorTv = new TextView(context);
	// floorTv.setText("" + unit.getFloors().length);
	// floorTv.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
	// // floorTv.setTextColor(Color.BLACK);
	// int weight = unit.getFloors().length;
	// int topWeight = maxFloorSize - weight + 2;
	// // if (topWeight > 7)
	// // {
	// // topWeight = 7;
	// // // weight = 6;
	// // }
	// if (topWeight / weight > 6)
	// {
	// topWeight = weight * 6;
	// }
	// else if (weight / topWeight > 7)
	// {
	// topWeight = weight / 7;
	// }
	// LayoutParams floorTopParams = new LayoutParams(
	// LayoutParams.MATCH_PARENT, 0, topWeight);
	// LinearLayout floorView = new LinearLayout(context);
	// floorView.setOrientation(LinearLayout.VERTICAL);
	// floorView.setGravity(Gravity.CENTER);
	// // floorView.setBackgroundColor(context.getResources().getColor(
	// // R.color.orange));
	// floorView.setBackgroundResource(R.drawable.init_floors_background);
	//
	// LayoutParams floorBottomParams = new LayoutParams(
	// LayoutParams.MATCH_PARENT, 0, weight);
	// LayoutParams floorItemParam = new LayoutParams(
	// LayoutParams.MATCH_PARENT, 30);
	// TextView roomTv = new TextView(context);
	// roomTv.setText("" + unit.getMaxRoomNum());
	// roomTv.setGravity(Gravity.CENTER);
	// roomTv.setTextColor(Color.BLACK);
	// floorView.addView(roomTv, floorItemParam);
	// // }
	//
	// unitView.addView(floorTv, floorTopParams);
	// unitView.addView(floorView, floorBottomParams);
	// contentView.addView(unitView, unitParams);
	// }
	// }

	public boolean updateFloorStartNum(int floorStartNum)
	{
		int startNum = units[0].startNum;
		if (startNum == floorStartNum)
		{
			return false;
		}
		for (Unit unit : units)
		{
			unit.updateStartNum(floorStartNum);
		}
		return true;
	}

	public boolean updateEntranceSuf(DictObject entranceDict)
	{
		String dyh = units[0].dyh;
		if (entranceDict == null || entranceDict.getDM().equals(dyh))
		{
			return false;
		}
		for (int i = 0; i < units.length; i++)
		{
			units[i].dyh = DictUtil.nextDm(entranceDict.getDM(), i);
		}
		return true;
	}

	public boolean updateFloorSuf(DictObject floorDict)
	{
		String sufLch = units[0].floors[0].sufLch;
		if (floorDict == null || floorDict.getDM().equals(sufLch))
		{
			return false;
		}

		for (int i = 0; i < units.length; i++)
		{
			Unit u = units[i];
			for (int j = 0; j < u.floors.length; j++)
			{
				u.floors[j].sufLch = floorDict.getDM();
			}
		}
		return true;
	}

	public boolean updateRoomSuf(DictObject roomDict)
	{
		String sufSh = units[0].floors[0].rooms[0].sufSh;
		if (roomDict == null || roomDict.getDM().equals(sufSh))
		{
			return false;
		}
		for (int i = 0; i < units.length; i++)
		{
			Unit u = units[i];
			for (int j = 0; j < u.floors.length; j++)
			{
				Floor f = u.floors[j];
				for (int k = 0; k < f.rooms.length; k++)
				{
					f.rooms[k].sufSh = roomDict.getDM();
				}
			}
		}
		return true;
	}

	public boolean updateEntranceNum(int entranceNum)
	{
		int len = units.length;
		if (len == entranceNum)
		{
			return false;
		}
		units = Arrays.copyOf(units, entranceNum);
		if (entranceNum > len)
		{
			for (int i = len; i < entranceNum; i++)
			{
				units[i] = JSON.parseObject(JSON.toJSONString(units[i - 1]),
						Unit.class);
				units[i].dyh = DictUtil.nextDm(units[i].dyh, 1);
				// TODO 更新房间UUID
			}
		}
		return true;
	}

	@Override
	public void drawForm(LinearLayout contentView)
	{
		final Context context = contentView.getContext();
		DictUtil dictUtil = DictUtil.getInstance(context);
		contentView.removeAllViews();
		if (units != null && units.length > 0)
		{
			for (int i = 0; i < units.length; i++)
			{
				final Unit unit = units[i];
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						500, LayoutParams.MATCH_PARENT);
				params.rightMargin = 10;

				View entranceLinearLayout = View.inflate(
						contentView.getContext(), R.layout.entrance_item, null);
				contentView.addView(entranceLinearLayout, params);

				TextView tv_unit_name = (TextView) entranceLinearLayout
						.findViewById(R.id.tv_unit_name);
				Button bt_init_floor = (Button) entranceLinearLayout
						.findViewById(R.id.bt_init_floor);
				final LinearLayout ll_floors = (LinearLayout) entranceLinearLayout
						.findViewById(R.id.ll_floors);

				tv_unit_name.setText(dictUtil.getDictValue(DictUtil.DZ_DYH,
						unit.dyh));
				bt_init_floor.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						final EditText et_floor_num = new EditText(context);
						et_floor_num
								.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
										2) });
						et_floor_num.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
						new AlertDialog.Builder(context)
								.setTitle("重新设置楼层数")
								.setView(et_floor_num)
								.setNegativeButton("取消", null)
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(
													DialogInterface dialog,
													int which)
											{
												String trim = et_floor_num
														.getText().toString()
														.trim();
												if (!"".equals(trim)
														&& (!trim
																.startsWith("0")))
												{
													int parseInt = Integer
															.parseInt(trim);
													if (unit.updateFloorNum(parseInt))
													{
														unit.drawForm(ll_floors);
													}
												}
											}
										}).show();
					}
				});
				unit.drawForm(ll_floors);
			}
		}
	}

	@Override
	public String toString()
	{
		return "Building [baseBuilding=" + baseBuilding + ", units="
				+ Arrays.toString(units) + "]";
	}

	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof Building)
		{
			Building b = (Building) o;
			Unit[] us = b.units;
			if (us != null && us.length == units.length)
			{
				if (us[0].startNum == units[0].startNum)
				{
					for (int i = 0; i < units.length; i++)
					{
						Floor[] floors = us[i].floors;
						if (floors == null
								|| floors.length != units[i].floors.length)
						{
							return false;
						}
						for (int j = 0; j < floors.length; j++)
						{
							Room[] rooms = floors[j].rooms;
							if (rooms == null
									|| rooms.length != units[i].floors[j].rooms.length)
							{
								return false;
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	// 按从大到小的顺序排序
	@Override
	public int compareTo(Building another)
	{
		return another.baseBuilding.mphm.DJSJ.compareTo(baseBuilding.mphm.DJSJ);
	}
}