package src.draw;

import java.io.Serializable;

public class Color4y implements Serializable {
	public int red;
	public int blue;
	public int green;
	public int alpha;
	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}
	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = blue;
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public Color4y() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 0;
	}
	
	public Color4y(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
}
