package src.game;



import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.Event;
import src.enums.ObjEXType;
import src.enums.Type;
import src.item.Barrier;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;


/**
 * 茎
 */
public class Stalk extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -7644967944795406729L;
	private static final int images_num = 1; //このクラスの総使用画像数
	private static transient BufferedImage[] images = new BufferedImage[images_num*2+1];
	private static Rectangle4y boundary = new Rectangle4y();

	private int plantYukkuri = -1;		// この茎が生えてる親
	private List<Integer> bindBabies = new LinkedList<Integer>();	// この茎にぶら下がってる子のID
	/** （食べたときの）量 */
	private int amount = 0;
	/**
	 * イメージをロードする.
	 * @param loader ローダ
	 * @param io イメージオブザーバ
	 * @throws IOException IO例外
	 */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		final String path = "images/yukkuri/general/";
		for( int i = 0; images_num > i ; i++ ){
			images[i*2] = ImageIO.read(loader.getResourceAsStream(path+"stalk"+String.format("%03d",i+1)+".png"));
			images[i*2+1] = ModLoader.flipImage(images[i*2]);
		}
		images[images_num*2] = ImageIO.read(loader.getResourceAsStream(path + "stalk_shadow.png"));
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height - 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if ( option == 0 ){
			layer[0] = images[1];
		}else{
			layer[0] = images[0];
		}
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		if(plantYukkuri == -1) return images[2];
		return null;
	}
	/**
	 * 方向を設定する.
	 * @param dir 方向
	 */
	public void setDirection( int dir ) {
		if ( dir == 0 ){
			option = 0;
		}else{
			option = 1;
		}
	}
	
	@Override
	public void upDate () {
		int i = 0;
		int babyX = 0;
		int babyZ = 0;
		if (getBindBabies() == null) {
			return;
		}
		for (Integer j : getBindBabies()){
			if (j == null) {
				i++;
				continue;
			}
			Body b = YukkuriUtil.getBodyInstance(j);
			if ( b == null ) {
				i++;
				continue;
			}
			if (option == 0) {
				babyX = (( i % 5 ) * -5 + 14);
				b.setDirection(Direction.RIGHT);
			}else{
				babyX = (( i % 5 ) * -5 + 14) * -1;
				b.setDirection(Direction.LEFT);
			}
			babyZ = (( i % 5 ) * -2 + 14);
			b.setCalcX( getX() + babyX );
			b.setCalcY( getY() + 1 );
			b.setCalcZ( getZ() + babyZ );
			b.kick(0,0,0);
			i++;
		}
	}

	@Override
	public void removeListData(){
		remove();
		SimYukkuri.world.getCurrentMap().stalk.remove(objId);
	}
	/**
	 * この茎をはやしているゆっくりを設定する.
	 * @param b この茎をはやしているゆっくり
	 */
	public void setPlantYukkuri( Body b ) {
		if (b == null) {
			plantYukkuri = -1;
		} else {
			plantYukkuri = b.getUniqueID();
		}
	}
	/**
	 * この茎をはやしているゆっくりを取得する.
	 * @return この茎をはやしているゆっくり
	 */
	public int getPlantYukkuri() {
		return plantYukkuri;
	}

	/*
	@Override
	public int getBindObj() {
		return plantYukkuri;
	}
	*/
	/**
	 * この茎に実ゆっくりを追加する.
	 * @param b この茎に生やそうとしている実ゆっくり
	 */
	public void setBindBaby( Body b ) {
		if ( bindBabies.size() < 5 ) {
			bindBabies.add(b == null ? -1 : b.getUniqueID());
		}
	}
	/**
	 * この茎に生えている実ゆっくりを取得する.
	 * @return この茎に生えている実ゆっくり
	 */
	public List<Integer> getBindBabies() {
		return bindBabies;
	}
	/**
	 * 茎から実ゆっくりをすべて取り除く.
	 */
	public void disBindBabys() {
		if ( plantYukkuri != -1) {
			Body planted = YukkuriUtil.getBodyInstance(plantYukkuri);
			if (planted != null && planted.getStalks() != null) {
				planted.getStalks().set(planted.getStalks().indexOf( this ), null );
			}
		}

		for ( int i : bindBabies ){
			Body b = YukkuriUtil.getBodyInstance(i);
			if ( b != null ){
				b.setBindStalk(null) ;
			}
		}
	}
	/**
	 * X座標を設定する.
	 * @param X座標
	 */
	public void setCalcX (int X)
	{
		if (X < 0 && plantYukkuri == -1) {
			x = 0;
		}
		else if (X > Translate.mapW && plantYukkuri == -1) {
			x = Translate.mapW;
		}
		else {
			x = X;
		}
	}
	/**
	 * Y座標を設定する.
	 * @param Y座標
	 */
	public void setCalcY (int Y) {
		if (Y < 0 && plantYukkuri == -1) {
			y = 0;
		}
		else if(Y > Translate.mapH && plantYukkuri == -1) {
			y = Translate.mapH;
		}
		else {
			y = Y;
		}
	}
	/**
	 * Z座標を設定する.
	 * @param Z座標
	 */
	public void setCalcZ(int Z)
	{
		if (Z < nMostDepth && plantYukkuri == -1) {
			if( bFallingUnderGround )
			{
				z = Z;
			}else{
				z = nMostDepth;				
			}
		}
		else if (Z > Translate.mapZ && plantYukkuri == -1) {
			z = Translate.mapZ;
		}
		else {
			z = Z;
		}
	}
	/**
	 * この茎がゆっくりから生えている状態であるかどうかを取得する.
	 * @return この茎がゆっくりから生えている状態であるかどうか
	 */
	@Transient
	public boolean isPlantYukkuri(){
		for ( int i : bindBabies ){
			Body b = YukkuriUtil.getBodyInstance(i);
			if ( b != null ){
				return true;
			}
		}
		return (plantYukkuri != -1);
	}
	/**
	 * 茎を食べる.
	 * @param eatAmount 食べる量
	 */
	public void eatStalk(int eatAmount)
	{
		amount -= eatAmount;
		if (amount <= 0) {
			amount = 0;
			for ( Integer i : bindBabies ){
				if (i == null) {
					continue;
				}
				Body b = YukkuriUtil.getBodyInstance(i);
				if ( b != null ){
					b.setBindStalk(null) ;
				}
			}
			remove();
			SimYukkuri.world.getCurrentMap().stalk.remove(objId);
		}
	}

	@Override
	public void grab() {
		grabbed = true;
		if ( takePlantYukkuri() != null ){
			takePlantYukkuri().removeStalk(this);
		}
		setPlantYukkuri(null);
	}
	
	/**
	 * 生えているゆっくりを取得する.
	 * @return 生えているゆっくり
	 */
	public Body takePlantYukkuri() {
		return SimYukkuri.world.getCurrentMap().body.get(plantYukkuri);
	}

	@Override
	public Event clockTick()
	{
		setAge(getAge() + TICK);
		if (isRemoved()) {
			removeListData();
			disBindBabys();
			return Event.REMOVED;
		}
		if (!grabbed && plantYukkuri == -1) {
			if (vx != 0) {
				x += vx;
				if (x < 0) {
					x = 0;
					vx *= -1;
				}
				else if (x > Translate.mapW) {
					x = Translate.mapW;
					vx *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
					x -= vx;
					vx = 0;
				}
			}
			if (vy != 0) {
				y += vy;
				if (y < 0) {
					y = 0;
					vy *= -1;
				}
				else if (y > Translate.mapH) {
					y = Translate.mapH;
					vy *= -1;
				}
				else if (Barrier.onBarrier(x, y, getW() >> 2, getH() >> 2, Barrier.MAP_ITEM)) {
					y -= vy;
					vy = 0;
				}
			}
			if (z != 0 || vz != 0) {
				vz += 1;
				z -= vz;
				if( !bFallingUnderGround)
				{
					if (z <= nMostDepth) {
						z = nMostDepth;
						vx = 0;
						vy = 0;
						vz = 0;
					}
				}
			}
		}
		upDate();
		calcPos();
		return Event.DONOTHING;
	}
	/**
	 * コンストラクタ.
	 * @param initX 初期X座標
	 * @param initY 初期Y座標
	 * @param initOption オプション
	 */
	public Stalk(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		objType = Type.OBJECT;
		objEXType = ObjEXType.STALK;
		amount = 100*24*5;
		SimYukkuri.world.getCurrentMap().stalk.put(objId, this);
		calcPos();
	}
	
	public Stalk() {
		
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return 0;
	}

	@Override
	public int objHitProcess(Obj o) {
		return 0;
	}
	
	@Override
	public void remove() {
		plantYukkuri = -1;
		for (Integer i : getBindBabies()) {
			if (i == null) {
				continue;
			}
			Body baby = YukkuriUtil.getBodyInstance(i);
			if (baby != null) {
				baby.setBindStalk(null);
				baby.setBindObj(-1);
			}
		}
		bindBabies.clear();
		//SimYukkuri.world.getCurrentMap().stalk.remove(this);
		super.remove();
	}
	
	@Override
	public String toString() {
		Body p = YukkuriUtil.getBodyInstance(plantYukkuri);
		String ret = "";
		ret += ResourceUtil.getInstance().read("game_stalk1");
		if (p != null) {
		ret += (plantYukkuri == -1 ? ResourceUtil.getInstance().read("command_status_nothing") : ResourceUtil.IS_JP ? 
				p.getNameJ() : p.getNameE());
		}
		ret += ResourceUtil.getInstance().read("game_stalk2");
		if (bindBabies == null || bindBabies.size() == 0) {
			ret += ResourceUtil.getInstance().read("command_status_nothing");
		} else {
			for (Object o : bindBabies) {
				if (o == null) {
					continue;
				} else {
					Integer b = (Integer)o;
					Body baby = YukkuriUtil.getBodyInstance(b);
					if (baby == null) {
						ret += ResourceUtil.getInstance().read("game_empty");
					} else {
						ret += ResourceUtil.IS_JP ? baby.getNameJ() : baby.getNameE();
					}
					ret += ",";
				}
			}
			ret = ret.substring(0, ret.length() - 1);
		}
		ret += ")";
		return ret;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setPlantYukkuri(int plantYukkuri) {
		this.plantYukkuri = plantYukkuri;
	}

	public void setBindBabies(List<Integer> bindBaby) {
		this.bindBabies = bindBaby;
	}

	@Override
	public GetMenuTarget hasGetPopup() {
		return GetMenuTarget.STALK;
	}
}

