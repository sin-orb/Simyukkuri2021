package src.yukkuri;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class BodySerializationTest {

    @Test
    public void testBodyAttributesSerialization() throws IOException {
        // 1. Instantiate a Reimu object (Body subclass)
        Reimu reimu = new Reimu();
        reimu.setX(0);
        reimu.setY(0);
        reimu.setZ(0);

        // 2. Tune parameters to randomize them (simulating game start)
        // This modifies damageLimitBase among others
        reimu.tuneParameters();

        // Capture the tuned values
        int[] originalDamageLimit = reimu.getDamageLimitBase();
        int[] originalHungryLimit = reimu.getHungryLimitBase();

        // Set a specific damage value
        int testDamage = 500;
        reimu.setDamage(testDamage);

        // 3. Serialize using Jackson
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(reimu);

        // 4. Deserialize
        Reimu loadedReimu = mapper.readValue(json, Reimu.class);

        // 5. Verify constraints
        assertNotNull(loadedReimu, "Deserialized object should not be null");

        // Verify damageLimitBase matches
        assertArrayEquals(originalDamageLimit, loadedReimu.getDamageLimitBase(),
                "damageLimitBase should be preserved exactly through serialization");

        // Verify hungryLimitBase matches (checking another one for safety)
        assertArrayEquals(originalHungryLimit, loadedReimu.getHungryLimitBase(),
                "hungryLimitBase should be preserved exactly through serialization");

        // Verify Damage matches
        assertEquals(testDamage, loadedReimu.getDamage(),
                "Damage value should be preserved");

        // Verify calculation logic (logic from previous bug)
        // If damage limit was lost (reverted to default), this might fail or cause
        // death logic to trigger if we simulated it.
        // Here we just ensure the data is correct.
    }
}
