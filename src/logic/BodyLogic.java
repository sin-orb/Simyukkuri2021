package src.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.attachment.Ants;
import src.attachment.Fire;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventPacket.EventPriority;
import src.base.Obj;
import src.base.Okazari.OkazariType;
import src.draw.Point4y;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.Direction;
import src.enums.EnumRelationMine;
import src.enums.GatheringDirection;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PanicType;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.event.AvoidMoldEvent;
import src.event.FuneralEvent;
import src.event.HateNoOkazariEvent;
import src.event.KillPredeatorEvent;
import src.event.ProposeEvent;
import src.item.Barrier;
import src.item.Toilet;
import src.system.MessagePool;
import src.util.YukkuriUtil;
import src.yukkuri.Fran;
import src.yukkuri.HybridYukkuri;
import src.yukkuri.Meirin;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;


/***************************************************
 * ゆっくり同士の処理
 */
public class BodyLogic {

	public static enum eActionGo {
		NONE, WAIT, GO, BACK
	};

	/**
	 * 自分が相手にとって何なのか判定
	 * @param me 自分
	 * @param you 相手
	 * @return 関係性
	 */
	public static final EnumRelationMine checkMyRelation(Body me, Body you) {
		// 父
		if (me.isFather(you)) {
			return EnumRelationMine.FATHER;
		}

		// 母
		if (me.isMother(you)) {
			return EnumRelationMine.MOTHER;
		}

		// つがい
		if (me.isPartner(you)) {
			return EnumRelationMine.PARTNAR;
		}

		// 父の子供
		if (you.isFather(me)) {
			return EnumRelationMine.CHILD_FATHER;
		}

		// 母の子供
		if (you.isMother(me)) {
			return EnumRelationMine.CHILD_MOTHER;
		}

		// 姉
		if ((me.isElderSister(you))) {
			return EnumRelationMine.ELDERSISTER;
		}

		// 妹
		if ((!me.isElderSister(you)) && me.isSister(you)) {
			return EnumRelationMine.YOUNGSISTER;
		}
		return EnumRelationMine.OTHER;
	}

	/**
	 * 行動トリガーと、移動先決定
	 * @param b ゆっくり
	 * @return 処理を行ったかどうか
	 */
	public static final boolean checkPartner(Body b) {
		// 他の用事がある場合等
		if (b.isToFood() ||  b.isToBed() || b.isToShit()) {
			return false;
		}
		if ((!b.isExciting() && !b.isRude() && b.wantToShit()) || b.nearToBirth()) {
			return false;
		}
		// 非ゆっくり症ならなにもしない
		if (b.isNYD()) {
			return false;
		}
		// 自分がイベント中なら実行しない
		if (b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPriority.LOW) {
			return false;
		}
		// うんうんを持っている場合
		if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
			if (b.isExciting()) {
				// 興奮している場合は捨てて行動する
				b.dropTakeoutItem(TakeoutItemType.SHIT);
			} else {
				return false;
			}
		}

		//初期値
		boolean ret = false;
		Body found = null;
		int minDistance = b.getEYESIGHTorg();
		int secondMinDistance = b.getEYESIGHTorg();
		/////////////////////////////////
		// 行動判定
		/////////////////////////////////

