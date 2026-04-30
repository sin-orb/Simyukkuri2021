package src.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.SimYukkuri;
import src.base.Body;
import src.command.ShowStatusFrame;
import src.game.Shit;
import src.game.Vomit;
import src.system.ItemMenu.GetMenu;
import src.system.MapPlaceData;
import src.util.GameWorld;

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

		MapPlaceData curMap = GameWorld.get().getCurrentMap();

		switch (m) {
		case PICKUP:
			synchronized (SimYukkuri.lock) {
				GameWorld.get().getPlayer().getItemList().addElement(ItemMenu.getGetTarget());
				if (ItemMenu.getGetTarget() instanceof Body) {
					Body b = (Body) ItemMenu.getGetTarget();
					b.removeAllStalks();
					b.setTaken(true);
					curMap.getBody().remove(b.getUniqueID());
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
			Body b = (Body) ItemMenu.getGetTarget();
			ShowStatusFrame instance = ShowStatusFrame.getInstance();
			instance.giveBodyInfo(b);
			instance.setVisible(true);
			break;
		}
	}
}
