package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.HybridYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Kimeemaru;
import org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.WorldTestHelper;

class LoggerYukkuriTest {

	@BeforeEach
	void setUp() throws ReflectiveOperationException {
		WorldTestHelper.resetWorld();
		WorldTestHelper.initializeMinimalWorld();
		WorldTestHelper.initializeStandardTranslate500();
		resetLoggerState();
	}

	@AfterEach
	void tearDown() {
		removeHandlers();
		WorldTestHelper.resetWorld();
	}

	@Test
	void testSetLogPageWrapsWithinValidRange() throws ReflectiveOperationException {
		LoggerYukkuri.setLogPage(2);
		assertEquals(2, getIntField("logPage"));

		LoggerYukkuri.setLogPage(-1);
		assertEquals(3, getIntField("logPage"));

		LoggerYukkuri.setLogPage(4);
		assertEquals(0, getIntField("logPage"));
	}

	@Test
	void testAddLogPageWrapsRelativeToCurrentPage() throws ReflectiveOperationException {
		LoggerYukkuri.setLogPage(0);
		LoggerYukkuri.addLogPage(-1);
		assertEquals(3, getIntField("logPage"));

		LoggerYukkuri.addLogPage(1);
		assertEquals(0, getIntField("logPage"));

		LoggerYukkuri.addLogPage(4);
		assertEquals(0, getIntField("logPage"));
	}

	@Test
	void testSetShowStoresFlag() {
		LoggerYukkuri.setShow(true);
		assertTrue(LoggerYukkuri.isShow());

		LoggerYukkuri.setShow(false);
		assertFalse(LoggerYukkuri.isShow());
	}

	@Test
	void testSetClearLogTimeStoresValue() {
		LoggerYukkuri.setClearLogTime(42);
		assertEquals(42, LoggerYukkuri.getClearLogTime());

		LoggerYukkuri.setClearLogTime(0);
		assertEquals(0, LoggerYukkuri.getClearLogTime());
	}

	@Test
	void testOutputLogFileInstallsSingleFileHandler() {
		removeHandlers();

		LoggerYukkuri.outputLogFile("first");
		assertEquals(1, LoggerYukkuri.logger.getHandlers().length);
		assertTrue(LoggerYukkuri.logger.getHandlers()[0] instanceof FileHandler);

		LoggerYukkuri.outputLogFile("second");
		assertEquals(1, LoggerYukkuri.logger.getHandlers().length);
	}

	@Test
	void testRunCountsCategoriesAgesSickShitAndCash() {
		GameWorld.get().getPlayer().setCash(4321);

		Yukkuri normalBaby = WorldTestHelper.createBody();
		normalBaby.setAgeState(AgeState.BABY);
		normalBaby.setX(100);
		normalBaby.setY(100);
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(normalBaby.getUniqueId(), normalBaby);

		Yukkuri predatorChild = WorldTestHelper.createBody();
		predatorChild.setAgeState(AgeState.CHILD);
		predatorChild.setPredatorType(PredatorType.BITE);
		predatorChild.setX(110);
		predatorChild.setY(100);
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(predatorChild.getUniqueId(), predatorChild);

		HybridYukkuri hybridAdult = new HybridYukkuri();
		hybridAdult.setAgeState(AgeState.ADULT);
		hybridAdult.setX(120);
		hybridAdult.setY(100);
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(hybridAdult.getUniqueId(), hybridAdult);

		TarinaiReimu tarinaiAdult = new TarinaiReimu();
		tarinaiAdult.setAgeState(AgeState.ADULT);
		tarinaiAdult.setX(130);
		tarinaiAdult.setY(100);
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(tarinaiAdult.getUniqueId(), tarinaiAdult);

		Kimeemaru rareAdult = new Kimeemaru();
		rareAdult.setAgeState(AgeState.ADULT);
		rareAdult.setSickPeriod(rareAdult.getIncubationPeriodBase() + 1);
		rareAdult.setX(140);
		rareAdult.setY(100);
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(rareAdult.getUniqueId(), rareAdult);

		Shit shit = new Shit();
		GameWorld.get().getCurrentWorldState().getShit().put(shit.getObjId(), shit);

		LoggerYukkuri.run();

		long[] current = LoggerYukkuri.getNumOfObjNowLog();
		long[] sum = LoggerYukkuri.getNumOfObjSumLog();

		long[] expected = new long[LoggerYukkuri.NUM_OF_LOGDATA_TYPE];
		expected[LoggerYukkuri.NUM_OF_NORMAL] = 1;
		expected[LoggerYukkuri.NUM_OF_PREDATOR] = 1;
		expected[LoggerYukkuri.NUM_OF_RARE] = 1;
		expected[LoggerYukkuri.NUM_OF_TARINAI] = 1;
		expected[LoggerYukkuri.NUM_OF_HYBRID] = 1;
		expected[LoggerYukkuri.NUM_OF_BABY] = 1;
		expected[LoggerYukkuri.NUM_OF_CHILD] = 1;
		expected[LoggerYukkuri.NUM_OF_ADULT] = 3;
		expected[LoggerYukkuri.NUM_OF_SICK] = 1;
		expected[LoggerYukkuri.NUM_OF_SHIT] = 1;
		expected[LoggerYukkuri.NUM_OF_CASH] = 4321;

		assertArrayEquals(expected, current);
		assertArrayEquals(expected, sum);
	}

