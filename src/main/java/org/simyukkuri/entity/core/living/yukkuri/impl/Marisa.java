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
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.engine.transform.TransformationService;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.YukkuriLayer;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.IniFileUtil;

/**
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

	private static BufferedImage[][][][] imagePack = new BufferedImage[YukkuriRank.values().length][][][];
	private static BufferedImage[][][] imagesKai = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][] imagesNora = new BufferedImage[ImageCode.values().length][2][3];
	private static BufferedImage[][][][] imagesNagasi = new BufferedImage[ImageCode
			.values().length][2][3][ModLoader.getMaxImgOtherVer() + 1];
	private static int[][] directionOffset = new int[ImageCode.values().length][2];
	private static int[][] directionOffsetNagasi = new int[ImageCode.values().length][2];
	private static Dimension4y[] boundary = new Dimension4y[3];
	private static Dimension4y[] braidBoundary = new Dimension4y[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point4y[]> AttachOffset = new HashMap<String, Point4y[]>();
	// ---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	// 個別表情管理(まりちゃ流し用)
	private int[][] imageVariantState = new int[ImageCode.values().length][2];

	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded) {
			return;
		}

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

		// 飼い
		imagePack[YukkuriRank.KAIYU.getImageIndex()] = imagesKai;

		// 野良
		if (imagesNora != null) {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesNora;
		} else {
			imagePack[YukkuriRank.NORAYU.getImageIndex()] = imagesKai;
		}

		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

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
		if (!isImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		} else { // 流し絵の場合
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
						* directionOffsetNagasi[type][0]][getAgeState()
								.ordinal()][imageIndex];
			} else {
				int otherVersionCount = 0;
				for (int i = 0; i < ModLoader.getMaxImgOtherVer(); i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getAgeState().ordinal()][i
							+ 1] != null) {
						otherVersionCount++;
					}
				}
				if (otherVersionCount != 0) {
					int randomIndex = GameRandom.nextInt(otherVersionCount + 1);
					imageVariantState[type][0] = randomIndex;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getAgeState().ordinal()][randomIndex];
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

	/**
	 * ゆ虐神拳を受けてドス等にトランスフォーム可能かどうかを返却する.
	 * ドスがいない状態&（実ゆもしくは赤ゆでない）で可能を返却.
	 * 
	 * @return ゆ虐神拳を受けてドス等にトランスフォーム可能かどうか
	 */
	@Override
	public boolean judgeCanTransForGodHand() {
		if (isUnBirth() || isBaby()) {
			bodyBurst();
			return false;
		}
		if (GameEnvironment.isAntidosSteam()) {
			return false;
		}
		// ドスまりさがワールドに既にいるなら突然変異しない
		for (Yukkuri candidate : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values()) {
			if (candidate.getType() == YukkuriType.DOSMARISA) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ゆ虐神拳を受けドスにトランスフォームする。
	 */
	@Override
	public void execTransform() {
		// 突然変異可能化チェック
		if (!canTransform()) {
			return;
		}
		if (isRude()) {
			return; // ゲスもだめ
		}
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
		if (!canTransform()) {
			return null;
		}

		// 自分以外に幸せを感じている大人のゆっくりが10体以上いる
		int adultCount = 0;
		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
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
	public YukkuriType getHybridType(YukkuriType partnerType) {
		switch (partnerType) {
			case REIMU:
			case WASAREIMU:
				return YukkuriType.REIMUMARISA;
			default:
				return YukkuriType.MARISA;
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
		if (GameLocale.isJapanese()) {
			return nameJ;
		} else {
			return nameE;
		}
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

	// 胴体のベースグラフィックを返す
	// option[0] 正面向きか横向きか
	/** 胴体の表示状態に基づく画像インデックスをレイヤーにセットし番号を返す。 */
	@Override
	public int getImageIndex(YukkuriLayer layer) {
		final int direction = this.getDirection().ordinal();

		layer.getOption()[0] = 0;
		layer.getOption()[1] = 0;
		layer.getOption()[2] = 0;

		int idx = 0;
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
				} else if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
			if (getCriticalDamege() == CriticalDamageType.INJURED) {
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
			if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
				idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
			}
		} else if (isFurifuri() && !isSleeping() && (!isLockmove() || isFixBack())) {
			// ふりふり
			// 流し用もるもる
			if (isImageNagasiMode()) {
				if (getAge() % 8 == 0) {
					idx += getImage(ImageCode.MROLL_LEFT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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

					if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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
			} else { // 以下、普通のふりふり
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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
					if (getCriticalDamege() == CriticalDamageType.INJURED) {
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
				if (getOkazaris() != null && getOkazaris().getOkazariType() == OkazariType.DEFAULT) {
					idx += getImage(ImageCode.ROLL_ACCESSORY.ordinal(), Const.LEFT, layer, idx);
				}
			}
		} else {
			// 皮むき時
			if (isPealed()) {
				idx += getImage(ImageCode.PEALED.ordinal(), direction, layer, idx);
			} else { // 通常時
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
		setBaseYukkuriFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}

	/** まりさ のデフォルトコンストラクタ。 */
	public Marisa() {

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
		// Tune individual parameters.
		double factor = Math.random() + 1;
		getHungryLimitBase()[AgeState.ADULT.ordinal()] = (int) (getHungryLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getHungryLimitBase()[AgeState.CHILD.ordinal()] = (int) (getHungryLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getHungryLimitBase()[AgeState.BABY.ordinal()] = (int) (getHungryLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() + 1;
		getShitLimitBase()[AgeState.ADULT.ordinal()] = (int) (getShitLimitBase()[AgeState.ADULT.ordinal()] * factor);
		getShitLimitBase()[AgeState.CHILD.ordinal()] = (int) (getShitLimitBase()[AgeState.CHILD.ordinal()] * factor);
		getShitLimitBase()[AgeState.BABY.ordinal()] = (int) (getShitLimitBase()[AgeState.BABY.ordinal()] * factor);
		factor = Math.random() * 2 + 1;
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
		setSameDirectionFactor(GameRandom.nextInt(10) + 10);
		setDeclinePeriodBase((int) (getDeclinePeriodBase() * (Math.random() + 0.5)));
		setImmunityStrength(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 1;
		factor = Math.random() + 1;
		getStrengthBase()[AgeState.ADULT.ordinal()] = (int) (getStrengthBase()[AgeState.ADULT.ordinal()] * factor);
		getStrengthBase()[AgeState.CHILD.ordinal()] = (int) (getStrengthBase()[AgeState.CHILD.ordinal()] * factor);
		getStrengthBase()[AgeState.BABY.ordinal()] = (int) (getStrengthBase()[AgeState.BABY.ordinal()] * factor);
		speed = baseSpeed;
	}

	/** 流し絵モード用の画像バリアント状態を返す。 */
	public int[][] getImageVariantState() {
		return imageVariantState;
	}

	/** 流し絵モード用の画像バリアント状態をセットする。 */
	public void setImageVariantState(int[][] imageVariantState) {
		this.imageVariantState = imageVariantState;
	}

}
