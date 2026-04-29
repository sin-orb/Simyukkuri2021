package src.logic;
import src.util.GameView;
import src.util.GameEnvironment;
import src.util.GameMessages;

import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.BodyRank;
import src.enums.CoreAnkoState;
import src.enums.FavItemType;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.enums.PurposeOfMoving;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.event.EatBodyEvent;
import src.event.FlyingEatEvent;
import src.event.KillPredeatorEvent;
import src.event.SuperEatingTimeEvent;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Bed;
import src.item.Food;
import src.item.Food.FoodType;
import src.item.Toilet;
import src.system.MessagePool;
import src.util.YukkuriUtil;
import src.yukkuri.Fran;
import src.yukkuri.Meirin;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;

/***************************************************
	餌関係の処理
 */
public class FoodLogic {


	/**
	 *  フィールド内から餌候補の検索と移動、捕食処理
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkFood(Body b) {
		/*流れとしては、C1→C2→B1→B2　(Aはキャンセル判定)といった感じ
		*/
		boolean[] forceEat = { false };

		//A.餌行動の終了
		/*		// 他の用事がある場合
				if(b.isToBody() || b.isToBed() || (b.isToShit() && !b.isSoHungry() && !(b.isAdult() && b.intelligence == Intelligence.WISE)) || ( b.isAdult() && b.isToSukkiri()) || b.isToSteal() ){
					return false;
				}*/

		if (FoodActionGate.shouldSkipBeforeSearch(b, forceEat)) {
			return false;
		}

		//B1.餌補足済みの時の特殊行動
		// 食べる対象が決まっていたら到達したかチェック
		Obj food = b.takeMoveTarget();

