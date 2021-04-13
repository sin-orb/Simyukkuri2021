package src.effect;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.base.Effect;
import src.draw.ModLoader;


/****************************************
 *  ホットプレート
 */
public class BakeSmoke extends Effect {

	private static final long serialVersionUID = 1L;

	private static BufferedImage[] images;
	private static int imgW;
	private static int imgH;
	private static int pivX;
	private static int pivY;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		// ホットプレートの煙
		images = new BufferedImage[3];
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "smoke_" + i + ".png");
		}
		imgW = images[0].getWidth(io);
		imgH = images[0].getHeight(io);
		pivX = imgW >> 1;
		pivY = imgH - 1;
	}
	
	public BufferedImage getImage() {
		return images[animeFrame];
	}

	public BakeSmoke(int sX, int sY, int sZ, int vX, int vY, int vZ, boolean invert,
						int life, int loop, boolean end, boolean grav, boolean front) {
		super(sX, sY, sZ, vX, vY, vZ, invert, life, loop, end, grav, front);
		setBoundary(pivX, pivY, imgW, imgH);
		interval = 0;
		frames = 3;
	}
}