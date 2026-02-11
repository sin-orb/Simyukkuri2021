package src.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.ConstState;
import src.SimYukkuri;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Type;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class OkazariTest {

    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        // boundary配列とimages配列をリフレクションで初期化
        initOkazariImages();
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    // --- OkazariType enum ---

    @Test
    public void testOkazariTypeEnum() {
        Okazari.OkazariType[] types = Okazari.OkazariType.values();
        assertEquals(8, types.length);
        assertNull(Okazari.OkazariType.DEFAULT.getFileName());
        assertEquals("okazari_baby_01", Okazari.OkazariType.BABY1.getFileName());
        assertEquals("okazari_baby_02", Okazari.OkazariType.BABY2.getFileName());
        assertEquals("okazari_child_01", Okazari.OkazariType.CHILD1.getFileName());
        assertEquals("okazari_child_02", Okazari.OkazariType.CHILD2.getFileName());
        assertEquals("okazari_adult_01", Okazari.OkazariType.ADULT1.getFileName());
        assertEquals("okazari_adult_02", Okazari.OkazariType.ADULT2.getFileName());
        assertEquals("okazari_adult_03", Okazari.OkazariType.ADULT3.getFileName());
    }

    // --- default constructor ---

    @Test
    public void testDefaultConstructor() {
        Okazari okazari = new Okazari();
        assertNotNull(okazari);
        assertEquals(0, okazari.getValue());
    }

    // --- コンストラクタ: DEFAULT type (fileName == null) ---

    @Test
    public void testConstructorWithDefaultType() {
        Body body = createBody();

        Okazari okazari = new Okazari(body, Okazari.OkazariType.DEFAULT);

        assertEquals(body.getUniqueID(), okazari.getOwner());
        assertEquals(Okazari.OkazariType.DEFAULT, okazari.getOkazariType());
        assertNull(okazari.getOffsetPos()); // DEFAULT → offsetPos = null
        assertEquals(Type.OKAZARI, okazari.getObjType());
        assertEquals(0, okazari.getValue());
        assertEquals(0, okazari.getCost());
        // DEFAULT → setBoundary(64, 127, 128, 128)
        assertEquals(64, okazari.getPivotX());
        assertEquals(127, okazari.getPivotY());
        assertEquals(128, okazari.getW());
        assertEquals(128, okazari.getH());
    }

    // --- コンストラクタ: non-DEFAULT type, body in world ---

    @Test
    public void testConstructorWithNamedTypeAndBodyInWorld() {
        Body body = createBody();

        Okazari okazari = new Okazari(body, Okazari.OkazariType.ADULT1);

        assertEquals(body.getUniqueID(), okazari.getOwner());
        assertEquals(Okazari.OkazariType.ADULT1, okazari.getOkazariType());
        assertEquals(Type.OKAZARI, okazari.getObjType());
    }

    // --- コンストラクタ: non-DEFAULT type, body NOT in world ---

    @Test
    public void testConstructorWithNamedTypeAndBodyNotInWorld() {
        Body body = new Reimu();
        body.setAgeState(AgeState.ADULT);
        // worldに追加しない → YukkuriUtil.getBodyInstance returns null

        Okazari okazari = new Okazari(body, Okazari.OkazariType.BABY1);

        assertEquals(Okazari.OkazariType.BABY1, okazari.getOkazariType());
        assertEquals(Type.OKAZARI, okazari.getObjType());
    }

    // --- getRandomOkazari ---

    @Test
    public void testGetRandomOkazariForBaby() {
        SimYukkuri.RND = new ConstState(0);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.BABY);
        // BABY: start=BABY1.ordinal()=1, num=2, RND=0 → index=1 → BABY1
        assertEquals(Okazari.OkazariType.BABY1, type);
    }

    @Test
    public void testGetRandomOkazariForChild() {
        SimYukkuri.RND = new ConstState(1);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.CHILD);
        // CHILD: start=CHILD1.ordinal()=3, num=2, RND=1 → index=4 → CHILD2
        assertEquals(Okazari.OkazariType.CHILD2, type);
    }

    @Test
    public void testGetRandomOkazariForAdult() {
        SimYukkuri.RND = new ConstState(2);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.ADULT);
        // ADULT: start=ADULT1.ordinal()=5, num=3, RND=2 → index=7 → ADULT3
        assertEquals(Okazari.OkazariType.ADULT3, type);
    }

    @Test
    public void testGetRandomOkazariForBaby2() {
        SimYukkuri.RND = new ConstState(1);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.BABY);
        // BABY: start=BABY1.ordinal()=1, num=2, RND=1 → index=2 → BABY2
        assertEquals(Okazari.OkazariType.BABY2, type);
    }

    @Test
    public void testGetRandomOkazariForChild1() {
        SimYukkuri.RND = new ConstState(0);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.CHILD);
        // CHILD: start=CHILD1.ordinal()=3, num=2, RND=0 → index=3 → CHILD1
        assertEquals(Okazari.OkazariType.CHILD1, type);
    }

    @Test
    public void testGetRandomOkazariForAdult1() {
        SimYukkuri.RND = new ConstState(0);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.ADULT);
        // ADULT: start=ADULT1.ordinal()=5, num=3, RND=0 → index=5 → ADULT1
        assertEquals(Okazari.OkazariType.ADULT1, type);
    }

    @Test
    public void testGetRandomOkazariForAdult2() {
        SimYukkuri.RND = new ConstState(1);

        Okazari.OkazariType type = Okazari.getRandomOkazari(AgeState.ADULT);
        // ADULT: start=ADULT1.ordinal()=5, num=3, RND=1 → index=6 → ADULT2
        assertEquals(Okazari.OkazariType.ADULT2, type);
    }

    // --- getOkazariImage ---

    @Test
    public void testGetOkazariImage() {
        BufferedImage img = Okazari.getOkazariImage(Okazari.OkazariType.BABY1, 0);
        assertNotNull(img);
    }

    @Test
    public void testGetOkazariImageDirection1() {
        BufferedImage img = Okazari.getOkazariImage(Okazari.OkazariType.BABY1, 1);
        assertNotNull(img);
    }

    // --- takeOkazariOfsPos ---

    @Test
    public void testTakeOkazariOfsPosReturnsNullWhenOffsetPosNull() {
        Body body = createBody();
        Okazari okazari = new Okazari(body, Okazari.OkazariType.DEFAULT);

        // DEFAULT → offsetPos = null
        assertNull(okazari.takeOkazariOfsPos());
    }

    @Test
    public void testTakeOkazariOfsPosReturnsNullWhenOwnerNotInWorld() {
        Okazari okazari = new Okazari();
        okazari.setOwner(-1);
        okazari.setOffsetPos(new Point4y[] { new Point4y(), new Point4y(), new Point4y() });

        // owner=-1 → getBodyInstance returns null
        assertNull(okazari.takeOkazariOfsPos());
    }

    @Test
    public void testTakeOkazariOfsPosReturnsCorrectPos() {
        Body body = createBody();
        body.setAgeState(AgeState.ADULT);

        Point4y[] offsets = new Point4y[3];
        for (int i = 0; i < 3; i++) {
            offsets[i] = new Point4y();
            offsets[i].setX(i * 10);
        }

        Okazari okazari = new Okazari();
        okazari.setOwner(body.getUniqueID());
        okazari.setOffsetPos(offsets);

        Point4y result = okazari.takeOkazariOfsPos();
        assertNotNull(result);
        assertEquals(AgeState.ADULT.ordinal() * 10, result.getX());
    }

    // --- getters/setters ---

    @Test
    public void testGettersAndSetters() {
        Okazari okazari = new Okazari();

        okazari.setOwner(42);
        assertEquals(42, okazari.getOwner());

        okazari.setOkazariType(Okazari.OkazariType.CHILD1);
        assertEquals(Okazari.OkazariType.CHILD1, okazari.getOkazariType());

        Point4y[] offsets = new Point4y[] { new Point4y() };
        okazari.setOffsetPos(offsets);
        assertEquals(offsets, okazari.getOffsetPos());
    }

    // --- ヘルパー ---

    private static Body createBody() {
        Body body = new Reimu();
        body.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        body.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        return body;
    }

    private static void initOkazariImages() throws Exception {
        // images配列にダミー画像をセット
        java.lang.reflect.Field imagesField = Okazari.class.getDeclaredField("images");
        imagesField.setAccessible(true);
        BufferedImage[][] images = new BufferedImage[8][2];
        for (int i = 0; i < 8; i++) {
            for (int d = 0; d < 2; d++) {
                images[i][d] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            }
        }
        imagesField.set(null, images);

        // boundary配列にダミー境界をセット
        java.lang.reflect.Field boundaryField = Okazari.class.getDeclaredField("boundary");
        boundaryField.setAccessible(true);
        Rectangle4y[] boundary = new Rectangle4y[8];
        for (int i = 0; i < 8; i++) {
            boundary[i] = new Rectangle4y();
            boundary[i].setX(16);
            boundary[i].setY(31);
            boundary[i].setWidth(32);
            boundary[i].setHeight(32);
        }
        boundaryField.set(null, boundary);
    }
}
