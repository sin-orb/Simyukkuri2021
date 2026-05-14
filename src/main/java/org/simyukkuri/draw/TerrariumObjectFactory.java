package org.simyukkuri.draw;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameWorld;

/**
 * Terrarium から切り出したオブジェクト生成・登録処理。
 */
public final class TerrariumObjectFactory {
	private TerrariumObjectFactory() {
	}

	/**
	 * うんうんを作成して登録する。
	 *
	 * @return 生成したオブジェクトID
	 */
	public static int addShit(int x, int y, int z, Yukkuri b, YukkuriType type) {
		Shit shit = new Shit(x, y, z, b, type);
		GameWorld.get().getCurrentMap().getShit().put(shit.objId, shit);
		return shit.objId;
	}

	/**
	 * ゆ下痢を作成して登録する。
	 */
	public static void addCrushedShit(int x, int y, int z, Yukkuri b, YukkuriType type) {
		Shit s = new Shit(x, y, z, b, type);
		s.crushShit();
		if (b != null && b.getMostDepth() < 0) {
			s.setMostDepth(b.getMostDepth());
			s.setMostDepth(b.getZ());
		}
		GameWorld.get().getCurrentMap().getShit().put(s.objId, s);
	}

	/**
	 * 吐餡を作成して登録する。
	 */
	public static Vomit addVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		GameWorld.get().getCurrentMap().getVomit().put(v.objId, v);
		if (body != null && body.getMostDepth() < 0) {
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		return v;
	}

	/**
	 * つぶれ吐餡を作成して登録する。
	 */
	public static void addCrushedVomit(int x, int y, int z, Yukkuri body, YukkuriType type) {
		Vomit v = new Vomit(x, y, z, body, type);
		v.crushVomit();
		if (body != null && body.getMostDepth() < 0) {
			v.setMostDepth(body.getMostDepth());
			v.setMostDepth(body.getZ());
		}
		GameWorld.get().getCurrentMap().getVomit().put(v.objId, v);
	}
}
