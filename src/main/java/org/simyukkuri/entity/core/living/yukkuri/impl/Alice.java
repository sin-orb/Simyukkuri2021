package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.system.YukkuriLayer;
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
	 * ありすのみオーバーライド。レイパーかつありすかつ興奮顔かどうかを返却する.
	 * 
	 * @param f ImageCodeのordinal
	 * @returns れいぱーかつありすかつ興奮顔かどうか
	 */
	@Override
	protected boolean isRaperExcitingFace(int f) {
		return isRaper() && f == ImageCode.EXCITING.ordinal();
	}

	/**
	 * ありすのみオーバーライド。ありすかつれいぱーかどうかを返却する.
	 * 
	 * @return ありすかつれいぱーかどうか
	 */
	@Override
	@Transient
	protected boolean isAliceRaper() {
		return isRaper();
	}

	/** 画像ロード */
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

	/** INIファイルロード */
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
		return YukkuriType.ALICE;
	}

	/** 交配相手の種別に応じた混血種別を返す。 */
	@Override
	@Transient
	public YukkuriType getHybridType(YukkuriType partnerType) {
		switch (partnerType) {
			default:
				return YukkuriType.ALICE;
		}
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

	// ゆっくりしてる時のアクション
	/** Kill time. 個別の動作がある種ははこれをオーバーライドしているので注意 */
	@Override
	public void killTime() {
		if (getCurrentEvent() != null) {
			return;
		}
		if (getPlaying() != null) {
			return;
		}
		int p = GameRandom.nextInt(50);
		// 7/50でキリッ
		if (p <= 6) {
			getInVain(true);
		} else if (p <= 14) {
			// 7/50でのびのび (if yukkuri is not rude, she goes into her shell by discipline)
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		} else if (p <= 21 && getRank() != YukkuriRank.KAIYU) {
			// 7/50でこーでねーと
			coordinate();
			addStress(-30);
			stay(40);
		} else if (p <= 28 && willingFurifuri()) {
			// 7/50でふりふり (if yukkuri is rude, she will not do furifuri by discipline)
			setMessage(GameMessages.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		} else if ((p <= 35 && isHungry()) || isSoHungry()) {
			// 7/50でふりふりで腹減った (空腹時)
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		} else if (p <= 39) {
			// 4/50でおもちゃで遊ぶ
			if (ToyLogic.checkToy(this)) {
				setPlaying(PlayStyle.BALL);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else {
				killTime();
			}
		} else if (p <= 42) {
			// 3/50でトランポリンで遊ぶ
			if (ToyLogic.checkTrampoline(this)) {
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else {
				killTime();
			}
		} else if (p <= 43) {
			// 1/50ですいーで遊ぶ
			if (ToyLogic.checkSui(this)) {
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else {
				killTime();
			}
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
		if (GameWorld.get().getCurrentWorldState().getBeds().size() == 0) {
			int i = 0;
			if (getRank() == YukkuriRank.NORAYU || getRank() == YukkuriRank.NORAYU_CLEAN
					|| getRank() == YukkuriRank.SUTEYU) {
				i = 1;
			}
			getInVain(true);
			Bed bed = (Bed) GadgetAction.putObjEx(Bed.class, getX(), getY(), i);
			GameWorld.get().getCurrentWorldState().getBeds().put(bed.objId, bed);
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
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/** ありす のデフォルトコンストラクタ。 */
	public Alice() {

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
		 * if (GameRandom.nextBoolean()) {
		 * motherhood = true;
		 * }
		 */
		if (GameRandom.nextInt(4) == 0) {
			setRapist(true);
		}
		double factor = Math.random() + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] = (int) (getHungryLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getHungryLimitBase()[AgeState.CHILD.ordinal()] = (int) (getHungryLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getHungryLimitBase()[AgeState.BABY.ordinal()] = (int) (getHungryLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] = (int) (getShitLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getShitLimitBase()[AgeState.CHILD.ordinal()] = (int) (getShitLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getShitLimitBase()[AgeState.BABY.ordinal()] = (int) (getShitLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() + 1;
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
		setSameDirectionFactor(GameRandom.nextInt(15) + 15);
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 2;
		factor = Math.random() + 0.5;
		getStrengthBase()[AgeState.ADULT.ordinal()] = (int) (getStrengthBase()[AgeState.ADULT.ordinal()] * factor);
		getStrengthBase()[AgeState.CHILD.ordinal()] = (int) (getStrengthBase()[AgeState.CHILD.ordinal()] * factor);
		getStrengthBase()[AgeState.BABY.ordinal()] = (int) (getStrengthBase()[AgeState.BABY.ordinal()] * factor);
		speed = baseSpeed;
	}
}
