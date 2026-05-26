package org.simyukkuri.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;

@ResourceLock("Translate")
class TranslateTest {

	@BeforeEach
	void setUp() {
		SimYukkuri.world = new World();
		Translate.setWorldSize(1000, 1000, 500);
		Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f, 0.5f, 0.25f });
		Translate.createTransTable(true);
	}

	@Test
	void testSetCanvasSizeDerivesFieldAndBufferDimensions() {
		Translate.setCanvasSize(900, 750, 80, 50, new float[] { 1.0f, 0.5f, 0.25f });
		Rectangle4y area = Translate.getDisplayArea();
		assertEquals(900, Translate.getCanvasW());
		assertEquals(750, Translate.getCanvasH());
		assertEquals(720, Translate.getFieldW());
		assertEquals(600, Translate.getFieldH());
		assertEquals(450, Translate.getBufferW());
		assertEquals(375, Translate.getBufferH());
		assertEquals(720, area.getWidth());
		assertEquals(600, area.getHeight());
		assertEquals(0, area.getX());
		assertEquals(0, area.getY());
	}

	@Test
	void testDistanceAndRealDistanceKnownValues() {
		assertEquals(25, Translate.distance(0, 0, 3, 4));
		assertEquals(25, Translate.distance(3, 4, 0, 0));
		assertEquals(0, Translate.distance(5, 5, 5, 5));
		assertEquals(5, Translate.getRealDistance(0, 0, 3, 4));
		assertEquals(5, Translate.getRealDistance(3, 4, 0, 0));
		assertEquals(0, Translate.getRealDistance(7, 7, 7, 7));
	}

	@Test
	void testGetRadianAndPointByDistAndRadRoundTrip() {
		Point4y right = Translate.getPointByDistAndRad(0, 0, 10, 0.0);
		assertEquals(10, right.getX());
		assertEquals(0, right.getY());
		assertEquals(0.0, Translate.getRadian(0, 0, right.getX(), right.getY()), 0.0001);

		Point4y down = Translate.getPointByDistAndRad(0, 0, 10, Math.PI / 2);
		assertEquals(0, down.getX(), 1);
		assertEquals(10, down.getY(), 1);
		assertEquals(Math.PI / 2, Translate.getRadian(0, 0, down.getX(), down.getY()), 0.0001);
	}

	@Test
	void testTransSizeReturnsSameValue() {
		assertEquals(42, Translate.transSize(42));
		assertEquals(0, Translate.transSize(0));
		assertEquals(-5, Translate.transSize(-5));
	}

	@Test
	void testTranslateRoundTripAndOrigin() {
		Point4y pos = new Point4y();
		Translate.translate(500, 500, pos);
		Point4y back = Translate.invertLimit(pos.getX(), pos.getY());
		Point4y roundTrip = new Point4y();
		Translate.translate(back.getX(), back.getY(), roundTrip);
		assertTrue(Math.abs(pos.getX() - roundTrip.getX()) <= 1);
		assertTrue(Math.abs(pos.getY() - roundTrip.getY()) <= 1);

		Point4y origin = new Point4y();
		Translate.translate(0, 0, origin);
		Point4y originBack = Translate.invertLimit(origin.getX(), origin.getY());
		assertEquals(0, originBack.getX());
		assertEquals(0, originBack.getY());
	}

	@Test
	void testVerticalTransformFormulas() {
		assertEquals((int) (100.0f * 600.0f / 501.0f), Translate.translateZ(100));
		assertEquals((int) (300.0f * 1001.0f / 600.0f), Translate.invertY(300));
		assertEquals((int) (300.0f * 501.0f / 600.0f), Translate.invertZ(300));
		assertEquals((int) (300.0f * 1001.0f / 600.0f * 0.7f), Translate.invertBgY(300));
	}

	@Test
	void testInvertRejectsInvalidPositions() {
		assertNull(Translate.invert(100, -1));
		assertNull(Translate.invert(100, Translate.getFieldH()));
		assertNull(Translate.invert(100, 0));
	}

	@Test
	void testInvertLimitClampsCoordinates() {
		Point4y lower = Translate.invertLimit(-100, -100);
		assertNotNull(lower);
		assertEquals(0, lower.getX());
		assertEquals(0, lower.getY());

		Point4y upper = Translate.invertLimit(99999, 99999);
		assertNotNull(upper);
		assertTrue(upper.getX() >= Translate.getWorldWidth() - 3);
		assertTrue(upper.getX() <= Translate.getWorldWidth() - 1);
		assertTrue(upper.getY() >= Translate.getWorldHeight() - 3);
		assertTrue(upper.getY() <= Translate.getWorldHeight() - 1);
	}

	@Test
	void testInInvertLimitDistinguishesWallAndFloor() {
		assertFalse(Translate.inInvertLimit(400, 0));
		int floorY = Translate.getFieldH() - 1;
		assertTrue(Translate.inInvertLimit(Translate.getFieldW() / 2, floorY));
	}

	@Test
	void testZoomRateClampsAndBufferZoomResizesDisplayArea() {
		Translate.setZoomRate(-5);
		Translate.setBufferZoom();
		assertEquals(1.0f, Translate.getCurrentZoomRate());
		assertEquals(800, Translate.getDisplayArea().getWidth());
		assertEquals(600, Translate.getDisplayArea().getHeight());

		Translate.setZoomRate(100);
		Translate.setBufferZoom();
		assertEquals(0.25f, Translate.getCurrentZoomRate());
		assertEquals(200, Translate.getDisplayArea().getWidth());
		assertEquals(150, Translate.getDisplayArea().getHeight());
	}

	@Test
	void testBufferPositionMethodsClampDisplayArea() {
		Translate.setZoomRate(1);
		Translate.setBufferZoom();
		Translate.setBufferPos(10, 20);
		assertEquals(10, Translate.getDisplayArea().getX());
		assertEquals(20, Translate.getDisplayArea().getY());

		Translate.setBufferCenterPos(400, 300);
		assertEquals(200, Translate.getDisplayArea().getX());
		assertEquals(150, Translate.getDisplayArea().getY());

		Translate.addBufferPos(5, -10);
		assertEquals(205, Translate.getDisplayArea().getX());
		assertEquals(140, Translate.getDisplayArea().getY());
	}
}
