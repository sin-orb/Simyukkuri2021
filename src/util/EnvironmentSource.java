package src.util;

import src.draw.Terrarium;

public interface EnvironmentSource {
	int getOperationTime();

	int getDayTime();

	int getNightTime();

	boolean isHumid();

	boolean isAntifungalSteam();

	boolean isOrangeSteam();

	boolean isAgeBoostSteam();

	boolean isAgeStopSteam();

	boolean isAntidosSteam();

	boolean isPoisonSteam();

	boolean isPredatorSteam();

	boolean isSugerSteam();

	boolean isNoSleepSteam();

	boolean isHybridSteam();

	boolean isRapidPregnantSteam();

	boolean isAntiNonYukkuriDiseaseSteam();

	boolean isEndlessFurifuriSteam();

	int getTick();

	void setAlarm();

	boolean getAlarm();

	Terrarium.DayState getDayState();

	void resetTerrariumEnvironment();

	int getInterval();
}
