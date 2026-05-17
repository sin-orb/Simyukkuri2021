package org.simyukkuri.engine;

import org.simyukkuri.entity.core.world.item.Diffuser;

/**
 * Terrarium の全体環境状態を保持する。
 */
public final class TerrariumEnvironment {

	private static int operationTime = 0;
	private static final int dayTime = 100 * 24 * 2 / 3;
	private static final int nightTime = 100 * 24 - dayTime;
	private static final int TICK = 1;
	private static boolean humid = false;
	private static boolean antifungalSteam = false;
	private static boolean orangeSteam = false;
	private static boolean ageBoostSteam = false;
	private static boolean ageStopSteam = false;
	private static boolean antidosSteam = false;
	private static boolean poisonSteam = false;
	private static boolean predatorSteam = false;
	private static boolean sugerSteam = false;
	private static boolean noSleepSteam = false;
	private static boolean hybridSteam = false;
	private static boolean rapidPregnantSteam = false;
	private static boolean antiNonYukkuriDiseaseSteam = false;
	private static boolean endlessFurifuriSteam = false;
	private static int intervalCount = 0;

	private TerrariumEnvironment() {
	}

	/**
	 * ゲーム経過ティック数を返す。
	 *
	 * @return 経過ティック数
	 */
	public static int getOperationTime() {
		return operationTime;
	}

	/**
	 * 1昼夜サイクルの昼間部分のティック数を返す。
	 *
	 * @return 昼間ティック数
	 */
	public static int getDayTime() {
		return dayTime;
	}

	/**
	 * 1昼夜サイクルの夜間部分のティック数を返す。
	 *
	 * @return 夜間ティック数
	 */
	public static int getNightTime() {
		return nightTime;
	}

	/**
	 * 1ステップのティック増分を返す。
	 *
	 * @return ティック増分
	 */
	public static int getTick() {
		return TICK;
	}

	/**
	 * インターバルカウンタ値（0–255）を返す。
	 *
	 * @return インターバルカウンタ
	 */
	public static int getInterval() {
		return intervalCount;
	}

	/**
	 * インターバルカウンタをインクリメントして返す。0–255 の循環カウンタ。
	 *
	 * @return インクリメント後のカウンタ値
	 */
	public static int advanceInterval() {
		intervalCount = (++intervalCount) & 255;
		return intervalCount;
	}

	/**
	 * 昼夜サイクル内での経過ティック数を返す。
	 *
	 * @return サイクル内経過ティック数
	 */
	public static int getElapsedTimeInDayCycle() {
		return operationTime % (dayTime + nightTime);
	}

	/**
	 * ゲーム経過ティック数を 1 進める。
	 */
	public static void advanceOperationTime() {
		operationTime += TICK;
	}

	/**
	 * 全スチームフラグをリセットする。毎ティックの処理開始時に呼ぶ。
	 */
	public static void resetTerrariumEnvironment() {
		antifungalSteam = false;
		humid = false;
		orangeSteam = false;
		ageBoostSteam = false;
		ageStopSteam = false;
		antidosSteam = false;
		poisonSteam = false;
		predatorSteam = false;
		sugerSteam = false;
		noSleepSteam = false;
		hybridSteam = false;
		rapidPregnantSteam = false;
		antiNonYukkuriDiseaseSteam = false;
		endlessFurifuriSteam = false;
	}

