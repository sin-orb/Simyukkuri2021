package src.draw;

import java.io.Serializable;

/**
 * awt代替のDimension
 */
public class Dimension4y implements Serializable {

	public int width;
	public int height;
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
	public Dimension4y() {
		width = 0;
		height = 0;
	}
	public Dimension4y(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
