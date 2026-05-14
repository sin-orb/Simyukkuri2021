package org.simyukkuri.entity.core.living.yukkuri;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.system.YukkuriLayer;

/**
 * StubBody のカバレッジ向上テスト.
 *
 * StubBody は Yukkuri の最小具象実装で test/src/base/ に配置されている。
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
        assertEquals(org.simyukkuri.enums.YukkuriType.TARINAI, body.getType());
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
        int result = body.getImage(0, 0, new YukkuriLayer(), 0);
        assertEquals(0, result);
    }

    @Test
    public void testGetImageWithVariousArgs() {
        assertEquals(0, body.getImage(1, 1, new YukkuriLayer(), 2));
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
    // getNonYukkuriDiseaseTolerance
    // ---------------------------------------------------------------

    @Test
    public void testCheckNonYukkuriDiseaseToleranceReturnsZero() {
        assertEquals(0, body.getNonYukkuriDiseaseTolerance());
    }

    // ---------------------------------------------------------------
    // 統合テスト: 全メソッドを一度に呼ぶ
    // ---------------------------------------------------------------

    @Test
    public void testAllMethodsCovered() {
        body.setAgeState(AgeState.ADULT);

        assertEquals(org.simyukkuri.enums.YukkuriType.TARINAI, body.getType());
        assertEquals("TestJ", body.getNameJ());
        assertEquals("TestE", body.getNameE());
        assertEquals("", body.getNameJ2());
        assertEquals("", body.getNameE2());
        assertEquals("MyTest", body.getMyName());
        assertEquals("MyTestD", body.getMyNameD());
        assertEquals(0, body.getImage(0, 0, new YukkuriLayer(), 0));
        assertDoesNotThrow(() -> body.tuneParameters());
        assertTrue(body.isImageLoaded());
        assertNull(body.getMountPoint("test"));
        assertEquals(0, body.getPivotX());
        assertEquals(0, body.getPivotY());
        assertEquals(0, body.getNonYukkuriDiseaseTolerance());
    }
}
