package src.command;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.item.AutoFeeder;
import src.item.Bed;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.Food;
import src.item.FoodMaker;
import src.item.GarbageChute;
import src.item.GarbageStation;
import src.item.Generator;
import src.item.HotPlate;
import src.item.House;
import src.item.MachinePress;
import src.item.Mixer;
import src.item.OrangePool;
import src.item.ProcesserPlate;
import src.item.ProductChute;
import src.item.StickyPlate;
import src.item.Stone;
import src.item.Sui;
import src.item.Toilet;
import src.item.Toy;
import src.item.Trampoline;
import src.item.Trash;
import src.item.Yunba;
import src.system.IconPool;
import src.system.MainCommandUI;
import src.system.ResourceUtil;

/**********************************************
 * 各種コマンドメニューのまとめ
 * Ctrl+Shift+Fでのフォーマット禁止
 */
public class GadgetMenu {

	/** ガジェットの効果対象*/
	public static enum ActionTarget {
		/** コマンド選択時点で実行 */
		IMMEDIATE(1),
		/** ゆっくりのみ有効 */
		BODY(2),
		/** アイテムのみ有効 */
		GADGET(4),
		/** ゆっくり、アイテムとも有効 */
		BODY_AND_GADGET(6),
		/** 何も無いところが有効 */
		TERRAIN(8),
		/** 壁にのみ有効 */
		WALL(16),
		/** シェイプにのみ有効 */
		FIELD(32),
		/** 何も無いところが有効 */
		TERRAIN_AND_GADET(12),
		;

		private int flag;

		ActionTarget(int flg) {
			this.flag = flg;
		}

		public int getFlag() {
			return this.flag;
		}
	}

	/**マウスアクションの種類*/
	public static enum ActionControl {
		/** 左クリック */
		LEFT_CLICK,
		/** 複数回の左クリックで1アクションを完結 */
		LEFT_MULTI_CLICK,
		/** 左ドラッグ */
		LEFT_DRAG,
	}

	/**右メニューンp最上位階層のカテゴライズの種類*/
	public static enum MainCategoryName {
		/** メイン */
		MAIN,
		/** 道具 */
		TOOL,
		/** 道具2 */
		TOOL2,
		/** アンプル */
		AMPOULE,
		/** えさ */
		FOODS,
		/** 清掃 */
		CLEAN,
		/** おかざり */
		ACCESSORY,
		/** おくるみ */
		PANTS,
		/** 床設置 */
		FLOOR,
		/** フィールド */
		BARRIER,
		/** おもちゃ */
		TOYS,
		/** ベルトコンベア */
		CONVEYOR,
		/** 声掛け */
		VOICE,
		/** テスト */
		TEST,
	}

	/** サブアクション用のヘルプコンテキスト
	 *  %mlb : マウス左ボタン
	 *  %mrb : マウス右ボタン
	 *  %sft : SHIFTキー
	 *  %ctl : CTRLキー*/
	public static enum HelpContext {
		SHIFT_LMB_ALL(ResourceUtil.getInstance().read("command_lmb_all")), 
		SHIFT_LMB_ALL_ONOFF(ResourceUtil.getInstance().read("command_lmb_onoff")),
		CTRL_LMB_ALL_INVERT(ResourceUtil.getInstance().read("command_lmb_invert")),
				;

		private String name;

