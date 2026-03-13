package src.enums;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Numbering enum のテスト.
 */
public class NumberingTest {

    @BeforeEach
    public void setUp() {
        // テストごとに ID をリセット（シングルトンの状態を制御）
        Numbering.INSTANCE.setObjId(0);
        Numbering.INSTANCE.setYukkuriID(0);
    }

    public static void main(String[] args) {
        NumberingTest test = new NumberingTest();
        test.setUp();
        System.out.println("Running testNumberingObjId...");
        test.testNumberingObjId();
        System.out.println("Running testSetAndGetObjId...");
        test.testSetAndGetObjId();
        System.out.println("Running testNumberingYukkuriID...");
        test.testNumberingYukkuriID();
        System.out.println("Running testSetAndGetYukkuriID...");
        test.testSetAndGetYukkuriID();
        System.out.println("Running testSingletonInstance...");
        test.testSingletonInstance();
        System.out.println("All NumberingTest passed!");
    }

    @Test
    public void testNumberingObjId() {
        int id1 = Numbering.INSTANCE.numberingObjId();
        int id2 = Numbering.INSTANCE.numberingObjId();
        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(2, Numbering.INSTANCE.getObjId());
    }

    @Test
    public void testSetAndGetObjId() {
        Numbering.INSTANCE.setObjId(100);
        assertEquals(100, Numbering.INSTANCE.getObjId());
        int nextId = Numbering.INSTANCE.numberingObjId();
        assertEquals(101, nextId);
    }

    @Test
    public void testNumberingYukkuriID() {
        int id1 = Numbering.INSTANCE.numberingYukkuriID();
        int id2 = Numbering.INSTANCE.numberingYukkuriID();
        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(2, Numbering.INSTANCE.getYukkuriID());
    }

    @Test
    public void testSetAndGetYukkuriID() {
        Numbering.INSTANCE.setYukkuriID(500);
        assertEquals(500, Numbering.INSTANCE.getYukkuriID());
        int nextId = Numbering.INSTANCE.numberingYukkuriID();
        assertEquals(501, nextId);
    }

    @Test
    public void testSingletonInstance() {
        Numbering instance1 = Numbering.INSTANCE;
        Numbering instance2 = Numbering.valueOf("INSTANCE");
        assertSame(instance1, instance2);
    }
}
