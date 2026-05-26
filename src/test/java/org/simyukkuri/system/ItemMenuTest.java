package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JMenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.WorldTestHelper;

class ItemMenuTest {

    private static final class DummyFieldShape extends FieldShape {
        private static final long serialVersionUID = 1L;
        private final ShapeMenuTarget target;

        private DummyFieldShape(ShapeMenuTarget target) {
            this.target = target;
        }

        @Override
        public int getAttribute() {
            return 0;
        }

        @Override
        public int getMinimumSize() {
            return 1;
        }

        @Override
        public void drawShape(java.awt.Graphics2D g2) {
            // no-op
        }

        @Override
        public ShapeMenuTarget hasShapePopup() {
            return target;
        }
    }

    private static final class DummyEntity extends Entity {
        private static final long serialVersionUID = 1L;
        private final GetMenuTarget getTarget;
        private final UseMenuTarget useTarget;

        private DummyEntity(GetMenuTarget getTarget, UseMenuTarget useTarget) {
            this.getTarget = getTarget;
            this.useTarget = useTarget;
        }

        @Override
        public GetMenuTarget hasGetPopup() {
            return getTarget;
        }

        @Override
        public UseMenuTarget hasUsePopup() {
            return useTarget;
        }
    }

    @BeforeEach
    void setUp() {
        ItemMenu.createPopupMenu();
    }

    @Test
    void testGetMenuTargetEnumValues() {
        assertFalse(GetMenuTarget.NONE.canPickup());
        assertFalse(GetMenuTarget.NONE.canStatus());
        assertFalse(GetMenuTarget.NONE.canDebug());

        assertTrue(GetMenuTarget.BODY.canPickup());
        assertTrue(GetMenuTarget.BODY.canStatus());
        assertTrue(GetMenuTarget.BODY.canDebug());

        assertTrue(GetMenuTarget.SHIT.canPickup());
        assertFalse(GetMenuTarget.SHIT.canStatus());
        assertTrue(GetMenuTarget.SHIT.canDebug());

        assertTrue(GetMenuTarget.VOMIT.canPickup());
        assertFalse(GetMenuTarget.VOMIT.canStatus());
        assertTrue(GetMenuTarget.VOMIT.canDebug());

        assertTrue(GetMenuTarget.FOOD.canPickup());
        assertFalse(GetMenuTarget.FOOD.canStatus());
        assertTrue(GetMenuTarget.FOOD.canDebug());

        assertFalse(GetMenuTarget.STALK.canPickup());
        assertFalse(GetMenuTarget.STALK.canStatus());
        assertTrue(GetMenuTarget.STALK.canDebug());
    }

    @Test
    void testUseMenuTargetEnumValues() {
        assertEquals(3, UseMenuTarget.values().length);
        assertEquals(UseMenuTarget.NONE, UseMenuTarget.values()[0]);
        assertEquals(UseMenuTarget.BODY, UseMenuTarget.values()[1]);
        assertEquals(UseMenuTarget.SHIT, UseMenuTarget.values()[2]);
    }

    @Test
    void testShapeMenuTargetEnumValues() {
        assertFalse(ShapeMenuTarget.NONE.canSetup());
        assertFalse(ShapeMenuTarget.NONE.canSort());
        assertFalse(ShapeMenuTarget.NONE.isFarm());

        assertTrue(ShapeMenuTarget.BELT.canSetup());
        assertTrue(ShapeMenuTarget.BELT.canSort());
        assertFalse(ShapeMenuTarget.BELT.isFarm());

        assertFalse(ShapeMenuTarget.POOL.canSetup());
        assertTrue(ShapeMenuTarget.POOL.canSort());
        assertFalse(ShapeMenuTarget.POOL.isFarm());

        assertFalse(ShapeMenuTarget.FARM.canSetup());
        assertTrue(ShapeMenuTarget.FARM.canSort());
        assertTrue(ShapeMenuTarget.FARM.isFarm());
    }

