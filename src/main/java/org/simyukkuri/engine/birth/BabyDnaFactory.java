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

	/**
	 * 母親・父親の情報を基に赤ゆ DNA を生成して返す。
	 * 母親が null の場合や種別が決定できない場合は null を返す。
	 *
	 * @param mother       母親
	 * @param father       父親（null 可）
	 * @param fatherType   父親の種別
	 * @param fatherrAtt   父親の性格
	 * @param fatherInt    父親の知性
	 * @param isRape       強制交配かどうか
	 * @param fatherDamage 父親がダメージ状態かどうか
	 * @param forceCreate  強制生成フラグ
	 * @return 生成した赤ゆ DNA。生成できない場合は null
	 */
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
