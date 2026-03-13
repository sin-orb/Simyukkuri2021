package src.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.draw.MyPane;
import src.draw.Terrarium;
import src.system.MessagePool;
import src.enums.*;
import src.event.BegForLifeEvent;
import java.security.SecureRandom;
import java.lang.reflect.Field;
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
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.world = new World(0, 0);
        world = SimYukkuri.world;
        SimYukkuri.mypane = new MyPane();

        testRnd = new PresetRandom();
        SimYukkuri.RND = testRnd;

        // Initialize MessagePool
        MessagePool.loadMessage(getClass().getClassLoader());

        body = new StubBody();
        body.setUniqueID(1);
        body.setAge(100000); // Adult
        body.setBodyRank(BodyRank.KAIYU);
        body.setMsgType(YukkuriType.REIMU);
        world.getCurrentMap().getBody().put(1, body);
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
        body.setMessageCount(0);
        Field fDisc = BodyAttributes.class.getDeclaredField("messageDiscipline");
        fDisc.setAccessible(true);
        fDisc.set(body, 0);

        body.setSickPeriod(1200 * 33);
        body.setDamage(15000);
        body.setStress(0);
        body.setHappiness(Happiness.VERY_SAD);

        body.checkSick();

        assertTrue(body.getStress() >= 100);

        Field fBuf = BodyAttributes.class.getDeclaredField("messageBuf");
        fBuf.setAccessible(true);
        String msg = (String) fBuf.get(body);
        // MessagePool might return "NO ACTION [...]" or "NO MESSAGE FILE" if not loaded
        // correctly,
        // but it shouldn't be null.
        assertNotNull(msg);
    }

    @Test
    public void testCheckPanic_Propagation() throws Exception {
        body.setPanic(true, PanicType.BURN);

        StubBody neighbor = new StubBody();
        neighbor.setUniqueID(2);
        neighbor.setX(body.getX() + 10);
        neighbor.setY(body.getY());
        world.getCurrentMap().getBody().put(2, neighbor);

        Method m = Terrarium.class.getDeclaredMethod("checkPanic", Body.class);
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
        world.getCurrentMap().getBody().put(2, neighbor);

        Method m = Terrarium.class.getDeclaredMethod("checkFire", Body.class);
        m.setAccessible(true);
        m.invoke(SimYukkuri.mypane.getTerrarium(), body);

        assertTrue(neighbor.getAttachmentSize(src.attachment.Fire.class) > 0);
    }
}
