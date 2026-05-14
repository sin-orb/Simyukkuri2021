package org.simyukkuri.util;

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

import org.simyukkuri.enums.YukkuriType;

public class YukkuriTypeMappingTest {

    @Test
    public void testTypeClassNameRoundTrip() {
        for (YukkuriType type : YukkuriType.values()) {
            assertEquals(type, YukkuriType.fromClassName(type.getClassName()));
            assertEquals(type, YukkuriType.fromTypeID(type.getTypeID()));
            assertEquals(type.getClassName(), YukkuriType.fromTypeID(type.getTypeID()).getClassName());
        }
    }
}
