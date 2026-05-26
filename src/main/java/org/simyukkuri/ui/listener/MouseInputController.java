package org.simyukkuri.ui.listener;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.command.GadgetMenu.ActionControl;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.MainCategoryName;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.RenderOrderComparator;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.system.ItemMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.ui.MainCommandUi;

/**
 * マウス入力をまとめる helper.
 */
public class MouseInputController extends MouseAdapter {
	private final SimYukkuri owner;
	private final Cursor cr = new Cursor(Cursor.HAND_CURSOR);
	private final Cursor defCr = new Cursor(Cursor.DEFAULT_CURSOR);
	private Entity grabbedObj = null;
	private int startY = -1;
	private int startZ = -1;
	private int oX = 0;
	private int oY = 0;
	@SuppressWarnings("unused")
	private int altitude = 0;
	private final Point4y translatePos = new Point4y();
	private final Rectangle4y imageRect = new Rectangle4y();
	private final List<Entity> list4sort = new LinkedList<Entity>();

	/** @param owner メインウィンドウ */
	public MouseInputController(SimYukkuri owner) {
		this.owner = owner;
	}

	// マウス位置の最も手前にあるオブジェクトを取得
	private Entity getUpFront(int mx, int my, boolean stalkMode) {
		list4sort.clear();
		list4sort.addAll(SimYukkuri.world.getWorldEntities());
		list4sort.addAll(SimYukkuri.world.getFixedObjects());
		list4sort.addAll(SimYukkuri.world.getObjects());
		list4sort.addAll(SimYukkuri.world.getPlatforms());

		Collections.sort(list4sort, RenderOrderComparator.getInstance());
		Entity found = null;
		Entity parent = null;
		Yukkuri body = null;
		Stalk stalk = null;
		int num = list4sort.size() - 1;
		Entity o = null;
		for (int i = num; i >= 0; i--) {
			o = list4sort.get(i);
			if (!o.isCanGrab()) {
				continue;
			}

			if (stalkMode && o instanceof Yukkuri) {
				body = (Yukkuri) o;
				if (body.getStalks() != null && body.getStalks().size() > 0) {
					parent = body.getStalks().get(0);
				} else {
					parent = body;
				}
			} else if (!stalkMode && o instanceof Stalk) {
				stalk = (Stalk) o;
				Yukkuri b = org.simyukkuri.util.GameWorld.get().getCurrentWorldState()
						.getYukkuriRegistry().get(stalk.getPlantYukkuri());
				if (b != null) {
					parent = b;
				} else {
					parent = o;
				}
			} else {
				parent = o;
			}
			Rectangle4y r = parent.getScreenRect();
			Rectangle screenRect = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
			if (screenRect.contains(mx, my)) {
				if (parent instanceof Yukkuri) {
					((Yukkuri) parent).getExpandShape(imageRect);
				} else {
					parent.getBoundaryShape(imageRect);
				}
				found = parent;
				oX = screenRect.x + Translate.transSize(imageRect.getX()) - mx;
				oY = screenRect.y + Translate.transSize(imageRect.getY()) - my;
				break;
			}
		}

		if (found == null) {
			List<WorldEntity> platformList = SimYukkuri.world.getPlatforms();
			for (Iterator<WorldEntity> i = platformList.iterator(); i.hasNext();) {
				WorldEntity oex = i.next();
				Rectangle4y r = oex.getScreenRect();
				Rectangle screenRect = new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
				oex.getBoundaryShape(imageRect);
				if (oex instanceof BeltconveyorObj) {
					if (((BeltconveyorObj) oex).checkContain(mx, my, true)) {
						found = oex;
						oX = screenRect.x + Translate.transSize(imageRect.getX()) - mx;
						oY = screenRect.y + Translate.transSize(imageRect.getY()) - my;
					}
				} else {
					if (screenRect.contains(mx, my)) {
						found = oex;
						oX = screenRect.x + Translate.transSize(imageRect.getX()) - mx;
						oY = screenRect.y + Translate.transSize(imageRect.getY()) - my;
					}
				}
			}
		}
		return found;
	}

