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
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.Cash;
import src.system.MessagePool;


/***************************************************
 * プレス機
 */
public class MachinePress extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;
	/**処理対象(ゆっくり、うんうん、吐餡)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.VOMIT;
	private static final int images_num = 8; //このクラスの総使用画像数
	private static int AnimeImagesNum[] = {8};//アニメごとに何枚使うか
	private static BufferedImage[] images = new BufferedImage[images_num + 1];
	private static Rectangle4y boundary = new Rectangle4y();
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num ; i++ ) {
			images[i] = ModLoader.loadItemImage(loader, "machinepress" + File.separator + "machinepress" + String.format("%03d",i+1) + ".png");
		}
		images[images_num] = ModLoader.loadItemImage(loader, "machinepress" + File.separator + "machinepress_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) layer[0] = images[(int)getAge() / 2 % AnimeImagesNum[0]];
		else layer[0] = images[AnimeImagesNum[0]];
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
		if(o.getObjType() == Type.YUKKURI){
			Body p = (Body)o;
			if(!p.isDead() && p.isNotNYD() && SimYukkuri.RND.nextInt(5) == 0){
				p.setHappiness(Happiness.VERY_SAD);
				p.setForceFace(ImageCode.CRYING.ordinal());
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.KilledInFactory), 40, true, true);
				}
			if ((int)getAge()/2%AnimeImagesNum[0] == 0) {
				p.setSilent(true);
				p.setShit(0, false);
				p.strikeByPress();
			}
		}
		return 0;
	}

	@Override
	public void upDate() {
		if ( getAge() % 2400 == 0  && enabled){
			Cash.addCash(-getCost());
		}
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().machinePress.remove(objId);
	}
	/**コンストラクタ*/
	public MachinePress(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), 8);
		SimYukkuri.world.getCurrentMap().machinePress.put(objId, this);
		objType = Type.FIX_OBJECT;
		objEXType = ObjEXType.MACHINEPRESS;

		value = 500000;
		cost = 1500;
	}
	public MachinePress() {
		
	}
}


