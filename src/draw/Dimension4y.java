package src.draw;

import java.io.Serializable;

/**
 * awt代替のDimension
 */
public class Dimension4y implements Serializable {

	private static final long serialVersionUID = -7555703845901421580L;
	private int width;
	private int height;
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
	
	public String toString() {
		return "width :" + width + ", height:" + height;
	}
}
