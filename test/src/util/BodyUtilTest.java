package src.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.ArrayList;

import src.SimYukkuri;
import src.base.Body;
import src.base.Okazari.OkazariType;
import src.enums.AgeState;
import src.enums.HairState;
import src.enums.Direction;
import src.enums.CriticalDamegeType;
import src.enums.CoreAnkoState;
import src.system.Sprite;
import src.system.MapPlaceData;
import src.game.Dna;

public class BodyUtilTest {

    private BufferedImage img;
    private Graphics2D g2;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
        img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2 = img.createGraphics();
        try {
            java.lang.reflect.Field imagePackField = src.yukkuri.Marisa.class.getDeclaredField("imagePack");
            imagePackField.setAccessible(true);
            BufferedImage[][][][] dummyPack = new BufferedImage[src.enums.BodyRank.values().length][200][20][20];
            BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < dummyPack.length; i++) {
                for (int j = 0; j < 200; j++) {
                    for (int k = 0; k < 20; k++) {
                        for (int l = 0; l < 20; l++) {
                            dummyPack[i][j][k][l] = dummyImage;
                        }
                    }
                }
            }
            imagePackField.set(null, dummyPack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Sprite createMockSprite() {
        Sprite s = new Sprite(100, 100, 0);
        if (s.getScreenRect() != null && s.getScreenRect().length > 1) {
            s.getScreenRect()[0].setWidth(100);
            s.getScreenRect()[0].setHeight(100);
            s.getScreenRect()[1].setWidth(100);
            s.getScreenRect()[1].setHeight(100);
        }
        return s;
    }

    private void mockSprites(Body body) throws Exception {
        java.lang.reflect.Field f1 = src.base.BodyAttributes.class.getDeclaredField("bodySpr");
        f1.setAccessible(true);
        f1.set(body, new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });

        java.lang.reflect.Field f2 = src.base.BodyAttributes.class.getDeclaredField("expandSpr");
        f2.setAccessible(true);
        f2.set(body, new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });

