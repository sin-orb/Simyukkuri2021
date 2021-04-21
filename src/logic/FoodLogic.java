package src.logic;

import java.util.ArrayList;
import java.util.Random;

import src.SimYukkuri;
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
import src.yukkuri.Fran;
import src.yukkuri.Meirin;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;

/***************************************************
	餌関係の処理
 */
public class FoodLogic {

	private static final Random rnd = new Random();
	private static final int NEEDLE = 0;
	private static final int HAMMER = 0;
	private static final int HOLDMESSAGE = 0;

	// フィールド内から餌候補の検索と移動、捕食処理
	public static final boolean checkFood(Body b) {
		/*流れとしては、C1→C2→B1→B2　(Aはキャンセル判定)といった感じ
		*/

		//A.餌行動の終了
		/*		// 他の用事がある場合
				if(b.isToBody() || b.isToBed() || (b.isToShit() && !b.isSoHungry() && !(b.isAdult() && b.intelligence == Intelligence.WISE)) || ( b.isAdult() && b.isToSukkiri()) || b.isToSteal() ){
					return false;
				}*/

		// かなりの空腹状態でなく、他の用事がある場合
		if (!b.isVeryHungry()) {
			if (b.isToBody() || b.isToBed() || (b.isToShit() && b.getIntelligence() == Intelligence.WISE)
					|| (b.isAdult() && b.isToSukkiri()) || b.isToSteal()
					|| (b.isRaper() && b.isExciting())) {
				if (b.isToFood()) {
					b.setToFood(false);
				}
				return false;
			}
		}

		//眠くて、空腹でない善良飼いゆか、満腹で賢くない野良の場合
		if (b.isSleepy()) {
			if ((!b.isHungry() && b.isSmart() && b.getBodyRank() == BodyRank.KAIYU)
					|| (b.isFull() && b.getIntelligence() != Intelligence.WISE && b.getBodyRank() != BodyRank.KAIYU)) {
				return false;
			}
		}

		// 非ゆっくり症末期の場合
		if (b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease) {
			return false;
		}

		// 興奮しているれいぱーで、飢餓状態でない場合終了
		if (b.isRaper() && b.isExciting() && !b.isStarving()) {
			/*Obj o = b.getMoveTarget();
			if( o != null && o instanceof Body ){
				return false;
			}*/
			return false;
		}

		EventPacket ev = b.getCurrentEvent();
		boolean bForceEat = false;
		if (ev != null && ev.getPriority() != EventPacket.EventPriority.LOW) {
			if (ev instanceof SuperEatingTimeEvent
					&& ((SuperEatingTimeEvent) ev).getState() == SuperEatingTimeEvent.STATE.START) {
				// 食べに行く
				bForceEat = true;
			} else {
				return false;
			}
		}

		// うんうん奴隷ではない
		if (b.getPublicRank() != PublicRank.UnunSlave) {
			// うんうんを持っている場合はトイレを優先
			if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
				return false;
			}
		}

		// 埋まっていたら無視
		if (b.getBaryState() != BaryInUGState.NONE) {
			return false;
		}

		// トイレに行きたくなったら餌行動をキャンセル
		if (!b.isRude() && !b.isIdiot() && b.wantToShit() && !b.isSoHungry()) {
			b.clearActions();
			return false;
		}

		//その他
		if (b.isExciting() && !b.isRaper() && !b.isSoHungry()) {
			return false;
		}
		if (b.isExciting() && !b.isRaper() && b.isSoHungry()) {
			b.setCalm();
		}
		if (b.isSleeping() || !b.canAction()
				|| b.nearToBirth() || b.isUnBirth() || b.isShutmouth()) {
			return false;
		}

		//怖がっていたり、激しい痛みを感じている場合は50%の確率でキャンセル。
		if ((b.isScare() || b.isFeelHardPain()) && rnd.nextBoolean()) {
			return false;
		}
		// ランダムで餌の再検索
		if (rnd.nextInt(300) == 0 && !b.isEating() && !bForceEat) {
			b.clearActions();
			return false;
		}

