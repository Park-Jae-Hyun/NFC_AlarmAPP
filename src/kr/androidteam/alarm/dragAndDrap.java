package kr.androidteam.alarm;

import kr.androidteam.alarm.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class dragAndDrap extends View {

	Point point; // 포인트
	Context c;
	int width; // 핸드폰 화면의 넓이
	int height; // 핸드폰 화면의 높이
	Rect[][] number = new Rect[2][5]; // 번호판
	Rect[] blank = new Rect[4]; // 시간을 받는 판
	Bitmap one, two, three, four, five, six, seven, eight, nine, zero;
	Paint p;
	Paint numberP;
	position[][] position = new position[2][5]; // 각 rect의 좌표를 가지는 함수
	int touchedX = 10, touchedY = 10; // 터치받은 번호의 어레이를 각각 기억하는 함수 디폴트는 10으로 지정
	int click = 0; // 어느 숫자인지 기억하는 함수

	boolean[] blankCheck = new boolean[4]; // 빈칸에 번호가 들어갈 수 있는지 확인하는 불린타입
	int[] saveTime = new int[4]; // 각 받은 빈칸에 시간을 지정함.
	TextView timer;
	boolean intentFlag = false;

	public dragAndDrap(Context context) {
		super(context);
		c = context;
		init();
	}

	public dragAndDrap(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		c = context;
		init();
	}

	public void init() {
		for (int i = 0; i < 4; i++)
			blankCheck[i] = false;

		Display display = ((WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth() - 200;
		height = display.getHeight() - 160;

		point = new Point();

		p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(width / 100);
		p.setColor(Color.GRAY);
		DashPathEffect dashPath = new DashPathEffect(new float[] { 20, 3 }, 1);
		p.setPathEffect(dashPath);

		numberP = new Paint();
		numberP.setStyle(Paint.Style.STROKE);
		numberP.setStrokeWidth(0);
		numberP.setColor(Color.WHITE);

		// 빈칸의 크기를 지정해줌
		for (int i = 0; i < 4; i++) {
			blank[i] = new Rect();

			blank[i].top = height * 1 / 80;
			blank[i].bottom = height * 1 / 8;
			blank[i].left = width * i / 4 + 100 + 10;
			blank[i].right = width * (i + 1) / 4 + 100;
		}

		height += 300;
		// 숫자 패드의 크기를 지정해줌
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				number[i][j] = new Rect();

				number[i][j].top = height * (1 + i) / 11;
				number[i][j].bottom = height * (2 + i) / 11;
				number[i][j].left = width * j / 5 + 100;
				number[i][j].right = width * (j + 1) / 5 + 100;

				position[i][j] = new position(number[i][j].top,
						number[i][j].bottom, number[i][j].left,
						number[i][j].right);
			}
		}

		height -= 300;
		// 해당 비트맵을 핸드폰 해상도에 맞게 줄임
		one = BitmapFactory.decodeResource(c.getResources(), R.drawable.one);
		one = Bitmap.createScaledBitmap(one, width / 5, height / 8, true);

		two = BitmapFactory.decodeResource(c.getResources(), R.drawable.two);
		two = Bitmap.createScaledBitmap(two, width / 5, height / 8, true);

		three = BitmapFactory
				.decodeResource(c.getResources(), R.drawable.three);
		three = Bitmap.createScaledBitmap(three, width / 5, height / 8, true);

		four = BitmapFactory.decodeResource(c.getResources(), R.drawable.four);
		four = Bitmap.createScaledBitmap(four, width / 5, height / 8, true);

		five = BitmapFactory.decodeResource(c.getResources(), R.drawable.five);
		five = Bitmap.createScaledBitmap(five, width / 5, height / 8, true);

		six = BitmapFactory.decodeResource(c.getResources(), R.drawable.six);
		six = Bitmap.createScaledBitmap(six, width / 5, height / 8, true);

		seven = BitmapFactory
				.decodeResource(c.getResources(), R.drawable.seven);
		seven = Bitmap.createScaledBitmap(seven, width / 5, height / 8, true);

		eight = BitmapFactory
				.decodeResource(c.getResources(), R.drawable.eight);
		eight = Bitmap.createScaledBitmap(eight, width / 5, height / 8, true);

		eight = BitmapFactory
				.decodeResource(c.getResources(), R.drawable.eight);
		eight = Bitmap.createScaledBitmap(eight, width / 5, height / 8, true);

		nine = BitmapFactory.decodeResource(c.getResources(), R.drawable.nine);
		nine = Bitmap.createScaledBitmap(nine, width / 5, height / 8, true);

		zero = BitmapFactory.decodeResource(c.getResources(), R.drawable.zero);
		zero = Bitmap.createScaledBitmap(zero, width / 5, height / 8, true);

	}

	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		int count = 0;

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 5; j++) {
					// 각 rect의 번호에 터치가 되었는지 확인
					if (number[i][j].contains((int) eventX, (int) eventY)) {
						touchedX = i;
						touchedY = j;
						// 터치된 번호를 click함수에 저장
						click = 1 + j + 5 * i;

						if (click == 10)
							click = 0;

						number[i][j].top = (int) eventY
								- ((position[i][j].getBottom() - position[i][j]
										.getTop()) / 2);

						number[i][j].left = (int) eventX
								- ((position[i][j].getRight() - position[i][j]
										.getLeft()) / 2);
						count = 1;
						break;
					}
				}
				if (count == 1)
					break;
			}
			// 눌리지 않았으면 invalidate시키지 않음
			if (count != 0 && touchedX != 10 && touchedY != 10)
				invalidate(); // Tell View that the canvas needs to be redrawn

			break;

		case MotionEvent.ACTION_MOVE:
			// rect 좌표를 터치된 좌표로 바꿈
			if (touchedX != 10 && touchedY != 10) {

				number[touchedX][touchedY].top = (int) eventY
						- ((position[touchedX][touchedY].getBottom() - position[touchedX][touchedY]
								.getTop()) / 2);

				number[touchedX][touchedY].left = (int) eventX
						- ((position[touchedX][touchedY].getRight() - position[touchedX][touchedY]
								.getLeft()) / 2);
				invalidate(); // Tell View that the canvas needs to be redrawn
			}

			break;

		case MotionEvent.ACTION_UP:

			if (touchedX != 10 && touchedY != 10) {

				// 터치가 뗀 후 빈칸에 들어갔는지 아닌지 확인
				for (int i = 0; i < 4; i++)
					if (blank[i].contains((int) eventX, (int) eventY)) {

						blankCheck[i] = true;
						// exception 검사
						if (checkTimeSpace(i, click))
							saveTime[i] = click;
						break;
					}
				// 초기화
				number[touchedX][touchedY].top = position[touchedX][touchedY]
						.getTop();
				number[touchedX][touchedY].left = position[touchedX][touchedY]
						.getLeft();

				touchedX = 10;
				touchedY = 10;

				invalidate(); // Tell View that the canvas needs to be redrawn
			}

			break;
		}

		return true;
	}

	public boolean checkTimeSpace(int which, int number) {

		// 맨 처음이 0일때 00시가 되면 안되고 2보다 커도 안되며 2일때 4이상의 수가 나와도 안됨
		if (which == 0) {

			if (number > 2) {
				blankCheck[0] = false;
				return false;
			}

			else if (blankCheck[1] && saveTime[1] > 3 && number == 2) {
				blankCheck[1] = false;
				return false;
			}

		}
		// 두번째일 때 위에서 적은 에러처리와 비슷함
		else if (which == 1) {

			if (!blankCheck[0]) {
				blankCheck[1] = false;
				return false;
			}

			else if (blankCheck[0] && saveTime[0] == 2 && number > 3) {
				blankCheck[1] = false;
				return false;

			}
		}

		else if (which == 2) {
			if (number > 5) {
				blankCheck[2] = false;
				return false;
			}
		}

		return true;
	}

	protected void drawBlank(Canvas canvas, int th, int click) {

		if (click == 1)
			canvas.drawBitmap(one, blank[th].left, blank[th].top, null);
		else if (click == 2)
			canvas.drawBitmap(two, blank[th].left, blank[th].top, null);
		else if (click == 3)
			canvas.drawBitmap(three, blank[th].left, blank[th].top, null);
		else if (click == 4)
			canvas.drawBitmap(four, blank[th].left, blank[th].top, null);
		else if (click == 5)
			canvas.drawBitmap(five, blank[th].left, blank[th].top, null);
		else if (click == 6)
			canvas.drawBitmap(six, blank[th].left, blank[th].top, null);
		else if (click == 7)
			canvas.drawBitmap(seven, blank[th].left, blank[th].top, null);
		else if (click == 8)
			canvas.drawBitmap(eight, blank[th].left, blank[th].top, null);
		else if (click == 9)
			canvas.drawBitmap(nine, blank[th].left, blank[th].top, null);
		else if (click == 0)
			canvas.drawBitmap(zero, blank[th].left, blank[th].top, null);
		canvas.save();

	}

	protected void onDraw(Canvas canvas) {

		for (int i = 0; i < 4; i++)
			if (blankCheck[i])
				drawBlank(canvas, i, saveTime[i]);

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 5; j++)
				canvas.drawRect(number[i][j], numberP);

		for (int i = 0; i < 4; i++)
			canvas.drawRect(blank[i], p);

		if (intentFlag) {

			for (int i = 0; i < 4; i++)
				drawBlank(canvas, i, saveTime[i]);
			intentFlag = false;
		}
		canvas.drawBitmap(one, number[0][0].left, number[0][0].top, null);
		canvas.drawBitmap(two, number[0][1].left, number[0][1].top, null);
		canvas.drawBitmap(three, number[0][2].left, number[0][2].top, null);
		canvas.drawBitmap(four, number[0][3].left, number[0][3].top, null);
		canvas.drawBitmap(five, number[0][4].left, number[0][4].top, null);

		canvas.drawBitmap(six, number[1][0].left, number[1][0].top, null);
		canvas.drawBitmap(seven, number[1][1].left, number[1][1].top, null);
		canvas.drawBitmap(eight, number[1][2].left, number[1][2].top, null);
		canvas.drawBitmap(nine, number[1][3].left, number[1][3].top, null);
		canvas.drawBitmap(zero, number[1][4].left, number[1][4].top, null);
		canvas.save();
	}

	public String getTime() {
		int count = 0;
		String time = "";
		for (int i = 0; i < 4; i++)
			if (blankCheck[i])
				count++;

		if (count == 4)
			time += saveTime[0] + "" + saveTime[1] + ":" + saveTime[2] + ""
					+ saveTime[3];

		return time;

	}

	public void setStart(String hour, String minute) {
		intentFlag = true;
		char temp;
		for (int i = 0; i < 2; i++) {
			blankCheck[i] = true;
			temp = hour.charAt(i);
			saveTime[i] = Integer.parseInt("" + temp);
		}

		for (int i = 0; i < 2; i++) {
			blankCheck[i + 2] = true;
			temp = minute.charAt(i);
			saveTime[i + 2] = Integer.parseInt("" + temp);
		}
	}

}
