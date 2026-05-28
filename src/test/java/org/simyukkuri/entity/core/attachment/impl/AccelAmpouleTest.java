package org.simyukkuri.entity.core.attachment.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ResourceUtil;
import org.simyukkuri.util.WorldTestHelper;

public class AccelAmpouleTest {

    @BeforeEach
    public void setUp() throws Exception {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardAttachmentMountPoints();
        AccelAmpoule.setImages(buildImages());
        AccelAmpoule.setImgW(new int[] {10, 20, 30});
        AccelAmpoule.setImgH(new int[] {11, 21, 31});
        AccelAmpoule.setPivX(new int[] {1, 2, 3});
        AccelAmpoule.setPivY(new int[] {4, 5, 6});
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
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        assertEquals(parent.getUniqueId(), ampoule.getParent());
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
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        ampoule.setProcessInterval(1);

        long before = parent.getAge();
        TickResult result = ampoule.clockTick();

        assertEquals(TickResult.NONE, result);
        assertEquals(before + Entity.TICK * 10000, parent.getAge());
    }

    @Test
    public void testGetImageUsesDirectionAndAge() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        Yukkuri holder = new Reimu();
        holder.setDirection(Direction.RIGHT);
        BufferedImage rightImage = ampoule.getImage(holder);
        assertSame(AccelAmpoule.getImages()[AgeState.CHILD.ordinal()][1], rightImage);

        holder.setDirection(Direction.LEFT);
        BufferedImage leftImage = ampoule.getImage(holder);
        assertSame(AccelAmpoule.getImages()[AgeState.CHILD.ordinal()][0], leftImage);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        ampoule.resetBoundary();

        assertEquals(2, ampoule.getPivotX());
        assertEquals(5, ampoule.getPivotY());
        assertEquals(20, ampoule.getW());
        assertEquals(21, ampoule.getH());
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Yukkuri parent = createParent(AgeState.BABY);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        assertEquals(ResourceUtil.getInstance().read("item_accell_ampoule"), ampoule.toString());
    }

    @Test
    public void testDefaultConstructor() {
        AccelAmpoule ampoule = new AccelAmpoule();
        assertEquals(0, ampoule.getParent());
    }

    @Test
    public void testConstructorWithParentInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        assertEquals(1000, ampoule.getValue());
        assertEquals(0, ampoule.getCost());
    }

    @Test
    public void testUpdateReturnsNullWhenParentNotInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());
        ampoule.setProcessInterval(1);
        assertNull(ampoule.clockTick());
    }

    @Test
    public void testUpdateSkipsAgeForDeadParent() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        parent.setDead(true);

        long before = parent.getAge();
        TickResult result = ampoule.clockTick();

        assertEquals(TickResult.NONE, result);
        assertEquals(before, parent.getAge());
    }

    @Test
    public void testUpdateSkipsAgeForAdultParent() {
        Yukkuri parent = createParent(AgeState.ADULT);
        AccelAmpoule ampoule = new AccelAmpoule(parent);

        long before = parent.getAge();
        TickResult result = ampoule.clockTick();

        assertEquals(TickResult.NONE, result);
        assertEquals(before, parent.getAge());
    }

    @Test
    public void testGetImageReturnsNullWhenParentNotInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        Yukkuri holder = new Reimu();
        holder.setDirection(Direction.LEFT);
        assertNull(ampoule.getImage(holder));
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInWorld() {
        Yukkuri parent = createParent(AgeState.CHILD);
        AccelAmpoule ampoule = new AccelAmpoule(parent);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(parent.getUniqueId());

        int origPivotX = ampoule.getPivotX();
        int origPivotY = ampoule.getPivotY();
        ampoule.resetBoundary();
        assertEquals(origPivotX, ampoule.getPivotX());
        assertEquals(origPivotY, ampoule.getPivotY());
    }

    private static Yukkuri createParent(AgeState ageState) {
        Yukkuri parent = new Reimu() {
            private static final long serialVersionUID = 1L;

            @Override
            public Point4y[] getMountPoint(String key) {
                if ("AccelAmpoule".equals(key)) {
                    return new Point4y[] {
                            new Point4y(1, 2),
                            new Point4y(3, 4),
                            new Point4y(5, 6)
                    };
                }
                return null;
            }
        };
        parent.setAgeState(ageState);
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(parent.getUniqueId(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][2];
        images[AgeState.BABY.ordinal()][0] = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.BABY.ordinal()][1] = new BufferedImage(11, 11, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.CHILD.ordinal()][0] =
                new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.CHILD.ordinal()][1] =
                new BufferedImage(21, 21, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.ADULT.ordinal()][0] =
                new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        images[AgeState.ADULT.ordinal()][1] =
                new BufferedImage(31, 31, BufferedImage.TYPE_INT_ARGB);
        return images;
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            AccelAmpoule.loadImages(AccelAmpoule.class.getClassLoader(), null);
        } catch (Exception e) {
            // ignore
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ChildBodyGetsLargeAgeAccelerationButStaysAlive() {
            Yukkuri parent = createParent(AgeState.CHILD);
            parent.setAge(100);
            AccelAmpoule ampoule = new AccelAmpoule(parent);
            ampoule.setProcessInterval(1);

            TickResult result = ampoule.clockTick();

            assertEquals(TickResult.NONE, result);
            assertEquals(100 + Entity.TICK * 10000, parent.getAge());
            assertEquals(false, parent.isDead());
        }

        @Test
        void testScenario_AdultBodyDoesNotAgeEvenWhenAmpouleUpdates() {
            Yukkuri parent = createParent(AgeState.ADULT);
            AccelAmpoule ampoule = new AccelAmpoule(parent);
            long before = parent.getAge();

            TickResult result = ampoule.clockTick();

            assertEquals(TickResult.NONE, result);
            assertEquals(before, parent.getAge());
        }
    }
}
