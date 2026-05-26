package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.CriticalDamageType;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.FootBake;
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

/**
 * ホットプレート
 */
public class HotPlate extends WorldEntity {

	private static final long serialVersionUID = -7407652564177670504L;
	/** 処理対象(ゆっくり) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private Yukkuri bindBody = null;
	private Effect smoke = null;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		for (int i = 0; i < 3; i++) {
			images[i] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate" + i + ".png");
		}
		images[3] = ModLoader.loadItemImage(loader, "hotplate" + File.separator + "hotplate_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			if (bindBody != null) {
				FootBake f = null;
				f = bindBody.getFootBakeLevel();
				if (f == FootBake.CRITICAL) {
					layer[0] = images[2];
				} else if (f == FootBake.MEDIUM) {
					layer[0] = images[1];
				} else {
					layer[0] = images[0];
				}
			} else {
				layer[0] = images[0];
			}
		} else {
			layer[0] = images[3];
			if (bindBody != null) {
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setCanPullOrPush(false);
				bindBody.setShadowVisible(true);
				bindBody = null;
			}
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

	/**
	 * Enable hit check.
	 *
	 * @return Enable hit check
	 */
	@Override
	public boolean enableHitCheck() {
		if (bindBody != null) {
			return false;
		}
		return true;
	}

	/** 衝突処理を行い、結果コードを返す。 */
	@Override
	public int objHitProcess(Entity o) {

		bindBody = (Yukkuri) o;
		if (bindBody.getCriticalDamageType() == CriticalDamageType.CUT) {
			return 0;
		}
		bindBody.clearActions();
		bindBody.setCalcX(x);
		bindBody.setCalcY(y);
		bindBody.setLockmove(true);
		if (smoke == null) {
			smoke = GameView.addEffect(EffectType.BAKED, bindBody.getX(), bindBody.getY() + 1,
					-2, 0, 0, 0, false, -1, -1, false, false, false);
		}
		return 1;
	}

	/** 毎ティックの状態更新を行う。 */
	@Override
	public void upDate() {
		if (getAge() % 2400 == 0) {
			Cash.addCash(-getCost());
		}
		if (bindBody != null) {
			bindBody.setShadowVisible(false);
			if (grabbed) {
				bindBody.setCalcX(x);
				bindBody.setCalcY(y);
				if (smoke != null) {
					smoke.setCalcX(x);
					smoke.setCalcY(y);
				}
			} else if (bindBody.getX() != x || bindBody.getY() != y || bindBody.getZ() != z || bindBody.isRemoved()) {
				bindBody.setForceFace(-1);
				bindBody.setLockmove(false);
				bindBody.setCanPullOrPush(false);
				bindBody.setShadowVisible(true);
				bindBody = null;
			} else {
				if (!bindBody.isDead()) {
					if (bindBody.isSleeping()) {
						bindBody.wakeup();
					}
					bindBody.addFootBakePeriod(50);
					bindBody.addDamage(20);
					bindBody.addStress(20);
					if (bindBody.getFootBakeLevel() == FootBake.CRITICAL) {
						bindBody.setCanPullOrPush(true);
					}
					if (bindBody.isNotNyd()) {
						bindBody.setHappiness(Happiness.VERY_SAD);
						bindBody.setForceFace(ImageCode.PAIN.ordinal());
					}
					if (GameRandom.nextInt(10) == 0) {
						bindBody.setMessage(GameMessages.getMessage(bindBody, MessagePool.Action.Burning), 40, true,
								true);
					}
				}
			}
		} else {
			if (smoke != null) {
				smoke.remove();
				smoke = null;
			}
		}
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		if (bindBody != null) {
			bindBody.setForceFace(-1);
			bindBody.setLockmove(false);
			bindBody = null;
		}
		if (smoke != null) {
			smoke.remove();
			smoke = null;
		}
		GameWorld.get().getCurrentWorldState().getHotPlates().remove(objId);
	}

	/** コンストラクタ */
	public HotPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getHotPlates().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.HOTPLATE;

		interval = 5;
		value = 5000;
		cost = 100;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public HotPlate() {

	}

	/** 関連付けられているゆっくりを返す。 */
	public Yukkuri getBoundYukkuri() {
		return bindBody;
	}

	/** 関連付けるゆっくりをセットする。 */
	public void setBoundYukkuri(Yukkuri bindBody) {
		this.bindBody = bindBody;
	}

	/** ホットプレートの煙エフェクトを返す。 */
	public Effect getSmoke() {
		return smoke;
	}

	/** ホットプレートの煙エフェクトをセットする。 */
	public void setSmoke(Effect smoke) {
		this.smoke = smoke;
	}

}