        java.lang.reflect.Field f3 = src.base.BodyAttributes.class.getDeclaredField("braidSpr");
        f3.setAccessible(true);
        f3.set(body, new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });
    }

    @Test
    public void testDrawBodyBasic() throws Exception {
        Body body = WorldTestHelper.createBody();
        mockSprites(body);
        BodyUtil.drawBody(g2, null, body);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDrawBodyFullCoverage() throws Exception {
        Body body = WorldTestHelper.createBody();
        mockSprites(body);

        // --- 1. Basic State Variations (Direction, Age, Force) ---
        for (Direction dir : Direction.values()) {
            body.setDirection(dir);
            for (AgeState age : AgeState.values()) {
                body.setAgeState(age);
                for (int force : new int[] { -10, 0, 10 }) {
                    body.setExtForce(force);
                    BodyUtil.drawBody(g2, null, body);
                }
            }
        }

        // --- 2. Jump/Animation Option Variations ---
        body.setZ(0);
        body.setExciting(true);
        BodyUtil.drawBody(g2, null, body);
        body.setExciting(false);

        body.setSukkiri(true);
        BodyUtil.drawBody(g2, null, body);
        body.setSukkiri(false);

        body.setNobinobi(true);
        BodyUtil.drawBody(g2, null, body);
        body.setNobinobi(false);

        body.setYunnyaa(true);
        BodyUtil.drawBody(g2, null, body);
        body.setYunnyaa(false);

        body.setFlyingType(true);
        body.setExciting(true);
        BodyUtil.drawBody(g2, null, body);
        body.setFlyingType(false);
        body.setExciting(false);

        body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        body.setExciting(true);
        BodyUtil.drawBody(g2, null, body);
        body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
        body.setExciting(false);

        // --- 3. Front View (Option 0) States ---
        body.setShitting(true);
        BodyUtil.drawBody(g2, null, body);
        body.setShitting(false);

        body.setFurifuri(true);
        BodyUtil.drawBody(g2, null, body);
        body.setbImageNagasiMode(true);
        for (int age : new int[] { 0, 2, 4 }) {
            body.setAge(age);
            BodyUtil.drawBody(g2, null, body);
        }
        body.setbImageNagasiMode(false);
        body.setFurifuri(false);

        body.setCrushed(true);
        BodyUtil.drawBody(g2, null, body);
        body.setCrushed(false);

        body.setPacked(true);
        body.setDead(true);
        BodyUtil.drawBody(g2, null, body);
        body.setDead(false);
        body.setPacked(false);

        // Birth state
        body.setBirth(true);
        body.getBabyTypes().add(new Dna());
        BodyUtil.drawBody(g2, null, body);
        body.setBirth(false);
        body.getBabyTypes().clear();

        // --- 4. Link Parent Scenarios ---
        Body parent = WorldTestHelper.createBody();
        mockSprites(parent);
        parent.setZ(-10);
        body.setZ(0);
        parent.setUniqueID(999);
        try {
            java.lang.reflect.Field mapBodyField = MapPlaceData.class.getDeclaredField("body");
            mapBodyField.setAccessible(true);
            ((Map<Integer, Body>) mapBodyField.get(SimYukkuri.world.getCurrentMap())).put(parent.getUniqueID(), parent);
        } catch (Exception e) {
        }
        body.setLinkParent(parent.getUniqueID());
        parent.setExciting(true);
        BodyUtil.drawBody(g2, null, body);
        parent.setExciting(false);
        parent.setLinkParent(-1);

        // --- 5. Abnormal States and Hair ---
        for (HairState h : HairState.values()) {
            body.seteHairState(h);
            BodyUtil.drawBody(g2, null, body);
        }

        body.setBurned(true);
        BodyUtil.drawBody(g2, null, body);
        body.setDead(true);
        BodyUtil.drawBody(g2, null, body);
        body.setDead(false);
        body.setBurned(false);

        body.addSickPeriod(5000);
        body.addDamage(1000);
        body.setAnalClose(true);
        body.setHasPants(true);
        body.setHasBraid(true);
        body.setBlind(true);
        BodyUtil.drawBody(g2, null, body);

        WorldTestHelper.setSleeping(body, true);
        BodyUtil.drawBody(g2, null, body);
        WorldTestHelper.setSleeping(body, false);

        body.takeOkazari(true);
        body.giveOkazari(OkazariType.DEFAULT);
        body.setOkazariPosition(0);
        BodyUtil.drawBody(g2, null, body);
        body.setOkazariPosition(1);
        BodyUtil.drawBody(g2, null, body);

        body.setCriticalDamege(CriticalDamegeType.CUT);
        BodyUtil.drawBody(g2, null, body);
        body.setCriticalDamege(CriticalDamegeType.INJURED);
        BodyUtil.drawBody(g2, null, body);
        body.setCriticalDamege(null);

        body.setMelt(true);
        body.setPealed(true);
        body.setBraidType(true);
        BodyUtil.drawBody(g2, null, body);
        body.setPealed(false);
        BodyUtil.drawBody(g2, null, body);
        body.setMelt(false);
        body.setBraidType(false);

        // --- 6. Direct drawBody Internal Overload ---
        body.setZ(10);
        BodyUtil.drawBody(g2, -10, 5, img, 0, 0, 100, 100, 100, 100, null);
        BodyUtil.drawBody(g2, 10, 0, img, 0, 0, 100, 100, 100, 100, null);

        // Boundary check (Translate.fieldH < y)
        try {
            java.lang.reflect.Field fieldH = src.draw.Translate.class.getDeclaredField("fieldH");
            fieldH.setAccessible(true);
            fieldH.setInt(null, 500);
            BodyUtil.drawBody(g2, 0, 0, img, 0, 600, 100, 100, 100, 100, null);
        } catch (Exception e) {
        }
    }

    @Test
    void testConstructor_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new BodyUtil());
    }
}