package src.command;

import java.awt.event.MouseEvent;
import java.util.List;

import src.attachment.Ants;
import src.attachment.Badge;
import src.base.Body;
import src.base.Obj;
import src.command.GadgetMenu.GadgetList;
import src.enums.BodyRank;
import src.enums.PublicRank;
import src.event.PredatorsGameEvent;
import src.logic.BadgeLogic;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.YukkuriUtil;

/**
 * テスト/デバッグカテゴリのコマンド群
 */
public class GadgetDebugAction {

	/**
	 * テストカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTest(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
			case RANKSET:
				if (found instanceof Body) {
					Body b = (Body) found;
					BodyRank rank = b.getBodyRank();
					if (rank == BodyRank.KAIYU) {
						b.setBodyRank(BodyRank.NORAYU);
					} else {
						b.setBodyRank(BodyRank.KAIYU);
					}
				}
				break;
			case RANKSET2:
				if (found instanceof Body) {
					Body b = (Body) found;
					PublicRank rank = b.getPublicRank();
					if (rank == PublicRank.NONE) {
						b.setPublicRank(PublicRank.UnunSlave);
						b.getFavItem().clear();
						Body p = YukkuriUtil.getBodyInstance(b.getPartner());
						if (p != null) {
							// うんうんどれいになるようなくずとは りこんっ！だよ！！
							b.setPartner(-1);
							p.setPartner(-1);
						}
					} else {
						b.setPublicRank(PublicRank.NONE);
						b.getFavItem().clear();
					}
				}
				break;
			case EVENT_SHIT:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.wakeup();
					List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToShit(b, childrenList);
					}
				}
				break;
			case EVENT_EAT:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.wakeup();
					List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.goToEat(b, childrenList);
					}
				}
				break;
			case EVENT_RIDEYUKKURI:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.wakeup();
					List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.rideOnParent(b, childrenList);
					}
				}
				break;
			case EVENT_PROUDCHILD:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.wakeup();
					List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
					if (childrenList != null && childrenList.size() != 0) {
						FamilyActionLogic.proudChild(b, childrenList);
					}
				}
				break;
			case SETVAIN:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.getInVain(true);
				}
				break;
			case Yunnyaa:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.doYunnyaa(true);
				}
				break;
			case BEGGINGFORLIFE:
				if (found instanceof Body) {
					Body b = (Body) found;
					b.begForLife(true);
				}
				break;
			case PREDATORSGAME:
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.isPredatorType())
						EventLogic.addWorldEvent(new PredatorsGameEvent(b, null, null, 1), b,
								GameMessages.getMessage(b, MessagePool.Action.GameStart));
				}
				break;
			case INVITEANTS:
				if (ev.isShiftDown() || ev.isControlDown()) {
					break;
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(Ants.class) != 0) {
							b.removeAnts();
						} else {
							b.addAttachment(new Ants((Body) found));
						}
					}
				}
				break;
			case FEED:
				if (ev.isShiftDown() || ev.isControlDown()) {
					break;
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.isDead())
							break;
						b.feed();
					}
				}
				break;
			case BADGE:
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(Badge.class) != 0) {
						b.removeAttachment(Badge.class);
					} else {
						BadgeLogic.badgeTest(b);
					}
				}
				break;
			default:
				break;
		}
	}
}
