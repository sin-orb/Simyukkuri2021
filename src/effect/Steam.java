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
		
		imageLayers = new BufferedImage[11];

		// ディフューザーの蒸気
		imageLayers[0] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_green.png");
		imageLayers[1] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_white.png");
		imageLayers[2] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_orange.png");
		imageLayers[3] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_liteblue.png");
		imageLayers[4] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_yellow.png");
		imageLayers[5] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_black.png");
		imageLayers[6] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_red.png");
		imageLayers[7] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_blue.png");
		imageLayers[8] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_green.png");
		imageLayers[9] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_pink.png");
		imageLayers[10] = ModLoader.loadItemImage(loader, "effect" + File.separator + "steam_purple.png");

		imageWidth = imageLayers[0].getWidth(io);
		imageHeight = imageLayers[0].getHeight(io);
		pivotX = imageWidth >> 1;
		pivotY = imageHeight >> 1;
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
	public Steam(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivotX, pivotY, imageWidth, imageHeight);
		interval = 0;
		frames = 1;
	}
	
	public Steam() {
		
	}
	public static BufferedImage[] getImageLayers() {
		return imageLayers;
	}

	public static void setImageLayers(BufferedImage[] imageLayers) {
		Steam.imageLayers = imageLayers;
	}
}
