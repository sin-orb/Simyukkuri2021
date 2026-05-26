package org.simyukkuri.entity.core.living;

import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;

/**
 * ゆっくりのパニック・恐怖反応をまとめた委譲クラス.
 * パニック状態の進行・解除・メッセージ出力を担う。
 */
public final class LivingEntityPanicDelegate {
	private final LivingEntity body;

	/** 指定の LivingEntity をラップしてデリゲートを初期化する。 */
	public LivingEntityPanicDelegate(LivingEntity body) {
		this.body = body;
	}

	/** パニック状態を解除する. */
	public void clearPanic() {
		body.setPanicType(null);
		body.setPanicPeriod(0);
		body.onClearPanic();
	}

	/** パニック・恐怖状態の更新. @return 常に DONOTHING */
	public TickResult checkFear() {
		if (body.isNyd() || body.isUnBirth()) {
			clearPanic();
			return TickResult.NONE;
		}
		if (!body.isDead()) {
			body.messageTicks--;
			if (body.messageTicks <= 0) {
				switch (body.getPanicType()) {
					case FEAR:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Fear));
						break;
					case REMIRYA:
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EscapeFromRemirya));
						break;
					default:
						break;
				}
			}
		}
		if (body.getPanicType() != null && body.getPanicType() != PanicType.BURN) {
			body.setPanicPeriod(body.getPanicPeriod() + LivingEntity.TICK);
		}
		if (body.getPanicPeriod() > 50) {
			clearPanic();
		}
		return TickResult.NONE;
	}
}
