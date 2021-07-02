package src.draw;

import java.io.Serializable;

/**
 * Rectangleの循環参照をなくしたクラス.
 */
public class Rectangle4y implements Serializable {
	
	public int x;
	public int y;
	public int width;
	public int height;
	
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Rectangle4y() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}
	
	public Rectangle4y(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
