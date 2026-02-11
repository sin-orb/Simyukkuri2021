
package src.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AgeStateTest {

    @Test
    public void testEnumValues() {
        assertEquals(0, AgeState.BABY.ordinal());
        assertEquals(1, AgeState.CHILD.ordinal());
        assertEquals(2, AgeState.ADULT.ordinal());

        assertEquals(3, AgeState.values().length);
    }

    // If AgeState had methods, test them here
}
