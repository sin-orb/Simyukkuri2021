package src.game;


//import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;

import javax.imageio.ImageIO;

import src.Const;
import src.base.Body;
import src.base.Obj;
import src.draw.ModLoader;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Event;
import src.enums.Numbering;
import src.enums.Type;
import src.enums.YukkuriType;
import src.item.Barrier;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;
import src.system.ResourceUtil;
/**
 * うんうんのクラス.
 */
public class Shit extends Obj implements java.io.Serializable {

	private static final long serialVersionUID = 8099262268179611867L;
	// public variables
	/** 通常うんうん */
	public static final int SHIT_NORMAL = 0;
	/** ゆ下痢 */
	public static final int SHIT_CRASHED = 1;
	/** うんうんの影 */
	public static final int SHIT_SHADOW = 2;
	/** うんうんの状態数 */
	public static final int NUM_OF_SHIT_STATE = 3;

	private static final int SHITLIMIT[] = {100*24*2, 100*24*4, 100*24*8};
	/** うんうんをしたゆっくりの名前。toString用 */
	public String ownerName;
	/** うんうんをしたゆっくりのUniqueID。誰がしたうんうんか？で子供のだったらトイレに運ぶ、とかの処理が可能 */
	public int ownerId;
	/** 赤ゆ/子ゆ/成ゆ、どのゆっくりがうんうんをしたか */
	public AgeState ageState;
	/** 落下時のダメージ */
	public int falldownDamage = 0;
	/** うんうんの量 */
	public int amount = 0;
	/** うんうんタイプ（あんこ、カスタード、クリーム等） */
	public int shitType = 0;
	
	private static final float[] shitSize = {0.4f, 0.7f, 1.0f};
	private static final String[] shitSizeDisplayName = {ResourceUtil.getInstance().read("game_little"), 
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
		StringBuilder ret = new StringBuilder(ResourceUtil.getInstance().read("system_unun"));
		ret.append(shitSizeDisplayName[ageState.ordinal()]);
		ret.append("(");
		ret.append(ownerName);
		ret.append(")");
		return ret.toString();
	}
	/**
	 * イメージを取得する.
	 * @return イメージ
	 */
	@Transient
	public BufferedImage getImage() {
		return (images[shitType][getShitState()][ageState.ordinal()]);
	}
	/**
	 * 影のイメージを取得する.
	 * @return 影のイメージ
	 */
	@Transient
	public BufferedImage getShadowImage() {
		return (images[shitType][SHIT_SHADOW][ageState.ordinal()]);
	}

	@Transient
	public int getSize() {
		return imgW[shitType][ageState.ordinal()];
	}
	/**
	 * コンストラクタ.
	 * @param initX 初期X座標
	 * @param initY 初期Y座標
	 * @param initZ 初期Z座標
	 * @param b うんうんしたゆっくり
	 * @param type うんうんタイプ
	 */
	public Shit (int initX, int initY, int initZ, Body b, YukkuriType type) {
		objId = Numbering.INSTANCE.numberingObjId();
		objType = Type.SHIT;
		shitType = type.ordinal();
		ownerName = ResourceUtil.IS_JP ? b.getNameJ() : b.getNameE();
		ownerId = b.getUniqueID();
		x = initX;
		y = initY;
		z = initZ;
		ageState = b.getBodyAgeState();
		amount = imgW[shitType][ageState.ordinal()] * 12;
		setRemoved(false);
		setBoundary(pivX[shitType][ageState.ordinal()], pivY[shitType][ageState.ordinal()],
					imgW[shitType][ageState.ordinal()], imgH[shitType][ageState.ordinal()]);
	}
	
	public Shit() {
		
	}
	
	/**
	 * うんうんの状態を取得する.
	 * @return うんうんの状態
	 */
	@Transient
	public int getShitState() {
		if (getAge() >= SHITLIMIT[ageState.ordinal()]/4) {
			return 1;
		}
		return 0;
	}
	/**
	 * うんうんが食べられたときの処理
	 * @param eatAmount 食べられた量
	 */
	public void eatShit(int eatAmount) {
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
			remove();
		}
	}
	/**
	 * うんうんを破壊しゆ下痢にする.
	 */
	public void crushShit() {
		setAge(getAge() + SHITLIMIT[ageState.ordinal()]/2);
	}
	/**
	 * うんうんをキックする,
	 */
	public void kick() {
		int blowLevel[] = {-6, -5, -4};
		kick(0, blowLevel[ageState.ordinal()]*2, blowLevel[ageState.ordinal()]);
	}
	/**
	 * うんうん量を取得する.
	 */
	@Transient
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
	@Override
	public Event clockTick()
	{
		if (!isRemoved()) {
			//age += TICK;
			if (getAge() >= SHITLIMIT[ageState.ordinal()]) {
				remove();
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
			calcPos();
			return Event.DONOTHING;
		}
		calcPos();
		return Event.REMOVED;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public AgeState getAgeState() {
		return ageState;
	}

	public void setAgeState(AgeState ageState) {
		this.ageState = ageState;
	}

	public int getFalldownDamage() {
		return falldownDamage;
	}

	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getShitType() {
		return shitType;
	}

	public void setShitType(int shitType) {
		this.shitType = shitType;
	}
	
	
}
