package kr.androidteam.alarm;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class AlarmContents extends Activity implements OnClickListener,
		OnCheckedChangeListener,
		android.widget.RadioGroup.OnCheckedChangeListener {

	ArrayList<String> timeTest = new ArrayList<String>();
	String string, time;
	Button btn2, btn3, btn4;
	ToggleButton mon, tue, wed, thu, fri, sat, sun;
	String[] days = new String[7]; // 월~금까지 체크가 된것을 나타냄
	// 시간은 나중에 드래그로 표현할거니까 이해안해도됨
	RadioGroup rg;
	RadioButton game, word, catchMe, nfc;
	ImageButton sms, Memo;
	String number;
	String memo;
	boolean[] check = new boolean[2];
	final static int DIALOG_1 = 0;
	final static int DIALOG_2 = 1;

	LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_contents);

		check[0] = check[1] = false;

		for (int i = 0; i < 7; i++)
			// 초기에 스트링어레이를 0으로 초기화
			days[i] = "0";

		btn2 = (Button) findViewById(R.id.insert);
		btn3 = (Button) findViewById(R.id.cancel);
		//btn4 = (Button) findViewById(R.id.nfcenroll);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		//btn4.setOnClickListener(this);

		mon = (ToggleButton) findViewById(R.id.mon);
		thu = (ToggleButton) findViewById(R.id.thu);
		wed = (ToggleButton) findViewById(R.id.wed);
		tue = (ToggleButton) findViewById(R.id.tue);
		fri = (ToggleButton) findViewById(R.id.fri);
		sat = (ToggleButton) findViewById(R.id.sat);
		sun = (ToggleButton) findViewById(R.id.sun);

		mon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mon.isChecked()) {
					mon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					mon.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		tue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tue.isChecked()) {
					tue.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					tue.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});
		wed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (wed.isChecked()) {
					wed.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					wed.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		thu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thu.isChecked()) {
					thu.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					thu.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		fri.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fri.isChecked()) {
					fri.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					fri.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		sat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sat.isChecked()) {
					sat.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					sat.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		sun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sun.isChecked()) {
					sun.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnon));
				} else {
					sun.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.togglebtnoff));
				}

			}
		});

		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		game = (RadioButton) findViewById(R.id.game);
		word = (RadioButton) findViewById(R.id.word);
		catchMe = (RadioButton) findViewById(R.id.catchme);
		nfc = (RadioButton) findViewById(R.id.nfc);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		sms = (ImageButton) findViewById(R.id.sms);
		sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sms.isClickable()) {
					showDialog(DIALOG_1);
					check[0] = true;
					sms.setSelected(true);

				} else {
					number = "";
					check[0] = false;
					sms.setSelected(false);

				}

			}

		});

		Memo = (ImageButton) findViewById(R.id.memo);
		Memo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Memo.isClickable()) {
					showDialog(DIALOG_2);
					check[1] = true;
					Memo.setSelected(true);

				} else {
					memo = "";
					check[1] = false;
					Memo.setSelected(false);

				}

			}

		});

		rg.setOnCheckedChangeListener(this);

		mon.setOnCheckedChangeListener(this);
		thu.setOnCheckedChangeListener(this);
		wed.setOnCheckedChangeListener(this);
		tue.setOnCheckedChangeListener(this);
		fri.setOnCheckedChangeListener(this);
		sat.setOnCheckedChangeListener(this);
		sun.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_1:
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.sendmsg, null);

			return new AlertDialog.Builder(AlarmContents.this)
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
									sms.setSelected(true);

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText phoneNumber = (EditText) linear
											.findViewById(R.id.number);
									check[0] = false;

									number = "";
									phoneNumber.setText(number);
									sms.setSelected(false);
								}
							}).create();
		case DIALOG_2:
			final LinearLayout linear2 = (LinearLayout) inflater.inflate(
					R.layout.memo, null);

			return new AlertDialog.Builder(AlarmContents.this)
					.setTitle("메모작성")
					.setView(linear2)
					.setPositiveButton("Accept",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									EditText userMemo = (EditText) linear2
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
									EditText userMemo = (EditText) linear2
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

		// 추가버튼을 눌렀을 때 시간을 지정안했으면 지정하게 한 후 정했으면 바로 보냄
		if (v == btn2) {

			kr.androidteam.alarm.dragAndDrap drap = (kr.androidteam.alarm.dragAndDrap) findViewById(R.id.drag);
			if (drap.getTime() != "") {
				string = drap.getTime();
				time = drap.getTime();
			}

			String option = "";
			String sendCheck = "";
			if (string == null)
				Toast.makeText(this, "Please set a Time", Toast.LENGTH_SHORT)
						.show();

			else if (!game.isChecked() && !word.isChecked()
					&& !catchMe.isChecked() && !nfc.isChecked())
				Toast.makeText(this, "Please check a option",
						Toast.LENGTH_SHORT).show();

			else {

				StringBuffer sendCheckedDays = new StringBuffer("");
				// 스트링버퍼로 체크된 요일을 붙여나감

				for (int i = 0; i < 7; i++)
					if (!days[i].equals("0")) // 만약 체크가 된 것이면 스트링에 덧붙임
						sendCheckedDays.append(days[i]);

				if (game.isChecked())
					option = "game";
				else if (word.isChecked())
					option = "word";
				else if (catchMe.isChecked())
					option = "catch";
				else
					option = "nfc";

				if (check[0])
					sendCheck += '1';
				if (check[1])
					sendCheck += '2';

				Intent it = new Intent(this, MainActivity.class);

				it.putExtra("info",
						new AlarmInfo(string, time, sendCheckedDays.toString(),
								memo, number, sendCheck, option, 0, 1, 0));
				setResult(RESULT_OK, it);
				finish();
			}

		}

		// 취소눌렀으면 그냥 뒤로 돌아감
		else if (v == btn3) {
			Intent it = new Intent(this, MainActivity.class);
			setResult(RESULT_CANCELED, it);
			finish(); // 해당 엑티비티종료
		} /*else if (v == btn4) {
			Intent it = new Intent(this, NfcEnroll.class);
			startActivity(it);
			finish();

		}*/
	}

	/*
	 * 이 액티비티에서 뒤로가기를 누르면 바로 꺼짐 -> 뒤로가기버튼동작을 제어해야 함 이기능을 추가할까말까
	 */

	// 요일이 눌렸을때 눌린요일에 대한 숫자를 저장, 삭제함
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (sun.isChecked())
			days[0] = "1";

		else
			days[0] = "0";

		if (mon.isChecked())
			days[1] = "2";

		else
			days[2] = "0";

		if (tue.isChecked())
			days[2] = "3";

		else
			days[2] = "0";

		if (wed.isChecked())
			days[3] = "4";

		else
			days[3] = "0";

		if (thu.isChecked())
			days[4] = "5";

		else
			days[4] = "0";

		if (fri.isChecked())
			days[5] = "6";

		else
			days[5] = "0";

		if (sat.isChecked())
			days[6] = "7";

		else
			days[6] = "0";

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		switch (checkedId) { // 각 버튼 눌렀을 때 계산을 함

		case R.id.game:
			word.setChecked(false);
			game.setChecked(true);
			catchMe.setChecked(false);
			nfc.setChecked(false);
			break;

		case R.id.word:
			game.setChecked(false);
			word.setChecked(true);
			catchMe.setChecked(false);
			nfc.setChecked(false);
			break;

		case R.id.catchme:
			word.setChecked(false);
			game.setChecked(false);
			nfc.setChecked(false);
			catchMe.setChecked(true);
			break;
		case R.id.nfc:
			word.setChecked(false);
			game.setChecked(false);
			catchMe.setChecked(false);
			nfc.setChecked(true);
			break;

		}

	}

	@Override
	public void onStart() {
		Log.i("Act1", "onStart");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.i("Act1", "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i("Act1", "onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		Log.i("Act1", "onResume");
		super.onResume();
	}

	@Override
	public void onRestart() {
		Log.i("Act1", "onRestart");
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		Log.i("Act1", "onDestroy");
		super.onDestroy();
	}

}
