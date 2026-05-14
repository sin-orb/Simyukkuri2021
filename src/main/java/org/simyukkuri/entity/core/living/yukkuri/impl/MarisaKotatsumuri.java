package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/**
 * こたつむり
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarisaKotatsumuri extends Yukkuri {
	private static final long serialVersionUID = 3052532904240393393L;
	/** こたつむりのタイプ */
	public static final YukkuriType type = YukkuriType.MARISAKOTATSUMURI;
	/** こたつむり和名 */
	public static final String nameJ = "まりさ";
	/** こたつむり英名 */
	public static final String nameE = "Marisa";
	/** こたつむりベースファイル名 */
	public static final String baseFileName = "marisa_kotatumuri";

	private static BufferedImage[][][][] imagePack = new BufferedImage[YukkuriRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode.values().length][2][3][ModLoader
			.getMaxImgOtherVer() + 1];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static int directionOffsetNagasi[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	private int imageVariantState[][] = new int[ImageCode.values().length][2];

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded)
			return;

		boolean res;
		res = ModLoader.loadYukkuriImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.getYkWordNagasi(),
				baseFileName, io);
		if (!res) {
			imagesNagasi = null;
		}
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
	 * INIファイルをロードする
	 * 
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadYukkuriIniMap(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadYukkuriIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	@Override
	public int getImage(int type, int direction, YukkuriLayer layer, int index) {
		if (!isImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		} else {
			if (GameEnvironment.getInterval() == 0 && !isDead()) {
				for (int i = 0; i < ImageCode.values().length; i++) {
					imageVariantState[i][1] = 0;
				}
			}

			if (imageVariantState[type][1] == 1) {
				int variantIndex = imageVariantState[type][0];
				layer.getImage()[index] = imagesNagasi[type][direction
						* directionOffsetNagasi[type][0]][getAgeState().ordinal()][variantIndex];

			} else {
				int otherVariantCount = 0;
				for (int i = 0; i < ModLoader.getMaxImgOtherVer(); i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getAgeState().ordinal()][i
							+ 1] != null) {
						otherVariantCount++;
					}
				}

				if (otherVariantCount != 0) {
					int randomVariantIndex = GameRandom.nextInt(otherVariantCount + 1);
					imageVariantState[type][0] = randomVariantIndex;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getAgeState().ordinal()][randomVariantIndex];
				} else {
					imageVariantState[type][0] = 0;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getAgeState().ordinal()][0];
				}

				imageVariantState[type][1] = 1;
			}

			layer.getDir()[index] = direction * directionOffsetNagasi[type][1];
		}
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
	public MarisaKotatsumuri(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.MARISAKOTATSUMURI);
		setShitType(YukkuriType.MARISAKOTATSUMURI);
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public MarisaKotatsumuri() {

	}

	@Override
	public void tuneParameters() {
		/*
		 * if (rnd.nextBoolean()) {
		 * motherhood = true;
		 * }
		 */
		// Tune individual parameters.
		double factor = Math.random() + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getShitLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getShitLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() * 2 + 1;
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
		setSameDirectionFactor(GameRandom.nextInt(10) + 10);
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength(GameRandom.nextInt(7) + 1);
		// EYESIGHT /= 1;
		factor = Math.random() + 1;
		getStrengthBase()[AgeState.ADULT.ordinal()] *= factor;
		getStrengthBase()[AgeState.CHILD.ordinal()] *= factor;
		getStrengthBase()[AgeState.BABY.ordinal()] *= factor;

		// speed = 120;
		speed = baseSpeed;
	}

	public int[][] getImageVariantState() {
		return imageVariantState;
	}

	public void setImageVariantState(int[][] imageVariantState) {
		this.imageVariantState = imageVariantState;
	}

}
