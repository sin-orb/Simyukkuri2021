package src.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.game.Vomit;
import src.game.Stalk;
import src.game.Dna;
import src.system.MapPlaceData;
import src.enums.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class BodyMetabolismTest {

    private StubBody body;
    private World world;
    private MapPlaceData gameMap;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.world = new World(0, 0);
        world = SimYukkuri.world;
        gameMap = world.getCurrentMap();
        SimYukkuri.mypane = new MyPane();

        setupVomitStatics();
        resetTerrariumSteams();

        body = new StubBody();
        body.setUniqueID(1);
        body.setAge(100000); // Adult
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

    private void resetTerrariumSteams() throws Exception {
        setStaticField(Terrarium.class, "humid", false);
        setStaticField(Terrarium.class, "antifungalSteam", false);
        setStaticField(Terrarium.class, "orangeSteam", false);
        setStaticField(Terrarium.class, "sugerSteam", false);
        setStaticField(Terrarium.class, "poisonSteam", false);
        setStaticField(Terrarium.class, "endlessFurifuriSteam", false);
    }

    private void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    public void testCheckHungry_Complex() throws Exception {
        // 1. Super eating case
        body.setHungry(100);
        Field f = BodyAttributes.class.getDeclaredField("noHungrybySupereatingTimePeriod");
        f.setAccessible(true);
        f.set(body, 10);
        body.checkHungry();
        assertEquals(101, body.getHungry()); // 100 + TICK

        f.set(body, 0);

        // 2. Unbirth case
        body.setHungry(1000);
        body.setUnBirth(true);
        body.setHasStalk(false);
        body.checkHungry();
        assertEquals(900, body.getHungry()); // 1000 - TICK * 100

        // 3. Stalks and Babies impact
        body.setUnBirth(false);
        body.setHungry(5000);
        body.setHasStalk(true);
        ArrayList<Stalk> stalks = new ArrayList<>();
        stalks.add(new Stalk());
        stalks.add(new Stalk());
        body.setStalks(stalks);
        body.setHasBaby(true);
        Dna dna = new Dna();
        dna.setType(YukkuriType.REIMU.getTypeID());
        body.getBabyTypes().add(dna);

        body.checkHungry();
        // Base -1, Stalks -2*5=10, Baby -1 -> Total -12
        assertEquals(4988, body.getHungry());
    }

    @Test
    public void testCheckSick_Advanced() throws Exception {
        setStaticField(Terrarium.class, "humid", true);
        body.setDamage(10000);
        body.setWet(true);
        body.setDirtyPeriod(0);
        body.checkSick();
        assertEquals(5, body.getDirtyPeriod());

        body.setSickPeriod(1200 * 33);
        body.setDamage(15000);
        body.setStress(0);
        body.checkSick();
        assertTrue(body.getStress() > 0);
        assertEquals(Happiness.VERY_SAD, body.getHappiness());
    }

    @Test
    public void testCheckDamage_Advanced() throws Exception {
        // Deterministic check for CUT
        body.setCriticalDamege(CriticalDamegeType.CUT);
        body.setDamage(1000);
        body.setHungry(6000); // Not hungry
        body.checkDamage();
        // -1 (Natural healing) + 100 (CUT) = +99 -> 1099
        assertEquals(1099, body.getDamage());

        body.setCriticalDamege(null);

        // Orange Steam
        setStaticField(Terrarium.class, "orangeSteam", true);
        body.setDamage(5000);
        body.setHungry(6000);
        body.checkDamage();
        // -1 (Natural healing) - 50 (orangeSteam) = -51 -> 4949
        assertEquals(4949, body.getDamage());

        resetTerrariumSteams();

        // Poison Steam
        setStaticField(Terrarium.class, "poisonSteam", true);
        body.setDamage(5000);
        body.setHungry(6000);
        body.checkDamage();
        // -1 (Natural healing) + 100 (poisonSteam) = +99 -> 5099
        assertEquals(5099, body.getDamage());
    }
}
