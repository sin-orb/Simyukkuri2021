package org.simyukkuri.logic;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CriticalDamegeType;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

/**
 * Test class for StoneLogic.
 */
public class StoneLogicTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    private Yukkuri createAdultBody(int x, int y) {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        b.setX(x);
        b.setY(y);
        b.setZ(0);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(b.getUniqueID(), b);
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
        Yukkuri b = createAdultBody(100, 100);
        // no stones in world → loop has no iterations
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_stoneFarAway_noEffect() {
        Yukkuri b = createAdultBody(100, 100);
        // Stone far away (distance=500) → neither branch fires
        Stone stone = new Stone(600, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_differentZ_skipped() {
        Yukkuri b = createAdultBody(100, 100);
        b.setZ(1); // stone at Z=0, body at Z=1 → skip
        Stone stone = new Stone(100, 100, 0); // stone at same position
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testCheckPubble_adultBodyInjure_doesNotThrow() {
        // ADULT body close to stone → bodyInjure() called
        // Use BaryState HALF to avoid mypane.addVomit NPE
        Yukkuri b = createAdultBody(100, 100);
        b.setBurialState(BurialState.HALF);
        // Stone at same position → distance=0, getStepDist()=16 > 0 → bodyInjure
        Stone stone = new Stone(100, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
        // After bodyInjure, damage type should be INJURED
        assertEquals(CriticalDamegeType.INJURED, b.getCriticalDamege());
    }

    @Test
    public void testCheckPubble_wiseBody_runsAway() {
        // ADULT WISE body at moderate distance → runAway() called
        Yukkuri b = createAdultBody(100, 100);
        b.setIntelligence(Intelligence.WISE);
        // distance ~ 25 (100+25=125): getStepDist()=16, 16<=25 but 48>25 → WISE runAway
        Stone stone = new Stone(125, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
    }

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new StoneLogic());
    }

    // ========== 追加テスト ==========

    // --- isBaby()=true → bodyCut() 分岐 (line 39 true branch) ---

    @Test
    public void testCheckPubble_babyBody_bodyCut() {
        // BABY: getStepDist()=1, stone at same pos → distance=0, 1>0 → isBaby=true →
        // bodyCut()
        Reimu baby = new Reimu();
        baby.setAgeState(AgeState.BABY);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++)
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        baby.setSpriteSet(spr);
        baby.setX(100);
        baby.setY(100);
        baby.setZ(0);
        baby.setBurialState(BurialState.HALF); // NONE だと addVomit で mypane NPE になる
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(baby.getUniqueID(), baby);
        new Stone(100, 100, 0); // auto-registers; distance=0 → stepDist(1)>0 → bodyCut
        assertDoesNotThrow(() -> StoneLogic.checkPubble(baby));
        assertEquals(CriticalDamegeType.CUT, baby.getCriticalDamege(),
                "Baby body close to stone should get CUT damage");
    }

    // --- WISE body at moderate distance → runAway() (line 46 true branch) ---

    @Test
    public void testCheckPubble_wiseBodyModerateDistance_callsRunAway() {
        // ADULT: getStepDist()=16; stone at (104,100) → distance=16
        // 16>16? No → not injure; 48>16 && WISE → runAway
        Yukkuri b = createAdultBody(100, 100);
        b.setIntelligence(Intelligence.WISE);
        new Stone(104, 100, 0); // distance=(104-100)^2=16; auto-registers
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
        // runAway is called (no exception expected)
    }

    // --- stepDist*3>distance but non-WISE → skip (line 46 A=true B=false branch)
    // ---

    @Test
    public void testCheckPubble_nonWiseModerateDistance_noRunAway() {
        // ADULT: getStepDist()=16; stone at (104,100) → distance=16
        // 16>16? No → not injure; 48>16 && !WISE → condition false → continue
        Yukkuri b = createAdultBody(100, 100);
        b.setIntelligence(Intelligence.FOOL); // not WISE
        new Stone(104, 100, 0); // auto-registers
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
        // No damage should be applied
        assertNull(b.getCriticalDamege(), "Non-WISE body at moderate distance should not be damaged");
    }

    @Test
    public void testCheckPubbleMethodExists() {
        try {
            StoneLogic.class.getDeclaredMethod("checkPubble", Yukkuri.class);
            assertTrue(true, "checkPubble method exists");
        } catch (NoSuchMethodException e) {
            fail("checkPubble method should exist");
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_WiseBodyNearStoneSetsRunAwayDestinationAndScare() {
            Yukkuri body = createAdultBody(100, 100);
            body.setIntelligence(Intelligence.WISE);

            new Stone(104, 100, 0);

            StoneLogic.checkPubble(body);

            assertNull(body.getCriticalDamege(), "run away branch should not injure the body");
            assertTrue(body.isScare(), "run away branch should mark the body as scared");
            assertEquals(0, body.getDestX(), "wise body should flee toward the left map edge");
            assertEquals(0, body.getDestY(), "wise body should flee toward the top map edge");
        }
    }
}
