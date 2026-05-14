package org.simyukkuri.logic;

import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameRandom;

/**
 * アリがたかる判定.
 */
public final class AntInfestationPolicy {
	private AntInfestationPolicy() {
	}

	public static void judgeNewAnt(Yukkuri body) {
		int antProbability = 1;
		switch (body.getBodyAgeState()) {
			case BABY:
				antProbability = 240000;
				break;
			case CHILD:
				antProbability = 480000;
				break;
			case ADULT:
				antProbability = 960000;
				break;
			default:
				// NOP.
		}
		if (body.isDirty() || body.isDamaged()) {
			antProbability /= 2;
		}
		if (body.isDontJump()) {
			antProbability /= 2;
		}
		if (GameRandom.nextInt(antProbability) == 1) {
			body.addAttachment(new Ants(body));
			body.clearEvent();
		}
	}
}
