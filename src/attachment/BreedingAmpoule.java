package src.attachment;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import src.base.Attachment;
import src.base.Body;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.AttachProperty;
import src.enums.Direction;
import src.enums.Event;
import src.game.Dna;

/****************************************
 *  精子アンプル
 * 
 */
public class BreedingAmpoule extends Attachment {

	private static final long serialVersionUID = 1L;

	private static final String POS_KEY = "AccelAmpoule";
	/**画像の入れ物
	 * <br>[年齢][左右反転]*/
	private static BufferedImage[][] images;
	/**画像のサイズ*/
	private static int[] imgW, imgH;
	/**画像の描画原点の座標*/
	private static int[] pivX, pivY;
	/**継承元のenum AttachProperty の代入値*/
	private static final int[] property = {
			2, // 赤ゆ用画像サイズ 原画をこの値で割る
			2, // 子ゆ用画像サイズ
			1, // 成ゆ用画像サイズ
			1, // 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
			0, // アニメ速度
			0, // アニメループ回数
			1 // アニメ画像枚数
	};

	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][2];

		images[adult][0] = ModLoader.loadItemImage(loader, "ampoule" + File.separator + "sperm.png");

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		images[child][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.CHILD_SIZE.ordinal()],
				h / property[AttachProperty.CHILD_SIZE.ordinal()]);
		images[baby][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.BABY_SIZE.ordinal()],
				h / property[AttachProperty.BABY_SIZE.ordinal()]);

		images[adult][1] = ModLoader.flipImage(images[adult][0]);
		images[child][1] = ModLoader.flipImage(images[child][0]);
		images[baby][1] = ModLoader.flipImage(images[baby][0]);

		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	@Override
	protected Event update() {
		if (parent.isDead() || parent.isBurned() || parent.isCrushed()) {
			return Event.DONOTHING;
		}
		parent.setHungry(100);
		parent.addDamage(-100);
		Random rnd = new Random();
		int babyType;
		if (rnd.nextInt(３) == 0) {
			babyType = rnd.nextInt(12);
			switch (babyType) {
			case 0: // まりさ
			case 8:
				babyType = getMarisaType();
				break;
			case 1: // れいむ
			case 9:
				switch (rnd.nextInt(5)) {
				case 0:
				case 2:
					babyType = 1;//普通のれいむ
					break;
				case 1:
					babyType = 2001;//わされいむ
					break;
				case 4:
					babyType = 2007;//たりないれいむ
					break;
				case 3:
					babyType = 2005;//でいぶ
					break;
				default:
					babyType = 1;
				}
				break;
			case 3: // ありす
				babyType = 2;
				break;
			case 4: // みょん
				babyType = 5;
				break;
			case 5: // ちぇん
				babyType = 4;
				break;
			case 6: // たりないゆ
				babyType = 2000;
				break;
			case 7: // ゆるさなえ
				babyType = 1000;
				break;
			case 10: // ぱちゅりー
				babyType = 3;
				break;
			case 11: //希少種
				babyType = 1000 + rnd.nextInt(12);
				break;
			}
		} else {
			babyType = parent.getType();
			// 親がドスなら他のまりさが均等に出る
			if (babyType == 2006) {
				babyType = getMarisaType();
			}
			parent.getBabyTypes().add(new Dna(babyType, null, null, false));
			parent.setHasBaby(true);
		}
		return Event.DONOTHING;
	}

	/**
	 * まりさの子供は何のまりさかランダムで決定。
	 * @return まりさの子供タイプ
	 */
	private int getMarisaType() {
		switch (rnd.nextInt(5)) {
		case 0:
		case 3:
		case 4:
			return 0;//普通のまりさ
		case 1:
			return 2004;//こたつむり
		case 2:
			return 2002;//つむり
		default:
			return 0;
		}
	}

	@Override
	public BufferedImage getImage(Body b) {
		if (b.getDirection() == Direction.RIGHT) {
			return images[parent.getBodyAgeState().ordinal()][1];
		}
		return images[parent.getBodyAgeState().ordinal()][0];
	}

	@Override
	public void resetBoundary() {
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
				pivY[parent.getBodyAgeState().ordinal()],
				imgW[parent.getBodyAgeState().ordinal()],
				imgH[parent.getBodyAgeState().ordinal()]);
	}

	/**
	 * コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public BreedingAmpoule(Body body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
				pivY[parent.getBodyAgeState().ordinal()],
				imgW[parent.getBodyAgeState().ordinal()],
				imgH[parent.getBodyAgeState().ordinal()]);
		value = 1000;
		cost = 0;
	}

}
