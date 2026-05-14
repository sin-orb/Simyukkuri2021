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

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.util.WorldTestHelper;

public class UnunSlaveDebug {
    @Test
    void testDebug() throws Exception {
        WorldTestHelper.resetStates();
        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f });
        Translate.createTransTable(false);
        WorldTestHelper.initializeMinimalWorld();

        Yukkuri body = WorldTestHelper.createBody();
        body.setHungry(body.getHungryLimit() / 4);
        body.setPublicRank(PublicRank.UnunSlave);

        // Set eyesightBase via reflection
        java.lang.reflect.Field eyesightField = org.simyukkuri.entity.core.living.LivingEntity.class.getDeclaredField("eyesightBase");
        eyesightField.setAccessible(true);
        eyesightField.set(body, 1000000);

        Shit shit = new Shit();
        shit.setX(100);
        shit.setY(100);
        body.setX(50);
        body.setY(50);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        SimYukkuri.world.getCurrentMap().getToilet().clear();

        boolean[] forceEat = { false };
        java.lang.reflect.Method m = FoodLogic.class.getDeclaredMethod("searchFoodForUnunSlave", Yukkuri.class,
                boolean[].class);
        m.setAccessible(true);
        Entity found = (Entity) m.invoke(null, body, forceEat);

        System.out.println("DEBUG: body pos=(" + body.getX() + "," + body.getY() + ")");
        System.out.println("DEBUG: shit pos=(" + shit.getX() + "," + shit.getY() + ")");
        System.out.println("DEBUG: minDistance=" + body.getEyesightBase());
        System.out.println("DEBUG: actual distance squared="
                + Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY()));
        System.out.println("DEBUG: acrossBarrier="
                + org.simyukkuri.field.impl.Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(), 4096)); // 4096 is
                                                                                                             // Barrier.BARRIER_KEKKAI

        if (found == null) {
            System.out.println("DEBUG: found is NULL");
        } else {
            System.out.println("DEBUG: found objective ID=" + found.getObjId());
        }

        assertTrue(found != null);
    }
}
