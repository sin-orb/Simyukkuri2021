package src.command;

import src.SimYukkuri;
import src.base.Body;
import src.enums.UnbirthBabyState;
import src.enums.YukkuriType;
import src.yukkuri.Reimu;

/**
 * プレイヤーの起こすアクションをBodyから外だししたクラス.
 */
public class GadgetTool {

	/**
	 * �?虐神拳を実行す�?.
	 * @param b �?っくりの実�?
	 */
	public static void doGodHand(Body b) {
		// 死んでたら何もしな�?
		if( b.isDead() ){
			return;
		}

		switch( SimYukkuri.RND.nextInt(8) ){
		case 0:
			if( b.judgeCanTransForGodHand() ){
				// 突然変異
				b.execTransform();
				b.kick();
			}
			else{
				// 突然変異できな�?場合�?�レイパ�?�をToggle
				b.setRapist(!b.isRapist());
				b.kick();
			}
			// 持ち物を�?�部落と�?
			b.dropAllTakeoutItem();
			break;

		case 1:	// �?断
			b.bodyCut();
			// 持ち物を�?�部落と�?
			b.dropAllTakeoutItem();
			b.kick();
			break;

		case 2:
			// つぶして�?た�?�を引っ張る場�?
			if( b.getAbFlagGodHand()[2]  ){
				b.setGodHandStretchPoint(b.getGodHandCompressPoint());
			}
			// 引っ張�?
			b.getAbFlagGodHand()[1] = true;
			b.getAbFlagGodHand()[2] = false;
			// 実ゆの場合、親が反応す�?
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;

		case 3:
			// 伸ばして�?た�?�をつぶす場�?
			if( b.getAbFlagGodHand()[1] ){
				b.setGodHandCompressPoint(b.getGodHandStretchPoint());
			}
			// つぶ�?
			b.getAbFlagGodHand()[1] = false;
			b.getAbFlagGodHand()[2] = true;
			// 実ゆの場合、親が反応す�?
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;

		case 4:// 回復
			// 痛めつけてから回復
			// ダメージがある状態から復活した場合�?�セリフをしゃべ�?
			b.setDamage(b.getDAMAGELIMITorg()[b.getBodyAgeState().ordinal()]/2);
			// 実ゆの場合、親が反応す�?
			b.checkReactionStalkMother(UnbirthBabyState.HAPPY);
			b.giveJuice();
			break;
		case 5:// 言語破�?
			// れいむの場�?
			if( b.getType() == Reimu.type){
				b.setMsgType(YukkuriType.TARINAIREIMU);
			}
			else{
				b.setMsgType(YukkuriType.TARINAI);
			}
			break;
		default:
			b.kick();
			// 持ち物を�?�部落と�?
			b.dropAllTakeoutItem();
			// 二回目なら�??発�?拡大
			if( b.getAbFlagGodHand()[0] ){
				b.setShit(b.getSHITLIMITorg()[b.getBodyAgeState().ordinal()] * 10);
				b.setAnalClose(true);
			}
			// 膨ら�?�
			b.getAbFlagGodHand()[0] = true;
			// 実ゆの場合、親が反応す�?
			b.checkReactionStalkMother(UnbirthBabyState.SAD);
			break;
		}
	}
}
