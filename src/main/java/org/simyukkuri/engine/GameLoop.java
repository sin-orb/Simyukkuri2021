package org.simyukkuri.engine;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.ui.MainCommandUI;
import org.simyukkuri.system.LoggerYukkuri;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameWorld;

/**
 * MyPane の更新ループをまとめる helper.
 */
public final class GameLoop {
	private final MyPane pane;

	/**
	 * ゲームループを指定パネルと関連付けて初期化する。
	 *
	 * @param pane ゲーム描画パネル
	 */
	public GameLoop(MyPane pane) {
		this.pane = pane;
	}

	/**
	 * ゲームループを実行する。ゆっくり追加ダイアログを表示後、isRunning が false になるまでゲームを更新し続ける。
	 */
	public void run() {
		pane.initBodies();
		synchronized (SimYukkuri.lock) {
			SimYukkuri.initialized = true;
		}

		while (pane.isRunning()) {
			int speed;

			synchronized (SimYukkuri.lock) {
				if (GameWorld.get().getNextWorldStateIndex() != -1) {
					continue;
				}
				speed = MyPane.gameSpeed[MainCommandUI.getSelectedGameSpeed()];
			}

			if (speed != MyPane.PAUSE) {
				synchronized (SimYukkuri.lock) {
					pane.getTerrarium().stepRun();
				}

				if (pane.getLogOutput() != 0) {
					if (GameEnvironment.getOperationTime() % 10 == 0) {
						LoggerYukkuri.run();
					}
				}
				SimYukkuri.checkMouseVel();
			}
			pane.repaint();
			try {
				if (speed >= 0) {
					Thread.sleep(speed);
				} else {
					Thread.sleep(MyPane.NORMAL);
				}
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
	}
}
