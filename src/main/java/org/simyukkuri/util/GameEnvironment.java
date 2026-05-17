package org.simyukkuri.util;

import org.simyukkuri.engine.Terrarium;

/**
 * GameEnvironment.
 */
public final class GameEnvironment {
	private static final EnvironmentSource DEFAULT = new EnvironmentSource() {
		/** 稼働時間を返す。 */
		@Override
		public int getOperationTime() {
			return Terrarium.getOperationTime();
		}

		/** 昼の時間設定を返す。 */
		@Override
		public int getDayTime() {
			return Terrarium.getDayTime();
		}

		/** 夜の時間設定を返す。 */
		@Override
		public int getNightTime() {
			return Terrarium.getNightTime();
		}

		/** 湿度スチームが有効かを返す。 */
		@Override
		public boolean isHumid() {
			return Terrarium.isHumid();
		}

		/** 抗菌スチームが有効かを返す。 */
		@Override
		public boolean isAntifungalSteam() {
			return Terrarium.isAntifungalSteam();
		}

		/** オレンジスチームが有効かを返す。 */
		@Override
		public boolean isOrangeSteam() {
			return Terrarium.isOrangeSteam();
		}

		/** 成長促進スチームが有効かを返す。 */
		@Override
		public boolean isAgeBoostSteam() {
			return Terrarium.isAgeBoostSteam();
		}

		/** 成長停止スチームが有効かを返す。 */
		@Override
		public boolean isAgeStopSteam() {
			return Terrarium.isAgeStopSteam();
		}

		/** 解毒スチームが有効かを返す。 */
		@Override
		public boolean isAntidosSteam() {
			return Terrarium.isAntidosSteam();
		}

		/** 毒スチームが有効かを返す。 */
		@Override
		public boolean isPoisonSteam() {
			return Terrarium.isPoisonSteam();
		}

		/** 捕食者スチームが有効かを返す。 */
		@Override
		public boolean isPredatorSteam() {
			return Terrarium.isPredatorSteam();
		}

		/** 砂糖スチームが有効かを返す。 */
		@Override
		public boolean isSugerSteam() {
			return Terrarium.isSugerSteam();
		}

		/** 不眠スチームが有効かを返す。 */
		@Override
		public boolean isNoSleepSteam() {
			return Terrarium.isNoSleepSteam();
		}

		/** ハイブリッドスチームが有効かを返す。 */
		@Override
		public boolean isHybridSteam() {
			return Terrarium.isHybridSteam();
		}

		/** 妊娠促進スチームが有効かを返す。 */
		@Override
		public boolean isRapidPregnantSteam() {
			return Terrarium.isRapidPregnantSteam();
		}

		/** 非ゆっくり病予防スチームが有効かを返す。 */
		@Override
		public boolean isAntiNonYukkuriDiseaseSteam() {
			return Terrarium.isAntiNonYukkuriDiseaseSteam();
		}

		/** エンドレスふりふりスチームが有効かを返す。 */
		@Override
		public boolean isEndlessFurifuriSteam() {
			return Terrarium.isEndlessFurifuriSteam();
		}

		/** 現在のティックカウントを返す。 */
		@Override
		public int getTick() {
			return Terrarium.getTick();
		}

		/** アラームをセットする。 */
		@Override
		public void setAlarm() {
			Terrarium.setAlarm();
		}

		/** アラーム状態を返す。 */
		@Override
		public boolean getAlarm() {
			return Terrarium.getAlarm();
		}

		@Override
		public Terrarium.DayState getDayState() {
			return Terrarium.getDayState();
		}

		/** テラリウム環境をリセットする。 */
		@Override
		public void resetTerrariumEnvironment() {
			Terrarium.resetTerrariumEnvironment();
		}

		/** インターバル値を返す。 */
		@Override
		public int getInterval() {
			return Terrarium.getInterval();
		}
	};

	private static EnvironmentSource override;

	private GameEnvironment() {
	}

	/** 稼働時間を返す。 */
	public static int getOperationTime() {
		return source().getOperationTime();
	}

	/** 昼の時間設定を返す。 */
	public static int getDayTime() {
		return source().getDayTime();
	}

	/** 夜の時間設定を返す。 */
	public static int getNightTime() {
		return source().getNightTime();
	}

	/** 湿度スチームが有効かを返す。 */
	public static boolean isHumid() {
		return source().isHumid();
	}

	/** 抗菌スチームが有効かを返す。 */
	public static boolean isAntifungalSteam() {
		return source().isAntifungalSteam();
	}

	/** オレンジスチームが有効かを返す。 */
	public static boolean isOrangeSteam() {
		return source().isOrangeSteam();
	}

	/** 成長促進スチームが有効かを返す。 */
	public static boolean isAgeBoostSteam() {
		return source().isAgeBoostSteam();
	}

	/** 成長停止スチームが有効かを返す。 */
	public static boolean isAgeStopSteam() {
		return source().isAgeStopSteam();
	}

	/** 解毒スチームが有効かを返す。 */
	public static boolean isAntidosSteam() {
		return source().isAntidosSteam();
	}

	/** 毒スチームが有効かを返す。 */
	public static boolean isPoisonSteam() {
		return source().isPoisonSteam();
	}

	/** 捕食者スチームが有効かを返す。 */
	public static boolean isPredatorSteam() {
		return source().isPredatorSteam();
	}

	/** 砂糖スチームが有効かを返す。 */
	public static boolean isSugerSteam() {
		return source().isSugerSteam();
	}

	/** 不眠スチームが有効かを返す。 */
	public static boolean isNoSleepSteam() {
		return source().isNoSleepSteam();
	}

	/** ハイブリッドスチームが有効かを返す。 */
	public static boolean isHybridSteam() {
		return source().isHybridSteam();
	}

	/** 妊娠促進スチームが有効かを返す。 */
	public static boolean isRapidPregnantSteam() {
		return source().isRapidPregnantSteam();
	}

	/** 非ゆっくり病予防スチームが有効かを返す。 */
	public static boolean isAntiNonYukkuriDiseaseSteam() {
		return source().isAntiNonYukkuriDiseaseSteam();
	}

	/** エンドレスふりふりスチームが有効かを返す。 */
	public static boolean isEndlessFurifuriSteam() {
		return source().isEndlessFurifuriSteam();
	}

	/** 現在のティックカウントを返す。 */
	public static int getTick() {
		return source().getTick();
	}

	/** アラームをセットする。 */
	public static void setAlarm() {
		source().setAlarm();
	}

	/** アラーム状態を返す。 */
	public static boolean getAlarm() {
		return source().getAlarm();
	}

	public static Terrarium.DayState getDayState() {
		return source().getDayState();
	}

	/** テラリウム環境をリセットする。 */
	public static void resetTerrariumEnvironment() {
		source().resetTerrariumEnvironment();
	}

	/** インターバル値を返す。 */
	public static int getInterval() {
		return source().getInterval();
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(EnvironmentSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}

	private static EnvironmentSource source() {
		return override != null ? override : DEFAULT;
	}
}