		//対象が決まってる時
		if ((b.isToFood() || b.isToTakeout()) && food != null) {
			// 途中で消されてたら他の餌候補を探す
			if (food.isRemoved()) {
				b.clearActions();
				return false;
			}
			//茎の場合の探索
			if (food instanceof Stalk) {
				Body p = GameWorld.get().getCurrentMap().getBody().get(((Stalk) food).getPlantYukkuri());
				// 自分の茎は無視
				if (p == b) {
					b.clearActions();
					return false;
				}
				if (p != null) {
					// 地中に埋まっているなら引っこ抜いて食べる。それ以外はスキップ
					if (p.getBaryState() != BaryInUGState.ALL &&
							!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
						b.clearActions();
						return false;
					}
				}
			}
			// 食べることができなかったら他の餌候補を探す
			else {
				if (!b.canflyCheck() && food.getZ() != 0) {
					b.clearActions();
					return false;
				}
			}

			///B2.餌補足済みの時の一般行動
			//自身がエサに到着しているとき
			if ((b.getStepDist() + 2) >= Translate.distance(b.getX(), b.getY(), food.getX(), food.getY())) {
				boolean sweets = false;
				boolean goodsweets = false;
				boolean fullmessage = false;
				// 食べる処理
				//if (!b.isTalking()) {
					//餌食い
					if (food instanceof Food) {
						Food f = (Food) b.takeMoveTarget();
						if (f.isEmpty()) {
							b.clearActions();
							return false;
						}
						//お持ち帰りしようとしてないか、とても空腹なとき
						if (!b.isToTakeout() || b.isVeryHungry()) {
							eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.getAmount()));
							f.eatFood(Math.min(b.getEatAmount(), f.getAmount()));
							// 食べ切ったら消滅
							if (f.getFoodType() == FoodType.STALK && f.isEmpty())
								f.remove();
							// あまあま
							if (f.getFoodType() == FoodType.SWEETS1 || f.getFoodType() == FoodType.SWEETS2 ||
									f.getFoodType() == FoodType.SWEETS_NORA1 || f.getFoodType() == FoodType.SWEETS_NORA2
									||
									f.getFoodType() == FoodType.SWEETS_YASEI1
									|| f.getFoodType() == FoodType.SWEETS_YASEI2) {
								sweets = true;
							}
							//高級あまあま
							if (f.getFoodType() == FoodType.SWEETS2 ||
									f.getFoodType() == FoodType.SWEETS_NORA2 ||
									f.getFoodType() == FoodType.SWEETS_YASEI2) {
								goodsweets = true;
							}
							// 満腹セリフでピコピコするかどうか
							if (f.getFoodType() != FoodType.STALK &&
									f.getFoodType() != FoodType.BITTER && f.getFoodType() != FoodType.HOT
									&& f.getFoodType() != FoodType.WASTE &&
									f.getFoodType() != FoodType.BITTER_NORA && f.getFoodType() != FoodType.HOT_NORA
									&& f.getFoodType() != FoodType.WASTE_NORA &&
									f.getFoodType() != FoodType.BITTER_YASEI && f.getFoodType() != FoodType.HOT_YASEI
									&& f.getFoodType() != FoodType.WASTE_YASEI ||
									(f.getFoodType() == FoodType.WASTE && b.getTangType() == TangType.POOR) ||
									(f.getFoodType() == FoodType.WASTE_NORA && b.getTangType() == TangType.POOR) ||
									(f.getFoodType() == FoodType.WASTE_YASEI && b.getTangType() == TangType.POOR)) {
								fullmessage = true;
							}
						} else {
							boolean alreadyTakenOut = false;
							for (Map.Entry<TakeoutItemType, Integer> entry : b.getTakeoutItem().entrySet()) {
								TakeoutItemType t = entry.getKey();
								if (t == TakeoutItemType.FOOD) {
									// すでにふーどをお持ち帰りしてる
									alreadyTakenOut = true;
									break;
								}
							}
							if (!alreadyTakenOut) {
								b.clearActions();
								// お持ち帰りする
								b.setTakeoutItem(TakeoutItemType.FOOD, f);
								b.setToTakeout(true);
								// 仮メッセージ
								b.setMessage(GameMessages.getMessage(b, MessagePool.Action.TransportFood));
								b.addStress(10);
								b.stay();
							} else {
								b.setToTakeout(false);
								b.setPurposeOfMoving(PurposeOfMoving.NONE);
							}
						}
					}
					//糞食い
					else if (food instanceof Shit) {
						Shit f = (Shit) food;
						if (!b.isToTakeout()) {
							eatFood(b, FoodType.SHIT, b.getEatAmount());
							f.eatShit(b.getEatAmount());
						} else {
							// お持ち帰りする
							b.setTakeoutItem(TakeoutItemType.SHIT, f);
							b.clearActions();
							b.setToTakeout(true);
							// うんうん奴隷の場合
							if (b.getPublicRank() == PublicRank.UnunSlave) {
								b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateShit));
								b.addStress(20);
								b.stay();
							}
						}
					}
					//ゆっくり食い
					else if (food instanceof Body) {
						Body f = (Body) food;
						//生餌
						if (!f.isDead()) {
							//捕食種の場合
							if (b.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
								// 捕食行動
								f.bodyInjure();
								if (b.canflyCheck()) {// && b.getBodyAgeState().ordinal() > f.getBodyAgeState().ordinal()) {
									// 空中での捕食は特殊なのでイベントで処理
									b.clearActions();
									EventLogic.addBodyEvent(b, new FlyingEatEvent(b, f, null, 1), null, null);
								} else {
									eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
									f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()), b);
									if (f.isSick())
										b.addSickPeriod(100);
								}
								// 母のいるゆっくりを食べると33%の確率で母による「捕食種はあっちいってね！」イベントが発生
								Body m = YukkuriUtil.getBodyInstance(f.getMother());
								if (GameRandom.nextInt(3) == 0 && m != null && !m.isDead() && !m.isRemoved()) {
									m.clearEvent();
									m.setPanic(false, null);
									m.setPeropero(false);
									m.setAngry();
									EventLogic.addBodyEvent(m, new KillPredeatorEvent(m, b, null, 10),
											null, null);
								}
							} else {
								// レイパーなら実ゆは食べる
								if (b.isRaper() && f.isUnBirth()) {
									eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
									f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()), b);
									if (f.isSick())
										b.addSickPeriod(100);
								} else {
									// 生き返ってたらキャンセル
									b.setPurposeOfMoving(PurposeOfMoving.NONE);
									b.clearActions();
									return false;
								}
							}
						}
						// 死体食べ
						else {
							eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
							f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()));
							if (!checkCanEatBody(b, f)) {
								b.clearActions();
								EventLogic.addBodyEvent(b, new EatBodyEvent(b, f, null, 30), null, null);
							}
							if (f.isSick() && GameRandom.nextBoolean())
								b.forceSetSick();
						}
					}
					//茎食べ
					else if (food instanceof Stalk) {
						Stalk s = (Stalk) food;
						Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
						if (s.getZ() == 0 && p == null) {
							eatFood(b, FoodType.STALK, Math.min(b.getEatAmount(), s.getAmount()));
							s.eatStalk(Math.min(b.getEatAmount(), s.getAmount()));
						} else {
							if (p != null) {
								p.removeStalk(s);
								s.setPlantYukkuri(null);
								// 地中に埋まっているなら引っこ抜いて食べる
								if (p.getBaryState() == BaryInUGState.ALL ||
										(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
									b.setMessage(GameMessages.getMessage(b, MessagePool.Action.FindVegetable),
											fullmessage);
									b.setHappiness(Happiness.VERY_HAPPY);
								}
								b.stay();
							}
						}
					} else if (food instanceof Vomit) {
						Vomit f = (Vomit) food;
						eatFood(b, FoodType.VOMIT, b.getEatAmount());
						f.eatVomit(b.getEatAmount());
					}

					//あまあまへの慣れの増減
					if (goodsweets) {
						b.addAmaamaDiscipline(5);
					} else if (sweets) {
						b.addAmaamaDiscipline(3);
					} else if (food instanceof Body) {
						b.addAmaamaDiscipline(1);
					} else {
						b.addAmaamaDiscipline(-1);
					}
					// 満腹用セリフの有無の判定
					if (b.isFull()) {
						if (b.isNotNYD()) {
							if (sweets) {
								b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), false);
								b.setEating(true);
								b.stay();
							} else {
								b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Full), fullmessage);
								b.stay();
								b.clearActions();
							}
						}
					}
					if (!b.isbFirstEatStalk()) {
						b.setbFirstEatStalk(true);
					}
				//}
			}
			//餌に未到着の時
			else {
				if (!b.canflyCheck())
					b.moveTo(food.getX(), food.getY(), 0);
				else
					b.moveTo(food.getX(), food.getY(), food.getZ());
			}
			return true;
		}

		//C.餌探索
		Obj found = null;
		// うんうん奴隷の場合
		if (b.getPublicRank() == PublicRank.UnunSlave) {
			found = searchFoodForUnunSlave(b, forceEat);
		} else {
			// フィールドの餌検索
			if (b.isIdiot() || (b.getFootBakeLevel() == FootBake.CRITICAL && !b.canflyCheck())) {
				// 足りないゆ、完全足焼き用
				found = searchFoodNearlest(b, forceEat);
			} else {
				if (b.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					// 捕食種用
					found = searchFoodPredetor(b, forceEat);
				} else {
					// 通常種用
					found = searchFoodStandard(b, forceEat);
				}
			}
		}

		//C2.探索して補足した餌に対する反応
		if (found != null) {
			return FoodFoundReaction.handleFoundFood(b, found, forceEat);
		}
		//何も見つからなかったとき
		else {
			if (b.isNotNYD()) {
				if (b.isSoHungry() && b.isLockmove()) {
					b.setToFood(false);
					if (!b.isTalking() && (GameRandom.nextInt(20) == 0)) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.NoFood), false);
						b.stay();
					}
					b.setHappiness(Happiness.SAD);
				}
			}
		}
		return false;
	}

	// 餌検索A
	// 足りないゆ、足焼き用 最も近いものを適当に食べる
	private static final Obj searchFoodNearlest(Body b, boolean[] forceEat) {
		Obj found = null;
		int minDistance = b.getEYESIGHTorg();
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (b.isFull())
			return null;

		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// フィールドの餌検索
		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food f = entry.getValue();
			if (f.isEmpty()) {
				continue;
			}
			// 最小距離のものが見つかっていたら
			if (minDistance < 1) {
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = f;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk s = entry.getValue();
			Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (p != null) {
				if (p == b) {
					continue;
				}
				// 地中に埋まっているなら引っこ抜いて食べる
				if (p.getBaryState() != BaryInUGState.ALL &&
						!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
					continue;
				}

				// 通常は実ゆつきは食べない
				List<Integer> babyList = ((Stalk) s).getBindBabies();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (int ibaby : babyList) {
						Body baby = YukkuriUtil.getBodyInstance(ibaby);
						if (baby == null) {
							continue;
						}
						bBabyFlag = true;
						break;
					}
					if (bBabyFlag) {
						continue;
					}
				}
			}
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
			Vomit v = entry.getValue();
			int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = v;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body d = entry.getValue();
			if (b == d)
				continue;
			if (!checkCanEatBody(b, d))
				continue;
			int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = d;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
			Shit s = entry.getValue();
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		return found;
	}

	// 餌検索B
	// 一般用
	public static final Obj searchFoodStandard(Body b, boolean[] forceEat) {
		return FoodSearchPolicy.searchFoodStandard(b, forceEat);
	}

	// 餌検索C
	/**
	 *  捕食種用エサ検索
	 * @param b 捕食種
	 * @param forceEat 強制給餌フラグ
	 * @return 検索されたエサオブジェクト
	 */
	public static final Obj searchFoodPredetor(Body b, boolean[] forceEat) {
		Obj found = null;
		Obj found2 = null; // 副候補
		Obj found3 = null; // 死体候補
		int minDistance = b.getEYESIGHTorg();
		int minDistance2 = minDistance;
		int minDistance3 = minDistance;
		int size = b.getBodyAgeState().ordinal();
		int looks = -1000;
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// ゆっくりから検索
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body d = entry.getValue();
			if (b == d)
				continue;
			//バカかよっぽどの飢餓状態じゃなきゃかびゆは食べない
			if (b.getIntelligence() != Intelligence.FOOL && b.findSick(d) && !b.isTooHungry())
				continue;
			//生餌
			if (!d.isDead()) {
				// 捕食種は食べない
				if (d.isPredatorType())
					continue;
				// 家族は食べない
				if (b.isFamily(d))
					continue;
				// 自分が飛べなかったら空中のは食べない
				if (!b.canflyCheck() && d.getZ() != 0)
					continue;
				/// れみりゃやふらんはさくや、めーりんを食べない
				if ((d.getType() == Sakuya.type || d.getType() == Meirin.type)
						&& (b.getType() == Remirya.type || b.getType() == Fran.type))
					continue;
				// 最小距離のものが見つかっていたら
				if (minDistance < 1) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
				if (d.getBodyAgeState().ordinal() < b.getBodyAgeState().ordinal()) {
					// 自分より小さい相手の場合
					if (minDistance > distance || d.getBodyAgeState().ordinal() < size) {
						if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
								Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						found = d;
						minDistance = distance;
						size = d.getBodyAgeState().ordinal();
					}
				} else {
					// 自分より同等以上の相手
					if (minDistance2 > distance) {
						if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
								Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						found2 = d;
						minDistance2 = distance;
					}
				}
			} else {
				// 死体は第三候補
				// ゲス以外は家族の死体は食べない
				if (!b.isRude() && d.hasOkazari() && b.isFamily(d))
					continue;
				int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
				if (minDistance3 > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					found3 = d;
					minDistance3 = distance;
				}
			}
		}
		// 自分より小さい相手がいなかったら副目標にする
		if (found == null)
			found = found2;

		// フィールドの餌検索
		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food f = entry.getValue();
			if (f.isEmpty()) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				boolean flag = false;
				switch (f.getFoodType()) {
				// 噛み砕いた茎
				case STALK:
					// 生まれたての赤ゆなら必ず食べる
					if (b.isBaby()) {
						if (!b.isbFirstEatStalk()) {
							flag = true;
							forceEat[0] = true;
						} else if (b.isHungry()) {
							flag = true;
						}
					}
					// ゲスで空腹なら食べる
					else if (b.isRude() && b.isSoHungry())
						flag = true;
					// 普通でもとても空腹なら食べる
					else if (!b.isRude() && b.isVeryHungry())
						flag = true;
					// レイパーなら食べる
					else if (!b.isRude() && b.isRaper())
						flag = true;
					break;
				// あまあま
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					// 満腹以上でないなら食べる
					if (!b.isTooFull()) {
						flag = true;
					}
					// 普通以下なら満腹でも食べに行く
					else if (!b.isOverEating() && (b.isRude() || b.isNormal())) {
						flag = true;
						forceEat[0] = true;
					}
					break;
				// 生ゴミ
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					// 飢餓状態かバカ舌なら食べる
					if (b.isTooHungry() || b.getTangType() == TangType.POOR)
						flag = true;
					break;
				// 普通のフード
				default:
					// 空腹なら食べる
					if (!b.isFull())
						flag = true;
					break;
				}

				// 候補の中から最も価値の高いもの、近いものを食べに行く
				if (flag) {
					if (looks <= f.getLooks()) {
						found3 = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
			}
		}

		// 賢い個体や瀕死の場合は動かないものを優先して食べる
		if (found3 != null && (found == null || b.getIntelligence() == Intelligence.WISE || b.isDamaged())) {
			found = found3;
		}
		// 強制食事フラグオン
		if (found3 != null && forceEat[0]) {
			found = found3;
		}

		if (found == null && b.isFull()) {
			return found;
		}

		// 非常食検索
		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk s = entry.getValue();
			Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (p != null) {
				if (p == b) {
					continue;
				}
				// 地中に埋まっているなら引っこ抜いて食べる
				if (p.getBaryState() != BaryInUGState.ALL &&
						!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
					continue;
				}

				// 通常は実ゆつきは食べない
				List<Integer> babyList = ((Stalk) s).getBindBabies();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (int ibaby : babyList) {
						Body baby = YukkuriUtil.getBodyInstance(ibaby);
						if (baby == null) {
							continue;
						}
						bBabyFlag = true;
						break;
					}
					if (bBabyFlag) {
						continue;
					}
				}
			}

			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		// 死体を見つけていたら食べる
		if (found == null)
			found = found3;

		if (found == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit v = entry.getValue();
				int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					found = v;
					minDistance = distance;
				}
			}
		}
		if (found == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit s = entry.getValue();
				if (!b.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					found = s;
					minDistance = distance;
				}
			}
		}
		return found;
	}

	// 餌検索D
	// うんうん奴隷用
	private static final Obj searchFoodForUnunSlave(Body b, boolean[] forceEat) {
		return FoodUnunSlaveSearchPolicy.searchFoodForUnunSlave(b, forceEat);
	}

	/**
	 *  食事処理
	 * @param b ゆっくり
	 * @param foodType エサタイプ
	 * @param amount 食事量
	 */
	public static final void eatFood(Body b, FoodType foodType, int amount) {
		FoodConsumptionPolicy.eatFood(b, foodType, amount);
	}

	/**
	 * お持ち帰り判定
	 * @param b ゆっくり
	 * @param o エサオブジェクト
	 * @return 持ち帰るかどうか
	 */
	public static boolean checkTakeout(Body b, Obj o) {
		return FoodTakeoutPolicy.checkTakeout(b, o);
	}

	/**
	 * 死体食べ判定
	 * @param b ゆっくり
	 * @param p 死体
	 * @return 食べるかどうか
	 */
	public static boolean checkCanEatBody(Body b, Body p) {
		return FoodEligibility.checkCanEatBody(b, p);
	}

}
