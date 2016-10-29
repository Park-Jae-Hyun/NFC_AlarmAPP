package kr.androidteam.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, OnItemSelectedListener {

	public static Activity IntroAct;

	RelativeLayout background;
	ImageButton btn1; // 추가버튼
	int CREATE_REQUEST = 1; // 리스트에 추가할때 requestcode와 맞는지
	int DELETEorCHANGE_REQUEST = 2; // 리스트에 수정할때 requestcode와 맞는지
	ListView list;
	int count = 0; // 리스트 뷰에 추가할때 어레이리스트의 갯수를 세주는 변수
	int number; // 리스트 뷰를 수정,삭제할때 어레이리스트가 몇번째인지 세주는 변수
	AlertDialog.Builder builder; // dialog창을 위한 변수
	AlarmInfo tempSaved; // 번들로 AlarmInfoChange 함수로 보내줄 때 parcelable로 변환해 주기 위해
							// 임시 저장하는 변수
	dataAdapter adapter = null; // 리스트뷰를 커스텀화 하기위해 필요한 클래스
	ArrayList<AlarmInfo> alarmInfo = new ArrayList<AlarmInfo>(); // 알람정보들을 받아서저장

	public static final String MYPREFS = "AlarmLOG"; // 알람 기록을 남김
	final int mode = Activity.MODE_PRIVATE;
	StringBuffer savedAlarmInfo = new StringBuffer(); // 알람의 정보를 저장했다가 다시 불러오는
														// 역할

	int request; // 알람을 등록하기 위한 요청 코드 - 코드당 1개의 알람만 등록이 됨
	int[] time = new int[2]; // 어레이 0번째엔 시, 1번째엔 분이 들어감
	int[] days; // 체크된 요일을 숫자로 체크한다.

	boolean toastFlag = false;
	boolean onBackground = false;

	int icon = R.drawable.time;
	String tickerText = "알람등록";
	long when = System.currentTimeMillis();
	Notification notification = new Notification(icon, tickerText, when);
	String serName = Context.NOTIFICATION_SERVICE;
	NotificationManager NM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntroAct = this;
		background = (RelativeLayout) findViewById(R.id.background);
		btn1 = (ImageButton) findViewById(R.id.button1);
		btn1.setOnClickListener(this);
		list = (ListView) findViewById(R.id.list);
		// customview를 위한 클래스를 지정해줌.
		SharedPreferences sh_Pref = getSharedPreferences(MYPREFS, mode);

		// 이 조건이 맞으면 알람의정보가 1개라도 들어있다는 뜻
		if (sh_Pref != null && sh_Pref.contains("saveInfo")
				&& sh_Pref.contains("savedInfoSize"))
			showSavedAlarmInfo();

		/*
		 * 알람이 울린다음 알람을 끌 때 이것이 단일알람인지 아닌지 값을 보내준다. 울린 알람의 포지션번호를 받아 단일알람이면 비활성화
		 * 시키고 단일알람이 아닐 시 그대로 냅둠
		 */

		Intent i = getIntent();
		int position = i.getIntExtra("position", -1);
		int singleAlarm = i.getIntExtra("singleAlarm", -1);

		if (singleAlarm != -1 && singleAlarm == 0 && position != -1)
			alarmInfo.get(position).setActive(0);

		// 커스텀뷰
		adapter = new dataAdapter(this, R.layout.alarm_list, alarmInfo);

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		list.setOnItemSelectedListener(this);

		NM = (NotificationManager) getSystemService(serName);
		Intent notifiIntent = new Intent(MainActivity.this, MainActivity.class);

		PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0,
				notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NM = (NotificationManager) getSystemService(serName);
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, "알라미", "알람이 등록되었습니다.", pi);
		setAlarmChecked();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nfc, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.new_nfc:
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void registerNotification() {

		NM.notify(1, notification);

	}

	public void notificationCancel() {
		NM.cancel(1);
	}

	public void alarmOn() {
		Resources res = getResources();

		TransitionDrawable drawable = (TransitionDrawable) res
				.getDrawable(R.drawable.wakebackground);
		background.setBackground(drawable);
		drawable.startTransition(1000);

		btn1.setImageResource(R.drawable.wakebutton);
		TransitionDrawable drawableBtn = (TransitionDrawable) btn1
				.getDrawable();
		drawableBtn.startTransition(1000);
	}

	public void alarmOff() {
		Resources res = getResources();

		TransitionDrawable drawable = (TransitionDrawable) res
				.getDrawable(R.drawable.convertbackground);
		background.setBackground(drawable);
		drawable.startTransition(1000);

		btn1.setImageResource(R.drawable.convertbutton);
		TransitionDrawable drawableBtn = (TransitionDrawable) btn1
				.getDrawable();
		drawableBtn.startTransition(1000);
	}

	public void backgroundAdjust() {
		int offVs = 0;

		if (alarmInfo.size() == 0) {
			alarmOff();

			if (NM != null) {

				notificationCancel();

			}
		}

		else {

			for (int i = 0; i < alarmInfo.size(); i++) {
				if (alarmInfo.get(i).getActive() == 0)
					offVs++;
			}

			if (offVs == alarmInfo.size() && !onBackground) {
				alarmOff();
				if (NM != null) {
					notificationCancel();

				}
				onBackground = true;

			}

		}
	}

	public void onBackground() {
		int onVs = 0;
		for (int i = 0; i < alarmInfo.size(); i++) {
			if (alarmInfo.get(i).getActive() == 1)
				onVs++;
		}

		if (onVs == 1) {
			alarmOn();
			registerNotification();

			onBackground = false;
		}

	}

	// 비활성화 했을 때 알람을 취소함
	public void setAlarmChecked() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		int[] time = new int[2];
		String[] temp = new String[2];
		PendingIntent sender;
		AlarmManager am = (AlarmManager) MainActivity.this
				.getSystemService(Context.ALARM_SERVICE);

		for (int i = 0; i < alarmInfo.size(); i++) {

			if (alarmInfo.get(i).getActive() == 0) {

				temp = alarmInfo.get(i).getReceive().split(":");
				time[0] = Integer.parseInt(temp[0]);
				time[1] = Integer.parseInt(temp[1]);
				intent.putExtra("time", time);
				intent.setAction("setAlarm");
				intent.putExtra("position", i);
				intent.putExtra("requestCode", alarmInfo.get(i)
						.getRequestCode());
				intent.putExtra("repeat", alarmInfo.get(i).getDay());
				intent.putExtra("option", alarmInfo.get(i).getOption());
				if (alarmInfo.get(i).getMessage() != null)
					intent.putExtra("message", alarmInfo.get(i).getMessage());

				if (alarmInfo.get(i).getNumber() != null)
					intent.putExtra("number", alarmInfo.get(i).getNumber());

				sender = PendingIntent.getBroadcast(MainActivity.this,
						alarmInfo.get(i).getRequestCode(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				am.cancel(sender);
				sender.cancel();

			}
		}
		backgroundAdjust();
	}

	public void checkboxAlarmRegister(int position) {
		calculateDayAndTime(alarmInfo.get(position));
		alarm(alarmInfo.get(position), position);
		onBackground();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(btn1)) {
			Intent it = new Intent(this, AlarmContents.class);
			startActivityForResult(it, CREATE_REQUEST);

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		/**
		 * 알람정보를 추가할때
		 */

		if (requestCode == CREATE_REQUEST) {
			if (resultCode == RESULT_OK) {

				/**
				 * 알람매니저를 전부 모은다음 서비스 클래스에 넘겨줘서 알람들을 등록한다.
				 * 
				 */
				// 리퀘스트코드를 0에서 10000사이의 수로 랜덤으로 찍어낸다.
				request = (int) (Math.random() * 100000 % 1000000)
						+ (int) (Math.random() * 1000 % 10000) + 1;

				alarmInfo.add((AlarmInfo) intent.getParcelableExtra("info"));

				adapter.notifyDataSetChanged();

				alarmInfo.get(count).setRequestCode(request);

				// 알람등록을 위해 스트링 형태인 시간과 요일을 정수형으로 바꾼 후 등록
				toastFlag = true;
				calculateDayAndTime(alarmInfo.get(count));
				alarm(alarmInfo.get(count), count);
				onBackground();

				count++;
				repeatWakeUp();

				/**
				 * 어레이리스트에 추가한 후 adapter를 수정해준다음 arraylist의 갯수인 count를 세줌
				 */
			}
		}

		/**
		 * 알람을 수정 및 삭제할때
		 */
		else if (requestCode == DELETEorCHANGE_REQUEST) {

			if (resultCode == RESULT_OK) {

				/**
				 * 번들로 보냈는데 왜 받을때 값을 저장 못하고 다시 번들로 값을 가져와야 되는지 값을 다시 get으로 받아오면
				 * intent로 보내는 거랑 뭐가 다르지? 번들에 대해 다시 공부해보기
				 */
				Bundle bundle = new Bundle();
				bundle = intent.getExtras();
				tempSaved = bundle.getParcelable("change");
				alarmInfo.set(number, tempSaved);
				if (alarmInfo.get(number).getDelete() == 0) {
					toastFlag = true;

					calculateDayAndTime(alarmInfo.get(number));
					alarm(alarmInfo.get(number), number);
				}

				/*
				 * 수정 되었을 시 수정된 시간과 요일을 다시 도출한 후 등록한다. 리퀘스트 코드1개당 1개의 알람만 울리고
				 * 이전알람은 무시되기 때문에 리퀘스트 코드를 똑같이 쓰면 이전에 등록된 알람은 취소가 된다.
				 */

				else {
					// 삭제될 시 해당 위치의 리퀘스트코드를 받은다음 취소한다.
					alarmCancel(); // 등록된 모든 알람을 전부 취소한다.
					alarmInfo.remove(number);
					// 다음 어레이리스트를 최신화 한다음 다시 전부 등록을 한다.
					reRegisterAlarmAfterCancel();
					count--; // 지웠으니 크기도 줄여야지
					toastFlag = false;
					if (alarmInfo.size() == 0)
						cancelWakeUp();

					backgroundAdjust();

				}

				adapter.notifyDataSetChanged();
			}
		}

	}

	/*
	 * 리스트뷰를 클릭했을 때 리스트뷰를 클릭할경우는 만들어진 알람 리스트를 수정하거나 삭제할 때 누르기 때문에 액션을 1가지만 취하면
	 * 되서 수정하는 activity로 보내는 번들만 적으면 됨
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		/**
		 * 인텐트와 번들. 다른 액티비티로 보내는 정보를 a라고 하면 번들로 a를 감싼다(번들을 택배상자라고 이해하면 편함) 그다음
		 * 번들로 감싼것을 인텐트를 통해 다른 액티비티로 배달한다
		 * 
		 */

		Intent intent = new Intent(this, AlarmInfoChange.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("change", alarmInfo.get(position));
		intent.putExtras(bundle);
		number = position;
		startActivityForResult(intent, DELETEorCHANGE_REQUEST);
		/**
		 * 객체를 넘겨주기위해 alarminfo에 새로이 값을 넘겨준 후 tempSaved에 받아온다음 변환된 객체를 번들로 보냄
		 */
	}

	// 리스트 뷰를 지우기위해 오랫동안 눌렀을 때
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// TODO Auto-generated method stub

		builder = new AlertDialog.Builder(this);

		builder.setTitle("Do you want to delete it ?");

		// setpositive는 왼쪽에 나타내고자 하는 것을 의미
		builder.setPositiveButton("Accept",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						// 알람인포의 정보와 시간을 저장한 어레이 리스트를 지움
						alarmCancel();// 등록된 알람 모두 지움

						alarmInfo.remove(position);
						// 최신화된 알람리스트를 다시 전부 등록한다.
						reRegisterAlarmAfterCancel();

						adapter.notifyDataSetChanged();
						count--; // count는 timeTest arraylist의 크기를 나타낸다. 하나를
									// 지웠으니 크기도 주는게 당연
						toastFlag = false;

						if (alarmInfo.size() == 0)
							cancelWakeUp();

						backgroundAdjust();

					}
				});

		// negative는 팝업창의 오른쪽을 의미함
		builder.setNegativeButton("Cancel", null);

		/**
		 * 리스트 뷰를 지울 때 어레이리스트에 있는 항목을 지운 다음 listview가 아닌 adapter를 최신화 시켜줘야함
		 */
		builder.show();
		return true;
	}

	/**
	 * day와 time은 string형태로 저장되어 있어 정수형으로 바꿔주는 함수
	 * 
	 * @param tempSaved
	 */
	public void calculateDayAndTime(AlarmInfo tempSaved) {
		String[] convertTime;

		if (tempSaved.getDay() != "") {
			this.days = new int[tempSaved.getDay().length()];

			for (int i = 0; i < tempSaved.getDay().length(); i++)
				this.days[i] = Integer.parseInt(""
						+ tempSaved.getDay().charAt(i));
		}

		convertTime = tempSaved.getReceive().split(":");

		this.time[0] = Integer.parseInt(convertTime[0]);
		this.time[1] = Integer.parseInt(convertTime[1]);

	}

	/**
	 * 알람을 등록하는 함수.
	 * 
	 * @param tempSaved
	 * @param position
	 */

	public void AlarmEnrollment(AlarmInfo tempSaved, int position,
			int alarmTimer) {
		long oneDay = 1000 * 60 * 60 * 24; // 하루를 밀리세컨드로 계산한 것.
		Intent intent; // 다른 액티비티를 실행하는 것
		PendingIntent sender; // 알람매니저를 사용할 때 필요함
		AlarmManager am;

		intent = new Intent(this, AlarmReceiver.class);
		// 어레이리스트의 위치, 리퀘스트코드, 날짜와 시간을 보냄.
		intent.putExtra("position", position);
		intent.putExtra("requestCode", tempSaved.getRequestCode());
		intent.putExtra("repeat", tempSaved.getDay());
		intent.putExtra("time", time);
		intent.putExtra("option", tempSaved.getOption());

		if (tempSaved.getMessage() != null)
			intent.putExtra("message", tempSaved.getMessage());

		if (tempSaved.getNumber() != null)
			intent.putExtra("number", tempSaved.getNumber());

		intent.setAction("setAlarm");
		sender = PendingIntent.getBroadcast(MainActivity.this,
				tempSaved.getRequestCode(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am = (AlarmManager) MainActivity.this
				.getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();

		// 체크된 요일이 있는 경우
		if (days.length != 0 && tempSaved.getActive() == 1) {

			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + oneDay
					* alarmTimer, sender);

			if (toastFlag)
				Toast.makeText(
						this,
						alarmTimer + "일 후" + time[0] + "시" + time[1]
								+ "분 에 울립니다.", 0).show();

		}

		// 단일 알람일 경우
		else if (days.length == 0 && tempSaved.getActive() == 1) {
			cal.set(Calendar.HOUR_OF_DAY, time[0]);
			cal.set(Calendar.MINUTE, time[1]);
			cal.set(Calendar.SECOND, 0);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + oneDay
					* alarmTimer, sender);

			if (toastFlag)
				Toast.makeText(
						this,
						alarmTimer + "일 후" + time[0] + "시" + time[1]
								+ "분 에 울립니다.", 0).show();

		}
		toastFlag = false;
	}

	// 체크된 요일이 있으면 지금 날짜부터 얼마만큼의 간격으로 떨어져 있는지 계산하는 함수.
	public void alarm(AlarmInfo tempSaved, int position) {
		Calendar cal = Calendar.getInstance();
		int alarmTimer = 0;

		if (days.length == 1) { // 길이가 1개라면 일주일중 하루만 울리게 하는 것
			if (cal.get(Calendar.DAY_OF_WEEK) > days[0]) // 알람을 등록했을 때 오늘보다 뒤에
															// 있는 날짜인 경우
				alarmTimer = 7 - cal.get(Calendar.DAY_OF_WEEK) + days[0];
			// 월요일이 체크되었으면 월요일날 울린 뒤 일주일 뒤에 다시 울리게 한다.

			else if (cal.get(Calendar.DAY_OF_WEEK) < days[0]) // 이미 날짜가 지나간 경우
				alarmTimer = days[0] - cal.get(Calendar.DAY_OF_WEEK);

			else { // 오늘일 경우
				alarmTimer = oneDayTimeCal(cal);

			}
			AlarmEnrollment(tempSaved, position, alarmTimer);

		}

		else if (days.length > 1) // 1개 이상 반복요일이 체크 되었을 시

			for (int i = 0; i < days.length; i++) {

				if (days[i] == cal.get(Calendar.DAY_OF_WEEK)) { // 등록한 알람이 오늘일
																// 경우
					alarmTimer = multiTimeCal(cal, i);
					AlarmEnrollment(tempSaved, position, alarmTimer);
					break;

				}

				else if (cal.get(Calendar.DAY_OF_WEEK) < days[i]) { // 오늘보다 체크된
																	// 요일이 더 뒤에
																	// 있을 시
					alarmTimer = days[i] - cal.get(Calendar.DAY_OF_WEEK);
					AlarmEnrollment(tempSaved, position, alarmTimer);
					break;
				}

				// 체크된 요일을 마지막 까지 확인 하고 마지막 체크요일도 오늘이 아니라면 오늘보다 체크된요일은 이전임
				else if (i == days.length - 1
						&& cal.get(Calendar.DAY_OF_WEEK) != days[i]) {
					alarmTimer = 7 - cal.get(Calendar.DAY_OF_WEEK) + days[0];
					AlarmEnrollment(tempSaved, position, alarmTimer);

				}
			}

		else { // 단일 알람일시
			alarmTimer = singleTimeCal(cal);
			AlarmEnrollment(tempSaved, position, alarmTimer);

		}

	}

	/**
	 * 다수 요일을 체크한 경우 시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우 바로 울리게 한다. 만약 시간이 맞지만 지정한
	 * 분이 현재시간의 분보다 지났거나 지정한시간의 시가 현재시간의 시보다 지난경우 2가지 경우로 나눠진다. 체크된 요일의 끝인 경우와
	 * 체크된 요일이 처음이나 중간인 경우이다.
	 * 
	 * 첫번째 체크된요일의 끝인 경우 체크된 요일의 처음에 울리게 하기위해 일주일에서 현재 요일을 뺀다음 처음 체크된 요일의 숫자를
	 * 더한다. 두번째 체크된요일이 처음이나 중간인 경우 다음 체크된 요일이 있기 때문에 현재 체크된 요일에서 바로 다음 체크된 요일을
	 * 빼서 차이나는 만큼 뒤에 울리게 만든다.
	 * 
	 * @param cal
	 * @param position
	 * @return
	 */
	public int multiTimeCal(Calendar cal, int position) {

		if (time[0] == cal.get(Calendar.HOUR_OF_DAY)) {

			if (time[1] >= cal.get(Calendar.MINUTE))
				return 0;

			else {

				if (position == days.length - 1)
					return (7 - cal.get(Calendar.DAY_OF_WEEK) + days[0]);

				else
					return (days[position + 1] - days[position]);
			}
		}

		else if (time[0] > cal.get(Calendar.HOUR_OF_DAY)) {
			return 0;
		}

		else {

			if (position == days.length - 1)
				return (7 - cal.get(Calendar.DAY_OF_WEEK) + days[0]);

			else
				return (days[position + 1] - days[position]);

		}

	}

	/**
	 * 단일알람은 한번만 울리면 된다. 그래서 시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우 바로 울리게 한다. 만약 시간이
	 * 맞지만 지정한 분이 현재시간의 분보다 지났거나 지정한시간의 시가 현재시간의 시보다 지난경우 단일알람이므로 하루뒤에 울리게 한다.
	 * 
	 * @param cal
	 * @return
	 */
	public int singleTimeCal(Calendar cal) {
		if (time[0] == cal.get(Calendar.HOUR_OF_DAY)) {
			if (time[1] >= cal.get(Calendar.MINUTE))
				return 0;
			else
				return 1;
		}

		else if (time[0] > cal.get(Calendar.HOUR_OF_DAY)) {
			return 0;
		}

		else {
			return 1;
		}

	}

	/**
	 * 일주일 중 하루만 체크한 경우 시간이 맞거나 지정한 시간이 현재시간보다 더 뒤에 있는 경우 바로 울리게 한다. 만약 시간이 맞지만
	 * 지정한 분이 현재시간의 분보다 지났거나 지정한시간의 시가 현재시간의 시보다 지난경우 일주일 중 하루만 울리게 하므로 일주일뒤 현재
	 * 요일에 울리게 한다.
	 * 
	 * @param cal
	 * @return
	 */
	public int oneDayTimeCal(Calendar cal) {

		if (time[0] == cal.get(Calendar.HOUR_OF_DAY)) {
			if (time[1] >= cal.get(Calendar.MINUTE))
				return 0;
			else
				return 7;
		}

		else if (time[0] > cal.get(Calendar.HOUR_OF_DAY)) {
			return 0;
		}

		else {
			return 7;
		}

	}

	/**
	 * 등록된 모든 알람을 취소한다. 왜이렇게 하냐면 알람이 1,2,3순으로 등록되면 각각 해당위치를 보낸다. 그후 1,2가 지워지면 3이
	 * 1번째로 바뀌는데 보내진 값은 여전히 3번째로 알고있어서 문제가 생긴다.
	 */

	public void alarmCancel() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		int[] time = new int[2];
		String[] temp = new String[2];
		PendingIntent sender;
		AlarmManager am = (AlarmManager) MainActivity.this
				.getSystemService(Context.ALARM_SERVICE);
		for (int i = 0; i < alarmInfo.size(); i++) {
			temp = alarmInfo.get(i).getReceive().split(":");
			time[0] = Integer.parseInt(temp[0]);
			time[1] = Integer.parseInt(temp[1]);
			intent.putExtra("time", time);
			intent.setAction("setAlarm");
			intent.putExtra("position", i);
			intent.putExtra("requestCode", alarmInfo.get(i).getRequestCode());
			intent.putExtra("repeat", alarmInfo.get(i).getDay());
			intent.putExtra("option", alarmInfo.get(i).getOption());

			if (alarmInfo.get(i).getMessage() != null)
				intent.putExtra("message", alarmInfo.get(i).getMessage());

			if (alarmInfo.get(i).getNumber() != null)
				intent.putExtra("number", alarmInfo.get(i).getNumber());

			sender = PendingIntent.getBroadcast(MainActivity.this, alarmInfo
					.get(i).getRequestCode(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			am.cancel(sender);
			sender.cancel();
			backgroundAdjust();

		}
	}

	// 알람을 전부 취소한 다음 다시 전부 등록하는 함수.
	public void reRegisterAlarmAfterCancel() {

		for (int i = 0; i < alarmInfo.size(); i++) {

			if (alarmInfo.get(i).getActive() != 0) {
				calculateDayAndTime(alarmInfo.get(i));

				alarm(alarmInfo.get(i), i);
			}
		}

	}

	/**
	 * parcelable은 ipc를 사용함. 고정적인 저장은 할수없음 그래서 serializable보다 빠름
	 */

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		savedAlarmInfo();
		super.onPause();

	}

	/**
	 * 알람의 정보를 sharedPreference로 저장해 놓는다.
	 */
	public void savedAlarmInfo() {

		SharedPreferences sh_Pref = getSharedPreferences(MYPREFS, mode);
		SharedPreferences.Editor toEdit = sh_Pref.edit();
		savedAlarmInfo.delete(0, savedAlarmInfo.length()); // 스트링 버퍼에 남아있을 수 있는
															// 값을 모두 초기화시킴
		toEdit.remove("saveInfo"); // 기존에 있던 sharedPreference를 모두 지운 후 다시 저장한다./
		toEdit.remove("savedInfoSize");

		if (alarmInfo.size() != 0) { // 알람정보를 가지고 있는 어레이리스트가 하나라도 있으면

			emerge();
			toEdit.putString("saveInfo", savedAlarmInfo.toString());
			toEdit.putInt("savedInfoSize", alarmInfo.size());

		}

		toEdit.commit();

	}

	public void repeatWakeUp() {
		Intent i = new Intent(MainActivity.this, wakeUp.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0,
				i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Calendar cal = Calendar.getInstance();
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				1000 * 60 * 120, sender);

	}

	public void cancelWakeUp() {
		Intent i = new Intent(MainActivity.this, wakeUp.class);

		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0,
				i, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		am.cancel(sender);
	}

	// stringBuffer에 알람정보를 담고있는 어레이리스트를 모두 저장한다.
	// '_' 은 알람정보 별개를 끊는 단위이고 '='은 알람별로 끊는 단위임.
	public void emerge() {

		for (int i = 0; i < alarmInfo.size(); i++) {
			savedAlarmInfo.append(alarmInfo.get(i).getTime() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getReceive() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getDay() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getMessage() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getNumber() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getCheck() + "_");
			savedAlarmInfo.append(alarmInfo.get(i).getOption() + "_");
			savedAlarmInfo.append(Integer
					.toString(alarmInfo.get(i).getDelete()) + "_");
			savedAlarmInfo.append(Integer
					.toString(alarmInfo.get(i).getActive()) + "_");
			savedAlarmInfo.append(Integer.toString(alarmInfo.get(i)
					.getRequestCode()) + "=");
		}
	}

	// 껐다가 다시 켰을 때 정보들을 다시 불러온다.
	public void showSavedAlarmInfo() {

		SharedPreferences my_Pref = getSharedPreferences(MYPREFS, mode);
		count = my_Pref.getInt("savedInfoSize", 0);
		String restoreInfo = my_Pref.getString("saveInfo", "");

		if (restoreInfo != "") {

			String[] restoreInfoArray = restoreInfo.split("=");
			// 알람 어레이리스트별로 끊어서 str에 저장한다.

			for (int i = 0; i < restoreInfoArray.length; i++) {
				String[] eachComponent = restoreInfoArray[i].split("_");
				// 그후 알람정보별로 끊은 것을 다시 쪼개서 저장함.

				tempSaved = new AlarmInfo(eachComponent[0], eachComponent[1],
						eachComponent[2], eachComponent[3], eachComponent[4],
						eachComponent[5], eachComponent[6],
						Integer.parseInt(eachComponent[7]),
						Integer.parseInt(eachComponent[8]),
						Integer.parseInt(eachComponent[9]));

				alarmInfo.add(tempSaved);
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}