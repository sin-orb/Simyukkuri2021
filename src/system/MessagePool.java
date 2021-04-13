package src.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import src.base.Body;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.CoreAnkoState;
import src.enums.FootBake;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.PublicRank;
import src.enums.YukkuriType;

/*****************************************************
	全キャラのメッセージ管理
*/

public class MessagePool {
	// アクション名定義
	public enum Action {
		/** 餌発見 */
		WantFood,
		/** 餌なし */
		NoFood,
		/** 餌を子に上げる */
		GiveFood,
		/** おちびちゃんほしいよ、、、 */
		WantPartner,
		/** 発情 */
		Excite,
		/** レイパーが発情 */
		ExciteForRaper,
		/** リラックス */
		Relax,
		/** おくるみをはいてリラックス */
		RelaxOkurumi,
		/** キリッ！！ */
		BeVain,
		/** 尻振り */
		FuriFuri,
		/** ゆっくりしていってね！ */
		TakeItEasy,
		/** 起床 */
		Wakeup,
		/** 睡眠中 */
		Sleep,
		/** うなされ */
		Nightmare,
		/** 悲鳴 */
		Scream,
		/** 悲鳴2 */
		Scream2,
		/** 怯え */
		Scare,
		/** 警戒 */
		Alarm,
		/** 瀕死 */
		Dying,
		/** 汎用、瀕死継続 */
		Dying2,
		/** 死亡 */
		Dead,
		/** 食事 */
		Eating,
		/** 食事(不味い) */
		EatingBadtasting,
		/** 食事(毒) */
		EatingBitter,
		/** 食事(嘔吐) */
		SpitFood,
		/** 食事(うんうん) */
		EatingShit,
		/** 満腹 */
		Full,
		/** 餌運び */
		TransportFood,
		/** うんうん運び */
		TransportShit,
		/** アイテムをおろす */
		DropItem,
		/** 回復 */
		Healing,
		/** 清掃 */
		Cleaned,
		/** 自主清掃 */
		CleanItself,
		/** 解放感 */
		ToFreedom,
		/** すっきり */
		Sukkiri,
		/** レイパーがすっきり */
		SukkiriForRaper,
		/** レイパーとすっきり */
		RaperSukkiri,
		/** 養殖プールで繁殖 */
		PoolSukkiri,
		//PoolSukkiriStalk 		//茎式養殖プールで繁殖
		/** 誕生 */
		Birth,
		/** 排泄 */
		Shit,
		/** 排泄2 */
		Shit2,

		/** すりすり */
		SuriSuri,
		/** ぺろぺろ */
		PeroPero,

