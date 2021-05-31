package src.attachment;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.base.Attachment;
import src.base.Body;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.AttachProperty;
import src.enums.Direction;
import src.enums.Event;
import src.system.ResourceUtil;


/****************************************
 *  成長抑制アンプル
 * 
 */
public class StopAmpoule extends Attachment {

	private static final long serialVersionUID = 1L;

	/**識別キー*/
	private static final String POS_KEY = "StopAmpoule";
	/**画像の入れ物
	 * <br>[年齢][左右反転]*/
	private static BufferedImage[][] images;
	/**画像のサイズ*/
	private static int[] imgW,imgH;
	/**画像の描画原点の座標*/
	private static int[] pivX,pivY;
	/**継承元のenum AttachProperty の代入値*/
	private static final int[] property = {
		2,		// 赤ゆ用画像サイズ 原画をこの値で割る
		2,		// 子ゆ用画像サイズ
		1,		// 成ゆ用画像サイズ
		1,		// 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
		0,		// アニメ速度
		0,		// アニメループ回数
		1		// アニメ画像枚数
	};
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][2];
		
		images[adult][0] = ModLoader.loadItemImage(loader, "ampoule" + File.separator + "body_stop.png");

		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		images[child][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()]);
		images[baby][0] = ModLoader.scaleImage(images[adult][0], w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()]);
			
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
		if(!parent.isAdult()) {
			parent.addAge(-TICK * 100);
		}
		return Event.DONOTHING;
	}
	@Override
	public BufferedImage getImage(Body b) {
		if(b.getDirection() == Direction.RIGHT) {
			return images[parent.getBodyAgeState().ordinal()][1];
		}
		return images[parent.getBodyAgeState().ordinal()][0];
	}
	@Override
	public void resetBoundary()
	{
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
	}
	/**
	 * コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public StopAmpoule(Body body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
		value = 1000;
		cost = 0;
		//処理インターバルの変更
		processInterval =100;
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("item_stop");
	}
}
