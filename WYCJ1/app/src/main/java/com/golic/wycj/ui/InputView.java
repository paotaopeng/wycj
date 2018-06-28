package com.golic.wycj.ui;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.golic.wycj.R;
import com.golic.wycj.model.DictObject;
import com.golic.wycj.util.DictDialogUtil;
import com.golic.wycj.util.DictUtil;
import com.golic.wycj.util.EditTextCheckUtil;

public class InputView extends LinearLayout
{
	public static final int EDIT_TEXT = 0;
	public static final int ID_CARD = 1;
	public static final int NUMBER = 2;
	public static final int NUMBER_SIGNED = 3;
	public static final int NUMBER_DECIMAL = 4;
	public static final int PHONE = 5;
	public static final int WORD = 6;
	public static final int DICT = 7;
	public static final int LEVEL_DICT = 8;
	public static final int DATE = 9;
	public static final int TEXT = 10;

	private String dicName;
	private String title;
	private String digits;
	private String prefix;
	private String suffix;
	private String hint;
	private Field field;
	private boolean necessary;
	private boolean show;
	private boolean export;

	private int lines;
	private int len;
	private int type;

	private TextView titleView;
	private LinearLayout contentView;
	private TextView showView;

	private boolean isBusy;
	private static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(
			"yyyyMMdd", Locale.getDefault());

	public void fillDictInputView(DictUtil util, String key)
	{
		if (!TextUtils.isEmpty(key))
		{
			DictObject dictObject = util.getDictObject(dicName, key);
			setTag(dictObject);
			if (dictObject != null)
			{
				showView.setText(dictObject.getMC());
			}
		}
	}

	public TextView getTitleView()
	{
		return titleView;
	}

	public void setTitleView(TextView titleView)
	{
		this.titleView = titleView;
	}

	public InputView(Context context)
	{
		super(context);
	}

