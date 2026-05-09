package src.logic;

import src.base.BodyAttributes;
import src.enums.Attitude;
import src.enums.Damage;
import src.enums.HairState;
import src.enums.Intelligence;
import src.enums.LovePlayer;
import src.enums.Trauma;

/**
 * Bodyのコア状態を集約する.
 */
public final class BodyCoreStateRule {
	private BodyCoreStateRule() {
	}

	public static int getDamage(BodyAttributes body) {
		return body.getDamageRaw();
	}

	public static void setDamage(BodyAttributes body, int value) {
		body.setDamageRaw(value);
	}

	public static int getStress(BodyAttributes body) {
		return body.getStressRaw();
	}

	public static void setStress(BodyAttributes body, int value) {
		if (value > 0) {
			body.setStressRaw(value);
		}
	}

	public static int getTang(BodyAttributes body) {
		return body.getTangRaw();
	}

	public static void setTang(BodyAttributes body, int value) {
		body.setTangRaw(value);
	}

	public static Damage getDamageState(BodyAttributes body) {
		int limit = body.getDamageLimitBase()[body.getBodyAgeState().ordinal()];
		int damage = body.getDamageRaw();
		if (damage > limit) {
			body.toDead();
			return Damage.TOOMUCH;
		}
		if (damage >= limit * 3 / 4) {
			return Damage.TOOMUCH;
		}
		if (damage >= limit / 2) {
			return Damage.VERY;
		}
		return Damage.NONE;
	}

	public static void setDamageState(BodyAttributes body, Damage value) {
		body.setDamageStateRaw(value);
	}

	public static Attitude getAttitude(BodyAttributes body) {
		return body.getAttitudeRaw();
	}

	public static void setAttitude(BodyAttributes body, Attitude value) {
		body.setAttitudeRaw(value);
	}

	public static Intelligence getIntelligence(BodyAttributes body) {
		return body.getIntelligenceRaw();
	}

	public static void setIntelligence(BodyAttributes body, Intelligence value) {
		body.setIntelligenceRaw(value);
	}

	public static int getShit(BodyAttributes body) {
		return body.getShitRaw();
	}

	public static void setShit(BodyAttributes body, int value) {
		body.setShitRaw(value);
	}

	public static int getMemories(BodyAttributes body) {
		return body.getMemoriesRaw();
	}

	public static void setMemories(BodyAttributes body, int value) {
		body.setMemoriesRaw(value);
	}

	public static Trauma getTrauma(BodyAttributes body) {
		return body.getTraumaRaw();
	}

	public static void setTrauma(BodyAttributes body, Trauma value) {
		body.setTraumaRaw(value);
	}

	public static int getLovePlayer(BodyAttributes body) {
		return body.getLovePlayerRaw();
	}

	public static void setLovePlayer(BodyAttributes body, int value) {
		body.setLovePlayerRaw(value);
	}

	public static LovePlayer getLovePlayerState(BodyAttributes body) {
		return body.getLovePlayerStateRaw();
	}

	public static void setLovePlayerState(BodyAttributes body, LovePlayer value) {
		body.setLovePlayerStateRaw(value);
	}

	public static HairState getHairState(BodyAttributes body) {
		return body.getHairStateRaw();
	}

	public static void setHairState(BodyAttributes body, HairState value) {
		body.setHairStateRaw(value);
	}
}
