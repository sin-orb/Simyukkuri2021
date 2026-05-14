package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import org.simyukkuri.command.GadgetMenu.GadgetList;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.impl.ANYDAmpoule;
import org.simyukkuri.entity.core.attachment.impl.AccelAmpoule;
import org.simyukkuri.entity.core.attachment.impl.BreedingAmpoule;
import org.simyukkuri.entity.core.attachment.impl.HungryAmpoule;
import org.simyukkuri.entity.core.attachment.impl.OrangeAmpoule;
import org.simyukkuri.entity.core.attachment.impl.PoisonAmpoule;
import org.simyukkuri.entity.core.attachment.impl.StopAmpoule;
import org.simyukkuri.entity.core.attachment.impl.VeryShitAmpoule;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameWorld;

final class GadgetAmpouleAction {

	private GadgetAmpouleAction() {
	}

	static void evaluateAmpoule(GadgetList item, MouseEvent ev, Entity targetObject) {
		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getBody().values());
		switch (item) {
			case ORANGE_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(OrangeAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(OrangeAmpoule.class) == 0)
								body.addAttachment(new OrangeAmpoule(body));
						} else {
							if (body.getAttachmentSize(OrangeAmpoule.class) != 0)
								body.removeAttachment(OrangeAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(OrangeAmpoule.class) != 0) {
							body.removeAttachment(OrangeAmpoule.class);
						} else {
							body.addAttachment(new OrangeAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(OrangeAmpoule.class) != 0) {
							body.removeAttachment(OrangeAmpoule.class);
						} else {
							body.addAttachment(new OrangeAmpoule(body));
						}
					}
				}
				break;
			case ACCEL_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(AccelAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(AccelAmpoule.class) == 0)
								body.addAttachment(new AccelAmpoule(body));
						} else {
							if (body.getAttachmentSize(AccelAmpoule.class) != 0)
								body.removeAttachment(AccelAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(AccelAmpoule.class) != 0) {
							body.removeAttachment(AccelAmpoule.class);
						} else {
							body.addAttachment(new AccelAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(AccelAmpoule.class) != 0) {
							body.removeAttachment(AccelAmpoule.class);
						} else {
							body.addAttachment(new AccelAmpoule(body));
						}
					}
				}
				break;
			case STOP_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(StopAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(StopAmpoule.class) == 0)
								body.addAttachment(new StopAmpoule(body));
						} else {
							if (body.getAttachmentSize(StopAmpoule.class) != 0)
								body.removeAttachment(StopAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(StopAmpoule.class) != 0) {
							body.removeAttachment(StopAmpoule.class);
						} else {
							body.addAttachment(new StopAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(StopAmpoule.class) != 0) {
							body.removeAttachment(StopAmpoule.class);
						} else {
							body.addAttachment(new StopAmpoule(body));
						}
					}
				}
				break;
			case HUNGRY_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(HungryAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(HungryAmpoule.class) == 0)
								body.addAttachment(new HungryAmpoule(body));
						} else {
							if (body.getAttachmentSize(HungryAmpoule.class) != 0)
								body.removeAttachment(HungryAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(HungryAmpoule.class) != 0) {
							body.removeAttachment(HungryAmpoule.class);
						} else {
							body.addAttachment(new HungryAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(HungryAmpoule.class) != 0) {
							body.removeAttachment(HungryAmpoule.class);
						} else {
							body.addAttachment(new HungryAmpoule(body));
						}
					}
				}
				break;
			case VERYSHIT_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(VeryShitAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(VeryShitAmpoule.class) == 0)
								body.addAttachment(new VeryShitAmpoule(body));
						} else {
							if (body.getAttachmentSize(VeryShitAmpoule.class) != 0)
								body.removeAttachment(VeryShitAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(VeryShitAmpoule.class) != 0) {
							body.removeAttachment(VeryShitAmpoule.class);
						} else {
							body.addAttachment(new VeryShitAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(VeryShitAmpoule.class) != 0) {
							body.removeAttachment(VeryShitAmpoule.class);
						} else {
							body.addAttachment(new VeryShitAmpoule(body));
						}
					}
				}
				break;
			case POISON_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(PoisonAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(PoisonAmpoule.class) == 0)
								body.addAttachment(new PoisonAmpoule(body));
						} else {
							if (body.getAttachmentSize(PoisonAmpoule.class) != 0)
								body.removeAttachment(PoisonAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(PoisonAmpoule.class) != 0) {
							body.removeAttachment(PoisonAmpoule.class);
						} else {
							body.addAttachment(new PoisonAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(PoisonAmpoule.class) != 0) {
							body.removeAttachment(PoisonAmpoule.class);
						} else {
							body.addAttachment(new PoisonAmpoule(body));
						}
					}
				}
				break;
			case BREEDING_AMP:
				if (ev.isShiftDown()) {
					int attachmentCount = 0;
					if (targetObject instanceof Yukkuri) {
						attachmentCount = ((Yukkuri) targetObject).getAttachmentSize(BreedingAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (attachmentCount == 0) {
							if (body.getAttachmentSize(BreedingAmpoule.class) == 0)
								body.addAttachment(new BreedingAmpoule(body));
						} else {
							if (body.getAttachmentSize(BreedingAmpoule.class) != 0)
								body.removeAttachment(BreedingAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(BreedingAmpoule.class) != 0) {
							body.removeAttachment(BreedingAmpoule.class);
						} else {
							body.addAttachment(new BreedingAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(BreedingAmpoule.class) != 0) {
							body.removeAttachment(BreedingAmpoule.class);
						} else {
							body.addAttachment(new BreedingAmpoule(body));
						}
					}
				}
				break;
			case ANYD_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (targetObject instanceof Yukkuri) {
						flag = ((Yukkuri) targetObject).getAttachmentSize(ANYDAmpoule.class);
					}
					for (Yukkuri body : bodyList) {
						if (flag == 0) {
							if (body.getAttachmentSize(ANYDAmpoule.class) == 0)
								body.addAttachment(new ANYDAmpoule(body));
						} else {
							if (body.getAttachmentSize(ANYDAmpoule.class) != 0)
								body.removeAttachment(ANYDAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Yukkuri body : bodyList) {
						if (body.getAttachmentSize(ANYDAmpoule.class) != 0) {
							body.removeAttachment(ANYDAmpoule.class);
						} else {
							body.addAttachment(new ANYDAmpoule(body));
						}
					}
				} else {
					if (targetObject instanceof Yukkuri) {
						Yukkuri body = (Yukkuri) targetObject;
						if (body.getAttachmentSize(ANYDAmpoule.class) != 0) {
							body.removeAttachment(ANYDAmpoule.class);
						} else {
							body.addAttachment(new ANYDAmpoule(body));
						}
					}
				}
				break;

			default:
				break;
		}
	}
}
