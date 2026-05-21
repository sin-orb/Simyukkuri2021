package org.simyukkuri.util;

import org.simyukkuri.engine.Terrarium;

/**
 * World environment state accessor.
 */
public interface EnvironmentSource {
	/**
	 * Returns the current operation time.
	 *
	 * @return operation time
	 */
	int getOperationTime();

	/**
	 * Returns the current day time.
	 *
	 * @return day time
	 */
	int getDayTime();

	/**
	 * Returns the current night time.
	 *
	 * @return night time
	 */
	int getNightTime();

	/**
	 * Returns whether the environment is humid.
	 *
	 * @return true if humid
	 */
	boolean isHumid();

	/**
	 * Returns whether antifungal steam is active.
	 *
	 * @return true if active
	 */
	boolean isAntifungalSteam();

	/**
	 * Returns whether orange steam is active.
	 *
	 * @return true if active
	 */
	boolean isOrangeSteam();

	/**
	 * Returns whether age boost steam is active.
	 *
	 * @return true if active
	 */
	boolean isAgeBoostSteam();

	/**
	 * Returns whether age stop steam is active.
	 *
	 * @return true if active
	 */
	boolean isAgeStopSteam();

	/**
	 * Returns whether antidos steam is active.
	 *
	 * @return true if active
	 */
	boolean isAntidosSteam();

	/**
	 * Returns whether poison steam is active.
	 *
	 * @return true if active
	 */
	boolean isPoisonSteam();

	/**
	 * Returns whether predator steam is active.
	 *
	 * @return true if active
	 */
	boolean isPredatorSteam();

	/**
	 * Returns whether suger steam is active.
	 *
	 * @return true if active
	 */
	boolean isSugerSteam();

	/**
	 * Returns whether no-sleep steam is active.
	 *
	 * @return true if active
	 */
	boolean isNoSleepSteam();

	/**
	 * Returns whether hybrid steam is active.
	 *
	 * @return true if active
	 */
	boolean isHybridSteam();

	/**
	 * Returns whether rapid pregnancy steam is active.
	 *
	 * @return true if active
	 */
	boolean isRapidPregnantSteam();

	/**
	 * Returns whether anti-non-yukkuri-disease steam is active.
	 *
	 * @return true if active
	 */
	boolean isAntiNonYukkuriDiseaseSteam();

	/**
	 * Returns whether endless furifuri steam is active.
	 *
	 * @return true if active
	 */
	boolean isEndlessFurifuriSteam();

	/**
	 * Returns the current tick count.
	 *
	 * @return tick count
	 */
	int getTick();

	/**
	 * Sets the alarm state.
	 */
	void setAlarm();

	/**
	 * Returns the alarm state.
	 *
	 * @return true if alarm is on
	 */
	boolean getAlarm();

	/**
	 * Returns the current day state.
	 *
	 * @return day state
	 */
	Terrarium.DayState getDayState();

	/**
	 * Resets the environment state.
	 */
	void resetTerrariumEnvironment();

	/**
	 * Returns the update interval.
	 *
	 * @return interval
	 */
	int getInterval();
}
