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
import src.command.GadgetMenu;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.entity.core.Entity;
import src.entity.core.effect.Effect;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.WorldEntity;
import src.entity.core.world.item.BeltconveyorObj;
import src.entity.core.world.mobile.Shit;
import src.entity.core.world.mobile.Vomit;
import src.enums.AgeState;
import src.enums.YukkuriType;
import src.field.FieldShape;
import src.field.impl.Barrier;
import src.field.impl.Beltconveyor;
import src.field.impl.Farm;
import src.field.impl.Pool;
import src.system.IconPool;
import src.system.LoggerYukkuri;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.Sprite;
import src.util.BodyUtil;
import src.util.GameEnvironment;
import src.util.GameWorld;
import src.visual.TerrainBillboard;

final class Renderer {

	void render(MyPane pane, Graphics g) {
		synchronized (SimYukkuri.lock) {
			MapPlaceData map = GameWorld.get().getCurrentMap();

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
			Yukkuri selectedBody = MyPane.getSelectBody();
			if (selectedBody != null) {
				if (selectedBody.isRemoved()) {
					MyPane.setSelectBody(null);
					selectedBody = null;
				} else {
					MainCommandUI.showStatus(selectedBody);
				}
			}

			TerrainField.drawBackGroundImage(pane.getBackBufferG2(), pane);
			TerrainField.drawFloor(pane.getBackBufferG2(), pane);

			for (int i = map.getFarm().size() - 1; i >= 0; i--) {
				map.getFarm().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = map.getPool().size() - 1; i >= 0; i--) {
				map.getPool().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = map.getBeltconveyor().size() - 1; i >= 0; i--) {
				map.getBeltconveyor().get(i).drawShape(pane.getBackBufferG2());
			}

			List<WorldEntity> platformList = GameWorld.get().getPlatformList();
			for (WorldEntity platform : platformList) {
				pane.calcDrawPosition(platform, pane.getTmpRect());
				int layerCount = platform.getImageLayer(pane.getLayerTmp());
				if (platform instanceof BeltconveyorObj) {
					((BeltconveyorObj) platform).getImageLayer(pane.getBackBufferG2(), pane.getLayerTmp());
				} else {
					for (int j = 0; j < layerCount; j++) {
						pane.getBackBufferG2().drawImage(pane.getLayerTmp()[j], pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(), pane.getTmpRect().getHeight(),
								pane);
					}
				}
			}

			pane.getBackBufferG2().setStroke(Barrier.WALL_STROKE);
			for (Barrier barrier : map.getBarrier()) {
				barrier.drawShape(pane.getBackBufferG2());
			}
			pane.getBackBufferG2().setStroke(MyPane.getDefaultStroke());

			Sprite bodyBaseSprite;
			Sprite bodyExpandedSprite;
			Sprite braidSprite;
			Yukkuri selectedBodyCheck = null;

			for (Entity o : pane.getList4sort()) {
				switch (o.getObjType()) {
					case YUKKURI: {
						Yukkuri body = (Yukkuri) o;
						if (body == MyPane.getSelectBody()) {
							selectedBodyCheck = body;
						}
						int direction = body.getDirection().ordinal();
						body.updateSpriteSize();
						bodyBaseSprite = body.getBodyBaseSpr();
						bodyExpandedSprite = body.getBodyExpandSpr();
						braidSprite = body.getBraidSprite();
						int shadowHeight = body.getShadowH();
						Translate.translate(body.getDrawOfsX(), body.getDrawOfsY(), pane.getTmpPoint());
						pane.calcDrawBodyPosition(pane.getTmpPoint(), bodyBaseSprite);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), bodyExpandedSprite);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), braidSprite);
						boolean drawShadow = true;
						Entity parentObject = body.takeMappedObj(body.getParentLinkId());
						if (parentObject != null && parentObject.getZ() < body.getZ()) {
							drawShadow = false;
						}
						if (drawShadow && body.isShadowVisible() && !body.isUnBirth() && 0 <= body.getZ()) {
							if (body.getType() == YukkuriType.REMIRYA && body.isImageNagasiMode()) {
								pane.getBackBufferG2().drawImage(body.getShadowImage(),
										bodyExpandedSprite.getScreenRect()[direction].getX(),
										bodyExpandedSprite.getScreenRect()[direction].getY()
												+ bodyExpandedSprite.getScreenRect()[direction].getHeight() * 11 / 12
												- shadowHeight,
										bodyExpandedSprite.getScreenRect()[direction].getWidth(), shadowHeight, pane);
							} else {
								pane.getBackBufferG2().drawImage(body.getShadowImage(),
										bodyExpandedSprite.getScreenRect()[direction].getX(),
										bodyExpandedSprite.getScreenRect()[direction].getY()
												+ bodyExpandedSprite.getScreenRect()[direction].getHeight()
												- shadowHeight,
										bodyExpandedSprite.getScreenRect()[direction].getWidth(), shadowHeight, pane);
							}
						}
						int zOffset = Translate.translateZ(body.getZ());
						bodyBaseSprite.getScreenRect()[0].setY(bodyBaseSprite.getScreenRect()[0].getY() - zOffset);
						bodyExpandedSprite.getScreenRect()[0]
								.setY(bodyExpandedSprite.getScreenRect()[0].getY() - zOffset);
						braidSprite.getScreenRect()[0].setY(braidSprite.getScreenRect()[0].getY() - zOffset);
						bodyBaseSprite.getScreenRect()[1].setY(bodyBaseSprite.getScreenRect()[1].getY() - zOffset);
						bodyExpandedSprite.getScreenRect()[1]
								.setY(bodyExpandedSprite.getScreenRect()[1].getY() - zOffset);
						braidSprite.getScreenRect()[1].setY(braidSprite.getScreenRect()[1].getY() - zOffset);
						body.setScreenPivot(pane.getTmpPoint());
						body.setScreenRect(bodyExpandedSprite.getScreenRect()[0]);
						if (body.isPinned()) {
							MyPane.markList.add(bodyExpandedSprite.getScreenRect()[0]);
						}
						if (body.getBurialState() != src.enums.BurialState.ALL) {
							BodyUtil.drawBody(pane.getBackBufferG2(), pane, body);
						}
						if (body.getMessageBuffer() != null && !MyPane.isDisableScript()) {
							pane.getMsgList().add(body);
						}
						break;
					}
					case SHIT: {
						Shit shit = (Shit) o;
						pane.calcDrawPosition(shit, pane.getTmpRect());
						if (MyPane.getDrawShadowShitBaby() == 1 || shit.getAgeState() != AgeState.BABY
								|| 0 < shit.getZ()) {
							pane.getBackBufferG2().drawImage(shit.getShadowImage(), pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(shit.getZ()));
						pane.getBackBufferG2().drawImage(shit.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case VOMIT: {
						Vomit vomit = (Vomit) o;
						pane.calcDrawPosition(vomit, pane.getTmpRect());
						if (MyPane.getDrawShadowVomitBaby() == 1 || vomit.getAgeState() != AgeState.BABY
								|| 0 < vomit.getZ()) {
							pane.getBackBufferG2().drawImage(vomit.getShadowImage(), pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(vomit.getZ()));
						pane.getBackBufferG2().drawImage(vomit.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case FIX_OBJECT: {
						WorldEntity platform = (WorldEntity) o;
						pane.calcDrawPosition(platform, pane.getTmpRect());
						int layerCount = platform.getImageLayer(pane.getLayerTmp());
						for (int i = 0; i < layerCount; i++) {
							pane.getBackBufferG2().drawImage(pane.getLayerTmp()[i], pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						break;
					}
					case OBJECT: {
						WorldEntity platform = (WorldEntity) o;
						pane.calcDrawPosition(platform, pane.getTmpRect());
						pane.getBackBufferG2().drawImage(platform.getShadowImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(platform.getZ()));
						int layerCount = platform.getImageLayer(pane.getLayerTmp());
						for (int i = 0; i < layerCount; i++) {
							pane.getBackBufferG2().drawImage(pane.getLayerTmp()[i], pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						break;
					}
					case LIGHT_EFFECT: {
						Effect effect = (Effect) o;
						pane.calcDrawPosition(effect, pane.getTmpRect());
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(effect.getZ()));
						pane.getBackBufferG2().drawImage(effect.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
					}
					case BG_OBJECT: {
						TerrainBillboard billboard = (TerrainBillboard) o;
						billboard.draw(pane.getBackBufferG2(), pane);
						break;
					}
					default:
						break;
				}
			}

			for (Entity o : GameWorld.get().getFrontEffectList()) {
				Effect effect = (Effect) o;
				pane.calcDrawPosition(effect, pane.getTmpRect());
				pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(effect.getZ()));
				pane.getBackBufferG2().drawImage(effect.getImage(), pane.getTmpRect().getX(), pane.getTmpRect().getY(),
						pane.getTmpRect().getWidth(), pane.getTmpRect().getHeight(), pane);
			}

			TerrainField.drawCeiling(pane.getBackBufferG2(), pane);
			java.awt.Point mousePos = pane.getMousePosition();

			if (MyPane.isEnableTarget()) {
				Image[] cursor = IconPool.getCursorIconImageArray();
				int cursorBaseIndex = IconPool.CursorIcon.CUR_LB.ordinal();
				for (Rectangle4y rect : MyPane.markList) {
					pane.getBackBufferG2().drawImage(cursor[cursorBaseIndex + 1], rect.getX(), rect.getY(), pane);
					pane.getBackBufferG2().drawImage(cursor[cursorBaseIndex + 0], rect.getX(),
							rect.getY() + rect.getWidth() - 20, pane);
					pane.getBackBufferG2().drawImage(cursor[cursorBaseIndex + 2], rect.getX() + rect.getWidth() - 20,
							rect.getY() + rect.getWidth() - 20, pane);
					pane.getBackBufferG2().drawImage(cursor[cursorBaseIndex + 3], rect.getX() + rect.getWidth() - 20,
							rect.getY(),
							pane);
				}
			}

			if (selectedBodyCheck == null) {
				MyPane.setSelectBody(null);
			}
			selectedBody = MyPane.getSelectBody();
			if (selectedBody != null) {
				Image[] select = IconPool.getCursorIconImageArray();
				int st = IconPool.CursorIcon.SEL_0.ordinal();
				Rectangle4y r = selectedBody.getScreenRect();
				int x = r.getX() + (r.getWidth() >> 1) - 12;
				int y = r.getY() + r.getHeight() + 2;
				pane.getBackBufferG2().drawImage(select[st + (int) (selectedBody.getAge() % 4)], x, y, pane);
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
					pane.getBackBufferG2().setStroke(FieldShape.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShape.PREVIEW_COLOR);
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
					pane.getBackBufferG2().setStroke(FieldShape.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShape.PREVIEW_COLOR);
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

			for (Yukkuri b : pane.getMsgList()) {
				String message = b.getMessageBuffer();
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
