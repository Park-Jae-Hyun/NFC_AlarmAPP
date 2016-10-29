package kr.androidteam.alarm;

import java.util.Calendar;

import kr.androidteam.alarm.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class maze extends View {

	Context c;
	boolean[][] check = new boolean[8][7]; //전체 맵에대해 블럭인지 길인지 확인하는 불린 함수
	
	private Bitmap start;
	private Bitmap finish;
	private Bitmap block;
	private Bitmap line;
	
	Path path; //길
	Point point; //포인트

	Paint tracing; //선

	position[][] position = new position[8][7]; //맵에대한 전체 좌표를 가지고 있는 클래스어레이
	
	int width; //핸드폰 화면의 넓이
	int height;  //핸드폰 화면의 높이

	 // 블럭의 좌표를 담고있는 어레이함수들
	int[] blockX = new int[56];
	int[] blockY = new int[56];
	
	//갈수있는 최소한의 길을 확보하는 어레이함수들
	int[] pathX=new int[7*8];
	int[] pathY=new int[7*8];

	//도차지점의 좌표를 저장하는 함수
	int countX; 
	int countY;
	
	//시작할수있는 시작점인지아닌지 확인하는 불린함수
	boolean collect;
	boolean flag; // 게임이 끝났을 때 다시 시작하기위해 체크하는 함수
	
	String repeat;
	int location;
	int request;
	AudioManager audio;
	String message;
	String number;
	CountDownTimer timer;
	
	private static PowerManager.WakeLock mWakeLock;

	
	public void init() {
		flag=false;
		countX=0;
		countY=0;
		collect=true;
		Display display = ((WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight() - 16;

		path = new Path();

		tracing = new Paint();
		tracing.setDither(true);
		tracing.setColor(0xFFFF00FF);
		tracing.setStyle(Paint.Style.STROKE);
		tracing.setStrokeJoin(Paint.Join.ROUND);
		tracing.setStrokeCap(Paint.Cap.ROUND);
		tracing.setStrokeWidth(width / 100);

		point = new Point();

		//핸드폰 해상도에 맞게 비트맵을 맞츰
		Resources res = getResources();
		start = BitmapFactory.decodeResource(res, R.drawable.start);
		start = Bitmap.createScaledBitmap(start, width / 8, height / 8, true);

		finish = BitmapFactory.decodeResource(res, R.drawable.finish);
		finish = Bitmap.createScaledBitmap(finish, width / 8, height / 8, true);

		block = BitmapFactory.decodeResource(res, R.drawable.block);
		block = Bitmap.createScaledBitmap(block, width / 8, height / 8, true);

		line = BitmapFactory.decodeResource(res, R.drawable.path);
		line = Bitmap.createScaledBitmap(line, width / 8, height / 8, true);

		//좌표값들을 어레이에 저장한 후 초기 맵 전체를 false로 바꿈
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 7; j++) {
				position[i][j] = new position(width / 8 * i, height / 8 * j);
			check[i][j]=false;	
			}
		//출발지점을 true로 바꿔놓음
				check[0][0]=true;
				
		
		//블럭을 랜덤으로 만들기 전에 도착지점까지 갈수있는 최소한 1개의 길을 확보해놓음
		//0또는1이 랜덤으로 나와서 0일땐 x좌표로 1칸 1일땐 y좌표로 한칸 가게 만듬
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 7; j++) {

				if ((int) (Math.random() * 10) % 2 == 0) {

					if (countX == 7) {

						break;
					}

					pathX[i * 7 + j] = countX++;
					pathY[i * 7 + j] = countY;

				} else {

					if (countY == 6) {

						break;
					}

					else {
						pathX[i * 7 + j] = countX;
						pathY[i * 7 + j] = countY++;
					}
				}
				check[countX][countY] = true;

			}
		}
		

		//블럭을 랜덤한 좌표값으로 생성함
		for (int i = 0; i < 56; i++) {
			blockX[i] = (int) (Math.random() * 10) % 8;
			blockY[i] = (int) (Math.random() * 10) % 7;
			
			if(check[blockX[i]][blockY[i]]) //만약 위에서 확보한 길의 좌표이면 루프문을 다시돌림
				i--;
			else 
				check[blockX[i]][blockY[i]] = false;
		}

		audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		//켜질 시 볼륨이 최대화가 된 상태로 켜짐
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
	
		
		
		Intent intent=((selectedMaze)c).getIntent();
		
		location=intent.getIntExtra("position", 0);
		repeat=intent.getStringExtra("repeat"); //반복요일인지 아닌지 확인 하기위해
		request=intent.getIntExtra("request", 0);
		if(intent.getStringExtra("message")!=null)
			message=intent.getStringExtra("message");
		
		if(intent.getStringExtra("number")!=null)
			number=intent.getStringExtra("number");
	}

	public maze(Context c) {
		super(c);
		this.c = c;
		init();
		timer();
	}

	public maze(Context c, AttributeSet a) {
		super(c, a);
		this.c = c;
		init();
		timer();
	}
	
	public void timer(){
		
		timer= new CountDownTimer(60*60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
              ((selectedMaze)c).time++;
              
              if(((selectedMaze)c).time ==60){
            	  
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

	protected void onDraw(Canvas canvas) {
		Paint back=new Paint();
		back.setColor(Color.WHITE);
		canvas.drawPaint(back); //배경화면ㅇ을 흰색으로 씌움
		
		
		// 도착지점까지 확보한 길을 그림
		for(int i=0;i<pathX.length;i++)
			canvas.drawBitmap(line, position[pathX[i]][pathY[i]].getX(),
					position[pathX[i]][pathY[i]].getY(), null);
			
		
		//생성한 블럭을 전체 그림에 그림
				for (int i = 0; i < 56; i++) //
					canvas.drawBitmap(block, position[blockX[i]][blockY[i]].getX(),
							position[blockX[i]][blockY[i]].getY(), null);
				
		//시작점과 도착점을 그림
		canvas.drawBitmap(finish, position[countX][countY].getX(),
				position[countX][countY].getY(), null);

		canvas.drawBitmap(start, position[0][0].getX(), position[0][0].getY(),
				null);
		
				canvas.drawPath(path, tracing);
	}

	public boolean onTouchEvent(MotionEvent event) {

		float eventX = event.getX();
		float eventY = event.getY();
		
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			
			//출발지점에서 시작을 하지 않으면 에러를 띄움
			if (eventX >= position[1][0].getX()
					|| eventY >= position[0][1].getY()){
				
				collect=false;
			}
			
			//출발지점에서 시작했을 경우
			else{
				
				flag=true;
				collect=true;
				path.moveTo(eventX, eventY);
				
			// 시작점을 어디로 할지 정하는 것
			}
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			//시작지점에서부터 출발했을 시만 그리게함
			
			if (collect) {
			
				path.quadTo(eventX, eventY, (point.x + eventX) / 2,
						(point.y + eventY) / 2);
			
				//만약 도착점까지 도달했을 때
				if (eventX >= position[countX][countY].getX()
						&& eventY >= position[countX][countY].getY()) {
				
					if(flag){
						flag=false;
						stop();
					}
					collect=false;
					path.reset();

				}
				
				// 블럭을 닿았는지 검사.
				for (int i = 0; i < 8 * 7; i++)

					//전체 맵중에 블럭이 있는 어레이만 검사하고 블럭의 시작점인 x,y좌표와 끝점인 x+width/8, y+height/8의 좌표를 검사
					//width와height를 8로 나눈것은 맨 처음 좌표를 나눌 때 /8을 했기 때문
					if (position[blockX[i]][blockY[i]].getX() <= (int) eventX
							&& (int) eventX <= position[blockX[i]][blockY[i]]
									.getX() + (int) width / 8)
				
						if (position[blockX[i]][blockY[i]].getY() <= (int) eventY
								&& (int) eventY <= position[blockX[i]][blockY[i]]
										.getY() + (int) height / 8){
						//블럭에 닿았으면 점을 시작점으로 옮기고 전부 초기화 함.
							path.moveTo(0,0);
							collect=false;
							eventX=0;
							eventY=0;
							point.x=(int)eventX;
							point.y=(int)eventY;
							path.reset();
							
						}
			}

			// 앞 2개는 시작점에서부터 클릭했을 때 까지의 거리를 나타냄

			//도착지점까지 도달했을 시 그린 길을 리셋하고 토스트를 띄움
			
			break;

		case MotionEvent.ACTION_UP:
			
			collect=false;
			eventX=0;
			eventY=0;
			point.x=(int)eventX;
			point.y=(int)eventY;
			path.reset();
			// 마지막 점을 저장해 터치한 점이 떨어졌을때 그선으로 바로 이어버림
			break;

		}

		point.x = (int) eventX;
		point.y = (int) eventY;
		invalidate();
		return true;
	}
	

	public void stop(){
		((selectedMaze)c).music.stop();
		timer.cancel();
		
		((selectedMaze)c).mWakeLock.release();
		Intent intent= new Intent(c,AlarmWeather.class);
		
		if(message!=null)
			intent.putExtra("message", message);
		intent.putExtra("position", location);
		
		intent.putExtra("repeat", repeat);
		
		//아래를 안쓰면 알람이 울릴때마다 메인이 계속적으로 불려서 쌓인다.
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이전까지 쌓인 스택을 전부 없엔다.
		
		
		PendingIntent sender = PendingIntent.getActivity(c,request,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+1, sender);
		
		((selectedMaze)c).finish();
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