		/** 出産 */
		Breed,
		/** 産後 */
		Breed2,
		/** うんうん発見 */
		HateShit,
		/** うんうん威嚇 */
		ShitIntimidation,
		//		HateToilet,			//トイレに入ってしまった時
		/** 空腹 */
		Hungry,
		/** おかざり没収 */
		NoAccessory,
		/** おかざりがなくなっていることに気がつく */
		NoticeNoAccessory,
		/** お飾りをこっそり奪う */
		GetOtherAccessoryStealthily,
		/** 持ち上げ(おしょら！！) */
		Flying,
		/** 捨てられると思ったとき */
		DontThrowMeAway,
		/** 子供死亡 */
		SadnessForChild,
		/** 親死亡 */
		SadnessForParent,
		/** つがい死亡 */
		SadnessForPartner,
		/** 姉死亡 */
		SadnessForEldersister,
		/** 妹死亡 */
		SadnessForSister,
		/** 壁発見 */
		BlockedByWall,
		/** おもちゃ発見 */
		GetTreasure,
		/** おもちゃ紛失 */
		LostTreasure,
		//		PlayTreasure,		// おもちゃ遊び-捕食種のお遊びイベントでも使用
		//		ProudTreasure,		//おもちゃ自慢
		/** 膨張 */
		Inflation,
		/** 子虐待 */
		AbuseBaby,
		/** 子虐待死 */
		AbuseBabyKilled,
		/** のびのび */
		Nobinobi,
		/** 出産前 */
		NearToBirth,
		/** 毒 */
		PoisonDamage,
		/** 排泄不可 */
		CantShit,
		/** 妊娠失敗 */
		NoPregnancy,
		/** ぺにぺに切断 */
		PenipeniCutting, 
		/** ぺにぺに切断後 */
		PenipeniCutted, 
		/** すっきりしたくない */
		CantUsePenipeni,
		/** 恐怖でパニック状態 */
		Fear,
		/** 火がついてる状態 */
		Burning,
		/** 水をかけられた */
		Wet,
		/** 浅瀬にいる */
		WetInShallowWater,
		/** 深瀬にいる */
		WetInDeepwWater,
		/** あにゃる閉鎖 */
		AnalSealed,
		/** 針が刺さった時 */
		NeedleStick,
		/** 針が刺さっている時 */
		NeedleScream,
		/** あにゃるに針が刺さっている時 */
		NeedleScreamInAnal,
		/** つがいの針をぐーりぐりする */
		ExtractingNeedlePartner,
		/** 子の針をぐーりぐりする */
		ExtractingNeedleChild,
		/** 被ぐーりぐり時 */
		NeedlePain,
		/** 針を抜いた時 */
		NeedleRemove,
		/** あまあま要求 */
		WantAmaama,
		/** 移動不能1 */
		CantMove,
		/** 移動不能2 */
		CantMove2,
		/** 目潰し */
		BLINDING,
		/** 盲目 */
		CANTSEE,
		/** むしられる */
		PLUNCKING,
		/** つちのなかにいる */
		BaryInUnderGround, 
		/** 引っ張る */
		Pull,
		/** 引っ張る2 */
		Pull2,
		/** つぶす */
		Press,
		/** つぶす2 */
		Press2,
		/** プレス機で潰される */
		KilledInFactory,
		/** あまあま発見 */
		FindAmaama,
		/** あまあま食事 */
		EatingAmaama,
		/** 野菜発見 */
		FindVegetable,
		/** 吐餡 */
		Vomit,
		/** 汎用、驚き */
		Surprise,
		/** むきむき時 */
		PEALING,
		/** ゆっくりできない(低) */
		LamentLowYukkuri,
		/** ゆっくりできない(完全) */
		LamentNoYukkuri, 
		/** ゆんやぁぁぁ */
		Yunnyaa,
		/** 非ゆっくり症初期 */
		NonYukkuriDiseaseNear, 
		/** 非ゆっくり症 */
		NonYukkuriDisease,
		/** 汎用、他のゆっくりに何かされたときに対する攻撃、反撃 */
		RevengeAttack,
		/** おさげ、尻尾、羽をちぎる */
		BraidCut,
		/** 他のゆっくりに生きたまま食べられる */
		EatenByBody,
		/** 他のゆっくりに生きたまま吸われる */
		EatenByBody2,
		/** 生きたままアリに食われる */
		EatenByAnts,
		/** 蟻に反撃 */
		RevengeAnts,
		/** れみりゃから逃げる */
		EscapeFromRemirya, 
		/** 口がふさがれている */
		CantTalk,
		/** しゃべれない状態で日ゆっくり */
		NYDCantTalk,
		/** 静かにする */
		BeingQuiet,
		/** 汚れている */
		Dirty,
		/** かびている */
		Moldy,
		/** 深刻なカビ */
		MoldySeriousry,

