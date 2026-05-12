package src.engine.birth;

import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Attitude;
import src.enums.Intelligence;
import src.enums.YukkuriType;

/**
 * 赤ゆDNAの組み立て.
 */
public final class BabyDnaFactory {
	private BabyDnaFactory() {
	}

	public static Dna createBabyDna(Yukkuri mother, Yukkuri father, YukkuriType fatherType, Attitude fatherrAtt,
			Intelligence fatherInt, boolean isRape, boolean fatherDamage, boolean forceCreate) {
		if (mother == null) {
			return null;
		}
		YukkuriType babyType = YukkuriBirthTypeResolver.resolveBabyType(mother, father, fatherType, forceCreate,
				fatherDamage);
		if (babyType == null) {
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
