package src.enums;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SimpleEnumsTest {

    @Test
    void testActionState() {
        verifyEnum(ActionState.values());
    }

    @Test
    void testBaryInUGState() {
        verifyEnum(BaryInUGState.values());
    }

    @Test
    void testBodyBake() {
        verifyEnum(BodyBake.values());
    }

    @Test
    void testBurst() {
        verifyEnum(Burst.values());
    }

    @Test
    void testCoreAnkoState() {
        verifyEnum(CoreAnkoState.values());
    }

    @Test
    void testCriticalDamegeType() {
        verifyEnum(CriticalDamegeType.values());
    }

    @Test
    void testDamage() {
        verifyEnum(Damage.values());
    }

    @Test
    void testDirection() {
        verifyEnum(Direction.values());
    }

    @Test
    void testEffectType() {
        verifyEnum(EffectType.values());
    }

    @Test
    void testEnumRelationMine() {
        verifyEnum(EnumRelationMine.values());
    }

    @Test
    void testEvent() {
        verifyEnum(Event.values());
    }

    @Test
    void testFavItemType() {
        verifyEnum(FavItemType.values());
    }

    @Test
    void testFootBake() {
        verifyEnum(FootBake.values());
    }

    @Test
    void testGatheringDirection() {
        verifyEnum(GatheringDirection.values());
    }

    @Test
    void testHairState() {
        verifyEnum(HairState.values());
    }

    @Test
    void testHappiness() {
        verifyEnum(Happiness.values());
    }

    @Test
    void testIntelligence() {
        verifyEnum(Intelligence.values());
    }

    @Test
    void testLovePlayer() {
        verifyEnum(LovePlayer.values());
    }

    @Test
    void testPain() {
        verifyEnum(Pain.values());
    }

    @Test
    void testPanicType() {
        verifyEnum(PanicType.values());
    }

    @Test
    void testParent() {
        verifyEnum(Parent.values());
    }

    @Test
    void testPlayStyle() {
        verifyEnum(PlayStyle.values());
    }

    @Test
    void testPredatorType() {
        verifyEnum(PredatorType.values());
    }

    @Test
    void testPublicRank() {
        verifyEnum(PublicRank.values());
    }

    @Test
    void testPurposeOfMoving() {
        verifyEnum(PurposeOfMoving.values());
    }

    @Test
    void testTakeoutItemType() {
        verifyEnum(TakeoutItemType.values());
    }

    @Test
    void testTangType() {
        verifyEnum(TangType.values());
    }

    @Test
    void testTrauma() {
        verifyEnum(Trauma.values());
    }

    @Test
    void testType() {
        verifyEnum(Type.values());
    }

    @Test
    void testUnbirthBabyState() {
        verifyEnum(UnbirthBabyState.values());
    }

    @Test
    void testWhere() {
        verifyEnum(Where.values());
    }

    @Test
    void testWindowType() {
        verifyEnum(WindowType.values());
    }

    private <E extends Enum<E>> void verifyEnum(E[] values) {
        assertNotNull(values, "Enum values should not be null");
        assertTrue(values.length > 0, "Enum should have at least one value");
        for (E value : values) {
            assertNotNull(value, "Enum constants should not be null");
            assertEquals(value, Enum.valueOf(value.getDeclaringClass(), value.name()), "Enum.valueOf match failed");
        }
    }
}
