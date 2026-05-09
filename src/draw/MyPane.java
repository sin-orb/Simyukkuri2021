package src.draw;
import src.util.GameLocale;
import src.util.GameEnvironment;
import src.util.GameText;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
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
import src.enums.BurialState;
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
import src.system.ResourceUtil;
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
 * <br>
 * 画像の読み込みや描画関連、スレッドを走らせることをメインにやっている模様
 *
 */
public class MyPane extends JPanel implements Runnable {

	private static final long serialVersionUID = 3984934418500303781L;
	/** メッセージボックスの位置行当たりの文字数 */
	static final int MSG_BOX_CHAR_NUM = 13;
	/** スレッドが走っているか否か */
	private boolean isRunning = false;
	/** ゲーム内環境 */
	private Terrarium terrarium = new Terrarium();

	/** 背景用バッファ */
	private BufferedImage backBuffer = null;
	/** 背景用画像 */
	private Graphics2D backBufferG2 = null;

	/** 描画用テンポラリ */
	private List<Obj> list4sort = new LinkedList<Obj>();
	private List<Body> msgList = new LinkedList<Body>();
	private int[] posTmp = new int[10];
	private BufferedImage[] layerTmp = new BufferedImage[10];
	/** 拡大表示倍率 */
	private Object renderScale;

	static boolean showLog = false;
	/* フレームレート */
	static FrameRate fps = new FrameRate();
	/**
	 * ゲームスピードの定義
	 * <br>
	 * 順に、停止、1倍、2倍、4倍、10倍、最速
	 */
	public static final int PAUSE = -1, MAX = 1, DECUPLE = 10, QUINTUPLE = 20, DOUBLE = 50, NORMAL = 100;
	/** スピードの値管理 */
	public static final int gameSpeed[] = { PAUSE, NORMAL, DOUBLE, QUINTUPLE, DECUPLE, MAX };

	@SuppressWarnings("rawtypes")
	/** ゆっくり追加ウィンドウ用コンボボックス群 */
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
	/** ゆっくり追加ウィンドウ用チェックボックス群 */
	static final String[] namesCommonJ = { Marisa.nameJ, Reimu.nameJ, Alice.nameJ, Patch.nameJ, Chen.nameJ,
			Myon.nameJ };
	/** ゆっくり追加ウィンドウ用チェックボックス群(英語) */
	static final String[] namesCommonE = { Marisa.nameE, Reimu.nameE, Alice.nameE, Patch.nameE, Chen.nameE,
			Myon.nameE };
	/** ゆっくり追加ウィンドウの、希少種用名前欄 */
	static final String[] namesRareJ = { Yurusanae.nameJ, Ayaya.nameJ, Tenko.nameJ, Udonge.nameJ, Meirin.nameJ,
			Suwako.nameJ, Chiruno.nameJ, Eiki.nameJ, Ran.nameJ, Nitori.nameJ, Yuuka.nameJ, Sakuya.nameJ };
	/** ゆっくり追加ウィンドウの、希少種用名前欄（英語） */
	static final String[] namesRareE = { Yurusanae.nameE, Ayaya.nameE, Tenko.nameE, Udonge.nameE, Meirin.nameE,
			Suwako.nameE, Chiruno.nameE, Eiki.nameE, Ran.nameE, Nitori.nameE, Yuuka.nameE, Sakuya.nameE };
	/** ゆっくり追加ウィンドウの、捕食種用名前欄 */
	static final String[] namesPredatorJ = { Remirya.nameJ, Fran.nameJ, Yuyuko.nameJ };
	/** ゆっくり追加ウィンドウの、捕食種用名前欄(英語) */
	static final String[] namesPredatorE = { Remirya.nameE, Fran.nameE, Yuyuko.nameE };

	/** 描画設定フラグ群 */
	private static boolean isDisableScript = false;
	private static boolean isEnableTarget = false;
	private static boolean isDisableHelp = false;
	/** カーソルで選択されているゆっくり */
	private static Body selectBody = null;

	public static int getNormalSpeed() {
		return NORMAL;
	}

	public static int[] getGameSpeed() {
		return gameSpeed;
	}

	public static boolean isDisableScript() {
		return isDisableScript;
	}

	public static void setDisableScript(boolean disableScript) {
		isDisableScript = disableScript;
	}

