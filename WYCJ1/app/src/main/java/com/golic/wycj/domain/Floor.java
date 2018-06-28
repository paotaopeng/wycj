package com.golic.wycj.domain;

import java.io.Serializable;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.golic.wycj.R;
import com.golic.wycj.model.base.GraphicForm;

public class Floor implements GraphicForm, Serializable
{
	private static final long serialVersionUID = 9003157308081813754L;
	public String lch;
	public String sufLch;
	public Room[] rooms;

//	// TODO 更改楼层号对应房间号所做的处理
//	public void setLch(String lch)
//	{
//		if (rooms != null)
//		{
//			for (Room room : rooms)
//			{
//				String sh = room.getSh().replaceFirst(this.lch, lch);
//				room.setSh(sh);
//			}
//		}
//		this.lch = lch;
//	}

	@Override
	public String toString()
	{
		if (rooms != null)
		{
			return "Floor [" + lch + "(" + sufLch + "), 其中" + lch + "楼的所有房间是："
					+ Arrays.asList(rooms) + "]";
		}
		return "Floor [" + lch + "(" + sufLch + "), 其中" + lch + "楼的房间为空";
	}

	@Override
	public void drawForm(LinearLayout contentView)
	{
		contentView.removeAllViews();
		Context context = contentView.getContext();
		if (rooms == null || rooms.length == 0)
		{
			return;
		}
		if (rooms.length < 6)
		{
			for (int i = 0; i < rooms.length; i++)
			{
				Room room = rooms[i];
				TextView roomItem = new TextView(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						70, 70);
				params.leftMargin = 5;
				params.rightMargin = 5;
				contentView.addView(roomItem, params);
				roomItem.setGravity(Gravity.CENTER);
				roomItem.setBackgroundResource(R.drawable.room_background);
				roomItem.setText(room.sh);
				roomItem.setTextColor(Color.WHITE);
			}
		}
		else
		{
			for (int i = 0; i < 3; i++)
			{
				Room room = rooms[i];
				TextView roomItem = new TextView(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						70, 70);
				params.leftMargin = 5;
				params.rightMargin = 5;
				contentView.addView(roomItem, params);
				roomItem.setGravity(Gravity.CENTER);
				roomItem.setBackgroundResource(R.drawable.room_background);
				roomItem.setText(room.sh);
				roomItem.setTextColor(Color.WHITE);
			}
			TextView ignoredItem = new TextView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					60, 70);
			params.leftMargin = 5;
			params.rightMargin = 5;
			contentView.addView(ignoredItem, params);
			ignoredItem.setGravity(Gravity.CENTER);
			ignoredItem.setText(". . .");
			ignoredItem.setTextColor(Color.WHITE);

			Room room = rooms[rooms.length - 1];
			LinearLayout.LayoutParams endParams = new LinearLayout.LayoutParams(
					70, 70);
			endParams.leftMargin = 5;
			endParams.rightMargin = 5;
			TextView roomItem = new TextView(context);
			contentView.addView(roomItem, endParams);
			roomItem.setGravity(Gravity.CENTER);
			roomItem.setBackgroundResource(R.drawable.room_background);
			roomItem.setText(room.sh);
			roomItem.setTextColor(Color.WHITE);
		}
	}
}