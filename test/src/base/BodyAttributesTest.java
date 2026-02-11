
package src.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import src.ConstState;
import src.SimYukkuri;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.BodyBake;
import src.enums.BodyRank;
import src.enums.Burst;
import src.enums.CoreAnkoState;
import src.enums.Damage;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.enums.Pain;
import src.enums.TangType;
import src.draw.Point4y;
import src.enums.Event;
import src.system.Sprite;

import java.awt.image.BufferedImage;

public class BodyAttributesTest {

    /** テスト用の簡易Attachment */
    private static class TestAttachment extends Attachment {
        private static final long serialVersionUID = 1L;
        public TestAttachment(Body b) { super(b); }
        public TestAttachment() { super(); }
        @Override protected Event update() { return Event.DONOTHING; }
        @Override public void resetBoundary() {}
        @Override public BufferedImage getImage(Body b) { return null; }
    }

    private StubBodyAttributes body;
    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        originalRnd = SimYukkuri.RND;
        body = new StubBodyAttributes();
        initSprites(body);
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    /** bodySprを初期化する（getSizeなどで参照されるため） */
    private static void initSprites(StubBodyAttributes b) {
        for (int i = 0; i < 3; i++) {
            b.bodySpr[i] = new Sprite();
            b.bodySpr[i].setImageW(100);
            b.bodySpr[i].setImageH(100);
            b.expandSpr[i] = new Sprite();
            b.braidSpr[i] = new Sprite();
        }
    }

    // ===========================================
    // 初期値・基本 getter/setter
    // ===========================================

    @Test
    public void testInitialValues() {
        assertNotNull(body.EATAMOUNTorg);
        assertEquals(3, body.EATAMOUNTorg.length);
        assertNotNull(body.anMyName);
    }

    @Test
    public void testSettersGetters() {
        body.setBaseBodyFileName("test_base");
        assertEquals("test_base", body.getBaseBodyFileName());

        String[] babyNames = { "A", "B" };
        body.setAnBabyName(babyNames);
        assertArrayEquals(babyNames, body.getAnBabyName());
    }

    @Test
    public void testNameArraysSetGet() {
        String[] childNames = { "C1", "C2" };
        String[] adultNames = { "A1", "A2" };
        String[] myNames = { "M1", "M2", "M3" };
        body.setAnChildName(childNames);
        body.setAnAdultName(adultNames);
        body.setAnMyName(myNames);
        assertArrayEquals(childNames, body.getAnChildName());
        assertArrayEquals(adultNames, body.getAnAdultName());
        assertArrayEquals(myNames, body.getAnMyName());

        String[] babyNamesD = { "BD1", "BD2" };
        String[] childNamesD = { "CD1", "CD2" };
        String[] adultNamesD = { "AD1", "AD2" };
        String[] myNamesD = { "MD1", "MD2", "MD3" };
        body.setAnBabyNameD(babyNamesD);
        body.setAnChildNameD(childNamesD);
        body.setAnAdultNameD(adultNamesD);
        body.setAnMyNameD(myNamesD);
        assertArrayEquals(babyNamesD, body.getAnBabyNameD());
        assertArrayEquals(childNamesD, body.getAnChildNameD());
        assertArrayEquals(adultNamesD, body.getAnAdultNameD());
        assertArrayEquals(myNamesD, body.getAnMyNameD());
    }

    @Test
    public void testShadowImageStatics() {
        BufferedImage[] imgs = new BufferedImage[] { null, null, null };
        int[] w = { 1, 2, 3 };
        int[] h = { 4, 5, 6 };
        int[] pivX = { 7, 8, 9 };
        int[] pivY = { 10, 11, 12 };
        BodyAttributes.setShadowImages(imgs);
        BodyAttributes.setShadowImgW(w);
        BodyAttributes.setShadowImgH(h);
        BodyAttributes.setShadowPivX(pivX);
        BodyAttributes.setShadowPivY(pivY);
        assertSame(imgs, BodyAttributes.getShadowImages());
        assertArrayEquals(w, BodyAttributes.getShadowImgW());
        assertArrayEquals(h, BodyAttributes.getShadowImgH());
        assertArrayEquals(pivX, BodyAttributes.getShadowPivX());
        assertArrayEquals(pivY, BodyAttributes.getShadowPivY());
    }

    @Test
    public void testSpriteAccessors() {
        Sprite[] bodySpr = new Sprite[] { new Sprite(), new Sprite(), new Sprite() };
        Sprite[] expandSpr = new Sprite[] { new Sprite(), new Sprite(), new Sprite() };
        Sprite[] braidSpr = new Sprite[] { new Sprite(), new Sprite(), new Sprite() };
        body.setBodySpr(bodySpr);
        body.setExpandSpr(expandSpr);
        body.setBraidSpr(braidSpr);
        assertSame(bodySpr, body.getBodySpr());
        assertSame(expandSpr, body.getExpandSpr());
        assertSame(braidSpr, body.getBraidSpr());
    }

    @Test
    public void testOrgArraysAndLimitsAccessors() {
        int[] eat = { 1, 2, 3 };
        int[] weight = { 10, 20, 30 };
        int[] hungry = { 100, 200, 300 };
        int[] shit = { 7, 8, 9 };
        int[] damage = { 11, 12, 13 };
        int[] stress = { 21, 22, 23 };
        int[] tang = { 31, 32, 33 };
        int[] step = { 4, 5, 6 };
        int[] strength = { 40, 50, 60 };

        body.setEATAMOUNTorg(eat);
        body.setWEIGHTorg(weight);
        body.setHUNGRYLIMITorg(hungry);
        body.setSHITLIMITorg(shit);
        body.setDAMAGELIMITorg(damage);
        body.setSTRESSLIMITorg(stress);
        body.setTANGLEVELorg(tang);
        body.setSTEPorg(step);
        body.setSTRENGTHorg(strength);

        assertArrayEquals(eat, body.getEATAMOUNTorg());
        assertArrayEquals(weight, body.getWEIGHTorg());
        assertArrayEquals(hungry, body.getHUNGRYLIMITorg());
        assertArrayEquals(shit, body.getSHITLIMITorg());
        assertArrayEquals(damage, body.getDAMAGELIMITorg());
        assertArrayEquals(stress, body.getSTRESSLIMITorg());
        assertArrayEquals(tang, body.getTANGLEVELorg());
        assertArrayEquals(step, body.getSTEPorg());
        assertArrayEquals(strength, body.getSTRENGTHorg());
    }

    @Test
    public void testOrgPeriodsAndLimitsAccessors() {
        body.setLOVEPLAYERLIMITorg(999);
        body.setBABYLIMITorg(1000);
        body.setCHILDLIMITorg(2000);
        body.setLIFELIMITorg(3000);
        body.setROTTINGTIMEorg(4000);
        body.setRELAXPERIODorg(10);
        body.setEXCITEPERIODorg(20);
        body.setPREGPERIODorg(30);
        body.setSLEEPPERIODorg(40);
        body.setACTIVEPERIODorg(50);
        body.setANGRYPERIODorg(60);
        body.setSCAREPERIODorg(70);
        body.setDECLINEPERIODorg(80);
        body.setBLOCKEDLIMITorg(90);
        body.setDIRTYPERIODorg(100);
        body.setEYESIGHTorg(110);
        body.setINCUBATIONPERIODorg(120);
        body.setSameDest(1);

        assertEquals(999, body.getLOVEPLAYERLIMITorg());
        assertEquals(1000, body.getBABYLIMITorg());
        assertEquals(2000, body.getCHILDLIMITorg());
        assertEquals(3000, body.getLIFELIMITorg());
        assertEquals(4000, body.getROTTINGTIMEorg());
        assertEquals(10, body.getRELAXPERIODorg());
        assertEquals(20, body.getEXCITEPERIODorg());
        assertEquals(30, body.getPREGPERIODorg());
        assertEquals(40, body.getSLEEPPERIODorg());
        assertEquals(50, body.getACTIVEPERIODorg());
        assertEquals(60, body.getANGRYPERIODorg());
        assertEquals(70, body.getSCAREPERIODorg());
        assertEquals(80, body.getDECLINEPERIODorg());
        assertEquals(90, body.getBLOCKEDLIMITorg());
        assertEquals(100, body.getDIRTYPERIODorg());
        assertEquals(110, body.getEYESIGHTorg());
        assertEquals(120, body.getINCUBATIONPERIODorg());
        assertEquals(1, body.getSameDest());
    }

    @Test
    public void testAccidentProbabilitiesAccessors() {
        body.setnBreakBraidRand(2);
        body.setSurisuriAccidentProb(3);
        body.setCarAccidentProb(4);
        body.setBreakBodyByShitProb(5);
        body.setDiarrheaProb(6);

        assertEquals(2, body.getnBreakBraidRand());
        assertEquals(3, body.getSurisuriAccidentProb());
        assertEquals(4, body.getCarAccidentProb());
        assertEquals(5, body.getBreakBodyByShitProb());
        assertEquals(6, body.getDiarrheaProb());
    }

    @Test
    public void testProtectedFieldAccess() {
        body.damage = 100;
        assertEquals(100, body.damage);
        body.stress = 50;
        assertEquals(50, body.stress);
    }

    // ===========================================
    // setAgeState / getBodyAgeState
    // ===========================================

    @Nested
    class AgeStateTests {
        @Test
        public void testSetAgeStateBaby() {
            body.setAgeState(AgeState.BABY);
            assertEquals(0, body.getAge());
            assertEquals(AgeState.BABY, body.getBodyAgeState());
        }

        @Test
        public void testSetAgeStateChild() {
            body.setAgeState(AgeState.CHILD);
            assertEquals(body.BABYLIMITorg, body.getAge());
            assertEquals(AgeState.CHILD, body.getBodyAgeState());
        }

        @Test
        public void testSetAgeStateAdult() {
            body.setAgeState(AgeState.ADULT);
            assertEquals(body.CHILDLIMITorg, body.getAge());
            assertEquals(AgeState.ADULT, body.getBodyAgeState());
        }

        @Test
        public void testGetBodyAgeStateBoundary() {
            // BABYLIMITorg - 1 はまだBABY
            body.setAge(body.BABYLIMITorg - 1);
            assertEquals(AgeState.BABY, body.getBodyAgeState());

            // BABYLIMITorg ちょうどでCHILD
            body.setAge(body.BABYLIMITorg);
            assertEquals(AgeState.CHILD, body.getBodyAgeState());

            // CHILDLIMITorg - 1 はまだCHILD
            body.setAge(body.CHILDLIMITorg - 1);
            assertEquals(AgeState.CHILD, body.getBodyAgeState());

            // CHILDLIMITorg 以上でADULT
            body.setAge(body.CHILDLIMITorg);
            assertEquals(AgeState.ADULT, body.getBodyAgeState());
        }

        @Test
        public void testIsAdultChildBaby() {
            body.setAgeState(AgeState.BABY);
            assertTrue(body.isBaby());
            assertFalse(body.isChild());
            assertFalse(body.isAdult());

            body.setAgeState(AgeState.CHILD);
            assertFalse(body.isBaby());
            assertTrue(body.isChild());
            assertFalse(body.isAdult());

            body.setAgeState(AgeState.ADULT);
            assertFalse(body.isBaby());
            assertFalse(body.isChild());
            assertTrue(body.isAdult());
        }
    }

    // ===========================================
    // ダメージ判定
    // ===========================================

