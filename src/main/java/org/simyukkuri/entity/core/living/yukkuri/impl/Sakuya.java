package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/**
 * さくや
 */
public class Sakuya extends Yukkuri {
	private static final long serialVersionUID = -7824015792761934296L;
	/** さくやのタイプ */
	public static final YukkuriType type = YukkuriType.SAKUYA;
	/** さくや和名 */
	public static final String nameJ = "さくや";
	/** さくや英名 */
	public static final String nameE = "Sakuya";
	/** さくやベースファイル名 */
	public static final String baseFileName = "sakuya";

	private static BufferedImage[][][][] imagePack = new BufferedImage[YukkuriRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded)
			return;

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

	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/** INIファイルをロードする */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadYukkuriIniMap(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadYukkuriIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	@Override
	public int getImage(int type, int direction, YukkuriLayer layer, int index) {
		layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
				* directionOffset[type][0]][getAgeState().ordinal()];
		layer.getDir()[index] = direction * directionOffset[type][1];
		return 1;
	}

	@Override
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	@Transient
	public YukkuriType getType() {
		return type;
	}

	@Override
	@Transient
	public YukkuriType getHybridType(YukkuriType partnerType) {
		return YukkuriType.SAKUYA;
	}

	@Override
	@Transient
	public String getNameJ() {
		return nameJ;
	}

	@Override
	@Transient
	public String getMyName() {
		if (getMyNames()[getAgeState().ordinal()] != null) {
			return getMyNames()[getAgeState().ordinal()];
		}
		return nameJ;
	}

	@Override
	@Transient
	public String getMyNameD() {
		if (getMyNamesDamaged()[getAgeState().ordinal()] != null) {
			return getMyNamesDamaged()[getAgeState().ordinal()];
		}
		return getMyName();
	}

	@Override
	@Transient
	public String getNameE() {
		return nameE;
	}

	@Override
	@Transient
	public String getNameJ2() {
		return "";
	}

	@Override
	@Transient
	public String getNameE2() {
		return "";
	}

	/** コンストラクタ */
	public Sakuya(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.SAKUYA);
		setShitType(YukkuriType.SAKUYA);
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Sakuya() {

	}

	@Override
	public void tuneParameters() {
		/*
		 * if (GameRandom.nextBoolean()) {
		 * motherhood = true;
		 * }
		 */
		double factor = Math.random() * 2 + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() * 2 + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getShitLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getShitLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		getDamageLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getDamageLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getDamageLimitBase()[AgeState.BABY.ordinal()] *= factor;
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
		setSameDirectionFactor(GameRandom.nextInt(20) + 20);
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 4;
		factor = Math.random() + 0.5;
		getStrengthBase()[AgeState.ADULT.ordinal()] *= factor;
		getStrengthBase()[AgeState.CHILD.ordinal()] *= factor;
		getStrengthBase()[AgeState.BABY.ordinal()] *= factor;

		// speed = 120;
		speed = baseSpeed;
	}

	@Override
	public boolean isServant() {
		return true;
	}

	@Override
	public boolean isServantOf(YukkuriType masterType) {
		return masterType == YukkuriType.REMIRYA || masterType == YukkuriType.FRAN;
	}

}
