package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/**
 * ちぇん
 */
public class Chen extends Yukkuri {

	private static final long serialVersionUID = 6045679671933105545L;
	/** ちぇんのタイプ */
	public static final YukkuriType type = YukkuriType.CHEN;
	/** ちぇんの日本語名 */
	public static final String nameJ = "ちぇん";
	/** ちぇんの英名 */
	public static final String nameE = "Chen";
	/** ちぇんのベースファイル名 */
	public static final String baseFileName = "chen";

	private static BufferedImage[][][][] imagePack = new BufferedImage[YukkuriRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static int[][] directionOffset = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;

	/**
	 * 画像ロード
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded) {
			return;
		}

		boolean res;
		res = ModLoader.loadYukkuriImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadYukkuriImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}
		imagePack[YukkuriRank.KAIYU.getImageIndex()] = imagesKai;
		if (imagesNora != null) {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesKai;
		}
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}

	/**
	 * INIファイルをロードする.
	 * 
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadYukkuriIniOffsets(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	/** 画像が読み込み済みかを返す。 */
	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/** 現在の表示状態に基づく画像をレイヤーにセットし、画像番号を返す。 */
	@Override
	public int getImage(int type, int direction, YukkuriLayer layer, int index) {
		layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
				* directionOffset[type][0]][getAgeState().ordinal()];
		layer.getDir()[index] = direction * directionOffset[type][1];
		return 1;
	}

	/** アタッチメントキーに対応する取り付け点座標を返す。 */
	@Override
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	/** ゆっくりの種別を返す。 */
	@Override
	@Transient
	public YukkuriType getType() {
		return type;
	}

	/** 日本語名を返す。 */
	@Override
	@Transient
	public String getNameJ() {
		return nameJ;
	}

	/** 自分の呼び名（愛称優先、なければ種族名）を返す。 */
	@Override
	@Transient
	public String getMyName() {
		if (getMyNames()[getAgeState().ordinal()] != null) {
			return getMyNames()[getAgeState().ordinal()];
		}
		return nameJ;
	}

	/** ダメージ時の呼び名（設定あれば優先、なければ通常の呼び名）を返す。 */
	@Override
	@Transient
	public String getMyNameD() {
		if (getMyNamesDamaged()[getAgeState().ordinal()] != null) {
			return getMyNamesDamaged()[getAgeState().ordinal()];
		}
		return getMyName();
	}

	/** 英語名を返す。 */
	@Override
	@Transient
	public String getNameE() {
		return nameE;
	}

	/** 追加の日本語名（ない場合は空文字）を返す。 */
	@Override
	@Transient
	public String getNameJ2() {
		return "";
	}

	/** 追加の英語名（ない場合は空文字）を返す。 */
	@Override
	@Transient
	public String getNameE2() {
		return "";
	}

	/** コンストラクタ */
	public Chen(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.CHEN);
		setShitType(YukkuriType.CHEN);
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/** ちぇん のデフォルトコンストラクタ。 */
	public Chen() {

	}

	@Override
	public void reinitializeBoundary() {
		setBoundary(boundary, braidBoundary);
	}

	/**
	 * Tune parameters.
	 */
	@Override
	public void tuneParameters() {
		/*
		 * if (rnd.nextBoolean()) {
		 * motherhood = true;
		 * }
		 */
		double factor = Math.random() * 2 + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] = (int) (getHungryLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getHungryLimitBase()[AgeState.CHILD.ordinal()] = (int) (getHungryLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getHungryLimitBase()[AgeState.BABY.ordinal()] = (int) (getHungryLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() * 2 + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] = (int) (getShitLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getShitLimitBase()[AgeState.CHILD.ordinal()] = (int) (getShitLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getShitLimitBase()[AgeState.BABY.ordinal()] = (int) (getShitLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() + 0.5;
		getDamageLimitBase()[AgeState.ADULT.ordinal()] = (int) (getDamageLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getDamageLimitBase()[AgeState.CHILD.ordinal()] = (int) (getDamageLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getDamageLimitBase()[AgeState.BABY.ordinal()] = (int) (getDamageLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() + 0.5;
		setBabyLimitBase((int) (getBabyLimitBase() * factor));
		setChildLimitBase((int) (getChildLimitBase() * factor));
		setLifeLimitBase((int) (getLifeLimitBase() * factor));
		factor = Math.random() + 1;
		setRelaxPeriodBase((int) (getRelaxPeriodBase() * factor));
		setExcitePeriodBase((int) (getExcitePeriodBase() * factor));
		setPregPeriodBase((int) (getPregPeriodBase() * factor));
		setSleepPeriodBase((int) (getSleepPeriodBase() * factor));
		setActivePeriodBase((int) (getActivePeriodBase() * factor));
		setSameDirectionFactor((GameRandom.nextInt(20) + 20));
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength((GameRandom.nextInt(10) + 1));
		// EYESIGHT /= 4;
		factor = Math.random() + 0.5;
		getStrengthBase()[AgeState.ADULT.ordinal()] = (int) (getStrengthBase()[AgeState.ADULT.ordinal()] * factor);
		getStrengthBase()[AgeState.CHILD.ordinal()] = (int) (getStrengthBase()[AgeState.CHILD.ordinal()] * factor);
		getStrengthBase()[AgeState.BABY.ordinal()] = (int) (getStrengthBase()[AgeState.BABY.ordinal()] * factor);
		speed = baseSpeed;
	}

}
