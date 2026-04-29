package src.draw;

import src.system.MapPlaceData;
import src.util.GameWorld;

/**
 * Terrarium のマップ警戒状態を扱う補助ロジック。
 */
public final class TerrariumAlarmLogic {
	private TerrariumAlarmLogic() {
	}

	/**
	 * マップ全体を危険状態にする。
	 *
	 * @param alarmPeriod 警戒継続時間
	 */
	public static void setAlarm(int alarmPeriod) {
		GameWorld.get().getCurrentMap().setAlarm(true);
		GameWorld.get().getCurrentMap().setAlarmPeriod(alarmPeriod);
	}

	/**
	 * 現在マップが危険状態かを返す。
	 *
	 * @return 危険ならtrue
	 */
	public static boolean getAlarm() {
		return GameWorld.get().getCurrentMap().isAlarm();
	}

	/**
	 * 警戒継続時間を1減らし、必要なら解除する。
	 *
	 * @param curMap 現在マップ
	 */
	public static void advanceAlarm(MapPlaceData curMap) {
		if (curMap.getAlarmPeriod() >= 0) {
			curMap.setAlarmPeriod(curMap.getAlarmPeriod() - 1);
			if (curMap.getAlarmPeriod() <= 0) {
				curMap.setAlarmPeriod(0);
				curMap.setAlarm(false);
			}
		}
	}
}
