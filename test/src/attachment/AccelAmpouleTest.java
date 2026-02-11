package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Direction;
import src.enums.Event;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class AccelAmpouleTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        AccelAmpoule.setImages(buildImages());
        AccelAmpoule.setImgW(new int[] { 10, 20, 30 });
        AccelAmpoule.setImgH(new int[] { 11, 21, 31 });
        AccelAmpoule.setPivX(new int[] { 1, 2, 3 });
        AccelAmpoule.setPivY(new int[] { 4, 5, 6 });
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("AccelAmpoule", AccelAmpoule.getPosKey());
        assertEquals(7, AccelAmpoule.getProperty().length);
        assertEquals(2, AccelAmpoule.getProperty()[0]);
        assertEquals(1, AccelAmpoule.getProperty()[2]);
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        assertEquals(parent.getUniqueID(), ampoule.getParent());
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
        assertEquals(100, ampoule.getProcessInterval());
        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testUpdateIncreasesAgeForNonAdult() {
        Body parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        long before = parent.getAge();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(before + Obj.TICK * 10000, parent.getAge());
    }

    @Test
    public void testGetImageUsesDirectionAndAge() {
        Body parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        Body holder = new Reimu();
        holder.setDirection(Direction.RIGHT);
        BufferedImage rightImage = ampoule.getImage(holder);
        assertSame(AccelAmpoule.getImages()[AgeState.CHILD.ordinal()][1], rightImage);

        holder.setDirection(Direction.LEFT);
        BufferedImage leftImage = ampoule.getImage(holder);
        assertSame(AccelAmpoule.getImages()[AgeState.CHILD.ordinal()][0], leftImage);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.BABY);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_accell_ampoule"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        AccelAmpoule ampoule = new AccelAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        // parentをworldに登録しないのでYukkuriUtil.getBodyInstanceがnullを返す
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdateReturnsNullWhenParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        assertNull(ampoule.update());
    }

    @Test
    public void testUpdateSkipsAgeForDeadParent() {
        Body parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        parent.setDead(true);

        long before = parent.getAge();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(before, parent.getAge());
    }

    @Test
    public void testUpdateSkipsAgeForAdultParent() {
        Body parent = createParent(AgeState.ADULT);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        long before = parent.getAge();
        Event result = ampoule.update();

        assertEquals(Event.DONOTHING, result);
        assertEquals(before, parent.getAge());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        Body holder = new Reimu();
        holder.setDirection(Direction.LEFT);
        assertNull(ampoule.getImage(holder));
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        // setBoundaryはコンストラクタでもスキップされてるのでデフォルト値のまま
        ampoule.resetBoundary();
        assertEquals(0, ampoule.getPivotX());
        assertEquals(0, ampoule.getPivotY());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][2];
        images[AgeState.BABY.ordinal()][0] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.BABY.ordinal()][1] = new BufferedImage(11, 11, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.CHILD.ordinal()][0] = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.CHILD.ordinal()][1] = new BufferedImage(21, 21, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.ADULT.ordinal()][0] = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.ADULT.ordinal()][1] = new BufferedImage(31, 31, BufferedImage.TYPE_INT_ARGB);
        return images;
    }
}
