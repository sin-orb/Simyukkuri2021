package src.draw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetMenu;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.enums.AgeState;
import src.game.Shit;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.Farm;
import src.item.Pool;
import src.system.FieldShapeBase;
import src.system.IconPool;
import src.system.LoggerYukkuri;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.Sprite;
import src.util.GameWorld;
import src.util.GameEnvironment;
import src.util.BodyUtil;

final class Renderer {

	void render(MyPane pane, Graphics g) {
		synchronized (SimYukkuri.lock) {
			MapPlaceData curMap = GameWorld.get().getCurrentMap();

			pane.getList4sort().clear();
			pane.getList4sort().addAll(GameWorld.get().getYukkuriList());
			pane.getList4sort().addAll(GameWorld.get().getFixObjList());
			pane.getList4sort().addAll(GameWorld.get().getObjectList());
			pane.getList4sort().addAll(GameWorld.get().getSortEffectList());
			pane.getList4sort().addAll(TerrainField.getStructList());
			Collections.sort(pane.getList4sort(), ObjDrawComp.getInstance());

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, pane.getRenderScale());

			Rectangle4y dispArea = Translate.getDisplayArea();
			pane.getBackBufferG2().setClip(dispArea.getX(), dispArea.getY(), dispArea.getWidth(), dispArea.getHeight());

			pane.getMsgList().clear();
			MyPane.markList.clear();
			Body selectBody = MyPane.getSelectBody();
			if (selectBody != null) {
				if (selectBody.isRemoved()) {
					MyPane.setSelectBody(null);
					selectBody = null;
				} else {
					MainCommandUI.showStatus(selectBody);
				}
			}

			TerrainField.drawBackGroundImage(pane.getBackBufferG2(), pane);
			TerrainField.drawFloor(pane.getBackBufferG2(), pane);

			for (int i = curMap.getFarm().size() - 1; i >= 0; i--) {
				curMap.getFarm().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = curMap.getPool().size() - 1; i >= 0; i--) {
				curMap.getPool().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = curMap.getBeltconveyor().size() - 1; i >= 0; i--) {
				curMap.getBeltconveyor().get(i).drawShape(pane.getBackBufferG2());
			}

			List<ObjEX> platformList = GameWorld.get().getPlatformList();
			for (ObjEX oex : platformList) {
				pane.calcDrawPosition(oex, pane.getTmpRect());
				int layerNum = oex.getImageLayer(pane.getLayerTmp());
				if (oex instanceof BeltconveyorObj) {
					((BeltconveyorObj) oex).getImageLayer(pane.getBackBufferG2(), pane.getLayerTmp());
				} else {
					for (int j = 0; j < layerNum; j++) {
						pane.getBackBufferG2().drawImage(pane.getLayerTmp()[j], pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(), pane.getTmpRect().getHeight(), pane);
					}
				}
			}

			pane.getBackBufferG2().setStroke(Barrier.WALL_STROKE);
			for (Barrier b : curMap.getBarrier()) {
				b.drawShape(pane.getBackBufferG2());
			}
			pane.getBackBufferG2().setStroke(MyPane.getDefaultStroke());

			Sprite base;
			Sprite expand;
			Sprite braid;
			Body selectBodyCheck = null;

			for (Obj o : pane.getList4sort()) {
				switch (o.getObjType()) {
					case YUKKURI: {
						Body b = (Body) o;
						if (b == MyPane.getSelectBody()) {
							selectBodyCheck = b;
						}
						int direction = b.getDirection().ordinal();
						b.updateSpriteSize();
						base = b.getBodyBaseSpr();
						expand = b.getBodyExpandSpr();
						braid = b.getBraidSprite();
						int shadowH = b.getShadowH();
						Translate.translate(b.getDrawOfsX(), b.getDrawOfsY(), pane.getTmpPoint());
						pane.calcDrawBodyPosition(pane.getTmpPoint(), base);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), expand);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), braid);
						boolean bDrawShadow = true;
						Obj obj = b.takeMappedObj(b.getLinkParent());
						if (obj != null && obj.getZ() < b.getZ()) {
							bDrawShadow = false;
						}
						if (bDrawShadow && b.isDropShadow() && !b.isUnBirth() && 0 <= b.getZ()) {
							if (b.getType() == src.yukkuri.Remirya.type && b.isbImageNagasiMode()) {
								pane.getBackBufferG2().drawImage(b.getShadowImage(),
										expand.getScreenRect()[direction].getX(),
										expand.getScreenRect()[direction].getY()
												+ expand.getScreenRect()[direction].getHeight() * 11 / 12 - shadowH,
										expand.getScreenRect()[direction].getWidth(), shadowH, pane);
							} else {
								pane.getBackBufferG2().drawImage(b.getShadowImage(),
										expand.getScreenRect()[direction].getX(),
										expand.getScreenRect()[direction].getY()
												+ expand.getScreenRect()[direction].getHeight() - shadowH,
										expand.getScreenRect()[direction].getWidth(), shadowH, pane);
							}
						}
						int tz = Translate.translateZ(b.getZ());
						base.getScreenRect()[0].setY(base.getScreenRect()[0].getY() - tz);
						expand.getScreenRect()[0].setY(expand.getScreenRect()[0].getY() - tz);
						braid.getScreenRect()[0].setY(braid.getScreenRect()[0].getY() - tz);
						base.getScreenRect()[1].setY(base.getScreenRect()[1].getY() - tz);
						expand.getScreenRect()[1].setY(expand.getScreenRect()[1].getY() - tz);
						braid.getScreenRect()[1].setY(braid.getScreenRect()[1].getY() - tz);
						b.setScreenPivot(pane.getTmpPoint());
						b.setScreenRect(expand.getScreenRect()[0]);
						if (b.isPin()) {
							MyPane.markList.add(expand.getScreenRect()[0]);
						}
						if (b.getBaryState() != src.enums.BaryInUGState.ALL) {
							BodyUtil.drawBody(pane.getBackBufferG2(), pane, b);
						}
						if (b.getMessageBuf() != null && !MyPane.isDisableScript()) {
							pane.getMsgList().add(b);
						}
						break;
					}
					case SHIT: {
						Shit s = (Shit) o;
						pane.calcDrawPosition(s, pane.getTmpRect());
						if (MyPane.getDrawShadowShitBaby() == 1 || s.getAgeState() != AgeState.BABY || 0 < s.getZ()) {
							pane.getBackBufferG2().drawImage(s.getShadowImage(), pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(s.getZ()));
						pane.getBackBufferG2().drawImage(s.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case VOMIT: {
						Vomit v = (Vomit) o;
						pane.calcDrawPosition(v, pane.getTmpRect());
						if (MyPane.getDrawShadowVomitBaby() == 1 || v.getAgeState() != AgeState.BABY || 0 < v.getZ()) {
							pane.getBackBufferG2().drawImage(v.getShadowImage(), pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(v.getZ()));
						pane.getBackBufferG2().drawImage(v.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case FIX_OBJECT: {
						ObjEX oex = (ObjEX) o;
						pane.calcDrawPosition(oex, pane.getTmpRect());
						int layerNum = oex.getImageLayer(pane.getLayerTmp());
						for (int i = 0; i < layerNum; i++) {
							pane.getBackBufferG2().drawImage(pane.getLayerTmp()[i], pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						break;
					}
					case OBJECT: {
						ObjEX oex = (ObjEX) o;
						pane.calcDrawPosition(oex, pane.getTmpRect());
						pane.getBackBufferG2().drawImage(oex.getShadowImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(oex.getZ()));
						int layerNum = oex.getImageLayer(pane.getLayerTmp());
						for (int i = 0; i < layerNum; i++) {
							pane.getBackBufferG2().drawImage(pane.getLayerTmp()[i], pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						break;
					}
					case LIGHT_EFFECT: {
						Effect ef = (Effect) o;
						pane.calcDrawPosition(ef, pane.getTmpRect());
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(ef.getZ()));
						pane.getBackBufferG2().drawImage(ef.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case BG_OBJECT: {
						TerrainBillboard tb = (TerrainBillboard) o;
						tb.draw(pane.getBackBufferG2(), pane);
						break;
					}
					default:
						break;
				}
			}

			for (Obj o : GameWorld.get().getFrontEffectList()) {
				Effect ef = (Effect) o;
				pane.calcDrawPosition(ef, pane.getTmpRect());
				pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(ef.getZ()));
				pane.getBackBufferG2().drawImage(ef.getImage(), pane.getTmpRect().getX(), pane.getTmpRect().getY(),
						pane.getTmpRect().getWidth(), pane.getTmpRect().getHeight(), pane);
			}

			TerrainField.drawCeiling(pane.getBackBufferG2(), pane);
			java.awt.Point mousePos = pane.getMousePosition();

			if (MyPane.isEnableTarget()) {
				Image[] cursor = IconPool.getCursorIconImageArray();
				int st = IconPool.CursorIcon.CUR_LB.ordinal();
				for (Rectangle4y rect : MyPane.markList) {
					pane.getBackBufferG2().drawImage(cursor[st + 1], rect.getX(), rect.getY(), pane);
					pane.getBackBufferG2().drawImage(cursor[st + 0], rect.getX(), rect.getY() + rect.getWidth() - 20, pane);
					pane.getBackBufferG2().drawImage(cursor[st + 2], rect.getX() + rect.getWidth() - 20,
							rect.getY() + rect.getWidth() - 20, pane);
					pane.getBackBufferG2().drawImage(cursor[st + 3], rect.getX() + rect.getWidth() - 20, rect.getY(),
							pane);
				}
			}

			if (selectBodyCheck == null) {
				MyPane.setSelectBody(null);
			}
			selectBody = MyPane.getSelectBody();
			if (selectBody != null) {
				Image[] select = IconPool.getCursorIconImageArray();
				int st = IconPool.CursorIcon.SEL_0.ordinal();
				Rectangle4y r = selectBody.getScreenRect();
				int x = r.getX() + (r.getWidth() >> 1) - 12;
				int y = r.getY() + r.getHeight() + 2;
				pane.getBackBufferG2().drawImage(select[st + (int) (selectBody.getAge() % 4)], x, y, pane);
			}

			if (GameWorld.get().getPlayer().getHoldItem() != null && mousePos != null) {
				pane.getBackBufferG2().translate(mousePos.x, mousePos.y);
				pane.getBackBufferG2().setStroke(MyPane.getItemCurStroke());
				pane.getBackBufferG2().setColor(MyPane.getItemCurColor());
				pane.getBackBufferG2().draw(MyPane.getItemCurShape());
				pane.getBackBufferG2().translate(-mousePos.x, -mousePos.y);
			}

			GadgetList curGadget = GadgetMenu.getCurrentGadget();
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.BARRIER) {
				if ((SimYukkuri.fieldSX >= 0) && (SimYukkuri.fieldSY >= 0)
						&& (SimYukkuri.fieldEX >= 0) && (SimYukkuri.fieldEY >= 0)) {
					pane.getBackBufferG2().setStroke(FieldShapeBase.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShapeBase.PREVIEW_COLOR);
					switch (curGadget) {
						case GAP_MINI:
						case GAP_BIG:
						case NET_MINI:
						case NET_BIG:
						case WALL:
						case ITEM:
						case NoUNUN:
						case KEKKAI:
							Barrier.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSX, SimYukkuri.fieldSY,
									SimYukkuri.fieldEX, SimYukkuri.fieldEY);
							break;
						case POOL:
							Pool.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSX, SimYukkuri.fieldSY,
									SimYukkuri.fieldEX, SimYukkuri.fieldEY);
							break;
						case FARM:
							Farm.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSX, SimYukkuri.fieldSY,
									SimYukkuri.fieldEX, SimYukkuri.fieldEY);
							break;
						case BELTCONVEYOR:
							Beltconveyor.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSX, SimYukkuri.fieldSY,
									SimYukkuri.fieldEX, SimYukkuri.fieldEY);
							break;
					}
				}
			}
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.CONVEYOR) {
				if ((SimYukkuri.fieldSX >= 0) && (SimYukkuri.fieldSY >= 0)
						&& (SimYukkuri.fieldEX >= 0) && (SimYukkuri.fieldEY >= 0)) {
					pane.getBackBufferG2().setStroke(FieldShapeBase.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShapeBase.PREVIEW_COLOR);
					switch (curGadget) {
						case BELTCONVEYOR_CUSTOM:
							BeltconveyorObj.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSX, SimYukkuri.fieldSY,
									SimYukkuri.fieldEX, SimYukkuri.fieldEY);
							break;
					}
				}
			}

			g2.drawImage(pane.getBackBuffer(), 0, 0, Translate.getCanvasW(), Translate.getCanvasH(),
					dispArea.getX(), dispArea.getY(), dispArea.getX() + dispArea.getWidth(),
					dispArea.getY() + dispArea.getHeight(), pane);

			LinearGradientPaint sky = TerrainField.getSkyGrad(GameEnvironment.getDayState().ordinal());
			if (sky != null) {
				g2.setPaint(sky);
				g2.fillRect(0, 0, Translate.getFieldW() * 100 / Translate.getMapScale(),
						Translate.getFieldH() * 100 / Translate.getMapScale());
			}

			for (Body b : pane.getMsgList()) {
				String message = b.getMessageBuf();
				int fontSize = b.getMessageTextSize();
				if (fontSize == 120) {
					g2.setFont(MyPane.getNegiFont());
				} else {
					g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
				}
				Rectangle4y bodyRect = b.getScreenRect();
				int width = Math.min(message.length(), MyPane.MSG_BOX_CHAR_NUM) * fontSize;
				int height = pane.drawStringMultiLine(g2, message, 0, 0, width, false);
				Translate.transFieldToCanvas(bodyRect.getX(), bodyRect.getY(), pane.getPosTmp());
				int wx = pane.getPosTmp()[0] + 14;
				int wy = pane.getPosTmp()[1] - height - 4;
				Color4y c = b.getMessageBoxColor();
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()));
				g2.fillRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				c = b.getMessageLineColor();
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()));
				g2.setStroke(b.getMessageWindowStroke());
				g2.drawRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				g2.setStroke(MyPane.getDefaultStroke());
				c = b.getMessageLineColor();
				g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()));
				pane.drawStringMultiLine(g2, message, wx + 4, wy + 4, width, true);
				g2.setFont(MyPane.getDefaultFont());
			}

			if (!MyPane.isDisableHelp() && GadgetMenu.getCurrentHelpNum() > 0) {
				if (mousePos != null) {
					g2.setFont(MyPane.getDefaultFont());
					g2.setColor(Color.WHITE);
					g2.fillRoundRect(mousePos.x, mousePos.y + 20, GadgetMenu.getHelpW(), GadgetMenu.getHelpH(), 8, 8);
					g2.setColor(Color.BLACK);
					g2.setStroke(MyPane.getDefaultStroke());
					g2.drawRoundRect(mousePos.x, mousePos.y + 20, GadgetMenu.getHelpW(), GadgetMenu.getHelpH(), 8, 8);
					for (int i = 0; i < GadgetMenu.getCurrentHelpNum(); i++) {
						int px = mousePos.x + 2;
						int py = mousePos.y + 2 + 20 + (16 * i);
						for (int j = 0; j < GadgetMenu.getCurrentHelpIcon()[i].length; j++) {
							if (GadgetMenu.getCurrentHelpIcon()[i][j] != null) {
								g2.drawImage(GadgetMenu.getHelpIconImage(GadgetMenu.getCurrentHelpIcon()[i][j]), px, py,
										pane);
								px += GadgetMenu.getCurrentHelpIcon()[i][j].getW();
							} else {
								pane.drawStringMultiLine(g2, GadgetMenu.getCurrentHelpBuf()[i][j], px, py,
										GadgetMenu.getCurrentHelpBuf()[i][j].length() * 12, true);
								px += GadgetMenu.getCurrentHelpBuf()[i][j].length() * 12;
							}
						}
					}
				}
			}

			if (LoggerYukkuri.isShow()) {
				LoggerYukkuri.displayLog(g2);
			}
		}
	}
}
