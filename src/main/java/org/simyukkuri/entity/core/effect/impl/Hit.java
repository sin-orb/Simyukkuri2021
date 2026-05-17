package org.simyukkuri.entity.core.effect.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.effect.Effect;

/****************************************
 * ヒットエフェクト
 */
public class Hit extends Effect {

	private static final long serialVersionUID = -4151167914114725276L;
	private static BufferedImage[][] imageLayers;
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

		imageLayers = new BufferedImage[2][4];
		for (int i = 0; i < 4; i++) {
			imageLayers[0][i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "hit_" + i + ".png");
			imageLayers[1][i] = ModLoader.flipImage(imageLayers[0][i]);
		}

		imageWidth = imageLayers[0][0].getWidth(io);
		imageHeight = imageLayers[0][0].getHeight(io);
		pivotX = imageWidth >> 1;
		pivotY = imageHeight >> 1;
	}

	/**
	 * イメージを取得する.
	 */
	@Override
	@Transient
	public BufferedImage getImage() {
		return imageLayers[direction][animeFrame];
	}

	/**
	 * コンストラクタ.
	 */
	public Hit(int startX, int startY, int startZ, int velocityX, int velocityY, int velocityZ, boolean invert,
			int life, int loop, boolean end, boolean grav, boolean front) {
		super(startX, startY, startZ, velocityX, velocityY, velocityZ, invert, life, loop, end, grav, front);
		setBoundary(pivotX, pivotY, imageWidth, imageHeight);
		interval = 1;
		frames = 4;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Hit() {

	}

	/** ヒットエフェクトの画像レイヤー配列を返す。 */
	public static BufferedImage[][] getImageLayers() {
		return imageLayers;
	}

	/** ヒットエフェクトの画像レイヤー配列をセットする。 */
	public static void setImageLayers(BufferedImage[][] imageLayers) {
		Hit.imageLayers = imageLayers;
	}
}
