package src.command;

import java.awt.event.MouseEvent;
import java.util.List;

import src.command.GadgetMenu.GadgetList;
import src.entity.core.Entity;
import src.entity.core.attachment.impl.Ants;
import src.entity.core.attachment.impl.Badge;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.BodyRank;
import src.enums.PublicRank;
import src.event.impl.PredatorsGameEvent;
import src.logic.BadgeLogic;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;
import src.util.GameMessages;

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
	public static void evaluateTest(GadgetList item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case RANKSET:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					BodyRank rank = body.getBodyRank();
					if (rank == BodyRank.KAIYU) {
						body.setBodyRank(BodyRank.NORAYU);
					} else {
						body.setBodyRank(BodyRank.KAIYU);
					}
				}
				break;
			case RANKSET2:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					PublicRank rank = body.getPublicRank();
					if (rank == PublicRank.NONE) {
						body.setPublicRank(PublicRank.UnunSlave);
						body.getFavoriteItems().clear();
						Yukkuri partnerBody = src.util.BodyRegistry.getBodyInstance(body.getPartner());
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
					List<Yukkuri> childrenList = BodyLogic.createActiveChildList(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToShit(body, childrenList);
					}
				}
				break;
			case EVENT_EAT:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = BodyLogic.createActiveChildList(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToEat(body, childrenList);
					}
				}
				break;
			case EVENT_RIDEYUKKURI:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = BodyLogic.createActiveChildList(body, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.rideOnParent(body, childrenList);
					}
				}
				break;
			case EVENT_PROUDCHILD:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
					body.wakeup();
					List<Yukkuri> childrenList = BodyLogic.createActiveChildList(body, true);
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
			case Yunnyaa:
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
					if (body.isPredatorType())
						EventLogic.addWorldEvent(new PredatorsGameEvent(body, null, null, 1), body,
								GameMessages.getMessage(body, MessagePool.Action.GameStart));
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
						if (body.isDead())
							break;
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
