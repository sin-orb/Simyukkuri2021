package org.simyukkuri.engine.transform;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;

/**
 * 変身時に body の状態をレイヤ単位で複製する.
 */
public final class TransformationBodyCopier {

	private TransformationBodyCopier() {
	}

	/**
	 * ゆっくりのステータスを from → to へ複製する.
	 * 種族固有ベースパラメータ (xxxBase 配列, speed, cost 等) はコピーしない.
	 * スプライト・名前セットは copyStateTo チェーン内の BodyAttributes レイヤで処理する.
	 * NameSet (種族名称) は TransformationService 側の setBaseBodyFileName +
	 * readYukkuriIniFile で上書きされる.
	 *
	 * @param to   変身後のゆっくり
	 * @param from 変身前のゆっくり
	 */
	public static void copy(Yukkuri to, Yukkuri from) {
		from.copyStateTo(to);
	}
}
