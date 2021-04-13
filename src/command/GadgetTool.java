package src.command;

import src.base.Body;
import src.enums.UnbirthBabyState;
import src.enums.YukkuriType;
import src.yukkuri.Reimu;

/**
 * プレイヤーの起こすアクションをBodyから外だししたクラス.
 */
public class GadgetTool {

	/**
	 * ゆ虐神拳を実行する.
	 * @param b ゆっくりの実体
	 */
	public static void doGodHand(Body b) {
		// 死んでたら何もしない
		if( b.isDead() ){
			return;
		}

		int nRnd = b.RND.nextInt(8);
		switch( nRnd ){
		case 0:
			if( b.judgeCanTransForGodHand() ){
				// 突然変異
				b.execTransform();
				b.kick();
			}
			else{
				// 突然変異できない場合はレイパーをToggle
				b.setRapist(!b.isRapist());
				b.kick();
			}
			// 持ち物を全部落とす
			b.dropAllTakeoutItem();
			break;

		case 1:	// 切断
			b.bodyCut();
			// 持ち物を全部落とす
			b.dropAllTakeoutItem();
			b.kick();
			break;

		case 2:
			// つぶしていたのを引っ張る場合
			if( b.getAbFlagGodHand()[2]  ){
				b.getAnGodHandPoint()[1] = b.getAnGodHandPoint()[2];
			}
			// 引っ張る
			b.getAbFlagGodHand()[1] = true;
			b.getAbFlagGodHand()[2] = false;
			// 実ゆの場合、親が反応する
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;

		case 3:
			// 伸ばしていたのをつぶす場合
			if( b.getAbFlagGodHand()[1] ){
				b.getAnGodHandPoint()[2] = b.getAnGodHandPoint()[1];
			}
			// つぶす
			b.getAbFlagGodHand()[1] = false;
			b.getAbFlagGodHand()[2] = true;
			// 実ゆの場合、親が反応する
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;

		case 4:// 回復
			// 痛めつけてから回復
			// ダメージがある状態から復活した場合のセリフをしゃべる
			b.setDamage(b.getDAMAGELIMIT()[b.getBodyAgeState().ordinal()]/2);
			// 実ゆの場合、親が反応する
			b.checkReactionStalkMother(UnbirthBabyState.HAPPY);
			b.giveJuice();
			break;
		case 5:// 言語破壊
			// れいむの場合
			if( b.getType() == Reimu.type){
				b.setMsgType(YukkuriType.TARINAIREIMU);
			}
			else{
				b.setMsgType(YukkuriType.TARINAI);
			}
			break;
		default:
			b.kick();
			// 持ち物を全部落とす
			b.dropAllTakeoutItem();
			// 二回目なら爆発的拡大
			if( b.getAbFlagGodHand()[0] ){
				b.setShit(b.getSHITLIMIT()[b.getBodyAgeState().ordinal()] * 10);
				b.setAnalClose(true);
			}
			// 膨らむ
			b.getAbFlagGodHand()[0] = true;
			// 実ゆの場合、親が反応する
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;
		}
	}
}
