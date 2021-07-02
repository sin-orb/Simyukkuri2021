package src.item;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;

import src.SimYukkuri;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.ObjEXType;
import src.enums.Type;

/***************************************************
 * 食べ物
 */
public class Food extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 2L;

	/** 空き皿画像テーブル*/
	public static enum EmptyImage {
		DISH("empty.png"),
		SWEETS("sweets_empty.png"),
		WASTE("waste_empty.png"),
		DISH_NORA("empty_nora.png"),
		SWEETS_NORA("sweets_nora_empty.png"),
		WASTE_NORA("waste_nora_empty.png"),
		DISH_YASEI("empty_yasei.png"),
		SWEETS_YASEI("sweets_yasei_empty.png"),
		WASTE_YASEI("waste_yasei_empty.png"),
		STALK(null),
		FUEL(null),
		;
		public String fileName;
		EmptyImage(String fn) {
			fileName = fn;
		}
	}
	
	/** 食料タイプテーブル */
	public static enum FoodType {
			// 値段 見た目  量         画像          空き皿           影
		SWEETS1( 500, 999, 100*24*2, "sweets1.png", EmptyImage.SWEETS, true),
		SWEETS2(1000, 999, 100*24*2, "sweets2.png", EmptyImage.SWEETS, true),
		STALK(     0, 500, 100*24*4, "stalk_food.png", EmptyImage.STALK, true),
		FOOD(    250, 400, 100*24*24, "gohan1.png", EmptyImage.DISH, true),
		BITTER(  400, 300, 100*24*24, "gohan2.png", EmptyImage.DISH, true),
		LEMONPOP(300, 300, 100*24*24, "gohan3.png", EmptyImage.DISH, true),
		HOT(     400, 150, 100*24*24, "gohan4.png", EmptyImage.DISH, true),
		VIYUGRA(1000, 150, 100*24*24, "gohan5.png", EmptyImage.DISH, true),
		WASTE(     0,-50, 100*24*32, "waste1.png", EmptyImage.WASTE, false),

		SWEETS_NORA1( 0,  50, 100*24, "sweets_nora1.png", EmptyImage.SWEETS_NORA, true),
		SWEETS_NORA2( 0,  50, 100*24, "sweets_nora2.png", EmptyImage.SWEETS_NORA, true),
		FOOD_NORA(    0,   0, 100*24*24, "gohan_nora1.png", EmptyImage.DISH_NORA, true),
		BITTER_NORA(  0,-200, 100*24*24, "gohan_nora2.png", EmptyImage.DISH_NORA, true),
		LEMONPOP_NORA(0,-100, 100*24*24, "gohan_nora3.png", EmptyImage.DISH_NORA, true),
		HOT_NORA(     0,-200, 100*24*24, "gohan_nora4.png", EmptyImage.DISH_NORA, true),
		VIYUGRA_NORA( 0,-200, 100*24*24, "gohan_nora5.png", EmptyImage.DISH_NORA, true),
		WASTE_NORA(   0,-300, 100*24*32, "waste_nora1.png", EmptyImage.WASTE_NORA, false),

		SWEETS_YASEI1( 0,  50, 100*24, "sweets_yasei1.png", EmptyImage.SWEETS_YASEI, true),
		SWEETS_YASEI2( 0,  50, 100*24, "sweets_yasei2.png", EmptyImage.SWEETS_YASEI, true),
		FOOD_YASEI(    0,   0, 100*24*24, "gohan_yasei1.png", EmptyImage.DISH_YASEI, true),
		BITTER_YASEI(  0,-200, 100*24*24, "gohan_yasei2.png", EmptyImage.DISH_YASEI, true),
		LEMONPOP_YASEI(0,-100, 100*24*24, "gohan_yasei3.png", EmptyImage.DISH_YASEI, true),
		HOT_YASEI(     0,-200, 100*24*24, "gohan_yasei4.png", EmptyImage.DISH_YASEI, true),
		VIYUGRA_YASEI( 0,-200, 100*24*24, "gohan_yasei5.png", EmptyImage.DISH_YASEI, true),
		WASTE_YASEI(   0,-300, 100*24*32, "waste_yasei1.png", EmptyImage.WASTE_YASEI, false),

		VOMIT(  0,-100, 0, null, null, false),
		SHIT(   0,-500, 0, null, null, false),
		BODY(   0,-999, 0, null, null, false),
		;
		public int value;
		public int looks;
		public int amount;
		public String fileName;
		public EmptyImage emptyImg;
		public boolean shadow;
		FoodType(int val, int look, int amt, String fn, EmptyImage emp, boolean sh) {
			value = val;
			looks = look;
			amount = amt;
			fileName = fn;
			emptyImg = emp;
			shadow = sh;
		}
	}

	private FoodType foodType;
	/**量*/
	public int amount;

	private static BufferedImage[] emptyImages = new BufferedImage[EmptyImage.values().length];
	private static Rectangle4y[] emptyBoundary = new Rectangle4y[EmptyImage.values().length];

	private static BufferedImage[] images = new BufferedImage[FoodType.values().length];
	private static Rectangle4y[] boundary = new Rectangle4y[FoodType.values().length];

	private static BufferedImage shadowImages;
	/**
	 * 画像ロード
	 * <br>空のエサ皿→餌入りのエサ皿→影　の順に読み込み
	 */
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		
		for(EmptyImage i :EmptyImage.values()) {
			if(i.fileName == null) continue;

			emptyImages[i.ordinal()] = ModLoader.loadItemImage(loader, "food" + File.separator + i.fileName);
			emptyBoundary[i.ordinal()] = new Rectangle4y();
			emptyBoundary[i.ordinal()].width = emptyImages[i.ordinal()].getWidth(io);
			emptyBoundary[i.ordinal()].height = emptyImages[i.ordinal()].getHeight(io);
			emptyBoundary[i.ordinal()].x = emptyBoundary[i.ordinal()].width >> 1;
			emptyBoundary[i.ordinal()].y = emptyBoundary[i.ordinal()].height - 1;
		}

		for(FoodType i :FoodType.values()) {
			if(i.fileName == null) continue;

			images[i.ordinal()] = ModLoader.loadItemImage(loader, "food" + File.separator + i.fileName);
			boundary[i.ordinal()] = new Rectangle4y();
			boundary[i.ordinal()].width = images[i.ordinal()].getWidth(io);
			boundary[i.ordinal()].height = images[i.ordinal()].getHeight(io);
			boundary[i.ordinal()].x = boundary[i.ordinal()].width >> 1;
			boundary[i.ordinal()].y = boundary[i.ordinal()].height - 1;
		}

		shadowImages = ModLoader.loadItemImage(loader, "food" + File.separator + "gohan_shadow.png");

	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (isEmpty()) {
			layer[0] = emptyImages[foodType.emptyImg.ordinal()];
		} else {
			layer[0] = images[foodType.ordinal()];	
		}
		return 1;
	}
	/** 影イメージの取得 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return shadowImages;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary[0];
	}

	/**餌ごとにサイズが違うので専用メソッド化*/
	public static Rectangle4y getFoodBounding(FoodType type) {
		return boundary[type.ordinal()];
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().food.remove(objId);
	}

	@Override
	public int getValue() {
		return foodType.value;
	}

	@Override
	public int getLooks() {
		return foodType.looks;
	}
	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Food(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		// 森なら野生に変更
		if( SimYukkuri.world.getCurrentMap().mapIndex == 5 ||  SimYukkuri.world.getCurrentMap().mapIndex == 6 ){
			foodType = FoodType.values()[initOption];
			switch(foodType){
				case SWEETS1:
					foodType = FoodType.SWEETS_YASEI1;
					break;
				case SWEETS2:
					foodType = FoodType.SWEETS_YASEI2;
					break;
				case STALK:
					break;
				case FOOD:
					foodType = FoodType.FOOD_YASEI;
					break;
				case BITTER:
					foodType = FoodType.BITTER_YASEI;
					break;
				case LEMONPOP:
					foodType = FoodType.LEMONPOP_YASEI;
					break;
				case HOT:
					foodType = FoodType.HOT_YASEI;
					break;
				case VIYUGRA:
					foodType = FoodType.VIYUGRA_YASEI;
					break;
				case WASTE:
					foodType = FoodType.WASTE_YASEI;
					break;
				default:
					break;
			}
		}else{
			foodType = FoodType.values()[initOption];
		}
		amount = foodType.amount;
		setBoundary(boundary[foodType.ordinal()]);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().food.put(objId, this);
		objType = Type.OBJECT;
		objEXType = ObjEXType.FOOD;
		setRemoved(false);
	}
	public Food() {
		
	}
	
	
	/**フードタイプ取得*/
	public FoodType getFoodType() {
		return foodType;
	}
	/**空かどうか*/
	public boolean isEmpty() {
		return (amount == 0);
	}
	/**餌を食べられる処理*/
	public void eatFood(int eatAmount)
	{
		if (isEmpty()) {
			return;
		}
		amount -= eatAmount;
		if (amount < 0) {
			amount = 0;
		}
	}
	
	@Override
	public void kick() {
		kick(0,  -8,  -4);
	}
}


