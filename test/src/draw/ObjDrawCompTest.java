package src.draw;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.enums.AgeState;
import src.yukkuri.Reimu;

class ObjDrawCompTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
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
        Obj o1 = new Obj();
        o1.setY(100);
        Obj o2 = new Obj();
        o2.setY(200);

        assertTrue(ObjDrawComp.getInstance().compare(o1, o2) < 0);
    }

    @Test
    void testCompareO1YGreaterThanO2YReturnsPositive() {
        Obj o1 = new Obj();
        o1.setY(300);
        Obj o2 = new Obj();
        o2.setY(100);

        assertTrue(ObjDrawComp.getInstance().compare(o1, o2) > 0);
    }

    // --- compare: same y, both non-Body ---

    @Test
    void testCompareSameYBothNonBodyReturnsZero() {
        Obj o1 = new Obj();
        o1.setY(100);
        Obj o2 = new Obj();
        o2.setY(100);

        // For non-Body objects: ordinal defaults to 1 for both, so difference is 0
        assertEquals(0, ObjDrawComp.getInstance().compare(o1, o2));
    }

    // --- compare: same y, Body with different AgeStates ---

    @Test
    void testCompareSameYBabyVsAdult() {
        // BABY ordinal = 0, ADULT ordinal = 2
        // compare returns: (o2.ageState.ordinal) - (o1.ageState.ordinal)
        // For BABY (o1) vs ADULT (o2): 2 - 0 = 2 > 0 means BABY drawn after ADULT
        Body baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // age < BABYLIMITorg -> BABY

        Body adult = new Reimu();
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
        Body adult = new Reimu();
        adult.setY(100);
        adult.setAge(100000); // ADULT

        Body baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // BABY

        // adult as o1, baby as o2: o2 ordinal (0) - o1 ordinal (2) = -2 < 0
        int result = ObjDrawComp.getInstance().compare(adult, baby);
        assertTrue(result < 0, "ADULT should be drawn before BABY (negative result)");
    }

    // --- compare: same y, one Body one Obj ---

    @Test
    void testCompareSameYBodyVsObj() {
        Body body = new Reimu();
        body.setY(100);
        body.setAge(100000); // ADULT, ordinal = 2

        Obj obj = new Obj();
        obj.setY(100);

        // body as o1, obj as o2: (1) - (2) = -1
        // o2 is non-Body so its value is 1, o1 is Body ADULT so ordinal is 2
        int result = ObjDrawComp.getInstance().compare(body, obj);
        assertTrue(result < 0, "ADULT Body drawn before non-Body Obj at same y");
    }

    @Test
    void testCompareSameYObjVsBody() {
        Obj obj = new Obj();
        obj.setY(100);

        Body body = new Reimu();
        body.setY(100);
        body.setAge(100000); // ADULT, ordinal = 2

        // obj as o1, body as o2: (2) - (1) = 1
        // o1 is non-Body so its value is 1, o2 is Body ADULT so ordinal is 2
        int result = ObjDrawComp.getInstance().compare(obj, body);
        assertTrue(result > 0, "non-Body Obj drawn after ADULT Body at same y");
    }

    @Test
    void testCompareSameYBabyBodyVsObj() {
        Body baby = new Reimu();
        baby.setY(100);
        baby.setAge(0); // BABY, ordinal = 0

        Obj obj = new Obj();
        obj.setY(100);

        // baby as o1, obj as o2: (1) - (0) = 1
        int result = ObjDrawComp.getInstance().compare(baby, obj);
        assertTrue(result > 0, "BABY Body drawn after non-Body Obj at same y");
    }
}