		// イベント系メッセージ
		//うんうん奴隷関連
		/** おかざりのないゆっくり発見 */
		HateYukkuri,
		/** うんうん奴隷に認定する */
		EngageUnunSlave,
		//気遣い関連
		/** つがいを気遣う */
		ConcernAboutPartner,
		/** 父を気遣う */
		ConcernAboutFather,
		/** 母を気遣う */
		ConcernAboutMother,
		/** 子を気遣う */
		ConcernAboutChild,
		/** 姉を気遣う */
		ConcernAboutEldersister,
		/** 妹を気遣う */
		ConcernAboutSister,
		/** 子をぺろぺろで治療行為を試みる */
		TreatChildByPeropero,
		/** つがいをぺろぺろで治療行為を試みる */
		TreatPartnerByPeropero,
		/** 父親をぺろぺろで治療行為を試みる */
		TreatFatherByPeropero,
		/** 母親をぺろぺろで治療行為を試みる */
		TreatMotherByPeropero,
		/** 姉をぺろぺろで治療行為を試みる */
		TreatElderSisterByPeropero,
		/** 妹をぺろぺろで治療行為を試みる */
		TreatSisterByPeropero,
		/** つがいをすりすりで治療行為を試みる */
		TreatPartnerBySurisuri,
		/** 子をすりすりで治療行為を試みる */
		TreatChildBySurisuri,
		/** 父親をすりすりで治療行為を試みる */
		TreatFatherBySurisuri,
		/** 母親をすりすりで治療行為を試みる */
		TreatMotherBySurisuri,
		/** 姉をすりすりで治療行為を試みる */
		TreatElderSisterBySurisuri,
		/** 妹をすりすりで治療行為を試みる */
		TreatSisterBySurisuri,

		/*相手によってすりすり、ぺろぺろのセリフを変えるようにする処理。現在はセリフ制作時に作者の血管が切れそうなのでオミット
		//ふつうのぺろぺろ
		peroperoChild,		// 子を
		peroperoPartner,		// つがいを
		peroperoFather,		// 父親を
		peroperoMother,		// 母親を
		peroperoElderSister,		// 姉を
		peroperoSister,		// 妹を
		//ふつうのすりすり
		SurisuriWithChild,		// 子を
		SurisuriWithPartner,		// つがいを
		SurisuriWithDad,		// 父親を
		SurisuriWithMom,		// 母親を
		SurisuriWithElderSister,		// 姉を
		SurisuriWithSister,		//妹と
		*/

