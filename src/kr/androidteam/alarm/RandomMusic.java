package kr.androidteam.alarm;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

public class RandomMusic {
	Context context;
	ArrayList<String> musicList=new ArrayList<String>();
	MediaPlayer mp=new MediaPlayer();
	int timer=0;
	CountDownTimer thread;
	boolean flag=false;
	public RandomMusic(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void start(){
		String[] mListData=new String[]{MediaColumns._ID , AudioColumns.IS_MUSIC};
		 //private Object ReadmediaTitle;
		  int mediaID;
		  int mediaIsMusic;
		  
		  Cursor cur = context.getContentResolver().query(MediaStore.Audio.Media.
				  EXTERNAL_CONTENT_URI , mListData, null, null, null);
		 //스마트폰 내부 저장소에 저장되어 있는 모든 음악 파일을 가르킨다. 
		 
		  cur.moveToFirst();
		do {

			mediaID = cur.getColumnIndex(MediaColumns._ID);
			mediaIsMusic = cur.getColumnIndex(AudioColumns.IS_MUSIC);

			//노래 파일명을 ID에 저장하고 이 파일이 노래파일인지 아닌지 구별한다.
			
			
			if (cur.getInt(mediaIsMusic) == 1)
				musicList.add(cur.getString(mediaID));
			//만약 노래파일이 맞으면 1이므로 어레이리스트에 저장한다.
			
		} while(cur.moveToNext());
		cur.close();

		
		//저장한 노래파일 리스트를 랜덤함수를 사용해 무작위로 선택한다.
		if (musicList.size() != 0) {
			int position = (int) (Math.random() * 1000 % musicList.size());
		
			Uri musicUri = Uri.withAppendedPath(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""
							+ getMusicID(position));
			
			try {
				if(mp.isPlaying())
					mp.stop();
				
				mp.setDataSource(context, musicUri);
				mp.prepare();
				mp.start();
				
			}
			catch(IllegalStateException e){
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else{
			try {
				if(mp.isPlaying())
					mp.stop();
				mp.create(context, R.raw.song);
				mp.prepare();
				mp.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(thread==null)
		nextMusic();
		
	}

    public int getMusicID(int position) {
        return Integer.parseInt((musicList.get(position)));
    }
    
    
    public void nextMusic(){
		
    	//노래가 끝났을 시 다시 재생
	thread= new CountDownTimer(60*60 * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
           
        	if(!mp.isPlaying() && !flag){
        		mp.seekTo(0);
        		mp.start();
        	}
        }

        public void onFinish() {
           
        }
    };
		thread.start();
		
    }
    @SuppressWarnings("deprecation")
	public void stop(){
    	mp.stop();
    	thread.cancel();
    }
    
    public void pause(){
    	timer=mp.getCurrentPosition();
    	mp.pause();
    	flag=true;
    	}
    public void reStart(){
    	mp.seekTo(timer);
    	mp.start();
    	flag=false;
    }

}
