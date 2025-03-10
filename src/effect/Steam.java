package src.effect;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.base.Effect;
import src.draw.ModLoader;


/****************************************
 *  ディフューザーの蒸気
 */
public class Steam extends Effect {

	private static final long serialVersionUID = 5645000843148899776L;
	public static BufferedImage[] images;
	public static int imgW;
	public static int imgH;
	public static int pivX;
	public static int pivY;
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		images = new BufferedImage[11];

		// ディフューザーの蒸気
		images[0] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_green.png");
		images[1] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_white.png");
		images[2] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_orange.png");
		images[3] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_liteblue.png");
		images[4] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_yellow.png");
		images[5] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_black.png");
		images[6] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_red.png");
		images[7] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_blue.png");
		images[8] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_green.png");
		images[9] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_pink.png");
		images[10] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_purple.png");

		imgW = images[0].getWidth(io);
		imgH = images[0].getHeight(io);
		pivX = imgW >> 1;
		pivY = imgH >> 1;
	}
	/**
	 * イメージを取得する.
	 */
	@Override
	@Transient
	public BufferedImage getImage() {
		return images[animeFrame];
	}
	/**
	 * コンストラクタ.
	 */
	public Steam(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivX, pivY, imgW, imgH);
		interval = 0;
		frames = 1;
	}
	
	public Steam() {
		
	}
	public static BufferedImage[] getImages() {
		return images;
	}
	public static void setImages(BufferedImage[] images) {
		Steam.images = images;
	}
}
