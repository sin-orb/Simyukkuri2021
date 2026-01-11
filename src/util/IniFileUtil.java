package src.util;

import java.util.HashMap;
import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.draw.ModLoader;

/**
 * INIファイルのためのUtility
 */
public class IniFileUtil {
	/** 読んだINIファイルをメモリに展開 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, Map<String, Object>> configsForIni = new HashMap<>();
	/** 読んだINIファイルをメモリに展開 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, Map<String, Object>> configsForYukkuriIni = new HashMap<>();

	/**
	 * INIファイルを読み込む
	 * @param b ゆっくり
	 * @param force 強制読み込みフラグ
	 */
	@SuppressWarnings("rawtypes")
	public static void readIniFile(Body b, boolean force) {
		Class clazz = b.getClass();
		if (configsForIni.containsKey(clazz) && !force) {
			// すでに読み込まれている、または強制読み込みなしの場合
			Map<String, Object> conf = configsForIni.get(b.getClass());
			b.getEATAMOUNTorg()[0] = (int) conf.get("EATAMOUNT.baby");
			b.getEATAMOUNTorg()[1] = (int) conf.get("EATAMOUNT.child");
			b.getEATAMOUNTorg()[2] = (int) conf.get("EATAMOUNT.adult");
			b.getWEIGHTorg()[0] = (int) conf.get("WEIGHT.baby");
			b.getWEIGHTorg()[1] = (int) conf.get("WEIGHT.child");
			b.getWEIGHTorg()[2] = (int) conf.get("WEIGHT.adult");
			b.getSTRENGTHorg()[0] = (int) conf.get("STRENGTH.baby");
			b.getSTRENGTHorg()[1] = (int) conf.get("STRENGTH.child");
			b.getSTRENGTHorg()[2] = (int) conf.get("STRENGTH.adult");
			b.getHUNGRYLIMITorg()[0] = (int) conf.get("HUNGRYLIMIT.baby");
			b.getHUNGRYLIMITorg()[1] = (int) conf.get("HUNGRYLIMIT.child");
			b.getHUNGRYLIMITorg()[2] = (int) conf.get("HUNGRYLIMIT.adult");
			b.getSHITLIMITorg()[0] = (int) conf.get("SHITLIMIT.baby");
			b.getSHITLIMITorg()[1] = (int) conf.get("SHITLIMIT.child");
			b.getSHITLIMITorg()[2] = (int) conf.get("SHITLIMIT.adult");
			b.getDAMAGELIMITorg()[0] = (int) conf.get("DAMAGELIMIT.baby");
			b.getDAMAGELIMITorg()[1] = (int) conf.get("DAMAGELIMIT.child");
			b.getDAMAGELIMITorg()[2] = (int) conf.get("DAMAGELIMIT.adult");
			b.getSTRESSLIMITorg()[0] = (int) conf.get("STRESSLIMIT.baby");
			b.getSTRESSLIMITorg()[1] = (int) conf.get("STRESSLIMIT.child");
			b.getSTRESSLIMITorg()[2] = (int) conf.get("STRESSLIMIT.adult");
			b.getTANGLEVELorg()[0] = (int) conf.get("TANGLEVEL.baby");
			b.getTANGLEVELorg()[1] = (int) conf.get("TANGLEVEL.child");
			b.getTANGLEVELorg()[2] = (int) conf.get("TANGLEVEL.adult");
			b.setBABYLIMITorg((int)conf.get("BABYLIMIT"));
			b.setCHILDLIMITorg((int)conf.get("CHILDLIMIT"));
			b.setLIFELIMITorg((int)conf.get("LIFELIMIT"));
			b.setROTTINGTIMEorg((int)conf.get("ROTTINGTIME"));
			b.setSurisuriAccidentProb((int)conf.get("SurisuriAccidentProbablity"));
			b.setCarAccidentProb((int)conf.get("CarAccidentProbablity"));
			b.setBreakBodyByShitProb((int)conf.get("BreakBodyByShitProbability"));
			b.setExciteProb((int)conf.get("GetExcitedProbablity"));
			b.getImmunity()[0] = (int) conf.get("Immunity.0");
			b.getImmunity()[1] = (int) conf.get("Immunity.1");
			b.getImmunity()[2] = (int) conf.get("Immunity.2");
			b.getImmunity()[3] = (int) conf.get("Immunity.3");
			b.setNotChangeCharacter((int) conf.get("NotChangeCharacter") == 0);
			b.getNiceLimit()[0] = (int) conf.get("NiceLimit") + SimYukkuri.RND.nextInt(20) - 10;
			b.getNiceLimit()[1] = (int) conf.get("VeryNiceLimit") + SimYukkuri.RND.nextInt(20) - 10;
			b.getRudeLimit()[0] = (int) conf.get("RudeLimit") + SimYukkuri.RND.nextInt(20) - 10;
			b.getRudeLimit()[1] = (int) conf.get("VeryRudeLimit") + SimYukkuri.RND.nextInt(20) - 10;
			b.setRealPregnantLimit((int) conf.get("RealPregnantLimit") == 1);
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
			ClassLoader loader = clazz.getClassLoader();
			int nTemp = 0;
			//		public int EATAMOUNT[] = {100*6, 100*12, 100*24};		// 一回の食事量
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "EATAMOUNT.baby");
			if (nTemp != 0) {
				b.getEATAMOUNTorg()[0] = nTemp;
			}
			conf.put("EATAMOUNT.baby", b.getEATAMOUNTorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "EATAMOUNT.child");
			if (nTemp != 0) {
				b.getEATAMOUNTorg()[1] = nTemp;
			}
			conf.put("EATAMOUNT.child", b.getEATAMOUNTorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "EATAMOUNT.adult");
			if (nTemp != 0) {
				b.getEATAMOUNTorg()[2] = nTemp;
			}
			conf.put("EATAMOUNT.adult", b.getEATAMOUNTorg()[2]);
			//		public int WEIGHT[] = {100, 300, 600};					// 体重
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.baby");
			if (nTemp != 0) {
				b.getWEIGHTorg()[0] = nTemp;
			}
			conf.put("WEIGHT.baby", b.getWEIGHTorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.child");
			if (nTemp != 0) {
				b.getWEIGHTorg()[1] = nTemp;
			}
			conf.put("WEIGHT.child", b.getWEIGHTorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "WEIGHT.adult");
			if (nTemp != 0) {
				b.getWEIGHTorg()[2] = nTemp;
			}
			conf.put("WEIGHT.adult", b.getWEIGHTorg()[2]);
			//		public int STRENGTH[] = {500, 1000, 3000};					// 基準の攻撃力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.baby");
			if (nTemp != 0) {
				b.getSTRENGTHorg()[0] = nTemp;
			}
			conf.put("STRENGTH.baby", b.getSTRENGTHorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.child");
			if (nTemp != 0) {
				b.getSTRENGTHorg()[1] = nTemp;
			}
			conf.put("STRENGTH.child", b.getSTRENGTHorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRENGTH.adult");
			if (nTemp != 0) {
				b.getSTRENGTHorg()[2] = nTemp;
			}
			conf.put("STRENGTH.adult", b.getSTRENGTHorg()[2]);
			//		public int HUNGRYLIMIT[] = {100*24, 100*24*2, 100*24*4}; // 空腹限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "HUNGRYLIMIT.baby");
			if (nTemp != 0) {
				b.getHUNGRYLIMITorg()[0] = nTemp;
			}
			conf.put("HUNGRYLIMIT.baby", b.getHUNGRYLIMITorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "HUNGRYLIMIT.child");
			if (nTemp != 0) {
				b.getHUNGRYLIMITorg()[1] = nTemp;
			}
			conf.put("HUNGRYLIMIT.child", b.getHUNGRYLIMITorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "HUNGRYLIMIT.adult");
			if (nTemp != 0) {
				b.getHUNGRYLIMITorg()[2] = nTemp;
			}
			conf.put("HUNGRYLIMIT.adult", b.getHUNGRYLIMITorg()[2]);

			//		public int SHITLIMIT[] = {100*12, 100*24, 100*24};		// うんうん限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "SHITLIMIT.baby");
			if (nTemp != 0) {
				b.getSHITLIMITorg()[0] = nTemp;
			}
			conf.put("SHITLIMIT.baby", b.getSHITLIMITorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "SHITLIMIT.child");
			if (nTemp != 0) {
				b.getSHITLIMITorg()[1] = nTemp;
			}
			conf.put("SHITLIMIT.child", b.getSHITLIMITorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "SHITLIMIT.adult");
			if (nTemp != 0) {
				b.getSHITLIMITorg()[2] = nTemp;
			}
			conf.put("SHITLIMIT.adult", b.getSHITLIMITorg()[2]);

			//		public int DAMAGELIMIT[] = {100*24, 100*24*3, 100*24*7}; // ダメージ限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "DAMAGELIMIT.baby");
			if (nTemp != 0) {
				b.getDAMAGELIMITorg()[0] = nTemp;
			}
			conf.put("DAMAGELIMIT.baby", b.getDAMAGELIMITorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "DAMAGELIMIT.child");
			if (nTemp != 0) {
				b.getDAMAGELIMITorg()[1] = nTemp;
			}
			conf.put("DAMAGELIMIT.child", b.getDAMAGELIMITorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "DAMAGELIMIT.adult");
			if (nTemp != 0) {
				b.getDAMAGELIMITorg()[2] = nTemp;
			}
			conf.put("DAMAGELIMIT.adult", b.getDAMAGELIMITorg()[2]);

			//		public int STRESSLIMIT[] = {100*24, 100*24*3, 100*24*7}; // ストレス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRESSLIMIT.baby");
			if (nTemp != 0) {
				b.getSTRESSLIMITorg()[0] = nTemp;
			}
			conf.put("STRESSLIMIT.baby", b.getSTRESSLIMITorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRESSLIMIT.child");
			if (nTemp != 0) {
				b.getSTRESSLIMITorg()[1] = nTemp;
			}
			conf.put("STRESSLIMIT.child", b.getSTRESSLIMITorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "STRESSLIMIT.adult");
			if (nTemp != 0) {
				b.getSTRESSLIMITorg()[2] = nTemp;
			}
			conf.put("STRESSLIMIT.adult", b.getSTRESSLIMITorg()[2]);
			//		public int TANGLEVEL[] = {300, 600, 1000};				// 味覚レベル
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "TANGLEVEL.baby");
			if (nTemp != 0) {
				b.getTANGLEVELorg()[0] = nTemp;
			}
			conf.put("TANGLEVEL.baby", b.getTANGLEVELorg()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "TANGLEVEL.child");
			if (nTemp != 0) {
				b.getTANGLEVELorg()[1] = nTemp;
			}
			conf.put("TANGLEVEL.child", b.getTANGLEVELorg()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "TANGLEVEL.adult");
			if (nTemp != 0) {
				b.getTANGLEVELorg()[2] = nTemp;
			}
			conf.put("TANGLEVEL.adult", b.getTANGLEVELorg()[2]);
			//		public int BABYLIMIT = 100*24*7;
			//		public int CHILDLIMIT = 100*24*21;
			//		public int LIFELIMIT = 100*24*365;
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "BABYLIMIT");
			if (nTemp != 0) {
				b.setBABYLIMITorg(nTemp);
			}
			conf.put("BABYLIMIT", b.getBABYLIMITorg());
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "CHILDLIMIT");
			if (nTemp != 0) {
				b.setCHILDLIMITorg(nTemp);
			}
			conf.put("CHILDLIMIT", b.getCHILDLIMITorg());
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "LIFELIMIT");
			if (nTemp != 0) {
				b.setLIFELIMITorg(nTemp);
			}
			conf.put("LIFELIMIT", b.getLIFELIMITorg());

			// 腐敗速度
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "ROTTINGTIME");
			if (nTemp != 0) {
				b.setROTTINGTIMEorg(nTemp);
			}
			conf.put("ROTTINGTIME", b.getROTTINGTIMEorg());

