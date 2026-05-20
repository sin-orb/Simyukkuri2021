package org.simyukkuri.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari.OkazariType;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.HairState;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.system.WorldState;

public class BodyUtilTest {

    private BufferedImage img;
    private Graphics2D g2;

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
        img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2 = img.createGraphics();
        try {
            java.lang.reflect.Field imagePackField = org.simyukkuri.entity.core.living.yukkuri.impl.Marisa.class
                    .getDeclaredField("imagePack");
            imagePackField.setAccessible(true);
            BufferedImage[][][][] dummyPack = new BufferedImage[org.simyukkuri.enums.YukkuriRank
                    .values().length][200][20][20];
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

    private void mockSprites(Yukkuri body) {
        body.setSpriteSet(new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });
        body.setExpandSpr(new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });
        body.setBraidSpr(new Sprite[] { createMockSprite(), createMockSprite(), createMockSprite() });
    }

    @Test
    public void testDrawBodyBasic() {
        Yukkuri body = WorldTestHelper.createBody();
        mockSprites(body);
        YukkuriUtil.drawYukkuri(g2, null, body);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDrawBodyFullCoverage() {
        Yukkuri body = WorldTestHelper.createBody();
        mockSprites(body);

        // --- 1. Basic State Variations (Direction, Age, Force) ---
        for (Direction dir : Direction.values()) {
            body.setDirection(dir);
            for (AgeState age : AgeState.values()) {
                body.setAgeState(age);
                for (int force : new int[] { -10, 0, 10 }) {
                    body.setExternalPressure(force);
                    YukkuriUtil.drawYukkuri(g2, null, body);
                }
            }
        }

        // --- 2. Jump/Animation Option Variations ---
        body.setZ(0);
        body.setExciting(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setExciting(false);

        body.setSukkiri(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setSukkiri(false);

        body.setNobinobi(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setNobinobi(false);

        body.setYunnyaa(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setYunnyaa(false);

        body.setFlyingType(true);
        body.setExciting(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setFlyingType(false);
        body.setExciting(false);

        body.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        body.setExciting(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setCoreAnkoState(CoreAnkoState.NORMAL);
        body.setExciting(false);

        // --- 3. Front View (Option 0) States ---
        body.setShitting(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setShitting(false);

        body.setFurifuri(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setImageNagasiMode(true);
        for (int age : new int[] { 0, 2, 4 }) {
            body.setAge(age);
            YukkuriUtil.drawYukkuri(g2, null, body);
        }
        body.setImageNagasiMode(false);
        body.setFurifuri(false);

        body.setCrushed(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setCrushed(false);

        body.setPacked(true);
        body.setDead(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setDead(false);
        body.setPacked(false);

        // Birth state
        body.setBirth(true);
        body.getBabyTypes().add(new Dna());
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setBirth(false);
        body.getBabyTypes().clear();

        // --- 4. Link Parent Scenarios ---
        Yukkuri parent = WorldTestHelper.createBody();
        mockSprites(parent);
        parent.setZ(-10);
        body.setZ(0);
        parent.setUniqueID(999);
        try {
            java.lang.reflect.Field mapBodyField = WorldState.class.getDeclaredField("body");
            mapBodyField.setAccessible(true);
            ((Map<Integer, Yukkuri>) mapBodyField.get(SimYukkuri.world.getCurrentWorldState())).put(
                    parent.getUniqueID(),
                    parent);
        } catch (Exception e) {
        }
        body.setParentLinkId(parent.getUniqueID());
        parent.setExciting(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        parent.setExciting(false);
        parent.setParentLinkId(-1);

        // --- 5. Abnormal States and Hair ---
        for (HairState h : HairState.values()) {
            body.setHairState(h);
            YukkuriUtil.drawYukkuri(g2, null, body);
        }

        body.setBurned(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setDead(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setDead(false);
        body.setBurned(false);

        body.addSickPeriod(5000);
        body.addDamage(1000);
        body.setAnalClose(true);
        body.setHasPants(true);
        body.setHasBraid(true);
        body.setBlind(true);
        YukkuriUtil.drawYukkuri(g2, null, body);

        WorldTestHelper.setSleeping(body, true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        WorldTestHelper.setSleeping(body, false);

        body.takeOkazari(true);
        body.giveOkazari(OkazariType.DEFAULT);
        body.setOkazariPosition(0);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setOkazariPosition(1);
        YukkuriUtil.drawYukkuri(g2, null, body);

        body.setCriticalDamege(CriticalDamageType.CUT);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setCriticalDamege(CriticalDamageType.INJURED);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setCriticalDamege(null);

        body.setMelt(true);
        body.setPealed(true);
        body.setBraidType(true);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setPealed(false);
        YukkuriUtil.drawYukkuri(g2, null, body);
        body.setMelt(false);
        body.setBraidType(false);

        // --- 6. Direct drawYukkuri Internal Overload ---
        body.setZ(10);
        YukkuriUtil.drawYukkuri(g2, -10, 5, img, 0, 0, 100, 100, 100, 100, null);
        YukkuriUtil.drawYukkuri(g2, 10, 0, img, 0, 0, 100, 100, 100, 100, null);

        // Boundary check (Translate.fieldH < y)
        try {
            java.lang.reflect.Field fieldH = org.simyukkuri.draw.Translate.class.getDeclaredField("fieldH");
            fieldH.setAccessible(true);
            fieldH.setInt(null, 500);
            YukkuriUtil.drawYukkuri(g2, 0, 0, img, 0, 600, 100, 100, 100, 100, null);
        } catch (Exception e) {
        }
    }

    @Test
    void testConstructor_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new YukkuriUtil());
    }
}
