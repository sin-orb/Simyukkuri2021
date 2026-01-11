package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
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

	private static final long serialVersionUID = -7407652564177670504L;
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private Body bindBody = null;
	private Effect smoke = null;
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
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
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
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
		bindBody.setCalcX(x);
		bindBody.setCalcY(y);
		bindBody.setLockmove(true);
		if(smoke == null) {
			smoke = SimYukkuri.mypane.getTerrarium().addEffect(EffectType.BAKE, bindBody.getX(), bindBody.getY() + 1,
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
				bindBody.setCalcX(x);
				bindBody.setCalcY(y);
				if(smoke != null) {
					smoke.setCalcX(x);
					smoke.setCalcY(y);
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
		SimYukkuri.world.getCurrentMap().getHotPlate().remove(objId);
	}
	/**コンストラクタ*/
	public HotPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().getHotPlate().put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.HOTPLATE;

		interval = 5;
		value = 5000;
		cost = 100;
	}
	public HotPlate() {
		
	}

	public Body getBindBody() {
		return bindBody;
	}

	public void setBindBody(Body bindBody) {
		this.bindBody = bindBody;
	}

	public Effect getSmoke() {
		return smoke;
	}

	public void setSmoke(Effect smoke) {
		this.smoke = smoke;
	}
	
}



