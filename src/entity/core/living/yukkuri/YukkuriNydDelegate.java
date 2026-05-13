package src.entity.core.living.yukkuri;

import src.entity.core.attachment.impl.ANYDAmpoule;
import src.enums.CoreAnkoState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.system.MessagePool;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * 非ゆっくり症関連をまとめる委譲クラス.
 */
public final class YukkuriNydDelegate {
	private final Yukkuri body;

	/**
	 * 非ゆっくり症を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriNydDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * 非ゆっくり症チェックを行う。
	 * ・基礎
	 * 足りないゆはほぼ非ゆっくり症にならない
	 * うんうん奴隷は常に甘いもの（うんうん）を食べているのでほぼ非ゆっくり症にならない
	 * 善良＜ゲスで耐性高い
	 * 赤ゆ、実ゆ＜子供＜大人で耐性高い
	 * バッヂ級＜餡子脳で耐性高い
	 * レイパーは非ゆっくり症にならない
	 * ・環境
	 * 完全空腹の場合に耐性Down
	 * 足焼きの度合いに応じて耐性Down
	 * カビが生えると耐性Down
	 * お飾りがないと耐性Down
	 * ぴこぴこがないと耐性Down
	 * ぺにぺにがないと耐性超Down
	 * 汚れていると耐性Down
	 * 固定されていると耐性Down
	 * 盲目だと耐性Down
	 * 口がふさがれてると耐性Down
	 * ケガしてると耐性Down
	 * 生きている子供の数だけ耐性Up
	 * 死んでいる子供の数だけ耐性Down
	 * ・つらい思い出
	 * 他ゆの死体を見ると耐性Down
	 * 他ゆ食いで吐餡してたら耐性Down
	 * 生ごみ、辛い餌、苦い餌を食べると耐性Down
	 * 出産失敗で耐性超Down
	 * ・いい思い出
	 * すっきりすると耐性Up
	 * 出産時に応援する、応援されると耐性Up
	 * 出産成功で耐性Up
	 * 茎を食べると耐性超Up
	 * あまあまを食べると耐性超Up
	 * すりすりされると耐性Up
	 * ぺろぺろされると耐性Up
	 * うんうん体操に参加すると耐性Up
	 * すぃーにのると耐性Up
	 *
	 * @return その後の処理をキャンセルするかどうか
	 */
	public boolean checkNonYukkuriDisease() {
		if (GameEnvironment.isAntiNonYukkuriDiseaseSteam() || body.getAttachmentSize(ANYDAmpoule.class) != 0) {
			body.setCoreAnkoState(CoreAnkoState.DEFAULT);
			return false;
		}

		int stressLimit = body.getStressLimitBase()[body.getBodyAgeState().ordinal()];
		int tolerance = body.checkNonYukkuriDiseaseTolerance();
		if (stressLimit * tolerance / 100 < body.getStress()) {
			if (body.isNotNYD()) {
				body.setCalm();
				body.setCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);
				body.setNonYukkuriDiseasePeriod(0);
				body.setSpeed(body.getSpeed() / 2);
			}
			if (stressLimit * tolerance / 100 * 2 < body.getStress()) {
				if (body.getCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear) {
					body.setCalm();
					body.setCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
					body.setNonYukkuriDiseasePeriod(0);
				}
			}
		} else {
			if (body.isNYD()) {
				body.setSpeed(body.getSpeed() * 2);
			}
			body.setCoreAnkoState(CoreAnkoState.DEFAULT);
		}

		if (body.isNotNYD()) {
			body.setNonYukkuriDiseasePeriod(0);
			return false;
		}

		if (body.isUnBirth()) {
			return true;
		}
		int randomThreshold = 40;
		body.wakeup();
		body.setBirth(false);

		if (body.isNYD() && body.isSleeping()) {
			body.wakeup();
		}
		if (body.getCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear
				&& GameRandom.nextInt(randomThreshold) == 0) {
			switch (body.getNonYukkuriDiseasePeriod()) {
				case 0:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(1);
					} else {
						body.setNonYukkuriDiseasePeriod(3);
					}
					if (!body.isFixBack()) {
						body.clearActions();
						if (body.getNonYukkuriDiseasePeriod() == 1) {
							body.setNYDForceFace(ImageCode.NYD_FRONT.ordinal());
						} else {
							body.setNYDForceFace(ImageCode.NYD_DOWN.ordinal());
							body.setNonYukkuriDiseasePeriod(3);
						}
					}
					break;
				case 1:
					body.setNonYukkuriDiseasePeriod(2);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_FRONT_CRY1.ordinal());
					}
					break;
				case 2:
					body.setNonYukkuriDiseasePeriod(0);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_FRONT_CRY2.ordinal());
					}
					body.stayPurupuru(20);
					break;
				case 3:
					body.setNonYukkuriDiseasePeriod(4);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_DOWN_CRY1.ordinal());
					}
					break;
				case 4:
					body.setNonYukkuriDiseasePeriod(0);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_DOWN_CRY2.ordinal());
					}
					body.stayPurupuru(20);
					break;
				default:
					break;
			}
			body.addStress(100);
			body.addMemories(-1);
			body.setHappiness(Happiness.VERY_SAD);
			body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.NonYukkuriDiseaseNear), false);
		}
		randomThreshold = 20;
		if (body.getCoreAnkoState() == CoreAnkoState.NonYukkuriDisease
				&& GameRandom.nextInt(randomThreshold) == 0) {
			switch (body.getNonYukkuriDiseasePeriod()) {
				case 0:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(1);
						if (!body.isFixBack()) {
							body.clearActions();
							body.setNYDForceFace(ImageCode.NYD_UP.ordinal());
						}
					} else {
						body.setNonYukkuriDiseasePeriod(4);
						if (!body.isFixBack()) {
							body.clearActions();
							body.setNYDForceFace(ImageCode.NYD_FRONT_WIDE.ordinal());
						}
					}
					break;
				case 1:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(2);
					}
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_UP.ordinal());
					}
					break;
				case 2:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(3);
					}
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_UP_CRY1.ordinal());
					}
					break;
				case 3:
					body.setNonYukkuriDiseasePeriod(1);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_UP_CRY2.ordinal());
					}
					body.stayPurupuru(20);
					break;
				case 4:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(5);
					}
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_FRONT_WIDE.ordinal());
					}
					break;
				case 5:
					if (GameRandom.nextBoolean()) {
						body.setNonYukkuriDiseasePeriod(6);
					}
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_FRONT_WIDE_CRY1.ordinal());
					}
					break;
				case 6:
					body.setNonYukkuriDiseasePeriod(4);
					if (!body.isFixBack()) {
						body.clearActions();
						body.setNYDForceFace(ImageCode.NYD_FRONT_WIDE_CRY2.ordinal());
					}
					body.stayPurupuru(20);
					break;
				default:
					break;
			}
			body.addStress(300);
			body.addMemories(-5);
			body.setHappiness(Happiness.VERY_SAD);
			body.setNYDMessage(GameMessages.getMessage(body, MessagePool.Action.NonYukkuriDisease), false);
			if (GameRandom.nextInt(randomThreshold) == 0) {
				body.setNonYukkuriDiseasePeriod(0);
			}
		}
		return true;
	}
}
