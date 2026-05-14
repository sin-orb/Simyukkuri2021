package org.simyukkuri.enums;



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
