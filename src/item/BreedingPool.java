package src.item;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.enums.Happiness;
import src.enums.ObjEXType;
import src.enums.Type;
import src.game.Dna;
import src.system.Cash;
import src.system.MessagePool;
import src.system.ResourceUtil;
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
 * 養殖プール
 */
public class BreedingPool extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	/**稼働タイプ*/
	public static enum PoolType {
		LOW(ResourceUtil.getInstance().read("item_cheap")), 
		RAPID(ResourceUtil.getInstance().read("item_normalbreed")), 
		PRO(ResourceUtil.getInstance().read("item_forpro")),
		INDUSTRY(ResourceUtil.getInstance().read("item_indust")),
		LOWS(ResourceUtil.getInstance().read("item_cheapstalk")),
		RAPIDS(ResourceUtil.getInstance().read("item_normalstalk")),
		PROS(ResourceUtil.getInstance().read("item_forprostalk")),
		INDUSTRYS(ResourceUtil.getInstance().read("item_induststalk")),
				;

		private String name;

		PoolType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static final int images_num = 4; //このクラスの総使用画像数
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();

	private boolean highQuality;
	private boolean stalkPool;
	private static int[] value = { 1000, 5000, 50000, 450000, 1000, 5000, 50000, 600000 };
	private static int[] cost = { 10, 50, 50, 1500, 10, 50, 50, 1500 };
	/**プールの上で死亡して、精子餡に混ざった種類のDNA*/
	public int liquidYukkuriType = -1;
	private int lastSelected = 0;

	/**画像ロード*/
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		for (int i = 0; i < images_num; i++) {
			images[i] = ModLoader.loadItemImage(loader,
					"breedingpool" + File.separator + "breedingpool" + String.format("%03d", i + 1) + ".png");
		}
		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if (enabled) {
			if (liquidYukkuriType == 2) {
				layer[0] = images[2];
			} else if (liquidYukkuriType != -1) {
				layer[0] = images[1];
			} else {
				layer[0] = images[0];
			}
		} else {
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
		if (!enabled)
			return 0;
		if (o.getObjType() == Type.YUKKURI) {
			Body p = (Body) o;
			// 避妊されてたら妊娠しない
			if (p.isBodyCastration() && !stalkPool)
				return 0;
			if (p.isStalkCastration() && stalkPool)
				return 0;

			//工業用専用の特殊処理
			if (option == 3 || option == 7) {
				//一日ごとのコスト
				if (getAge() % 2400 == 0) {
					Cash.addCash(-getCost());
				}
				//赤ゆには茎を実らせない
				if (stalkPool) {
					if (p.isBaby())
						return 0;
				}
				//ゆっくりが膨らんでる時の糞抜き
				if (p.isInfration()) {
					p.setShit(0, false);
					//胎生妊娠プールの場合爆発寸前でストップ
					if (!stalkPool && p.isAboutToBurst()) {
						cry(p);
						return 0;
					}
				}
				//母体の自動回復
				if (p.isDamaged()) {
					p.injectJuice();
				}
			}

			if (!p.isDead()
					&& (int) getAge() % (((highQuality == true) ? 5 : 10) * ((stalkPool == true) ? 2 : 1)) == 0) {
				//赤ゆのDNA決定
				for (int i = 0; i < 5; i++) {
					int babyType;
					if (liquidYukkuriType == -1) {
						babyType = p.getType();
					} else if (!p.isHybrid() && liquidYukkuriType < 10000 && (SimYukkuri.RND.nextInt(50) == 0)) {
						babyType = p.getHybridType(liquidYukkuriType);
					} else if (SimYukkuri.RND.nextBoolean()) {
						babyType = liquidYukkuriType;
					} else {
						babyType = p.getType();
					}
					// ドスまりさはただのまりさに変換
					if (babyType == DosMarisa.type) {
						babyType = Marisa.type;
					}
					if (babyType == Deibu.type) {
						babyType = Reimu.type;
					}
					// 突然変異
					if ((babyType == Reimu.type) && SimYukkuri.RND.nextInt(20) == 0) {
						babyType = WasaReimu.type;
					} else if ((babyType == WasaReimu.type) && SimYukkuri.RND.nextInt(20) != 0) {
						babyType = Reimu.type;
					} else if ((babyType == Marisa.type || babyType == MarisaKotatsumuri.type)
							&& SimYukkuri.RND.nextInt(20) == 0) {
						babyType = MarisaTsumuri.type;
					} else if ((babyType == Marisa.type || babyType == MarisaTsumuri.type)
							&& SimYukkuri.RND.nextInt(20) == 0) {
						babyType = MarisaKotatsumuri.type;
					} else if ((babyType == MarisaTsumuri.type || babyType == MarisaKotatsumuri.type)
							&& SimYukkuri.RND.nextInt(20) != 0) {
						babyType = Marisa.type;
					} else if ((babyType == Kimeemaru.type) && SimYukkuri.RND.nextInt(20) != 0) {
						babyType = Ayaya.type;
					} else if ((babyType == Ayaya.type) && SimYukkuri.RND.nextInt(20) == 0) {
						babyType = Kimeemaru.type;
					}
					if (p.isSick() || p.isDamaged() || p.isOverPregnantLimit()
							|| (!highQuality && SimYukkuri.RND.nextInt(500) == 0)) {
						if (SimYukkuri.RND.nextBoolean() && (babyType == Reimu.type || babyType == WasaReimu.type)) {
							babyType = TarinaiReimu.type;
						} else {
							babyType = Tarinai.type;
						}
					}
					//実らせる
					if (stalkPool) {
						cry(p);
						p.setHappiness(Happiness.VERY_SAD);
						p.addStress(50);
						//p.addMemories(-10);
						p.getStalkBabyTypes()
								.add((SimYukkuri.RND.nextBoolean() ? new Dna(babyType, null, null, false) : null));
						p.setHasStalk(true);
					} else {
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
				if (option != 0 && option != 4) {
					p.rapidPregnantPeriod();
				}
				//一匹一匹でコストになるのは工業用以外
				if (option != 3 && option != 7) {
					Cash.addCash(-getCost());
				}
			} else if (p.isDead() && liquidYukkuriType == -1 && p.isCrushed()) {
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
		SimYukkuri.world.getCurrentMap().breedingPool.remove(objId);
	}

	/** プール上のゆっくりを泣かせる処理 */
	public void cry(Body p) {
		if (p.hasBabyOrStalk()) {
			if (p.isNYD()) {
				p.setNYDMessage(MessagePool.getMessage(p, MessagePool.Action.NonYukkuriDisease), false);
			} else if (SimYukkuri.RND.nextInt(40) == 0) {
				p.setPikoMessage(MessagePool.getMessage(p, MessagePool.Action.PoolSukkiri), true);
			} else {
				p.setMessage(MessagePool.getMessage(p, MessagePool.Action.PoolSukkiri));
			}
		}
	}

	/**コンストラクタ*/
	public BreedingPool(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());

		SimYukkuri.world.getCurrentMap().breedingPool.put(objId, this);

		objType = Type.PLATFORM;
		objEXType = ObjEXType.BREEDINGPOOL;

		interval = 1;

		boolean ret = setupPool(this, false);
		if (!ret) {
			SimYukkuri.world.getCurrentMap().breedingPool.remove(objId);
		}
	}
	
	public BreedingPool() {
		
	}

	// 設定メニュー
	public static boolean setupPool(BreedingPool o, boolean init) {

		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[PoolType.values().length];
		boolean ret = false;

		mainPanel.setLayout(new GridLayout(4, 2));
		mainPanel.setPreferredSize(new Dimension(350, 150));
		ButtonGroup bg = new ButtonGroup();

		for (int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(PoolType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}

		if (!init) {
			but[0].setSelected(true);
		} else {
			but[o.lastSelected].setSelected(true);
		}
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, 
				ResourceUtil.getInstance().read("item_poolsettings"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			//廉価版
			if (but[0].isSelected()) {
				o.highQuality = false;
				//				o.rapidGrowth = false;
				o.stalkPool = false;
				//				o.industrial = false;
				o.lastSelected = 0;
				o.option = 0;
			}
			//通常版
			else if (but[1].isSelected()) {
				o.highQuality = false;
				//				o.rapidGrowth = true;
				o.stalkPool = false;
				//				o.industrial = false;
				o.lastSelected = 1;
				o.option = 1;
			}
			//プロ用
			else if (but[2].isSelected()) {
				o.highQuality = true;
				//				o.rapidGrowth = true;
				o.stalkPool = false;
				//				o.industrial = false;
				o.lastSelected = 2;
				o.option = 2;
			}
			//工業用
			else if (but[3].isSelected()) {
				o.highQuality = true;
				//				o.rapidGrowth = true;
				o.stalkPool = false;
				//				o.industrial = true;
				o.lastSelected = 3;
				o.option = 3;
			}
			//廉価版(茎)
			else if (but[4].isSelected()) {
				o.highQuality = false;
				//				o.rapidGrowth = false;
				o.stalkPool = true;
				//				o.industrial = false;
				o.lastSelected = 4;
				o.option = 4;
			}
			//通常版(茎)
			else if (but[5].isSelected()) {
				o.highQuality = false;
				//				o.rapidGrowth = true;
				o.stalkPool = true;
				//				o.industrial = false;
				o.lastSelected = 5;
				o.option = 5;
			}
			//プロ用(茎)
			else if (but[6].isSelected()) {
				o.highQuality = true;
				//				o.rapidGrowth = true;
				o.stalkPool = true;
				//				o.industrial = false;
				o.lastSelected = 6;
				o.option = 6;
			}
			//工業用(茎)
			else if (but[7].isSelected()) {
				o.highQuality = true;
				//				o.rapidGrowth = true;
				o.stalkPool = true;
				//				o.industrial = true;
				o.lastSelected = 7;
				o.option = 7;
			}
			ret = true;
		}
		return ret;
	}
}
