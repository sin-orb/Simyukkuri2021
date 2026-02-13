package src.base;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.enums.Burst;
import src.system.Sprite;

public class BodyAttributesBurstTest {

    @Test
    public void testBurstStateWithNullSprites() {
        StubBodyAttributes body = new StubBodyAttributes();
        body.bodySpr = null;
        assertEquals(Burst.NONE, body.getBurstState());
    }

    @Test
    public void testBurstStateWithZeroOriginSize() {
        StubBodyAttributes body = new StubBodyAttributes();
        body.bodySpr = new Sprite[3];
        for (int i = 0; i < body.bodySpr.length; i++) {
            body.bodySpr[i] = new Sprite();
            body.bodySpr[i].setImageW(0);
            body.bodySpr[i].setImageH(0);
        }
        assertEquals(Burst.NONE, body.getBurstState());
    }
}
