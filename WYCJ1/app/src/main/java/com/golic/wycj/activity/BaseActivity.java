package com.golic.wycj.activity;

import com.golic.wycj.Constans;
import com.golic.wycj.Source;
import com.golic.wycj.util.NoticeUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;

public class BaseActivity extends Activity
{
	static final int WAIT_FOR_RESULT = 11;
	static boolean hasError = false;
	static Class<?>[] classQueue = new Class[2];
	private static final IntentFilter FINISH_FILTER = new IntentFilter(
			Constans.FINISH_ACTION);
	BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Source.clear();
			finish();
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		registerReceiver(receiver, FINISH_FILTER);
		checkError(savedInstanceState);
	};

	private void checkError(Bundle savedInstanceState)
	{
		if (hasError)
		{
			finish();
		}
		classQueue[0] = classQueue[1];
		classQueue[1] = getClass();
		if (classQueue[0] == null && classQueue[1] != null)
		{
			if (savedInstanceState != null
					&& classQueue[1] != SplashActivity.class)
			{
				hasError = true;
				// 说明程序挂了
				NoticeUtil.showErrorDialog(this, "程序出错了！");
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		Class<? extends BaseActivity> clazz = getClass();
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (clazz == MapActivity.class)
			{
				sendBroadcast(new Intent(Constans.FINISH_ACTION));
			}
			else if (clazz == LoginActivity.class)
			{
				sendBroadcast(new Intent(Constans.FINISH_ACTION));
			}
			return true;
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == WAIT_FOR_RESULT && resultCode == RESULT_OK)
		{
			setResult(RESULT_OK);
			finish();
		}
	}
}