package kr.androidteam.alarm;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmInfo implements Parcelable{
	/**
	 * 알람 내용을 저장하고 객체끼리 보내주기위해 parcelable 역할(징검다리 역할)을 하는 클래스
	 */
	
	String time,receive,day,message,option,number,check;
	// 여기서 boolean타입으로 받는것을 찾을 수가 없어서 임시로 인트로 확인하게 만듬
	int delete; //지울것이 있으면 1로 체크가 됨
	int active; //리스트의 활성화를 나타냄
	int requestCode;
	public AlarmInfo(String time,String receive, String day, String message,
			String number,String check,String option,int delete,int active,int requestCode){
		this.time=time;
		this.receive=receive;
		this.day=day;
		this.message=message;
		this.check=check;
		this.option=option;
		this.delete=delete;
		this.active=active;
		this.requestCode=requestCode;
		this.number=number;
	}
	
	public AlarmInfo(Parcel source){
		time=source.readString();
		receive=source.readString();
		day=source.readString();
		message=source.readString();
		number=source.readString();
		check=source.readString();
		option=source.readString();
		delete=source.readInt();
		active=source.readInt();
		requestCode=source.readInt();
		
	}
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getDelete() {
		return delete;
	}
	public void setDelete(int delete) {
		this.delete = delete;
	}
	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	
	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(time);
		dest.writeString(receive);
		dest.writeString(day);
		dest.writeString(message);
		dest.writeString(number);
		dest.writeString(check);
		dest.writeString(option);
		dest.writeInt(delete);
		dest.writeInt(active);
		dest.writeInt(requestCode);
	}
	
	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public static Parcelable.Creator<AlarmInfo> CREATOR= new Parcelable.Creator<AlarmInfo>() {

		@Override
		public AlarmInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new AlarmInfo(source);
		}

		@Override
		public AlarmInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new AlarmInfo[size];
		}
		
	};
	}