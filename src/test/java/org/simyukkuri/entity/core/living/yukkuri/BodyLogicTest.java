package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.WorldState;

public class BodyLogicTest {

    private StubBody body;
    private World world;
    private WorldState gameMap;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.world = new World(0, 0);
        world = SimYukkuri.world;
        gameMap = world.getCurrentWorldState();

        // Setup MyPane to avoid NPE in bodyCut
        try {
            SimYukkuri.mypane = new MyPane();
        } catch (Throwable t) {
            assertNotNull(t);
        }

        // Manually initialize Vomit static arrays to avoid NPE in constructor
        setupVomitStatics();

        body = new StubBody();
        body.setUniqueId(1);
        body.setAge(100000);
        body.setShitType(YukkuriType.REIMU);
        gameMap.getYukkuriRegistry().put(1, body);
    }

    private void setupVomitStatics() throws Exception {
        int types = YukkuriType.values().length;
        setStaticField(Vomit.class, "pivX", new int[types][3]);
        setStaticField(Vomit.class, "pivY", new int[types][3]);
        setStaticField(Vomit.class, "imgW", new int[types][3]);
        setStaticField(Vomit.class, "imgH", new int[types][3]);
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    public void testEatBody_DeadBodyCrush() {
        body.setDead(true);
        body.setAnkoAmount(10000);
        // limit/2 for Adult is 8400.
        body.eatYukkuri(2000);
        assertTrue(body.isCrushed(), "Should be crushed when amount <= limit/2 (8400)");
    }

    @Test
    public void testEatBody_DeadBodyRemove() {
        body.setDead(true);
        body.setAnkoAmount(1000);
        body.eatYukkuri(1000);
        assertTrue(body.isRemoved());
    }

    @Test
    public void testEatBody_LiveBodyDamage() {
        body.setDead(false);
        body.setHungry(100);
        body.setAnkoAmount(10000);
        body.eatYukkuri(200);
        assertEquals(-100, body.getHungry());
        assertTrue(body.getDamage() > 0);
    }

    @Test
    public void testEatBody_LiveBodyDeath() {
        body.setDead(false);
        body.setAnkoAmount(1000);
        body.eatYukkuri(1000);
        assertTrue(body.isDead());
        assertTrue(body.isCrushed());
    }

    @Test
    public void testPruneRemovedFamilyMembers() throws Exception {
        StubBody sister = new StubBody();
        sister.setObjId(2);
        gameMap.getYukkuriRegistry().put(2, sister);

        StubBody removedChild = new StubBody();
        removedChild.setObjId(3);
        removedChild.remove();
        gameMap.getYukkuriRegistry().put(3, removedChild);

        body.getSisters().add(2);
        body.getChildren().add(3);

        Method m = Yukkuri.class.getDeclaredMethod("pruneRemovedFamilyMembers");
        m.setAccessible(true);
        m.invoke(body);

        assertTrue(body.getSisters().contains(2));
        assertFalse(body.getChildren().contains(3));
    }
}
