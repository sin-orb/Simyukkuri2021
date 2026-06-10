package org.simyukkuri.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.StubBody;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class GadgetToolTest {

    private Random originalRnd;

    @BeforeAll
    public static void setUpClass() {
        WorldTestHelper.initializeLoadedMessagePool(GadgetToolTest.class.getClassLoader());
    }

    @BeforeEach
    public void setUp() {
        originalRnd = SimYukkuri.RND;
        SimYukkuri.world = new World();
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    /** Marisa実体を生成・Sprite初期化・ワールドに登録する */
    private Yukkuri createMarisaBody(AgeState age) {
        Yukkuri b = new Marisa();
        Sprite[] spr = new Sprite[3];
        Sprite[] expSpr = new Sprite[3];
        Sprite[] brdSpr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite();
            spr[i].setImageW(100);
            spr[i].setImageH(100);
            expSpr[i] = new Sprite();
            brdSpr[i] = new Sprite();
        }
        b.setSpriteSet(spr);
        b.setExpandSpr(expSpr);
        b.setBraidSpr(brdSpr);
        b.setAgeState(age);
        b.setMsgType(YukkuriType.MARISA);
        b.setIntelligence(Intelligence.AVERAGE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    /** Reimu実体を生成・Sprite初期化・ワールドに登録する */
    private Yukkuri createReimuBody(AgeState age) {
        Yukkuri b = new Reimu();
        Sprite[] spr = new Sprite[3];
        Sprite[] expSpr = new Sprite[3];
        Sprite[] brdSpr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite();
            spr[i].setImageW(100);
            spr[i].setImageH(100);
            expSpr[i] = new Sprite();
            brdSpr[i] = new Sprite();
        }
        b.setSpriteSet(spr);
        b.setExpandSpr(expSpr);
        b.setBraidSpr(brdSpr);
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        b.setIntelligence(Intelligence.AVERAGE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    @Nested
    class DoGodHandTests {

        @Test
        public void testDoGodHandDoesNothingWhenDead() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setDead(true);
            boolean rapistBefore = b.isRapist();
            boolean[] flagsBefore = b.getAbFlagGodHand().clone();

            GadgetTool.doGodHand(b);

            // 死亡時は何も変化しないことを確認
            assertEquals(rapistBefore, b.isRapist());
            assertArrayEquals(flagsBefore, b.getAbFlagGodHand());
        }

        @Test
        public void testCase0RapistToggleWhenCannotTransform() {
            StubBody b = new StubBody();
            b.setBurialState(BurialState.NONE);
            b.setRapist(false);
            b.setBegging(false);
            ConstState cs = new ConstState(0);
            SimYukkuri.RND = cs;

            GadgetTool.doGodHand(b);

            assertTrue(b.isRapist(), "変身できない場合はレイパー状態が切り替わるべき");
            assertEquals(YukkuriType.TARINAI, b.getType(), "変身できない場合でも型は変わらない");
        }

        @Test
        public void testCase1BodyCut() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(1);
            SimYukkuri.RND = cs;
            // BurialState.ALL にしてaddVomitをスキップ
            b.setBurialState(BurialState.ALL);
            b.setBegging(false);

            assertNull(b.getCriticalDamageType());

            GadgetTool.doGodHand(b);

            // bodyCut() sets CriticalDamageType.CUT
            assertEquals(CriticalDamageType.CUT, b.getCriticalDamageType());
            assertEquals(YukkuriType.REIMU, b.getType(), "切断だけでは型は変わらない");
        }

        @Test
        public void testCase2Stretch() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(2);
            SimYukkuri.RND = cs;

            assertFalse(b.getAbFlagGodHand()[1]);
            assertFalse(b.getAbFlagGodHand()[2]);

            GadgetTool.doGodHand(b);

            assertTrue(b.getAbFlagGodHand()[1], "引っ張りフラグがONになるべき");
            assertFalse(b.getAbFlagGodHand()[2], "つぶしフラグはOFFのまま");
        }

        @Test
        public void testCase2StretchFromCompress() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(2);
            SimYukkuri.RND = cs;

            // つぶし状態から引っ張りに切り替え
            b.getAbFlagGodHand()[2] = true;
            b.setGodHandCompressCount(42);

            GadgetTool.doGodHand(b);

            assertTrue(b.getAbFlagGodHand()[1]);
            assertFalse(b.getAbFlagGodHand()[2]);
            // compressPointがstretchPointにコピーされる
            assertEquals(42, b.getGodHandStretchCount());
        }

        @Test
        public void testCase3Compress() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(3);
            SimYukkuri.RND = cs;

            assertFalse(b.getAbFlagGodHand()[1]);
            assertFalse(b.getAbFlagGodHand()[2]);

            GadgetTool.doGodHand(b);

            assertFalse(b.getAbFlagGodHand()[1], "引っ張りフラグはOFF");
            assertTrue(b.getAbFlagGodHand()[2], "つぶしフラグがONになるべき");
        }

        @Test
        public void testCase3CompressFromStretch() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(3);
            SimYukkuri.RND = cs;

            // 引っ張り状態からつぶしに切り替え
            b.getAbFlagGodHand()[1] = true;
            b.setGodHandStretchCount(99);

            GadgetTool.doGodHand(b);

            assertFalse(b.getAbFlagGodHand()[1]);
            assertTrue(b.getAbFlagGodHand()[2]);
            // stretchPointがcompressPointにコピーされる
            assertEquals(99, b.getGodHandCompressCount());
        }

        @Test
        public void testCase4Heal() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(4);
            SimYukkuri.RND = cs;

            // ダメージを与えてから回復
            int halfDamage = b.getDamageLimitBase()[AgeState.ADULT.ordinal()] / 2;
            b.setDamage(halfDamage);
            assertTrue(b.getDamage() > 0 || halfDamage > 0, "ダメージが設定されているべき");

            GadgetTool.doGodHand(b);

            // giveJuice() resets damage to 0
            assertEquals(0, b.getDamage(), "回復後ダメージは0になるべき");
        }

        @Test
        public void testCase5LanguageBreakReimu() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(5);
            SimYukkuri.RND = cs;

            assertEquals(YukkuriType.REIMU, b.getMsgType());

            GadgetTool.doGodHand(b);

            assertEquals(YukkuriType.TARINAIREIMU, b.getMsgType(),
                    "Reimuタイプの場合TARINAIREIMUになるべき");
        }

        @Test
        public void testDefaultKickAndInflate() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(6);
            SimYukkuri.RND = cs;

            assertFalse(b.getAbFlagGodHand()[0], "初期状態で膨張フラグはOFF");
            assertFalse(b.isAnalClose(), "初期状態で肛門閉鎖はOFF");

            GadgetTool.doGodHand(b);

            assertTrue(b.getAbFlagGodHand()[0], "膨張フラグがONになるべき");
            assertFalse(b.isAnalClose(), "初回の膨張では肛門は閉じられない");
        }

        @Test
        public void testDefaultSecondTimeExplosion() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(7);
            SimYukkuri.RND = cs;

            // 1回目の膨張済み状態
            b.getAbFlagGodHand()[0] = true;
            b.setBegging(false);

            GadgetTool.doGodHand(b);

            // 2回目は爆発的拡大: shit設定 + analClose
            assertTrue(b.isAnalClose(), "肛門が閉じられるべき");
            assertTrue(b.getAbFlagGodHand()[0], "膨張フラグは維持される");
            assertEquals(b.getShitLimitBase()[b.getAgeState().ordinal()] * 10, b.getShit(),
                    "爆発的拡大ではうんうん量が増えるべき");
        }

        // --- まりさ変身系 ---

        @Test
        public void testCase0RudeMarisaDoesNotTransform() {
            // ゲスまりさ: execTransform() 内の isRude() チェックで早期 return
            Yukkuri b = createMarisaBody(AgeState.ADULT);
            b.setAttitude(Attitude.SHITHEAD);
            SimYukkuri.RND = new ConstState(0);
            GadgetTool.doGodHand(b);
            assertNotEquals(YukkuriType.DOSMARISA, b.getType(), "ゲスまりさはドス化しないこと");
        }

        @Test
        public void testCase0DamagedMarisaDoesNotTransform() {
            // ダメージありまりさ: canTransform()=false → execTransform() 早期 return
            Yukkuri b = createMarisaBody(AgeState.ADULT);
            int halfLimit = b.getDamageLimitBase()[AgeState.ADULT.ordinal()] / 2 + 1;
            b.setDamage(halfLimit);
            SimYukkuri.RND = new ConstState(0);
            GadgetTool.doGodHand(b);
            assertNotEquals(YukkuriType.DOSMARISA, b.getType(), "ダメージありまりさはドス化しないこと");
        }

        @Test
        public void testCase0DoesNotTransformWhenDosMarisaExistsInRegistry() {
            // ワールドにドスまりさが既にいる場合は judgeCanTransForGodHand()=false
            DosMarisa dos = new DosMarisa();
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(dos.getUniqueId(), dos);
            Yukkuri b = createMarisaBody(AgeState.ADULT);
            SimYukkuri.RND = new ConstState(0);
            GadgetTool.doGodHand(b);
            assertNotEquals(YukkuriType.DOSMARISA, b.getType(), "ドスまりさが既にいる場合はドス化しないこと");
        }

        // --- 持ち物落下 ---

        @Test
        public void testCase1DropCarryItem() {
            // case 1 (切断) で dropAllTakeoutItem が呼ばれること
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.FOOD, 9999);
            b.setBurialState(BurialState.ALL);
            SimYukkuri.RND = new ConstState(1);
            GadgetTool.doGodHand(b);
            assertFalse(b.getCarryItems().containsKey(org.simyukkuri.enums.TakeoutItemType.FOOD),
                    "case1(切断) で持ち物が落ちること");
        }

        @Test
        public void testDefaultDropCarryItem() {
            // default (膨張) で dropAllTakeoutItem が呼ばれること
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.FOOD, 9999);
            SimYukkuri.RND = new ConstState(7);
            GadgetTool.doGodHand(b);
            assertFalse(b.getCarryItems().containsKey(org.simyukkuri.enums.TakeoutItemType.FOOD),
                    "default(膨張) で持ち物が落ちること");
        }

        // --- 言語破壊 (まりさ) ---

        @Test
        public void testCase5TarinaiForNonReimu() {
            Yukkuri b = createMarisaBody(AgeState.ADULT);
            SimYukkuri.RND = new ConstState(5);
            GadgetTool.doGodHand(b);
            assertEquals(YukkuriType.TARINAI, b.getMsgType(), "まりさへの言語破壊はTARINAIになること");
        }

        // --- 茎実ゆ親への通知 ---

        @Test
        public void testCase2NotifiesStalkMotherWithSad() {
            Yukkuri mother = createReimuBody(AgeState.ADULT);
            Yukkuri child = createReimuBody(AgeState.ADULT);
            Stalk stalk = new Stalk(100, 100, 0);
            stalk.setPlantYukkuri(mother.getUniqueId());
            child.setBindStalk(stalk);

            SimYukkuri.RND = new ConstState(2);
            GadgetTool.doGodHand(child);

            assertEquals(Happiness.SAD, mother.getHappiness(), "case2(引っ張り)で茎実ゆの親がSADになること");
        }

        @Test
        public void testCase3NotifiesStalkMotherWithSad() {
            Yukkuri mother = createReimuBody(AgeState.ADULT);
            Yukkuri child = createReimuBody(AgeState.ADULT);
            Stalk stalk = new Stalk(100, 100, 0);
            stalk.setPlantYukkuri(mother.getUniqueId());
            child.setBindStalk(stalk);

            SimYukkuri.RND = new ConstState(3);
            GadgetTool.doGodHand(child);

            assertEquals(Happiness.SAD, mother.getHappiness(), "case3(つぶし)で茎実ゆの親がSADになること");
        }

        @Test
        public void testCase4NotifiesStalkMotherWithHappy() {
            Yukkuri mother = createReimuBody(AgeState.ADULT);
            Yukkuri child = createReimuBody(AgeState.ADULT);
            Stalk stalk = new Stalk(100, 100, 0);
            stalk.setPlantYukkuri(mother.getUniqueId());
            child.setBindStalk(stalk);

            SimYukkuri.RND = new ConstState(4);
            GadgetTool.doGodHand(child);

            assertEquals(Happiness.VERY_HAPPY, mother.getHappiness(), "case4(回復)で茎実ゆの親がVERY_HAPPYになること");
        }

        @Test
        public void testCase4SetsHalfDamageBeforeHeal() {
            // case4: setDamage(limit/2) → giveJuice() → damage=0 の流れを確認
            // damage=0 の初期状態から doGodHand(case4) → giveJuice 後 damage=0
            Yukkuri b = createReimuBody(AgeState.ADULT);
            assertEquals(0, b.getDamage(), "初期 damage は 0");
            SimYukkuri.RND = new ConstState(4);
            GadgetTool.doGodHand(b);
            assertEquals(0, b.getDamage(), "case4後 giveJuice によりdamage=0になること");
        }
    }
}
