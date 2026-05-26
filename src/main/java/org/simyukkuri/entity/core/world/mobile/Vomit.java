package org.simyukkuri.entity.core.world.mobile;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Numbering;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameText;

/**
 * 吐餡クラス.
 */
public class Vomit extends Entity {

	private static final long serialVersionUID = -5000572959095410611L;
	// public variables
	/** 通常の吐餡 */
	public static final int VOMIT_NORMAL = 0;
	/** 壊れた吐餡 */
	public static final int VOMIT_CRASHED = 1;
	/** 吐餡の影 */
	public static final int VOMIT_SHADOW = 2;
	/** 吐餡の状態 */
	public static final int NUM_OF_VOMIT_STATE = 3;

	private static final int[] VOMITLIMIT = { 100 * 24 * 2, 100 * 24 * 4, 100 * 24 * 8 };
	private String ownerName = null;
	private AgeState ageState;
	private int falldownDamage = 0;
	private int amount = 0;
	private int vomitType = 0;
	private static final float[] imageSize = { 0.25f, 0.5f, 1.0f };
	private static final String[] sizeDisplayName = { GameText.read("game_little"),
			GameText.read("game_middle"),
			GameText.read("game_big") };
	private static final int[] value = { 50, 100, 300 };

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

		images = new BufferedImage[name.length][NUM_OF_VOMIT_STATE][3];
		imgW = new int[name.length][3];
		imgH = new int[name.length][3];
		pivX = new int[name.length][3];
		pivY = new int[name.length][3];

		int sx;
		int sy;

