package org.simyukkuri.util;

import org.simyukkuri.enums.ImageCode;

public class ImageCodePrinter {
    public static void main(String[] args) {
        for (ImageCode code : ImageCode.values()) {
            System.out.println(code.ordinal() + ": " + code.name());
        }
    }
}
