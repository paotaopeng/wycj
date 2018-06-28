package com.golic.wycj;

import android.app.Application;

import com.golic.wycj.exception.MyExceptionHandler;

public class App extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
//		Thread.currentThread().setUncaughtExceptionHandler(
//				new MyExceptionHandler());
	}
}