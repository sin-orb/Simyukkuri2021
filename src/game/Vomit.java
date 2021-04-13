package src.game;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.Const;
import src.base.Body;
import src.base.Obj;
import src.draw.ModLoader;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Event;
import src.enums.Type;
import src.enums.YukkuriType;
import src.item.Barrier;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;

public class Vomit extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	
	// public variables
	public static final int VOMIT_NORMAL = 0;
	public static final int VOMIT_CRASHED = 1;
	public static final int VOMIT_SHADOW = 2;
	public static final int NUM_OF_VOMIT_STATE = 3;

	private static final int VOMITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	private Body owner;
	private AgeState ageState;
	private int falldownDamage = 0;
	private int amount = 0;
	private int vomitType = 0;
	private static final float[] imageSize = {0.25f, 0.5f, 1.0f};
	private static final String[] sizeDisplayName = {"小", "中", "大"};
	private static final int value[] = {50,100,300};
	
	private static BufferedImage[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/";
		final YukkuriType[] name = YukkuriType.values();
		
		images = new BufferedImage[name.length][NUM_OF_VOMIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx, sy;

		for(int i = 0; i < name.length; i++) {
			if(name[i].imageDirName.length() == 0) continue;
			
			images[i][VOMIT_NORMAL][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan.png"));
			images[i][VOMIT_CRASHED][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan2.png"));
			images[i][VOMIT_SHADOW][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/toan_shadow.png"));

			for(int j = 0; j < NUM_OF_VOMIT_STATE; j++) {
				imgW[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getWidth(io);
				imgH[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getHeight(io);
				pivX[i][Const.ADULT_INDEX] = imgW[i][Const.ADULT_INDEX] >> 1;
				pivY[i][Const.ADULT_INDEX] = imgH[i][Const.ADULT_INDEX] - 1;

				sx = (int)((float)imgW[i][Const.ADULT_INDEX] * imageSize[1]);
				sy = (int)((float)imgH[i][Const.ADULT_INDEX] * imageSize[1]);
				images[i][j][Const.CHILD_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
				sx = (int)((float)imgW[i][Const.ADULT_INDEX] * imageSize[0]);
				sy = (int)((float)imgH[i][Const.ADULT_INDEX] * imageSize[0]);
				images[i][j][Const.BABY_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
			}
		}
		for(int i = 0; i < name.length; i++) {
			for(int j = 0; j < 3; j++) {
				if(images[i][0][j] == null) continue;
				
				imgW[i][j] = images[i][0][j].getWidth(io);
				imgH[i][j] = images[i][0][j].getHeight(io);
				pivX[i][j] = imgW[i][j] >> 1;
				pivY[i][j] = imgH[i][j] - 1;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder("吐餡");
		ret.append(sizeDisplayName[ageState.ordinal()]);
		if(owner != null) {
			ret.append("(");
			ret.append(owner.getNameJ());
			ret.append(")");
		}
		return ret.toString();
	}

	public BufferedImage getImage() {
		return (images[vomitType][getVomitState()][ageState.ordinal()]);
	}
	
	public BufferedImage getShadowImage() {
		return (images[vomitType][VOMIT_SHADOW][ageState.ordinal()]);
	}

	public int getSize() {
		return imgW[vomitType][ageState.ordinal()];
	}

	public Vomit (int initX, int initY, int initZ, Body b, YukkuriType type) {
		objType = Type.VOMIT;
		vomitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		if(b == null) {
			owner = null;
			ageState = AgeState.ADULT;
		} else {
			owner = b;
			ageState = b.getBodyAgeState();
		}
		switch (ageState) {
		case BABY:
			amount = 100;
			break;
		case CHILD:
			amount = 100*2;
			break;
		case ADULT:
			amount = 100*4;
			break;
		}
		setRemoved(false);
		setBoundary(pivX[vomitType][ageState.ordinal()], pivY[vomitType][ageState.ordinal()],
					imgW[vomitType][ageState.ordinal()], imgH[vomitType][ageState.ordinal()]);
	}

	public AgeState getAgeState() { return ageState; }

	public int getVomitState() {
		if (getAge() >= VOMITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}

	public void eatVomit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			setRemoved(true);
		}
	}

	public void crushVomit() {
		setAge(getAge() + VOMITLIMIT[ageState.ordinal()]/2);
	}
	
	public void kick() {
		int blowLevel[] = {-6, -5, -4};
		kick(0, blowLevel[ageState.ordinal()]*2, blowLevel[ageState.ordinal()]);
	}

	public int getValue() {
		return value[ageState.ordinal()];
	}
	
	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.VOMIT;
	}

	@Override
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.NONE;
	}

	public Event clockTick()
	{
		if (!isRemoved()) {
			//age += TICK;
			if (getAge() >= VOMITLIMIT[ageState.ordinal()]) {
				setRemoved(true);
			}
			if (!grabbed) {
				if (vx != 0) {
					x += vx;
					if (x < 0) {
						x = 0;
						vx *= -1;
					}
					else if (x > Translate.mapW) {
						x = Translate.mapW;
						vx *= -1;
					}
					else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
						x -= vx;
						vx = 0;
					}
				}
				if (vy != 0) {
					y += vy;
					if (y < 0) {
						y = 0;
						vy = 0;
					}
					else if (y > Translate.mapH) {
						y = Translate.mapH;
						vy = 0;
					}
					else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
						y -= vy;
						vy = 0;
					}
				}
				if (z != 0 || vz != 0) {
					vz += 1;
					z -= vz;
					falldownDamage += vz;
					if( !bFallingUnderGround)
					{
						if (z <= nMostDepth) {
							if (falldownDamage > 10) {
								crushVomit();
							}
							z = nMostDepth;
							vx = 0;
							vy = 0;
							vz = 0;
							falldownDamage = 0;
						}
					}
				}
			}
			return Event.DONOTHING;
		}
		return Event.REMOVED;
	}
}
