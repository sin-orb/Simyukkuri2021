package org.simyukkuri.entity.core.living.yukkuri;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.Terrarium;
import org.simyukkuri.enums.*;
import org.simyukkuri.event.impl.BegForLifeEvent;
import org.simyukkuri.util.WorldTestHelper;
import java.security.SecureRandom;
import java.lang.reflect.Method;

public class BodyBehaviorTest {

    private StubBody body;
    private World world;

    private static class PresetRandom extends SecureRandom {
        private boolean nextBoolValue = true;
        private int nextIntValue = 0;

        @Override
        public int nextInt(int n) {
            return nextIntValue % n;
        }

        @Override
        public boolean nextBoolean() {
            return nextBoolValue;
        }

        public void setNextInt(int v) {
            nextIntValue = v;
        }

        public void setNextBool(boolean b) {
            nextBoolValue = b;
        }
    }

    private PresetRandom testRnd;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World(0, 0);
        world = SimYukkuri.world;
        SimYukkuri.mypane = new MyPane();

        testRnd = new PresetRandom();
        SimYukkuri.RND = testRnd;

        WorldTestHelper.initializeLoadedMessagePool(getClass().getClassLoader());

        body = new StubBody();
        body.setUniqueID(1);
        body.setAge(100000); // Adult
        body.setRank(YukkuriRank.KAIYU);
        body.setMsgType(YukkuriType.REIMU);
        world.getCurrentMap().getYukkuriMap().put(1, body);
    }

    @AfterEach
    public void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    public void testBegForLife_VeryNice() {
        body.setAttitude(Attitude.VERY_NICE);
        body.setDamage(9000); // isDamaged() true
        testRnd.setNextInt(0); // frag = true for NormalP(2)

        body.begForLife();

        assertFalse(body.getEventList().isEmpty());
        assertTrue(body.getEventList().get(0) instanceof BegForLifeEvent);
    }

    @Test
    public void testBegForLife_SuperShithead() {
        body.setAttitude(Attitude.SUPER_SHITHEAD);
        body.setDamage(15000); // isDamagedHeavily() true
        body.setStress(10000); // isVeryStressful() true
        testRnd.setNextInt(0); // frag = true for RudeP(3)

        body.begForLife();

        assertFalse(body.getEventList().isEmpty());
    }

    @Test
    public void testCheckSick_Terminal() throws Exception {
        // Reset message constraints
        body.setMessageTicks(0);
        body.setSpeechDiscipline(0);

        body.setSickPeriod(1200 * 33);
        body.setDamage(15000);
        body.setStress(0);
        body.setHappiness(Happiness.VERY_SAD);

        body.checkSick();

        assertTrue(body.getStress() >= 100);

        assertNotNull(body.getMessageBuffer());
    }

    @Test
    public void testCheckPanic_Propagation() throws Exception {
        body.setPanic(true, PanicType.BURN);

        StubBody neighbor = new StubBody();
        neighbor.setUniqueID(2);
        neighbor.setX(body.getX() + 10);
        neighbor.setY(body.getY());
        world.getCurrentMap().getYukkuriMap().put(2, neighbor);

        Method m = Terrarium.class.getDeclaredMethod("checkPanic", Yukkuri.class);
        m.setAccessible(true);
        m.invoke(SimYukkuri.mypane.getTerrarium(), body);

        assertNotNull(neighbor.getPanicType());
        assertEquals(PanicType.FEAR, neighbor.getPanicType());
    }

    @Test
    public void testCheckFire_Propagation() throws Exception {
        body.giveFire();

        StubBody neighbor = new StubBody();
        neighbor.setUniqueID(2);
        neighbor.setX(body.getX() + 1);
        neighbor.setY(body.getY());
        world.getCurrentMap().getYukkuriMap().put(2, neighbor);

        Method m = Terrarium.class.getDeclaredMethod("checkFire", Yukkuri.class);
        m.setAccessible(true);
        m.invoke(SimYukkuri.mypane.getTerrarium(), body);

        assertTrue(neighbor.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.Fire.class) > 0);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ForcedBegForLifeStartsEventEvenWithoutRandomHit() {
            body.setAttitude(Attitude.VERY_NICE);
            body.setDamage(9000);
            testRnd.setNextInt(1);

            body.begForLife(true);

            assertFalse(body.getEventList().isEmpty());
            assertTrue(body.getEventList().get(0) instanceof BegForLifeEvent);
        }

        @Test
        void testScenario_PanicDoesNotPropagateToRaperBody() throws Exception {
            body.setPanic(true, PanicType.BURN);

            StubBody raper = new StubBody();
            raper.setUniqueID(2);
            raper.setX(body.getX() + 10);
            raper.setY(body.getY());
            raper.setRaper(true);
            world.getCurrentMap().getYukkuriMap().put(2, raper);

            Method m = Terrarium.class.getDeclaredMethod("checkPanic", Yukkuri.class);
            m.setAccessible(true);
            m.invoke(SimYukkuri.mypane.getTerrarium(), body);

            assertNull(raper.getPanicType());
        }

        @Test
        void testScenario_FireDoesNotPropagateToDistantBody() throws Exception {
            body.giveFire();

            StubBody distant = new StubBody();
            distant.setUniqueID(2);
            distant.setX(body.getX() + 1000);
            distant.setY(body.getY() + 1000);
            world.getCurrentMap().getYukkuriMap().put(2, distant);

            Method m = Terrarium.class.getDeclaredMethod("checkFire", Yukkuri.class);
            m.setAccessible(true);
            m.invoke(SimYukkuri.mypane.getTerrarium(), body);

            assertEquals(0, distant.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.Fire.class));
        }

        @Test
        void testScenario_AverageBodyDoesNotBegForLifeWithoutStressEvenWhenDamaged() {
            body.setAttitude(Attitude.AVERAGE);
            body.setDamage(9000);
            body.setStress(0);
            testRnd.setNextInt(0);

            body.begForLife();

            assertTrue(body.getEventList().isEmpty());
        }

        @Test
        void testScenario_FoolShitheadDoesNotBegForLifeWithoutStressTrigger() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setIntelligence(Intelligence.FOOL);
            body.setDamage(9000);
            body.setStress(0);
            testRnd.setNextInt(0);

            body.begForLife();

            assertTrue(body.getEventList().isEmpty());
        }
    }
}
