package org.simyukkuri.entity.core.world.mobile;

//import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.simyukkuri.Const;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Numbering;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameText;

/**
 * うんうんのクラス.
 */
public class Shit extends Entity {

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

	private static final int SHITLIMIT[] = { 100 * 24 * 2, 100 * 24 * 4, 100 * 24 * 8 };
	/** うんうんをしたゆっくりの名前。toString用 */
	private String ownerName;
	/** うんうんをしたゆっくりのUniqueID。誰がしたうんうんか？で子供のだったらトイレに運ぶ、とかの処理が可能 */
	private int ownerId;
	/** 赤ゆ/子ゆ/成ゆ、どのゆっくりがうんうんをしたか */
	private AgeState ageState;
	/** 落下時のダメージ */
	private int falldownDamage = 0;
	/** うんうんの量 */
	private int amount = 0;
	/** うんうんタイプ（あんこ、カスタード、クリーム等） */
	private int shitType = 0;

	private static final float[] shitSize = { 0.4f, 0.7f, 1.0f };
	private static final String[] shitSizeDisplayName = { GameText.read("game_little"),
			GameText.read("game_middle"),
			GameText.read("game_big") };

	private static final int value[] = { 50, 100, 300 };

	private static BufferedImage[][][] images = null;
	private static int[][] imgW = null;
	private static int[][] imgH = null;
	private static int[][] pivX = null;
	private static int[][] pivY = null;

	/**
	 * イメージをロードする.
	 * 
	 * @param loader ローダ
	 * @param io     イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/";
		final YukkuriType[] name = YukkuriType.values();

		images = new BufferedImage[name.length][NUM_OF_SHIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx, sy;

		for (int i = 0; i < name.length; i++) {
			if (name[i].getImageDirName().length() == 0)
				continue;

			images[i][SHIT_NORMAL][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/unun.png"));
			images[i][SHIT_CRASHED][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/unun2.png"));
			images[i][SHIT_SHADOW][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/unun-shadow.png"));

			for (int j = 0; j < NUM_OF_SHIT_STATE; j++) {
				imgW[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getWidth(io);
				imgH[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getHeight(io);
				pivX[i][Const.ADULT_INDEX] = imgW[i][Const.ADULT_INDEX] >> 1;
				pivY[i][Const.ADULT_INDEX] = imgH[i][Const.ADULT_INDEX] - 1;

				sx = (int) ((float) imgW[i][Const.ADULT_INDEX] * shitSize[1]);
				sy = (int) ((float) imgH[i][Const.ADULT_INDEX] * shitSize[1]);
				images[i][j][Const.CHILD_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
				sx = (int) ((float) imgW[i][Const.ADULT_INDEX] * shitSize[0]);
				sy = (int) ((float) imgH[i][Const.ADULT_INDEX] * shitSize[0]);
				images[i][j][Const.BABY_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
			}
		}
		for (int i = 0; i < name.length; i++) {
			for (int j = 0; j < 3; j++) {
				if (images[i][0][j] == null)
					continue;
				imgW[i][j] = images[i][0][j].getWidth(io);
				imgH[i][j] = images[i][0][j].getHeight(io);
				pivX[i][j] = imgW[i][j] >> 1;
				pivY[i][j] = imgH[i][j] - 1;
			}
		}
	}

	/** うんうんのサイズ・排出者名を含む文字列表現を返す。 */
	@Override
	public String toString() {
		String base = GameText.read("system_unun");
		StringBuilder ret = new StringBuilder(base == null ? "" : base);
		ret.append(shitSizeDisplayName[ageState.ordinal()]);
		ret.append("(");
		ret.append(ownerName == null ? "Unknown" : ownerName);
		ret.append(")");
		return ret.toString();
	}

	/**
	 * イメージを取得する.
	 * 
	 * @return イメージ
	 */
	@Transient
	public BufferedImage getImage() {
		return (images[shitType][getShitState()][ageState.ordinal()]);
	}

	/**
	 * 影のイメージを取得する.
	 * 
	 * @return 影のイメージ
	 */
	@Transient
	public BufferedImage getShadowImage() {
		return (images[shitType][SHIT_SHADOW][ageState.ordinal()]);
	}

