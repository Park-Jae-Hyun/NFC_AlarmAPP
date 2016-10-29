package kr.androidteam.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

/**
 * 등록된 알람을 받음.
 * 단일 알람이 아니라면 이 함수에서 울려야 하는 다음시간을 계산한다음 다시 울리게 한다.
 * @author Lee
 */

public class AlarmReceiver extends BroadcastReceiver{

	int[] days;
	public static final String MYPREFS="AlarmLOG"; //알람 기록을 남김
	final int mode=Activity.MODE_PRIVATE;
	StringBuffer savedAlarmInfo=new StringBuffer(); //알람의 정보를 저장했다가 다시 불러오는 역할
	AlarmInfo tempSaved;
	int count=0;
	int request;
	int position;
	int[] time=new int[2];
	String repeat;
	String option;
	String message=null;
	String number =null;
	AlarmNotification notification;
	Intent intent;
	Context context;
	final Handler handler = new Handler();
	Thread runner;
	
	private static PowerManager.WakeLock mWakeLock;
	
	final Runnable start = new Runnable() { 
		public void run() {
			excute();
			mWakeLock.release();
			
		};
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//일정 시간마다 계속적으로 알람 cpu를 깨워줌
		
		PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
		PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver");
		
		mWakeLock.acquire();
		
		// 전달받은 값들을 저장
		this.intent=intent;
		this.context=context;
		// 핸드폰을 껐다가 켰을 때 알람들을 재등록함.

		if (runner == null) {
			runner = new Thread() {
				public void run() {

					handler.post(start);
				}

			};
			runner.start();
		}

	}

