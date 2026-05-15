package org.simyukkuri.util;

import java.util.HashMap;
import java.util.Map;

import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;

/**
 * INIファイルのためのUtility
 */
public class IniFileUtil {
	/** 読んだINIファイルをメモリに展開 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, Map<String, Object>> bodyIniConfigs = new HashMap<>();
	/** 読んだINIファイルをメモリに展開 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, Map<String, Object>> yukkuriIniConfigs = new HashMap<>();

	/**
	 * INIファイルを読み込む
	 * 
	 * @param b     ゆっくり
	 * @param force 強制読み込みフラグ
	 */
	@SuppressWarnings("rawtypes")
	public static void readIniFile(Yukkuri b, boolean force) {
		Class bodyClass = b.getClass();
		if (bodyIniConfigs.containsKey(bodyClass) && !force) {
			// すでに読み込まれている、または強制読み込みなしの場合
			Map<String, Object> conf = bodyIniConfigs.get(bodyClass);
			b.getEatAmountBase()[0] = (int) conf.get("EATAMOUNT.baby");
			b.getEatAmountBase()[1] = (int) conf.get("EATAMOUNT.child");
			b.getEatAmountBase()[2] = (int) conf.get("EATAMOUNT.adult");
			b.getWeightBase()[0] = (int) conf.get("WEIGHT.baby");
			b.getWeightBase()[1] = (int) conf.get("WEIGHT.child");
			b.getWeightBase()[2] = (int) conf.get("WEIGHT.adult");
			b.getStrengthBase()[0] = (int) conf.get("STRENGTH.baby");
			b.getStrengthBase()[1] = (int) conf.get("STRENGTH.child");
			b.getStrengthBase()[2] = (int) conf.get("STRENGTH.adult");
			b.getHungryLimitBase()[0] = (int) conf.get("HUNGRYLIMIT.baby");
			b.getHungryLimitBase()[1] = (int) conf.get("HUNGRYLIMIT.child");
			b.getHungryLimitBase()[2] = (int) conf.get("HUNGRYLIMIT.adult");
			b.getShitLimitBase()[0] = (int) conf.get("SHITLIMIT.baby");
			b.getShitLimitBase()[1] = (int) conf.get("SHITLIMIT.child");
			b.getShitLimitBase()[2] = (int) conf.get("SHITLIMIT.adult");
			b.getDamageLimitBase()[0] = (int) conf.get("DAMAGELIMIT.baby");
			b.getDamageLimitBase()[1] = (int) conf.get("DAMAGELIMIT.child");
			b.getDamageLimitBase()[2] = (int) conf.get("DAMAGELIMIT.adult");
			b.getStressLimitBase()[0] = (int) conf.get("STRESSLIMIT.baby");
			b.getStressLimitBase()[1] = (int) conf.get("STRESSLIMIT.child");
			b.getStressLimitBase()[2] = (int) conf.get("STRESSLIMIT.adult");
			b.getTangLevelBase()[0] = (int) conf.get("TANGLEVEL.baby");
			b.getTangLevelBase()[1] = (int) conf.get("TANGLEVEL.child");
			b.getTangLevelBase()[2] = (int) conf.get("TANGLEVEL.adult");
			b.setBabyLimitBase((int) conf.get("BABYLIMIT"));
			b.setChildLimitBase((int) conf.get("CHILDLIMIT"));
			b.setLifeLimitBase((int) conf.get("LIFELIMIT"));
			b.setRottingTimeBase((int) conf.get("ROTTINGTIME"));
			b.setSurisuriAccidentProb((int) conf.get("SurisuriAccidentProbablity"));
			b.setCarAccidentProb((int) conf.get("CarAccidentProbablity"));
			b.setBreakByShitProb((int) conf.get("BreakBodyByShitProbability"));
			b.setExciteProb((int) conf.get("GetExcitedProbablity"));
			b.getImmunity()[0] = (int) conf.get("Immunity.0");
			b.getImmunity()[1] = (int) conf.get("Immunity.1");
			b.getImmunity()[2] = (int) conf.get("Immunity.2");
			b.getImmunity()[3] = (int) conf.get("Immunity.3");
			b.setNotChangeCharacter((int) conf.get("NotChangeCharacter") == 0);
			b.getNiceLimit()[0] = (int) conf.get("niceLimit") + GameRandom.nextInt(20) - 10;
			b.getNiceLimit()[1] = (int) conf.get("VeryNiceLimit") + GameRandom.nextInt(20) - 10;
			b.getRudeLimit()[0] = (int) conf.get("rudeLimit") + GameRandom.nextInt(20) - 10;
			b.getRudeLimit()[1] = (int) conf.get("VeryRudeLimit") + GameRandom.nextInt(20) - 10;
			b.setUseRealPregnantLimit((int) conf.get("RealPregnantLimit") == 1);
			// 自主洗浄失敗確率
			b.getCleaningFailProbWise()[0] = (int) conf.get("CleaningFailProb.Wise.baby");
			b.getCleaningFailProbWise()[1] = (int) conf.get("CleaningFailProb.Wise.child");
			b.getCleaningFailProbWise()[2] = (int) conf.get("CleaningFailProb.Wise.adult");
			b.getCleaningFailProbAverage()[0] = (int) conf.get("CleaningFailProb.Average.baby");
			b.getCleaningFailProbAverage()[1] = (int) conf.get("CleaningFailProb.Average.child");
			b.getCleaningFailProbAverage()[2] = (int) conf.get("CleaningFailProb.Average.adult");
			b.getCleaningFailProbFool()[0] = (int) conf.get("CleaningFailProb.Fool.baby");
			b.getCleaningFailProbFool()[1] = (int) conf.get("CleaningFailProb.Fool.child");
			b.getCleaningFailProbFool()[2] = (int) conf.get("CleaningFailProb.Fool.adult");
		} else {
			Map<String, Object> conf = new HashMap<String, Object>();
			ClassLoader loader = bodyClass.getClassLoader();
			int iniValue = 0;
			// public int EATAMOUNT[] = {100*6, 100*12, 100*24}; // 一回の食事量
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "EATAMOUNT.baby");
			if (iniValue != 0) {
				b.getEatAmountBase()[0] = iniValue;
			}
			conf.put("EATAMOUNT.baby", b.getEatAmountBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"EATAMOUNT.child");
			if (iniValue != 0) {
				b.getEatAmountBase()[1] = iniValue;
			}
			conf.put("EATAMOUNT.child", b.getEatAmountBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"EATAMOUNT.adult");
			if (iniValue != 0) {
				b.getEatAmountBase()[2] = iniValue;
			}
			conf.put("EATAMOUNT.adult", b.getEatAmountBase()[2]);
			// public int WEIGHT[] = {100, 300, 600}; // 体重
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.baby");
			if (iniValue != 0) {
				b.getWeightBase()[0] = iniValue;
			}
			conf.put("WEIGHT.baby", b.getWeightBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.child");
			if (iniValue != 0) {
				b.getWeightBase()[1] = iniValue;
			}
			conf.put("WEIGHT.child", b.getWeightBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.adult");
			if (iniValue != 0) {
				b.getWeightBase()[2] = iniValue;
			}
			conf.put("WEIGHT.adult", b.getWeightBase()[2]);
			// public int STRENGTH[] = {500, 1000, 3000}; // 基準の攻撃力
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.baby");
			if (iniValue != 0) {
				b.getStrengthBase()[0] = iniValue;
			}
			conf.put("STRENGTH.baby", b.getStrengthBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.child");
			if (iniValue != 0) {
				b.getStrengthBase()[1] = iniValue;
			}
			conf.put("STRENGTH.child", b.getStrengthBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.adult");
			if (iniValue != 0) {
				b.getStrengthBase()[2] = iniValue;
			}
			conf.put("STRENGTH.adult", b.getStrengthBase()[2]);
			// public int HUNGRYLIMIT[] = {100*24, 100*24*2, 100*24*4}; // 空腹限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"HUNGRYLIMIT.baby");
			if (iniValue != 0) {
				b.getHungryLimitBase()[0] = iniValue;
			}
			conf.put("HUNGRYLIMIT.baby", b.getHungryLimitBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"HUNGRYLIMIT.child");
			if (iniValue != 0) {
				b.getHungryLimitBase()[1] = iniValue;
			}
			conf.put("HUNGRYLIMIT.child", b.getHungryLimitBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"HUNGRYLIMIT.adult");
			if (iniValue != 0) {
				b.getHungryLimitBase()[2] = iniValue;
			}
			conf.put("HUNGRYLIMIT.adult", b.getHungryLimitBase()[2]);

			// public int SHITLIMIT[] = {100*12, 100*24, 100*24}; // うんうん限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "SHITLIMIT.baby");
			if (iniValue != 0) {
				b.getShitLimitBase()[0] = iniValue;
			}
			conf.put("SHITLIMIT.baby", b.getShitLimitBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"SHITLIMIT.child");
			if (iniValue != 0) {
				b.getShitLimitBase()[1] = iniValue;
			}
			conf.put("SHITLIMIT.child", b.getShitLimitBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"SHITLIMIT.adult");
			if (iniValue != 0) {
				b.getShitLimitBase()[2] = iniValue;
			}
			conf.put("SHITLIMIT.adult", b.getShitLimitBase()[2]);

			// public int DAMAGELIMIT[] = {100*24, 100*24*3, 100*24*7}; // ダメージ限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"DAMAGELIMIT.baby");
			if (iniValue != 0) {
				b.getDamageLimitBase()[0] = iniValue;
			}
			conf.put("DAMAGELIMIT.baby", b.getDamageLimitBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"DAMAGELIMIT.child");
			if (iniValue != 0) {
				b.getDamageLimitBase()[1] = iniValue;
			}
			conf.put("DAMAGELIMIT.child", b.getDamageLimitBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"DAMAGELIMIT.adult");
			if (iniValue != 0) {
				b.getDamageLimitBase()[2] = iniValue;
			}
			conf.put("DAMAGELIMIT.adult", b.getDamageLimitBase()[2]);

			// public int STRESSLIMIT[] = {100*24, 100*24*3, 100*24*7}; // ストレス限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"STRESSLIMIT.baby");
			if (iniValue != 0) {
				b.getStressLimitBase()[0] = iniValue;
			}
			conf.put("STRESSLIMIT.baby", b.getStressLimitBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"STRESSLIMIT.child");
			if (iniValue != 0) {
				b.getStressLimitBase()[1] = iniValue;
			}
			conf.put("STRESSLIMIT.child", b.getStressLimitBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"STRESSLIMIT.adult");
			if (iniValue != 0) {
				b.getStressLimitBase()[2] = iniValue;
			}
			conf.put("STRESSLIMIT.adult", b.getStressLimitBase()[2]);
			// public int TANGLEVEL[] = {300, 600, 1000}; // 味覚レベル
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "TANGLEVEL.baby");
			if (iniValue != 0) {
				b.getTangLevelBase()[0] = iniValue;
			}
			conf.put("TANGLEVEL.baby", b.getTangLevelBase()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"TANGLEVEL.child");
			if (iniValue != 0) {
				b.getTangLevelBase()[1] = iniValue;
			}
			conf.put("TANGLEVEL.child", b.getTangLevelBase()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"TANGLEVEL.adult");
			if (iniValue != 0) {
				b.getTangLevelBase()[2] = iniValue;
			}
			conf.put("TANGLEVEL.adult", b.getTangLevelBase()[2]);
			// public int BABYLIMIT = 100*24*7;
			// public int CHILDLIMIT = 100*24*21;
			// public int LIFELIMIT = 100*24*365;
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "BABYLIMIT");
			if (iniValue != 0) {
				b.setBabyLimitBase(iniValue);
			}
			conf.put("BABYLIMIT", b.getBabyLimitBase());
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "CHILDLIMIT");
			if (iniValue != 0) {
				b.setChildLimitBase(iniValue);
			}
			conf.put("CHILDLIMIT", b.getChildLimitBase());
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "LIFELIMIT");
			if (iniValue != 0) {
				b.setLifeLimitBase(iniValue);
			}
			conf.put("LIFELIMIT", b.getLifeLimitBase());

			// 腐敗速度
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "ROTTINGTIME");
			if (iniValue != 0) {
				b.setRottingTimeBase(iniValue);
			}
			conf.put("ROTTINGTIME", b.getRottingTimeBase());

