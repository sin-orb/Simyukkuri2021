package src.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.MyPane;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Intelligence;
import src.enums.YukkuriType;
import src.system.MessagePool;
import src.system.Sprite;
import src.yukkuri.Reimu;

@Disabled("GUI-dependent")
public class ShowStatusFrameTest {

    @BeforeAll
    public static void setUpClass() {
        assumeTrue(hasDisplay());
        MessagePool.loadMessage(ShowStatusFrameTest.class.getClassLoader());
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
    }

    private Body createReimuBody(AgeState age) {
        Body b = new Reimu();
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
        Body b = createReimuBody(AgeState.ADULT);
        ShowStatusFrame.getInstance().giveBodyInfo(b);

        assertSame(b, MyPane.getSelectBody());
    }

    private static boolean hasDisplay() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = osName.contains("windows");
        return isWindows || System.getenv("DISPLAY") != null;
    }
}
