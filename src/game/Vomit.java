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
import src.system.ResourceUtil;
/**
 * 吐餡クラス.
 */
public class Vomit extends Obj implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	
	// public variables
	/** 通常の吐餡 */
	public static final int VOMIT_NORMAL = 0;
	/** 壊れた吐餡 */
	public static final int VOMIT_CRASHED = 1;
	/** 吐餡の影 */
	public static final int VOMIT_SHADOW = 2;
	/** 吐餡の状態 */
	public static final int NUM_OF_VOMIT_STATE = 3;

	private static final int VOMITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	private String ownerName = null;;
	private AgeState ageState;
	private int falldownDamage = 0;
	private int amount = 0;
	private int vomitType = 0;
	private static final float[] imageSize = {0.25f, 0.5f, 1.0f};
	private static final String[] sizeDisplayName = {ResourceUtil.getInstance().read("game_little"), 
			ResourceUtil.getInstance().read("game_middle"),
			ResourceUtil.getInstance().read("game_big")};
	private static final int value[] = {50,100,300};
	
	private static BufferedImage[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
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
		StringBuilder ret = new StringBuilder(ResourceUtil.getInstance().read("game_toan"));
		ret.append(sizeDisplayName[ageState.ordinal()]);
		ret.append("(");
		ret.append(ownerName);
		ret.append(")");
		return ret.toString();
	}
	/**
	 * イメージを取得する.
	 * @return イメージ
	 */
	public BufferedImage getImage() {
		return (images[vomitType][getVomitState()][ageState.ordinal()]);
	}
	/**
	 * 影のイメージを取得する.
	 * @return 影のイメージ
	 */
	public BufferedImage getShadowImage() {
		return (images[vomitType][VOMIT_SHADOW][ageState.ordinal()]);
	}
	/**
	 * サイズを取得する.
	 * @return サイズ
	 */
	public int getSize() {
		return imgW[vomitType][ageState.ordinal()];
	}
	/**
	 * コンストラクタ
	 * @param initX 初期X座標
	 * @param initY 初期Y座標
	 * @param initZ 初期Z座標
	 * @param b 吐いたゆっくり
	 * @param type 吐餡タイプ
	 */
	public Vomit (int initX, int initY, int initZ, Body b, YukkuriType type) {
		objType = Type.VOMIT;
		vomitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		if(b == null) {
			ageState = AgeState.ADULT;
		} else {
			ownerName = ResourceUtil.IS_JP ? b.getNameJ() : b.getNameE();
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
		calcPos();
		setRemoved(false);
		setBoundary(pivX[vomitType][ageState.ordinal()], pivY[vomitType][ageState.ordinal()],
					imgW[vomitType][ageState.ordinal()], imgH[vomitType][ageState.ordinal()]);
	}
	/**
	 * 成長ステージを取得する.
	 * @return 成長ステージ
	 */
	public AgeState getAgeState() { return ageState; }
	/**
	 * 吐餡の状態を取得する.
	 * @return 吐餡の状態
	 */
	public int getVomitState() {
		if (getAge() >= VOMITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}
	/**
	 * 吐餡を食べる.
	 * @param eatAmount
	 */
	public void eatVomit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			setRemoved(true);
		}
	}
	/**
	 * 吐餡を壊す.
	 */
	public void crushVomit() {
		setAge(getAge() + VOMITLIMIT[ageState.ordinal()]/2);
	}
	/**
	 * 吐餡をキックする.
	 */
	public void kick() {
		int blowLevel[] = {-6, -5, -4};
		kick(0, blowLevel[ageState.ordinal()]*2, blowLevel[ageState.ordinal()]);
	}
	/**
	 * 吐餡の価格を取得する.
	 */
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
	@Override
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
			calcPos();
			return Event.DONOTHING;
		}
		calcPos();
		return Event.REMOVED;
	}
}
