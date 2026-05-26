package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.YukkuriRelationType;
import org.simyukkuri.util.WorldTestHelper;

public class EmotionLogicTest {

    private Yukkuri dummyParent;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
        dummyParent = WorldTestHelper.createBody();
        dummyParent.setUniqueId(999);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry()
                .put(dummyParent.getUniqueId(), dummyParent);
    }

    private void mockRelation(Yukkuri me, Yukkuri you, YukkuriRelationType relation)
            throws Exception {
        switch (relation) {
            case FATHER:
                you.setParents(new int[] {me.getUniqueId(), -1});
                break;
            case MOTHER:
                you.setParents(new int[] {-1, me.getUniqueId()});
                break;
            case PARTNER:
                me.setPartner(you.getUniqueId());
                you.setPartner(me.getUniqueId());
                break;
            case CHILD_OF_FATHER:
                me.setParents(new int[] {you.getUniqueId(), -1});
                break;
            case CHILD_OF_MOTHER:
                me.setParents(new int[] {-1, you.getUniqueId()});
                break;
            case ELDER_SISTER:
                me.setParents(new int[] {999, -1});
                you.setParents(new int[] {999, -1});
                me.setAge(100);
                you.setAge(50);
                break;
            case YOUNGER_SISTER:
                me.setParents(new int[] {999, -1});
                you.setParents(new int[] {999, -1});
                me.setAge(50);
                you.setAge(100);
                break;
            case OTHER:
            default:
                break;
        }
    }

    private void setPainStates(Yukkuri b, boolean pain, boolean damaged, boolean critical) {
        b.setNeedled(pain);
        b.setDamage(damaged ? 1000 : 0);
        b.setCriticalDamege(critical ? CriticalDamageType.CUT : null);
    }

    @Test
    public void testCheckEmotionForOther() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        me.setUniqueId(1);
        you.setUniqueId(2);

        for (Happiness hTarget : Happiness.values()) {
            you.setHappiness(hTarget);
            for (Happiness hMine : Happiness.values()) {
                me.setHappiness(hMine);
                for (YukkuriRelationType rel : YukkuriRelationType.values()) {
                    resetRelation(me, you);
                    mockRelation(me, you, rel);

                    for (boolean rude : new boolean[] {false, true}) {
                        me.setAttitude(rude ? Attitude.SHITHEAD : Attitude.AVERAGE);

                        for (boolean p : new boolean[] {false, true}) {
                            for (boolean d : new boolean[] {false, true}) {
                                for (boolean c : new boolean[] {false, true}) {
                                    setPainStates(you, p, d, c);

                                    boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
                                    assertNotNull(result);
                                    assertEquals(7, result.length);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testCheckEmotion_FamilyJoy() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Both happy family -> Joy
        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.PARTNER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[0], "Should feel Joy for happy partner");
    }

    @Test
    public void testCheckEmotion_ChildEnvy() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Happy parent + sad child -> Envy
        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.VERY_HAPPY);
        mockRelation(me, you, YukkuriRelationType.CHILD_OF_MOTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[5], "Child should feel Envy for very happy mother");
    }

    @Test
    public void testCheckEmotion_RudeSchadenfreude() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Rude self + sad other -> Joy + Pleasure (Schadenfreude)
        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[0], "Rude yukkuri should feel Joy at others sadness");
        assertTrue(result[3], "Rude yukkuri should feel Pleasure at others sadness");
    }

    @Test
    public void testCheckEmotion_FamilyWorry() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Family in pain -> Worry + Fear
        mockRelation(me, you, YukkuriRelationType.MOTHER);
        you.setHappiness(Happiness.VERY_SAD);
        setPainStates(
                you, false, false,
                true); // critical=true → getCriticalDamege()!=null → bIsPainOther=true

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[6], "Should worry about family in pain");
        assertTrue(result[4], "Should feel fear for family in pain");
    }

    @Test
    public void testCheckEmotion_RudeEnvyAndAnger() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Rude very sad self + happy other -> Anger + Envy
        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.VERY_SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[1], "Sad rude yukkuri should feel Anger at happy other");
        assertTrue(result[5], "Sad rude yukkuri should feel Envy at happy other");
    }

    // --- constructor ---

    @Test
    public void testConstructor_doesNotThrow() {
        // Line 10: コンストラクタ呼び出し (3 missed instructions)
        assertDoesNotThrow(() -> new EmotionLogic());
    }

    // --- mine=SAD × target=HAPPY × FATHER relation → joy (lines 99,103-104) ---

    @Test
    public void testCheckEmotion_SadMeSeeHappyFather_Joy() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // me=SAD, you=HAPPY, rel=FATHER (me is father of you)
        me.setHappiness(Happiness.SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.FATHER);

        // target=HAPPY × mine=SAD × FATHER → abEmote[0]=true (喜)
        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[0], "Sad father should feel Joy seeing happy child");
    }

    // --- mine=SAD × target=SAD × family → sad+worry (line 147 branch) ---

    @Test
    public void testCheckEmotion_SadMeSeesSadFamily_SadAndWorry() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // me=SAD, you=SAD, rel=MOTHER (me is mother of you)
        me.setHappiness(Happiness.SAD);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, YukkuriRelationType.MOTHER);

        // target=SAD × mine=SAD × MOTHER family → abEmote[2]=true (哀), abEmote[6]=true
        // (心配)
        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[2], "Sad mother should feel Sadness seeing sad child");
        assertTrue(result[6], "Sad mother should feel Worry seeing sad child");
    }

    // --- mine=VERY_SAD × target=SAD × OTHER (non-family) → sad only (line 147 +
    // default branch) ---

    @Test
    public void testCheckEmotion_VerySadMeSeesSadOther_SadOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // me=VERY_SAD, you=SAD, rel=OTHER
        me.setHappiness(Happiness.VERY_SAD);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        // target=SAD × mine=VERY_SAD × OTHER → abEmote[2]=true (哀)
        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[2], "Very sad yukkuri should feel Sadness seeing sad stranger");
        assertFalse(result[6], "No worry for strangers");
    }

    @Test
    public void testScenario_HappyParentSeesSadInjuredChild_SadWorryFearOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, YukkuriRelationType.MOTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, false, true, false, true, false, true}, result);
    }

    @Test
    public void testScenario_AverageNonRudeSeesSadInjuredStranger_FearOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, YukkuriRelationType.OTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, false, false, false, true, false, false}, result);
    }

    @Test
    public void testScenario_VerySadRudeSeesHappyStranger_AngerAndEnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.VERY_SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, true, false, false, false, true, false}, result);
    }

    @Test
    public void testScenario_HappyNonRudeSeesHappyStranger_PleasureOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, false, false, true, false, false, false}, result);
    }

    @Test
    public void testScenario_AverageSeesHappyPartner_EnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.PARTNER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, false, false, false, false, true, false}, result);
    }

    @Test
    public void testScenario_SadSeesHappyStranger_SadAndEnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, YukkuriRelationType.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {false, false, true, false, false, true, false}, result);
    }

    @Test
    public void testScenario_AverageRudeSeesSadStranger_JoyAndPleasureOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.VERY_SAD);
        mockRelation(me, you, YukkuriRelationType.OTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] {true, false, false, true, false, false, false}, result);
    }

    private void registerBodies(Yukkuri... bodies) {
        for (Yukkuri b : bodies) {
            org.simyukkuri.SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(b.getUniqueId(), b);
        }
    }

    private void resetRelation(Yukkuri me, Yukkuri you) throws Exception {
        me.setParents(new int[] {-1, -1});
        you.setParents(new int[] {-1, -1});
        me.setPartner(-1);
        you.setPartner(-1);
    }
}
