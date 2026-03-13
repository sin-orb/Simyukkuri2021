package src.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.game.Vomit;
import src.system.MapPlaceData;
import src.enums.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

public class BodyLogicTest {

    private StubBody body;
    private World world;
    private MapPlaceData gameMap;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.world = new World(0, 0);
        world = SimYukkuri.world;
        gameMap = world.getCurrentMap();

        // Setup MyPane to avoid NPE in bodyCut
        try {
            SimYukkuri.mypane = new MyPane();
        } catch (Throwable t) {
        }

        // Manually initialize Vomit static arrays to avoid NPE in constructor
        setupVomitStatics();

        body = new StubBody();
        body.setUniqueID(1);
        body.setAge(100000);
        body.setShitType(YukkuriType.REIMU);
        gameMap.getBody().put(1, body);
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
        body.setBodyAmount(10000);
        // limit/2 for Adult is 8400.
        body.eatBody(2000);
        assertTrue(body.isCrushed(), "Should be crushed when amount <= limit/2 (8400)");
    }

    @Test
    public void testEatBody_DeadBodyRemove() {
        body.setDead(true);
        body.setBodyAmount(1000);
        body.eatBody(1000);
        assertTrue(body.isRemoved());
    }

    @Test
    public void testEatBody_LiveBodyDamage() {
        body.setDead(false);
        body.setHungry(100);
        body.setBodyAmount(10000);
        body.eatBody(200);
        assertEquals(-100, body.getHungry());
        assertTrue(body.getDamage() > 0);
    }

    @Test
    public void testEatBody_LiveBodyDeath() {
        body.setDead(false);
        body.setBodyAmount(1000);
        body.eatBody(1000);
        assertTrue(body.isDead());
        assertTrue(body.isCrushed());
    }

    @Test
    public void testCheckRemovedFamilyList() throws Exception {
        StubBody sister = new StubBody();
        sister.setUniqueID(2);
        gameMap.getBody().put(2, sister);

        StubBody removedChild = new StubBody();
        removedChild.setUniqueID(3);
        removedChild.remove();
        gameMap.getBody().put(3, removedChild);

        body.getSisterList().add(2);
        body.getChildrenList().add(3);

        Method m = Body.class.getDeclaredMethod("checkRemovedFamilyList");
        m.setAccessible(true);
        m.invoke(body);

        assertTrue(body.getSisterList().contains(2));
        assertFalse(body.getChildrenList().contains(3));
    }
}
