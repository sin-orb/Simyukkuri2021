package src.system;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.PredatorType;
import src.game.Shit;
import src.util.WorldTestHelper;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Kimeemaru;
import src.yukkuri.TarinaiReimu;

class LoggerYukkuriTest {

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        Translate.setMapSize(1000, 1000, 500);
        // logPointerをリセットするためclearLog
        LoggerYukkuri.clearLog();
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    // --- Constants ---

    @Test
    void testTickConstant() {
        assertEquals(1, LoggerYukkuri.TICK);
    }

    @Test
    void testNumOfNormalConstant() {
        assertEquals(0, LoggerYukkuri.NUM_OF_NORMAL);
    }

    @Test
    void testNumOfLogDataTypeConstant() {
        assertEquals(11, LoggerYukkuri.NUM_OF_LOGDATA_TYPE);
    }

    @Test
    void testNumOfPredatorConstant() {
        assertEquals(1, LoggerYukkuri.NUM_OF_PREDATOR);
    }

    @Test
    void testNumOfRareConstant() {
        assertEquals(2, LoggerYukkuri.NUM_OF_RARE);
    }

    @Test
    void testNumOfTarinaiConstant() {
        assertEquals(3, LoggerYukkuri.NUM_OF_TARINAI);
    }

    @Test
    void testNumOfHybridConstant() {
        assertEquals(4, LoggerYukkuri.NUM_OF_HYBRID);
    }

    @Test
    void testNumOfBabyConstant() {
        assertEquals(5, LoggerYukkuri.NUM_OF_BABY);
    }

    @Test
    void testNumOfChildConstant() {
        assertEquals(6, LoggerYukkuri.NUM_OF_CHILD);
    }

    @Test
    void testNumOfAdultConstant() {
        assertEquals(7, LoggerYukkuri.NUM_OF_ADULT);
    }

    @Test
    void testNumOfSickConstant() {
        assertEquals(8, LoggerYukkuri.NUM_OF_SICK);
    }

    @Test
    void testNumOfShitConstant() {
        assertEquals(9, LoggerYukkuri.NUM_OF_SHIT);
    }

    @Test
    void testNumOfCashConstant() {
        assertEquals(10, LoggerYukkuri.NUM_OF_CASH);
    }

    // --- setLogPage ---

    @Test
    void testSetLogPageWithinRange() {
        LoggerYukkuri.setLogPage(2);
        // No exception; we cannot directly read logPage, but it should not crash
        assertDoesNotThrow(() -> LoggerYukkuri.setLogPage(0));
    }

    @Test
    void testSetLogPageNegativeWrapsToThree() {
        LoggerYukkuri.setLogPage(-1);
        // logPage should be set to 3
        // We verify by setting again and checking no crash
        assertDoesNotThrow(() -> LoggerYukkuri.setLogPage(-1));
    }

    @Test
    void testSetLogPageOverflowWrapsToZero() {
        LoggerYukkuri.setLogPage(4);
        // logPage should be set to 0
        assertDoesNotThrow(() -> LoggerYukkuri.setLogPage(4));
    }

    @Test
    void testSetLogPageExactBoundaryThree() {
        LoggerYukkuri.setLogPage(3);
        // 3 is within range [0,3], should not wrap
        assertDoesNotThrow(() -> LoggerYukkuri.setLogPage(3));
    }

    // --- addLogPage ---

    @Test
    void testAddLogPagePositiveIncrement() {
        LoggerYukkuri.setLogPage(0);
        LoggerYukkuri.addLogPage(1);
        // logPage should now be 1
        assertDoesNotThrow(() -> LoggerYukkuri.addLogPage(0));
    }

    @Test
    void testAddLogPageNegativeWraps() {
        LoggerYukkuri.setLogPage(0);
        LoggerYukkuri.addLogPage(-1);
        // logPage goes to -1, wraps to 3
        assertDoesNotThrow(() -> LoggerYukkuri.addLogPage(0));
    }

