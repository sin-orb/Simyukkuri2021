package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.enums.CoreAnkoState;
import src.enums.Happiness;
import src.enums.ObjEXType;
import src.enums.Type;
import src.game.Dna;
import src.system.Cash;
import src.system.MessagePool;
import src.yukkuri.Ayaya;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Reimu;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.WasaReimu;

/***************************************************
養殖プール
*/
public class BreedingPool extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	public static enum PoolType {
		LOW("廉価品"),
//        HIGH("こくまろ"),
		RAPID("通常版"),
		PRO("プロ用"),
		INDUSTRY("工業用"),
		LOWS("茎：廉価品"),
//        HIGHS("茎：こくまろ"),
		RAPIDS("茎：通常版"),
		PROS("茎：プロ用"),
		INDUSTRYS("茎：工業用"),
		;
		private String name;
		PoolType(String name) { this.name = name; }
		public String toString() { return name; }
	}

	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int images_num = 4; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle boundary = new Rectangle();
	protected Random rnd = new Random();

	private boolean highQuality;
	private boolean stalkPool;
	//以下二つはoptionの数字で判別
	//private boolean rapidGrowth;
	//private boolean industrial;
	private static int[] value = {1000,5000,50000,450000,1000,5000,50000,600000};
	private static int[] cost = {10,50,50,1500,10,50,50,1500};
	public int liquidYukkuriType = -1;
	private int lastSelected = 0;


	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
		for( int i = 0; i < images_num ; i++ ){
			images[i] = ModLoader.loadItemImage(loader, "breedingpool" + File.separator + "breedingpool" + String.format("%03d",i+1) + ".png");
		}
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(enabled) {
			if ( liquidYukkuriType == 2 ){
				layer[0] = images[2];
			} else if (  liquidYukkuriType != -1  ) {
				layer[0] = images[1];
			} else {
				layer[0] = images[0];
			}
		}
		else {
			layer[0] = images[3];
		}
		return 1;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override
	public int objHitProcess( Obj o ) {
		if(!enabled)return 0;
		if ( o.getObjType() == Type.YUKKURI ){
			Body p = (Body)o;
			// 避妊されてたら妊娠しない
			if(p.isBodyCastration() && !stalkPool ) return 0;
			if(p.isStalkCastration() && stalkPool ) return 0;

			//工業用専用の特殊処理
			if(option==3||option==7){
				//一日ごとのコスト
				if ( getAge() % 2400 == 0){
					Cash.addCash(-getCost());
				}
				//赤ゆには茎を実らせない
				if(stalkPool){
					if(p.isBaby()) return 0;
				}
				//ゆっくりが膨らんでる時の糞抜き
				if(p.isInfration()){
					p.setShit(0,false);
					//胎生妊娠プールの場合爆発寸前でストップ
					if(!stalkPool  && p.isAboutToBurst()) {
						cry(p);
						return 0;
					}
				}
				//母体の自動回復
				if(p.isDamaged()){
					p.injectJuice();
				}
			}

			if ( !p.isDead() && (int)getAge() % (((highQuality==true)?5:10)*((stalkPool==true)?2:1)) == 0 ) {
				//赤ゆのDNA決定
				for(int i = 0; i < 5; i++) {
					int babyType;
					if ( liquidYukkuriType == -1 ) {
						babyType = p.getType();
					}else if (!p.isHybrid() && liquidYukkuriType < 10000 && (rnd.nextInt(50) == 0)) {
						babyType = p.getHybridType( liquidYukkuriType );
					}
					else if (rnd.nextBoolean()) {
						babyType = liquidYukkuriType;
					}
					else {
						babyType = p.getType();
					}
					// ドスまりさはただのまりさに変換
					if(babyType == DosMarisa.type) {
						babyType = Marisa.type;
					}
					if(babyType == Deibu.type) {
						babyType = Reimu.type;
					}
					// 突然変異
					if ((babyType == Reimu.type) && rnd.nextInt(20) == 0) {
						babyType = WasaReimu.type;
					}else if ((babyType == WasaReimu.type) && rnd.nextInt(20) != 0) {
						babyType = Reimu.type;
					}else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(20) == 0){
						babyType = MarisaTsumuri.type;
					}else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type  ) && rnd.nextInt(20) == 0){
						babyType = MarisaKotatsumuri.type;
					}else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type ) && rnd.nextInt(20) != 0){
						babyType = Marisa.type;
					}else if ((babyType == Kimeemaru.type ) && rnd.nextInt(20) != 0){
						babyType = Ayaya.type;
					}else if ((babyType == Ayaya.type ) && rnd.nextInt(20) == 0){
						babyType = Kimeemaru.type;
					}
					if ( p.isSick() || p.isDamaged() || p.isOverPregnantLimit() || (!highQuality&&rnd.nextInt(500)==0)) {
						if(rnd.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
							babyType = TarinaiReimu.type;
						}
						else {
							babyType = Tarinai.type;
						}
					}
					//実らせる
					if ( stalkPool  ) {
						cry(p);
						p.setHappiness(Happiness.VERY_SAD);
						p.addStress(50);
						//p.addMemories(-10);
						p.getStalkBabyTypes().add((rnd.nextBoolean()?new Dna(babyType, null, null, false):null));
						p.setHasStalk(true);
					}
					else {
						cry(p);
						p.setHappiness(Happiness.VERY_SAD);
						p.getBabyTypes().add(new Dna(babyType, null, null, false));
						p.setHasBaby(true);
						p.addStress(50);
						//p.addMemories(-10);
						break;
					}
				}
				p.subtractPregnantLimit();
				//廉価版では成長促進効果なし
				if ( option!=0 && option!=4){
					p.rapidPregnantPeriod();
				}
				//一匹一匹でコストになるのは工業用以外
				if(option!=3 && option!=7){
					Cash.addCash(-getCost());
				}
			}
			else if ( p.isDead()  && liquidYukkuriType == -1 && p.isCrushed()  ){
				liquidYukkuriType = p.getType();
				p.remove();
			}
		}
		return 0;
	}

	@Override
	public int getValue() {
		return value[option];
	}

	@Override
	public int getCost() {
		return cost[option];
	}

	@Override
	public void removeListData() {
		SimYukkuri.world.currentMap.breedingPool.remove(this);
	}

	public void cry(Body p){
		if(p.hasBabyOrStalk()){
			if(p.geteCoreAnkoState() != CoreAnkoState.DEFAULT){
				p.setNYDMessage(MessagePool.getMessage(p, MessagePool.Action.NonYukkuriDisease),false);
			}
			else if(rnd.nextInt(40) == 0){
				p.setPikoMessage(MessagePool.getMessage(p, MessagePool.Action.PoolSukkiri),true);
			}
			else{
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.PoolSukkiri));
			}
		}
	}

	public BreedingPool(int initX, int initY,  int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());

		ArrayList<BreedingPool> list = SimYukkuri.world.currentMap.breedingPool;
		list.add(this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.BREEDINGPOOL;

		interval = 1;

		boolean ret = setupPool(this, false);
		if(!ret) {
			list.remove(this);
		}
	}

	// 設定メニュー
	public static boolean setupPool(BreedingPool o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[PoolType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(4, 2));
		mainPanel.setPreferredSize(new Dimension(350, 150));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(PoolType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}

		if( !init){
			but[0].setSelected(true);
		}else{
			but[o.lastSelected].setSelected(true);
		}
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, "養殖プール設定", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if(dlgRet == JOptionPane.OK_OPTION) {
			//廉価版
			if(but[0].isSelected()) {
				o.highQuality = false;
//				o.rapidGrowth = false;
				o.stalkPool = false;
//				o.industrial = false;
				o.lastSelected = 0;
				o.option=0;
			}
			//通常版
			else if(but[1].isSelected()) {
				o.highQuality = false;
//				o.rapidGrowth = true;
				o.stalkPool = false;
//				o.industrial = false;
				o.lastSelected = 1;
				o.option=1;
			}
			//プロ用
			else if(but[2].isSelected()) {
				o.highQuality = true;
//				o.rapidGrowth = true;
				o.stalkPool = false;
//				o.industrial = false;
				o.lastSelected = 2;
				o.option=2;
			}
			//工業用
			else if(but[3].isSelected()) {
				o.highQuality = true;
//				o.rapidGrowth = true;
				o.stalkPool = false;
//				o.industrial = true;
				o.lastSelected = 3;
				o.option=3;
			}
			//廉価版(茎)
			else if(but[4].isSelected()) {
				o.highQuality = false;
//				o.rapidGrowth = false;
				o.stalkPool = true;
//				o.industrial = false;
				o.lastSelected = 4;
				o.option=4;
			}
			//通常版(茎)
			else if(but[5].isSelected()) {
				o.highQuality = false;
//				o.rapidGrowth = true;
				o.stalkPool = true;
//				o.industrial = false;
				o.lastSelected = 5;
				o.option=5;
			}
			//プロ用(茎)
			else if(but[6].isSelected()) {
				o.highQuality = true;
//				o.rapidGrowth = true;
				o.stalkPool = true;
//				o.industrial = false;
				o.lastSelected = 6;
				o.option=6;
			}
			//工業用(茎)
			else if(but[7].isSelected()) {
				o.highQuality = true;
//				o.rapidGrowth = true;
				o.stalkPool = true;
//				o.industrial = true;
				o.lastSelected = 7;
				o.option=7;
			}
			ret = true;
		}
		return ret;
	}
}
