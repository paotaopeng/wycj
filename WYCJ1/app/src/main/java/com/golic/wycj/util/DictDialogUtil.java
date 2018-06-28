package com.golic.wycj.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.golic.wycj.R;
import com.golic.wycj.Source;
import com.golic.wycj.dao.GgsjDaoImpl;
import com.golic.wycj.dao.YwzyDaoImpl;
import com.golic.wycj.domain.BaseAttrs;
import com.golic.wycj.domain.Ywzy;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.model.MapLevelDictObject;
import com.golic.wycj.ui.InputView;
import com.golic.wycj.ui.adapter.DictListAdapterSingleChoose;
import com.golic.wycj.ui.adapter.YwzyListAdapte;

public class DictDialogUtil
{

	public interface MapDictCallback
	{
		void call(MapLevelDictObject mapLevelDictObject);
	}

	@SuppressWarnings("unchecked")
	public static void changeType(Activity context,
			final MapDictCallback callback)
	{

		final DictUtil instance = DictUtil.getInstance(context);
		final ArrayList<MapLevelDictObject> dicts = instance
				.getMapLevelChildren(null, DictUtil.GGSJ_DICT);

		View view = View.inflate(context, R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);
		LinearLayout fliter = (LinearLayout) view
				.findViewById(R.id.dialog_filter);
		final ArrayList<MapLevelDictObject> showDatas = (ArrayList<MapLevelDictObject>) dicts
				.clone();
		final DictListAdapterSingleChoose adapter = new DictListAdapterSingleChoose(
				context, showDatas);
		Builder builder = new AlertDialog.Builder(context)
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
					// tempType = Type.valueOf(mapLevelDictObject.getYwbm());
					// if (tempType != null)
					// {
					// ll_notice.setVisibility(View.VISIBLE);
					// im_type.setBackgroundResource(tempType.getSource());
					// NoticeUtil.showNoticeAni(ll_notice);
					// }
					callback.call(mapLevelDictObject);
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

	public interface YwzyChooseCallback
	{
		void call(Ywzy ywzy);

		void dismiss();
	}

	public static void sigleYwzyChoose(final Activity context,
			final ArrayList<Ywzy> ywzyList, final BaseAttrs attrs,
			final YwzyChooseCallback callback)
	{
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle("匹配业务专用数据").setView(view)
				.setIcon(R.drawable.down_icon).setNegativeButton("取消", null)
				.create();

		final YwzyListAdapte adapter = new YwzyListAdapte(context, ywzyList);
		dialogList.setAdapter(adapter);
		// 控制过滤框弹出的条件
		dialogList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Ywzy ywzy = ywzyList.get(position);
				ywzy.setBs(1);
				ywzy.setX(attrs.X);
				ywzy.setY(attrs.Y);
				ywzy.setGgsj_id(attrs.ID);
				callback.call(ywzy);
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				callback.dismiss();
			}
		});
		dialog.show();
	}

	public static void clearYwzyChoose(final Activity context,
			final ArrayList<Ywzy> ywzyList, final BaseAttrs attrs)
	{
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setTitle("解除关联").setView(view).setIcon(R.drawable.down_icon)
				.setNegativeButton("取消", null).create();
		final YwzyListAdapte adapter = new YwzyListAdapte(context, ywzyList);
		dialogList.setAdapter(adapter);
		// 控制过滤框弹出的条件
		dialogList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// 删除选中的业务专用数据
				Ywzy ywzy = ywzyList.get(position);
				ywzy.setBs(0);
				int update = new YwzyDaoImpl(context).update(ywzy);
				if (update > 0)
				{
					// 从内存中删除业务专用数据
					ArrayList<Ywzy> arrayList = Source.ywzys.get(ywzy.getName());
					Iterator<Ywzy> it = arrayList.iterator();
					while (it.hasNext())
					{
						Ywzy y = it.next();
						if (y.getId().equals(ywzy.getId()))
						{
							it.remove();
						}
					}
				}
				// 更新公共数据
				attrs.bs--;
				int updateGgsj = new GgsjDaoImpl(context).updateGgsj(attrs);
				if (updateGgsj > 0)
				{
					Source.updateGgsj(attrs);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@SuppressWarnings("unchecked")
	public static AlertDialog showSingleChooseDialog(Context context,
			String dictName, InputView inputView)
	{
		ArrayList<DictObject> dicts = (ArrayList<DictObject>) DictUtil
				.getInstance(context).queryDict(dictName);
		return showSingleChooseDialog(context, dicts, inputView);
	}

	@SuppressWarnings("unchecked")
	private static AlertDialog showSingleChooseDialog(Context context,
			final ArrayList<DictObject> dicts, final InputView inputView)
	{
		Builder builder = new AlertDialog.Builder(context).setTitle("字典选择")
				.setPositiveButton("置空", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						inputView.getShowView().setText("");
						inputView.setTag(null);
					}
				}).setIcon(R.drawable.down_icon).setNegativeButton("取消", null);

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);
		LinearLayout fliter = (LinearLayout) view
				.findViewById(R.id.dialog_filter);

		final ArrayList<DictObject> showDatas = (ArrayList<DictObject>) dicts
				.clone();
		final DictListAdapterSingleChoose adapter = new DictListAdapterSingleChoose(
				context, showDatas);
		dialogList.setAdapter(adapter);
		// 控制过滤框弹出的条件
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
					String fliter = fliterEt.getText().toString();
					showDatas.clear();
					showDatas.addAll(filterDicts(dicts, fliter));
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
		setSingleChoose(dialogList, dialog, inputView);
		dialog.show();
		return dialog;
	}

	public static AlertDialog showLevelChooseDialog(Context context,
			final String dictName, final InputView inputView)
	{
		final DictUtil util = DictUtil.getInstance(context);
		Builder builder = new AlertDialog.Builder(context).setTitle("字典选择")
				.setIcon(R.drawable.down_icon).setNegativeButton("取消", null);
		if (inputView.getTitle() != null)
		{
			builder.setPositiveButton("置空", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					inputView.getShowView().setText("");
					inputView.setTag(null);
				}
			});
		}
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_list_layout, null);
		ListView dialogList = (ListView) view.findViewById(R.id.dialog_list);
		LinearLayout fliter = (LinearLayout) view
				.findViewById(R.id.dialog_filter);
		final ArrayList<DictObject> dicts = util.getIntLevelDictObjects(
				dictName, null);
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
					dicts.clear();
					dicts.addAll(filterLevelDicts(dicts, filter));
				}
			});
		}
		else
		{
			fliter.setVisibility(View.GONE);
		}
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		final DictListAdapterSingleChoose adapter = new DictListAdapterSingleChoose(
				context, dicts);
		dialogList.setAdapter(adapter);
		dialogList.setOnItemClickListener(new OnItemClickListener()
		{
			boolean start = false;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				DictObject dict = dicts.get(position);
				ArrayList<DictObject> intLevelDictObjects = util
						.getIntLevelDictObjects(dictName, dict.getDM());
				if (intLevelDictObjects.size() == 0 || (position == 0 && start))
				{
					inputView.setTag(dict);
					TextView showView = inputView.getShowView();
					if (showView != null)
					{
						showView.setText(dict.getMC());
					}
					dialog.dismiss();
				}
				else
				{
					dicts.clear();
					dicts.addAll(util.getIntLevelDictObjects(dictName,
							dict.getDM()));
					adapter.notifyDataSetChanged();
				}
				start = true;
			}
		});
		dialog.show();
		return dialog;
	}

	private static void setSingleChoose(final ListView dialogList,
			final AlertDialog dialog, final InputView inputView)
	{
		dialogList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Object item = dialogList.getAdapter().getItem(position);
				if (item != null)
				{
					DictObject dic = (DictObject) item;
					inputView.getShowView().setText(dic.getMC());
					inputView.setTag(dic);
				}
				dialog.dismiss();
			}
		});
	}

	// private static void setIntLevelChoose(final Context context,final String
	// dictName,
	// final ArrayList<DictObject> showDatas, ListView dialogList,
	// final InputView inputView, final AlertDialog dialog)
	// {
	// final DictListAdapterSingleChoise adapter = new
	// DictListAdapterSingleChoise(
	// context, showDatas);
	// dialogList.setAdapter(adapter);
	// dialogList.setOnItemClickListener(new OnItemClickListener()
	// {
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id)
	// {
	// DictUtil
	// .getInstance(context).getIntLevelDictObjects(dictName, dm)
	// MapLevelDictObject mapLevelDictObject = showDatas.get(position);
	// ArrayList<MapLevelDictObject> levelChildren = DictUtil
	// .getInstance(context).getMapLevelChildren(
	// mapLevelDictObject, DictUtil.GGSJ_DICT);
	// if (levelChildren.size() == 0)
	// {
	// inputView.setTag(mapLevelDictObject);
	// TextView showView = inputView.getShowView();
	// if (showView != null)
	// {
	// showView.setText(mapLevelDictObject.getMC());
	// }
	// dialog.dismiss();
	// }
	// else
	// {
	// showDatas.clear();
	// showDatas.addAll(DictUtil.getInstance(context)
	// .getMapLevelChildren(mapLevelDictObject,
	// DictUtil.GGSJ_DICT));
	// adapter.notifyDataSetChanged();
	// }
	// }
	// });
	// }

	// private ArrayList<DictObject> cuteLeaf(ArrayList<MapLevelDictObject>
	// list)
	// {
	// ArrayList<DictObject> result = new ArrayList<DictObject>();
	// for (MapLevelDictObject obj : list)
	// {
	// result.add(new DictObject(obj.getDM(), obj.getMC()));
	// }
	// return result;
	// }

	public static ArrayList<DictObject> filterDicts(
			ArrayList<DictObject> dicts, String filter)
	{
		ArrayList<DictObject> result = new ArrayList<DictObject>();
		for (DictObject dic : dicts)
		{
			if (dic.getMC().contains(filter))
			{
				result.add(dic);
			}
		}
		return result;
	}

	public static ArrayList<? extends DictObject> filterLevelDicts(
			ArrayList<? extends DictObject> dicts, String filter)
	{
		ArrayList<DictObject> result = new ArrayList<DictObject>();
		for (DictObject dic : dicts)
		{
			if (dic.getMC().contains(filter))
			{
				result.add(dic);
			}
		}
		return result;
	}
}