package src.engine.transform;

import java.util.function.BooleanSupplier;

import src.SimYukkuri;
import src.draw.BodyFactory;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.YukkuriType;
import src.util.GameView;
import src.util.GameWorld;
import src.util.IniFileUtil;

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
					&& !GameWorld.get().getCurrentMap().makeOrKillDos(true)) {
				return;
			}

			int originalId = from.getUniqueID();
			Yukkuri to = BodyFactory.create(from.getX(), from.getY(), from.getZ(), targetType, null,
					from.getBodyAgeState(), null, null, false, GameView::loadBodyImage, dosMaker);
			TransformationBodyCopier.copy(to, from);
			TransformationPolicy.normalizeTransformedAge(to, from);

			to.setUniqueID(originalId);
			GameWorld.get().getCurrentMap().getBody().remove(originalId);
			GameWorld.get().getCurrentMap().getBody().put(originalId, to);
			to.setBaseBodyFileName(TransformationPolicy.resolveBaseBodyFileName(targetType));
			IniFileUtil.readYukkuriIniFile(to);
			if (TransformationPolicy.isSelectedBody(from)) {
				src.draw.MyPane.setSelectBody(to);
			}
			from.setRemoved(true);
		}
	}
}
