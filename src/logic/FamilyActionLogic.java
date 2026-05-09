package src.logic;
import src.util.GameEnvironment;
import src.util.GameMessages;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Body;
import src.event.EventPacket;
import src.base.Obj;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.event.FuneralEvent;
import src.event.ProudChildEvent;
import src.event.ShitExercisesEvent;
import src.event.SuperEatingTimeEvent;
import src.event.YukkuriRideEvent;
import src.field.impl.Barrier;
import src.item.Food;
import src.item.Toilet;
import src.system.MessagePool;

/***************************************************
	家族イベント関係の処理
 */
public class FamilyActionLogic {

	/**
	 * 家族関係処理
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkFamilyAction(Body body) {
		// 他の用事がある場合
		if (body.isToFood() || body.isToBody() || body.isToSukkiri() ||
				body.isToBed() || body.isToShit() || body.isToSteal() || body.isToTakeout()) {
			return false;
		}

		if (GameRandom.nextInt(300) != 0) {
			return false;
		}

		//-------------------------------------
		// イベント処理
		//-------------------------------------
		EventPacket currentEvent = body.getCurrentEvent();
		// イベント中なら終了
		if (currentEvent instanceof SuperEatingTimeEvent || currentEvent instanceof ShitExercisesEvent
				|| currentEvent instanceof YukkuriRideEvent || currentEvent instanceof ProudChildEvent
				|| currentEvent instanceof FuneralEvent) {
			return true;
		} else if (currentEvent != null) {
			return false;
		}
		// 大人だけが実行する
		if (!body.isAdult()) {
			return false;
		}

		// パートナーもチェック
		Body partnerBody = src.util.BodyRegistry.getBodyInstance(body.getPartner());
		if (partnerBody != null) {
			// イベント中なら終了
			if (partnerBody.getCurrentEvent() != null) {
				return false;
			}
			// 同時にイベントを行わないよう、歳をとっている方にイベントを任せる
			if (partnerBody.getAge() < body.getAge()) {
				return false;
			}
		}

		//--------------------------------------------------
		//自分の状態チェック
		if (body.isIdiot() || body.isDamaged() || !body.hasOkazari())
			return false;
		// うんうん奴隷の場合
		if (body.getPublicRank() == PublicRank.UnunSlave)
			return false;
		// 非ゆっくり症の場合
		if (body.isNYD())
			return false;
		// うんうん中、出産中、食事中は終了
		if (body.isShitting() || body.isBirth() || body.isEating() || body.nearToBirth()) {
			return false;
		}
		//　子供のリストに生きている子供がいるか
		List<Body> childrenList = BodyLogic.createActiveChildList(body, true);
		if (childrenList == null || childrenList.size() == 0) {
			return false;
		}
		// 興奮中は終了
		if (body.isExciting()) {
			return false;
		}

		//-------------------------------
		// 番の状態チェック
		if (partnerBody != null) {
			if (partnerBody.isDamaged() ||
					partnerBody.isLockmove() ||
					partnerBody.isNeedled() ||
					partnerBody.getCriticalDamegeType() != null ||
					!partnerBody.hasOkazari()) {
				return false;
			}
			// 産気づいたら終了
			if (partnerBody.nearToBirth()) {
				return false;
			}
			// うんうん中、出産中は終了
			if (partnerBody.isShitting() || partnerBody.isBirth()) {
				return false;
			}
		}
		//-------------------------------
		// 子供の状態チェック
		boolean wantToShit = true;
		boolean wantToEat = true;
		boolean hasBaby = false;
		// 自分が満腹なら食欲はない
		if (body.isFull()) {
			wantToEat = false;
		}
		// 子供がダメージを受けている、動けない場合は終了
		for (Body bodyChild : childrenList) {
			if (bodyChild == null) {
				continue;
			}

			// 怪我をしている
			if (bodyChild.isDamaged() || bodyChild.isNeedled() || bodyChild.getCriticalDamegeType() != null) {
				wantToShit = false;
				wantToEat = false;
				break;
			}
			if (bodyChild.isLockmove() || !bodyChild.hasOkazari()) {
				wantToShit = false;
				wantToEat = false;
				continue;
			}

			// 子供の初回食事がすんでいない場合はやらない
			if (!bodyChild.isFirstEatStalk()) {
				wantToShit = false;
				wantToEat = false;
				break;
			}

			// 自分と子ゆとの間に壁があるなら終了
			if (Barrier.onBarrier(body.getX(), body.getY(), bodyChild.getX(), bodyChild.getY(), Barrier.BARRIER_YUKKURI)) {
				wantToShit = false;
				wantToEat = false;
				break;
			}

			//-------------------------------------
			// うんうん判定
			double dShitPer = 100 * bodyChild.getShit() / bodyChild.getShitLimit();
			// 赤ゆのみチェック
			if (bodyChild.isBaby()) {
				hasBaby = true;
				// 子供がうんうん中ならスキップ
				if (bodyChild.isShitting()) {
					wantToShit = false;
				}
				// 各子供のうんうん量が25%以下、100%以上ならスキップ
				if (dShitPer <= 25 || 100 <= dShitPer) {
					wantToShit = false;
				}
				// 子供が空腹ならスキップ
				if (bodyChild.isHungry()) {
					wantToShit = false;
				}
			}

			//-------------------------------------
			// 子供が食事中なら何もしない
			if (bodyChild.isEating()) {
				wantToEat = false;
			}
			double dHungryPer = 100 * bodyChild.getHungry() / bodyChild.getHungryLimit();
			// 各子供の満腹度が80%以上ならスキップ
			if (dHungryPer >= 80) {
				wantToEat = false;
			} else {
				// うんうん量が多いならやらない
				if (50 < dShitPer) {
					wantToEat = false;
				}
			}
		}

		// 赤ゆがいないならうんうん体操はしない
		if (!hasBaby) {
			wantToShit = false;
		}

		// おチビちゃん運び判定
		List<Body> rideCandidates = new LinkedList<Body>();
		if (!wantToShit && !wantToEat) {
			// 子供がダメージを受けている、動けない場合は終了
			for (Body bodyChild : childrenList) {
				if (bodyChild == null || bodyChild.canAction() == false || bodyChild.isRemoved()) {
					continue;
				}
				if (bodyChild.getCurrentEvent() != null) {
					continue;
				}
				if (bodyChild.isLockmove() || !bodyChild.hasOkazari()) {
					continue;
				}
				// 子供の初回食事がすんでいない場合はやらない
				if (!bodyChild.isFirstEatStalk()) {
					break;
				}
				// 子供がうんうん中ならスキップ
				if (bodyChild.isShitting()) {
					continue;
				}
				// 子供が食事中なら何もしない
				if (bodyChild.isEating()) {
					continue;
				}
				if (!bodyChild.isBaby()) {
					continue;
				}

				// 自分と子ゆとの間に壁があるなら終了
				if (Barrier.onBarrier(body.getX(), body.getY(), bodyChild.getX(), bodyChild.getY(),
						Barrier.BARRIER_YUKKURI)) {
					continue;
				}
				rideCandidates.add(bodyChild);
			}
		}
		//-------------------------
		// 親が主体で行動を起こす
		//-------------------------

		// ・子が空腹の場合、家族一緒に餌まで移動する
		//   ・家族で移動する場合、移動速度は一番若い子ゆに合わせる
		//   ・餌まで移動した場合、一緒に食事をする
		//   ・空腹じゃなくても食べて家族で空腹度を合わせる
		if (wantToEat) {
			if (goToEat(body, childrenList)) {
				return true;
			}
		}
		// ・子がうんうんをためていた場合、家族一緒にトイレまで移動する
		//   ・トイレの近くでうんうん体操をする
		//   ・トイレにうんうんを片付ける
		//   ・少量でも出して家族でうんうん量を合わせる
		//   ・汚れていた場合ぺろぺろする
		//     ・子は親に近づく
		if (wantToShit) {
			if (goToShit(body, childrenList)) {
				return true;
			}
		}

		//おちび自慢（赤ゆがいる場合のみ）
		if (hasBaby && GameRandom.nextBoolean()) {
			if (proudChild(body, childrenList)) {
				return true;
			}
		}

		// おちびちゃん運び
		if (rideOnParent(body, rideCandidates)) {
			return true;
		}

		// 未実装
		// ・ランダムで家族でピクニック
		// ・夕方になると家族でベッド（おうち）まで移動する
		// ・夜になると眠くなくても家族よりそって寝る

		return false;
	}

	// うんうん体操
	public static final boolean goToShit(Body body, List<Body> childrenList) {
		Obj targetToilet = searchToilet(body);
		if (!body.checkWait(2000)) {
			return false;
		}
		body.setLastActionTime();
		// うんうん体操実施
		ShitExercisesEvent event = new ShitExercisesEvent(body, null, targetToilet, 10);
		EventLogic.addWorldEvent(event, body, GameMessages.getMessage(body, MessagePool.Action.ShitExercisesGOFrom));
		// イベント開始
		//b.currentEvent = ev);
		event.start(body);
		return true;
	}

	/**
	 * トイレを探す
	 * @param body ゆっくり
	 * @return 探しだしたトイレオブジェクト
	 */
	public static Obj searchToilet(Body body) {
		Obj nearestToilet = null;
		int minimumDistance = body.getEyesightBase();
		for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
			Toilet toilet = entry.getValue();
			// 最小距離のものが見つかっていたら
			if (minimumDistance < 1) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), toilet.getX(), toilet.getY() - toilet.getH() / 6);
			if (minimumDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), toilet.getX(), toilet.getY() - toilet.getH() / 6,
						Barrier.BARRIER_YUKKURI + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestToilet = toilet;
				minimumDistance = distance;
			}
		}
		return nearestToilet;
	}

	/**
	 *  食事に行く
	 * @param body ゆっくり
	 * @param childrenList 子供リスト
	 * @return 処理が行われたか
	 */
	public static final boolean goToEat(Body body, List<Body> childrenList) {
		// 餌を持っていたら落とす
		body.dropTakeoutItem(TakeoutItemType.FOOD);
		// フィールドの餌検索
		// 基本普通の餌でしかイベントは起こさない。茎があれば終了。
		Obj targetFood = searchFood(body);
		if (targetFood == null) {
			return false;
		}
		if (!body.checkWait(5000)) {
			return false;
		}
		body.setLastActionTime();
		SuperEatingTimeEvent event = new SuperEatingTimeEvent(body, null, targetFood, 10);
		EventLogic.addWorldEvent(event, body, GameMessages.getMessage(body, MessagePool.Action.FamilyEatingTimeWait));
		// イベント開始
		//b.currentEvent = ev);
		event.start(body);
		return true;
	}

	/**
	 * 餌を探す
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final Obj searchFood(Body body) {
		Obj nearestFood = null;
		int minimumDistance = body.getEyesightBase();
		int bestLooks = -1000;

		// フィールドの餌検索
		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food food = entry.getValue();
			if (food.isEmpty()) {
				continue;
			}
			// 最小距離のものが見つかっていたら
			if (minimumDistance < 1) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), food.getX(), food.getY());
			if (minimumDistance > distance) {
				// 餌と自分との間に何らかの壁があればスキップ
				if (Barrier.acrossBarrier(body.getX(), body.getY(), food.getX(), food.getY(),
						Barrier.BARRIER_YUKKURI + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				boolean isCandidate = false;
				switch (food.getFoodType()) {
				// 普通のフード
				default:
					isCandidate = true;
					break;
				// 噛み砕いた茎
				case STALK:
					isCandidate = true;
					break;
				//return null;
				// あまあま
				case SWEETS1:
				case SWEETS2:
					isCandidate = true;
					break;
				// 生ゴミ
				case WASTE:
					// 飢餓状態かバカ舌なら食べる
					if (body.isTooHungry() || body.getTangType() == TangType.POOR)
						isCandidate = true;
					break;
				}

				// 候補の中から最も価値の高いもの、近いものを食べに行く
				if (isCandidate) {
					if (bestLooks <= food.getLooks()) {
						nearestFood = food;
						minimumDistance = distance;
						bestLooks = food.getLooks();
					}
				}
			}
		}
		return nearestFood;
	}

	/**
	 *  レイパーしかいないかどうか
	 * @return レイパーしかいないかどうか
	 */
	public static final boolean checkRaperFamily() {
		boolean hasRapeTarget = isRapeTarget();
		// レイプ対象がいない
		if (!hasRapeTarget) {
			for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
				Body body = entry.getValue();
				if (body.isRaper()) {
					body.setExciting(false);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * れいぱーのターゲットかどうか
	 * @return れいぱーのターゲットかどうか
	 */
	public static final boolean isRapeTarget() {
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body body = entry.getValue();
			// レイプの対象がいる
			if (!body.isUnBirth() && !body.isDead() && !body.isRemoved() && !body.isRaper()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 親に乗る処理
	 * @param body ゆっくり
	 * @param childrenList 子供リスト
	 * @return 処理が行われたか
	 */
	public static final boolean rideOnParent(Body body, List<Body> childrenList) {
		if (childrenList == null || childrenList.size() == 0) {
			return false;
		}

		if (!body.checkWait(3000)) {
			return false;
		}
		body.setLastActionTime();
		Collections.shuffle(childrenList);
		for (Body child : childrenList) {
			if (child.isBaby() && !child.isEating() && !child.isShitting()) {
				Obj target = null;
				// 空腹
				if (target == null) {
					if (child.isHungry()) {
						if (body.getCarryItem(TakeoutItemType.FOOD) == null) {
							Obj foundFood = FamilyActionLogic.searchFood(body);
							if (foundFood != null) {
								target = foundFood;
							}
						}
					}
				}

				// トイレ
				if (target == null) {
					if (child.wantToShit()) {
						Obj foundToilet = FamilyActionLogic.searchToilet(body);
						if (foundToilet != null) {
							target = foundToilet;
						}
					}
				}

				// ベッド
				if (target == null) {
					if (child.isSleepy() || GameEnvironment.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()) {
						Obj foundBed = BedLogic.searchBed(body);
						if (foundBed != null) {
							target = foundBed;
						}
					}
				}

				// 目的地有り
				if (target != null) {
					// 近いなら運ばない
					int distance = Translate.distance(child.getX(), child.getY(), target.getX(), target.getY());
					if (distance < 10) {
						continue;
					} else {
						// おちびちゃん運び実施
						YukkuriRideEvent event = new YukkuriRideEvent(body, childrenList.get(0), target, 10);
						EventLogic.addWorldEvent(event, body, GameMessages.getMessage(body, MessagePool.Action.RideOnMe));
						// イベント開始
						//b.currentEvent = ev);
						event.start(body);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * おちび自慢処理
	 * @param body ゆっくり
	 * @param childrenList 子供リスト
	 * @return 処理が行われたか
	 */
	public static final boolean proudChild(Body body, List<Body> childrenList) {
		if (!body.checkWait(2000)) {
			return false;
		}
		body.setLastActionTime();

		// 実施
		ProudChildEvent event = new ProudChildEvent(body, null, null, 10);
		EventLogic.addWorldEvent(event, body, GameMessages.getMessage(body, MessagePool.Action.ProudChildsGOFrom));
		// イベント開始
		//b.currentEvent = ev);
		event.start(body);
		return true;
	}
}

