package src.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import src.SimYukkuri;
import src.draw.World;
import src.enums.*;
import src.system.BodyLayer;
import src.draw.Point4y;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

public class BodyRenderingTest {

    private RenderingStubBody body;
    private BodyLayer layer;

    public static class RenderingStubBody extends StubBody {
        public List<Integer> codes = new ArrayList<>();

        public RenderingStubBody() {
            super();
        }

        @Override
        public int getImage(int type, int direction, BodyLayer layer, int index) {
            codes.add(type);
            return 1;
        }
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        body = new RenderingStubBody();
        layer = new BodyLayer();
        for (int i = 0; i < 3; i++) {
            body.bodySpr[i] = new src.system.Sprite();
            body.expandSpr[i] = new src.system.Sprite();
            body.braidSpr[i] = new src.system.Sprite();
        }
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = null;
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field == null)
            throw new NoSuchFieldException(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testGetFaceImage_DeadAndPealed() {
        body.setDead(true);
        body.setPealed(true);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.PEALEDDEADFACE.ordinal()));
    }

    @Test
    public void testGetFaceImage_DeadNotPealed() {
        body.setDead(true);
        body.setPealed(false);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.DEAD.ordinal()));
    }

    @Test
    public void testGetFaceImage_NYD() throws Exception {
        setField(body, "eCoreAnkoState", CoreAnkoState.NonYukkuriDisease);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.NYD_FRONT_WIDE.ordinal()));
    }

    @Test
    public void testGetFaceImage_Exciting() {
        body.setExciting(true);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.EXCITING.ordinal()));
    }

    @Test
    public void testGetFaceImage_Pain() {
        body.setCriticalDamege(CriticalDamegeType.CUT);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.PAIN.ordinal()));
    }

    @Test
    public void testGetBodyBaseImage_Crushed() throws Exception {
        body.setCrushed(true);
        setField(body, "okazari", null);
        body.getBodyBaseImage(layer);
        assertTrue(body.codes.contains(ImageCode.CRUSHED2.ordinal()), "Actual: " + body.codes);
    }

    @Test
    public void testGetBodyBaseImage_Shitting() {
        body.setShitting(true);
        body.getBodyBaseImage(layer);
        assertTrue(body.codes.contains(ImageCode.FRONT_SHIT.ordinal()));
    }

    @Test
    public void testGetEffectImage_HungryAndWet() throws Exception {
        // Set age to Adult (CHILDLIMITorg is 50400)
        body.setAge(100000);
        body.setHungry(0);
        // damageState is calculated from damage field.
        // For Adult, limit is 16800. 16800 / 2 = 8400.
        body.setDamage(8400);
        assertTrue(body.getDamageState() != Damage.NONE,
                "Damage state should not be NONE, was: " + body.getDamageState());

        body.setWet(true);
        body.getEffectImage(layer);
        // HUNGRY2 ordinal is 129
        assertTrue(body.codes.contains(129), "Expected HUNGRY2(129), Actual: " + body.codes);
        assertTrue(body.codes.contains(120), "Expected WET(120), Actual: " + body.codes);
    }

    @Test
    public void testGetEffectImage_SickProgression() throws Exception {
        setField(body, "sickPeriod", 100000);
        body.getEffectImage(layer);
        assertTrue(body.codes.contains(ImageCode.SICK3.ordinal()));
    }

    @Test
    public void testGetAbnormalBodyImage_Melt() {
        body.setMelt(true);
        body.getAbnormalBodyImage(layer);
        assertTrue(body.codes.contains(ImageCode.MELT.ordinal()));
    }

    @Test
    public void testGetFaceImage_BlinkingUnyo() throws Exception {
        SimYukkuri.UNYO = true;
        body.setSleeping(true);
        setField(body, "mabatakiCnt", 1);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.EYE2.ordinal()));

        body.codes.clear();
        setField(body, "mabatakiCnt", 4);
        body.getFaceImage(layer);
        assertTrue(body.codes.contains(ImageCode.EYE3.ordinal()));

        SimYukkuri.UNYO = false;
    }
}