	/**
	 * ディフューザーのスチームフラグ配列を環境フラグに反映する。
	 *
	 * @param flags ディフューザーが出力するスチームフラグ配列（{@link Diffuser.SteamType} のordinalに対応）
	 */
	public static void applyDiffuserSteamFlags(boolean[] flags) {
		if (flags[Diffuser.SteamType.ANTI_FUNGAL.ordinal()]) {
			antifungalSteam = true;
		}
		if (flags[Diffuser.SteamType.STEAM.ordinal()]) {
			humid = true;
		}
		if (flags[Diffuser.SteamType.ORANGE.ordinal()]) {
			orangeSteam = true;
		}
		if (flags[Diffuser.SteamType.AGE_BOOST.ordinal()]) {
			ageBoostSteam = true;
		}
		if (flags[Diffuser.SteamType.AGE_STOP.ordinal()]) {
			ageStopSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_DOS.ordinal()]) {
			antidosSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_YU.ordinal()]) {
			poisonSteam = true;
		}
		if (flags[Diffuser.SteamType.PREDATOR.ordinal()]) {
			predatorSteam = true;
		}
		if (flags[Diffuser.SteamType.SUGER.ordinal()]) {
			sugerSteam = true;
		}
		if (flags[Diffuser.SteamType.NOSLEEP.ordinal()]) {
			noSleepSteam = true;
		}
		if (flags[Diffuser.SteamType.HYBRID.ordinal()]) {
			hybridSteam = true;
		}
		if (flags[Diffuser.SteamType.RAPIDPREGNANT.ordinal()]) {
			rapidPregnantSteam = true;
		}
		if (flags[Diffuser.SteamType.ANTI_NONYUKKURI.ordinal()]) {
			antiNonYukkuriDiseaseSteam = true;
		}
		if (flags[Diffuser.SteamType.ENDLESS_FURIFURI.ordinal()]) {
			endlessFurifuriSteam = true;
		}
	}

	/**
	 * スチームによる加湿フラグを返す。
	 *
	 * @return 加湿スチームが有効なら true
	 */
	public static boolean isHumid() {
		return humid;
	}

	/**
	 * 抗菌スチームフラグを返す。
	 *
	 * @return 抗菌スチームが有効なら true
	 */
	public static boolean isAntifungalSteam() {
		return antifungalSteam;
	}

	/**
	 * オレンジスチームフラグを返す。
	 *
	 * @return オレンジスチームが有効なら true
	 */
	public static boolean isOrangeSteam() {
		return orangeSteam;
	}

	/**
	 * 加齢促進スチームフラグを返す。
	 *
	 * @return 加齢促進スチームが有効なら true
	 */
	public static boolean isAgeBoostSteam() {
		return ageBoostSteam;
	}

	/**
	 * 加齢停止スチームフラグを返す。
	 *
	 * @return 加齢停止スチームが有効なら true
	 */
	public static boolean isAgeStopSteam() {
		return ageStopSteam;
	}

	/**
	 * アンチDOSスチームフラグを返す。
	 *
	 * @return アンチDOSスチームが有効なら true
	 */
	public static boolean isAntidosSteam() {
		return antidosSteam;
	}

	/**
	 * 毒スチームフラグを返す。
	 *
	 * @return 毒スチームが有効なら true
	 */
	public static boolean isPoisonSteam() {
		return poisonSteam;
	}

	/**
	 * 捕食者スチームフラグを返す。
	 *
	 * @return 捕食者スチームが有効なら true
	 */
	public static boolean isPredatorSteam() {
		return predatorSteam;
	}

	/**
	 * 砂糖スチームフラグを返す。
	 *
	 * @return 砂糖スチームが有効なら true
	 */
	public static boolean isSugerSteam() {
		return sugerSteam;
	}

	/**
	 * 睡眠抑制スチームフラグを返す。
	 *
	 * @return 睡眠抑制スチームが有効なら true
	 */
	public static boolean isNoSleepSteam() {
		return noSleepSteam;
	}

	/**
	 * ハイブリッドスチームフラグを返す。
	 *
	 * @return ハイブリッドスチームが有効なら true
	 */
	public static boolean isHybridSteam() {
		return hybridSteam;
	}

	/**
	 * 急速妊娠スチームフラグを返す。
	 *
	 * @return 急速妊娠スチームが有効なら true
	 */
	public static boolean isRapidPregnantSteam() {
		return rapidPregnantSteam;
	}

	/**
	 * 非ゆっくり症治癒スチームフラグを返す。
	 *
	 * @return 非ゆっくり症治癒スチームが有効なら true
	 */
	public static boolean isAntiNonYukkuriDiseaseSteam() {
		return antiNonYukkuriDiseaseSteam;
	}

	/**
	 * エンドレスふりふりスチームフラグを返す。
	 *
	 * @return エンドレスふりふりスチームが有効なら true
	 */
	public static boolean isEndlessFurifuriSteam() {
		return endlessFurifuriSteam;
	}
}
