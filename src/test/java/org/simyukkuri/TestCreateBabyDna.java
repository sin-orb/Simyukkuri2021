package org.simyukkuri;

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

import org.simyukkuri.engine.birth.BabyDnaFactory;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

public class TestCreateBabyDna {
    public static void main(String[] args) {
        SimYukkuri.RND = new SequenceRNG(5, 10, 15, 20, 25);

        Reimu mother = new Reimu();
        Marisa father = new Marisa();

        try {
            System.out.println("Calling createBabyDna...");
            Object dna = BabyDnaFactory.createBabyDna(
                    mother, father, father.getType(),
                    org.simyukkuri.enums.Attitude.AVERAGE,
                    org.simyukkuri.enums.Intelligence.AVERAGE,
                    false, false, false);

            System.out.println("Success! DNA created: " + dna);
        } catch (Exception e) {
            System.out.println("Exception thrown: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
