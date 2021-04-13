package src.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
  ゆっくり処理クラス

 */
public class YukkuriUtil {

	private static Random rnd = new Random();

	// クラス名からタイプ取得
	public static final YukkuriType getYukkuriType(String className) {
		YukkuriType ret = null;
		for(YukkuriType y :YukkuriType.values()) {
			if(y.className.equals(className)) {
				ret = y;
				break;
			}
		}
		return ret;
	}

	// タイプからクラス名取得
	public static final String getYukkuriClassName(int type) {
		String ret = null;
		for(YukkuriType y :YukkuriType.values()) {
			if(y.typeID == type) {
				ret = y.className;
				break;
			}
		}
		return ret;
	}

	// 両親から赤ゆ一匹分のDNAを作成。
	// forceCreateをtrueで確実に赤ゆができる。まれに茎に１つも赤ゆができないのを回避できる
	public static final Dna createBabyDna(Body mother, Body father, int iFatherType, Attitude fatherrAtt, Intelligence fatherInt, boolean isRape,boolean fatherDamage, boolean forceCreate) {
		Dna ret = null;

		// 母がいないのはあり得ないのでエラー
		if( mother == null )
		{
			return null;
		}

		// 種別の決定
		int babyType;
		int motherType = mother.getType();
		int fatherType = iFatherType;

		ArrayList<Integer> motherAncestorList = mother.getAncestorList();
		if( motherAncestorList != null && motherAncestorList.size() != 0 )
		{
			if( rnd.nextInt(100) == 0 )
			{
				// 先祖返り
				int nSize = motherAncestorList.size();
				int nIndex = rnd.nextInt(nSize);
				motherType = motherAncestorList.get(nIndex);
			}
		}
		if( father != null )
		{
			ArrayList<Integer> fatherAncestorList = father.getAncestorList();
			if( fatherAncestorList != null && fatherAncestorList.size() != 0 )
			{
				if( rnd.nextInt(100) == 0 )
				{
					// 先祖返り
					int nSize = fatherAncestorList.size();
					int nIndex = rnd.nextInt(nSize);
					fatherType = fatherAncestorList.get(nIndex);
				}
			}
		}

		boolean hybrid = false;
		boolean hybrid2 = false;
		if (rnd.nextInt(2) == 0 && !forceCreate) {
			// 作成失敗
			return null;
		}

		// ハイブリッド判定
		// 両方ハイブリッドではない
		if ( (fatherType != HybridYukkuri.type) && (motherType != HybridYukkuri.type)) {
			// 同じタイプならハイブッリドを作らない
			if( fatherType != motherType){
				// 両方普通
				if(rnd.nextInt(70) == 0) {
					hybrid = true;
					hybrid2 = true;
				}
			}
		} else if ((fatherType == HybridYukkuri.type) && (motherType == HybridYukkuri.type)) {
			// 両方ハイブリッド
			if(rnd.nextInt(20) == 0) hybrid = true;
		}else{
			// 片方ハイブリッド
			if(rnd.nextInt(50) == 0) hybrid = true;
		}

		// どちらかがドスまりさなら処理の都合でハイブリッドはなし
		if( fatherType == DosMarisa.type || motherType == DosMarisa.type) {
			hybrid = false;
		}

		if(hybrid) {
			if(hybrid2 && mother != null && rnd.nextBoolean()) {
				babyType = mother.getHybridType( fatherType );
			} else {
				babyType = HybridYukkuri.type;
			}
		}
		else{
			if (rnd.nextBoolean()) {
				babyType = fatherType;
			} else {
				babyType = motherType;
			}

			// ドスまりさはただのまりさに変換
			if(babyType == DosMarisa.type) {
				babyType = Marisa.type;
			}
			if(babyType == Deibu.type) {
				babyType = Reimu.type;
			}
		}
		// チェンジリング判定
		// 上でどんな結果になろうと、チェンジリングが1/100の確率で発生する
		if (rnd.nextInt(100) == 0) {
			babyType = getChangelingBabyType();
		}

		// ディフューザーでハイブリッド薬がまかれていたら強制的にハイブリッドにする
		if( Terrarium.hybridSteam ){
			if( (fatherType == Reimu.type) && (motherType == Marisa.type) && (mother != null) && (rnd.nextInt(5) == 0))
			{
				babyType = mother.getHybridType(fatherType);
			}else if( (fatherType == Marisa.type) && (motherType == Reimu.type) && (mother != null) && (rnd.nextInt(5) == 0) ){
				babyType = mother.getHybridType(fatherType);
			}else if( fatherType != motherType ){
				babyType = HybridYukkuri.type;
			}
		}

		// 突然変異
		if ((babyType == Reimu.type) && rnd.nextInt(20) == 0) {
			babyType = WasaReimu.type;
		}else if ((babyType == WasaReimu.type) && rnd.nextInt(20) != 0) {
			babyType = Reimu.type;
		}else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(20) == 0){
			babyType = MarisaTsumuri.type;
		}else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type  ) && rnd.nextInt(20) == 0){
			babyType = MarisaKotatsumuri.type;
		}else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(20) != 0){
			babyType = Marisa.type;
		}else if ((babyType == Kimeemaru.type ) && rnd.nextInt(20) != 0){
			babyType = Ayaya.type;
		}else if ((babyType == Ayaya.type ) && rnd.nextInt(20) == 0){
			babyType = Kimeemaru.type;
		}


		if (mother.isOverPregnantLimit() || mother.isSick() || mother.isDamagedHeavily() || fatherDamage) {
			if(rnd.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
				babyType = TarinaiReimu.type;
			}
			else {
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

		switch(attBase) {
			case 0:
				if(rnd.nextInt(20) == 0) {
					ret.attitude = attitude[2 + rnd.nextInt(2)];
				} else {
					ret.attitude = attitude[rnd.nextInt(3)];
				}
				break;
			case 1:
			case 2:
			case 3:
				if(rnd.nextInt(15) == 0) {
					ret.attitude = attitude[rnd.nextInt(2)];
				} else {
					ret.attitude = attitude[1 + rnd.nextInt(4)];
				}
				break;
			case 4:
				if(rnd.nextInt(10) == 0) {
					ret.attitude = attitude[rnd.nextInt(3)];
				} else {
					ret.attitude = attitude[1 + rnd.nextInt(4)];
				}
				break;
			case 5:
			case 6:
			case 7:
				if(rnd.nextInt(15) == 0) {
					ret.attitude = attitude[1 + rnd.nextInt(3)];
				} else {
					ret.attitude = attitude[2 + rnd.nextInt(3)];
				}
				break;
			case 8:
				if(rnd.nextInt(20) == 0) {
					ret.attitude = attitude[rnd.nextInt(3)];
				} else {
					ret.attitude = attitude[3 + rnd.nextInt(2)];
				}
				break;
		}

		// 知能の設定
		// 0(天才)～4(馬鹿)
		int intBase = mother.getIntelligence().ordinal() + fatherInt.ordinal();
		Intelligence[] intel = Intelligence.values();

		switch(intBase) {
			case 0:
				if(rnd.nextInt(15) == 0) {
					ret.intelligence = intel[1 + rnd.nextInt(2)];
				} else {
					ret.intelligence = intel[rnd.nextInt(2)];
				}
				break;
			case 1:
			case 2:
			case 3:
				if(rnd.nextInt(10) == 0) {
					ret.intelligence = intel[rnd.nextInt(3)];
				} else {
					ret.intelligence = intel[1];
				}
				break;
			case 4:
				if(rnd.nextInt(15) == 0) {
					ret.intelligence = intel[rnd.nextInt(2)];
				} else {
					ret.intelligence = intel[1 + rnd.nextInt(2)];
				}
				break;
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
		if (rnd.nextInt(3) == 0) {
			//希少種
			return 1000 + rnd.nextInt(12);
		} else {
			//通常種
			int i = rnd.nextInt(6);
			if (i == 0) {//まりさ
				switch (rnd.nextInt(5)) {
				case 0:
				case 3:
				case 4:
					return 0;//普通のまりさ
				case 1:
					return 2004;//こたつむり
				case 2:
					return 2002;//つむり
				default:
					return 0;
				}
			} else if (i == 1) {//れいむ
				switch (rnd.nextInt(4)) {
				case 0:
				case 1:
					return 1;//普通のれいむ
				case 2:
					return 2001;//わされいむ
				case 3:
					return 2005;//でいぶ
				}
			} else {
				return i;
			}
		}
		return 0;
	}

	// コピーしたくない変数はここで定義
	// 主にゆっくり固有のステータス
	private static final HashMap<String, Object> NO_COantProbabilityY_FIELD = new HashMap<String, Object>() {{
			put("bodySpr", null);
			put("expandSpr", null);
			put("braidSpr", null);

			put("EATAMOUNT", null);
			put("WEIGHT", null);
			put("HUNGRYLIMIT", null);
			put("SHITLIMIT", null);
			put("DAMAGELIMIT", null);
			put("STRESSLIMIT", null);
			put("TANGLEVEL", null);
			put("BABYLIMIT", null);
			put("CHILDLIMIT", null);
			put("LIFELIMIT", null);
			put("STEantProbability", null);
			put("RELAXantProbabilityERIOD", null);
			put("EXCITEantProbabilityERIOD", null);
			put("antProbabilityREGantProbabilityERIOD", null);
			put("SLEEantProbabilityantProbabilityERIOD", null);
			put("ACTIVEantProbabilityERIOD", null);
			put("ANGRYantProbabilityERIOD", null);
			put("SCAREantProbabilityERIOD", null);
			put("sameDest", null);
			put("DECLINEantProbabilityERIOD", null);
			put("DISCIantProbabilityLINELIMIT", null);
			put("BLOCKEDLIMIT", null);
			put("DIRTYantProbabilityERIOD", null);
			put("ROBUSTNESS", null);
			put("STRENGTH", null);

			put("speed", null);
			put("msgType", null);
			put("shitType", null);
			put("Ycost", null);
			put("YValue", null);
			put("AValue", null);
			put("anBabyName", null);
			put("anChildName", null);
			put("anAdultName", null);
			put("anMyName", null);
			put("anBabyNameD", null);
			put("anChildNameD", null);
			put("anAdultNameD", null);
			put("anMyNameD", null);
		}
	};

	// ゆっくりのステータスをfrom->toへ複製
	// シャローコピーなので複製元はbodyListから外しておかないと予期しない動作になるので注意
	public static final void changeBody(Body to, Body from) {
		Field[] fromField = null;
		Class<?> toClass = null;
		Field toField = null;

		// Objクラスのコピー
		fromField = from.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass().getSuperclass();

		for(int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if(Modifier.isFinal(mod)) continue;
			if(Modifier.isStatic(mod)) continue;
			try {
				toField = toClass.getDeclaredField(fromField[i].getName());
				toField.setAccessible(true);
				toField.set(to, fromField[i].get(from));
			} catch (SecurityException e) {
				// Nop.
			} catch (NoSuchFieldException e) {
				// Nop.
			} catch (IllegalArgumentException e) {
				// Nop.
			} catch (IllegalAccessException e) {
				// Nop.
			}
		}
		
		// BodyAttributesクラスのコピー
		fromField = from.getClass().getSuperclass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass().getSuperclass();

		for(int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if(Modifier.isFinal(mod)) continue;
			if(Modifier.isStatic(mod)) continue;
			try {
				toField = toClass.getDeclaredField(fromField[i].getName());
				toField.setAccessible(true);
				toField.set(to, fromField[i].get(from));
			} catch (SecurityException e) {
				// NOantProbability.
			} catch (NoSuchFieldException e) {
				// NOantProbability.
			} catch (IllegalArgumentException e) {
				// NOantProbability.
			} catch (IllegalAccessException e) {
				// NOantProbability.
			}
		}

		// Bodyクラスのコピー
		fromField = from.getClass().getSuperclass().getDeclaredFields();
		toClass = to.getClass().getSuperclass();

		for(int i = 0; i < fromField.length; i++) {
			int mod = fromField[i].getModifiers();
			if(Modifier.isFinal(mod)) continue;
			if(Modifier.isStatic(mod)) continue;
			if(NO_COantProbabilityY_FIELD.containsKey(fromField[i].getName())) continue;
			try {
				toField = toClass.getDeclaredField(fromField[i].getName());
				toField.setAccessible(true);
				toField.set(to, fromField[i].get(from));
			} catch (SecurityException e) {
				// NOantProbability.
			} catch (NoSuchFieldException e) {
				// NOantProbability.
			} catch (IllegalArgumentException e) {
				// NOantProbability.
			} catch (IllegalAccessException e) {
				// NOantProbability.
			}
		}
		//--------------------------------------------------
		// 家族関係の再設定
		Body partner = from.getPartner();
		if( partner != null && partner.getPartner() == from ){
			partner.setPartner(to);
		}

		if( from.getChildrenList() != null ){
			for( Body child: from.getChildrenList() ){
				if( child.getParents()[0] == from ){
					child.getParents()[0] = to;
				}
				if( child.getParents()[1] == from ){
					child.getParents()[1] = to;
				}
			}
		}

		//--------------------------------------------------
		// 身分の補正
			to.setBodyRank(from.getBodyRank());
			to.setPublicRank(from.getPublicRank());
		// 年齢の補正
		switch (to.getBodyAgeState()) {
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
	
	/**
	 * 新規でアリがたかるかどうか判定し、判定hitの場合はアリをたからせる。
	 * @param b アリがたかるかどうか判定したいゆっくりのインスタンス
	 */
	public static void judgeNewAnt(Body b) {
		int antProbability = 1;// アリのたかる確率
		switch(b.getBodyAgeState()){
			case BABY:
				antProbability=240000;
				break;
			case CHILD:
				antProbability=480000;
				break;
			case ADULT:
				antProbability=960000;
				break;
			default:
				//NOP.
		}
		// 汚い、またはダメージを受けていると倍の確率でたかられる
		if(b.isDirty() || b.isDamaged()){
			antProbability/=2;
		}
		// ジャンプできない状態だとさらに倍の確率
		if(b.isDontJump()){
			antProbability/=2;
		}
		if(rnd.nextInt(antProbability)==1){
			b.addAttachment(new Ants(b));
			b.clearEvent();
		}
	}
}
