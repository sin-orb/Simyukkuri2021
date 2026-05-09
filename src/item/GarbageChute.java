package src.item;
import src.util.GameMessages;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import src.SimYukkuri;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.base.Entity;
import src.base.WorldEntity;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Happiness;
import src.enums.WorldEntityKind;
import src.enums.Type;
import src.system.Cash;
import src.system.MessagePool;

/***************************************************
 * ダストシュート
 */
public class GarbageChute extends WorldEntity {

	private static final long serialVersionUID = -2629236583041612041L;
	/** 処理対象(ゆっくり、うんうん、フード、吐餡、茎) */
	public static final int hitCheckObjType = WorldEntity.YUKKURI | WorldEntity.SHIT | WorldEntity.FOOD | WorldEntity.TOY | WorldEntity.OBJECT
			| WorldEntity.VOMIT | WorldEntity.STALK;
	private static final int images_num = 4; // このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	List<Entity> bindObjList = new LinkedList<Entity>();

	private ItemRank itemRank;
	private Yukkuri bindBody = null;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute.png");
		images[1] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute_off.png");
		images[2] = ModLoader.loadItemImage(loader,
				"garbagechute" + File.separator + "garbagechute" + ModLoader.getYkWordNora() + ".png");
		images[3] = ModLoader.loadItemImage(loader,
				"garbagechute" + File.separator + "garbagechute" + ModLoader.getYkWordNora() + "_off.png");
		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (itemRank == ItemRank.HOUSE) {
			if (enabled)
				layer[0] = images[0];
			else
				layer[0] = images[1];
		} else {
			if (enabled)
				layer[0] = images[2];
			else
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
	public int objHitProcess(Entity o) {
		// ディフューザー、ゆんばは消さない
		if ((o instanceof Diffuser) || (o instanceof Yunba)) {
			return 0;
		}
		if (o == null || bindObjList.contains(o)) {
			return 0;
		}
		if (o instanceof Yukkuri) {
			bindBody = (Yukkuri) o;
			bindObjList.add(o);
			if (!bindBody.isDead()) {
				bindBody.setHappiness(Happiness.VERY_SAD);
				if (bindBody.isOverPregnantLimit()) {
					bindBody.setPikoMessage(GameMessages.getMessage(bindBody, MessagePool.Action.DontThrowMeAway), 60,
							true);
				} else
					bindBody.setMessage(GameMessages.getMessage(bindBody, MessagePool.Action.Surprise), 60, true, true);
			}
			o.setFallingUnderGround(true);
		} else {
			o.remove();
		}
		Cash.addCash(-getCost());
		return 0;
	}

	@Override
	public void upDate() {
		if (bindObjList == null || bindObjList.size() == 0) {
			return;
		}
		int size = bindObjList.size();

		for (int i = size - 1; 0 <= i; i--) {
			Entity o = bindObjList.get(i);
			o.setFallingUnderGround(true);
			if (o == null || o.isRemoved()) {
				continue;
			}
			o.setCalcX(this.getX());
			o.setCalcY(this.getY());
			int zCoord = o.getZ();
			o.setCalcZ(zCoord - 2);
			int translateZ = Translate.translateZ(zCoord - 1);
			int collisionX = o.getH();
			if (translateZ < -collisionX) {
				bindObjList.remove(o);
				o.remove();
			}
		}
	}

	@Override
	public void removeListData() {
		GameWorld.get().getCurrentMap().getGarbagechute().remove(objId);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param initX      x座標
	 * @param initY      y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public GarbageChute(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentMap().getGarbagechute().put(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.GARBAGECHUTE;

		interval = 4;

		itemRank = ItemRank.values()[initOption];
		if (itemRank == ItemRank.HOUSE) {
			value = 5000;
			cost = 5;
		} else {
			value = 0;
			cost = 0;
		}
	}

	public GarbageChute() {

	}

	public List<Entity> getBindObjList() {
		return bindObjList;
	}

	public void setBindObjList(List<Entity> bindObjList) {
		this.bindObjList = bindObjList;
	}

	public ItemRank getItemRank() {
		return itemRank;
	}

	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}

	public Yukkuri getBindBody() {
		return bindBody;
	}

	public void setBindBody(Yukkuri bindBody) {
		this.bindBody = bindBody;
	}

}
