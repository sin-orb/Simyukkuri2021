package src.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.DefaultListModel;

import org.junit.jupiter.api.Test;

import src.base.Obj;

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
        DefaultListModel<Obj> newList = new DefaultListModel<>();
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
        java.util.List<Obj> newList = new java.util.ArrayList<>();
        newList.add(new Player());
        player.setItemForSave(newList);
        assertSame(newList, player.getItemForSave());
    }
}
