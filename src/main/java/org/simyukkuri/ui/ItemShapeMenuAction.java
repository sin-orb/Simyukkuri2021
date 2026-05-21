package org.simyukkuri.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.simyukkuri.system.ItemMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenu;

/**
 * シェイプメニューの実行.
 */
public final class ItemShapeMenuAction implements ActionListener {
	/** @param e アクションイベント */
	@Override
	public void actionPerformed(ActionEvent e) {
		ShapeMenu m = ShapeMenu.valueOf(e.getActionCommand());
		if (m == null) {
			return;
		}

		ItemMenu.getShapeTarget().executeShapePopup(m);
		ItemMenu.setShapeTarget(null);
	}
}
