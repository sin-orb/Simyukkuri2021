package src.util;

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

import src.enums.YukkuriType;

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
