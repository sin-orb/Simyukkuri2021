package org.simyukkuri.entity.core.world.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Cash;
import org.simyukkuri.util.GameWorld;

/**
 * 製品投入口
 */
public class ProductChute extends WorldEntity {

	private static final long serialVersionUID = 3997454948004838706L;
	/** 処理対象(ゆっくり、うんうん、フード、おもちゃ、物全般、吐餡、茎) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI + WorldEntity.SHIT + WorldEntity.FOOD
			+ WorldEntity.TOY + WorldEntity.OBJECT
			+ WorldEntity.VOMIT + WorldEntity.STALK;
	private static final int IMAGE_COUNT = 2; // このクラスの総使用画像数
	private static BufferedImage[] imageLayers = new BufferedImage[IMAGE_COUNT];
	private static Rectangle4y boundary = new Rectangle4y();

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		imageLayers[0] = ModLoader.loadItemImage(loader, "ProductChute" + File.separator + "ProductChute.png");
		imageLayers[1] = ModLoader.loadItemImage(loader, "ProductChute" + File.separator + "ProductChute_off.png");
		boundary.setWidth(imageLayers[0].getWidth(io));
		boundary.setHeight(imageLayers[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			layer[0] = imageLayers[0];
		} else {
			layer[0] = imageLayers[1];
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

	/** 衝突処理を行い、結果コードを返す。 */
	@Override
	public int objHitProcess(Entity o) {
		// ディフューザー、ゆんばは消さない
		if ((o instanceof Diffuser) || (o instanceof Yunba)) {
			return 0;
		}
		if (o instanceof Yukkuri) {
			Cash.sellYukkuri((Yukkuri) o);
		} else {
			Cash.addCash(o.getValue() >> 1);
		}
		Cash.addCash(-getCost());
		o.remove();
		return 0;
	}

	/** ワールドからこのアイテムを除去する。 */
	@Override
	public void removeFromWorld() {
		GameWorld.get().getCurrentWorldState().getProductChutes().remove(objId);
	}

	/**
	 * コンストラクタ
	 */
	public ProductChute(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		if (GameWorld.get() != null) {
			GameWorld.get().getCurrentWorldState().getProductChutes().put(objId, this);
			GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		}
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.PRODUCTCHUTE;
		interval = 10;
		value = 5000;
		cost = 50;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public ProductChute() {

	}

}