		//出産イベント関連
		/** つがい出産時 */
		RootForPartner,
		/** 子への最初の挨拶 */
		FirstGreeting,
		//れいぱー関連
		/** レイパー怯え */
		ScareRapist,
		/** レイパーに攻撃 */
		AttackRapist,
		/** レイパーに反撃命令 */
		CounterRapist,
		//すいーイベント関連
		/** ゆっくりしているすぃーを見て */
		YukkuringSui,
		/** すぃーを欲しがる */
		WantingSui,
		/** すぃーを欲しがっているゆっくりのパートナー */
		WantingSuiPartner,
		/** すぃーを欲しがっているゆっくりの親 */
		WantingSuiParent,
		/** すぃーを見つける */
		FindSui,
		/** すぃーを自分のものにする */
		GetSui,
		/** 自分のすぃーにのりにいく */
		FindGetSui,
		/** 自分以外のすぃーにのりにいく */
		FindGetSuiOtner,
		/** 自分以外のすぃーにのりにいくときに呼ぶ */
		WantRideSuiOtner,
		/** すぃーにのる */
		RideSui,
		/** すぃーにのっている */
		RidingSui,
		/** すぃーを運転する */
		DrivingSui,
		/** すぃーを運転するパートナーをみて（自分はのっていない） */
		DrivingSuiPartner,
		/** すぃーを運転するこどもをみて（自分はのっていない） */
		DrivingSuiChild,
		/** すぃーを運転する父親をみて（自分はのっていない） */
		DrivingSuiPAPA,
		/** すぃーを運転する母親をみて（自分はのっていない） */
		DrivingSuiMAMA,
		/** すぃーを運転する姉をみて（自分はのっていない） */
		DrivingSuiOldSister,
		/** すぃーを運転する妹をみて（自分はのっていない） */
		DrivingSuiYoungSister,
		/** すぃーから降りる */
		RideOffSui,
		/** 持っているすぃーを自慢する */
		hasSui,
		/** 持っているすぃーを自慢されるパートナー */
		hasSuiPartner,
		/** 子どもの持っているすぃーを自慢される親 */
		hasSuiChild,
		/** 父親の持っているすぃーを自慢されるこども */
		hasSuiPAPAChild,
		/** 母親の持っているすぃーを自慢されるこども */
		hasSuiMAMAChild,
		/** 姉の持っているすぃーを自慢される */
		hasSuiOldSister,
		/** 妹の持っているすぃーを自慢される */
		hasSuiYoungSister,
		//家族の反応系
		/** 子の状態を喜ぶ */
		GladAboutChild,
		/** つがいの状態を喜ぶ */
		GladAboutPartner,
		/** 父の状態を喜ぶ */
		GladAboutFather,
		/** 母の状態を喜ぶ */
		GladAboutMother,
		/** 姉の状態を喜ぶ */
		GladAboutElderSister,
		/** 妹の状態を喜ぶ */
		GladAboutSister,
		/** 姉の状態をうらやましがる */
		EnvyAboutElderSister,
		/** 妹の状態をうらやましがる */
		EnvyAboutSister,
		/** 他人の状態をうらやましがる */
		EnvyAboutOther,
		/** 姉の状態をうらやましがって泣く */
		EnvyCryAboutElderSister, 
		/** 妹の状態をうらやましがって泣く */
		EnvyCryAboutSister,
		/** 他人の状態をうらやましがって泣く */
		EnvyCryAboutOther,
		/** 子供の状態がうらやましすぎて憎む */
		HateWithEnvyAboutChild,
		/** つがいの状態がうらやましすぎて憎む */
		HateWithEnvyAboutPartner,
		/** 父の状態がうらやましすぎて憎む */
		HateWithEnvyAboutFather,
		/** 母の状態がうらやましすぎて憎む */
		HateWithEnvyAboutMother,
		/** 姉の状態がうらやましすぎて憎む */
		HateWithEnvyAboutElderSister,
		/** 妹の状態がうらやましすぎて憎む */
		HateWithEnvyAboutSister,
		/** 他人の状態がうらやましすぎて憎む */
		HateWithEnvyAboutOther,
		/** 他人を憐れむ */
		MercyAboutOther,
		/** プレイヤーにすりすりされている */
		SuriSuriByPlayer,
		/** 姉の状態をうらやましがる(プレイヤーによるすりすり時) */
		EnvyAboutElderSisterInSurisuri,
		/** 妹の状態をうらやましがる(プレイヤーによるすりすり時) */
		EnvyAboutSisterInSurisuri,
		/** 姉の状態をうらやましがって泣く(プレイヤーによるすりすり時) */
		EnvyCryAboutElderSisterInSurisuri,
		/** 妹の状態をうらやましがって泣く(プレイヤーによるすりすり時) */
		EnvyCryAboutSisterInSurisuri,
		/** うんうん体操イベント親1 */
		ShitExercisesGOFrom, 
		/** うんうん体操イベント親2 */
		ShitExercisesWAITFrom,
		/** うんうん体操イベント親3 */
		ShitExercisesSTARTFrom,
		/**うんうん体操イベント 親4 */
		ShitExercisesYURAYURAFrom,
		/** うんうん体操イベント親5 */
		ShitExercisesNOBINOBIFrom,
		/** うんうん体操イベント親6 */
		ShitExercisesPOKAPOKAFrom,
		/** うんうん体操イベント親7 */
		ShitExercisesUNUNFrom,
		/** うんうん体操イベント親8 */
		ShitExercisesENDFrom,
		/** うんうん体操イベント子1 */
		ShitExercisesGO,
		/** うんうん体操イベント子2 */
		ShitExercisesWAIT,
		/** うんうん体操イベント子3 */
		ShitExercisesSTART,
		/** うんうん体操イベント子4 */
		ShitExercisesYURAYURA,
		/** うんうん体操イベント子5 */
		ShitExercisesNOBINOBI,
		/** うんうん体操イベント子6 */
		ShitExercisesPOKAPOKA,
		/** うんうん体操イベント子7 */
		ShitExercisesUNUN,
		//そのほか家族イベント
		/** 家族で食事に行く（待機中） */
		FamilyEatingTimeWait,
		/** すーぱーむーしゃむーしゃたいむ */
		SuperEatingTime,
		/** おちびちゃん運び */
		RideOnMe,
		/** 親を探す */
		LookForParents,
		//
		/** おちび自慢イベント親：歌 */
		ProudChildsSING,
		/** おちび自慢イベント親；来い */
		ProudChildsGOFrom,
		/** おちび自慢イベント親：待つ */
		ProudChildsWAITFrom,
		/** おちび自慢イベント親：始める */
		ProudChildsSTARTFrom,
		/** おちび自慢イベント親：本編 */
		ProudChildsPROUDFrom,
		/** おちび自慢イベント親：終わる */
		ProudChildsENDFrom,
		/** おちび自慢イベント子：行く */
		ProudChildsGO,
		/** おちび自慢イベント子：待つ */
		ProudChildsWAIT,
		/** おちび自慢イベント子：始める */
		ProudChildsSTART,
		/** おちび自慢イベント子：本編 */
		ProudChildsPROUD,
		/** おちび自慢イベント子：終わる */
		ProudChildsEND,
		//カビイベント
		/** 子供かびる */
		SadnessForMoldyChild,
		/** 父かびる */
		SadnessForMoldyFather,
		/** 母かびる */
		SadnessForMoldyMother, 
		/** つがいかびる */
		SadnessForMoldyPartner,
		/** 姉かびる */
		SadnessForMoldyEldersister,
		/** 妹かびる */
		SadnessForMoldySister,
		/** 子供に謝罪 */
		ApologyToChild, 
		/** 家族に謝罪	//下の三つは現状で内容が一緒なため、共用 */
		ApologyToFamily, //
		//		ApologyToPartner,		//番に謝罪
		//		ApologyToElderSister,		//姉に謝罪
		//		ApologyToSister,		//妹に謝罪
		/** かびゆヘイト */
		HateMoldyYukkuri, 
		//命乞いイベント
		/** 人間に謝罪 */
		ApologyToHuman,
		/** 命乞い */
		BegForLife,
		/** 人間に感謝 */
		ThanksHuman,
		/** 人間に感謝(ダメージ有) */
		ThanksHuman2,
		/** 命乞いのあとの独り言 */
		Monologue,
		
