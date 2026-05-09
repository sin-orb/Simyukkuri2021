package src.effect;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.base.Effect;
import src.draw.ModLoader;


/****************************************
 *  ミキサー
 */
public class Mix extends Effect {

	private static final long serialVersionUID = 3746115855650888135L;
	private static BufferedImage[] imageLayers;
	private static int imageWidth;
	private static int imageHeight;
	private static int pivotX;
	private static int pivotY;
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		imageLayers = new BufferedImage[3];
		for(int i = 0; i < 3; i++) {
			imageLayers[i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "mix_" + i + ".png");
		}

		imageWidth = imageLayers[0].getWidth(io);
		imageHeight = imageLayers[0].getHeight(io);
		pivotX = imageWidth >> 1;
		pivotY = imageHeight - 1;
	}
	/**
	 * イメージを取得する.
	 */
	@Override
	@Transient
	public BufferedImage getImage() {
		return imageLayers[animeFrame];
	}
	/**
	 * コンストラクタ.
	 */
	public Mix(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivotX, pivotY, imageWidth, imageHeight);
		interval = 0;
		frames = 3;
	}
	
	public Mix() {
		
	}
	public static BufferedImage[] getImageLayers() {
		return imageLayers;
	}

	public static void setImageLayers(BufferedImage[] imageLayers) {
		Mix.imageLayers = imageLayers;
	}
}
