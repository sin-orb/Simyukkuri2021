package src.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.entity.core.Entity;
import src.entity.core.effect.Effect;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.WorldEntity;
import src.enums.CriticalDamegeType;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Type;
import src.enums.WorldEntityKind;
import src.system.Cash;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameView;
import src.util.GameWorld;

/***************************************************
 * ミキサー
 */
public class Mixer extends WorldEntity {

	private static final long serialVersionUID = 6267442912877753797L;
	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private int bind = -1;
	private Effect mix = null;
	private int counter = 0; // ゆっくりが乗ってからの経過時間
	private int amount = 0; // 保有原料
	private int sweet = 0; // 糖度
	private boolean sick = false; // カビ混入

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		for (int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "mixer" + File.separator + "mixer_" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "mixer" + File.separator + "mixer_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		Yukkuri bindBody = src.util.BodyRegistry.getBodyInstance(bind);
		if (enabled) {
			if (bindBody != null) {
				if (counter > 60) {
					if (bindBody.getAnkoAmount() < (bindBody.getDamageLimit() >> 1))
						layer[0] = images[2];
					else
						layer[0] = images[1];
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
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
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
		Yukkuri bindBody = src.util.BodyRegistry.getBodyInstance(bind);
		if (bindBody != null)
			return false;
		return true;
	}

	@Override
	public int objHitProcess(Entity o) {
		if (!enabled)
			return 0;
		Yukkuri bindBody = (Yukkuri) o;
		bindBody.clearActions();
		bindBody.setCalcX(x);
		bindBody.setCalcY(y);
		bindBody.setLockmove(true);
		bind = bindBody.getUniqueID();
		counter = 0;

		return 1;
	}

	@Override
	public void upDate() {
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}
		Yukkuri bindBody = src.util.BodyRegistry.getBodyInstance(bind);
		if (bindBody != null && enabled) {
			bindBody.setShadowVisible(false);
			if (grabbed) {
				bindBody.setCalcX(x);
				bindBody.setCalcY(y);
				if (mix != null) {
					mix.setCalcX(x);
					mix.setCalcY(y);
				}
				bind = bindBody.getUniqueID();
			} else if (bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				if (counter > 60)
					bindBody.setCriticalDamegeType(CriticalDamegeType.CUT);
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setShadowVisible(true);
				bind = -1;
				return;
			}
			counter++;
			// ミキサー駆動開始
			if (counter > 60) {
				if (mix == null) {
					mix = GameView.addEffect(EffectType.MIX, bindBody.getX(),
							bindBody.getY() + 1,
							-2, 0, 0, 0, false, -1, -1, false, false, false);
				}
				if (!bindBody.isDead()) {
					if (bindBody.isSleeping())
						bindBody.wakeup();
					bindBody.addDamage(100);
					bindBody.addStress(100);
					bindBody.setHappiness(Happiness.VERY_SAD);
					bindBody.setForceFace(ImageCode.PAIN.ordinal());
					if (GameRandom.nextInt(10) == 0) {
						if (bindBody.getCriticalDamegeType() == CriticalDamegeType.CUT)
							bindBody.setMessage(GameMessages.getMessage(bindBody, MessagePool.Action.Scream2), true);
						else
							bindBody.setMessage(GameMessages.getMessage(bindBody, MessagePool.Action.Scream), true);
					}
				}
				// 材料採取
				amount += 100;
				sweet += bindBody.getStress();
				if (bindBody.isSick())
					sick = true;
				if (bindBody.addAmount(-100)) {
					bindBody.remove();
					bind = -1;
					if (mix != null) {
						mix.remove();
						mix = null;
					}
				}
				// 一定量で餌生成
				if (amount > 12000) {
					WorldEntity oex = null;
					if (sick)
						oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.WASTE.ordinal());
					else {
						if (sweet > 200000)
							oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.SWEETS1.ordinal());
						else
							oex = GadgetAction.putObjEX(Food.class, getX(), getY(), Food.FoodType.FOOD.ordinal());
					}
					oex.kick(0, 6, -4);
					GameWorld.get().getCurrentMap().getFood().put(oex.objId, (Food) oex);
					amount -= 8400;
					sweet = 0;
					sick = false;
				}
			}
		} else {
			if (mix != null) {
				mix.remove();
				mix = null;
			}
		}
	}

	@Override
	public void removeListData() {
		Yukkuri bindBody = src.util.BodyRegistry.getBodyInstance(bind);
		if (bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setLockmove(false);
			bind = -1;
		}
		if (mix != null) {
			mix.remove();
			mix = null;
		}
		GameWorld.get().getCurrentMap().getMixer().remove(objId);
	}

	/** コンストラクタ */
	public Mixer(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentMap().getMixer().put(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.MIXER;

		interval = 5;
		value = 3000;
		cost = 50;
	}

	public Mixer() {

	}

	public int getBind() {
		return bind;
	}

	public void setBind(int bind) {
		this.bind = bind;
	}

	public Effect getMix() {
		return mix;
	}

	public void setMix(Effect mix) {
		this.mix = mix;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getSweet() {
		return sweet;
	}

	public void setSweet(int sweet) {
		this.sweet = sweet;
	}

	public boolean isSick() {
		return sick;
	}

	public void setSick(boolean sick) {
		this.sick = sick;
	}

}
