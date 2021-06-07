package src.util;

import java.lang.reflect.Method;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void readIniFile(Body b, boolean force) {
		Class clazz = b.getClass();
		if (configsForIni.containsKey(clazz) && !force) {
			// すでに読み込まれている、または強制読み込みなしの場合
			Map<String, Object> conf = configsForIni.get(b.getClass());
			String[] props1 = { "EATAMOUNT", "WEIGHT", "STRENGTH",
					"HUNGRYLIMIT", "SHITLIMIT", "DAMAGELIMIT", "STRESSLIMIT",
					"TANGLEVEL" };
			try {
				for (String prop : props1) {
					Method method = clazz.getMethod("get" + prop);
					int[] ret = (int[]) method.invoke(b);
					ret[0] = (int) conf.get(prop + ".baby");
					ret[1] = (int) conf.get(prop + ".child");
					ret[2] = (int) conf.get(prop + ".adult");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			b.setBABYLIMIT((int)conf.get("BABYLIMIT"));
			b.setCHILDLIMIT((int)conf.get("CHILDLIMIT"));
			b.setLIFELIMIT((int)conf.get("LIFELIMIT"));
			b.setROTTINGTIME((int)conf.get("ROTTINGTIME"));
			b.setSurisuriAccidentProbablity((int)conf.get("SurisuriAccidentProbablity"));
			b.setCarAccidentProbablity((int)conf.get("CarAccidentProbablity"));
			b.setBreakBodyByShitProbability((int)conf.get("BreakBodyByShitProbability"));
			b.setGetExcitedProbablity((int)conf.get("GetExcitedProbablity"));
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
		} else {
			Map<String, Object> conf = new HashMap<String, Object>();
			ClassLoader loader = clazz.getClassLoader();
			int nTemp = 0;
			//		public int EATAMOUNT[] = {100*6, 100*12, 100*24};		// 一回の食事量
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.baby");
			if (nTemp != 0) {
				b.getEATAMOUNT()[0] = nTemp;
			}
			conf.put("EATAMOUNT.baby", b.getEATAMOUNT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.child");
			if (nTemp != 0) {
				b.getEATAMOUNT()[1] = nTemp;
			}
			conf.put("EATAMOUNT.child", b.getEATAMOUNT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.adult");
			if (nTemp != 0) {
				b.getEATAMOUNT()[2] = nTemp;
			}
			conf.put("EATAMOUNT.adult", b.getEATAMOUNT()[2]);
			//		public int WEIGHT[] = {100, 300, 600};					// 体重
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.baby");
			if (nTemp != 0) {
				b.getWEIGHT()[0] = nTemp;
			}
			conf.put("WEIGHT.baby", b.getWEIGHT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.child");
			if (nTemp != 0) {
				b.getWEIGHT()[1] = nTemp;
			}
			conf.put("WEIGHT.child", b.getWEIGHT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.adult");
			if (nTemp != 0) {
				b.getWEIGHT()[2] = nTemp;
			}
			conf.put("WEIGHT.adult", b.getWEIGHT()[2]);
			//		public int STRENGTH[] = {500, 1000, 3000};					// 基準の攻撃力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.baby");
			if (nTemp != 0) {
				b.getSTRENGTH()[0] = nTemp;
			}
			conf.put("STRENGTH.baby", b.getSTRENGTH()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.child");
			if (nTemp != 0) {
				b.getSTRENGTH()[1] = nTemp;
			}
			conf.put("STRENGTH.child", b.getSTRENGTH()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.adult");
			if (nTemp != 0) {
				b.getSTRENGTH()[2] = nTemp;
			}
			conf.put("STRENGTH.adult", b.getSTRENGTH()[2]);
			//		public int HUNGRYLIMIT[] = {100*24, 100*24*2, 100*24*4}; // 空腹限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.baby");
			if (nTemp != 0) {
				b.getHUNGRYLIMIT()[0] = nTemp;
			}
			conf.put("HUNGRYLIMIT.baby", b.getHUNGRYLIMIT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.child");
			if (nTemp != 0) {
				b.getHUNGRYLIMIT()[1] = nTemp;
			}
			conf.put("HUNGRYLIMIT.child", b.getHUNGRYLIMIT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.adult");
			if (nTemp != 0) {
				b.getHUNGRYLIMIT()[2] = nTemp;
			}
			conf.put("HUNGRYLIMIT.adult", b.getHUNGRYLIMIT()[2]);

			//		public int SHITLIMIT[] = {100*12, 100*24, 100*24};		// うんうん限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.baby");
			if (nTemp != 0) {
				b.getSHITLIMIT()[0] = nTemp;
			}
			conf.put("SHITLIMIT.baby", b.getSHITLIMIT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.child");
			if (nTemp != 0) {
				b.getSHITLIMIT()[1] = nTemp;
			}
			conf.put("SHITLIMIT.child", b.getSHITLIMIT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.adult");
			if (nTemp != 0) {
				b.getSHITLIMIT()[2] = nTemp;
			}
			conf.put("SHITLIMIT.adult", b.getSHITLIMIT()[2]);

			//		public int DAMAGELIMIT[] = {100*24, 100*24*3, 100*24*7}; // ダメージ限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.baby");
			if (nTemp != 0) {
				b.getDAMAGELIMIT()[0] = nTemp;
			}
			conf.put("DAMAGELIMIT.baby", b.getDAMAGELIMIT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.child");
			if (nTemp != 0) {
				b.getDAMAGELIMIT()[1] = nTemp;
			}
			conf.put("DAMAGELIMIT.child", b.getDAMAGELIMIT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.adult");
			if (nTemp != 0) {
				b.getDAMAGELIMIT()[2] = nTemp;
			}
			conf.put("DAMAGELIMIT.adult", b.getDAMAGELIMIT()[2]);

			//		public int STRESSLIMIT[] = {100*24, 100*24*3, 100*24*7}; // ストレス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.baby");
			if (nTemp != 0) {
				b.getSTRESSLIMIT()[0] = nTemp;
			}
			conf.put("STRESSLIMIT.baby", b.getSTRESSLIMIT()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.child");
			if (nTemp != 0) {
				b.getSTRESSLIMIT()[1] = nTemp;
			}
			conf.put("STRESSLIMIT.child", b.getSTRESSLIMIT()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.adult");
			if (nTemp != 0) {
				b.getSTRESSLIMIT()[2] = nTemp;
			}
			conf.put("STRESSLIMIT.adult", b.getSTRESSLIMIT()[2]);
			//		public int TANGLEVEL[] = {300, 600, 1000};				// 味覚レベル
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.baby");
			if (nTemp != 0) {
				b.getTANGLEVEL()[0] = nTemp;
			}
			conf.put("TANGLEVEL.baby", b.getTANGLEVEL()[0]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.child");
			if (nTemp != 0) {
				b.getTANGLEVEL()[1] = nTemp;
			}
			conf.put("TANGLEVEL.child", b.getTANGLEVEL()[1]);
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.adult");
			if (nTemp != 0) {
				b.getTANGLEVEL()[2] = nTemp;
			}
			conf.put("TANGLEVEL.adult", b.getTANGLEVEL()[2]);
			//		public int BABYLIMIT = 100*24*7;
			//		public int CHILDLIMIT = 100*24*21;
			//		public int LIFELIMIT = 100*24*365;
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BABYLIMIT");
			if (nTemp != 0) {
				b.setBABYLIMIT(nTemp);
			}
			conf.put("BABYLIMIT", b.getBABYLIMIT());
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "CHILDLIMIT");
			if (nTemp != 0) {
				b.setCHILDLIMIT(nTemp);
			}
			conf.put("CHILDLIMIT", b.getCHILDLIMIT());
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "LIFELIMIT");
			if (nTemp != 0) {
				b.setLIFELIMIT(nTemp);
			}
			conf.put("LIFELIMIT", b.getLIFELIMIT());

			// 腐敗速度
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ROTTINGTIME");
			if (nTemp != 0) {
				b.setROTTINGTIME(nTemp);
			}
			conf.put("ROTTINGTIME", b.getROTTINGTIME());

			//赤ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BabyImmunity");
			b.getImmunity()[0] = nTemp;
			conf.put("Immunity.0", nTemp);
			//子ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ChildImmunity");
			b.getImmunity()[1] = nTemp;
			conf.put("Immunity.1", nTemp);
			//成ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "AdultImmunity");
			b.getImmunity()[2] = nTemp;
			conf.put("Immunity.2", nTemp);
			//老ゆの免疫力
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "OldImmunity");
			b.getImmunity()[3] = nTemp;
			conf.put("Immunity.3", nTemp);
			//性格変更入り切り
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ChangeCharacter");
			if (nTemp == 0) {
				b.setNotChangeCharacter(true);
			}
			conf.put("NotChangeCharacter", nTemp);
			//超善良限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "VeryNiceLimit");
			b.getNiceLimit()[1] = nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("VeryNiceLimit", nTemp);
			//善良限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "NiceLimit");
			b.getNiceLimit()[0] = nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("NiceLimit", nTemp);
			//ゲス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "RudeLimit");
			b.getRudeLimit()[0] = -nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("RudeLimit", -nTemp);
			//ドゲス限界
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SuperRudeLimit");
			b.getRudeLimit()[1] = -nTemp + SimYukkuri.RND.nextInt(20) - 10;
			conf.put("VeryRudeLimit", -nTemp);
			//リアルな妊娠限界の入り切り
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "RealPregnantLimit");
			if (nTemp == 1) {
				b.setRealPregnantLimit(true);
			} else {
				b.setRealPregnantLimit(false);
			}
			conf.put("RealPregnantLimit", nTemp);
			// せいっさいっ！時にお飾りが破壊される割合
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BreakBraidRand");
			b.setBreakBraidRand(nTemp);
			conf.put("BreakBraidRand", nTemp);
			//すりすりで事故る確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
					"SurisuriAccidentProbablity");
			b.setSurisuriAccidentProbablity(nTemp);
			conf.put("SurisuriAccidentProbablity", nTemp);
			//路上で踏み潰される確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
					"CarAccidentProbablity");
			b.setCarAccidentProbablity(nTemp);
			conf.put("CarAccidentProbablity", nTemp);
			//あんよが傷ついていた場合、一定確率であんよが爆ぜる確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
					"BreakBodyByShitProbability");
			b.setBreakBodyByShitProbability(nTemp);
			conf.put("BreakBodyByShitProbability", nTemp);
			//発情する確率
			nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
					"GetExcitedProbablity");
			if (nTemp != 0) {
				b.setGetExcitedProbablity(nTemp);
			}
			conf.put("GetExcitedProbablity", b.getExciteProb());
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
			b.setBABYNAME((String[]) conf.get("BABYNAME"));
			b.setCHILDNAME((String[]) conf.get("CHILDNAME"));
			b.setADULTNAME((String[]) conf.get("ADULTNAME"));
			b.setBABYNAME_DAMAGED((String[]) conf.get("BABYNAME_DAMAGED"));
			b.setCHILDNAME_DAMAGED((String[]) conf.get("CHILDNAME_DAMAGED"));
			b.setADULTNAME_DAMAGED((String[]) conf.get("ADULTNAME_DAMAGED"));
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
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"BABYNAME");
			if (anStrTemp != null) {
				b.setBABYNAME(anStrTemp);
			}
			conf.put("BABYNAME", b.getAnBabyName());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"CHILDNAME");
			if (anStrTemp != null) {
				b.setCHILDNAME(anStrTemp);
			}
			conf.put("CHILDNAME", b.getAnChildName());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"ADULTNAME");
			if (anStrTemp != null) {
				b.setADULTNAME(anStrTemp);
			}
			conf.put("ADULTNAME", b.getAnAdultName());
			
			//ダメージ時一人称取得
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"BABYNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setBABYNAME_DAMAGED(anStrTemp);
			}
			conf.put("BABYNAME_DAMAGED", b.getAnBabyNameD());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"CHILDNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setCHILDNAME_DAMAGED(anStrTemp);
			}
			conf.put("CHILDNAME_DAMAGED", b.getAnChildNameD());
			anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"ADULTNAME_DAMAGED");
			if (anStrTemp != null) {
				b.setADULTNAME_DAMAGED(anStrTemp);
			}
			conf.put("ADULTNAME_DAMAGED", b.getAnAdultNameD());

			setFirstPersonName(b);
			// 値段取得
			int NTemp;
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "cost");
			b.setcost(NTemp);
			conf.put("cost", b.getYcost());
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "value");
			b.getSaleValue()[0] = NTemp;
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"contentValue");
			b.getSaleValue()[1] = NTemp;
			conf.put("saleValue", b.getSaleValue());
			
			//妊娠限界取得
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"pregnantLimit");
			b.setPregnantLimit(NTemp + SimYukkuri.RND.nextInt(100));
			conf.put("pregnantLimit", NTemp);
			//下痢になる確率取得
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
					"GetDiarrheaProbability");
			b.setDiarrheaProb(NTemp + SimYukkuri.RND.nextInt(2));
			conf.put("GetDiarrheaProbability", NTemp);
			//お飾りの位置設定
			NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(),
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
