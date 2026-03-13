package src.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Happiness;
import src.system.Sprite;
import src.system.BodyLayer;

/**
 * StubBodyAttributes の未カバーメソッドのカバレッジ向上テスト.
 *
 * 既存の BodyAttributesTest は StubBodyAttributes を使うが、
 * getType, getNameJ/E, getNameJ2/E2, getMyName/D, getImage,
 * tuneParameters, isImageLoaded, getMountPoint などの
 * オーバーライドメソッド自体が呼ばれていなかった。
 * forceSetHappiness の例外パス（catchブロック）も未カバーだった。
 */
public class StubBodyAttributesTest {

    private StubBodyAttributes body;
    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        body = new StubBodyAttributes();
        initSprites(body);
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    private static void initSprites(StubBodyAttributes b) {
        for (int i = 0; i < 3; i++) {
            b.bodySpr[i] = new Sprite();
            b.bodySpr[i].setImageW(100);
            b.bodySpr[i].setImageH(100);
            b.expandSpr[i] = new Sprite();
            b.braidSpr[i] = new Sprite();
        }
    }

    // ---------------------------------------------------------------
    // getType（未カバー: line 15）
    // ---------------------------------------------------------------

    @Test
    public void testGetTypeReturnsZero() {
        assertEquals(0, body.getType());
    }

    // ---------------------------------------------------------------
    // 名称系メソッド（未カバー: lines 39, 44, 49, 54, 59, 64）
    // ---------------------------------------------------------------

    @Test
    public void testGetNameJ() {
        assertEquals("TestJ", body.getNameJ());
    }

    @Test
    public void testGetNameE() {
        assertEquals("TestE", body.getNameE());
    }

    @Test
    public void testGetNameJ2() {
        assertEquals("", body.getNameJ2());
    }

    @Test
    public void testGetNameE2() {
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
    // getImage（未カバー: line 69）
    // ---------------------------------------------------------------

    @Test
    public void testGetImageReturnsZero() {
        assertEquals(0, body.getImage(0, 0, new BodyLayer(), 0));
    }

    @Test
    public void testGetImageWithVariousArgs() {
        assertEquals(0, body.getImage(1, 1, new BodyLayer(), 3));
    }

    // ---------------------------------------------------------------
    // tuneParameters（未カバー: line 74）
    // ---------------------------------------------------------------

    @Test
    public void testTuneParametersDoesNotThrow() {
        assertDoesNotThrow(() -> body.tuneParameters());
    }

    // ---------------------------------------------------------------
    // isImageLoaded（未カバー: line 78）
    // ---------------------------------------------------------------

    @Test
    public void testIsImageLoadedReturnsTrue() {
        assertTrue(body.isImageLoaded());
    }

    // ---------------------------------------------------------------
    // getMountPoint（未カバー: line 83）
    // ---------------------------------------------------------------

    @Test
    public void testGetMountPointReturnsNull() {
        assertNull(body.getMountPoint("key"));
    }

    @Test
    public void testGetMountPointWithEmptyKey() {
        assertNull(body.getMountPoint(""));
    }

    // ---------------------------------------------------------------
    // forceSetHappiness 全 Happiness 値
    // ---------------------------------------------------------------

    @Test
    public void testForceSetHappinessVeryHappy() {
        body.setAgeState(AgeState.ADULT);
        body.forceSetHappiness(Happiness.VERY_HAPPY);
        assertEquals(Happiness.VERY_HAPPY, body.getHappiness());
    }

    @Test
    public void testForceSetHappinessHappy() {
        body.setAgeState(AgeState.ADULT);
        body.forceSetHappiness(Happiness.HAPPY);
        assertEquals(Happiness.HAPPY, body.getHappiness());
    }

    @Test
    public void testForceSetHappinessAverage() {
        body.setAgeState(AgeState.ADULT);
        body.forceSetHappiness(Happiness.AVERAGE);
        assertEquals(Happiness.AVERAGE, body.getHappiness());
    }

    @Test
    public void testForceSetHappinessSad() {
        body.setAgeState(AgeState.ADULT);
        body.forceSetHappiness(Happiness.SAD);
        assertEquals(Happiness.SAD, body.getHappiness());
    }

    @Test
    public void testForceSetHappinessVerySad() {
        body.setAgeState(AgeState.ADULT);
        body.forceSetHappiness(Happiness.VERY_SAD);
        assertEquals(Happiness.VERY_SAD, body.getHappiness());
    }

    // ---------------------------------------------------------------
    // expandSizeW setter/getter（既カバー分の確認）
    // ---------------------------------------------------------------

    @Test
    public void testSetExpandSizeW() {
        body.setExpandSizeW(42);
        assertEquals(42, body.getExpandSizeW());
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
        assertNull(body.getMountPoint("test_key"));
        assertEquals(0, body.checkNonYukkuriDiseaseTolerance());
    }
}
