package org.simyukkuri.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BodyRankTest {

    static Stream<Arguments> rankMappings() {
        return Stream.of(
            Arguments.of(YukkuriRank.KAIYU,        0, 0),
            Arguments.of(YukkuriRank.SUTEYU,       0, 0),
            Arguments.of(YukkuriRank.NORAYU_CLEAN, 0, 1),
            Arguments.of(YukkuriRank.NORAYU,       1, 1),
            Arguments.of(YukkuriRank.YASEIYU,      0, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("rankMappings")
    void testBodyRankProperties(YukkuriRank rank, int expectedImage, int expectedMessage) {
        assertEquals(expectedImage,   rank.getImageIndex(),   rank + " imageIndex mismatch");
        assertEquals(expectedMessage, rank.getMessageIndex(), rank + " messageIndex mismatch");
    }

    @ParameterizedTest
    @MethodSource("rankMappings")
    void testSpecificValues(YukkuriRank rank, int expectedImage, int expectedMessage) {
        assertEquals(expectedImage,   rank.getImageIndex());
        assertEquals(expectedMessage, rank.getMessageIndex());
    }
}
