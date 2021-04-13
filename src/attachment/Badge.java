package src.attachment;


//import java.awt.Rectangle;
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
//import src.Attachment.AttachProperty;
import src.enums.Event;


/****************************************
 *  バッジ
 * 
 */
public class Badge extends Attachment {

	private static final long serialVersionUID = 1L;

	private static final String POS_KEY = "Badge";
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

	/**バッジランク定義*/
	public static enum BadgeRank {
		FAKE("fake.png"),
		BRONZE("bronze.png"),
		SILVER("silver.png"),
		GOLD("gold.png"),
		;
		public String fileName;
		BadgeRank(String fn) {
			fileName = fn;
		}
	}
	/**画像の入れ物
	 * <br>[親オブジェクト(ゆっくり)の年齢][ランク]*/
	private static BufferedImage[][] images = new BufferedImage[3][BadgeRank.values().length];
	/**ランクの入れ物*/
	private BadgeRank eBadgeRank;
	
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		int baby = AgeState.BABY.ordinal();
		int child = AgeState.CHILD.ordinal();
		int adult = AgeState.ADULT.ordinal();

		for(BadgeRank i :BadgeRank.values()) {
			if(i.fileName == null) continue;

			images[adult][i.ordinal()] = ModLoader.loadItemImage(loader, "badge" + File.separator + i.fileName);
			int w = images[adult][i.ordinal()].getWidth(io);
			int h = images[adult][i.ordinal()].getHeight(io);
			images[child][i.ordinal()] = ModLoader.scaleImage(images[adult][i.ordinal()], w / property[AttachProperty.CHILD_SIZE.ordinal()], h / property[AttachProperty.CHILD_SIZE.ordinal()]);
			images[baby][i.ordinal()] = ModLoader.scaleImage(images[adult][i.ordinal()], w / property[AttachProperty.BABY_SIZE.ordinal()], h / property[AttachProperty.BABY_SIZE.ordinal()]);
		}		

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
		return Event.DONOTHING;
	}
	
	@Override
	public BufferedImage getImage(Body b) {
		if(b.getDirection() == Direction.RIGHT) {
			//return null;
		}
		return images[parent.getBodyAgeState().ordinal()][eBadgeRank.ordinal()];
	}
	/**バッジランク取得*/
	public BadgeRank getBadgeRank()
	{
		return eBadgeRank;
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
	 * コンストラクタ.
	 * @param body 装着されるゆっくり
	 * @param ieBadgeRank バッジランク
	 */	
	public Badge(Body body, BadgeRank ieBadgeRank) {
		super(body);
		setAttachProperty(property, POS_KEY);
		eBadgeRank = ieBadgeRank;

		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
		value = 0;
		cost = 0;
	}

}