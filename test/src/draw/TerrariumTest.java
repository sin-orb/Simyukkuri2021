package src.draw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.BreedingAmpoule;
import src.attachment.Fire;
import src.attachment.PoisonAmpoule;
import src.base.Yukkuri;
import src.effect.Effect;
import src.event.EventPacket;
import src.base.Entity;
import src.entity.world.bodylinked.Okazari.OkazariType;
import src.enums.ActionState;
import src.enums.BurialState;
import src.enums.PanicType;
import src.enums.TakeoutItemType;
import src.enums.AgeState;
import src.enums.FavItemType;
import src.enums.YukkuriType;
import src.game.Dna;
import src.game.Stalk;
import src.event.BegForLifeEvent;
import src.event.FuneralEvent;
import src.event.PredatorsGameEvent;
import src.event.ProposeEvent;
import src.event.ProudChildEvent;
import src.event.RaperReactionEvent;
import src.event.ShitExercisesEvent;
import src.event.SuperEatingTimeEvent;
import src.item.AutoFeeder;
import src.item.Bed;
import src.field.impl.Beltconveyor;
import src.field.impl.Farm;
import src.item.Diffuser;
import src.item.Food;
import src.item.GarbageChute;
import src.item.GarbageStation;
import src.item.HotPlate;
import src.item.Mixer;
import src.item.ProcesserPlate;
import src.item.StickyPlate;
import src.item.Sui;
import src.item.Yunba;
import src.system.Sprite;
import src.util.WorldTestHelper;
import src.yukkuri.Deibu;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaReimu;
import src.yukkuri.Reimu;
import src.yukkuri.ReimuMarisa;
import src.yukkuri.DosMarisa;
import src.yukkuri.Remirya;
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

    private void roundTripSaveLoad(File tempFile) throws Exception {
        Terrarium.saveState(tempFile);
        WorldTestHelper.resetStates();
        WorldTestHelper.initializeMinimalWorld();
        terrarium = new Terrarium();
        Terrarium.loadState(tempFile);
    }

    private Yukkuri findBodyAcrossMaps(int uniqueId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            Yukkuri found = map.getBody().get(uniqueId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private Stalk findStalkAcrossMaps(java.util.UUID stalkId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            for (Stalk stalk : map.getStalk().values()) {
                if (stalkId.equals(stalk.getStalkId())) {
                    return stalk;
                }
            }
        }
        return null;
    }

    private Fire findFireAttachment(Yukkuri body) {
        for (src.attachment.Attachment attachment : body.getAttach()) {
            if (attachment instanceof Fire) {
                return (Fire) attachment;
            }
        }
        return null;
    }

    private <T> T findAttachment(Yukkuri body, Class<T> type) {
        for (src.attachment.Attachment attachment : body.getAttach()) {
            if (type.isInstance(attachment)) {
                return type.cast(attachment);
            }
        }
        return null;
    }

    private Beltconveyor findBeltAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            for (Beltconveyor belt : map.getBeltconveyor()) {
                if (belt.getObjId() == objId) {
                    return belt;
                }
            }
        }
        return null;
    }

    private Beltconveyor findBeltAcrossMaps(int mapSX, int mapSY, int mapEX, int mapEY) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            for (Beltconveyor belt : map.getBeltconveyor()) {
                if (belt.getMapSX() == mapSX && belt.getMapSY() == mapSY
                        && belt.getMapEX() == mapEX && belt.getMapEY() == mapEY) {
                    return belt;
                }
            }
        }
        return null;
    }

    private Farm findFarmAcrossMaps(int mapSX, int mapSY, int mapEX, int mapEY) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            for (Farm farm : map.getFarm()) {
                if (farm.getMapSX() == mapSX && farm.getMapSY() == mapSY
                        && farm.getMapEX() == mapEX && farm.getMapEY() == mapEY) {
                    return farm;
                }
            }
        }
        return null;
    }

    private Sui findSuiAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getSui().containsKey(objId)) {
                return map.getSui().get(objId);
            }
        }
        return null;
    }

    private Bed findBedAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getBed().containsKey(objId)) {
                return map.getBed().get(objId);
            }
        }
        return null;
    }

    private AutoFeeder findAutoFeederAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getAutofeeder().containsKey(objId)) {
                return map.getAutofeeder().get(objId);
            }
        }
        return null;
    }

    private HotPlate findHotPlateAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getHotPlate().containsKey(objId)) {
                return map.getHotPlate().get(objId);
            }
        }
        return null;
    }

    private StickyPlate findStickyPlateAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getStickyPlate().containsKey(objId)) {
                return map.getStickyPlate().get(objId);
            }
        }
        return null;
    }

    private Mixer findMixerAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getMixer().containsKey(objId)) {
                return map.getMixer().get(objId);
            }
        }
        return null;
    }

    private GarbageChute findGarbageChuteAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getGarbagechute().containsKey(objId)) {
                return map.getGarbagechute().get(objId);
            }
        }
        return null;
    }

    private GarbageStation findGarbageStationAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getGarbageStation().containsKey(objId)) {
                return map.getGarbageStation().get(objId);
            }
        }
        return null;
    }

    private Diffuser findDiffuserAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getDiffuser().containsKey(objId)) {
                return map.getDiffuser().get(objId);
            }
        }
        return null;
    }

    private Yunba findYunbaAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            if (map.getYunba().containsKey(objId)) {
                return map.getYunba().get(objId);
            }
        }
        return null;
    }

    private Effect findEffectAcrossMaps(int objId) {
        for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
            Effect effect = map.getSortEffect().get(objId);
            if (effect != null) {
                return effect;
            }
            effect = map.getFrontEffect().get(objId);
            if (effect != null) {
                return effect;
            }
        }
        return null;
    }

    private void installSyntheticBodySprites(Yukkuri body, int width, int height) {
        Sprite[] bodySpr = new Sprite[3];
        Sprite[] expandSpr = new Sprite[3];
        Sprite[] braidSpr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            bodySpr[i] = new Sprite(width, height, Sprite.PIVOT_CENTER_BOTTOM);
            expandSpr[i] = new Sprite(width, height, Sprite.PIVOT_CENTER_BOTTOM);
            braidSpr[i] = new Sprite(width, height, Sprite.PIVOT_CENTER_BOTTOM);
        }
        body.setBodySpr(bodySpr);
        body.setExpandSpr(expandSpr);
        body.setBraidSpr(braidSpr);
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
        // addBody(int x, int y, int z, int type, AgeState age, Yukkuri p1, Yukkuri p2)
        // Use getTypeID() instead of ordinal()
        terrarium.addBody(100, 100, 0, YukkuriType.REIMU.getTypeID(), AgeState.ADULT, null, null);
        assertEquals(initialBodyCount + 1, SimYukkuri.world.getCurrentMap().getBody().size());
    }

    @Test
    void testCheckPanic_PanicNearShit() {
        Yukkuri body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setPanicType(PanicType.FEAR);

        assertDoesNotThrow(() -> {
            java.lang.reflect.Method m = Terrarium.class.getDeclaredMethod("checkPanic", Yukkuri.class);
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

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_GetDayStateUsesExpectedBoundaryBuckets() throws Exception {
            java.lang.reflect.Field operationTimeField = Terrarium.class.getDeclaredField("operationTime");
            operationTimeField.setAccessible(true);

            operationTimeField.setInt(null, 0);
            assertEquals(Terrarium.DayState.MORNING, Terrarium.getDayState());

            operationTimeField.setInt(null, Terrarium.getNightTime() / 5);
            assertEquals(Terrarium.DayState.DAY, Terrarium.getDayState());

            operationTimeField.setInt(null, Terrarium.getDayTime() - Terrarium.getNightTime() / 5);
            assertEquals(Terrarium.DayState.EVENING, Terrarium.getDayState());

            operationTimeField.setInt(null, Terrarium.getDayTime());
            assertEquals(Terrarium.DayState.NIGHT, Terrarium.getDayState());
        }

        @Test
        void testScenario_ResetTerrariumEnvironmentClearsAllSteamFlags() throws Exception {
            for (String fieldName : new String[] {
                    "humid", "antifungalSteam", "orangeSteam", "ageBoostSteam", "ageStopSteam",
                    "antidosSteam", "poisonSteam", "predatorSteam", "sugerSteam", "noSleepSteam",
                    "hybridSteam", "rapidPregnantSteam", "antiNonYukkuriDiseaseSteam", "endlessFurifuriSteam" }) {
                java.lang.reflect.Field field = Terrarium.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setBoolean(null, true);
            }

            Terrarium.resetTerrariumEnvironment();

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
        void testScenario_CheckPanicBurnPropagatesFearOnlyToNearbyNonRaperBodies() throws Exception {
            Yukkuri source = WorldTestHelper.createBody();
            source.setX(100);
            source.setY(100);
            source.setPanic(true, PanicType.BURN);

            Yukkuri nearby = WorldTestHelper.createBody();
            nearby.setX(102);
            nearby.setY(102);

            Yukkuri nearbyRaper = WorldTestHelper.createBody();
            nearbyRaper.setX(103);
            nearbyRaper.setY(103);
            nearbyRaper.setRaper(true);

            Yukkuri far = WorldTestHelper.createBody();
            far.setX(1000);
            far.setY(1000);
            far.setEyesightBase(10);

            SimYukkuri.world.getCurrentMap().getBody().put(source.getUniqueID(), source);
            SimYukkuri.world.getCurrentMap().getBody().put(nearby.getUniqueID(), nearby);
            SimYukkuri.world.getCurrentMap().getBody().put(nearbyRaper.getUniqueID(), nearbyRaper);
            SimYukkuri.world.getCurrentMap().getBody().put(far.getUniqueID(), far);

            assertTrue(nearbyRaper.isRaper());
            assertNull(nearbyRaper.getPanicType());

            java.lang.reflect.Method m = Terrarium.class.getDeclaredMethod("checkPanic", Yukkuri.class);
            m.setAccessible(true);
            m.invoke(terrarium, source);

            assertEquals(PanicType.FEAR, nearby.getPanicType());
            assertNull(nearbyRaper.getPanicType());
            assertNull(far.getPanicType());
        }

        @Test
        void testScenario_SaveLoadRestoresPickedUpBodyInPlayerInventory() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_inventory_body", ".sav").toFile();
            try {
                Yukkuri pickedUp = WorldTestHelper.createBody();
                int pickedUpId = pickedUp.getUniqueID();
                SimYukkuri.world.getCurrentMap().getBody().put(pickedUpId, pickedUp);

                SimYukkuri.world.getPlayer().getItemList().addElement(pickedUp);
                pickedUp.setTaken(true);
                SimYukkuri.world.getCurrentMap().getBody().remove(pickedUpId);

                Terrarium.saveState(tempFile);

                WorldTestHelper.resetStates();
                WorldTestHelper.initializeMinimalWorld();
                terrarium = new Terrarium();

                Terrarium.loadState(tempFile);

                assertTrue(SimYukkuri.world.getCurrentMap().getBody().isEmpty(),
                        "picked-up body should stay out of the field body map after load");
                assertEquals(1, SimYukkuri.world.getPlayer().getItemList().getSize(),
                        "picked-up body should be restored into player inventory after load");

                Object restored = SimYukkuri.world.getPlayer().getItemList().getElementAt(0);
                assertTrue(restored instanceof Yukkuri, "player inventory should still contain a body after load");
                Yukkuri restoredBody = (Yukkuri) restored;
                assertEquals(pickedUpId, restoredBody.getUniqueID(),
                        "restored inventory body should preserve the original unique id");
                assertTrue(restoredBody.isTaken(), "restored inventory body should remain marked as taken");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadRestoresBodyTakeoutFoodReference() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_takeout_food", ".sav").toFile();
            try {
                Yukkuri carrier = WorldTestHelper.createBody();
                carrier.setX(100);
                carrier.setY(100);
                SimYukkuri.world.getCurrentMap().getBody().put(carrier.getUniqueID(), carrier);

                Food carriedFood = new Food(100, 100, Food.FoodType.FOOD.ordinal());
                int carriedFoodObjId = carriedFood.getObjId();

                carrier.setCarryItem(TakeoutItemType.FOOD, carriedFood);

                Terrarium.saveState(tempFile);

                WorldTestHelper.resetStates();
                WorldTestHelper.initializeMinimalWorld();
                terrarium = new Terrarium();

                Terrarium.loadState(tempFile);

                Yukkuri restoredCarrier = null;
                boolean carriedFoodFoundInTakenOutMap = false;
                boolean carriedFoodFoundInFieldFoodMap = false;
                for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
                    if (restoredCarrier == null && map.getBody().size() == 1) {
                        restoredCarrier = map.getBody().values().iterator().next();
                    }
                    carriedFoodFoundInTakenOutMap |= map.getTakenOutFood().containsKey(carriedFoodObjId);
                    carriedFoodFoundInFieldFoodMap |= map.getFood().containsKey(carriedFoodObjId);
                }

                assertNotNull(restoredCarrier, "carrier body should survive save/load on some map");
                assertNotNull(restoredCarrier.getCarryItem(TakeoutItemType.FOOD),
                        "carrier should still resolve its carried food after load");
                assertEquals(carriedFoodObjId, restoredCarrier.getCarryItem(TakeoutItemType.FOOD).getObjId(),
                        "carried food should preserve the original object id across save/load");
                assertTrue(carriedFoodFoundInTakenOutMap,
                        "taken-out food map should be restored for carried food after load");
                assertFalse(carriedFoodFoundInFieldFoodMap,
                        "carried food should not reappear in the field food map after load");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesFoodCarriedOnHeadState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_food_on_head", ".sav").toFile();
            try {
                Yukkuri carrier = WorldTestHelper.createBody();
                carrier.setX(100);
                carrier.setY(100);
                SimYukkuri.world.getCurrentMap().getBody().put(carrier.getUniqueID(), carrier);

                Food carriedFood = new Food(100, 100, Food.FoodType.FOOD.ordinal());
                int carrierId = carrier.getUniqueID();
                int carriedFoodObjId = carriedFood.getObjId();

                carrier.setCarryItem(TakeoutItemType.FOOD, carriedFood);

                roundTripSaveLoad(tempFile);

                Yukkuri restoredCarrier = findBodyAcrossMaps(carrierId);
                assertNotNull(restoredCarrier);
                assertNotNull(restoredCarrier.getCarryItem(TakeoutItemType.FOOD));
                assertEquals(carriedFoodObjId, restoredCarrier.getCarryItem(TakeoutItemType.FOOD).getObjId());
                assertTrue(SimYukkuri.world.getCurrentMap().getTakenOutFood().containsKey(carriedFoodObjId)
                        || SimYukkuri.world.getMapList().stream().anyMatch(
                                map -> map.getTakenOutFood().containsKey(carriedFoodObjId)));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesParentChildAndPartnerRelations() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_family", ".sav").toFile();
            try {
                Reimu parent = new Reimu();
                Reimu partner = new Reimu();
                Marisa child = new Marisa();

                SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
                SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                parent.setPartner(partner.getUniqueID());
                partner.setPartner(parent.getUniqueID());
                child.setParents(new int[] { parent.getUniqueID(), partner.getUniqueID() });
                parent.getChildrenList().add(child.getUniqueID());
                partner.getChildrenList().add(child.getUniqueID());

                int parentId = parent.getUniqueID();
                int partnerId = partner.getUniqueID();
                int childId = child.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredParent = findBodyAcrossMaps(parentId);
                Yukkuri restoredPartner = findBodyAcrossMaps(partnerId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredParent);
                assertNotNull(restoredPartner);
                assertNotNull(restoredChild);
                assertEquals(partnerId, restoredParent.getPartner());
                assertEquals(parentId, restoredPartner.getPartner());
                assertArrayEquals(new int[] { parentId, partnerId }, restoredChild.getParents());
                assertTrue(restoredParent.getChildrenList().contains(childId));
                assertTrue(restoredPartner.getChildrenList().contains(childId));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesAnimalPregnancyAndFamilyRelations() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_pregnant", ".sav").toFile();
            try {
                Reimu mother = new Reimu();
                Marisa partner = new Marisa();
                Reimu child = new Reimu();

                SimYukkuri.world.getCurrentMap().getBody().put(mother.getUniqueID(), mother);
                SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                mother.setPartner(partner.getUniqueID());
                partner.setPartner(mother.getUniqueID());
                child.setParents(new int[] { mother.getUniqueID(), partner.getUniqueID() });
                mother.getChildrenList().add(child.getUniqueID());
                partner.getChildrenList().add(child.getUniqueID());

                Dna babyDna = new Dna();
                babyDna.setFather(partner.getUniqueID());
                babyDna.setMother(mother.getUniqueID());
                mother.setHasBaby(true);
                mother.getBabyTypes().add(babyDna);

                int motherId = mother.getUniqueID();
                int partnerId = partner.getUniqueID();
                int childId = child.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredMother = findBodyAcrossMaps(motherId);
                Yukkuri restoredPartner = findBodyAcrossMaps(partnerId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredMother);
                assertNotNull(restoredPartner);
                assertNotNull(restoredChild);
                assertTrue(restoredMother.isHasBaby());
                assertEquals(1, restoredMother.getBabyTypes().size());
                assertEquals(partnerId, restoredMother.getBabyTypes().get(0).getFather());
                assertEquals(motherId, restoredMother.getBabyTypes().get(0).getMother());
                assertEquals(partnerId, restoredMother.getPartner());
                assertArrayEquals(new int[] { motherId, partnerId }, restoredChild.getParents());
                assertTrue(restoredMother.getChildrenList().contains(childId));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadRestoresStalkPregnancyBindings() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_stalk_pregnancy", ".sav").toFile();
            try {
                Reimu parent = new Reimu();
                Reimu unbornBaby = new Reimu();
                unbornBaby.setUnBirth(true);

                SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
                SimYukkuri.world.getCurrentMap().getBody().put(unbornBaby.getUniqueID(), unbornBaby);

                parent.setHasStalk(true);
                parent.getStalkBabyTypes().add(new Dna());

                Stalk stalk = new Stalk(parent.getX(), parent.getY(), 0);
                stalk.setPlantYukkuri(parent);
                stalk.getBindBabies().add(unbornBaby.getUniqueID());
                parent.getStalks().add(stalk);

                unbornBaby.setParentLinkId(parent.getUniqueID());
                unbornBaby.setBindStalk(stalk);

                int parentId = parent.getUniqueID();
                int babyId = unbornBaby.getUniqueID();
                java.util.UUID stalkId = stalk.getStalkId();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredParent = findBodyAcrossMaps(parentId);
                Yukkuri restoredBaby = findBodyAcrossMaps(babyId);
                Stalk restoredStalk = findStalkAcrossMaps(stalkId);

                assertNotNull(restoredParent);
                assertNotNull(restoredBaby);
                assertNotNull(restoredStalk);
                assertTrue(restoredParent.isHasStalk());
                assertEquals(1, restoredParent.getStalkBabyTypes().size());
                assertEquals(parentId, restoredStalk.getPlantYukkuri());
                assertTrue(restoredStalk.getBindBabies().contains(babyId));
                assertEquals(parentId, restoredBaby.getParentLinkId());
                assertNotNull(restoredBaby.getBindStalk());
                assertEquals(stalkId, restoredBaby.getBindStalk().getStalkId());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesTransformedReimuWithPregnancyAndFamilyRelations() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_transformed_reimu", ".sav").toFile();
            try {
                SimYukkuri.mypane = new MyPane();

                Reimu reimu = new Reimu();
                reimu.setAge(100000);
                WorldTestHelper.makeTransformationReady(reimu);
                reimu.setHasBaby(true);
                reimu.getBabyTypes().add(new Dna());

                Reimu partner = new Reimu();
                Reimu child = new Reimu();
                reimu.setPartner(partner.getUniqueID());
                partner.setPartner(reimu.getUniqueID());
                child.setParents(new int[] { reimu.getUniqueID(), -1 });
                reimu.getChildrenList().add(child.getUniqueID());

                SimYukkuri.world.getCurrentMap().getBody().put(reimu.getUniqueID(), reimu);
                SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                int transformedId = reimu.getUniqueID();
                int partnerId = partner.getUniqueID();
                int childId = child.getUniqueID();

                reimu.execTransform();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(transformedId);
                Yukkuri restoredPartner = findBodyAcrossMaps(partnerId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restored);
                assertTrue(restored instanceof Deibu);
                assertTrue(restored.isHasBaby());
                assertEquals(1, restored.getBabyTypes().size());
                assertEquals(partnerId, restored.getPartner());
                assertTrue(restored.getChildrenList().contains(childId));
                assertNotNull(restoredPartner);
                assertEquals(transformedId, restoredPartner.getPartner());
                assertNotNull(restoredChild);
                assertEquals(transformedId, restoredChild.getParents()[0]);
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesTransformedMarisaWithStalkPregnancyAndFamilyRelations() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_transformed_marisa", ".sav").toFile();
            try {
                SimYukkuri.mypane = new MyPane();

                Marisa marisa = new Marisa();
                marisa.setAge(100000);
                WorldTestHelper.makeTransformationReady(marisa);
                marisa.setHasStalk(true);
                marisa.getStalkBabyTypes().add(new Dna());

                Reimu partner = new Reimu();
                Reimu child = new Reimu();
                marisa.setPartner(partner.getUniqueID());
                partner.setPartner(marisa.getUniqueID());
                child.setParents(new int[] { marisa.getUniqueID(), -1 });
                marisa.getChildrenList().add(child.getUniqueID());

                SimYukkuri.world.getCurrentMap().getBody().put(marisa.getUniqueID(), marisa);
                SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                int transformedId = marisa.getUniqueID();
                int partnerId = partner.getUniqueID();
                int childId = child.getUniqueID();

                marisa.execTransform();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(transformedId);
                Yukkuri restoredPartner = findBodyAcrossMaps(partnerId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restored);
                assertTrue(restored instanceof DosMarisa);
                assertTrue(restored.isHasStalk());
                assertEquals(1, restored.getStalkBabyTypes().size());
                assertEquals(partnerId, restored.getPartner());
                assertTrue(restored.getChildrenList().contains(childId));
                assertNotNull(restoredPartner);
                assertEquals(transformedId, restoredPartner.getPartner());
                assertNotNull(restoredChild);
                assertEquals(transformedId, restoredChild.getParents()[0]);
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadRestoresFavoriteBedReference() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_favorite_bed", ".sav").toFile();
            try {
                Yukkuri body = WorldTestHelper.createBody();
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                Bed bed = new Bed(120, 120, 1);
                int bodyId = body.getUniqueID();
                int bedObjId = bed.getObjId();

                body.setFavoriteItem(FavItemType.BED, bed);

                roundTripSaveLoad(tempFile);

                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);
                assertNotNull(restoredBody);
                assertNotNull(restoredBody.getFavoriteItem(FavItemType.BED),
                        "favorite bed should still resolve after save/load");
                assertEquals(bedObjId, restoredBody.getFavoriteItem(FavItemType.BED).getObjId(),
                        "favorite bed should preserve its object id across save/load");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesSleepingBodyPositionOnBed() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_sleeping_bed_body", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                Bed bed = new Bed(160, 160, Bed.ItemRank.NORA.ordinal());
                body.setFavoriteItem(FavItemType.BED, bed);
                body.forceToSleep();
                body.setX(163);
                body.setY(158);

                int bodyId = body.getUniqueID();
                int bedId = bed.getObjId();
                int expectedX = body.getX();
                int expectedY = body.getY();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);
                Bed restoredBed = findBedAcrossMaps(bedId);

                assertNotNull(restoredBody);
                assertNotNull(restoredBed);
                assertEquals(expectedX, restoredBody.getX(), "sleeping body x position should survive save/load");
                assertEquals(expectedY, restoredBody.getY(), "sleeping body y position should survive save/load");
                assertSame(restoredBed, restoredBody.getFavoriteItem(FavItemType.BED),
                        "sleeping body should still point to the restored bed instance");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesRepresentativeItemProperties() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_item_properties", ".sav").toFile();
            try {
                Bed bed = new Bed(140, 140, 1);
                int bedId = bed.getObjId();

                AutoFeeder feeder = new AutoFeeder();
                feeder.setMode(1);
                feeder.setFeedingInterval(321);
                feeder.setFeedingP(7);
                SimYukkuri.world.getCurrentMap().getAutofeeder().put(feeder.getObjId(), feeder);
                int feederId = feeder.getObjId();

                ProcesserPlate plate = new ProcesserPlate();
                plate.setEnumProcessType(ProcesserPlate.ProcessType.PACKING);
                SimYukkuri.world.getCurrentMap().getProcesserPlate().put(plate.getObjId(), plate);
                int plateId = plate.getObjId();

                roundTripSaveLoad(tempFile);

                Bed restoredBed = null;
                AutoFeeder restoredFeeder = null;
                ProcesserPlate restoredPlate = null;
                for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
                    if (restoredBed == null && map.getBed().containsKey(bedId)) {
                        restoredBed = map.getBed().get(bedId);
                    }
                    if (restoredFeeder == null && map.getAutofeeder().containsKey(feederId)) {
                        restoredFeeder = map.getAutofeeder().get(feederId);
                    }
                    if (restoredPlate == null && map.getProcesserPlate().containsKey(plateId)) {
                        restoredPlate = map.getProcesserPlate().get(plateId);
                    }
                }

                assertNotNull(restoredBed);
                assertEquals(Bed.ItemRank.NORA, restoredBed.getItemRank(),
                        "bed rank should survive save/load");

                assertNotNull(restoredFeeder);
                assertEquals(1, restoredFeeder.getMode(), "autofeeder mode should survive save/load");
                assertEquals(321, restoredFeeder.getFeedingInterval(),
                        "autofeeder feeding interval should survive save/load");
                assertEquals(7, restoredFeeder.getFeedingP(),
                        "autofeeder feeding probability should survive save/load");

                assertNotNull(restoredPlate);
                assertEquals(ProcesserPlate.ProcessType.PACKING, restoredPlate.getEnumProcessType(),
                        "processer plate process type should survive save/load");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesDiffuserProperties() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_diffuser_props", ".sav").toFile();
            try {
                Diffuser diffuser = new Diffuser();
                diffuser.setObjId(4123);
                diffuser.setX(180);
                diffuser.setY(210);
                diffuser.setEnabled(false);

                boolean[] steamType = new boolean[Diffuser.SteamType.values().length];
                steamType[Diffuser.SteamType.ANTI_FUNGAL.ordinal()] = true;
                steamType[Diffuser.SteamType.ORANGE.ordinal()] = true;
                steamType[Diffuser.SteamType.ANTI_NONYUKKURI.ordinal()] = true;
                diffuser.setSteamType(steamType);
                diffuser.setSteamNum(Diffuser.SteamType.ORANGE.ordinal());

                SimYukkuri.world.getCurrentMap().getDiffuser().put(diffuser.getObjId(), diffuser);

                roundTripSaveLoad(tempFile);

                Diffuser restoredDiffuser = findDiffuserAcrossMaps(4123);
                assertNotNull(restoredDiffuser);
                assertEquals(4123, restoredDiffuser.getObjId());
                assertEquals(180, restoredDiffuser.getX());
                assertEquals(210, restoredDiffuser.getY());
                assertFalse(restoredDiffuser.getEnabled());
                assertArrayEquals(steamType, restoredDiffuser.getSteamType(),
                        "diffuser steam type configuration should survive save/load");
                assertEquals(Diffuser.SteamType.ORANGE.ordinal(), restoredDiffuser.getSteamNum(),
                        "diffuser steam cursor should survive save/load");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesActiveDiffuserSteamEmission() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_diffuser_active", ".sav").toFile();
            try {
                SimYukkuri.mypane = new MyPane();

                Diffuser diffuser = new Diffuser();
                diffuser.setObjId(4124);
                diffuser.setX(220);
                diffuser.setY(240);
                diffuser.setEnabled(true);
                diffuser.setAge(80);

                boolean[] steamType = new boolean[Diffuser.SteamType.values().length];
                int activeSteam = Diffuser.SteamType.AGE_STOP.ordinal();
                steamType[activeSteam] = true;
                diffuser.setSteamType(steamType);
                diffuser.setSteamNum(activeSteam);

                SimYukkuri.world.getCurrentMap().getDiffuser().put(diffuser.getObjId(), diffuser);

                roundTripSaveLoad(tempFile);
                SimYukkuri.mypane = new MyPane();

                Diffuser restoredDiffuser = findDiffuserAcrossMaps(4124);
                assertNotNull(restoredDiffuser);
                assertTrue(restoredDiffuser.getEnabled());
                assertEquals(activeSteam, restoredDiffuser.getSteamNum(),
                        "active diffuser should keep its current steam slot after load");

                int effectCountBefore = SimYukkuri.world.getCurrentMap().getSortEffect().size();
                restoredDiffuser.upDate();
                int effectCountAfter = SimYukkuri.world.getCurrentMap().getSortEffect().size();

                assertEquals(effectCountBefore + 1, effectCountAfter,
                        "active diffuser should still emit steam after save/load");

                Effect emitted = SimYukkuri.world.getCurrentMap().getSortEffect().values().stream()
                        .max(java.util.Comparator.comparingInt(Effect::getObjId))
                        .orElse(null);
                assertNotNull(emitted);
                assertEquals(Diffuser.SteamType.values()[activeSteam].getColor(), emitted.getAnimeFrame(),
                        "emitted steam should keep the expected color frame after save/load");
                assertEquals(0, restoredDiffuser.getSteamNum(),
                        "current diffuser implementation wraps the steam cursor back to zero after a single-slot emit");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesActiveHotPlateFootBakeProgress() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_hotplate_active", ".sav").toFile();
            try {
                SimYukkuri.mypane = new MyPane();

                HotPlate hotPlate = new HotPlate();
                hotPlate.setObjId(4224);
                hotPlate.setX(260);
                hotPlate.setY(280);
                SimYukkuri.world.getCurrentMap().getHotPlate().put(hotPlate.getObjId(), hotPlate);

                Yukkuri body = WorldTestHelper.createBody();
                body.setObjId(5224);
                body.setCalcX(hotPlate.getX());
                body.setCalcY(hotPlate.getY());
                body.setCalcZ(hotPlate.getZ());
                body.setSleeping(true);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                hotPlate.objHitProcess(body);
                body.setX(hotPlate.getX());
                body.setY(hotPlate.getY());
                body.setZ(hotPlate.getZ());

                int hotPlateId = hotPlate.getObjId();
                int bodyId = body.getUniqueID();
                int smokeId = hotPlate.getSmoke().getObjId();
                int footBakeBeforeSave = body.getFootBakePeriod();
                int damageBeforeSave = body.getDamage();
                int stressBeforeSave = body.getStress();

                roundTripSaveLoad(tempFile);
                SimYukkuri.mypane = new MyPane();

                HotPlate restoredHotPlate = findHotPlateAcrossMaps(hotPlateId);
                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);
                Effect restoredSmoke = findEffectAcrossMaps(smokeId);

                assertNotNull(restoredHotPlate);
                assertNotNull(restoredBody);
                assertNotNull(restoredSmoke);
                assertSame(restoredBody, restoredHotPlate.getBindBody(),
                        "loaded hot plate should still point at the restored bound body");
                assertSame(restoredSmoke, restoredHotPlate.getSmoke(),
                        "loaded hot plate should still point at the restored smoke effect");

                restoredHotPlate.upDate();

                assertFalse(restoredBody.isSleeping(),
                        "bound body should keep being woken up after load");
                assertEquals(footBakeBeforeSave + 50, restoredBody.getFootBakePeriod(),
                        "foot bake period should continue from the pre-save state after load");
                assertEquals(damageBeforeSave + 20, restoredBody.getDamage(),
                        "damage should continue accumulating after load");
                assertEquals(stressBeforeSave + 20, restoredBody.getStress(),
                        "stress should continue accumulating after load");
                assertSame(restoredBody, restoredHotPlate.getBindBody(),
                        "hot plate should remain in the active burning state after one loaded tick");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesActiveStickyPlateBinding() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_stickyplate_active", ".sav").toFile();
            try {
                StickyPlate stickyPlate = new StickyPlate();
                stickyPlate.setObjId(4325);
                stickyPlate.setX(300);
                stickyPlate.setY(320);
                SimYukkuri.world.getCurrentMap().getStickyPlate().put(stickyPlate.getObjId(), stickyPlate);

                Yukkuri body = WorldTestHelper.createBody();
                body.setObjId(5325);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                stickyPlate.objHitProcess(body);
                body.setX(stickyPlate.getX());
                body.setY(stickyPlate.getY());
                body.setZ(stickyPlate.getZ());

                int plateId = stickyPlate.getObjId();
                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                StickyPlate restoredPlate = findStickyPlateAcrossMaps(plateId);
                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);

                assertNotNull(restoredPlate);
                assertNotNull(restoredBody);
                assertSame(restoredBody, restoredPlate.getBindBody());

                restoredPlate.upDate();

                assertSame(restoredBody, restoredPlate.getBindBody(),
                        "sticky plate should keep the restored body bound after load");
                assertTrue(restoredBody.isLockmove(),
                        "restored sticky plate binding should keep the body immobilized");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesActiveMixerProcessing() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_mixer_active", ".sav").toFile();
            try {
                SimYukkuri.mypane = new MyPane();

                Mixer mixer = new Mixer();
                mixer.setObjId(4326);
                mixer.setX(340);
                mixer.setY(360);
                SimYukkuri.world.getCurrentMap().getMixer().put(mixer.getObjId(), mixer);

                Yukkuri body = WorldTestHelper.createBody();
                body.setObjId(5326);
                body.setAnkoAmount(2000);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                mixer.objHitProcess(body);
                body.setX(mixer.getX());
                body.setY(mixer.getY());
                body.setZ(mixer.getZ());
                mixer.setCounter(61);
                mixer.upDate();

                int mixerId = mixer.getObjId();
                int bodyId = body.getUniqueID();
                int mixId = mixer.getMix().getObjId();
                int amountBeforeSave = mixer.getAmount();
                int damageBeforeSave = body.getDamage();
                int stressBeforeSave = body.getStress();

                roundTripSaveLoad(tempFile);
                SimYukkuri.mypane = new MyPane();

                Mixer restoredMixer = findMixerAcrossMaps(mixerId);
                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);
                Effect restoredMix = findEffectAcrossMaps(mixId);

                assertNotNull(restoredMixer);
                assertNotNull(restoredBody);
                assertNotNull(restoredMix);
                assertEquals(bodyId, restoredMixer.getBind());
                assertSame(restoredMix, restoredMixer.getMix());

                restoredMixer.upDate();

                assertEquals(amountBeforeSave + 100, restoredMixer.getAmount(),
                        "restored mixer should continue accumulating material");
                assertEquals(damageBeforeSave + 100, restoredBody.getDamage(),
                        "restored mixer should continue damaging the bound body");
                assertEquals(stressBeforeSave + 100, restoredBody.getStress(),
                        "restored mixer should continue stressing the bound body");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesGarbageChuteBoundObjects() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_garbagechute_active", ".sav").toFile();
            try {
                GarbageChute garbageChute = new GarbageChute();
                garbageChute.setObjId(4327);
                garbageChute.setX(380);
                garbageChute.setY(400);
                SimYukkuri.world.getCurrentMap().getGarbagechute().put(garbageChute.getObjId(), garbageChute);

                Yukkuri body = WorldTestHelper.createBody();
                body.setObjId(5327);
                installSyntheticBodySprites(body, 32, 32);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                garbageChute.objHitProcess(body);
                body.setX(garbageChute.getX());
                body.setY(garbageChute.getY());
                body.setZ(10);

                int chuteId = garbageChute.getObjId();
                int bodyId = body.getUniqueID();
                int zBeforeSave = body.getZ();

                roundTripSaveLoad(tempFile);

                GarbageChute restoredChute = findGarbageChuteAcrossMaps(chuteId);
                Yukkuri restoredBody = findBodyAcrossMaps(bodyId);

                assertNotNull(restoredChute);
                assertNotNull(restoredBody);
                assertEquals(1, restoredChute.getBindObjList().size());
                assertSame(restoredBody, restoredChute.getBindObjList().get(0));
                assertSame(restoredBody, restoredChute.getBindBody());

                restoredChute.upDate();

                assertEquals(zBeforeSave - 2, restoredBody.getZ(),
                        "restored garbage chute should keep dropping the bound body");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesGarbageStationFoodSlots() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_garbagestation_food", ".sav").toFile();
            try {
                GarbageStation garbageStation = new GarbageStation();
                garbageStation.setObjId(4328);
                boolean[] enable = new boolean[GarbageStation.GomiType.values().length];
                enable[GarbageStation.GomiType.WASTE.ordinal()] = true;
                enable[GarbageStation.GomiType.NORMAL.ordinal()] = true;
                garbageStation.setEnable(enable);
                SimYukkuri.world.getCurrentMap().getGarbageStation().put(garbageStation.getObjId(), garbageStation);

                Food leftFood = new Food(garbageStation.getX() - 20, garbageStation.getY(), Food.FoodType.WASTE_NORA.ordinal());
                Food rightFood = new Food(garbageStation.getX() + 20, garbageStation.getY(), Food.FoodType.FOOD_NORA.ordinal());
                SimYukkuri.world.getCurrentMap().getFood().put(leftFood.getObjId(), leftFood);
                SimYukkuri.world.getCurrentMap().getFood().put(rightFood.getObjId(), rightFood);
                garbageStation.setFood(new Entity[] { leftFood, rightFood });

                int stationId = garbageStation.getObjId();
                int leftFoodId = leftFood.getObjId();
                int rightFoodId = rightFood.getObjId();

                roundTripSaveLoad(tempFile);

                GarbageStation restoredStation = findGarbageStationAcrossMaps(stationId);
                Food restoredLeftFood = null;
                Food restoredRightFood = null;
                for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
                    if (restoredLeftFood == null) {
                        restoredLeftFood = map.getFood().get(leftFoodId);
                    }
                    if (restoredRightFood == null) {
                        restoredRightFood = map.getFood().get(rightFoodId);
                    }
                }

                assertNotNull(restoredStation);
                assertNotNull(restoredLeftFood);
                assertNotNull(restoredRightFood);
                assertSame(restoredLeftFood, restoredStation.getFood()[0]);
                assertSame(restoredRightFood, restoredStation.getFood()[1]);
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesAutoFeederBlockedByLivingSpawnedBody() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_autofeeder_blocked_by_living_body", ".sav")
                    .toFile();
            try {
                AutoFeeder feeder = new AutoFeeder();
                feeder.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
                feeder.setX(220);
                feeder.setY(220);
                feeder.setEnabled(true);
                feeder.setMode(0);
                feeder.setType(AutoFeeder.FeedType.BODY.ordinal());
                feeder.setAge(0);
                SimYukkuri.world.getCurrentMap().getAutofeeder().put(feeder.getObjId(), feeder);

                Yukkuri spawnedBody = WorldTestHelper.createBody();
                spawnedBody.setAgeState(AgeState.BABY);
                spawnedBody.setX(feeder.getX());
                spawnedBody.setY(feeder.getY());
                SimYukkuri.world.getCurrentMap().getBody().put(spawnedBody.getUniqueID(), spawnedBody);
                feeder.setFood(spawnedBody);

                int feederId = feeder.getObjId();
                int spawnedBodyId = spawnedBody.getUniqueID();

                roundTripSaveLoad(tempFile);

                AutoFeeder restoredFeeder = findAutoFeederAcrossMaps(feederId);
                Yukkuri restoredSpawnedBody = findBodyAcrossMaps(spawnedBodyId);

                assertNotNull(restoredFeeder);
                assertNotNull(restoredSpawnedBody);
                assertSame(restoredSpawnedBody, restoredFeeder.getFood(),
                        "autofeeder should still point at the living spawned body after save/load");

                int bodyCountBeforeUpdate = 0;
                for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
                    bodyCountBeforeUpdate += map.getBody().size();
                }

                restoredFeeder.setAge(0);
                restoredFeeder.upDate();

                int bodyCountAfterUpdate = 0;
                for (src.system.MapPlaceData map : SimYukkuri.world.getMapList()) {
                    bodyCountAfterUpdate += map.getBody().size();
                }

                assertEquals(bodyCountBeforeUpdate, bodyCountAfterUpdate,
                        "autofeeder should not spawn another body while the previous spawned body is still alive");
                assertSame(restoredSpawnedBody, restoredFeeder.getFood(),
                        "autofeeder should remain blocked by the same living body after update");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesYunbaInstructionsAndContinuesWorking() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_yunba_command_state", ".sav").toFile();
            try {
                Yunba yunba = new Yunba();
                yunba.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
                yunba.setX(100);
                yunba.setY(100);
                yunba.setDestX(100);
                yunba.setDestY(100);
                yunba.setDefaultX(320);
                yunba.setDefaultY(340);
                yunba.setSpeed(600);
                yunba.setLayerCount(1);
                yunba.setDrawLayer(new int[] { 0 });
                yunba.setActionFlags(new boolean[Yunba.Action.values().length][3]);
                yunba.setActionFlags2(new boolean[1][5]);
                yunba.setActionFlags3(new boolean[1][3]);
                yunba.getActionFlags()[Yunba.Action.WALLTHROUGH.ordinal()][0] = true;
                yunba.getActionFlags()[Yunba.Action.CLEAN.ordinal()][1] = true;
                yunba.getActionFlags2()[0][2] = true;
                yunba.getActionFlags3()[0][1] = true;
                yunba.setBodyCheck(true);
                yunba.setShitCheck(true);
                yunba.setStalkCheck(true);
                yunba.setNorndCheck(true);
                yunba.setKillCheck(true);
                yunba.setMineutiCheck(true);
                yunba.setNoDamageFallCheck(true);
                yunba.setFoodCheck(true);
                yunba.setAction(Yunba.Action.HEAL);
                SimYukkuri.world.getCurrentMap().getYunba().put(yunba.getObjId(), yunba);

                Yukkuri targetBody = WorldTestHelper.createBody();
                WorldTestHelper.setDamage(targetBody, 50);
                targetBody.setX(100);
                targetBody.setY(100);
                SimYukkuri.world.getCurrentMap().getBody().put(targetBody.getUniqueID(), targetBody);
                yunba.setTarget(targetBody);

                int yunbaId = yunba.getObjId();
                int targetId = targetBody.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yunba restoredYunba = findYunbaAcrossMaps(yunbaId);
                Yukkuri restoredTarget = findBodyAcrossMaps(targetId);

                assertNotNull(restoredYunba);
                assertNotNull(restoredTarget);
                assertEquals(320, restoredYunba.getDefaultX(), "yunba default x should survive save/load");
                assertEquals(340, restoredYunba.getDefaultY(), "yunba default y should survive save/load");
                assertEquals(600, restoredYunba.getSpeed(), "yunba speed should survive save/load");
                assertTrue(restoredYunba.getActionFlags()[Yunba.Action.WALLTHROUGH.ordinal()][0],
                        "yunba wall-through instruction should survive save/load");
                assertTrue(restoredYunba.getActionFlags()[Yunba.Action.CLEAN.ordinal()][1],
                        "yunba secondary action flag should survive save/load");
                assertTrue(restoredYunba.getActionFlags2()[0][2],
                        "yunba second flag table should survive save/load");
                assertTrue(restoredYunba.getActionFlags3()[0][1],
                        "yunba third flag table should survive save/load");
                assertTrue(restoredYunba.isBodyCheck(), "yunba body check should survive save/load");
                assertTrue(restoredYunba.isShitCheck(), "yunba shit check should survive save/load");
                assertTrue(restoredYunba.isStalkCheck(), "yunba stalk check should survive save/load");
                assertTrue(restoredYunba.isNorndCheck(), "yunba no-random check should survive save/load");
                assertTrue(restoredYunba.isKillCheck(), "yunba kill check should survive save/load");
                assertTrue(restoredYunba.isMineutiCheck(), "yunba mineuti check should survive save/load");
                assertTrue(restoredYunba.isNoDamageFallCheck(),
                        "yunba no-fall-damage check should survive save/load");
                assertTrue(restoredYunba.isFoodCheck(), "yunba food check should survive save/load");
                assertEquals(Yunba.Action.HEAL, restoredYunba.getAction(),
                        "yunba current action should survive save/load");
                assertSame(restoredTarget, restoredYunba.getTarget(),
                        "yunba current target should reconnect to the restored body instance");

                restoredYunba.clockTick();

                assertEquals(0, restoredTarget.getDamage(), "restored yunba should still heal its restored target");
                assertNull(restoredYunba.getAction(), "yunba should clear the action after finishing the restored task");
                assertNull(restoredYunba.getTarget(), "yunba should clear the target after finishing the restored task");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesEquippedOkazariTypeOnNormalBody() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_okazari_normal", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                body.giveOkazari(OkazariType.ADULT3);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertTrue(restored.hasOkazari());
                assertNotNull(restored.getOkazari());
                assertEquals(OkazariType.ADULT3, restored.getOkazari().getOkazariType());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesNoOkazariStateOnNormalBody() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_no_okazari_normal", ".sav").toFile();
            try {
                Marisa body = new Marisa();
                body.setOkazari(null);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertFalse(restored.hasOkazari());
                assertNull(restored.getOkazari());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesOkazariStateOnMarisaReimu() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_okazari_marisa_reimu", ".sav").toFile();
            try {
                MarisaReimu body = new MarisaReimu();
                body.giveOkazari(OkazariType.ADULT1);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertTrue(restored instanceof MarisaReimu);
                assertTrue(restored.hasOkazari());
                assertNotNull(restored.getOkazari());
                assertEquals(OkazariType.ADULT1, restored.getOkazari().getOkazariType());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesOkazariStateOnReimuMarisa() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_okazari_reimu_marisa", ".sav").toFile();
            try {
                ReimuMarisa body = new ReimuMarisa();
                body.giveOkazari(OkazariType.ADULT2);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertTrue(restored instanceof ReimuMarisa);
                assertTrue(restored.hasOkazari());
                assertNotNull(restored.getOkazari());
                assertEquals(OkazariType.ADULT2, restored.getOkazari().getOkazariType());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesPreBurnBodyState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_pre_burn_body", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertFalse(restored.isDead());
                assertFalse(restored.isBurned());
                assertEquals(0, restored.getAttachmentSize(Fire.class));
                assertNull(findFireAttachment(restored));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBurningBodyState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_burning_body", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                Fire fire = new Fire(body);
                fire.setBurnPeriod(450);
                body.addAttachment(fire);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertFalse(restored.isDead());
                assertFalse(restored.isBurned());
                assertEquals(1, restored.getAttachmentSize(Fire.class));
                Fire restoredFire = findFireAttachment(restored);
                assertNotNull(restoredFire);
                assertEquals(450, restoredFire.getBurnPeriod());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBurnedCorpseState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_burned_corpse", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                body.setDead(true);
                body.setBurned(true);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertTrue(restored.isDead());
                assertTrue(restored.isBurned());
                assertEquals(0, restored.getAttachmentSize(Fire.class));
                assertNull(findFireAttachment(restored));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesAntsAttachmentState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_ants_attachment", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                body.setAntCount(120);
                body.addAttachment(new Ants(body));
                body.setAntCount(120);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertEquals(1, restored.getAttachmentSize(Ants.class));
                assertNotNull(findAttachment(restored, Ants.class));
                assertEquals(120, restored.getAntCount());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBadgeAttachmentRank() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_badge_attachment", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                body.addAttachment(new Badge(body, Badge.BadgeRank.GOLD));
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertEquals(1, restored.getAttachmentSize(Badge.class));
                Badge restoredBadge = findAttachment(restored, Badge.class);
                assertNotNull(restoredBadge);
                assertEquals(Badge.BadgeRank.GOLD, restoredBadge.getBadgeRank());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesRepresentativeAmpouleAttachments() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_ampoule_attachments", ".sav").toFile();
            try {
                Reimu poisonBody = new Reimu();
                poisonBody.addAttachment(new PoisonAmpoule(poisonBody));
                SimYukkuri.world.getCurrentMap().getBody().put(poisonBody.getUniqueID(), poisonBody);

                Marisa breedingBody = new Marisa();
                breedingBody.addAttachment(new BreedingAmpoule(breedingBody));
                SimYukkuri.world.getCurrentMap().getBody().put(breedingBody.getUniqueID(), breedingBody);

                int poisonBodyId = poisonBody.getUniqueID();
                int breedingBodyId = breedingBody.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredPoisonBody = findBodyAcrossMaps(poisonBodyId);
                Yukkuri restoredBreedingBody = findBodyAcrossMaps(breedingBodyId);

                assertNotNull(restoredPoisonBody);
                assertNotNull(restoredBreedingBody);
                assertEquals(1, restoredPoisonBody.getAttachmentSize(PoisonAmpoule.class));
                assertNotNull(findAttachment(restoredPoisonBody, PoisonAmpoule.class));
                assertEquals(1, restoredBreedingBody.getAttachmentSize(BreedingAmpoule.class));
                assertNotNull(findAttachment(restoredBreedingBody, BreedingAmpoule.class));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBodyRidingOnSuiState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_sui_ride", ".sav").toFile();
            try {
                Yukkuri rider = WorldTestHelper.createBody();
                rider.setX(140);
                rider.setY(140);
                SimYukkuri.world.getCurrentMap().getBody().put(rider.getUniqueID(), rider);

                Sui sui = new Sui();
                sui.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
                sui.setX(140);
                sui.setY(140);
                SimYukkuri.world.getCurrentMap().getSui().put(sui.getObjId(), sui);

                assertTrue(sui.rideOn(rider));

                int riderId = rider.getUniqueID();
                int suiId = sui.getObjId();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredRider = findBodyAcrossMaps(riderId);
                Sui restoredSui = findSuiAcrossMaps(suiId);

                assertNotNull(restoredRider);
                assertNotNull(restoredSui);
                assertEquals(suiId, restoredRider.getParentLinkId());
                assertEquals(restoredRider, restoredSui.getBindobj());
                assertTrue(restoredSui.isriding(restoredRider));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesChildRidingOnParentsHeadState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_child_on_head", ".sav").toFile();
            try {
                Reimu parent = new Reimu();
                parent.setX(180);
                parent.setY(180);
                SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);

                Reimu child = new Reimu();
                child.setAgeState(AgeState.BABY);
                child.setX(180);
                child.setY(180);
                child.setParentLinkId(parent.getObjId());
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                int parentId = parent.getUniqueID();
                int childId = child.getUniqueID();
                int parentObjId = parent.getObjId();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredParent = findBodyAcrossMaps(parentId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredParent);
                assertNotNull(restoredChild);
                assertEquals(parentObjId, restoredChild.getParentLinkId());
                assertEquals(restoredParent, restoredChild.takeMappedObj(restoredChild.getParentLinkId()));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesFarmBuriedBodyState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_farm_buried_body", ".sav").toFile();
            try {
                Farm farm = new Farm(100, 100, 180, 180);
                Reimu body = new Reimu();
                body.setX((farm.getMapSX() + farm.getMapEX()) / 2);
                body.setY((farm.getMapSY() + farm.getMapEY()) / 2);
                body.setBurialState(BurialState.NEARLY_ALL);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();
                int mapSX = farm.getMapSX();
                int mapSY = farm.getMapSY();
                int mapEX = farm.getMapEX();
                int mapEY = farm.getMapEY();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                Farm restoredFarm = findFarmAcrossMaps(mapSX, mapSY, mapEX, mapEY);

                assertNotNull(restored);
                assertNotNull(restoredFarm);
                assertEquals(BurialState.NEARLY_ALL, restored.getBurialState());
                assertTrue(restoredFarm.mapContains(restored.getX(), restored.getY()));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesGodHandExpandedBodyState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_expanded_body", ".sav").toFile();
            try {
                Reimu body = new Reimu();
                installSyntheticBodySprites(body, 100, 80);
                body.setGodHandHoldCount(80);
                assertTrue(body.getSize() > body.getOriginSize(),
                        "fixture should observe the enlarged body before save");
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();
                int expectedHoldCount = body.getGodHandHoldCount();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                assertNotNull(restored);
                assertEquals(expectedHoldCount, restored.getGodHandHoldCount());
                assertTrue(restored.getSize() > restored.getOriginSize(),
                        "expanded body should still be larger than its origin size after load");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBodyOnBeltconveyorState() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_body_on_belt", ".sav").toFile();
            try {
                Beltconveyor belt = new Beltconveyor();
                belt.setMapPos(200, 200, 280, 220);
                belt.setMapW(81);
                belt.setMapH(21);
                belt.setFieldPos(200, 200, 280, 220);
                belt.setFieldW(81);
                belt.setFieldH(21);
                SimYukkuri.world.getCurrentMap().getBeltconveyor().add(belt);
                Reimu body = new Reimu();
                body.setX((belt.getMapSX() + belt.getMapEX()) / 2);
                body.setY((belt.getMapSY() + belt.getMapEY()) / 2);
                body.setOnNonMovingConveyor(true);
                SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

                int bodyId = body.getUniqueID();
                int mapSX = belt.getMapSX();
                int mapSY = belt.getMapSY();
                int mapEX = belt.getMapEX();
                int mapEY = belt.getMapEY();

                roundTripSaveLoad(tempFile);

                Yukkuri restored = findBodyAcrossMaps(bodyId);
                Beltconveyor restoredBelt = findBeltAcrossMaps(mapSX, mapSY, mapEX, mapEY);

                assertNotNull(restored);
                assertNotNull(restoredBelt);
                assertTrue(restored.isOnNonMovingConveyor());
                assertTrue(restoredBelt.mapContains(restored.getX(), restored.getY()));
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_LoadStateRejectsCorruptedSaveWithoutReplacingWorld() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_corrupt", ".sav").toFile();
            try {
                terrarium.addBody(100, 100, 0, YukkuriType.REIMU.getTypeID(), AgeState.ADULT, null, null);
                int originalBodyCount = SimYukkuri.world.getCurrentMap().getBody().size();
                long originalCash = SimYukkuri.world.getPlayer().getCash();

                Terrarium.saveState(tempFile);
                byte[] bytes = Files.readAllBytes(tempFile.toPath());
                bytes[Math.max(0, bytes.length / 2)] ^= 0x5A;
                Files.write(tempFile.toPath(), bytes);

                assertThrows(Exception.class, () -> Terrarium.loadState(tempFile),
                        "corrupted save should fail to load");
                assertEquals(originalBodyCount, SimYukkuri.world.getCurrentMap().getBody().size(),
                        "failed load should not replace the in-memory world body map");
                assertEquals(originalCash, SimYukkuri.world.getPlayer().getCash(),
                        "failed load should not replace the in-memory player state");
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesFuneralEventStateAcrossParticipants() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_funeral_event", ".sav").toFile();
            try {
                Reimu from = new Reimu();
                Reimu deceased = new Reimu();
                Marisa child = new Marisa();
                child.setAgeState(AgeState.BABY);
                child.setParents(new int[] { from.getUniqueID(), -1 });

                SimYukkuri.world.getCurrentMap().getBody().put(from.getUniqueID(), from);
                SimYukkuri.world.getCurrentMap().getBody().put(deceased.getUniqueID(), deceased);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                FuneralEvent event = new FuneralEvent(from, deceased, null, 10);
                Class<?> funeralStateClass = Class.forName("src.event.FuneralEvent$STATE");
                java.lang.reflect.Method setState = FuneralEvent.class.getMethod("setState", funeralStateClass);
                Object goodbyeState = Enum.valueOf((Class) funeralStateClass, "GOODBYE");
                setState.invoke(event, goodbyeState);
                event.setTick(17);
                event.setActionFlag(false);
                event.setUnunActionFlag(false);
                event.setFromWaitCount(23);
                from.setCurrentEvent(event);
                child.setCurrentEvent(event);

                int fromId = from.getUniqueID();
                int deceasedId = deceased.getUniqueID();
                int childId = child.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredFrom = findBodyAcrossMaps(fromId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredFrom);
                assertNotNull(restoredChild);
                assertTrue(restoredFrom.getCurrentEvent() instanceof FuneralEvent);
                assertTrue(restoredChild.getCurrentEvent() instanceof FuneralEvent);

                FuneralEvent fromEvent = (FuneralEvent) restoredFrom.getCurrentEvent();
                FuneralEvent childEvent = (FuneralEvent) restoredChild.getCurrentEvent();
                java.lang.reflect.Method getState = FuneralEvent.class.getMethod("getState");
                assertEquals("GOODBYE", String.valueOf(getState.invoke(fromEvent)));
                assertEquals("GOODBYE", String.valueOf(getState.invoke(childEvent)));
                assertEquals(17, fromEvent.getTick());
                assertFalse(fromEvent.isActionFlag());
                assertFalse(fromEvent.isUnunActionFlag());
                assertEquals(23, fromEvent.getFromWaitCount());
                assertEquals(17, childEvent.getTick());
                assertFalse(childEvent.isActionFlag());
                assertFalse(childEvent.isUnunActionFlag());
                assertEquals(23, childEvent.getFromWaitCount());
                assertEquals(fromId, fromEvent.getFrom());
                assertEquals(deceasedId, fromEvent.getTo());
                assertEquals(fromId, childEvent.getFrom());
                assertEquals(deceasedId, childEvent.getTo());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesSuperEatingTimeEventStateAndTarget() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_super_eating_event", ".sav").toFile();
            try {
                Reimu from = new Reimu();
                Marisa child = new Marisa();
                child.setAgeState(AgeState.BABY);
                child.setParents(new int[] { from.getUniqueID(), -1 });
                Food food = new Food(100, 100, Food.FoodType.FOOD.ordinal());

                SimYukkuri.world.getCurrentMap().getBody().put(from.getUniqueID(), from);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
                event.setState(SuperEatingTimeEvent.STATE.START);
                event.setTick(19);
                event.setWaitTicks(11);
                event.setMinimumStep(4);
                from.setCurrentEvent(event);
                child.setCurrentEvent(event);

                int fromId = from.getUniqueID();
                int childId = child.getUniqueID();
                int foodId = food.getObjId();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredFrom = findBodyAcrossMaps(fromId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredFrom);
                assertNotNull(restoredChild);
                assertTrue(restoredFrom.getCurrentEvent() instanceof SuperEatingTimeEvent);
                assertTrue(restoredChild.getCurrentEvent() instanceof SuperEatingTimeEvent);

                SuperEatingTimeEvent fromEvent = (SuperEatingTimeEvent) restoredFrom.getCurrentEvent();
                SuperEatingTimeEvent childEvent = (SuperEatingTimeEvent) restoredChild.getCurrentEvent();
                assertEquals(SuperEatingTimeEvent.STATE.START, fromEvent.getState());
                assertEquals(SuperEatingTimeEvent.STATE.START, childEvent.getState());
                assertEquals(19, fromEvent.getTick());
                assertEquals(11, fromEvent.getWaitTicks());
                assertEquals(4, fromEvent.getMinimumStep());
                assertEquals(19, childEvent.getTick());
                assertEquals(11, childEvent.getWaitTicks());
                assertEquals(4, childEvent.getMinimumStep());
                assertEquals(fromId, fromEvent.getFrom());
                assertEquals(foodId, fromEvent.getTarget());
                assertEquals(fromId, childEvent.getFrom());
                assertEquals(foodId, childEvent.getTarget());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesProposeEventProgress() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_propose_event", ".sav").toFile();
            try {
                Reimu from = new Reimu();
                Reimu to = new Reimu();
                SimYukkuri.world.getCurrentMap().getBody().put(from.getUniqueID(), from);
                SimYukkuri.world.getCurrentMap().getBody().put(to.getUniqueID(), to);

                ProposeEvent event = new ProposeEvent(from, to, null, 10);
                event.setTick(21);
                event.setStarted(true);
                from.setCurrentEvent(event);
                to.setCurrentEvent(event);

                int fromId = from.getUniqueID();
                int toId = to.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredFrom = findBodyAcrossMaps(fromId);
                Yukkuri restoredTo = findBodyAcrossMaps(toId);

                assertNotNull(restoredFrom);
                assertNotNull(restoredTo);
                assertTrue(restoredFrom.getCurrentEvent() instanceof ProposeEvent);
                assertTrue(restoredTo.getCurrentEvent() instanceof ProposeEvent);

                EventPacket fromEvent = restoredFrom.getCurrentEvent();
                EventPacket toEvent = restoredTo.getCurrentEvent();
                assertEquals(fromId, fromEvent.getFrom());
                assertEquals(toId, fromEvent.getTo());
                assertEquals(fromId, toEvent.getFrom());
                assertEquals(toId, toEvent.getTo());

                ProposeEvent restoredFromEvent = (ProposeEvent) fromEvent;
                ProposeEvent restoredToEvent = (ProposeEvent) toEvent;
                assertEquals(21, restoredFromEvent.getTick());
                assertEquals(21, restoredToEvent.getTick());
                assertTrue(restoredFromEvent.isStarted());
                assertTrue(restoredToEvent.isStarted());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesProudChildEventProgressFields() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_proud_child_event", ".sav").toFile();
            try {
                Reimu from = new Reimu();
                Marisa child = new Marisa();
                child.setAgeState(AgeState.BABY);
                child.setParents(new int[] { from.getUniqueID(), -1 });

                SimYukkuri.world.getCurrentMap().getBody().put(from.getUniqueID(), from);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
                Class<?> stateClass = Class.forName("src.event.ProudChildEvent$STATE");
                java.lang.reflect.Method setState = ProudChildEvent.class.getMethod("setState", stateClass);
                Object proudState = Enum.valueOf((Class) stateClass, "PROUD");
                setState.invoke(event, proudState);
                event.setTick(29);
                event.setActionFlag(false);
                event.setUnunActionFlag(false);
                event.setFromWaitCount(37);
                from.setCurrentEvent(event);
                child.setCurrentEvent(event);

                int fromId = from.getUniqueID();
                int childId = child.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredFrom = findBodyAcrossMaps(fromId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredFrom);
                assertNotNull(restoredChild);
                assertTrue(restoredFrom.getCurrentEvent() instanceof ProudChildEvent);
                assertTrue(restoredChild.getCurrentEvent() instanceof ProudChildEvent);

                ProudChildEvent fromEvent = (ProudChildEvent) restoredFrom.getCurrentEvent();
                ProudChildEvent childEvent = (ProudChildEvent) restoredChild.getCurrentEvent();
                java.lang.reflect.Method getState = ProudChildEvent.class.getMethod("getState");
                assertEquals("PROUD", String.valueOf(getState.invoke(fromEvent)));
                assertEquals("PROUD", String.valueOf(getState.invoke(childEvent)));
                assertEquals(29, fromEvent.getTick());
                assertFalse(fromEvent.isActionFlag());
                assertFalse(fromEvent.isUnunActionFlag());
                assertEquals(37, fromEvent.getFromWaitCount());
                assertEquals(29, childEvent.getTick());
                assertFalse(childEvent.isActionFlag());
                assertFalse(childEvent.isUnunActionFlag());
                assertEquals(37, childEvent.getFromWaitCount());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesShitExercisesEventProgressFields() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_shit_exercises_event", ".sav").toFile();
            try {
                Reimu from = new Reimu();
                Reimu child = new Reimu();
                child.setAgeState(AgeState.BABY);
                child.setParents(new int[] { from.getUniqueID(), -1 });

                SimYukkuri.world.getCurrentMap().getBody().put(from.getUniqueID(), from);
                SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

                ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
                Class<?> stateClass = Class.forName("src.event.ShitExercisesEvent$STATE");
                java.lang.reflect.Method setState = ShitExercisesEvent.class.getMethod("setState", stateClass);
                Object ununState = Enum.valueOf((Class) stateClass, "UNUN");
                setState.invoke(event, ununState);
                event.setTick(39);
                event.setActionFlag(false);
                event.setUnunActionFlag(false);
                event.setFromWaitCount(41);
                from.setCurrentEvent(event);
                child.setCurrentEvent(event);

                int fromId = from.getUniqueID();
                int childId = child.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredFrom = findBodyAcrossMaps(fromId);
                Yukkuri restoredChild = findBodyAcrossMaps(childId);

                assertNotNull(restoredFrom);
                assertNotNull(restoredChild);
                assertTrue(restoredFrom.getCurrentEvent() instanceof ShitExercisesEvent);
                assertTrue(restoredChild.getCurrentEvent() instanceof ShitExercisesEvent);

                ShitExercisesEvent fromEvent = (ShitExercisesEvent) restoredFrom.getCurrentEvent();
                ShitExercisesEvent childEvent = (ShitExercisesEvent) restoredChild.getCurrentEvent();
                java.lang.reflect.Method getState = ShitExercisesEvent.class.getMethod("getState");
                assertEquals("UNUN", String.valueOf(getState.invoke(fromEvent)));
                assertEquals("UNUN", String.valueOf(getState.invoke(childEvent)));
                assertEquals(39, fromEvent.getTick());
                assertFalse(fromEvent.isActionFlag());
                assertFalse(fromEvent.isUnunActionFlag());
                assertEquals(41, fromEvent.getFromWaitCount());
                assertEquals(39, childEvent.getTick());
                assertFalse(childEvent.isActionFlag());
                assertFalse(childEvent.isUnunActionFlag());
                assertEquals(41, childEvent.getFromWaitCount());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesRaperReactionEventProgressFields() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_raper_reaction_event", ".sav").toFile();
            try {
                Reimu raper = new Reimu();
                Marisa target = new Marisa();

                SimYukkuri.world.getCurrentMap().getBody().put(raper.getUniqueID(), raper);
                SimYukkuri.world.getCurrentMap().getBody().put(target.getUniqueID(), target);

                RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
                event.setState(ActionState.ATTACK);
                event.setAge(27);
                target.setCurrentEvent(event);

                int targetId = target.getUniqueID();
                int raperId = raper.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredTarget = findBodyAcrossMaps(targetId);

                assertNotNull(restoredTarget);
                assertTrue(restoredTarget.getCurrentEvent() instanceof RaperReactionEvent);

                RaperReactionEvent restoredEvent = (RaperReactionEvent) restoredTarget.getCurrentEvent();
                assertEquals(ActionState.ATTACK, restoredEvent.getState());
                assertEquals(27, restoredEvent.getAge());
                assertEquals(raperId, restoredEvent.getFrom());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesBegForLifeEventProgressFields() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_beg_for_life_event", ".sav").toFile();
            try {
                Reimu actor = new Reimu();
                Reimu attacker = new Reimu();

                SimYukkuri.world.getCurrentMap().getBody().put(actor.getUniqueID(), actor);
                SimYukkuri.world.getCurrentMap().getBody().put(attacker.getUniqueID(), attacker);

                BegForLifeEvent event = new BegForLifeEvent(actor, attacker, null, 10);
                event.setRoop(3);
                event.setRoop2(7);
                event.setRoop3(1);
                event.setTick(8);
                event.setWait(50);
                actor.setCurrentEvent(event);

                int actorId = actor.getUniqueID();
                int attackerId = attacker.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredActor = findBodyAcrossMaps(actorId);

                assertNotNull(restoredActor);
                assertTrue(restoredActor.getCurrentEvent() instanceof BegForLifeEvent);

                BegForLifeEvent restoredEvent = (BegForLifeEvent) restoredActor.getCurrentEvent();
                assertEquals(3, restoredEvent.getRoop());
                assertEquals(7, restoredEvent.getRoop2());
                assertEquals(1, restoredEvent.getRoop3());
                assertEquals(8, restoredEvent.getTick());
                assertEquals(50, restoredEvent.getWait());
                assertEquals(actorId, restoredEvent.getFrom());
                assertEquals(attackerId, restoredEvent.getTo());
            } finally {
                tempFile.delete();
            }
        }

        @Test
        void testScenario_SaveLoadPreservesPredatorsGameEventProgressFields() throws Exception {
            File tempFile = Files.createTempFile("simyukkuri_test_save_predators_game_event", ".sav").toFile();
            try {
                Remirya predator = new Remirya();
                Reimu toy = new Reimu();

                SimYukkuri.world.getCurrentMap().getBody().put(predator.getUniqueID(), predator);
                SimYukkuri.world.getCurrentMap().getBody().put(toy.getUniqueID(), toy);

                PredatorsGameEvent event = new PredatorsGameEvent(predator, null, null, 1);
                event.setTick(13);
                event.setTick2(21);
                event.setToy(toy.getUniqueID());
                event.setFlyGame(true);
                event.setGrabbing(true);
                event.setSnack(true);
                predator.setCurrentEvent(event);

                int predatorId = predator.getUniqueID();
                int toyId = toy.getUniqueID();

                roundTripSaveLoad(tempFile);

                Yukkuri restoredPredator = findBodyAcrossMaps(predatorId);

                assertNotNull(restoredPredator);
                assertTrue(restoredPredator.getCurrentEvent() instanceof PredatorsGameEvent);

                PredatorsGameEvent restoredEvent = (PredatorsGameEvent) restoredPredator.getCurrentEvent();
                assertEquals(13, restoredEvent.getTick());
                assertEquals(21, restoredEvent.getTick2());
                assertEquals(toyId, restoredEvent.getToy());
                assertTrue(restoredEvent.isFlyGame());
                assertTrue(restoredEvent.isGrabbing());
                assertTrue(restoredEvent.isSnack());
            } finally {
                tempFile.delete();
            }
        }
    }
}
