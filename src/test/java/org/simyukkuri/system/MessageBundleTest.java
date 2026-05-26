package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Test class for MessageBundle.
 */
public class MessageBundleTest {

    @Test
    public void testConstructorInitializesEmptyBundle() {
        MessageBundle messageBundle = new MessageBundle();

        assertNotNull(messageBundle);
        assertFalse(messageBundle.isNormalFlag());
        assertFalse(messageBundle.isRudeFlag());
        assertNotNull(messageBundle.getNormalTag());
        assertNotNull(messageBundle.getRudeTag());
        assertNotNull(messageBundle.getMessages());
        assertEquals(MessageBundle.MessageTag.values().length, messageBundle.getNormalTag().length);
        assertEquals(MessageBundle.MessageTag.values().length, messageBundle.getRudeTag().length);
        assertTrue(messageBundle.getMessages().isEmpty());
        for (boolean enabled : messageBundle.getNormalTag()) {
            assertFalse(enabled);
        }
        for (boolean enabled : messageBundle.getRudeTag()) {
            assertFalse(enabled);
        }
    }

    @Test
    public void testSettersRoundTripStoreAssignedValues() {
        final MessageBundle messageBundle = new MessageBundle();

        boolean[] normalTag = new boolean[MessageBundle.MessageTag.values().length];
        normalTag[0] = true;
        normalTag[3] = true;
        boolean[] rudeTag = new boolean[MessageBundle.MessageTag.values().length];
        rudeTag[1] = true;
        rudeTag[4] = true;

        Map<String, String[]> messages = new HashMap<>();
        messages.put("normal", new String[] { "first", "second" });
        messages.put("rude", new String[] { "third" });

        messageBundle.setNormalFlag(true);
        messageBundle.setRudeFlag(true);
        messageBundle.setNormalTag(normalTag);
        messageBundle.setRudeTag(rudeTag);
        messageBundle.setMessages(messages);

        assertTrue(messageBundle.isNormalFlag());
        assertTrue(messageBundle.isRudeFlag());
        assertArrayEquals(normalTag, messageBundle.getNormalTag());
        assertArrayEquals(rudeTag, messageBundle.getRudeTag());
        assertEquals(messages, messageBundle.getMessages());
    }

    @Test
    public void testMessageTagEnumValuesStayOrdered() {
        assertArrayEquals(new MessageBundle.MessageTag[] {
                MessageBundle.MessageTag.normal,
                MessageBundle.MessageTag.rude,
                MessageBundle.MessageTag.baby,
                MessageBundle.MessageTag.child,
                MessageBundle.MessageTag.adult,
                MessageBundle.MessageTag.damage,
                MessageBundle.MessageTag.footbake,
                MessageBundle.MessageTag.pants,
                MessageBundle.MessageTag.loveplayer,
                MessageBundle.MessageTag.dislikeplayer,
                MessageBundle.MessageTag.ununSlave,
                MessageBundle.MessageTag.fool,
                MessageBundle.MessageTag.wise },
                MessageBundle.MessageTag.values());
    }
}