	@Test
	void testGetLogRingBufferWrapsAfterOverwrap() throws ReflectiveOperationException {
		for (int i = 0; i < 121; i++) {
			LoggerYukkuri.run();
		}

		assertNotNull(LoggerYukkuri.getLog(0));
		assertNull(LoggerYukkuri.getLog(-1));
		assertNull(LoggerYukkuri.getLog(120));
		assertTrue(getBooleanField("overwrapped"));
		assertEquals(121, getIntField("logPointer"));
	}

	@Test
	void testDisplayLogDoesNotMutateLoggerState() throws ReflectiveOperationException {
		LoggerYukkuri.run();
		int beforePointer = getIntField("logPointer");
		long[] beforeNowLog = Arrays.copyOf(LoggerYukkuri.getNumOfObjNowLog(), LoggerYukkuri.NUM_OF_LOGDATA_TYPE);
		long[] beforeSumLog = Arrays.copyOf(LoggerYukkuri.getNumOfObjSumLog(), LoggerYukkuri.NUM_OF_LOGDATA_TYPE);

		BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		try {
			LoggerYukkuri.displayLog(g2);
		} finally {
			g2.dispose();
		}

		assertEquals(beforePointer, getIntField("logPointer"));
		assertArrayEquals(beforeNowLog, LoggerYukkuri.getNumOfObjNowLog());
		assertArrayEquals(beforeSumLog, LoggerYukkuri.getNumOfObjSumLog());
	}

	private static void resetLoggerState() throws ReflectiveOperationException {
		setIntField("logPage", 0);
		setIntField("logPointer", 0);
		setBooleanField("overwrapped", false);
		setBooleanField("show", false);
		setIntField("clearLogTime", 0);
		clearLongArray("prevLogData");
		clearLongArray("logDataSum");
		clearLongMatrix("logList");
		removeHandlers();
	}

	private static void removeHandlers() {
		for (Handler handler : LoggerYukkuri.logger.getHandlers()) {
			LoggerYukkuri.logger.removeHandler(handler);
			handler.close();
		}
	}

	private static int getIntField(String name) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(null);
	}

	private static boolean getBooleanField(String name) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		return field.getBoolean(null);
	}

	private static void setIntField(String name, int value) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		field.setInt(null, value);
	}

	private static void setBooleanField(String name, boolean value) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		field.setBoolean(null, value);
	}

	private static void clearLongArray(String name) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		long[] values = (long[]) field.get(null);
		Arrays.fill(values, 0L);
	}

	private static void clearLongMatrix(String name) throws ReflectiveOperationException {
		Field field = LoggerYukkuri.class.getDeclaredField(name);
		field.setAccessible(true);
		long[][] values = (long[][]) field.get(null);
		for (long[] row : values) {
			Arrays.fill(row, 0L);
		}
	}
}
