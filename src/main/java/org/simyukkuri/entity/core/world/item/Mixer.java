package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import org.simyukkuri.command.GadgetAction;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Cash;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

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

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		Yukkuri bindBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(bind);
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

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** 境界線の取得 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 衝突判定対象タイプを返す。 */
	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	/**
	 * Enable hit check.
	 *
	 * @return Enable hit check
	 */
	public boolean enableHitCheck() {
		Yukkuri bindBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(bind);
		if (bindBody != null)
			return false;
		return true;
	}

	/** 衝突処理を行い、結果コードを返す。 */
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

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}
		Yukkuri bindBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(bind);
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
					bindBody.setCriticalDamageType(CriticalDamageType.CUT);
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
					mix = GameView.addEffect(EffectType.MIXED, bindBody.getX(),
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
						if (bindBody.getCriticalDamageType() == CriticalDamageType.CUT)
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
					GameWorld.get().getCurrentWorldState().getFoods().put(oex.objId, (Food) oex);
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

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		Yukkuri bindBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(bind);
		if (bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setLockmove(false);
			bind = -1;
		}
		if (mix != null) {
			mix.remove();
			mix = null;
		}
		GameWorld.get().getCurrentWorldState().getMixers().remove(objId);
	}

	/** コンストラクタ */
	public Mixer(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getMixers().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.MIXER;

		interval = 5;
		value = 3000;
		cost = 50;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public Mixer() {

	}

	/** バインド（拘束）中のゆっくり数を返す。 */
	public int getBind() {
		return bind;
	}

	/** バインド中のゆっくり数をセットする。 */
	public void setBind(int bind) {
		this.bind = bind;
	}

	/** ミキサーで混入するエフェクト（効果）を返す。 */
	public Effect getMix() {
		return mix;
	}

	/** ミキサーで混入するエフェクトをセットする。 */
	public void setMix(Effect mix) {
		this.mix = mix;
	}

	/** 動作カウンター値を返す。 */
	public int getCounter() {
		return counter;
	}

	/** 動作カウンター値をセットする。 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/** アイテムの量・個数を返す。 */
	public int getAmount() {
		return amount;
	}

	/** アイテムの量・個数をセットする。 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/** 甘味レベルを返す。 */
	public int getSweet() {
		return sweet;
	}

	/** 甘味レベルをセットする。 */
	public void setSweet(int sweet) {
		this.sweet = sweet;
	}

	/** 病原体フラグを返す。 */
	public boolean isSick() {
		return sick;
	}

	/** 病原体フラグをセットする。 */
	public void setSick(boolean sick) {
		this.sick = sick;
	}

}
