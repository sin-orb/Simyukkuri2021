package src.draw;

import src.entity.core.effect.Effect;
import src.entity.core.effect.impl.BakeSmoke;
import src.entity.core.effect.impl.Hit;
import src.entity.core.effect.impl.Mix;
import src.entity.core.effect.impl.Steam;
import src.enums.EffectType;

/**
 * Terrarium から切り出したエフェクト生成補助。
 */
public final class TerrariumEffectFactory {
	private TerrariumEffectFactory() {
	}

	/**
	 * 指定タイプのエフェクトを生成する。
	 *
	 * @return 生成されたエフェクト。対応外の場合はnull
	 */
	public static Effect addEffect(EffectType type, int x, int y, int z, int vx, int vy, int vz,
			boolean invert, int life, int loop, boolean end, boolean grav, boolean front) {
		switch (type) {
			case BAKE:
				return new BakeSmoke(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			case HIT:
				return new Hit(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			case MIX:
				return new Mix(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			case STEAM:
				return new Steam(x, y, z, vx, vy, vz, invert, life, loop, end, grav, front);
			default:
				return null;
		}
	}
}
