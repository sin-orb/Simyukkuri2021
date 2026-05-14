package src.entity.core.living;

import src.enums.Event;
import src.enums.PanicType;
import src.system.MessagePool;
import src.util.GameMessages;

/**
 * ゆっくりのパニック・恐怖反応をまとめた委譲クラス.
 * パニック状態の進行・解除・メッセージ出力を担う。
 */
public final class LivingPanicDelegate {
	private final LivingEntity body;

	public LivingPanicDelegate(LivingEntity body) {
		this.body = body;
	}

	/** パニック状態を解除する. */
	public void clearPanic() {
		body.setPanicType(null);
		body.setPanicPeriod(0);
		body.onClearPanic();
	}

	/** パニック・恐怖状態の更新. @return 常に DONOTHING */
	public Event checkFear() {
		if (body.isNYD() || body.isUnBirth()) {
			clearPanic();
			return Event.DONOTHING;
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
			body.panicPeriod += LivingEntity.TICK;
		}
		if (body.panicPeriod > 50) {
			clearPanic();
		}
		return Event.DONOTHING;
	}
}