	/** うんうんの画像幅（サイズ）を返す。 */
	@Transient
	public int getSize() {
		return imgW[shitType][ageState.ordinal()];
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param initX 初期X座標
	 * @param initY 初期Y座標
	 * @param initZ 初期Z座標
	 * @param b     うんうんしたゆっくり
	 * @param type  うんうんタイプ
	 */
	public Shit(int initX, int initY, int initZ, Yukkuri b, YukkuriType type) {
		objId = Numbering.INSTANCE.numberingObjId();
		objType = Type.SHIT;
		shitType = type.ordinal();
		ownerName = GameLocale.isJapanese() ? b.getNameJ() : b.getNameE();
		ownerId = b.getUniqueID();
		x = initX;
		y = initY;
		z = initZ;
		ageState = b.getAgeState();
		amount = imgW[shitType][ageState.ordinal()] * 12;
		setRemoved(false);
		setBoundary(pivX[shitType][ageState.ordinal()], pivY[shitType][ageState.ordinal()],
				imgW[shitType][ageState.ordinal()], imgH[shitType][ageState.ordinal()]);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Shit() {
		ageState = AgeState.ADULT;
		ownerName = "Unknown";
	}

	/**
	 * うんうんの状態を取得する.
	 * 
	 * @return うんうんの状態
	 */
	@Transient
	public int getShitState() {
		if (getAge() >= SHITLIMIT[ageState.ordinal()] / 4) {
			return 1;
		}
		return 0;
	}

	/**
	 * うんうんが食べられたときの処理
	 * 
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
		setAge(getAge() + SHITLIMIT[ageState.ordinal()] / 2);
	}

	/**
	 * うんうんをキックする,
	 */
	public void kick() {
		int blowLevel[] = { -6, -5, -4 };
		kick(0, blowLevel[ageState.ordinal()] * 2, blowLevel[ageState.ordinal()]);
	}

	/**
	 * うんうん量を取得する.
	 */
	@Transient
	public int getValue() {
		return value[ageState.ordinal()];
	}

	/** 取得ポップアップを持つかを返す（常に SHIT）。 */
	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.SHIT;
	}

	/** 使用ポップアップを持つかを返す（常に SHIT）。 */
	@Override
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.SHIT;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public TickResult clockTick() {
		if (!isRemoved()) {
			// age += TICK;
			if (getAge() >= SHITLIMIT[ageState.ordinal()]) {
				remove();
			}

			int mapX = Translate.getWorldWidth();
			int mapY = Translate.getWorldHeight();

			if (!grabbed) {
				if (vx != 0) {
					x += vx;
					if (x < 0) {
						x = 0;
						vx *= -1;
					} else if (x > mapX) {
						x = mapX;
						vx *= -1;
					} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
						x -= vx;
						vx = 0;
					}
				}
				if (vy != 0) {
					y += vy;
					if (y < 0) {
						y = 0;
						vy = 0;
					} else if (y > mapY) {
						y = mapY;
						vy = 0;
					} else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.ITEM_BLOCK_FLAG)) {
						y -= vy;
						vy = 0;
					}
				}
				if (z != 0 || vz != 0) {
					vz += 1;
					z -= vz;
					falldownDamage += vz;
					if (!isFallingUnderGround()) {
						if (z <= mostDepth) {
							if (falldownDamage > 10) {
								crushShit();
							}
							z = mostDepth;
							vx = 0;
							vy = 0;
							vz = 0;
							falldownDamage = 0;
						}
					}
				}
			}
			calcPos();
			return TickResult.NONE;
		}
		calcPos();
		return TickResult.REMOVED;
	}

	/** 排出したゆっくりの名前を返す。 */
	public String getOwnerName() {
		return ownerName;
	}

	/** 排出したゆっくりの名前をセットする。 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/** 排出したゆっくりの ID を返す。 */
	public int getOwnerId() {
		return ownerId;
	}

	/** 排出したゆっくりの ID をセットする。 */
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	/** 排出したゆっくりの年齢層を返す。 */
	public AgeState getAgeState() {
		return ageState;
	}

	/** 排出したゆっくりの年齢層をセットする。 */
	public void setAgeState(AgeState ageState) {
		this.ageState = ageState;
	}

	/** うんうんが落下した際のダメージ値を返す。 */
	public int getFalldownDamage() {
		return falldownDamage;
	}

	/** うんうんが落下した際のダメージ値をセットする。 */
	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	/** うんうんの量を返す。 */
	public int getAmount() {
		return amount;
	}

	/** うんうんの量をセットする。 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** うんうんの種類（通常/毒/下痢等）を返す。 */
	public int getShitType() {
		return shitType;
	}

	/** うんうんの種類をセットする。 */
	public void setShitType(int shitType) {
		this.shitType = shitType;
	}

}
