package kr.androidteam.alarm;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/*custom listview를 위한 클래스 extends로 아래와같이 받으면 inflater를 사용안하고 super에 인자값을 건내줌
 * extends에 BaseAdapter를 사용하면 inflater를 사용해서 layout을 지정해줘야함
*/

public class dataAdapter extends ArrayAdapter<AlarmInfo> {

	private Context context;
	private ArrayList<AlarmInfo> alarmInfo;
	private AlarmInfo temp;
	ViewHolder holder;
	
	public dataAdapter(Context context, int layoutResourceId,
			ArrayList<AlarmInfo> alarmInfo) {
		// TODO Auto-generated constructor stub
		super(context, layoutResourceId, alarmInfo);

		this.context = context;
		this.alarmInfo = alarmInfo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = null;

		if (view == null){ 
			view=convertView;
			holder = new ViewHolder(context);
		}

		temp = alarmInfo.get(position);
		holder.setMessage(temp,position);
		view = holder;
		
		return view;
		
		//customview는 솔직히 잘 모르겠음 알아서공부
	}

}
