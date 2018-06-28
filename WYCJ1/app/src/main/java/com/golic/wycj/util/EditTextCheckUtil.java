package com.golic.wycj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class EditTextCheckUtil
{

	/**
	 * NOTICE
	 * 在edittext中调用setError()的时候，有可能只弹出提示框但提示框内的文字看不见，这是由于系统主题和Error的文本颜色冲突
	 * （背景颜色导致无法显示），这个时候我们只要修改系统主题的文字颜色即可（将自定义主题加上<item
	 * name="android:textColorPrimaryInverse"
	 * >@android:color/primary_text_light</item> ）
	 */

	private static final String cityCode[] = { "11", "12", "13", "14", "15",
			"21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41",
			"42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61",
			"62", "63", "64", "65", "71", "81", "82", "91" };

	// 每位加权因子
	private static final int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9,
			10, 5, 8, 4, 2 };

	// 第18位校检码
	private static final String verifyCode[] = { "1", "0", "X", "9", "8", "7",
			"6", "5", "4", "3", "2" };

	final static Pattern cardNumPattern = Pattern.compile("^[0-9]*$");
	final static Pattern phonePattern = Pattern.compile("^[1][3-8]\\d{9}$");

	// final static Pattern phonePattern = Pattern
	// .compile("^1$|^1[3,5,8]$|^13[0-9]{1}$|15[0125689]{1}$|18[0-3,5-9]{1}$");

	public static void addPhoneCheck(final EditText et)
	{
		final Pattern pattern = Pattern.compile("^[1][3-8]\\d{9}$");
		// Matcher matcher;

		et.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				if ("".equals(s.toString()))
				{
					return;
				}
				int len = s.length();
				if (len < 11)
				{
					if (len >= 1)
					{
						if (len >= 2)
						{
							CharSequence cs = s.subSequence(1, 2);
							if (!"3578".contains(cs))
							{
								et.setError("手机号码不正确");
								return;
							}
						}
						CharSequence cs = s.subSequence(0, 1);
						if (!"1".equals(cs.toString()))
						{
							et.setError("手机号码不正确");
						}
					}
				}
				else if (len == 11)
				{
					if (!pattern.matcher(s).matches())
					{
						et.setError("手机号码不正确");
					}
				}
				else
				{
					et.setError("手机号码不正确");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{

			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
	}

	public static void addIdCardCheck(final EditText et)
	{
		et.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				int len = s.length();
				if (len < 18)
				{
					if (!cardNumPattern.matcher(s).matches())
					{
						et.setError("输入的身份证不合法");
						return;
					}
					if (len > 1)
					{
						CharSequence provinceId = s.subSequence(0, 2);
						if (!checkProvinceId(provinceId.toString()))
						{
							et.setError("输入的身份证不合法");
							return;
						}
						if (len == 7)
						{
							char preYear = s.charAt(6);
							if ((preYear != '1') && (preYear != '2'))
							{
								et.setError("输入的日期不对");
								return;
							}
						}
						if (len == 8)
						{
							char sufYear = s.charAt(7);
							if ((sufYear != '9') && (sufYear != '0'))
							{
								et.setError("输入的日期不对");
								return;
							}
						}
						if (len > 13)
						{
							CharSequence birthday = s.subSequence(6, 14);
							if (!checkBirthday(birthday.toString()))
							{
								et.setError("输入的身份证不合法");
								return;
							}
						}
					}
				}
				else if (len == 18)
				{
					CharSequence idcard = s.subSequence(0, 18);
					if (!checkCode(idcard.toString()))
					{
						et.setError("输入的身份证不合法");
						return;
					}
				}
				else
				{
					et.setError("输入的身份证不合法");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
	}

	public static void addIdCardCheck(final Button bt, final EditText et)
	{
		final Pattern numPattern = Pattern.compile("^[0-9]*$");
		et.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				if (!"居民身份证".equals(bt.getText()))
				{
					return;
				}

				if ("".equals(s.toString()))
				{
					return;
				}
				CharSequence cs = s;
				if (s.length() < 18)
				{
					if (!numPattern.matcher(cs).matches())
					{
						et.setError("输入的身份证不合法");
						return;
					}
					if (s.length() > 1)
					{
						CharSequence provinceId = s.subSequence(0, 2);
						if (!checkProvinceId(provinceId.toString()))
						{
							et.setError("输入的身份证不合法");
							return;
						}
						if (s.length() > 13)
						{
							CharSequence birthday = s.subSequence(6, 14);
							if (!checkBirthday(birthday.toString()))
							{
								et.setError("输入的身份证不合法");
								return;
							}
						}
					}
				}
				else if (s.length() == 18)
				{
					CharSequence idcard = s.subSequence(0, 18);
					if (!checkCode(idcard.toString()))
					{
						et.setError("输入的身份证不合法");
						return;
					}
				}
				else
				{
					et.setError("输入的身份证不合法");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
	}

	private static boolean checkProvinceId(String provinceId)
	{
		boolean flag = false;
		for (String code : cityCode)
		{
			if (code.equals(provinceId))
			{
				flag = true;
				break;
			}
		}
		return flag;
	}

	private static boolean checkBirthday(String birthday)
	{
		boolean flag = true;
		Date birthdate = null;
		try
		{
			birthdate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
					.parse(birthday);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		if (birthdate == null || new Date().before(birthdate))
		{
			System.out.println("日期为null或者超前");
			return false;
		}
		GregorianCalendar curDay = new GregorianCalendar();
		int year = Integer.parseInt(birthday.substring(0, 4));
		int month = Integer.parseInt(birthday.substring(4, 6));
		int day = Integer.parseInt(birthday.substring(6, 8));

		if (year < 1900 || year > curDay.get(Calendar.YEAR))
		{
			System.out.println("年份不合法");
			return false;
		}
		// 判断是否为合法的月份
		if (month < 1 || month > 12)
		{
			System.out.println("月份不合法");
			return false;
		}

		curDay.setTime(birthdate);
		switch (month)
		{
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			flag = (day >= 1 && day <= 31);
			break;
		case 2: // 公历的2月非闰年有28天,闰年的2月是29天。
			if (curDay.isLeapYear(curDay.get(Calendar.YEAR)))
			{
				flag = (day >= 1 && day <= 29);
			}
			else
			{
				flag = (day >= 1 && day <= 28);
			}
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			flag = (day >= 1 && day <= 30);
			break;
		}
		return flag;
	}

	public static boolean checkPhone(String phoneNum)
	{
		return phonePattern.matcher(phoneNum).matches();
	}

	public static boolean checkCode(String idcard)
	{
		// 获取前17位
		String idcard17 = idcard.substring(0, 17);
		// 获取第18位
		String idcard18Code = idcard.substring(17, 18);
		char[] c = idcard17.toCharArray();
		int[] nums = converCharToInt(c);
		if (nums == null)
		{
			return false;
		}
		int sum17 = getPowerSum(nums);
		String checkCode = getCheckCodeBySum(sum17);
		if (idcard18Code.equalsIgnoreCase(checkCode))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private static int[] converCharToInt(char[] c) throws NumberFormatException
	{
		int[] a = new int[c.length];
		int k = 0;
		try
		{
			for (char temp : c)
			{
				a[k++] = Integer.parseInt(String.valueOf(temp));
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
			a = null;
		}
		return a;
	}

	private static int getPowerSum(int[] bit)
	{
		int sum = 0;

		if (power.length != bit.length)
		{
			return sum;
		}

		for (int i = 0; i < bit.length; i++)
		{
			for (int j = 0; j < power.length; j++)
			{
				if (i == j)
				{
					sum = sum + bit[i] * power[j];
				}
			}
		}
		return sum;
	}

	private static String getCheckCodeBySum(int sum17)
	{
		String checkCode = verifyCode[sum17 % 11];
		return checkCode;
		//
		//
		// switch (sum17 % 11)
		// {
		// case 10:
		// checkCode = "2";
		// break;
		// case 9:
		// checkCode = "3";
		// break;
		// case 8:
		// checkCode = "4";
		// break;
		// case 7:
		// checkCode = "5";
		// break;
		// case 6:
		// checkCode = "6";
		// break;
		// case 5:
		// checkCode = "7";
		// break;
		// case 4:
		// checkCode = "8";
		// break;
		// case 3:
		// checkCode = "9";
		// break;
		// case 2:
		// checkCode = "x";
		// break;
		// case 1:
		// checkCode = "0";
		// break;
		// case 0:
		// checkCode = "1";
		// break;
		// }
		// return checkCode;
	}
}
