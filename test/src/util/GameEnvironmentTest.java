package src.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.draw.Terrarium;

public class GameEnvironmentTest {

	@AfterEach
	public void tearDown() {
		GameEnvironment.clearOverride();
	}

	@Test
	public void testEnvironmentAccessUsesOverrideWhenSet() {
		RecordingEnvironmentSource source = new RecordingEnvironmentSource();
		GameEnvironment.setOverride(source);

		GameEnvironment.setAlarm();
		GameEnvironment.resetTerrariumEnvironment();

		assertEquals(12, GameEnvironment.getOperationTime());
		assertEquals(34, GameEnvironment.getDayTime());
		assertEquals(56, GameEnvironment.getNightTime());
		assertEquals(78, GameEnvironment.getTick());
		assertEquals(90, GameEnvironment.getInterval());
		assertEquals(Terrarium.DayState.NIGHT, GameEnvironment.getDayState());
		assertTrue(GameEnvironment.isHumid());
		assertTrue(GameEnvironment.isPoisonSteam());
		assertTrue(GameEnvironment.getAlarm());
		assertTrue(source.alarmSet);
		assertTrue(source.reset);
	}

	private static class RecordingEnvironmentSource implements EnvironmentSource {
		private boolean alarmSet;
		private boolean reset;

		@Override
		public int getOperationTime() {
			return 12;
		}

		@Override
		public int getDayTime() {
			return 34;
		}

		@Override
		public int getNightTime() {
			return 56;
		}

		@Override
		public boolean isHumid() {
			return true;
		}

		@Override
		public boolean isAntifungalSteam() {
			return false;
		}

		@Override
		public boolean isOrangeSteam() {
			return false;
		}

		@Override
		public boolean isAgeBoostSteam() {
			return false;
		}

		@Override
		public boolean isAgeStopSteam() {
			return false;
		}

		@Override
		public boolean isAntidosSteam() {
			return false;
		}

		@Override
		public boolean isPoisonSteam() {
			return true;
		}

		@Override
		public boolean isPredatorSteam() {
			return false;
		}

		@Override
		public boolean isSugerSteam() {
			return false;
		}

		@Override
		public boolean isNoSleepSteam() {
			return false;
		}

		@Override
		public boolean isHybridSteam() {
			return false;
		}

		@Override
		public boolean isRapidPregnantSteam() {
			return false;
		}

		@Override
		public boolean isAntiNonYukkuriDiseaseSteam() {
			return false;
		}

		@Override
		public boolean isEndlessFurifuriSteam() {
			return false;
		}

		@Override
		public int getTick() {
			return 78;
		}

		@Override
		public void setAlarm() {
			alarmSet = true;
		}

		@Override
		public boolean getAlarm() {
			return true;
		}

		@Override
		public Terrarium.DayState getDayState() {
			return Terrarium.DayState.NIGHT;
		}

		@Override
		public void resetTerrariumEnvironment() {
			reset = true;
		}

		@Override
		public int getInterval() {
			return 90;
		}
	}
}
