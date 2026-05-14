package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.transform.TransformationService;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.BodyRank;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PlayStyle;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.logic.ToyLogic;
import org.simyukkuri.system.BodyLayer;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.IniFileUtil;

/*****************************************************
 * れいむ。でいぶ、わさ、まりされいむはこれを継承している
 */
public class Reimu extends Yukkuri {
	private static final long serialVersionUID = -7573106487924456286L;
	/** れいむのタイプ */
	public static final YukkuriType type = YukkuriType.REIMU;
	/** れいむ和名 */
	public static final String nameJ = "れいむ";
	/** れいむ英名 */
	public static final String nameE = "Reimu";
	/** れいむベースファイル名 */
	public static final String baseFileName = "reimu";

	private static BufferedImage[][][][] imagePack = new BufferedImage[BodyRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode
			.values().length][2][3][ModLoader.getMaxImgOtherVer() + 1];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static int directionOffsetNagasi[][] = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	// 個別表情管理(まりちゃ流し用)
	private int imageVariantState[][] = new int[ImageCode.values().length][2];

	/** イメージのロード */
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
		res = ModLoader.loadBodyImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.getYkWordNagasi(),
				baseFileName, io);
		if (!res) {
			imagesNagasi = null;
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
		if (!isImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getBodyAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		} else {
			// インターバル毎に初期化する
			if (GameEnvironment.getInterval() == 0 && !isDead()) {
				for (int i = 0; i < ImageCode.values().length; i++) {
					imageVariantState[i][1] = 0;
				}
			}

			// 前回と同じ表示
			if (imageVariantState[type][1] == 1) {
				int imageIndex = imageVariantState[type][0];
				layer.getImage()[index] = imagesNagasi[type][direction
						* directionOffsetNagasi[type][0]][getBodyAgeState()
								.ordinal()][imageIndex];

			} else {
				int otherVersionCount = 0;
				for (int i = 0; i < ModLoader.getMaxImgOtherVer(); i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][i
							+ 1] != null) {
						otherVersionCount++;
					}
				}

				if (otherVersionCount != 0) {
					int randomIndex = GameRandom.nextInt(otherVersionCount + 1);
					imageVariantState[type][0] = randomIndex;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][randomIndex];
				} else {
					imageVariantState[type][0] = 0;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][0];
				}

				imageVariantState[type][1] = 1;
			}

			layer.getDir()[index] = direction * directionOffsetNagasi[type][1];
		}
		return 1;
	}

	/**
	 * ゆ虐神拳を受けてドス等にトランスフォーム可能かどうかを返却する.
	 * 実ゆでなければ可能を返却する.
	 * 
	 * @return ゆ虐神拳を受けてドス等にトランスフォーム可能かどうか
	 */
	@Override
	public boolean judgeCanTransForGodHand() {
		if (isUnBirth()) {
			bodyBurst();
			return false;
		}
		return true;
	}

	/**
	 * ゆ虐神拳を受けでいぶにトランスフォームする。
	 */
	@Override
	public void execTransform() {
		// でいぶ化
		TransformationService.transform(this, YukkuriType.DEIBU);
	}

	/**
	 * 突然変異チェックをする.
	 * れいむ→でいぶ.
	 * 
	 * @return 突然変異する際のゆっくり
	 */
	@Override
	public Yukkuri checkTransform() {
		// 自身が突然変異可能かチェック
		if (!canTransform())
			return null;
		// 大人であり、夫がいて夫がゲスではなく、自身がゲス
		Yukkuri partner = org.simyukkuri.util.BodyRegistry.getBodyInstance(getPartner());
		if (isAdult() && partner != null && !partner.isRude() && isRude()) {
			if (GameRandom.nextInt(1000) == 0) {
				return this;
			}
		}
		// または、ゲスでいぶの子供である
		Yukkuri mother = org.simyukkuri.util.BodyRegistry.getBodyInstance(getMother());
		if (!isAdult() && mother != null && mother.getType() == Deibu.type && mother.isRude()) {
			// ゲスバカとドゲスは確実にでいぶ化
			if ((isRude() && getIntelligence() != Intelligence.FOOL) || getAttitude() == Attitude.SUPER_SHITHEAD) {
				return this;
			}
			// あとは1/2。
			else if (GameRandom.nextBoolean()) {
				return this;
			}
		}
		return null;
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
		switch (partnerType) {
			case MARISA:
				return YukkuriType.MARISAREIMU;
			default:
				return YukkuriType.REIMU;
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

	// 胴体のベースグラフィックを返す
	// mode[0] 正面向きか横向きか
	@Override
	public int getBodyBaseImage(BodyLayer layer) {
		int direction = this.getDirection().ordinal();
		int idx = 0;

		layer.getOption()[0] = 0;
		layer.getOption()[1] = 0;
		layer.getOption()[2] = 0;

		if (isBurned() && isDead()) {
			// 焼死体
			idx += getImage(ImageCode.BURNED.ordinal(), Const.LEFT, layer, idx);
		} else if (isCrushed()) {
			// 潰れた死体
			if (isBurned()) {
				idx += getImage(ImageCode.BURNED2.ordinal(), Const.LEFT, layer, idx);
			} else {
				if (isPealed()) {
					idx += getImage(ImageCode.CRUSHED3.ordinal(), Const.LEFT, layer, idx);
				} else if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
					idx += getImage(ImageCode.CRUSHED.ordinal(), Const.LEFT, layer, idx);
				} else {
					idx += getImage(ImageCode.CRUSHED2.ordinal(), Const.LEFT, layer, idx);
				}
			}
		} else if (isPacked()) {
			if (isDead()) {
				idx += getImage(ImageCode.PACKED_DEAD.ordinal(), Const.LEFT, layer, idx);
			} else if (getAge() % 6 <= 2) {
				idx += getImage(ImageCode.PACKED1.ordinal(), Const.LEFT, layer, idx);
			} else {
				idx += getImage(ImageCode.PACKED2.ordinal(), Const.LEFT, layer, idx);
			}
		} else if (isShitting() || isBirth() && getBabyTypes().size() > 0 || (isFixBack() && !isFurifuri())) {
			// 排泄、出産時
			idx += getImage(ImageCode.FRONT_SHIT.ordinal(), Const.LEFT, layer, idx);

			if (getHairState() == HairState.DEFAULT) {
				idx += getImage(ImageCode.FRONT_HAIR.ordinal(), Const.LEFT, layer, idx);
			} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
				idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
			}

			if (isAnalClose()) {
				idx += getImage(ImageCode.FRONT_SEALED.ordinal(), Const.LEFT, layer, idx);
			}
			if (getCriticalDamege() == CriticalDamegeType.INJURED) {
				idx += getImage(ImageCode.FRONT_INJURED.ordinal(), Const.LEFT, layer, idx);
			}
			if (isBlind()) {
				idx += getImage(ImageCode.FRONT_BLIND.ordinal(), Const.LEFT, layer, idx);
			}
			if (isHasPants()) {
				idx += getImage(ImageCode.FRONT_PANTS.ordinal(), Const.LEFT, layer, idx);
			}
			if (isHasBraid()) {
				idx += getImage(ImageCode.FRONT_BRAID.ordinal(), Const.LEFT, layer, idx);
			}
			if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
			}
		} else if (isFurifuri() && !isSleeping() && (!isLockmove() || isFixBack())) {
			// ふりふり
			if (getAge() % 8 <= 3) {
				idx += getImage(ImageCode.ROLL_LEFT_SHIT.ordinal(), Const.LEFT, layer, idx);

				if (getHairState() == HairState.DEFAULT) {
					idx += getImage(ImageCode.ROLL_LEFT_HAIR.ordinal(), Const.LEFT, layer, idx);
				} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
					idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
				}

				if (isAnalClose()) {
					idx += getImage(ImageCode.ROLL_LEFT_SEALED.ordinal(), Const.LEFT, layer, idx);
				}
				if (getCriticalDamege() == CriticalDamegeType.INJURED) {
					idx += getImage(ImageCode.ROLL_LEFT_INJURED.ordinal(), Const.LEFT, layer, idx);
				}
				if (isBlind()) {
					idx += getImage(ImageCode.ROLL_LEFT_BLIND.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasPants()) {
					idx += getImage(ImageCode.ROLL_LEFT_PANTS.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasBraid()) {
					idx += getImage(ImageCode.ROLL_LEFT_BRAID.ordinal(), Const.LEFT, layer, idx);
				}

			} else if (getAge() % 8 <= 7) {
				idx += getImage(ImageCode.ROLL_RIGHT_SHIT.ordinal(), Const.LEFT, layer, idx);

				if (getHairState() == HairState.DEFAULT) {
					idx += getImage(ImageCode.ROLL_RIGHT_HAIR.ordinal(), Const.LEFT, layer, idx);
				} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
					idx += getImage(ImageCode.FRONT_HAIR2.ordinal(), Const.LEFT, layer, idx);
				}

				if (isAnalClose()) {
					idx += getImage(ImageCode.ROLL_RIGHT_SEALED.ordinal(), Const.LEFT, layer, idx);
				}
				if (getCriticalDamege() == CriticalDamegeType.INJURED) {
					idx += getImage(ImageCode.ROLL_RIGHT_INJURED.ordinal(), Const.LEFT, layer, idx);
				}
				if (isBlind()) {
					idx += getImage(ImageCode.ROLL_RIGHT_BLIND.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasPants()) {
					idx += getImage(ImageCode.ROLL_RIGHT_PANTS.ordinal(), Const.LEFT, layer, idx);
				}
				if (isHasBraid()) {
					idx += getImage(ImageCode.ROLL_RIGHT_BRAID.ordinal(), Const.LEFT, layer, idx);
				}
			}
			if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
			}
		}
		// れいむ種専用
		else if (isYunnyaa()) {
			// ゆんやあ
			/* if(getImageNagasiMode()) */ {
				if (getAge() % 6 <= 1) {
					idx += getImage(ImageCode.YUNYAA1.ordinal(), direction, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA1_ACCESSORY.ordinal(), direction, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA1_HAIR.ordinal(), direction, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.YUNYAA1_HAIR2.ordinal(), direction, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.YUNYAA1_INJURED.ordinal(), direction, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.YUNYAA1_BLIND.ordinal(), direction, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.YUNYAA1_PANTS.ordinal(), direction, layer, idx);
					}
					if (isDirty()) {
						idx += getImage(ImageCode.YUNYAA1_DIRTY.ordinal(), direction, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA1_BRAID.ordinal(), direction, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA1_CUTBRAID.ordinal(), direction, layer, idx);
					}
				} else if (getAge() % 6 == 2 || getAge() % 6 == 5) {
					idx += getImage(ImageCode.YUNYAA2.ordinal(), direction, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA2_ACCESSORY.ordinal(), direction, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA2_HAIR.ordinal(), direction, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.YUNYAA2_HAIR2.ordinal(), direction, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.YUNYAA2_INJURED.ordinal(), direction, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.YUNYAA2_BLIND.ordinal(), direction, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.YUNYAA2_PANTS.ordinal(), direction, layer, idx);
					}
					if (isDirty()) {
						idx += getImage(ImageCode.YUNYAA2_DIRTY.ordinal(), direction, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA2_BRAID.ordinal(), direction, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA2_CUTBRAID.ordinal(), direction, layer, idx);
					}
				} else /* if (getAge() % 6 <= 3) */ {
					idx += getImage(ImageCode.YUNYAA3.ordinal(), direction, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA3_ACCESSORY.ordinal(), direction, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA3_HAIR.ordinal(), direction, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.YUNYAA3_HAIR3.ordinal(), direction, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.YUNYAA3_INJURED.ordinal(), direction, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.YUNYAA3_BLIND.ordinal(), direction, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.YUNYAA3_PANTS.ordinal(), direction, layer, idx);
					}
					if (isDirty()) {
						idx += getImage(ImageCode.YUNYAA3_DIRTY.ordinal(), direction, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA3_BRAID.ordinal(), direction, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.YUNYAA3_CUTBRAID.ordinal(), direction, layer, idx);
					}
				}
			}
		}

		else {
			// 皮むき時
			if (isPealed()) {
				idx += getImage(ImageCode.PEALED.ordinal(), direction, layer, idx);
			}
			// 通常時
			else {
				idx += getImage(ImageCode.BODY.ordinal(), direction, layer, idx);
			}
			layer.getOption()[0] = 1;
		}
		return idx;
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
		// 7/50でおうた
		else if (p <= 21) {
			setMessage(GameMessages.getMessage(this, MessagePool.Action.ProudChildsSING), 40);
			setNobinobi(true);
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

	/** コンストラクタ */
	public Reimu(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.REIMU);
		setShitType(YukkuriType.REIMU);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Reimu() {

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

	public int[][] getImageVariantState() {
		return imageVariantState;
	}

	public void setImageVariantState(int[][] imageVariantState) {
		this.imageVariantState = imageVariantState;
	}

}