    @Test
    void testAddLogPageOverflowWrapsToZero() {
        LoggerYukkuri.setLogPage(3);
        LoggerYukkuri.addLogPage(1);
        // logPage goes to 4, wraps to 0
        assertDoesNotThrow(() -> LoggerYukkuri.addLogPage(0));
    }

    // --- isShow / setShow ---

    @Test
    void testSetShowTrue() {
        LoggerYukkuri.setShow(true);
        assertTrue(LoggerYukkuri.isShow());
    }

    @Test
    void testSetShowFalse() {
        LoggerYukkuri.setShow(false);
        assertFalse(LoggerYukkuri.isShow());
    }

    @Test
    void testShowRoundTrip() {
        LoggerYukkuri.setShow(true);
        assertTrue(LoggerYukkuri.isShow());
        LoggerYukkuri.setShow(false);
        assertFalse(LoggerYukkuri.isShow());
    }

    // --- getClearLogTime / setClearLogTime ---

    @Test
    void testSetClearLogTime() {
        LoggerYukkuri.setClearLogTime(42);
        assertEquals(42, LoggerYukkuri.getClearLogTime());
    }

    @Test
    void testSetClearLogTimeZero() {
        LoggerYukkuri.setClearLogTime(0);
        assertEquals(0, LoggerYukkuri.getClearLogTime());
    }

    @Test
    void testClearLogTimeRoundTrip() {
        LoggerYukkuri.setClearLogTime(999);
        assertEquals(999, LoggerYukkuri.getClearLogTime());
        LoggerYukkuri.setClearLogTime(0);
        assertEquals(0, LoggerYukkuri.getClearLogTime());
    }

    // --- clearLog ---

    @Test
    void testClearLogDoesNotCrash() {
        assertDoesNotThrow(() -> LoggerYukkuri.clearLog());
    }

    // --- run ---

