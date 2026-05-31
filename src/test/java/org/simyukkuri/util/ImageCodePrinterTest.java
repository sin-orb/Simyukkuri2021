package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import org.simyukkuri.enums.ImageCode;

public class ImageCodePrinterTest {

    @Test
    public void testMainDoesNotThrow() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            ImageCodePrinter.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String output = baos.toString();
        // ImageCode の全 enum 値が出力されていること
        String[] lines = output.trim().split(System.lineSeparator().isEmpty()
                ? "\n" : System.lineSeparator());
        assertEquals(ImageCode.values().length, lines.length,
                "出力行数が ImageCode.values().length と一致すること");
        // 先頭行が "0: <最初のenum名>" であること
        assertTrue(lines[0].startsWith("0: " + ImageCode.values()[0].name()),
                "先頭行が '0: " + ImageCode.values()[0].name() + "' で始まること");
    }
}
