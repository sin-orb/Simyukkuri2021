package src.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.SimYukkuri;
import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.BreedingAmpoule;
import src.attachment.Fire;
import src.attachment.HungryAmpoule;
import src.attachment.Needle;
import src.attachment.OrangeAmpoule;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Body;
import src.base.Effect;
import src.base.Obj;
import src.base.ObjEX;
import src.base.Okazari;
import src.command.GadgetMenu;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.effect.BakeSmoke;
import src.effect.Hit;
import src.effect.Mix;
import src.effect.Steam;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.BodyRank;
import src.enums.YukkuriType;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.AutoFeeder;
import src.item.Barrier;
import src.item.Bed;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.Farm;
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
import src.item.Pool;
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
import src.system.Cash;
import src.system.FieldShapeBase;
import src.system.FrameRate;
import src.system.IconPool;
import src.system.LoadWindow;
import src.system.LoggerYukkuri;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.Sprite;
import src.util.BodyUtil;
import src.yukkuri.Alice;
import src.yukkuri.Ayaya;
import src.yukkuri.Chen;
import src.yukkuri.Chiruno;
import src.yukkuri.Deibu;
import src.yukkuri.DosMarisa;
import src.yukkuri.Eiki;
import src.yukkuri.Fran;
import src.yukkuri.Kimeemaru;
import src.yukkuri.Marisa;
import src.yukkuri.MarisaKotatsumuri;
import src.yukkuri.MarisaReimu;
import src.yukkuri.MarisaTsumuri;
import src.yukkuri.Meirin;
import src.yukkuri.Myon;
import src.yukkuri.Nitori;
import src.yukkuri.Patch;
import src.yukkuri.Ran;
import src.yukkuri.Reimu;
import src.yukkuri.ReimuMarisa;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;
import src.yukkuri.Suwako;
import src.yukkuri.Tarinai;
import src.yukkuri.TarinaiReimu;
import src.yukkuri.Tenko;
import src.yukkuri.Udonge;
import src.yukkuri.WasaReimu;
import src.yukkuri.Yurusanae;
import src.yukkuri.Yuuka;
import src.yukkuri.Yuyuko;

/**
 * SimYukuri.javaの補完
 * <br>画像の読み込みや描画関連、スレッドを走らせることをメインにやっている模様
 *
 */
public class MyPane extends JPanel implements Runnable {
	static final long serialVersionUID = 4L;
	/**メッセージボックスの位置行当たりの文字数*/
	static final int MSG_BOX_CHAR_NUM = 13;
	/**スレッドが走っているか否か*/
	public boolean isRunning = false;
	/**ゲーム内環境*/
	public Terrarium terrarium = new Terrarium();

	/**背景用バッファ*/
	private BufferedImage backBuffer = null;
	/**背景用画像*/
	private Graphics2D backBufferG2 = null;

	/** 描画用テンポラリ*/
	private List<Obj> list4sort = new LinkedList<Obj>();
	private List<Body> msgList = new LinkedList<Body>();
	private int[] posTmp = new int[10];
	private BufferedImage[] layerTmp = new BufferedImage[10];
	/**拡大表示倍率*/
	private Object renderScale;

	static boolean showLog = false;
	/*フレームレート*/
	static FrameRate fps = new FrameRate();

	static final Random rnd = new Random();
	/**ゲームスピードの定義
	 * <br>順に、停止、1倍、2倍、4倍、10倍、最速*/
	public static final int PAUSE = -1, MAX = 1, DECUPLE = 10, QUINTUPLE = 20, DOUBLE = 50, NORMAL = 100;
	/**スピードの値管理*/
	public static final int gameSpeed[] = { PAUSE, NORMAL, DOUBLE, QUINTUPLE, DECUPLE, MAX };

	@SuppressWarnings("rawtypes")
	/**ゆっくり追加ウィンドウ用コンボボックス群*/
	static JComboBox cb1;
	@SuppressWarnings("rawtypes")
	static JComboBox cb2;
	@SuppressWarnings("rawtypes")
	static JComboBox cb3;
	@SuppressWarnings("rawtypes")
	static JComboBox cb4;
	@SuppressWarnings("rawtypes")
	static JComboBox cb5;
	static JCheckBox cb6;
	/**ゆっくり追加ウィンドウ用チェックボックス群*/
	static final String[] namesCommonJ = { Marisa.nameJ, Reimu.nameJ, Alice.nameJ, Patch.nameJ, Chen.nameJ,
			Myon.nameJ };
	/**ゆっくり追加ウィンドウの、希少種用名前欄*/
	static final String[] namesRareJ = { Yurusanae.nameJ, Ayaya.nameJ, Tenko.nameJ, Udonge.nameJ, Meirin.nameJ,
			Suwako.nameJ, Chiruno.nameJ, Eiki.nameJ, Ran.nameJ, Nitori.nameJ, Yuuka.nameJ, Sakuya.nameJ };
	/**ゆっくり追加ウィンドウの、捕食種用名前欄*/
	static final String[] namesPredatorJ = { Remirya.nameJ, Fran.nameJ, Yuyuko.nameJ };