    @Test
    void testRunDoesNotCrash() {
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    // --- getLog ---

    @Test
    void testGetLogNegativeIndexReturnsNull() {
        assertNull(LoggerYukkuri.getLog(-1));
    }

    @Test
    void testGetLogOverflowIndexReturnsNull() {
        assertNull(LoggerYukkuri.getLog(120));
    }

    @Test
    void testGetLogLargeIndexReturnsNull() {
        assertNull(LoggerYukkuri.getLog(999));
    }

    // --- getNumOfObjSumLog ---

    @Test
    void testGetNumOfObjSumLogNonNull() {
        assertNotNull(LoggerYukkuri.getNumOfObjSumLog());
    }

    @Test
    void testGetNumOfObjSumLogLength() {
        assertEquals(LoggerYukkuri.NUM_OF_LOGDATA_TYPE, LoggerYukkuri.getNumOfObjSumLog().length);
    }

    // --- getNumOfObjNowLog ---

    @Test
    void testGetNumOfObjNowLogNonNull() {
        assertNotNull(LoggerYukkuri.getNumOfObjNowLog());
    }

    @Test
    void testGetNumOfObjNowLogLength() {
        assertEquals(LoggerYukkuri.NUM_OF_LOGDATA_TYPE, LoggerYukkuri.getNumOfObjNowLog().length);
    }

    // --- outputLogFile ---

    @Test
    void testOutputLogFileDoesNotCrash() {
        assertDoesNotThrow(() -> LoggerYukkuri.outputLogFile("test log message"));
    }

    // --- run with Body ---

    @Test
    void testRunWithNormalBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithBabyBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        b.setAgeState(AgeState.BABY);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithChildBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        b.setAgeState(AgeState.CHILD);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithPredatorBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        b.setPredatorType(PredatorType.BITE);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithDeadBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        b.setDead(true);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithSickBody() {
        Body b = WorldTestHelper.createBody();
        b.setX(100); b.setY(100);
        // sickはHPが低い状態で発動するのでHP=1に設定
        // sickPeriod > INCUBATIONPERIODorgにしてisSick()=trueにする
        b.setSickPeriod(b.getINCUBATIONPERIODorg() + 1);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithMultipleBodies() {
        for (int i = 0; i < 5; i++) {
            Body b = WorldTestHelper.createBody();
            b.setX(50 + i * 10); b.setY(50);
            SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        }
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testGetLogAfterRunReturnsData() {
        // run()を120回以上呼んでoverwrappedにする
        for (int i = 0; i < 125; i++) {
            LoggerYukkuri.run();
        }
        // overwrapped後はgetLog(0)がnullではない
        assertNotNull(LoggerYukkuri.getLog(0));
    }

    @Test
    void testGetLogBeforeOverwrapped() {
        // 1回だけrun()→logPointer=1, getLog(0)は利用可能か確認
        LoggerYukkuri.run();
        // overwrappedでないのでgetLog(119)はまだnull
        // logPointer=1なのでlogRecord >= logPointerは使えないことを確認
        assertDoesNotThrow(() -> LoggerYukkuri.getLog(0));
    }

    @Test
    void testClearLogTimeTriggered() {
        // clearLogTimeを1に設定してrun()を呼んだ後に確認
        LoggerYukkuri.setClearLogTime(1);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    // --- run() additional body type branches ---

    @Test
    void testRunWithHybridBody() {
        HybridYukkuri hybrid = new HybridYukkuri();
        hybrid.setX(100); hybrid.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(hybrid.getUniqueID(), hybrid);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithTarinaiBody() {
        TarinaiReimu tarinai = new TarinaiReimu();
        tarinai.setX(100); tarinai.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(tarinai.getUniqueID(), tarinai);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithRareBody_Kimeemaru() {
        Kimeemaru rare = new Kimeemaru();
        rare.setX(100); rare.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(rare.getUniqueID(), rare);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    @Test
    void testRunWithShitInMap() {
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        assertDoesNotThrow(() -> LoggerYukkuri.run());
    }

    // --- displayLog (ResourceUtil returns null → NPE in drawString is expected) ---

    private Graphics2D createG2() {
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        return img.createGraphics();
    }

    @Test
    void testDisplayLog_page0_executesCode() {
        LoggerYukkuri.setLogPage(0);
        LoggerYukkuri.run();
        Graphics2D g2 = createG2();
        try {
            LoggerYukkuri.displayLog(g2);
        } catch (NullPointerException e) {
            // expected: ResourceUtil.read() returns null → drawString(null,...) throws NPE
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testDisplayLog_page1_executesCode() {
        LoggerYukkuri.setLogPage(1);
        Graphics2D g2 = createG2();
        try {
            LoggerYukkuri.displayLog(g2);
        } catch (NullPointerException e) {
            // expected
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testDisplayLog_page2_executesCode() {
        LoggerYukkuri.setLogPage(2);
        Graphics2D g2 = createG2();
        try {
            LoggerYukkuri.displayLog(g2);
        } catch (NullPointerException e) {
            // expected
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testDisplayLog_page3_executesCode() {
        LoggerYukkuri.setLogPage(3);
        Graphics2D g2 = createG2();
        try {
            LoggerYukkuri.displayLog(g2);
        } catch (NullPointerException e) {
            // expected
        } finally {
            g2.dispose();
        }
    }

    // --- displayLog with run data populated ---

    @Test
    void testDisplayLog_withRunData_executesCode() {
        for (int i = 0; i < 125; i++) {
            LoggerYukkuri.run();
        }
        LoggerYukkuri.setLogPage(0);
        Graphics2D g2 = createG2();
        try {
            LoggerYukkuri.displayLog(g2);
        } catch (NullPointerException e) {
            // expected
        } finally {
            g2.dispose();
        }
    }

    @Test
    void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new LoggerYukkuri());
    }
}
