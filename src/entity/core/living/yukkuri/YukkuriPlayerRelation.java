package src.entity.core.living.yukkuri;

import src.enums.CriticalDamegeType;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.entity.core.world.item.Sui;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * ゆっくりとプレイヤーのすりすり関係を扱う委譲クラス。
 */
public final class YukkuriPlayerRelation {
	private final Yukkuri body;

	/**
	 * プレイヤーとのすりすり関係を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriPlayerRelation(Yukkuri body) {
		this.body = body;
	}

	/**
	 * プレイヤーにすりすりされたときの処理.
	 *
	 * @return 感情処理を終えるかどうか
	 */
	public boolean doSurisuriByPlayer() {
		// プレイヤーにすりすりされていないなら終了
		if (!body.isSurisuriFromPlayer()) {
			return false;
		}

		boolean foundTarget = false;
		// 初回なら時間を初期化
		if (body.getLastSurisuriTime() == 0) {
			body.setLastSurisuriTime(System.currentTimeMillis());
			foundTarget = true;
		} else {
			// 二回目以降は前回より3秒以上経過してたら処理実行
			long nowTimeMillis = System.currentTimeMillis();
			long elapsedMillis = nowTimeMillis - body.getLastSurisuriTime();
			if (2000 < elapsedMillis) {
				body.setLastSurisuriTime(nowTimeMillis);
				foundTarget = true;
			}
		}

		if (!foundTarget) {
			return false;
		}

		// 動けない場合,パニック中,すぃーに乗っている
		if ((body.isLockmove()) ||
				(body.getPanicType() != null) ||
				(body.isSleeping()) ||
				(body.takeMappedObj(body.getParentLinkId()) instanceof Sui)) {
			return false;
		}

		// -----------------------------------------------------------
		// 処理を分けよう
		// 無反応：ホットプレート、ミキサー、足焼き、寝ている時、すぃーに乗っている、
		// すっきりー：興奮中
		// 痛み：針が刺さっている、足カット、痛みを感じている、瀕死
		// 拒絶：レイパーされている、うんうん中、食事中、出産中、攻撃している、攻撃されている、
		// -----------------------------------------------------------
		if (body.isNYD()) {
			return false;
		}

		// すりすり実行
		// 興奮時
		if (body.isExciting()) {
			// すっきりー
			if (GameRandom.nextInt(5) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Sukkiri), 60, true, false);
				body.setStress(0);
				body.stayPurupuru(60);
				body.setSukkiri(true);
				body.setExciting(false);
				body.setHappiness(Happiness.HAPPY);
				body.clearActions();
				// なつき度設定
				body.addLovePlayer(100);

				// おくるみはいてたら茎が生える
				if (body.isHasPants()) {
					body.dripSperm(body.getDna());
				}
			} else {
				body.stayPurupuru(30);
				// なつき度設定
				body.addLovePlayer(10);
				if (body.isRaper()) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ExciteForRaper));
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Excite));
				}
			}
			body.addMemories(1);
			return true;
		}

		// 切断されている場合
		if ((body.getCriticalDamege() == CriticalDamegeType.CUT) ||
				(body.getFootBakeLevel() == FootBake.CRITICAL) ||
				body.isDamaged() ||
				body.isPealed() ||
				body.isPacked()) {
			body.stayPurupuru(20);
			body.setHappiness(Happiness.VERY_SAD);
			body.addStress(100);
			// なつき度設定
			body.addLovePlayer(-20);
			body.setForceFace(ImageCode.PAIN.ordinal());
			body.clearActions();

			if (GameRandom.nextInt(2) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying2), 30, true, false);
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Dying), 30, true, false);
			}
			return true;
		}

		// 針が刺さっている場合
		if (body.isNeedled()) {
			body.stayPurupuru(40);
			body.setHappiness(Happiness.VERY_SAD);
			body.addStress(50);
			// なつき度設定
			body.addLovePlayer(-20);
			body.setForceFace(ImageCode.PAIN.ordinal());
			// ぐーりぐーりされた時のメッセージ
			if (GameRandom.nextBoolean()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NeedlePain), 60, true, false);
			} else {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NeedlePain), 60, true, true);
			}
			body.clearActions();
			return true;
		}

		// デフォルトすりすり
		body.addStress(-100);
		body.stay(40);
		// なつき度設定
		body.addLovePlayer(10);
		body.setHappiness(Happiness.VERY_HAPPY);
		body.setForceFace(ImageCode.CHEER.ordinal());
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.SuriSuriByPlayer), true);
		body.setNobinobi(true);
		body.addMemories(1);

		int randomIndex = GameRandom.nextInt(5);
		if (randomIndex == 0) {
			body.setForceFace(ImageCode.SMILE.ordinal());
		} else if (0 < randomIndex && randomIndex < 3) {
			body.setForceFace(ImageCode.NORMAL.ordinal());
		} else {
			body.setForceFace(ImageCode.CHEER.ordinal());
		}

		body.clearActions();
		// 低確率で寝る
		if (GameRandom.nextInt(20) == 0) {
			body.forceToSleep();
		}
		return true;
	}
}
