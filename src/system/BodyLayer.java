package src.system;

import java.awt.image.BufferedImage;


/**************************************************
 * ゆっくりのレイヤー画像テンポラリ
 */
public class BodyLayer {
	/** 画像実体への参照 */
	public BufferedImage[] image;
	/** 使用する描画矩形 */
	public int[] dir;
	/** 汎用 */
	public int[] option;
	/**
	 * コンストラクタ.
	 */
	public BodyLayer() {
		image = new BufferedImage[10];
		dir = new int[10];
		option = new int[10];
	}
}