	public static boolean isEnableTarget() {
		return isEnableTarget;
	}

	public static void setEnableTarget(boolean enableTarget) {
		isEnableTarget = enableTarget;
	}

	public static boolean isDisableHelp() {
		return isDisableHelp;
	}

	public static void setDisableHelp(boolean disableHelp) {
		isDisableHelp = disableHelp;
	}

	public static Body getSelectBody() {
		return selectBody;
	}

	public static void setSelectBody(Body body) {
		selectBody = body;
	}

	/** カーソル描画用長方形 */
	static List<Rectangle4y> markList = new LinkedList<Rectangle4y>();

	/** 標準のライン定数 */
	private static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);
	/** 標準フォント */
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	/** ねぎぃメッセージ用フォント */
	private static final Font NEGI_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 120);

	// アイテムカーソル
	private static final Stroke ITEM_CUR_STROKE = new BasicStroke(3.0f);
	private static final Color ITEM_CUR_COLOR = new Color(0, 0, 0);
	private static final Ellipse2D.Float ITEM_CUR_SHAPE = new Ellipse2D.Float(-16, -16, 32, 32);

	/** 計算テンポラリ */
	private static Point4y tmpPoint = new Point4y();
	/** 画像描画用の標準汎用長方形 */
	private static Rectangle4y tmpRect = new Rectangle4y();

	// ini設定
	/** ログダイアログへの出力有無 */
	private static int logOutput = 1;
	/** 赤ゆサイズのうんうんの影描写の有無 */
	private static int drawShadowShitBaby = 1;
	/** 赤ゆサイズの吐餡の影描写の有無 */
	private static int drawShadowVomitBaby = 1;

	/**
	 * 全画像の読み込み
	 * 
	 * @param isBg     背景を読み込むか否か
	 * @param isItem   アイテム画像を読み込むか否か
	 * @param isEffect エフェクト画像を読み込むか否か
	 * @param isBody   ゆっくり(通常種のみ)を読み込むか否か
	 * @param isAttach アタッチメント一式を読み込むか否か
	 * @param isIni    iniファイルの読み込み
	 */
	public void loadImage(boolean isBg, boolean isItem, boolean isEffect, boolean isBody, boolean isAttach,
			boolean isIni) {
		ImageLoadService.loadImages(this, isBg, isItem, isEffect, isBody, isAttach, isIni);
	}

	/**
	 * ゆっくり用遅延読み込み
	 *
	 * @param type 読み込むゆっくりの種
	 */
	public void loadBodyImage(YukkuriType type) {
		ImageLoadService.loadBodyImage(this, type);
	}

	/** 背景ファイルリロード */
	public void loadTerrainFile() {
		ClassLoader loader = this.getClass().getClassLoader();
		TerrainField.loadTerrain(GameWorld.get().getNextMap(), loader, this);
		System.gc();
	}

	/**
	 * ズーム時の中心の表示倍率
	 * 
	 * @param hint オブジェクト
	 */
	public void setRenderScale(Object hint) {
		renderScale = hint;
	}

	/**
	 * 背景画像用バッファ作成
	 *
	 */
	public void createBackBuffer() {
		backBuffer = new BufferedImage(Translate.getBufferW(), Translate.getBufferH(), BufferedImage.TYPE_3BYTE_BGR);
		backBufferG2 = backBuffer.createGraphics();
		backBufferG2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}

	BufferedImage getBackBuffer() {
		return backBuffer;
	}

	Graphics2D getBackBufferG2() {
		return backBufferG2;
	}

	@Override
	public void run() {
		new GameLoop(this).run();
	}

	ClassLoader getImageLoader() {
		return ImageLoadService.getImageLoader(this);
	}

	/** ゆっくり追加用クラス */
	public class MyAddYukkuriListener implements ItemListener, ActionListener {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void itemStateChanged(ItemEvent e) {
			boolean isJp = GameLocale.isJapanese();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource() == cb3) {
					if (cb3.getSelectedIndex() == 0) {
						String[] namesCommon = isJp ? namesCommonJ : namesCommonE;
						cb1.setModel(new DefaultComboBoxModel(namesCommon));
					} else if (cb3.getSelectedIndex() == 1) {
						String[] namesRare = isJp ? namesRareJ : namesRareE;
						cb1.setModel(new DefaultComboBoxModel(namesRare));
					} else {
						String[] namesPredator = isJp ? namesPredatorJ : namesPredatorE;
						cb1.setModel(new DefaultComboBoxModel(namesPredator));
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

		String[] tempAges = { GameText.read("draw_baby"),
				GameText.read("draw_child"), GameText.read("draw_adult") };
		ages = tempAges;
		String[] tempRare = { GameText.read("draw_normalsp"),
				GameText.read("draw_raresp"), GameText.read("draw_predsp") };
		rare = tempRare;
		String[] tempo = { GameText.read("yes"), GameText.read("no") };
		options = tempo;
		String[] tempmode = { GameText.read("off"), GameText.read("draw_normalsp"),
				GameText.read("draw_raresp"), GameText.read("draw_normraresp") };
		mode = tempmode;
		String[] tempnum = { "1", "2", "3", "4", "5", "10", "50", "100" };
		num = tempnum;
		mess1 = GameText.read("draw_addmes");
		mess2 = GameText.read("draw_addmore");
		mess3 = GameText.read("draw_randommode");
		mess4 = GameText.read("draw_addnum");
		mess5 = GameText.read("draw_raperize");

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
				cb1.setModel(new DefaultComboBoxModel(GameLocale.isJapanese() ? namesCommonJ : namesCommonE));
			} else {
				cb1.setModel(new DefaultComboBoxModel(GameLocale.isJapanese() ? namesRareJ : namesRareE));
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
			cb5.setPreferredSize(new java.awt.Dimension(60, 32));
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
			panel.setPreferredSize(new java.awt.Dimension(400, 180));

			int ret = JOptionPane.showConfirmDialog(this, panel, SimYukkuri.TITLE, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (!shouldProceedAfterAddDialog(ret)) {
				break;
			}

			int maxNum;
			try {
				maxNum = Integer.parseInt(cb5.getSelectedItem().toString());
			} catch (NumberFormatException ne) {
				maxNum = 1;
			}
			final int fMaxNum = maxNum;
			final int fRndType = cb4.getSelectedIndex();
			final int fBaseType = cb1.getSelectedIndex();
			final int fRareType = cb3.getSelectedIndex();
			final int fAgeType = cb2.getSelectedIndex();
			final boolean fRaper = cb6.isSelected();
			final String loadingMsg = GameLocale.isJapanese() ? "読み込み中..." : "Loading...";
			SimYukkuri.simYukkuri.runWithLoadingDialog(loadingMsg, () -> {
				for (int i = 0; i < fMaxNum; i++) {
					int selectType;
					int selectAge;
					switch (fRndType) {
						case 0:
						default:
							selectType = fBaseType;
							if (fRareType == 1) {
								selectType += 1000;
							} else if (fRareType == 2) {
								selectType += 3000;
							}
							selectAge = fAgeType;
							break;
						case 1:
							selectType = GameRandom.nextInt(namesCommonJ.length);
							selectAge = GameRandom.nextInt(3);
							break;
						case 2:
							selectType = GameRandom.nextInt(namesRareJ.length) + 1000;
							selectAge = GameRandom.nextInt(3);
							break;
						case 3:
							int selectRare = GameRandom.nextInt(2);
							switch (selectRare) {
								case 0:
								default:
									selectType = GameRandom.nextInt(namesCommonJ.length);
									break;
								case 1:
									selectType = GameRandom.nextInt(namesRareJ.length) + 1000;
									break;
							}
							selectAge = GameRandom.nextInt(3);
							break;
					}

					boolean imageNagasiMode = false;
					if (selectType == Reimu.type && GameRandom.nextInt(20) == 0)
						selectType = WasaReimu.type;
					if (selectType == Reimu.type && GameRandom.nextInt(15) == 0)
						selectType = Deibu.type;
					if (selectType == Marisa.type && GameRandom.nextInt(50) == 0)
						selectType = MarisaTsumuri.type;
					if (selectType == Marisa.type && GameRandom.nextInt(50) == 0)
						selectType = MarisaKotatsumuri.type;
					if (selectType == Ayaya.type && GameRandom.nextInt(20) == 0)
						selectType = Kimeemaru.type;

					// if(selectType == Reimu.type || selectType == Marisa.type)
					{
						if (SimYukkuri.NAGASI_MODE == 1) {
							imageNagasiMode = true;
						}
						if (SimYukkuri.NAGASI_MODE == 2) {
							if (GameRandom.nextInt(20) == 0) {
								imageNagasiMode = true;
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
					Body b;
					synchronized (SimYukkuri.lock) {
						b = terrarium.makeBody(GameRandom.nextInt(Translate.getMapW()),
								GameRandom.nextInt(Translate.getMapH()), 0, selectType,
								null, age, null, null, true);
					}
					b.addAge(256);
					if (fRaper) {
						b.setRaper(true);
					} else {
						b.setRaper(false);
					}
					b.setImageNagasiMode(imageNagasiMode);

					bodies.add(b);
				}
			});
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

	static boolean shouldProceedAfterAddDialog(int result) {
		return result == JOptionPane.OK_OPTION;
	}

	/**
	 * ゆっくり用描画位置の計算
	 * 
	 * @param origin 対象ゆっくりの原点
	 * @param spr    対象ゆっくりのスプライト
	 */
	void calcDrawBodyPosition(Point4y origin, Sprite spr) {
		int sizeW = Translate.transSize(spr.getImageW());
		int sizeH = Translate.transSize(spr.getImageH());
		int pivX = Translate.transSize(spr.getPivotX());
		int pivY = Translate.transSize(spr.getPivotY());

		// 左右両向きの描画範囲を作成
		spr.calcScreenRect(origin, pivX, pivY, sizeW, sizeH);
	}

	/**
	 * 汎用の描画位置の計算
	 *
	 * @param o    描画対象オブジェクト
	 * @param rect オブジェクトの占有長方形
	 */
	void calcDrawPosition(Obj o, Rectangle4y rect) {
		int sizeW = Translate.transSize(o.getW());
		int sizeH = Translate.transSize(o.getH());
		int pivX = Translate.transSize(o.getPivotX());
		int pivY = Translate.transSize(o.getPivotY());
		Translate.translate(o.getX(), o.getY(), tmpPoint);
		rect.setX(tmpPoint.getX() - pivX);
		rect.setY(tmpPoint.getY() - pivY);
		rect.setWidth(sizeW);
		rect.setHeight(sizeH);

		// オブジェクトに描画情報を設定
		// この描画範囲を使用してマウスとの当たり判定を取得
		o.setScreenPivot(tmpPoint);
		o.setScreenRect(rect);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void paint(Graphics g) {
		new Renderer().render(this, g);
	}

	/** 文字メッセージの表示 */
	int drawStringMultiLine(Graphics2D g2d, String str, int posX, int posY, int width, boolean flag) {
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

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	int getLogOutput() {
		return logOutput;
	}

	public Terrarium getTerrarium() {
		return terrarium;
	}

	public void setTerrarium(Terrarium terrarium) {
		this.terrarium = terrarium;
	}


	public List<Obj> getList4sort() {
		return list4sort;
	}

	public void setList4sort(List<Obj> list4sort) {
		this.list4sort = list4sort;
	}

	public List<Body> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<Body> msgList) {
		this.msgList = msgList;
	}

	public int[] getPosTmp() {
		return posTmp;
	}

	public void setPosTmp(int[] posTmp) {
		this.posTmp = posTmp;
	}

	public BufferedImage[] getLayerTmp() {
		return layerTmp;
	}

	public void setLayerTmp(BufferedImage[] layerTmp) {
		this.layerTmp = layerTmp;
	}

	Point4y getTmpPoint() {
		return tmpPoint;
	}

	Rectangle4y getTmpRect() {
		return tmpRect;
	}

	public Object getRenderScale() {
		return renderScale;
	}

	public static Stroke getItemCurStroke() {
		return ITEM_CUR_STROKE;
	}

	public static Color getItemCurColor() {
		return ITEM_CUR_COLOR;
	}

	public static Ellipse2D.Float getItemCurShape() {
		return ITEM_CUR_SHAPE;
	}

	static Stroke getDefaultStroke() {
		return DEFAULT_STROKE;
	}

	static Font getDefaultFont() {
		return DEFAULT_FONT;
	}

	static Font getNegiFont() {
		return NEGI_FONT;
	}

	static int getDrawShadowShitBaby() {
		return drawShadowShitBaby;
	}

	static int getDrawShadowVomitBaby() {
		return drawShadowVomitBaby;
	}
}
