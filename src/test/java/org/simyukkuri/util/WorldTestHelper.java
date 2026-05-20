package org.simyukkuri.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JComboBox;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.LoggerYukkuri;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.ui.MainCommandUI;

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

            Field rndField = SimYukkuri.class.getDeclaredField("RND");
            rndField.setAccessible(true);
            rndField.set(null, new Random());

            initialized = false;

            org.simyukkuri.enums.Numbering.INSTANCE.setYukkuriID(0);
            org.simyukkuri.enums.Numbering.INSTANCE.setObjId(0);
            resetTerrariumState();
            resetMainCommandUIState();
            resetLoggerYukkuriState();
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
            List<WorldState> mapList = (List<WorldState>) mapListField.get(world);
            if (mapList.isEmpty()) {
                WorldState map = new WorldState(0);
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
     * Set up a Yukkuri to be transformation-ready
     * This sets the necessary state for canTransform() to return true
     */
    public static void makeTransformationReady(Yukkuri body) {
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

    public static void setDamage(Yukkuri body, int damage) {
        body.setDamage(damage);
    }

    public static Yukkuri createBody() {
        Yukkuri b = new Marisa();
        b.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        b.setUniqueID(org.simyukkuri.enums.Numbering.INSTANCE.numberingYukkuriID());
        return b;
    }

    public static void setParents(Yukkuri body, int fatherId, int motherId) {
        body.setParents(new int[] { fatherId, motherId });
    }

    public static void addChild(Yukkuri body, int childId) {
        body.getChildren().add(childId);
    }

    public static void setSleeping(Yukkuri body, boolean sleeping) {
        body.setSleeping(sleeping);
    }

    /**
     * Reset the World for a fresh test
     */
    public static void resetWorld() {
        resetStates();
    }

    /**
     * Reset Terrarium static environment flags used by metabolism/event tests.
     */
    public static void resetTerrariumState() {
        try {
            setStaticField(Terrarium.class, "operationTime", 0);
            setStaticField(Terrarium.class, "intervalCount", 0);
            setStaticField(Terrarium.class, "humid", false);
            setStaticField(Terrarium.class, "antifungalSteam", false);
            setStaticField(Terrarium.class, "orangeSteam", false);
            setStaticField(Terrarium.class, "ageBoostSteam", false);
            setStaticField(Terrarium.class, "ageStopSteam", false);
            setStaticField(Terrarium.class, "antidosSteam", false);
            setStaticField(Terrarium.class, "poisonSteam", false);
            setStaticField(Terrarium.class, "predatorSteam", false);
            setStaticField(Terrarium.class, "sugerSteam", false);
            setStaticField(Terrarium.class, "noSleepSteam", false);
            setStaticField(Terrarium.class, "hybridSteam", false);
            setStaticField(Terrarium.class, "rapidPregnantSteam", false);
            setStaticField(Terrarium.class, "antiNonYukkuriDiseaseSteam", false);
            setStaticField(Terrarium.class, "endlessFurifuriSteam", false);
            clearStaticCollection(Terrarium.class, "babyList");
        } catch (Exception e) {
            System.err.println("Failed to reset Terrarium state: " + e.getMessage());
        }
    }

    /**
     * Initialize Translate to a common non-perspective test configuration.
     */
    public static void initializeTranslate(int mapX, int mapY, int mapZ, int canvasW, int canvasH,
            int fieldSize, int bufferSize, float[] zoomRates) {
        Translate.setWorldSize(mapX, mapY, mapZ);
        Translate.setCanvasSize(canvasW, canvasH, fieldSize, bufferSize, zoomRates);
        Translate.createTransTable(false);
    }

    public static void initializeStandardTranslate200() {
        initializeTranslate(1000, 1000, 200, 800, 600, 100, 100, new float[] { 1.0f });
    }

    public static void initializeStandardTranslate500() {
        initializeTranslate(1000, 1000, 500, 800, 600, 100, 100, new float[] { 1.0f });
    }

    /**
     * Reset MainCommandUI static state to a minimal headless-safe baseline.
     */
    public static void resetMainCommandUIState() {
        MainCommandUI.setSelectedGameSpeed(1);
        MainCommandUI.setSelectedZoomScale(0);
        MainCommandUI.setGameSpeedCombo(null);
        MainCommandUI.setMainItemCombo(null);
        MainCommandUI.setSubItemCombo(null);
        MainCommandUI.setYuStatusLabel(new javax.swing.JLabel[12]);
        MainCommandUI.setStatIconLabel(new javax.swing.JLabel[8]);
        MainCommandUI.setItemIconLabel(new javax.swing.JLabel[1]);
        MainCommandUI.setSystemButton(new javax.swing.JButton[7]);
        MainCommandUI.setScriptButton(null);
        MainCommandUI.setTargetButton(null);
        MainCommandUI.setPinButton(null);
        MainCommandUI.setHelpButton(null);
        MainCommandUI.setOptionButton(null);
        MainCommandUI.setPlayerButton(new javax.swing.JToggleButton[2]);
        MainCommandUI.setOptionPopup(new javax.swing.JPopupMenu());
        MainCommandUI.setWorldWindow(null);
        MainCommandUI.setItemWindow(null);
    }

    /**
     * Initialize the minimal MainCommandUI state needed by headless listener tests.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void initializeMainCommandUITestState() {
        resetMainCommandUIState();
        MainCommandUI.setGameSpeedCombo(new JComboBox(new String[] { "0", "1", "2" }));
        MainCommandUI.setMainItemCombo(new JComboBox(org.simyukkuri.command.GadgetMenu.getMainCategory()));
        MainCommandUI.setSubItemCombo(new JComboBox());
    }

    /**
     * Initialize MessagePool with empty maps for tests that only need lookup
     * safety.
     */
    public static void initializeEmptyMessagePool() {
        try {
            Field field = MessagePool.class.getDeclaredField("pool_j");
            field.setAccessible(true);
            int len = YukkuriType.values().length;
            @SuppressWarnings("unchecked")
            HashMap<String, ?>[] pool = new HashMap[len];
            for (int i = 0; i < len; i++) {
                pool[i] = new HashMap<>();
            }
            field.set(null, pool);
        } catch (Exception e) {
            System.err.println("Failed to initialize MessagePool: " + e.getMessage());
        }
    }

    /**
     * Load the real message pool for tests that exercise production messaging.
     */
    public static void initializeLoadedMessagePool(ClassLoader loader) {
        try {
            MessagePool.loadMessage(loader);
        } catch (Exception e) {
            System.err.println("Failed to load MessagePool: " + e.getMessage());
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

    /**
     * Reset LoggerYukkuri static state so tests do not leak log pages or samples.
     */
    public static void resetLoggerYukkuriState() {
        try {
            setStaticField(LoggerYukkuri.class, "show", false);
            setStaticField(LoggerYukkuri.class, "clearLogTime", 0);
            setStaticField(LoggerYukkuri.class, "logPointer", 0);
            setStaticField(LoggerYukkuri.class, "overwrapped", false);
            setStaticField(LoggerYukkuri.class, "logPage", 0);
            setStaticField(LoggerYukkuri.class, "prevLogData", new long[LoggerYukkuri.NUM_OF_LOGDATA_TYPE]);
            setStaticField(LoggerYukkuri.class, "logDataSum", new long[LoggerYukkuri.NUM_OF_LOGDATA_TYPE]);
            setStaticField(LoggerYukkuri.class, "logList", new long[120][LoggerYukkuri.NUM_OF_LOGDATA_TYPE]);
            setStaticField(LoggerYukkuri.class, "backColor", null);
            setStaticField(LoggerYukkuri.class, "textColor1", null);
            setStaticField(LoggerYukkuri.class, "textFontTitle", null);
            setStaticField(LoggerYukkuri.class, "textFonttext", null);
        } catch (Exception e) {
            System.err.println("Failed to reset LoggerYukkuri state: " + e.getMessage());
        }
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void clearStaticCollection(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(null);
        if (value instanceof List<?>) {
            ((List<?>) value).clear();
        }
    }
}
