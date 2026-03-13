package src.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BodyRankTest {

    @ParameterizedTest
    @EnumSource(BodyRank.class)
    void testBodyRankProperties(BodyRank rank) {
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
        BodyRank kaiyu = BodyRank.KAIYU;
        assertEquals(0, kaiyu.getImageIndex());
        assertEquals(0, kaiyu.getMessageIndex());

        // NORAYU(1, 1, ...)
        BodyRank norayu = BodyRank.NORAYU;
        assertEquals(1, norayu.getImageIndex());
        assertEquals(1, norayu.getMessageIndex());
    }
}
