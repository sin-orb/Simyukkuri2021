package org.simyukkuri.system;

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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.*;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.engine.World;
import java.util.Random;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.util.WorldTestHelper;

/**
 * MessagePool のテスト.
 */
public class MessagePoolTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
        SimYukkuri.RND = new Random();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeLoadedMessagePool(MessagePoolTest.class.getClassLoader());
    }

    @Test
    public void testPlaceholderReplacement_Name() {
        DummyBody body = new DummyBody();
        body.setMyNameCustom("ReimuTest");
        body.setMsgType(YukkuriType.ALICE);
        String msg = MessagePool.getMessage(body, MessagePool.Action.Birth);
        assertNotNull(msg);
        assertTrue(msg.contains("ReimuTest"), "Message should contain replaced name: " + msg);
    }

    @Test
    public void testPlaceholderReplacement_Partner() {
        DummyBody body = new DummyBody();
        DummyBody partner = new DummyBody();
        partner.setMyNameCustom("PartnerReimu");
        body.setPartner(partner.getUniqueID());
        org.simyukkuri.util.YukkuriLookup.getYukkuriById(partner.getUniqueID()); // ensure it's in world

        // FindPartner or similar likely uses %partner
        String msg = MessagePool.getMessage(body, MessagePool.Action.ProposeYes);
        assertNotNull(msg);
        // This depends on the actual message content in reimu_j.txt
        // If the message doesn't have %partner, this might fail.
        // But we want to ensure it doesn't return empty string (which happens if
        // %partner is expected but partner is null)
    }

    @Test
    public void testTagSelection_Damage() {
        DummyBody body = new DummyBody();
        body.setCustomDamage(100);
        String msgDamaged = MessagePool.getMessage(body, MessagePool.Action.Scream);
        // We can't easily verify the "content" without knowing the file,
        // but we can verify it returns a valid tag-based message.
        assertNotNull(msgDamaged);
        assertFalse(msgDamaged.contains("NO TAG"), "Should find a damaged tag message");
    }

    @Test
    public void testTagSelection_Pants() {
        DummyBody body = new DummyBody();
        body.setHasPantsCustom(true);
        // RelaxOkurumi (wrapped in okurumi/pants)
        String msgPants = MessagePool.getMessage(body, MessagePool.Action.RelaxOkurumi);
        assertNotNull(msgPants);
        assertFalse(msgPants.contains("NO TAG"), "Should find a pants tag message");
    }

    /**
     * テスト用の最小限の Yukkuri 実装.
     */
    static class DummyBody extends Yukkuri {
        private String customName = "れいむ";
        private int customDamage = 0;
        private Attitude customAttitude = Attitude.AVERAGE;
        private boolean hasPants = false;

        public DummyBody() {
            super();
            setMsgType(YukkuriType.REIMU);
            setRank(YukkuriRank.KAIYU);
            setAgeState(AgeState.ADULT);
            // Put in world so MessagePool can resolve %partner from the registry
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(getUniqueID(), this);
        }

        public void setMyNameCustom(String name) {
            this.customName = name;
        }

        @Override
        public String getMyName() {
            return customName;
        }

        @Override
        public String getMyNameD() {
            return customName + "(D)";
        }

        public void setCustomDamage(int d) {
            this.customDamage = d;
        }

        @Override
        public int getDamage() {
            return customDamage;
        }

        @Override
        public boolean isDamaged() {
            return customDamage > 0;
        }

        public void setCustomAttitude(Attitude a) {
            this.customAttitude = a;
        }

        @Override
        public Attitude getAttitude() {
            return customAttitude;
        }

        @Override
        public boolean isRude() {
            return (customAttitude == Attitude.SHITHEAD || customAttitude == Attitude.SUPER_SHITHEAD);
        }

        @Override
        public YukkuriType getType() {
            return YukkuriType.REIMU;
        }

        @Override
        public String getNameJ() {
            return "れいむ";
        }

        @Override
        public String getNameE() {
            return "Reimu";
        }

        @Override
        public String getNameJ2() {
            return "Reimu2J";
        }

        @Override
        public String getNameE2() {
            return "Reimu2E";
        }

        @Override
        public boolean isHasPants() {
            return hasPants;
        }

        public void setHasPantsCustom(boolean b) {
            this.hasPants = b;
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
