package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.MessageBundle.MessageTag;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.RandomSource;

/**
 * MessagePool のテスト.
 */
public class MessagePoolTest {

    private static final RandomSource DETERMINISTIC_RANDOM = new RandomSource() {
        @Override
        public int nextInt(int bound) {
            return 0;
        }

        @Override
        public boolean nextBoolean() {
            return false;
        }
    };

    private static Object previousPool;

    @BeforeAll
    public static void setUpClass() throws Exception {
        System.setProperty("java.awt.headless", "true");
        Field poolField = MessagePool.class.getDeclaredField("pool_j");
        poolField.setAccessible(true);
        previousPool = poolField.get(null);
        poolField.set(null, createSyntheticMessagePool());
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        Field poolField = MessagePool.class.getDeclaredField("pool_j");
        poolField.setAccessible(true);
        poolField.set(null, previousPool);
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        GameLocale.setOverride(() -> Locale.JAPANESE);
        GameRandom.setOverride(DETERMINISTIC_RANDOM);
    }

    @AfterEach
    public void tearDown() {
        GameRandom.clearOverride();
        GameLocale.clearOverride();
    }

    @Test
    public void testPlaceholderReplacementNameAndName2() {
        DummyBody body = new DummyBody(YukkuriType.HYBRIDYUKKURI, "HybridBody", "HybridBody2");
        String msg = MessagePool.getMessage(body, MessagePool.Action.Birth);
        assertEquals("ゆっきゅりちていっちぇね！HybridBodyはHybridBody2だよ！", msg);
    }

    @Test
    public void testPlaceholderReplacementPartner() {
        DummyBody body = new DummyBody(YukkuriType.DEIBU, "BodyReimu", "BodyReimu2");
        DummyBody partner = new DummyBody(YukkuriType.DEIBU, "PartnerReimu", "PartnerReimu2");
        body.setPartner(partner.getUniqueId());

        String msg = MessagePool.getMessage(body, MessagePool.Action.Propose);
        assertEquals("ゆん！PartnerReimu！BodyReimuのおよめさんになってほしいよ！", msg);
    }

    @Test
    public void testTagSelectionDamage() {
        DummyBody body = new DummyBody(YukkuriType.REIMU, "ReimuTest", "ReimuTest2");
        body.setCustomDamage(100);

        String msg = MessagePool.getMessage(body, MessagePool.Action.Scream);
        assertEquals("ゆ、ゆ、ゆ、、、", msg);
    }

    @Test
    public void testTagSelectionPants() {
        DummyBody body = new DummyBody(YukkuriType.REIMU, "ReimuTest", "ReimuTest2");
        body.setHasPantsCustom(true);

        String msg = MessagePool.getMessage(body, MessagePool.Action.RelaxOkurumi);
        assertEquals("う～♪う～♪", msg);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, MessageBundle>[] createSyntheticMessagePool() {
        HashMap<String, MessageBundle>[] pool = new HashMap[YukkuriType.values().length];
        pool[YukkuriType.HYBRIDYUKKURI.ordinal()] = new HashMap<String, MessageBundle>();
        pool[YukkuriType.DEIBU.ordinal()] = new HashMap<String, MessageBundle>();
        pool[YukkuriType.REIMU.ordinal()] = new HashMap<String, MessageBundle>();

        put(pool[YukkuriType.HYBRIDYUKKURI.ordinal()], MessagePool.Action.Birth,
                "normal_", "ゆっきゅりちていっちぇね！%nameは%name2だよ！", MessageTag.normal);
        put(pool[YukkuriType.DEIBU.ordinal()], MessagePool.Action.Propose,
                "normal_adult_", "ゆん！%partner！%nameのおよめさんになってほしいよ！",
                MessageTag.normal, MessageTag.adult);
        put(pool[YukkuriType.REIMU.ordinal()], MessagePool.Action.Scream,
                "normal_adult_damage_", "ゆ、ゆ、ゆ、、、",
                MessageTag.normal, MessageTag.adult, MessageTag.damage);
        put(pool[YukkuriType.REIMU.ordinal()], MessagePool.Action.RelaxOkurumi,
                "normal_adult_pants_", "う～♪う～♪",
                MessageTag.normal, MessageTag.adult, MessageTag.pants);

        return pool;
    }

    private static void put(HashMap<String, MessageBundle> map, MessagePool.Action action, String key,
            String message, MessageTag... tags) {
        MessageBundle bundle = new MessageBundle();
        bundle.setNormalFlag(true);
        for (MessageTag tag : tags) {
            bundle.getNormalTag()[tag.ordinal()] = true;
        }
        bundle.getMessages().put(key, new String[] { message });
        map.put(action.name(), bundle);
    }

    /**
     * テスト用の最小限の Yukkuri 実装.
     */
    static class DummyBody extends Yukkuri {
        private final YukkuriType type;
        private final String customName;
        private final String customName2;
        private int customDamage;
        private Attitude customAttitude = Attitude.AVERAGE;
        private boolean hasPants;

        DummyBody(YukkuriType type, String customName, String customName2) {
            super();
            this.type = type;
            this.customName = customName;
            this.customName2 = customName2;
            setMsgType(type);
            setRank(YukkuriRank.KAIYU);
            setAgeState(AgeState.ADULT);
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(getUniqueId(), this);
        }

        @Override
        public String getMyName() {
            return customName;
        }

        @Override
        public String getMyNameD() {
            return customName + "(D)";
        }

        void setCustomDamage(int damage) {
            this.customDamage = damage;
        }

        @Override
        public int getDamage() {
            return customDamage;
        }

        @Override
        public boolean isDamaged() {
            return customDamage > 0;
        }

        @Override
        public Attitude getAttitude() {
            return customAttitude;
        }

        @Override
        public boolean isRude() {
            return customAttitude == Attitude.SHITHEAD || customAttitude == Attitude.SUPER_SHITHEAD;
        }

        @Override
        public YukkuriType getType() {
            return type;
        }

        @Override
        public String getNameJ() {
            return customName;
        }

        @Override
        public String getNameE() {
            return customName;
        }

        @Override
        public String getNameJ2() {
            return customName2;
        }

        @Override
        public String getNameE2() {
            return customName2;
        }

        @Override
        public boolean isHasPants() {
            return hasPants;
        }

        void setHasPantsCustom(boolean hasPants) {
            this.hasPants = hasPants;
        }

        @Override
        public int getImage(int type, int direction, YukkuriLayer layer, int index) {
            return 0;
        }

        @Override
        public void tuneParameters() {
        }

        @Override
        public boolean isImageLoaded() {
            return true;
        }

        @Override
        public Point4y[] getMountPoint(String key) {
            return null;
        }

        @Override
        public int getNonYukkuriDiseaseTolerance() {
            return 100;
        }
    }
}
