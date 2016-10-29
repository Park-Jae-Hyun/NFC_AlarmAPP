package kr.androidteam.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class selectednfc extends Activity {
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	public PowerManager.WakeLock mWakeLock;
	int time = 0;
	CountDownTimer timer;
	RandomMusic music;
	boolean flag = true;
	String number;
	String repeat;
	int location;
	String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		Intent intent = new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "selectednfc");
		mWakeLock.acquire();

		
		
		if (flag) {
			music = new RandomMusic(this);
			music.start();
		}
		/*
		SQLiteDatabase db = openOrCreateDatabase("nfc.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS NFC "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,nfc_Id TEXT, location TEXT);");
		String sql = "SELECT * FROM NFC;";
		Cursor c = db.rawQuery(sql,null);
		c.moveToFirst();
		
		int count=c.getCount();
		int num = (int)Math.random()%count;
		Toast.makeText(this, ""+count+" " + num, Toast.LENGTH_SHORT).show();
	
		sql = "SELECT location FROM NFC WHERE _id="+num+";";
		
		c = db.rawQuery(sql,null);
		c.moveToFirst();
		String loc = c.getString(1);
		
		Log.i("tag",count + " " + num + " " + loc);		//알람을 취소하기 위해선 intent로 보낸것을 전부 받아야 취소가 된다.
		*/
		location=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat"); //반복요일인지 아닌지 확인 하기위해
		timer();

		if (nfcAdapter == null)
			Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.",
					Toast.LENGTH_SHORT).show();

		if (!nfcAdapter.isEnabled()) {

			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setTitle("Info");
			alertbox.setMessage("NFC를 켜시겠습니까?");
			alertbox.setPositiveButton("Turn On",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_NFC_SETTINGS);

								startActivity(intent);
							} else {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
							}
						}
					});
			alertbox.setNegativeButton("Close",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			alertbox.show();
		}
		
		
		
		

	}

	public void loadDB() {

		SQLiteDatabase db = openOrCreateDatabase("nfc.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS NFC "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,nfc_Id TEXT, location TEXT);");

	}
	public void stop(){
		music.stop();
		timer.cancel();
		mWakeLock.release();
		Intent intent= new Intent(this,AlarmWeather.class);
		
		
		
		Log.i("tag","4");
		intent.putExtra("position", location);
		intent.putExtra("repeat", repeat);
		
		//아래를 안쓰면 알람이 울릴때마다 메인이 계속적으로 불려서 쌓인다.
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이전까지 쌓인 스택을 전부 없엔다.
		Log.i("tag","5");
		try{
		PendingIntent sender = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Log.i("tag","6");
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis(), sender);
		}catch(Exception e){}
		finish();
	}
public void timer(){
		
		timer= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              time++;
              
              if(time ==60){
            	  
            	  if(number!=null && number!="")
            	  SmsManager.getDefault().sendTextMessage(number, null,
            			  "깨워주세요!! 계속 자고 있어요!!", null,null);
              }
            }
    
            public void onFinish() {
               
            }
            
        };
			timer.start();
			
	}

	@Override
	protected void onPause() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
		Log.i("tag", "onPause");
		super.onPause();
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

		}
		Log.i("tag", "onResume");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			byte[] tagId = tag.getId();
			
			
			if (toHexString(tagId).equals("04B6063A873C84")){
				stop();
				Toast.makeText(this, "세수할 시간입니다.", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(this, "잘못된 NFC입니다.", Toast.LENGTH_SHORT).show();
			
		}
	}

	public static final String CHARS = "0123456789ABCDEF";

	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; ++i) {
			sb.append(CHARS.charAt((data[i] >> 4) & 0x0F)).append(
					CHARS.charAt(data[i] & 0x0F));
		}
		return sb.toString();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		flag=savedInstanceState.getBoolean("flag");
		time=savedInstanceState.getInt("time");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("flag",false);
		outState.putInt("time", time);
		
	}


	// 뒤로가기를 비활성화시킴
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
			Toast.makeText(this, "you don't use back button!!", 0).show();

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
