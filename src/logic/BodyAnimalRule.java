package src.logic;

import src.attachment.Ants;
import src.base.BodyAttributes;

/**
 * Bodyの動物由来の被食状態を集約する.
 */
public final class BodyAnimalRule {
	private BodyAnimalRule() {
	}

	public static boolean isEatenByAnimals(BodyAttributes body) {
		return body.getAttachmentSize(Ants.class) != 0;
	}
}
