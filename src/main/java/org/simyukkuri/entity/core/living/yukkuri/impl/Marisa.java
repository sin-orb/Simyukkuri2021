package org.simyukkuri.entity.core.living.yukkuri.impl;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Dimension4y;
import org.simyukkuri.draw.ModLoader;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.transform.TransformationService;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BodyRank;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.BodyLayer;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.IniFileUtil;

/*****************************************************
 * まりさ。れいむまりさ、つむりまりさはこれを継承している
 */
public class Marisa extends Yukkuri {

	private static final long serialVersionUID = -7022828510837286120L;
	/** まりさのタイプ */
	public static final YukkuriType type = YukkuriType.MARISA;
	/** まりさ和名 */
	public static final String nameJ = "まりさ";
	/** まりさ英名 */
	public static final String nameE = "Marisa";
	/** まりさベースファイル名 */
	public static final String baseFileName = "marisa";

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
		res = ModLoader.loadBodyImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.getYkWordNagasi(),
				baseFileName, io);
		if (!res) {
			imagesNagasi = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.getYkWordNora(), baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}

		// 飼い
		imagePack[BodyRank.KAIYU.getImageIndex()] = imagesKai;

		// 野良
		if (imagesNora != null) {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.getImageIndex()] = imagesKai;
		}

		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}

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
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.getDataIniDir(), baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), baseFileName, "speed");
	}

	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		if (!isImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getBodyAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		}
		// 流し絵の場合
		else {
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
	 * ドスがいない状態&（実ゆもしくは赤ゆでない）で可能を返却.
	 * 
	 * @return ゆ虐神拳を受けてドス等にトランスフォーム可能かどうか
	 */
	@Override
	public boolean judgeCanTransForGodHand() {
		boolean ret = true;
		if (isUnBirth() || isBaby()) {
			bodyBurst();
			return false;
		}
		// ドスまりさが他にいるなら突然変異しない
		return ret;
	}

	/**
	 * ゆ虐神拳を受けドスにトランスフォームする。
	 */
	@Override
	public void execTransform() {
		// 突然変異可能化チェック
		if (!canTransform())
			return;
		if (isRude())
			return;// ゲスもだめ
		TransformationService.transform(this, YukkuriType.DOSMARISA);
	}

	/**
	 * 突然変異チェックをする.
	 * まりさ→ドス.
	 * 
	 * @return 突然変異する際のゆっくり
	 */
	@Override
	public Yukkuri checkTransform() {
		// 自身が突然変異可能かチェック
		if (!canTransform())
			return null;

		// 自分以外に幸せを感じている大人のゆっくりが10体以上いる
		int adultCount = 0;
		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getBody().values());
		for (Yukkuri otherBody : bodyList) {
			if (otherBody == this) {
				continue;
			}
			if (otherBody.isAdult() && otherBody.isHappy()) {
				adultCount++;
			}
		}
		if (adultCount < 10) {
			return null;
		}

		// その上で、1/300の確率で突然変異
		if (GameRandom.nextInt(300) == 0) {
			return this;
		}
		return null;
	}

	public Point4y[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	@Transient
	public YukkuriType getType() {
		return type;
	}

	@Override
	public YukkuriType getHybridType(YukkuriType partnerType) {
		switch (partnerType) {
			case REIMU:
			case WASAREIMU:
				return YukkuriType.REIMUMARISA;
			default:
				return YukkuriType.MARISA;
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
		if (GameLocale.isJapanese()) {
			return nameJ;
		} else {
			return nameE;
		}
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
	// option[0] 正面向きか横向きか
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
			// 流し用もるもる
			if (isImageNagasiMode()) {
				if (getAge() % 8 == 0) {
					idx += getImage(ImageCode.MROLL_LEFT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_LEFT2.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}

					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_LEFT2_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_LEFT2_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_LEFT2_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_LEFT2_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT2_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT2_CUTBRAID.ordinal(), Const.LEFT, layer, idx);
					}
				} else if (getAge() % 8 <= 2) {
					idx += getImage(ImageCode.MROLL_LEFT_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_LEFT.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_LEFT_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}

					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_LEFT_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_LEFT_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_LEFT_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_LEFT_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT_CUTBRAID.ordinal(), Const.LEFT, layer, idx);
					}
				} else if (getAge() % 8 == 3) {
					idx += getImage(ImageCode.MROLL_LEFT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_LEFT2.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}

					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_LEFT2_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_LEFT2_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_LEFT2_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_LEFT2_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT2_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
					if (!isHasBraid()) {
						idx += getImage(ImageCode.MROLL_LEFT2_CUTBRAID.ordinal(), Const.LEFT, layer, idx);
					}
				} else if (getAge() % 8 == 4) {
					idx += getImage(ImageCode.MROLL_RIGHT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_RIGHT2.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}
					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_RIGHT2_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
				} else if (getAge() % 8 <= 6) {
					idx += getImage(ImageCode.MROLL_RIGHT_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_RIGHT.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_RIGHT_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}
					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_RIGHT_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_RIGHT_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_RIGHT_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_RIGHT_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_RIGHT_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
				} else if (getAge() % 8 == 7) {
					idx += getImage(ImageCode.MROLL_RIGHT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_RIGHT2.ordinal(), Const.LEFT, layer, idx);
					}
					if (getHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (getHairState() == HairState.BRINDLED1 || getHairState() == HairState.BRINDLED2) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR2.ordinal(), Const.LEFT, layer, idx);
					}
					if (isAnalClose()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_SEALED.ordinal(), Const.LEFT, layer, idx);
					}
					if (getCriticalDamege() == CriticalDamegeType.INJURED) {
						idx += getImage(ImageCode.MROLL_RIGHT2_INJURED.ordinal(), Const.LEFT, layer, idx);
					}
					if (isBlind()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_BLIND.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasPants()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_PANTS.ordinal(), Const.LEFT, layer, idx);
					}
					if (isHasBraid()) {
						idx += getImage(ImageCode.MROLL_RIGHT2_BRAID.ordinal(), Const.LEFT, layer, idx);
					}
				}
			}

			// 以下、普通のふりふり
			else {
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
		} else {
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

	/** コンストラクタ */
	public Marisa(int initX, int initY, int initZ, AgeState initAgeState, Yukkuri p1, Yukkuri p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.MARISA);
		setShitType(YukkuriType.MARISA);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	public Marisa() {

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
		setImmunityStrength(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 1;
		factor = Math.random() + 1;
		getStrengthBase()[AgeState.ADULT.ordinal()] *= factor;
		getStrengthBase()[AgeState.CHILD.ordinal()] *= factor;
		getStrengthBase()[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
	}

	public int[][] getImageVariantState() {
		return imageVariantState;
	}

	public void setImageVariantState(int[][] imageVariantState) {
		this.imageVariantState = imageVariantState;
	}

}
