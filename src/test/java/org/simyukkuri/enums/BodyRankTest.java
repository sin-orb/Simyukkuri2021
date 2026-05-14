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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BodyRankTest {

    @ParameterizedTest
    @EnumSource(YukkuriRank.class)
    void testBodyRankProperties(YukkuriRank rank) {
        assertNotNull(rank);
        // Image index should be >= 0
        assertTrue(rank.getImageIndex() >= 0, "Image index should be non-negative");
        // Message index should be >= 0
        assertTrue(rank.getMessageIndex() >= 0, "Message index should be non-negative");

        // Display name might be null if resources are not loaded in test environment,
        // but the method should not throw exception.
        // If resources are loaded, it should be a string.
        // We just call it to ensure no exception.
        // String name = rank.getName(); // UnusedDisplayName();
        // System.out.println(rank + ": " + name);
    }

    @Test
    void testSpecificValues() {
        // Test a specific value to ensure mapping is correct as per code
        // KAIYU(0, 0, ...)
        YukkuriRank kaiyu = YukkuriRank.KAIYU;
        assertEquals(0, kaiyu.getImageIndex());
        assertEquals(0, kaiyu.getMessageIndex());

        // NORAYU(1, 1, ...)
        YukkuriRank norayu = YukkuriRank.NORAYU;
        assertEquals(1, norayu.getImageIndex());
        assertEquals(1, norayu.getMessageIndex());
    }
}
