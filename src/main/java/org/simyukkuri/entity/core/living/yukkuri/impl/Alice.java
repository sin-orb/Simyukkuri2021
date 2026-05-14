package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BodyRank;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.system.BodyLayer;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.IniFileUtil;

/**
 * ありす
 */
public class Alice extends Yukkuri {

	private static final long serialVersionUID = -2849085882014396337L;
	/** ありすのタイプNo：2 */
	public static final int type = 2;
	/** 日本語名 */
	public static final String nameJ = "ありす";
	/** 英名 */
	public static final String nameE = "Alice";
	/** ベースファイル名 */
	public static final String baseFileName = "alice";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
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

	/**
	 * ありすのみオーバーライド。レイパーかつありすかつ興奮顔かどうかを返却する.
	 * 
	 * @param f ImageCodeのordinal
	 * @returns れいぱーかつありすかつ興奮顔かどうか
	 */
	@Override
	protected boolean isRaperExcitingFace(int f) {
		return isRaper() && f == ImageCode.EXCITING.ordinal();
	};

	/**
	 * ありすのみオーバーライド。ありすかつれいぱーかどうかを返却する.
	 * 
	 * @return ありすかつれいぱーかどうか
	 */
	@Override
	@Transient
	protected boolean isAliceRaper() {
		return isRaper();
	};

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded)
			return;

		boolean res;
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}
		imagePack[BodyRank.KAIYU.getImageIndex()] = imagesKai;
		if (imagesNora != null) {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesKai;
		}
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}

	/** INIファイルロード */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.getDataIniDir(), baseFileName);

		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction
				* directionOffset[type][0]][getBodyAgeState().ordinal()];
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
		return YukkuriType.ALICE;
	}

	@Override
	@Transient
	public YukkuriType getHybridType(YukkuriType partnerType) {
		switch (partnerType) {
			default:
				return YukkuriType.ALICE;
		}
	}

	@Override
	@Transient
	public String getNameJ() {
		return nameJ;
	}

	@Override
	@Transient
	public String getMyName() {
		if (getMyNames()[getBodyAgeState().ordinal()] != null) {
			return getMyNames()[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}

	@Override
	@Transient
	public String getMyNameD() {
		if (getMyNamesDamaged()[getBodyAgeState().ordinal()] != null) {
			return getMyNamesDamaged()[getBodyAgeState().ordinal()];
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

	// ゆっくりしてる時のアクション
	// 個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	public void killTime() {
		if (getCurrentEvent() != null)
			return;
		if (getPlaying() != null)
			return;
		int p = GameRandom.nextInt(50);
		// 7/50でキリッ
		if (p <= 6) {
			getInVain(true);
		}
		// 7/50でのびのび
		else if (p <= 14) {
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		// 7/50でこーでねーと
		else if (p <= 21 && getBodyRank() != BodyRank.KAIYU) {
			coordinate();
			addStress(-30);
			stay(40);
		}
		// 7/50でふりふり
		else if (p <= 28 && willingFurifuri()) {
			// if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(GameMessages.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		}
		// 7/50でふりふりで腹減った
		else if ((p <= 35 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		// 4/50でおもちゃで遊ぶ
		else if (p <= 39) {
			if (ToyLogic.checkToy(this)) {
				setPlaying(PlayStyle.BALL);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		// 3/50でトランポリンで遊ぶ
		else if (p <= 42) {
			if (ToyLogic.checkTrampoline(this)) {
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		// 1/50ですいーで遊ぶ
		else if (p <= 43) {
			if (ToyLogic.checkSui(this)) {
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else
				killTime();
		} else {
			// おくるみありで汚れていない場合
			if (isHasPants() && !isDirty() && GameRandom.nextInt(5) == 0) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.RelaxOkurumi));
			} else {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}

	/**
	 * こーでぃねーとをする.
	 */
	public void coordinate() {
		if (GameWorld.get().getCurrentMap().getBed().size() == 0) {
			int i = 0;
			if (getBodyRank() == BodyRank.NORAYU || getBodyRank() == BodyRank.NORAYU_CLEAN
					|| getBodyRank() == BodyRank.SUTEYU) {
				i = 1;
			}
			getInVain(true);
			Bed bed = (Bed) GadgetAction.putObjEX(Bed.class, getX(), getY(), i);
			GameWorld.get().getCurrentMap().getBed().put(bed.objId, bed);
			return;
		}
	}

	/**
	 * コンストラクタ
	 */
	public Alice(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.ALICE);
		setShitType(YukkuriType.ALICE);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Alice() {

	}

	@Override
	public void tuneParameters() {
		/*
		 * if (GameRandom.nextBoolean()) {
		 * motherhood = true;
		 * }
		 */
		if (GameRandom.nextInt(4) == 0) {
			setRapist(true);
		}
		double factor = Math.random() + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getHungryLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] *= factor;
		getShitLimitBase()[AgeState.CHILD.ordinal()] *= factor;
		getShitLimitBase()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
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
		setSameDirectionFactor(GameRandom.nextInt(15) + 15);
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 2;
		factor = Math.random() + 0.5;
		getStrengthBase()[AgeState.ADULT.ordinal()] *= factor;
		getStrengthBase()[AgeState.CHILD.ordinal()] *= factor;
		getStrengthBase()[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
	}
}
