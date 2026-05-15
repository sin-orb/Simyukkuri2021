package org.simyukkuri.item;

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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.util.WorldTestHelper;

class SuiTest extends ItemTestBase {

    private Yukkuri createBody() {
        Yukkuri b = WorldTestHelper.createBody();
        b.setX(100);
        b.setY(100);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    void testConstructor_Default() {
        Sui item = new Sui();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getSuis().containsKey(item.getObjId()));
    }

    @Test
    void testConstructor_WithParams() {
        Sui item = new Sui(50, 60, 0);
        assertNotNull(item);
    }

    // --- rideOn ---

    @Test
    void testRideOn_NullBody_returnsFalse() {
        Sui sui = new Sui();
        assertFalse(sui.rideOn(null));
    }

    @Test
    void testRideOn_NYDBody_returnsFalse() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE);
        assertFalse(sui.rideOn(b));
    }

    @Test
    void testRideOn_FirstRider_returnsTrue() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        assertTrue(sui.rideOn(b));
        assertEquals(1, sui.getCurrent_bindbody_num());
    }

    @Test
    void testRideOn_SameBodyTwice_returnsFalse() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        assertTrue(sui.rideOn(b));
        assertFalse(sui.rideOn(b));
    }

    @Test
    void testRideOn_SecondRider_returnsTrue() {
        Sui sui = new Sui();
        Yukkuri b1 = createBody();
        Yukkuri b2 = createBody();
        assertTrue(sui.rideOn(b1));
        assertTrue(sui.rideOn(b2));
        assertEquals(2, sui.getCurrent_bindbody_num());
    }

    // --- isriding ---

    @Test
    void testIsriding_NullBody_returnsFalse() {
        Sui sui = new Sui();
        assertFalse(sui.isriding(null));
    }

    @Test
    void testIsriding_NotOnSui_returnsFalse() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        assertFalse(sui.isriding(b));
    }

    @Test
    void testIsriding_AfterRideOn_returnsTrue() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        sui.rideOn(b);
        assertTrue(sui.isriding(b));
    }

    // --- iscanriding ---

    @Test
    void testIscanriding_NoOwner_returnsFalse() {
        Sui sui = new Sui();
        assertFalse(sui.iscanriding());
    }

    @Test
    void testIscanriding_OwnerOnBoard_returnsTrue() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        sui.rideOn(b);
        // bindobj == b, b is on board → true
        assertTrue(sui.iscanriding());
    }

    // --- NoCanBind ---

    @Test
    void testNoCanBind_NoOwner_returnsFalse() {
        Sui sui = new Sui();
        assertFalse(sui.NoCanBind());
    }

    @Test
    void testNoCanBind_WithOwner_returnsTrue() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        sui.rideOn(b);
        assertTrue(sui.NoCanBind());
    }

    // --- rideOff ---

    @Test
    void testRideOff_NullBody_doesNotThrow() {
        Sui sui = new Sui();
        assertDoesNotThrow(() -> sui.rideOff(null));
    }

    @Test
    void testRideOff_Owner_allOff() {
        Sui sui = new Sui();
        Yukkuri owner = createBody();
        Yukkuri passenger = createBody();
        sui.rideOn(owner);
        sui.rideOn(passenger);
        // owner descends → all off
        sui.rideOff(owner);
        assertFalse(sui.isriding(owner));
        assertFalse(sui.isriding(passenger));
        assertEquals(0, sui.getCurrent_bindbody_num());
    }

    @Test
    void testRideOff_NonOwner_onlyThatBodyOff() {
        Sui sui = new Sui();
        Yukkuri owner = createBody();
        Yukkuri passenger = createBody();
        sui.rideOn(owner);
        sui.rideOn(passenger);
        // passenger descends → only passenger off
        sui.rideOff(passenger);
        assertFalse(sui.isriding(passenger));
        assertTrue(sui.isriding(owner));
    }

    // --- getters/setters ---

    @Test
    void testGetSetCurrentBindBodyNum() {
        Sui sui = new Sui();
        sui.setCurrent_bindbody_num(2);
        assertEquals(2, sui.getCurrent_bindbody_num());
    }

    @Test
    void testGetSetBindBody() {
        Sui sui = new Sui();
        Yukkuri[] bodies = new Yukkuri[3];
        sui.setBoundYukkuri(bodies);
        assertNotNull(sui.getBoundYukkuri());
    }

    @Test
    void testGetSetCurrentDirection() {
        Sui sui = new Sui();
        sui.setCurrent_direction(3);
        assertEquals(3, sui.getCurrent_direction());
    }

    @Test
    void testGetSetCurrentCondition() {
        Sui sui = new Sui();
        sui.setCurrent_condition(1);
        assertEquals(1, sui.getCurrent_condition());
    }

    // --- enableHitCheck / getHitCheckObjType ---

    @Test
    void testEnableHitCheck_returnsTrue() {
        Sui sui = new Sui();
        assertTrue(sui.enableHitCheck());
    }

    @Test
    void testGetHitCheckObjType() {
        Sui sui = new Sui();
        assertEquals(Sui.hitCheckObjType, sui.getHitCheckObjType());
    }

    // --- getBounding ---

    @Test
    void testGetBounding_doesNotThrow() {
        assertDoesNotThrow(() -> Sui.getBounding());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData_doesNotThrow() {
        Sui sui = new Sui();
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        assertDoesNotThrow(() -> sui.removeFromWorld());
    }

    // --- ChangeY ---

    @Test
    void testChangeY_doesNotThrow() {
        Sui sui = new Sui();
        assertDoesNotThrow(() -> sui.ChangeY(true));
        assertDoesNotThrow(() -> sui.ChangeY(false));
    }

    // --- upDate ---

    @Test
    void testUpDate_doesNotThrow() {
        Sui sui = new Sui();
        assertDoesNotThrow(() -> sui.upDate());
    }

    // --- clockTick ---

    @Test
    void testClockTick_doesNotThrow() {
        Sui sui = new Sui();
        assertDoesNotThrow(() -> sui.clockTick());
    }

    // --- getBindobj / setBindobj ---

    @Test
    void testGetSetBindobj() {
        Sui sui = new Sui();
        assertNull(sui.getBindobj());
        Yukkuri b = createBody();
        sui.setBindobj(b);
        assertEquals(b, sui.getBindobj());
    }

    // --- getDestX / setDestX / getDestY / setDestY ---

    @Test
    void testGetSetDestX() {
        Sui sui = new Sui();
        sui.setDestX(200);
        assertEquals(200, sui.getDestX());
    }

    @Test
    void testGetSetDestY() {
        Sui sui = new Sui();
        sui.setDestY(300);
        assertEquals(300, sui.getDestY());
    }

    // --- getVecX / setVecX / getVecY / setVecY ---

    @Test
    void testGetSetVecX() {
        Sui sui = new Sui();
        sui.setVecX(5);
        assertEquals(5, sui.getVecX());
    }

    @Test
    void testGetSetVecY() {
        Sui sui = new Sui();
        sui.setVecY(-3);
        assertEquals(-3, sui.getVecY());
    }

    // --- getSpeed / setSpeed ---

    @Test
    void testGetSetSpeed() {
        Sui sui = new Sui();
        sui.setSpeed(800);
        assertEquals(800, sui.getSpeed());
    }

    // --- objHitProcess (current_condition != out_of_control → returns 0) ---

    @Test
    void testObjHitProcess_normalCondition_returnsZero() {
        Sui sui = new Sui();
        // default condition is rest (not out_of_control) → returns 0 without hitting
        Yukkuri b = createBody();
        assertEquals(0, sui.objHitProcess(b));
    }

    // --- getImageLayer (images are null in headless, but method is callable) ---

    @Test
    void testGetImageLayer_doesNotThrow() {
        Sui sui = new Sui();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> sui.getImageLayer(layer));
    }

    // --- getShadowImage (images are null in headless) ---

    @Test
    void testGetShadowImage_doesNotThrow() {
        Sui sui = new Sui();
        assertDoesNotThrow(() -> sui.getShadowImage());
    }

    // --- clockTick with owner riding ---

    @Test
    void testClockTick_withOwnerRiding_doesNotThrow() {
        Sui sui = new Sui(100, 100, 0);
        Yukkuri b = createBody();
        sui.rideOn(b);
        assertDoesNotThrow(() -> sui.clockTick());
    }

    // --- upDate with body grabbed ---

    @Test
    void testUpDate_bodyGrabbed_ridesOff() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        sui.rideOn(b);
        b.setGrabbed(true);
        assertDoesNotThrow(() -> sui.upDate());
        assertFalse(sui.isriding(b));
    }

    // --- upDate with removed owner ---

    @Test
    void testUpDate_ownerRemoved_clearsBindobj() {
        Sui sui = new Sui();
        Yukkuri b = createBody();
        sui.setBindobj(b);
        b.setRemoved(true);
        assertDoesNotThrow(() -> sui.upDate());
        assertNull(sui.getBindobj());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Sui.loadImages(Sui.class.getClassLoader(), null);
        } catch (Exception e) {
        }
    }
}
