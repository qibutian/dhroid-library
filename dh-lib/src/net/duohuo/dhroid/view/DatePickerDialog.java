package net.duohuo.dhroid.view;

import java.util.Calendar;

import net.duohuo.dhroid.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class DatePickerDialog extends AlertDialog {

	NumberPicker yearPicker, monthPicker, dayPicker;

	OnDialogCallBack dialogCallBack;

	public static final int CANCLE = 1;

	public static final int YES = 2;
	int minYear, minMonth, minDay, maxYear, maxMonth, maxDay;

	Button okB;

	View backviewV;

	long animduring = 250;

	int direction = 1;

	int currentYear;

	int currentMonth;

	int monthPickerVisibility, dayPickerVisibility;
	String title;
	TextView dialog_title;
	int year_newVal, value_year, month;

	public DatePickerDialog(Context context) {
		super(context);
	}

	public DatePickerDialog(Context context, String title) {
		super(context);
		this.title = title;
	}

	public void setName(String title) {
		this.title = title;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_picker_dialog);
		dialog_title = (TextView) findViewById(R.id.dialog_title);
		dialog_title.setText(title);

		Calendar a = Calendar.getInstance();
		currentYear = a.get(Calendar.YEAR);// 得到年

		currentMonth = a.get(Calendar.MONTH) + 1;
		backviewV = findViewById(R.id.backview);
		backviewV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		yearPicker = (NumberPicker) findViewById(R.id.year);
		yearPicker.setMinValue(minYear);
		yearPicker.setMaxValue(maxYear);
		yearPicker.setFocusableInTouchMode(true);
		yearPicker.setFocusable(true);
		yearPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				if (newVal == currentYear) {
					monthPicker.setMaxValue(currentMonth);

				} else {
					monthPicker.setMaxValue(12);
				}
				year_newVal = newVal;
				dayPicker.setMinValue(1);
				dayPicker.setMaxValue(getDay(year_newVal, month));
			}
		});
		value_year = yearPicker.getValue();
		monthPicker = (NumberPicker) findViewById(R.id.month);
		monthPicker.setMinValue(minMonth);
		monthPicker.setMaxValue(maxMonth);
		monthPicker.setFocusableInTouchMode(true);
		monthPicker.setFocusable(true);
		monthPicker.setVisibility(monthPickerVisibility);
		monthPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker numberPicker, int i, int i1) {
				month = i1;
				dayPicker.setMinValue(1);
				dayPicker.setMaxValue(getDay(year_newVal, month));
			}
		});

		dayPicker = (NumberPicker) findViewById(R.id.day);
		dayPicker.setMinValue(1);
		dayPicker.setMaxValue(31);
		dayPicker.setFocusableInTouchMode(true);
		dayPicker.setFocusable(true);
		dayPicker.setVisibility(dayPickerVisibility);

		okB = (Button) findViewById(R.id.ok);
		okB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialogCallBack != null) {
					if (monthPicker.getVisibility() == View.VISIBLE) {
						dialogCallBack.onSelectResult(yearPicker.getValue()
								+ "", monthPicker.getValue() + "",
								dayPicker.getValue() + "");
					} else {
						dialogCallBack.onSelectResult(yearPicker.getValue()
								+ "", "", "");
					}
				}
				dismiss();
			}
		});

		if (minYear == currentYear) {
			monthPicker.setMaxValue(currentMonth);
		}
	}

	private int getDay(int year, int mouth) {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year);
		// year年
		time.set(Calendar.MONTH, mouth - 1);
		// Calendar对象默认一月为0,month月
		int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);// 本月份的天数
		return day;
	}

	public void setMonthPickerVisibility(int Visibility) {
		this.monthPickerVisibility = Visibility;
	}

	public void setDayPickerVisibility(int Visibility) {

		this.dayPickerVisibility = Visibility;
	}

	@SuppressLint("NewApi")
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		// playAnim();
	}

	// public void playAnim()
	// {
	// View v = findViewById(R.id.container);
	// AnimatorSet animatorSet = new AnimatorSet();
	// ObjectAnimator ani2 =
	// ObjectAnimator.ofFloat(v,
	// "translationY",
	// direction * 200,
	// -direction * 50,
	// direction * 30,
	// -direction * 10,
	// 0).setDuration(animduring * 2);
	// ObjectAnimator ani1 = ObjectAnimator.ofFloat(v, "alpha", 0.2f,
	// 1).setDuration(animduring);
	// animatorSet.play(ani1).with(ani2);
	// animatorSet.start();
	// }

	public int getMinDay() {
		return minDay;
	}

	public void setMinDay(int minDay) {
		this.minDay = minDay;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(int minMonth) {
		this.minMonth = minMonth;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}

	public int getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(int maxMonth) {
		this.maxMonth = maxMonth;
	}

	public OnDialogCallBack getDialogCallBack() {
		return dialogCallBack;
	}

	public void setDialogCallBack(OnDialogCallBack dialogCallBack) {
		this.dialogCallBack = dialogCallBack;
	}

	public interface OnDialogCallBack {
		void onSelectResult(String year, String month, String day);
	}

}
