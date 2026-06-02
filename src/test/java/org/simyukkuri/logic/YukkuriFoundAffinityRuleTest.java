package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

class YukkuriFoundAffinityRuleTest {

    private Yukkuri actor;
    private Yukkuri target;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
        SimYukkuri.RND = new ConstState(0);

        actor = WorldTestHelper.createBody();
        target = WorldTestHelper.createBody();
        actor.setX(100); actor.setY(100);
        target.setX(120); target.setY(100);
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.ADULT);
        actor.setPublicRank(PublicRank.NONE);
        target.setPublicRank(PublicRank.NONE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(actor.getUniqueId(), actor);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testHandleFoundAffinity_RankMismatch_ReturnsFalse() {
        // publicRank 不一致 → 先頭ガードで false を返す
        actor.setPublicRank(PublicRank.NONE);
        target.setPublicRank(PublicRank.UNUN_SLAVE);

        assertFalse(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
    }

    @Test
    void testHandleFoundAffinity_TargetNeedled_SameRank_ReturnsTrue() {
        // targetBody.isNeedled()=true → RNG 無関係で必ず true を返す
        target.setNeedled(true);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
    }

    @Test
    void testHandleFoundAffinity_TargetNeedled_IsPartner_RngZero_MovesToTarget() {
        // needled + partner + nextInt(50)==0 → actor が target に向かう
        SimYukkuri.RND = new ConstState(0); // nextInt(50)=0
        target.setPartner(actor.getUniqueId()); // target.isPartner(actor)=true
        target.setNeedled(true);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_TargetNeedled_IsPartner_RngNonZero_NoMove() {
        // needled + partner だが nextInt(50)!=0 → 移動しないが true は返す
        SimYukkuri.RND = new ConstState(1); // nextInt(50)=1 ≠ 0
        target.setPartner(actor.getUniqueId());
        target.setNeedled(true);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
    }

    @Test
    void testHandleFoundAffinity_TargetNeedled_IsParentChild_RngZero_MovesToTarget() {
        // needled + actor が target の親(父) + RNG==0 → actor が target に向かう
        SimYukkuri.RND = new ConstState(0);
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        WorldTestHelper.setParents(target, actor.getUniqueId(), -1); // target の父は actor
        target.setNeedled(true);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_NoRelation_AllRngNonZero_ReturnsFalse() {
        // needled なし、無関係、すべての nextInt が 0 でない → 最終的に false
        SimYukkuri.RND = new ConstState(1); // nextInt always 1 ≠ 0, nextBoolean=false
        // no partner, no parent-child

        assertFalse(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_IsPartner_RngZero_MovesToPartner() {
        // needled なし + partner + nextInt(150)==0 → partner のところへ向かう
        SimYukkuri.RND = new ConstState(0);
        target.setPartner(actor.getUniqueId()); // target.isPartner(actor)=true

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_IsChild_RngZero_MovesToParent() {
        // needled なし + actor が子供、target が親 + nextInt(100)==0 → 親のところへ向かう
        SimYukkuri.RND = new ConstState(0);
        actor.setAgeState(AgeState.CHILD);
        target.setAgeState(AgeState.ADULT);
        WorldTestHelper.setParents(actor, target.getUniqueId(), -1); // actor の父は target

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_IsSister_RngZero_MovesToSister() {
        // needled なし + 姉妹 + nextInt(150)==0 → 姉妹のところへ向かう
        SimYukkuri.RND = new ConstState(0);
        actor.setAgeState(AgeState.CHILD);
        target.setAgeState(AgeState.CHILD);
        // 同じ父を持つ → isSister
        Yukkuri commonParent = WorldTestHelper.createBody();
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(commonParent.getUniqueId(), commonParent);
        WorldTestHelper.setParents(actor, commonParent.getUniqueId(), -1);
        WorldTestHelper.setParents(target, commonParent.getUniqueId(), -1);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_AdultIsFamily_RngZero_MovesToChild() {
        // actor が成人 + target が子供 + isFamily + nextInt(150)==0 → 子のところへ向かう
        SimYukkuri.RND = new ConstState(0);
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        WorldTestHelper.setParents(target, actor.getUniqueId(), -1);

        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
        assertEquals(target.getObjId(), actor.getMoveTargetId());
    }

    @Test
    void testHandleFoundAffinity_NotNeedled_IdiotNoOkazari_ChildRelation_ReturnsTrue() {
        // actor が FOOL で hasOkazari=false、target が子供 → nextBoolean=true 分岐で true を返す
        SimYukkuri.RND = new ConstState(0); // nextBoolean=false の ConstState(0)、setFixedBoolean で true に
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(true); // nextBoolean=true
        SimYukkuri.RND = cs;
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        actor.setIntelligence(Intelligence.FOOL);
        actor.setOkazaris(null); // hasOkazari=false
        WorldTestHelper.setParents(target, actor.getUniqueId(), -1);

        // FOOL + !hasOkazari + child関係 → 近づかない → true を返す（ブロック分岐）
        assertTrue(YukkuriFoundAffinityRule.handleFoundAffinity(actor, target, 0, 0));
    }
}
