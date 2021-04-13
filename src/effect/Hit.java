package src.effect;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.base.Effect;
import src.draw.ModLoader;


/****************************************
 *  ヒットエフェクト
 */
public class Hit extends Effect {

	private static final long serialVersionUID = 1L;

	private static BufferedImage[][] images;
	private static int imgW;
	private static int imgH;
	private static int pivX;
	private static int pivY;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		images = new BufferedImage[2][4];
		for(int i = 0; i < 4; i++) {
			images[0][i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "hit_" + i + ".png");
			images[1][i] = ModLoader.flipImage(images[0][i]);
		}

		imgW = images[0][0].getWidth(io);
		imgH = images[0][0].getHeight(io);
		pivX = imgW >> 1;
		pivY = imgH >> 1;
	}
	
	public BufferedImage getImage() {
		return images[direction][animeFrame];
	}

	public Hit(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivX, pivY, imgW, imgH);
		interval = 1;
		frames = 4;
	}
}
