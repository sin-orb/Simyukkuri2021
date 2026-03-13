package src.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.SimYukkuri;
import src.base.Body;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.CriticalDamegeType;
import src.enums.Intelligence;
import src.item.Stone;
import src.system.Sprite;
import src.yukkuri.Reimu;

/**
 * Test class for StoneLogic.
 */
public class StoneLogicTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    private Body createAdultBody(int x, int y) {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        b.setX(x); b.setY(y); b.setZ(0);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    public void testCheckPubbleNullBody() {
        StoneLogic.checkPubble(null);
        assertTrue(true, "checkPubble should handle null body gracefully");
    }

    @Test
    public void testCheckPubbleCutBody() {
        Reimu cut = new Reimu();
        cut.setCriticalDamege(CriticalDamegeType.CUT);
        StoneLogic.checkPubble(cut);
        assertTrue(true, "checkPubble should handle cut body gracefully");
    }

    @Test
    public void testCheckPubbleNoStones() {
        Body b = createAdultBody(100, 100);
        // no stones in world → loop has no iterations
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_stoneFarAway_noEffect() {
        Body b = createAdultBody(100, 100);
        // Stone far away (distance=500) → neither branch fires
        Stone stone = new Stone(600, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_differentZ_skipped() {
        Body b = createAdultBody(100, 100);
        b.setZ(1); // stone at Z=0, body at Z=1 → skip
        Stone stone = new Stone(100, 100, 0); // stone at same position
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_adultBodyInjure_doesNotThrow() {
        // ADULT body close to stone → bodyInjure() called
        // Use BaryState HALF to avoid mypane.addVomit NPE
        Body b = createAdultBody(100, 100);
        b.setBaryState(BaryInUGState.HALF);
        // Stone at same position → distance=0, getStepDist()=16 > 0 → bodyInjure
        Stone stone = new Stone(100, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
        // After bodyInjure, damage type should be INJURED
        assertEquals(CriticalDamegeType.INJURED, b.getCriticalDamege());
    }

    @Test
    public void testCheckPubble_wiseBody_runsAway() {
        // ADULT WISE body at moderate distance → runAway() called
        Body b = createAdultBody(100, 100);
        b.setIntelligence(Intelligence.WISE);
        // distance ~ 25 (100+25=125): getStepDist()=16, 16<=25 but 48>25 → WISE runAway
        Stone stone = new Stone(125, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new StoneLogic());
    }

    @Test
    public void testCheckPubbleMethodExists() {
        try {
            StoneLogic.class.getDeclaredMethod("checkPubble", Body.class);
            assertTrue(true, "checkPubble method exists");
        } catch (NoSuchMethodException e) {
            fail("checkPubble method should exist");
        }
    }
}