	public InputView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initAttrs(context, attrs);
		initView(context);
	}

	public String getDicName()
	{
		return dicName;
	}

	public TextView getShowView()
	{
		return showView;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isNecessary()
	{
		return necessary;
	}

	public void setNecessary(boolean necessary)
	{
		this.necessary = necessary;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public void setDicName(String dicName)
	{
		this.dicName = dicName;
	}

	public void setDigits(String digits)
	{
		this.digits = digits;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public void setHint(String hint)
	{
		this.hint = hint;
	}

	public void setShow(boolean show)
	{
		this.show = show;
	}

	public void setExport(boolean export)
	{
		this.export = export;
	}

	public void setLines(int lines)
	{
		this.lines = lines;
	}

	public void setLen(int len)
	{
		this.len = len;
	}

	public Field getField()
	{
		return field;
	}

	public void setField(Field field)
	{
		this.field = field;
	}

	private void initView(Context context)
	{
		// 左侧标题
		this.setPadding(
				0,
				(int) getResources().getDimension(
						R.dimen.smart_line_margin_vertical),
				0,
				(int) getResources().getDimension(
						R.dimen.smart_line_margin_vertical));
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		// params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		titleView = new TextView(context);
		// titleView.setBackgroundColor(Color.RED);
		LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 2);
		leftParams.gravity = Gravity.CENTER_VERTICAL;
		titleView.setGravity(Gravity.CENTER_VERTICAL);
		titleView.setTextSize(18);
		// Gravity.CENTER_HORIZONTAL
		if (title != null)
		{
			titleView.setText(title);
		}
		if (necessary)
		{
			titleView.setTextColor(Color.RED);
		}
		addView(titleView, leftParams);

		// 右侧内容控件
		contentView = createContentViewByType(context, type);
		// LayoutParams rightParams = new LayoutParams(0,
		// LayoutParams.WRAP_CONTENT, 5);
		// rightParams.gravity = Gravity.CENTER_VERTICAL;
		contentView.setGravity(Gravity.CENTER_VERTICAL);
		addView(contentView);
	}

	@SuppressWarnings("deprecation")
	public LinearLayout createContentViewByType(final Context context,
			final int type)
	{
		LinearLayout contentView = new LinearLayout(context);
		contentView.setPadding(
				0,
				(int) getResources().getDimension(
						R.dimen.smart_line_margin_vertical),
				0,
				(int) getResources().getDimension(
						R.dimen.smart_line_margin_vertical));
		int height = LayoutParams.WRAP_CONTENT;
		if (type != TEXT)
		{
			height = (int) (context.getResources().getDimension(
					R.dimen.smart_line_tv_height) * lines);
		}
		LayoutParams params = new LayoutParams(0, height);
		params.setMargins(5, 10, 5, 10);
		// params.topMargin = 10;
		params.weight = 5;
		params.gravity = Gravity.CENTER_VERTICAL;
		contentView.setLayoutParams(params);
		// android.view.ViewGroup.LayoutParams layoutParams = contentView
		// .getLayoutParams();
		// layoutParams.width = 0;

		if (prefix != null && (!"".equals(prefix)))
		{
			TextView preTv = new TextView(context);
			LayoutParams preParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			preParams.gravity = Gravity.CENTER_VERTICAL;
			contentView.addView(preTv, preParams);
		}
		LayoutParams midParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// midParams.gravity = Gravity.CENTER_VERTICAL;
		midParams.gravity = Gravity.CENTER;
		final TextView midContent;
		if (type < 7)
		{
			midContent = new EditText(context);
			midContent.setTextSize(16);
			midContent.setFocusableInTouchMode(true);
			midContent.setBackgroundDrawable(null);
			if (digits != null && (!"".equals(digits)))
			{
				setDigits((EditText) midContent, digits);
			}
			if (len > 0)
			{
				midContent
						.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								len) });
			}
			switch (type)
			{
			case ID_CARD:
				if (len == 0)
				{
					midContent
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
									18) });
				}

				midContent.setKeyListener(new NumberKeyListener()
				{
					@Override
					public int getInputType()
					{
						return InputType.TYPE_CLASS_TEXT;
					}

					@Override
					protected char[] getAcceptedChars()
					{
						char[] chars = { '0', '1', '2', '3', '4', '5', '6',
								'7', '8', '9', 'X', 'x' };

						return chars;
					}
				});
				EditTextCheckUtil.addIdCardCheck((EditText) midContent);
				break;
			case NUMBER:
				midContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
				break;
			case NUMBER_SIGNED:
				midContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER
						| EditorInfo.TYPE_NUMBER_FLAG_SIGNED);
				break;
			case NUMBER_DECIMAL:
				midContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER
						| EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
				break;
			case PHONE:
				if (len == 0)
				{
					midContent
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
									11) });
				}
				midContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				EditTextCheckUtil.addPhoneCheck((EditText) midContent);
				break;
			case WORD:
				midContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
				break;
			}
			contentView.addView(midContent, midParams);
		}
		else
		{
			midContent = new TextView(context);
			midContent.setTextSize(16);
			midContent.setGravity(Gravity.CENTER_VERTICAL);
			midContent.setLeft(50);
			if (type < 9)
			{
				midContent.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.dict_select_icon, 0);
			}
			if (type < 10)
			{
				contentView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (isBusy)
						{
							return;
						}
						AlertDialog dialog;
						switch (type)
						{
						case DICT:
							// TODO 单选字典
							dialog = DictDialogUtil.showSingleChooseDialog(
									context, dicName, InputView.this);
							dialog.setOnDismissListener(dialogDismissListener);
							break;
						case LEVEL_DICT:
							// TODO 层级字典
							dialog = DictDialogUtil.showLevelChooseDialog(
									context, dicName, InputView.this);
							dialog.setOnDismissListener(dialogDismissListener);
							break;
						case DATE:
							showDataPicker(midContent);
							break;
						}
						isBusy = true;
					}
				});
			}
			contentView.addView(midContent, midParams);
		}
		if (type < 10)
		{
			contentView.setBackgroundResource(R.drawable.input_view);
			// contentView.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.input_view));
		}

		if (hint != null && (!"".equals(hint)))
		{
			midContent.setHint(hint);
		}

		if (suffix != null && (!"".equals(suffix)))
		{
			TextView sufTv = new TextView(context);
			LinearLayout.LayoutParams sufParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1);
			sufParams.gravity = Gravity.CENTER_VERTICAL;
			contentView.addView(sufTv, sufParams);
		}
		this.showView = midContent;
		return contentView;
	}

	/**
	 * 最好只对输入类型为全文本的控件提供输入限制(其余的已经有特定限制了)
	 * 
	 * @param et
	 *            需要设置限制的输入控件
	 * @param digits
	 *            限制的字符序列
	 */
	private void setDigits(EditText et, final String digits)
	{
		et.setKeyListener(new NumberKeyListener()
		{
			@Override
			public int getInputType()
			{
				return InputType.TYPE_CLASS_TEXT;
			}

			@Override
			protected char[] getAcceptedChars()
			{
				char[] chars = digits.toCharArray();
				return chars;
			}
		});
	}

	private void initAttrs(Context context, AttributeSet attrs)
	{
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.InputView);// TypedArray是一个数组容器
		title = a.getString(R.styleable.InputView_title);
		dicName = a.getString(R.styleable.InputView_dict_name);
		digits = a.getString(R.styleable.InputView_digits);
		prefix = a.getString(R.styleable.InputView_prefix);
		suffix = a.getString(R.styleable.InputView_suffix);
		hint = a.getString(R.styleable.InputView_hint);
		necessary = a.getBoolean(R.styleable.InputView_necessary, false);
		show = a.getBoolean(R.styleable.InputView_show, true);
		export = a.getBoolean(R.styleable.InputView_export, true);
		lines = a.getInteger(R.styleable.InputView_lines, 1);
		len = a.getInteger(R.styleable.InputView_len, 0);
		type = a.getInteger(R.styleable.InputView_type, EDIT_TEXT);
		a.recycle();
	}

	private void showDataPicker(final TextView datepickerEt)
	{
		isBusy = true;
		String timeStr = datepickerEt.getText().toString();
		final Calendar c = Calendar.getInstance();
		if (timeStr != null && !"".equals(timeStr))
		{
			Date time;
			try
			{
				time = SDF_YMD.parse(timeStr);
				c.setTime(time);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}

		DatePicker datePicker = new DatePicker(getContext());
		datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH),
				new DatePicker.OnDateChangedListener()
				{
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth)
					{
						c.set(year, monthOfYear, dayOfMonth);
						view.init(year, monthOfYear, dayOfMonth, this);
					}
				});
		AlertDialog dialog = new AlertDialog.Builder(getContext())
				.setView(datePicker)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						datepickerEt.setText(SDF_YMD.format(new Date(c
								.getTimeInMillis())));
					}
				}).setNeutralButton("置空", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						datepickerEt.setText("");
					}
				}).setNegativeButton("取消", null).show();
		dialog.setOnDismissListener(dialogDismissListener);
	}

	DialogInterface.OnDismissListener dialogDismissListener = new DialogInterface.OnDismissListener()
	{
		@Override
		public void onDismiss(DialogInterface dialog)
		{
			isBusy = false;
		}
	};

	public String check()
	{
		String message = null;
		if (necessary)
		{
			String content = showView.getText().toString().trim();
			if ("".equals(content))
			{
				return title + "不能为空";
			}
			switch (type)
			{
			case ID_CARD:
				if (content.length() != 18)
				{
					message = title + "格式不正确";
				}
				else if (content.length() == 18)
				{
					if (!EditTextCheckUtil.checkCode(content))
					{
						message = title + "不存在";
					}
				}
				break;
			case PHONE:
				if (content.length() != 11)
				{
					message = title + "格式不正确";
				}
				else if (content.length() == 11)
				{
					if (!EditTextCheckUtil.checkPhone(content))
					{
						message = title + "不存在";
					}
				}
				break;
			}
		}
		return message;
	}
}