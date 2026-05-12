package src.game;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.DefaultListModel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.Entity;
import src.entity.core.meta.Player;
import src.util.WorldTestHelper;

class PlayerTest {

    @Test
    void testConstructor() {
        Player player = new Player();
        assertEquals(10000, player.getCash());
        assertNotNull(player.getItemList());
        assertNull(player.getHoldItem());
        assertNotNull(player.getItemForSave());
    }

    @Test
    void testCashOperations() {
        Player player = new Player();
        player.setCash(5000);
        assertEquals(5000, player.getCash());

        player.addCash(100);
        assertEquals(5100, player.getCash());

        player.addCash(-200);
        assertEquals(4900, player.getCash());
    }

    @Test
    void testItemList() {
        Player player = new Player();
        DefaultListModel<Entity> newList = new DefaultListModel<>();
        player.setItemList(newList);
        assertSame(newList, player.getItemList());
    }

    @Test
    void testHoldItem() {
        Player player = new Player();
        player.setHoldItem(null);
        assertNull(player.getHoldItem());

        Player dummyItem = new Player();
        player.setHoldItem(dummyItem);
        assertSame(dummyItem, player.getHoldItem());
    }

    @Test
    void testSetItemForSave() {
        Player player = new Player();
        java.util.List<Entity> newList = new java.util.ArrayList<>();
        newList.add(new Player());
        player.setItemForSave(newList);
        assertSame(newList, player.getItemForSave());
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_AddCashStillSucceedsWhenWorldExistsButPlayerStatusUiIsNotInitialized() {
            WorldTestHelper.resetWorld();
            WorldTestHelper.initializeMinimalWorld();
            Player player = SimYukkuri.world.getPlayer();
            long before = player.getCash();

            assertDoesNotThrow(() -> player.addCash(321));

            assertEquals(before + 321, player.getCash());
        }
    }
}