    @Test
    void testCreatePopupMenuBuildsCategories() {
        assertNotNull(ItemMenu.getGetPopup());
        assertNotNull(ItemMenu.getUsePopup());
        assertNotNull(ItemMenu.getShapePopup());

        assertEquals(ItemMenu.GetMenu.values().length, ItemMenu.getGetPopup().getComponentCount());
        assertEquals(ItemMenu.UseMenu.values().length, ItemMenu.getUsePopup().getComponentCount());
        assertEquals(ItemMenu.ShapeMenu.values().length, ItemMenu.getShapePopup().getComponentCount());

        for (int i = 0; i < ItemMenu.GetMenu.values().length; i++) {
            JMenuItem item = (JMenuItem) ItemMenu.getGetPopup().getComponent(i);
            assertEquals(ItemMenu.GetMenu.values()[i].name(), item.getActionCommand());
            assertEquals(ItemMenu.GetMenu.values()[i].toString(), item.getText());
        }
        for (int i = 0; i < ItemMenu.UseMenu.values().length; i++) {
            JMenuItem item = (JMenuItem) ItemMenu.getUsePopup().getComponent(i);
            assertEquals(ItemMenu.UseMenu.values()[i].name(), item.getActionCommand());
            assertEquals(ItemMenu.UseMenu.values()[i].toString(), item.getText());
        }
        for (int i = 0; i < ItemMenu.ShapeMenu.values().length; i++) {
            JMenuItem item = (JMenuItem) ItemMenu.getShapePopup().getComponent(i);
            assertEquals(ItemMenu.ShapeMenu.values()[i].name(), item.getActionCommand());
            assertEquals(ItemMenu.ShapeMenu.values()[i].toString(), item.getText());
        }
    }

    @Test
    void testSetGetPopupMenuEnablesExpectedActions() {
        ItemMenu.setGetPopupMenu(new DummyEntity(GetMenuTarget.BODY, UseMenuTarget.NONE));
        JMenuItem pickup = (JMenuItem) ItemMenu.getGetPopup().getComponent(0);
        JMenuItem status = (JMenuItem) ItemMenu.getGetPopup().getComponent(1);
        assertTrue(pickup.isEnabled());
        assertTrue(status.isEnabled());

        ItemMenu.setGetPopupMenu(new DummyEntity(GetMenuTarget.NONE, UseMenuTarget.NONE));
        assertFalse(pickup.isEnabled());
        assertFalse(status.isEnabled());
    }

    @Test
    void testSetShapePopupMenuConfiguresVisibilityAndEnablement() {
        ItemMenu.setShapePopupMenu(new DummyFieldShape(ShapeMenuTarget.BELT));
        JMenuItem setup = (JMenuItem) ItemMenu.getShapePopup().getComponent(0);
        JMenuItem harvest = (JMenuItem) ItemMenu.getShapePopup().getComponent(1);
        JMenuItem sortTop = (JMenuItem) ItemMenu.getShapePopup().getComponent(2);
        assertTrue(setup.isEnabled());
        assertFalse(harvest.isVisible());
        assertTrue(sortTop.isEnabled());

        ItemMenu.setShapePopupMenu(new DummyFieldShape(ShapeMenuTarget.FARM));
        assertFalse(setup.isEnabled());
        assertTrue(harvest.isVisible());
        assertTrue(sortTop.isEnabled());
    }

    @Test
    void testItemModeCancelClearsHeldItemWhenRequested() {
        WorldTestHelper.initializeMinimalWorld();
        Entity held = new DummyEntity(GetMenuTarget.BODY, UseMenuTarget.BODY);
        GameWorld.get().getPlayer().setHoldItem(held);

        ItemMenu.itemModeCancel(true);

        assertNull(GameWorld.get().getPlayer().getHoldItem());
        assertFalse(ItemMenu.getGetPopup().isVisible());
        assertFalse(ItemMenu.getUsePopup().isVisible());
        assertFalse(ItemMenu.getShapePopup().isVisible());
    }

    @Test
    void testItemModeCancelKeepsHeldItemWhenNotRequested() {
        WorldTestHelper.initializeMinimalWorld();
        Entity held = new DummyEntity(GetMenuTarget.BODY, UseMenuTarget.BODY);
        GameWorld.get().getPlayer().setHoldItem(held);

        ItemMenu.itemModeCancel(false);

        assertSame(held, GameWorld.get().getPlayer().getHoldItem());
        assertFalse(ItemMenu.getGetPopup().isVisible());
        assertFalse(ItemMenu.getUsePopup().isVisible());
        assertFalse(ItemMenu.getShapePopup().isVisible());
    }
}
