package com.golic.wycj.activity;

import java.util.ArrayList;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.golic.wycj.R;

/**
 * 数据分析模块，三种可能的错误类型分别是：公共数据名称与类型不一致、标准地址幢楼类型无门牌号、多个业务专用数据挂在一个公共数据上面（
 * 对公共数据进行修改重复关联，很少出现）
 */
@SuppressWarnings("deprecation")
public class DataAnalyseActivity extends BaseActivity
{
	private ViewPager pager;
	private ArrayList<View> listViews;
	private ImageView cursor;
	// private Button bt_point;
	// private Button bt_line;
	// private Button bt_polygon;
	public RadioGroup rg_tab;
	private RadioButton rb_name, rb_mph, rb_ywzy;

	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;

	// private int hasInit = 7;
	private LocalActivityManager manager = null;

	// MapElement mapElement;
	// Type type;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_analyse);
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		initData();
		initView();
	}

	private void initView()
	{
		pager = (ViewPager) findViewById(R.id.vp);
		cursor = (ImageView) findViewById(R.id.iv_cursor);

		rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
		rb_name = (RadioButton) findViewById(R.id.rb_name);
		rb_mph = (RadioButton) findViewById(R.id.rb_mph);
		rb_ywzy = (RadioButton) findViewById(R.id.rb_ywzy);

		rg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch (checkedId)
				{
				case R.id.rb_name:
					pager.setCurrentItem(0);
					rb_name.setTextColor(0xfff7a916);
					rb_mph.setTextColor(0xffffffff);
					rb_ywzy.setTextColor(0xffffffff);
					break;
				case R.id.rb_mph:
					pager.setCurrentItem(1);
					rb_name.setTextColor(0xffffffff);
					rb_mph.setTextColor(0xfff7a916);
					rb_ywzy.setTextColor(0xffffffff);
					break;
				case R.id.rb_ywzy:
					pager.setCurrentItem(2);
					rb_name.setTextColor(0xffffffff);
					rb_mph.setTextColor(0xffffffff);
					rb_ywzy.setTextColor(0xfff7a916);
					break;
				}
			}
		});

		MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);
		pager.setAdapter(mpAdapter);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置

		pager.setOnPageChangeListener(new MyOnPageChangeListener());
		pager.setCurrentItem(0);
		rb_name.setChecked(true);
	}

	private void initData()
	{
		listViews = new ArrayList<View>();
		Intent intent1 = new Intent(getApplicationContext(),
				NameMatchingErrorListActivity.class);
		listViews.add(getView("name_matching_error", intent1));

		Intent intent2 = new Intent(getApplicationContext(),
				NonMphBuildingActivity.class);
		listViews.add(getView("building_no_mph", intent2));

		Intent intent3 = new Intent(getApplicationContext(),
				YwzyNameMatchingErrorActivity.class);
		listViews.add(getView("more_than_one_ywzy", intent3));
	}

	public class MyPagerAdapter extends PagerAdapter
	{
		public ArrayList<View> mListViews;

		public MyPagerAdapter(ArrayList<View> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0)
		{
		}

		@Override
		public int getCount()
		{
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int position)
		{
			View view = mListViews.get(position);
			((ViewPager) arg0).addView(view, 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener
	{

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0)
		{
			Animation animation = null;
			switch (arg0)
			{
			case 0:
				rb_name.setChecked(true);
				if (currIndex == 1)
				{
					animation = new TranslateAnimation(one, 0, 0, 0);
					// System.out.println("case 0, one:" + one);
				}
				else if (currIndex == 2)
				{
					animation = new TranslateAnimation(two, 0, 0, 0);
					// System.out.println("case 0, two:" + two);
				}
				break;
			case 1:
				rb_mph.setChecked(true);
				if (currIndex == 0)
				{
					animation = new TranslateAnimation(offset, one, 0, 0);
					// System.out.println("case 1, offset to one:" + offset +
					// " "
					// + one);
				}
				else if (currIndex == 2)
				{
					animation = new TranslateAnimation(two, one, 0, 0);
					// System.out.println("case 1, two to one:" + two + " " +
					// one);
				}
				break;
			case 2:
				rb_ywzy.setChecked(true);
				if (currIndex == 0)
				{
					animation = new TranslateAnimation(offset, two, 0, 0);
					// System.out.println("case 2, offset to two:" + offset +
					// " "
					// + two);
				}
				else if (currIndex == 1)
				{
					animation = new TranslateAnimation(one, two, 0, 0);
					// System.out.println("case 2, one to two:" + one + " " +
					// two);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
		}
	}

	private View getView(String id, Intent intent)
	{
		return manager.startActivity(id, intent).getDecorView();
	}

	public void back(View view)
	{
		finish();
	}
	// @Override
	// public void onClick(View v)
	// {
	// int id = v.getId();
	// switch (id)
	// {
	// case R.id.bt_point:
	// pager.setCurrentItem(0);
	// // bt_point.
	// bt_point.setPressed(true);
	// break;
	// case R.id.bt_line:
	// pager.setCurrentItem(1);
	// bt_line.setPressed(true);
	// break;
	// case R.id.bt_polygon:
	// pager.setCurrentItem(2);
	// bt_polygon.setPressed(true);
	// break;
	// }
	// }
}