		/** 捕食種の遊びイベント開始 */
		GameStart,
		/** まて～ */
		HeyYouWait, 
		/** つっかまえた */
		CaughtYou,
		/** 落っことす */
		DropYukkuri,
		/** 私で遊ぶな */
		DontPlayMe, 
		/** もろさに文句 */
		ComplainAboutFragleness,
		/** 捕食種の遊びイベント終了 */
		GameEnd, //
		//プロポーズイベント
		/** 待ってください */
		PleaseWait,
		/** プロポーズ */
		Propose,
		/** OK */
		ProposeYes,
		/** NO */
		ProposeNo,
		/** それじゃすっきりしよう */
		LetsPlay,
		/** いいよ、きて// */
		OKcome, 
		/** 失恋 */
		Heartbreak, 
		/** じゃましないでね！せいっさいするよ！ */
		DontPreventUs,
		//おとむらいイベント
		/** おとむらいイベント開始時(親側) */
		FuneralSTARTFrom, 
		/** おとむらいイベント開始時(子側) */
		FuneralSTART,
		/** おとむらいイベント説明(親側) */
		FuneralIntroduceFrom,
		/** おとむらいイベント子の説明へのリアクション */
		FuneralIntroduce,
		/** ちんこんかっ！ */
		Requiem,
		/** おとむらいイベント雑談(親側) */
		FuneralTalkFrom,
		/** おとむらいイベント雑談(子側) */
		FueralTALK,
		/** さようならっ！ */
		GoodbyeForever,
		/** おとむらいイベント終了時(親側) */
		FuneralENDFrom,
		/** おとむらいイベント終了時(子側) */
		FuneralEND,

	}

	// 埋め込み文字定義
	public enum Replace {
		name, name2, partner, dummy
	}

	// クラス別接尾子
	public static final String[] RANK_SUFFIX = { "", "_<nora>" };

	private static Random rnd = new Random();
	private static HashMap<String, MessageMap>[] pool_j = null;

