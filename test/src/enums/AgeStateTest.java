package src.enums;



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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AgeStateTest {

    @Test
    public void testEnumValues() {
        assertEquals(0, AgeState.BABY.ordinal());
        assertEquals(1, AgeState.CHILD.ordinal());
        assertEquals(2, AgeState.ADULT.ordinal());

        assertEquals(3, AgeState.values().length);
    }

    // If AgeState had methods, test them here
}
