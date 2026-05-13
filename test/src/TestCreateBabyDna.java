package src;

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

import src.engine.birth.BabyDnaFactory;
import src.entity.core.living.yukkuri.impl.Marisa;
import src.entity.core.living.yukkuri.impl.Reimu;

public class TestCreateBabyDna {
    public static void main(String[] args) {
        SimYukkuri.RND = new SequenceRNG(5, 10, 15, 20, 25);

        Reimu mother = new Reimu();
        Marisa father = new Marisa();

        try {
            System.out.println("Calling createBabyDna...");
            Object dna = BabyDnaFactory.createBabyDna(
                    mother, father, father.getType(),
                    src.enums.Attitude.AVERAGE,
                    src.enums.Intelligence.AVERAGE,
                    false, false, false);

            System.out.println("Success! DNA created: " + dna);
        } catch (Exception e) {
            System.out.println("Exception thrown: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
