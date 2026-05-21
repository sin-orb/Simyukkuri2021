package org.simyukkuri.entity.core.effect.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.effect.Effect;

/**
 * ホットプレートの煙.
 */
public class BakeSmoke extends Effect {

	private static final long serialVersionUID = 5547702513633744632L;
	private static BufferedImage[] imageLayers;
	private static int imageWidth;
	private static int imageHeight;
	private static int pivotX;
	private static int pivotY;

	/**
	 * イメージをロードする.
	 * 
	 * @param loader ローダ
	 * @param io     イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		// ホットプレートの煙
		imageLayers = new BufferedImage[3];
		for (int i = 0; i < 3; i++) {
			imageLayers[i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "smoke_" + i + ".png");
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
	public BufferedImage getImage() {
		return imageLayers[animeFrame];
	}

	/**
	 * コンストラクタ.
	 */
	public BakeSmoke(int startX, int startY, int startZ, int velocityX, int velocityY, int velocityZ, boolean invert,
			int life, int loop, boolean end, boolean grav, boolean front) {
		super(startX, startY, startZ, velocityX, velocityY, velocityZ, invert, life, loop, end, grav, front);
		setBoundary(pivotX, pivotY, imageWidth, imageHeight);
		interval = 0;
		frames = 3;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public BakeSmoke() {

	}

	/** 煙の画像レイヤー配列を返す。 */
	public static BufferedImage[] getImageLayers() {
		return imageLayers;
	}

	/** 煙の画像レイヤー配列をセットする。 */
	public static void setImageLayers(BufferedImage[] imageLayers) {
		BakeSmoke.imageLayers = imageLayers;
	}
}
