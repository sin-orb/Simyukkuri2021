package src.logic;

import src.base.Body;
import src.enums.BodyRank;
import src.enums.Happiness;
import src.item.Food.FoodType;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameView;

/**
 * 食事処理と反応をまとめたヘルパー。
 */
public final class FoodConsumptionPolicy {
	private static final int NEEDLE = 0;
	private static final int HAMMER = 0;
	private static final int HOLDMESSAGE = 0;

	private FoodConsumptionPolicy() {
	}

	public static void eatFood(Body b, FoodType foodType, int amount) {
		if (b.isDead()) {
			return;
		}
		b.setToTakeout(false);
		if (b.isOnlyAmaama()) {
			switch (foodType) {
			case BODY:
			case SWEETS1:
			case SWEETS_NORA1:
			case SWEETS_YASEI1:
			case SWEETS2:
			case SWEETS_NORA2:
			case SWEETS_YASEI2:
				break;
			default:
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SpitFood));
				b.setHappiness(Happiness.VERY_SAD);
				GameView.addVomit(b.getX() + 7 - GameRandom.nextInt(14),
						b.getY() + 7 - GameRandom.nextInt(14), 0, b, b.getShitType());
				return;
			}
		}
		switch (b.getTangType()) {
		case POOR:
			poorEating(b, foodType);
			break;
		case NORMAL:
			normalEating(b, foodType);
			break;
		case GOURMET:
			gourmetEating(b, foodType);
			break;
		default:
			break;
		}
		int eatAmount = Math.min(b.getEatAmount(), amount);
		b.eatFood(eatAmount);
		b.checkTang();
	}

	static int[][] lovePointTable = {
			// バカ舌,	普通,	肥えてる
			{ -50, -100, -500 }, //	SHIT　うんうん
			{ -1, -30, -50 }, //	BITTER
			{ 50, 10, 0 }, //	LEMONPOP
			{ -200, -200, -400 }, //	HOTPOINT
			{ 0, 0, 0 }, //	VIYUGRA
			{ 100, 10, 0 }, //	BODY
			{ 0, 0, 0 }, //	STALK ちぎれた茎
			{ 500, 100, 10 }, //	SWEETS1
			{ 1000, 500, 50 }, //	SWEETS2
			{ 10, -50, -200 }, //	WASTE　生ゴミ
			{ 0, 0, 0 }, //	VOMIT　吐餡
			{ 30, 10, 0 } //	その他
	};

	// バカ舌状態でのリアクション
	private static final void poorEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:// うんうん
			b.setHappiness(Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(100);
			b.addTang(-10);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[0][0]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.VERY_HAPPY);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.addTang(200);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[1][0]);
			} else {
				b.strike(NEEDLE * 2);
				b.setHappiness(Happiness.SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(250);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(lovePointTable[1][0]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-500);
			b.addTang(50);
			// なつき度設定
			b.addLovePlayer(lovePointTable[2][0]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (b.isLikeHotFood()) {
				b.setHappiness(Happiness.VERY_HAPPY);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-500);
				b.addTang(200);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[3][0]);
			} else {
				b.strike(HAMMER >> 1);
				b.setHappiness(Happiness.SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(500);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(lovePointTable[3][0]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && GameRandom.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(lovePointTable[4][0]);
			break;
		case BODY:// 生け餌
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.addStress(-500);
			b.addTang(50);
			b.addMemories(5);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[5][0]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-500);
			b.addDamage(-500);
			b.addMemories(20);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[6][0]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(200);
			b.addMemories(30);
			// なつき度設定
			b.addLovePlayer(lovePointTable[7][0]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(50);
			// なつき度設定
			b.addLovePlayer(lovePointTable[8][0]);
			break;
		case WASTE:// 生ゴミ
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(-100);
			b.addTang(-30);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[9][0]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(100);
			// なつき度設定
			b.addLovePlayer(lovePointTable[10][0]);
			break;
		default:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.addStress(-300);
			b.addTang(100);
			b.addMemories(1);
			break;
		}
	}

	// 普通状態でのリアクション
	private static final void normalEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:
			b.setHappiness(Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(200);
			b.addTang(-10);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[0][1]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.HAPPY);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[1][1]);
			} else {
				b.strike(NEEDLE * 4);
				b.setHappiness(Happiness.SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(300);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(lovePointTable[1][1]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-200);
			b.addTang(20);
			// なつき度設定
			b.addLovePlayer(lovePointTable[2][1]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (!b.isLikeHotFood()) {
				b.strike(HAMMER);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(800);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(lovePointTable[3][1]);
			} else {
				b.setHappiness(Happiness.HAPPY);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-200);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[3][1]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && GameRandom.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(lovePointTable[4][1]);
			break;
		case BODY:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addMemories(1);
			if (!b.isPredatorType()) {
				b.addTang(10);
			}
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[5][1]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-200);
			b.addDamage(-500);
			b.addMemories(20);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[6][1]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(100);
			b.addMemories(30);
			// なつき度設定
			b.addLovePlayer(lovePointTable[7][1]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(50);
			// なつき度設定
			b.addLovePlayer(lovePointTable[8][1]);
			break;
		case WASTE:
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBadtasting));
			b.setStrike(true);
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(100);
			b.addTang(-30);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[9][1]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			if (!b.isPredatorType()) {
				b.addTang(50);
			}
			// なつき度設定
			b.addLovePlayer(lovePointTable[10][1]);
			break;
		default:
			b.setHappiness(Happiness.HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addMemories(1);
			break;
		}
	}

	// 肥え状態でのリアクション
	private static final void gourmetEating(Body b, FoodType type) {
		switch (type) {
		case SHIT:
			b.setHappiness(Happiness.VERY_SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingShit));
			b.setEatingShit(true);
			b.addStress(500);
			b.addTang(-10);
			// 飼いゆの場合のみ。野良ならうんうん奴隷の可能性があるので
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[0][2]);
			}
			break;
		case BITTER:
		case BITTER_NORA:
		case BITTER_YASEI:
			if (b.isLikeBitterFood()) {
				b.setHappiness(Happiness.AVERAGE);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[1][2]);
			} else {
				b.strike(NEEDLE * 4);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				if (b.getDiarrhea())
					b.rapidShit();
				b.addStress(300);
				b.addMemories(-5);
				// なつき度設定
				b.addLovePlayer(lovePointTable[1][2]);
			}
			break;
		case LEMONPOP:
		case LEMONPOP_NORA:
		case LEMONPOP_YASEI:
			b.setHappiness(b.isRude() ? Happiness.VERY_SAD : Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.forceToSleep();
			b.addStress(-800);
			b.addTang(-100);
			// なつき度設定
			b.addLovePlayer(lovePointTable[2][2]);
			break;
		case HOT:
		case HOT_NORA:
		case HOT_YASEI:
			if (!b.isLikeHotFood()) {
				b.strike(HAMMER);
				b.setHappiness(Happiness.VERY_SAD);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingBitter));
				b.setStrike(true);
				b.addStress(1000);
				b.addMemories(-10);
				// なつき度設定
				b.addLovePlayer(lovePointTable[3][2]);
			} else {
				b.setHappiness(Happiness.AVERAGE);
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
				b.addStress(-100);
				b.addTang(20);
				// なつき度設定
				b.addLovePlayer(-1 * lovePointTable[3][2]);
			}
			break;
		case VIYUGRA:
		case VIYUGRA_NORA:
		case VIYUGRA_YASEI:
			b.setHappiness(b.isRude() ? Happiness.VERY_SAD : Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.rapidExcitingDiscipline();
			if (!b.isSuperRaper() && GameRandom.nextInt(10) == 0) {
				b.setSuperRaper(true);
				b.setRaper(true);
			}
			b.addLovePlayer(lovePointTable[4][2]);
			break;
		case BODY:
			b.setHappiness(Happiness.AVERAGE);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-100);
			b.addMemories(1);
			if (!b.isPredatorType()) {
				b.addTang(10);
			}
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[5][2]);
			}
			break;
		case STALK:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addStress(-500);
			b.addDamage(-500);
			b.addMemories(30);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[6][2]);
			}
			break;
		case SWEETS1:
		case SWEETS_NORA1:
		case SWEETS_YASEI1:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(500);
			b.addMemories(20);
			// なつき度設定
			b.addLovePlayer(lovePointTable[7][2]);
			break;
		case SWEETS2:
		case SWEETS_NORA2:
		case SWEETS_YASEI2:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			b.addTang(1000);
			b.addMemories(40);
			// なつき度設定
			b.addLovePlayer(lovePointTable[8][2]);
			break;
		case WASTE:
		case WASTE_NORA:
		case WASTE_YASEI:
			b.setHappiness(Happiness.VERY_SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Eating));
			b.addDirtyPeriod(Body.TICK * 4);
			b.addStress(0);
			b.addTang(-30);
			b.addMemories(-1);
			// 飼いゆの場合のみ
			if (b.getBodyRank() == BodyRank.KAIYU) {
				// なつき度設定
				b.addLovePlayer(lovePointTable[9][2]);
			}
			break;
		case VOMIT:
			b.setHappiness(Happiness.VERY_HAPPY);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.setStress(0);
			if (!b.isPredatorType()) {
				b.addTang(100);
			}
			// なつき度設定
			b.addLovePlayer(lovePointTable[10][2]);
			break;
		default:
			b.setHappiness(b.isRude() ? Happiness.VERY_SAD : Happiness.SAD);
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), HOLDMESSAGE, true, true);
			b.addStress(-300);
			b.addTang(100);
			b.addMemories(1);
			break;
		}
	}
}