	/** 描画設定フラグ群*/
	public static boolean isDisableScript = false;
	public static boolean isEnableTarget = false;
	public static boolean isDisableHelp = false;
	/** カーソルで選択されているゆっくり*/
	public static Body selectBody = null;
	/**カーソル描画用長方形*/
	static List<Rectangle> markList = new LinkedList<Rectangle>();

	/** 標準のライン定数*/
	private static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
	/**標準フォント*/
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	/**ねぎぃメッセージ用フォント*/
	private static final Font NEGI_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 120);

	// アイテムカーソル
	private static final Stroke ITEM_CUR_STROKE = new BasicStroke(3.0f);
	private static final Color ITEM_CUR_COLOR = new Color(0, 0, 0);
	private static final Ellipse2D.Float ITEM_CUR_SHAPE = new Ellipse2D.Float(-16, -16, 32, 32);

	/** 計算テンポラリ*/
	private static Point tmpPoint = new Point();
	/**画像描画用の標準汎用長方形*/
	private static Rectangle tmpRect = new Rectangle();

	// ini設定
	/**ログダイアログへの出力有無*/
	private static int nLogOutput = 1;
	/**赤ゆサイズのうんうんの影描写の有無*/
	private static int nDrawShadowShit_Baby = 1;
	/**赤ゆサイズの吐餡の影描写の有無*/
	private static int nDrawShadowVomit_Baby = 1;

	/**
	 *  全画像の読み込み
	 * @param isBg 背景を読み込むか否か
	 * @param isItem アイテム画像を読み込むか否か
	 * @param isEffect エフェクト画像を読み込むか否か
	 * @param isBody ゆっくり(通常種のみ)を読み込むか否か
	 * @param isAttach アタッチメント一式を読み込むか否か
	 * @param isIni iniファイルの読み込み
	 */
	public void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
			boolean isIni) {
		try {
			LoadWindow win = new LoadWindow(SimYukkuri.getFrames()[0]);
			win.setVisible(true);

			ClassLoader loader = this.getClass().getClassLoader();

			// 背景
			if (isBg) {
				win.addLine("Load Terrain");
				TerrainField.loadTerrain(SimYukkuri.world.currentMap.mapIndex, loader, this);
			}

			// 道具
			if (isItem) {
				win.addLine("Load Item");
				Food.loadImages(loader, this);
				Toilet.loadImages(loader, this);
				Bed.loadImages(loader, this);
				Toy.loadImages(loader, this);
				Stone.loadImages(loader, this);
				BeltconveyorObj.loadImages(loader, this);
				Beltconveyor.loadImages(loader, this);
				BreedingPool.loadImages(loader, this);
				GarbageChute.loadImages(loader, this);
				FoodMaker.loadImages(loader, this);
				OrangePool.loadImages(loader, this);
				ProductChute.loadImages(loader, this);
				MachinePress.loadImages(loader, this);
				Diffuser.loadImages(loader, this);
				Yunba.loadImages(loader, this);
				StickyPlate.loadImages(loader, this);
				HotPlate.loadImages(loader, this);
				ProcesserPlate.loadImages(loader, this);
				Mixer.loadImages(loader, this);
				AutoFeeder.loadImages(loader, this);
				Sui.loadImages(loader, this);
				Trash.loadImages(loader, this);
				GarbageStation.loadImages(loader, this);
				House.loadImages(loader, this);
				Pool.loadImages(loader, this);
				Farm.loadImages(loader, this);
				Trampoline.loadImages(loader, this);
				Generator.loadImages(loader, this);
			}

			// エフェクト
			if (isEffect) {
				win.addLine("Load Effect");
				BakeSmoke.loadImages(loader, this);
				Hit.loadImages(loader, this);
				Mix.loadImages(loader, this);
				Steam.loadImages(loader, this);
			}

			// ゆっくり達とサブパーツ
			if (isBody) {
				win.addLine("Load Yukkuri");
				Alice.loadImages(loader, this);
				Chen.loadImages(loader, this);
				Deibu.loadImages(loader, this);
				DosMarisa.loadImages(loader, this);
				Marisa.loadImages(loader, this);
				MarisaKotatsumuri.loadImages(loader, this);
				MarisaReimu.loadImages(loader, this);
				MarisaTsumuri.loadImages(loader, this);
				Myon.loadImages(loader, this);
				Patch.loadImages(loader, this);
				Reimu.loadImages(loader, this);
				ReimuMarisa.loadImages(loader, this);
				Tarinai.loadImages(loader, this);
				TarinaiReimu.loadImages(loader, this);
				WasaReimu.loadImages(loader, this);
				Body.loadShadowImages(loader, this);

				Shit.loadImages(loader, this);
				Vomit.loadImages(loader, this);
			}

			// アタッチメント
			if (isAttach) {
				win.addLine("Load Attachment");
				Stalk.loadImages(loader, this);
				Fire.loadImages(loader, this);
				Ants.loadImages(loader, this);
				Needle.loadImages(loader, this);
				OrangeAmpoule.loadImages(loader, this);
				AccelAmpoule.loadImages(loader, this);
				StopAmpoule.loadImages(loader, this);
				Okazari.loadImages(loader, this);
				HungryAmpoule.loadImages(loader, this);
				VeryShitAmpoule.loadImages(loader, this);
				PoisonAmpoule.loadImages(loader, this);
				BreedingAmpoule.loadImages(loader, this);
				Badge.loadImages(loader, this);
				ANYDAmpoule.loadImages(loader, this);
			}

			// INIファイル
			if (isIni) {
				win.addLine("Load Ini");
				Alice.loadIniFile(loader);
				Ayaya.loadIniFile(loader);
				Chen.loadIniFile(loader);
				Chiruno.loadIniFile(loader);
				Deibu.loadIniFile(loader);
				DosMarisa.loadIniFile(loader);
				Eiki.loadIniFile(loader);
				Fran.loadIniFile(loader);
				Kimeemaru.loadIniFile(loader);
				Marisa.loadIniFile(loader);
				MarisaKotatsumuri.loadIniFile(loader);
				MarisaReimu.loadIniFile(loader);
				MarisaTsumuri.loadIniFile(loader);
				Meirin.loadIniFile(loader);
				Myon.loadIniFile(loader);
				Nitori.loadIniFile(loader);
				Patch.loadIniFile(loader);
				Ran.loadIniFile(loader);
				Reimu.loadIniFile(loader);
				ReimuMarisa.loadIniFile(loader);
				Remirya.loadIniFile(loader);
				Sakuya.loadIniFile(loader);
				Suwako.loadIniFile(loader);
				Tarinai.loadIniFile(loader);
				TarinaiReimu.loadIniFile(loader);
				Tenko.loadIniFile(loader);
				Udonge.loadIniFile(loader);
				WasaReimu.loadIniFile(loader);
				Yurusanae.loadIniFile(loader);
				Yuuka.loadIniFile(loader);
				Yuyuko.loadIniFile(loader);

				nLogOutput = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "LOG_RUN");
				nDrawShadowShit_Baby = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
						"DrawShadowShit_Baby");
				nDrawShadowVomit_Baby = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
						"DrawShadowVomit_Baby");
				SimYukkuri.NAGASI_MODE = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play",
						"NAGASI_MODE");
			}

			win.setVisible(false);
			win = null;

			System.gc();

		} catch (IOException e1) {
			System.out.println("File I/O error");
		} catch (OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, "メモリ不足です");
		}
	}

	/** ゆっくり用遅延読み込み
	 *
	 * @param type 読み込むゆっくりの種
	 */
	public void loadBodyImage(YukkuriType type) {
		synchronized (SimYukkuri.lock) {
			try {
				ClassLoader loader = this.getClass().getClassLoader();

				Class<?> c = Class.forName("src.yukkuri." + type.className);
				Method m = c.getMethod("loadImages", ClassLoader.class, ImageObserver.class);
				m.invoke(null, loader, this);
			} catch (SecurityException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				JOptionPane.showMessageDialog(null, "メモリ不足です");
			}
		}
	}

	/** 背景ファイルリロード*/
	public void loadTerrainFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		TerrainField.loadTerrain(SimYukkuri.world.getNextMap(), loader, this);
		System.gc();
	}

	/**ズーム時の中心の表示倍率
	 * @param hint オブジェクト
	 */
	public void setRenderScale(Object hint) {
		renderScale = hint;
	}

	/**背景画像用バッファ作成
	 *
	 */
	public void createBackBuffer() {
		backBuffer = new BufferedImage(Translate.bufferW, Translate.bufferH, BufferedImage.TYPE_3BYTE_BGR);
		backBufferG2 = backBuffer.createGraphics();
		backBufferG2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}

	@Override
	public void run() {

		initBodies();
		synchronized (SimYukkuri.lock) {
			SimYukkuri.initialized = true;
		}

		// run animation
		while (isRunning) {
			int speed;

			synchronized (SimYukkuri.lock) {

				// マップ切り替え準備中は画面の書き換えを行わない
				if (SimYukkuri.world.getNextMap() != -1) {
					continue;
				}

				//				stress = 100 * Terrarium.bodyList.size() / Body.getHeadageLimit() * 10000 / (Terrarium.terrariumSizeParcent*Terrarium.terrariumSizeParcent);
				speed = gameSpeed[MainCommandUI.selectedGameSpeed];
			}

			if (speed != PAUSE) {
				synchronized (SimYukkuri.lock) {
					terrarium.stepRun();
				}

				if (nLogOutput != 0) {
					if (Terrarium.operationTime % 10 == 0) {
						LoggerYukkuri.run();
					}
				}
				SimYukkuri.checkMouseVel();
			}
			repaint();
			try {
				if (speed >= 0) {
					Thread.sleep(speed);
				} else {
					Thread.sleep(NORMAL);
				}
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
	}

	ClassLoader getImageLoader() {
		return this.getClass().getClassLoader();
	}

	/**ゆっくり追加用クラス*/
	public class MyAddYukkuriListener implements ItemListener, ActionListener {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void itemStateChanged(ItemEvent e) {

			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == cb3) {
					if (cb3.getSelectedIndex() == 0) {
						cb1.setModel(new DefaultComboBoxModel(namesCommonJ));
					} else if (cb3.getSelectedIndex() == 1) {
						cb1.setModel(new DefaultComboBoxModel(namesRareJ));
					} else {
						cb1.setModel(new DefaultComboBoxModel(namesPredatorJ));
					}
				} else if (e.getSource() == cb4) {
					if (cb4.getSelectedIndex() == 0) {
						cb1.setEnabled(true);
						cb2.setEnabled(true);
						cb3.setEnabled(true);
					} else {
						cb1.setEnabled(false);
						cb2.setEnabled(false);
						cb3.setEnabled(false);
					}
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cb5) {
				String str = cb5.getSelectedItem().toString();
				Integer num;
				try {
					num = new Integer(str);
				} catch (NumberFormatException ne) {
					num = new Integer(1);
				}
				cb5.setSelectedItem(num.toString());
			}
		}
	}

	/*------------------------------------------------------------------
		ゆっくり追加ダイアログ
	
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initBodies() {
		List<Body> bodies = new LinkedList<Body>();
		String[] options;
		String[] ages;
		String[] rare;
		String[] mode;
		String[] num;
		String mess1, mess2, mess3, mess4, mess5;
		final int BABY = 0, CHILD = 1, ADULT = 2;
		MyAddYukkuriListener mayl = new MyAddYukkuriListener();

		String[] tempAges = { "赤ちゃん", "子供", "大人" };
		ages = tempAges;
		String[] tempRare = { "通常種", "希少種", "捕食種" };
		rare = tempRare;
		String[] tempo = { "はい", "いいえ" };
		options = tempo;
		String[] tempmode = { "OFF", "通常種", "希少種", "通常種＋希少種" };
		mode = tempmode;
		String[] tempnum = { "1", "2", "3", "4", "5", "10", "50", "100" };
		num = tempnum;
		mess1 = "どのゆっくりを追加しますか？";
		mess2 = "もっと追加しますか？";
		mess3 = "ランダムモード";
		mess4 = "追加数";
		mess5 = "強制レイパー化";

		for (int choice = 0; choice == 0;) {
			JPanel panel = new JPanel();
			JPanel panel2 = new JPanel();
			JPanel panel3 = new JPanel();
			JPanel panel4 = new JPanel();
			cb1 = new JComboBox();
			cb2 = new JComboBox(ages);
			cb2.setSelectedIndex(2);
			panel2.add(cb2);
			cb3 = new JComboBox(rare);
			cb3.setSelectedIndex(0);
			panel2.add(cb3);
			cb3.addItemListener(mayl);
			cb1 = new JComboBox();
			if (cb3.getSelectedIndex() == 0) {
				cb1.setModel(new DefaultComboBoxModel(namesCommonJ));
			} else {
				cb1.setModel(new DefaultComboBoxModel(namesRareJ));
			}
			cb1.setMaximumRowCount(8);
			cb1.setSelectedIndex(0);
			panel2.add(cb1);
			JLabel modelabel = new JLabel(mess3);
			panel3.add(modelabel);
			cb4 = new JComboBox(mode);
			cb4.addItemListener(mayl);
			panel3.add(cb4);

			JLabel numlabel = new JLabel(mess4);
			panel3.add(numlabel);
			cb5 = new JComboBox(num);
			cb5.setEditable(true);
			cb5.setPreferredSize(new Dimension(60, 32));
			cb5.addActionListener(mayl);
			panel3.add(cb5);
			JLabel rapelabel = new JLabel(mess5);
			panel4.add(rapelabel);
			cb6 = new JCheckBox();
			cb6.setSelected(false);
			panel4.add(cb6);
			JLabel label = new JLabel(mess1);
			panel.add(label);
			panel.add(panel2);
			panel.add(panel3);
			panel.add(panel4);
			panel.setLayout(new GridLayout(4, 1));
			panel.setPreferredSize(new Dimension(400, 180));

			int ret = JOptionPane.showConfirmDialog(this, panel, SimYukkuri.TITLE, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (ret == 2) {
				break;
			}

			int maxNum;
			try {
				maxNum = Integer.parseInt(cb5.getSelectedItem().toString());
			} catch (NumberFormatException ne) {
				maxNum = 1;
			}

			int rndType = cb4.getSelectedIndex();
			for (int i = 0; i < maxNum; i++) {
				int selectType;
				int selectAge;
				switch (rndType) {
				case 0:
				default:
					selectType = cb1.getSelectedIndex();
					if (cb3.getSelectedIndex() == 1) {
						selectType += 1000;
					} else if (cb3.getSelectedIndex() == 2) {
						selectType += 3000;
					}
					selectAge = cb2.getSelectedIndex();
					break;
				case 1:
					selectType = rnd.nextInt(namesCommonJ.length);
					selectAge = rnd.nextInt(3);
					break;
				case 2:
					selectType = rnd.nextInt(namesRareJ.length) + 1000;
					selectAge = rnd.nextInt(3);
					break;
				case 3:
					int selectRare = rnd.nextInt(2);
					switch (selectRare) {
					case 0:
					default:
						selectType = rnd.nextInt(namesCommonJ.length);
						break;
					case 1:
						selectType = rnd.nextInt(namesRareJ.length) + 1000;
						break;
					}
					selectAge = rnd.nextInt(3);
					break;
				}

				boolean bImageNagasiMode = false;
				if (selectType == Reimu.type && rnd.nextInt(20) == 0)
					selectType = WasaReimu.type;
				if (selectType == Reimu.type && rnd.nextInt(15) == 0)
					selectType = Deibu.type;
				if (selectType == Marisa.type && rnd.nextInt(50) == 0)
					selectType = MarisaTsumuri.type;
				if (selectType == Marisa.type && rnd.nextInt(50) == 0)
					selectType = MarisaKotatsumuri.type;
				if (selectType == Ayaya.type && rnd.nextInt(20) == 0)
					selectType = Kimeemaru.type;

				//if(selectType == Reimu.type || selectType == Marisa.type)
				{
					if (SimYukkuri.NAGASI_MODE == 1) {
						bImageNagasiMode = true;
					}
					if (SimYukkuri.NAGASI_MODE == 2) {
						if (rnd.nextInt(20) == 0) {
							bImageNagasiMode = true;
						}
					}
				}

				AgeState age;
				switch (selectAge) {
				case BABY:
					age = AgeState.BABY;
					break;
				case CHILD:
					age = AgeState.CHILD;
					break;
				case ADULT:
				default:
					age = AgeState.ADULT;
					break;
				}
				Body b = terrarium.makeBody(rnd.nextInt(Translate.mapW), rnd.nextInt(Translate.mapH), 0, selectType,
						null, age, null, null, true);
				b.addAge(256);
				if (cb6.isSelected()) {
					b.setRaper(true);
				} else {
					b.setRaper(false);
				}
				b.setbImageNagasiMode(bImageNagasiMode);

				bodies.add(b);
			}
			choice = 1;
			choice = JOptionPane.showOptionDialog(this, mess2 + System.getProperty("line.separator"), SimYukkuri.TITLE,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		}
		for (Body b : bodies) {
			synchronized (SimYukkuri.lock) {
				terrarium.addBody(b);
				if (b.getBodyRank() == BodyRank.KAIYU)
					Cash.buyYukkuri(b);
			}
		}
	}

	/** ゆっくり用描画位置の計算
	 * @param origin 対象ゆっくりの原点
	 * @param spr 対象ゆっくりのスプライト
	 */
	private void calcDrawBodyPosition(Point origin, Sprite spr) {
		int sizeW = Translate.transSize(spr.imageW);
		int sizeH = Translate.transSize(spr.imageH);
		int pivX = Translate.transSize(spr.pivotX);
		int pivY = Translate.transSize(spr.pivotY);

		// 左右両向きの描画範囲を作成
		spr.calcScreenRect(origin, pivX, pivY, sizeW, sizeH);
	}

	/** 汎用の描画位置の計算
	 *
	 * @param o 描画対象オブジェクト
	 * @param rect オブジェクトの占有長方形
	 */
	private void calcDrawPosition(Obj o, Rectangle rect) {
		int sizeW = Translate.transSize(o.getW());
		int sizeH = Translate.transSize(o.getH());
		int pivX = Translate.transSize(o.getPivotX());
		int pivY = Translate.transSize(o.getPivotY());
		Translate.translate(o.getX(), o.getY(), tmpPoint);
		rect.x = tmpPoint.x - pivX;
		rect.y = tmpPoint.y - pivY;
		rect.width = sizeW;
		rect.height = sizeH;

		// オブジェクトに描画情報を設定
		// この描画範囲を使用してマウスとの当たり判定を取得
		o.setScreenPivot(tmpPoint);
		o.setScreenRect(rect);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void paint(Graphics g) {
		synchronized (SimYukkuri.lock) {

			MapPlaceData curMap = SimYukkuri.world.getCurrentMap();

			//			fps.count();

			list4sort.clear();
			list4sort.addAll(SimYukkuri.world.getYukkuriList());
			list4sort.addAll(SimYukkuri.world.getFixObjList());
			list4sort.addAll(SimYukkuri.world.getObjectList());
			list4sort.addAll(SimYukkuri.world.getSortEffectList());
			list4sort.addAll(TerrainField.getStructList());
			Collections.sort(list4sort, ObjDrawComp.INSTANCE);

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, renderScale);

			Rectangle dispArea = Translate.getDisplayArea();
			backBufferG2.setClip(dispArea.x, dispArea.y, dispArea.width, dispArea.height);

			msgList.clear();
			markList.clear();
			if (selectBody != null) {
				if (selectBody.isRemoved())
					selectBody = null;
				else
					MainCommandUI.showStatus(selectBody);
			}
			// 背景の下地と最下面オブジェクト描画
			TerrainField.drawBackGroundImage(backBufferG2, this);
			TerrainField.drawFloor(backBufferG2, this);

			// シェイプはリストの0が最前面に来るように逆順で描画
			int num = 0;
			// 畑描画
			num = curMap.farm.size() - 1;
			for (int i = num; i >= 0; i--) {
				Farm p = curMap.farm.get(i);
				p.drawShape(backBufferG2);
			}

			// 池描画
			num = curMap.pool.size() - 1;
			for (int i = num; i >= 0; i--) {
				Pool p = curMap.pool.get(i);
				p.drawShape(backBufferG2);
			}

			// ベルトコンベア描画
			num = curMap.beltconveyor.size() - 1;
			for (int i = num; i >= 0; i--) {
				Beltconveyor p = curMap.beltconveyor.get(i);
				p.drawShape(backBufferG2);
			}

			// 床置きオブジェクト描画
			List<ObjEX> platformList = SimYukkuri.world.getPlatformList();
			for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
				ObjEX oex = i.next();
				calcDrawPosition(oex, tmpRect);
				int layerNum = oex.getImageLayer(layerTmp);
				if (oex instanceof BeltconveyorObj) {
					((BeltconveyorObj) (oex)).getImageLayer(backBufferG2, layerTmp, tmpRect);
				} else {
					for (int j = 0; j < layerNum; j++) {
						backBufferG2.drawImage(layerTmp[j], tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					}
				}
			}

			// 壁描画
			backBufferG2.setStroke(Barrier.WALL_STROKE);
			for (Barrier b : curMap.barrier) {
				b.drawShape(backBufferG2);
			}
			backBufferG2.setStroke(DEFAULT_STROKE);

			// ソートオブジェクト描画
			Sprite base;
			Sprite expand;
			Sprite braid;
			Body selectBodyCheck = null;

			for (Obj o : list4sort) {
				switch (o.getObjType()) {
				case YUKKURI: {
					Body b = (Body) o;
					// 選択中の固体がいるかチェック用
					if (b == selectBody) {
						selectBodyCheck = b;
					}

					int direction = b.getDirection().ordinal();
					// 妊娠などによるゆっくり画像サイズを最新のものに計算
					b.updateSpriteSize();
					base = b.getBodyBaseSpr();
					expand = b.getBodyExpandSpr();
					braid = b.getBraidSprite();

					int shadowH = b.getShadowH(); // 影の画像高さ  横は体の幅を使う

					// マップ上の位置から背景上の位置へ変換
					Translate.translate(b.getDrawOfsX(), b.getDrawOfsY(), tmpPoint);
					calcDrawBodyPosition(tmpPoint, base);
					calcDrawBodyPosition(tmpPoint, expand);
					calcDrawBodyPosition(tmpPoint, braid);

					boolean bDrawShadow = true;
					if (b.getLinkParent() != null && b.getLinkParent().getZ() < b.getZ()) {
						bDrawShadow = false;
					}
					// 影
					if (bDrawShadow && b.isDropShadow() && !b.isUnBirth() && 0 <= b.getZ()) {
						if (b.getType() == Remirya.type && b.isbImageNagasiMode()) {
							backBufferG2.drawImage(b.getShadowImage(), expand.screenRect[direction].x,
									expand.screenRect[direction].y + expand.screenRect[direction].height * 11 / 12
											- shadowH,
									expand.screenRect[direction].width, shadowH, this);
						} else {
							backBufferG2.drawImage(b.getShadowImage(), expand.screenRect[direction].x,
									expand.screenRect[direction].y + expand.screenRect[direction].height - shadowH,
									expand.screenRect[direction].width, shadowH, this);
						}
					}

					// 本体が宙に浮いてる分
					int tz = Translate.translateZ(b.getZ());
					base.screenRect[0].y -= tz;
					expand.screenRect[0].y -= tz;
					braid.screenRect[0].y -= tz;
					base.screenRect[1].y -= tz;
					expand.screenRect[1].y -= tz;
					braid.screenRect[1].y -= tz;

					// 描画情報の登録
					b.setScreenPivot(tmpPoint);
					b.setScreenRect(expand.screenRect[0]);

					// カーソル登録
					if (b.isPin()) {
						//						Rectangle rect = new Rectangle();
						//						rect.x = bodyExpandRect.x;
						//						rect.y = bodyExpandRect.y;
						//						rect.width = bodyExpandRect.width;
						//						rect.height = bodyExpandRect.height;
						markList.add(expand.screenRect[0]);
					}

					// 本体描画
					if (b.getBaryState() != BaryInUGState.ALL) {
						BodyUtil.drawBody(backBufferG2, this, b);
					}
					// メッセージ固体登録
					if (b.getMessageBuf() != null && !isDisableScript) {
						msgList.add(b);
					}
					//{
					//	Point pp = new Point();
					//	Translate.translate(b.destX, b.destY, pp);
					//	g2.drawRect(pp.x, pp.y, 3, 3);				
					//}
				}
					break;
				case SHIT: {
					Shit s = (Shit) o;
					calcDrawPosition(s, tmpRect);
					// 赤ゆでかつ接地している場合は影を描画しない
					if (nDrawShadowShit_Baby == 1 || s.ageState != AgeState.BABY || 0 < s.getZ()) {
						backBufferG2.drawImage(s.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height,
								this);
					}
					tmpRect.y -= Translate.translateZ(s.getZ());
					backBufferG2.drawImage(s.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
				}
					break;
				case VOMIT: {
					Vomit v = (Vomit) o;
					calcDrawPosition(v, tmpRect);
					// 赤ゆでかつ接地している場合は影を描画しない
					if (nDrawShadowVomit_Baby == 1 || v.getAgeState() != AgeState.BABY || 0 < v.getZ()) {
						backBufferG2.drawImage(v.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height,
								this);
					}
					tmpRect.y -= Translate.translateZ(v.getZ());
					backBufferG2.drawImage(v.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
				}
					break;
				case FIX_OBJECT: {
					ObjEX oex = (ObjEX) o;
					calcDrawPosition(oex, tmpRect);
					int layerNum = oex.getImageLayer(layerTmp);
					for (int i = 0; i < layerNum; i++) {
						backBufferG2.drawImage(layerTmp[i], tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					}
				}
					break;
				case OBJECT: {
					ObjEX oex = (ObjEX) o;
					calcDrawPosition(oex, tmpRect);
					backBufferG2.drawImage(oex.getShadowImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height,
							this);
					tmpRect.y -= Translate.translateZ(oex.getZ());
					int layerNum = oex.getImageLayer(layerTmp);
					for (int i = 0; i < layerNum; i++) {
						backBufferG2.drawImage(layerTmp[i], tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
					}
				}
					break;
				case LIGHT_EFFECT: {
					Effect ef = (Effect) o;
					calcDrawPosition(ef, tmpRect);
					tmpRect.y -= Translate.translateZ(ef.getZ());
					backBufferG2.drawImage(ef.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
				}
					break;
				case BG_OBJECT: {
					TerrainBillboard tb = (TerrainBillboard) o;
					tb.draw(backBufferG2, this);
				}
					break;
				default:
					break;
				}
			}

			// 最前面エフェクト描画
			Effect ef;
			for (Obj o : SimYukkuri.world.getFrontEffectList()) {
				ef = (Effect) o;
				calcDrawPosition(ef, tmpRect);
				tmpRect.y -= Translate.translateZ(ef.getZ());
				backBufferG2.drawImage(ef.getImage(), tmpRect.x, tmpRect.y, tmpRect.width, tmpRect.height, this);
			}

			// 最全面背景描画
			TerrainField.drawCeiling(backBufferG2, this);

			// 各種カーソル表示
			Point mousePos = getMousePosition();

			if (isEnableTarget) {
				Image[] cursor = IconPool.getCursorIconImageArray();
				int st = IconPool.CursorIcon.CUR_LB.ordinal();
				for (Rectangle rect : markList) {
					backBufferG2.drawImage(cursor[st + 1], rect.x, rect.y, this);
					backBufferG2.drawImage(cursor[st + 0], rect.x, rect.y + rect.width - 20, this);
					backBufferG2.drawImage(cursor[st + 2], rect.x + rect.width - 20, rect.y + rect.width - 20, this);
					backBufferG2.drawImage(cursor[st + 3], rect.x + rect.width - 20, rect.y, this);
				}
			}

			if (selectBodyCheck == null) {
				selectBody = null;
			}
			if (selectBody != null) {
				Image[] select = IconPool.getCursorIconImageArray();
				int st = IconPool.CursorIcon.SEL_0.ordinal();
				Rectangle r = selectBody.getScreenRect();
				int x = r.x + (r.width >> 1) - 12;
				int y = r.y + r.height + 2;
				backBufferG2.drawImage(select[st + (int) (selectBody.getAge() % 4)], x, y, this);
			}

			if (SimYukkuri.world.player.holdItem != null && mousePos != null) {
				backBufferG2.translate(mousePos.x, mousePos.y);
				backBufferG2.setStroke(ITEM_CUR_STROKE);
				backBufferG2.setColor(ITEM_CUR_COLOR);
				backBufferG2.draw(ITEM_CUR_SHAPE);
				backBufferG2.translate(-mousePos.x, -mousePos.y);
			}

			// ドラッグ中のフィールドプレビュー描画
			GadgetList curGadget = GadgetMenu.getCurrentGadget();
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.BARRIER) {
				if ((SimYukkuri.fieldSX >= 0) && (SimYukkuri.fieldSY >= 0)
						&& (SimYukkuri.fieldEX >= 0) && (SimYukkuri.fieldEY >= 0)) {

					backBufferG2.setStroke(FieldShapeBase.PREVIEW_STROKE);
					backBufferG2.setColor(FieldShapeBase.PREVIEW_COLOR);
					switch (curGadget) {
					case GAP_MINI:
					case GAP_BIG:
					case NET_MINI:
					case NET_BIG:
					case WALL:
					case ITEM:
					case NoUNUN:
					case KEKKAI:
						Barrier.drawPreview(backBufferG2, SimYukkuri.fieldSX,
								SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						break;
					case POOL:
						Pool.drawPreview(backBufferG2, SimYukkuri.fieldSX,
								SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						break;
					case FARM:
						Farm.drawPreview(backBufferG2, SimYukkuri.fieldSX,
								SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						break;
					case BELTCONVEYOR:
						Beltconveyor.drawPreview(backBufferG2, SimYukkuri.fieldSX,
								SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						break;
					}
				}
			}
			if (curGadget != null && curGadget.getGroup() == MainCategoryName.CONVEYOR) {
				if ((SimYukkuri.fieldSX >= 0) && (SimYukkuri.fieldSY >= 0)
						&& (SimYukkuri.fieldEX >= 0) && (SimYukkuri.fieldEY >= 0)) {

					backBufferG2.setStroke(FieldShapeBase.PREVIEW_STROKE);
					backBufferG2.setColor(FieldShapeBase.PREVIEW_COLOR);
					switch (curGadget) {
					case BELTCONVEYOR_CUSTOM:
						BeltconveyorObj.drawPreview(backBufferG2, SimYukkuri.fieldSX,
								SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						break;
					}
				}
			}
			// バックバッファの転送
			g2.drawImage(backBuffer, 0, 0, Translate.canvasW, Translate.canvasH,
					dispArea.x, dispArea.y, dispArea.x + dispArea.width, dispArea.y + dispArea.height, this);

			// 時間帯グラデ
			LinearGradientPaint sky = TerrainField.getSkyGrad(Terrarium.getDayState().ordinal());
			if (sky != null) {
				g2.setPaint(sky);
				g2.fillRect(0, 0, Translate.fieldW * 100 / Translate.mapScale,
						Translate.fieldH * 100 / Translate.mapScale);
			}
			//メッセージ表示
			String message;
			int fontSize;
			int wx, wy;
			Rectangle bodyRect;
			for (Body b : msgList) {
				message = b.getMessageBuf();
				fontSize = b.getMessageTextSize();
				if (fontSize == 120) {
					g2.setFont(NEGI_FONT);
				} else {
					Font fontMessage = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
					g2.setFont(fontMessage);
				}
				bodyRect = b.getScreenRect();
				int width = Math.min(message.length(), MSG_BOX_CHAR_NUM) * fontSize;
				int height = drawStringMultiLine(g2, message, 0, 0, width, false);

				Translate.transFieldToCanvas(bodyRect.x, bodyRect.y, posTmp);
				wx = posTmp[0] + 14;
				//				if(wx + width > Translate.canvasW) wx = Translate.canvasW - width; 
				wy = posTmp[1] - height - 4;
				//				if(wy < 0) wy = 0;
				g2.setColor(b.getMessageBoxColor());
				g2.fillRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				g2.setColor(b.getMessageLineColor()); // no transparent black.
				g2.setStroke(b.getMessageWindowStroke());
				g2.drawRoundRect(wx, wy, width + 8, height + 8, 8, 8);
				g2.setStroke(DEFAULT_STROKE);
				g2.setColor(b.getMessageTextColor()); // no transparent black.
				drawStringMultiLine(g2, message, wx + 4, wy + 4, width, true);
				g2.setFont(DEFAULT_FONT);
			}

			// ヘルプ表示
			if (!isDisableHelp && GadgetMenu.currentHelpNum > 0) {
				if (mousePos != null) {
					g2.setFont(DEFAULT_FONT);
					g2.setColor(Color.WHITE);
					g2.fillRoundRect(mousePos.x, mousePos.y + 20, GadgetMenu.helpW, GadgetMenu.helpH, 8, 8);
					g2.setColor(Color.BLACK);
					g2.setStroke(DEFAULT_STROKE);
					g2.drawRoundRect(mousePos.x, mousePos.y + 20, GadgetMenu.helpW, GadgetMenu.helpH, 8, 8);
					for (int i = 0; i < GadgetMenu.currentHelpNum; i++) {
						int px = mousePos.x + 2;
						int py = mousePos.y + 2 + 20 + (16 * i);
						for (int j = 0; j < GadgetMenu.currentHelpIcon[i].length; j++) {
							if (GadgetMenu.currentHelpIcon[i][j] != null) {
								g2.drawImage(GadgetMenu.getHelpIconImage(GadgetMenu.currentHelpIcon[i][j]), px, py,
										this);
								px += GadgetMenu.currentHelpIcon[i][j].getW();
							} else {
								drawStringMultiLine(g2, GadgetMenu.currentHelpBuf[i][j], px, py,
										GadgetMenu.currentHelpBuf[i][j].length() * 12, true);
								px += GadgetMenu.currentHelpBuf[i][j].length() * 12;
							}
						}
					}
				}
			}
			// ログ
			if (LoggerYukkuri.show) {
				LoggerYukkuri.displayLog(g2);
			}
		}
	}
	/**文字メッセージの表示*/
	private int drawStringMultiLine(Graphics2D g2d, String str, int posX, int posY, int width, boolean flag) {
		AttributedString as = new AttributedString(str);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		AttributedCharacterIterator asiterator = as.getIterator();
		FontRenderContext context = g2d.getFontRenderContext();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(as.getIterator(), context);
		float formatWidth = (float) width;
		float drawPosX = 0;
		float drawPosY = posY;
		int beginIndex = asiterator.getBeginIndex();
		int endIndex = asiterator.getEndIndex();
		lineMeasurer.setPosition(beginIndex);
		while (lineMeasurer.getPosition() < endIndex) {
			TextLayout layout = lineMeasurer.nextLayout(formatWidth);
			drawPosY += layout.getAscent();
			if (layout.isLeftToRight()) {
				drawPosX = posX;
			} else {
				drawPosX = posX + formatWidth - layout.getAdvance();
			}
			if (flag) {
				layout.draw(g2d, drawPosX, drawPosY);
			}
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		return (int) Math.ceil(drawPosY);
	}
}
