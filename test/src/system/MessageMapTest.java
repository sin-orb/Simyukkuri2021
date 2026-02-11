package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * Test class for MessageMap.
 * MessageMap is a pure data structure with no dependencies - 100% testable.
 */
public class MessageMapTest {

    @Test
    public void testConstructor() {
        MessageMap messageMap = new MessageMap();

        assertNotNull(messageMap);
        assertFalse(messageMap.isNormalFlag());
        assertFalse(messageMap.isRudeFlag());
        assertNotNull(messageMap.getNormalTag());
        assertNotNull(messageMap.getRudeTag());
        assertNotNull(messageMap.getMap());
    }

    @Test
    public void testNormalFlagGetterSetter() {
        MessageMap messageMap = new MessageMap();

        messageMap.setNormalFlag(true);
        assertTrue(messageMap.isNormalFlag());

        messageMap.setNormalFlag(false);
        assertFalse(messageMap.isNormalFlag());
    }

    @Test
    public void testRudeFlagGetterSetter() {
        MessageMap messageMap = new MessageMap();

        messageMap.setRudeFlag(true);
        assertTrue(messageMap.isRudeFlag());

        messageMap.setRudeFlag(false);
        assertFalse(messageMap.isRudeFlag());
    }

    @Test
    public void testNormalTagGetterSetter() {
        MessageMap messageMap = new MessageMap();

        boolean[] newTags = new boolean[MessageMap.Tag.values().length];
        newTags[0] = true;
        newTags[1] = false;

        messageMap.setNormalTag(newTags);
        assertArrayEquals(newTags, messageMap.getNormalTag());
    }

    @Test
    public void testRudeTagGetterSetter() {
        MessageMap messageMap = new MessageMap();

        boolean[] newTags = new boolean[MessageMap.Tag.values().length];
        newTags[0] = false;
        newTags[1] = true;

        messageMap.setRudeTag(newTags);
        assertArrayEquals(newTags, messageMap.getRudeTag());
    }

    @Test
    public void testMapGetterSetter() {
        MessageMap messageMap = new MessageMap();

        Map<String, String[]> newMap = new java.util.HashMap<>();
        newMap.put("test", new String[] { "value1", "value2" });

        messageMap.setMap(newMap);
        assertEquals(newMap, messageMap.getMap());
    }

    @Test
    public void testTagEnum() {
        // Verify all expected tag values exist
        MessageMap.Tag[] tags = MessageMap.Tag.values();

        assertTrue(tags.length > 0);
        assertNotNull(MessageMap.Tag.valueOf("normal"));
        assertNotNull(MessageMap.Tag.valueOf("rude"));
        assertNotNull(MessageMap.Tag.valueOf("baby"));
        assertNotNull(MessageMap.Tag.valueOf("child"));
        assertNotNull(MessageMap.Tag.valueOf("adult"));
    }

    @Test
    public void testInitialState() {
        MessageMap messageMap = new MessageMap();

        // Verify initial state
        assertFalse(messageMap.isNormalFlag(), "Normal flag should be false initially");
        assertFalse(messageMap.isRudeFlag(), "Rude flag should be false initially");
        assertTrue(messageMap.getMap().isEmpty(), "Map should be empty initially");
    }

    @Test
    public void testMapOperations() {
        MessageMap messageMap = new MessageMap();

        // Add entries to map
        messageMap.getMap().put("action1", new String[] { "message1", "message2" });
        messageMap.getMap().put("action2", new String[] { "message3" });

        assertEquals(2, messageMap.getMap().size());
        assertArrayEquals(new String[] { "message1", "message2" }, messageMap.getMap().get("action1"));
        assertArrayEquals(new String[] { "message3" }, messageMap.getMap().get("action2"));
    }

    @Test
    public void testTagArrayLength() {
        MessageMap messageMap = new MessageMap();

        // Tag arrays should match enum length
        int expectedLength = MessageMap.Tag.values().length;
        assertEquals(expectedLength, messageMap.getNormalTag().length);
        assertEquals(expectedLength, messageMap.getRudeTag().length);
    }
}
