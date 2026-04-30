package src.draw;

import src.SimYukkuri;
import src.system.MainCommandUI;
import src.system.LoggerYukkuri;
import src.util.GameEnvironment;
import src.util.GameWorld;

/**
 * MyPane の更新ループをまとめる helper.
 */
public final class GameLoop {
	private final MyPane pane;

	public GameLoop(MyPane pane) {
		this.pane = pane;
	}

	public void run() {
		pane.initBodies();
		synchronized (SimYukkuri.lock) {
			SimYukkuri.initialized = true;
		}

		while (pane.isRunning()) {
			int speed;

			synchronized (SimYukkuri.lock) {
				if (GameWorld.get().getNextMap() != -1) {
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
