package org.simyukkuri.draw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.List;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.MainCategoryName;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.system.IconPool;
import org.simyukkuri.system.LoggerYukkuri;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.ui.MainCommandUi;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.YukkuriUtil;
import org.simyukkuri.visual.TerrainBillboard;

final class Renderer {
	void render(MyPane pane, Graphics g) {
		synchronized (SimYukkuri.lock) {
			final WorldState map = GameWorld.get().getCurrentWorldState();

			pane.getRenderQueue().clear();
			pane.getRenderQueue().addAll(GameWorld.get().getWorldEntities());
			pane.getRenderQueue().addAll(GameWorld.get().getFixedObjects());
			pane.getRenderQueue().addAll(GameWorld.get().getObjects());
			pane.getRenderQueue().addAll(GameWorld.get().getSortedEffects());
			pane.getRenderQueue().addAll(TerrainField.getBillboards());
			Collections.sort(pane.getRenderQueue(), RenderOrderComparator.getInstance());

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, pane.getRenderScale());

			Rectangle4y dispArea = Translate.getDisplayArea();
			pane.getBackBufferG2().setClip(dispArea.getX(), dispArea.getY(), dispArea.getWidth(), dispArea.getHeight());

			pane.getMessageBodies().clear();
			MyPane.markList.clear();
			Yukkuri selectedBody = MyPane.getSelectedYukkuri();
			if (selectedBody != null) {
				if (selectedBody.isRemoved()) {
					MyPane.setSelectedYukkuri(null);
					selectedBody = null;
				} else {
					MainCommandUi.showStatus(selectedBody);
				}
			}

			TerrainField.drawBackGroundImage(pane.getBackBufferG2(), pane);
			TerrainField.drawFloor(pane.getBackBufferG2(), pane);

			for (int i = map.getFarms().size() - 1; i >= 0; i--) {
				map.getFarms().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = map.getPools().size() - 1; i >= 0; i--) {
				map.getPools().get(i).drawShape(pane.getBackBufferG2());
			}
			for (int i = map.getBeltconveyors().size() - 1; i >= 0; i--) {
				map.getBeltconveyors().get(i).drawShape(pane.getBackBufferG2());
			}

			List<WorldEntity> platformList = GameWorld.get().getPlatforms();
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
			for (Barrier barrier : map.getBarriers()) {
				barrier.drawShape(pane.getBackBufferG2());
			}
			pane.getBackBufferG2().setStroke(MyPane.getDefaultStroke());

			Sprite bodyBaseSprite;
			Sprite bodyExpandedSprite;
			Sprite braidSprite;
			Yukkuri selectedBodyCheck = null;

			for (Object o : pane.getRenderQueue()) {
				if (o instanceof TerrainBillboard) {
					TerrainBillboard billboard = (TerrainBillboard) o;
					billboard.draw(pane.getBackBufferG2(), pane);
					continue;
				}
				Entity entity = (Entity) o;
				switch (entity.getObjType()) {
					case YUKKURI:
						{
						Yukkuri body = (Yukkuri) entity;
						if (body == MyPane.getSelectedYukkuri()) {
							selectedBodyCheck = body;
						}
						final int direction = body.getDirection().ordinal();
						body.updateSpriteSize();
						bodyBaseSprite = body.getSpriteSetite();
						bodyExpandedSprite = body.getExpandedSpriteSet();
						braidSprite = body.getBraidSprite();
						final int shadowHeight = body.getShadowH();
						Translate.translate(body.getDrawOfsX(), body.getDrawOfsY(), pane.getTmpPoint());
						pane.calcDrawBodyPosition(pane.getTmpPoint(), bodyBaseSprite);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), bodyExpandedSprite);
						pane.calcDrawBodyPosition(pane.getTmpPoint(), braidSprite);
						boolean drawShadow = true;
						Entity parentObject = body.takeMappedObj(body.getParentLinkId());
						if (parentObject != null && parentObject.getZ() < body.getZ()) {
							drawShadow = false;
						}
						final Rectangle4y dirSr = bodyExpandedSprite.getScreenRect()[direction];
						if (drawShadow && body.isShadowVisible() && !body.isUnBirth() && 0 <= body.getZ()) {
							if (body.getType() == YukkuriType.REMIRYA && body.isImageNagasiMode()) {
								pane.getBackBufferG2().drawImage(body.getShadowImage(),
										dirSr.getX(),
										dirSr.getY() + dirSr.getHeight() * 11 / 12 - shadowHeight,
										dirSr.getWidth(), shadowHeight, pane);
							} else {
								pane.getBackBufferG2().drawImage(body.getShadowImage(),
										dirSr.getX(),
										dirSr.getY() + dirSr.getHeight() - shadowHeight,
										dirSr.getWidth(), shadowHeight, pane);
							}
						}
						int zoffset = Translate.translateZ(body.getZ());
						bodyBaseSprite.getScreenRect()[0].setY(bodyBaseSprite.getScreenRect()[0].getY() - zoffset);
						bodyExpandedSprite.getScreenRect()[0]
								.setY(bodyExpandedSprite.getScreenRect()[0].getY() - zoffset);
						braidSprite.getScreenRect()[0].setY(braidSprite.getScreenRect()[0].getY() - zoffset);
						bodyBaseSprite.getScreenRect()[1].setY(bodyBaseSprite.getScreenRect()[1].getY() - zoffset);
						bodyExpandedSprite.getScreenRect()[1]
								.setY(bodyExpandedSprite.getScreenRect()[1].getY() - zoffset);
						braidSprite.getScreenRect()[1].setY(braidSprite.getScreenRect()[1].getY() - zoffset);
						body.setScreenPivot(pane.getTmpPoint());
						body.setScreenRect(bodyExpandedSprite.getScreenRect()[0]);
						if (body.isPinned()) {
							MyPane.markList.add(bodyExpandedSprite.getScreenRect()[0]);
						}
						if (body.getBurialState() != org.simyukkuri.enums.BurialState.ALL) {
							YukkuriUtil.drawYukkuri(pane.getBackBufferG2(), pane, body);
						}
						if (body.getMessageBuffer() != null && !MyPane.isDisableScript()) {
							pane.getMessageBodies().add(body);
						}
						break;
						}
					case SHIT:
						{
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
					case VOMIT:
						{
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
					case FIX_OBJECT:
						{
						WorldEntity platform = (WorldEntity) entity;
						pane.calcDrawPosition(platform, pane.getTmpRect());
						int layerCount = platform.getImageLayer(pane.getLayerTmp());
						for (int i = 0; i < layerCount; i++) {
							pane.getBackBufferG2().drawImage(pane.getLayerTmp()[i], pane.getTmpRect().getX(),
									pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
									pane.getTmpRect().getHeight(), pane);
						}
						break;
						}
					case OBJECT:
						{
						WorldEntity platform = (WorldEntity) entity;
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
					case LIGHT_EFFECT:
						{
						Effect effect = (Effect) entity;
						pane.calcDrawPosition(effect, pane.getTmpRect());
						pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(effect.getZ()));
						pane.getBackBufferG2().drawImage(effect.getImage(), pane.getTmpRect().getX(),
								pane.getTmpRect().getY(), pane.getTmpRect().getWidth(),
								pane.getTmpRect().getHeight(), pane);
						break;
						}
					default:
						break;
				}
			}

			for (Entity o : GameWorld.get().getFrontEffects()) {
				Effect effect = (Effect) o;
				pane.calcDrawPosition(effect, pane.getTmpRect());
				pane.getTmpRect().setY(pane.getTmpRect().getY() - Translate.translateZ(effect.getZ()));
				pane.getBackBufferG2().drawImage(effect.getImage(), pane.getTmpRect().getX(), pane.getTmpRect().getY(),
						pane.getTmpRect().getWidth(), pane.getTmpRect().getHeight(), pane);
			}

			TerrainField.drawCeiling(pane.getBackBufferG2(), pane);
			final java.awt.Point mousePos = pane.getMousePosition();

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
				MyPane.setSelectedYukkuri(null);
			}
			selectedBody = MyPane.getSelectedYukkuri();
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

