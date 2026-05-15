package org.simyukkuri.engine.transform;

import java.util.function.BooleanSupplier;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.YukkuriFactory;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;
import org.simyukkuri.util.IniFileUtil;

/**
 * ゆっくり変身の置換処理をまとめるサービス.
 */
public final class TransformationService {
	private TransformationService() {
	}

	/**
	 * 指定タイプへ body を置換する.
	 *
	 * @param from       変身前
	 * @param targetType 変身先種別
	 */
	public static void transform(Yukkuri from, YukkuriType targetType) {
		transform(from, targetType, () -> true);
	}

	/**
	 * 指定タイプへ body を置換する.
	 *
	 * @param from       変身前
	 * @param targetType 変身先種別
	 * @param dosMaker   DOSまりさ判定用 callback
	 */
	public static void transform(Yukkuri from, YukkuriType targetType, BooleanSupplier dosMaker) {
		if (from == null || targetType == null) {
			return;
		}

		synchronized (SimYukkuri.lock) {
			if (TransformationPolicy.needsDosReservation(targetType)
					&& !GameWorld.get().getCurrentWorldState().makeDos()) {
				return;
			}

			int originalId = from.getUniqueID();
			Yukkuri to = YukkuriFactory.create(from.getX(), from.getY(), from.getZ(), targetType, null,
					from.getAgeState(), null, null, false, GameView::loadYukkuriImage, dosMaker);
			TransformationBodyCopier.copy(to, from);
			TransformationPolicy.normalizeTransformedAge(to, from);

			to.setUniqueID(originalId);
			GameWorld.get().getCurrentWorldState().getYukkuriRegistry().remove(originalId);
			GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(originalId, to);
			to.setBaseYukkuriFileName(TransformationPolicy.resolveBaseYukkuriFileName(targetType));
			IniFileUtil.readYukkuriIniFile(to);
			if (TransformationPolicy.isSelectedYukkuri(from)) {
				org.simyukkuri.draw.MyPane.setSelectedYukkuri(to);
			}
			from.setRemoved(true);
		}
	}
}
