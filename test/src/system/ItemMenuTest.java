package src.system;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JPopupMenu;

import org.junit.jupiter.api.Test;

public class ItemMenuTest {

    // --- GetMenuTarget enum ---

    @Test
    public void testGetMenuTargetEnum_values() {
        ItemMenu.GetMenuTarget[] targets = ItemMenu.GetMenuTarget.values();
        assertTrue(targets.length > 0);
        for (ItemMenu.GetMenuTarget t : targets) {
            assertNotNull(t.name());
            t.canPickup();
            t.canStatus();
            t.canDebug();
        }
    }

    // --- UseMenuTarget enum ---

    @Test
    public void testUseMenuTargetEnum_values() {
        ItemMenu.UseMenuTarget[] targets = ItemMenu.UseMenuTarget.values();
        for (ItemMenu.UseMenuTarget t : targets) {
            assertNotNull(t.name());
        }
    }

    // --- ShapeMenuTarget enum ---

    @Test
    public void testShapeMenuTargetEnum_values() {
        ItemMenu.ShapeMenuTarget[] targets = ItemMenu.ShapeMenuTarget.values();
        assertTrue(targets.length > 0);
        for (ItemMenu.ShapeMenuTarget t : targets) {
            assertNotNull(t.name());
            t.canSetup();
            t.canSort();
            t.isFarm();
        }
    }

    // --- GetMenu enum ---

    @Test
    public void testGetMenuEnum_values() {
        ItemMenu.GetMenu[] menus = ItemMenu.GetMenu.values();
        for (ItemMenu.GetMenu m : menus) {
            assertNotNull(m.name());
            assertNotNull(m.getName());
            assertNotNull(m.toString());
        }
    }

    // --- UseMenu enum ---

    @Test
    public void testUseMenuEnum_values() {
        ItemMenu.UseMenu[] menus = ItemMenu.UseMenu.values();
        for (ItemMenu.UseMenu m : menus) {
            assertNotNull(m.name());
            assertNotNull(m.getName());
            assertNotNull(m.toString());
        }
    }

    // --- ShapeMenu enum ---

    @Test
    public void testShapeMenuEnum_values() {
        ItemMenu.ShapeMenu[] menus = ItemMenu.ShapeMenu.values();
        for (ItemMenu.ShapeMenu m : menus) {
            assertNotNull(m.name());
            assertNotNull(m.getName());
            assertNotNull(m.toString());
        }
    }

    // --- getGetPopup / setGetPopup ---

    @Test
    public void testGetSetGetPopup() {
        JPopupMenu popup = new JPopupMenu();
        ItemMenu.setGetPopup(popup);
        assertSame(popup, ItemMenu.getGetPopup());
    }

    // --- getGetTarget / setGetTarget ---

    @Test
    public void testGetSetGetTarget() {
        ItemMenu.setGetTarget(null);
        assertNull(ItemMenu.getGetTarget());
    }

    // --- getUsePopup / setUsePopup ---

    @Test
    public void testGetSetUsePopup() {
        JPopupMenu popup = new JPopupMenu();
        ItemMenu.setUsePopup(popup);
        assertSame(popup, ItemMenu.getUsePopup());
    }

    // --- getUseTarget / setUseTarget ---

    @Test
    public void testGetSetUseTarget() {
        ItemMenu.setUseTarget(null);
        assertNull(ItemMenu.getUseTarget());
    }

    // --- getShapePopup / setShapePopup ---

    @Test
    public void testGetSetShapePopup() {
        JPopupMenu popup = new JPopupMenu();
        ItemMenu.setShapePopup(popup);
        assertSame(popup, ItemMenu.getShapePopup());
    }

    // --- getShapeTarget / setShapeTarget ---

    @Test
    public void testGetSetShapeTarget() {
        ItemMenu.setShapeTarget(null);
        assertNull(ItemMenu.getShapeTarget());
    }

    // --- constructor ---

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new ItemMenu());
    }
}
