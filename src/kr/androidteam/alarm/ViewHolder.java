package kr.androidteam.alarm;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//����Ʈ���� ���̾ƿ��� framelayout�̿��� extends�� �����ӷ��̾ƿ��� �޾���
//���Ŀ� ����Ƽ��� �ٲܰ���
public class ViewHolder extends RelativeLayout implements
		OnCheckedChangeListener {

	private View childView;
	private AlarmInfo item;
	private CheckBox check;
	private ImageView sms, memo, function;
	private TextView text, text1;
	private RelativeLayout frame;
	boolean[] checkbox = new boolean[2];
	Context context;
	int position;
	LayoutParams fl;

	public ViewHolder(Context context) {
		// TODO Auto-generated constructor stub

		super(context);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// ���̾ƿ��� �������ֱ����� ����

		childView = inflater.inflate(R.layout.alarm_list, null);
		// view�� ���̾ƿ��� ��������.
		fl = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// ���̾ƿ��� �⺻�Ӽ��� �ٽ� ����

		addView(childView, fl);
		// �ٲ� ���̾ƿ��� ��������

		text = (TextView) findViewById(R.id.text1);
		text1 = (TextView) findViewById(R.id.text2);
		check = (CheckBox) findViewById(R.id.btn1);
		frame = (RelativeLayout) findViewById(R.id.frame);
		sms = (ImageView) findViewById(R.id.message);
		memo = (ImageView) findViewById(R.id.memo);
		function = (ImageView) findViewById(R.id.function);
	}

	public void setMessage(AlarmInfo msg, int position) {
		check.setFocusable(false);
		item = msg;
		this.position = position;
		String time = item.getTime();
		SpannableStringBuilder day = splitDay(item.getDay());
		Log.i("1", "2");
		setFunctions(item);

		if (item.getActive() == 0) {
			check.setChecked(false);
			frame.setBackgroundResource(R.color.gray);

			check.setButtonDrawable(R.drawable.pushsun);
			sms.setImageResource(R.drawable.offmessage);
			memo.setImageResource(R.drawable.offmemo);
			if (item.getOption().equals("word"))
				function.setImageResource(R.drawable.offshowlip);

			else if (item.getOption().equals("nfc"))
				function.setImageResource(R.drawable.offshownfc);

			else if (item.getOption().equals("game"))
				function.setImageResource(R.drawable.offshowgame);

			else
				function.setImageResource(R.drawable.offshowcatchme);

		}

		else {
			check.setChecked(true);
			frame.setBackgroundResource(R.color.white);
			check.setButtonDrawable(R.drawable.sun);
			sms.setImageResource(R.drawable.message);
			memo.setImageResource(R.drawable.memo);
			if (item.getOption().equals("word"))
				function.setImageResource(R.drawable.showagari);

			else if (item.getOption().equals("nfc"))
				function.setImageResource(R.drawable.shownfc);

			else if (item.getOption().equals("game"))
				function.setImageResource(R.drawable.showgame);

			else
				function.setImageResource(R.drawable.showcatchme);

		}

		// ��ư�� ������ �� Ȱ��ȭ ��Ȱ��ȭ ���¸� ��Ÿ��
		check.setOnCheckedChangeListener(this);

		text.setText(time);
		text1.append(day);

	}

	// ȭ���� Ȱ��ȭ, ��Ȱ��ȭ�� ���� �̹����� �ٲ���.
	public void setFunctions(AlarmInfo item) {
		if (item.getCheck().length() == 1)

			for (int i = 0; i < item.getCheck().length(); i++) {

				if (item.getCheck().charAt(0) == '1') {
					checkbox[0] = true;
					checkbox[1] = false;
				}

				else {
					checkbox[0] = false;
					checkbox[1] = true;
				}

			}

		else if (item.getCheck().length() == 2) {
			checkbox[0] = checkbox[1] = true;
		}

		else
			checkbox[0] = checkbox[1] = false;

		if (checkbox[0])
			sms.setVisibility(View.VISIBLE);

		else
			sms.setVisibility(View.INVISIBLE);

		if (checkbox[1])
			memo.setVisibility(View.VISIBLE);

		else
			memo.setVisibility(View.INVISIBLE);

		if (item.getOption().equals("word"))
			function.setImageResource(R.drawable.showagari);

		else if (item.getOption().equals("nfc"))
			function.setImageResource(R.drawable.shownfc);

		else if (item.getOption().equals("game"))
			function.setImageResource(R.drawable.showgame);

		else
			function.setImageResource(R.drawable.showcatchme);

	}

	public SpannableStringBuilder splitDay(String numberedDays) {

		SpannableStringBuilder sp = new SpannableStringBuilder("��ȭ��������� ");

		for (int i = 0; i < numberedDays.length(); i++) {

			if (numberedDays.charAt(i) == '1') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '2') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '3') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '4') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '5') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '6') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (numberedDays.charAt(i) == '7') {
				sp.setSpan(new ForegroundColorSpan(Color.argb(255, 223, 0, 0)),
						5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return sp;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		if (buttonView.isChecked()) {
			item.setActive(1);
			check.setChecked(true);
			check.setButtonDrawable(R.drawable.sun);
			frame.setBackgroundResource(R.color.white);

			sms.setImageResource(R.drawable.message);
			memo.setImageResource(R.drawable.memo);
			if (item.getOption().equals("word"))
				function.setImageResource(R.drawable.showagari);

			else if (item.getOption().equals("nfc"))
				function.setImageResource(R.drawable.shownfc);

			else if (item.getOption().equals("game"))
				function.setImageResource(R.drawable.showgame);

			else
				function.setImageResource(R.drawable.showcatchme);

			((MainActivity) context).checkboxAlarmRegister(position);

		}

		else {
			item.setActive(0);
			check.setChecked(false);

			check.setButtonDrawable(R.drawable.pushsun);
			frame.setBackgroundResource(R.color.gray);
			sms.setImageResource(R.drawable.offmessage);
			memo.setImageResource(R.drawable.offmemo);
			if (item.getOption().equals("word"))
				function.setImageResource(R.drawable.offshowlip);

			else if (item.getOption().equals("nfc"))
				function.setImageResource(R.drawable.offshownfc);

			else if (item.getOption().equals("game"))
				function.setImageResource(R.drawable.offshowgame);

			else
				function.setImageResource(R.drawable.offshowcatchme);

			((MainActivity) context).setAlarmChecked();

		}
	}
}
