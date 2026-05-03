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
import src.util.YukkuriUtil;

final class GadgetToolAction {

	private GadgetToolAction() {
	}

	static void evaluateTool(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
			case PUNISH:
				GadgetMenu.executeBodyMethod(ev, found, "strikeByPunish");
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
					found.kick();
				}
				break;
			case VIBRATOR:
				GadgetMenu.executeBodyMethod(ev, found, "forceToExcite");
				break;
			case PENICUT:
				GadgetMenu.executeBodyMethod(ev, found, "cutPenipeni");
				break;
			case JUICE:
				GadgetMenu.executeBodyMethod(ev, found, "giveJuice");
				break;
			case Medical_JUICE:
				GadgetMenu.executeBodyMethod(ev, found, "injectJuice");
				break;
			case LEMON_SPLAY:
				GadgetMenu.executeBodyMethod(ev, found, "forceToSleep");
				break;
			case Pheromone_SPLAY:
				GadgetMenu.executeBodyMethod(ev, found, "invPheromone");
				break;
			case HAMMER:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
						Body b = entry.getValue();
						b.strikeByHammer();
						if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
							int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
							if (b.getDirection() == Direction.LEFT) {
								ofsX = -ofsX;
							}
							if (!b.isPacked() && !b.isShutmouth()) {
								GameView.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b, b.getShitType());
							}
							b.stay();
						}
						b.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (found instanceof Body) {
					Body b = (Body) found;
					b.strikeByHammer();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT) {
							ofsX = -ofsX;
						}
						if (!b.isPacked() && !b.isShutmouth()) {
							GameView.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b, b.getShitType());
						}
						b.stay();
					}
					b.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case GATHERINJECTINTO:
			if (found instanceof Body) {
				Body b = (Body) found;
				if (SimYukkuri.sperm == null) {
					SimYukkuri.sperm = b.getDna();
				} else {
					b.injectInto(SimYukkuri.sperm);
					SimYukkuri.sperm = null;
				}
			}
			break;
		case DRIPSPERM:
			if (found instanceof Body) {
				Body b = (Body) found;
				if (SimYukkuri.sperm == null) {
					SimYukkuri.sperm = b.getDna();
				} else {
					b.dripSperm(SimYukkuri.sperm);
					SimYukkuri.sperm = null;
				}
			}
			break;
		case PUNCH:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
						Body b = entry.getValue();
						b.strikeByPunch();
						if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
							int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
							if (b.getDirection() == Direction.LEFT) {
								ofsX = -ofsX;
							}
							if (!b.isPacked() && !b.isShutmouth()) {
								GameView.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b, b.getShitType());
							}
							b.stay();
						}
						b.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (found instanceof Body) {
					Body b = (Body) found;
					b.strikeByPunch();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT) {
							ofsX = -ofsX;
						}
						if (!b.isPacked() && !b.isShutmouth()) {
							GameView.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b, b.getShitType());
						}
						b.stay();
					}
					b.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case GODHAND:
				if (ev.isShiftDown()) {
					List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
					int nSize = bodyList.size();
					for (int i = nSize - 1; -1 < i; i--) {
						Body b = bodyList.get(i);
						if (b != null) {
							GadgetTool.doGodHand(b);
						}
					}
				} else if (found instanceof Body) {
					Body b = (Body) found;
					GadgetTool.doGodHand(b);
				}
				break;
			case PEAL:
				List<Body> bodyListP = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown() || ev.isControlDown()) {
					for (Body b : bodyListP) {
						b.peal();
						b.setStubbornlyDirty(false);
					}
				} else if (found instanceof Body) {
					((Body) found).peal();
					((Body) found).setStubbornlyDirty(false);
				}
				break;
			case Blind:
				List<Body> bodyListB = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean flag = true;
					if (found instanceof Body) {
						flag = !((Body) found).isBlind();
					}
					for (Body b : bodyListB) {
						if (!flag && b.isBlind()) {
							b.breakeyes();
						} else if (flag && !b.isBlind()) {
							b.breakeyes();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyListB) {
						b.breakeyes();
					}
				} else if (found instanceof Body) {
					((Body) found).breakeyes();
				}
				break;
			case SHUTMOUTH:
				List<Body> bodyListS = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean flag = true;
					if (found instanceof Body) {
						flag = !((Body) found).isShutmouth();
					}
					for (Body b : bodyListS) {
						if (!flag && b.isShutmouth()) {
							b.ShutMouth();
						} else if (flag && !b.isShutmouth()) {
							b.ShutMouth();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyListS) {
						b.ShutMouth();
					}
				} else if (found instanceof Body) {
					((Body) found).ShutMouth();
				}
				break;
			case HAIRCUT:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeBodyMethod(ev, found, "pickHair");
				}
				break;
			case PACK:
				List<Body> bodyListPa = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
				if (ev.isShiftDown()) {
					boolean flag = true;
					if (found instanceof Body) {
						flag = !((Body) found).isPacked();
					}
					for (Body b : bodyListPa) {
						if (!flag && b.isPacked()) {
							b.pack();
						} else if (flag && !b.isPacked()) {
							b.pack();
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyListPa) {
						b.pack();
					}
				} else if (found instanceof Body) {
					((Body) found).pack();
				}
				break;
			case HOLD:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeBodyMethod(ev, found, "Hold");
				}
				break;
			case STOMP:
				GadgetMenu.executeBodyMethod(ev, found, "strikeByPress");
				break;
			default:
				break;
		}
	}

	static void evaluateTool2(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
			case BRAID_PLUCK:
				GadgetMenu.executeBodyMethod(ev, found, "takeBraid");
				break;
			case ANAL_CLOSE:
				GadgetMenu.executeBodyMethod(ev, found, "isAnalClose", "setAnalClose", "invAnalClose");
				break;
			case STALK_CUT:
				GadgetMenu.executeBodyMethod(ev, found, "getStalkCastration", "castrateStalk", "invStalkCastration");
				break;
			case STALK_UNPLUG:
				if (found instanceof Stalk) {
					Stalk s = ((Stalk) found);
					int id = s.getPlantYukkuri();
					if (GameWorld.get().getCurrentMap().getBody().get(id) == null) {
						int id2 = s.getPlantYukkuri();
						GameWorld.get().getCurrentMap().getBody().get(id2).touchStalk();
					}
				}
				break;
			case CASTRATION:
				GadgetMenu.executeBodyMethod(ev, found, "getBodyCastration", "castrateBody", "invBodyCastration");
				break;
			case LIGHTER:
				GadgetMenu.executeBodyMethod(ev, found, "giveFire");
				break;
			case NEEDLE:
				GadgetMenu.executeBodyMethod(ev, found, "getNeedle", "setNeedle", "invNeedle");
				break;
			case WATER:
				GadgetMenu.executeBodyMethod(ev, found, "giveWater");
				break;
			case BURY:
				GadgetMenu.executeBodyMethod(ev, found, "baryInUnderGround");
				break;
			case SET_SICK:
				GadgetMenu.executeBodyMethod(ev, found, "moldToggle");
				break;
			case SET_RAPER:
				GadgetMenu.executeBodyMethod(ev, found, "raperToggle");
				break;
			default:
				break;
		}
	}
}