	// マウス位置の最も手前にあるシェイプを取得
	private FieldShape getShapeFront(int mx, int my) {
		Point4y pos = Translate.invert(mx, my);
		if (pos == null) {
			return null;
		}
		WorldState curMap = org.simyukkuri.util.GameWorld.get().getCurrentWorldState();
		int flags = Translate.getCurrentFieldGridValue(pos.getX(), pos.getY());
		if ((flags & FieldShape.FIELD_BELT) != 0) {
			int num = curMap.getBeltconveyors().size();
			for (int i = num - 1; i >= 0; i--) {
				Beltconveyor b = curMap.getBeltconveyors().get(i);
				if (b.mapContains(pos.getX(), pos.getY())) {
					return b;
				}
			}
		}
		if ((flags & FieldShape.FIELD_FARM) != 0) {
			int num = curMap.getFarms().size();
			for (int i = num - 1; i >= 0; i--) {
				Farm b = curMap.getFarms().get(i);
				if (b.mapContains(pos.getX(), pos.getY())) {
					return b;
				}
			}
		}
		if ((flags & FieldShape.FIELD_POOL) != 0) {
			int num = curMap.getPools().size();
			for (int i = num - 1; i >= 0; i--) {
				Pool b = curMap.getPools().get(i);
				if (b.mapContains(pos.getX(), pos.getY())) {
					return b;
				}
			}
		}
		return null;
	}

