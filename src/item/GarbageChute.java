package src.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Happiness;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.MessagePool;

/***************************************************
 * ダストシュート
 */
public class GarbageChute extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**処理対象(ゆっくり、うんうん、フード、吐餡、茎)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.FOOD | ObjEX.TOY | ObjEX.OBJECT
			| ObjEX.VOMIT | ObjEX.STALK;
	private static final int images_num = 4; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	List<Obj> bindObjList = new LinkedList<Obj>();

	private ItemRank itemRank;
	private Body bindBody = null;

	/**画像ロード*/
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute.png");
		images[1] = ModLoader.loadItemImage(loader, "garbagechute" + File.separator + "garbagechute_off.png");
		images[2] = ModLoader.loadItemImage(loader,
				"garbagechute" + File.separator + "garbagechute" + ModLoader.YK_WORD_NORA + ".png");
		images[3] = ModLoader.loadItemImage(loader,
				"garbagechute" + File.separator + "garbagechute" + ModLoader.YK_WORD_NORA + "_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
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
	public int objHitProcess(Obj o) {
		// ディフューザー、ゆんばは消さない
		if ((o instanceof Diffuser) || (o instanceof Yunba)) {
			return 0;
		}
		if (o == null || bindObjList.contains(o)) {
			return 0;
		}
		if (o instanceof Body) {
			bindBody = (Body) o;
			bindObjList.add(o);
			if (!bindBody.isDead()) {
				bindBody.setHappiness(Happiness.VERY_SAD);
				if (bindBody.isOverPregnantLimit()) {
					bindBody.setPikoMessage(MessagePool.getMessage(bindBody, MessagePool.Action.DontThrowMeAway), 60,
							true);
				} else
					bindBody.setMessage(MessagePool.getMessage(bindBody, MessagePool.Action.Surprise), 60, true, true);
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
		int nSize = bindObjList.size();

		for (int i = nSize - 1; 0 <= i; i--) {
			Obj o = bindObjList.get(i);
			o.setFallingUnderGround(true);
			if (o == null || o.isRemoved()) {
				continue;
			}
			o.setX(this.getX());
			o.setY(this.getY());
			int nZ = o.getZ();
			o.setZ(nZ - 2);
			int tz = Translate.translateZ(nZ - 1);
			int nColX = o.getH();
			if (tz < -nColX) {
				bindObjList.remove(o);
				o.remove();
			}
		}
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.getCurrentMap().garbagechute.remove(objId);
	}

	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public GarbageChute(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().garbagechute.put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.GARBAGECHUTE;

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
}
