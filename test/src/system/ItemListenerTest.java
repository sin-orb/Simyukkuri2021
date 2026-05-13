package src.system;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.entity.core.living.yukkuri.Yukkuri;
import src.draw.World;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.mobile.Shit;
import src.entity.core.world.mobile.Vomit;
import src.enums.AgeState;
import src.system.ItemMenu.GetMenu;
import src.util.WorldTestHelper;
import src.entity.core.living.yukkuri.impl.Reimu;

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
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        ItemMenu.setGetTarget(b);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getBody().containsKey(b.getUniqueID()));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(b));
        assertTrue(b.isTaken());
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_BodyDetachesFromStalk() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Stalk stalk = new Stalk();
        stalk.getBindBabies().add(b.getUniqueID());
        b.setBindStalk(stalk);
        b.setParentLinkId(123);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        ItemMenu.setGetTarget(b);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getBody().containsKey(b.getUniqueID()));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(b));
        assertTrue(b.isTaken());
        assertNull(b.getBindStalk());
        assertEquals(-1, b.getParentLinkId());
        assertNull(stalk.getBindBabies().get(0));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Shit() {
        Shit s = new Shit();
        s.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentMap().getShit().put(s.objId, s);
        ItemMenu.setGetTarget(s);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getShit().containsKey(s.objId));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(s));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Vomit() {
        Vomit v = new Vomit();
        v.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentMap().getVomit().put(v.objId, v);
        ItemMenu.setGetTarget(v);

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getVomit().containsKey(v.objId));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(v));
        assertNull(ItemMenu.getGetTarget());
    }
}