		// 対象が決まっていたら到達したかチェック
		Obj target = b.takeMappedObj(b.getMoveTarget());
		if ((b.isToBody() || b.isToSukkiri() || b.isToSteal()) && target instanceof Body) {
			Body p = (Body) target;
			found = p;
			// 壁の向こうに移動していたらリセット
			int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance > dist) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					found = null;
				}
			}
			if (found != null) {
				// 接近していた場合、相手に対して行動を実施
				return doActionOther(found, b);
			}
		}

		/////////////////////////////////
		// 移動判定
		/////////////////////////////////
		Body bodyHasOkazari = null;
		Body bodyHasOkazariAndPherommone = null;
		Body bodyHasPheromone = null;
		Obj oMoveTarget = b.takeMoveTarget();
		Body bodyOldMoveTarget = null;
		if (oMoveTarget instanceof Body) {
			bodyOldMoveTarget = (Body) oMoveTarget;
		}

		//発情時
		// レイパーですっきり中なら続けて同ターゲットに
		Body pa = YukkuriUtil.getBodyInstance(b.getPartner());
		if (b.isExciting() && b.isRaper() && b.isToSukkiri() && bodyOldMoveTarget != null
				&& !bodyOldMoveTarget.isRaper()) {
			found = (Body) b.takeMoveTarget();
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		}
		//つがいが既にいるなら優先して向かう
		else if (b.isExciting() && pa != null &&
				!(pa.isDead()) && !b.isRaper()) {
			if (b.getPublicRank() == pa.getPublicRank()) {
				found = pa;
				minDistance = Translate.distance(b.getX(), b.getY(), pa.getX(), pa.getY());
			}
		}

		//自分が泣き叫んでいるなら、他ゆに目をくれない。
		else if (b.isCallingParents()) {
			checkNearParent(b);
			return false;
		} else {
			// 全ゆっくりに対してチェック
			for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
				Body p = entry.getValue();
				// 自分同士のチェックは無意味なのでスキップ
				if (p == b)
					continue;
				// 最小距離のものが見つかっていたら
				if (minDistance < 1 && !p.isbPheromone()) {
					continue;
				}
				// 相手が浮いてて自分が飛べないならスキップ
				if (!b.canflyCheck() && p.getZ() != 0)
					continue;

				//以下、 捕食種が通常種に近づいた場合。
				//捕食種がパックされてたらおびえない
				if (p.isPacked()) {
				}
				// さくや、めーりんはれみりゃやふらんにおびえない
				else if ((b.getType() == Sakuya.type || b.getType() == Meirin.type)
						&& (p.getType() == Remirya.type || p.getType() == Fran.type)) {
				}
				//家族もおびえない
				else if (p.isFamily(b)) {
				}
				// 捕食防止
				else if (Terrarium.predatorSteam) {
				} else {
					//捕食種はあっちいってね！イベントで攻撃のときはその個体は怯えない
					if (b.getCurrentEvent() != null && b.getCurrentEvent().getClass().equals(KillPredeatorEvent.class)
							&& b.isAdult() && b.isNotNYD() && !b.isPacked() && !b.isBurned()
							&& !b.isHasBaby() && !b.isHasStalk() ){
						b.setPanic(false, null);
						b.setAngry();
					} else {
						// 捕食種から逃げる
						int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
						if (p.isPredatorType() && dist <= b.getEYESIGHTorg() && b.getPanicType() == null) {
							if (b.canAction() && !b.isPredatorType() && !p.isFamily(b) && !b.isSleeping()) {
								// 最高高度の半分以下または相手が飛べるなら相手が認識
								if (p.getZ() < Translate.getFlyHeightLimit() || b.canflyCheck()) {
									if (!Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
											Barrier.MAP_BODY[AgeState.ADULT.ordinal()] + Barrier.BARRIER_KEKKAI)) {
										if (b.isNotNYD() && !b.isNeedled() && !b.isRaper()) {
											b.setPanic(true, PanicType.REMIRYA);
										}
									}
								}
							}
						}
					}
				}

				//発情時
				if (b.isExciting()) {
					// 埋まっていたら無視
					if (p.getBaryState() != BaryInUGState.NONE) {
						continue;
					}
					// 燃えているのも無視
					if (p.getAttachmentSize(Fire.class) != 0) {
						continue;
					}
					//饅頭も無視
					if (p.isPacked()) {
						continue;
					}
					//れいぱーの時
					if (b.isRaper()) {
						//レイパー以外を狙う
						if ((p.isDead() && p.isCrushed()) || p.isUnBirth() || p.isRaper()) {
							continue;
						}
					}
					// 自分が通常の発情の場合
					else {
						// 死体は無視
						if (p.isDead()) {
							continue;
						}
						// 強制発情状態ではない場合
						if (!b.isForceExciting()) {
							//うんうん奴隷ならうんうん奴隷同士で、そうでないならそうでない同士ですっきりする
							if (b.getPublicRank() != p.getPublicRank()) {
								continue;
							}
							//自ゆんより年下と親子はスキップ
							if (b.getBodyAgeState().ordinal() > p.getBodyAgeState().ordinal() || p.isChild(b)
									|| p.isParent(b)) {
								continue;
							}
						}
					}
				} else if (p.isDead() && (!p.hasOkazari() || b.isIdiot())) {
					// 自分が足りないゆで相手がおかざりなしの死体なら食料扱いなのでスキップ
					continue;
				}
				// 相手が発情中のレイパーなら何もしない
				if (p.isRaper() && p.isExciting()) {
					continue;
				}
				// 相手との間に壁があればスキップ
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				// 完全に埋まっていたら無視
				if (p.getBaryState() == BaryInUGState.ALL) {
					continue;
				}
				// ほぼ完全に埋まっていてお飾りなしなら無視
				if (p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari()) {
					continue;
				}
				// フェロモンを持っている相手ならキープ
				if (p.isbPheromone()) {
					// 相手発見確定
					bodyHasPheromone = p;
				}
				// 一番近いゆっくりと、二番目に近いゆっくりを取得
				int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
				if (minDistance > dist) {
					// 一番近い相手に確定
					minDistance = dist;
					found = p;
				} else if (minDistance <= dist && dist < secondMinDistance) {
					//二番目に近い相手だと、ランダムでそっちに確定
					secondMinDistance = dist;
					if (SimYukkuri.RND.nextBoolean())
						found = p;
				}
				// 自分のおかざりがなくて、相手にお飾りがある。うんうん奴隷のものは奪わない
				if (!b.hasOkazari() && p.hasOkazari() && b.getBodyAgeState() == p.getBodyAgeState() &&
						b.getType() == p.getType() && b.getType() != HybridYukkuri.type
						&& p.getOkazari().getOkazariType() == OkazariType.DEFAULT &&
						(p.getPublicRank() == PublicRank.NONE || b.getPublicRank() == PublicRank.UnunSlave )
						&& !b.isLockmove()) {
					// ゲス,ストレスが10%以上
					if (b.isRude()) {
						bodyHasOkazari = p;
						// フェロモンを持っている相手ならキープ
						if (p.isbPheromone()) {
							bodyHasOkazariAndPherommone = p;
						}
					}
				}
			}
			// フェロモンを持っている相手がいれば優先する
			if (bodyHasPheromone != null) {
				found = bodyHasPheromone;
			}
			if (bodyHasOkazariAndPherommone != null) {
				bodyHasOkazari = bodyHasOkazariAndPherommone;
			}
		}

		// 目標が定まっていないなら終了
		if (found == null) {
			//興奮してたらオナニー
			if (b.isExciting() && SimYukkuri.RND.nextInt(60) == 0) {
				b.doOnanism();
				return true;
			}
			// とくにすることがないなら親のそばに行く
			//			checkNearParent(b);
			return ret;
		}

		// 目標が定まったら移動セット
		int mz = 0;
		// 飛行種はZも移動可能
		if (b.canflyCheck()) {
			mz = found.getZ();
		}

		// ゆっくり同士が重ならないように目標地点は体のサイズを考慮
		int colX = calcCollisionX(b, found);

		// 相手が死体でなく、かつ除去されてなければ
		if (!found.isDead() && !found.isRemoved()) {
			// 生まれていない
			if (b.isUnBirth()) {
				return false;
			}
			// 生まれていない
			if (found.isUnBirth()) {
				return false;
			}

			// 自分が発情していればすっきりに向かう
			if (b.isExciting()) {
				//自分がれいぱー/既婚/（ドゲスで1/10の確率）の場合はすっきりしに行く
				if (b.isRaper() || (b.isVeryRude() && SimYukkuri.RND.nextInt(10) == 0) || b.isPartner(found)
						|| found.isPartner(b)) {
					b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
				}
				//れいぱーでない独身勢はプロポーズへ
				else {
					//ただし、相手が足りないゆの時はキャンセル
					if (found.isIdiot() && !b.isIdiot()) {
						b.setCalm();
						return true;
					}
					//ドゲスの場合は50%の確率でプロポーズをする
					if (b.getAttitude() != Attitude.SUPER_SHITHEAD || (b.getAttitude() == Attitude.SUPER_SHITHEAD && SimYukkuri.RND.nextBoolean())) {
						EventLogic.addBodyEvent(b, new ProposeEvent(b, found, null, 1), null, null);
						return true;
					} else if (b.getAttitude() == Attitude.SUPER_SHITHEAD) {
						// ドゲスの場合は50%の確率でレイプだけする
						b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(true);
					}
				}
				ret = true;
			} else if (!found.hasOkazari() && b.getOkazari() != null
					&& b.getOkazari().getOkazariType() == OkazariType.DEFAULT && b.isRude() && !b.isIdiot()
					&& !b.isDamaged() && !found.isUnBirth() && b.getCurrentEvent() == null) {
				// 自分が通常種で相手が捕食種の場合は参加しない
				if (b.isPredatorType() || !found.isPredatorType()) {
					// 相手がおかざりのないゆっくりなら制裁を呼びかける
					if (b.isVeryRude() || !found.isDamaged()) {
						if (SimYukkuri.RND.nextInt(20) == 0) {
							if (!b.isTalking()) {
								// 自分がうんうん奴隷ではない場合
								if (b.getPublicRank() != PublicRank.UnunSlave) {
									// 非ゆっくり症は参加しない
									if (b.isNotNYD() && found.isNotNYD()) {
										EventLogic.addWorldEvent(new HateNoOkazariEvent(b, found, null, 10), b,
												MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
									}
								}
							}
							ret = true;
						}
					}
				}
			}

			// プレイヤーにすりすりされていた場合の処理
			eActionGo eAct = checkActionSurisuriFromPlayer(b, found);
			if (eAct != eActionGo.NONE) {
				if (eAct == eActionGo.GO) {
					// 近づきすぎないように近づく
					b.moveToBody(found, found.getX() + colX * 2, found.getY(), mz);
					b.setTargetBind(false);
				}
				return true;
			}

			if (checkEmotionFromUnunSlave(b, found)) {
				return true;
			}

			// 自分のおかざりがなくて、相手にお飾りがある。うんうん奴隷のものは奪わない
			if (bodyHasOkazari != null) {
				b.setToSteal(false);
				// 視界内に起きているゆっくりがいない
				if (!BodyLogic.checkWakeupOtherYukkuri(b) || SimYukkuri.RND.nextInt(20) == 0) {
					b.moveToBody(bodyHasOkazari, bodyHasOkazari.getX() + colX, bodyHasOkazari.getY(), mz);
					b.setTargetBind(false);
					b.setToSteal(true);
					return true;
				}
			}
			if (b.getPublicRank() != found.getPublicRank()) {
				return false;
			}

			// 相手に針が刺さっている場合
			if (found.isNeedled()) {
				// ランダムで向かう
				if (SimYukkuri.RND.nextInt(50) == 0) {
					if (b.isAdult() && !found.isAdult() && (found.isChild(b) || b.isMother(found))) {
						// 自分が母親で相手が針の刺さった子供ならぐーりぐーりしにいく
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
					} else if (found.isPartner(b)) {
						// つがいで相手が針の刺さっているならぐーりぐーりしにいく
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
					}
				}
				return true;
			}

			// 以下相手に針が刺さっていない場合

			if (SimYukkuri.RND.nextBoolean()) {
				if (b.isAdult() && !found.isAdult() && (found.isChild(b) || b.isMother(found))
						&& (b.getIntelligence() == Intelligence.FOOL && !b.hasOkazari())) {
					// 相手が子供でも、子供にお飾りがなくてかつ親がバカならよらない
					return true;
				}
				//相手が子か番で、アリに食われていたらそっちに向かう
				if (((found.isChild(b) || b.isMother(found)) || b.isPartner(found))
						&& found.getAttachmentSize(Ants.class) != 0) {
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					return true;
				} else if (b.isAdult() && !found.isAdult() && found.isNormalDirty()
						&& (found.isChild(b) || b.isMother(found))) {
					// 相手が汚れた子供ならぺろぺろしに向かう
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					return true;
				} else if (b.isChild(found) && !b.isAdult() && b.isDirty()) {
					// 自分が汚れた子供なら家族のところへ向かう
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(true);
					return true;
				}
			}

			// ランダムでつがいのところへ向かう
			if (found.isPartner(b)) {
				if (SimYukkuri.RND.nextInt(150) == 0) {
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					return true;
				}
			}

			// ランダムで親のところへ向かう
			if (!b.isAdult() && b.isChild(found)) {
				if (SimYukkuri.RND.nextInt(100) == 0) {
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					return true;
				}
			}

			// ランダムで姉妹のところへ向かう
			if (!b.isAdult() && b.isSister(found)) {
				if (SimYukkuri.RND.nextInt(150) == 0) {
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					return true;
				}
			}

			// ランダムで家族のところへ向かう
			if (b.isAdult() && !found.isAdult() && b.isFamily(found)) {
				if (SimYukkuri.RND.nextInt(150) == 0) {
					b.moveToBody(found, found.getX() + colX, found.getY(), mz);
					b.setTargetBind(false);
					return true;
				}
			}

			if (!ret) {
				// 特にすることがないなら親のそばに行く
				checkNearParent(b);
			}
			return ret;

		}
		// 死体相手の行動
		else {
			if (b.isExciting()) {
				// すっきり
				b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
				b.setTargetBind(false);
				return true;
			}
			if (SimYukkuri.RND.nextInt(10) != 0) {
				return ret;
			}
			// 片方だけがうんうん奴隷の場合はなにもしない
			if (b.getPublicRank() == found.getPublicRank()) {
				// レイパーじゃないなら気にする
				if (!b.isRaper()) {
					// 家族の死体に嘆く
					if (b.isAdult()) {
						if (b.isParent(found) || b.isPartner(found) || found.isParent(b)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType())
									&& !Terrarium.predatorSteam) {
								b.lookTo(found.getX() + colX, found.getY());
							}
						}
					} else {
						//自身が対象死体の姉妹または対象死体が自身の親なら、そちらに向かう
						if (b.isSister(found) || found.isParent(b)) {
							b.moveToBody(found, found.getX() + colX, found.getY(), mz);
							b.setTargetBind(false);
							ret = true;
						} else {
							//自身も対象死体も捕食種、または自身が通常種の場合、死体から逃げる
							if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType())
									&& !Terrarium.predatorSteam) {
								b.runAway(found.getX() + colX, found.getY());
							}
						}
					}
				}
			}

			// フィールドの死体に怯える
			if (!b.isTalking()) {
				if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType()) && !Terrarium.predatorSteam) {
					if (b.isNotNYD()) {
						// レイパー,捕食種じゃないなら気にする
						if (!b.isRaper() && !b.isPredatorType()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Scare));
							b.setHappiness(Happiness.SAD);
							b.addMemories(-1);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 接触している場合の動作
	 * @param p 自分
	 * @param b 相手
	 * @return 動作を行った場合
	 */
	public static final boolean doActionOther(Body p, Body b) {
		// 途中で消されてたら他の候補を探す
		if (p.isRemoved()) {
			b.clearActions();
			return false;
		}

		// 相手が宙に浮いてたら無視
		if (!b.canflyCheck() && p.getZ() != 0) {
			b.clearActions();
			return false;
		}

		if (b.isNYD()) {
			return false;
		}

		// 片方だけがうんうん奴隷の場合はなにもしない
		if (b.getPublicRank() != p.getPublicRank() && !(b.isRaper() && b.isExciting())) {
			// 盗みに行かない場合は終了
			if (!b.isToSteal()) {
				b.clearActions();
				return false;
			}
		}

		int rangeX = Translate.invertX((int) ((b.getCollisionX() + p.getCollisionX()) * 0.6f), p.getY());
		rangeX = Translate.transSize(rangeX);
		int distX = Math.abs(b.getX() - p.getX());
		int distY = Math.abs(b.getY() - p.getY());
		int range = Math.abs(rangeX - distX);
		// 見つかった相手に対するコリジョンチェック
		// 体が隣接するように横長のボックスで判定を取る

		if (range < 3 && distY < 5) {
			// 相手との距離が隣接状態と判断された場合
			if (p.isDead()) {
				// 相手が死体の場合
				// 発情してたらすっきり
				if (b.isExciting()) {
					if (b.isRaper()) {
						if (!p.isRaper()) {
							b.doRape(p);
							b.clearActions();
							return true;
						}
					} else {
						b.doOnanism(p);
						b.clearActions();
						return true;
					}
				}

				if (b.isAdult()) {
					// 自分が成体で相手が家族なら嘆く
					if (!b.isTalking()) {
						if (b.isParent(p)) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForChild));
							//if(SimYukkuri.RND.nextInt(10)==0 ){
							if (b.checkWait(2000)) {
								b.setLastActionTime();
								EventLogic.addWorldEvent(new FuneralEvent(b, p, null, 10), b, null);
							}
							//}
						} else if (b.isPartner(p)) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForPartner));
						}
						b.setHappiness(Happiness.VERY_SAD);
						b.addMemories(-2);
						b.addStress(100);
						//b.clearActions();
						return true;
					}
					//return true;
				}
				if (p.isParent(b)) {
					//相手が親なら嘆く
					if (!b.isTalking()) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForParent));
					}
					b.setHappiness(Happiness.VERY_SAD);
					b.setForceFace(ImageCode.SURPRISE.ordinal());
					b.addMemories(-2);
					b.addStress(100);
					//b.clearActions();
					return true;
				}
				if (b.isSister(p)) {
					// 相手が姉妹なら嘆く
					if (!b.isTalking()) {
						if (b.getAge() < p.getAge()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForEldersister));
						} else {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForSister));
						}
						b.setHappiness(Happiness.VERY_SAD);
						b.addStress(100);
						b.addMemories(-2);
						//b.clearActions();
						//return true;
					}
				}
				return true;
				//b.clearActions();
			}

			// おかざり盗み
			if (b.isToSteal()) {
				// 自分のおかざりがなくて、相手にお飾りがある。自分がうんうん奴隷でない場合うんうん奴隷のものは奪わない
				if (!b.hasOkazari() && p.hasOkazari() && b.getBodyAgeState() == p.getBodyAgeState() &&
						b.getType() == p.getType() && b.getType() != HybridYukkuri.type
						&& p.getOkazari().getOkazariType() == OkazariType.DEFAULT &&
						(p.getPublicRank() == PublicRank.NONE || b.getPublicRank() == PublicRank.UnunSlave)&& !b.isLockmove()) {
					// ゲス,ストレスが50%以上
					if (b.isRude()) {
						// 視界内に起きている一般ゆがいない
						if (!BodyLogic.checkWakeupOtherYukkuri(b)) {
							// 自分が奴隷で相手が奴隷ではないなら自分を格上げ、相手はうんうん奴隷に堕とす
							if (b.getPublicRank() != PublicRank.NONE && p.getPublicRank() == PublicRank.NONE) {
								b.setPublicRank(PublicRank.NONE);
								p.setPublicRank(PublicRank.UnunSlave);
							}
							p.takeOkazari(false);
							b.giveOkazari(OkazariType.DEFAULT);
							b.setHappiness(Happiness.HAPPY);
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetOtherAccessoryStealthily));
							b.addMemories(100);
							b.addStress(-b.getStressLimit() / 2);
							b.clearActions();
							b.stay();
							return true;
						}
					}
				}
				return false;
			}

			//↓以下、相手が生きて居てかつお飾り盗みでない場合

			// 自分が発情してたらすっきり実行
			if (b.isExciting()) {
				// れいぱーまたは確率でドゲスはれいぷする
				if (b.isRaper() || b.isVeryRude()) {
					if (!p.isRaper()) {
						if (b.getX() < p.getX()) {
							b.setDirection(Direction.RIGHT);
						} else {
							b.setDirection(Direction.LEFT);
						}
						b.constraintDirection(p, true);
						b.doRape(p);
						b.clearActions();
						return true;
					}
				} else {
					// 大人が相手の場合は、プロポーズしてからすっきりする
					if (p.isAdult()) {
						b.constraintDirection(p, false);
						b.clearActions();
						if (b.isPartner(p) || p.isPartner(b)) {
							b.doSukkiri(p);
							return true;
						} else {
							b.doOnanism();
							return true;
						}
					}
					//強制的に発情させられた場合は見境なし
					if (b.isForceExciting()) {
						b.doSukkiri(p);
						b.clearActions();
					}
				}
			}

			// 相手に針が刺さっている場合
			if (p.isNeedled()) {
				if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isMother(p))) {
					// 自分が母親で相手が針の刺さった子供ならぐーりぐーり
					b.constraintDirection(p, false);
					b.doGuriguri(p);
				} else if (p.isPartner(b)) {
					// つがいで相手が針の刺さっているならぐーりぐーり
					b.constraintDirection(p, false);
					b.doGuriguri(p);
				} else if (!b.isAdult() && b.isSister(p) && SimYukkuri.RND.nextInt(1) == 0) {
					// 姉妹で相手が針の刺さっているならぐーりぐーり
					b.constraintDirection(p, false);
					b.doGuriguri(p);
				}
				b.clearActions();
				return true;
			}

			//自分がかびてなくてかつ、相手がかびてるとき
			if (b.findSick(p) && !b.isSick()) {
				EventLogic.addBodyEvent(b, new AvoidMoldEvent(b, p, null, 1), null, null);
				return true;
			}
			//相手がかびてなくてかつ、自分がかびてるとき
			if (p.findSick(b) && !p.isSick()) {
				EventLogic.addBodyEvent(p, new AvoidMoldEvent(p, b, null, 1), null, null);
				return true;
			}
			// 相手が子供でも、子供にお飾りがなくてかつ親がバカならなら制裁する
			if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isMother(p))
					&& (b.getIntelligence() == Intelligence.FOOL && !p.hasOkazari())) {
				if (b.getCurrentEvent() == null && p.isNYD() && SimYukkuri.RND.nextBoolean()) {
					b.clearActions();
					EventLogic.addWorldEvent(new HateNoOkazariEvent(b, p, null, 10), b,
							MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
				}
				return true;
			}
			//相手がありに食われてる時
			if (p.getAttachmentSize(Ants.class) != 0) {
				//自分がアリに食われてない時のみ相手をぺろぺろする余裕がある
				if (b.getAttachmentSize(Ants.class) == 0) {
					b.doPeropero(p);
				}
			}

			// 餌を保持している
			if (b.isParent(p) && p.isVeryHungry() && !p.isAdult() && b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
				// 吐き出す
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GiveFood), false);
				b.dropTakeoutItem(TakeoutItemType.FOOD);
				return true;
			}

			if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isParent(p))) {
				// 自分が親で相手が子供の時のスキンシップ
				b.constraintDirection(p, false);
				//相手が汚れていてかつ自分が母親の時か、ランダムでぺろぺろ
				if ((p.isDirty() && b.isMother(p)) || SimYukkuri.RND.nextBoolean()) {
					b.doPeropero(p);
				}
				//他はすりすり
				else if (SimYukkuri.RND.nextBoolean()) {
					b.doSurisuri(p);
				}
				b.clearActions();
				return true;
			}
			if (p.isPartner(b) && SimYukkuri.RND.nextBoolean()) {
				// 相手が自分の番ならすりすり
				b.constraintDirection(p, false);
				b.doSurisuri(p);
				b.clearActions();
				return true;
			}
			if (!b.isAdult() && (b.isChild(p) || p.isParent(b))) {
				//自分が子供で、相手が親の時のスキンシップ
				b.constraintDirection(p, false);
				//自分が汚れた赤ゆなら、ぺろぺろしてもらう
				if (b.isBaby() && b.isDirty() && p.isMother(b)) {
					p.doPeropero(b);
				}
				//親がダメージ食らってたらランダムでぺろぺろ
				if (p.isDamaged() && SimYukkuri.RND.nextBoolean()) {
					b.doPeropero(p);
				}
				//他はすりすり
				else if (SimYukkuri.RND.nextBoolean()) {
					b.doSurisuri(p);
				}
				b.clearActions();
				return true;
			}
			if (!b.isAdult() && b.isSister(p) && SimYukkuri.RND.nextBoolean()) {
				// 姉妹の場合のスキンシップ
				//善良で、赤ゆでなく、相手が汚れていたら無条件でぺろぺろ
				b.constraintDirection(p, false);
				if (b.isSmart() && !b.isBaby() && p.isDirty()) {
					b.doPeropero(p);
				} else {
					if (p.isDamaged() && SimYukkuri.RND.nextBoolean()) {
						if (b.isElderSister(p)) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
						} else {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutSister));
						}
						b.setHappiness(Happiness.SAD);
						b.stay();
						p.stay();
					} else if (p.isDamaged() && SimYukkuri.RND.nextBoolean()) {
						b.doPeropero(p);
					} else if (SimYukkuri.RND.nextBoolean()) {
						b.doSurisuri(p);
					}
				}
				b.clearActions();
				return true;
			}

		}

		//非接触状態の場合
		else {
			int dir = 1;
			if (b.getX() < p.getX())
				dir = -1;
			rangeX *= dir;
			if (b.canflyCheck()) {
				b.moveTo(p.getX() + rangeX, p.getY(), p.getZ());
			} else {
				b.moveTo(p.getX() + rangeX, p.getY());
			}
			// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
			if (Translate.distance(b.getX(), b.getY(), p.getX(), p.getY()) < 2500) {
				if (SimYukkuri.RND.nextInt(3) == 0) {
					if (b.isTargetBind())
						p.stay();
				}
			}
		}
		return true;
	}

	/**
	 *  体同士が触れる位置のX座標を求める
	 * @param from ゆっくり
	 * @param to 相手のゆっくり
	 * @return X座標
	 */
	public static final int calcCollisionX(Body from, Body to) {
		if (from == null || to == null) {
			return 0;
		}

		int colX = Translate.invertX((int) ((from.getCollisionX() + to.getCollisionX()) * 0.6f), to.getY());
		colX = Translate.transSize(colX);

		// お互いの位置から右と左最短距離を選択
		int dir = 1;
		if (from.getX() < to.getX())
			dir = -1;
		colX *= dir;

		return colX;
	}

	/**
	 *  他のゆっくりがプレイヤーにすりすりされていた場合の行動判定
	 * @param b 自分
	 * @param bodyTarget 相手
	 * @return 行動をしたかどうか
	 */
	public static final eActionGo checkActionSurisuriFromPlayer(Body b, Body bodyTarget) {
		// 例外除去
		if (b == null || bodyTarget == null) {
			return eActionGo.NONE;
		}
		if (!bodyTarget.isbSurisuriFromPlayer()) {
			return eActionGo.NONE;
		}

		// 一定確率以上は終了
		if (SimYukkuri.RND.nextInt(10) != 0) {
			return eActionGo.NONE;
		}

		// 障害ゆは反応しない
		if (b.isIdiot()) {
			return eActionGo.NONE;
		}
		if (b.isNYD()) {
			return eActionGo.NONE;
		}

		boolean[] abEmote = new boolean[7];
		abEmote = EmotionLogic.checkEmotionForOther(b, bodyTarget);
		// 自分との関係
		EnumRelationMine eRelation = checkMyRelation(b, bodyTarget);
		eActionGo eAct = eActionGo.NONE;

		// 喜ぶ
		if ((abEmote[0])) {
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
				// 子供の状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutChild));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case PARTNAR: // つがい
				// つがいの状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutPartner));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case CHILD_FATHER: // 父の子供
				// 父の状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutFather));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case CHILD_MOTHER: // 母の子供
				// 母の状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutMother));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case ELDERSISTER: // 姉
				// 妹の状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutSister));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case YOUNGSISTER: // 妹
				// 姉の状態を喜ぶ
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutElderSister));
				b.setHappiness(Happiness.HAPPY);
				b.stay();
				eAct = eActionGo.GO;
				break;
			default: // 他人
				break;
			}
		}

		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		// 羨望
		// うらやましくて悲しいかつ怒ってない
		if ((abEmote[2]) && (abEmote[5]) && !abEmote[1]) {
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
			case PARTNAR: // つがい
			case CHILD_FATHER: // 父の子供
			case CHILD_MOTHER: // 母の子供
			case ELDERSISTER: // 姉
				// 妹の状態をうらやましがって泣く
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyCryAboutSisterInSurisuri));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case YOUNGSISTER: // 妹
				// 姉の状態をうらやましがって泣く
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyCryAboutElderSisterInSurisuri));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			default: // 他人
				// 他人をうらやましがって泣く
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyCryAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		// 羨望2
		// うらやましいけど怒ってない
		if ((abEmote[5]) && !abEmote[1]) {
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
			case PARTNAR: // つがい
			case CHILD_FATHER: // 父の子供
			case CHILD_MOTHER: // 母の子供
				break;
			case ELDERSISTER: // 姉
				// 姉の状態をうらやましがる
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyAboutSisterInSurisuri));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			case YOUNGSISTER: // 妹
				// 姉の状態をうらやましがる
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyAboutSisterInSurisuri));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			default: // 他人
				// 他人をうらやましがる
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EnvyAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//羨望3
		// うらやましくて怒ってる
		if ((abEmote[5]) && (abEmote[1])) {
			b.addMemories(-1);
			// うらやましすぎて憎む
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
				// 子供の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutChild));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			case PARTNAR: // つがい
				// 子供の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutPartner));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			case CHILD_FATHER: // 父の子供
				// 父の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutFather));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			case CHILD_MOTHER: // 母の子供
				// 母の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			case ELDERSISTER: // 姉
				// 妹の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutSister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			case YOUNGSISTER: // 妹
				// 姉の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutElderSister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			default: // 他人
				// 他人の状態を憎む
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutOther));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.WAIT;
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//恐怖1
		// 心配してない
		if (!abEmote[2] && (abEmote[4])) {
			// ゆっくりできない
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
			case PARTNAR: // つがい
			case CHILD_FATHER: // 父の子供
			case CHILD_MOTHER: // 母の子供
			case ELDERSISTER: // 姉
			case YOUNGSISTER: // 妹
			default: // 他人
				eAct = eActionGo.WAIT;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Scare));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//心配2
		// +悲しい怖い
		if ((abEmote[2]) && (abEmote[6]) && (abEmote[4])) {
			switch (eRelation) {
			// 家族は怯えながら励ます
			case FATHER: // 父
			case MOTHER: // 母
				// 子供を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutChild));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case PARTNAR: // つがい
				// つがいを励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutPartner));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case CHILD_FATHER: // 父の子供
				// 父を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutFather));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case CHILD_MOTHER: // 母の子供
				// 母を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutMother));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case ELDERSISTER: // 姉
				// 姉を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				break;
			case YOUNGSISTER: // 妹
				// 妹を励ます
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.VERY_SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			default: // 他人
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//心配3
		//+悲しいかつ怖くない
		if ((abEmote[2]) && (abEmote[6]) && !abEmote[4]) {
			// 家族は励ます
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
				// 子供を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutChild));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case PARTNAR: // つがい
				// つがいを励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutPartner));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case CHILD_FATHER: // 父の子供
				// 父を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutFather));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case CHILD_MOTHER: // 母の子供
				// 母を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutMother));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case ELDERSISTER: // 姉
				// 姉を励ます
				eAct = eActionGo.GO;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.SAD);
				b.stay();
				break;
			case YOUNGSISTER: // 妹
				// 妹を励ます
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ConcernAboutEldersister));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			default: // 他人
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//心配4
		// 悲しいけど心配していない
		if ((abEmote[2]) && !abEmote[6]) {
			// 哀れみ
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
			case PARTNAR: // つがい
			case CHILD_FATHER: // 父の子供
			case CHILD_MOTHER: // 母の子供
			case ELDERSISTER: // 姉
			case YOUNGSISTER: // 妹
				break;
			default: // 他人
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.MercyAboutOther));
				b.setHappiness(Happiness.SAD);
				b.stay();
				eAct = eActionGo.GO;
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct != eActionGo.NONE) {
			return eAct;
		}

		//喜び1
		// うれしくて楽しい
		if ((abEmote[0]) && (abEmote[0])) {
			// 見下して喜ぶ
			switch (eRelation) {
			case FATHER: // 父
			case MOTHER: // 母
			case PARTNAR: // つがい
			case CHILD_FATHER: // 父の子供
			case CHILD_MOTHER: // 母の子供
			case ELDERSISTER: // 姉
			case YOUNGSISTER: // 妹
				break;
			default: // 他人
				// 見下して喜ぶ
				eAct = eActionGo.WAIT;
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
				break;
			}
		}
		// 処理が決まったら終了
		if (eAct == eActionGo.NONE) {
			return eAct;
		}

		return eAct;
	}

	/**
	 * 婚姻候補のリストを作る。既婚の場合は、相手のみを含むリストを作る
	 * @param b 自分
	 * @param age ゆん生のステージ
	 * @return 婚姻候補のリスト
	 */
	public static final List<Body> createActiveFianceeList(Body b, int age) {
		// ほかにいないならスキップ
		if (SimYukkuri.world.getCurrentMap().body.size() <= 1) {
			return null;
		}

		List<Body> activeFianceeList = new LinkedList<Body>();

		//番がすでにいれば要素はそれのみに
		Body pa = YukkuriUtil.getBodyInstance(b.getPartner());
		if (pa != null) {
			activeFianceeList.add(pa);
			return activeFianceeList;
		}

		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body f = entry.getValue();
			if (f == null) {
				continue;
			}
			//自身はスキップ
			if (f == b) {
				continue;
			}
			//死んでる
			if (f.isDead()) {
				continue;
			}
			//除去された
			if (f.isRemoved()) {
				continue;
			}
			//生まれてない
			if (f.isUnBirth()) {
				continue;
			}
			// 相手に子供がいる場合はスキップ
			if (f.getChildrenListSize() != 0) {
				continue;
			}
			// 自分とランクが違ったらスキップ
			if (b.getPublicRank() != f.getPublicRank()) {
				continue;
			}
			//障害ゆんもスキップ
			if (f.hasDisorder()) {
				continue;
			}
			//かびてるのもスキップ
			if (b.findSick(f)) {
				continue;
			}
			//ロリコンはいない
			if (age > f.getBodyAgeState().ordinal()) {
				continue;
			}
			//お相手がすでにいるのは50%の確率でスキップ
			if (YukkuriUtil.getBodyInstance(f.getPartner()) != null) {
				if (SimYukkuri.RND.nextBoolean()) {
					continue;
				}
			}
			activeFianceeList.add(f);
		}
		return activeFianceeList;
	}
	/**
	 * アクティブな赤ゆ/子ゆのリストを作成する.
	 * @param b ゆっくり
	 * @param bState 子ゆっくりを入れるかどうか（これがfalseなら赤ゆのみのリストになる）
	 * @return アクティブな赤ゆ/子ゆのリスト
	 */
	public static final List<Body> createActiveChildList(Body b, boolean bState) {
		// 子供がいないならスキップ
		int nChildlenListSize = b.getChildrenListSize();
		if (nChildlenListSize == 0) {
			return null;
		}
		List<Body> activeChildlenList = new LinkedList<Body>();
		for (int i = 0; i < nChildlenListSize; i++) {
			Body bodyChild = b.getChildren(i);
			if (bodyChild == null) {
				continue;
			}
			//　死んでる
			if (bodyChild.isDead()) {
				continue;
			}
			//　除去された
			if (bodyChild.isRemoved()) {
				continue;
			}
			//　生まれてない
			if (bodyChild.isUnBirth()) {
				continue;
			}
			// プレイヤーにアイテムとして持たれてる
			if (bodyChild.isTaken()) {
				continue;
			}
			// 子供に子供がいる場合はスキップ
			if (bodyChild.getChildrenListSize() != 0) {
				continue;
			}
			// うんうん奴隷はスキップ
			if (bodyChild.getPublicRank() == PublicRank.UnunSlave) {
				continue;
			}
			if (bodyChild.isNYD() || bodyChild.isNotAllright()) {
				continue;
			}
			if (!bState) {
				// 赤ゆっくり以外参加しないのでスキップ
				if (!bodyChild.isBaby()) {
					continue;
				}
			} else {
				// 赤ゆっくり、子ゆっくり以外参加しないのでスキップ
				if (bodyChild.isAdult()) {
					continue;
				}
			}
			activeChildlenList.add(bodyChild);
		}
		return activeChildlenList;
	}

	/**
	 *  ぜんゆん集合
	 */
	public static final void gatheringYukkuri() {
		Body[] bodyList = YukkuriUtil.getBodyInstances();
		if (bodyList.length != 0) {
			Toilet t = null;
			for (Map.Entry<Integer, Toilet> entry : SimYukkuri.world.getCurrentMap().toilet.entrySet()) {
				t = entry.getValue();
				break;
			}
			if (t != null) {
				gatheringYukkuriSquare(t, bodyList, GatheringDirection.UP, null);
			}
		}
	}

	/**
	 *  ぜんゆん集合(四角形前面)
	 * @param bTop 先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList) {
		return gatheringYukkuriSquare(bTop, TargetList.toArray(new Body[0]), GatheringDirection.DOWN, null);
	}

	/**
	 *  ぜんゆん集合(四角形前面)
	 * @param bTop 先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param e イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList, EventPacket e) {
		return gatheringYukkuriSquare(bTop, TargetList.toArray(new Body[0]) , GatheringDirection.DOWN, e);
	}
	/**
	 * ぜんゆん集合
	 * @param oTop 先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param eDir 並べる方向
	 * @param e イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriSquare(Obj oTop, Body[] TargetList, GatheringDirection eDir,
			EventPacket e) {
		int nMaxRowSize = 3;// 初期最大幅

		if (oTop == null || TargetList == null) {
			return false;
		}
		int nSize = TargetList.length;
		if (nSize == 0) {
			return false;
		}

		boolean bKi = true;
		// 最大幅以下ならそれを最大幅にする
		if (nSize < nMaxRowSize) {
			nMaxRowSize = nSize;
		}

		// 奇数か偶数か
		if (nMaxRowSize % 2 == 0) {
			// 偶数
			bKi = false;
		}

		int nCount = 0;
		int nColY = 0;
		int nCol = 0;
		int nRow = 1;
		int nDir = -1;
		int colX = 10;
		Obj objFrontCenter = oTop;
		Obj objNextFrontCenter = null;

		boolean bFlag = true;
		for (Body b : TargetList) {
			int nSpace = 10;
			if (b == null) {
				continue;
			}
			// 別のイベント中なら集めない
			if (e != null && b.getCurrentEvent() != null && b.getCurrentEvent() != e) {
				continue;
			}

			nCount++;
			// 目標が定まったら移動セット
			int mz = 0;
			// 飛行種はZも移動可能
			if (b.canflyCheck()) {
				mz = oTop.getZ();
			}

			colX = Translate.invertX(b.getCollisionX(), objFrontCenter.getY());
			//colX = b.getCollisionX();
			int x = 0;
			int y = 0;
			boolean bMoved = false;

			//　一列目の一体目なら
			if ((nMaxRowSize == 1) || (nCount % nMaxRowSize == 1)) {
				// 次回は次の行
				nCol++;
				if (objNextFrontCenter != null) {
					objFrontCenter = objNextFrontCenter;
				}
				objNextFrontCenter = b;
				int nLastLineSize = nSize - nMaxRowSize * (nCol - 1);
				if ((nLastLineSize < nMaxRowSize) && (0 < nLastLineSize)) {
					bKi = true;
					// 最後の行が偶数の場合
					if (nLastLineSize % 2 == 0) {
						bKi = false;
					}
				}

				// 縦と横は別に計算したほうがいいとは思うけど雑に計算
				nColY = colX + nSpace;
				// 座標計算
				switch (eDir) {
				case UP:
					x = objFrontCenter.getX();
					y = objFrontCenter.getY() - nColY;
					break;
				case DOWN:
					x = objFrontCenter.getX();
					y = objFrontCenter.getY() + nColY;
					break;
				case LEFT:
					x = objFrontCenter.getX() - nColY;
					y = objFrontCenter.getY();
					break;
				case RIGHT:
					x = objFrontCenter.getX() + nColY;
					y = objFrontCenter.getY();
					break;
				}

				if (x < 0) {
					x = 0;
				} else if (Translate.mapW < x) {
					x = Translate.mapW;
				}

				if (y < 0) {
					y = 0;
				} else if (Translate.mapH < y) {
					y = Translate.mapH;
				}

				// 列はリセット
				nRow = 1;
				// 奇数
				if (bKi) {
					// 正面に立つ
					// 移動
					if (e == null) {
						b.moveToBody(objFrontCenter, x, y, mz);
					} else {
						b.moveToEvent(e, x, y, mz);
					}
					bMoved = true;
				}
			}

			if (!bMoved) {
				// 座標計算
				switch (eDir) {
				case UP:
					// 奇数の場合
					if (bKi) {
						x = objFrontCenter.getX() + (colX + nSpace) * nRow * nDir;
						y = objFrontCenter.getY() - nColY;
					} else {
						//最初の2体は間隔半分
						if (nRow == 1) {
							nSpace = nSpace / 2;
						}
						x = objFrontCenter.getX() + (colX + nSpace) * (2 * nRow - 1) * nDir;
						y = objFrontCenter.getY() - nColY;
					}
					break;
				case DOWN:
					// 奇数の場合
					if (bKi) {
						x = objFrontCenter.getX() + (colX + nSpace) * nRow * nDir;
						y = objFrontCenter.getY() + nColY;
					} else {
						//最初の2体は間隔半分
						if (nRow == 1) {
							nSpace = nSpace / 2;
						}
						x = objFrontCenter.getX() + (colX * nRow + nSpace * 3 / 2 * nRow - 1) * nDir;
						y = objFrontCenter.getY() + nColY;
					}
					break;
				case LEFT:
					// 奇数の場合
					if (bKi) {
						x = objFrontCenter.getX() - nColY;
						y = objFrontCenter.getY() + (colX + nSpace) * nRow * nDir;
					} else {
						//最初の2体は間隔半分
						if (nRow == 1) {
							nSpace = nSpace / 2;
						}
						x = objFrontCenter.getX() - nColY;
						y = objFrontCenter.getY() + (colX + nSpace) * (2 * nRow - 1) * nDir;
					}
					break;
				case RIGHT:
					// 奇数の場合
					if (bKi) {
						x = objFrontCenter.getX() + nColY;
						y = objFrontCenter.getY() + (colX + nSpace) * nRow * nDir;
					} else {
						//最初の2体は間隔半分
						if (nRow == 1) {
							nSpace = nSpace / 2;
						}
						x = objFrontCenter.getX() + nColY;
						y = objFrontCenter.getY() + (colX + nSpace) * (2 * nRow - 1) * nDir;
					}
					break;
				}

				if (x < 0) {
					x = 0;
				} else if (Translate.mapW < x) {
					x = Translate.mapW;
				}
				if (y < 0) {
					y = 0;
				} else if (Translate.mapH < y) {
					y = Translate.mapH;
				}

				if (e == null) {
					b.moveToBody(oTop, x, y, mz);
				} else {
					b.moveToEvent(e, x, y, mz);
				}

				if (nDir == -1) {
					nDir = 1;
				} else {
					nDir = -1;
					nRow++;
				}
			}

			// 壁に引っかかってるなら
			if (Barrier.onBarrier(b.getX(), b.getY(), x, y,
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}

			// 目的地にたどり着いていない
			if (1 < Translate.distance(b.getX(), b.getY(), x, y)) {
				bFlag = false;
			}

		}
		return bFlag;
	}

	/**
	 *  ぜんゆん集合(先頭の後ろに一列)
	 * @param bTop 先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param e イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriBackLine(Body bTop, List<Body> TargetList, EventPacket e) {
		if (TargetList == null) {
			return false;
		}

		Body bodyFound = bTop;
		//int nDir = 1;
		if (bodyFound.getDirection() == Direction.RIGHT) {
			//	nDir = -1;
		}
		boolean bResult = true;

		for (Body b : TargetList) {
			if (b == null) {
				continue;
			}
			if (bodyFound == null) {
				continue;
			}
			if (b.isDead()) {
				continue;
			}
			// 別のイベント中なら集めない
			if (e != null && b.getCurrentEvent() != null && b.getCurrentEvent() != e) {
				continue;
			}
			int colX = Math.abs(calcCollisionX(b, bodyFound));
			// 目標が定まったら移動セット
			int mz = 0;
			// 飛行種はZも移動可能
			if (b.canflyCheck()) {
				mz = bodyFound.getZ();
			}
			int dist = Translate.getRealDistance(b.getX(), b.getY(), bodyFound.getX(), bodyFound.getY());
			int nToDist = dist - colX * 2;
			// すでに近くにいる
			if (nToDist < 1) {
				continue;
			}
			double dRad = Translate.getRadian(b.getX(), b.getY(), bodyFound.getX(), bodyFound.getY());
			Point4y p2 = Translate.getPointByDistAndRad(b.getX(), b.getY(), nToDist, dRad);
			// 視界内の一定距離内の地点まで移動する
			int x = p2.x;
			int y = p2.y;
			// 移動
			if (e == null) {
				b.moveToBody(bodyFound, x, y, mz);
			} else {
				b.moveToEvent(e, x, y, mz);
			}
			b.setTargetBind(false);
			bodyFound = b;
			// 壁に引っかかってるなら
			if (Barrier.onBarrier(b.getX(), b.getY(), x, y,
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			// 目的地にたどり着いていない
			if (1 < Translate.distance(b.getX(), b.getY(), x, y)) {
				bResult = false;
			} else {
				b.setDirection(bodyFound.getDirection());
			}
		}
		return bResult;
	}
	/**
	 * うんうんどれいの感情処理
	 * @param b 自分
	 * @param bodyTarget 相手
	 * @return 処理を行ったかどうか
	 */
	public static boolean checkEmotionFromUnunSlave(Body b, Body bodyTarget) {
		if (b == null || bodyTarget == null) {
			return false;
		}

		// 一定確率以上は終了
		if (SimYukkuri.RND.nextInt(50) != 0) {
			return false;
		}

		// 足りないゆは反応しない
		if (b.isIdiot()) {
			return false;
		}

		if (b.isNYD()) {
			return false;
		}

		boolean[] abEmote = new boolean[7];
		abEmote = EmotionLogic.checkEmotionForOther(b, bodyTarget);

		// 自分との関係
		EnumRelationMine eRelation = checkMyRelation(b, bodyTarget);

		// 自分がうんうん奴隷で相手は違う場合
		if ((b.getPublicRank() == PublicRank.UnunSlave) && (bodyTarget.getPublicRank() != PublicRank.UnunSlave)) {
			if (abEmote[5]) {
				// うらやましがる
				switch (eRelation) {
				case FATHER: // 父
				case MOTHER: // 母
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutChild));
					break;
				case PARTNAR: // つがい
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutPartner));
					break;
				case CHILD_FATHER: // 父の子供
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutFather));
					break;
				case CHILD_MOTHER: // 母の子供
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
					break;
				case ELDERSISTER: // 姉
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutElderSister));
					break;
				case YOUNGSISTER: // 妹
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutSister));
					break;
				default: // 他人
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutOther));
					break;
				}
				b.setHappiness(Happiness.VERY_SAD);
				b.addStress(10);
				b.stay();
				return true;
			}
		}

		return false;
	}
	/**
	 * 近い親をチェックする.
	 * @param b 赤ゆなど
	 */
	public static void checkNearParent(Body b) {
		// 大人なら終了
		if (b.isAdult()) {
			return;
		}

		int minDistance = b.getEYESIGHTorg();
		Body bodyParent = YukkuriUtil.getBodyInstance(b.getMother());
		if (bodyParent == null) {
			bodyParent = YukkuriUtil.getBodyInstance(b.getFather());
		}
		if (bodyParent == null) {
			int nSize = b.getElderSisterListSize();
			if (0 < nSize) {
				bodyParent = b.getElderSister(0);
			}
		}
		if (bodyParent == null) {
			return;
		}

		int dist = Translate.distance(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY());
		int nParcent = 32;

		//子ゆが泣き叫んでる時
		if (b.isCallingParents() && bodyParent.isSleeping())
			bodyParent.wakeup();
		//泣き叫びの原因がぺろぺろで対処できるとき
		if ((b.isDirty() || b.getAttachmentSize(Ants.class) != 0) && bodyParent.canEventResponse()) {
			if (dist <= bodyParent.getStepDist()) {
				bodyParent.constraintDirection(b, false);
				bodyParent.doPeropero(b);
				return;
			} else {
				b.moveTo(bodyParent.getX(), bodyParent.getY());
				//				bodyParent.moveTo( b.getX(), b.getY() );
				return;
			}
		}

		// 視界内の一定距離内ならなにもしない
		if (dist < minDistance / nParcent) {
			return;
		}

		// 一定距離外なら
		if (minDistance / nParcent <= dist) {
			// 相手との間に壁があれば終了
			if (Barrier.acrossBarrier(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				return;
			}

			int nToDist = (int) Math.sqrt(dist) - (int) Math.sqrt(minDistance / nParcent);
			double dRad = Translate.getRadian(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY());
			Point4y p2 = Translate.getPointByDistAndRad(b.getX(), b.getY(), nToDist, dRad);
			// 視界内の一定距離内の地点まで移動する
			b.moveTo(p2.x, p2.y, b.getZ());
		}
	}

	/**
	 *  視界内に起きているゆっくりがいないかチェック
	 * @param b ゆっくり
	 * @return 視界内に起きているゆっくりがいるかどうか
	 */
	public static boolean checkWakeupOtherYukkuri(Body b) {
		boolean bIsWakeup = false;
		int minDistance = b.getEYESIGHTorg();
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body p = entry.getValue();
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b)
				continue;
			if (p.isDead() || p.isRemoved() || p.isUnBirth())
				continue;
			if (p.isNYD())
				continue;
			if (b.getPublicRank() == PublicRank.NONE && p.getPublicRank() == PublicRank.UnunSlave)
				continue;
			if (p.getBaryState() != BaryInUGState.NONE)
				continue;

			int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance > dist) {
				// 相手との間に壁があればスキップ
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
			}
			if (!p.isSleeping()) {
				bIsWakeup = true;
				break;
			}
		}
		return bIsWakeup;
	}
}
