package src.game;


//import java.awt.Point;
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

public class Shit extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	
	// public variables
	public static final int SHIT_NORMAL = 0;
	public static final int SHIT_CRASHED = 1;
	public static final int SHIT_SHADOW = 2;
	public static final int NUM_OF_SHIT_STATE = 3;

	private static final int SHITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	public Body owner;
	public AgeState ageState;
	public int falldownDamage = 0;
	public int amount = 0;
	public int shitType = 0;
	
	private static final float[] shitSize = {0.4f, 0.7f, 1.0f};
	private static final String[] shitSizeDisplayName = {"小", "中", "大"};

	private static final int value[] = {50,100,300};
	
	private static BufferedImage[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;

	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/";
		final YukkuriType[] name = YukkuriType.values();

		images = new BufferedImage[name.length][NUM_OF_SHIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx, sy;
		
		for(int i = 0; i < name.length; i++) {
			if(name[i].imageDirName.length() == 0) continue;
			
			images[i][SHIT_NORMAL][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun.png"));
			images[i][SHIT_CRASHED][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun2.png"));
			images[i][SHIT_SHADOW][Const.ADULT_INDEX] = ImageIO.read(loader.getResourceAsStream(path+name[i].imageDirName+"/unun-shadow.png"));

			for(int j = 0; j < NUM_OF_SHIT_STATE; j++) {
				imgW[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getWidth(io);
				imgH[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getHeight(io);
				pivX[i][Const.ADULT_INDEX] = imgW[i][Const.ADULT_INDEX] >> 1;
				pivY[i][Const.ADULT_INDEX] = imgH[i][Const.ADULT_INDEX] - 1;

				sx = (int)((float)imgW[i][Const.ADULT_INDEX] * shitSize[1]);
				sy = (int)((float)imgH[i][Const.ADULT_INDEX] * shitSize[1]);
				images[i][j][Const.CHILD_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
				sx = (int)((float)imgW[i][Const.ADULT_INDEX] * shitSize[0]);
				sy = (int)((float)imgH[i][Const.ADULT_INDEX] * shitSize[0]);
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
		StringBuilder ret = new StringBuilder("うんうん");
		ret.append(shitSizeDisplayName[ageState.ordinal()]);
		ret.append("(");
		ret.append(owner.getNameJ());
		ret.append(")");
		return ret.toString();
	}
	
	public BufferedImage getImage() {
		return (images[shitType][getShitState()][ageState.ordinal()]);
	}
	
	public BufferedImage getShadowImage() {
		return (images[shitType][SHIT_SHADOW][ageState.ordinal()]);
	}

	public int getSize() {
		return imgW[shitType][ageState.ordinal()];
	}
	
	public Shit (int initX, int initY, int initZ, Body b, YukkuriType type) {
		objType = Type.SHIT;
		shitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		owner = b;
		ageState = b.getBodyAgeState();
		amount = imgW[shitType][ageState.ordinal()] * 12;
		setRemoved(false);
		setBoundary(pivX[shitType][ageState.ordinal()], pivY[shitType][ageState.ordinal()],
					imgW[shitType][ageState.ordinal()], imgH[shitType][ageState.ordinal()]);
	}

	public int getShitState() {
		if (getAge() >= SHITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}

	public void eatShit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			setRemoved(true);
		}
	}

	public void crushShit() {
		setAge(getAge() + SHITLIMIT[ageState.ordinal()]/2);
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
		return GetMenuTarget.SHIT;
	}

	@Override
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.SHIT;
	}

	public Event clockTick()
	{
		if (!isRemoved()) {
			//age += TICK;
			if (getAge() >= SHITLIMIT[ageState.ordinal()]) {
				setRemoved(true);
			}

			int mapX = Translate.mapW;
			int mapY = Translate.mapH;

			if (!grabbed) {
				if (vx != 0) {
					x += vx;
					if (x < 0) {
						x = 0;
						vx *= -1;
					}
					else if (x > mapX) {
						x = mapX;
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
					else if (y > mapY) {
						y = mapY;
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
								crushShit();
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
