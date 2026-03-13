package src.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.enums.AgeState;
import src.system.Sprite;
import src.system.BodyLayer;

/**
 * StubBody のカバレッジ向上テスト.
 *
 * StubBody は Body の最小具象実装で test/src/base/ に配置されている。
 * 既存のテストでは一部メソッド（getNameE, getNameE2, getImage,
 * isImageLoaded, getPivotX, getPivotY）が未カバーだったため追加する。
 */
public class StubBodyTest {

    private StubBody body;
    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        body = new StubBody();
        initSprites(body);
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    private static void initSprites(StubBody b) {
        for (int i = 0; i < 3; i++) {
            b.bodySpr[i] = new Sprite();
            b.bodySpr[i].setImageW(100);
            b.bodySpr[i].setImageH(100);
            b.expandSpr[i] = new Sprite();
            b.braidSpr[i] = new Sprite();
        }
    }

    // ---------------------------------------------------------------
    // コンストラクタ
    // ---------------------------------------------------------------

    @Test
    public void testDefaultConstructor() {
        StubBody stub = new StubBody();
        assertNotNull(stub);
    }

    @Test
    public void testParameterizedConstructor() {
        StubBody stub = new StubBody(10, 20, 0, AgeState.ADULT, null, null);
        assertNotNull(stub);
        assertEquals(10, stub.getX());
        assertEquals(20, stub.getY());
    }

    // ---------------------------------------------------------------
    // getType
    // ---------------------------------------------------------------

    @Test
    public void testGetTypeReturnsZero() {
        assertEquals(0, body.getType());
    }

    // ---------------------------------------------------------------
    // 名称系メソッド（未カバー分）
    // ---------------------------------------------------------------

    @Test
    public void testGetNameJ() {
        assertEquals("TestJ", body.getNameJ());
    }

    @Test
    public void testGetNameE() {
        // line 31: getNameE() は未カバーだった
        assertEquals("TestE", body.getNameE());
    }

    @Test
    public void testGetNameJ2() {
        assertEquals("", body.getNameJ2());
    }

    @Test
    public void testGetNameE2() {
        // line 41: getNameE2() は未カバーだった
        assertEquals("", body.getNameE2());
    }

    @Test
    public void testGetMyName() {
        assertEquals("MyTest", body.getMyName());
    }

    @Test
    public void testGetMyNameD() {
        assertEquals("MyTestD", body.getMyNameD());
    }

    // ---------------------------------------------------------------
    // getImage（未カバー）
    // ---------------------------------------------------------------

    @Test
    public void testGetImageReturnsZero() {
        // line 56: getImage() は未カバーだった
        int result = body.getImage(0, 0, new BodyLayer(), 0);
        assertEquals(0, result);
    }

    @Test
    public void testGetImageWithVariousArgs() {
        assertEquals(0, body.getImage(1, 1, new BodyLayer(), 2));
    }

    // ---------------------------------------------------------------
    // tuneParameters
    // ---------------------------------------------------------------

    @Test
    public void testTuneParametersDoesNotThrow() {
        assertDoesNotThrow(() -> body.tuneParameters());
    }

    // ---------------------------------------------------------------
    // isImageLoaded（未カバー）
    // ---------------------------------------------------------------

    @Test
    public void testIsImageLoadedReturnsTrue() {
        // line 65: isImageLoaded() は未カバーだった
        assertTrue(body.isImageLoaded());
    }

    // ---------------------------------------------------------------
    // getMountPoint
    // ---------------------------------------------------------------

    @Test
    public void testGetMountPointReturnsNull() {
        assertNull(body.getMountPoint("key"));
    }

    // ---------------------------------------------------------------
    // getPivotX / getPivotY（未カバー）
    // ---------------------------------------------------------------

    @Test
    public void testGetPivotXReturnsZero() {
        // line 75: getPivotX() は未カバーだった
        assertEquals(0, body.getPivotX());
    }

    @Test
    public void testGetPivotYReturnsZero() {
        // line 80: getPivotY() は未カバーだった
        assertEquals(0, body.getPivotY());
    }

    // ---------------------------------------------------------------
    // checkNonYukkuriDiseaseTolerance
    // ---------------------------------------------------------------

    @Test
    public void testCheckNonYukkuriDiseaseToleranceReturnsZero() {
        assertEquals(0, body.checkNonYukkuriDiseaseTolerance());
    }

    // ---------------------------------------------------------------
    // 統合テスト: 全メソッドを一度に呼ぶ
    // ---------------------------------------------------------------

    @Test
    public void testAllMethodsCovered() {
        body.setAgeState(AgeState.ADULT);

        assertEquals(0, body.getType());
        assertEquals("TestJ", body.getNameJ());
        assertEquals("TestE", body.getNameE());
        assertEquals("", body.getNameJ2());
        assertEquals("", body.getNameE2());
        assertEquals("MyTest", body.getMyName());
        assertEquals("MyTestD", body.getMyNameD());
        assertEquals(0, body.getImage(0, 0, new BodyLayer(), 0));
        assertDoesNotThrow(() -> body.tuneParameters());
        assertTrue(body.isImageLoaded());
        assertNull(body.getMountPoint("test"));
        assertEquals(0, body.getPivotX());
        assertEquals(0, body.getPivotY());
        assertEquals(0, body.checkNonYukkuriDiseaseTolerance());
    }
}
