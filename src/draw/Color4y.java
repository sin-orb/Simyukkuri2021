package src.draw;

import java.io.Serializable;

public class Color4y implements Serializable {

	private static final long serialVersionUID = 6628001935213373183L;
	private int red;
	private int blue;
	private int green;
	private int alpha;

	/** 値を0-255の範囲にクランプする */
	private static int clamp(int value) {
		if (value < 0) return 0;
		if (value > 255) return 255;
		return value;
	}

	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = clamp(red);
	}
	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = clamp(blue);
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = clamp(green);
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = clamp(alpha);
	}

	public Color4y() {
		red = 0;
		green = 0;
		blue = 0;
		alpha = 0;
	}

	public Color4y(int red, int green, int blue, int alpha) {
		this.red = clamp(red);
		this.green = clamp(green);
		this.blue = clamp(blue);
		this.alpha = clamp(alpha);
	}
	
	public String toString() {
		return "red: " + red + ", green: " + green + ", blue: " + blue + ", alpha: " + alpha;
	}
}
