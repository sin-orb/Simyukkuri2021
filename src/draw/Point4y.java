package src.draw;

import java.io.Serializable;

/**
 * awtのPointでなく循環参照なしの独自クラス.
 */
public class Point4y implements Serializable {

	private static final long serialVersionUID = 4990971219226306239L;
	public int x;
	public int y;
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
	public Point4y() {
		x = 0;
		y = 0;
	}
	public Point4y(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public String toString() {
		return "x: " + x + ", y: " + y;
	}
}

