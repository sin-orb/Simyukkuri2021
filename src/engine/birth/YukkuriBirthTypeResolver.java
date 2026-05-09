package src.engine.birth;

import src.base.Body;
import src.enums.Intelligence;
import src.enums.Attitude;
import src.enums.YukkuriType;
import java.util.List;
import src.util.GameRandom;
import src.util.GameEnvironment;
import src.yukkuri.Ayaya;
import src.yukkuri.DosMarisa;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Reimu;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.WasaReimu;

/**
 * 産まれるゆっくりの種別決定ロジック.
 */
public final class YukkuriBirthTypeResolver {
	private YukkuriBirthTypeResolver() {
	}

	/**
	 * チェンジリングのゆっくりのタイプを取得する.
	 * チェンジリングとはいえ、親の餡とちがくなるとは限らない…
	 *
	 * @return チェンジリング後のゆっくりのタイプ
	 */
	public static int getChangelingBabyType() {
		// 66%で通常種、33%で希少種
		if (GameRandom.nextInt(3) == 0) {
			//希少種
			return 1000 + GameRandom.nextInt(12);
		} else {
			//通常種
			int i = GameRandom.nextInt(6);
			if (i == 0) {//まりさ
				switch (GameRandom.nextInt(5)) {
				case 1:
					return 2004;//こたつむり
				case 2:
					return 2002;//つむり
				default:
					return 0;//普通のまりさ
				}
			} else if (i == 1) {//れいむ
				switch (GameRandom.nextInt(4)) {
				case 2:
					return 2001;//わされいむ
				case 3:
					return 2005;//でいぶ
				default:
					return 1;//普通のれいむ
				}
			} else {
				return i;
			}
		}
	}

	/**
	 * ランダムなゆっくりタイプを取得する.
	 * ドスが親の場合は他のまりさが出る.
	 *
	 * @param parent 親のゆっくり（ドスチェック）
	 * @return ランダムなタイプのゆっくりタイプ（int）
	 */
	public static int getRandomYukkuriType(Body parent) {
		int babyType = 0;
		int i = GameRandom.nextInt(5);
		if (i == 0 || i == 1) {
			babyType = GameRandom.nextInt(12);
			switch (babyType) {
			case 0: // まりさ
			case 8:
				babyType = getMarisaType();
				break;
			case 1: // れいむ
			case 9:
				switch (GameRandom.nextInt(5)) {
				case 0:
				case 2:
					babyType = 1;//普通のれいむ
					break;
				case 1:
					babyType = 2001;//わされいむ
					break;
				case 4:
					babyType = 2007;//たりないれいむ
					break;
				case 3:
					babyType = 2005;//でいぶ
					break;
				default:
					babyType = 1;
				}
				break;
			case 3: // ありす
				babyType = 2;
				break;
			case 4: // みょん
				babyType = 5;
				break;
			case 5: // ちぇん
				babyType = 4;
				break;
			case 6: // たりないゆ
				babyType = 2000;
				break;
			case 7: // ゆるさなえ
				babyType = 1000;
				break;
			case 10: // ぱちゅりー
				babyType = 3;
				break;
			case 11: // 希少種
				babyType = 1000 + GameRandom.nextInt(12);
				break;
			}
		} else {
			if (parent != null) {
				babyType = parent.getType();
				// 親がドスなら他のまりさが均等に出る
				if (babyType == 2006) {
					babyType = getMarisaType();
				}
			} else {
				babyType = GameRandom.nextInt(6);
			}
		}
		return babyType;
	}

	/**
	 * まりさの子供は何のまりさかランダムで決定.
	 *
	 * @return まりさの子供タイプ
	 */
	public static int getMarisaType() {
		switch (GameRandom.nextInt(5)) {
		case 1:
			return 2004;//こたつむり
		case 2:
			return 2002;//つむり
		default:
			return 0;
		}
	}

