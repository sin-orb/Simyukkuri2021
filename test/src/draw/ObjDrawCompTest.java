package src.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.util.WorldTestHelper;

class ObjDrawCompTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- getInstance ---

    @Test
    void testGetInstanceNonNull() {
        assertNotNull(ObjDrawComp.getInstance());
    }

    @Test
    void testGetInstanceSingleton() {
        assertSame(ObjDrawComp.getInstance(), ObjDrawComp.getInstance());
    }

    // --- compare: y ordering ---

    @Test
    void testCompareO1YLessThanO2YReturnsNegative() {
        Entity o1 = new Entity();
        o1.setY(100);
        Entity o2 = new Entity();
        o2.setY(200);

        assertTrue(ObjDrawComp.getInstance().compare(o1, o2) < 0);
    }

    @Test
    void testCompareO1YGreaterThanO2YReturnsPositive() {
        Entity o1 = new Entity();
        o1.setY(300);
        Entity o2 = new Entity();
        o2.setY(100);

        assertTrue(ObjDrawComp.getInstance().compare(o1, o2) > 0);
    }

    // --- compare: same y, both non-Yukkuri ---

    @Test
    void testCompareSameYBothNonBodyReturnsZero() {
        Entity o1 = new Entity();
        o1.setY(100);
        Entity o2 = new Entity();
        o2.setY(100);

        // For non-Yukkuri objects: ordinal defaults to 1 for both, so difference is 0
        assertEquals(0, ObjDrawComp.getInstance().compare(o1, o2));
    }

    // --- compare: same y, Yukkuri with different AgeStates ---

    @Test
    void testCompareSameYBabyVsAdult() {
        // BABY ordinal = 0, ADULT ordinal = 2
        // compare returns: (o2.ageState.ordinal) - (o1.ageState.ordinal)
        // For BABY (o1) vs ADULT (o2): 2 - 0 = 2 > 0 means BABY drawn after ADULT
        Yukkuri baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // age < BABYLIMITorg -> BABY

        Yukkuri adult = new Reimu();
        adult.setY(100);
        adult.setAge(100000); // age >= CHILDLIMITorg -> ADULT

        // Verify age states are what we expect
        assertEquals(AgeState.BABY, baby.getBodyAgeState());
        assertEquals(AgeState.ADULT, adult.getBodyAgeState());

        // baby as o1, adult as o2: o2 ordinal (2) - o1 ordinal (0) = 2 > 0
        int result = ObjDrawComp.getInstance().compare(baby, adult);
        assertTrue(result > 0, "BABY should be drawn after ADULT (positive result)");
    }

    @Test
    void testCompareSameYAdultVsBaby() {
        Yukkuri adult = new Reimu();
        adult.setY(100);
        adult.setAge(100000); // ADULT

        Yukkuri baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // BABY

        // adult as o1, baby as o2: o2 ordinal (0) - o1 ordinal (2) = -2 < 0
        int result = ObjDrawComp.getInstance().compare(adult, baby);
        assertTrue(result < 0, "ADULT should be drawn before BABY (negative result)");
    }

    // --- compare: same y, one Yukkuri one Entity ---

    @Test
    void testCompareSameYBodyVsObj() {
        Yukkuri body = new Reimu();
        body.setY(100);
        body.setAge(100000); // ADULT, ordinal = 2

        Entity obj = new Entity();
        obj.setY(100);

        // body as o1, obj as o2: (1) - (2) = -1
        // o2 is non-Yukkuri so its value is 1, o1 is Yukkuri ADULT so ordinal is 2
        int result = ObjDrawComp.getInstance().compare(body, obj);
        assertTrue(result < 0, "ADULT Yukkuri drawn before non-Yukkuri Entity at same y");
    }

    @Test
    void testCompareSameYObjVsBody() {
        Entity obj = new Entity();
        obj.setY(100);

        Yukkuri body = new Reimu();
        body.setY(100);
        body.setAge(100000); // ADULT, ordinal = 2

        // obj as o1, body as o2: (2) - (1) = 1
        // o1 is non-Yukkuri so its value is 1, o2 is Yukkuri ADULT so ordinal is 2
        int result = ObjDrawComp.getInstance().compare(obj, body);
        assertTrue(result > 0, "non-Yukkuri Entity drawn after ADULT Yukkuri at same y");
    }

    @Test
    void testCompareSameYBabyBodyVsObj() {
        Yukkuri baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // BABY, ordinal = 0

        Entity obj = new Entity();
        obj.setY(100);

        // baby as o1, obj as o2: (1) - (0) = 1
        int result = ObjDrawComp.getInstance().compare(baby, obj);
        assertTrue(result > 0, "BABY Yukkuri drawn after non-Yukkuri Entity at same y");
    }
}