		for (int i = 0; i < name.length; i++) {
			if (name[i].getImageDirName().length() == 0) {
				continue;
			}

			images[i][VOMIT_NORMAL][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/toan.png"));
			images[i][VOMIT_CRASHED][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/toan2.png"));
			images[i][VOMIT_SHADOW][Const.ADULT_INDEX] = ImageIO
					.read(loader.getResourceAsStream(path + name[i].getImageDirName() + "/toan_shadow.png"));

			for (int j = 0; j < NUM_OF_VOMIT_STATE; j++) {
				imgW[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getWidth(io);
				imgH[i][Const.ADULT_INDEX] = images[i][0][Const.ADULT_INDEX].getHeight(io);
				pivX[i][Const.ADULT_INDEX] = imgW[i][Const.ADULT_INDEX] >> 1;
				pivY[i][Const.ADULT_INDEX] = imgH[i][Const.ADULT_INDEX] - 1;

				sx = (int) ((float) imgW[i][Const.ADULT_INDEX] * imageSize[1]);
				sy = (int) ((float) imgH[i][Const.ADULT_INDEX] * imageSize[1]);
				images[i][j][Const.CHILD_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
				sx = (int) ((float) imgW[i][Const.ADULT_INDEX] * imageSize[0]);
				sy = (int) ((float) imgH[i][Const.ADULT_INDEX] * imageSize[0]);
				images[i][j][Const.BABY_INDEX] = ModLoader.scaleImage(images[i][j][Const.ADULT_INDEX], sx, sy);
			}
		}
		for (int i = 0; i < name.length; i++) {
			for (int j = 0; j < 3; j++) {
				if (images[i][0][j] == null) {
					continue;
				}

				imgW[i][j] = images[i][0][j].getWidth(io);
				imgH[i][j] = images[i][0][j].getHeight(io);
				pivX[i][j] = imgW[i][j] >> 1;
				pivY[i][j] = imgH[i][j] - 1;
			}
		}
	}

	/** とあんのサイズ・排出者名を含む文字列表現を返す。 */
	@Override
	public String toString() {
		String base = GameText.read("game_toan");
		StringBuilder ret = new StringBuilder(base == null ? "" : base);
		ret.append(sizeDisplayName[ageState.ordinal()]);
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
		return (images[vomitType][getVomitState()][ageState.ordinal()]);
	}

	/**
	 * 影のイメージを取得する.
	 * 
	 * @return 影のイメージ
	 */
	@Transient
	public BufferedImage getShadowImage() {
		return (images[vomitType][VOMIT_SHADOW][ageState.ordinal()]);
	}

	/**
	 * サイズを取得する.
	 * 
	 * @return サイズ
	 */
	@Transient
	public int getSize() {
		return imgW[vomitType][ageState.ordinal()];
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX 初期X座標
	 * @param initY 初期Y座標
	 * @param initZ 初期Z座標
	 * @param b     吐いたゆっくり
	 * @param type  吐餡タイプ
	 */
	public Vomit(int initX, int initY, int initZ, Yukkuri b, YukkuriType type) {
		objId = Numbering.INSTANCE.numberingObjId();
		objType = Type.VOMIT;
		vomitType = type.ordinal();
		x = initX;
		y = initY;
		z = initZ;
		if (b == null) {
			ageState = AgeState.ADULT;
		} else {
			ownerName = GameLocale.isJapanese() ? b.getNameJ() : b.getNameE();
			ageState = b.getAgeState();
		}
		switch (ageState) {
			case BABY:
				amount = 100;
				break;
			case CHILD:
				amount = 100 * 2;
				break;
			case ADULT:
				amount = 100 * 4;
				break;
			default:
				break;
		}
		calcPos();
		setRemoved(false);
		setBoundary(pivX[vomitType][ageState.ordinal()], pivY[vomitType][ageState.ordinal()],
				imgW[vomitType][ageState.ordinal()], imgH[vomitType][ageState.ordinal()]);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Vomit() {
		ageState = AgeState.ADULT;
		ownerName = "Unknown";
	}

	/**
	 * 成長ステージを取得する.
	 * 
	 * @return 成長ステージ
	 */
	public AgeState getAgeState() {
		return ageState;
	}

	/**
	 * 吐餡の状態を取得する.
	 * 
	 * @return 吐餡の状態
	 */
	@Transient
	public int getVomitState() {
		if (getAge() >= VOMITLIMIT[ageState.ordinal()] / 4) {
			return 1;
		}
		return 0;
	}

	/**
	 * 吐餡を食べる.
	 * 
	 * @param eatAmount 食べる量
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
		setAge(getAge() + VOMITLIMIT[ageState.ordinal()] / 2);
	}

	/**
	 * 吐餡をキックする.
	 */
	public void kick() {
		int[] blowLevel = { -6, -5, -4 };
		kick(0, blowLevel[ageState.ordinal()] * 2, blowLevel[ageState.ordinal()]);
	}

	/**
	 * 吐餡の価格を取得する.
	 */
	@Transient
	public int getValue() {
		return value[ageState.ordinal()];
	}

	/** 取得ポップアップを持つかを返す（常に VOMIT）。 */
	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.VOMIT;
	}

	/** 使用ポップアップを持つかを返す（常に null）。 */
	@Override
	public UseMenuTarget hasUsePopup() {
		return UseMenuTarget.NONE;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public TickResult clockTick() {
		if (!isRemoved()) {
			// age += TICK;
			if (getAge() >= VOMITLIMIT[ageState.ordinal()]) {
				setRemoved(true);
			}
			if (!grabbed) {
				if (vx != 0) {
					x += vx;
					if (x < 0) {
						x = 0;
						vx *= -1;
					} else if (x > Translate.getWorldWidth()) {
						x = Translate.getWorldWidth();
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
					} else if (y > Translate.getWorldHeight()) {
						y = Translate.getWorldHeight();
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
								crushVomit();
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

	/** とあんが落下した際のダメージ値を返す。 */
	public int getFalldownDamage() {
		return falldownDamage;
	}

	/** とあんが落下した際のダメージ値をセットする。 */
	public void setFalldownDamage(int falldownDamage) {
		this.falldownDamage = falldownDamage;
	}

	/** とあんの量を返す。 */
	public int getAmount() {
		return amount;
	}

	/** とあんの量をセットする。 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** とあんの種類を返す。 */
	public int getVomitType() {
		return vomitType;
	}

	/** とあんの種類をセットする。 */
	public void setVomitType(int vomitType) {
		this.vomitType = vomitType;
	}

	/** 排出したゆっくりの年齢層をセットする。 */
	public void setAgeState(AgeState ageState) {
		this.ageState = ageState;
	}

}
