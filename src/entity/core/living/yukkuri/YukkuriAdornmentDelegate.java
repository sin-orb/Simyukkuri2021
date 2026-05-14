package src.entity.core.living.yukkuri;

import src.entity.core.world.bodylinked.Okazari;
import src.entity.core.world.bodylinked.Okazari.OkazariType;
import src.enums.Happiness;
import src.enums.UnbirthBabyState;
import src.system.MessagePool;
import src.util.GameMessages;

/**
 * お飾り・おくるみなどの身体装飾を扱う委譲クラス.
 */
public final class YukkuriAdornmentDelegate {
	private final Yukkuri body;

	/**
	 * 身体装飾の委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriAdornmentDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * お飾りが無いことを認識する.
	 */
	public void noticeNoOkazari() {
		if (body.isDead() || body.isRemoved() || body.isUnBirth()) {
			return;
		}

		if (body.getOkazari() != null || body.isNoticeNoOkazari()) {
			return;
		}

		if (!body.isSleeping()) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoticeNoAccessory), true);
			body.setHappiness(Happiness.VERY_SAD);
			body.addStress(1200);
			body.setNoticeNoOkazari(true);
		}
	}

	/**
	 * お飾りを取られる.
	 *
	 * @param takenByPlayer プレイヤーに取られたかどうか
	 */
	public void takeOkazari(boolean takenByPlayer) {
		body.setOkazari(null);
		if (body.isIdiot()) {
			return;
		}
		if (!body.isDead()) {
			if (!body.isSleeping()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoAccessory), true);
				body.setHappiness(Happiness.VERY_SAD);
				body.addStress(1200);
				if (takenByPlayer) {
					body.addLovePlayer(-100);
				}
				body.setNoticeNoOkazari(true);
			} else {
				body.setNoticeNoOkazari(false);
			}
		}
		body.checkReactionStalkMother(UnbirthBabyState.SAD);
	}

	/**
	 * お飾りを落とす(未使用).
	 */
	public void dropOkazari() {
		if (body.getOkazari() != null) {
			body.getOkazari().setCalcX(body.getX());
			body.getOkazari().setCalcY(body.getY());
			body.getOkazari().setCalcZ(body.getZ() + 10);
			src.util.GameWorld.get().getCurrentMap().getOkazari().put(body.getOkazari().objId, body.getOkazari());
			body.setOkazari(null);
		}
	}

	/**
	 * お飾りをあげたときの反応を記述する.
	 *
	 * @param type お飾りのタイプ
	 */
	public void giveOkazari(OkazariType type) {
		body.setOkazari(new Okazari(body, type));
		body.setNoticeNoOkazari(false);
		if (!body.isDead() && !body.isIdiot()) {
			if (body.getOkazari().getOkazariType() == OkazariType.DEFAULT) {
				body.setHappiness(Happiness.VERY_HAPPY);
				body.addStress(-1250);
				body.addLovePlayer(10);
				body.checkReactionStalkMother(UnbirthBabyState.HAPPY);
			} else {
				body.setHappiness(Happiness.SAD);
				body.addStress(-100);
				body.addLovePlayer(10);
			}
		}
	}

	/**
	 * おくるみをあげる.
	 */
	public void givePants() {
		body.setHasPants(true);
		if (body.canAction()) {
			if (!body.isDirty() && body.hasOkazari()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.RelaxOkurumi), 30);
				body.setHappiness(Happiness.HAPPY);
				body.addStress(-250);
				body.addLovePlayer(100);
				body.checkReactionStalkMother(UnbirthBabyState.HAPPY);
			}
		}
	}
}
