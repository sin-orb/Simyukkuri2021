package src.util;

import src.SimYukkuri;
import src.draw.World;
import src.draw.MyPane;
import src.base.Body;
import java.util.Random;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Test helper to initialize minimal World infrastructure for testing
 * World-dependent methods like killTime(), coordinate(), execTransform(), etc.
 */
public class WorldTestHelper {

    private static boolean initialized = false;
    private static boolean mypaneInitialized = false;

    /**
     * Initialize minimal SimYukkuri.world for testing
     * This sets up just enough infrastructure to allow World-dependent methods to
     * run
     */
    public static void initializeMinimalWorld() {
        if (initialized) {
            return;
        }

        try {
            // Initialize World with minimal setup (windowType=0, scaleIndex=0)
            World world = new World(0, 0);

            // Use reflection to set the static world field
            Field worldField = SimYukkuri.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(null, world);

            // Ensure RND is initialized (should already be, but make sure)
            if (SimYukkuri.RND == null) {
                Field rndField = SimYukkuri.class.getDeclaredField("RND");
                rndField.setAccessible(true);
                rndField.set(null, new Random());
            }

            initialized = true;
        } catch (Exception e) {
            System.err.println("Failed to initialize minimal world: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize MyPane for testing methods that require image loading
     * infrastructure
     * This is needed for execTransform() and similar methods
     */
    public static void initializeMyPane() {
        if (mypaneInitialized) {
            return;
        }

        try {
            // Create a minimal MyPane instance (may fail if GUI not available)
            MyPane pane = new MyPane();

            // Use reflection to set the static mypane field
            Field mypaneField = SimYukkuri.class.getDeclaredField("mypane");
            mypaneField.setAccessible(true);
            mypaneField.set(null, pane);

            mypaneInitialized = true;
        } catch (Exception e) {
            // MyPane initialization may fail in headless environment
            System.err.println("Failed to initialize MyPane (expected in headless mode): " + e.getMessage());
        }
    }

    /**
     * Set up a Body to be transformation-ready
     * This sets the necessary state for canTransform() to return true
     */
    public static void makeTransformationReady(Body body) {
        try {
            // Use reflection to set private fields that affect canTransform()
            // canTransform() checks: !isDead, stress==0, !damaged, !feelPain, !unBirth,
            // etc.

            // Set unBirth to false (実ゆではない)
            setPrivateField(body, "unBirth", false);

            // Set stress to 0
            setPrivateField(body, "stress", 0);

            // Set damaged to false
            setPrivateField(body, "damaged", false);

            // Set feelPain to false
            setPrivateField(body, "feelPain", false);

            // Set dead to false
            setPrivateField(body, "dead", false);

        } catch (Exception e) {
            System.err.println("Failed to make body transformation-ready: " + e.getMessage());
        }
    }

    /**
     * Helper method to set private fields using reflection
     */
    private static void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = findField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    /**
     * Find a field in class hierarchy
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Reset the World for a fresh test
     */
    public static void resetWorld() {
        initialized = false;
        mypaneInitialized = false;
        try {
            Field worldField = SimYukkuri.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(null, null);

            Field mypaneField = SimYukkuri.class.getDeclaredField("mypane");
            mypaneField.setAccessible(true);
            mypaneField.set(null, null);
        } catch (Exception e) {
            System.err.println("Failed to reset world: " + e.getMessage());
        }
    }

    /**
     * Set a deterministic RNG seed for testing
     */
    public static void setDeterministicRNG(long seed) {
        try {
            Field rndField = SimYukkuri.class.getDeclaredField("RND");
            rndField.setAccessible(true);
            rndField.set(null, new Random(seed));
        } catch (Exception e) {
            System.err.println("Failed to set deterministic RNG: " + e.getMessage());
        }
    }
}
