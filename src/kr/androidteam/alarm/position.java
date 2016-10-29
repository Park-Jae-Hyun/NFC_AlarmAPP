package kr.androidteam.alarm;

public class position {

	int x;
	int y;
	int top,bottom,left,right;
	public position(int i, int j) {
		// TODO Auto-generated constructor stub
		this.x=i;
		this.y=j;
	}
	public position(int top,int bottom,int left, int right){
		this.top=top;
		this.bottom=bottom;
		this.left=left;
		this.right=right;
	}

	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getBottom() {
		return bottom;
	}
	public void setBottom(int bottom) {
		this.bottom = bottom;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
