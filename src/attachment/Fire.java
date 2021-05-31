package src.attachment;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Attachment;
import src.base.Body;
import src.draw.ModLoader;
import src.enums.AgeState;
import src.enums.AttachProperty;
import src.enums.Event;
import src.enums.HairState;
import src.enums.Happiness;
import src.system.MessagePool;
import src.system.ResourceUtil;


/****************************************
 * 炎
 *
 */
public class Fire extends Attachment {

	private static final long serialVersionUID = 1L;

	private static final String POS_KEY = "Fire";
	/**画像の入れ物
	 * <br>[年齢][左右反転][アニメパターン]*/
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
		0,		// 親オブジェクトの位置基準 0:顔、お飾り向けの元サイズ 1:妊娠などの膨らみも含むサイズ
		1,		// アニメ速度
		0,		// アニメループ回数
		4		// アニメ画像枚数
	};

	/**燃焼時間*/
	private int burnPeriod;
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
		images = new BufferedImage[3][4];

		// 炎
		for(int i = 0; i < 4; i++) {
			images[adult][i] = ModLoader.loadItemImage(loader, "effect" + File.separator + "fire_" + i + ".png");
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
	public BufferedImage getImage(Body b) {
		return images[parent.getBodyAgeState().ordinal()][animeFrame];
	}

	@Override
	protected Event update() {
		// 生きてたらセリフとダメージ加算
		if(!parent.isDead()) {
			parent.clearActions();
			if( parent.isNotNYD() ){
				if(!parent.isTalking()) {
					parent.setMessage(MessagePool.getMessage(parent, MessagePool.Action.Burning), 20, true, true);
				}
			}
			parent.addDamage(TICK * 90);
			parent.addStress(50);
			// 背面固定で針が刺さってないなら尻を振る
			if( parent.isFixBack()  && !parent.isNeedled()){
				if( SimYukkuri.RND.nextInt(10) == 0){
					parent.setFurifuri(true);
				}
			}
			else{
				if(parent.isLockmove() ){
					if( SimYukkuri.RND.nextInt(3) == 0){
						parent.setNobinobi(true);
					}
				}
			}

		}
		// 燃焼時間
		burnPeriod += TICK*90 ;
		parent.addBodyBakePeriod(90);
		// お飾り消失
		if(burnPeriod > (parent.getDamageLimit() / 3) && parent.hasOkazari()) {
			parent.takeOkazari(false);
		}
		else if(burnPeriod > (parent.getDamageLimit()*2 / 3) && parent.geteHairState()!=HairState.BALDHEAD){
			parent.pickHair();

		}
		else if(burnPeriod*90 > parent.getDamageLimit()) {
			if (parent.isDead()) {
				parent.setBurned(true);
			}
		}
		if(parent.isDead() && parent.isBurned()) {
			return Event.REMOVED;
		}

		// 実ゆの場合、親が反応する
		if(SimYukkuri.RND.nextInt(3) == 0){
			Body bodyMother = parent.getBindStalkMotherCanNotice();
			if ( bodyMother != null ) {
				if( bodyMother.isNotNYD() ){
					bodyMother.setHappiness(Happiness.VERY_SAD);
					bodyMother.setMessage(MessagePool.getMessage(bodyMother, MessagePool.Action.AbuseBaby));
					bodyMother.addStress(15);
				}
			}
		}
		return Event.DONOTHING;
	}

	@Override
	public void resetBoundary(){
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
	}

	/**
	 * コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public Fire(Body body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		setBoundary(pivX[parent.getBodyAgeState().ordinal()],
					pivY[parent.getBodyAgeState().ordinal()],
					imgW[parent.getBodyAgeState().ordinal()],
					imgH[parent.getBodyAgeState().ordinal()]);
		burnPeriod = 0;
		value = 0;
		cost = 0;

		//処理インターバルの変更
		processInterval = 1;
	}
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("item_fire");
	}
}