package org.simyukkuri.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.system.ItemMenu;
import org.simyukkuri.system.ItemMenu.GetMenu;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/**
 * 取得メニューの実行.
 */
public final class ItemGetMenuAction implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		GetMenu m = GetMenu.valueOf(e.getActionCommand());
		if (m == null) {
			return;
		}

		WorldState curMap = GameWorld.get().getCurrentWorldState();

		switch (m) {
			case PICKUP:
				synchronized (SimYukkuri.lock) {
					GameWorld.get().getPlayer().getInventoryView().addElement(ItemMenu.getGetTarget());
					if (ItemMenu.getGetTarget() instanceof Yukkuri) {
						Yukkuri b = (Yukkuri) ItemMenu.getGetTarget();
						if (b.getBindStalk() != null) {
							b.detachFromStalk();
						}
						b.removeAllStalks();
						b.setTaken(true);
						curMap.getYukkuriRegistry().remove(b.getUniqueID());
					} else if (ItemMenu.getGetTarget() instanceof Shit) {
						curMap.getShit().remove(ItemMenu.getGetTarget().objId);
					} else if (ItemMenu.getGetTarget() instanceof Vomit) {
						curMap.getVomit().remove(ItemMenu.getGetTarget().objId);
					}
					ItemMenu.setGetTarget(null);
				}
				break;
			case STATUS:
				if (ItemMenu.getGetTarget() == null) {
					return;
				}
				Yukkuri b = (Yukkuri) ItemMenu.getGetTarget();
				ShowStatusFrame instance = ShowStatusFrame.getInstance();
				instance.giveYukkuriInfo(b);
				instance.setVisible(true);
				break;
		}
	}
}
