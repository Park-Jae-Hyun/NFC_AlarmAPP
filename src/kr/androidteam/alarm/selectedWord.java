package kr.androidteam.alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class selectedWord extends Activity {

	TextView text;
	String repeat;
	String message;
	String number;
	int position;
	AudioManager audio;
	private static PowerManager.WakeLock mWakeLock;
	RandomMusic music;
	String[] matchWords = {"일어나","세종대왕","밥 먹자","학교 가자","어플리케이션"};
	String speak = ""; // matchWord는 미리 지정해놓은 변수, speak은 내가 직접 말한것을 음성인식하여 저장하는 변수.
	String matchWord;
	CountDownTimer timer;
	CountDownTimer count;
	int time=0;
	int cnt=0;
	boolean flag=true;
		@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.word);
		text = (TextView) findViewById(R.id.textLog);

		text.setText(""); // 값을 초기화함.
		
		if(flag){
			int number=(int)(Math.random()*5);
			
			matchWord=matchWords[number];
			Toast.makeText(this, matchWords.length+matchWord, 0).show();
		music=new RandomMusic(this);
		music.start();
		}
		
		Intent intent=getIntent();
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
		
		//알람을 취소하기 위해선 intent로 보낸것을 전부 받아야 취소가 된다.
		position=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat"); //반복요일인지 아닌지 확인 하기위해
		timer();
		
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		//켜질 시 볼륨이 최대화가 된 상태로 켜짐
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
		PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "selectedWord");
		mWakeLock.acquire();
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				music.pause();
				voice();
				cnt=0;
				if(count!=null)
				count.cancel();
				countDown();
				
			}

		});

	}
	
	public void voice()

	{
		// 인텐트를 만들고 액티비티를 시작한다.

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // 음성인식기를
						
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음악을 끄고싶으면" + "\n'"
				+ matchWord + "'" + "라고 말해주세요.");

		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); // 내가말한 단어의
																// 개수(ex. 5라고 치면
																// 5가지 비슷한 단어를
																// 보여줌.)

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		startActivityForResult(intent, 1);
		
	}
	
	public void countDown(){
		count= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              cnt++;
          	if(cnt==10){
    		
          	  music.reStart();
          	  
    		}
            }
    
            public void onFinish() {
               
            }
            
        };
			count.start();

	}
	
		@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)

	{

		if (requestCode == 1 && resultCode == RESULT_OK)

		{

			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (int i = 0; i < results.size(); i++) {

				text.setText(results.get(i));

				speak = results.get(i).toString().trim();

			}

			if (speak.equals(matchWord.trim())) { // 기존에 저장된 단어와 내가 말한 단어가 match하면 꺼줌.
				stop();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
		

	}
	
	public void stop(){
		music.stop();
		timer.cancel();
		count.cancel();
		mWakeLock.release();
		Intent intent= new Intent(this,AlarmWeather.class);
		
		intent.putExtra("position", position);
		intent.putExtra("repeat", repeat);
		//아래를 안쓰면 알람이 울릴때마다 메인이 계속적으로 불려서 쌓인다.
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이전까지 쌓인 스택을 전부 없엔다.
		
		PendingIntent sender = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis(), sender);
		finish();
	}

	@Override
	 public boolean  onKeyDown(int keyCode, KeyEvent event){
	  
	  if(keyCode == KeyEvent.KEYCODE_BACK)
		  Toast.makeText(this, "you don't use back button!!", 0).show();
		  
	  return false;
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

}