			// 赤ゆの免疫力
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "BabyImmunity");
			b.getImmunity()[0] = iniValue;
			conf.put("Immunity.0", iniValue);
			// 子ゆの免疫力
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "ChildImmunity");
			b.getImmunity()[1] = iniValue;
			conf.put("Immunity.1", iniValue);
			// 成ゆの免疫力
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "AdultImmunity");
			b.getImmunity()[2] = iniValue;
			conf.put("Immunity.2", iniValue);
			// 老ゆの免疫力
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "OldImmunity");
			b.getImmunity()[3] = iniValue;
			conf.put("Immunity.3", iniValue);
			// 性格変更入り切り
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"ChangeCharacter");
			if (iniValue == 0) {
				b.setNotChangeCharacter(true);
			}
			conf.put("NotChangeCharacter", iniValue);
			// 超善良限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "VeryNiceLimit");
			b.getNiceLimit()[1] = iniValue + GameRandom.nextInt(20) - 10;
			conf.put("VeryNiceLimit", iniValue);
			// 善良限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "niceLimit");
			b.getNiceLimit()[0] = iniValue + GameRandom.nextInt(20) - 10;
			conf.put("niceLimit", iniValue);
			// ゲス限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "rudeLimit");
			b.getRudeLimit()[0] = -iniValue + GameRandom.nextInt(20) - 10;
			conf.put("rudeLimit", -iniValue);
			// ドゲス限界
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "SuperRudeLimit");
			b.getRudeLimit()[1] = -iniValue + GameRandom.nextInt(20) - 10;
			conf.put("VeryRudeLimit", -iniValue);
			// リアルな妊娠限界の入り切り
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"RealPregnantLimit");
			if (iniValue == 1) {
				b.setUseRealPregnantLimit(true);
			} else {
				b.setUseRealPregnantLimit(false);
			}
			conf.put("RealPregnantLimit", iniValue);
			// せいっさいっ！時にお飾りが破壊される割合
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"BraidBreakChance");
			b.setBraidBreakChance(iniValue);
			conf.put("BraidBreakChance", iniValue);
			// すりすりで事故る確率
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"SurisuriAccidentProbablity");
			b.setSurisuriAccidentProb(iniValue);
			conf.put("SurisuriAccidentProbablity", iniValue);
			// 路上で踏み潰される確率
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CarAccidentProbablity");
			b.setCarAccidentProb(iniValue);
			conf.put("CarAccidentProbablity", iniValue);
			// あんよが傷ついていた場合、一定確率であんよが爆ぜる確率
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"BreakBodyByShitProbability");
			b.setBreakByShitProb(iniValue);
			conf.put("BreakBodyByShitProbability", iniValue);
			// 発情する確率
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"GetExcitedProbablity");
			if (iniValue != 0) {
				b.setExciteProb(iniValue);
			}
			conf.put("GetExcitedProbablity", b.getExciteProb());
			// 自主洗浄失敗確率（賢い）
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.baby");
			if (iniValue != 0) {
				b.getCleaningFailProbWise()[0] = iniValue;
			}
			conf.put("CleaningFailProb.Wise.baby", b.getCleaningFailProbWise()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.child");
			if (iniValue != 0) {
				b.getCleaningFailProbWise()[1] = iniValue;
			}
			conf.put("CleaningFailProb.Wise.child", b.getCleaningFailProbWise()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.adult");
			if (iniValue != 0) {
				b.getCleaningFailProbWise()[2] = iniValue;
			}
			conf.put("CleaningFailProb.Wise.adult", b.getCleaningFailProbWise()[2]);
			// 自主洗浄失敗確率（普通）
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.baby");
			if (iniValue != 0) {
				b.getCleaningFailProbAverage()[0] = iniValue;
			}
			conf.put("CleaningFailProb.Average.baby", b.getCleaningFailProbAverage()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.child");
			if (iniValue != 0) {
				b.getCleaningFailProbAverage()[1] = iniValue;
			}
			conf.put("CleaningFailProb.Average.child", b.getCleaningFailProbAverage()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.adult");
			if (iniValue != 0) {
				b.getCleaningFailProbAverage()[2] = iniValue;
			}
			conf.put("CleaningFailProb.Average.adult", b.getCleaningFailProbAverage()[2]);
			// 自主洗浄失敗確率（餡子脳）
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.baby");
			if (iniValue != 0) {
				b.getCleaningFailProbFool()[0] = iniValue;
			}
			conf.put("CleaningFailProb.Fool.baby", b.getCleaningFailProbFool()[0]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.child");
			if (iniValue != 0) {
				b.getCleaningFailProbFool()[1] = iniValue;
			}
			conf.put("CleaningFailProb.Fool.child", b.getCleaningFailProbFool()[1]);
			iniValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.adult");
			if (iniValue != 0) {
				b.getCleaningFailProbFool()[2] = iniValue;
			}
			conf.put("CleaningFailProb.Fool.adult", b.getCleaningFailProbFool()[2]);
			bodyIniConfigs.put(bodyClass, conf);
		}
	}

	/**
	 * ゆっくり用のINIファイルを読み込む
	 * 
	 * @param b ゆっくり
	 */
	public static void readYukkuriIniFile(Yukkuri b) {
		readYukkuriIniFile(b, false);
	}

	/**
	 * ゆっくり用のINIファイルを読み込む
	 * 
	 * @param b     ゆっくり
	 * @param force 強制読み込みフラグ
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void readYukkuriIniFile(Yukkuri b, boolean force) {
		Class bodyClass = b.getClass();
		if (yukkuriIniConfigs.containsKey(bodyClass) && !force) {
			// すでに読み込まれている、または強制読み込みなしの場合
			Map<String, Object> conf = yukkuriIniConfigs.get(bodyClass);
			b.setBabyNames((String[]) conf.get("BABYNAME"));
			b.setChildNames((String[]) conf.get("CHILDNAME"));
			b.setAdultNames((String[]) conf.get("ADULTNAME"));
			b.setBabyNamesDamaged((String[]) conf.get("BABYNAME_DAMAGED"));
			b.setChildNamesDamaged((String[]) conf.get("CHILDNAME_DAMAGED"));
			b.setAdultNamesDamaged((String[]) conf.get("ADULTNAME_DAMAGED"));
			setFirstPersonName(b);

			b.setCost((int) conf.get("cost"));
			b.setOkazariPosition((int) conf.get("OkazariPosition"));
			b.setSaleValues((int[]) conf.get("saleValue"));
			b.setPregnantLimit((int) conf.get("pregnantLimit") + GameRandom.nextInt(100));
			b.setDiarrheaProb((int) conf.get("GetDiarrheaProbability") + GameRandom.nextInt(2));

		} else {
			Map<String, Object> conf = new HashMap<String, Object>();
			ClassLoader loader = bodyClass.getClassLoader();
			// 一人称取得
			String[] nameParts;
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"BABYNAME");
			if (nameParts != null) {
				b.setBabyNames(nameParts);
			}
			conf.put("BABYNAME", b.getBabyNames());
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"CHILDNAME");
			if (nameParts != null) {
				b.setChildNames(nameParts);
			}
			conf.put("CHILDNAME", b.getChildNames());
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"ADULTNAME");
			if (nameParts != null) {
				b.setAdultNames(nameParts);
			}
			conf.put("ADULTNAME", b.getAdultNames());

			// ダメージ時一人称取得
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"BABYNAME_DAMAGED");
			if (nameParts != null) {
				b.setBabyNamesDamaged(nameParts);
			}
			conf.put("BABYNAME_DAMAGED", b.getBabyNamesDamaged());
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"CHILDNAME_DAMAGED");
			if (nameParts != null) {
				b.setChildNamesDamaged(nameParts);
			}
			conf.put("CHILDNAME_DAMAGED", b.getChildNamesDamaged());
			nameParts = ModLoader.loadYukkuriIniStrings(loader, ModLoader.getDataIniDir(),
					b.getBaseYukkuriFileName(),
					"ADULTNAME_DAMAGED");
			if (nameParts != null) {
				b.setAdultNamesDamaged(nameParts);
			}
			conf.put("ADULTNAME_DAMAGED", b.getAdultNamesDamaged());

			setFirstPersonName(b);
			// 値段取得
			int loadedValue;
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"cost");
			b.setCost(loadedValue);
			conf.put("cost", b.getCost());
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"value");
			b.getSaleValues()[0] = loadedValue;
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"contentValue");
			b.getSaleValues()[1] = loadedValue;
			conf.put("saleValue", b.getSaleValues());

			// 妊娠限界取得
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"pregnantLimit");
			b.setPregnantLimit(loadedValue + GameRandom.nextInt(100));
			conf.put("pregnantLimit", loadedValue);
			// 下痢になる確率取得
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"GetDiarrheaProbability");
			b.setDiarrheaProb(loadedValue + GameRandom.nextInt(2));
			conf.put("GetDiarrheaProbability", loadedValue);
			// お飾りの位置設定
			loadedValue = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataIniDir(), b.getBaseYukkuriFileName(),
					"OkazariPosition");
			b.setOkazariPosition(loadedValue);
			conf.put("OkazariPosition", loadedValue);
			yukkuriIniConfigs.put(bodyClass, conf);
		}
	}

	private static void setFirstPersonName(Yukkuri b) {
		int previousNameIndex = -1;
		// 一人称設定
		if (b.getBabyNames() != null && 0 < b.getBabyNames().length) {
			int nameCount = b.getBabyNames().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			b.getMyNames()[0] = b.getBabyNames()[nameIndex];
			previousNameIndex = nameIndex;
		}
		if (b.getChildNames() != null && 0 < b.getChildNames().length) {
			int nameCount = b.getChildNames().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if (previousNameIndex < nameCount) {
				nameIndex = previousNameIndex;
			}
			previousNameIndex = nameIndex;
			b.getMyNames()[1] = b.getChildNames()[nameIndex];
		}
		if (b.getAdultNames() != null && 0 < b.getAdultNames().length) {
			int nameCount = b.getAdultNames().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if (previousNameIndex < nameCount) {
				nameIndex = previousNameIndex;
			}
			b.getMyNames()[2] = b.getAdultNames()[nameIndex];
		}
		int previousNameDIndex = -1;

		// ダメージ時一人称設定
		if (b.getBabyNamesDamaged() != null && 0 < b.getBabyNamesDamaged().length) {
			int nameCount = b.getBabyNamesDamaged().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			b.getMyNamesDamaged()[0] = b.getBabyNamesDamaged()[nameIndex];
			previousNameDIndex = nameIndex;
		}
		if (b.getChildNamesDamaged() != null && 0 < b.getChildNamesDamaged().length) {
			int nameCount = b.getChildNamesDamaged().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			// 名前リストで同じ並びの物があればを優先する
			if (previousNameDIndex < nameCount) {
				nameIndex = previousNameDIndex;
			}
			previousNameDIndex = nameIndex;
			b.getMyNamesDamaged()[1] = b.getChildNamesDamaged()[nameIndex];
		}
		if (b.getAdultNamesDamaged() != null && 0 < b.getAdultNamesDamaged().length) {
			int nameCount = b.getAdultNamesDamaged().length;
			int nameIndex = GameRandom.nextInt(nameCount);
			// 名前リストで同じ並びの物があればを優先する
			if (previousNameDIndex < nameCount) {
				nameIndex = previousNameDIndex;
			}
			b.getMyNamesDamaged()[2] = b.getAdultNamesDamaged()[nameIndex];
		}
	}
}
