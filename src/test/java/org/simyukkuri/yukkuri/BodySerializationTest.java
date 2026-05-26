package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

public class BodySerializationTest {

    @Test
    public void testBodyAttributesSerialization() throws IOException {
        // 1. Instantiate a Reimu object (Yukkuri subclass)
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
        reimu.setHungry(12345);

        // 3. Serialize using Jackson
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(reimu);
        assertTrue(json.contains("\"hungry\":12345"));

        // 4. Deserialize
        Reimu loadedReimu = mapper.readValue(json, Reimu.class);

        // 5. Verify constraints
        assertNotNull(loadedReimu, "Deserialized object should not be null");

        // Verify damageLimitBase matches
        assertArrayEquals(
                originalDamageLimit,
                loadedReimu.getDamageLimitBase(),
                "damageLimitBase should be preserved exactly through serialization");

        // Verify hungryLimitBase matches (checking another one for safety)
        assertArrayEquals(
                originalHungryLimit,
                loadedReimu.getHungryLimitBase(),
                "hungryLimitBase should be preserved exactly through serialization");

        // Verify Damage matches
        assertEquals(testDamage, loadedReimu.getDamage(), "Damage value should be preserved");

        assertEquals(12345, loadedReimu.getHungry(), "hungry value should be preserved");

        // Verify calculation logic (logic from previous bug)
        // If damage limit was lost (reverted to default), this might fail or cause
        // death logic to trigger if we simulated it.
        // Here we just ensure the data is correct.
    }
}
