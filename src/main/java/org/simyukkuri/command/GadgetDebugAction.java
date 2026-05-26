package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import java.util.List;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.attachment.impl.Badge;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.event.impl.PredatorsGameEvent;
import org.simyukkuri.logic.BadgeLogic;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.logic.FamilyActionLogic;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;

/**
 * テスト/デバッグカテゴリのコマンド群
 */
public class GadgetDebugAction {

	/**
	 * テストカテゴリの実行
	 *
	 * @param item         実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateTest(GadgetMenuChoice item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case RANKSET:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					YukkuriRank rank = body.getRank();
					if (rank == YukkuriRank.KAIYU) {
						body.setRank(YukkuriRank.NORAYU);
					} else {
						body.setRank(YukkuriRank.KAIYU);
					}
				}
				break;
			case RANKSET2:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					PublicRank rank = body.getPublicRank();
					if (rank == PublicRank.NONE) {
						body.setPublicRank(PublicRank.UNUN_SLAVE);
						body.getFavoriteItems().clear();
						Yukkuri partnerBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner());
						if (partnerBody != null) {
							// うんうんどれいになるようなくずとは りこんっ！だよ！！
							body.setPartner(-1);
							partnerBody.setPartner(-1);
						}
					} else {
						body.setPublicRank(PublicRank.NONE);
						body.getFavoriteItems().clear();
					}
				}
				break;
			case EVENT_SHIT:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToShit(body, childrenList);
					}
				}
				break;
			case EVENT_EAT:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToEat(body, childrenList);
					}
				}
				break;
			case EVENT_RIDEYUKKURI:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.rideOnParent(body, childrenList);
					}
				}
				break;
			case EVENT_PROUDCHILD:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.proudChild(body, childrenList);
					}
				}
				break;
			case SETVAIN:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.getInVain(true);
				}
				break;
			case YUNNYAA:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.doYunnyaa(true);
				}
				break;
			case BEGGINGFORLIFE:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.begForLife(true);
				}
				break;
			case PREDATORSGAME:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					if (body.isPredatorType()) {
						EventLogic.addWorldEvent(new PredatorsGameEvent(body, null, null, 1), body,
								GameMessages.getMessage(body, MessagePool.Action.GameStart));
					}
				}
				break;
			case INVITEANTS:
				if (ev.isShiftDown() || ev.isControlDown()) {
					break;
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(Ants.class) != 0) {
							body.removeAnts();
						} else {
							body.addAttachment(new Ants(body));
						}
					}
				}
				break;
			case FEED:
				if (ev.isShiftDown() || ev.isControlDown()) {
					break;
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.isDead()) {
							break;
						}
						body.feed();
					}
				}
				break;
			case BADGE:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					if (body.getAttachmentSize(Badge.class) != 0) {
						body.removeAttachment(Badge.class);
					} else {
						BadgeLogic.badgeTest(body);
					}
				}
				break;
			default:
				break;
		}
	}
}
