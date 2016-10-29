package kr.androidteam.alarm;

import kr.androidteam.alarm.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class AlarmSplash extends Activity{
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		Handler h = new Handler();

		h.postDelayed(new splashhandler(), 1500);

	}

	class splashhandler implements Runnable{

		public void run(){

			startActivity(new Intent (getApplication(), MainActivity.class));

			AlarmSplash.this.finish();

		}

	}

	}
