package src.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.util.WorldTestHelper;

public class EventTestBase {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
    }

    @AfterEach
    public void tearDown() {
        WorldTestHelper.resetWorld();
    }

    protected Yukkuri createBody(int id, int x, int y) {
        Yukkuri b = WorldTestHelper.createBody();
        b.setObjId(id);
        b.setX(x);
        b.setY(y);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }
}
