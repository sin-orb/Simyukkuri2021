package src.util;

import src.draw.Terrarium;

public final class GameEnvironment {
	private static final EnvironmentSource DEFAULT = new EnvironmentSource() {
		@Override
		public int getOperationTime() {
			return Terrarium.getOperationTime();
		}

		@Override
		public int getDayTime() {
			return Terrarium.getDayTime();
		}

		@Override
		public int getNightTime() {
			return Terrarium.getNightTime();
		}

		@Override
		public boolean isHumid() {
			return Terrarium.isHumid();
		}

		@Override
		public boolean isAntifungalSteam() {
			return Terrarium.isAntifungalSteam();
		}

		@Override
		public boolean isOrangeSteam() {
			return Terrarium.isOrangeSteam();
		}

		@Override
		public boolean isAgeBoostSteam() {
			return Terrarium.isAgeBoostSteam();
		}

		@Override
		public boolean isAgeStopSteam() {
			return Terrarium.isAgeStopSteam();
		}

		@Override
		public boolean isAntidosSteam() {
			return Terrarium.isAntidosSteam();
		}

		@Override
		public boolean isPoisonSteam() {
			return Terrarium.isPoisonSteam();
		}

		@Override
		public boolean isPredatorSteam() {
			return Terrarium.isPredatorSteam();
		}

		@Override
		public boolean isSugerSteam() {
			return Terrarium.isSugerSteam();
		}

		@Override
		public boolean isNoSleepSteam() {
			return Terrarium.isNoSleepSteam();
		}

		@Override
		public boolean isHybridSteam() {
			return Terrarium.isHybridSteam();
		}

		@Override
		public boolean isRapidPregnantSteam() {
			return Terrarium.isRapidPregnantSteam();
		}

		@Override
		public boolean isAntiNonYukkuriDiseaseSteam() {
			return Terrarium.isAntiNonYukkuriDiseaseSteam();
		}

		@Override
		public boolean isEndlessFurifuriSteam() {
			return Terrarium.isEndlessFurifuriSteam();
		}

		@Override
		public int getTick() {
			return Terrarium.getTick();
		}

		@Override
		public void setAlarm() {
			Terrarium.setAlarm();
		}

		@Override
		public boolean getAlarm() {
			return Terrarium.getAlarm();
		}

		@Override
		public Terrarium.DayState getDayState() {
			return Terrarium.getDayState();
		}

		@Override
		public void resetTerrariumEnvironment() {
			Terrarium.resetTerrariumEnvironment();
		}

		@Override
		public int getInterval() {
			return Terrarium.getInterval();
		}
	};

	private static EnvironmentSource override;

	private GameEnvironment() {
	}

	public static int getOperationTime() {
		return source().getOperationTime();
	}

	public static int getDayTime() {
		return source().getDayTime();
	}

	public static int getNightTime() {
		return source().getNightTime();
	}

	public static boolean isHumid() {
		return source().isHumid();
	}

	public static boolean isAntifungalSteam() {
		return source().isAntifungalSteam();
	}

	public static boolean isOrangeSteam() {
		return source().isOrangeSteam();
	}

	public static boolean isAgeBoostSteam() {
		return source().isAgeBoostSteam();
	}

	public static boolean isAgeStopSteam() {
		return source().isAgeStopSteam();
	}

	public static boolean isAntidosSteam() {
		return source().isAntidosSteam();
	}

	public static boolean isPoisonSteam() {
		return source().isPoisonSteam();
	}

	public static boolean isPredatorSteam() {
		return source().isPredatorSteam();
	}

	public static boolean isSugerSteam() {
		return source().isSugerSteam();
	}

	public static boolean isNoSleepSteam() {
		return source().isNoSleepSteam();
	}

	public static boolean isHybridSteam() {
		return source().isHybridSteam();
	}

	public static boolean isRapidPregnantSteam() {
		return source().isRapidPregnantSteam();
	}

	public static boolean isAntiNonYukkuriDiseaseSteam() {
		return source().isAntiNonYukkuriDiseaseSteam();
	}

	public static boolean isEndlessFurifuriSteam() {
		return source().isEndlessFurifuriSteam();
	}

	public static int getTick() {
		return source().getTick();
	}

	public static void setAlarm() {
		source().setAlarm();
	}

	public static boolean getAlarm() {
		return source().getAlarm();
	}

	public static Terrarium.DayState getDayState() {
		return source().getDayState();
	}

	public static void resetTerrariumEnvironment() {
		source().resetTerrariumEnvironment();
	}

	public static int getInterval() {
		return source().getInterval();
	}

	public static void setOverride(EnvironmentSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}

	private static EnvironmentSource source() {
		return override != null ? override : DEFAULT;
	}
}