	/** マウスクリック時にガジェット操作・右クリックメニューを処理する。 */
	@Override
	public void mouseClicked(MouseEvent e) {
		synchronized (SimYukkuri.lock) {
			Translate.transCanvasToField(e.getX(), e.getY(), SimYukkuri.fieldMousePos);

			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) {
				if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
					if (SimYukkuri.fieldSx > -1 || SimYukkuri.fieldSy > -1) {
						SimYukkuri.fieldSx = -1;
						SimYukkuri.fieldSy = -1;
						SimYukkuri.fieldEx = -1;
						SimYukkuri.fieldEy = -1;
						ItemMenu.itemModeCancel(true);
						return;
					}

					Entity found = getUpFront(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1], false);
					if (found == null) {
						FieldShape foundShape = getShapeFront(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1]);
						if (foundShape != null && foundShape.hasShapePopup() != ShapeMenuTarget.NONE) {
							ItemMenu.setShapePopupMenu(foundShape);
							ItemMenu.getShapePopup().show(SimYukkuri.mypane, e.getX() + 10, e.getY());
						} else {
							ItemMenu.itemModeCancel(true);
							if (GadgetMenu.isPopupDisplay()) {
								GadgetMenu.getPopup().setVisible(false);
								GadgetMenu.setPopupDisplay(false);
							} else {
								GadgetMenu.getPopup().show(SimYukkuri.mypane, e.getX(), owner.getY());
								GadgetMenu.setPopupDisplay(true);
							}
						}
					} else {
						if (org.simyukkuri.util.GameWorld.get().getPlayer().getHoldItem() == null) {
							if (found.hasGetPopup() != ItemMenu.GetMenuTarget.NONE) {
								ItemMenu.setGetPopupMenu(found);
								ItemMenu.getGetPopup().show(SimYukkuri.mypane, e.getX() + 10, e.getY());
							} else {
								ItemMenu.itemModeCancel(true);
								if (found instanceof WorldEntity) {
									WorldEntity oex = (WorldEntity) found;
									oex.invertEnabled();
								}
							}
							return;
						} else {
							if (found.hasUsePopup() != ItemMenu.UseMenuTarget.NONE) {
								ItemMenu.getUsePopup().show(SimYukkuri.mypane, e.getX() + 10, e.getY());
							}
						}
					}
				}
				return;
			}
			GadgetMenu.setPopupDisplay(false);
			ItemMenu.itemModeCancel(false);

			if (org.simyukkuri.util.GameWorld.get().getPlayer().getHoldItem() != null) {
				ItemMenu.dropItem(e);
				return;
			}
			GadgetMenuChoice sel = GadgetMenu.getCurrentGadget();
			if (sel == null) {
				return;
			}

			if (sel.getActionTarget() == ActionTarget.IMMEDIATE) {
				GadgetAction.immediateEvaluate(sel);
				return;
			}

			boolean stalkMode = false;
			if (sel == GadgetMenuChoice.STALK_UNPLUG) {
				stalkMode = true;
			}
			Entity found = getUpFront(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1], stalkMode);
			ActionTarget foundType = ActionTarget.TERRAIN;

			if (found instanceof Yukkuri) {
				MainCommandUi.showStatus((Yukkuri) found);
				MyPane.setSelectedYukkuri((Yukkuri) found);
				foundType = ActionTarget.BODY;
			} else if (found != null) {
				foundType = ActionTarget.GADGET;
			}

			if (sel.getActionControl() == ActionControl.LEFT_CLICK) {
				if ((sel.getActionTarget().getMask() & foundType.getMask()) != 0
						|| sel.getActionTarget() == ActionTarget.WALL
						|| sel.getActionTarget() == ActionTarget.FIELD) {
					GadgetAction.leftClickEvaluate(sel, found, e, SimYukkuri.fieldMousePos);
				}
			} else if (sel.getActionControl() == ActionControl.LEFT_MULTI_CLICK) {
				if ((sel.getActionTarget().getMask() & foundType.getMask()) != 0) {
					GadgetAction.leftMultiClickEvaluate(sel, found, e, SimYukkuri.fieldMousePos);
				}
			}
		}
	}

	/** マウスボタン押下時にオブジェクトの掴み操作を開始する。 */
	@Override
	public void mousePressed(MouseEvent e) {
		synchronized (SimYukkuri.lock) {
			ItemMenu.itemModeCancel(false);
			Translate.transCanvasToField(e.getX(), e.getY(), SimYukkuri.fieldMousePos);

			if (e.isShiftDown()) {
				SimYukkuri.scrollOldX = e.getX();
				SimYukkuri.scrollOldY = e.getY();
				return;
			}

			GadgetMenuChoice sel = GadgetMenu.getCurrentGadget();
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if ((sel != GadgetMenuChoice.PICKUP) && (sel != GadgetMenuChoice.SURISURI)) {
					return;
				}
			}

			if (javax.swing.SwingUtilities.isMiddleMouseButton(e)) {
				return;
			}

			if (grabbedObj != null) {
				return;
			}
			boolean stalkMode = false;
			if (sel == GadgetMenuChoice.STALK_UNPLUG) {
				stalkMode = true;
			}

			grabbedObj = getUpFront(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1], stalkMode);
			if (grabbedObj != null) {
				if (((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (sel == GadgetMenuChoice.SURISURI)) {
					if (sel == GadgetMenuChoice.SURISURI) {
						if (grabbedObj instanceof Yukkuri) {
							Yukkuri b = (Yukkuri) grabbedObj;
							b.setSurisuriFromPlayer(true);
						}
					}
				} else {
					if (grabbedObj instanceof Yukkuri) {
						Yukkuri b = (Yukkuri) grabbedObj;

						if (b.getBindStalk() != null) {
							b.detachFromStalk();
							b.setZ(0);
							b.setCalcZ(0);
							b.kick(0, 0, 0);
							b.setZ(b.getZ() - 1);
							b.setCalcZ(b.getZ());
						}
						if (b.getBindStalk() == null && b.getParentLinkId() == -1) {
							b.setZ(0);
							b.setCalcZ(0);
						}
					}
					startY = SimYukkuri.fieldMousePos[1];
					startZ = SimYukkuri.fieldMousePos[1] + Translate.transSize(grabbedObj.getZ() * 58 / 10);
					grabbedObj.grab();
					if (grabbedObj instanceof Yukkuri) {
						MainCommandUi.showStatus((Yukkuri) grabbedObj);
						MyPane.setSelectedYukkuri((Yukkuri) grabbedObj);
					}
				}
			}
		}
	}

	/** マウスボタン解放時に掴んだオブジェクトを投げる。 */
	@Override
	public void mouseReleased(MouseEvent e) {
		synchronized (SimYukkuri.lock) {
			Translate.transCanvasToField(e.getX(), e.getY(), SimYukkuri.fieldMousePos);

			if ((e.getModifiers() & (MouseEvent.BUTTON1_MASK | MouseEvent.BUTTON3_MASK)) == 0) {
				return;
			}

			if (grabbedObj != null) {
				if (grabbedObj instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) grabbedObj;
					if (body.canPullOrPush()) {
						body.releaseLockNobinobi();
					}

					body.setSurisuriFromPlayer(false);

					if (body.canflyCheck()) {
						if (grabbedObj.getZ() > 0) {
							grabbedObj.kick(0, 0, 0);
						}
					} else {
						if (grabbedObj.getZ() > 0) {
							grabbedObj.kick(SimYukkuri.mouseVX / 15, 0, SimYukkuri.mouseVY / 20);
						}
					}
				} else {
					if (grabbedObj.getZ() > 0) {
						grabbedObj.kick(SimYukkuri.mouseVX / 15, 0, SimYukkuri.mouseVY / 20);
					}
				}
				grabbedObj.release();
				grabbedObj = null;
				startY = -1;
				startZ = -1;
				altitude = 0;
			}
		}
	}

	/** マウスドラッグ時にオブジェクト移動・なでなでを処理する。 */
	@Override
	public void mouseDragged(MouseEvent e) {
		synchronized (SimYukkuri.lock) {
			Translate.transCanvasToField(e.getX(), e.getY(), SimYukkuri.fieldMousePos);

			if (e.isShiftDown()) {
				int dx = (int) ((float) (SimYukkuri.scrollOldX - e.getX()) * Translate.getCurrentZoomRate());
				int dy = (int) ((float) (SimYukkuri.scrollOldY - e.getY()) * Translate.getCurrentZoomRate());
				Translate.addBufferPos(dx, dy);
				SimYukkuri.scrollOldX = e.getX();
				SimYukkuri.scrollOldY = e.getY();
				return;
			}

			GadgetMenuChoice sel = GadgetMenu.getCurrentGadget();

			if (grabbedObj != null) {
				int button = 1;
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					button = 1;
				} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
					button = 2;
				} else {
					return;
				}

				if ((button == 1) && (sel == GadgetMenuChoice.PICKUP)) {
					int newX = SimYukkuri.fieldMousePos[0] + oX;
					int newY = startY;
					int newZ = startZ - SimYukkuri.fieldMousePos[1];
					int hitX;
					switch (grabbedObj.getObjType()) {
						case YUKKURI:
							Yukkuri b = (Yukkuri) grabbedObj;
							if (b.canPullOrPush() && !b.isDead()) {
								b.wakeup();
								if (b.getZ() <= 0) {
									b.lockSetZ(newZ * Translate.getWorldDepth() / Translate.getCanvasH());
								}
							} else {
								hitX = 4;
								altitude = startZ - SimYukkuri.fieldMousePos[1];
								Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
								grabbedObj.setCalcX(translatePos.getX());
								if (newZ > 0) {
									grabbedObj.setCalcZ(translatePos.getY());
								}
							}
							break;
						case SHIT:
						case VOMIT:
						case OBJECT:
						case FIX_OBJECT:
							hitX = grabbedObj.getPivotX();
							altitude = startZ - SimYukkuri.fieldMousePos[1];
							Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
							grabbedObj.setCalcX(translatePos.getX());
							grabbedObj.setCalcZ(translatePos.getY());
							break;
						case PLATFORM:
							hitX = grabbedObj.getPivotX();
							altitude = startZ - SimYukkuri.fieldMousePos[1];
							Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
							grabbedObj.setCalcX(translatePos.getX());
							break;
						default:
							hitX = 1;
							break;
					}
				} else if (button == 2) {
					int newX = SimYukkuri.fieldMousePos[0] + oX;
					int newY = SimYukkuri.fieldMousePos[1] + oY;
					int hitX;
					int hitY;
					switch (grabbedObj.getObjType()) {
						case YUKKURI:
							hitX = 4;
							hitY = 4;
							break;
						case SHIT:
						case VOMIT:
						case OBJECT:
						case FIX_OBJECT:
							hitX = grabbedObj.getPivotX();
							hitY = 4;
							break;
						case PLATFORM:
							if (grabbedObj instanceof BeltconveyorObj) {
								hitX = 4;
								hitY = grabbedObj.getPivotY();
							} else {
								hitX = grabbedObj.getPivotX();
								hitY = grabbedObj.getPivotY();
							}
							break;
						default:
							hitX = 1;
							hitY = 1;
							break;

					}
					Translate.invertGround(newX, newY, hitX, hitY, translatePos);
					grabbedObj.setCalcX(translatePos.getX());
					grabbedObj.setCalcY(translatePos.getY());
				}
				if ((button == 1) && (sel == GadgetMenuChoice.SURISURI)) {
					Entity found = getUpFront(SimYukkuri.fieldMousePos[0], SimYukkuri.fieldMousePos[1], false);
					boolean onTarget = true;
					if (found == null) {
						onTarget = false;
					} else {
						if (grabbedObj != found) {
							onTarget = false;
						}
					}

					if (!onTarget) {
						if (grabbedObj instanceof Yukkuri) {
							Yukkuri b = (Yukkuri) grabbedObj;
							b.setSurisuriFromPlayer(false);
							grabbedObj.release();
							grabbedObj = null;
						}
					}
				}
			}
			SimYukkuri.mouseNewX = SimYukkuri.fieldMousePos[0];
			SimYukkuri.mouseNewY = SimYukkuri.fieldMousePos[1];
		}
	}

	/** マウス移動時にフィールド選択矩形の終端座標を更新する。 */
	@Override
	public void mouseMoved(MouseEvent e) {
		synchronized (SimYukkuri.lock) {
			GadgetMenuChoice sel = GadgetMenu.getCurrentGadget();
			if (sel == null) {
				return;
			}

			if ((sel.getGroup() != MainCategoryName.BARRIER && sel.getGroup() != MainCategoryName.CONVEYOR)
					|| sel.getInitOption() == 0) {
				return;
			}
			if (SimYukkuri.fieldSx < 0 || SimYukkuri.fieldSy < 0) {
				return;
			}

			Translate.transCanvasToField(e.getX(), e.getY(), SimYukkuri.fieldMousePos);
			SimYukkuri.fieldEx = SimYukkuri.fieldMousePos[0];
			SimYukkuri.fieldEy = SimYukkuri.fieldMousePos[1];
			SimYukkuri.mouseNewX = SimYukkuri.fieldMousePos[0];
			SimYukkuri.mouseNewY = SimYukkuri.fieldMousePos[1];
		}
	}

	/** カーソルがキャンバスに入ったときにカスタムカーソルを設定する。 */
	@Override
	public void mouseEntered(MouseEvent e) {
		owner.setCursor(cr);
	}

	/** カーソルがキャンバスを出たときにデフォルトカーソルに戻す。 */
	@Override
	public void mouseExited(MouseEvent e) {
		owner.setCursor(defCr);
	}

	/** マウスホイール操作でゲームスピードを変更する。 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int select;
		select = MainCommandUi.getGameSpeedCombo().getSelectedIndex();
		select += e.getWheelRotation();
		if (select < 0) {
			select = 0;
		}
		if (select >= MainCommandUi.getGameSpeedCombo().getItemCount()) {
			select = MainCommandUi.getGameSpeedCombo().getItemCount() - 1;
		}
		MainCommandUi.getGameSpeedCombo().setSelectedIndex(select);
	}
}