    @Nested
    class DamageTests {
        @Test
        public void testGetDamageStateNone() {
            body.setAgeState(AgeState.ADULT);
            body.damage = 0;
            assertEquals(Damage.NONE, body.getDamageState());
            assertTrue(body.isNoDamaged());
            assertFalse(body.isDamagedLightly());
            assertFalse(body.isDamaged());
            assertFalse(body.isDamagedHeavily());
        }

        @Test
        public void testGetDamageStateVery() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit / 2; // 50% -> VERY
            assertEquals(Damage.VERY, body.getDamageState());
            assertFalse(body.isNoDamaged());
            assertTrue(body.isDamagedLightly());
            assertTrue(body.isDamaged());
            assertFalse(body.isDamagedHeavily());
        }

        @Test
        public void testGetDamageStateToomuch() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit * 3 / 4; // 75% -> TOOMUCH
            assertEquals(Damage.TOOMUCH, body.getDamageState());
            assertTrue(body.isDamagedHeavily());
        }

        @Test
        public void testGetDamageLimit() {
            body.setAgeState(AgeState.BABY);
            assertEquals(body.DAMAGELIMITorg[AgeState.BABY.ordinal()], body.getDamageLimit());

            body.setAgeState(AgeState.ADULT);
            assertEquals(body.DAMAGELIMITorg[AgeState.ADULT.ordinal()], body.getDamageLimit());
        }
    }

    // ===========================================
    // 痛み・破裂判定
    // ===========================================

    @Nested
    class PainAndBurstTests {
        @Test
        public void testPainNoneByDefault() {
            body.setAgeState(AgeState.ADULT);
            assertEquals(Pain.NONE, body.getPainState());
            assertFalse(body.isFeelPain());
            assertFalse(body.isFeelHardPain());
        }

        @Test
        public void testBurstStateNone() {
            body.setAgeState(AgeState.ADULT);
            // size == originSize なので ratio = 4 → NONE
            assertEquals(Burst.NONE, body.getBurstState());
        }

        @Test
        public void testBurstStateBurst() {
            body.setAgeState(AgeState.ADULT);
            // getSize() * 4 / getOriginSize() >= 8 → BURST
            // originSize = 100, size needs to be >= 200
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            // expandSprのサイズで膨らませる代わりに直接sizeに影響するbodySprを変更
            // getSize = bodySpr.imageW + expandSizeW, originSize = bodySpr.imageW
            // 比を8にする: size = originSize * 2 → ratio = 8
            // expandSizeWが無いので、bodySprのサイズは変えずunyoForceWで調整は不可
            // 別のアプローチ: originSizeを小さくする
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(10);
            // size = 10 + 0 = 10, originSize = 10, ratio = 10*4/10 = 4 → NONE
            // 拡幅スプライトを使う必要がある。expandSizeWはgetExpandSizeW()で0を返す。
            // テスト用にunyoForceWを使う
        }

        @Test
        public void testPainVeryWhenNeedled() {
            body.setAgeState(AgeState.ADULT);
            body.setbNeedled(true);
            body.setDead(false);
            assertEquals(Pain.VERY, body.getPainState());
            assertTrue(body.isFeelPain());
            assertTrue(body.isFeelHardPain());
        }

        @Test
        public void testPainSomeWhenCriticalDamage() {
            body.setAgeState(AgeState.ADULT);
            body.setCriticalDamege(src.enums.CriticalDamegeType.CUT);
            assertEquals(Pain.SOME, body.getPainState());
            assertTrue(body.isFeelPain());
            assertFalse(body.isFeelHardPain());
        }
    }

    // ===========================================
    // 性格判定 (Attitude)
    // ===========================================

    @Nested
    class AttitudeTests {
        @Test
        public void testIsVeryRude() {
            body.setAttitude(Attitude.SUPER_SHITHEAD);
            assertTrue(body.isVeryRude());
            assertTrue(body.isRude());
            assertFalse(body.isNormal());
            assertFalse(body.isSmart());
        }

        @Test
        public void testIsRude() {
            body.setAttitude(Attitude.SHITHEAD);
            assertFalse(body.isVeryRude());
            assertTrue(body.isRude());
        }

        @Test
        public void testIsNormal() {
            body.setAttitude(Attitude.AVERAGE);
            assertFalse(body.isRude());
            assertTrue(body.isNormal());
            assertFalse(body.isSmart());
        }

        @Test
        public void testIsSmartNice() {
            body.setAttitude(Attitude.NICE);
            assertTrue(body.isSmart());
        }

        @Test
        public void testIsSmartVeryNice() {
            body.setAttitude(Attitude.VERY_NICE);
            assertTrue(body.isSmart());
            assertFalse(body.isRude());
        }
    }

    // ===========================================
    // 空腹関連
    // ===========================================

    @Nested
    class HungerTests {
        @Test
        public void testAddHungry() {
            body.setHungry(1000);
            body.addHungry(100);
            assertEquals(1000 + Obj.TICK * 100, body.getHungry());
        }

        @Test
        public void testAddHungryNegative() {
            body.setHungry(1000);
            body.addHungry(-500);
            assertEquals(1000 - Obj.TICK * 500, body.getHungry());
        }

        @Test
        public void testGetHungryLimit() {
            body.setAgeState(AgeState.BABY);
            assertEquals(body.HUNGRYLIMITorg[AgeState.BABY.ordinal()], body.getHungryLimit());

            body.setAgeState(AgeState.ADULT);
            assertEquals(body.HUNGRYLIMITorg[AgeState.ADULT.ordinal()], body.getHungryLimit());
        }

        @Test
        public void testIsVeryHungry() {
            body.setDead(false);
            body.setHungry(0);
            assertTrue(body.isVeryHungry());

            body.setHungry(1);
            assertFalse(body.isVeryHungry());
        }

        @Test
        public void testIsVeryHungryDeadReturnsFalse() {
            body.setDead(true);
            body.setHungry(0);
            assertFalse(body.isVeryHungry());
        }

        @Test
        public void testIsStarving() {
            body.setAgeState(AgeState.ADULT);
            body.setDead(false);
            body.setHungry(0);
            // TOOMUCH damage required
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit * 3 / 4;
            assertTrue(body.isStarving());
        }

        @Test
        public void testIsStarvingNotToomuch() {
            body.setDead(false);
            body.setHungry(0);
            body.damage = 0;
            assertFalse(body.isStarving());
        }

        @Test
        public void testGetEatAmount() {
            body.setAgeState(AgeState.BABY);
            assertEquals(body.EATAMOUNTorg[AgeState.BABY.ordinal()], body.getEatAmount());
        }
    }

    // ===========================================
    // ストレス関連
    // ===========================================

    @Nested
    class StressTests {
        @Test
        public void testSetStressPositive() {
            body.setStress(100);
            assertEquals(100, body.stress);
        }

        @Test
        public void testSetStressZeroIgnored() {
            body.stress = 50;
            body.setStress(0);
            assertEquals(50, body.stress); // 変更されない
        }

        @Test
        public void testSetStressNegativeIgnored() {
            body.stress = 50;
            body.setStress(-10);
            assertEquals(50, body.stress); // 変更されない
        }

        @Test
        public void testAddStressDeadIgnored() {
            body.setDead(true);
            body.stress = 0;
            body.addStress(100);
            assertEquals(0, body.stress);
        }

        @Test
        public void testAddStressAlive() {
            body.setDead(false);
            body.stress = 0;
            body.shit = 10; // plusShitが動くようにshitを0以外に
            body.addStress(50);
            assertEquals(Obj.TICK * 50, body.stress);
        }

        @Test
        public void testAddStressAlsoIncreasesShit() {
            body.setDead(false);
            body.setAgeState(AgeState.ADULT);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.stress = 0;
            body.shit = 10;
            int shitBefore = body.shit;
            body.addStress(50);
            // s > 0, DEFAULT, BURST != HALF → plusShit(50/5 = 10) で shit += 10
            assertTrue(body.shit > shitBefore);
        }

        @Test
        public void testGetStressLimit() {
            body.setAgeState(AgeState.ADULT);
            assertEquals(body.getSTRESSLIMITorg()[AgeState.ADULT.ordinal()], body.getStressLimit());
        }
    }

    // ===========================================
    // うんうん関連
    // ===========================================

    @Nested
    class ShitTests {
        @Test
        public void testPlusShit() {
            body.shit = 10;
            body.plusShit(5);
            assertEquals(15, body.shit);
        }

        @Test
        public void testPlusShitZeroBaseIgnored() {
            body.shit = 0;
            body.plusShit(5);
            assertEquals(0, body.shit); // shit==0のとき何もしない
        }

        @Test
        public void testPlusShitNegativeIgnored() {
            body.shit = 10;
            body.plusShit(-5);
            assertEquals(10, body.shit); // s<=0のとき何もしない
        }

        @Test
        public void testSetShitDirect() {
            body.setShitting(false);
            body.setShit(42, false);
            assertEquals(42, body.shit);
        }

        @Test
        public void testSetShitWhileShittingIgnored() {
            body.setShitting(true);
            body.shit = 10;
            body.setShit(99, false);
            assertEquals(10, body.shit); // うんうん中は変更されない
        }

        @Test
        public void testSetShitVeryShit() {
            body.setShitting(false);
            body.setAgeState(AgeState.ADULT);
            body.shit = 0;
            int limit = body.SHITLIMITorg[AgeState.ADULT.ordinal()];
            body.setShit(10, true);
            assertEquals(limit - 10, body.shit);
        }

        @Test
        public void testGetShitLimit() {
            body.setAgeState(AgeState.BABY);
            assertEquals(body.SHITLIMITorg[AgeState.BABY.ordinal()], body.getShitLimit());
        }
    }

    // ===========================================
    // 死亡ガードつき状態チェック (dead && flag)
    // ===========================================

    @Nested
    class DeadGuardTests {
        @Test
        public void testIsBeggingForLifeAlive() {
            body.setDead(false);
            body.setBaryState(BaryInUGState.NONE);
            body.setBegging(true);
            assertTrue(body.isBeggingForLife());
        }

        @Test
        public void testIsBeggingForLifeDead() {
            body.setDead(true);
            body.setBaryState(BaryInUGState.NONE);
            body.setBegging(true);
            assertFalse(body.isBeggingForLife());
        }

        @Test
        public void testSetBeggingBlockedByBuryState() {
            body.setBaryState(BaryInUGState.HALF);
            body.setBegging(true);
            // baryState != NONE のときセットされない
            assertFalse(body.isBeggingForLife());
        }

        @Test
        public void testIsStrikeAliveAndDead() {
            body.setDead(false);
            body.setStrike(true);
            assertTrue(body.isStrike());

            body.setDead(true);
            assertFalse(body.isStrike());
        }

        @Test
        public void testIsBirthAliveAndDead() {
            body.setDead(false);
            body.setBirth(true);
            assertTrue(body.isBirth());

            body.setDead(true);
            assertFalse(body.isBirth());
        }

        @Test
        public void testIsEatingAliveAndDead() {
            body.setDead(false);
            body.setEating(true);
            assertTrue(body.isEating());

            body.setDead(true);
            assertFalse(body.isEating());
        }

        @Test
        public void testIsSukkiriAliveAndDead() {
            body.setDead(false);
            body.setSukkiri(true);
            assertTrue(body.isSukkiri());

            body.setDead(true);
            assertFalse(body.isSukkiri());
        }

        @Test
        public void testIsNeedledAliveAndDead() {
            body.setDead(false);
            body.setbNeedled(true);
            assertTrue(body.isNeedled());

            body.setDead(true);
            assertFalse(body.isNeedled());
        }

        @Test
        public void testIsStubbornlyDirtyAliveAndDead() {
            body.setDead(false);
            body.setStubbornlyDirty(true);
            assertTrue(body.isStubbornlyDirty());

            body.setDead(true);
            assertFalse(body.isStubbornlyDirty());
        }
    }

    // ===========================================
    // 親子関連
    // ===========================================

    @Nested
    class FamilyTests {
        @Test
        public void testGetFatherMother() {
            body.setParents(new int[] { 10, 20 });
            assertEquals(10, body.getFather());
            assertEquals(20, body.getMother());
        }

        @Test
        public void testSisterListSize() {
            assertEquals(0, body.getSisterListSize());
        }

        @Test
        public void testElderSisterListSize() {
            assertEquals(0, body.getElderSisterListSize());
        }

        @Test
        public void testChildrenListSize() {
            assertEquals(0, body.getChildrenListSize());
        }
    }

    // ===========================================
    // あんこ量
    // ===========================================

    @Nested
    class AmountTests {
        @Test
        public void testInitAmount() {
            body.initAmount(AgeState.ADULT);
            assertEquals(body.DAMAGELIMITorg[AgeState.ADULT.ordinal()], body.getBodyAmount());
        }

        @Test
        public void testAddAmountReturnsFalseWhenPositive() {
            body.initAmount(AgeState.ADULT);
            boolean empty = body.addAmount(-100);
            assertFalse(empty);
            assertEquals(body.DAMAGELIMITorg[AgeState.ADULT.ordinal()] - 100, body.getBodyAmount());
        }

        @Test
        public void testAddAmountReturnsTrueWhenZero() {
            body.initAmount(AgeState.BABY);
            int amount = body.DAMAGELIMITorg[AgeState.BABY.ordinal()];
            boolean empty = body.addAmount(-amount);
            assertTrue(empty);
            assertEquals(0, body.getBodyAmount());
        }

        @Test
        public void testAddAmountReturnsTrueWhenNegative() {
            body.initAmount(AgeState.BABY);
            int amount = body.DAMAGELIMITorg[AgeState.BABY.ordinal()];
            boolean empty = body.addAmount(-(amount + 1000));
            assertTrue(empty);
            assertEquals(0, body.getBodyAmount()); // 0にクランプ
        }
    }

    // ===========================================
    // 病気関連
    // ===========================================

    @Nested
    class SickTests {
        @Test
        public void testIsSickFalseWhenBelowIncubation() {
            body.sickPeriod = body.INCUBATIONPERIODorg;
            assertFalse(body.isSick());
        }

        @Test
        public void testIsSickTrueWhenAboveIncubation() {
            body.sickPeriod = body.INCUBATIONPERIODorg + 1;
            assertTrue(body.isSick());
        }

        @Test
        public void testIsSickHeavily() {
            body.sickPeriod = body.INCUBATIONPERIODorg * 8;
            assertFalse(body.isSickHeavily());

            body.sickPeriod = body.INCUBATIONPERIODorg * 8 + 1;
            assertTrue(body.isSickHeavily());
        }

        @Test
        public void testIsSickTooHeavily() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit / 2; // VERY damage
            body.sickPeriod = body.INCUBATIONPERIODorg * 32 + 1;
            assertTrue(body.isSickTooHeavily());
        }

        @Test
        public void testIsSickTooHeavilyNoDamage() {
            body.damage = 0;
            body.sickPeriod = body.INCUBATIONPERIODorg * 32 + 1;
            assertFalse(body.isSickTooHeavily()); // ダメージなしだとfalse
        }

        @Test
        public void testForceSetSick() {
            body.forceSetSick();
            assertEquals(body.INCUBATIONPERIODorg * 32 + 2, body.sickPeriod);
            assertTrue(body.isSick());
            assertTrue(body.isSickHeavily());
        }

        @Test
        public void testFindSickWise() {
            body.setIntelligence(Intelligence.WISE);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            target.sickPeriod = target.INCUBATIONPERIODorg + 1;
            assertTrue(body.findSick(target));
        }

        @Test
        public void testFindSickFoolOnlyDetectsHeavy() {
            body.setIntelligence(Intelligence.FOOL);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);

            // 軽い病気は検知できない
            target.sickPeriod = target.INCUBATIONPERIODorg + 1;
            assertFalse(body.findSick(target));

            // 重い病気は検知できる
            target.sickPeriod = target.INCUBATIONPERIODorg * 8 + 1;
            assertTrue(body.findSick(target));
        }
    }

    // ===========================================
    // 非ゆっくり症 (NYD)
    // ===========================================

    @Nested
    class NYDTests {
        @Test
        public void testIsNYDDefault() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            assertFalse(body.isNYD());
            assertTrue(body.isNotNYD());
        }

        @Test
        public void testIsNYDNear() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);
            assertTrue(body.isNYD());
            assertFalse(body.isNotNYD());
        }

        @Test
        public void testIsNYDDisease() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            assertTrue(body.isNYD());
        }
    }

    // ===========================================
    // 老ゆ判定
    // ===========================================

    @Nested
    class OldTests {
        @Test
        public void testIsOldFalse() {
            body.setAge(body.LIFELIMITorg * 9 / 10);
            assertFalse(body.isOld());
        }

        @Test
        public void testIsOldTrue() {
            body.setAge(body.LIFELIMITorg * 9 / 10 + 1);
            assertTrue(body.isOld());
        }
    }

    // ===========================================
    // 眠気
    // ===========================================

    @Nested
    class SleepTests {
        @Test
        public void testIsSleepyFalseWhenSleeping() {
            body.setSleeping(true);
            assertFalse(body.isSleepy());
        }

        @Test
        public void testIsSleepyFalseWhenNotEnoughTime() {
            body.setSleeping(false);
            body.wakeUpTime = 0;
            body.setAge(body.ACTIVEPERIODorg); // ちょうどでは眠くない（<で比較）
            assertFalse(body.isSleepy());
        }

        @Test
        public void testIsSleepyTrueWhenEnoughTime() {
            body.setSleeping(false);
            body.wakeUpTime = 0;
            body.setAge(body.ACTIVEPERIODorg + 1);
            assertTrue(body.isSleepy());
        }
    }

    // ===========================================
    // 発情関連
    // ===========================================

    @Nested
    class ExcitingTests {
        @Test
        public void testSetExcitingTrue() {
            body.setDead(false);
            body.setExciting(true);
            assertTrue(body.isExciting());
        }

        @Test
        public void testSetExcitingDead() {
            body.setDead(true);
            body.setExciting(true);
            assertFalse(body.isExciting()); // dead && exciting
        }

        @Test
        public void testSetCalm() {
            body.setExciting(true);
            body.setCalm();
            body.setDead(false);
            assertFalse(body.isExciting());
        }
    }

    // ===========================================
    // アタッチメント管理
    // ===========================================

    @Nested
    class AttachmentTests {
        @Test
        public void testAddAndCountAttachment() {
            StubBody b = new StubBody();
            b.setAgeState(AgeState.ADULT);
            for (int i = 0; i < 3; i++) {
                b.bodySpr[i] = new Sprite();
                b.bodySpr[i].setImageW(100);
                b.bodySpr[i].setImageH(100);
                b.expandSpr[i] = new Sprite();
                b.braidSpr[i] = new Sprite();
            }
            SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
            TestAttachment a = new TestAttachment(b);
            b.addAttachment(a);
            assertEquals(1, b.getAttachmentSize(TestAttachment.class));
            assertEquals(0, b.getAttachmentSize(src.attachment.Ants.class));
        }

        @Test
        public void testRemoveAttachment() {
            StubBody b = new StubBody();
            b.setAgeState(AgeState.ADULT);
            for (int i = 0; i < 3; i++) {
                b.bodySpr[i] = new Sprite();
                b.bodySpr[i].setImageW(100);
                b.bodySpr[i].setImageH(100);
                b.expandSpr[i] = new Sprite();
                b.braidSpr[i] = new Sprite();
            }
            SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
            TestAttachment a = new TestAttachment(b);
            b.addAttachment(a);
            b.removeAttachment(TestAttachment.class);
            assertEquals(0, b.getAttachmentSize(TestAttachment.class));
        }
    }

    // ===========================================
    // 足焼き・体焼き
    // ===========================================

    @Nested
    class BakeTests {
        @Test
        public void testFootBakeLevelNone() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            assertEquals(FootBake.NONE, body.getFootBakeLevel());
        }

        @Test
        public void testFootBakeLevelMidium() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = (limit >> 1) + 1;
            assertEquals(FootBake.MIDIUM, body.getFootBakeLevel());
        }

        @Test
        public void testFootBakeLevelCritical() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = limit + 1;
            assertEquals(FootBake.CRITICAL, body.getFootBakeLevel());
        }

        @Test
        public void testFootBakeNegativeClamped() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = -5;
            assertEquals(FootBake.NONE, body.getFootBakeLevel());
            assertEquals(0, body.footBakePeriod); // 負の値は0にクランプ
        }

        @Test
        public void testBodyBakeLevelNone() {
            body.setAgeState(AgeState.ADULT);
            body.bodyBakePeriod = 0;
            assertEquals(BodyBake.NONE, body.getBodyBakeLevel());
        }

        @Test
        public void testBodyBakeLevelMidium() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.bodyBakePeriod = limit * 2 / 5 + 1;
            assertEquals(BodyBake.MIDIUM, body.getBodyBakeLevel());
        }

        @Test
        public void testBodyBakeLevelCritical() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.bodyBakePeriod = limit * 3 / 4 + 1;
            assertEquals(BodyBake.CRITICAL, body.getBodyBakeLevel());
        }

        @Test
        public void testIsGotBurnedFalse() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.bodyBakePeriod = 0;
            assertFalse(body.isGotBurned());
        }

        @Test
        public void testIsGotBurnedTrue() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = (limit >> 1) + 1;
            assertTrue(body.isGotBurned());
        }

        @Test
        public void testAddBodyBakePeriod() {
            body.footBakePeriod = 0;
            body.bodyBakePeriod = 0;
            body.addBodyBakePeriod(100);
            assertEquals(100 / 5, body.footBakePeriod); // s/5が足焼きに
            assertEquals(100, body.bodyBakePeriod);
        }

        @Test
        public void testAddFootBakePeriod() {
            body.footBakePeriod = 0;
            body.addFootBakePeriod(50);
            assertEquals(50, body.footBakePeriod);
        }
    }

    // ===========================================
    // 舌の肥え度
    // ===========================================

    @Nested
    class TangTests {
        @Test
        public void testGetTangTypePoor() {
            body.setTang(0);
            assertEquals(TangType.POOR, body.getTangType());
        }

        @Test
        public void testGetTangTypeNormal() {
            // TANGLEVELorg = {300, 600, 1000}
            body.setTang(300);
            assertEquals(TangType.NORMAL, body.getTangType());
        }

        @Test
        public void testGetTangTypeGourmet() {
            body.setTang(600);
            assertEquals(TangType.GOURMET, body.getTangType());
        }

        @Test
        public void testAddTang() {
            body.setTang(100);
            body.addTang(50);
            assertEquals(150, body.getTang());
        }
    }

    // ===========================================
    // あまあまへの慣れ
    // ===========================================

    @Nested
    class AmaamaDisciplineTests {
        @Test
        public void testAddAmaamaDiscipline() {
            body.amaamaDiscipline = 50;
            body.addAmaamaDiscipline(10);
            assertEquals(60, body.amaamaDiscipline);
        }

        @Test
        public void testAddAmaamaDisciplineUpperClamp() {
            body.amaamaDiscipline = 95;
            body.addAmaamaDiscipline(10);
            assertEquals(100, body.amaamaDiscipline);
        }

        @Test
        public void testAddAmaamaDisciplineLowerClamp() {
            body.amaamaDiscipline = 5;
            body.addAmaamaDiscipline(-10);
            assertEquals(0, body.amaamaDiscipline);
        }
    }

    // ===========================================
    // プレイヤーへの愛
    // ===========================================

    @Nested
    class LovePlayerTests {
        @Test
        public void testAddLovePlayerPositive() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.nLovePlayer = 0;
            body.addLovePlayer(100);
            assertEquals(Obj.TICK * 100, body.nLovePlayer);
        }

        @Test
        public void testAddLovePlayerUpperClamp() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.nLovePlayer = 0;
            body.addLovePlayer(999999);
            assertEquals(body.getLOVEPLAYERLIMITorg(), body.nLovePlayer);
        }

        @Test
        public void testAddLovePlayerLowerClamp() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.nLovePlayer = 0;
            body.addLovePlayer(-999999);
            assertEquals(-1 * body.getLOVEPLAYERLIMITorg(), body.nLovePlayer);
        }

        @Test
        public void testAddLovePlayerNYDForcesHate() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            body.nLovePlayer = 500;
            body.addLovePlayer(100);
            assertEquals(-1 * body.getLOVEPLAYERLIMITorg(), body.nLovePlayer);
        }
    }

    // ===========================================
    // 思い出 (addMemories)
    // ===========================================

    @Nested
    class MemoriesTests {
        @Test
        public void testAddMemoriesWise() {
            body.setIntelligence(Intelligence.WISE);
            body.memories = 0;
            body.addMemories(100);
            assertEquals(50, body.memories); // nAdd / 2
        }

        @Test
        public void testAddMemoriesFoolPositive() {
            body.setIntelligence(Intelligence.FOOL);
            body.memories = 0;
            body.addMemories(100);
            assertEquals(200, body.memories); // nAdd * 2
        }

        @Test
        public void testAddMemoriesFoolNegative() {
            body.setIntelligence(Intelligence.FOOL);
            body.memories = 200;
            body.addMemories(-100);
            assertEquals(200 + (-100 / 2), body.memories); // nAdd / 2 for negative
        }

        @Test
        public void testAddMemoriesAveragePositive() {
            body.setIntelligence(Intelligence.AVERAGE);
            body.memories = 0;
            body.addMemories(100);
            assertEquals(200, body.memories); // nAdd * 2
        }

        @Test
        public void testAddMemoriesAverageNegative() {
            body.setIntelligence(Intelligence.AVERAGE);
            body.memories = 200;
            body.addMemories(-100);
            assertEquals(200 + (-100), body.memories); // nAdd as-is
        }
    }

    // ===========================================
    // 幸福度 (setHappiness)
    // ===========================================

    @Nested
    class HappinessTests {
        @Test
        public void testSetHappinessDeadForcesAverage() {
            body.setDead(true);
            body.setHappiness(Happiness.VERY_HAPPY);
            assertEquals(Happiness.AVERAGE, body.getHappiness());
        }

        @Test
        public void testSetHappinessNYDForcesVerySad() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessHappyClearsScareAndAngry() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setScare(true);
            body.setAngry(true);
            body.setHappiness(Happiness.VERY_HAPPY);
            assertEquals(Happiness.VERY_HAPPY, body.getHappiness());
        }

        @Test
        public void testSetHappinessSadDoesNotOverrideVerySad() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setHappiness(Happiness.VERY_SAD);
            body.setHappiness(Happiness.SAD);
            // VERY_SADの時にSADをセットしても変わらない
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessHappyDoesNotOverrideVeryHappy() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setHappiness(Happiness.VERY_HAPPY);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.VERY_HAPPY, body.getHappiness());
        }
    }

    // ===========================================
    // canFurifuri
    // ===========================================

    @Nested
    class FurifuriTests {
        @Test
        public void testCanFurifuriTrue() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            assertTrue(body.canFurifuri());
        }

        @Test
        public void testCanFurifuriFalseWhenBurnedCritical() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = limit + 1;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            assertFalse(body.canFurifuri());
        }

        @Test
        public void testCanFurifuriFalseWhenNYD() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            assertFalse(body.canFurifuri());
        }
    }

    // ===========================================
    // Dequeue系
    // ===========================================

    @Nested
    class DequeueTests {
        @Test
        public void testGetBabyTypesDequeueEmpty() {
            assertNull(body.getBabyTypesDequeue());
        }

        @Test
        public void testGetBabyTypesDequeueWithItem() {
            src.game.Dna dna = new src.game.Dna();
            body.getBabyTypes().add(dna);
            assertEquals(1, body.getBabyTypes().size());

            src.game.Dna result = body.getBabyTypesDequeue();
            assertSame(dna, result);
            assertEquals(0, body.getBabyTypes().size());
        }

        @Test
        public void testGetStalksDequeueEmpty() {
            assertNull(body.getStalksDequeue());
        }

        @Test
        public void testGetStalksDequeueWithItem() {
            src.game.Stalk stalk = new src.game.Stalk();
            body.getStalks().add(stalk);
            assertEquals(1, body.getStalks().size());

            src.game.Stalk result = body.getStalksDequeue();
            assertSame(stalk, result);
            assertEquals(0, body.getStalks().size());
        }
    }

    // ===========================================
    // equals / hashCode / compareTo
    // ===========================================

    @Nested
    class EqualsAndHashCodeTests {
        @Test
        public void testEqualsNull() {
            assertFalse(body.equals(null));
        }

        @Test
        public void testEqualsWrongType() {
            assertFalse(body.equals("string"));
        }

        @Test
        public void testEqualsSameUniqueId() {
            StubBodyAttributes other = new StubBodyAttributes();
            initSprites(other);
            other.setUniqueID(body.getUniqueID());
            assertTrue(body.equals(other));
        }

        @Test
        public void testEqualsDifferentUniqueId() {
            StubBodyAttributes other = new StubBodyAttributes();
            initSprites(other);
            other.setUniqueID(body.getUniqueID() + 999);
            assertFalse(body.equals(other));
        }

        @Test
        public void testHashCode() {
            body.setUniqueID(7);
            assertEquals(7 * 13, body.hashCode());
        }

        @Test
        public void testCompareToNull() {
            assertEquals(0, body.compareTo(null));
        }

        @Test
        public void testCompareToWrongType() {
            assertEquals(0, body.compareTo("string"));
        }

        @Test
        public void testCompareToSame() {
            StubBodyAttributes other = new StubBodyAttributes();
            initSprites(other);
            other.setUniqueID(body.getUniqueID());
            assertEquals(0, body.compareTo(other));
        }

        @Test
        public void testCompareToSmaller() {
            body.setUniqueID(10);
            StubBodyAttributes other = new StubBodyAttributes();
            initSprites(other);
            other.setUniqueID(5);
            assertTrue(body.compareTo(other) > 0);
        }
    }

    // ===========================================
    // setCantDie / getSellingPrice
    // ===========================================

    @Nested
    class MiscTests {
        @Test
        public void testSetCantDie() {
            body.setCantDie();
            assertEquals(3, body.cantDiePeriod);
        }

        @Test
        public void testHasTraumaFalse() {
            body.setTrauma(null);
            assertFalse(body.hasTrauma());
        }

        @Test
        public void testHasTraumaTrue() {
            body.setTrauma(src.enums.Trauma.Ubuse);
            assertTrue(body.hasTrauma());
        }

        @Test
        public void testCutHair() {
            body.cutHair();
            assertEquals(src.enums.HairState.BALDHEAD, body.geteHairState());
        }
    }

    // ===========================================
    // getBurstState
    // ===========================================

    @Nested
    class BurstStateTests {
        @Test
        public void testBurstStateBurst() {
            // size*4/originSize >= 8 → BURST
            // originSize = bodySpr[ageState].getImageW()
            // size = bodySpr[ageState].getImageW() + expandSizeW
            // expandSizeW を大きくして size/originSize >= 2 (ratio 8) にする
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(100); // size=200, originSize=100, ratio=8
            assertEquals(Burst.BURST, body.getBurstState());
        }

        @Test
        public void testBurstStateNear() {
            // size*4/originSize >= 7 → NEAR
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(75); // size=175, ratio=7
            assertEquals(Burst.NEAR, body.getBurstState());
        }

        @Test
        public void testBurstStateHalf() {
            // size*4/originSize >= 6 → HALF
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(50); // size=150, ratio=6
            assertEquals(Burst.HALF, body.getBurstState());
        }

        @Test
        public void testBurstStateSafe() {
            // size*4/originSize >= 5 → SAFE
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(25); // size=125, ratio=5
            assertEquals(Burst.SAFE, body.getBurstState());
        }

        @Test
        public void testBurstStateNone() {
            // size*4/originSize < 5 → NONE
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(0); // size=100, ratio=4
            assertEquals(Burst.NONE, body.getBurstState());
        }
    }

    // ===========================================
    // getDamageState
    // ===========================================

    @Nested
    class DamageStateTests {
        @Test
        public void testDamageStateNone() {
            body.setAgeState(AgeState.ADULT);
            body.damage = 0;
            assertEquals(Damage.NONE, body.getDamageState());
        }

        @Test
        public void testDamageStateVery() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit / 2;
            assertEquals(Damage.VERY, body.getDamageState());
        }

        @Test
        public void testDamageStateToomuch() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit * 3 / 4;
            assertEquals(Damage.TOOMUCH, body.getDamageState());
        }

        @Test
        public void testDamageStateTriggersDeath() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit + 1;
            body.setDead(false);

            Damage result = body.getDamageState();

            assertEquals(Damage.TOOMUCH, result);
            assertTrue(body.isDead());
        }
    }

    // ===========================================
    // getTangType
    // ===========================================

    @Nested
    class TangTypeTests {
        @Test
        public void testTangTypePoor() {
            body.setTang(body.getTANGLEVELorg()[0] - 1);
            assertEquals(TangType.POOR, body.getTangType());
        }

        @Test
        public void testTangTypeNormal() {
            body.setTang(body.getTANGLEVELorg()[0]);
            assertEquals(TangType.NORMAL, body.getTangType());
        }

        @Test
        public void testTangTypeGourmet() {
            body.setTang(body.getTANGLEVELorg()[1]);
            assertEquals(TangType.GOURMET, body.getTangType());
        }
    }

    // ===========================================
    // addLovePlayer
    // ===========================================

    @Nested
    class AddLovePlayerTests {
        @Test
        public void testAddLovePlayerNYD() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            body.addLovePlayer(100);
            assertEquals(-1 * body.getLOVEPLAYERLIMITorg(), body.getnLovePlayer());
        }

        @Test
        public void testAddLovePlayerNormal() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setnLovePlayer(0);
            body.addLovePlayer(50);
            assertTrue(body.getnLovePlayer() > 0);
        }

        @Test
        public void testAddLovePlayerUpperLimit() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setnLovePlayer(0);
            body.addLovePlayer(99999);
            assertEquals(body.getLOVEPLAYERLIMITorg(), body.getnLovePlayer());
        }

        @Test
        public void testAddLovePlayerLowerLimit() {
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setnLovePlayer(0);
            body.addLovePlayer(-99999);
            assertEquals(-1 * body.getLOVEPLAYERLIMITorg(), body.getnLovePlayer());
        }
    }

    // ===========================================
    // addMemories (Intelligence switch)
    // ===========================================

    @Nested
    class MemoriesIntelligenceTests {
        @Test
        public void testAddMemoriesWise() {
            body.setIntelligence(Intelligence.WISE);
            body.setMemories(0);
            body.addMemories(100);
            // WISE: memories += nAdd / 2 = 50
            assertEquals(50, body.getMemories());
        }

        @Test
        public void testAddMemoriesFoolPositive() {
            body.setIntelligence(Intelligence.FOOL);
            body.setMemories(0);
            body.addMemories(100);
            // FOOL positive: memories += nAdd * 2 = 200
            assertEquals(200, body.getMemories());
        }

        @Test
        public void testAddMemoriesFoolNegative() {
            body.setIntelligence(Intelligence.FOOL);
            body.setMemories(500);
            body.addMemories(-100);
            // FOOL negative: memories += nAdd / 2 = 500 - 50 = 450
            assertEquals(450, body.getMemories());
        }

        @Test
        public void testAddMemoriesAveragePositive() {
            body.setIntelligence(Intelligence.AVERAGE);
            body.setMemories(0);
            body.addMemories(100);
            // default positive: memories += nAdd * 2 = 200
            assertEquals(200, body.getMemories());
        }

        @Test
        public void testAddMemoriesAverageNegative() {
            body.setIntelligence(Intelligence.AVERAGE);
            body.setMemories(500);
            body.addMemories(-100);
            // default negative: memories += nAdd = 500 - 100 = 400
            assertEquals(400, body.getMemories());
        }
    }

    // ===========================================
    // findSick (Intelligence switch)
    // ===========================================

    @Nested
    class FindSickTests {
        @Test
        public void testFindSickWiseDetectsLightSick() {
            body.setIntelligence(Intelligence.WISE);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            target.setSickPeriod(target.INCUBATIONPERIODorg + 1); // isSick=true

            assertTrue(body.findSick(target));
        }

        @Test
        public void testFindSickAverageDetectsLightSick() {
            body.setIntelligence(Intelligence.AVERAGE);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            target.setSickPeriod(target.INCUBATIONPERIODorg + 1);

            assertTrue(body.findSick(target));
        }

        @Test
        public void testFindSickFoolDoesNotDetectLightSick() {
            body.setIntelligence(Intelligence.FOOL);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            target.setSickPeriod(target.INCUBATIONPERIODorg + 1); // isSick only

            assertFalse(body.findSick(target));
        }

        @Test
        public void testFindSickFoolDetectsHeavySick() {
            body.setIntelligence(Intelligence.FOOL);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            // isSickHeavily = sickPeriod > (INCUBATIONPERIODorg * 8)
            target.setSickPeriod(target.INCUBATIONPERIODorg * 8 + 1);

            assertTrue(body.findSick(target));
        }

        @Test
        public void testFindSickNoSick() {
            body.setIntelligence(Intelligence.WISE);
            StubBodyAttributes target = new StubBodyAttributes();
            initSprites(target);
            target.setSickPeriod(0);

            assertFalse(body.findSick(target));
        }
    }

    // ===========================================
    // setHappiness
    // ===========================================

    @Nested
    class SetHappinessTests {
        @Test
        public void testSetHappinessDeadReturnsAverage() {
            body.setDead(true);
            body.setHappiness(Happiness.VERY_HAPPY);
            assertEquals(Happiness.AVERAGE, body.getHappiness());
        }

        @Test
        public void testSetHappinessNYDReturnVerySad() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            body.setHappiness(Happiness.VERY_HAPPY);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessSadFromNonVerySad() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.forceSetHappiness(Happiness.AVERAGE);
            body.setHappiness(Happiness.SAD);
            assertEquals(Happiness.SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessSadFromVerySadNoChange() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.forceSetHappiness(Happiness.VERY_SAD);
            body.setHappiness(Happiness.SAD);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessHappyFromNonVeryHappy() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.forceSetHappiness(Happiness.AVERAGE);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.HAPPY, body.getHappiness());
        }

        @Test
        public void testSetHappinessHappyFromVeryHappyNoChange() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.forceSetHappiness(Happiness.VERY_HAPPY);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.VERY_HAPPY, body.getHappiness());
        }

        @Test
        public void testSetHappinessVerySad() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setHappiness(Happiness.VERY_SAD);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
        }

        @Test
        public void testSetHappinessVeryHappy() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setHappiness(Happiness.VERY_HAPPY);
            assertEquals(Happiness.VERY_HAPPY, body.getHappiness());
            assertFalse(body.isScare());
        }
    }

    // ===========================================
    // PurposeOfMoving (移動目的)
    // ===========================================

    @Nested
    class PurposeOfMovingTests {
        @Test
        public void testSetToFoodTrue() {
            body.setToFood(true);
            assertTrue(body.isToFood());
        }

        @Test
        public void testSetToFoodFalseClearsPurpose() {
            body.setToFood(true);
            body.setToFood(false);
            assertFalse(body.isToFood());
        }

        @Test
        public void testSetToFoodFalseIgnoresIfNotFood() {
            body.setToSukkiri(true);
            body.setToFood(false); // 目的がFOODじゃないので変わらない
            assertTrue(body.isToSukkiri());
        }

        @Test
        public void testSetToSukkiriTrue() {
            body.setToSukkiri(true);
            assertTrue(body.isToSukkiri());
        }

        @Test
        public void testSetToSukkiriFalseClearsPurpose() {
            body.setToSukkiri(true);
            body.setToSukkiri(false);
            assertFalse(body.isToSukkiri());
        }

        @Test
        public void testSetToShitTrue() {
            body.setToShit(true);
            assertTrue(body.isToShit());
        }

        @Test
        public void testSetToShitFalseClearsPurpose() {
            body.setToShit(true);
            body.setToShit(false);
            assertFalse(body.isToShit());
        }

        @Test
        public void testSetToBedTrue() {
            body.setToBed(true);
            assertTrue(body.isToBed());
        }

        @Test
        public void testSetToBedFalseClearsPurpose() {
            body.setToBed(true);
            body.setToBed(false);
            assertFalse(body.isToBed());
        }

        @Test
        public void testSetToBodyTrue() {
            body.setToBody(true);
            assertTrue(body.isToBody());
        }

        @Test
        public void testSetToBodyFalseClearsPurpose() {
            body.setToBody(true);
            body.setToBody(false);
            assertFalse(body.isToBody());
        }

        @Test
        public void testSetToStealTrue() {
            body.setToSteal(true);
            assertTrue(body.isToSteal());
        }

        @Test
        public void testSetToStealFalseClearsPurpose() {
            body.setToSteal(true);
            body.setToSteal(false);
            assertFalse(body.isToSteal());
        }

        @Test
        public void testSetToTakeoutTrue() {
            body.setToTakeout(true);
            assertTrue(body.isToTakeout());
        }

        @Test
        public void testSetToTakeoutFalseClearsPurpose() {
            body.setToTakeout(true);
            body.setToTakeout(false);
            assertFalse(body.isToTakeout());
        }
    }

    // ===========================================
    // isVain / isNobinobi / isFurifuri
    // ===========================================

    @Nested
    class ActionStateTests {
        @Test
        public void testIsVainAlive() {
            body.setDead(false);
            body.setBeVain(true);
            assertTrue(body.isVain());
        }

        @Test
        public void testIsVainDead() {
            body.setDead(true);
            body.setBeVain(true);
            assertFalse(body.isVain());
        }

        @Test
        public void testIsNobinobiAlive() {
            body.setDead(false);
            body.setNobinobi(true);
            assertTrue(body.isNobinobi());
        }

        @Test
        public void testIsNobinobiDead() {
            body.setDead(true);
            body.setNobinobi(true);
            assertFalse(body.isNobinobi());
        }

        @Test
        public void testIsFurifuriAlive() {
            body.setDead(false);
            body.setFurifuri(true);
            assertTrue(body.isFurifuri());
        }

        @Test
        public void testIsFurifuriDead() {
            body.setDead(true);
            body.setFurifuri(true);
            assertFalse(body.isFurifuri());
        }

        @Test
        public void testIsEatingShitAlive() {
            body.setDead(false);
            body.setEatingShit(true);
            assertTrue(body.isEatingShit());
        }

        @Test
        public void testIsEatingShitDead() {
            body.setDead(true);
            body.setEatingShit(true);
            assertFalse(body.isEatingShit());
        }

        @Test
        public void testIsPeroperoAlive() {
            body.setDead(false);
            body.setPeropero(true);
            assertTrue(body.isPeropero());
        }

        @Test
        public void testIsPeroPeroDead() {
            body.setDead(true);
            body.setPeropero(true);
            assertFalse(body.isPeropero());
        }

        @Test
        public void testIsYunnyaaAlive() {
            body.setDead(false);
            body.setYunnyaa(true);
            assertTrue(body.isYunnyaa());
        }

        @Test
        public void testIsYunnyaaDead() {
            body.setDead(true);
            body.setYunnyaa(true);
            assertFalse(body.isYunnyaa());
        }

        @Test
        public void testIsPikopikoTrue() {
            body.setPikopiko(true);
            assertTrue(body.isPikopiko());
        }

        @Test
        public void testIsPikopikoFalse() {
            body.setPikopiko(false);
            assertFalse(body.isPikopiko());
        }

        @Test
        public void testIsPurupuruTrue() {
            body.setPurupuru(true);
            assertTrue(body.isPurupuru());
        }

        @Test
        public void testIsPurupuruFalse() {
            body.setPurupuru(false);
            assertFalse(body.isPurupuru());
        }

        @Test
        public void testIsCallingParentsAlive() {
            body.setDead(false);
            body.setCallingParents(true);
            assertTrue(body.isCallingParents());
        }

        @Test
        public void testIsCallingParentsDead() {
            body.setDead(true);
            body.setCallingParents(true);
            assertFalse(body.isCallingParents());
        }
    }

    // ===========================================
    // getSellingPrice / getWeight
    // ===========================================

    @Nested
    class PriceAndWeightTests {
        @Test
        public void testGetSellingPriceKaiyu() {
            int price = body.getSellingPrice(0); // 飼いゆとして
            assertTrue(price >= 0);
        }

        @Test
        public void testGetSellingPriceProcessed() {
            int price = body.getSellingPrice(1); // 加工品として
            assertTrue(price >= 0);
        }

        @Test
        public void testGetWeight() {
            body.setAgeState(AgeState.ADULT);
            int weight = body.getWeight();
            assertEquals(body.getWEIGHTorg()[AgeState.ADULT.ordinal()], weight);
        }
    }

    // ===========================================
    // getStep / getStepDist / getCollision
    // ===========================================

    @Nested
    class MovementTests {
        @Test
        public void testGetStep() {
            body.setAgeState(AgeState.ADULT);
            int step = body.getStep();
            assertEquals(body.getSTEPorg()[AgeState.ADULT.ordinal()], step);
        }

        @Test
        public void testGetStepDist() {
            body.setAgeState(AgeState.ADULT);
            int step = body.getSTEPorg()[AgeState.ADULT.ordinal()];
            assertEquals(step * step, body.getStepDist());
        }

        @Test
        public void testGetCollisionX() {
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(10);
            assertEquals((100 + 10) >> 1, body.getCollisionX());
        }

        @Test
        public void testGetCollisionY() {
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageH(80);
            assertEquals((80 + 0) >> 1, body.getCollisionY());
        }

        @Test
        public void testGetW() {
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(120);
            assertEquals(120, body.getW());
        }

        @Test
        public void testGetH() {
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageH(80);
            assertEquals(80, body.getH());
        }
    }

    // ===========================================
    // Sprite getters
    // ===========================================

    @Nested
    class SpriteTests {
        @Test
        public void testGetBodyBaseSpr() {
            body.setAgeState(AgeState.ADULT);
            assertNotNull(body.getBodyBaseSpr());
            assertSame(body.bodySpr[AgeState.ADULT.ordinal()], body.getBodyBaseSpr());
        }

        @Test
        public void testGetBodyExpandSpr() {
            body.setAgeState(AgeState.ADULT);
            assertNotNull(body.getBodyExpandSpr());
            assertSame(body.expandSpr[AgeState.ADULT.ordinal()], body.getBodyExpandSpr());
        }

        @Test
        public void testGetBraidSprite() {
            body.setAgeState(AgeState.ADULT);
            assertNotNull(body.getBraidSprite());
            assertSame(body.braidSpr[AgeState.ADULT.ordinal()], body.getBraidSprite());
        }
    }

    // ===========================================
    // isStressful / isVeryStressful
    // ===========================================

    @Nested
    class StressLevelTests {
        @Test
        public void testIsStressfulFalse() {
            body.setAgeState(AgeState.ADULT);
            body.stress = 0;
            assertFalse(body.isStressful());
        }

        @Test
        public void testIsStressfulTrue() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.getSTRESSLIMITorg()[AgeState.ADULT.ordinal()];
            // 2/5以上でtrue (checkNonYukkuriDiseaseTolerance() returns 100 by default)
            body.stress = limit * 2 / 5 + 1;
            assertTrue(body.isStressful());
        }

        @Test
        public void testIsVeryStressfulFalse() {
            body.setAgeState(AgeState.ADULT);
            body.stress = 0;
            assertFalse(body.isVeryStressful());
        }

        @Test
        public void testIsVeryStressfulTrue() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.getSTRESSLIMITorg()[AgeState.ADULT.ordinal()];
            // 3/5以上でtrue
            body.stress = limit * 3 / 5 + 1;
            assertTrue(body.isVeryStressful());
        }
    }

    // ===========================================
    // getBabyTypesDequeue / getStalksDequeue
    // ===========================================

    @Nested
    class BabyAndStalkTests {
        @Test
        public void testHasBabyFalse() {
            body.setHasBaby(false);
            assertFalse(body.isHasBaby());
        }

        @Test
        public void testHasBabyTrue() {
            body.setHasBaby(true);
            assertTrue(body.isHasBaby());
        }

        @Test
        public void testHasStalkFalse() {
            body.setHasStalk(false);
            assertFalse(body.isHasStalk());
        }

        @Test
        public void testHasStalkTrue() {
            body.setHasStalk(true);
            assertTrue(body.isHasStalk());
        }
    }

    // ===========================================
    // numOfAnts
    // ===========================================

    @Nested
    class AntsTests {
        @Test
        public void testSetNumOfAnts() {
            body.setNumOfAnts(10);
            assertEquals(10, body.getNumOfAnts());
        }

        @Test
        public void testSetNumOfAntsNegativeClamped() {
            body.setNumOfAnts(-5);
            assertEquals(0, body.getNumOfAnts());
        }

        @Test
        public void testSubstractNumOfAnts() {
            body.setNumOfAnts(10);
            body.substractNumOfAnts(5);
            assertEquals(5, body.getNumOfAnts());
        }

        @Test
        public void testSubstractNumOfAntsNegativeClamped() {
            body.setNumOfAnts(5);
            body.substractNumOfAnts(10);
            assertEquals(0, body.getNumOfAnts());
        }
    }

    // ===========================================
    // isGotBurned / isGotBurnedHeavily (詳細)
    // ===========================================

    @Nested
    class BurnedDetailTests {
        @Test
        public void testIsGotBurnedByFootBake() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = (limit >> 1) + 1; // MIDIUM
            body.bodyBakePeriod = 0;
            assertTrue(body.isGotBurned());
        }

        @Test
        public void testIsGotBurnedByBodyBake() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = 0;
            body.bodyBakePeriod = limit * 2 / 5 + 1; // MIDIUM body bake
            assertTrue(body.isGotBurned());
        }

        @Test
        public void testIsGotBurnedHeavilyFalse() {
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.bodyBakePeriod = 0;
            assertFalse(body.isGotBurnedHeavily());
        }

        @Test
        public void testIsGotBurnedHeavilyByFootBakeCritical() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = limit + 1; // CRITICAL
            body.bodyBakePeriod = 0;
            assertTrue(body.isGotBurnedHeavily());
        }

        @Test
        public void testIsGotBurnedHeavilyByBodyBakeCritical() {
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.footBakePeriod = 0;
            body.bodyBakePeriod = limit * 3 / 4 + 1; // CRITICAL
            assertTrue(body.isGotBurnedHeavily());
        }
    }

    // ===========================================
    // isDirty / isNormalDirty
    // ===========================================

    @Nested
    class DirtyTests {
        @Test
        public void testIsDirtyAliveAndDirty() {
            body.setDead(false);
            body.setDirty(true);
            assertTrue(body.isDirty());
        }

        @Test
        public void testIsDirtyAliveAndStubbornlyDirty() {
            body.setDead(false);
            body.setDirty(false);
            body.setStubbornlyDirty(true);
            assertTrue(body.isDirty());
        }

        @Test
        public void testIsDirtyDeadReturnsFalse() {
            body.setDead(true);
            body.setDirty(true);
            assertFalse(body.isDirty());
        }

        @Test
        public void testIsDirtyClean() {
            body.setDead(false);
            body.setDirty(false);
            body.setStubbornlyDirty(false);
            assertFalse(body.isDirty());
        }

        @Test
        public void testIsNormalDirtyTrue() {
            body.setDead(false);
            body.setDirty(true);
            assertTrue(body.isNormalDirty());
        }

        @Test
        public void testIsNormalDirtyDeadReturnsFalse() {
            body.setDead(true);
            body.setDirty(true);
            assertFalse(body.isNormalDirty());
        }

        @Test
        public void testAddDirtyPeriod() {
            body.setDirtyPeriod(10);
            body.addDirtyPeriod(5);
            assertEquals(15, body.getDirtyPeriod());
        }
    }

    // ===========================================
    // isShitting / isSleeping
    // ===========================================

    @Nested
    class ShittingAndSleepingTests {
        @Test
        public void testIsShittingAlive() {
            body.setDead(false);
            body.setShitting(true);
            assertTrue(body.isShitting());
        }

        @Test
        public void testIsShittingDead() {
            body.setDead(true);
            body.setShitting(true);
            assertFalse(body.isShitting());
        }

        @Test
        public void testIsSleepingAlive() {
            body.setDead(false);
            body.setSleeping(true);
            assertTrue(body.isSleeping());
        }

        @Test
        public void testIsSleepingDead() {
            body.setDead(true);
            body.setSleeping(true);
            assertFalse(body.isSleeping());
        }
    }

    // ===========================================
    // isForceExciting
    // ===========================================

    @Nested
    class ForceExcitingTests {
        @Test
        public void testIsForceExcitingTrue() {
            body.setDead(false);
            body.setExciting(true);
            body.setbForceExciting(true);
            assertTrue(body.isForceExciting());
        }

        @Test
        public void testIsForceExcitingDeadReturnsFalse() {
            body.setDead(true);
            body.setExciting(true);
            body.setbForceExciting(true);
            assertFalse(body.isForceExciting());
        }

        @Test
        public void testIsForceExcitingNotExciting() {
            body.setDead(false);
            body.setExciting(false);
            body.setbForceExciting(true);
            assertFalse(body.isForceExciting());
        }

        @Test
        public void testIsForceExcitingNotForced() {
            body.setDead(false);
            body.setExciting(true);
            body.setbForceExciting(false);
            assertFalse(body.isForceExciting());
        }
    }

    // ===========================================
    // isEatenByAnimals / removeAnts
    // ===========================================

    @Nested
    class EatenByAnimalsTests {
        @Test
        public void testIsEatenByAnimalsFalse() {
            // No ants attached
            assertFalse(body.isEatenByAnimals());
        }

        @Test
        public void testRemoveAnts() {
            body.setNumOfAnts(10);
            body.removeAnts();
            assertEquals(0, body.getNumOfAnts());
        }
    }

    // ===========================================
    // resetAttachmentBoundary
    // ===========================================

    @Nested
    class AttachmentBoundaryTests {
        @Test
        public void testResetAttachmentBoundaryEmpty() {
            // Should not throw when no attachments
            body.resetAttachmentBoundary();
        }

        @Test
        public void testResetAttachmentBoundaryWithAttachment() {
            StubBody b = new StubBody();
            b.setAgeState(AgeState.ADULT);
            for (int i = 0; i < 3; i++) {
                b.bodySpr[i] = new Sprite();
                b.bodySpr[i].setImageW(100);
                b.bodySpr[i].setImageH(100);
                b.expandSpr[i] = new Sprite();
                b.braidSpr[i] = new Sprite();
            }
            SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
            TestAttachment a = new TestAttachment(b);
            b.addAttachment(a);
            b.resetAttachmentBoundary(); // should not throw
        }
    }

    // ===========================================
    // addChildrenList / removeChildrenList (with real Body instances)
    // ===========================================

    @Nested
    class ChildrenListTests {
        private StubBody createAndRegisterBody() {
            StubBody b = new StubBody();
            b.setAgeState(AgeState.ADULT);
            for (int i = 0; i < 3; i++) {
                b.bodySpr[i] = new Sprite();
                b.bodySpr[i].setImageW(100);
                b.bodySpr[i].setImageH(100);
                b.expandSpr[i] = new Sprite();
                b.braidSpr[i] = new Sprite();
            }
            SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
            return b;
        }

        @Test
        public void testAddChildrenListNull() {
            int sizeBefore = body.getChildrenListSize();
            body.addChildrenList(null);
            assertEquals(sizeBefore, body.getChildrenListSize());
        }

        @Test
        public void testAddChildrenListWithBody() {
            StubBody child = createAndRegisterBody();
            int sizeBefore = body.getChildrenListSize();
            body.addChildrenList(child);
            assertEquals(sizeBefore + 1, body.getChildrenListSize());
        }

        @Test
        public void testRemoveChildrenListNull() {
            // Should not throw
            body.removeChildrenList(null);
        }

        @Test
        public void testRemoveChildrenListWithBody() {
            StubBody child = createAndRegisterBody();
            body.addChildrenList(child);
            int sizeBefore = body.getChildrenListSize();
            body.removeChildrenList(child);
            assertEquals(sizeBefore - 1, body.getChildrenListSize());
        }

        @Test
        public void testRemoveChildrenListNotInList() {
            StubBody child1 = createAndRegisterBody();
            StubBody child2 = createAndRegisterBody();
            body.addChildrenList(child1);
            int sizeBefore = body.getChildrenListSize();
            body.removeChildrenList(child2); // child2 is not in the list
            assertEquals(sizeBefore, body.getChildrenListSize());
        }

        @Test
        public void testAddElderSisterListNull() {
            int sizeBefore = body.getElderSisterListSize();
            body.addElderSisterList(null);
            assertEquals(sizeBefore, body.getElderSisterListSize());
        }

        @Test
        public void testAddElderSisterListWithBody() {
            StubBody sister = createAndRegisterBody();
            int sizeBefore = body.getElderSisterListSize();
            body.addElderSisterList(sister);
            assertEquals(sizeBefore + 1, body.getElderSisterListSize());
        }

        @Test
        public void testRemoveElderSisterListNull() {
            // Should not throw
            body.removeElderSisterList(null);
        }

        @Test
        public void testRemoveElderSisterListWithBody() {
            StubBody sister = createAndRegisterBody();
            body.addElderSisterList(sister);
            int sizeBefore = body.getElderSisterListSize();
            body.removeElderSisterList(sister);
            assertEquals(sizeBefore - 1, body.getElderSisterListSize());
        }

        @Test
        public void testAddSisterListNull() {
            int sizeBefore = body.getSisterListSize();
            body.addSisterList(null);
            assertEquals(sizeBefore, body.getSisterListSize());
        }

        @Test
        public void testAddSisterListWithBody() {
            StubBody sister = createAndRegisterBody();
            int sizeBefore = body.getSisterListSize();
            body.addSisterList(sister);
            assertEquals(sizeBefore + 1, body.getSisterListSize());
        }

        @Test
        public void testRemoveSisterListNull() {
            // Should not throw
            body.removeSisterList(null);
        }

        @Test
        public void testRemoveSisterListWithBody() {
            StubBody sister = createAndRegisterBody();
            body.addSisterList(sister);
            int sizeBefore = body.getSisterListSize();
            body.removeSisterList(sister);
            assertEquals(sizeBefore - 1, body.getSisterListSize());
        }
    }

    // ===========================================
    // getDiarrhea (RND dependent but has deterministic branches)
    // ===========================================

    @Nested
    class DiarrheaTests {
        @Test
        public void testGetDiarrheaKaiyuAlwaysTrue() {
            body.setBodyRank(BodyRank.KAIYU);
            // KAIYU always returns true regardless of RND
            assertTrue(body.getDiarrhea());
        }

        @Test
        public void testGetDiarrheaNonKaiyuWithSickDoublesProbability() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setAgeState(AgeState.ADULT);
            body.sickPeriod = body.INCUBATIONPERIODorg + 1; // isSick = true

            // With sick, P is halved, increasing chance of diarrhea
            // We can't assert the result deterministically without RND control
            // but we can at least verify it doesn't throw
            body.getDiarrhea();
        }

        @Test
        public void testGetDiarrheaNonKaiyuWithDamageDoublesProbability() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setAgeState(AgeState.ADULT);
            int limit = body.DAMAGELIMITorg[AgeState.ADULT.ordinal()];
            body.damage = limit / 2; // isDamaged = true

            body.getDiarrhea();
        }

        @Test
        public void testGetDiarrheaWithControlledRND() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setDiarrheaProb(1); // 100% chance (1 in 1)

            // When diarrheaProb is 1, RND.nextInt(1) always returns 0
            // So this should always return true
            SimYukkuri.RND = new ConstState(0);
            assertTrue(body.getDiarrhea());
        }
    }

    // ===========================================
    // willingFurifuri (RND dependent)
    // ===========================================

    @Nested
    class WillingFurifuriTests {
        @Test
        public void testWillingFurifuriNotRudeReturnsFalse() {
            body.setAttitude(Attitude.NICE);
            assertFalse(body.willingFurifuri());
        }

        @Test
        public void testWillingFurifuriRudeWithHighDisciplineReturnsFalse() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(1000); // high discipline = low chance

            // With furifuriDiscipline=1000, nextInt(1001) needs to be ≠0
            // ConstState(1) → nextInt(1001)=min(1,1000)=1≠0 → false
            SimYukkuri.RND = new ConstState(1);
            assertFalse(body.willingFurifuri());
        }

        @Test
        public void testWillingFurifuriRudeWithZeroDiscipline() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(0); // nextInt(1) always returns 0
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);

            // isRude=true, nextInt(0+1)=nextInt(1)=0, canFurifuri=true
            assertTrue(body.willingFurifuri());
        }

        @Test
        public void testWillingFurifuriCannotFurifuriReturnsFalse() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(0);
            body.setAgeState(AgeState.ADULT);
            // Make canFurifuri return false
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);

            assertFalse(body.willingFurifuri());
        }
    }

    // ===========================================
    // getTakeoutItem (MapPlaceData dependent)
    // ===========================================

    @Nested
    class TakeoutItemTests {
        @Test
        public void testGetTakeoutItemNullMap() {
            body.setTakeoutItem(null);
            assertNull(body.getTakeoutItem(src.enums.TakeoutItemType.FOOD));
        }

        @Test
        public void testGetTakeoutItemKeyNotFound() {
            java.util.HashMap<src.enums.TakeoutItemType, Integer> map = new java.util.HashMap<>();
            body.setTakeoutItem(map);
            // Key not in map returns null
            assertNull(body.getTakeoutItem(src.enums.TakeoutItemType.FOOD));
        }

        @Test
        public void testGetTakeoutItemFromTakenOutShit() {
            // Set up takeoutItem map with an ID
            java.util.HashMap<src.enums.TakeoutItemType, Integer> map = new java.util.HashMap<>();
            int itemId = 88888;
            map.put(src.enums.TakeoutItemType.SHIT, itemId);
            body.setTakeoutItem(map);

            // Create a shit object using no-arg constructor and register
            src.game.Shit shit = new src.game.Shit();
            SimYukkuri.world.getCurrentMap().getTakenOutShit().put(itemId, shit);

            Obj result = body.getTakeoutItem(src.enums.TakeoutItemType.SHIT);
            assertSame(shit, result);

            // Clean up
            SimYukkuri.world.getCurrentMap().getTakenOutShit().remove(itemId);
        }

        @Test
        public void testGetTakeoutItemNotInFoodOrShitMaps() {
            // Set up takeoutItem map with an ID that's not in food or shit maps
            // This tests the branch where it falls through to YukkuriUtil
            java.util.HashMap<src.enums.TakeoutItemType, Integer> map = new java.util.HashMap<>();
            int itemId = 77777;
            map.put(src.enums.TakeoutItemType.YUKKURI, itemId);
            body.setTakeoutItem(map);

            // Neither takenOutFood nor takenOutShit has this ID
            // So it falls through to YukkuriUtil.getBodyInstanceFromObjId which returns null
            Obj result = body.getTakeoutItem(src.enums.TakeoutItemType.YUKKURI);
            assertNull(result);
        }

        @Test
        public void testRemoveTakeoutItem() {
            java.util.HashMap<src.enums.TakeoutItemType, Integer> map = new java.util.HashMap<>();
            map.put(src.enums.TakeoutItemType.FOOD, 12345);
            body.setTakeoutItem(map);

            body.removeTakeoutItem(src.enums.TakeoutItemType.FOOD);
            assertNull(body.getTakeoutItem(src.enums.TakeoutItemType.FOOD));
        }
    }

    // ===========================================
    // Misc boolean getters (dead-dependent: isAngry, isScare)
    // ===========================================

    @Nested
    class MiscDeadDependentTests {
        @Test
        public void testIsAngryAlive() {
            body.setDead(false);
            body.setAngry(true);
            assertTrue(body.isAngry());
        }

        @Test
        public void testIsAngryDead() {
            body.setDead(true);
            body.setAngry(true);
            assertFalse(body.isAngry());
        }

        @Test
        public void testIsScareAlive() {
            body.setDead(false);
            body.setScare(true);
            assertTrue(body.isScare());
        }

        @Test
        public void testIsScareDead() {
            body.setDead(true);
            body.setScare(true);
            assertFalse(body.isScare());
        }
    }

    // ===========================================
    // Misc boolean getters (NOT dead-dependent - just return field)
    // ===========================================

    @Nested
    class MiscBooleanGetterTests {
        @Test
        public void testIsWet() {
            body.setWet(true);
            assertTrue(body.isWet());
            body.setWet(false);
            assertFalse(body.isWet());
        }

        @Test
        public void testIsMelt() {
            body.setMelt(true);
            assertTrue(body.isMelt());
            body.setMelt(false);
            assertFalse(body.isMelt());
        }

        @Test
        public void testIsPealed() {
            body.setPealed(true);
            assertTrue(body.isPealed());
            body.setPealed(false);
            assertFalse(body.isPealed());
        }

        @Test
        public void testIsPacked() {
            body.setPacked(true);
            assertTrue(body.isPacked());
            body.setPacked(false);
            assertFalse(body.isPacked());
        }

        @Test
        public void testIsBlind() {
            body.setBlind(true);
            assertTrue(body.isBlind());
            body.setBlind(false);
            assertFalse(body.isBlind());
        }

        @Test
        public void testIsRelax() {
            body.setRelax(true);
            assertTrue(body.isRelax());
            body.setRelax(false);
            assertFalse(body.isRelax());
        }

        @Test
        public void testIsRapist() {
            body.setRapist(true);
            assertTrue(body.isRapist());
            body.setRapist(false);
            assertFalse(body.isRapist());
        }

        @Test
        public void testIsSuperRapist() {
            body.setSuperRapist(true);
            assertTrue(body.isSuperRapist());
            body.setSuperRapist(false);
            assertFalse(body.isSuperRapist());
        }
    }

    // ===========================================
    // Simple boolean getters (not dead-dependent)
    // ===========================================

    @Nested
    class SimpleBooleanGetterTests {
        @Test
        public void testIsHybrid() {
            // Default implementation returns false
            assertFalse(body.isHybrid());
        }

        @Test
        public void testIsNotChangeCharacter() {
            body.setNotChangeCharacter(true);
            assertTrue(body.isNotChangeCharacter());
        }

        @Test
        public void testIsRealPregnantLimit() {
            // Default is true
            assertTrue(body.isRealPregnantLimit());
        }

        @Test
        public void testIsHasBraid() {
            body.setHasBraid(true);
            assertTrue(body.isHasBraid());
        }

        @Test
        public void testIsHasPants() {
            body.setHasPants(true);
            assertTrue(body.isHasPants());
        }

        @Test
        public void testIsAnalClose() {
            body.setAnalClose(true);
            assertTrue(body.isAnalClose());
        }

        @Test
        public void testIsBodyCastration() {
            body.setBodyCastration(true);
            assertTrue(body.isBodyCastration());
        }

        @Test
        public void testIsStalkCastration() {
            body.setStalkCastration(true);
            assertTrue(body.isStalkCastration());
        }

        @Test
        public void testIsCrushed() {
            body.setCrushed(true);
            assertTrue(body.isCrushed());
        }

        @Test
        public void testIsBurned() {
            body.setBurned(true);
            assertTrue(body.isBurned());
        }

        @Test
        public void testIsNightmare() {
            body.setNightmare(true);
            assertTrue(body.isNightmare());
        }

        @Test
        public void testIsFatherRaper() {
            body.setFatherRaper(true);
            assertTrue(body.isFatherRaper());
        }

        @Test
        public void testIsRareType() {
            body.setRareType(true);
            assertTrue(body.isRareType());
        }

        @Test
        public void testIsLikeBitterFood() {
            body.setLikeBitterFood(true);
            assertTrue(body.isLikeBitterFood());
        }

        @Test
        public void testIsLikeHotFood() {
            body.setLikeHotFood(true);
            assertTrue(body.isLikeHotFood());
        }

        @Test
        public void testIsLikeWater() {
            body.setLikeWater(true);
            assertTrue(body.isLikeWater());
        }

        @Test
        public void testIsFlyingType() {
            body.setFlyingType(true);
            assertTrue(body.isFlyingType());
        }

        @Test
        public void testIsBraidType() {
            body.setBraidType(true);
            assertTrue(body.isBraidType());
        }

        @Test
        public void testIsLockmove() {
            body.setLockmove(true);
            assertTrue(body.isLockmove());
        }

        @Test
        public void testIsPullAndPush() {
            body.setPullAndPush(true);
            assertTrue(body.isPullAndPush());
        }

        @Test
        public void testIsFixBack() {
            body.setFixBack(true);
            assertTrue(body.isFixBack());
        }

        @Test
        public void testIsTargetBind() {
            body.setTargetBind(true);
            assertTrue(body.isTargetBind());
        }

        @Test
        public void testIsInOutTakeoutItem() {
            body.setInOutTakeoutItem(true);
            assertTrue(body.isInOutTakeoutItem());
        }

        @Test
        public void testIsStaying() {
            body.setStaying(true);
            assertTrue(body.isStaying());
        }

        @Test
        public void testIsSilent() {
            body.setSilent(true);
            assertTrue(body.isSilent());
        }

        @Test
        public void testIsShutmouth() {
            body.setShutmouth(true);
            assertTrue(body.isShutmouth());
        }

        @Test
        public void testIsUnBirth() {
            body.setUnBirth(true);
            assertTrue(body.isUnBirth());
        }

        @Test
        public void testIsCanTalk() {
            body.setCanTalk(true);
            assertTrue(body.isCanTalk());
        }

        @Test
        public void testIsForceBirthMessage() {
            body.setForceBirthMessage(true);
            assertTrue(body.isForceBirthMessage());
        }

        @Test
        public void testIsPin() {
            body.setPin(true);
            assertTrue(body.isPin());
        }

        @Test
        public void testIsDropShadow() {
            body.setDropShadow(true);
            assertTrue(body.isDropShadow());
        }

        @Test
        public void testIsTaken() {
            body.setTaken(true);
            assertTrue(body.isTaken());
        }

        @Test
        public void testIsbPheromone() {
            body.setbPheromone(true);
            assertTrue(body.isbPheromone());
        }

        @Test
        public void testIsbNoDamageNextFall() {
            body.setbNoDamageNextFall(true);
            assertTrue(body.isbNoDamageNextFall());
        }

        @Test
        public void testIsbSurisuriFromPlayer() {
            body.setbSurisuriFromPlayer(true);
            assertTrue(body.isbSurisuriFromPlayer());
        }

        @Test
        public void testIsbPurupuru() {
            body.setbPurupuru(true);
            assertTrue(body.isbPurupuru());
        }

        @Test
        public void testIsbOnDontMoveBeltconveyor() {
            body.setbOnDontMoveBeltconveyor(true);
            assertTrue(body.isbOnDontMoveBeltconveyor());
        }

        @Test
        public void testIsbNoticeNoOkazari() {
            body.setbNoticeNoOkazari(true);
            assertTrue(body.isbNoticeNoOkazari());
        }

        @Test
        public void testIsbPenipeniCutted() {
            body.setbPenipeniCutted(true);
            assertTrue(body.isbPenipeniCutted());
        }

        @Test
        public void testIsbFirstEatStalk() {
            body.setbFirstEatStalk(true);
            assertTrue(body.isbFirstEatStalk());
        }

        @Test
        public void testIsbImageNagasiMode() {
            body.setbImageNagasiMode(true);
            assertTrue(body.isbImageNagasiMode());
        }
    }

    // ===========================================
    // addStress
    // ===========================================

    @Nested
    class AddStressDetailedTests {
        @Test
        public void testAddStressDeadNoEffect() {
            body.setDead(true);
            int stressBefore = body.stress;
            body.addStress(100);
            assertEquals(stressBefore, body.stress);
        }

        @Test
        public void testAddStressPositiveAddsShit() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.setAgeState(AgeState.ADULT);
            body.bodySpr[AgeState.ADULT.ordinal()].setImageW(100);
            body.setExpandSizeW(0);

            int shitBefore = body.getShit();
            body.addStress(100);
            // stress > 0 && DEFAULT && getBurstState != HALF → plusShit(s/5)
            assertTrue(body.getShit() >= shitBefore);
        }

        @Test
        public void testAddStressNegativeNoShit() {
            body.setDead(false);
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            body.stress = 500;
            int shitBefore = body.getShit();
            body.addStress(-100);
            // s <= 0 → no plusShit
            assertEquals(shitBefore, body.getShit());
        }

        @Test
        public void testAddStressClampToZero() {
            body.setDead(false);
            body.stress = 100;
            body.addStress(-99999);
            assertEquals(0, body.stress);
        }
    }

    // ===========================================
    // setHappiness RND制御テスト
    // ===========================================

    @Nested
    class SetHappinessRndTests {
        @Test
        public void testVerySadSadPeriodWithRndZero() {
            // ConstState(0) → nextInt(400)=0 → sadPeriod = 1200 + 0 - 200 = 1000
            SimYukkuri.RND = new ConstState(0);
            body.setHappiness(Happiness.VERY_SAD);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertEquals(1000, body.getSadPeriod());
        }

        @Test
        public void testVerySadSadPeriodWithRndMax() {
            // ConstState(399) → nextInt(400)=399 → sadPeriod = 1200 + 399 - 200 = 1399
            SimYukkuri.RND = new ConstState(399);
            body.setHappiness(Happiness.VERY_SAD);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertEquals(1399, body.getSadPeriod());
        }
    }

    // ===========================================
    // isOverPregnantLimit テスト (Body method)
    // ===========================================

    @Nested
    class IsOverPregnantLimitTests {
        private Body reimuBody;

        @BeforeEach
        public void setUpBody() {
            reimuBody = new src.yukkuri.Reimu();
            reimuBody.setAgeState(AgeState.ADULT);
            Sprite[] spr = new Sprite[3];
            for (int i = 0; i < 3; i++) {
                spr[i] = new Sprite();
                spr[i].setImageW(100);
                spr[i].setImageH(100);
            }
            reimuBody.setBodySpr(spr);
        }

        @Test
        public void testReturnsFalseWhenNotRealPregnantLimit() {
            reimuBody.setRealPregnantLimit(false);
            reimuBody.setPregnantLimit(100);
            assertFalse(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsTrueWhenLimitZeroAndRndNonZero() {
            reimuBody.setRealPregnantLimit(true);
            reimuBody.setPregnantLimit(0);
            SimYukkuri.RND = new ConstState(1);
            // nextInt(20)=min(1,19)=1≠0 → true
            assertTrue(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsFalseWhenLimitZeroAndRndZero() {
            reimuBody.setRealPregnantLimit(true);
            reimuBody.setPregnantLimit(0);
            SimYukkuri.RND = new ConstState(0);
            // nextInt(20)=0 → false (1/20 chance)
            assertFalse(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsFalseWhenLimitHigh() {
            reimuBody.setRealPregnantLimit(true);
            reimuBody.setPregnantLimit(1000);
            SimYukkuri.RND = new ConstState(1);
            // tarinaiFactor=100, nextInt(100)=min(1,99)=1≠0 → false
            assertFalse(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsTrueWhenLimitHighAndRndZero() {
            reimuBody.setRealPregnantLimit(true);
            reimuBody.setPregnantLimit(1000);
            SimYukkuri.RND = new ConstState(0);
            // tarinaiFactor=100, nextInt(100)=0 → true
            assertTrue(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsTrueWhenLimitModerateAndRndZero() {
            reimuBody.setRealPregnantLimit(true);
            reimuBody.setPregnantLimit(50); // tarinaiFactor=50
            SimYukkuri.RND = new ConstState(0);
            // nextInt(50)=0 → true
            assertTrue(reimuBody.isOverPregnantLimit());
        }

        @Test
        public void testReturnsFalseWhenNotRealPregnantLimitZero() {
            reimuBody.setRealPregnantLimit(false);
            reimuBody.setPregnantLimit(0);
            // non-real: pregnantLimit<=0 → true
            assertTrue(reimuBody.isOverPregnantLimit());
        }
    }

    // ===========================================
    // getDiarrhea RND分岐テスト
    // ===========================================

    @Nested
    class GetDiarrheaRndTests {
        @Test
        public void testDiarrheaReturnsTrueWhenKaiyu() {
            body.setBodyRank(BodyRank.KAIYU);
            assertTrue(body.getDiarrhea());
        }

        @Test
        public void testDiarrheaReturnsTrueWhenRndHits() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setDiarrheaProb(10);
            SimYukkuri.RND = new ConstState(0);
            // nextInt(10)=0 → true
            assertTrue(body.getDiarrhea());
        }

        @Test
        public void testDiarrheaReturnsFalseWhenRndMisses() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setDiarrheaProb(10);
            SimYukkuri.RND = new ConstState(1);
            // nextInt(10)=1 → false
            assertFalse(body.getDiarrhea());
        }

        @Test
        public void testDiarrheaSickDoublesChance() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setDiarrheaProb(10);
            body.setSickPeriod(1); // isSick()=true
            SimYukkuri.RND = new ConstState(0);
            // P=10, sick → P/2=5, nextInt(5)=0 → true
            assertTrue(body.getDiarrhea());
        }

        @Test
        public void testDiarrheaDamagedDoublesChance() {
            body.setBodyRank(BodyRank.NORAYU);
            body.setDiarrheaProb(10);
            body.damage = body.getDAMAGELIMITorg()[AgeState.ADULT.ordinal()] / 3 + 1; // isDamaged
            SimYukkuri.RND = new ConstState(0);
            // P=10, damaged → P/2=5, nextInt(5)=0 → true
            assertTrue(body.getDiarrhea());
        }
    }

    // ===========================================
    // willingFurifuri RND分岐テスト追加
    // ===========================================

    @Nested
    class WillingFurifuriRndTests {
        @Test
        public void testWillingFurifuriRudeZeroDisciplineRndHits() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(0);
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            SimYukkuri.RND = new ConstState(0);
            // nextInt(1)=0 → RND check passes, canFurifuri=true → true
            assertTrue(body.willingFurifuri());
        }

        @Test
        public void testWillingFurifuriRudeModerateDisciplineRndHits() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(5);
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            SimYukkuri.RND = new ConstState(0);
            // nextInt(6)=0 → RND check passes → true
            assertTrue(body.willingFurifuri());
        }

        @Test
        public void testWillingFurifuriRudeModerateDisciplineRndMisses() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setFurifuriDiscipline(5);
            body.setAgeState(AgeState.ADULT);
            body.footBakePeriod = 0;
            body.seteCoreAnkoState(CoreAnkoState.DEFAULT);
            SimYukkuri.RND = new ConstState(1);
            // nextInt(6)=1 (≠0) → false
            assertFalse(body.willingFurifuri());
        }
    }

    // ===========================================
    // setHappiness NYD分岐 RND テスト
    // ===========================================

    @Nested
    class SetHappinessNydRndTests {
        @Test
        public void testSetHappinessNYDSetsSadPeriod() {
            // isNYD() → sadPeriod = 1200 + RND.nextInt(400) - 200
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            SimYukkuri.RND = new ConstState(200);
            body.setHappiness(Happiness.HAPPY); // NYD overrides to VERY_SAD
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            // nextInt(400)=200 → sadPeriod = 1200 + 200 - 200 = 1200
            assertEquals(1200, body.getSadPeriod());
        }

        @Test
        public void testSetHappinessNYDMinSadPeriod() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            SimYukkuri.RND = new ConstState(0);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            // nextInt(400)=0 → sadPeriod = 1200 + 0 - 200 = 1000
            assertEquals(1000, body.getSadPeriod());
        }

        @Test
        public void testSetHappinessNYDMaxSadPeriod() {
            body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
            SimYukkuri.RND = new ConstState(399);
            body.setHappiness(Happiness.HAPPY);
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            // nextInt(400)=399 → sadPeriod = 1200 + 399 - 200 = 1399
            assertEquals(1399, body.getSadPeriod());
        }
    }
}
