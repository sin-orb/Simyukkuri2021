package org.simyukkuri.engine.birth;

import java.util.List;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameRandom;

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
	public static YukkuriType getChangelingBabyType() {
		// 66%で通常種、33%で希少種
		if (GameRandom.nextInt(3) == 0) {
			// 希少種
			return YukkuriType.fromTypeID(1000 + GameRandom.nextInt(12));
		} else {
			// 通常種
			int i = GameRandom.nextInt(6);
			if (i == 0) {// まりさ
				switch (GameRandom.nextInt(5)) {
					case 1:
						return YukkuriType.MARISAKOTATSUMURI;
					case 2:
						return YukkuriType.MARISATSUMURI;
					default:
						return YukkuriType.MARISA;
				}
			} else if (i == 1) {// れいむ
				switch (GameRandom.nextInt(4)) {
					case 2:
						return YukkuriType.WASAREIMU;
					case 3:
						return YukkuriType.DEIBU;
					default:
						return YukkuriType.REIMU;
				}
			} else {
				return YukkuriType.fromTypeID(i);
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
	public static YukkuriType getRandomYukkuriType(Yukkuri parent) {
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
							babyType = YukkuriType.REIMU.getTypeID();// 普通のれいむ
							break;
						case 1:
							babyType = YukkuriType.WASAREIMU.getTypeID();// わされいむ
							break;
						case 4:
							babyType = YukkuriType.TARINAI.getTypeID();// たりないれいむ
							break;
						case 3:
							babyType = YukkuriType.DEIBU.getTypeID();// でいぶ
							break;
						default:
							babyType = YukkuriType.REIMU.getTypeID();// 普通のれいむ
					}
					break;
				case 3: // ありす
					babyType = YukkuriType.ALICE.getTypeID();
					break;
				case 4: // みょん
					babyType = YukkuriType.MYON.getTypeID();
					break;
				case 5: // ちぇん
					babyType = YukkuriType.CHEN.getTypeID();
					break;
				case 6: // たりないゆ
					babyType = YukkuriType.TARINAI.getTypeID();
					break;
				case 7: // ゆるさなえ
					babyType = YukkuriType.YURUSANAE.getTypeID();
					break;
				case 10: // ぱちゅりー
					babyType = YukkuriType.PATCH.getTypeID();
					break;
				case 11: // 希少種
					babyType = 1000 + GameRandom.nextInt(12);
					break;
			}
		} else {
			if (parent != null) {
				babyType = parent.getType().getTypeID();
				// 親がドスなら他のまりさが均等に出る
				if (babyType == YukkuriType.DOSMARISA.getTypeID()) {
					babyType = getMarisaType();
				}
			} else {
				babyType = GameRandom.nextInt(6);
			}
		}
		return YukkuriType.fromTypeID(babyType);
	}

	/**
	 * まりさの子供は何のまりさかランダムで決定.
	 *
	 * @return まりさの子供タイプ
	 */
	public static int getMarisaType() {
		switch (GameRandom.nextInt(5)) {
			case 1:
				return YukkuriType.MARISAKOTATSUMURI.getTypeID();// こたつむり
			case 2:
				return YukkuriType.MARISATSUMURI.getTypeID();// つむり
			default:
				return YukkuriType.MARISA.getTypeID();
		}
	}

	/**
	 * createBabyDna 用の子タイプ決定.
	 *
	 * @param mother       母体
	 * @param father       父体
	 * @param iFatherType  父タイプ
	 * @param forceCreate  強制作成フラグ
	 * @param fatherDamage 父ダメージ
	 * @return 子タイプ
	 */
	public static YukkuriType resolveBabyType(Yukkuri mother, Yukkuri father, YukkuriType fatherType,
			boolean forceCreate,
			boolean fatherDamage) {
		YukkuriType babyType;
		YukkuriType motherType = mother.getType();

		motherType = applyAncestorReversion(mother.getAncestors(), motherType);
		if (father != null) {
			fatherType = applyAncestorReversion(father.getAncestors(), fatherType);
		}

		if (GameRandom.nextInt(2) == 0 && !forceCreate) {
			return null;
		}

		boolean hybrid = false;
		boolean hybrid2 = false;
		if ((fatherType != YukkuriType.HYBRIDYUKKURI) && (motherType != YukkuriType.HYBRIDYUKKURI)) {
			if (fatherType != motherType) {
				if (GameRandom.nextInt(70) == 0) {
					hybrid = true;
					hybrid2 = true;
				}
			}
		} else if ((fatherType == YukkuriType.HYBRIDYUKKURI) && (motherType == YukkuriType.HYBRIDYUKKURI)) {
			if (GameRandom.nextInt(20) == 0) {
				hybrid = true;
			}
		} else {
			if (GameRandom.nextInt(50) == 0) {
				hybrid = true;
			}
		}

		if (fatherType == YukkuriType.DOSMARISA || motherType == YukkuriType.DOSMARISA) {
			hybrid = false;
		}

		if (hybrid) {
			if (hybrid2 && mother != null && GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else {
				babyType = YukkuriType.HYBRIDYUKKURI;
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
			if ((fatherType == YukkuriType.REIMU) && (motherType == YukkuriType.MARISA) && (mother != null)
					&& GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if ((fatherType == YukkuriType.MARISA) && (motherType == YukkuriType.REIMU) && (mother != null)
					&& GameRandom.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if (fatherType != motherType) {
				babyType = YukkuriType.HYBRIDYUKKURI;
			}
		}

		babyType = applyMutation(babyType);
		return applyConditionCorrection(mother, fatherDamage, babyType);
	}

	private static YukkuriType applyAncestorReversion(List<Integer> ancestorList, YukkuriType type) {
		if (ancestorList != null && !ancestorList.isEmpty() && GameRandom.nextInt(100) == 0) {
			return YukkuriType.fromTypeID(ancestorList.get(GameRandom.nextInt(ancestorList.size())));
		}
		return type;
	}

	private static YukkuriType applyMutation(YukkuriType babyType) {
		if ((babyType == YukkuriType.REIMU) && GameRandom.nextInt(20) == 0) {
			return YukkuriType.WASAREIMU;
		} else if ((babyType == YukkuriType.WASAREIMU) && GameRandom.nextInt(20) != 0) {
			return YukkuriType.REIMU;
		} else if ((babyType == YukkuriType.MARISA || babyType == YukkuriType.MARISAKOTATSUMURI)
				&& GameRandom.nextInt(20) == 0) {
			return YukkuriType.MARISATSUMURI;
		} else if ((babyType == YukkuriType.MARISA || babyType == YukkuriType.MARISATSUMURI)
				&& GameRandom.nextInt(20) == 0) {
			return YukkuriType.MARISAKOTATSUMURI;
		} else if ((babyType == YukkuriType.MARISATSUMURI
				|| babyType == YukkuriType.MARISAKOTATSUMURI)
				&& GameRandom.nextInt(20) != 0) {
			return YukkuriType.MARISA;
		} else if ((babyType == YukkuriType.KIMEEMARU) && GameRandom.nextInt(20) != 0) {
			return YukkuriType.AYAYA;
		} else if ((babyType == YukkuriType.AYAYA) && GameRandom.nextInt(20) == 0) {
			return YukkuriType.KIMEEMARU;
		}
		return babyType;
	}

	private static YukkuriType applyConditionCorrection(Yukkuri mother, boolean fatherDamage, YukkuriType babyType) {
		if (mother.isOverPregnantLimit() || mother.isSick() || mother.isDamagedHeavily() || fatherDamage) {
			if (GameRandom.nextBoolean()
					&& (babyType == YukkuriType.REIMU || babyType == YukkuriType.WASAREIMU)) {
				return YukkuriType.TARINAIREIMU;
			}
			return YukkuriType.TARINAI;
		}
		return babyType;
	}

	/**
	 * 父体のゆっくりタイプを祖先遡り補正して返す。
	 *
	 * @param iFatherType 父タイプID（父体がいない場合の初期値）
	 * @param father      父体（null 可）
	 * @return 補正後の父タイプID
	 */
	public static int resolveFatherType(int iFatherType, Yukkuri father) {
		if (father == null) {
			return iFatherType;
		}
		return applyAncestorReversion(father.getAncestors(), YukkuriType.fromTypeID(iFatherType)).getTypeID();
	}

	/**
	 * 母体のゆっくりタイプを祖先遡り補正して返す。
	 *
	 * @param mother 母体
	 * @return 補正後の母タイプID
	 */
	public static int resolveMotherType(Yukkuri mother) {
		return applyAncestorReversion(mother.getAncestors(), mother.getType()).getTypeID();
	}

	/**
	 * 親の性格から子供の性格を確率的に決定して返す。
	 *
	 * @param mother     母体
	 * @param fatherrAtt 父の性格
	 * @return 子供の性格
	 */
	public static Attitude resolveAttitude(Yukkuri mother, Attitude fatherrAtt) {
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

	/**
	 * 親の知性から子供の知性を確率的に決定して返す。
	 *
	 * @param mother     母体
	 * @param fatherInt  父の知性
	 * @return 子供の知性
	 */
	public static Intelligence resolveIntelligence(Yukkuri mother, Intelligence fatherInt) {
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
