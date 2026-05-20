package org.simyukkuri.entity.core.living.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.security.SecureRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.engine.TerrariumWorldLogic;
import org.simyukkuri.engine.World;
import org.simyukkuri.engine.YukkuriTickProcessor;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.event.impl.BegForLifeEvent;
import org.simyukkuri.util.WorldTestHelper;

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

        // public void setNextBool(boolean b) {
        // nextBoolValue = b;
        // }
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
        world.getCurrentWorldState().getYukkuriRegistry().put(1, body);
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

        assertFalse(body.getEvents().isEmpty());
        assertTrue(body.getEvents().get(0) instanceof BegForLifeEvent);
    }

    @Test
    public void testBegForLife_SuperShithead() {
        body.setAttitude(Attitude.SUPER_SHITHEAD);
        body.setDamage(15000); // isDamagedHeavily() true
        body.setStress(10000); // isVeryStressful() true
        testRnd.setNextInt(0); // frag = true for RudeP(3)

        body.begForLife();

        assertFalse(body.getEvents().isEmpty());
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
    public void testCheckPanic_Propagation() {
        body.setPanic(true, PanicType.BURN);

        StubBody neighbor = new StubBody();
        neighbor.setUniqueID(2);
        neighbor.setX(body.getX() + 10);
        neighbor.setY(body.getY());
        world.getCurrentWorldState().getYukkuriRegistry().put(2, neighbor);

        TerrariumWorldLogic.checkPanic(body);

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
        world.getCurrentWorldState().getYukkuriRegistry().put(2, neighbor);

        Method m = YukkuriTickProcessor.class.getDeclaredMethod("checkFire",
                Yukkuri.class, org.simyukkuri.system.WorldState.class);
        m.setAccessible(true);
        m.invoke(null, body, world.getCurrentWorldState());

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

            assertFalse(body.getEvents().isEmpty());
            assertTrue(body.getEvents().get(0) instanceof BegForLifeEvent);
        }

        @Test
        void testScenario_PanicDoesNotPropagateToRaperBody() {
            body.setPanic(true, PanicType.BURN);

            StubBody raper = new StubBody();
            raper.setUniqueID(2);
            raper.setX(body.getX() + 10);
            raper.setY(body.getY());
            raper.setRaper(true);
            world.getCurrentWorldState().getYukkuriRegistry().put(2, raper);

            TerrariumWorldLogic.checkPanic(body);

            assertNull(raper.getPanicType());
        }

        @Test
        void testScenario_FireDoesNotPropagateToDistantBody() throws Exception {
            body.giveFire();

            StubBody distant = new StubBody();
            distant.setUniqueID(2);
            distant.setX(body.getX() + 1000);
            distant.setY(body.getY() + 1000);
            world.getCurrentWorldState().getYukkuriRegistry().put(2, distant);

            Method m = YukkuriTickProcessor.class.getDeclaredMethod("checkFire",
                    Yukkuri.class, org.simyukkuri.system.WorldState.class);
            m.setAccessible(true);
            m.invoke(null, body, world.getCurrentWorldState());

            assertEquals(0, distant.getAttachmentSize(org.simyukkuri.entity.core.attachment.impl.Fire.class));
        }

        @Test
        void testScenario_AverageBodyDoesNotBegForLifeWithoutStressEvenWhenDamaged() {
            body.setAttitude(Attitude.AVERAGE);
            body.setDamage(9000);
            body.setStress(0);
            testRnd.setNextInt(0);

            body.begForLife();

            assertTrue(body.getEvents().isEmpty());
        }

        @Test
        void testScenario_FoolShitheadDoesNotBegForLifeWithoutStressTrigger() {
            body.setAttitude(Attitude.SHITHEAD);
            body.setIntelligence(Intelligence.FOOL);
            body.setDamage(9000);
            body.setStress(0);
            testRnd.setNextInt(0);

            body.begForLife();

            assertTrue(body.getEvents().isEmpty());
        }
    }
}
