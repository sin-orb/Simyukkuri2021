package src.draw;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;

class TranslateTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f, 0.5f, 0.25f});
        Translate.createTransTable(true);
    }

    // --- setMapSize / getters ---

    @Test
    void testSetMapSizeWidth() {
        assertEquals(1001, Translate.getMapW());
    }

    @Test
    void testSetMapSizeHeight() {
        assertEquals(1001, Translate.getMapH());
    }

    @Test
    void testSetMapSizeDepth() {
        assertEquals(501, Translate.getMapZ());
    }

    // --- flyLimit ---

    @Test
    void testGetFlyLimit() {
        assertEquals(0.175f, Translate.getFlyLimit());
    }

    @Test
    void testGetFlyHeightLimit() {
        assertEquals((int)(501 * 0.175f), Translate.getFlyHeightLimit());
    }

    // --- distance ---

    @Test
    void testDistanceKnownValues() {
        assertEquals(25, Translate.distance(0, 0, 3, 4));
    }

    @Test
    void testDistanceSamePoint() {
        assertEquals(0, Translate.distance(5, 5, 5, 5));
    }

    // --- getRealDistance ---

    @Test
    void testGetRealDistanceKnownValues() {
        assertEquals(5, Translate.getRealDistance(0, 0, 3, 4));
    }

    @Test
    void testGetRealDistanceSamePoint() {
        assertEquals(0, Translate.getRealDistance(7, 7, 7, 7));
    }

    // --- getRadian ---

    @Test
    void testGetRadianBasic() {
        // angle from (0,0) to (1,0) should be 0 radians
        double radian = Translate.getRadian(0, 0, 1, 0);
        assertEquals(0.0, radian, 0.0001);
    }

    @Test
    void testGetRadianUp() {
        // angle from (0,0) to (0,1) should be PI/2
        double radian = Translate.getRadian(0, 0, 0, 1);
        assertEquals(Math.PI / 2, radian, 0.0001);
    }

    // --- getPointByDistAndRad ---

    @Test
    void testGetPointByDistAndRadRight() {
        Point4y p = Translate.getPointByDistAndRad(0, 0, 10, 0.0);
        assertEquals(10, p.getX());
        assertEquals(0, p.getY());
    }

    @Test
    void testGetPointByDistAndRadDown() {
        Point4y p = Translate.getPointByDistAndRad(0, 0, 10, Math.PI / 2);
        assertEquals(0, p.getX(), 1);
        assertEquals(10, p.getY(), 1);
    }

    // --- transSize ---

    @Test
    void testTransSizeReturnsSameValue() {
        assertEquals(42, Translate.transSize(42));
        assertEquals(0, Translate.transSize(0));
        assertEquals(-5, Translate.transSize(-5));
    }

    // --- mapScale ---

    @Test
    void testSetMapScaleGetMapScale() {
        Translate.setMapScale(200);
        assertEquals(200, Translate.getMapScale());
    }

    // --- translate ---

    @Test
    void testTranslateValidCoordinates() {
        Point4y pos = new Point4y();
        Translate.translate(500, 500, pos);
        // After translation, pos should have valid field coordinates
        assertTrue(pos.getX() >= 0);
        assertTrue(pos.getY() >= 0);
    }

    @Test
    void testTranslateOrigin() {
        Point4y pos = new Point4y();
        Translate.translate(0, 0, pos);
        assertTrue(pos.getX() >= 0);
        assertTrue(pos.getY() >= 0);
    }

    // --- translateZ ---

    @Test
    void testTranslateZBasic() {
        int result = Translate.translateZ(100);
        // rateZ = fieldH / mapZ = 600 / 501
        assertTrue(result >= 0);
    }

    @Test
    void testTranslateZZero() {
        assertEquals(0, Translate.translateZ(0));
    }

    // --- invertY ---

    @Test
    void testInvertYBasic() {
        int result = Translate.invertY(300);
        // result = (int)(300 * 1001 / 600)
        assertEquals((int)(300.0f * 1001.0f / 600.0f), result);
    }

    // --- invertZ ---

    @Test
    void testInvertZBasic() {
        int result = Translate.invertZ(300);
        // result = (int)(300 * 501 / 600)
        assertEquals((int)(300.0f * 501.0f / 600.0f), result);
    }

    // --- invertBgY ---

    @Test
    void testInvertBgYBasic() {
        int result = Translate.invertBgY(300);
        // result = (int)(300 * 1001 / 600 * 0.7)
        assertEquals((int)(300.0f * 1001.0f / 600.0f * 0.7f), result);
    }

    // --- invertX ---

    @Test
    void testInvertXBasicWithClamping() {
        // invertX clamps mapY to [0, mapH-1]
        int result = Translate.invertX(100, 500);
        assertTrue(result >= 0);
    }

    @Test
    void testInvertXNegativeMapYClamped() {
        int result = Translate.invertX(100, -5);
        // mapY clamped to 0
        int expected = Translate.invertX(100, 0);
        assertEquals(expected, result);
    }

    @Test
    void testInvertXOverflowMapYClamped() {
        int result = Translate.invertX(100, 99999);
        // mapY clamped to mapH - 1
        int expected = Translate.invertX(100, Translate.getMapH() - 1);
        assertEquals(expected, result);
    }

    // --- invert ---

    @Test
    void testInvertReturnsNullForNegativeY() {
        assertNull(Translate.invert(100, -1));
    }

    @Test
    void testInvertReturnsNullForYGreaterThanOrEqualFieldH() {
        assertNull(Translate.invert(100, Translate.getFieldH()));
    }

    @Test
    void testInvertReturnsNullForWallArea() {
        // y=0 is in the wall area, fieldToMapY[0] should be -1
        assertNull(Translate.invert(100, 0));
    }

    // --- invertLimit ---

    @Test
    void testInvertLimitClampsCoordinates() {
        Point4y result = Translate.invertLimit(-100, -100);
        assertNotNull(result);
        assertTrue(result.getX() >= 0);
        assertTrue(result.getY() >= 0);
    }

    @Test
    void testInvertLimitLargeCoordinates() {
        Point4y result = Translate.invertLimit(99999, 99999);
        assertNotNull(result);
        assertTrue(result.getX() <= Translate.getMapW());
        assertTrue(result.getY() <= Translate.getMapH());
    }

    // --- inInvertLimit ---

    @Test
    void testInInvertLimitReturnsFalseForWallArea() {
        // y=0 is wall area
        assertFalse(Translate.inInvertLimit(400, 0));
    }

    @Test
    void testInInvertLimitReturnsTrueForFloorArea() {
        // A point well within the floor area
        int floorY = Translate.getFieldH() - 1;
        // center X should be in field
        assertTrue(Translate.inInvertLimit(Translate.getFieldW() / 2, floorY));
    }

    // --- addZoomRate ---

    @Test
    void testAddZoomRateWithinBounds() {
        // zoomRate starts at 0, table has 3 entries
        assertTrue(Translate.addZoomRate(1));
    }

    @Test
    void testAddZoomRateBelowZeroClampsAndReturnsFalse() {
        // zoomRate is 0, subtracting should clamp to 0
        assertFalse(Translate.addZoomRate(-1));
    }

    @Test
    void testAddZoomRateAboveMaxClampsAndReturnsFalse() {
        // zoomTable has 3 entries (indices 0,1,2), go to max
        Translate.addZoomRate(1); // now 1
        Translate.addZoomRate(1); // now 2 (max)
        assertFalse(Translate.addZoomRate(1)); // tries 3, clamped to 2
    }

    // --- setZoomRate ---

    @Test
    void testSetZoomRateClampsNegativeToZero() {
        Translate.setZoomRate(-5);
        assertEquals(1.0f, Translate.getCurrentZoomRate()); // index 0 -> 1.0f
    }

    @Test
    void testSetZoomRateClampsAboveMax() {
        Translate.setZoomRate(100);
        assertEquals(0.25f, Translate.getCurrentZoomRate()); // index 2 (max) -> 0.25f
    }

    // --- getCurrentZoomRate ---

    @Test
    void testGetCurrentZoomRateDefault() {
        // After setUp, zoomRate should be 0
        assertEquals(1.0f, Translate.getCurrentZoomRate());
    }

    @Test
    void testGetCurrentZoomRateAfterSet() {
        Translate.setZoomRate(1);
        assertEquals(0.5f, Translate.getCurrentZoomRate());
    }

    // --- buffer positioning ---

    @Test
    void testSetBufferPos() {
        Translate.setBufferPos(10, 10);
        Rectangle4y area = Translate.getDisplayArea();
        assertNotNull(area);
        assertTrue(area.getX() >= 0);
        assertTrue(area.getY() >= 0);
    }

    @Test
    void testAddBufferPos() {
        Translate.setBufferPos(0, 0);
        Translate.addBufferPos(5, 5);
        Rectangle4y area = Translate.getDisplayArea();
        assertNotNull(area);
        assertTrue(area.getX() >= 0);
        assertTrue(area.getY() >= 0);
    }

    @Test
    void testSetBufferCenterPos() {
        Translate.setBufferCenterPos(400, 300);
        Rectangle4y area = Translate.getDisplayArea();
        assertNotNull(area);
        assertTrue(area.getX() >= 0);
        assertTrue(area.getY() >= 0);
    }

    // --- getDisplayArea ---

    @Test
    void testGetDisplayAreaNonNull() {
        assertNotNull(Translate.getDisplayArea());
    }

    // --- field/buffer/canvas sizes ---

    @Test
    void testGetFieldW() {
        // fieldW = canvasW * fieldSize / 100 = 800 * 100 / 100 = 800
        assertEquals(800, Translate.getFieldW());
    }

    @Test
    void testGetFieldH() {
        // fieldH = canvasH * fieldSize / 100 = 600 * 100 / 100 = 600
        assertEquals(600, Translate.getFieldH());
    }

    @Test
    void testGetBufferW() {
        // bufferW = canvasW * bufSize / 100 = 800 * 100 / 100 = 800
        assertEquals(800, Translate.getBufferW());
    }

    @Test
    void testGetBufferH() {
        // bufferH = canvasH * bufSize / 100 = 600 * 100 / 100 = 600
        assertEquals(600, Translate.getBufferH());
    }

    @Test
    void testGetCanvasW() {
        assertEquals(800, Translate.getCanvasW());
    }

    @Test
    void testGetCanvasH() {
        assertEquals(600, Translate.getCanvasH());
    }
}
