package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.event.impl.PredatorsGameEvent;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/**
 * ふらん
 */
public class Fran extends Yukkuri {
	private static final long serialVersionUID = -7839169741238599190L;
	/** ふらんのタイプ */
	public static final YukkuriType type = YukkuriType.FRAN;
	/** ふらんの和名 */
	public static final String nameJ = "ふらん";
	/** ふらんの英名 */
	public static final String nameE = "Fran";
	/** ふらんのベースファイル名 */
	public static final String baseFileName = "fran";

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

	/** イメージをロードする */
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
		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, true, io);

		imageLoaded = true;
	}

	/** 画像が読み込み済みかを返す。 */
	@Override
	@Transient
	public boolean isImageLoaded() {
		return imageLoaded;
	}

	/**
	 * INIファイルをロードする
	 * 
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadYukkuriIniOffsets(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	/** 現在の表示状態に基づく画像をレイヤーにセットし、画像番号を返す。 */
	@Override
	public int getImage(int type, int direction, YukkuriLayer layer, int index) {
		layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
				* directionOffset[type][0]][getAgeState().ordinal()];
		layer.getDir()[index] = direction * directionOffset[type][1];
		return 1;
	}

	@Override
	/** アタッチメントキーに対応する取り付け点座標を返す。 */
	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	/** ゆっくりの種別を返す。 */
	@Override
	@Transient
	public YukkuriType getType() {
		return type;
	}

	/** 交配相手の種別に応じた混血種別を返す。 */
	@Override
	@Transient
	public YukkuriType getHybridType(YukkuriType partnerType) {
		return Fran.type;
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
	// 個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	/**
	 * Kill time.
	 */
	public void killTime() {
		if (getCurrentEvent() != null)
			return;
		if (getPlaying() != null)
			return;
		int p = GameRandom.nextInt(50);
		// 8/50でキリッ
		if (p <= 7) {
			getInVain(true);
		}
		// 8/50でのびのび
		else if (p <= 15) {
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		// 8/50でふりふり
		else if (p <= 23 && willingFurifuri()) {
			// if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(GameMessages.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		}
		// 8/50で腹減った
		else if ((p <= 31 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(GameMessages.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		// 5/50でおもちゃで遊ぶ
		else if (p <= 36) {
			EventLogic.addWorldEvent(new PredatorsGameEvent(this, null, null, 1), this,
					GameMessages.getMessage(this, MessagePool.Action.GameStart));
			return;
		}
		// 3/50でトランポリンで遊ぶ
		else if (p <= 39) {
			if (ToyLogic.checkTrampoline(this)) {
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		// 2/50ですいーで遊ぶ
		else if (p <= 41) {
			if (ToyLogic.checkSui(this)) {
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 + GameRandom.nextInt(100) - 49;
				return;
			} else
				killTime();
		} else {
			// おくるみありで汚れていない場合
			if (isHasPants() && !isDirty() && GameRandom.nextInt(10) == 0) {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.RelaxOkurumi));
			} else {
				setMessage(GameMessages.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}

	/** コンストラクタ */
	public Fran(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.FRAN);
		setShitType(YukkuriType.FRAN);
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/** ふらん（捕食種） のデフォルトコンストラクタ。 */
	public Fran() {

	}

	@Override
	/**
	 * Tune parameters.
	 */
	public void tuneParameters() {
		/*
		 * if (rnd.nextBoolean()) {
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
		setImmunityStrength(GameRandom.nextInt(15) + 1);
		// EYESIGHT /= 4;
		factor = Math.random() + 0.5;
		getStrengthBase()[AgeState.ADULT.ordinal()] *= factor;
		getStrengthBase()[AgeState.CHILD.ordinal()] *= factor;
		getStrengthBase()[AgeState.BABY.ordinal()] *= factor;

		// speed = 150;
		setFlyingType(true);
		setPredatorType(PredatorType.SUCTION);

		z = (int) (Translate.getWorldDepth() * Translate.getFlyLimit());
		speed = baseSpeed;
	}

	/** このゆっくりが捕食種かを返す。 */
	@Override
	public boolean isPredator() {
		return true;
	}
}
