package src.draw;

import src.item.Diffuser;

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

	public static int getOperationTime() {
		return operationTime;
	}

	public static int getDayTime() {
		return dayTime;
	}

	public static int getNightTime() {
		return nightTime;
	}

	public static int getTick() {
		return TICK;
	}

	public static int getInterval() {
		return intervalCount;
	}

	public static int advanceInterval() {
		intervalCount = (++intervalCount) & 255;
		return intervalCount;
	}

	public static int getElapsedTimeInDayCycle() {
		return operationTime % (dayTime + nightTime);
	}

	public static void advanceOperationTime() {
		operationTime += TICK;
	}

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

	public static boolean isHumid() {
		return humid;
	}

	public static boolean isAntifungalSteam() {
		return antifungalSteam;
	}

	public static boolean isOrangeSteam() {
		return orangeSteam;
	}

	public static boolean isAgeBoostSteam() {
		return ageBoostSteam;
	}

	public static boolean isAgeStopSteam() {
		return ageStopSteam;
	}

	public static boolean isAntidosSteam() {
		return antidosSteam;
	}

	public static boolean isPoisonSteam() {
		return poisonSteam;
	}

	public static boolean isPredatorSteam() {
		return predatorSteam;
	}

	public static boolean isSugerSteam() {
		return sugerSteam;
	}

	public static boolean isNoSleepSteam() {
		return noSleepSteam;
	}

	public static boolean isHybridSteam() {
		return hybridSteam;
	}

	public static boolean isRapidPregnantSteam() {
		return rapidPregnantSteam;
	}

	public static boolean isAntiNonYukkuriDiseaseSteam() {
		return antiNonYukkuriDiseaseSteam;
	}

	public static boolean isEndlessFurifuriSteam() {
		return endlessFurifuriSteam;
	}
}
