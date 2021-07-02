package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.CriticalDamegeType;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.MessagePool;
import src.util.YukkuriUtil;

/***************************************************
 * ミキサー
 */
public class Mixer extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private int bind = -1;
	private Effect mix = null;
	private int counter = 0;		// ゆっくりが乗ってからの経過時間
	private int amount = 0;			// 保有原料
	private int sweet = 0;			// 糖度
	private boolean sick = false;	// カビ混入
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for(int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "mixer" + File.separator + "mixer_" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "mixer" + File.separator + "mixer_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}


	@Override
	public int getImageLayer(BufferedImage[] layer) {
		Body bindBody = YukkuriUtil.getBodyInstance(bind);
		if(enabled) {
			if(bindBody != null) {
				if(counter > 60) {
					if(bindBody.getBodyAmount() < (bindBody.getDamageLimit() >> 1)) layer[0] = images[2];
					else layer[0] = images[1];
				} else {
					layer[0] = images[0];
				}
			} else {
				layer[0] = images[0];
			}
		} else {
			layer[0] = images[3];
		}
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public boolean enableHitCheck() {
		Body bindBody = YukkuriUtil.getBodyInstance(bind);
		if(bindBody != null) return false;
		return true;
	}

	@Override
	public int objHitProcess( Obj o ) {
		if(!enabled)return 0;
		Body bindBody = (Body)o;
		bindBody.clearActions();
		bindBody.setX(x);
		bindBody.setY(y);
		bindBody.setLockmove(true);
		bind = bindBody.getUniqueID();
		counter = 0;

		return 1;
	}

	@Override
	public void upDate() {
		if ( getAge() % 2400 == 0 ) {
			Cash.addCash(-getCost());
		}
		Body bindBody = YukkuriUtil.getBodyInstance(bind);
		if(bindBody != null && enabled) {
			bindBody.setDropShadow(false);
			if(grabbed) {
				bindBody.setX(x);
				bindBody.setY(y);
				if(mix != null) {
					mix.setX(x);
					mix.setY(y);
				}
				bind = bindBody.getUniqueID();
			} else if(bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				if(counter > 60) bindBody.setCriticalDamegeType(CriticalDamegeType.CUT);
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setDropShadow(true);
				bind = -1;
				return;
			}
			counter++;
			// ミキサー駆動開始
			if(counter > 60) {
				if(mix == null) {
					mix = SimYukkuri.mypane.terrarium.addEffect(EffectType.MIX, bindBody.getX(), bindBody.getY() + 1,
																	-2, 0, 0, 0, false, -1, -1, false, false, false);
				}
				if(!bindBody.isDead()) {
					if(bindBody.isSleeping()) bindBody.wakeup();
					bindBody.addDamage(100);
					bindBody.addStress(100);
						bindBody.setHappiness(Happiness.VERY_SAD);
						bindBody.setForceFace(ImageCode.PAIN.ordinal());
						if(SimYukkuri.RND.nextInt(10) == 0) {
							if(bindBody.getCriticalDamegeType() == CriticalDamegeType.CUT)bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Scream2), true);
							else bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Scream), true);
						}
					}
				// 材料採取
				amount += 100;
				sweet += bindBody.getStress();
				if(bindBody.isSick()) sick = true;
				if(bindBody.addAmount(-100)) {
					bindBody.remove();
					bind = -1;
					if(mix != null) {
						mix.remove();
						mix = null;
					}
				}
				// 一定量で餌生成
				if(amount > 12000) {
					ObjEX oex = null;
					if(sick)
						oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.WASTE.ordinal());
					else {
						if(sweet > 200000)
							oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.SWEETS1.ordinal());
						else
							oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.FOOD.ordinal());
					}
					oex.kick(0, 6, -4);
					SimYukkuri.world.getCurrentMap().food.put(oex.objId, (Food)oex);
					amount -= 8400;
					sweet = 0;
					sick = false;
				}
			}
		}
		else {
			if(mix != null) {
				mix.remove();
				mix = null;
			}
		}
	}

	@Override
	public void removeListData(){
		Body bindBody = YukkuriUtil.getBodyInstance(bind);
		if(bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setLockmove(false);
			bind = -1;
		}
		if(mix != null) {
			mix.remove();
			mix = null;
		}
		SimYukkuri.world.getCurrentMap().mixer.remove(objId);
	}
	/** コンストラクタ */
	public Mixer(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().mixer.put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.MIXER;

		interval = 5;
		value = 3000;
		cost = 50;
	}
	public Mixer() {
		
	}
}



