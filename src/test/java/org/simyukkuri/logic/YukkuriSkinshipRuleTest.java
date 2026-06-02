package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.util.WorldTestHelper;

class YukkuriSkinshipRuleTest {

    private Yukkuri actor;
    private Yukkuri target;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
        SimYukkuri.RND = new ConstState(0); // nextBoolean=false

        actor = WorldTestHelper.createBody();
        target = WorldTestHelper.createBody();
        actor.setX(100); actor.setY(100);
        target.setX(102); target.setY(100);
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.ADULT);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(actor.getUniqueId(), actor);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(target.getUniqueId(), target);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testHandleSkinship_NoConditionMatches_ReturnsFalse() {
        // 関係なし、アリなし → false
        // nextBoolean=false なのでランダム分岐もスキップされる
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.ADULT);

        assertFalse(YukkuriSkinshipRule.handleSkinship(target, actor));
    }

    @Test
    void testHandleSkinship_TargetHasAnts_ActorNoAnts_DoesPeroperoReturnsTrue() {
        // target にアリ、actor にはアリなし → actor が peropero して clearActions + true
        target.addAttachment(new Ants());
        // actor.getAttachmentSize(Ants)==0 なので peropero 分岐
        // actor.isPeropero() が設定されることを確認
        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
        assertTrue(actor.isPeropero());
    }

    @Test
    void testHandleSkinship_BothHaveAnts_ClearsActionsNoPeroperoReturnsTrue() {
        // target にもアリ、actor にもアリ → peropero せずに clearActions + true
        target.addAttachment(new Ants());
        actor.addAttachment(new Ants());

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
        // actor.isPeropero() は設定されない
        assertFalse(actor.isPeropero());
    }

    @Test
    void testHandleSkinship_ActorIsParent_TargetVeryHungry_ActorHasFood_DropsFood() {
        // actor が target の親、target が非常に空腹、actor が食料保持 → dropTakeoutItem → true
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        WorldTestHelper.setParents(target, actor.getUniqueId(), -1); // actor は target の父

        target.setHungry(0); // isVeryHungry=true
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        actor.setCarryItem(TakeoutItemType.FOOD, food);

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
        // 食料が吐き出された → carryItem が null になる
        assertNull(actor.getCarryItem(TakeoutItemType.FOOD));
    }

    @Test
    void testHandleSkinship_AdultActor_ChildTarget_ParentRelation_ReturnsTrueWithSkinship() {
        // adult actor + child target + 親子関係 → スキンシップ + clearActions + true
        // nextBoolean=false → doSurisuri 分岐（peropero は skip）
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        WorldTestHelper.setParents(target, actor.getUniqueId(), -1);

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
    }

    @Test
    void testHandleSkinship_AdultActor_ChildTarget_MotherRelation_DirtyChildPeropero() {
        // adult actor(母) + dirty child → doPeropero + clearActions + true
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        actor.setAgeState(AgeState.ADULT);
        target.setAgeState(AgeState.CHILD);
        target.setDirty(true);
        // actor を母に設定: setParents(child, -1, motherId)
        WorldTestHelper.setParents(target, -1, actor.getUniqueId());

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
        assertTrue(actor.isPeropero());
    }

    @Test
    void testHandleSkinship_Partner_ReturnsTrueWithSurisuri() {
        // target.isPartner(actor)=true + nextBoolean=true → doSurisuri + clearActions + true
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(true); // nextBoolean=true → surisuri 分岐へ
        SimYukkuri.RND = cs;
        target.setPartner(actor.getUniqueId()); // target.isPartner(actor)=true

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
    }

    @Test
    void testHandleSkinship_ChildActor_ParentTarget_ReturnsTrueWithSkinship() {
        // child actor + parent target → スキンシップ + clearActions + true
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(false); // nextBoolean=false → doSurisuri 側
        SimYukkuri.RND = cs;
        actor.setAgeState(AgeState.CHILD);
        target.setAgeState(AgeState.ADULT);
        WorldTestHelper.setParents(actor, target.getUniqueId(), -1); // actor の父は target

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
    }

    @Test
    void testHandleSkinship_ChildActor_SisterTarget_ReturnsTrueWithSkinship() {
        // child actor + sister (same parent) → スキンシップ + true
        ConstState cs = new ConstState(0);
        cs.setFixedBoolean(true); // nextBoolean=true
        SimYukkuri.RND = cs;
        actor.setAgeState(AgeState.CHILD);
        target.setAgeState(AgeState.CHILD);
        Yukkuri commonParent = WorldTestHelper.createBody();
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(commonParent.getUniqueId(), commonParent);
        WorldTestHelper.setParents(actor, commonParent.getUniqueId(), -1);
        WorldTestHelper.setParents(target, commonParent.getUniqueId(), -1);

        boolean result = YukkuriSkinshipRule.handleSkinship(target, actor);

        assertTrue(result);
    }
}
