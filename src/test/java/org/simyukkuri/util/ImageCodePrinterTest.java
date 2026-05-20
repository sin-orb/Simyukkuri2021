package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ImageCodePrinterTest {

    @Test
    public void testMainDoesNotThrow() {
        assertDoesNotThrow(() -> ImageCodePrinter.main(new String[0]));
    }
}
