package src.yukkuri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import src.base.Body;

public class SerializationTest {

    public static void main(String[] args) {
        try {
            System.out.println("Starting Serialization Test...");

            // 1. Create a Yukkuri (Reimu)
            Reimu reimu = new Reimu();

            // 2. Tune parameters (this randomizes DAMAGELIMITorg)
            reimu.tuneParameters();

            // Capture the tuned limit and age state
            int ageStateIndex = reimu.getBodyAgeState().ordinal();
            int tunedLimit = reimu.getDAMAGELIMITorg()[ageStateIndex];

            System.out.println("Original Tuned DAMAGELIMIT: " + tunedLimit);

            // 3. Set damage to a value that is SAFE for the tuned limit,
            // but potentially FATAL if the limit reverts to default (assuming default is
            // lower).
            // For checking purposes, we just set a specific high damage value.
            int testDamage = tunedLimit - 100;
            if (testDamage < 0)
                testDamage = 0;
            reimu.setDamage(testDamage);

            System.out.println("Set Damage: " + testDamage);
            System.out.println("Is Dead before save? " + reimu.isDead());

            // 4. Serialize to file
            File tempFile = new File("test_save.dat");
            saveYukkuri(reimu, tempFile);

            // 5. Deserialize
            Reimu loadedReimu = loadYukkuri(tempFile);

            // 6. Verify
            int loadedLimit = loadedReimu.getDAMAGELIMITorg()[loadedReimu.getBodyAgeState().ordinal()];
            int loadedDamage = loadedReimu.getDamage();

            System.out.println("Loaded DAMAGELIMIT: " + loadedLimit);
            System.out.println("Loaded Damage: " + loadedDamage);
            System.out.println("Is Dead after load? " + loadedReimu.isDead());

            if (loadedLimit != tunedLimit) {
                System.err.println("FAILURE: DAMAGELIMITorg was NOT restored correctly!");
                System.err.println("Expected: " + tunedLimit + ", Got: " + loadedLimit);
            } else {
                System.out.println("SUCCESS: DAMAGELIMITorg restored correctly.");
            }

            if (loadedDamage != testDamage) {
                System.err.println("FAILURE: Damage was NOT restored correctly!");
                System.err.println("Expected: " + testDamage + ", Got: " + loadedDamage);
            } else {
                System.out.println("SUCCESS: Damage restored correctly.");
            }

            // Check if limits match implies fix success
            if (loadedLimit == tunedLimit && loadedDamage == testDamage && !loadedReimu.isDead()) {
                System.out.println("TEST PASSED: Yukkuri survived load.");
            } else {
                System.out.println("TEST FAILED.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveYukkuri(Body body, File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Configure mapper if needed (e.g., allow private field access if we weren't
        // using annotations)
        // mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        String json = mapper.writeValueAsString(body);

        try (FileOutputStream fos = new FileOutputStream(file);
                GZIPOutputStream gos = new GZIPOutputStream(fos)) {
            gos.write(json.getBytes("UTF-8"));
        }
    }

    private static Reimu loadYukkuri(File file) throws IOException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        java.util.Scanner s = null;

        try (FileInputStream fis = new FileInputStream(file);
                GZIPInputStream gis = new GZIPInputStream(fis)) {
            s = new java.util.Scanner(gis).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            return mapper.readValue(json, Reimu.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
