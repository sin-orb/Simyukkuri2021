package src.logic;

import src.base.Body;
import src.enums.EnumRelationMine;
import src.enums.Happiness;

public class EmotionLogic {

	// 相手に対する感情のチェック
	public static boolean[] checkEmotionForOther(Body b, Body bodyTarget)
	{
		//-------------------------------------------------
		// 対象の状態判定
		// 自分がゲスかどうか
		boolean bIsRude = b.isRude();

		// 自分との関係
		EnumRelationMine eRelation = BodyLogic.checkMyRelation(b ,bodyTarget);

		// 自分の幸福度
		Happiness eHappinessMine = b.getHappiness();
		
		// 相手の幸福度
		Happiness eHappinessTarget = bodyTarget.getHappiness();

		boolean[] abEmote = new boolean[7];

		abEmote[0] = false;	// 喜
		abEmote[1] = false;	// 怒
		abEmote[2] = false;	// 哀
		abEmote[3] = false;	// 楽
		abEmote[4] = false;	// 恐怖
		abEmote[5] = false;	// うらやましい
		abEmote[6] = false;	// 心配

		boolean bIsPainOther = false; // 相手が痛がっている
		// 針が刺さっている、足焼き中、切断されている、ダメージをくらっている
		// 感情判定
		switch( eHappinessTarget ){
			//--------------------------------------------------------
			// 相手がとってもしあわせ
			case VERY_HAPPY:
			// 相手がしあわせ
			case HAPPY:
				switch( eHappinessMine )
				{
					case VERY_HAPPY:
					case HAPPY:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
							case PARTNAR: // つがい
							case ELDERSISTER: // 姉
								// おちびちゃんとってもゆっくりしてるよ！まりさもゆっくりしてるよ！いもうちょとってもゆっくりしてるのじぇ
								abEmote[0] = true;	// 喜
								break;
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
							case YOUNGSISTER: // 妹
								// まりちゃもゆっくりしたいのじぇっ！
								abEmote[5] = true;	// うらやましい
								break;
							default : // 他人
								// とってもゆっくりしてるのぜ
								abEmote[3] = true;	// 楽
								break;
						}
						break;
					case AVERAGE:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
							case ELDERSISTER: // 姉
								// おちびちゃんとってもゆっくりしてるよ！いもうちょとってもゆっくりしてるのじぇ
								abEmote[0] = true;	// 喜
								break;
							case PARTNAR: // つがい
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
							case YOUNGSISTER: // 妹
								// まりちゃもゆっくりしたいのじぇっ！
								abEmote[5] = true;	// うらやましい
								break;
							default : // 他人
								// とってもゆっくりしてるのぜ
								abEmote[3] = true;	// 楽
								break;
						}
						break;
					case SAD:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
								// おちびちゃんとってもゆっくりしてるよ！いもうちょとってもゆっくりしてるのじぇ
								abEmote[0] = true;	// 喜
								break;
							case PARTNAR: // つがい
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
							case ELDERSISTER: // 姉
								// まりちゃもゆっくりしたいのじぇっ！
								abEmote[5] = true;	// うらやましい
								break;
							case YOUNGSISTER: // 妹
							default : // 他人
								// まりちゃもゆっくりしたいのじぇっ！
								abEmote[2] = true;	// 哀
								abEmote[5] = true;	// うらやましい
								break;
						}
						break;
					case VERY_SAD:
						// 自分がゲスではない
						if( !bIsRude)
						{
							// まりさもゆっくりしたいんだぜ
							abEmote[2] = true;	// 哀
							abEmote[5] = true;	// うらやましい
						}else{
							//　自分がゲス
							// じぶんだけゆっくりするげすはしね！
							abEmote[1] = true;	// 怒
							abEmote[5] = true;	// うらやましい
						}
						break;
					default :
						break;
				}
				break;
			//--------------------------------------------------------
			// 相手が普通
			case AVERAGE:
				break;
			//--------------------------------------------------------
			// 相手がかなしい
			case SAD:
			//--------------------------------------------------------
			// 相手がとてもかなしい
			case VERY_SAD:
				switch( eHappinessMine )
				{
					case VERY_HAPPY:
					case HAPPY:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
							case PARTNAR: // つがい
							case ELDERSISTER: // 姉
							case YOUNGSISTER: // 妹
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
								// おちびちゃん、ゆっくりしてね！
								abEmote[2] = true;	// 哀
								abEmote[6] = true;	// 心配
								
								// 痛みを伴う場合
								if( bIsPainOther )
								{
									// ゆんやーゆっくりできないーー！
									abEmote[4] = true;	// 恐怖
								}
								break;
							default : // 他人
								//　自分がゲス
								if( bIsRude)
								{
									// ゆっくりしていないゆっくりがいるよ！
									abEmote[0] = true;	// 喜
									abEmote[3] = true;	// 楽
								}
								break;
						}
						break;
					case AVERAGE:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
							case ELDERSISTER: // 姉
							case PARTNAR: // つがい
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
							case YOUNGSISTER: // 妹
								// ゆっくりしてないけどだいじょうぶ？
								abEmote[6] = true;	// 心配
								
								// 痛みを伴う場合
								if( bIsPainOther )
								{
									// ゆっくりにげるよ！
									abEmote[4] = true;	// 恐怖
								}
								break;
							default : // 他人
								//　自分がゲスではない
								if( !bIsRude)
								{
									// 痛みを伴う場合
									if( bIsPainOther )
									{
										// ゆっくりにげるよ！
										abEmote[4] = true;	// 恐怖
									}
								}else{
									// 自分がゲス
									// ゆっくりしていないゆっくりがいるよ！
									abEmote[0] = true;	// 喜
									abEmote[3] = true;	// 楽
								}
								break;
						}
						break;
					case SAD:
					case VERY_SAD:
						switch( eRelation )
						{
							case FATHER: // 父
							case MOTHER: // 母
							case PARTNAR: // つがい
							case ELDERSISTER: // 姉
							case YOUNGSISTER: // 妹
							case CHILD_FATHER: // 父の子供
							case CHILD_MOTHER: // 母の子供
								// おちびちゃん、ゆっくりしてね！
								abEmote[2] = true;	// 哀
								abEmote[6] = true;	// 心配
								
								// 痛みを伴う場合
								if( bIsPainOther )
								{
									// ゆんやーゆっくりできないーー！
									abEmote[4] = true;	// 恐怖
								}
								break;
							default : // 他人
								// ゆっくりしたいだけなのに
								abEmote[2] = true;	// 哀

								// 痛みを伴う場合
								if( bIsPainOther )
								{
									// ゆっくりにげるよ！
									abEmote[4] = true;	// 恐怖
								}
						}
						break;
					default :
						break;
				}
				break;
				//--------------------------------------------------------
			default :// 未使用
				break;
		}
		
		return abEmote;
	}
	
}
