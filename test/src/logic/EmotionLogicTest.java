package src.logic;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.EnumRelationMine;
import src.enums.Happiness;
import src.enums.Attitude;
import src.enums.CriticalDamegeType;
import src.util.WorldTestHelper;
import src.system.MapPlaceData;

public class EmotionLogicTest {

    private Yukkuri dummyParent;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
        dummyParent = WorldTestHelper.createBody();
        dummyParent.setUniqueID(999);
        try {
            java.lang.reflect.Field mapBodyField = MapPlaceData.class.getDeclaredField("body");
            mapBodyField.setAccessible(true);
            ((Map<Integer, Yukkuri>) mapBodyField.get(SimYukkuri.world.getCurrentMap())).put(dummyParent.getUniqueID(),
                    dummyParent);
        } catch (Exception e) {
        }
    }

    private void mockRelation(Yukkuri me, Yukkuri you, EnumRelationMine relation) throws Exception {
        switch (relation) {
            case FATHER:
                you.setParents(new int[] { me.getUniqueID(), -1 });
                break;
            case MOTHER:
                you.setParents(new int[] { -1, me.getUniqueID() });
                break;
            case PARTNAR:
                me.setPartner(you.getUniqueID());
                you.setPartner(me.getUniqueID());
                break;
            case CHILD_FATHER:
                me.setParents(new int[] { you.getUniqueID(), -1 });
                break;
            case CHILD_MOTHER:
                me.setParents(new int[] { -1, you.getUniqueID() });
                break;
            case ELDERSISTER:
                me.setParents(new int[] { 999, -1 });
                you.setParents(new int[] { 999, -1 });
                me.setAge(100);
                you.setAge(50);
                break;
            case YOUNGSISTER:
                me.setParents(new int[] { 999, -1 });
                you.setParents(new int[] { 999, -1 });
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
        b.setCriticalDamege(critical ? CriticalDamegeType.CUT : null);
    }

    @Test
    public void testCheckEmotionForOther() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        me.setUniqueID(1);
        you.setUniqueID(2);

        for (Happiness hTarget : Happiness.values()) {
            you.setHappiness(hTarget);
            for (Happiness hMine : Happiness.values()) {
                me.setHappiness(hMine);
                for (EnumRelationMine rel : EnumRelationMine.values()) {
                    resetRelation(me, you);
                    mockRelation(me, you, rel);

                    for (boolean rude : new boolean[] { false, true }) {
                        me.setAttitude(rude ? Attitude.SHITHEAD : Attitude.AVERAGE);

                        for (boolean p : new boolean[] { false, true }) {
                            for (boolean d : new boolean[] { false, true }) {
                                for (boolean c : new boolean[] { false, true }) {
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
        mockRelation(me, you, EnumRelationMine.PARTNAR);

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
        mockRelation(me, you, EnumRelationMine.CHILD_MOTHER);

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
        mockRelation(me, you, EnumRelationMine.OTHER);

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
        mockRelation(me, you, EnumRelationMine.MOTHER);
        you.setHappiness(Happiness.VERY_SAD);
        setPainStates(you, false, false, true); // critical=true → getCriticalDamege()!=null → bIsPainOther=true

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
        mockRelation(me, you, EnumRelationMine.OTHER);

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
        mockRelation(me, you, EnumRelationMine.FATHER);

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
        mockRelation(me, you, EnumRelationMine.MOTHER);

        // target=SAD × mine=SAD × MOTHER family → abEmote[2]=true (哀), abEmote[6]=true (心配)
        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[2], "Sad mother should feel Sadness seeing sad child");
        assertTrue(result[6], "Sad mother should feel Worry seeing sad child");
    }

    // --- mine=VERY_SAD × target=SAD × OTHER (non-family) → sad only (line 147 + default branch) ---

    @Test
    public void testCheckEmotion_VerySadMeSeesSadOther_SadOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // me=VERY_SAD, you=SAD, rel=OTHER
        me.setHappiness(Happiness.VERY_SAD);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, EnumRelationMine.OTHER);

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
        mockRelation(me, you, EnumRelationMine.MOTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, false, true, false, true, false, true }, result);
    }

    @Test
    public void testScenario_AverageNonRudeSeesSadInjuredStranger_FearOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.SAD);
        mockRelation(me, you, EnumRelationMine.OTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, false, false, false, true, false, false }, result);
    }

    @Test
    public void testScenario_VerySadRudeSeesHappyStranger_AngerAndEnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.VERY_SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, EnumRelationMine.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, true, false, false, false, true, false }, result);
    }

    @Test
    public void testScenario_HappyNonRudeSeesHappyStranger_PleasureOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.HAPPY);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, EnumRelationMine.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, false, false, true, false, false, false }, result);
    }

    @Test
    public void testScenario_AverageSeesHappyPartner_EnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, EnumRelationMine.PARTNAR);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, false, false, false, false, true, false }, result);
    }

    @Test
    public void testScenario_SadSeesHappyStranger_SadAndEnvyOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.AVERAGE);
        me.setHappiness(Happiness.SAD);
        you.setHappiness(Happiness.HAPPY);
        mockRelation(me, you, EnumRelationMine.OTHER);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { false, false, true, false, false, true, false }, result);
    }

    @Test
    public void testScenario_AverageRudeSeesSadStranger_JoyAndPleasureOnly() throws Exception {
        Yukkuri me = WorldTestHelper.createBody();
        Yukkuri you = WorldTestHelper.createBody();
        registerBodies(me, you);

        me.setAttitude(Attitude.SHITHEAD);
        me.setHappiness(Happiness.AVERAGE);
        you.setHappiness(Happiness.VERY_SAD);
        mockRelation(me, you, EnumRelationMine.OTHER);
        setPainStates(you, false, false, true);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);

        assertArrayEquals(new boolean[] { true, false, false, true, false, false, false }, result);
    }

    private void registerBodies(Yukkuri... bodies) {
        for (Yukkuri b : bodies) {
            src.SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        }
    }

    private void resetRelation(Yukkuri me, Yukkuri you) throws Exception {
        me.setParents(new int[] { -1, -1 });
        you.setParents(new int[] { -1, -1 });
        me.setPartner(-1);
        you.setPartner(-1);
    }
}
