package src.item;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;

/***************************************************
製品投入口
*/
public class ProductChute extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**処理対象(ゆっくり、うんうん、フード、おもちゃ、物全般、吐餡、茎)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI + ObjEX.SHIT + ObjEX.FOOD + ObjEX.TOY + ObjEX.OBJECT + ObjEX.VOMIT + ObjEX.STALK;
	private static final int images_num = 2; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "ProductChute" + File.separator + "ProductChute.png");
		images[1] = ModLoader.loadItemImage(loader, "ProductChute" + File.separator + "ProductChute_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}


	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) layer[0] = images[0];
		else layer[0] = images[1];
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
	public int objHitProcess( Obj o ) {
		// ディフューザー、ゆんばは消さない
		if( (o instanceof Diffuser) || (o instanceof Yunba) ){
			return 0;
		}
		if (o instanceof Body) {
			Cash.sellYukkuri((Body)o);
		}
		else {
			Cash.addCash(o.getValue() >> 1);
		}
		Cash.addCash( -getCost() );
		o.remove();
		return 0;
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().productchute.remove(objId);
	}
	/**
	 * コンストラクタ
	 */
	public ProductChute(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().productchute.put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.PRODUCTCHUTE;
		interval = 10;
		value = 5000;
		cost = 50;
	}
	public ProductChute() {
		
	}
}


