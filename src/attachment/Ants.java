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
import src.enums.Event;
import src.system.ResourceUtil;


/****************************************
 *  アリ
 *
 */
public class Ants extends Attachment {

	private static final long serialVersionUID = 1L;
	/**識別キー*/
	private static final String POS_KEY = "Ants";
	/**画像の入れ物
	 * <br>[年齢][進行度]*/
	private static BufferedImage[][] images;
	/**画像のサイズ*/
	private static int[] imgW,imgH;
	/**画像の描画原点の座標*/
	private static int[] pivX,pivY;
	/**継承元のenum AttachProperty の代入値*/
	private static final int[] property = {
		4,		// 赤ゆ用画像サイズ 原画をこの値で割る
		2,		// 子ゆ用画像サイズ
		1,		// 成ゆ用画像サイズ
		1,		// 親オブジェクトの位置基準 	0:顔とお飾り向けの元サイズ 		1:妊娠などの膨らみも含むサイズ
		0,		// アニメ速度
		0,		// アニメループ回数
		1		// アニメ画像枚数
	};

	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();
		images = new BufferedImage[3][3];

		for(int i = 0; i < 3; i++) {
			images[adult][i] = ModLoader.loadItemImage(loader, "animal" + File.separator + "Ants_" + i + ".png");
		}
		int w = images[adult][0].getWidth(io);
		int h = images[adult][0].getHeight(io);
		for(int i = 0; i < images[adult].length; i++) {
			images[child][i] = ModLoader.scaleImage(images[adult][i], w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()]);
			images[baby][i] = ModLoader.scaleImage(images[adult][i], w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()]);
		}
		imgW = new int[3];
		imgH = new int[3];
		pivX = new int[3];
		pivY = new int[3];
		for (int i = 0; i < 3; i++) {
			if(images[i][0] == null) continue;
			imgW[i] = images[i][0].getWidth(io);
			imgH[i] = images[i][0].getHeight(io);
			pivX[i] = imgW[i] >> 1;
			pivY[i] = imgH[i] - 1;
		}
	}

	@Override
	protected Event update() {
		parent.beEaten((parent.getNumOfAnts()/3), 0, false);
		return Event.DONOTHING;
	}

	@Override
	public BufferedImage getImage(Body b) {
		int ants = b.getNumOfAnts();
		if(ants>= b.getDamageLimit()*2/3) {
			return images[b.getBodyAgeState().ordinal()][2];
		}
		else if(ants>= b.getDamageLimit()/3) {
			return images[b.getBodyAgeState().ordinal()][1];
		}
		return images[b.getBodyAgeState().ordinal()][0];
	}

	@Override
	public void resetBoundary(){
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
	}

	/**コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public Ants(Body body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
		parent.setNumOfAnts(50);
		value = 0;
		cost = 0;
		
		//処理インターヴァルの変更
		processInterval = 100;
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("item_ants");
	}
}



