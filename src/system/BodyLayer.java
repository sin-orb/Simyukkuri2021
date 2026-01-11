package src.system;

import java.awt.image.BufferedImage;


/**************************************************
 * ゆっくりのレイヤー画像テンポラリ
 */
public class BodyLayer {
	/** 画像実体への参照 */
	private transient BufferedImage[] image;
	/** 使用する描画矩形 */
	private int[] dir;
	/** 汎用 */
	private int[] option;
	/**
	 * コンストラクタ.
	 */
	public BodyLayer() {
		image = new BufferedImage[10];
		dir = new int[10];
		option = new int[10];
	}
	
	public BufferedImage[] getImage() {
		return image;
	}
	
	public void setImage(BufferedImage[] image) {
		this.image = image;
	}
	
	public int[] getDir() {
		return dir;
	}
	
	public void setDir(int[] dir) {
		this.dir = dir;
	}
	
	public int[] getOption() {
		return option;
	}
	
	public void setOption(int[] option) {
		this.option = option;
	}
}




