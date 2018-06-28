package com.golic.wycj.util;

import java.util.Timer;
import java.util.TimerTask;

import com.golic.wycj.Constans;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public class NoticeUtil
{
	private static final int NOTICE_ANI = 11;
	private static final int FINISH_ANI = 22;
	private static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			int what = msg.what;
			Object o = msg.obj;
			View view = (View) o;
			if (what == NOTICE_ANI)
			{
				int visiable = view.getVisibility();
				if (visiable == View.INVISIBLE)
				{
					view.setVisibility(View.VISIBLE);
				}
				else
				{
					view.setVisibility(View.INVISIBLE);
				}
			}
			else if (what == FINISH_ANI)
			{
				view.setVisibility(View.VISIBLE);
			}
		}
	};

	public static void showErrorDialog(final Activity activity, String errorMsg)
	{
		AlertDialog dialog = new Builder(activity).setTitle("出错了")
				.setMessage(errorMsg).setPositiveButton("确定", null).create();
		dialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				activity.sendBroadcast(new Intent(Constans.FINISH_ACTION));
				// 1：这里要关闭程序，否则程序未关闭可能导致BaseActivity中的hasError变量一直为true导致程序无法再次运行
				// 2:关闭程序需要延迟，否则可能某些activity还没有接收到退出广播（虽然这很难发生）
				new Thread()
				{
					public void run()
					{
						SystemClock.sleep(1000);
						System.exit(0);
					}
				}.start();
			}
		});
		dialog.show();
	}

	public static void showWarningDialog(final Activity activity,
			String warningMsg)
	{
		if (activity.getParent() != null)
		{
			new Builder(activity.getParent()).setTitle("提示")
					.setMessage(warningMsg).setPositiveButton("确定", null)
					.show();
		}
		else
		{
			new Builder(activity).setTitle("提示").setMessage(warningMsg)
					.setPositiveButton("确定", null).show();
		}
	}

	public static void showNoticeAni(final View view)
	{
		// final Message msg = Message.obtain();
		// msg.obj = view;
		final Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask()
		{
			int time = 0;

			@Override
			public void run()
			{
				if (time < 4)
				{
					Message msg = new Message();
					msg.what = NOTICE_ANI;
					msg.obj = view;
					handler.sendMessage(msg);
				}
				else
				{
					t.cancel();
				}
				time++;
			}
		}, 300, 300);
	}
}