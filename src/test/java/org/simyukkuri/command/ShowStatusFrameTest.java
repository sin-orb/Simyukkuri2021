package org.simyukkuri.command;

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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

@Disabled("GUI-dependent")
public class ShowStatusFrameTest {

    @BeforeAll
    public static void setUpClass() {
        assumeTrue(hasDisplay());
        WorldTestHelper.initializeLoadedMessagePool(ShowStatusFrameTest.class.getClassLoader());
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
    }

    private Yukkuri createReimuBody(AgeState age) {
        Yukkuri b = new Reimu();
        Sprite[] spr = new Sprite[3];
        Sprite[] expSpr = new Sprite[3];
        Sprite[] brdSpr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite();
            spr[i].setImageW(100);
            spr[i].setImageH(100);
            expSpr[i] = new Sprite();
            brdSpr[i] = new Sprite();
        }
        b.setSpriteSet(spr);
        b.setExpandSpr(expSpr);
        b.setBraidSpr(brdSpr);
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        b.setIntelligence(Intelligence.AVERAGE);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    public void testGiveBodyInfoUpdatesSelectBody() {
        Yukkuri b = createReimuBody(AgeState.ADULT);
        ShowStatusFrame.getInstance().giveYukkuriInfo(b);

        assertSame(b, MyPane.getSelectedYukkuri());
    }

    private static boolean hasDisplay() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = osName.contains("windows");
        return isWindows || System.getenv("DISPLAY") != null;
    }
}
