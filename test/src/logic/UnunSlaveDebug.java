package src.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Yukkuri;
import src.base.Entity;
import src.draw.Translate;
import src.enums.PublicRank;
import src.game.Shit;
import src.util.WorldTestHelper;

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
        java.lang.reflect.Field eyesightField = src.entity.living.LivingEntity.class.getDeclaredField("eyesightBase");
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
                + src.field.impl.Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(), 4096)); // 4096 is
                                                                                                             // Barrier.BARRIER_KEKKAI

        if (found == null) {
            System.out.println("DEBUG: found is NULL");
        } else {
            System.out.println("DEBUG: found objective ID=" + found.getObjId());
        }

        assertTrue(found != null);
    }
}