			GadgetMenuChoice curGadget = GadgetMenu.getCurrentGadget();
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.BARRIER) {
				if ((SimYukkuri.fieldSx >= 0) && (SimYukkuri.fieldSy >= 0)
						&& (SimYukkuri.fieldEx >= 0) && (SimYukkuri.fieldEy >= 0)) {
					pane.getBackBufferG2().setStroke(FieldShape.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShape.PREVIEW_COLOR);
					switch (curGadget) {
						case GAP_MINI:
						case GAP_BIG:
						case NET_MINI:
						case NET_BIG:
						case WALL:
						case ITEM:
						case NO_UNUN:
						case KEKKAI:
							Barrier.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSx, SimYukkuri.fieldSy,
									SimYukkuri.fieldEx, SimYukkuri.fieldEy);
							break;
						case POOL:
							Pool.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSx, SimYukkuri.fieldSy,
									SimYukkuri.fieldEx, SimYukkuri.fieldEy);
							break;
						case FARM:
							Farm.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSx, SimYukkuri.fieldSy,
									SimYukkuri.fieldEx, SimYukkuri.fieldEy);
							break;
						case BELTCONVEYOR:
							Beltconveyor.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSx, SimYukkuri.fieldSy,
									SimYukkuri.fieldEx, SimYukkuri.fieldEy);
							break;
						default:
							break;
					}
				}
			}
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.CONVEYOR) {
				if ((SimYukkuri.fieldSx >= 0) && (SimYukkuri.fieldSy >= 0)
						&& (SimYukkuri.fieldEx >= 0) && (SimYukkuri.fieldEy >= 0)) {
					pane.getBackBufferG2().setStroke(FieldShape.PREVIEW_STROKE);
					pane.getBackBufferG2().setColor(FieldShape.PREVIEW_COLOR);
					switch (curGadget) {
						case BELTCONVEYOR_CUSTOM:
							BeltconveyorObj.drawPreview(pane.getBackBufferG2(), SimYukkuri.fieldSx, SimYukkuri.fieldSy,
									SimYukkuri.fieldEx, SimYukkuri.fieldEy);
							break;
						default:
							break;
					}
				}
			}

			g2.drawImage(pane.getBackBufferImage(), 0, 0, Translate.getCanvasW(), Translate.getCanvasH(),
					dispArea.getX(), dispArea.getY(), dispArea.getX() + dispArea.getWidth(),
					dispArea.getY() + dispArea.getHeight(), pane);

			LinearGradientPaint sky = TerrainField.getSkyGrad(GameEnvironment.getDayState().ordinal());
			if (sky != null) {
				g2.setPaint(sky);
				g2.fillRect(0, 0, Translate.getFieldW() * 100 / Translate.getWorldScale(),
						Translate.getFieldH() * 100 / Translate.getWorldScale());
			}

			for (Yukkuri b : pane.getMessageBodies()) {
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
								Image helpIcon = GadgetMenu.getHelpIconImage(
										GadgetMenu.getCurrentHelpIcon()[i][j]);
								g2.drawImage(helpIcon, px, py, pane);
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
