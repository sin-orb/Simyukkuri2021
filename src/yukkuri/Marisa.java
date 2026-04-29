package src.yukkuri;
import src.util.GameView;
import src.util.GameLocale;
import src.util.GameEnvironment;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Body;
import src.base.Okazari.OkazariType;
import src.draw.Dimension4y;
import src.draw.ModLoader;
import src.draw.MyPane;
import src.draw.Point4y;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.CriticalDamegeType;
import src.enums.HairState;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.system.BodyLayer;
import src.system.ResourceUtil;
import src.util.IniFileUtil;
import src.util.YukkuriUtil;

/*****************************************************
 * まりさ。れいむまりさ、つむりまりさはこれを継承している
 */
public class Marisa extends Body {

	private static final long serialVersionUID = -7022828510837286120L;
	/** まりさのタイプ */
	public static final int type = 0;
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
	private int anImageVerStateCtrlNagasi[][] = new int[ImageCode.values().length][2];

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
		if (!isbImageNagasiMode() || imagesNagasi == null) {
			layer.getImage()[index] = imagePack[getBodyRank().getImageIndex()][type][direction
					* directionOffset[type][0]][getBodyAgeState().ordinal()];
			layer.getDir()[index] = direction * directionOffset[type][1];
		}
		// 流し絵の場合
		else {
			// インターバル毎に初期化する
			if (GameEnvironment.getInterval() == 0 && !isDead()) {
				for (int i = 0; i < ImageCode.values().length; i++) {
					anImageVerStateCtrlNagasi[i][1] = 0;
				}
			}
			// 前回と同じ表示
			if (anImageVerStateCtrlNagasi[type][1] == 1) {
				int nIndex = anImageVerStateCtrlNagasi[type][0];
				layer.getImage()[index] = imagesNagasi[type][direction
						* directionOffsetNagasi[type][0]][getBodyAgeState()
								.ordinal()][nIndex];
			} else {
				int nOtherVerCount = 0;
				for (int i = 0; i < ModLoader.getMaxImgOtherVer(); i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][i
							+ 1] != null) {
						nOtherVerCount++;
					}
				}
				if (nOtherVerCount != 0) {
					int nRndIndex = GameRandom.nextInt(nOtherVerCount + 1);
					anImageVerStateCtrlNagasi[type][0] = nRndIndex;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][nRndIndex];
				} else {
					anImageVerStateCtrlNagasi[type][0] = 0;
					layer.getImage()[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][0];
				}
				anImageVerStateCtrlNagasi[type][1] = 1;
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
		synchronized (SimYukkuri.lock) {
			int originalId = getUniqueID();
			// ドス化
			// ドスはフィールドに一体だけ
			if (!GameWorld.get().getCurrentMap().makeOrKillDos(true)) {
				return;
			}
			GameWorld.get().getCurrentMap().getBody().remove(this.getUniqueID());
			GameView.loadBodyImage(YukkuriType.DOSMARISA);
			Body to = new DosMarisa(getX(), getY(), getZ(), getBodyAgeState(), null, null);
			try {
				YukkuriUtil.changeBody(to, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			to.setUniqueID(originalId);
			GameWorld.get().getCurrentMap().getBody().put(to.getUniqueID(), to);
			// iniファイル再設定
			to.setBaseBodyFileName("dosmarisa");
			IniFileUtil.readYukkuriIniFile(to);
			if (MyPane.getSelectBody() == this) {
				MyPane.setSelectBody(to);
			}
			setRemoved(true);
		}
	}

	/**
	 * 突然変異チェックをする.
	 * まりさ→ドス.
	 * 
	 * @return 突然変異する際のゆっくり
	 */
	@Override
	public Body checkTransform() {
		// 自身が突然変異可能かチェック
		if (!canTransform())
			return null;

		// 自分以外に幸せを感じている大人のゆっくりが10体以上いる
		int nCount = 0;
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
		for (Body bOther : bodyList) {
			if (bOther == this) {
				continue;
			}
			if (bOther.isAdult() && bOther.isHappy()) {
				nCount++;
			}
		}
		if (nCount < 10) {
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
	public int getType() {
		return type;
	}

	@Override
	public int getHybridType(int partnerType) {
		switch (partnerType) {
			case Reimu.type:
			case WasaReimu.type:
				return ReimuMarisa.type;
			default:
				return Marisa.type;
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
		if (anMyName[getBodyAgeState().ordinal()] != null) {
			return anMyName[getBodyAgeState().ordinal()];
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
		if (anMyNameD[getBodyAgeState().ordinal()] != null) {
			return anMyNameD[getBodyAgeState().ordinal()];
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

			if (geteHairState() == HairState.DEFAULT) {
				idx += getImage(ImageCode.FRONT_HAIR.ordinal(), Const.LEFT, layer, idx);
			} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
			if (isbImageNagasiMode()) {
				if (getAge() % 8 == 0) {
					idx += getImage(ImageCode.MROLL_LEFT2_SHIT.ordinal(), Const.LEFT, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.MROLL_ACCESSORY_LEFT2.ordinal(), Const.LEFT, layer, idx);
					}
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_LEFT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.MROLL_RIGHT2_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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

					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.ROLL_LEFT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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

					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.ROLL_RIGHT_HAIR.ordinal(), Const.LEFT, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
	public Marisa(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
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
		getHUNGRYLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getHUNGRYLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 1;
		getSHITLIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getSHITLIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() * 2 + 1;
		getDAMAGELIMITorg()[AgeState.ADULT.ordinal()] *= factor;
		getDAMAGELIMITorg()[AgeState.CHILD.ordinal()] *= factor;
		getDAMAGELIMITorg()[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		setBABYLIMITorg((int) (getBABYLIMITorg() * factor));
		setCHILDLIMITorg((int) (getCHILDLIMITorg() * factor));
		setLIFELIMITorg((int) (getLIFELIMITorg() * factor));
		factor = Math.random() + 1;
		setRELAXPERIODorg((int) (getRELAXPERIODorg() * factor));
		setEXCITEPERIODorg((int) (getEXCITEPERIODorg() * factor));
		setPREGPERIODorg((int) (getPREGPERIODorg() * factor));
		setSLEEPPERIODorg((int) (getSLEEPPERIODorg() * factor));
		setACTIVEPERIODorg((int) (getACTIVEPERIODorg() * factor));
		setSameDest(GameRandom.nextInt(10) + 10);
		setDECLINEPERIODorg((int) (getDECLINEPERIODorg() * (Math.random() + 0.5)));
		setROBUSTNESS(GameRandom.nextInt(10) + 1);
		// EYESIGHT /= 1;
		factor = Math.random() + 1;
		getSTRENGTHorg()[AgeState.ADULT.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.CHILD.ordinal()] *= factor;
		getSTRENGTHorg()[AgeState.BABY.ordinal()] *= factor;
		speed = baseSpeed;
	}

	public int[][] getAnImageVerStateCtrlNagasi() {
		return anImageVerStateCtrlNagasi;
	}

	public void setAnImageVerStateCtrlNagasi(int[][] anImageVerStateCtrlNagasi) {
		this.anImageVerStateCtrlNagasi = anImageVerStateCtrlNagasi;
	}

}
