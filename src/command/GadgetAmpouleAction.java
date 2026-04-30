package src.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.BreedingAmpoule;
import src.attachment.HungryAmpoule;
import src.attachment.OrangeAmpoule;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Body;
import src.base.Obj;
import src.command.GadgetMenu.GadgetList;
import src.util.GameWorld;

final class GadgetAmpouleAction {

	private GadgetAmpouleAction() {
	}

	static void evaluateAmpoule(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
		switch (item) {
			case ORANGE_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(OrangeAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(OrangeAmpoule.class) == 0)
								b.addAttachment(new OrangeAmpoule(b));
						} else {
							if (b.getAttachmentSize(OrangeAmpoule.class) != 0)
								b.removeAttachment(OrangeAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(OrangeAmpoule.class) != 0) {
							b.removeAttachment(OrangeAmpoule.class);
						} else {
							b.addAttachment(new OrangeAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(OrangeAmpoule.class) != 0) {
							b.removeAttachment(OrangeAmpoule.class);
						} else {
							b.addAttachment(new OrangeAmpoule((Body) found));
						}
					}
				}
				break;
			case ACCEL_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(AccelAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(AccelAmpoule.class) == 0)
								b.addAttachment(new AccelAmpoule(b));
						} else {
							if (b.getAttachmentSize(AccelAmpoule.class) != 0)
								b.removeAttachment(AccelAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(AccelAmpoule.class) != 0) {
							b.removeAttachment(AccelAmpoule.class);
						} else {
							b.addAttachment(new AccelAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(AccelAmpoule.class) != 0) {
							b.removeAttachment(AccelAmpoule.class);
						} else {
							b.addAttachment(new AccelAmpoule((Body) found));
						}
					}
				}
				break;
			case STOP_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(StopAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(StopAmpoule.class) == 0)
								b.addAttachment(new StopAmpoule(b));
						} else {
							if (b.getAttachmentSize(StopAmpoule.class) != 0)
								b.removeAttachment(StopAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(StopAmpoule.class) != 0) {
							b.removeAttachment(StopAmpoule.class);
						} else {
							b.addAttachment(new StopAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(StopAmpoule.class) != 0) {
							b.removeAttachment(StopAmpoule.class);
						} else {
							b.addAttachment(new StopAmpoule((Body) found));
						}
					}
				}
				break;
			case HUNGRY_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(HungryAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(HungryAmpoule.class) == 0)
								b.addAttachment(new HungryAmpoule(b));
						} else {
							if (b.getAttachmentSize(HungryAmpoule.class) != 0)
								b.removeAttachment(HungryAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(HungryAmpoule.class) != 0) {
							b.removeAttachment(HungryAmpoule.class);
						} else {
							b.addAttachment(new HungryAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(HungryAmpoule.class) != 0) {
							b.removeAttachment(HungryAmpoule.class);
						} else {
							b.addAttachment(new HungryAmpoule((Body) found));
						}
					}
				}
				break;
			case VERYSHIT_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(VeryShitAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(VeryShitAmpoule.class) == 0)
								b.addAttachment(new VeryShitAmpoule(b));
						} else {
							if (b.getAttachmentSize(VeryShitAmpoule.class) != 0)
								b.removeAttachment(VeryShitAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(VeryShitAmpoule.class) != 0) {
							b.removeAttachment(VeryShitAmpoule.class);
						} else {
							b.addAttachment(new VeryShitAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(VeryShitAmpoule.class) != 0) {
							b.removeAttachment(VeryShitAmpoule.class);
						} else {
							b.addAttachment(new VeryShitAmpoule((Body) found));
						}
					}
				}
				break;
			case POISON_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(PoisonAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(PoisonAmpoule.class) == 0)
								b.addAttachment(new PoisonAmpoule(b));
						} else {
							if (b.getAttachmentSize(PoisonAmpoule.class) != 0)
								b.removeAttachment(PoisonAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(PoisonAmpoule.class) != 0) {
							b.removeAttachment(PoisonAmpoule.class);
						} else {
							b.addAttachment(new PoisonAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(PoisonAmpoule.class) != 0) {
							b.removeAttachment(PoisonAmpoule.class);
						} else {
							b.addAttachment(new PoisonAmpoule((Body) found));
						}
					}
				}
				break;
			case BREEDING_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(BreedingAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(BreedingAmpoule.class) == 0)
								b.addAttachment(new BreedingAmpoule(b));
						} else {
							if (b.getAttachmentSize(BreedingAmpoule.class) != 0)
								b.removeAttachment(BreedingAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(BreedingAmpoule.class) != 0) {
							b.removeAttachment(BreedingAmpoule.class);
						} else {
							b.addAttachment(new BreedingAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(BreedingAmpoule.class) != 0) {
							b.removeAttachment(BreedingAmpoule.class);
						} else {
							b.addAttachment(new BreedingAmpoule((Body) found));
						}
					}
				}
				break;
			case ANYD_AMP:
				if (ev.isShiftDown()) {
					int flag = 0;
					if (found instanceof Body) {
						flag = ((Body) found).getAttachmentSize(ANYDAmpoule.class);
					}
					for (Body b : bodyList) {
						if (flag == 0) {
							if (b.getAttachmentSize(ANYDAmpoule.class) == 0)
								b.addAttachment(new ANYDAmpoule(b));
						} else {
							if (b.getAttachmentSize(ANYDAmpoule.class) != 0)
								b.removeAttachment(ANYDAmpoule.class);
						}
					}
				} else if (ev.isControlDown()) {
					for (Body b : bodyList) {
						if (b.getAttachmentSize(ANYDAmpoule.class) != 0) {
							b.removeAttachment(ANYDAmpoule.class);
						} else {
							b.addAttachment(new ANYDAmpoule(b));
						}
					}
				} else {
					if (found instanceof Body) {
						Body b = (Body) found;
						if (b.getAttachmentSize(ANYDAmpoule.class) != 0) {
							b.removeAttachment(ANYDAmpoule.class);
						} else {
							b.addAttachment(new ANYDAmpoule((Body) found));
						}
					}
				}
				break;

			default:
				break;
		}
	}
}