		//B1.餌補足済みの時の特殊行動
		// 食べる対象が決まっていたら到達したかチェック
		Obj food = b.getMoveTarget();

		//対象が決まってる時
		if ((b.isToFood() || b.isToTakeout()) && food != null) {
			// 途中で消されてたら他の餌候補を探す
			if (food.isRemoved()) {
				b.clearActions();
				return false;
			}
			//茎の場合の探索
			if (food instanceof Stalk) {
				Body p = ((Stalk) food).getPlantYukkuri();
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

			// 非ゆっくり症初期の場合はあまあまだけ食べる(それ以外を受け付けない)
			if (b.isNYD()) {
				if (food instanceof Food) {
					Food f = (Food) food;
					if (f != null) {
						if (f.getFoodType() != Food.FoodType.SWEETS1 && f.getFoodType() != Food.FoodType.SWEETS2 &&
								f.getFoodType() != Food.FoodType.SWEETS_NORA1
								&& f.getFoodType() != Food.FoodType.SWEETS_NORA2 &&
								f.getFoodType() != Food.FoodType.SWEETS_YASEI1
								&& f.getFoodType() != Food.FoodType.SWEETS_YASEI2) {
							return false;
						}
					}
				}
			}

			///B2.餌補足済みの時の一般行動
			//自身がエサに到着しているとき
			if ((b.getStepDist() + 2) >= Translate.distance(b.getX(), b.getY(), food.getX(), food.getY())) {
				boolean sweets = false;
				boolean goodsweets = false;
				boolean fullmessage = false;
				// 食べる処理
				if (!b.isTalking()) {
					//餌食い
					if (food instanceof Food) {
						Food f = (Food) b.getMoveTarget();
						if (f.isEmpty()) {
							b.clearActions();
							return false;
						}
						//お持ち帰りしようとしてないか、とても空腹なとき
						if (!b.isToTakeout() || b.isVeryHungry()) {
							eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.amount));
							f.eatFood(Math.min(b.getEatAmount(), f.amount));
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
							b.clearActions();
							// お持ち帰りする
							b.setTakeoutItem(TakeoutItemType.FOOD, f);
							b.setToTakeout(true);
							// 仮メッセージ
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.TransportFood));
							b.addStress(10);
							b.stay();
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
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit));
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
							if (b.isPredatorType() && !Terrarium.predatorSteam) {
								// 捕食行動
								f.bodyInjure();
								if (b.canflyCheck() && b.getBodyAgeState().ordinal() > f.getBodyAgeState().ordinal()) {
									// 空中での捕食は特殊なのでイベントで処理
									b.clearActions();
									EventLogic.addBodyEvent(b, new FlyingEatEvent(b, f, null, 1), null, null);
								} else {
									eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
									f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()), b);
									if (f.isSick())
										b.addSickPeriod(100);
								}
								// 母のいるゆっくりを食べると母による「捕食種はあっちいってね！」イベントが発生
								if (f.getMother() != null && !f.getMother().isDead() && !f.getMother().isRemoved()) {
									f.getMother().clearEvent();
									f.getMother().setAngry();
									EventLogic.addBodyEvent(f.getMother(), new KillPredeatorEvent(f.getMother(), b, null, 1),
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
							if (f.isSick() && rnd.nextBoolean())
								b.forceSetSick();
						}
					}
					//茎食べ
					else if (food instanceof Stalk) {
						Stalk s = (Stalk) food;
						Body p = s.getPlantYukkuri();
						if (s.getZ() == 0 && p == null) {
							eatFood(b, FoodType.STALK, Math.min(b.getEatAmount(), s.amount));
							s.eatStalk(Math.min(b.getEatAmount(), s.amount));
						} else {
							if (p != null) {
								p.removeStalk(s);
								s.setPlantYukkuri(null);
								// 地中に埋まっているなら引っこ抜いて食べる
								if (p.getBaryState() == BaryInUGState.ALL ||
										(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
									b.setMessage(MessagePool.getMessage(b, MessagePool.Action.FindVegetable),
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
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), false);
								b.setEating(true);
								b.stay();
							} else {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Full), fullmessage);
								b.stay();
								b.clearActions();
							}
						}
					}
					if (!b.isbFirstEatStalk()) {
						b.setbFirstEatStalk(true);
					}
				}
			}
			//餌に未到着の時
			else {
				if (b.canflyCheck())
					b.moveTo(food.getX(), food.getY(), 0);
				else
					b.moveTo(food.getX(), food.getY(), food.getZ());
			}
			return true;
		}

		//C.餌探索
		boolean[] forceEat = { false };
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
				if (b.isPredatorType() && !Terrarium.predatorSteam) {
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
			// 非ゆっくり症初期の場合はあまあま以外は除外
			if (b.isNYD()) {
				if (found instanceof Food) {
					Food f = (Food) found;
					if (f != null) {
						if (f.getFoodType() != Food.FoodType.SWEETS1 && f.getFoodType() != Food.FoodType.SWEETS2 &&
								f.getFoodType() != Food.FoodType.SWEETS_NORA1
								&& f.getFoodType() != Food.FoodType.SWEETS_NORA2 &&
								f.getFoodType() != Food.FoodType.SWEETS_YASEI1
								&& f.getFoodType() != Food.FoodType.SWEETS_YASEI2) {
							return false;
						}
					}
				} else {
					return false;
				}
			}
			//味覚破壊されてる時。
			else if (b.isOnlyAmaama() && b.getPublicRank() != PublicRank.UnunSlave) {
				if (!b.isStarving()) {
					if (found instanceof Food) {
						Food f = (Food) found;
						if (f != null) {
							if (f.getFoodType() != Food.FoodType.SWEETS1 && f.getFoodType() != Food.FoodType.SWEETS2 &&
									f.getFoodType() != Food.FoodType.SWEETS_NORA1
									&& f.getFoodType() != Food.FoodType.SWEETS_NORA2 &&
									f.getFoodType() != Food.FoodType.SWEETS_YASEI1
									&& f.getFoodType() != Food.FoodType.SWEETS_YASEI2) {
								b.setToFood(false);
								if (b.isTooHungry()) {
									b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantAmaama));
									b.setAngry();
								} else if (rnd.nextInt(150) == 0) {
									b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantAmaama));
									//b.stay();
									b.setAngry();
								}
								return false;
							}
						}
					} else {
						return false;
					}
				}
			}
			// 発見した餌まで移動
			if (b.isHungry() || forceEat[0] || b.isToTakeout()) {
				if (!b.isTalking()) {
					int mz = 0;
					if (b.canflyCheck())
						mz = found.getZ();
					// go to nearest food
					//見つけたのがフードの時
					if (found instanceof Food) {
						if (b.isNotNYD()) {
							//見つけたのがあまあまの時
							if (((Food) found).getFoodType() == FoodType.SWEETS1
									|| ((Food) found).getFoodType() == FoodType.SWEETS2 ||
									((Food) found).getFoodType() == FoodType.SWEETS_NORA1
									|| ((Food) found).getFoodType() == FoodType.SWEETS_NORA2 ||
									((Food) found).getFoodType() == FoodType.SWEETS_YASEI1
									|| ((Food) found).getFoodType() == FoodType.SWEETS_YASEI2) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.FindAmaama));
							} else if (b.isOnlyAmaama()) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantAmaama));
							} else {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantFood));
							}
						}
						boolean takeOut = false;
						if (b.isToTakeout())
							takeOut = true;
						b.moveToFood(found, ((Food) found).getFoodType(), found.getX(), found.getY(), mz);
						if (takeOut)
							b.setToTakeout(true);
					}
					//見つけたエサがうんうんの時
					else if (found instanceof Shit) {
						// うんうん奴隷が持って帰る場合
						boolean takeOut = false;
						if (b.getPublicRank() == PublicRank.UnunSlave && b.isToTakeout()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.TransportShit), false);
							takeOut = true;
						} else {
							//　食べる場合
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
						}
						b.moveToFood(found, FoodType.SHIT, found.getX(), found.getY(), mz);
						if (takeOut)
							b.setToTakeout(true);
					}
					//見つけたエサが同族の時
					else if (found instanceof Body) {
						b.moveToFood(found, FoodType.BODY, found.getX(), found.getY(), mz);
					}
					//見つけたエサが茎の時
					else if (found instanceof Stalk) {
						b.moveToFood(found, FoodType.STALK, found.getX(), found.getY(), mz);
					}
					//見つけたエサがゲロの時
					else if (found instanceof Vomit) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
						b.moveToFood(found, FoodType.VOMIT, found.getX(), found.getY(), mz);
					}
				}
				return true;
			}
		}
		//何も見つからなかったとき
		else {
			if (b.isNotNYD()) {
				if (b.isSoHungry() && b.isLockmove()) {
					b.setToFood(false);
					if (!b.isTalking() && (rnd.nextInt(20) == 0)) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood), false);
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
		int minDistance = b.getEYESIGHT();
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (b.isFull())
			return null;

		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// フィールドの餌検索
		ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
		for (Food f : foodList) {
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
		ArrayList<Stalk> stalkList = SimYukkuri.world.currentMap.stalk;
		for (ObjEX s : stalkList) {
			Body p = ((Stalk) s).getPlantYukkuri();
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
				ArrayList<Body> babyList = ((Stalk) s).getBindBaby();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (Body baby : babyList) {
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
		ArrayList<Vomit> vomitList = SimYukkuri.world.currentMap.vomit;
		for (Vomit v : vomitList) {
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
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for (Body d : bodyList) {
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
		ArrayList<Shit> shitList = SimYukkuri.world.currentMap.shit;
		for (Shit s : shitList) {
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
		Obj found = null;
		Obj foundTakeout = null;
		int minDistance = b.getEYESIGHT();
		int looks = -1000;
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// 空腹の場合
		if (b.isSoHungry()) {
			// 餌を保持している
			if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
				// 吐き出す
				found = b.dropTakeoutItem(TakeoutItemType.FOOD);
			}
		}

		// フィールドの餌検索
		ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
		for (Food f : foodList) {
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

				boolean flag = false;
				boolean flagtakeout = false;
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
					else if (b.isRaper() && b.isExciting())
						flag = true;
					break;
				// あまあま
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					// 空腹なら食べる
					if (!b.isTooFull()) {
						flag = true;
					}
					// 普通以下なら満腹でも食べに行く
					else if (!b.isOverEating() && (b.isRude() || b.isNormal())) {
						flag = true;
						forceEat[0] = true;
					} else {
						// 善良で空腹ではないなら持って帰る
						flagtakeout = true;
					}
					break;
				// 生ゴミ
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					// 飢餓状態なら食べる
					if (b.getTangType() == TangType.GOURMET && b.isStarving())
						flag = true;
					else if (b.getTangType() == TangType.NORMAL && b.isTooHungry())
						flag = true;
					else if (b.getTangType() == TangType.POOR) {
						// バカ舌で空腹なら食べる
						if (b.isHungry()) {
							flag = true;
						} else {
							flagtakeout = true;
						}
					}
					break;
				// 普通のフード
				default:
					// 空腹なら食べる
					if (b.isHungry()) {
						flag = true;
					} else {
						flagtakeout = true;
					}
					break;
				}

				// 候補の中から最も価値の高いもの、近いものを食べに行く
				if (flag) {
					if (looks <= f.getLooks()) {
						found = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
				if (flagtakeout) {
					if (looks <= f.getLooks()) {
						foundTakeout = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
			}
		}

		if (foundTakeout != null) {
			// 他に餌を保持していないなら
			if (b.getTakeoutItem(TakeoutItemType.FOOD) == null) {
				if (checkTakeout(b, foundTakeout)) {
					// お持ち帰りする
					b.setToTakeout(true);
					return foundTakeout;
				}
			}
		}
		//ここまでで餌がみつからず、かつ満腹ならリターン
		if (found == null && b.isFull())
			return null;

		// 非常食検索
		//第一候補：茎
		ArrayList<Stalk> stalkList = SimYukkuri.world.currentMap.stalk;
		for (ObjEX s : stalkList) {
			Body p = ((Stalk) s).getPlantYukkuri();
			if (p != null) {
				if (p == b) {
					continue;
				}
				// 地中に埋まっていないならスキップ
				if (p.getBaryState() != BaryInUGState.ALL &&
						!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
					continue;
				}
				// 通常は実ゆつきは食べない
				ArrayList<Body> babyList = ((Stalk) s).getBindBaby();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (Body baby : babyList) {
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

		//第二候補：吐餡
		if (found == null) {
			ArrayList<Vomit> vomitList = SimYukkuri.world.currentMap.vomit;
			for (Vomit v : vomitList) {
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
		//第三候補：死体
		if (found == null) {
			ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
			for (Body d : bodyList) {
				if (d == null || d.isRemoved()) {
					continue;
				}
				if (b == d)
					continue;
				//れいぱーの時
				if (b.isRaper()) {
					// 生きてて実ゆではないならスキップ
					if (!d.isDead() && !d.isUnBirth()) {
						continue;
					}
				} else {
					if (!checkCanEatBody(b, d))
						continue;
				}
				//茎付きは茎を優先
				if (d.isbindStalk())
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
		}
		//第四候補:うんうん
		if (found == null) {
			ArrayList<Shit> shitList = SimYukkuri.world.currentMap.shit;
			for (Shit s : shitList) {
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

	// 餌検索C
	// 捕食種用
	public static final Obj searchFoodPredetor(Body b, boolean[] forceEat) {
		Obj found = null;
		Obj found2 = null; // 副候補
		Obj found3 = null; // 死体候補
		int minDistance = b.getEYESIGHT();
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
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for (Body d : bodyList) {
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
		ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
		for (Food f : foodList) {
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
		ArrayList<Stalk> stalkList = SimYukkuri.world.currentMap.stalk;
		for (ObjEX s : stalkList) {
			Body p = ((Stalk) s).getPlantYukkuri();
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
				ArrayList<Body> babyList = ((Stalk) s).getBindBaby();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (Body baby : babyList) {
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
			ArrayList<Vomit> vomitList = SimYukkuri.world.currentMap.vomit;
			for (Vomit v : vomitList) {
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
			ArrayList<Shit> shitList = SimYukkuri.world.currentMap.shit;
			for (Shit s : shitList) {
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
		Obj found = null;
		int minDistance = b.getEYESIGHT();
		//		int looks = -1000;
		int wallMode = b.getBodyAgeState().ordinal();

		forceEat[0] = false;

		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// うんうん奴隷ではない場合
		if (b.getPublicRank() != PublicRank.UnunSlave)
			return null;

		// かなり空腹の場合
		if (b.isVeryHungry()) {
			// うんうんを保持している
			if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
				// 吐き出す
				b.dropTakeoutItem(TakeoutItemType.SHIT);
			}
		}

		if (found == null) {
			ArrayList<Shit> shitList = SimYukkuri.world.currentMap.shit;
			for (Shit s : shitList) {
				// 最小距離のものが見つかっていたら
				if (minDistance < 1) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (checkTakeout(b, s)) {
						ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
						boolean bOtherTarget = false;
						// 自分以外のゆっくりが処理対象にしていないか
						for (Body bodyOther : bodyList) {
							if (b == bodyOther || bodyOther == null || bodyOther.isDead() || bodyOther.isRemoved()) {
								continue;
							}
							Obj objTarget = bodyOther.getMoveTarget();
							if (s == objTarget) {
								bOtherTarget = true;
								break;
							}
						}
						if (bOtherTarget) {
							continue;
						}
						// お持ち帰りする
						b.setToTakeout(true);
						found = s;
					}
					// お持ち帰り指定がなければその他のうんうんを食べに行く
					if (!b.isToTakeout()) {
						found = s;
					}
					minDistance = distance;
				}
			}
		}

		if (found == null) {
			ArrayList<Vomit> vomitList = SimYukkuri.world.currentMap.vomit;
			for (Vomit v : vomitList) {
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
			ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
			for (Body d : bodyList) {
				if (b == d)
					continue;
				if (!checkCanEatBody(b, d))
					continue;
				//　飢餓状態なら食べる
				if (!b.isSoHungry() || !b.isTooHungry()) {
					break;
				}
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
		}

		if (found == null) {
			ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
			for (Food f : foodList) {
				if (f.isEmpty()) {
					continue;
				}
				int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (f.getFoodType() == Food.FoodType.WASTE ||
							f.getFoodType() == Food.FoodType.WASTE_NORA ||
							f.getFoodType() == Food.FoodType.WASTE_YASEI) {
						// 飢餓状態なら食べる
						if (b.isTooHungry()) {
							found = f;
							break;
						}
					}
				}
			}
		}
		return found;
	}

	// 食事処理
	public static final void eatFood(Body b, FoodType foodType, int amount) {
		if (b.isDead()) {
			return;
		}
		b.setToTakeout(false);
		//味覚破壊されてる時。
		if (b.isOnlyAmaama()) {
			switch (foodType) {
			case BODY:
			case SWEETS1:
			case SWEETS_NORA1:
			case SWEETS_YASEI1:
			case SWEETS2:
			case SWEETS_NORA2:
			case SWEETS_YASEI2:
				break;
			//あまあま以外だと吐く
			default:
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SpitFood));
				b.setHappiness(Happiness.VERY_SAD);
				SimYukkuri.mypane.terrarium.addVomit(b.getX() + 7 - rnd.nextInt(14), b.getY() + 7 - rnd.nextInt(14), 0,
						b, b.getShitType());
				return;
			}
		}
		// 餌タイプ別のリアクションとステータス変化
		switch (b.getTangType()) {
		// バカ舌
		case POOR:
			poorEating(b, foodType);
			break;
		// 普通
		case NORMAL:
			normalEating(b, foodType);
			break;
		// 美食
		case GOURMET:
			gourmetEating(b, foodType);
			break;
		default:
			break;
		}
		// 食事実行
		int eatAmount = Math.min(b.getEatAmount(), amount);
		b.eatFood(eatAmount);
		b.checkTang();
	}

	public static int[][] anLovePoint = {
			// バカ舌,	普通,	肥えてる
			{ -50, -100, -500 }, //	SHIT　うんうん
			{ -1, -30, -50 }, //	BITTER
			{ 50, 10, 0 }, //	LEMONPOP
			{ -200, -200, -400 }, //	HOTPOINT
			{ 0, 0, 0 }, //	VIYUGRA
			{ 100, 10, 0 }, //	BODY
			{ 0, 0, 0 }, //	STALK ちぎれた茎
			{ 500, 100, 10 }, //	SWEETS1
			{ 1000, 500, 50 }, //	SWEETS2
			{ 10, -50, -200 }, //	WASTE　生ゴミ
			{ 0, 0, 0 }, //	VOMIT　吐餡
			{ 30, 10, 0 } //	その他
	};

	// バカ舌状態でのリアクション
	private static final void poorEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:// うんうん
			b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(100);
			b.addTang(-10);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[0][0]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.VERY_HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.addTang(200);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[1][0]);
			} else {
				b.strike(NEEDLE * 2);
				b.setHappiness(Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(250);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(anLovePoint[1][0]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-500);
			b.addTang(50);
			// なつき度設定
			b.addLovePlayer(anLovePoint[2][0]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (b.isLikeHotFood()) {
				b.setHappiness(Happiness.VERY_HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.addTang(200);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[3][0]);
			} else {
				b.strike(HAMMER >> 1);
				b.setHappiness(Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(500);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(anLovePoint[3][0]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && rnd.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(anLovePoint[4][0]);
			break;
		case BODY:// 生け餌
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.addStress(-500);
			b.addTang(50);
			b.addMemories(5);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[5][0]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-500);
			b.addDamage(-500);
			b.addMemories(20);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[6][0]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(200);
			b.addMemories(30);
			// なつき度設定
			b.addLovePlayer(anLovePoint[7][0]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(50);
			// なつき度設定
			b.addLovePlayer(anLovePoint[8][0]);
			break;
		case WASTE:// 生ゴミ
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(-100);
			b.addTang(-30);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[9][0]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(100);
			// なつき度設定
			b.addLovePlayer(anLovePoint[10][0]);
			break;
		default:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.addStress(-300);
			b.addTang(100);
			b.addMemories(1);
			break;
		}
	}

	// 普通状態でのリアクション
	private static final void normalEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:
			b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(200);
			b.addTang(-10);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[0][1]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[1][1]);
			} else {
				b.strike(NEEDLE * 4);
				b.setHappiness(Happiness.SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(300);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(anLovePoint[1][1]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-200);
			b.addTang(20);
			// なつき度設定
			b.addLovePlayer(anLovePoint[2][1]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (!b.isLikeHotFood()) {
				b.strike(HAMMER);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(800);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(anLovePoint[3][1]);
			} else {
				b.setHappiness(Happiness.HAPPY);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[3][1]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && rnd.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(anLovePoint[4][1]);
			break;
		case BODY:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addMemories(1);
			if (!b.isPredatorType()) {
				b.addTang(10);
			}
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[5][1]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-200);
			b.addDamage(-500);
			b.addMemories(20);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[6][1]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(100);
			b.addMemories(30);
			// なつき度設定
			b.addLovePlayer(anLovePoint[7][1]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(50);
			// なつき度設定
			b.addLovePlayer(anLovePoint[8][1]);
			break;
		case WASTE:
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.setStrike(true);
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(100);
			b.addTang(-30);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[9][1]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			if (!b.isPredatorType()) {
				b.addTang(50);
			}
			// なつき度設定
			b.addLovePlayer(anLovePoint[10][1]);
			break;
		default:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addMemories(1);
			break;
		}
	}

	// 肥え状態でのリアクション
	private static final void gourmetEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:
			b.setHappiness(Happiness.VERY_SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(500);
			b.addTang(-20);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[0][2]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.AVERAGE);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.addTang(-20);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[1][2]);
			} else {
				b.strike(NEEDLE * 6);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(300);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(anLovePoint[1][2]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			if (b.isRude())
				b.setHappiness(Happiness.VERY_SAD);
			else
				b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.forceToSleep();
			b.addStress(10);
			b.addTang(-20);
			// なつき度設定
			b.addLovePlayer(anLovePoint[2][2]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (b.isLikeHotFood()) {
				b.setHappiness(Happiness.AVERAGE);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.addTang(-20);
				// なつき度設定
				b.addLovePlayer(-1 * anLovePoint[3][2]);
			} else {
				b.strike(HAMMER * 2);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(1200);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(anLovePoint[3][2]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			if (b.isRude())
				b.setHappiness(Happiness.VERY_SAD);
			else
				b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && rnd.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(anLovePoint[4][2]);
			break;
		case BODY:
			b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.addStress(100);
			b.addTang(10);
			b.addMemories(1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[5][2]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Eating));
			b.addStress(10);
			b.addDamage(-500);
			b.addMemories(20);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[6][2]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama));
			b.setStress(0);
			b.addTang(100);
			b.addMemories(30);
			// なつき度設定
			b.addLovePlayer(anLovePoint[7][2]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(50);
			// なつき度設定
			b.addLovePlayer(anLovePoint[8][2]);
			break;
		case WASTE:
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.VERY_SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBitter));
			b.setStrike(true);
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(100);
			b.addTang(-50);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(anLovePoint[9][2]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.addStress(20);
			if (!b.isPredatorType()) {
				b.addTang(50);
			}
			// なつき度設定
			b.addLovePlayer(anLovePoint[10][2]);
			break;
		default:
			if (b.isRude())
				b.setHappiness(Happiness.VERY_SAD);
			else
				b.setHappiness(Happiness.SAD);
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.addStress(50);
			b.addTang(-10);
			b.addMemories(1);
			break;
		}
	}

	//お持ち帰り判定
	public static boolean checkTakeout(Body b, Obj o) {
		if (b == null || o == null) {
			return false;
		}
		if (b.isVeryHungry()) {
			return false;
		}

		// うんうん奴隷の場合
		if (b.getPublicRank() == PublicRank.UnunSlave) {
			if (o instanceof Shit) {
				// 他にうんうんを保持している
				if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
					return false;
				}
				Shit s = (Shit) o;
				boolean bIsInToiletForSlave = false;
				boolean bIsToiletForSlave = false;
				ArrayList<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
				if (toiletList != null && toiletList.size() != 0) {
					for (Toilet t : toiletList) {
						// うんうん奴隷用トイレのどれかにあれば終了
						if (t.isForSlave()) {
							bIsToiletForSlave = true;
							if ((t.checkHitObj(null, s))) {
								bIsInToiletForSlave = true;
								break;
							}
						}
					}
				}
				// うんうん奴隷用トイレが存在して、そこにないならお持ち帰り
				if ((bIsToiletForSlave) && !bIsInToiletForSlave) {
					return true;
				}
				return false;
			}
		}
		//うんうん奴隷でない場合
		else {
			if (b.isExciting() || b.isRaper()) {
				return false;
			}
			//対象がエサの時のみ
			if (o instanceof Food) {
				// 空なら無視
				if (((Food) o).isEmpty()) {
					return false;
				}
				// 他に餌を保持している
				if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
					return false;
				}
				// ベッドがお気に入りにないなら持って帰らない
				Obj oFav = b.getFavItem(FavItemType.BED);
				if (oFav == null || !(oFav instanceof ObjEX)) {
					return false;
				}
				ObjEX oExFav = (ObjEX) oFav;
				// 家族がいる
				if (b.getPartner() != null || b.getChildrenListSize() != 0) {
					ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
					if (foodList != null && foodList.size() != 0) {
						for (Food foodOnMyBed : foodList) {
							// 空なら無視
							if (foodOnMyBed.isEmpty()) {
								continue;
							}
							// お気に入りのベッド上にご飯があれば終了
							if (oExFav.checkHitObj(foodOnMyBed, false)) {
								return false;
							}
						}
					}
					// ベッドの上にない餌を対象にする
					boolean bIsOnbed = false;
					ArrayList<Bed> bedList = SimYukkuri.world.currentMap.bed;
					if (bedList != null && bedList.size() != 0) {
						for (Bed bed : bedList) {
							if (bed.checkHitObj(o, false)) {
								bIsOnbed = true;
								break;
							}
						}
					}
					if (!bIsOnbed) {
						return true;
					}
				}
			}
		}
		return false;
	}

	//死体食べ判定
	public static boolean checkCanEatBody(Body b, Body p) {
		if (b.isPredatorType())
			return true;
		if (!p.isDead())
			return false;
		if (p.isbindStalk())
			return false;
		if (!b.isVeryRude() && p.hasOkazari())
			return false;
		//バカかよっぽどの飢餓状態じゃなきゃかびゆは食べない
		if (b.getIntelligence() != Intelligence.FOOL && b.findSick(p) && !b.isTooHungry())
			return false;
		return true;
	}

}
