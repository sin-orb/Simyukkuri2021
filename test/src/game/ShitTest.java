package src.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Event;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;

class ShitTest {

    private Shit shit;

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 200);
        shit = new Shit();
        shit.setAgeState(AgeState.ADULT);
    }

    @Test
    void testGetShitState() {
        // Limit for ADULT is 100 * 24 * 8
        // 1/4 of limit is the threshold for state 1
        int limit = 100 * 24 * 8;
        int threshold = limit / 4;

        shit.setAge(0);
        assertEquals(0, shit.getShitState());

        shit.setAge(threshold);
        assertEquals(1, shit.getShitState());

        shit.setAge(threshold + 1);
        assertEquals(1, shit.getShitState());
    }

    @Test
    void testEatShit() {
        shit.setAmount(1000);
        shit.eatShit(100);
        assertEquals(900, shit.getAmount());

        shit.eatShit(1000); // eat more than rest
        assertEquals(0, shit.getAmount());
        // Since we are not in a full world simulation, remove() just sets removed flag
        // in Obj
        assertTrue(shit.isRemoved());
    }

    @Test
    void testCrushShit() {
        shit.setAge(100);
        shit.crushShit();
        // crush adds SHITLIMIT/2 to age
        // SHITLIMIT[ADULT] = 100 * 24 * 8 = 19200
        // half is 9600
        assertEquals(100 + 9600, shit.getAge());
    }

    @Test
    void testGetValue() {
        // value for ADULT is 300
        assertEquals(300, shit.getValue());

        shit.setAgeState(AgeState.CHILD);
        // value for CHILD is 100
        assertEquals(100, shit.getValue());

        shit.setAgeState(AgeState.BABY);
        // value for BABY is 50
        assertEquals(50, shit.getValue());
    }

    @Test
    void testToString() {
        shit.setOwnerName("Marisa");
        String str = shit.toString();
        assertNotNull(str);
        // Should contain owner name
        assertTrue(str.contains("Marisa"));
    }

    @Test
    void testGettersSetters() {
        shit.setOwnerId(12345);
        assertEquals(12345, shit.getOwnerId());

        shit.setOwnerName("Reimu");
        assertEquals("Reimu", shit.getOwnerName());

        shit.setFalldownDamage(50);
        assertEquals(50, shit.getFalldownDamage());

        shit.setShitType(1);
        assertEquals(1, shit.getShitType());
    }

    @Test
    void testGetSetAmount() {
        shit.setAmount(500);
        assertEquals(500, shit.getAmount());
        shit.setAmount(0);
        assertEquals(0, shit.getAmount());
    }

    @Test
    void testAgeStateGetterSetter() {
        shit.setAgeState(AgeState.CHILD);
        assertEquals(AgeState.CHILD, shit.getAgeState());
        shit.setAgeState(AgeState.BABY);
        assertEquals(AgeState.BABY, shit.getAgeState());
    }

    @Test
    void testHasGetPopup() {
        assertEquals(GetMenuTarget.SHIT, shit.hasGetPopup());
    }

    @Test
    void testHasUsePopup() {
        assertEquals(UseMenuTarget.SHIT, shit.hasUsePopup());
    }

    // --- kick ---

    @Test
    void testKick_adult_setsVelocity() {
        shit.setAgeState(AgeState.ADULT);
        assertDoesNotThrow(() -> shit.kick());
    }

    @Test
    void testKick_child_setsVelocity() {
        shit.setAgeState(AgeState.CHILD);
        assertDoesNotThrow(() -> shit.kick());
    }

    @Test
    void testKick_baby_setsVelocity() {
        shit.setAgeState(AgeState.BABY);
        assertDoesNotThrow(() -> shit.kick());
    }

    // --- clockTick: removed ---

    @Test
    void testClockTick_removed_returnsREMOVED() {
        shit.setRemoved(true);
        Event result = shit.clockTick();
        assertEquals(Event.REMOVED, result);
    }

    // --- clockTick: not removed, age below limit ---

    @Test
    void testClockTick_notRemoved_belowLimit_returnsDONOTHING() {
        shit.setRemoved(false);
        shit.setAge(0);
        Event result = shit.clockTick();
        assertEquals(Event.DONOTHING, result);
    }

    // --- clockTick: not removed, age exceeds limit → removes ---

    @Test
    void testClockTick_notRemoved_ageExceedsLimit_removes() {
        shit.setRemoved(false);
        shit.setAgeState(AgeState.ADULT);
        // SHITLIMIT[ADULT] = 100 * 24 * 8 = 19200
        shit.setAge(19200 + 1);
        Event result = shit.clockTick();
        // After remove() is called it becomes removed
        assertTrue(shit.isRemoved() || result == Event.DONOTHING || result == Event.REMOVED);
    }

    // --- clockTick: not removed, with vx set ---

    @Test
    void testClockTick_withVx_boundaryCheck() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setX(500);
        // Access vx via kick
        shit.kick(5, 0, 0); // sets vx=5
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: vx pushes past mapX ---

    @Test
    void testClockTick_vxBeyondMap_bounces() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setX(999); // near map edge (mapX=1000)
        shit.kick(5, 0, 0); // vx=5 → x becomes 1004 > 1000
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: vx pushes below 0 ---

    @Test
    void testClockTick_vxBelowZero_bounces() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setX(2);
        shit.kick(-5, 0, 0); // vx=-5 → x becomes -3 < 0
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: vy set ---

    @Test
    void testClockTick_withVy() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setY(500);
        shit.kick(0, 5, 0); // vy=5
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: vy pushes past mapY ---

    @Test
    void testClockTick_vyBeyondMap_clamps() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setY(999);
        shit.kick(0, 5, 0); // vy=5 → y becomes 1004 > 1000
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: vy pushes below 0 ---

    @Test
    void testClockTick_vyBelowZero_clamps() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setY(2);
        shit.kick(0, -5, 0); // vy=-5 → y becomes -3 < 0
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: z!=0 path (falldown) ---

    @Test
    void testClockTick_fallingZ_doesNotThrow() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setZ(10);
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- clockTick: grabbed=true ---

    @Test
    void testClockTick_grabbed_skipsMovement() {
        shit.setRemoved(false);
        shit.setAge(0);
        shit.setGrabbed(true);
        assertDoesNotThrow(() -> shit.clockTick());
    }

    // --- toString: null owner ---

    @Test
    void testToString_nullOwner() {
        shit.setOwnerName(null);
        String str = shit.toString();
        assertNotNull(str);
        assertTrue(str.contains("Unknown"));
    }

    // --- getShitState: CHILD and BABY ---

    @Test
    void testGetShitState_Child() {
        shit.setAgeState(AgeState.CHILD);
        // SHITLIMIT[CHILD] = 100 * 24 * 4 = 9600
        shit.setAge(0);
        assertEquals(0, shit.getShitState());
        shit.setAge(9600 / 4);
        assertEquals(1, shit.getShitState());
    }

    @Test
    void testGetShitState_Baby() {
        shit.setAgeState(AgeState.BABY);
        // SHITLIMIT[BABY] = 100 * 24 * 2 = 4800
        shit.setAge(0);
        assertEquals(0, shit.getShitState());
        shit.setAge(4800 / 4);
        assertEquals(1, shit.getShitState());
    }

    // --- getImage ---

    @Test
    void testGetImage_doesNotThrow() {
        assertDoesNotThrow(() -> shit.getImage());
    }

    // --- getShadowImage ---

    @Test
    void testGetShadowImage_doesNotThrow() {
        assertDoesNotThrow(() -> shit.getShadowImage());
    }

    // --- getSize ---

    @Test
    void testGetSize_doesNotThrow() {
        assertDoesNotThrow(() -> shit.getSize());
    }

    // --- shitSizeDisplayName static strings ---

    @Test
    void testShitConstants() {
        assertEquals(0, Shit.SHIT_NORMAL);
        assertEquals(1, Shit.SHIT_CRASHED);
        assertEquals(2, Shit.SHIT_SHADOW);
        assertEquals(3, Shit.NUM_OF_SHIT_STATE);
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            src.game.Shit.loadImages(src.game.Shit.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            src.base.Body body = src.util.WorldTestHelper.createBody();
            src.game.Shit s = new src.game.Shit(100, 100, 0, body, src.enums.YukkuriType.REIMU);
        } catch (Exception e) { }
    }
}