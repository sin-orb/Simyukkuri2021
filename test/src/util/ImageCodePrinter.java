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

import src.enums.ImageCode;

public class ImageCodePrinter {
    public static void main(String[] args) {
        for (ImageCode code : ImageCode.values()) {
            System.out.println(code.ordinal() + ": " + code.name());
        }
    }
}
