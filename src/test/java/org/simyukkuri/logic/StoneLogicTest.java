package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

/** Test class for StoneLogic. */
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
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    @Test
    public void testCheckPubbleNullBody() {
        // null body で NPE にならないこと（事前に body=null）
        assertDoesNotThrow(() -> StoneLogic.checkPubble(null),
                "checkPubble(null) が例外を投げないこと");
    }

    @Test
    public void testCheckPubbleCutBody() {
        Reimu cut = new Reimu();
        cut.setCriticalDamege(CriticalDamageType.CUT);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(cut),
                "CUT状態の body で checkPubble が例外を投げないこと");
        // CUT状態のbodyは早期リターンのためcriticalDamageが変化しないこと
        assertEquals(CriticalDamageType.CUT, cut.getCriticalDamege(),
                "CUT状態は checkPubble 後も CUT のままであること");
    }

    @Test
    public void testCheckPubbleNoStones() {
        Yukkuri b = createAdultBody(100, 100);
        // no stones in world → loop has no iterations
        StoneLogic.checkPubble(b);
        // 石なしならダメージを受けないこと
        assertNull(b.getCriticalDamege(), "石がない場合は criticalDamage が null のままであること");
    }

    @Test
    public void testCheckPubble_stoneFarAway_noEffect() {
        Yukkuri b = createAdultBody(100, 100);
        // Stone far away (distance=500) → neither branch fires
        new Stone(600, 100, 0);
        StoneLogic.checkPubble(b);
        // 遠い石ではダメージを受けないこと
        assertNull(b.getCriticalDamege(), "遠距離の石では criticalDamage が null のままであること");
    }

    @Test
    public void testCheckPubble_differentZ_skipped() {
        Yukkuri b = createAdultBody(100, 100);
        b.setZ(1); // stone at Z=0, body at Z=1 → skip
        new Stone(100, 100, 0); // stone at same position
        StoneLogic.checkPubble(b);
        // 異なるZ層の石はスキップされるためダメージを受けないこと
        assertNull(b.getCriticalDamege(), "異なるZ層の石では criticalDamage が null のままであること");
    }

    @Test
    public void testCheckPubble_adultBodyInjure_doesNotThrow() {
        // ADULT body close to stone → bodyInjure() called
        // Use BaryState HALF to avoid mypane.addVomit NPE
        Yukkuri b = createAdultBody(100, 100);
        b.setBurialState(BurialState.HALF);
        // Stone at same position → distance=0, getStepDist()=16 > 0 → bodyInjure
        new Stone(100, 100, 0);
        assertDoesNotThrow(() -> StoneLogic.checkPubble(b));
        // After bodyInjure, damage type should be INJURED
        assertEquals(CriticalDamageType.INJURED, b.getCriticalDamege());
    }

    @Test
    public void testCheckPubble_wiseBody_runsAway() {
        // ADULT WISE body at moderate distance → runAway() called
        // distance=(104-100)^2=16: stepDist=16, 16<=16 (not injure); stepDist*3=48>16 && WISE → runAway
        Yukkuri b = createAdultBody(100, 100);
        b.setIntelligence(Intelligence.WISE);
        new Stone(104, 100, 0);
        StoneLogic.checkPubble(b);
        // runAway が呼ばれた場合、body が scared になること
        assertTrue(b.isScare(), "WISE body が適切な距離の石を検知して scared 状態になること");
    }

    @Test
    public void testConstructor_doesNotThrow() {
        StoneLogic instance = new StoneLogic();
        assertNotNull(instance, "StoneLogic インスタンスが生成されること");
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
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        baby.setSpriteSet(spr);
        baby.setX(100);
        baby.setY(100);
        baby.setZ(0);
        baby.setBurialState(BurialState.HALF); // NONE だと addVomit で mypane NPE になる
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(baby.getUniqueId(), baby);
        new Stone(100, 100, 0); // auto-registers; distance=0 → stepDist(1)>0 → bodyCut
        assertDoesNotThrow(() -> StoneLogic.checkPubble(baby));
        assertEquals(
                CriticalDamageType.CUT,
                baby.getCriticalDamege(),
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
        StoneLogic.checkPubble(b);
        // runAway が呼ばれた場合、body が scared 状態になること
        assertTrue(b.isScare(), "WISE body が中間距離の石を検知して scared 状態になること");
        // ダメージは受けないこと
        assertNull(b.getCriticalDamege(), "runAway 分岐ではダメージを受けないこと");
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
        assertNull(
                b.getCriticalDamege(), "Non-WISE body at moderate distance should not be damaged");
    }

    @Test
    public void testCheckPubbleMethodExists() {
        // checkPubble メソッドが存在し引数型が正しいこと
        try {
            java.lang.reflect.Method m = StoneLogic.class.getDeclaredMethod("checkPubble", Yukkuri.class);
            assertNotNull(m, "checkPubble メソッドが存在すること");
            assertEquals(void.class, m.getReturnType(), "checkPubble の戻り型が void であること");
        } catch (NoSuchMethodException e) {
            fail("checkPubble(Yukkuri) メソッドが存在すること");
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
