package src;

import src.util.YukkuriUtil;
import src.yukkuri.Reimu;
import src.yukkuri.Marisa;

public class TestCreateBabyDna {
    public static void main(String[] args) {
        SimYukkuri.RND = new SequenceRNG(5, 10, 15, 20, 25);

        Reimu mother = new Reimu();
        Marisa father = new Marisa();

        try {
            System.out.println("Calling createBabyDna...");
            Object dna = YukkuriUtil.createBabyDna(
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
