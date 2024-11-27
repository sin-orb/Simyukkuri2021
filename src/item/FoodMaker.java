package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetAction;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.CoreAnkoState;
import src.enums.ObjEXType;
import src.enums.Type;
import src.enums.YukkuriType;
import src.game.Shit;
import src.game.Vomit;
import src.item.Food.FoodType;
import src.system.Cash;

/***************************************************
 * フードメーカー
 */
public class FoodMaker extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = 2267609715917033769L;
	/**処理対象(ゆっくり、うんうん、フード、吐餡、茎)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.FOOD | ObjEX.VOMIT | ObjEX.STALK;
	private static final int images_num = 6; //このクラスの総使用画像数
	private static int AnimeImagesNum[] = {images_num};//アニメごとに何枚使うか
	private static BufferedImage[] images = new BufferedImage[images_num + 1];
	private static Rectangle4y boundary = new Rectangle4y();

	protected boolean processReady = true;
	protected int stockFood = -1;
	private int foodAmount = 0;
	//private static final int numOfBodyType= 5;
	//private static final int numOfFoodType = 5;

	private static final int[][] makeTable = {{ 1,3,5,1,1,1,2,3,4,5,0,8,6,7},//通常種
											  { 3,1,5,1,1,1,2,3,4,5,0,8,6,7},//希少種・捕食種
											  { 5,5,1,5,1,5,2,5,4,5,0,8,6,7},//ありす
											  { 1,1,5,2,1,2,2,2,4,5,0,8,6,7},//ちぇん
											  { 1,1,1,1,1,1,1,1,1,1,1,8,6,7},//その他
											  { 1,1,5,2,1,1,1,1,4,1,0,8,6,7},//えさ：ふつう
											  { 2,2,2,2,1,1,2,0,0,0,0,8,6,7},//えさ：苦い
											  { 3,3,5,2,1,1,0,3,4,0,0,8,6,7},//えさ：ラムネ
											  { 4,4,4,4,1,4,0,4,4,0,0,8,6,7},//えさ：辛い
											  { 5,5,5,5,1,1,0,0,0,5,0,8,6,7},//えさ：バイゆグラ
											  { 0,0,0,0,1,0,0,0,0,0,0,8,6,7},//shit
											  { 8,8,8,8,8,8,8,8,8,8,8,8,8,8},//ゴミ
											  { 6,6,6,6,6,6,6,6,6,6,6,8,6,7},//軽い非ゆっくり症
											  { 7,7,7,7,7,7,7,7,7,7,7,8,7,7},//重い非ゆっくり症
											};


	private static final FoodType[] foodTable =	{FoodType.SHIT,
													 FoodType.FOOD,
													 FoodType.BITTER,
													 FoodType.LEMONPOP,
													 FoodType.HOT,
													 FoodType.VIYUGRA,
													 FoodType.SWEETS1,
													 FoodType.SWEETS2,
													 FoodType.WASTE,

													};
	/** 画像ロード */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num; i++ ){
			images[i] = ModLoader.loadItemImage(loader, "foodmaker" + File.separator + "foodmaker" + String.format("%03d",i+1) + ".png");
		}
		images[images_num] = ModLoader.loadItemImage(loader, "foodmaker" + File.separator + "foodmaker_off.png");
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) {
			layer[0] = images[(int)getAge() / 2 % AnimeImagesNum[0]];
		} else {
			layer[0] = images[AnimeImagesNum[0]];
		}
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int objHitProcess( Obj o ) {
		if ( !processReady ){
			return 0;
		}
		FoodType foodType = null;
		if (stockFood == -1){
			if ( o.getObjType() == Type.YUKKURI ){
				Body b = (Body)o;
				if ( b.isCrushed()  || b.isPealed() || b.isBaby()  ) {
					if ( b.isSick() ) {//カビ
						stockFood = 11;
					}else if( b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear){//軽度の非ゆっくり症
						stockFood = 12;
					}else if( b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease){//重度の非ゆっくり症
						stockFood = 13;
					}else if ( b.getType() == 2 ) {//ありす
						stockFood = 2;
					}else if ( b.getType() == 4 ) {//ちぇん
						stockFood = 3;
					}else if ( b.getType() == 1004 ) {//めーりん
						stockFood = 8;
					}else if( b.getType() >= 10000 ){//ハイブリッド・雑種
						stockFood = 4;
					}else if( b.getType() >= 3000 ){//捕食種
						stockFood = 1;
					}else if( b.getType() >= 2000 ){//変異種
						stockFood = 4;
					}else if( b.getType() >= 1000 ){//希少種
						stockFood = 1;
					}else{//通常種・
						stockFood = 0;
					}
					switch(b.getBodyAgeState()) {
						case BABY:
							foodAmount += 1;
							break;
						case CHILD:
							foodAmount += 2;
							break;
						case ADULT:
							foodAmount += 4;
							break;
					}
					if ( b.isAdult()  ) {
						objHitProcess( o );
					}
					else{
						b.remove();
					}
				}
			}
			else if ( o.getObjType() == Type.OBJECT && o instanceof Food ) {
				Food f = (Food)o;
				switch(f.getFoodType()){
					case FOOD:
						stockFood = 5;
						break;
					case BITTER:
						stockFood = 6;
						break;
					case LEMONPOP:
						stockFood = 7;
						break;
					case HOT:
						stockFood = 8;
						break;
					case VIYUGRA:
						stockFood = 9;
						break;
					case WASTE:
						stockFood = 11;
						break;
					default:
						stockFood = 5;
						break;
				}
				f.remove();
			}else if ( o.getObjType() == Type.SHIT){
				Shit s = (Shit)o;
				stockFood = 5+5;
				s.remove();
			}else if (o.getObjType() == Type.VOMIT ){
				Vomit v = (Vomit)o;
				stockFood = 5+5;
				v.remove();
			}

		}
		else{
			if ( o.getObjType() == Type.YUKKURI ){
				Body b = (Body)o;
				if ( b.isCrushed()  || b.isPealed() || b.isBaby()  ) {
					if ( b.isSick() ) {//カビ
						foodType = foodTable[makeTable[stockFood][11]];
					}else if( b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDiseaseNear){//軽度の非ゆっくり症
						foodType = foodTable[makeTable[stockFood][12]];
					}else if( b.geteCoreAnkoState() == CoreAnkoState.NonYukkuriDisease){//重度の非ゆっくり症
						foodType = foodTable[makeTable[stockFood][13]];
					}else if ( b.getType() == 2 ) {//ありす
						foodType = foodTable[makeTable[stockFood][2]];
					}else if ( b.getType() == 4 ) {//ちぇん
						foodType = foodTable[makeTable[stockFood][3]];
					}else if ( b.getType() == 1004 ) {//めーりん
						foodType = foodTable[makeTable[stockFood][8]];
					}else if( b.getType() >= 10000 ){//ハイブリッド・雑種
						foodType = foodTable[makeTable[stockFood][4]];
					}else if( b.getType() >= 3000 ){//捕食種
						foodType = foodTable[makeTable[stockFood][1]];
					}else if( b.getType() >= 2000 ){//変異種
						foodType = foodTable[makeTable[stockFood][4]];
					}else if( b.getType() >= 1000 ){//希少種
						foodType = foodTable[makeTable[stockFood][1]];
					}else{//通常種
						foodType = foodTable[makeTable[stockFood][0]];
					}
					switch(b.getBodyAgeState()) {
					case BABY:
						foodAmount += 1;
						break;
					case CHILD:
						foodAmount += 2;
						break;
					case ADULT:
						foodAmount += 4;
						break;
					}
					Cash.addCash(-getCost());
					b.remove();
				}
			}
			else if ( o.getObjType() == Type.OBJECT && o instanceof Food ){
				Food f = (Food)o;
				switch(f.getFoodType()){
					case FOOD:
						foodType = foodTable[makeTable[stockFood][5]];
						break;
					case BITTER:
						foodType = foodTable[makeTable[stockFood][6]];
						break;
					case LEMONPOP:
						foodType = foodTable[makeTable[stockFood][7]];
						break;
					case HOT:
						foodType = foodTable[makeTable[stockFood][8]];
						break;
					case VIYUGRA:
						foodType = foodTable[makeTable[stockFood][9]];
						break;
					case WASTE:
						foodType = foodTable[makeTable[stockFood][11]];
						break;
					default:
						foodType = foodTable[makeTable[stockFood][5]];
						break;
				}
				Cash.addCash(-getCost());
				f.remove();
			}
			else if ( o.getObjType() == Type.SHIT ){
				Shit s = (Shit)o;
				foodType = foodTable[makeTable[stockFood][10]];
				Cash.addCash(-getCost());
				s.remove();
			}
			else if (o.getObjType() == Type.VOMIT){
				Vomit v = (Vomit)o;
				foodType = foodTable[makeTable[stockFood][10]];
				Cash.addCash(-getCost());
				v.remove();
			}
			if (foodType == null) {
				return 0;
			}
			int dir = 1;
			if(x + 40 >= Translate.mapW) dir = -1;
			if (foodType == FoodType.SHIT){
				SimYukkuri.mypane.getTerrarium().addVomit(x + (40 * dir), y, 0, null, YukkuriType.REIMU);
			}
			else{
				for(int i = 0;i<(foodAmount>>1);i++){
					Food f = (Food)GadgetAction.putObjEX(Food.class, x + (40 * dir), y, foodType.ordinal());
					SimYukkuri.world.getCurrentMap().food.put(f.objId, f);
				}
				foodAmount = 0;

			}
			stockFood = -1;
		}
		return 0;
	}

	@Override
	public void upDate() {
		if ( getAge() % 4 == 0 && !processReady){
			processReady = true;
		}
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().foodmaker.remove(objId);
	}
	/** コンストラクタ*/
	public FoodMaker(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().foodmaker.put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.FOODMAKER;

		interval = 20;
		value = 50000;
		cost = 30;
	}
	
	public FoodMaker() {
		
	}

	public boolean isProcessReady() {
		return processReady;
	}

	public void setProcessReady(boolean processReady) {
		this.processReady = processReady;
	}

	public int getStockFood() {
		return stockFood;
	}

	public void setStockFood(int stockFood) {
		this.stockFood = stockFood;
	}

	public int getFoodAmount() {
		return foodAmount;
	}

	public void setFoodAmount(int foodAmount) {
		this.foodAmount = foodAmount;
	}
	
}



