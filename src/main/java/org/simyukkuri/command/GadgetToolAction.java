package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

final class GadgetToolAction {

	private GadgetToolAction() {
	}

	static void evaluateTool(GadgetMenuChoice item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case PUNISH:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "strikeByPunish");
				break;
			case SNAPPING:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
						entry.getValue().kick();
					}
					Map<Integer, Shit> shits = GameWorld.get().getCurrentWorldState().getShit();
					for (Map.Entry<Integer, Shit> entry : shits.entrySet()) {
						entry.getValue().kick();
					}
					Map<Integer, Vomit> vomits = GameWorld.get().getCurrentWorldState().getVomit();
					for (Map.Entry<Integer, Vomit> entry : vomits.entrySet()) {
						entry.getValue().kick();
					}
				} else {
					targetObject.kick();
				}
				break;
			case VIBRATOR:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "forceToExcite");
				break;
			case PENICUT:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "cutPenipeni");
				break;
			case JUICE:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "giveJuice");
				break;
			case ORANGE_JUICE:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "injectJuice");
				break;
			case LEMON_SPLAY:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "forceToSleep");
				break;
			case PHEROMONE_SPRAY:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "invPheromone");
				break;
			case HAMMER:
				if (ev.isShiftDown()) {
					for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
						Yukkuri body = entry.getValue();
						body.strikeByHammer();
						if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
							int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
							if (body.getDirection() == Direction.LEFT) {
								offsetX = -offsetX;
							}
							if (!body.isPacked() && !body.isShutmouth()) {
								GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body,
										body.getShitType());
							}
							body.stay();
						}
						body.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.strikeByHammer();
					if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
						int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
						if (body.getDirection() == Direction.LEFT) {
							offsetX = -offsetX;
						}
						if (!body.isPacked() && !body.isShutmouth()) {
							GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body,
									body.getShitType());
						}
						body.stay();
					}
					body.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case INJECT_SPERM:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					if (SimYukkuri.sperm == null) {
						SimYukkuri.sperm = body.getDna();
					} else {
						body.injectInto(SimYukkuri.sperm);
						SimYukkuri.sperm = null;
					}
				}
				break;
			case DRIP_SPERM:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
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
					for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
						Yukkuri body = entry.getValue();
						body.strikeByPunch();
						if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
							int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
							if (body.getDirection() == Direction.LEFT) {
								offsetX = -offsetX;
							}
							if (!body.isPacked() && !body.isShutmouth()) {
								GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body,
										body.getShitType());
							}
							body.stay();
						}
						body.makeDirty(true);
					}
					GameEnvironment.setAlarm();
				} else if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.strikeByPunch();
					if (!body.isHasPants() && !body.isDead() && !body.isShutmouth()) {
						int offsetX = Translate.invertX(body.getCollisionX() >> 1, body.getY());
						if (body.getDirection() == Direction.LEFT) {
							offsetX = -offsetX;
						}
						if (!body.isPacked() && !body.isShutmouth()) {
							GameView.addVomit(body.getX() + offsetX, body.getY(), body.getZ(), body,
									body.getShitType());
						}
						body.stay();
					}
					body.makeDirty(true);
					GameEnvironment.setAlarm();
				}
				break;
			case GODHAND:
				if (ev.isShiftDown()) {
					List<Yukkuri> bodyList = new LinkedList<Yukkuri>(
							GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
					int bodyCount = bodyList.size();
					for (int i = bodyCount - 1; -1 < i; i--) {
						Yukkuri body = bodyList.get(i);
						if (body != null) {
							GadgetTool.doGodHand(body);
						}
					}
				} else if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					GadgetTool.doGodHand(body);
				}
				break;
			case PEAL:
				List<Yukkuri> bodyListP = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
				if (ev.isShiftDown() || ev.isControlDown()) {
					for (Yukkuri body : bodyListP) {
						body.peal();
						body.setStubbornlyDirty(false);
					}
				} else if (targetObject instanceof Yukkuri) {
					((Yukkuri) targetObject).peal();
					((Yukkuri) targetObject).setStubbornlyDirty(false);
				}
				break;
			case BLIND:
				List<Yukkuri> bodyListB = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
				if (ev.isShiftDown()) {
					boolean shouldBreakEyes = true;
					if (targetObject instanceof Yukkuri) {
						shouldBreakEyes = !((Yukkuri) targetObject).isBlind();
					}
					for (Yukkuri body : bodyListB) {
						if (!shouldBreakEyes && body.isBlind()) {
							body.breakeyes();
						} else if (shouldBreakEyes && !body.isBlind()) {
							body.breakeyes();
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyListB) {
						body.breakeyes();
					}
				} else if (targetObject instanceof Yukkuri) {
					((Yukkuri) targetObject).breakeyes();
				}
				break;
			case SHUTMOUTH:
				List<Yukkuri> bodyListS = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
				if (ev.isShiftDown()) {
					boolean shouldshutMouth = true;
					if (targetObject instanceof Yukkuri) {
						shouldshutMouth = !((Yukkuri) targetObject).isShutmouth();
					}
					for (Yukkuri body : bodyListS) {
						if (!shouldshutMouth && body.isShutmouth()) {
							body.shutMouth();
						} else if (shouldshutMouth && !body.isShutmouth()) {
							body.shutMouth();
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyListS) {
						body.shutMouth();
					}
				} else if (targetObject instanceof Yukkuri) {
					((Yukkuri) targetObject).shutMouth();
				}
				break;
			case HAIRCUT:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeYukkuriMethod(ev, targetObject, "pickHair");
				}
				break;
			case PACK:
				List<Yukkuri> bodyListPa = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values());
				if (ev.isShiftDown()) {
					boolean shouldPack = true;
					if (targetObject instanceof Yukkuri) {
						shouldPack = !((Yukkuri) targetObject).isPacked();
					}
					for (Yukkuri body : bodyListPa) {
						if (!shouldPack && body.isPacked()) {
							body.pack();
						} else if (shouldPack && !body.isPacked()) {
							body.pack();
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyListPa) {
						body.pack();
					}
				} else if (targetObject instanceof Yukkuri) {
					((Yukkuri) targetObject).pack();
				}
				break;
			case HOLD:
				if (!ev.isShiftDown()) {
					GadgetMenu.executeYukkuriMethod(ev, targetObject, "hold");
				}
				break;
			case STOMP:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "strikeByPress");
				break;
			default:
				break;
		}
	}

	static void evaluateTool2(GadgetMenuChoice item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case BRAID_PLUCK:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "takeBraid");
				break;
			case ANAL_CLOSE:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "isAnalClose", "setAnalClose", "invAnalClose");
				break;
			case STALK_CUT:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "getStalkCastration", "castrateStalk",
						"invStalkCastration");
				break;
			case STALK_UNPLUG:
				if (targetObject instanceof Stalk) {
					Stalk s = ((Stalk) targetObject);
					int id = s.getPlantYukkuri();
					if (GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id) == null) {
						int id2 = s.getPlantYukkuri();
						GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id2).touchStalk();
					}
				}
				break;
			case CASTRATION:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "isCastrated", "castrateYukkuri",
						"toggleCastration");
				break;
			case LIGHTER:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "giveFire");
				break;
			case NEEDLE:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "getNeedle", "setNeedle", "invNeedle");
				break;
			case WATER:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "giveWater");
				break;
			case BURY:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "baryInUnderGround");
				break;
			case SET_SICK:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "moldToggle");
				break;
			case SET_RAPER:
				GadgetMenu.executeYukkuriMethod(ev, targetObject, "raperToggle");
				break;
			default:
				break;
		}
	}
}
