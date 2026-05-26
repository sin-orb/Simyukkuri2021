package org.simyukkuri.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule;
import org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class GadgetActionTest {

    private Random originalRnd;

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        // Initialize dummy data for ampoules to avoid NPE in constructors
        OrangeAmpoule.setPivX(new int[] { 0, 0, 0 });
        OrangeAmpoule.setPivY(new int[] { 0, 0, 0 });
        OrangeAmpoule.setImgW(new int[] { 1, 1, 1 });
        OrangeAmpoule.setImgH(new int[] { 1, 1, 1 });

        PoisonAmpoule.setPivX(new int[] { 0, 0, 0 });
        PoisonAmpoule.setPivY(new int[] { 0, 0, 0 });
        PoisonAmpoule.setImgW(new int[] { 1, 1, 1 });
        PoisonAmpoule.setImgH(new int[] { 1, 1, 1 });

        Ants.setPivX(new int[] { 0, 0, 0 });
        Ants.setPivY(new int[] { 0, 0, 0 });
        Ants.setImgW(new int[] { 1, 1, 1 });
        Ants.setImgH(new int[] { 1, 1, 1 });

        WorldTestHelper.initializeLoadedMessagePool(GadgetActionTest.class.getClassLoader());
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

    /** MouseEvent生成ヘルパー */
    private MouseEvent createEvent(int modifiers) {
        return new MouseEvent(new JPanel(), MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), modifiers, 0, 0, 1, false);
    }

    // immediateEvaluate テスト

    @Nested
    class ImmediateEvaluateTests {

        @Test
        public void testYuCleanSetsCleaningOnLiveBodies() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.makeDirty(true);
            assertTrue(b.isDirty());

            GadgetAction.immediateEvaluate(GadgetMenuChoice.YU_CLEAN);

            assertFalse(b.isDirty(), "清掃後は汚れが取れるべき");
        }

        @Test
        public void testYuCleanDoesNotRemoveDeadBodies() {
            Yukkuri live = createReimuBody(AgeState.ADULT);
            live.makeDirty(true);
            Yukkuri dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            GadgetAction.immediateEvaluate(GadgetMenuChoice.YU_CLEAN);

            assertFalse(live.isDirty(), "生きているBodyは清掃されるべき");
            assertFalse(live.isRemoved(), "生きているBodyは除去されるべきではない");
            assertTrue(dead.isDead(), "死亡Bodyの死亡状態は変わるべきではない");
            assertFalse(dead.isRemoved(), "YU_CLEANは死亡Bodyを除去しないべき");
        }

        @Test
        public void testBodyRemovesDeadBodies() {
            Yukkuri alive = createReimuBody(AgeState.ADULT);
            Yukkuri dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            assertFalse(alive.isRemoved());
            assertFalse(dead.isRemoved());

            GadgetAction.immediateEvaluate(GadgetMenuChoice.BODY);

            assertFalse(alive.isRemoved(), "生きてるBodyは除去されないべき");
            assertTrue(dead.isRemoved(), "死亡Bodyは除去されるべき");
        }

        @Test
        public void testShitRemovesShitAndVomit() {
            // Shit / Vomit をマップに追加
            Shit shit = new Shit();
            SimYukkuri.world.getCurrentWorldState().getShit().put(1, shit);
            Vomit vomit = new Vomit();
            SimYukkuri.world.getCurrentWorldState().getVomit().put(1, vomit);

            assertFalse(shit.isRemoved());
            assertFalse(vomit.isRemoved());

            GadgetAction.immediateEvaluate(GadgetMenuChoice.SHIT);

            assertTrue(shit.isRemoved(), "うんうんは除去されるべき");
            assertTrue(vomit.isRemoved(), "嘔吐は除去されるべき");
        }

        @Test
        public void testAllRemovesDeadAndShitButNotLive() {
            Yukkuri alive = createReimuBody(AgeState.ADULT);
            Yukkuri dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            Shit shit = new Shit();
            SimYukkuri.world.getCurrentWorldState().getShit().put(2, shit);

            GadgetAction.immediateEvaluate(GadgetMenuChoice.ALL);

            assertFalse(alive.isRemoved(), "生きてるBodyは除去されないべき");
            assertTrue(dead.isRemoved(), "死亡Bodyは除去されるべき");
            assertTrue(shit.isRemoved(), "うんうんは除去されるべき");
        }

        @Test
        public void testEtcRemovesEmptyFood() {
            Food food = new Food();
            food.setAmount(0); // empty
            SimYukkuri.world.getCurrentWorldState().getFoods().put(1, food);

            GadgetAction.immediateEvaluate(GadgetMenuChoice.ETC);

            assertTrue(food.isRemoved(), "空の餌は除去されるべき");
        }
    }

    // evaluateTool テスト

    @Nested
    class EvaluateToolTests {
        @Test
        public void testPunishStrikesBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            int damageBefore = b.getDamage();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTool(GadgetMenuChoice.PUNISH, ev, b);

            assertTrue(b.getDamage() > damageBefore, "制裁でダメージが増えるべき");
        }

        @Test
        public void testSnappingKicksBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTool(GadgetMenuChoice.SNAPPING, ev, b);

            // kick sets vx, vy, vz in Yukkuri.kick()
            assertTrue(b.getVy() != 0 || b.getVx() != 0, "ケリで移動速度が設定されるべき");
        }

        @Test
        public void testVibratorExcitesBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            assertFalse(b.isExciting());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTool(GadgetMenuChoice.VIBRATOR, ev, b);

            assertTrue(b.isExciting(), "バイブで発情するべき");
        }
    }

    // evaluateAmpoule テスト

    @Nested
    class EvaluateAmpouleTests {
        @Test
        public void testOrangeAmpouleAddsAttachment() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            assertEquals(0, b.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule.class));
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAmpoule(GadgetMenuChoice.ORANGE_AMP, ev, b);

            assertEquals(1, b.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule.class),
                    "オレンジアンプルが追加されるべき");
        }

        @Test
        public void testPoisonAmpouleAddsAttachment() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            assertEquals(0, b.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule.class));
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAmpoule(GadgetMenuChoice.POISON_AMP, ev, b);

            assertEquals(1, b.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule.class),
                    "毒アンプルが追加されるべき");
        }
    }

    // evaluateVoice テスト

    @Nested
    class EvaluateVoiceTests {
        @Test
        public void testTakeItEasySetsMessage() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateCommunicate(GadgetMenuChoice.YUKKURISITEITTENE, ev, b);

            assertNotNull(b.getMessageBuffer(), "ゆっくりしていってね！でメッセージが設定されるべき");
        }
    }

    // evaluateClean テスト

    @Nested
    class EvaluateCleanTests {

        @Test
        public void testIndividualRemovesDeadBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setDead(true);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetMenuChoice.INDIVIDUAL, ev, b);

            assertTrue(b.isRemoved(), "死亡Bodyは個別除去されるべき");
        }

        @Test
        public void testIndividualSetsCleaningOnLiveBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.makeDirty(true);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetMenuChoice.INDIVIDUAL, ev, b);

            assertFalse(b.isDirty(), "生きてるBodyは清掃されるべき");
            assertFalse(b.isRemoved(), "生きてるBodyは除去されないべき");
        }

        @Test
        public void testIndividualRemovesNonBodyObj() {
            // Shit is an Entity (non-Yukkuri)
            Shit shit = new Shit();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetMenuChoice.INDIVIDUAL, ev, shit);

            assertTrue(shit.isRemoved(), "非BodyオブジェクトはINDIVIDUALで除去されるべき");
        }
    }

    // evaluateAccessory テスト

    @Nested
    class EvaluateAccessoryTests {

        @Test
        public void testNormalClickTakeOkazariWhenHasOkazari() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            // Reimuはデフォルトでokazariあり
            assertTrue(b.hasOkazari());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAccessory(GadgetMenuChoice.OKAZARI_HIDE, ev, b);

            assertFalse(b.hasOkazari(), "おかざり付きでクリックすると外れるべき");
        }

        @Test
        public void testNormalClickGiveOkazariWhenNoOkazari() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setOkazaris(null); // おかざりを無くす
            assertFalse(b.hasOkazari());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAccessory(GadgetMenuChoice.OKAZARI_HIDE, ev, b);

            assertTrue(b.hasOkazari(), "おかざり無しでクリックすると付けるべき");
        }

        @Test
        public void testShiftAppliesBasedOnTarget() {
            Yukkuri target = createReimuBody(AgeState.ADULT);
            target.setOkazaris(null);
            assertFalse(target.hasOkazari());

            Yukkuri otherWithOkazari = createReimuBody(AgeState.ADULT);
            assertTrue(otherWithOkazari.hasOkazari());

            Yukkuri otherWithoutOkazari = createReimuBody(AgeState.ADULT);
            otherWithoutOkazari.setOkazaris(null);
            assertFalse(otherWithoutOkazari.hasOkazari());

            MouseEvent ev = createEvent(MouseEvent.SHIFT_DOWN_MASK);

            GadgetAction.evaluateAccessory(GadgetMenuChoice.OKAZARI_HIDE, ev, target);

            assertTrue(target.hasOkazari(), "Shift: ターゲットのおかざりが付くべき");
            assertTrue(otherWithOkazari.hasOkazari(), "Shift: 既におかざり付きのBodyは維持されるべき");
            assertTrue(otherWithoutOkazari.hasOkazari(), "Shift: 他のBodyのおかざりも付くべき");
        }

        @Test
        public void testCtrlInvertsAll() {
            Yukkuri withOkazari = createReimuBody(AgeState.ADULT);
            assertTrue(withOkazari.hasOkazari());

            Yukkuri withoutOkazari = createReimuBody(AgeState.ADULT);
            withoutOkazari.setOkazaris(null);
            assertFalse(withoutOkazari.hasOkazari());

            Yukkuri extraWithOkazari = createReimuBody(AgeState.ADULT);
            assertTrue(extraWithOkazari.hasOkazari());

            MouseEvent ev = createEvent(MouseEvent.CTRL_DOWN_MASK);

            GadgetAction.evaluateAccessory(GadgetMenuChoice.OKAZARI_HIDE, ev, withOkazari);

            assertFalse(withOkazari.hasOkazari(), "Ctrl: おかざり有り→無しになるべき");
            assertTrue(withoutOkazari.hasOkazari(), "Ctrl: おかざり無し→有りになるべき");
            assertFalse(extraWithOkazari.hasOkazari(), "Ctrl: 他のBodyのおかざりも反転するべき");
        }
    }

    // evaluatePants テスト

    @Nested
    class EvaluatePantsTests {

        @Test
        public void testNormalClickGivePantsWhenNoPants() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            assertFalse(b.isHasPants());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluatePants(GadgetMenuChoice.PANTS_NORMAL, ev, b);

            assertTrue(b.isHasPants(), "おくるみ無しでクリックすると付けるべき");
        }

        @Test
        public void testNormalClickTakePantsWhenHasPants() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.givePants();
            assertTrue(b.isHasPants());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluatePants(GadgetMenuChoice.PANTS_NORMAL, ev, b);

            assertFalse(b.isHasPants(), "おくるみ有りでクリックすると外れるべき");
        }

        @Test
        public void testCtrlInvertsAllPants() {
            Yukkuri withPants = createReimuBody(AgeState.ADULT);
            withPants.givePants();
            assertTrue(withPants.isHasPants());

            Yukkuri withoutPants = createReimuBody(AgeState.ADULT);
            assertFalse(withoutPants.isHasPants());

            Yukkuri extraWithPants = createReimuBody(AgeState.ADULT);
            extraWithPants.givePants();
            assertTrue(extraWithPants.isHasPants());

            MouseEvent ev = createEvent(MouseEvent.CTRL_DOWN_MASK);

            GadgetAction.evaluatePants(GadgetMenuChoice.PANTS_NORMAL, ev, withPants);

            assertFalse(withPants.isHasPants(), "Ctrl: おくるみ有り→無しになるべき");
            assertTrue(withoutPants.isHasPants(), "Ctrl: おくるみ無し→有りになるべき");
            assertFalse(extraWithPants.isHasPants(), "Ctrl: 他のBodyのおくるみも反転するべき");
        }
    }

    // evaluateTest テスト

    @Nested
    class EvaluateTestTests {

        @Test
        public void testRankSetTogglesKaiyuToNorayu() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setRank(YukkuriRank.KAIYU);
            int favoriteSizeBefore = b.getFavoriteItems().size();
            org.simyukkuri.enums.PublicRank publicRankBefore = b.getPublicRank();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetMenuChoice.RANKSET, ev, b);

            assertEquals(YukkuriRank.NORAYU, b.getRank(), "KAIYU→NORAYUに変わるべき");
            assertEquals(publicRankBefore, b.getPublicRank(), "public rank は変わらないべき");
            assertEquals(favoriteSizeBefore, b.getFavoriteItems().size(), "favorite items は変わらないべき");
        }

        @Test
        public void testRankSetTogglesNorayuToKaiyu() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setRank(YukkuriRank.NORAYU);
            int favoriteSizeBefore = b.getFavoriteItems().size();
            org.simyukkuri.enums.PublicRank publicRankBefore = b.getPublicRank();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetMenuChoice.RANKSET, ev, b);

            assertEquals(YukkuriRank.KAIYU, b.getRank(), "NORAYU→KAIYUに変わるべき");
            assertEquals(publicRankBefore, b.getPublicRank(), "public rank は変わらないべき");
            assertEquals(favoriteSizeBefore, b.getFavoriteItems().size(), "favorite items は変わらないべき");
        }

        @Test
        public void testSetVainCallsGetInVain() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(0);
            SimYukkuri.RND = cs;

            assertFalse(b.isBeVain());
            b.addStress(100);
            int stressBefore = b.getStress();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetMenuChoice.SETVAIN, ev, b);

            assertTrue(b.isBeVain(), "SEtvainでbeVainフラグがONになるべき");
            assertEquals(stressBefore - 90, b.getStress(), "SEtvainでstressが下がるべき");
            assertNotNull(b.getMessageBuffer(), "SEtvainでメッセージが設定されるべき");
        }

        @Test
        public void testFeedCallsFeedOnLiveBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setHungry(0);
            MouseEvent ev = createEvent(0);
            int hungryBefore = b.getHungry();
            int lovePlayerBefore = b.getLovePlayer();

            GadgetAction.evaluateTest(GadgetMenuChoice.FEED, ev, b);

            assertEquals(hungryBefore + 1500, b.getHungry(), "生きてるBodyはfeedで空腹値が増えるべき");
            assertEquals(Happiness.HAPPY, b.getHappiness(), "空腹が少ないBodyはfeedでhappyになるべき");
            assertEquals(lovePlayerBefore + 30, b.getLovePlayer(), "生きてるBodyはfeedで好感度が増えるべき");
            assertNotNull(b.getMessageBuffer(), "feedでメッセージが設定されるべき");
        }

        @Test
        public void testFeedDoesNothingOnDeadBody() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.setDead(true);
            MouseEvent ev = createEvent(0);
            int hungryBefore = b.getHungry();
            int lovePlayerBefore = b.getLovePlayer();
            Happiness happinessBefore = b.getHappiness();
            String messageBefore = b.getMessageBuffer();

            GadgetAction.evaluateTest(GadgetMenuChoice.FEED, ev, b);

            assertEquals(hungryBefore, b.getHungry(), "死亡Bodyはfeedされないべき");
            assertEquals(lovePlayerBefore, b.getLovePlayer(), "死亡Bodyは好感度が変化しないべき");
            assertEquals(happinessBefore, b.getHappiness(), "死亡Bodyは幸福度が変化しないべき");
            assertEquals(messageBefore, b.getMessageBuffer(), "死亡Bodyはメッセージが変化しないべき");
        }

        @Test
        public void testInviteAntsIgnoresShiftAndCtrl() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.addAttachment(new Ants(b));
            Yukkuri other = createReimuBody(AgeState.ADULT);
            MouseEvent shiftEv = createEvent(MouseEvent.SHIFT_DOWN_MASK);
            assertEquals(1, b.getAttachmentSize(Ants.class));
            assertEquals(0, other.getAttachmentSize(Ants.class));

            GadgetAction.evaluateTest(GadgetMenuChoice.INVITEANTS, shiftEv, b);

            assertEquals(1, b.getAttachmentSize(Ants.class), "Shift時は蟻操作されないべき");
            assertEquals(0, other.getAttachmentSize(Ants.class), "Shift時は他Bodyの蟻も変化しないべき");
        }

        @Test
        public void testInviteAntsIgnoresCtrl() {
            Yukkuri b = createReimuBody(AgeState.ADULT);
            b.addAttachment(new Ants(b));
            Yukkuri other = createReimuBody(AgeState.ADULT);
            MouseEvent ctrlEv = createEvent(MouseEvent.CTRL_DOWN_MASK);
            assertEquals(1, b.getAttachmentSize(Ants.class));
            assertEquals(0, other.getAttachmentSize(Ants.class));

            GadgetAction.evaluateTest(GadgetMenuChoice.INVITEANTS, ctrlEv, b);

            assertEquals(1, b.getAttachmentSize(Ants.class), "Ctrl時は蟻操作されないべき");
            assertEquals(0, other.getAttachmentSize(Ants.class), "Ctrl時は他Bodyの蟻も変化しないべき");
        }
    }
}