			//赤ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "BabyImmunity");
			b.getImmunity()[0] = nTemp;
			conf.put("Immunity.0", nTemp);
			//子ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "ChildImmunity");
			b.getImmunity()[1] = nTemp;
			conf.put("Immunity.1", nTemp);
			//成ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "AdultImmunity");
			b.getImmunity()[2] = nTemp;
			conf.put("Immunity.2", nTemp);
			//老ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "OldImmunity");
			b.getImmunity()[3] = nTemp;
			conf.put("Immunity.3", nTemp);
			//性格変更入り切り
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "ChangeCharacter");
			if (nTemp == 0) {
				b.setNotChangeCharacter(true);
			}
			conf.put("NotChangeCharacter", nTemp);
			//超善良限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "VeryNiceLimit");
			b.getNiceLimit()[1] = nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("VeryNiceLimit", nTemp);
			//善良限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "NiceLimit");
			b.getNiceLimit()[0] = nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("NiceLimit", nTemp);
			//ゲス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "RudeLimit");
			b.getRudeLimit()[0] = -nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("RudeLimit", -nTemp);
			//ドゲス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "SuperRudeLimit");
			b.getRudeLimit()[1] = -nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("VeryRudeLimit", -nTemp);
			//リアルな妊娠限界の入り切り
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "RealPregnantLimit");
			if (nTemp == 1) {
				b.setRealPregnantLimit(true);
			} else {
				b.setRealPregnantLimit(false);
			}
			conf.put("RealPregnantLimit", nTemp);
			// せいっさいっ！時にお飾りが破壊される割合
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play", "BreakBraidRand");
			b.setnBreakBraidRand(nTemp);
			conf.put("BreakBraidRand", nTemp);
			//すりすりで事故る確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"SurisuriAccidentProbablity");
			b.setSurisuriAccidentProb(nTemp);
			conf.put("SurisuriAccidentProbablity", nTemp);
			//路上で踏み潰される確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CarAccidentProbablity");
			b.setCarAccidentProb(nTemp);
			conf.put("CarAccidentProbablity", nTemp);
			//あんよが傷ついていた場合、一定確率であんよが爆ぜる確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"BreakBodyByShitProbability");
			b.setBreakBodyByShitProb(nTemp);
			conf.put("BreakBodyByShitProbability", nTemp);
			//発情する確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"GetExcitedProbablity");
			if (nTemp != 0) {
				b.setExciteProb(nTemp);
			}
			conf.put("GetExcitedProbablity", b.getExciteProb());
			// 自主洗浄失敗確率（賢い）
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.baby");
			if (nTemp != 0) {
				b.getCleaningFailProbWise()[0] = nTemp;
			}
			conf.put("CleaningFailProb.Wise.baby", b.getCleaningFailProbWise()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.child");
			if (nTemp != 0) {
				b.getCleaningFailProbWise()[1] = nTemp;
			}
			conf.put("CleaningFailProb.Wise.child", b.getCleaningFailProbWise()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Wise.adult");
			if (nTemp != 0) {
				b.getCleaningFailProbWise()[2] = nTemp;
			}
			conf.put("CleaningFailProb.Wise.adult", b.getCleaningFailProbWise()[2]);
			// 自主洗浄失敗確率（普通）
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.baby");
			if (nTemp != 0) {
				b.getCleaningFailProbAverage()[0] = nTemp;
			}
			conf.put("CleaningFailProb.Average.baby", b.getCleaningFailProbAverage()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.child");
			if (nTemp != 0) {
				b.getCleaningFailProbAverage()[1] = nTemp;
			}
			conf.put("CleaningFailProb.Average.child", b.getCleaningFailProbAverage()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Average.adult");
			if (nTemp != 0) {
				b.getCleaningFailProbAverage()[2] = nTemp;
			}
			conf.put("CleaningFailProb.Average.adult", b.getCleaningFailProbAverage()[2]);
			// 自主洗浄失敗確率（餡子脳）
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.baby");
			if (nTemp != 0) {
				b.getCleaningFailProbFool()[0] = nTemp;
			}
			conf.put("CleaningFailProb.Fool.baby", b.getCleaningFailProbFool()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.child");
			if (nTemp != 0) {
				b.getCleaningFailProbFool()[1] = nTemp;
			}
			conf.put("CleaningFailProb.Fool.child", b.getCleaningFailProbFool()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataWorldIniDir(), "play",
					"CleaningFailProb.Fool.adult");
			if (nTemp != 0) {
				b.getCleaningFailProbFool()[2] = nTemp;
			}
			conf.put("CleaningFailProb.Fool.adult", b.getCleaningFailProbFool()[2]);
			configsForIni.put(clazz, conf);
		}
	}

	/**
	 * ゆっくり用のINIファイルを読み込む
	 * @param b ゆっくり
	 */
	public static void readYukkuriIniFile(Body b) {
		readYukkuriIniFile(b, false);
	}

	/**
	 * ゆっくり用のINIファイルを読み込む
	 * @param b ゆっくり
	 * @param force 強制読み込みフラグ
	 */
	@SuppressWarnings({"rawtypes"})
	public static void readYukkuriIniFile(Body b, boolean force) {
		Class clazz = b.getClass();
		if (configsForYukkuriIni.containsKey(clazz) && !force) {
			// すでに読み込まれている、または強制読み込みなしの場合
			Map<String, Object> conf = configsForYukkuriIni.get(b.getClass());
			b.setAnBabyName((String[]) conf.get("BABYNAME"));
			b.setAnChildName((String[]) conf.get("CHILDNAME"));
			b.setAnAdultName((String[]) conf.get("ADULTNAME"));
			b.setAnBabyNameD((String[]) conf.get("BABYNAME_DAMAGED"));
			b.setAnChildNameD((String[]) conf.get("CHILDNAME_DAMAGED"));
			b.setAnAdultNameD((String[]) conf.get("ADULTNAME_DAMAGED"));
			setFirstPersonName(b);
			
			b.setcost((int)conf.get("cost"));
			b.setOkazariPosition((int)conf.get("OkazariPosition"));
			b.setSaleValue((int[])conf.get("saleValue"));
			b.setPregnantLimit((int)conf.get("pregnantLimit")+ SimYukkuri.RND.nextInt(100));
			b.setDiarrheaProb((int)conf.get("GetDiarrheaProbability")+ SimYukkuri.RND.nextInt(2));
			
		} else {
			Map<String, Object> conf = new HashMap<String, Object>();
			ClassLoader loader = clazz.getClassLoader();
			// 一人称取得
			String[] anStrTemp;
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"BABYNAME");
			if (anStrTemp != null) {
				b.setAnBabyName(anStrTemp);
			}
			conf.put("BABYNAME", b.getAnBabyName());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"CHILDNAME");
			if (anStrTemp != null) {
				b.setAnChildName(anStrTemp);
			}
			conf.put("CHILDNAME", b.getAnChildName());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"ADULTNAME");
			if (anStrTemp != null) {
				b.setAnAdultName(anStrTemp);
			}
			conf.put("ADULTNAME", b.getAnAdultName());
			
			//ダメージ時一人称取得
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"BABYNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setAnBabyNameD(anStrTemp);
			}
			conf.put("BABYNAME_DAMAGED", b.getAnBabyNameD());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"CHILDNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setAnChildNameD(anStrTemp);
			}
			conf.put("CHILDNAME_DAMAGED", b.getAnChildNameD());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"ADULTNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setAnAdultNameD(anStrTemp);
			}
			conf.put("ADULTNAME_DAMAGED", b.getAnAdultNameD());

			setFirstPersonName(b);
			// 値段取得
			int NTemp;
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(), "cost");
			b.setcost(NTemp);
			conf.put("cost", b.getYcost());
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(), "value");
			b.getSaleValue()[0] = NTemp;
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"contentValue");
			b.getSaleValue()[1] = NTemp;
			conf.put("saleValue", b.getSaleValue());
			
			//妊娠限界取得
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"pregnantLimit");
			b.setPregnantLimit(NTemp + SimYukkuri.RND.nextInt(100));
			conf.put("pregnantLimit", NTemp);
			//下痢になる確率取得
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"GetDiarrheaProbability");
			b.setDiarrheaProb(NTemp + SimYukkuri.RND.nextInt(2));
			conf.put("GetDiarrheaProbability", NTemp);
			//お飾りの位置設定
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.getDataIniDir(), b.getBaseBodyFileName(),
					"OkazariPosition");
			b.setOkazariPosition(NTemp);
			conf.put("OkazariPosition", NTemp);
			configsForYukkuriIni.put(clazz, conf);
		}
	}
	
	private static void setFirstPersonName(Body b) {
		int nBeforeNameIndex = -1;
		//一人称設定
		if (b.getAnBabyName() != null && 0 < b.getAnBabyName().length) {
			int nSize = b.getAnBabyName().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			b.getAnMyName()[0] = b.getAnBabyName()[nIndex];
			nBeforeNameIndex = nIndex;
		}
		if (b.getAnChildName() != null && 0 < b.getAnChildName().length) {
			int nSize = b.getAnChildName().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if (nBeforeNameIndex < nSize) {
				nIndex = nBeforeNameIndex;
			}
			nBeforeNameIndex = nIndex;
			b.getAnMyName()[1] = b.getAnChildName()[nIndex];
		}
		if (b.getAnAdultName() != null && 0 < b.getAnAdultName().length) {
			int nSize = b.getAnAdultName().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if (nBeforeNameIndex < nSize) {
				nIndex = nBeforeNameIndex;
			}
			b.getAnMyName()[2] = b.getAnAdultName()[nIndex];
		}
		int nBeforeNameDIndex = -1;

		//ダメージ時一人称設定
		if (b.getAnBabyNameD() != null && 0 < b.getAnBabyNameD().length) {
			int nSize = b.getAnBabyNameD().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			b.getAnMyNameD()[0] = b.getAnBabyNameD()[nIndex];
			nBeforeNameDIndex = nIndex;
		}
		if (b.getAnChildNameD() != null && 0 < b.getAnChildNameD().length) {
			int nSize = b.getAnChildNameD().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があればを優先する
			if (nBeforeNameIndex < nSize) {
				nIndex = nBeforeNameDIndex;
			}
			nBeforeNameDIndex = nIndex;
			b.getAnMyNameD()[1] = b.getAnChildNameD()[nIndex];
		}
		if (b.getAnAdultNameD() != null && 0 < b.getAnAdultNameD().length) {
			int nSize = b.getAnAdultNameD().length;
			int nIndex = SimYukkuri.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があればを優先する
			if (nBeforeNameDIndex < nSize) {
				nIndex = nBeforeNameDIndex;
			}
			b.getAnMyNameD()[2] = b.getAnAdultNameD()[nIndex];
		}
	}
}
