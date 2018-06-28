package com.golic.wycj.domain;

import java.io.Serializable;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.golic.wycj.R;
import com.golic.wycj.model.base.GraphicForm;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.NoticeUtil;

public class Unit implements GraphicForm, Serializable
{
	private static final long serialVersionUID = 785693505321156406L;
	public String dyh;
	public Floor[] floors;
	public int startNum;

	public void updateStartNum(int startNum)
	{
		if (this.startNum != 0 && (this.startNum != startNum))
		{
			if (startNum < 0)
			{
				for (int j = 0; j < floors.length; j++)
				{
					int lch = startNum + j;
					if (lch >= 0)
					{
						lch += 1;
					}
					String oldLch = floors[j].lch;
					floors[j].lch = "" + lch;
					if (floors[j].rooms != null)
					{
						for (Room room : floors[j].rooms)
						{
							String sh = room.sh.replaceFirst(oldLch,
									floors[j].lch);
							room.sh = sh;
						}
					}
				}
			}
			else
			{
				for (int j = 0; j < floors.length; j++)
				{
					int lch = startNum + j;
					String oldLch = floors[j].lch;
					floors[j].lch = "" + lch;
					if (floors[j].rooms != null)
					{
						for (Room room : floors[j].rooms)
						{
							String sh = room.sh.replaceFirst(oldLch,
									floors[j].lch);
							room.sh = sh;
						}
					}
				}
			}
		}
		this.startNum = startNum;
	}

	@Override
	public String toString()
	{
		if (floors != null)
		{
			return "Unit [" + dyh + ", 其中" + dyh + "单元所有的楼层是："
					+ Arrays.asList(floors) + "]";
		}
		return "Unit [" + dyh + ", 其中" + dyh + "楼层为空";
	}

	public boolean updateFloorNum(int floorNum)
	{
		int len = floors.length;
		if (floorNum == 0 || len == floorNum)
		{
			return false;
		}
		floors = Arrays.copyOf(floors, floorNum);
		if (floorNum > len)
		{
			for (int i = len; i < floorNum; i++)
			{
				floors[i] = JSON.parseObject(JSON.toJSONString(floors[i - 1]),
						Floor.class);
				String lchStr = floors[i].lch;
				int parseInt = Integer.parseInt(lchStr);
				parseInt += 1;
				if (parseInt == 0)
				{
					parseInt = 1;
				}
				floors[i].lch = "" + parseInt;
				if (floors[i].rooms != null)
				{
					for (Room room : floors[i].rooms)
					{
						String sh = room.sh.replaceFirst(lchStr, floors[i].lch);
						room.sh = sh;
					}
				}
				// TODO 更新房间UUID
			}
		}
		return true;
	}

	// public int getMaxRoomNum()
	// {
	// int num = 0;
	// if (floors == null)
	// {
	// return 0;
	// }
	// else
	// {
	// for (Floor f : floors)
	// {
	// if (num < f.getRooms().length)
	// {
	// num = f.getRooms().length;
	// }
	// }
	// return num;
	// }
	// }
	//
	// public int getRoomNum()
	// {
	// int num = 0;
	// if (floors == null)
	// {
	// return 0;
	// }
	// else
	// {
	// for (Floor f : floors)
	// {
	// if (f.getRooms() != null)
	// {
	// num += f.getRooms().length;
	// }
	// }
	// return num;
	// }
	// }

	@Override
	public void drawForm(LinearLayout contentView)
	{
		// DictUtil dictUtil = DictUtil.getInstance(contentView.getContext());

		contentView.removeAllViews();
		final Context context = contentView.getContext();
		if (floors == null || floors.length == 0)
		{
			return;
		}
		// String sufLch = dictUtil.getDictValue(DictUtil.DZ_LCHZ,
		// floors[0].getSufLch());
		for (int i = floors.length - 1; i >= 0; i--)
		{
			final Floor floor = floors[i];
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 150);
			View floorLinearLayout = View.inflate(context, R.layout.item_floor,
					null);
			contentView.addView(floorLinearLayout, params);
			TextView tv_floor_name = (TextView) floorLinearLayout
					.findViewById(R.id.tv_floor_name);
			Button bt_room_num = (Button) floorLinearLayout
					.findViewById(R.id.bt_room_num);
			final LinearLayout ll_rooms = (LinearLayout) floorLinearLayout
					.findViewById(R.id.ll_rooms);
			ll_rooms.setGravity(Gravity.CENTER);
			tv_floor_name.setText(floor.lch);
			bt_room_num.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					final EditText et_room_num = new EditText(context);
					et_room_num
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
									2) });
					et_room_num.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
					new AlertDialog.Builder(context)
							.setTitle("重新设置房间数")
							.setView(et_room_num)
							.setNegativeButton("取消", null)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(
												DialogInterface dialog,
												int which)
										{
											String trim = et_room_num.getText()
													.toString().trim();
											if (!"".equals(trim))
											{
												int parseInt = Integer
														.parseInt(trim);
												if (parseInt == 0)
												{
													NoticeUtil.showWarningDialog((Activity) context, "该楼层将被制空！");
												}
												String sufSh = floor.rooms[0].sufSh;
												if (parseInt > floor.rooms.length)
												{
													Room[] rooms = new Room[parseInt];
													floor.rooms = rooms;
													for (int k = 0; k < parseInt; k++)
													{
														rooms[k] = new Room();
														rooms[k].sh = floor.lch
																+ DictUtil
																		.nextDm("01",
																				k);
														rooms[k].sufSh = sufSh;
														// rooms[k].zzbh = UUID
														// .randomUUID()
														// .toString();
													}
												}
												else
												{
													floor.rooms = Arrays
															.copyOf(floor.rooms,
																	parseInt);
												}
												floor.drawForm(ll_rooms);
											}
										}
									}).show();
				}
			});
			floor.drawForm(ll_rooms);
		}
	}

}