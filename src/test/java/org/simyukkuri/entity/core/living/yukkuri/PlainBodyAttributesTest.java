package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.system.YukkuriLayer;

// YukkuriLayer は定数を持たないクラスなので new YukkuriLayer() でインスタンス生成して渡す

/**
 * PlainBodyAttributes のカバレッジ向上テスト.
 *
 * PlainBodyAttributes は BodyAttributes の最小具象実装で、
 * テスト用スタブとして test/src/base/ に配置されている。
 * このクラスは既存の BodyAttributesTest では使われず 0% カバレッジだったため、
 * 各オーバーライドメソッドをすべて呼び出すテストを追加する。
 */
public class PlainBodyAttributesTest {

    private PlainBodyAttributes body;
    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        body = new PlainBodyAttributes();
        initSprites(body);
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    /** bodySpr / expandSpr / braidSpr を初期化する（getSizeなどで参照されるため） */
    private static void initSprites(PlainBodyAttributes b) {
        for (int i = 0; i < 3; i++) {
            b.getSpriteSet()[i] = new Sprite();
            b.getSpriteSet()[i].setImageW(100);
            b.getSpriteSet()[i].setImageH(100);
            b.getExpandSpr()[i] = new Sprite();
            b.getBraidSpr()[i] = new Sprite();
        }
    }

    // ---------------------------------------------------------------
    // コンストラクタ
    // ---------------------------------------------------------------

    @Test
    public void testInstantiation() {
        PlainBodyAttributes plain = new PlainBodyAttributes();
        assertNotNull(plain);
    }

    // ---------------------------------------------------------------
    // getType
    // ---------------------------------------------------------------

    @Test
    public void testGetTypeReturnsZero() {
        assertEquals(org.simyukkuri.enums.YukkuriType.TARINAI, body.getType());
    }

    // ---------------------------------------------------------------
    // forceSetHappiness (正常系 + 例外系)
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
    // 名称系メソッド
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
    // getImage
    // ---------------------------------------------------------------

    @Test
    public void testGetImageReturnsZero() {
        int result = body.getImage(0, 0, new YukkuriLayer(), 0);
        assertEquals(0, result);
    }

    @Test
    public void testGetImageWithVariousArgs() {
        assertEquals(0, body.getImage(1, 1, new YukkuriLayer(), 1));
    }

    // ---------------------------------------------------------------
    // tuneParameters
    // ---------------------------------------------------------------

    @Test
    public void testTuneParametersDoesNotThrow() {
        assertDoesNotThrow(() -> body.tuneParameters());
    }

    // ---------------------------------------------------------------
    // isImageLoaded
    // ---------------------------------------------------------------

    @Test
    public void testIsImageLoadedReturnsTrue() {
        assertTrue(body.isImageLoaded());
    }

    // ---------------------------------------------------------------
    // getMountPoint
    // ---------------------------------------------------------------

    @Test
    public void testGetMountPointReturnsNull() {
        assertNull(body.getMountPoint("any_key"));
    }

    @Test
    public void testGetMountPointWithEmptyKey() {
        assertNull(body.getMountPoint(""));
    }

    // ---------------------------------------------------------------
    // getNonYukkuriDiseaseTolerance
    // ---------------------------------------------------------------

    @Test
    public void testCheckNonYukkuriDiseaseToleranceReturnsZero() {
        assertEquals(0, body.getNonYukkuriDiseaseTolerance());
    }

    // ---------------------------------------------------------------
    // PlainBodyAttributes を BodyAttributes として使う統合テスト
    // ---------------------------------------------------------------

    @Test
    public void testBodyAttributesIntegration() {
        body.setAgeState(AgeState.BABY);
        body.forceSetHappiness(Happiness.HAPPY);
        assertEquals(Happiness.HAPPY, body.getHappiness());
        assertEquals(AgeState.BABY, body.getAgeState());
        assertEquals(org.simyukkuri.enums.YukkuriType.TARINAI, body.getType());
        assertEquals("TestJ", body.getNameJ());
        assertEquals("TestE", body.getNameE());
        assertTrue(body.isImageLoaded());
        assertEquals(0, body.getNonYukkuriDiseaseTolerance());
    }

    @Test
    public void testForceSetHappinessAllValues() {
        body.setAgeState(AgeState.ADULT);
        for (Happiness h : Happiness.values()) {
            body.forceSetHappiness(h);
            assertEquals(h, body.getHappiness());
        }
    }
}
