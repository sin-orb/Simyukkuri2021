package src.item;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.enums.CriticalDamegeType;
import src.enums.EffectType;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.MessagePool;

/***************************************************
 * ホットプレート
 */
public class HotPlate extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle boundary = new Rectangle();

	private Body bindBody = null;
	private Effect smoke = null;
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) {
			if(bindBody != null) {
				FootBake f = null;
				f = bindBody.getFootBakeLevel();
				if(f == FootBake.CRITICAL) layer[0] = images[2];
				else if(f == FootBake.MIDIUM) layer[0] = images[1];
				else layer[0] = images[0];
			} else {
				layer[0] = images[0];
			}
		}
		else {
			layer[0] = images[3];
			if(bindBody!=null){
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setPullAndPush(false);
				bindBody.setDropShadow(true);
				bindBody = null;
			}
		}
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}
	/**境界線の取得*/
	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public boolean enableHitCheck() {
		if(bindBody != null) return false;
		return true;
	}

	@Override
	public int objHitProcess( Obj o ) {
		
		bindBody = (Body)o;
		if(bindBody.getCriticalDamegeType() == CriticalDamegeType.CUT) return 0;
		bindBody.clearActions();
		bindBody.setX(x);
		bindBody.setY(y);
		bindBody.setLockmove(true);
		if(smoke == null) {
			smoke = SimYukkuri.mypane.terrarium.addEffect(EffectType.BAKE, bindBody.getX(), bindBody.getY() + 1,
															-2, 0, 0, 0, false, -1, -1, false, false, false);
		}
		return 1;
	}

	@Override
	public void upDate() {
		if ( getAge() % 2400 == 0 ){
			Cash.addCash(-getCost());
		}
		if(bindBody != null) {
			bindBody.setDropShadow(false);
			if(grabbed) {
				bindBody.setX(x);
				bindBody.setY(y);
				if(smoke != null) {
					smoke.setX(x);
					smoke.setY(y);
				}
			}
			else if(bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setPullAndPush(false);
				bindBody.setDropShadow(true);
				bindBody = null;
			}
			else {
				if(!bindBody.isDead()) {
					if(bindBody.isSleeping()) bindBody.wakeup();
					bindBody.addFootBakePeriod(50);
					bindBody.addDamage(20);
					bindBody.addStress(20);
					if(bindBody.getFootBakeLevel() == FootBake.CRITICAL){
						bindBody.setPullAndPush(true);
					}
					if( bindBody.isNotNYD() ){
						bindBody.setHappiness(Happiness.VERY_SAD);
						bindBody.setForceFace(ImageCode.PAIN.ordinal());
					}
					if(SimYukkuri.RND.nextInt(10) == 0) {
						bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Burning), 40, true, true);
					}
				}
			}
		}
		else {
			if(smoke != null) {
				smoke.remove();
				smoke = null;
			}
		}
	}

	@Override
	public void removeListData(){
		if(bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setLockmove(false);
			bindBody = null;
		}
		if(smoke != null) {
			smoke.remove();
			smoke = null;
		}
		SimYukkuri.world.getCurrentMap().hotPlate.remove(this);
	}
	/**コンストラクタ*/
	public HotPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().hotPlate.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.HOTPLATE;

		interval = 5;
		value = 5000;
		cost = 100;
	}
}


