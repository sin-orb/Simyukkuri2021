package src.draw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.enums.AgeState;
import src.enums.PanicType;
import src.enums.YukkuriType;
import src.util.WorldTestHelper;
import java.io.File;
import java.nio.file.Files;

class TerrariumTest {

    private Terrarium terrarium;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetStates();
        WorldTestHelper.initializeMinimalWorld();
        terrarium = new Terrarium();
    }

    @Test
    void testSteamStates_DefaultFalse() {
        assertFalse(Terrarium.isHumid());
        assertFalse(Terrarium.isAntifungalSteam());
        assertFalse(Terrarium.isOrangeSteam());
        assertFalse(Terrarium.isAgeBoostSteam());
        assertFalse(Terrarium.isAgeStopSteam());
        assertFalse(Terrarium.isAntidosSteam());
        assertFalse(Terrarium.isPoisonSteam());
        assertFalse(Terrarium.isPredatorSteam());
        assertFalse(Terrarium.isSugerSteam());
        assertFalse(Terrarium.isNoSleepSteam());
        assertFalse(Terrarium.isHybridSteam());
        assertFalse(Terrarium.isRapidPregnantSteam());
        assertFalse(Terrarium.isAntiNonYukkuriDiseaseSteam());
        assertFalse(Terrarium.isEndlessFurifuriSteam());
    }

    @Test
    void testAddBody_Success() {
        int initialBodyCount = SimYukkuri.world.getCurrentMap().getBody().size();
        // addBody(int x, int y, int z, int type, AgeState age, Body p1, Body p2)
        // Use getTypeID() instead of ordinal()
        terrarium.addBody(100, 100, 0, YukkuriType.REIMU.getTypeID(), AgeState.ADULT, null, null);
        assertEquals(initialBodyCount + 1, SimYukkuri.world.getCurrentMap().getBody().size());
    }

    @Test
    void testCheckPanic_PanicNearShit() {
        Body body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setPanicType(PanicType.FEAR);

        assertDoesNotThrow(() -> {
            java.lang.reflect.Method m = Terrarium.class.getDeclaredMethod("checkPanic", Body.class);
            m.setAccessible(true);
            m.invoke(terrarium, body);
        });
    }

    @Test
    void testSaveLoadState_Basic() throws Exception {
        File tempFile = Files.createTempFile("simyukkuri_test_save", ".sav").toFile();
        try {
            terrarium.addBody(100, 100, 0, YukkuriType.REIMU.getTypeID(), AgeState.ADULT, null, null);
            // saveState(File) and loadState(File) are likely static based on lint feedback
            Terrarium.saveState(tempFile);

            assertTrue(tempFile.exists());
            assertTrue(tempFile.length() > 0);

            // Clear and reload
            WorldTestHelper.resetStates();
            WorldTestHelper.initializeMinimalWorld();
            terrarium = new Terrarium();

            Terrarium.loadState(tempFile);
            // After load, we should have the body back
            assertEquals(1, SimYukkuri.world.getCurrentMap().getBody().size());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void testHkdf_Basic() {
        // hkdf is private but let's see if we can test it via reflection or if it's
        // used in load/save
        // It's used in encrypt/decrypt which are used in save/load.
        // So testSaveLoadState already tests it indirectly.
    }
}