	/**
	 * createBabyDna 用の子タイプ決定.
	 *
	 * @param mother 母体
	 * @param father 父体
	 * @param iFatherType 父タイプ
	 * @param forceCreate 強制作成フラグ
	 * @param fatherDamage 父ダメージ
	 * @return 子タイプ
	 */
	public static int resolveBabyType(Body mother, Body father, int iFatherType, boolean forceCreate,
			boolean fatherDamage) {
		int babyType;
		int motherType = mother.getType();
		int fatherType = iFatherType;

		motherType = applyAncestorReversion(mother.getAncestorList(), motherType);
		if (father != null) {
			fatherType = applyAncestorReversion(father.getAncestorList(), fatherType);
		}

		if (GameRandom.nextInt(2) == 0 && !forceCreate) {
			return -1;
		}

		boolean hybrid = false;
		boolean hybrid2 = false;
		if ((fatherType != HybridYukkuri.type) && (motherType != HybridYukkuri.type)) {
			if (fatherType != motherType) {
				if (GameRandom.nextInt(70) == 0) {
					hybrid = true;
					hybrid2 = true;
				}
			}
		} else if ((fatherType == HybridYukkuri.type) && (motherType == HybridYukkuri.type)) {
			if (GameRandom.nextInt(20) == 0) {
				hybrid = true;
			}
		} else {
			if (GameRandom.nextInt(50) == 0) {
				hybrid = true;
			}
		}

		if (fatherType == DosMarisa.type || motherType == DosMarisa.type) {
			hybrid = false;
		}

		if (hybrid) {
			if (hybrid2 && mother != null && GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else {
				babyType = HybridYukkuri.type;
			}
		} else {
			if (GameRandom.nextBoolean()) {
				babyType = fatherType;
			} else {
				babyType = motherType;
			}
			babyType = YukkuriType.normalizeOffspringType(babyType);
		}

		if (GameRandom.nextInt(100) == 0) {
			babyType = getChangelingBabyType();
		}

		if (GameEnvironment.isHybridSteam()) {
			if ((fatherType == Reimu.type) && (motherType == Marisa.type) && (mother != null)
					&& GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if ((fatherType == Marisa.type) && (motherType == Reimu.type) && (mother != null)
					&& GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if (fatherType != motherType) {
				babyType = HybridYukkuri.type;
			}
		}

		babyType = applyMutation(babyType);
		return applyConditionCorrection(mother, fatherDamage, babyType);
	}

	private static int applyAncestorReversion(List<Integer> ancestorList, int type) {
		if (ancestorList != null && !ancestorList.isEmpty() && GameRandom.nextInt(100) == 0) {
			return ancestorList.get(GameRandom.nextInt(ancestorList.size()));
		}
		return type;
	}

	private static int applyMutation(int babyType) {
		if ((babyType == Reimu.type) && GameRandom.nextInt(20) == 0) {
			return WasaReimu.type;
		} else if ((babyType == WasaReimu.type) && GameRandom.nextInt(20) != 0) {
			return Reimu.type;
		} else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type) && GameRandom.nextInt(20) == 0) {
			return MarisaTsumuri.type;
		} else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type) && GameRandom.nextInt(20) == 0) {
			return MarisaKotatsumuri.type;
		} else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type) && GameRandom.nextInt(20) != 0) {
			return Marisa.type;
		} else if ((babyType == Kimeemaru.type) && GameRandom.nextInt(20) != 0) {
			return Ayaya.type;
		} else if ((babyType == Ayaya.type) && GameRandom.nextInt(20) == 0) {
			return Kimeemaru.type;
		}
		return babyType;
	}

	private static int applyConditionCorrection(Body mother, boolean fatherDamage, int babyType) {
		if (mother.isOverPregnantLimit() || mother.isSick() || mother.isDamagedHeavily() || fatherDamage) {
			if (GameRandom.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
				return TarinaiReimu.type;
			}
			return Tarinai.type;
		}
		return babyType;
	}

	public static int resolveFatherType(int iFatherType, Body father) {
		if (father == null) {
			return iFatherType;
		}
		return applyAncestorReversion(father.getAncestorList(), iFatherType);
	}

	public static int resolveMotherType(Body mother) {
		return applyAncestorReversion(mother.getAncestorList(), mother.getType());
	}

	public static Attitude resolveAttitude(Body mother, Attitude fatherrAtt) {
		int attBase = mother.getAttitude().ordinal() + fatherrAtt.ordinal();
		Attitude[] attitude = Attitude.values();
		switch (attBase) {
		case 0:
			if (GameRandom.nextInt(20) == 0) {
				return attitude[2 + GameRandom.nextInt(2)];
			}
			return attitude[GameRandom.nextInt(3)];
		case 1:
		case 2:
		case 3:
			if (GameRandom.nextInt(15) == 0) {
				return attitude[GameRandom.nextInt(2)];
			}
			return attitude[1 + GameRandom.nextInt(4)];
		case 4:
			if (GameRandom.nextInt(10) == 0) {
				return attitude[GameRandom.nextInt(3)];
			}
			return attitude[1 + GameRandom.nextInt(4)];
		case 5:
		case 6:
		case 7:
			if (GameRandom.nextInt(15) == 0) {
				return attitude[1 + GameRandom.nextInt(3)];
			}
			return attitude[2 + GameRandom.nextInt(3)];
		case 8:
			if (GameRandom.nextInt(20) == 0) {
				return attitude[GameRandom.nextInt(3)];
			}
			return attitude[3 + GameRandom.nextInt(2)];
		default:
			return attitude[0];
		}
	}

	public static Intelligence resolveIntelligence(Body mother, Intelligence fatherInt) {
		int intBase = mother.getIntelligence().ordinal() + fatherInt.ordinal();
		Intelligence[] intel = Intelligence.values();
		switch (intBase) {
		case 0:
			if (GameRandom.nextInt(15) == 0) {
				return intel[1 + GameRandom.nextInt(2)];
			}
			return intel[GameRandom.nextInt(2)];
		case 4:
			if (GameRandom.nextInt(15) == 0) {
				return intel[GameRandom.nextInt(2)];
			}
			return intel[1 + GameRandom.nextInt(2)];
		default:
			if (GameRandom.nextInt(10) == 0) {
				return intel[GameRandom.nextInt(3)];
			}
			return intel[1];
		}
	}
}
