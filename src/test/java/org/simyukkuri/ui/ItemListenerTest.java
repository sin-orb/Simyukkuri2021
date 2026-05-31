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
        WorldTestHelper.initializeMainCommandUiTestState();
    }

    @Test
    public void testGetPopupAction_pausesAndResumesSpeed() {
        ItemPopupSpeedAction action = new ItemPopupSpeedAction();

        // Initial speed
        MainCommandUi.setSelectedGameSpeed(2);
        MainCommandUi.getGameSpeedCombo().setSelectedIndex(2);

        // Popup visible -> speed should be 0
        action.popupMenuWillBecomeVisible(null);
        assertEquals(0, MainCommandUi.getGameSpeedCombo().getSelectedIndex());

        // Popup invisible -> speed should be restored to 2
        action.popupMenuWillBecomeInvisible(null);
        assertEquals(2, MainCommandUi.getGameSpeedCombo().getSelectedIndex());
        assertEquals(2, MainCommandUi.getSelectedGameSpeed());
    }

    @Test
    public void testUsePopupAction_pausesAndResumesSpeed() {
        ItemPopupSpeedAction action = new ItemPopupSpeedAction();

        MainCommandUi.setSelectedGameSpeed(1);
        MainCommandUi.getGameSpeedCombo().setSelectedIndex(1);

        action.popupMenuWillBecomeVisible(null);
        assertEquals(0, MainCommandUi.getGameSpeedCombo().getSelectedIndex(),
                "popup visible でスピードが 0 になること");

        action.popupMenuWillBecomeInvisible(null);
        assertEquals(1, MainCommandUi.getGameSpeedCombo().getSelectedIndex(),
                "popup invisible でスピードが元の index に戻ること");
        assertEquals(1, MainCommandUi.getSelectedGameSpeed(),
                "popup invisible で selectedGameSpeed も元の値に戻ること");
    }

    @Test
    public void testGetMenuAction_PICKUP_Body() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        ItemMenu.setGetTarget(b);

        // pickup 前の状態確認
        assertTrue(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueId()),
                "pickup 前は registry に存在すること");

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());
        action.actionPerformed(e);

        // pickup 後の状態確認
        assertFalse(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueId()),
                "pickup 後は registry から除去されること");
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(b),
                "pickup 後は inventory に追加されること");
        assertTrue(b.isTaken(), "pickup 後は isTaken=true");
        assertNull(ItemMenu.getGetTarget(), "pickup 後は getTarget が null になること");
    }

    @Test
    public void testGetMenuAction_PICKUP_BodyDetachesFromStalk() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Stalk stalk = new Stalk();
        stalk.getAttachedBabyIds().add(b.getUniqueId());
        b.setBindStalk(stalk);
        b.setParentLinkId(123);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        ItemMenu.setGetTarget(b);

        // pickup 前の状態確認
        assertTrue(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueId()),
                "pickup 前は registry に存在すること");

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());
        action.actionPerformed(e);

        // pickup 後の状態確認
        assertFalse(SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().containsKey(b.getUniqueId()),
                "pickup 後は registry から除去されること");
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(b));
        assertTrue(b.isTaken());
        assertNull(b.getBindStalk(), "pickup 後は bindStalk が null になること");
        assertEquals(-1, b.getParentLinkId(), "pickup 後は parentLinkId が -1 になること");
        assertNull(stalk.getAttachedBabyIds().get(0));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Shit() {
        Shit s = new Shit();
        s.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentWorldState().getShit().put(s.objId, s);
        ItemMenu.setGetTarget(s);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getShit().containsKey(s.objId),
                "pickup 前は shit map に存在すること");

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());
        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getShit().containsKey(s.objId),
                "pickup 後は shit map から除去されること");
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(s));
        assertNull(ItemMenu.getGetTarget());
    }

    @Test
    public void testGetMenuAction_PICKUP_Vomit() {
        Vomit v = new Vomit();
        v.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        SimYukkuri.world.getCurrentWorldState().getVomit().put(v.objId, v);
        ItemMenu.setGetTarget(v);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getVomit().containsKey(v.objId),
                "pickup 前は vomit map に存在すること");

        ItemGetMenuAction action = new ItemGetMenuAction();
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GetMenu.PICKUP.name());
        action.actionPerformed(e);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getVomit().containsKey(v.objId),
                "pickup 後は vomit map から除去されること");
        assertTrue(SimYukkuri.world.getPlayer().getInventoryView().contains(v));
        assertNull(ItemMenu.getGetTarget());
    }
}