	// 全メッセージ読み込み
	// ひとつの固体の複数ファイルに渡る全メッセージを1つのマップに格納するので
	// アクション名の被りに注意
	@SuppressWarnings("unchecked")
	public static final void loadMessage(ClassLoader loader) {
		BufferedReader br = null;

		YukkuriType[] yk = YukkuriType.values();
		pool_j = new HashMap[yk.length];

		for (int i = 0; i < yk.length; i++) {
			pool_j[i] = new HashMap<String, MessageMap>();
			// 飼いゆ
			// 汎用メッセージ
			br = ModLoader.openMessageFile(loader, ModLoader.DATA_MSG_DIR, yk[i].messageFileName + "_j.txt", true);
			try {
				readMessageMap(br, pool_j[i], RANK_SUFFIX[BodyRank.KAIYU.messageIndex]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// イベントメッセージ
			br = ModLoader.openMessageFile(loader, ModLoader.DATA_MSG_DIR, yk[i].messageFileName + "_ev_j.txt", true);
			try {
				readMessageMap(br, pool_j[i], RANK_SUFFIX[BodyRank.KAIYU.messageIndex]);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 野良ゆ
			// 汎用メッセージ
			br = ModLoader.openMessageFile(loader, ModLoader.DATA_MSG_DIR + ModLoader.YK_WORD_NORA,
					yk[i].messageFileName + "_j.txt", false);
			if (br != null) {
				try {
					readMessageMap(br, pool_j[i], RANK_SUFFIX[BodyRank.NORAYU.messageIndex]);
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// イベントメッセージ
			br = ModLoader.openMessageFile(loader, ModLoader.DATA_MSG_DIR + ModLoader.YK_WORD_NORA,
					yk[i].messageFileName + "_ev_j.txt", false);
			if (br != null) {
				try {
					readMessageMap(br, pool_j[i], RANK_SUFFIX[BodyRank.NORAYU.messageIndex]);
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 1ファイル読み込み
	private static final void readMessageMap(BufferedReader br, HashMap<String, MessageMap> map, String suffix)
			throws IOException {
		String actName = null;
		MessageMap act = null;
		String line = null;
		ArrayList<String> msg = null;
		boolean[] flags = null;
		String tagName = null;

		do {
			line = br.readLine();
			if (line != null)
				line = line.trim();

			if (line == null || "".equals(line))
				continue;

			// 先頭文字判定
			String head1 = line.substring(0, 1);
			if ("#".equals(head1))
				continue;

			if ("[".equals(head1)) // アクション
			{
				String head2 = line.substring(1, 2);
				// アクションを閉じる
				if ("/".equals(head2)) {
					if (actName != null && act != null) {
						map.put(actName, act);
						actName = null;
						act = null;
						msg = null;
						flags = null;
						tagName = null;
					}
					continue;
				}
				// アクション開始
				int st = line.indexOf("[") + 1;
				int ed = line.indexOf("]");
				actName = line.substring(st, ed) + suffix;
			} else if ("<".equals(head1)) // サブタグ
			{
				if (actName == null)
					continue;

				// 現在までのメッセージを登録
				if (msg != null && msg.size() > 0) {
					String key = createTagKey(flags);
					if (key != null) {
						act.map.put(key, (String[]) msg.toArray(new String[] {}));
					}
					msg = new ArrayList<String>();
				}

				// タグを閉じる
				String head2 = line.substring(1, 2);
				if ("/".equals(head2)) {
					int st = line.indexOf("/") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);
					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					flags[tag.ordinal()] = false;

					// normal, rudeタグの区切り
					if (MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag)) {
						flags = null;
						msg = null;
					}
				} else {
					// タグ開始
					int st = line.indexOf("<") + 1;
					int ed = line.indexOf(">");
					tagName = line.substring(st, ed);

					MessageMap.Tag tag = MessageMap.Tag.valueOf(tagName);
					if (tag != null) {
						if (MessageMap.Tag.normal.equals(tag) || MessageMap.Tag.rude.equals(tag)) {
							if (act == null) {
								act = new MessageMap();
							}
							msg = new ArrayList<String>();
							flags = new boolean[MessageMap.Tag.values().length];
							if (MessageMap.Tag.rude.equals(tag)) {
								act.rudeFlag = true;
							} else {
								act.normalFlag = true;
							}
						}
						flags[tag.ordinal()] = true;
						if (flags[MessageMap.Tag.normal.ordinal()]) {
							act.normalTag[tag.ordinal()] = true;
						} else if (flags[MessageMap.Tag.rude.ordinal()]) {
							act.rudeTag[tag.ordinal()] = true;
						}
					}
				}
			} else // セリフ
			{
				msg.add(line);
			}
		} while (line != null);
	}

	// フラグからマップキー作成
	private static final String createTagKey(boolean[] flags) {
		StringBuffer key = new StringBuffer("");
		MessageMap.Tag[] tags = MessageMap.Tag.values();

		for (int i = 0; i < tags.length; i++) {
			if (flags[i]) {
				key.append(tags[i].name());
				key.append("_");
			}
		}
		if (key.length() == 0)
			return null;

		return key.toString();
	}

	// メッセージ取得
	public static final String getMessage(Body body, Action action) {
		//メッセージ変更
		//皮むき時
		if (body.isPealed() && action == MessagePool.Action.Scream) {
			action = MessagePool.Action.Scream2;
		}
		//口封じ時
		if (body.isShutmouth()) {
			if (body.isSleeping())
				return null;
			else if (body.geteCoreAnkoState() != CoreAnkoState.DEFAULT)
				action = MessagePool.Action.NYDCantTalk;
			else
				action = MessagePool.Action.CantTalk;
		}

		HashMap<String, MessageMap> map = null;
		MessageMap act = null;
		String name = "";
		String name2 = "";
		String partnerName = "";
		String suffix = RANK_SUFFIX[body.getBodyRank().messageIndex];
		map = pool_j[body.getMsgType().ordinal()];
		//name = body.getMyNameJ;
		if (body.isStressful() && body.isDamaged() && rnd.nextBoolean()) {
			name = body.getMyNameD();
		} else {
			name = body.getMyName();
		}
		if (name.isEmpty()) {
			name = body.getNameJ();
			//name =YukkuriUtil.getYukkuriClassName(body.getType());
		}
		name2 = body.getNameJ2();
		if (body.getPartner() != null)
			partnerName = body.getPartner().getNameJ();

		if (map == null)
			return "NO MESSAGE FILE";

		act = map.get(action.name() + suffix);
		// 読み込み失敗かつ飼いゆメッセージではないなら飼いゆメッセージを読み込む
		if (act == null && body.getBodyRank().messageIndex != BodyRank.KAIYU.messageIndex) {
			//System.out.println("NO NORA MESSAGE FILE: " + body.getNameJ() );
			suffix = RANK_SUFFIX[BodyRank.KAIYU.messageIndex];
			act = map.get(action.name() + suffix);
		}

		if (act == null)
			return "NO ACTION [" + action.name() + "]";

		boolean[] flags = null;
		StringBuilder key = null;
		String[] beforeMsg = null;
		String[] tmpMsg = null;
		// ゲスチェック
		if ((body.isRude() && act.rudeFlag) || !act.normalFlag) {
			flags = act.rudeTag;
			key = new StringBuilder(MessageMap.Tag.rude.name() + "_");
		} else {
			flags = act.normalTag;
			key = new StringBuilder(MessageMap.Tag.normal.name() + "_");
		}

		// 上位タグの文字列を保存
		tmpMsg = act.map.get(key.toString());
		if (tmpMsg != null) {
			beforeMsg = tmpMsg;
		}

		// 年齢チェック
		if (body.getMindAgeState() == AgeState.BABY && flags[MessageMap.Tag.baby.ordinal()]) {
			key.append(MessageMap.Tag.baby.name() + "_");

			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		} else if (body.getMindAgeState() == AgeState.CHILD && flags[MessageMap.Tag.child.ordinal()]) {
			key.append(MessageMap.Tag.child.name() + "_");

			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		} else if (body.getMindAgeState() == AgeState.ADULT && flags[MessageMap.Tag.adult.ordinal()]) {
			key.append(MessageMap.Tag.adult.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}

		// ダメージチェック
		if (body.isDamaged() && flags[MessageMap.Tag.damage.ordinal()]) {
			key.append(MessageMap.Tag.damage.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}
		// 足焼きチェック
		if (body.getFootBakeLevel() == FootBake.CRITICAL && flags[MessageMap.Tag.footbake.ordinal()]) {
			key.append(MessageMap.Tag.footbake.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}
		// おくるみチェック
		if (body.isHasPants() && flags[MessageMap.Tag.pants.ordinal()]) {
			key.append(MessageMap.Tag.pants.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}
		// なつき度チェック(好き)
		if ((body.checkLovePlayerState() == LovePlayer.GOOD) && flags[MessageMap.Tag.loveplayer.ordinal()]) {
			key.append(MessageMap.Tag.loveplayer.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
			// なつき度チェック(嫌い)
		} else if ((body.checkLovePlayerState() == LovePlayer.BAD) && flags[MessageMap.Tag.dislikeplayer.ordinal()]) {
			key.append(MessageMap.Tag.dislikeplayer.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}

		// うんうん奴隷チェック
		if (body.getPublicRank() == PublicRank.UnunSlave && flags[MessageMap.Tag.ununSlave.ordinal()]) {
			key.append(MessageMap.Tag.ununSlave.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}

		// 賢い子チェック
		if (body.getIntelligence() == Intelligence.WISE && flags[MessageMap.Tag.wise.ordinal()]) {
			key.append(MessageMap.Tag.wise.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}
		// あほの子チェック
		if (body.getIntelligence() == Intelligence.FOOL && flags[MessageMap.Tag.fool.ordinal()]) {
			key.append(MessageMap.Tag.fool.name() + "_");
			// 上位タグの文字列を保存
			tmpMsg = act.map.get(key.toString());
			if (tmpMsg != null) {
				beforeMsg = tmpMsg;
			}
		}

		String[] msg = act.map.get(key.toString());
		if (msg == null) {
			msg = beforeMsg;
		}

		if (msg == null)
			return "NO TAG <" + key.toString() + ">";

		StringBuffer ret = new StringBuffer(msg[rnd.nextInt(msg.length)]);
		// 埋め込み文字の置き換え
		if (ret.indexOf("%") != -1) {
			if (ret.indexOf("%" + Replace.dummy.name()) != -1)
				return null;
			int st;
			do {
				st = ret.indexOf("%" + Replace.name2.name());
				if (st != -1) {
					try {
						ret.replace(st, st + Replace.name2.name().length() + 1, name2);
					} catch (StringIndexOutOfBoundsException e) {
						System.out.println("[" + action + "]" + ret + "(" + st + ")" + "name2");
					}
				}
			} while (st != -1);
			do {
				st = ret.indexOf("%" + Replace.name.name());
				if (st != -1) {
					try {
						ret.replace(st, st + Replace.name.name().length() + 1, name);
					} catch (StringIndexOutOfBoundsException e) {
						System.out.println("[" + action + "]" + ret + "(" + st + ")" + "name");
					}
				}
			} while (st != -1);
			do {
				st = ret.indexOf("%" + Replace.partner.name());
				if (st != -1) {
					if (partnerName.length() == 0) {
						return "";
					}
					try {
						ret.replace(st, st + Replace.partner.name().length() + 1, partnerName);
					} catch (StringIndexOutOfBoundsException e) {
						System.out.println("[" + action + "]" + ret + "(" + st + ")" + "partnerName");
					}
				}
			} while (st != -1);
		}
		// デバッグ用
		//System.out.println(action.name() + " " + ret.toString());

		return ret.toString();
	}

	/*
		private static final void test(ClassLoader loader)
		{
			try {
				InputStream is = loader.getResourceAsStream("data/marisa_j.xls");
				Workbook wb;
				wb = WorkbookFactory.create(is);
				Sheet sheet = wb.getSheetAt(0);
				Row row = sheet.getRow(0);
				Cell cell = row.getCell(0);
				System.out.println(cell.getStringCellValue());
			} catch (InvalidFormatException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	*/
}