	public void excute(){
		
		if (intent.getAction().equals("setAlarm")) {
			request = intent.getIntExtra("requestCode", 0);
			position = intent.getIntExtra("position", 0);
			time = intent.getIntArrayExtra("time");
			repeat = intent.getStringExtra("repeat");
			option=intent.getStringExtra("option");
			
			if(intent.getStringExtra("message")!=null)
				message=intent.getStringExtra("message");

			if(intent.getStringExtra("number")!=null)
				number=intent.getStringExtra("number");

			Log.e("setAlarm","등록완료");
			calculateDay(); // 날짜를 계산함
			nextActivity(context);
			// 노래를 틀기위해 다음 액티비티를 실행시키는 함수

			if (repeat.length() != 0) // 단일알람이 아닐 시
				nextAlarm(context);
			// 단일 알람이 아니면 다음 번에 실행 될 시간을 계산하고 등록시킴
		}
		

		else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			Log.e("부팅시","쿠팅완료");
				SharedPreferences sh_Pref = context.getSharedPreferences(MYPREFS,mode);
				 
				// 이 조건이 맞으면 알람의정보가 1개라도 들어있다는 뜻
				if (sh_Pref != null && sh_Pref.contains("saveInfo")
						&& sh_Pref.contains("savedInfoSize")) {
					showSavedAlarmInfo(context);
					wakeCPU(context);
					
				}
			}

	}
		public void wakeCPU(Context context){
		
		Intent intent=new Intent(context,wakeUp.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),1000 * 60 * 120, sender);

	}
	
	//쉐어드프리퍼런스에서 값을 가져옴
	public void showSavedAlarmInfo(Context context) {
		SharedPreferences my_Pref = context.getSharedPreferences(MYPREFS, mode);
		
		count = my_Pref.getInt("savedInfoSize", 0);
		String restoreInfo = my_Pref.getString("saveInfo", "");

		if (restoreInfo != "") {

			String[] restoreInfoArray = restoreInfo.split("=");
			// 알람 어레이리스트별로 끊어서 str에 저장한다.

			for (int i = 0; i < restoreInfoArray.length; i++) {

				String[] eachComponent = restoreInfoArray[i].split("_");
				// 그후 알람정보별로 끊은 것을 다시 쪼개서 저장함.

				tempSaved = new AlarmInfo(eachComponent[0], eachComponent[1],
						eachComponent[2], eachComponent[3],eachComponent[4],eachComponent[5]
						, eachComponent[6],Integer.parseInt(eachComponent[7]),
						Integer.parseInt(eachComponent[8]),Integer.parseInt(eachComponent[9]));

				//날짜와시간을 계산하는 함수
				if(tempSaved.getActive()==1){
					
					if(notification==null){
					notification=new AlarmNotification(context);
					notification.registerNotification();
					}
					calculateDayTime(tempSaved);
				
				// 알람을 다시 등록하는 함수
				registAlarm(context,tempSaved,i);
				}
			}
			
		}

	}

	//날짜와 시간을 계산
	public void calculateDayTime(AlarmInfo tempSaved){
		String[] convertTime;

		if (tempSaved.getDay() != "") {
			days = new int[tempSaved.getDay().length()];


			for (int i = 0; i < tempSaved.getDay().length(); i++)
				days[i] = Integer.parseInt(""+ tempSaved.getDay().charAt(i));
		}
		

		
		convertTime = tempSaved.getReceive().split(":");

		time[0] = Integer.parseInt(convertTime[0]);
		time[1] = Integer.parseInt(convertTime[1]);
	}
	
	//맞는 요일별로 
	public void registAlarm(Context context,AlarmInfo tempSaved, int position){
		Calendar cal = Calendar.getInstance();
		int alarmTimer=0;
		
		if(days.length==1){ //길이가 1개라면 일주일중 하루만 울리게 하는 것 			
			if(cal.get(Calendar.DAY_OF_WEEK)>days[0]) //알람을 등록했을 때 오늘보다 뒤에 있는 날짜인 경우
			alarmTimer=7-cal.get(Calendar.DAY_OF_WEEK)+days[0]; 
			//월요일이 체크되었으면 월요일날 울린 뒤 일주일 뒤에 다시 울리게 한다.
			
			else if(cal.get(Calendar.DAY_OF_WEEK) < days[0]) //이미 날짜가 지나간 경우
				alarmTimer=days[0]-cal.get(Calendar.DAY_OF_WEEK);
				
			else{ //오늘일 경우
				alarmTimer=oneDayTimeCal(cal);
				
			}
			AlarmReEnrollment(context,tempSaved,position,alarmTimer);
			
		}
		
		else if(days.length>1) //1개 이상 반복요일이 체크 되었을 시
			
		for(int i=0;i<days.length;i++){
			
			if(days[i] ==cal.get(Calendar.DAY_OF_WEEK)){  //등록한 알람이 오늘일 경우
				alarmTimer=multiTimeCal(cal,i);
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);	
				break;
			}
			
			else if(cal.get(Calendar.DAY_OF_WEEK) < days[i]){ //오늘보다 체크된 요일이 더 뒤에 있을 시
				alarmTimer=days[i]-cal.get(Calendar.DAY_OF_WEEK);
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);
				break;
			}
				
			// 체크된 요일을 마지막 까지 확인 하고 마지막 체크요일도 오늘이 아니라면 오늘보다 체크된요일은 이전임
			else if(i==days.length-1 && cal.get(Calendar.DAY_OF_WEEK) != days[i]){
				alarmTimer=7- cal.get(Calendar.DAY_OF_WEEK) + days[0];
				AlarmReEnrollment(context,tempSaved,position,alarmTimer);
				
			}
		}
		
		else{  //단일 알람일시
			alarmTimer=singleTimeCal(cal);
			AlarmReEnrollment(context,tempSaved,position,alarmTimer);
		
		}
			
	}
	
	/**
	 * 다수 요일을 체크한 경우  시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우
	 * 바로 울리게 한다. 만약 시간이 맞지만 지정한 분이 현재시간의 분보다 지났거나
	 * 지정한시간의 시가 현재시간의 시보다 지난경우 2가지 경우로 나눠진다.
	 * 체크된 요일의 끝인 경우와 체크된 요일이 처음이나 중간인 경우이다.
	 * 
	 * 첫번째 체크된요일의 끝인 경우 체크된 요일의 처음에 울리게 하기위해 일주일에서 현재 요일을 뺀다음 
	 * 처음 체크된 요일의 숫자를 더한다.
	 * 두번째 체크된요일이 처음이나 중간인 경우 다음 체크된 요일이 있기 때문에 현재 체크된 요일에서 바로 다음 체크된 요일을 빼서
	 * 차이나는 만큼 뒤에 울리게 만든다.
	 * @param cal
	 * @param position
	 * @return
	 */
	public int multiTimeCal(Calendar cal, int position){
		
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			
			else{
			
				if(position==days.length-1)
					return (7- cal.get(Calendar.DAY_OF_WEEK) + days[0]);
				
				else
					return (days[position+1] - days[position]);
			}
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{

			if(position==days.length-1)
				return (7- cal.get(Calendar.DAY_OF_WEEK) + days[0]);
			
			else
				return (days[position+1] - days[position]);

		}

	}
	/**
	 * 단일알람은 한번만 울리면 된다. 그래서 시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우
	 * 바로 울리게 한다. 만약 시간이 맞지만 지정한 분이 현재시간의 분보다 지났거나
	 * 지정한시간의 시가 현재시간의 시보다 지난경우 단일알람이므로 하루뒤에 울리게 한다.
	 * @param cal
	 * @return
	 */
	public int singleTimeCal(Calendar cal){
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			else
				return 1;
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{
			return 1;
		}

		
	}
	/**
	 * 일주일 중 하루만 체크한 경우   시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우
	 * 바로 울리게 한다. 만약 시간이 맞지만 지정한 분이 현재시간의 분보다 지났거나
	 * 지정한시간의 시가 현재시간의 시보다 지난경우 일주일 중 하루만 울리게 하므로
	 * 일주일뒤 현재 요일에 울리게 한다.
	 * @param cal
	 * @return
	 */
	public int oneDayTimeCal(Calendar cal){
		
		if(time[0]==cal.get(Calendar.HOUR_OF_DAY)){
			if(time[1]>=cal.get(Calendar.MINUTE))
				return 0;
			else
				return 7;
		}
		
		else if(time[0]>cal.get(Calendar.HOUR_OF_DAY)){
			return 0;
		}
		
		else{
			return 7;
		}
		
	}
	
	//알람을 등록
	public void AlarmReEnrollment(Context context,AlarmInfo tempSaved, int position, int alarmTimer) {
		long oneDay = 1000 * 60 * 60 * 24; //하루를 밀리세컨드로 계산한 것.
		Intent intent= new Intent(context,AlarmReceiver.class);
//어레이리스트의 위치, 리퀘스트코드, 날짜와 시간을 보냄.
		intent.putExtra("position", position);
		intent.putExtra("requestCode", tempSaved.getRequestCode());
		intent.putExtra("repeat", tempSaved.getDay());
		intent.putExtra("time", time);
		intent.putExtra("option", tempSaved.getOption());
		
		if(tempSaved.getMessage()!=null)
			intent.putExtra("message",tempSaved.getMessage());

		if(tempSaved.getNumber()!=null)
			intent.putExtra("number",tempSaved.getNumber());

		
		intent.setAction("setAlarm");
		
		PendingIntent sender = PendingIntent.getBroadcast(context,tempSaved.getRequestCode(),
				intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();

		if (tempSaved.getActive() == 1) {
			
			if (days.length != 0) {
			
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+oneDay*alarmTimer, sender);
			
		}

		//단일 알람일 경우
		else {
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+oneDay*alarmTimer,sender);

		}
		}
	}

	public void calculateDay(){
		
			if (repeat != "") {
				days = new int[repeat.length()];

				for (int i = 0; i < repeat.length(); i++)
					days[i] = Integer.parseInt(""+ repeat.charAt(i));
			}
			
		}

	/*
	 * 다음번에 울릴 시간을 계산한 뒤 받은 리퀘스트 코드로 다시 등록을 한다.
	 * 이 함수로는 단일 알람의 접근을 막아 놔서 들어오지 못함.
	 */
	public void nextAlarm(Context context){
		Calendar cal = Calendar.getInstance();
		int nextAlarmTimer=0;
		
		if(days.length==1){ //길이가 1개라면 일주일중 하루만 울리게 하는 것
			nextAlarmTimer=7; //월요일이 체크되었으면 월요일날 울린 뒤 일주일 뒤에 다시 울리게 한다.
			changedAlarmRegister(context,nextAlarmTimer);	
		}
		
		else
			
		for(int i=0;i<days.length;i++)
		//울렸을 시에만 해당 클래스를 들어오기 때문에 days안에 있는 요일중 무조건 하나는 오늘이다.
			//그래서 어레이 안에 오늘 날짜가 몇번 째인지 확인 한다음에 간격을 구한다.
			if(days[i] ==cal.get(Calendar.DAY_OF_WEEK)){
			
				if(i==days.length-1){ 
					nextAlarmTimer=7-days[i]+days[0]; 
					changedAlarmRegister(context,nextAlarmTimer);	
				}
				
				else{
					nextAlarmTimer=days[i+1]-days[i];
					changedAlarmRegister(context,nextAlarmTimer);
				}
			}
	}

	//다음 울릴 알람을 등록함 
	public void changedAlarmRegister(Context context,int nextAlarmTimer) {
		
		long oneDay = 1000 * 60 * 60 * 24; //하루를 밀리세컨드로 계산한 것.
		Calendar cal = Calendar.getInstance();

		Intent intent= new Intent(context,AlarmReceiver.class);
		
		intent.putExtra("position", position);
		intent.putExtra("requestCode", request);
		intent.putExtra("repeat", repeat);
		intent.putExtra("time", time);
		intent.putExtra("option", option);
		
		if(message!=null)
			intent.putExtra("message", message);

		if(number!=null)
			intent.putExtra("number", number);

		intent.setAction("setAlarm");
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context,request ,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (cal.get(Calendar.HOUR_OF_DAY) <= time[0])
		
			if (cal.get(Calendar.MINUTE) <= time[1]) {
		
				cal.set(Calendar.HOUR_OF_DAY, time[0]);
				cal.set(Calendar.MINUTE, time[1]);
				cal.set(Calendar.SECOND, 0);
				am.set(AlarmManager.RTC_WAKEUP,	cal.getTimeInMillis()+oneDay*nextAlarmTimer, sender);
			}
	}
	
	//노래가 나오는 액티비티로 전달해주는 함수
	public void nextActivity(Context context){
		Calendar cal = Calendar.getInstance();
		
		Intent i=null;
		if(option.equals("word"))
			i= new Intent(context,selectedWord.class);
		else if(option.equals("game"))
			i= new Intent(context,selectedMaze.class);
		else if(option.equals("catch"))
			i=new Intent(context,selectedcatch.class);
		else if(option.equals("nfc")){
			i=new Intent(context, selectednfc.class);
			Log.i("test","ok?");
		}
		i.putExtra("repeat", repeat);
		i.putExtra("position", position);
		i.putExtra("request", request);
		
		if(message!=null)
			i.putExtra("message", message);
		
		if(number!=null)
			i.putExtra("number", number);
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getActivity(context,request ,i, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), sender);
		
	}

}
