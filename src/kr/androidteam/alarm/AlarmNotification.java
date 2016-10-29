package kr.androidteam.alarm;

import kr.androidteam.alarm.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmNotification {
	int icon=R.drawable.time;
	String tickerText="알람등록";
	long when=System.currentTimeMillis();
	Notification notification=new Notification(icon,tickerText,when);
	String serName=Context.NOTIFICATION_SERVICE;
	NotificationManager NM;
	Context context;
	
	public AlarmNotification(Context c){
		context=c;
	}
	
@SuppressWarnings("deprecation")
public void registerNotification(){
	Intent notifiIntent=new Intent(context,MainActivity.class);
	
	PendingIntent pi=PendingIntent.getActivity(context,0,
			notifiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	
	NM=(NotificationManager)context.getSystemService(serName);
		
		notification.flags=Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(context, "알람", "sleep if you can", pi);
		
		NM.notify(1, notification);
		
	}
	
	public void notificationCancel(){
		NM.cancel(1);
	}

}
