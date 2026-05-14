package org.simyukkuri.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.simyukkuri.system.ItemMenu.ShapeMenu;

/**
 * シェイプメニューの実行.
 */
public final class ItemShapeMenuAction implements ActionListener {
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
