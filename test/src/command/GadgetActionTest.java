
package src.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;

import src.ConstState;
import src.SimYukkuri;
import src.attachment.Ants;
import src.base.Body;
import src.base.Obj;
import src.base.Okazari;
import src.command.GadgetMenu.GadgetList;
import src.draw.World;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.enums.YukkuriType;
import src.game.Shit;
import src.game.Vomit;
import src.system.MessagePool;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class GadgetActionTest {

    private Random originalRnd;

    @BeforeAll
    public static void setUpClass() {
        MessagePool.loadMessage(GadgetActionTest.class.getClassLoader());
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
    private Body createReimuBody(AgeState age) {
        Body b = new Reimu();
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
        b.setBodySpr(spr);
        b.setExpandSpr(expSpr);
        b.setBraidSpr(brdSpr);
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        b.setIntelligence(Intelligence.AVERAGE);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    /** MouseEvent生成ヘルパー */
    private MouseEvent createEvent(int modifiers) {
        return new MouseEvent(new JPanel(), MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(), modifiers, 0, 0, 1, false);
    }

    // ===========================================
    // immediateEvaluate テスト
    // ===========================================

    @Nested
    class ImmediateEvaluateTests {

        @Test
        public void testYuCleanSetsCleaningOnLiveBodies() {
            Body b = createReimuBody(AgeState.ADULT);
            b.makeDirty(true);
            assertTrue(b.isDirty());

            GadgetAction.immediateEvaluate(GadgetList.YU_CLEAN);

            assertFalse(b.isDirty(), "清掃後は汚れが取れるべき");
        }

        @Test
        public void testYuCleanDoesNotRemoveDeadBodies() {
            Body dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            GadgetAction.immediateEvaluate(GadgetList.YU_CLEAN);

            // YU_CLEANは清掃のみで、死亡Bodyの除去は行わない
            assertFalse(dead.isRemoved(), "YU_CLEANは死亡Bodyを除去しないべき");
        }

        @Test
        public void testBodyRemovesDeadBodies() {
            Body alive = createReimuBody(AgeState.ADULT);
            Body dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            assertFalse(alive.isRemoved());
            assertFalse(dead.isRemoved());

            GadgetAction.immediateEvaluate(GadgetList.BODY);

            assertFalse(alive.isRemoved(), "生きてるBodyは除去されないべき");
            assertTrue(dead.isRemoved(), "死亡Bodyは除去されるべき");
        }

        @Test
        public void testShitRemovesShitAndVomit() {
            // Shit / Vomit をマップに追加
            Shit shit = new Shit();
            SimYukkuri.world.getCurrentMap().getShit().put(1, shit);
            Vomit vomit = new Vomit();
            SimYukkuri.world.getCurrentMap().getVomit().put(1, vomit);

            assertFalse(shit.isRemoved());
            assertFalse(vomit.isRemoved());

            GadgetAction.immediateEvaluate(GadgetList.SHIT);

            assertTrue(shit.isRemoved(), "うんうんは除去されるべき");
            assertTrue(vomit.isRemoved(), "嘔吐は除去されるべき");
        }

        @Test
        public void testAllRemovesDeadAndShitButNotLive() {
            Body alive = createReimuBody(AgeState.ADULT);
            Body dead = createReimuBody(AgeState.ADULT);
            dead.setDead(true);

            Shit shit = new Shit();
            SimYukkuri.world.getCurrentMap().getShit().put(2, shit);

            GadgetAction.immediateEvaluate(GadgetList.ALL);

            assertFalse(alive.isRemoved(), "生きてるBodyは除去されないべき");
            assertTrue(dead.isRemoved(), "死亡Bodyは除去されるべき");
            assertTrue(shit.isRemoved(), "うんうんは除去されるべき");
        }
    }

    // ===========================================
    // evaluateClean テスト
    // ===========================================

    @Nested
    class EvaluateCleanTests {

        @Test
        public void testIndividualRemovesDeadBody() {
            Body b = createReimuBody(AgeState.ADULT);
            b.setDead(true);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetList.INDIVIDUAL, ev, b);

            assertTrue(b.isRemoved(), "死亡Bodyは個別除去されるべき");
        }

        @Test
        public void testIndividualSetsCleaningOnLiveBody() {
            Body b = createReimuBody(AgeState.ADULT);
            b.makeDirty(true);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetList.INDIVIDUAL, ev, b);

            assertFalse(b.isDirty(), "生きてるBodyは清掃されるべき");
            assertFalse(b.isRemoved(), "生きてるBodyは除去されないべき");
        }

        @Test
        public void testIndividualRemovesNonBodyObj() {
            // Shit is an Obj (non-Body)
            Shit shit = new Shit();
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateClean(GadgetList.INDIVIDUAL, ev, shit);

            assertTrue(shit.isRemoved(), "非BodyオブジェクトはINDIVIDUALで除去されるべき");
        }
    }

    // ===========================================
    // evaluateAccessory テスト
    // ===========================================

    @Nested
    class EvaluateAccessoryTests {

        @Test
        public void testNormalClickTakeOkazariWhenHasOkazari() {
            Body b = createReimuBody(AgeState.ADULT);
            // Reimuはデフォルトでokazariあり
            assertTrue(b.hasOkazari());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAccessory(GadgetList.OKAZARI_HIDE, ev, b);

            assertFalse(b.hasOkazari(), "おかざり付きでクリックすると外れるべき");
        }

        @Test
        public void testNormalClickGiveOkazariWhenNoOkazari() {
            Body b = createReimuBody(AgeState.ADULT);
            b.setOkazari(null); // おかざりを無くす
            assertFalse(b.hasOkazari());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateAccessory(GadgetList.OKAZARI_HIDE, ev, b);

            assertTrue(b.hasOkazari(), "おかざり無しでクリックすると付けるべき");
        }

        @Test
        public void testShiftAppliesBasedOnTarget() {
            Body target = createReimuBody(AgeState.ADULT);
            // targetはおかざりあり → flag=false → !flagで条件成立 → hasOkazariのをtake
            assertTrue(target.hasOkazari());

            Body other = createReimuBody(AgeState.ADULT);
            assertTrue(other.hasOkazari());

            MouseEvent ev = createEvent(MouseEvent.SHIFT_DOWN_MASK);

            GadgetAction.evaluateAccessory(GadgetList.OKAZARI_HIDE, ev, target);

            // target has okazari → flag = !true = false
            // !flag && b.hasOkazari() → true → takeOkazari
            assertFalse(target.hasOkazari(), "Shift: ターゲットのおかざりが外れるべき");
            assertFalse(other.hasOkazari(), "Shift: 他のBodyのおかざりも外れるべき");
        }

        @Test
        public void testCtrlInvertsAll() {
            Body withOkazari = createReimuBody(AgeState.ADULT);
            assertTrue(withOkazari.hasOkazari());

            Body withoutOkazari = createReimuBody(AgeState.ADULT);
            withoutOkazari.setOkazari(null);
            assertFalse(withoutOkazari.hasOkazari());

            MouseEvent ev = createEvent(MouseEvent.CTRL_DOWN_MASK);

            GadgetAction.evaluateAccessory(GadgetList.OKAZARI_HIDE, ev, withOkazari);

            assertFalse(withOkazari.hasOkazari(), "Ctrl: おかざり有り→無しになるべき");
            assertTrue(withoutOkazari.hasOkazari(), "Ctrl: おかざり無し→有りになるべき");
        }
    }

    // ===========================================
    // evaluatePants テスト
    // ===========================================

    @Nested
    class EvaluatePantsTests {

        @Test
        public void testNormalClickGivePantsWhenNoPants() {
            Body b = createReimuBody(AgeState.ADULT);
            assertFalse(b.isHasPants());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluatePants(GadgetList.PANTS_NORMAL, ev, b);

            assertTrue(b.isHasPants(), "おくるみ無しでクリックすると付けるべき");
        }

        @Test
        public void testNormalClickTakePantsWhenHasPants() {
            Body b = createReimuBody(AgeState.ADULT);
            b.givePants();
            assertTrue(b.isHasPants());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluatePants(GadgetList.PANTS_NORMAL, ev, b);

            assertFalse(b.isHasPants(), "おくるみ有りでクリックすると外れるべき");
        }

        @Test
        public void testCtrlInvertsAllPants() {
            Body withPants = createReimuBody(AgeState.ADULT);
            withPants.givePants();
            assertTrue(withPants.isHasPants());

            Body withoutPants = createReimuBody(AgeState.ADULT);
            assertFalse(withoutPants.isHasPants());

            MouseEvent ev = createEvent(MouseEvent.CTRL_DOWN_MASK);

            GadgetAction.evaluatePants(GadgetList.PANTS_NORMAL, ev, withPants);

            assertFalse(withPants.isHasPants(), "Ctrl: おくるみ有り→無しになるべき");
            assertTrue(withoutPants.isHasPants(), "Ctrl: おくるみ無し→有りになるべき");
        }
    }

    // ===========================================
    // evaluateTest テスト
    // ===========================================

    @Nested
    class EvaluateTestTests {

        @Test
        public void testRankSetTogglesKaiyuToNorayu() {
            Body b = createReimuBody(AgeState.ADULT);
            b.setBodyRank(BodyRank.KAIYU);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetList.RANKSET, ev, b);

            assertEquals(BodyRank.NORAYU, b.getBodyRank(), "KAIYU→NORAYUに変わるべき");
        }

        @Test
        public void testRankSetTogglesNorayuToKaiyu() {
            Body b = createReimuBody(AgeState.ADULT);
            b.setBodyRank(BodyRank.NORAYU);
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetList.RANKSET, ev, b);

            assertEquals(BodyRank.KAIYU, b.getBodyRank(), "NORAYU→KAIYUに変わるべき");
        }

        @Test
        public void testSetVainCallsGetInVain() {
            Body b = createReimuBody(AgeState.ADULT);
            ConstState cs = new ConstState(0);
            SimYukkuri.RND = cs;

            assertFalse(b.isBeVain());
            MouseEvent ev = createEvent(0);

            GadgetAction.evaluateTest(GadgetList.SETVAIN, ev, b);

            assertTrue(b.isBeVain(), "SEtvainでbeVainフラグがONになるべき");
        }

        @Test
        public void testFeedCallsFeedOnLiveBody() {
            Body b = createReimuBody(AgeState.ADULT);
            MouseEvent ev = createEvent(0);
            int hungryBefore = b.getHungry();

            GadgetAction.evaluateTest(GadgetList.FEED, ev, b);

            // feed() adds 1500 to hungry via eatFood
            assertTrue(b.getHungry() > hungryBefore, "生きてるBodyはfeedで空腹値が増えるべき");
        }

        @Test
        public void testFeedDoesNothingOnDeadBody() {
            Body b = createReimuBody(AgeState.ADULT);
            b.setDead(true);
            MouseEvent ev = createEvent(0);
            int hungryBefore = b.getHungry();

            GadgetAction.evaluateTest(GadgetList.FEED, ev, b);

            assertEquals(hungryBefore, b.getHungry(), "死亡Bodyはfeedされないべき");
        }

        @Test
        public void testInviteAntsIgnoresShiftAndCtrl() {
            // Ants()コンストラクタは画像リソースに依存するため、
            // Shift/Ctrlでのスキップのみテスト（Antsインスタンス化不要）
            Body b = createReimuBody(AgeState.ADULT);
            MouseEvent shiftEv = createEvent(MouseEvent.SHIFT_DOWN_MASK);
            assertEquals(0, b.getAttachmentSize(Ants.class));

            GadgetAction.evaluateTest(GadgetList.INVITEANTS, shiftEv, b);

            assertEquals(0, b.getAttachmentSize(Ants.class), "Shift時は蟻操作されないべき");
        }

        @Test
        public void testInviteAntsIgnoresCtrl() {
            Body b = createReimuBody(AgeState.ADULT);
            MouseEvent ctrlEv = createEvent(MouseEvent.CTRL_DOWN_MASK);
            assertEquals(0, b.getAttachmentSize(Ants.class));

            GadgetAction.evaluateTest(GadgetList.INVITEANTS, ctrlEv, b);

            assertEquals(0, b.getAttachmentSize(Ants.class), "Ctrl時は蟻操作されないべき");
        }
    }
}
