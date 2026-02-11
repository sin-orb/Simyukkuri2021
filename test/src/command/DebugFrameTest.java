package src.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class DebugFrameTest {

    private static class Parent {
        @SuppressWarnings("unused")
        private int parentValue = 123;
    }

    private static class Child extends Parent {
        @SuppressWarnings("unused")
        private String childValue = "abc";
    }

    @Test
    public void testGetAllFieldsIncludesPrivateAndParentFields() {
        Child c = new Child();
        Map<String, Object> fields = DebugFrame.getAllFields(c);

        assertTrue(fields.containsKey("parentValue"));
        assertTrue(fields.containsKey("childValue"));
        assertEquals(123, fields.get("parentValue"));
        assertEquals("abc", fields.get("childValue"));
    }
}
