package src.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import src.SimYukkuri;
import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.BreedingAmpoule;
import src.attachment.HungryAmpoule;
import src.attachment.OrangeAmpoule;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetMenu.GadgetList;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.BodyRank;
import src.enums.Direction;
import src.enums.PublicRank;
import src.event.PredatorsGameEvent;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.BeltconveyorObj;
import src.item.Diffuser;
import src.item.OrangePool;
import src.item.Pool;
import src.item.BreedingPool;
import src.item.Farm;
import src.item.Barrier;
import src.item.Beltconveyor;
import src.item.Yunba;
import src.logic.BadgeLogic;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.system.Cash;
import src.util.GameView;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.util.GameEnvironment;
import src.util.GameWorld;

final class GadgetToolAction {

	private GadgetToolAction() {
	}

	static void evaluateTool(GadgetList item, MouseEvent ev, Obj targetObject) {
		switch (item) {
			case PUNISH:
				GadgetMenu.executeBodyMethod(ev, targetObject, "strikeByPunish");
				break;
			case SNAPPING:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
						entry.getValue().kick();
					}
					Map<Integer, Shit> shits = GameWorld.get().getCurrentMap().getShit();
					for (Map.Entry<Integer, Shit> entry : shits.entrySet()) {
						entry.getValue().kick();
					}
					Map<Integer, Vomit> vomits = GameWorld.get().getCurrentMap().getVomit();
					for (Map.Entry<Integer, Vomit> entry : vomits.entrySet()) {
						entry.getValue().kick();
					}
				} else {
					targetObject.kick();
				}
				break;
			case VIBRATOR:
				GadgetMenu.executeBodyMethod(ev, targetObject, "forceToExcite");
				break;
			case PENICUT:
				GadgetMenu.executeBodyMethod(ev, targetObject, "cutPenipeni");
				break;
			case JUICE:
				GadgetMenu.executeBodyMethod(ev, targetObject, "giveJuice");
				break;
			case Medical_JUICE:
				GadgetMenu.executeBodyMethod(ev, targetObject, "injectJuice");
				break;
			case LEMON_SPLAY:
				GadgetMenu.executeBodyMethod(ev, targetObject, "forceToSleep");
				break;
			case Pheromone_SPLAY:
				GadgetMenu.executeBodyMethod(ev, targetObject, "invPheromone");
				break;
			case HAMMER:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
						Body body = entry.getValue();
						body.strikeByHammer();
						if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
							int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
							if (body.getDirection() == Direction.LEFT) {
								offsetX = -offsetX;
							}
							if (!body.isPacked() && !body.isShutmouth()) {
								GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body, body.getShitType());
							}
							body.stay();
						}
						body.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					body.strikeByHammer();
					if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
						int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
						if (body.getDirection() == Direction.LEFT) {
							offsetX = -offsetX;
						}
						if (!body.isPacked() && !body.isShutmouth()) {
							GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body, body.getShitType());
						}
						body.stay();
					}
					body.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case GATHERINJECTINTO:
				if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					if (SimYukkuri.sperm == null) {
						SimYukkuri.sperm = body.getDna();
					} else {
						body.injectInto(SimYukkuri.sperm);
						SimYukkuri.sperm = null;
					}
				}
				break;
			case DRIPSPERM:
				if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					if (SimYukkuri.sperm == null) {
						SimYukkuri.sperm = body.getDna();
					} else {
						body.dripSperm(SimYukkuri.sperm);
						SimYukkuri.sperm = null;
					}
				}
				break;
			case PUNCH:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
						Body body = entry.getValue();
						body.strikeByPunch();
						if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
							int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
							if (body.getDirection() == Direction.LEFT) {
								offsetX = -offsetX;
							}
							if (!body.isPacked() && !body.isShutmouth()) {
								GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body, body.getShitType());
							}
							body.stay();
						}
						body.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					body.strikeByPunch();
					if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
						int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
						if (body.getDirection() == Direction.LEFT) {
							offsetX = -offsetX;
						}
						if (!body.isPacked() && !body.isShutmouth()) {
							GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body, body.getShitType());
						}
						body.stay();
					}
					body.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case GODHAND:
				if (ev.isShiftDown()) {
					List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
					int bodyCount = bodyList.size();
					for (int i = bodyCount - 1; -1 < i; i--) {
						Body body = bodyList.get(i);
						if (body != null) {
							GadgetTool.doGodHand(body);
						}
					}
				} else if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					GadgetTool.doGodHand(body);
				}
				break;
			case PEAL:
				List<Body> bodyListP = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown() || ev.isControlDown()) {
					for (Body body : bodyListP) {
						body.peal();
						body.setStubbornlyDirty(false);
					}
				} else if (targetObject instanceof Body) {
					((Body) targetObject).peal();
					((Body) targetObject).setStubbornlyDirty(false);
				}
				break;
			case Blind:
				List<Body> bodyListB = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean shouldBreakEyes = true;
					if (targetObject instanceof Body) {
						shouldBreakEyes = !((Body) targetObject).isBlind();
					}
					for (Body body : bodyListB) {
						if (!shouldBreakEyes && body.isBlind()) {
							body.breakeyes();
						} else if (shouldBreakEyes && !body.isBlind()) {
							body.breakeyes();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body body : bodyListB) {
						body.breakeyes();
					}
				} else if (targetObject instanceof Body) {
					((Body) targetObject).breakeyes();
				}
				break;
			case SHUTMOUTH:
				List<Body> bodyListS = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean shouldShutMouth = true;
					if (targetObject instanceof Body) {
						shouldShutMouth = !((Body) targetObject).isShutmouth();
					}
					for (Body body : bodyListS) {
						if (!shouldShutMouth && body.isShutmouth()) {
							body.ShutMouth();
						} else if (shouldShutMouth && !body.isShutmouth()) {
							body.ShutMouth();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body body : bodyListS) {
						body.ShutMouth();
					}
				} else if (targetObject instanceof Body) {
					((Body) targetObject).ShutMouth();
				}
				break;
			case HAIRCUT:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeBodyMethod(ev, targetObject, "pickHair");
				}
				break;
			case PACK:
				List<Body> bodyListPa = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean shouldPack = true;
					if (targetObject instanceof Body) {
						shouldPack = !((Body) targetObject).isPacked();
					}
					for (Body body : bodyListPa) {
						if (!shouldPack && body.isPacked()) {
							body.pack();
						} else if (shouldPack && !body.isPacked()) {
							body.pack();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body body : bodyListPa) {
						body.pack();
					}
				} else if (targetObject instanceof Body) {
					((Body) targetObject).pack();
				}
				break;
			case HOLD:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeBodyMethod(ev, targetObject, "Hold");
				}
				break;
			case STOMP:
				GadgetMenu.executeBodyMethod(ev, targetObject, "strikeByPress");
				break;
			default:
				break;
		}
	}

	static void evaluateTool2(GadgetList item, MouseEvent ev, Obj targetObject) {
		switch (item) {
			case BRAID_PLUCK:
				GadgetMenu.executeBodyMethod(ev, targetObject, "takeBraid");
				break;
			case ANAL_CLOSE:
				GadgetMenu.executeBodyMethod(ev, targetObject, "isAnalClose", "setAnalClose", "invAnalClose");
				break;
			case STALK_CUT:
				GadgetMenu.executeBodyMethod(ev, targetObject, "getStalkCastration", "castrateStalk", "invStalkCastration");
				break;
			case STALK_UNPLUG:
				if (targetObject instanceof Stalk) {
					Stalk s = ((Stalk) targetObject);
					int id = s.getPlantYukkuri();
					if (GameWorld.get().getCurrentMap().getBody().get(id) == null) {
						int id2 = s.getPlantYukkuri();
						GameWorld.get().getCurrentMap().getBody().get(id2).touchStalk();
					}
				}
				break;
			case CASTRATION:
				GadgetMenu.executeBodyMethod(ev, targetObject, "getBodyCastration", "castrateBody", "invBodyCastration");
				break;
			case LIGHTER:
				GadgetMenu.executeBodyMethod(ev, targetObject, "giveFire");
				break;
			case NEEDLE:
				GadgetMenu.executeBodyMethod(ev, targetObject, "getNeedle", "setNeedle", "invNeedle");
				break;
			case WATER:
				GadgetMenu.executeBodyMethod(ev, targetObject, "giveWater");
				break;
			case BURY:
				GadgetMenu.executeBodyMethod(ev, targetObject, "baryInUnderGround");
				break;
			case SET_SICK:
				GadgetMenu.executeBodyMethod(ev, targetObject, "moldToggle");
				break;
			case SET_RAPER:
				GadgetMenu.executeBodyMethod(ev, targetObject, "raperToggle");
				break;
			default:
				break;
		}
	}
}
