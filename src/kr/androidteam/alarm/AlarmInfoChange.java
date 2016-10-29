package kr.androidteam.alarm;

import java.util.ArrayList;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AlarmInfoChange extends Activity implements OnClickListener,
		OnCheckedChangeListener,
		android.widget.RadioGroup.OnCheckedChangeListener {

	ArrayList<String> timeTest = new ArrayList<String>();

	String string;
	Button btn2, btn3;
	Button btn4;
	Bundle bundle;
	AlarmInfo change;
	String[] timeArray = null;
	Intent it;
	String day;
	ToggleButton mon, tue, wed, thu, fri, sat, sun;
	String[] days = new String[7]; // 보내진 요일을 확인받고 체크된 요일을 다시 저장함
	RadioGroup rg;
	RadioButton game, word, catched, nfc;
	String option;
	ImageButton sms, Memo;
	String number, memo;
	boolean[] check = new boolean[2];
	LayoutInflater inflater;
	kr.androidteam.alarm.dragAndDrap drag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarminfochange);
		btn2 = (Button) findViewById(R.id.insert);
		btn3 = (Button) findViewById(R.id.cancel);
		btn4 = (Button) findViewById(R.id.delete);

		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);

		mon = (ToggleButton) findViewById(R.id.mon);
		thu = (ToggleButton) findViewById(R.id.thu);
		wed = (ToggleButton) findViewById(R.id.wed);
		tue = (ToggleButton) findViewById(R.id.tue);
		fri = (ToggleButton) findViewById(R.id.fri);
		sat = (ToggleButton) findViewById(R.id.sat);
		sun = (ToggleButton) findViewById(R.id.sun);

		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		game = (RadioButton) findViewById(R.id.game);
		word = (RadioButton) findViewById(R.id.word);
		catched = (RadioButton) findViewById(R.id.catchme);
		nfc = (RadioButton) findViewById(R.id.nfc);
		rg.setOnCheckedChangeListener(this);

		mon.setOnCheckedChangeListener(this);
		thu.setOnCheckedChangeListener(this);
		wed.setOnCheckedChangeListener(this);
		tue.setOnCheckedChangeListener(this);
		fri.setOnCheckedChangeListener(this);
		sat.setOnCheckedChangeListener(this);
		sun.setOnCheckedChangeListener(this);

		/**
		 * 메인에서 보내온 알람정보를 parelable로 된 객체를 받음
		 * 
		 * 보내진것의 역순으로 풀면됨. 메인에서 보내진 택배인 intent를 받고 번들을 푼다음 보내진 a를 받음
		 */

		for (int i = 0; i < 7; i++)
			// 요일을 0으로 초기화
			days[i] = "0";

		Intent intent = getIntent();
		bundle = intent.getExtras();
		change = bundle.getParcelable("change");
		timeArray = change.getReceive().split(":");
		option = change.getOption();
		day = change.getDay();
		number = change.getNumber();
		memo = change.getMessage();
		String checkBool = change.getCheck();

		drag = (kr.androidteam.alarm.dragAndDrap) findViewById(R.id.drag);
		drag.setStart(timeArray[0], timeArray[1]);

		if (option.equals("word"))
			word.setChecked(true);
		else if (option.equals("game"))
			game.setChecked(true);
		else if (option.equals("catch"))
			catched.setChecked(true);
		else
			nfc.setChecked(true);

		if (checkBool.length() == 1)

			for (int i = 0; i < checkBool.length(); i++) {

				if (checkBool.charAt(0) == '1') {
					check[0] = true;
					check[1] = false;
				}

				else {
					check[0] = false;
					check[1] = true;
				}

			}

		else if (checkBool.length() == 2) {
			check[0] = check[1] = true;
		}

		else
			check[0] = check[1] = false;

		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		sms = (ImageButton) findViewById(R.id.sms);

		if (check[0]) {
			sms.setClickable(true);

			sms.setSelected(true);
		} else
			sms.setSelected(false);

		sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sms.isClickable()) {
					showDialog(1);
					check[0] = true;

					sms.setSelected(true);
				}

				else {
					number = "";
					check[0] = false;

					sms.setSelected(false);
				}
			}

		});

		Memo = (ImageButton) findViewById(R.id.memo);

		if (check[1]) {
			Memo.setClickable(true);
			Memo.setSelected(true);
		}

		else
			Memo.setSelected(false);

		Memo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Memo.isClickable()) {
					showDialog(2);
					check[1] = true;
					Memo.setSelected(true);
				} else {
					memo = "";
					check[1] = false;
					Memo.setSelected(false);

				}

			}

		});

		if (day != "")
			for (int i = 0; i < day.length(); i++) {
				if (1 == Integer.parseInt("" + day.charAt(i))) {
					sun.setChecked(true);
					days[0] = "1";
					sun.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (2 == Integer.parseInt("" + day.charAt(i))) {
					mon.setChecked(true);
					days[1] = "2";
					mon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (3 == Integer.parseInt("" + day.charAt(i))) {
					tue.setChecked(true);
					days[2] = "3";
					tue.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (4 == Integer.parseInt("" + day.charAt(i))) {
					wed.setChecked(true);
					days[3] = "4";
					wed.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (5 == Integer.parseInt("" + day.charAt(i))) {
					thu.setChecked(true);
					days[4] = "5";
					thu.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (6 == Integer.parseInt("" + day.charAt(i))) {
					fri.setChecked(true);
					days[5] = "6";
					fri.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else if (7 == Integer.parseInt("" + day.charAt(i))) {
					sat.setChecked(true);
					days[6] = "7";
					sat.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				}
			}

		it = new Intent(this, MainActivity.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.sendmsg, null);

			return new AlertDialog.Builder(AlarmInfoChange.this)
					.setTitle("문자전송")
					.setView(linear)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									number = phoneNumber.getText().toString();

									if (number != "")
										phoneNumber.setText(number);

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									sms.setSelected(false);
									check[0] = false;

									number = "";
									phoneNumber.setText(number);
								}
							}).create();
		}

		else if (id == 2) {
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.memo, null);

			return new AlertDialog.Builder(AlarmInfoChange.this)
					.setTitle("메모작성")
					.setView(linear)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear
											.findViewById(R.id.memo);
									memo = userMemo.getText().toString();

									if (memo != "")
										userMemo.setText(memo);
									Memo.setSelected(true);

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear
											.findViewById(R.id.memo);
									check[1] = false;

									memo = "";
									userMemo.setText(memo);
									Memo.setSelected(false);
								}
							}).create();
		}

		return null;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == btn2) {

			kr.androidteam.alarm.dragAndDrap drap = (kr.androidteam.alarm.dragAndDrap) findViewById(R.id.drag);
			if (drap.getTime() != "") {
				string = drap.getTime();

			}

			if (string == null)
				Toast.makeText(this, "Please set a Time", Toast.LENGTH_SHORT)
						.show();

			else if (!game.isChecked() && !word.isChecked()
					&& !catched.isChecked() && !nfc.isChecked())
				Toast.makeText(this, "Please check a option",
						Toast.LENGTH_SHORT).show();

			else {

				/**
				 * 보낼때와마찬가지로 alarmInfo를 거쳐야 parcelable이 되기 때문에 alarmInfo에 먼저 보낸
				 * 후 번들로 감싸서 보냄
				 * 
				 */

				if (game.isChecked())
					option = "game";
				else if (word.isChecked())
					option = "word";
				else if (catched.isChecked())
					option = "catch";
				else
					option = "nfc";

				String sendCheck = "";
				StringBuffer sendCheckedDays = new StringBuffer("");

				for (int i = 0; i < 7; i++)
					// 만약 체크가 된 것이면 스트링에 덧붙임
					if (!days[i].equals("0"))
						sendCheckedDays.append(days[i]);
				if (check[0])
					sendCheck += '1';
				if (check[1])
					sendCheck += '2';

				change.setCheck(sendCheck);
				change.setDay(sendCheckedDays.toString());
				change.setTime(string);
				change.setReceive(string);
				change.setOption(option);
				change.setMessage(memo);
				change.setNumber(number);
				bundle.putParcelable("change", change);
				it.putExtras(bundle);
				setResult(Activity.RESULT_OK, it);
				finish();
			}

		}

		else if (v == btn3) {

			bundle.putParcelable("change", change);
			it.putExtras(bundle);

			setResult(Activity.RESULT_CANCELED, it);
			finish(); // 해당 엑티비티종료

		}

		else if (v == btn4) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Do you want to delete it ?");
			builder.setPositiveButton("Accept",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							change.delete = 1;
							bundle.putParcelable("change", change);
							it.putExtras(bundle);
							setResult(Activity.RESULT_OK, it);
							finish();

						}
					});

			builder.setNegativeButton("Cancel", null);

			/**
			 * 리스트 뷰를 지울 때 어레이리스트에 있는 항목을 지운 다음 listview가 아닌 adapter를 최신화 시켜줘야함
			 */
			builder.show();

		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if (sun.isChecked()) {
			days[0] = "1";
			sun.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[0] = "0";
			sun.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (mon.isChecked()) {
			days[1] = "2";
			mon.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[2] = "0";
			mon.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (tue.isChecked()) {
			days[2] = "3";
			tue.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[2] = "0";
			tue.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (wed.isChecked()) {
			days[3] = "4";
			wed.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[3] = "0";
			wed.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (thu.isChecked()) {
			days[4] = "5";
			thu.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[4] = "0";
			thu.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (fri.isChecked()) {
			days[5] = "6";
			fri.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[5] = "0";
			fri.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}
		if (sat.isChecked()) {
			days[6] = "7";
			sat.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnon));
		} else {
			days[6] = "0";
			sat.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.togglebtnoff));
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) { // 각 버튼 눌렀을 때 계산을 함

		case R.id.game:
			word.setChecked(false);
			game.setChecked(true);
			catched.setChecked(false);
			nfc.setChecked(false);
			break;

		case R.id.word:
			game.setChecked(false);
			word.setChecked(true);
			catched.setChecked(false);
			nfc.setChecked(false);
			break;

		case R.id.catchme:
			word.setChecked(false);
			game.setChecked(false);
			catched.setChecked(true);
			nfc.setChecked(false);
			break;
		case R.id.nfc:
			word.setChecked(false);
			game.setChecked(false);
			catched.setChecked(false);
			nfc.setChecked(true);
			break;

		}

	}

}