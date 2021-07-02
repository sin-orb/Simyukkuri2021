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
import src.util.YukkuriUtil;


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
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null) return null; 
		return images[pa.getBodyAgeState().ordinal()][animeFrame];
	}

	@Override
	protected Event update() {
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null) return Event.DONOTHING;
		// 生きてたらセリフとダメージ加算
		if(!pa.isDead()) {
			pa.clearActions();
			if( pa.isNotNYD() ){
				if(!pa.isTalking()) {
					pa.setMessage(MessagePool.getMessage(pa, MessagePool.Action.Burning), 20, true, true);
				}
			}
			pa.addDamage(TICK * 90);
			pa.addStress(50);
			// 背面固定で針が刺さってないなら尻を振る
			if( pa.isFixBack()  && !pa.isNeedled()){
				if( SimYukkuri.RND.nextInt(10) == 0){
					pa.setFurifuri(true);
				}
			}
			else{
				if(pa.isLockmove() ){
					if( SimYukkuri.RND.nextInt(3) == 0){
						pa.setNobinobi(true);
					}
				}
			}

		}
		// 燃焼時間
		burnPeriod += TICK*90 ;
		pa.addBodyBakePeriod(90);
		// お飾り消失
		if(burnPeriod > (pa.getDamageLimit() / 3) && pa.hasOkazari()) {
			pa.takeOkazari(false);
		}
		else if(burnPeriod > (pa.getDamageLimit()*2 / 3) && pa.geteHairState()!=HairState.BALDHEAD){
			pa.pickHair();

		}
		else if(burnPeriod*90 > pa.getDamageLimit()) {
			if (pa.isDead()) {
				pa.setBurned(true);
			}
		}
		if(pa.isDead() && pa.isBurned()) {
			return Event.REMOVED;
		}

		// 実ゆの場合、親が反応する
		if(SimYukkuri.RND.nextInt(3) == 0){
			Body bodyMother = YukkuriUtil.getBodyInstance(pa.getBindStalkMotherCanNotice());
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
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa == null) return;
		setBoundary(pivX[pa.getBodyAgeState().ordinal()],
					pivY[pa.getBodyAgeState().ordinal()],
					imgW[pa.getBodyAgeState().ordinal()],
					imgH[pa.getBodyAgeState().ordinal()]);
	}

	/**
	 * コンストラクタ
	 * @param body 装着されるゆっくり
	 */
	public Fire(Body body) {
		super(body);
		setAttachProperty(property, POS_KEY);
		Body pa = YukkuriUtil.getBodyInstance(parent);
		if (pa != null) {
		setBoundary(pivX[pa.getBodyAgeState().ordinal()],
					pivY[pa.getBodyAgeState().ordinal()],
					imgW[pa.getBodyAgeState().ordinal()],
					imgH[pa.getBodyAgeState().ordinal()]);
		}
		burnPeriod = 0;
		value = 0;
		cost = 0;

		//処理インターバルの変更
		processInterval = 1;
	}
	
	public Fire() {
		
	}
	
	public int getBurnPeriod() {
		return burnPeriod;
	}

	public void setBurnPeriod(int burnPeriod) {
		this.burnPeriod = burnPeriod;
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("item_fire");
	}
}