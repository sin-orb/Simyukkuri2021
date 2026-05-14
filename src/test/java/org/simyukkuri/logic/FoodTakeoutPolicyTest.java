package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

class FoodTakeoutPolicyTest {

    private Yukkuri body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
        SimYukkuri.RND = new ConstState(1);

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(body.getUniqueID(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void checkTakeout_UnunSlaveWithoutSlaveToilet_ReturnsFalse() {
        body.setPublicRank(PublicRank.UnunSlave);
        body.setHungry(body.getHungryLimit() / 2);

        Shit shit = new Shit();
        shit.setX(120);
        shit.setY(120);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        assertFalse(FoodTakeoutPolicy.checkTakeout(body, shit));
    }

    @Test
    void checkTakeout_UnunSlaveWithSlaveToiletAndNoHit_ReturnsTrue() {
        body.setPublicRank(PublicRank.UnunSlave);
        body.setHungry(body.getHungryLimit() / 2);

        Shit shit = new Shit();
        shit.setX(120);
        shit.setY(120);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        Toilet slaveToilet = new Toilet();
        slaveToilet.setForSlave(true);
        slaveToilet.setScreenPivot(10, 10);
        SimYukkuri.world.getCurrentMap().getToilet().put(slaveToilet.getObjId(), slaveToilet);

        assertTrue(FoodTakeoutPolicy.checkTakeout(body, shit));
    }

    @Test
    void checkTakeout_FoodWithoutFamily_ReturnsFalse() {
        body.setHungry(body.getHungryLimit() / 2);

        Bed favBed = new Bed(300, 300, 0);
        SimYukkuri.world.getCurrentMap().getBed().put(favBed.getObjId(), favBed);
        body.setFavoriteItem(FavItemType.BED, favBed);

        Food food = new Food(300, 300, Food.FoodType.FOOD.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        assertFalse(FoodTakeoutPolicy.checkTakeout(body, food));
    }

    @Test
    void checkTakeout_FoodWithFamilyAndNoOverlap_ReturnsTrue() {
        body.setHungry(body.getHungryLimit() / 2);

        Yukkuri partner = WorldTestHelper.createBody();
        partner.setX(200);
        partner.setY(200);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(partner.getUniqueID(), partner);
        body.setPartner(partner.getUniqueID());

        Bed favBed = new Bed(300, 300, 0);
        SimYukkuri.world.getCurrentMap().getBed().put(favBed.getObjId(), favBed);
        body.setFavoriteItem(FavItemType.BED, favBed);

        Food food = new Food(500, 500, Food.FoodType.FOOD.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        assertTrue(FoodTakeoutPolicy.checkTakeout(body, food));
    }
}
