package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImageCodePrinterTest {

    @Test
    public void testMainDoesNotThrow() {
        assertDoesNotThrow(() -> ImageCodePrinter.main(new String[0]));
    }
}
