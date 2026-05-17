package org.simyukkuri.ui;

import java.awt.event.KeyEvent;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.draw.Translate;

/**
 * 入力解釈をまとめる helper.
 */
public final class InputController {
	private int savedGameSpeed;

	/**
	 * キー押下時のアクション（ポーズ・ゆっくり操作等）を処理する。
	 *
	 * @param e     キーイベント
	 * @param owner メインウィンドウ
	 */
	public void handleKeyPressed(KeyEvent e, SimYukkuri owner) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			synchronized (SimYukkuri.lock) {
				if (MainCommandUI.getSelectedGameSpeed() != 0) {
					savedGameSpeed = MainCommandUI.getSelectedGameSpeed();
					MainCommandUI.setSelectedGameSpeed(0);
				} else {
					MainCommandUI.setSelectedGameSpeed(savedGameSpeed);
				}
				MainCommandUI.getGameSpeedCombo().setSelectedIndex(MainCommandUI.getSelectedGameSpeed());
			}
			break;
		case KeyEvent.VK_DELETE:
			synchronized (SimYukkuri.lock) {
				GadgetAction.immediateEvaluate(GadgetMenuChoice.ALL);
			}
			break;
		case KeyEvent.VK_Z:
			synchronized (SimYukkuri.lock) {
				if (Translate.addZoomRate(-1)) {
					java.awt.Point mpos = owner.getMousePosition();
					Translate.setBufferZoom();
					if (mpos != null) {
						Translate.transCanvasToField(mpos.x, mpos.x, SimYukkuri.fieldMousePos);
						Translate.setBufferCenterPos(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1]);
					}
				}
			}
			break;
		case KeyEvent.VK_X:
			synchronized (SimYukkuri.lock) {
				if (Translate.addZoomRate(1)) {
					java.awt.Point mpos = owner.getMousePosition();
					Translate.setBufferZoom();
					if (mpos != null) {
						Translate.transCanvasToField(mpos.x, mpos.x, SimYukkuri.fieldMousePos);
						Translate.setBufferCenterPos(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1]);
					}
				}
			}
			break;
		case KeyEvent.VK_C:
			synchronized (SimYukkuri.lock) {
				Translate.setZoomRate(0);
				Translate.setBufferPos(0, 0);
				Translate.setBufferZoom();
			}
			break;
		case KeyEvent.VK_W:
			synchronized (SimYukkuri.lock) {
				Translate.addBufferPos(0, -Translate.getDisplayArea().getHeight() / 3);
			}
			break;
		case KeyEvent.VK_S:
			synchronized (SimYukkuri.lock) {
				Translate.addBufferPos(0, Translate.getDisplayArea().getHeight() / 3);
			}
			break;
		case KeyEvent.VK_A:
			synchronized (SimYukkuri.lock) {
				Translate.addBufferPos(-Translate.getDisplayArea().getWidth() / 3, 0);
			}
			break;
		case KeyEvent.VK_D:
			synchronized (SimYukkuri.lock) {
				Translate.addBufferPos(Translate.getDisplayArea().getWidth() / 3, 0);
			}
			break;
		default:
			break;
		}
	}

	/** @param e キーイベント */
	public void handleKeyReleased(KeyEvent e) {
	}

	/** @param e キーイベント */
	public void handleKeyTyped(KeyEvent e) {
	}
}
