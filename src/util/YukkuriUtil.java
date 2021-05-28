package src.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import src.SimYukkuri;
import src.attachment.Ants;
import src.base.Body;
import src.draw.Terrarium;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.enums.YukkuriType;
import src.game.Dna;
import src.yukkuri.Ayaya;
import src.yukkuri.Deibu;
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

/***************************************************
 *   ゆっくり処理クラス
 */
public class YukkuriUtil {

	/**
	 *  クラス名からタイプ取得
	 * @param className クラス名
	 * @return ゆっくりのタイプ
	 */
	public static final YukkuriType getYukkuriType(String className) {
		YukkuriType ret = null;
		for (YukkuriType y : YukkuriType.values()) {
			if (y.className.equals(className)) {
				ret = y;
				break;
			}
		}
		return ret;
	}

	/**
	 *  タイプからクラス名取得
	 * @param type タイプ
	 * @return ゆっくりのクラス名
	 */
	public static final String getYukkuriClassName(int type) {
		String ret = null;
		for (YukkuriType y : YukkuriType.values()) {
			if (y.typeID == type) {
				ret = y.className;
				break;
			}
		}
		return ret;
	}

	/**
	 *  両親から赤ゆ一匹分のDNAを作成。
	 *  forceCreateをtrueで確実に赤ゆができる。まれに茎に１つも赤ゆができないのを回避できる
	 * @param mother 母ゆ
	 * @param father 父ゆ
	 * @param iFatherType 父のタイプ
	 * @param fatherrAtt 父の性格
	 * @param fatherInt 父の知性
	 * @param isRape レイプでできた子か
	 * @param fatherDamage 父のダメージ
	 * @param forceCreate 強制作成フラグ
	 * @return 赤ゆのDNA
	 */
	public static final Dna createBabyDna(Body mother, Body father, int iFatherType, Attitude fatherrAtt,
			Intelligence fatherInt, boolean isRape, boolean fatherDamage, boolean forceCreate) {
		Dna ret = null;

		// 母がいないのはあり得ないのでエラー
		if (mother == null) {
			return null;
		}

		// 種別の決定
		int babyType;
		int motherType = mother.getType();
		int fatherType = iFatherType;

		List<Integer> motherAncestorList = mother.getAncestorList();
		if (motherAncestorList != null && motherAncestorList.size() != 0) {
			if (SimYukkuri.RND.nextInt(100) == 0) {
				// 先祖返り
				int nSize = motherAncestorList.size();
				int nIndex = SimYukkuri.RND.nextInt(nSize);
				motherType = motherAncestorList.get(nIndex);
			}
		}
		if (father != null) {
			List<Integer> fatherAncestorList = father.getAncestorList();
			if (fatherAncestorList != null && fatherAncestorList.size() != 0) {
				if (SimYukkuri.RND.nextInt(100) == 0) {
					// 先祖返り
					int nSize = fatherAncestorList.size();
					int nIndex = SimYukkuri.RND.nextInt(nSize);
					fatherType = fatherAncestorList.get(nIndex);
				}
			}
		}

		boolean hybrid = false;
		boolean hybrid2 = false;
		if (SimYukkuri.RND.nextInt(2) == 0 && !forceCreate) {
			// 作成失敗
			return null;
		}

		// ハイブリッド判定
		// 両方ハイブリッドではない
		if ((fatherType != HybridYukkuri.type) && (motherType != HybridYukkuri.type)) {
			// 同じタイプならハイブッリドを作らない
			if (fatherType != motherType) {
				// 両方普通
				if (SimYukkuri.RND.nextInt(70) == 0) {
					hybrid = true;
					hybrid2 = true;
				}
			}
		} else if ((fatherType == HybridYukkuri.type) && (motherType == HybridYukkuri.type)) {
			// 両方ハイブリッド
			if (SimYukkuri.RND.nextInt(20) == 0)
				hybrid = true;
		} else {
			// 片方ハイブリッド
			if (SimYukkuri.RND.nextInt(50) == 0)
				hybrid = true;
		}

		// どちらかがドスまりさなら処理の都合でハイブリッドはなし
		if (fatherType == DosMarisa.type || motherType == DosMarisa.type) {
			hybrid = false;
		}

		if (hybrid) {
			if (hybrid2 && mother != null && SimYukkuri.RND.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else {
				babyType = HybridYukkuri.type;
			}
		} else {
			if (SimYukkuri.RND.nextBoolean()) {
				babyType = fatherType;
			} else {
				babyType = motherType;
			}

			// ドスまりさはただのまりさに変換
			if (babyType == DosMarisa.type) {
				babyType = Marisa.type;
			}
			if (babyType == Deibu.type) {
				babyType = Reimu.type;
			}
		}
		// チェンジリング判定
		// 上でどんな結果になろうと、チェンジリングが1/100の確率で発生する
		if (SimYukkuri.RND.nextInt(100) == 0) {
			babyType = getChangelingBabyType();
		}

		// ディフューザーでハイブリッド薬がまかれていたら強制的にハイブリッドにする
		if (Terrarium.hybridSteam) {
			if ((fatherType == Reimu.type) && (motherType == Marisa.type) && (mother != null)
					&& SimYukkuri.RND.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if ((fatherType == Marisa.type) && (motherType == Reimu.type) && (mother != null)
					&& SimYukkuri.RND.nextBoolean()) {
				babyType = mother.getHybridType(fatherType);
			} else if (fatherType != motherType) {
				babyType = HybridYukkuri.type;
			}
		}

		// 突然変異
		if ((babyType == Reimu.type) && SimYukkuri.RND.nextInt(20) == 0) {
			babyType = WasaReimu.type;
		} else if ((babyType == WasaReimu.type) && SimYukkuri.RND.nextInt(20) != 0) {
			babyType = Reimu.type;
		} else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type) && SimYukkuri.RND.nextInt(20) == 0) {
			babyType = MarisaTsumuri.type;
		} else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type) && SimYukkuri.RND.nextInt(20) == 0) {
			babyType = MarisaKotatsumuri.type;
		} else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type) && SimYukkuri.RND.nextInt(20) != 0) {
			babyType = Marisa.type;
		} else if ((babyType == Kimeemaru.type) && SimYukkuri.RND.nextInt(20) != 0) {
			babyType = Ayaya.type;
		} else if ((babyType == Ayaya.type) && SimYukkuri.RND.nextInt(20) == 0) {
			babyType = Kimeemaru.type;
		}

		if (mother.isOverPregnantLimit() || mother.isSick() || mother.isDamagedHeavily() || fatherDamage) {
			if (SimYukkuri.RND.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
				babyType = TarinaiReimu.type;
			} else {
				babyType = Tarinai.type;
			}
		}

		ret = new Dna();
		ret.type = babyType;
		ret.raperChild = isRape;
		ret.mother = mother;
		ret.father = father;

		// 性格の設定
		// 0(大善良+大善良)～8(ドゲス+ドゲス)
		int attBase = mother.getAttitude().ordinal() + fatherrAtt.ordinal();
		Attitude[] attitude = Attitude.values();

		switch (attBase) {
		case 0:
			if (SimYukkuri.RND.nextInt(20) == 0) {
				ret.attitude = attitude[2 + SimYukkuri.RND.nextInt(2)];
			} else {
				ret.attitude = attitude[SimYukkuri.RND.nextInt(3)];
			}
			break;
		case 1:
		case 2:
		case 3:
			if (SimYukkuri.RND.nextInt(15) == 0) {
				ret.attitude = attitude[SimYukkuri.RND.nextInt(2)];
			} else {
				ret.attitude = attitude[1 + SimYukkuri.RND.nextInt(4)];
			}
			break;
		case 4:
			if (SimYukkuri.RND.nextInt(10) == 0) {
				ret.attitude = attitude[SimYukkuri.RND.nextInt(3)];
			} else {
				ret.attitude = attitude[1 + SimYukkuri.RND.nextInt(4)];
			}
			break;
		case 5:
		case 6:
		case 7:
			if (SimYukkuri.RND.nextInt(15) == 0) {
				ret.attitude = attitude[1 + SimYukkuri.RND.nextInt(3)];
			} else {
				ret.attitude = attitude[2 + SimYukkuri.RND.nextInt(3)];
			}
			break;
		case 8:
			if (SimYukkuri.RND.nextInt(20) == 0) {
				ret.attitude = attitude[SimYukkuri.RND.nextInt(3)];
			} else {
				ret.attitude = attitude[3 + SimYukkuri.RND.nextInt(2)];
			}
			break;
		}

		// 知能の設定
		// 0(天才)～4(馬鹿)
		int intBase = mother.getIntelligence().ordinal() + fatherInt.ordinal();
		Intelligence[] intel = Intelligence.values();

		switch (intBase) {
		case 0:
			if (SimYukkuri.RND.nextInt(15) == 0) {
				ret.intelligence = intel[1 + SimYukkuri.RND.nextInt(2)];
			} else {
				ret.intelligence = intel[SimYukkuri.RND.nextInt(2)];
			}
			break;
		case 4:
			if (SimYukkuri.RND.nextInt(15) == 0) {
				ret.intelligence = intel[SimYukkuri.RND.nextInt(2)];
			} else {
				ret.intelligence = intel[1 + SimYukkuri.RND.nextInt(2)];
			}
			break;
		default:
			if (SimYukkuri.RND.nextInt(10) == 0) {
				ret.intelligence = intel[SimYukkuri.RND.nextInt(3)];
			} else {
				ret.intelligence = intel[1];
			}
		}
		return ret;
	}

	/**
	 * チェンジリングのゆっくりのタイプを取得する.
	 * チェンジリングとはいえ、親の餡とちがくなるとは限らない…
	 * @return チェンジリング後のゆっくりのタイプ
	 */
	public static int getChangelingBabyType() {
		// 66%で通常種、33%で希少種
		if (SimYukkuri.RND.nextInt(3) == 0) {
			//希少種
			return 1000 + SimYukkuri.RND.nextInt(12);
		} else {
			//通常種
			int i = SimYukkuri.RND.nextInt(6);
			if (i == 0) {//まりさ
				switch (SimYukkuri.RND.nextInt(5)) {
				case 1:
					return 2004;//こたつむり
				case 2:
					return 2002;//つむり
				default:
					return 0;//普通のまりさ
				}
			} else if (i == 1) {//れいむ
				switch (SimYukkuri.RND.nextInt(4)) {
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

	// コピーしたくない変数はここで定義
	// 主にゆっくり固有のステータス
	private static final String[] NOCOPY_FIELD = {
			"bodySpr",
			"expandSpr",
			"braidSpr",
			"EATAMOUNT",
			"WEIGHT",
			"HUNGRYLIMIT",
			"SHITLIMIT",
			"DAMAGELIMIT",
			"STRESSLIMIT",
			"TANGLEVEL",
			"BABYLIMIT",
			"CHILDLIMIT",
			"LIFELIMIT",
			"LOVEPLAYERLIMIT",
			"ROTTINGTIME",
			"STEP",
			"RELAXPERIOD",
			"EXCITEPERIOD",
			"PREGPERIOD",
			"SLEEPPERIOD",
			"ACTIVEPERIOD",
			"ANGRYPERIOD",
			"SCAREPERIOD",
			"sameDest",
			"DECLINEPERIOD",
			"DISCIPLINELIMIT",
			"BLOCKEDLIMIT",
			"DIRTYPERIOD",
			"ROBUSTNESS",
			"STRENGTH",
			"EYESIGHT",
			"INCUBATIONPERIOD",
			"speed",
			"Ycost",
			"YValue",
			"AValue",
			"anBabyName",
			"anChildName",
			"anAdultName",
			"anMyName",
			"anBabyNameD",
			"anChildNameD",
			"anAdultNameD",
			"anMyNameD",
			"baseBodyFileName"
	};

	/**
	 * ゆっくりのステータスをfrom->toへ複製
	 * シャローコピーなので複製元はbodyListから外しておかないと予期しない動作になるので注意
	 * @param to 変異後のゆっくり
	 * @param from 変異前のゆっくり
	 * @throws Exception リフレクションでコピー中に発生する例外
	 */
	public static final void changeBody(Body to, Body from) throws Exception {
		Field[] fromField = null;
		Class<?> toClass = null;
		Field toField = null;
		// Objクラスのコピー
		fromField = from.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass();

		for (int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if (Modifier.isFinal(mod)) {
				continue;
			}
			if (Modifier.isStatic(mod)) {
				continue;
			}
			if (isNoCopyField(fromField[i].getName())) {
				continue;
			}
			toField = toClass.getDeclaredField(fromField[i].getName());
			toField.setAccessible(true);
			fromField[i].setAccessible(true);
			toField.set(to, fromField[i].get(from));
		}

		// BodyAttributesクラスのコピー
		fromField = from.getClass().getSuperclass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass().getSuperclass();

		for (int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if (Modifier.isFinal(mod)) {
				continue;
			}
			if (Modifier.isStatic(mod)) {
				continue;
			}
			if (isNoCopyField(fromField[i].getName())) {
				continue;
			}
			toField = toClass.getDeclaredField(fromField[i].getName());
			toField.setAccessible(true);
			fromField[i].setAccessible(true);
			toField.set(to, fromField[i].get(from));
		}

		// Bodyクラスのコピー
		fromField = from.getClass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass();

		for (int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if (Modifier.isFinal(mod)) {
				continue;
			}
			if (Modifier.isStatic(mod)) {
				continue;
			}
			if (isNoCopyField(fromField[i].getName())) {
				continue;
			}
			toField = toClass.getDeclaredField(fromField[i].getName());
			toField.setAccessible(true);
			fromField[i].setAccessible(true);
			toField.set(to, fromField[i].get(from));

		}
		//まりさ、れいむクラスのコピーはしない（意味がない）

		//--------------------------------------------------
		// 家族関係の再設定
		Body partner = from.getPartner();
		if (partner != null && partner.getPartner() == from) {
			partner.setPartner(to);
		}

		if (from.getChildrenList() != null) {
			for (Body child : from.getChildrenList()) {
				if (child.getParents()[0] == from) {
					child.getParents()[0] = to;
				}
				if (child.getParents()[1] == from) {
					child.getParents()[1] = to;
				}
			}
		}

		//--------------------------------------------------
		// 身分の補正
		to.setBodyRank(from.getBodyRank());
		to.setPublicRank(from.getPublicRank());
		// 年齢の補正
		switch (from.getBodyAgeState()) {
		case BABY:
			to.setAge(0);
			break;
		case CHILD:
			to.setAge(to.getBABYLIMIT() + 1);
			break;
		case ADULT:
		default:
			to.setAge(to.getCHILDLIMIT() + 1);
			break;
		}
	}

	private static boolean isNoCopyField(String name) {
		for (String f : NOCOPY_FIELD) {
			if (f.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 新規でアリがたかるかどうか判定し、判定hitの場合はアリをたからせる。
	 * @param b アリがたかるかどうか判定したいゆっくりのインスタンス
	 */
	public static void judgeNewAnt(Body b) {
		int antProbability = 1;// アリのたかる確率
		switch (b.getBodyAgeState()) {
		case BABY:
			antProbability = 240000;
			break;
		case CHILD:
			antProbability = 480000;
			break;
		case ADULT:
			antProbability = 960000;
			break;
		default:
			//NOP.
		}
		// 汚い、またはダメージを受けていると倍の確率でたかられる
		if (b.isDirty() || b.isDamaged()) {
			antProbability /= 2;
		}
		// ジャンプできない状態だとさらに倍の確率
		if (b.isDontJump()) {
			antProbability /= 2;
		}
		if (SimYukkuri.RND.nextInt(antProbability) == 1) {
			b.addAttachment(new Ants(b));
			b.clearEvent();
		}
	}

	/**
	 * ランダムなゆっくりタイプを取得する.
	 * ドスが親の場合は他のまりさが出る。
	 * @param parent 親のゆっくり（ドスチェック）
	 * @return ランダムなタイプのゆっくりタイプ（int）
	 */
	public static int getRandomYukkuriType(Body parent) {
		int babyType = 0;
		int i = SimYukkuri.RND.nextInt(5);
		if (i == 0 || i == 1) {
			babyType = SimYukkuri.RND.nextInt(12);
			switch (babyType) {
			case 0: // まりさ
			case 8:
				babyType = getMarisaType();
				break;
			case 1: // れいむ
			case 9:
				switch (SimYukkuri.RND.nextInt(5)) {
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
			case 11: //希少種
				babyType = 1000 + SimYukkuri.RND.nextInt(12);
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
				babyType = SimYukkuri.RND.nextInt(6);
			}
		}
		return babyType;
	}

	/**
	 * まりさの子供は何のまりさかランダムで決定。
	 * @return まりさの子供タイプ
	 */
	public static int getMarisaType() {
		switch (SimYukkuri.RND.nextInt(5)) {
		case 1:
			return 2004;//こたつむり
		case 2:
			return 2002;//つむり
		default:
			return 0;
		}
	}
}