		HelpContext(String nameJ) {
			this.name = nameJ;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/** 置き換え文字列 icon[]と対応*/
	public static enum HelpIcon {
		mlb(1, 16), mrb(2, 16), sft(3, 32), ctl(0, 32),
		;

		private int imageIndex;
		private int width;

		HelpIcon(int idx, int w) {
			this.imageIndex = idx;
			this.width = w;
		}

		public int getImageIndex() {
			return imageIndex;
		}

		public int getW() {
			return width;
		}
	}

	/** 全メニュー項目*/
	public static enum GadgetList {
		// メインカテゴリ
		TOOL(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_item"), null, 0, null, null, null, null), 
		TOOL2(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_item2"), null, 0,null, null, null, null),
		AMPOULE(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_ampoule"), null, 0, null, null, null, null), 
		FOODS(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_food"), null, 0, null, null, null,null),
		CLEAN(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_clean"), null, 0, null, null, null, null),
		ACCESSORY(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_accessory"), null, 0, null, null, null,null),
		PANTS(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_sweater"), null, 0, null, null, null, null), 
		FLOOR(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_floor"), null, 0, null, null, null,null),
		BARRIER(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_field"), null, 0, null, null, null,null),
		TOYS(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_toys"), null, 0, null, null, null,null),
		CONVEYOR(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_belcon"), null, 0, null,null, null, null),
		VOICE(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_approach"),null, 0, null, null, null,null),
		TEST(MainCategoryName.MAIN, ResourceUtil.getInstance().read("command_test"), null,0, null, null, null, null),
		// ツールカテゴリ
		PUNISH(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_chastisement"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL, null),
		SNAPPING(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_decopin"), null, 0,ActionTarget.BODY_AND_GADGET, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL,null),
		PICKUP(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_flyhigh"), null, 0, ActionTarget.BODY_AND_GADGET,ActionControl.LEFT_DRAG, null, null),
		HOLD(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_hold"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, null,null),
		SURISURI(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_suri"), null, 0, ActionTarget.BODY,ActionControl.LEFT_DRAG, null, null),
		VIBRATOR(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_vibe"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL, null), 
		PENICUT(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_cutpeni"), null, 0, ActionTarget.BODY,ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL,null),
		JUICE(MainCategoryName.TOOL, ResourceUtil.getInstance().read("command_juice"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL,null),
		Medical_JUICE(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_orangejuice"), null, 0, ActionTarget.BODY,ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL,null),
		LEMON_SPLAY(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_ramune"), null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL,null),
		Pheromone_SPLAY(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_phero"), null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL,null),
		HAMMER(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_hammer"), null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL,null), 
		GATHERINJECTINTO(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_inject"),null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		DRIPSPERM(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_drip"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		PUNCH(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_hit"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		GODHAND(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_godhand"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		PEAL(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_peal"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		Blind(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_eyeball"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,	null,null),
		SHUTMOUTH(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_mouthshut"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		HAIRCUT(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_hage"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		PACK(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_manju"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		STOMP(MainCategoryName.TOOL,ResourceUtil.getInstance().read("command_crush"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		// ツールカテゴリ2
		BRAID_PLUCK(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_nopico"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,	HelpContext.SHIFT_LMB_ALL, null),
		ANAL_CLOSE(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_anal"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		STALK_CUT(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_stalkinfer"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		CASTRATION(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_babyinfer"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		STALK_UNPLUG(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_stalkpull"),null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK, null,null), 
		LIGHTER(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_lighter"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL, null),
		WATER(MainCategoryName.TOOL2,ResourceUtil.getInstance().read("command_water"), null, 0, ActionTarget.BODY,ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL,null),
		NEEDLE(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_needle"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		BURY(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_bury"), null,0, ActionTarget.BODY,ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT), 
		SET_SICK(MainCategoryName.TOOL2, ResourceUtil.getInstance().read("command_mold"),null, 0, ActionTarget.BODY,ActionControl.LEFT_CLICK, null,null),
		SET_RAPER(MainCategoryName.TOOL2,ResourceUtil.getInstance().read("command_raper"), null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null, null),
		//	 アンプルカテゴリ
		ORANGE_AMP(MainCategoryName.AMPOULE, ResourceUtil.getInstance().read("item_orange"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF, HelpContext.CTRL_LMB_ALL_INVERT),
		ACCEL_AMP(MainCategoryName.AMPOULE,ResourceUtil.getInstance().read("item_accell_ampoule"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT), 
		STOP_AMP(MainCategoryName.AMPOULE, ResourceUtil.getInstance().read("item_stop"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		HUNGRY_AMP(MainCategoryName.AMPOULE, ResourceUtil.getInstance().read("item_hungry"), null,0, ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		VERYSHIT_AMP(MainCategoryName.AMPOULE,ResourceUtil.getInstance().read("item_veryshit"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		POISON_AMP(MainCategoryName.AMPOULE,ResourceUtil.getInstance().read("item_poison"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT), 
		BREEDING_AMP(MainCategoryName.AMPOULE, ResourceUtil.getInstance().read("item_breeding"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		ANYD_AMP(MainCategoryName.AMPOULE, ResourceUtil.getInstance().read("item_anti_nyd"), null,0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF,HelpContext.CTRL_LMB_ALL_INVERT),
		// えさカテゴリ
		NORMAL(MainCategoryName.FOODS,ResourceUtil.getInstance().read("command_food_normal"), Food.class, 3, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		BITTER(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_bitter"), Food.class, 4, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null, null),
		LEMON_POP(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_ramune"), Food.class, 5,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null, null),
		HOT(MainCategoryName.FOODS,ResourceUtil.getInstance().read("command_food_hot"), Food.class, 6, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		VIYUGRA(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_viagra"), Food.class, 7,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		SWEETS1(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_sweet1"), Food.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		SWEETS2(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_sweet2"), Food.class,1, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		WASTE(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_garbage"), Food.class,8, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		AUTO(MainCategoryName.FOODS, ResourceUtil.getInstance().read("command_food_auto"),AutoFeeder.class, 0,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null, null),
		// 清掃カテゴリ
		INDIVIDUAL(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_per"), null, 0, ActionTarget.BODY_AND_GADGET, ActionControl.LEFT_CLICK, null,null),
		YU_CLEAN(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_yuclean"), null, 0, ActionTarget.IMMEDIATE,ActionControl.LEFT_CLICK, null, null),
		BODY(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_body"), null, 0,ActionTarget.IMMEDIATE, ActionControl.LEFT_CLICK, null,null),
		SHIT(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_shit"), null, 0, ActionTarget.IMMEDIATE,ActionControl.LEFT_CLICK, null, null),
		ETC(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_etc"), null,0, ActionTarget.IMMEDIATE, ActionControl.LEFT_CLICK, null,null),
		ALL(MainCategoryName.CLEAN, ResourceUtil.getInstance().read("command_clean_all"), null, 0,ActionTarget.IMMEDIATE, ActionControl.LEFT_CLICK, null, null),
		// おかざりカテゴリ
		OKAZARI_HIDE(MainCategoryName.ACCESSORY, ResourceUtil.getInstance().read("command_okazari"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF, HelpContext.CTRL_LMB_ALL_INVERT),
		// おくるみカテゴリ
		PANTS_NORMAL(MainCategoryName.PANTS, ResourceUtil.getInstance().read("command_okurumi"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF, HelpContext.CTRL_LMB_ALL_INVERT),
		// 床設置カテゴリ
		TOILET(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_toilet"), Toilet.class, 0, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		BED(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_bed"), Bed.class, 0, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK,null, null),
		STICKY_PLATE(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_sticky"), StickyPlate.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		HOT_PLATE(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_hotplate"), HotPlate.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		PROCESSER_PLATE(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_process"), ProcesserPlate.class,0, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		FOOD_MAKER(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_foodmaker"), FoodMaker.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		MIXER(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_mixer"), Mixer.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		DIFFUSER(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_diffuser"),Diffuser.class, 0,ActionTarget.TERRAIN_AND_GADET,ActionControl.LEFT_CLICK, null,null),
		ORANGE_POOL(MainCategoryName.FLOOR,ResourceUtil.getInstance().read("command_floor_orangepool"), OrangePool.class, 0,ActionTarget.TERRAIN_AND_GADET,ActionControl.LEFT_CLICK, null,null),
		BREED_POOL(MainCategoryName.FLOOR, ResourceUtil.getInstance().read("command_floor_cultivation"),BreedingPool.class, 0,ActionTarget.TERRAIN_AND_GADET,ActionControl.LEFT_CLICK, null,null),
		GARBAGE_CHUTE(MainCategoryName.FLOOR,ResourceUtil.getInstance().read("command_floor_dustchute"),GarbageChute.class, 0,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK,null,null),
		MACHINE_PRESS(MainCategoryName.FLOOR,ResourceUtil.getInstance().read("command_floor_press"),MachinePress.class,0,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK,null,null), 
		GENERATOR(MainCategoryName.FLOOR,ResourceUtil.getInstance().read("command_floor_generator"),Generator.class,0,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK,null,null), 
		PRODUCT_CHUTE(MainCategoryName.FLOOR,ResourceUtil.getInstance().read("command_floor_product"),ProductChute.class,0,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK,null,null),
		// フィールドカテゴリ
		GAP_MINI(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_dansashou"), null, 1, ActionTarget.TERRAIN, ActionControl.LEFT_MULTI_CLICK, null,null),
		GAP_BIG(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_dansadai"), null, 1, ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK, null, null),
		NET_MINI(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_wireshou"), null,1, ActionTarget.TERRAIN, ActionControl.LEFT_MULTI_CLICK, null,null),
		NET_BIG(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_wiredai"), null, 1, ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK, null, null),
		WALL(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_wall"),null, 1, ActionTarget.TERRAIN, ActionControl.LEFT_MULTI_CLICK, null,null),
		ITEM(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_noitem"), null, 1,ActionTarget.TERRAIN, ActionControl.LEFT_MULTI_CLICK, null,null),
		NoUNUN(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_noitemunun"), null, 1,ActionTarget.TERRAIN, ActionControl.LEFT_MULTI_CLICK,null, null),
		KEKKAI(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_kekkai"),null, 1, ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK, null,null),
		POOL(MainCategoryName.BARRIER, ResourceUtil.getInstance().read("command_field_pond"), null,2, ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK, null,null),
		FARM(MainCategoryName.BARRIER,ResourceUtil.getInstance().read("command_field_hatake"), null, 3,ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK,null, null),
		BELTCONVEYOR(MainCategoryName.BARRIER,ResourceUtil.getInstance().read("command_belcon"), null, 4,ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK,null,null),
		WALL_DELETE(MainCategoryName.BARRIER,ResourceUtil.getInstance().read("command_field_removewall"), null, 0,ActionTarget.WALL,ActionControl.LEFT_CLICK,null,null),
		FIELD_DELETE(MainCategoryName.BARRIER,ResourceUtil.getInstance().read("command_field_removefield"),null, 0,ActionTarget.FIELD,ActionControl.LEFT_CLICK,null,null), 
		ALL_DELETE(MainCategoryName.BARRIER,ResourceUtil.getInstance().read("command_field_removeall"),null,0,ActionTarget.IMMEDIATE,ActionControl.LEFT_CLICK,null,null),
		// おもちゃカテゴリ
		BALL(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_ball"), Toy.class, 0, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		YUNBA(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_yunba"), Yunba.class, 0, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null, null),
		YUNBA_SETUP(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_yunbasettings"), null, 0,ActionTarget.GADGET, ActionControl.LEFT_CLICK, null, null), 
		SUI(MainCategoryName.TOYS,ResourceUtil.getInstance().read("command_toys_suii"), Sui.class, 0, ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		TRASH(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_junk"), Trash.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		TRAMPOLINE(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_trampolin"), Trampoline.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		STONE(MainCategoryName.TOYS, ResourceUtil.getInstance().read("command_toys_stone"), Stone.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null),
		// コンベアカテゴリ
		BELTCONVEYOR_CUSTOM(MainCategoryName.CONVEYOR, ResourceUtil.getInstance().read("command_conveyer_custom"), null, 5, ActionTarget.TERRAIN,ActionControl.LEFT_MULTI_CLICK, null, null),
		BELTCONVEYOR_SETUP(MainCategoryName.CONVEYOR, ResourceUtil.getInstance().read("command_conveyer_changesettings"), null,5, ActionTarget.GADGET, ActionControl.LEFT_CLICK, null, null),
		// 声掛け
		YUKKURISITEITTENE(MainCategoryName.VOICE, ResourceUtil.getInstance().read("command_say_takeiteasy"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,HelpContext.SHIFT_LMB_ALL_ONOFF, null),
		YUKKURIDIE(MainCategoryName.VOICE, ResourceUtil.getInstance().read("command_say_dropdeadeasy"), null, 0,ActionTarget.BODY, ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF,null),
		YUKKURIFURIFURI(MainCategoryName.VOICE, ResourceUtil.getInstance().read("command_say_morunmorun"), null, 0, ActionTarget.BODY,ActionControl.LEFT_CLICK, HelpContext.SHIFT_LMB_ALL_ONOFF, null),
		// テストコマンド
		RANKSET(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_pick"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK, null,null),
		RANKSET2(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_ununslave"), null, 0, ActionTarget.BODY, ActionControl.LEFT_CLICK,null, null),
		GARBAGE_STATION(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_garbagepoint"), GarbageStation.class, 0,ActionTarget.TERRAIN, ActionControl.LEFT_CLICK, null,null), 
		BED_NORA(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_bednora"), Bed.class, 1, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null, null), 
		TOILET_NORA(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_toiletnora"), Toilet.class, 1, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null, null),
		HOUSE_NORA(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_homenora"), House.class, 1, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		GARBAGE_NORA(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_dustnora"),GarbageChute.class, 1, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		ORANGE_NORA(MainCategoryName.TEST, ResourceUtil.getInstance().read("command_test_orangenora"),OrangePool.class, 1, ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		STICKY_NORA(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_stickynora"), StickyPlate.class, 1,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		TOY_NORA(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_toynora"), Toy.class, 1,ActionTarget.TERRAIN,ActionControl.LEFT_CLICK, null,null),
		REMOVEALL(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_exterm"), null, 0,ActionTarget.IMMEDIATE,ActionControl.LEFT_CLICK,null, null),
		EVENT_SHIT(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_ununtaiso"),null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		EVENT_EAT(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_super"),null, 0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		EVENT_RIDEYUKKURI(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_ochibihakobi"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		EVENT_PROUDCHILD(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_ochibijiman"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		SETVAIN(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_vain"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null), 
		Yunnyaa(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_yunyaaa"),	null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		BEGGINGFORLIFE(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_beg"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		PREDATORSGAME(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_playofpredetor"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		INVITEANTS(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_ants"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		FEED(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_forcefeed"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		BADGE(MainCategoryName.TEST,ResourceUtil.getInstance().read("item_badge"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,	null,null),
		DEBUG2(MainCategoryName.TEST,ResourceUtil.getInstance().read("command_test_pastime"),null,0,ActionTarget.BODY,ActionControl.LEFT_CLICK,null,null),
		;

		private MainCategoryName group;
		private String displayName;
		private Class<?> gadgetClass;
		private int initOption;
		private ActionTarget actionTarget;
		private ActionControl actionControl;
		private HelpContext help1;
		private HelpContext help2;

		GadgetList(MainCategoryName grp, String str, Class<?> cls, int opt, ActionTarget tgt,
				ActionControl ctl, HelpContext h1, HelpContext h2) {
			this.group = grp;
			this.displayName = str;
			this.gadgetClass = cls;
			this.initOption = opt;
			this.actionTarget = tgt;
			this.actionControl = ctl;
			this.help1 = h1;
			this.help2 = h2;
		}

		public MainCategoryName getGroup() {
			return this.group;
		}

		public String getDisplayName() {
			return this.displayName;
		}

		public Class<?> getGadgetClass() {
			return this.gadgetClass;
		}

		public int getInitOption() {
			return this.initOption;
		}

		public ActionTarget getActionTarget() {
			return this.actionTarget;
		}

		public ActionControl getActionControl() {
			return this.actionControl;
		}

		public HelpContext getHelp1() {
			return this.help1;
		}

		public HelpContext getHelp2() {
			return this.help2;
		}

		@Override
		public String toString() {
			return this.displayName;
		}
	}

	/**メインカテゴリボタンの種類*/
	public static GadgetList[] MainCategory = {
			GadgetList.TOOL,
			GadgetList.TOOL2,
			GadgetList.AMPOULE,
			GadgetList.FOODS,
			GadgetList.CLEAN,
			GadgetList.ACCESSORY,
			GadgetList.PANTS,
			GadgetList.FLOOR,
			GadgetList.BARRIER,
			GadgetList.TOYS,
			GadgetList.CONVEYOR,
			GadgetList.VOICE,
			GadgetList.TEST
	};
	/**メインカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> mainModel = new DefaultComboBoxModel<GadgetList>(MainCategory);

	/**道具カテゴリボタンの種類*/
	public static GadgetList[] ToolCategory = {
			GadgetList.PUNISH,
			GadgetList.SNAPPING,
			GadgetList.PICKUP,
			GadgetList.HOLD,
			GadgetList.SURISURI,
			GadgetList.VIBRATOR,
			GadgetList.PENICUT,
			GadgetList.JUICE,
			GadgetList.Medical_JUICE,
			GadgetList.LEMON_SPLAY,
			GadgetList.Pheromone_SPLAY,
			GadgetList.HAMMER,
			GadgetList.GATHERINJECTINTO,
			GadgetList.DRIPSPERM,
			GadgetList.PUNCH,
			GadgetList.PEAL,
			GadgetList.Blind,
			GadgetList.SHUTMOUTH,
			GadgetList.HAIRCUT,
			GadgetList.PACK,
			GadgetList.STOMP,
			GadgetList.GODHAND
	};
	/**道具カテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> toolModel = new DefaultComboBoxModel<GadgetList>(ToolCategory);

	/**道具2カテゴリボタンの種類*/
	public static GadgetList[] ToolCategory2 = {
			GadgetList.BRAID_PLUCK,
			GadgetList.ANAL_CLOSE,
			GadgetList.STALK_CUT,
			GadgetList.CASTRATION,
			GadgetList.STALK_UNPLUG,
			GadgetList.LIGHTER,
			GadgetList.NEEDLE,
			GadgetList.WATER,
			GadgetList.BURY,
			GadgetList.SET_SICK,
			GadgetList.SET_RAPER,
	};
	/**道具2カテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> toolModel2 = new DefaultComboBoxModel<GadgetList>(ToolCategory2);

	/**アンプルカテゴリボタンの種類*/
	public static GadgetList[] AmpouleCategory = {
			GadgetList.ORANGE_AMP,
			GadgetList.ACCEL_AMP,
			GadgetList.STOP_AMP,
			GadgetList.HUNGRY_AMP,
			GadgetList.VERYSHIT_AMP,
			GadgetList.POISON_AMP,
			GadgetList.BREEDING_AMP,
			GadgetList.ANYD_AMP,
	};
	/**アンプルカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> AmpouleModel = new DefaultComboBoxModel<GadgetList>(AmpouleCategory);

	/**えさカテゴリボタンの種類*/
	public static GadgetList[] FoodCategory = {
			GadgetList.NORMAL,
			GadgetList.BITTER,
			GadgetList.LEMON_POP,
			GadgetList.HOT,
			GadgetList.VIYUGRA,
			GadgetList.SWEETS1,
			GadgetList.SWEETS2,
			GadgetList.WASTE,
			GadgetList.AUTO
	};
	/**えさカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> foodModel = new DefaultComboBoxModel<GadgetList>(FoodCategory);

	/**清掃カテゴリボタンの種類*/
	public static GadgetList[] CleanCategory = {
			GadgetList.INDIVIDUAL,
			GadgetList.YU_CLEAN,
			GadgetList.BODY,
			GadgetList.SHIT,
			GadgetList.ETC,
			GadgetList.ALL
	};
	/**清掃カテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> cleanModel = new DefaultComboBoxModel<GadgetList>(CleanCategory);

	/**おかざりカテゴリボタンの種類*/
	public static GadgetList[] OkazariCategory = {
			GadgetList.OKAZARI_HIDE,
	};
	/**おかざりカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> okazariModel = new DefaultComboBoxModel<GadgetList>(OkazariCategory);

	/**おくるみカテゴリボタンの種類*/
	public static GadgetList[] PantsCategory = {
			GadgetList.PANTS_NORMAL,
	};
	/**おくるみカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> pantsModel = new DefaultComboBoxModel<GadgetList>(PantsCategory);

	/**床設置カテゴリボタンの種類*/
	public static GadgetList[] FloorCategory = {
			GadgetList.TOILET,
			GadgetList.BED,
			GadgetList.STICKY_PLATE,
			GadgetList.HOT_PLATE,
			GadgetList.PROCESSER_PLATE,
			GadgetList.FOOD_MAKER,
			GadgetList.MIXER,
			GadgetList.DIFFUSER,
			GadgetList.ORANGE_POOL,
			GadgetList.BREED_POOL,
			GadgetList.GARBAGE_CHUTE,
			GadgetList.MACHINE_PRESS,
			GadgetList.GENERATOR,
			GadgetList.PRODUCT_CHUTE
	};
	/**床設置カテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> floorModel = new DefaultComboBoxModel<GadgetList>(FloorCategory);

	/**フィールドカテゴリボタンの種類*/
	public static GadgetList[] BarrierCategory = {
			GadgetList.GAP_MINI,
			GadgetList.GAP_BIG,
			GadgetList.NET_MINI,
			GadgetList.NET_BIG,
			GadgetList.WALL,
			GadgetList.ITEM,
			GadgetList.NoUNUN,
			GadgetList.KEKKAI,
			GadgetList.POOL,
			GadgetList.FARM,
			GadgetList.BELTCONVEYOR,
			GadgetList.WALL_DELETE,
			GadgetList.FIELD_DELETE,
			GadgetList.ALL_DELETE
	};
	/**フィールド用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> barrierModel = new DefaultComboBoxModel<GadgetList>(BarrierCategory);

	/**おもちゃカテゴリボタンの種類*/
	public static GadgetList[] ToysCategory = {
			GadgetList.BALL,
			GadgetList.YUNBA,
			GadgetList.YUNBA_SETUP,
			GadgetList.SUI,
			GadgetList.TRASH,
			GadgetList.TRAMPOLINE,
			GadgetList.STONE
	};
	/**おもちゃ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> toyModel = new DefaultComboBoxModel<GadgetList>(ToysCategory);

	/**コンベアカテゴリボタンの種類*/
	public static GadgetList[] ConveyorCategory = {
			GadgetList.BELTCONVEYOR_CUSTOM,
			GadgetList.BELTCONVEYOR_SETUP
	};
	/**コンベアカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> conveyorModel = new DefaultComboBoxModel<GadgetList>(
			ConveyorCategory);

	/**声かけカテゴリボタンの種類*/
	public static GadgetList[] VoiceCategory = {
			GadgetList.YUKKURISITEITTENE,
			GadgetList.YUKKURIDIE,
			GadgetList.YUKKURIFURIFURI
	};
	/**声かけ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> voiceModel = new DefaultComboBoxModel<GadgetList>(VoiceCategory);

	/**テストカテゴリボタンの種類*/
	public static GadgetList[] TestCategory = {
			GadgetList.RANKSET,
			GadgetList.RANKSET2,
			GadgetList.GARBAGE_STATION,
			GadgetList.BED_NORA,
			GadgetList.TOILET_NORA,
			GadgetList.HOUSE_NORA,
			GadgetList.GARBAGE_NORA,
			GadgetList.ORANGE_NORA,
			GadgetList.STICKY_NORA,
			GadgetList.TOY_NORA,
			GadgetList.REMOVEALL,
			GadgetList.EVENT_SHIT,
			GadgetList.EVENT_EAT,
			GadgetList.EVENT_RIDEYUKKURI,
			GadgetList.EVENT_PROUDCHILD,
			GadgetList.SETVAIN,
			GadgetList.Yunnyaa,
			GadgetList.BEGGINGFORLIFE,
			GadgetList.PREDATORSGAME,
			GadgetList.INVITEANTS,
			GadgetList.FEED,
			GadgetList.BADGE,
			GadgetList.DEBUG2
	};
	/**テストカテゴリ用コンボボックス定義*/
	public static DefaultComboBoxModel<GadgetList> testModel = new DefaultComboBoxModel<GadgetList>(TestCategory);

	/**速度の種類定義*/
	public static enum GameSpeed {
		PAUSE(ResourceUtil.getInstance().read("command_gamespeed_pause"), "Pause"), 
		X1(ResourceUtil.getInstance().read("command_gamespeed_one"), "Speed: x1"), 
		X2(ResourceUtil.getInstance().read("command_gamespeed_two"), "Speed: x2"), 
		X5(ResourceUtil.getInstance().read("command_gamespeed_five"),"Speed: x5"),
		X10(ResourceUtil.getInstance().read("command_gamespeed_ten"), "Speed: x10"),
		MAX(ResourceUtil.getInstance().read("command_gamespeed_max"), "Speed: Max"),
				;

		private String name;

		GameSpeed(String nameJ, String nameE) {
			this.name = nameJ;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**右上の真ん中のウィンドウ
	 * <br>初期は”道具”となってるところ*/
	public static GadgetList selectMain = GadgetList.TOOL;
	/**右上の一番下のウィンドウ
	 * <br>初期は”おしおき”となってるところ*/
	public static GadgetList selectSub = GadgetList.PUNISH;

	/**ポップアップ汎用*/
	public static JPopupMenu popup = new JPopupMenu();
	/**ポップアップが開いているかどうか*/
	public static boolean popupDisplay = false;
	/** ヘルプの番号 */
	public static int currentHelpNum = 0;
	/** ヘルプのバッファ */
	public static String[][] currentHelpBuf = null;
	/** ヘルプアイコン */
	public static HelpIcon[][] currentHelpIcon = null;
	/** ヘルプの幅・高さ */
	public static int helpW, helpH;

	/**アイコンの画像の入れ物*/
	private static BufferedImage[] icon;
	/**ポップアップのインスタンス(?)*/
	private static PopupAction action = new PopupAction();
	/** ヘルプ */
	private static HelpContext[] currentHelp = new HelpContext[4];

	/**最新状態の右上の、中と下のウィンドウの表示のゲッター*/
	public static final GadgetList getCurrentGadget() {
		if (selectSub != null)
			return selectSub;
		else if (selectMain != null)
			return selectMain;
		return null;
	}

	/**
	 * ヘルプアイコンを取得する.
	 * @param str ヘルプアイコンを表す文字列
	 * @return ヘルプアイコン
	 */
	public static final HelpIcon getHelpIcon(String str) {
		if (str.indexOf("%") != 0)
			return null;
		return HelpIcon.valueOf(str.substring(1));
	}

	/**
	 * ヘルプアイコンのイメージを取得する.
	 * @param help ヘルプアイコン
	 * @return ヘルプアイコンのイメージ
	 */
	public static final Image getHelpIconImage(HelpIcon help) {
		return icon[help.getImageIndex()];
	}

	/**カテゴリーのポップアップを作る*/
	public static final void createPopupMenu() {
		icon = IconPool.getHelpIconImageArray();
		popup.add(createSubGroup(MainCategory[0], ToolCategory));
		popup.add(createSubGroup(MainCategory[1], ToolCategory2));
		popup.add(createSubGroup(MainCategory[2], AmpouleCategory));
		popup.add(createSubGroup(MainCategory[3], FoodCategory));
		popup.add(createSubGroup(MainCategory[4], CleanCategory));
		popup.add(createSubGroup(MainCategory[5], OkazariCategory));
		popup.add(createSubGroup(MainCategory[6], PantsCategory));
		popup.add(createSubGroup(MainCategory[7], FloorCategory));
		popup.add(createSubGroup(MainCategory[8], BarrierCategory));
		popup.add(createSubGroup(MainCategory[9], ToysCategory));
		popup.add(createSubGroup(MainCategory[10], ConveyorCategory));
		popup.add(createSubGroup(MainCategory[11], VoiceCategory));
		popup.add(createSubGroup(MainCategory[12], TestCategory));
	}

	/**個別動作のポップアップを作る*/
	private static final JMenu createSubGroup(GadgetList root, GadgetList[] group) {
		JMenu ret;
		JMenuItem subMenu;

		ret = new JMenu(root.getDisplayName());
		int size = group.length;
		for (int i = 0; i < size; i++) {
			subMenu = new JMenuItem(group[i].getDisplayName());
			subMenu.addActionListener(action);
			subMenu.setActionCommand(group[i].name());
			ret.add(subMenu);
		}
		return ret;
	}

	/**
	 * 選択グループのサブリストへ更新
	 * @param mainSel メインセル
	 * @param subSel サブセル
	 */
	@SuppressWarnings("unchecked")
	public static final void setSelectCategory(GadgetList mainSel, int subSel) {

		synchronized (SimYukkuri.lock) {
			switch (mainSel) {
			case TOOL:
				MainCommandUI.subItemCombo.setModel(toolModel);
				GadgetMenu.selectSub = GadgetMenu.ToolCategory[subSel];
				break;
			case TOOL2:
				MainCommandUI.subItemCombo.setModel(toolModel2);
				GadgetMenu.selectSub = GadgetMenu.ToolCategory2[subSel];
				break;
			case AMPOULE:
				MainCommandUI.subItemCombo.setModel(AmpouleModel);
				GadgetMenu.selectSub = GadgetMenu.AmpouleCategory[subSel];
				break;
			case FOODS:
				MainCommandUI.subItemCombo.setModel(foodModel);
				GadgetMenu.selectSub = GadgetMenu.FoodCategory[subSel];
				break;
			case CLEAN:
				MainCommandUI.subItemCombo.setModel(cleanModel);
				GadgetMenu.selectSub = GadgetMenu.CleanCategory[subSel];
				break;
			case ACCESSORY:
				MainCommandUI.subItemCombo.setModel(okazariModel);
				GadgetMenu.selectSub = GadgetMenu.OkazariCategory[subSel];
				break;
			case PANTS:
				MainCommandUI.subItemCombo.setModel(pantsModel);
				GadgetMenu.selectSub = GadgetMenu.PantsCategory[subSel];
				break;
			case FLOOR:
				MainCommandUI.subItemCombo.setModel(floorModel);
				GadgetMenu.selectSub = GadgetMenu.FloorCategory[subSel];
				break;
			case BARRIER:
				MainCommandUI.subItemCombo.setModel(barrierModel);
				GadgetMenu.selectSub = GadgetMenu.BarrierCategory[subSel];
				break;
			case TOYS:
				MainCommandUI.subItemCombo.setModel(toyModel);
				GadgetMenu.selectSub = GadgetMenu.ToysCategory[subSel];
				break;
			case CONVEYOR:
				MainCommandUI.subItemCombo.setModel(conveyorModel);
				GadgetMenu.selectSub = GadgetMenu.ConveyorCategory[subSel];
				break;
			case VOICE:
				MainCommandUI.subItemCombo.setModel(voiceModel);
				GadgetMenu.selectSub = GadgetMenu.VoiceCategory[subSel];
				break;
			case TEST:
				MainCommandUI.subItemCombo.setModel(testModel);
				GadgetMenu.selectSub = GadgetMenu.TestCategory[subSel];
				break;
			default:
				break;
			}
			MainCommandUI.subItemCombo.setSelectedIndex(subSel);
		}
	}

	/** 選択コマンドのヘルプを設定*/
	public static final void setActionHelp(GadgetList item) {

		HelpContext help;
		currentHelpNum = 0;
		help = item.getHelp1();
		if (help != null) {
			currentHelp[currentHelpNum] = help;
			currentHelpNum++;
		}
		help = item.getHelp2();
		if (help != null) {
			currentHelp[currentHelpNum] = help;
			currentHelpNum++;
		}
		createHelpBuffer();
	}

	/**ヘルプのポップアップを作る実行部*/
	private static final void createHelpBuffer() {
		if (currentHelpNum == 0) {
			currentHelpBuf = null;
			currentHelpIcon = null;
			return;
		}
		currentHelpBuf = new String[currentHelpNum][];
		currentHelpIcon = new HelpIcon[currentHelpNum][];
		helpW = 5;
		helpH = 16 * currentHelpNum + 4;
		for (int i = 0; i < currentHelpNum; i++) {
			currentHelpBuf[i] = currentHelp[i].toString().split(",");
			currentHelpIcon[i] = new HelpIcon[currentHelpBuf[i].length];
			int tmpW = 0;

			for (int j = 0; j < currentHelpBuf[i].length; j++) {
				currentHelpIcon[i][j] = getHelpIcon(currentHelpBuf[i][j]);
				if (currentHelpIcon[i][j] != null) {
					tmpW += currentHelpIcon[i][j].getW();
				} else {
					tmpW += currentHelpBuf[i][j].length() * 12;
				}
			}
			if (tmpW > helpW)
				helpW = tmpW;
		}
	}

	/** ゆっくりにメソッド実行
	 *<br> パラメータなし、SHIFTで全体実行系のコマンド用
	 * @param e 入力されたマウスの動作
	 * @param found 対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 */
	public static final void executeBodyMethod(MouseEvent e, Obj found, String method) {
		try {
			Method m;
			Body[] bodyList = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
			if (e.isShiftDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(method, (Class<?>[]) null);
					m.invoke(b, (Object[]) null);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(method, (Class<?>[]) null);
					m.invoke(((Body) found), (Object[]) null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	/** ゆっくりにメソッド実行
	 *<br>パラメータあり、SHIFTで全体実行系のコマンド用
	 * @param e 入力されたマウスの動作
	 * @param found 対象オブジェクト(主にゆっくり)
	 * @param method 実行したいメソッド名
	 * @param prm 指定パラメータ
	 */
	public static final void executeBodyMethod(MouseEvent e, Obj found, String method, int prm) {
		try {
			Method m;
			Body[] bodyList = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);

			if (e.isShiftDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(method, int.class);
					m.invoke(b, prm);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(method, int.class);
					m.invoke(((Body) found), prm);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	/** ゆっくりにメソッド実行
	 * <br> パラメータboolean、SHIFTで全体、CTRLで反転実行系のコマンド用
	 * 
	 * @param e 入力されたマウスの動作
	 * @param found 対象オブジェクト(主にゆっくり)
	 * @param getMethod 実行したいメソッド名(ゆっくり用)
	 * @param setMethod 実行したいメソッド名(その他用)
	 * @param invMethod 反転実行系コマンド実行
	 */
	public static final void executeBodyMethod(MouseEvent e, Obj found, String getMethod, String setMethod,String invMethod) {
		try {
			Method m;
			Body[] bodyList = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);

			if (e.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(getMethod, (Class<?>[]) null);
					flag = !((Boolean) m.invoke(((Body) found), (Object[]) null)).booleanValue();
				}
				for (Body b : bodyList) {
					m = b.getClass().getMethod(setMethod, boolean.class);
					m.invoke(b, flag);
				}
			} else if (e.isControlDown()) {
				for (Body b : bodyList) {
					m = b.getClass().getMethod(invMethod, (Class<?>[]) null);
					m.invoke(b, (Object[]) null);
				}
			} else {
				if (found instanceof Body) {
					m = ((Body) found).getClass().getMethod(invMethod, (Class<?>[]) null);
					m.invoke(((Body) found), (Object[]) null);
				}
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}
}

/************
 * ポップアップの選択をUIへ反映
 *
 */
class PopupAction implements ActionListener {
	@Override
	public final void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		GadgetList sel = GadgetList.valueOf(command);
		MainCommandUI.mainItemCombo.setSelectedIndex(sel.getGroup().ordinal() - 1);
		GadgetMenu.selectMain = GadgetList.values()[sel.getGroup().ordinal() - 1];
		GadgetMenu.selectSub = sel;
		GadgetMenu.setActionHelp(sel);
		int idx = getIndex(sel);
		MainCommandUI.subItemCombo.setSelectedIndex(idx);

		// 即時実行コマンドはここで実行
		if (sel.getActionTarget() == ActionTarget.IMMEDIATE) {
			GadgetAction.immediateEvaluate(sel);
		}

		GadgetMenu.popupDisplay = false;
	}

	/**いくつのコンボをつなげるかを返す*/
	private final int getIndex(GadgetList item) {

		int num;
		num = GadgetMenu.ToolCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.ToolCategory[i])
				return i;
		}
		num = GadgetMenu.ToolCategory2.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.ToolCategory2[i])
				return i;
		}
		num = GadgetMenu.AmpouleCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.AmpouleCategory[i])
				return i;
		}
		num = GadgetMenu.FoodCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.FoodCategory[i])
				return i;
		}
		num = GadgetMenu.CleanCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.CleanCategory[i])
				return i;
		}
		num = GadgetMenu.OkazariCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.OkazariCategory[i])
				return i;
		}
		num = GadgetMenu.PantsCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.PantsCategory[i])
				return i;
		}
		num = GadgetMenu.FloorCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.FloorCategory[i])
				return i;
		}
		num = GadgetMenu.BarrierCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.BarrierCategory[i])
				return i;
		}
		num = GadgetMenu.ToysCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.ToysCategory[i])
				return i;
		}
		num = GadgetMenu.ConveyorCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.ConveyorCategory[i])
				return i;
		}
		num = GadgetMenu.VoiceCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.VoiceCategory[i])
				return i;
		}
		num = GadgetMenu.TestCategory.length;
		for (int i = 0; i < num; i++) {
			if (item == GadgetMenu.TestCategory[i])
				return i;
		}
		return 0;
	}
}
