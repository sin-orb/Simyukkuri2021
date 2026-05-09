package src.attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.MyPane;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.YukkuriType;
import src.game.Vomit;
import src.system.ResourceUtil;
import src.yukkuri.Reimu;

public class AntsTest {

    private static class PresetRandom extends SecureRandom {
        private int nextIntValue = 0;

        @Override
        public int nextInt(int n) {
            return nextIntValue % n;
        }

        public void setNextInt(int v) {
            nextIntValue = v;
        }
    }

    private PresetRandom testRnd;

    @BeforeEach
    public void setUp() {
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.world = new World();
        Ants.setImages(buildImages());
        Ants.setImgW(new int[] { 10, 20, 30 });
        Ants.setImgH(new int[] { 11, 21, 31 });
        Ants.setPivX(new int[] { 1, 2, 3 });
        Ants.setPivY(new int[] { 4, 5, 6 });

        testRnd = new PresetRandom();
        SimYukkuri.RND = testRnd;

        // Initialize Vomit static arrays so bodyCut/addVomit does not NPE
        try {
            int numTypes = YukkuriType.values().length;
            int numAgeStates = 3;
            java.lang.reflect.Field pivXField = Vomit.class.getDeclaredField("pivX");
            java.lang.reflect.Field pivYField = Vomit.class.getDeclaredField("pivY");
            java.lang.reflect.Field imgWField = Vomit.class.getDeclaredField("imgW");
            java.lang.reflect.Field imgHField = Vomit.class.getDeclaredField("imgH");
            java.lang.reflect.Field imagesField = Vomit.class.getDeclaredField("images");
            pivXField.setAccessible(true);
            pivYField.setAccessible(true);
            imgWField.setAccessible(true);
            imgHField.setAccessible(true);
            imagesField.setAccessible(true);
            pivXField.set(null, new int[numTypes][numAgeStates]);
            pivYField.set(null, new int[numTypes][numAgeStates]);
            imgWField.set(null, new int[numTypes][numAgeStates]);
            imgHField.set(null, new int[numTypes][numAgeStates]);
            imagesField.set(null, new BufferedImage[numTypes][Vomit.NUM_OF_VOMIT_STATE][numAgeStates]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Vomit statics", e);
        }
    }

    @Test
    public void testStaticAccessors() {
        assertEquals("Ants", Ants.getPosKey());
        assertEquals(7, Ants.getProperty().length);
        assertEquals(4, Ants.getProperty()[0]); // 赤ゆ用画像サイズ
        assertEquals(2, Ants.getProperty()[1]); // 子ゆ用画像サイズ
        assertEquals(1, Ants.getProperty()[2]); // 成ゆ用画像サイズ
    }

    @Test
    public void testConstructorDefaultsAndBoundary() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        assertEquals(parent.getUniqueID(), ants.getParent());
        assertEquals(0, ants.getValue());
        assertEquals(0, ants.getCost());
        assertEquals(100, ants.getProcessInterval());
        // CHILD index = 1, pivX[1]=2, pivY[1]=5, imgW[1]=20, imgH[1]=21
        assertEquals(2, ants.getPivotX());
        assertEquals(5, ants.getPivotY());
        assertEquals(20, ants.getW());
        assertEquals(21, ants.getH());
    }

