package org.simyukkuri.engine.birth;

import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.YukkuriType;

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
		ret.setFather(father == null ? -1 : father.getUniqueID());
		ret.setAttitude(YukkuriBirthTypeResolver.resolveAttitude(mother, fatherrAtt));
		ret.setIntelligence(YukkuriBirthTypeResolver.resolveIntelligence(mother, fatherInt));
		return ret;
	}
}
