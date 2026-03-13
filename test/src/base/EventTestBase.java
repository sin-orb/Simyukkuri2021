package src.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import src.SimYukkuri;
import src.base.Body;
import src.draw.Translate;
import src.util.WorldTestHelper;

public class EventTestBase {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    @AfterEach
    public void tearDown() {
        WorldTestHelper.resetWorld();
    }

    protected Body createBody(int id, int x, int y) {
        Body b = WorldTestHelper.createBody();
        b.setObjId(id);
        b.setX(x);
        b.setY(y);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }
}
