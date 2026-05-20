package org.simyukkuri.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.system.ItemMenu;
import org.simyukkuri.system.ItemMenu.GetMenu;
import org.simyukkuri.util.WorldTestHelper;

public class ItemListenerTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeMainCommandUITestState();
    }

    @Test
    public void testGetPopupAction_pausesAndResumesSpeed() {
        ItemPopupSpeedAction action = new ItemPopupSpeedAction();

        // Initial speed
        MainCommandUI.setSelectedGameSpeed(2);
        MainCommandUI.getGameSpeedCombo().setSelectedIndex(2);

        // Popup visible -> speed should be 0
        action.popupMenuWillBecomeVisible(null);
        assertEquals(0, MainCommandUI.getGameSpeedCombo().getSelectedIndex());

        // Popup invisible -> speed should be restored to 2
        action.popupMenuWillBecomeInvisible(null);
        assertEquals(2, MainCommandUI.getGameSpeedCombo().getSelectedIndex());
        assertEquals(2, MainCommandUI.getSelectedGameSpeed());
    }

    @Test
    public void testUsePopupAction_pausesAndResumesSpeed() {
        ItemPopupSpeedAction action = new ItemPopupSpeedAction();

        MainCommandUI.setSelectedGameSpeed(1);
        MainCommandUI.getGameSpeedCombo().setSelectedIndex(1);

        action.popupMenuWillBecomeVisible(null);
        assertEquals(0, MainCommandUI.getGameSpeedCombo().getSelectedIndex());

        action.popupMenuWillBecomeInvisible(null);
        assertEquals(1, MainCommandUI.getGameSpeedCombo().getSelectedIndex());
    }

    @Test
    public void testGetMenuAction_PICKUP_Body() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
        ItemMenu.setGetTarget(b);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueID()));
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(b));
        assertTrue(b.isTaken());
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_BodyDetachesFromStalk() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Stalk stalk = new Stalk();
        stalk.getAttachedBabyIds().add(b.getUniqueID());
        b.setBindStalk(stalk);
        b.setParentLinkId(123);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
        ItemMenu.setGetTarget(b);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueID()));
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(b));
        assertTrue(b.isTaken());
        assertNull(b.getBindStalk());
        assertEquals(-1, b.getParentLinkId());
        assertNull(stalk.getAttachedBabyIds().get(0));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Shit() {
        Shit s = new Shit();
        s.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentWorldState().getShit().put(s.objId, s);
        ItemMenu.setGetTarget(s);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getShit().containsKey(s.objId));
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(s));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Vomit() {
        Vomit v = new Vomit();
        v.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentWorldState().getVomit().put(v.objId, v);
        ItemMenu.setGetTarget(v);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getVomit().containsKey(v.objId));
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(v));
        assertNull(ItemMenu.getGetTarget());
    }
}
