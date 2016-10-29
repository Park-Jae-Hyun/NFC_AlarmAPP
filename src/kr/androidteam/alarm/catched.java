package kr.androidteam.alarm;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class catched extends View {
	Context c;
	int width;
	int height;
	int randomX,randomY;
	Bitmap catched;
	Paint p; 
	int catchedX,catchedY;
	CountDownTimer timer,time;
	String repeat;
	int location;
	AudioManager audio;
	String message;
	String number;
	
	public catched(Context context){
		super(context);
		c=context;
		init();
		timer();
	}
	
	public catched(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		c=context;
		init();
		timer();
	}
	
	public void init(){	
		Display display = ((WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		catchedX=width/9;
		catchedY=width/9;
		p=new Paint();
		
		catched=BitmapFactory.decodeResource(c.getResources(), R.drawable.catchme);
		catched=Bitmap.createScaledBitmap(catched,catchedX,catchedY, true);
		
		audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		//켜질 시 볼륨이 최대화가 된 상태로 켜짐
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
	
		Intent intent=((selectedcatch)c).getIntent();
		
		location=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat"); //반복요일인지 아닌지 확인 하기위해
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
		
		randomLocation();
		thread();
		timer();
		
		//쓰레드바꾸기
			}
	public boolean onTouchEvent(MotionEvent event) { 
		float eventX=event.getX();
		float eventY=event.getY();
		
		switch(event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
		timer.cancel();
			break;
		
		case MotionEvent.ACTION_UP:
			
			if(randomX<=(int)eventX &&  (int)eventX<randomX+catchedX &&
			randomY <=(int) eventY && eventY<randomY+catchedY){
				timer.cancel();
				stop();
			}
			
			else{
				timer.start();
			}
			break;
		}
		
		return true;
	}

	public void randomLocation(){
		randomX=(int)(Math.random()*1000%width);
		randomY=(int)(Math.random()*1000%height);
		
	}

	public void thread(){
			
		timer= new CountDownTimer(60*60 * 1000, 600) {
            public void onTick(long millisUntilFinished) {
               randomLocation();
            	invalidate();
            }
    
            public void onFinish() {
               
            }
        };
			timer.start();
			
	}
	
public void timer(){
		
		time= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              ((selectedcatch)c).time++;
              
              if(((selectedcatch)c).time ==60){
            	  if(number!=null && number!=""){
            	  SmsManager.getDefault().sendTextMessage(number, null,
            			  "깨워주세요!! 계속 자고 있어요!!", null,null);
            	  }
              }
            }
    
            public void onFinish() {
               
            }
            
        };
			time.start();
			
	}

	protected void onDraw(Canvas canvas) {
		
			canvas.drawBitmap(catched, randomX, randomY,p);
		
	}
	
	public void stop(){
		((selectedcatch)c).music.stop();
		timer.cancel();
		time.cancel();
		((selectedcatch)c).mWakeLock.release();
		Intent intent= new Intent(c,AlarmWeather.class);
		
		intent.putExtra("position", location);
		
		intent.putExtra("repeat", repeat);
		
		//아래를 안쓰면 알람이 울릴때마다 메인이 계속적으로 불려서 쌓인다.
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이전까지 쌓인 스택을 전부 없엔다.
		
		
		PendingIntent sender = PendingIntent.getActivity(c,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis(), sender);
		
		((selectedcatch)c).finish();
	}
	
	//뒤로가기를 비활성화시킴
		@Override
		 public boolean  onKeyDown(int keyCode, KeyEvent event)
		 {
		  
		  if(keyCode == KeyEvent.KEYCODE_BACK)
		  {
			  
			  
		  }
		  return false;
		 }
		
}
