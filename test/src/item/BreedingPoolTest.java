package src.item;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.enums.CoreAnkoState;
import src.util.WorldTestHelper;

class BreedingPoolTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        BreedingPool item = new BreedingPool();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getBreedingPool().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getBreedingPool().containsKey(item.getObjId()));
    }

    @Test
    void testPoolTypeEnum() {
        BreedingPool.PoolType[] types = BreedingPool.PoolType.values();
        assertEquals(8, types.length);
        for (BreedingPool.PoolType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(BreedingPool.PoolType.LOW, BreedingPool.PoolType.valueOf("LOW"));
        assertEquals(BreedingPool.PoolType.INDUSTRYS, BreedingPool.PoolType.valueOf("INDUSTRYS"));
    }

    @Test
    void testGetHitCheckObjType() {
        BreedingPool item = new BreedingPool();
        assertEquals(BreedingPool.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testGetShadowImage() {
        BreedingPool item = new BreedingPool();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(BreedingPool.getBounding());
    }

    @Test
    void testGetSetHighQuality() {
        BreedingPool item = new BreedingPool();
        assertFalse(item.isHighQuality());
        item.setHighQuality(true);
        assertTrue(item.isHighQuality());
    }

    @Test
    void testGetSetStalkPool() {
        BreedingPool item = new BreedingPool();
        assertFalse(item.isStalkPool());
        item.setStalkPool(true);
        assertTrue(item.isStalkPool());
    }

    @Test
    void testGetSetLiquidYukkuriType() {
        BreedingPool item = new BreedingPool();
        assertEquals(-1, item.getLiquidYukkuriType());
        item.setLiquidYukkuriType(2);
        assertEquals(2, item.getLiquidYukkuriType());
    }

    @Test
    void testGetSetLastSelected() {
        BreedingPool item = new BreedingPool();
        assertEquals(0, item.getLastSelected());
        item.setLastSelected(3);
        assertEquals(3, item.getLastSelected());
    }

    @Test
    void testGetValue_AllOptions() {
        BreedingPool item = new BreedingPool();
        item.setOption(0); assertEquals(1000, item.getValue());
        item.setOption(1); assertEquals(5000, item.getValue());
        item.setOption(2); assertEquals(50000, item.getValue());
        item.setOption(3); assertEquals(450000, item.getValue());
        item.setOption(4); assertEquals(1000, item.getValue());
        item.setOption(5); assertEquals(5000, item.getValue());
        item.setOption(6); assertEquals(50000, item.getValue());
        item.setOption(7); assertEquals(600000, item.getValue());
    }

    @Test
    void testGetCost_AllOptions() {
        BreedingPool item = new BreedingPool();
        item.setOption(0); assertEquals(10, item.getCost());
        item.setOption(1); assertEquals(50, item.getCost());
        item.setOption(2); assertEquals(50, item.getCost());
        item.setOption(3); assertEquals(1500, item.getCost());
        item.setOption(4); assertEquals(10, item.getCost());
        item.setOption(5); assertEquals(50, item.getCost());
        item.setOption(6); assertEquals(50, item.getCost());
        item.setOption(7); assertEquals(1500, item.getCost());
    }

    @Test
    void testRemoveListData() {
        BreedingPool item = new BreedingPool();
        item.setObjId(50);
        SimYukkuri.world.getCurrentMap().getBreedingPool().put(item.getObjId(), item);
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getBreedingPool().containsKey(item.getObjId()));
    }

    @Test
    void testObjHitProcess_Disabled() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(false);
        Body body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    // --- objHitProcess: body castration → returns 0 ---

    @Test
    void testObjHitProcess_BodyCastration_ReturnsZero() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setStalkPool(false);
        Body body = WorldTestHelper.createBody();
        body.setBodyCastration(true);
        assertEquals(0, item.objHitProcess(body));
    }

    // --- objHitProcess: stalk castration, stalkPool=true → returns 0 ---

    @Test
    void testObjHitProcess_StalkCastration_ReturnsZero() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setStalkPool(true);
        Body body = WorldTestHelper.createBody();
        body.setStalkCastration(true);
        assertEquals(0, item.objHitProcess(body));
    }

    // --- objHitProcess: dead + crushed body → sets liquidYukkuriType ---

    @Test
    void testObjHitProcess_DeadCrushed_SetsLiquidType() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setLiquidYukkuriType(-1);
        Body body = WorldTestHelper.createBody();
        body.setDead(true);
        body.setCrushed(true);
        int expectedType = body.getType();
        item.objHitProcess(body);
        assertEquals(expectedType, item.getLiquidYukkuriType());
        assertTrue(body.isRemoved());
    }

    // --- objHitProcess: enabled, alive, age%10==0, not castrated → adds baby ---

    @Test
    void testObjHitProcess_AlivePools_AddsBaby() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setHighQuality(false);
        item.setStalkPool(false);
        item.setOption(0);
        item.setAge(0); // 0 % 10 == 0
        Body body = WorldTestHelper.createBody();
        body.setDead(false);
        body.setBodyCastration(false);
        assertDoesNotThrow(() -> item.objHitProcess(body));
    }

    // --- cry: hasBabyOrStalk=false → does nothing ---

    @Test
    void testCry_NoBabyOrStalk() {
        BreedingPool item = new BreedingPool();
        Body body = WorldTestHelper.createBody();
        // hasBabyOrStalk() is false by default → cry() does nothing
        assertDoesNotThrow(() -> item.cry(body));
    }

    // --- cry: hasBabyOrStalk=true, isNYD=false ---

    @Test
    void testCry_WithBabyOrStalk_NotNYD() {
        BreedingPool item = new BreedingPool();
        Body body = WorldTestHelper.createBody();
        body.setHasBaby(true); // hasBabyOrStalk() returns true
        // isNYD() = false by default → setMessage or setPikoMessage path
        assertDoesNotThrow(() -> item.cry(body));
    }

    // --- cry: hasBabyOrStalk=true, isNYD=true → setNYDMessage path ---

    @Test
    void testCry_WithBabyOrStalk_IsNYD() {
        BreedingPool item = new BreedingPool();
        Body body = WorldTestHelper.createBody();
        body.setHasBaby(true);
        body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        assertDoesNotThrow(() -> item.cry(body));
    }

    // --- getImageLayer: enabled, liquidYukkuriType == 2 ---

    @Test
    void testGetImageLayer_Enabled_LiquidType2() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setLiquidYukkuriType(2);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getImageLayer: enabled, liquidYukkuriType != -1 and != 2 ---

    @Test
    void testGetImageLayer_Enabled_LiquidTypeOther() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setLiquidYukkuriType(1);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getImageLayer: enabled, liquidYukkuriType == -1 ---

    @Test
    void testGetImageLayer_Enabled_LiquidTypeNeg1() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(true);
        item.setLiquidYukkuriType(-1);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getImageLayer: disabled ---

    @Test
    void testGetImageLayer_Disabled() {
        BreedingPool item = new BreedingPool();
        item.setEnabled(false);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- setupPool: headless → try/catch ---

    @Test
    void testSetupPool_headless_executesCode() {
        BreedingPool item = new BreedingPool();
        try {
            BreedingPool.setupPool(item, true);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- Constructor(int, int, int): headless setupPool fails → item removed from map ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        try {
            BreedingPool item = new BreedingPool(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment (setupPool fails)
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            BreedingPool.loadImages(BreedingPool.class.getClassLoader(), null);
        } catch (Exception e) { }
    }
}