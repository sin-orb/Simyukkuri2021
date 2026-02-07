package src.attachment;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Attachment;
import src.base.Body;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.AttachProperty;
import src.enums.Direction;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.UnbirthBabyState;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/****************************************
 *  針
 * 
 */
public class Needle extends Attachment {

	private static final long serialVersionUID = 4370778209880937836L;
	/**識別キー*/
	private static final String POS_KEY = "Needle", POS_KEY_IN_ANAL = "Needle_In_Anal";
	/**画像の入れ物
	 * <br>[年齢][左右反転]*/
	private static BufferedImage[][] images;
	/**画像のサイズ*/
	private static int[] imgW, imgH;
	/**画像の描画原点の座標*/
	private static int[] pivX, pivY;
	/**継承元のenum AttachProperty の代入値*/
	private static final int[] property = {
			3, // 赤ゆ用画像サイズ 原画をこの値で割る
			2, // 子ゆ用画像サイズ
			1, // 成ゆ用画像サイズ
			1, // 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
			0, // アニメ速度
			0, // アニメループ回数
			1 // アニメ画像枚数
	};

	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {

		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][2];

		images[adult][0] = ModLoader.loadItemImage(loader, "needle/needle.png");

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
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null) return Event.DONOTHING;
		// 生きてたらセリフとダメージ加算
		if (!pa.isDead()) {
			pa.clearActions();
			if (pa.isSleeping())
				pa.wakeup();
			if (pa.isFixBack()) {
				pa.setDirection(Direction.LEFT);
			}
			if (pa.isNotNYD()) {
				if (pa.isFixBack()) {
					if (!pa.isTalking()) {
						pa.setMessage(MessagePool.getMessage(pa, MessagePool.Action.NeedleScreamInAnal), 20,
								true, true);
					}
				} else {
					if (!pa.isTalking()) {
						pa.setMessage(MessagePool.getMessage(pa, MessagePool.Action.NeedleScream), 20, true,
								true);
					}
				}
				pa.setHappiness(Happiness.VERY_SAD);
				pa.setForceFace(ImageCode.PAIN.ordinal());
			}
			if (SimYukkuri.RND.nextInt(50) == 0) {
				pa.stayPurupuru(20);
			}

			pa.addDamage(TICK * 1);
			pa.addStress(15);

			// 実ゆの場合、親が反応する
			if (SimYukkuri.RND.nextInt(50) == 0) {
				pa.checkReactionStalkMother(UnbirthBabyState.ATTAKED);
			}
		} else {
			// 死んでいたら背面固定解除
			pa.setFixBack(false);
		}
		return Event.DONOTHING;
	}

	@Override
	public BufferedImage getImage(Body b) {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null) return null;
		if (b.getDirection() == Direction.RIGHT) {
			return images[pa.getBodyAgeState().ordinal()][1];
		}
		return images[pa.getBodyAgeState().ordinal()][0];
	}

	@Override
	public void resetBoundary() {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getBodyAgeState().ordinal()],
				pivY[pa.getBodyAgeState().ordinal()],
				imgW[pa.getBodyAgeState().ordinal()],
				imgH[pa.getBodyAgeState().ordinal()]);
		}
	}

	/**
	 * コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public Needle(Body body) {
		super(body);
		if (body.isFurifuri() || body.isShitting() || body.isBirth()) {
			body.setFixBack(true);
		}
		if (body.isFixBack()) {
			setAttachProperty(property, POS_KEY_IN_ANAL);
		} else {
			setAttachProperty(property, POS_KEY);
		}
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa != null) {
			setBoundary(pivX[pa.getBodyAgeState().ordinal()],
				pivY[pa.getBodyAgeState().ordinal()],
				imgW[pa.getBodyAgeState().ordinal()],
				imgH[pa.getBodyAgeState().ordinal()]);
		}
		value = 0;
		cost = 0;
		//処理インターバルの変更
		processInterval = 1;
	}
	
	public Needle() {
		
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("item_needle");
	}

	// テスト用静的アクセサ
	public static BufferedImage[][] getImages() {
		return images;
	}

	public static void setImages(BufferedImage[][] images) {
		Needle.images = images;
	}

	public static void setImgW(int[] imgW) {
		Needle.imgW = imgW;
	}

	public static void setImgH(int[] imgH) {
		Needle.imgH = imgH;
	}

	public static void setPivX(int[] pivX) {
		Needle.pivX = pivX;
	}

	public static void setPivY(int[] pivY) {
		Needle.pivY = pivY;
	}

	public static String getPosKey() {
		return POS_KEY;
	}

	public static String getPosKeyInAnal() {
		return POS_KEY_IN_ANAL;
	}

	public static int[] getProperty() {
		return property;
	}
}