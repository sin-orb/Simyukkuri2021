package src.yukkuri;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.Okazari.OkazariType;
import src.draw.ModLoader;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BodyRank;
import src.enums.CriticalDamegeType;
import src.enums.HairState;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.Numbering;
import src.enums.PlayStyle;
import src.enums.YukkuriType;
import src.logic.ToyLogic;
import src.system.BodyLayer;
import src.system.MessagePool;
import src.util.IniFileUtil;
import src.util.YukkuriUtil;

/*****************************************************
 * れいむ。でいぶ、わさ、まりされいむはこれを継承している
*/
public class Reimu extends Body implements java.io.Serializable {
	static final long serialVersionUID = 2L;
	/** れいむのタイプ */
	public static final int type = 1;
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
			.values().length][2][3][ModLoader.nMaxImgOtherVer + 1];
	private static int directionOffset[][] = new int[ImageCode.values().length][2];
	private static int directionOffsetNagasi[][] = new int[ImageCode.values().length][2];
	private static Dimension[] boundary = new Dimension[3];
	private static Dimension[] braidBoundary = new Dimension[3];
	private static boolean imageLoaded = false;
	private static Map<String, Point[]> AttachOffset = new HashMap<String, Point[]>();
	//---
	// iniファイルから読み込んだ初期値
	private static int baseSpeed = 100;
	// 個別表情管理(まりちゃ流し用)
	private int anImageVerStateCtrlNagasi[][] = new int[ImageCode.values().length][2];
	/** イメージのロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		if (imageLoaded)
			return;

		boolean res;
		res = ModLoader.loadBodyImagePack(loader, imagesNora, directionOffset, ModLoader.YK_WORD_NORA, baseFileName,
				io);
		if (!res) {
			imagesNora = null;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesKai, directionOffset, null, baseFileName, io);
		if (!res) {
			imagesKai = null;
		}
		imagePack[BodyRank.KAIYU.imageIndex] = imagesKai;
		if (imagesNora != null) {
			imagePack[BodyRank.NORAYU.imageIndex] = imagesNora;
		} else {
			imagePack[BodyRank.NORAYU.imageIndex] = imagesKai;
		}
		res = ModLoader.loadBodyImagePack(loader, imagesNagasi, directionOffsetNagasi, ModLoader.YK_WORD_NAGASI,
				baseFileName, io);
		if (!res) {
			imagesNagasi = null;
		}

		ModLoader.setImageSize(imagesKai, boundary, braidBoundary, io);

		imageLoaded = true;
	}
	/**
	 * INIファイルをロードする
	 * @param loader クラスローダ
	 */
	public static void loadIniFile(ClassLoader loader) {
		AttachOffset = ModLoader.loadBodyIniMap(loader, ModLoader.DATA_INI_DIR, baseFileName);
		baseSpeed = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, baseFileName, "speed");
	}
	@Override
	public boolean isImageLoaded() {
		return imageLoaded;
	}
	@Override
	public int getImage(int type, int direction, BodyLayer layer, int index) {
		if (!isbImageNagasiMode() || imagesNagasi == null) {
			layer.image[index] = imagePack[getBodyRank().imageIndex][type][direction
					* directionOffset[type][0]][getBodyAgeState().ordinal()];
			layer.dir[index] = direction * directionOffset[type][1];
		} else {
			// インターバル毎に初期化する
			if (Terrarium.getInterval() == 0 && !isDead()) {
				for (int i = 0; i < ImageCode.values().length; i++) {
					anImageVerStateCtrlNagasi[i][1] = 0;
				}
			}

			// 前回と同じ表示
			if (anImageVerStateCtrlNagasi[type][1] == 1) {
				int nIndex = anImageVerStateCtrlNagasi[type][0];
				layer.image[index] = imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState()
						.ordinal()][nIndex];

			} else {
				int nOtherVerCount = 0;
				for (int i = 0; i < ModLoader.nMaxImgOtherVer; i++) {
					if (imagesNagasi[type][direction * directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][i
							+ 1] != null) {
						nOtherVerCount++;
					}
				}

				if (nOtherVerCount != 0) {
					int nRndIndex = SimYukkuri.RND.nextInt(nOtherVerCount + 1);
					anImageVerStateCtrlNagasi[type][0] = nRndIndex;
					layer.image[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][nRndIndex];
				} else {
					anImageVerStateCtrlNagasi[type][0] = 0;
					layer.image[index] = imagesNagasi[type][direction
							* directionOffsetNagasi[type][0]][getBodyAgeState().ordinal()][0];
				}

				anImageVerStateCtrlNagasi[type][1] = 1;
			}

			layer.dir[index] = direction * directionOffsetNagasi[type][1];
		}
		return 1;
	}

	/**
	 * ゆ虐神拳を受けてドス等にトランスフォーム可能かどうかを返却する.
	 * 実ゆでなければ可能を返却する.
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
		synchronized (SimYukkuri.lock) {
			List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
			bodyList.remove(this);
			SimYukkuri.mypane.loadBodyImage(YukkuriType.DEIBU);
			Body to = new Deibu(getX(), getY(), getZ(), getBodyAgeState(), null, null);
			try {
				YukkuriUtil.changeBody(to, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			to.setUniqueID(Numbering.INSTANCE.numberingYukkuriID());
			bodyList.add(to);
			to.setBaseBodyFileName("deibu");
			IniFileUtil.readYukkuriIniFile(to);
			if (MyPane.selectBody == this) {
				MyPane.selectBody = to;
			}
		}
		this.remove();
	}

	/**
	 * 突然変異チェックをする.
	 * れいむ→でいぶ.
	 * @return 突然変異する際のゆっくり
	 */
	@Override
	public Body checkTransform() {
		// 自身が突然変異可能かチェック
		if (!canTransform())
			return null;
		// 大人であり、夫がいて夫がゲスではなく、自身がゲスではない
		Body partner = getPartner();
		if (isAdult() && partner != null && !partner.isRude() && isRude()) {
			if (SimYukkuri.RND.nextInt(1000) == 0) {
				return this;
			}
		}
		//または、ゲスでいぶの子供である
		Body mother = getMother();
		if (!isAdult() && mother != null && mother.getType() == Deibu.type && mother.isRude()) {
			//ゲスバカとドゲスは確実にでいぶ化
			if ((isRude() && getIntelligence() != Intelligence.FOOL) || getAttitude() == Attitude.SUPER_SHITHEAD) {
				return this;
			}
			//あとは1/2。
			else if (SimYukkuri.RND.nextBoolean()) {
				return this;
			}
		}
		return null;
	}
	@Override
	public Point[] getMountPoint(String key) {
		return AttachOffset.get(key);
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getHybridType(int partnerType) {
		switch (partnerType) {
		case Marisa.type:
			return MarisaReimu.type;
		default:
			return Reimu.type;
		}
	}
	@Override
	public String getNameJ() {
		return nameJ;
	}
	@Override
	public String getMyName() {
		if (anMyName[getBodyAgeState().ordinal()] != null) {
			return anMyName[getBodyAgeState().ordinal()];
		}
		return nameJ;
	}
	@Override
	public String getMyNameD() {
		if (anMyNameD[getBodyAgeState().ordinal()] != null) {
			return anMyNameD[getBodyAgeState().ordinal()];
		}
		return getMyName();
	}
	@Override
	public String getNameE() {
		return nameE;
	}

	@Override
	public String getNameJ2() {
		return "";
	}

	@Override
	public String getNameE2() {
		return "";
	}

	// 胴体のベースグラフィックを返す
	// mode[0] 正面向きか横向きか
	@Override
	public int getBodyBaseImage(BodyLayer layer) {
		int direction = this.getDirection().ordinal();
		int idx = 0;

		layer.option[0] = 0;
		layer.option[1] = 0;
		layer.option[2] = 0;

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
		//れいむ種専用
		else if (isYunnyaa()) {
			//ゆんやあ
			/*if(getImageNagasiMode())*/ {
				if (getAge() % 6 <= 1) {
					idx += getImage(ImageCode.YUNYAA1.ordinal(), direction, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA1_ACCESSORY.ordinal(), direction, layer, idx);
					}
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA1_HAIR.ordinal(), direction, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA2_HAIR.ordinal(), direction, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
				} else /*if (getAge() % 6 <= 3)*/ {
					idx += getImage(ImageCode.YUNYAA3.ordinal(), direction, layer, idx);

					if (getOkazari() != null && getOkazari().getOkazariType() == OkazariType.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA3_ACCESSORY.ordinal(), direction, layer, idx);
					}
					if (geteHairState() == HairState.DEFAULT) {
						idx += getImage(ImageCode.YUNYAA3_HAIR.ordinal(), direction, layer, idx);
					} else if (geteHairState() == HairState.BRINDLED1 || geteHairState() == HairState.BRINDLED2) {
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
			//皮むき時
			if (isPealed()) {
				idx += getImage(ImageCode.PEALED.ordinal(), direction, layer, idx);
			}
			// 通常時
			else {
				idx += getImage(ImageCode.BODY.ordinal(), direction, layer, idx);
			}
			layer.option[0] = 1;
		}
		return idx;
	}

	//ゆっくりしてる時のアクション
	//個別の動作がある種ははこれをオーバーライドしているので注意
	@Override
	public void killTime() {
		if (getCurrentEvent() != null)
			return;
		if (getPlaying() != null)
			return;
		int p = SimYukkuri.RND.nextInt(50);
		//7/50でキリッ
		if (p <= 6) {
			getInVain(true);
		}
		//7/50でのびのび
		else if (p <= 14) {
			// if yukkuri is not rude, she goes into her shell by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Nobinobi), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		//7/50でおうた
		else if (p <= 21) {
			setMessage(MessagePool.getMessage(this, MessagePool.Action.ProudChildsSING), 40);
			setNobinobi(true);
			addStress(-30);
			stay(40);
		}
		//7/50でふりふり
		else if (p <= 28 && willingFurifuri()) {
			//if yukkuri is rude, she will not do furifuri by discipline.
			setMessage(MessagePool.getMessage(this, MessagePool.Action.FuriFuri), 30);
			setFurifuri(true);
			addStress(-50);
			stay(30);
		}
		//7/50でふりふりで腹減った
		else if ((p <= 35 && isHungry()) || isSoHungry()) {
			// 空腹時
			setMessage(MessagePool.getMessage(this, MessagePool.Action.Hungry), 30);
			stay(30);
		}
		//4/50でおもちゃで遊ぶ
		else if (p <= 39) {
			if (ToyLogic.checkToy(this)) {
				setPlaying(PlayStyle.BALL);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		//3/50でトランポリンで遊ぶ
		else if (p <= 42) {
			if (ToyLogic.checkTrampoline(this)) {
				setPlaying(PlayStyle.TRAMPOLINE);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		}
		//1/50ですいーで遊ぶ
		else if (p <= 43) {
			if (ToyLogic.checkSui(this)) {
				setPlaying(PlayStyle.SUI);
				playingLimit = 150 + SimYukkuri.RND.nextInt(100) - 49;
				return;
			} else
				killTime();
		} else {
			// おくるみありで汚れていない場合
			if (isHasPants() && !isDirty() && SimYukkuri.RND.nextInt(5) == 0) {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.RelaxOkurumi));
			} else {
				setMessage(MessagePool.getMessage(this, MessagePool.Action.Relax));
			}
			addStress(-50);
			stay(30);
		}
	}

	/** コンストラクタ */
	public Reimu(int initX, int initY, int initZ, AgeState initAgeState, Body p1, Body p2) {
		super(initX, initY, initZ, initAgeState, p1, p2);
		setBoundary(boundary, braidBoundary);
		setMsgType(YukkuriType.REIMU);
		setShitType(YukkuriType.REIMU);
		setBaseBodyFileName(baseFileName);
		IniFileUtil.readYukkuriIniFile(this);
	}
	@Override
	public void tuneParameters() {
		/*if (SimYukkuri.RND.nextBoolean()) {
		motherhood = true;
		}*/
		double factor = Math.random() * 2 + 1;
		HUNGRYLIMIT[AgeState.ADULT.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.CHILD.ordinal()] *= factor;
		HUNGRYLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() * 2 + 1;
		SHITLIMIT[AgeState.ADULT.ordinal()] *= factor;
		SHITLIMIT[AgeState.CHILD.ordinal()] *= factor;
		SHITLIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		DAMAGELIMIT[AgeState.ADULT.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.CHILD.ordinal()] *= factor;
		DAMAGELIMIT[AgeState.BABY.ordinal()] *= factor;
		factor = Math.random() + 0.5;
		BABYLIMIT *= factor;
		CHILDLIMIT *= factor;
		LIFELIMIT *= factor;
		factor = Math.random() + 1;
		RELAXPERIOD *= factor;
		EXCITEPERIOD *= factor;
		PREGPERIOD *= factor;
		SLEEPPERIOD *= factor;
		ACTIVEPERIOD *= factor;
		sameDest = SimYukkuri.RND.nextInt(20) + 20;
		DECLINEPERIOD *= (Math.random() + 0.5);
		ROBUSTNESS = SimYukkuri.RND.nextInt(10) + 1;
		//EYESIGHT /= 4;
		factor = Math.random() + 0.5;
		STRENGTH[AgeState.ADULT.ordinal()] *= factor;
		STRENGTH[AgeState.CHILD.ordinal()] *= factor;
		STRENGTH[AgeState.BABY.ordinal()] *= factor;

		//speed = 120;
		speed = baseSpeed;
	}
}