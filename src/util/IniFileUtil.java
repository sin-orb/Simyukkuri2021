package src.util;

import src.base.Body;
import src.draw.ModLoader;

/**
 * INIファイルのためのUtility
 */
public class IniFileUtil {
	
	/**
	 * INIファイルを読み込む
	 * @param b ゆっくり
	 */
	public static void readIniFile(Body b){
		ClassLoader loader = b.getClass().getClassLoader();
		int nTemp = 0;

//		public int EATAMOUNT[] = {100*6, 100*12, 100*24};		// 一回の食事量
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.baby");
		if( nTemp != 0) b.getEATAMOUNT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.child");
		if( nTemp != 0) b.getEATAMOUNT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "EATAMOUNT.adult");
		if( nTemp != 0) b.getEATAMOUNT()[2] = nTemp;

//		public int WEIGHT[] = {100, 300, 600};					// 体重
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.baby");
		if( nTemp != 0) b.getWEIGHT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.child");
		if( nTemp != 0) b.getWEIGHT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "WEIGHT.adult");
		if( nTemp != 0) b.getWEIGHT()[2] = nTemp;

//		public int STRENGTH[] = {500, 1000, 3000};					// 基準の攻撃力
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.baby");
		if( nTemp != 0) b.getSTRENGTH()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.child");
		if( nTemp != 0) b.getSTRENGTH()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRENGTH.adult");
		if( nTemp != 0) b.getSTRENGTH()[2] = nTemp;

//		public int HUNGRYLIMIT[] = {100*24, 100*24*2, 100*24*4}; // 空腹限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.baby");
		if( nTemp != 0) b.getHUNGRYLIMIT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.child");
		if( nTemp != 0) b.getHUNGRYLIMIT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "HUNGRYLIMIT.adult");
		if( nTemp != 0) b.getHUNGRYLIMIT()[2] = nTemp;

//		public int SHITLIMIT[] = {100*12, 100*24, 100*24};		// うんうん限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.baby");
		if( nTemp != 0) b.getSHITLIMIT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.child");
		if( nTemp != 0) b.getSHITLIMIT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SHITLIMIT.adult");
		if( nTemp != 0) b.getSHITLIMIT()[2] = nTemp;

//		public int DAMAGELIMIT[] = {100*24, 100*24*3, 100*24*7}; // ダメージ限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.baby");
		if( nTemp != 0) b.getDAMAGELIMIT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.child");
		if( nTemp != 0) b.getDAMAGELIMIT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "DAMAGELIMIT.adult");
		if( nTemp != 0) b.getDAMAGELIMIT()[2] = nTemp;

//		public int STRESSLIMIT[] = {100*24, 100*24*3, 100*24*7}; // ストレス限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.baby");
		if( nTemp != 0) b.getSTRESSLIMIT()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.child");
		if( nTemp != 0) b.getSTRESSLIMIT()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "STRESSLIMIT.adult");
		if( nTemp != 0) b.getSTRESSLIMIT()[2] = nTemp;

//		public int TANGLEVEL[] = {300, 600, 1000};				// 味覚レベル
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.baby");
		if( nTemp != 0) b.getTANGLEVEL()[0] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.child");
		if( nTemp != 0) b.getTANGLEVEL()[1] = nTemp;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "TANGLEVEL.adult");
		if( nTemp != 0) b.getTANGLEVEL()[2] = nTemp;

