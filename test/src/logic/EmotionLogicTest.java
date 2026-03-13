package src.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import src.SimYukkuri;
import src.base.Body;
import src.enums.EnumRelationMine;
import src.enums.Happiness;
import src.enums.Attitude;
import src.enums.Pain;
import src.enums.Damage;
import src.enums.CriticalDamegeType;
import src.util.WorldTestHelper;
import src.system.MapPlaceData;

public class EmotionLogicTest {

    private Body dummyParent;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
        dummyParent = WorldTestHelper.createBody();
        dummyParent.setUniqueID(999);
        try {
            java.lang.reflect.Field mapBodyField = MapPlaceData.class.getDeclaredField("body");
            mapBodyField.setAccessible(true);
            ((Map<Integer, Body>) mapBodyField.get(SimYukkuri.world.getCurrentMap())).put(dummyParent.getUniqueID(),
                    dummyParent);
        } catch (Exception e) {
        }
    }

    private void mockRelation(Body me, Body you, EnumRelationMine relation) throws Exception {
        java.lang.reflect.Field parentsField = src.base.BodyAttributes.class.getDeclaredField("parents");
        parentsField.setAccessible(true);

        switch (relation) {
            case FATHER:
                parentsField.set(you, new int[] { me.getUniqueID(), -1 });
                break;
            case MOTHER:
                parentsField.set(you, new int[] { -1, me.getUniqueID() });
                break;
            case PARTNAR:
                java.lang.reflect.Field partnerField = src.base.BodyAttributes.class.getDeclaredField("partner");
                partnerField.setAccessible(true);
                partnerField.set(me, you.getUniqueID());
                partnerField.set(you, me.getUniqueID());
                break;
            case CHILD_FATHER:
                parentsField.set(me, new int[] { you.getUniqueID(), -1 });
                break;
            case CHILD_MOTHER:
                parentsField.set(me, new int[] { -1, you.getUniqueID() });
                break;
            case ELDERSISTER:
                parentsField.set(me, new int[] { 999, -1 });
                parentsField.set(you, new int[] { 999, -1 });
                me.setAge(100);
                you.setAge(50);
                break;
            case YOUNGSISTER:
                parentsField.set(me, new int[] { 999, -1 });
                parentsField.set(you, new int[] { 999, -1 });
                me.setAge(50);
                you.setAge(100);
                break;
            case OTHER:
            default:
                break;
        }
    }

    private void setPainStates(Body b, boolean pain, boolean damaged, boolean critical) throws Exception {
        java.lang.reflect.Field needleField = src.base.BodyAttributes.class.getDeclaredField("bNeedled");
        needleField.setAccessible(true);
        needleField.set(b, pain);

        java.lang.reflect.Field damageField = src.base.BodyAttributes.class.getDeclaredField("damage");
        damageField.setAccessible(true);
        damageField.setInt(b, damaged ? 1000 : 0);

        b.setCriticalDamege(critical ? CriticalDamegeType.CUT : null);
    }

    @Test
    public void testCheckEmotionForOther() throws Exception {
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
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
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
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
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
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
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
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
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
        registerBodies(me, you);

        // Family in pain -> Worry + Fear
        mockRelation(me, you, EnumRelationMine.MOTHER);
        you.setHappiness(Happiness.VERY_SAD);
        setPainStates(you, true, false, false);

        boolean[] result = EmotionLogic.checkEmotionForOther(me, you);
        assertTrue(result[6], "Should worry about family in pain");
        assertTrue(result[4], "Should feel fear for family in pain");
    }

    @Test
    public void testCheckEmotion_RudeEnvyAndAnger() throws Exception {
        Body me = WorldTestHelper.createBody();
        Body you = WorldTestHelper.createBody();
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

    private void registerBodies(Body... bodies) {
        for (Body b : bodies) {
            src.SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        }
    }

    private void resetRelation(Body me, Body you) throws Exception {
        java.lang.reflect.Field parentsField = src.base.BodyAttributes.class.getDeclaredField("parents");
        parentsField.setAccessible(true);
        parentsField.set(me, new int[] { -1, -1 });
        parentsField.set(you, new int[] { -1, -1 });

        java.lang.reflect.Field partnerField = src.base.BodyAttributes.class.getDeclaredField("partner");
        partnerField.setAccessible(true);
        partnerField.set(me, -1);
        partnerField.set(you, -1);
    }
}
