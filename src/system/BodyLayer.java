package src.system;

import java.awt.image.BufferedImage;


/**************************************************

	ゆっくりのレイヤー画像テンポラリ

*/
public class BodyLayer {
	
	public BufferedImage[] image;		// 画像実体への参照
	public int[] dir;			// 使用する描画矩形
	public int[] option;		// 汎用
	
	public BodyLayer() {
		image = new BufferedImage[10];
		dir = new int[10];
		option = new int[10];
	}
}




