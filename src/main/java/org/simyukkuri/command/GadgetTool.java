package org.simyukkuri.command;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.UnbirthBabyState;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameRandom;

/**
 * プレイヤーの起こすアクションをBodyから外だししたクラス.
 */
public class GadgetTool {

	/**
	 * ゆ虐神拳を実行する.
	 * 
	 * @param body ゆっくりの実体
	 */
	public static void doGodHand(Yukkuri body) {
		// 死んでたら何もしない
		if (body.isDead()) {
			return;
		}

		switch (GameRandom.nextInt(8)) {
			case 0:
				if (body.judgeCanTransForGodHand()) {
					// 突然変異
					body.execTransform();
					body.kick();
				} else {
					// 突然変異できない場合はレイパーをToggle
					body.setRapist(!body.isRapist());
					body.kick();
				}
				// 持ち物を全部落とす
				body.dropAllTakeoutItem();
				break;

			case 1: // 切断
				body.bodyCut();
				// 持ち物を全部落とす
				body.dropAllTakeoutItem();
				body.kick();
				break;

			case 2:
				// つぶしていたのを引っ張る場合
				if (body.getAbFlagGodHand()[2]) {
					body.setGodHandStretchCount(body.getGodHandCompressCount());
				}
				// 引っ張る
				body.getAbFlagGodHand()[1] = true;
				body.getAbFlagGodHand()[2] = false;
				// 実ゆの場合、親が反応する
				body.checkReactionStalkMother(UnbirthBabyState.SAD);
				break;

			case 3:
				// 伸ばしていたのをつぶす場合
				if (body.getAbFlagGodHand()[1]) {
					body.setGodHandCompressCount(body.getGodHandStretchCount());
				}
				// つぶす
				body.getAbFlagGodHand()[1] = false;
				body.getAbFlagGodHand()[2] = true;
				// 実ゆの場合、親が反応する
				body.checkReactionStalkMother(UnbirthBabyState.SAD);
				break;

			case 4:// 回復
					// 痛めつけてから回復
					// ダメージがある状態から復活した場合のセリフをしゃべる
				body.setDamage(body.getDamageLimitBase()[body.getAgeState().ordinal()] / 2);
				// 実ゆの場合、親が反応する
				body.checkReactionStalkMother(UnbirthBabyState.HAPPY);
				body.giveJuice();
				break;
			case 5:// 言語破壊
					// れいむの場合
				if (body.getType() == YukkuriType.REIMU) {
					body.setMsgType(YukkuriType.TARINAIREIMU);
				} else {
					body.setMsgType(YukkuriType.TARINAI);
				}
				break;
			default:
				body.kick();
				// 持ち物を全部落とす
				body.dropAllTakeoutItem();
				// 二回目なら爆発的拡大
				if (body.getAbFlagGodHand()[0]) {
					body.setShit(body.getShitLimitBase()[body.getAgeState().ordinal()] * 10);
					body.setAnalClose(true);
				}
				// 膨らむ
				body.getAbFlagGodHand()[0] = true;
				// 実ゆの場合、親が反応する
				body.checkReactionStalkMother(UnbirthBabyState.SAD);
				break;
		}
	}
}
