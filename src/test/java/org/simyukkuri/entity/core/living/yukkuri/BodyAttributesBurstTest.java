package org.simyukkuri.entity.core.living.yukkuri;

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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Burst;
import org.simyukkuri.system.Sprite;

public class BodyAttributesBurstTest {

    @Test
    public void testBurstStateWithNullSprites() {
        StubBodyAttributes body = new StubBodyAttributes();
        body.setBodySpr(null);
        assertEquals(Burst.NONE, body.getBurstState());
    }

    @Test
    public void testBurstStateWithZeroOriginSize() {
        StubBodyAttributes body = new StubBodyAttributes();
        body.setBodySpr(new Sprite[3]);
        for (int i = 0; i < body.getBodySpr().length; i++) {
            body.getBodySpr()[i] = new Sprite();
            body.getBodySpr()[i].setImageW(0);
            body.getBodySpr()[i].setImageH(0);
        }
        assertEquals(Burst.NONE, body.getBurstState());
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ExpandWidthAtFiveQuarterThresholdIsSafe() {
            StubBodyAttributes body = createBodyWithOriginWidth(100);
            body.setExpandSizeW(25);

            assertEquals(125, body.getSize());
            assertEquals(Burst.SAFE, body.getBurstState());
        }

        @Test
        void testScenario_ExpandWidthAtSixQuarterThresholdIsHalf() {
            StubBodyAttributes body = createBodyWithOriginWidth(100);
            body.setExpandSizeW(50);

            assertEquals(150, body.getSize());
            assertEquals(Burst.HALF, body.getBurstState());
        }

        @Test
        void testScenario_ExpandWidthAtSevenQuarterThresholdIsNear() {
            StubBodyAttributes body = createBodyWithOriginWidth(100);
            body.setExpandSizeW(75);

            assertEquals(175, body.getSize());
            assertEquals(Burst.NEAR, body.getBurstState());
        }

        @Test
        void testScenario_ExpandWidthAtDoubleWidthThresholdIsBurst() {
            StubBodyAttributes body = createBodyWithOriginWidth(100);
            body.setExpandSizeW(100);

            assertEquals(200, body.getSize());
            assertEquals(Burst.BURST, body.getBurstState());
        }
    }

    private static StubBodyAttributes createBodyWithOriginWidth(int width) {
        StubBodyAttributes body = new StubBodyAttributes();
        body.setAgeState(AgeState.ADULT);
        body.setBodySpr(new Sprite[3]);
        body.getBodySpr()[AgeState.ADULT.ordinal()] = new Sprite(width, 50, Sprite.PIVOT_CENTER_CENTER);
        return body;
    }
}
