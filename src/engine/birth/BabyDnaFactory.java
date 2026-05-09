package src.engine.birth;

import src.base.Yukkuri;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.game.Dna;

/**
 * 赤ゆDNAの組み立て.
 */
public final class BabyDnaFactory {
	private BabyDnaFactory() {
	}

	public static Dna createBabyDna(Yukkuri mother, Yukkuri father, int iFatherType, Attitude fatherrAtt,
			Intelligence fatherInt, boolean isRape, boolean fatherDamage, boolean forceCreate) {
		if (mother == null) {
			return null;
		}
		int babyType = YukkuriBirthTypeResolver.resolveBabyType(mother, father, iFatherType, forceCreate, fatherDamage);
		if (babyType < 0) {
			return null;
		}
		Dna ret = new Dna();
		ret.setType(babyType);
		ret.setRaperChild(isRape);
		ret.setMother(mother.getUniqueID());
		ret.setFather(father.getUniqueID());
		ret.setAttitude(YukkuriBirthTypeResolver.resolveAttitude(mother, fatherrAtt));
		ret.setIntelligence(YukkuriBirthTypeResolver.resolveIntelligence(mother, fatherInt));
		return ret;
	}
}
