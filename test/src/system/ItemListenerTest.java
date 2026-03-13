package src.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.draw.World;
import src.enums.AgeState;
import src.game.Shit;
import src.game.Vomit;
import src.system.ItemMenu.GetMenu;
import src.yukkuri.Reimu;

public class ItemListenerTest {

    private ItemListener listener;

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        listener = new ItemListener();

        // Initialize MainCommandUI static components
        MainCommandUI.setGameSpeedCombo(new JComboBox<>(new String[] { "0", "1", "2" }));
        MainCommandUI.setSelectedGameSpeed(1);
    }

    @Test
    public void testGetPopupAction_pausesAndResumesSpeed() {
        ItemListener.GetPopupAction action = listener.new GetPopupAction();

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
        ItemListener.UsePopupAction action = listener.new UsePopupAction();

        MainCommandUI.setSelectedGameSpeed(1);
        MainCommandUI.getGameSpeedCombo().setSelectedIndex(1);

        action.popupMenuWillBecomeVisible(null);
        assertEquals(0, MainCommandUI.getGameSpeedCombo().getSelectedIndex());

        action.popupMenuWillBecomeInvisible(null);
        assertEquals(1, MainCommandUI.getGameSpeedCombo().getSelectedIndex());
    }

    @Test
    public void testGetMenuAction_PICKUP_Body() {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        ItemMenu.setGetTarget(b);

        ItemListener.GetMenuAction action = listener.new GetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getBody().containsKey(b.getUniqueID()));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(b));
        assertTrue(b.isTaken());
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Shit() {
        Shit s = new Shit();
        s.setObjId(src.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentMap().getShit().put(s.objId, s);
        ItemMenu.setGetTarget(s);

        ItemListener.GetMenuAction action = listener.new GetMenuAction();
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

        ItemListener.GetMenuAction action = listener.new GetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());

        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentMap().getVomit().containsKey(v.objId));
        assertTrue(SimYukkuri.world.getPlayer().getItemList().contains(v));
        assertNull(ItemMenu.getGetTarget());
    }
}
