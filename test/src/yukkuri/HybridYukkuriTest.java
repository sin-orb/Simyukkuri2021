package src.yukkuri;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HybridYukkuriTest {

    @Test
    public void testHybridYukkuriIdentity() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertEquals(20000, hybrid.getType());
    }

    @Test
    public void testHybridYukkuriIsHybrid() {
        HybridYukkuri hybrid = new HybridYukkuri();
        assertTrue(hybrid.isHybrid());
    }

    @Test
    public void testHybridYukkuriHybridType() {
        HybridYukkuri hybrid = new HybridYukkuri();
        // HybridYukkuri always returns HybridYukkuri type
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Reimu.type));
        assertEquals(HybridYukkuri.type, hybrid.getHybridType(Marisa.type));
    }

    @Test
    public void testHybridYukkuriDoreiGettersSetters() {
        HybridYukkuri hybrid = new HybridYukkuri();

        Reimu dorei1 = new Reimu();
        Marisa dorei2 = new Marisa();
        Alice dorei3 = new Alice();
        Chen dorei4 = new Chen();

        hybrid.setDorei(dorei1);
        hybrid.setDorei2(dorei2);
        hybrid.setDorei3(dorei3);
        hybrid.setDorei4(dorei4);

        assertSame(dorei1, hybrid.getDorei());
        assertSame(dorei2, hybrid.getDorei2());
        assertSame(dorei3, hybrid.getDorei3());
        assertSame(dorei4, hybrid.getDorei4());
    }

    @Test
    public void testHybridYukkuriGetBaseBody() {
        HybridYukkuri hybrid = new HybridYukkuri();

        Reimu dorei1 = new Reimu();
        Marisa dorei2 = new Marisa();
        Alice dorei3 = new Alice();
        Chen dorei4 = new Chen();

        hybrid.setDorei(dorei1);
        hybrid.setDorei2(dorei2);
        hybrid.setDorei3(dorei3);
        hybrid.setDorei4(dorei4);

        assertSame(dorei1, hybrid.getBaseBody(0));
        assertSame(dorei2, hybrid.getBaseBody(1));
        assertSame(dorei3, hybrid.getBaseBody(2));
        assertSame(dorei4, hybrid.getBaseBody(3));
    }

    @Test
    public void testHybridYukkuriNameGettersSetters() {
        HybridYukkuri hybrid = new HybridYukkuri();

        hybrid.setNameJ("TestJ");
        hybrid.setNameE("Test");
        hybrid.setNameJ2("TestJ2");
        hybrid.setNameE2("Test2");

        assertEquals("TestJ", hybrid.getNameJ());
        assertEquals("Test", hybrid.getNameE());
        assertEquals("TestJ2", hybrid.getNameJ2());
        assertEquals("Test2", hybrid.getNameE2());
    }

    @Test
    public void testHybridYukkuriImagesGetterSetter() {
        HybridYukkuri hybrid = new HybridYukkuri();

        // Images array is created in tuneParameters
        hybrid.tuneParameters();
        assertNotNull(hybrid.getImages());
    }
}