//		public int BABYLIMIT = 100*24*7;
//		public int CHILDLIMIT = 100*24*21;
//		public int LIFELIMIT = 100*24*365;
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BABYLIMIT");
		if( nTemp != 0) b.setBABYLIMIT(nTemp);
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "CHILDLIMIT");
		if( nTemp != 0) b.setCHILDLIMIT(nTemp);
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "LIFELIMIT");
		if( nTemp != 0) b.setLIFELIMIT(nTemp);

		// 腐敗速度
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ROTTINGTIME");
		if( nTemp != 0) b.setROTTINGTIME(nTemp);

		//赤ゆの免疫力
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BabyImmunity");
		b.getImmunity()[0] = nTemp;
		//子ゆの免疫力
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ChildImmunity");
		b.getImmunity()[1] = nTemp;
		//成ゆの免疫力
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "AdultImmunity");
		b.getImmunity()[2] = nTemp;
		//老ゆの免疫力
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "OldImmunity");
		b.getImmunity()[3] = nTemp;
		//性格変更入り切り
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "ChangeCharacter");
		if(nTemp == 0) b.setNotChangeCharacter(true);
		//超善良限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "VeryNiceLimit");
		b.getNiceLimit()[1] = nTemp+b.RND.nextInt(20)-10;
		//善良限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "NiceLimit");
		b.getNiceLimit()[0] = nTemp+b.RND.nextInt(20)-10;
		//ゲス限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "RudeLimit");
		b.getRudeLimit()[0] = -nTemp+b.RND.nextInt(20)-10;
		//ドゲス限界
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SuperRudeLimit");
		b.getRudeLimit()[1] = -nTemp+b.RND.nextInt(20)-10;

		//リアルな妊娠限界の入り切り
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "RealPregnantLimit");
		if(nTemp == 1) b.setRealPregnantLimit(true);
		else b.setRealPregnantLimit(false);

		// せいっさいっ！時にお飾りが破壊される割合
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BreakBraidRand");
		b.setnBreakBraidRand(nTemp);
		//すりすりで事故る確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "SurisuriAccidentProbablity");
		b.setSurisuriAccidentProb(nTemp);
		//路上で踏み潰される確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "CarAccidentProbablity");
		b.setCarAccidentProb(nTemp);
		//あんよが傷ついていた場合、一定確率であんよが爆ぜる確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "BreakBodyByShitProbability");
		b.setBreakBodyByShitProb(nTemp);
		//発情する確率
		nTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "GetExcitedProbablity");
		if(nTemp!=0)b.setExciteProb(nTemp);
	}
	
	/**
	 * ゆっくり用のINIファイルを読み込む
	 * @param b ゆっくり
	 */
	public static void readYukkuriIniFile(Body b) {
		ClassLoader loader = b.getClass().getClassLoader();
		// 一人称取得
		String[] anStrTemp;
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "BABYNAME");
		if( anStrTemp != null) b.setAnBabyName(anStrTemp);
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "CHILDNAME");
		if( anStrTemp != null) b.setAnChildName(anStrTemp);
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "ADULTNAME");
		if( anStrTemp != null) b.setAnAdultName(anStrTemp);
		int nBeforeNameIndex = -1;

		//一人称設定
		if( b.getAnBabyName() != null && 0 < b.getAnBabyName().length ){
			int nSize = b.getAnBabyName().length;
			int nIndex = b.RND.nextInt(nSize);
			b.getAnMyName()[0] = b.getAnBabyName()[nIndex];
			nBeforeNameIndex = nIndex;
		}
		if( b.getAnChildName() != null && 0 < b.getAnChildName().length ){
			int nSize = b.getAnChildName().length;
			int nIndex = b.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if( nBeforeNameIndex < nSize ){
				nIndex = nBeforeNameIndex;
			}
			nBeforeNameIndex = nIndex;
			b.getAnMyName()[1] = b.getAnChildName()[nIndex];
		}
		if( b.getAnAdultName() != null && 0 < b.getAnAdultName().length ){
			int nSize = b.getAnAdultName().length;
			int nIndex = b.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があれば先のものを優先する
			if( nBeforeNameIndex < nSize ){
				nIndex = nBeforeNameIndex;
			}
			b.getAnMyName()[2] = b.getAnAdultName()[nIndex];
		}

		//ダメージ時一人称取得
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "BABYNAME_DAMAGED");
		if( anStrTemp != null) b.setAnBabyNameD(anStrTemp);
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "CHILDNAME_DAMAGED");
		if( anStrTemp != null) b.setAnChildNameD(anStrTemp);
		anStrTemp = ModLoader.loadBodyIniMapForArrayString(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "ADULTNAME_DAMAGED");
		if( anStrTemp != null) b.setAnAdultNameD(anStrTemp);
		int nBeforeNameDIndex = -1;

		//ダメージ時一人称設定
		if( b.getAnBabyNameD() != null && 0 < b.getAnBabyNameD().length ){
			int nSize = b.getAnBabyNameD().length;
			int nIndex = b.RND.nextInt(nSize);
			b.getAnMyNameD()[0] = b.getAnBabyNameD()[nIndex];
			nBeforeNameDIndex = nIndex;
		}
		if( b.getAnChildNameD() != null && 0 < b.getAnChildNameD().length ){
			int nSize = b.getAnChildNameD().length;
			int nIndex = b.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があればを優先する
			if( nBeforeNameIndex < nSize ){
				nIndex = nBeforeNameDIndex;
			}
			nBeforeNameDIndex = nIndex;
			b.getAnMyNameD()[1] = b.getAnChildNameD()[nIndex];
		}
		if( b.getAnAdultNameD() != null && 0 < b.getAnAdultNameD().length ){
			int nSize = b.getAnAdultNameD().length;
			int nIndex = b.RND.nextInt(nSize);
			// 名前リストで同じ並びの物があればを優先する
			if( nBeforeNameDIndex < nSize ){
				nIndex = nBeforeNameDIndex;
			}
			b.getAnMyNameD()[2] = b.getAnAdultNameD()[nIndex];
		}

		// 値段取得
		int NTemp;
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "cost");
		b.setYcost(NTemp);
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "value");
		b.getSaleValue()[0] = NTemp;
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "contentValue");
		b.getSaleValue()[1] = NTemp;
		//妊娠限界取得
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "pregnantLimit");
		b.setPregnantLimit(NTemp +b.RND.nextInt(100));
		//下痢になる確率取得
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "GetDiarrheaProbability");
		b.setDiarrheaProb(NTemp +b.RND.nextInt(2));
		//お飾りの位置設定
		NTemp = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_INI_DIR, b.getBaseBodyFileName(), "OkazariPosition");
		b.setOkazariPosition(NTemp);
	}
}
