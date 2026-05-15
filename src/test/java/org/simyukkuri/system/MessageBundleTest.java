package org.simyukkuri.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

/**
 * Test class for MessageBundle.
 * MessageBundle is a pure data structure with no dependencies - 100% testable.
 */
public class MessageBundleTest {

    @Test
    public void testConstructor() {
        MessageBundle messageBundle = new MessageBundle();

        assertNotNull(messageBundle);
        assertFalse(messageBundle.isNormalFlag());
        assertFalse(messageBundle.isRudeFlag());
        assertNotNull(messageBundle.getNormalTag());
        assertNotNull(messageBundle.getRudeTag());
        assertNotNull(messageBundle.getMessages());
    }

    @Test
    public void testNormalFlagGetterSetter() {
        MessageBundle messageBundle = new MessageBundle();

        messageBundle.setNormalFlag(true);
        assertTrue(messageBundle.isNormalFlag());

        messageBundle.setNormalFlag(false);
        assertFalse(messageBundle.isNormalFlag());
    }

    @Test
    public void testRudeFlagGetterSetter() {
        MessageBundle messageBundle = new MessageBundle();

        messageBundle.setRudeFlag(true);
        assertTrue(messageBundle.isRudeFlag());

        messageBundle.setRudeFlag(false);
        assertFalse(messageBundle.isRudeFlag());
    }

    @Test
    public void testNormalTagGetterSetter() {
        MessageBundle messageBundle = new MessageBundle();

        boolean[] newTags = new boolean[MessageBundle.MessageTag.values().length];
        newTags[0] = true;
        newTags[1] = false;

        messageBundle.setNormalTag(newTags);
        assertArrayEquals(newTags, messageBundle.getNormalTag());
    }

    @Test
    public void testRudeTagGetterSetter() {
        MessageBundle messageBundle = new MessageBundle();

        boolean[] newTags = new boolean[MessageBundle.MessageTag.values().length];
        newTags[0] = false;
        newTags[1] = true;

        messageBundle.setRudeTag(newTags);
        assertArrayEquals(newTags, messageBundle.getRudeTag());
    }

    @Test
    public void testMapGetterSetter() {
        MessageBundle messageBundle = new MessageBundle();

        Map<String, String[]> newMap = new java.util.HashMap<>();
        newMap.put("test", new String[] { "value1", "value2" });

        messageBundle.setMessages(newMap);
        assertEquals(newMap, messageBundle.getMessages());
    }

    @Test
    public void testTagEnum() {
        MessageBundle.MessageTag[] tags = MessageBundle.MessageTag.values();

        assertTrue(tags.length > 0);
        assertNotNull(MessageBundle.MessageTag.valueOf("normal"));
        assertNotNull(MessageBundle.MessageTag.valueOf("rude"));
        assertNotNull(MessageBundle.MessageTag.valueOf("baby"));
        assertNotNull(MessageBundle.MessageTag.valueOf("child"));
        assertNotNull(MessageBundle.MessageTag.valueOf("adult"));
    }

    @Test
    public void testInitialState() {
        MessageBundle messageBundle = new MessageBundle();

        assertFalse(messageBundle.isNormalFlag(), "Normal flag should be false initially");
        assertFalse(messageBundle.isRudeFlag(), "Rude flag should be false initially");
        assertTrue(messageBundle.getMessages().isEmpty(), "Map should be empty initially");
    }

    @Test
    public void testMapOperations() {
        MessageBundle messageBundle = new MessageBundle();

        messageBundle.getMessages().put("action1", new String[] { "message1", "message2" });
        messageBundle.getMessages().put("action2", new String[] { "message3" });

        assertEquals(2, messageBundle.getMessages().size());
        assertArrayEquals(new String[] { "message1", "message2" }, messageBundle.getMessages().get("action1"));
        assertArrayEquals(new String[] { "message3" }, messageBundle.getMessages().get("action2"));
    }

    @Test
    public void testTagArrayLength() {
        MessageBundle messageBundle = new MessageBundle();

        int expectedLength = MessageBundle.MessageTag.values().length;
        assertEquals(expectedLength, messageBundle.getNormalTag().length);
        assertEquals(expectedLength, messageBundle.getRudeTag().length);
    }
}