    @Test
    public void testConstructorSetsNumOfAntsTo50() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);
        assertEquals(50, parent.getAntCount());
        // parent should be retrievable from world map
        assertSame(parent, src.util.BodyRegistry.getBodyInstance(ants.getParent()));
    }

    @Test
    public void testUpdateReturnsDoNothingWhenParentIsNull() {
        Ants ants = new Ants();
        Event result = ants.update();
        assertEquals(Event.DONOTHING, result);
    }

    @Test
    public void testUpdateCallsBeEaten() {
        Body parent = createParent(AgeState.ADULT);
        parent.setAntCount(60);
        parent.setHappiness(Happiness.HAPPY);
        parent.setDamage(0);
        parent.setHungry(0); // hungry<=0 triggers addDamage in eatBody
        parent.initAmount(AgeState.ADULT); // ankoAmount = DAMAGELIMIT[ADULT] so body is alive
        // Ensure coreAnkoState is DEFAULT so isNotNYD() is true -> happiness set to
        // VERY_SAD
        parent.setCoreAnkoState(CoreAnkoState.DEFAULT);
        // Ensure parent has a valid Shit type to avoid NPE in Vomit
        parent.setShitType(YukkuriType.REIMU);
        // Initialize MyPane to provide Terrarium for beEaten
        SimYukkuri.mypane = new MyPane();

        int bodyAmountBefore = parent.getAnkoAmount();
        Ants ants = new Ants(parent);
        ants.update();

        // pa.beEaten(60/3=20, 0, false) -> eatBody reduces ankoAmount; makeDirty sets
        // flag
        assertTrue(parent.getAnkoAmount() < bodyAmountBefore, "ankoAmount should decrease after eatBody");
        assertTrue(parent.isDirty(), "body should be marked dirty by beEaten");
        // With coreAnkoState=DEFAULT, P=0 -> setHappiness(VERY_SAD)
        assertEquals(Happiness.VERY_SAD, parent.getHappiness(), "happiness should be set to VERY_SAD by beEaten");
    }

    @Test
    public void testGetImageReturnsImage0WhenAntsLow() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        parent.setAntCount(0);
        BufferedImage img = ants.getImage(parent);
        assertSame(Ants.getImages()[parent.getBodyAgeState().ordinal()][0], img);
    }

    @Test
    public void testGetImageReturnsImage1WhenAntsMedium() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);

        // ants >= getDamageLimit()/3 -> image index 1
        parent.setAntCount(parent.getDamageLimit() / 3);
        BufferedImage img = ants.getImage(parent);
        assertSame(Ants.getImages()[parent.getBodyAgeState().ordinal()][1], img);
    }

    @Test
    public void testGetImageReturnsImage2WhenAntsHigh() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);

        // ants >= getDamageLimit()*2/3 -> image index 2
        parent.setAntCount(parent.getDamageLimit() * 2 / 3);
        BufferedImage img = ants.getImage(parent);
        assertSame(Ants.getImages()[parent.getBodyAgeState().ordinal()][2], img);
    }

    @Test
    public void testResetBoundaryUsesParentAge() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);

        // setPivX was called with {1,2,3} in setUp; ADULT index=2
        ants.resetBoundary();
        assertEquals(3, ants.getPivotX()); // pivX[ADULT=2] = 3
        assertEquals(6, ants.getPivotY()); // pivY[ADULT=2] = 6
        assertEquals(30, ants.getW()); // imgW[ADULT=2] = 30
        assertEquals(31, ants.getH()); // imgH[ADULT=2] = 31
    }

    @Test
    public void testToStringUsesResourceUtil() {
        Body parent = createParent(AgeState.ADULT);
        Ants ants = new Ants(parent);
        assertEquals(ResourceUtil.getInstance().read("item_ants"), ants.toString());
    }

    @Test
    public void testDefaultConstructor() {
        Ants ants = new Ants();
        assertEquals(0, ants.getParent());
    }

    @Test
    public void testConstructorWithParentNotInWorld() {
        Body parent = new Reimu();
        parent.setAgeState(AgeState.CHILD);
        Ants ants = new Ants(parent);
        assertEquals(0, ants.getValue());
        assertEquals(0, ants.getCost());
    }

    @Test
    public void testResetBoundaryDoesNothingWhenParentNotInMap() {
        Body parent = createParent(AgeState.CHILD);
        Ants ants = new Ants(parent);

        int origPivotX = ants.getPivotX();
        int origPivotY = ants.getPivotY();

        // Remove parent from world map so getBodyInstance returns null
        SimYukkuri.world.getCurrentMap().getBody().remove(parent.getUniqueID());
        ants.resetBoundary();

        // Boundary should remain unchanged since parent is null
        assertEquals(origPivotX, ants.getPivotX());
        assertEquals(origPivotY, ants.getPivotY());
    }

    private static Body createParent(AgeState ageState) {
        Body parent = new Reimu();
        parent.setAgeState(ageState);
        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        return parent;
    }

    private static BufferedImage[][] buildImages() {
        BufferedImage[][] images = new BufferedImage[3][3];
        for (int age = 0; age < 3; age++) {
            for (int level = 0; level < 3; level++) {
                images[age][level] = new BufferedImage(10 + age * 10, 10 + age * 10, BufferedImage.TYPE_INT_ARGB);
            }
        }
        return images;
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Ants.loadImages(Ants.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_UpdateConsumesExactlyNumOfAntsDividedByThreeFromBodyAndHungry() {
            Body parent = createParent(AgeState.ADULT);
            parent.initAmount(AgeState.ADULT);
            parent.setAntCount(9);
            parent.setHungry(100);
            int bodyAmountBefore = parent.getAnkoAmount();
            int hungryBefore = parent.getHungry();

            Ants ants = new Ants(parent);
            parent.setAntCount(9);
            testRnd.setNextInt(1);

            ants.update();

            assertEquals(bodyAmountBefore - 3, parent.getAnkoAmount());
            assertEquals(hungryBefore - 3, parent.getHungry());
            assertTrue(parent.isDirty());
            assertFalse(parent.isPurupuru());
        }

        @Test
        void testScenario_LockmoveBodyHitByAntsEntersPainPurupuruBranchWithoutReducingAntCount() {
            Body parent = createParent(AgeState.ADULT);
            parent.initAmount(AgeState.ADULT);
            parent.setAntCount(12);
            parent.setLockmove(true);
            parent.setHappiness(Happiness.HAPPY);

            Ants ants = new Ants(parent);
            parent.setAntCount(12);
            testRnd.setNextInt(0);

            ants.update();

            assertEquals(Happiness.VERY_SAD, parent.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), parent.getForceFace());
            assertTrue(parent.isPurupuru());
            assertEquals(12, parent.getAntCount());
        }
    }
}
