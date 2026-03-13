package src.util;

import src.SimYukkuri;
import src.draw.World;
import src.base.Body;
import src.base.BodyAttributes;
import java.util.Random;
import java.lang.reflect.Field;
import src.system.MapPlaceData;
import src.draw.Translate;
import java.util.List;

/**
 * Test helper to initialize minimal World infrastructure for testing
 * World-dependent methods like killTime(), coordinate(), execTransform(), etc.
 */
public class WorldTestHelper {

    private static boolean initialized = false;

    /**
     * Reset static states in SimYukkuri to ensure test isolation
     */
    public static void resetStates() {
        System.setProperty("java.awt.headless", "true");
        try {
            Field mypaneField = SimYukkuri.class.getDeclaredField("mypane");
            mypaneField.setAccessible(true);
            mypaneField.set(null, null);

            Field worldField = SimYukkuri.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(null, null);

            initialized = false;

            src.enums.Numbering.INSTANCE.setYukkuriID(0);
            src.enums.Numbering.INSTANCE.setObjId(0);
        } catch (Exception e) {
            System.err.println("Failed to reset states: " + e.getMessage());
        }
    }

    /**
     * Initialize minimal SimYukkuri.world for testing
     * This sets up just enough infrastructure to allow World-dependent methods to
     * run
     */
    public static void initializeMinimalWorld() {
        if (initialized && SimYukkuri.world != null) {
            return;
        }

        try {
            // Initialize Translate with reasonable values
            Translate.setCanvasSize(900, 700, 100, 100, new float[] { 1.0f });

            // Initialize World with minimal setup (windowType=0, scaleIndex=0)
            World world = new World(0, 0);

            // Use reflection to set the static world field
            Field worldField = SimYukkuri.class.getDeclaredField("world");
            worldField.setAccessible(true);
            worldField.set(null, world);

            // Add at least one map if empty
            Field mapListField = World.class.getDeclaredField("mapList");
            mapListField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<MapPlaceData> mapList = (List<MapPlaceData>) mapListField.get(world);
            if (mapList.isEmpty()) {
                MapPlaceData map = new MapPlaceData(0);
                mapList.add(map);
            }

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

    public static void setDamage(Body body, int damage) {
        try {
            Field field = BodyAttributes.class.getDeclaredField("damage");
            field.setAccessible(true);
            field.setInt(body, damage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Body createBody() {
        Body b = new src.yukkuri.Marisa();
        b.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
        b.setUniqueID(src.enums.Numbering.INSTANCE.numberingYukkuriID());
        return b;
    }

    public static void setParents(Body body, int fatherId, int motherId) {
        try {
            Field field = BodyAttributes.class.getDeclaredField("parents");
            field.setAccessible(true);
            int[] parents = { fatherId, motherId };
            field.set(body, parents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addChild(Body body, int childId) {
        try {
            Field field = BodyAttributes.class.getDeclaredField("childrenList");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Integer> children = (List<Integer>) field.get(body);
            children.add(childId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSleeping(Body body, boolean sleeping) {
        try {
            Field field = BodyAttributes.class.getDeclaredField("sleeping");
            field.setAccessible(true);
            field.setBoolean(body, sleeping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the World for a fresh test
     */
    public static void resetWorld() {
        resetStates();
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
