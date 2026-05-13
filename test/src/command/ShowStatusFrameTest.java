package src.command;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.MyPane;
import src.draw.World;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.enums.Intelligence;
import src.enums.YukkuriType;
import src.system.Sprite;
import src.util.WorldTestHelper;

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
        b.setBodySpr(spr);
        b.setExpandSpr(expSpr);
        b.setBraidSpr(brdSpr);
        b.setAgeState(age);
        b.setMsgType(YukkuriType.REIMU);
        b.setIntelligence(Intelligence.AVERAGE);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    public void testGiveBodyInfoUpdatesSelectBody() {
        Yukkuri b = createReimuBody(AgeState.ADULT);
        ShowStatusFrame.getInstance().giveBodyInfo(b);

        assertSame(b, MyPane.getSelectBody());
    }

    private static boolean hasDisplay() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = osName.contains("windows");
        return isWindows || System.getenv("DISPLAY") != null;
    }
}
