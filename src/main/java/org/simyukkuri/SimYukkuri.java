package org.simyukkuri;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import org.simyukkuri.command.GadgetMenu;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.draw.TerrainField;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.system.IconPool;
import org.simyukkuri.system.ItemMenu;
import org.simyukkuri.ui.InputController;
import org.simyukkuri.ui.MainCommandUI;
import org.simyukkuri.ui.listener.MouseInputController;
import org.simyukkuri.util.GameLocale;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameText;

/**
 * しむゆっくりメイン
 */
public class SimYukkuri extends JFrame {
	static final long serialVersionUID = 1L;
	/** アプリ名 */
	public static final String TITLE = GameText.read("title");
	/** バージョン */
	public static final String VERSION = GameText.read("version");
	/** うにょフラグ */
	public static boolean UNYO = true;
	/** 流しモード（0:いつもの 1:まりちゃ流しOnly 2：共存環境） */
	public static int NAGASI_MODE = 0;
	/** 初期化済みフラグ */
	public static boolean initialized = false;
	/** ロック用オブジェクト */
	public static final Object lock = new Object();
	/** 時間最小単位 */
	public static final int TICK = 1;
	/** フィールド倍率 */
	public static final String[] fieldScaleTbl = { "x0.5", "x1", "x2" }; // , "x4", "x8"};
	/** フィールド大きさ */
	public static final int[] fieldScaleData = { 50, 100, 200, 400, 800 };
	/** バッファ大きさ */
	public static final int[] bufferSizeData = { 50, 100, 200, 400, 200 };
	/** フィールド倍率レート */
	public static final float[][] fieldZoomRate = {
			{ 1.0f },
			{ 1.0f, 0.8f, 0.7f, 0.6f, 0.5f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.25f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.125f },
	};

	/** 標準ウィンドウのテラリウム描画エリアのサイズ */
	public static final int[] PAINT_PANE_X = { 900, 1260 };
	public static final int[] PAINT_PANE_Y = { 700, 980 };
	/** 標準ウィンドウのテラリウム描画エリアのアスペクト比 */
	public static final double ASPECT = 1.29;
	/** 標準ウィンドウの内部マップサイズ */
	public static final int[] DEFAULT_MAP_X = { 300, 500, 500 };
	public static final int[] DEFAULT_MAP_Y = { 300, 500, 500 };
	public static final int[] DEFAULT_MAP_Z = { 100, 150, 150 };
	/** ペインのindex */
	public static int paintPaneMode = 0;

	private static int windowType;
	private static int scaleIndex;
	/** 自オブジェクト */
	public static SimYukkuri simYukkuri = null;
	/** 世界 */
	public static World world = null;
	/** ペイン */
	public static MyPane mypane = null;
	/** スレッド */
	static Thread mythread;
	/** ドラッグ始点 */
	public static int fieldSX = -1;
	public static int fieldSY = -1;
	/** ドラッグ終点 */
	public static int fieldEX = -1;
	public static int fieldEY = -1;
	/** 「フィールド」オブジェクトのタイプ */
	public static int fieldType = 0;

	/** マウス処理の座標保存 */
	public static int mouseNewX = 0;
	public static int mouseNewY = 0;
	public static int mouseOldX = 0;
	public static int mouseOldY = 0;
	public static int mouseVX = 0;
	public static int mouseVY = 0;
	/** マウススクロール処理の座標保存 */
	public static int scrollOldX = 0;
	public static int scrollOldY = 0;
	/** マウスのポイント座標 */
	public static int[] fieldMousePos = new int[2];
	/** 精子餡インスタンス */
	public static Dna sperm = null;
	/** ランダム */
	public static Random RND = new Random();

	/**
	 * コンストラクタ
	 */
	public SimYukkuri() {
		super(TITLE + "  " + VERSION);

		mypane = new MyPane();

		ClassLoader loader = this.getClass().getClassLoader();

		// アイコン
		try {
			IconPool.loadImages(loader, mypane);
		} catch (IOException e) {
			e.printStackTrace();
		}

		GadgetMenu.createPopupMenu();
		ItemMenu.createPopupMenu();
		mypane.setBorder(new EmptyBorder(0, 0, 0, 0));

		// 右メニュー作成
		final JPanel ui = MainCommandUI.createInterface(PAINT_PANE_Y[paintPaneMode]);

		// setup my pane
		mypane.setFocusable(true);
		MyMouseListener mml = new MyMouseListener();
		mypane.addMouseListener(mml);
		mypane.addMouseMotionListener(mml);
		mypane.addMouseWheelListener(mml);
		MyKeyListener mkl = new MyKeyListener();
		mypane.addKeyListener(mkl);
		// setup root pane
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		mainPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainPane.add(mypane);
		mainPane.add(ui);

		// setup my frame
		initTerrariumSize();
		world = new World(windowType, scaleIndex);
		GameMessages.loadMessage(loader);
		mypane.loadImage(true, true, true, true, true, true);
		mypane.createBackBuffer();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(mainPane);
		setResizable(false);
		setVisible(true);

		// 初期設定

		NAGASI_MODE = ModLoader.loadYukkuriIniValue(loader, ModLoader.getDataWorldIniDir(), "play", "NAGASI_MODE");
	}

	/**
	 * セーブする.
	 */
	public void doSave() {
		synchronized (lock) {
			final JFileChooser fc = new JFileChooser(ModLoader.getJarPath());
			int result = fc.showSaveDialog(SimYukkuri.this);
			if (result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File file = fc.getSelectedFile();
			try {
				Terrarium.saveState(file);
			} catch (IOException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * ロードする.
	 */
	public void doLoad() {
		final JFileChooser fc = new JFileChooser(ModLoader.getJarPath());
		int result = fc.showOpenDialog(SimYukkuri.this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}
		final File file = fc.getSelectedFile();
		String msg = GameLocale.isJapanese() ? "読み込み中..." : "Loading...";
		runWithLoadingDialog(msg, () -> {
			synchronized (lock) {
				try {
					Terrarium.loadState(file);
				} catch (IOException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	/**
	 * ローディングダイアログを表示しながらタスクをバックグラウンドで実行する。
	 *
	 * @param message ダイアログに表示するメッセージ
	 * @param task    実行するタスク
	 */
	public void runWithLoadingDialog(String message, Runnable task) {
		final JDialog loadingDialog = createLoadingDialog(message);
		final Throwable[] err = new Throwable[1];
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				try {
					task.run();
				} catch (Throwable t) {
					err[0] = t;
				}
				return null;
			}

			@Override
			protected void done() {
				loadingDialog.dispose();
				if (err[0] != null) {
					Throwable t = err[0];
					if (t instanceof RuntimeException && t.getCause() != null) {
						t = t.getCause();
					}
					System.out.println(t);
					JOptionPane.showMessageDialog(SimYukkuri.this, t.getLocalizedMessage(), SimYukkuri.TITLE,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		worker.execute();
		loadingDialog.setVisible(true);
	}

	private JDialog createLoadingDialog(String message) {
		String msg = message == null ? "" : message;
		JDialog dialog = new JDialog(SimYukkuri.this, msg, Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setResizable(false);
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(12, 16, 12, 16));
		panel.add(new JLabel(msg), BorderLayout.NORTH);
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		panel.add(bar, BorderLayout.CENTER);
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(SimYukkuri.this);
		return dialog;
	}

	/**
	 * メインメソッド
	 * 
	 * @param args 引数
	 */
	public static void main(String[] args) {

		try {
			ModLoader.setJarPath();

			if (simYukkuri == null) {
				simYukkuri = new SimYukkuri();
			}
			Translate.createTransTable(TerrainField.isPers());
			mypane.setRunning(true);
			mythread = new Thread(mypane);
			mythread.start();
		} catch (OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, GameText.read("outofmemory"));
			System.exit(0);
		}
	}

	/**
	 * キーリスナ
	 */
	public class MyKeyListener implements KeyListener {
		private final InputController controller = new InputController();

		/**
		 * キー押下
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			controller.handleKeyPressed(e, SimYukkuri.this);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			controller.handleKeyReleased(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			controller.handleKeyTyped(e);
		}
	}

	/**
	 * マウスリスナ
	 */
	public class MyMouseListener extends MouseInputController {
		/** コンストラクタ. */
		public MyMouseListener() {
			super(SimYukkuri.this);
		}
	}

	/** マウスの運動量取得 */
	public static void checkMouseVel() {
		mouseVX = (mouseNewX - mouseOldX) + mouseVX / 3 * 2;
		mouseVY = (mouseNewY - mouseOldY) + mouseVY / 3 * 2;
		mouseOldX = mouseNewX;
		mouseOldY = mouseNewY;
	}

	/**
	 * ウィンドウの大きさ指定
	 * 
	 * @param size  ウィンドウのサイズ
	 * @param scale 拡大倍率
	 */
	public void setWindowMode(int size, int scale) {
		paintPaneMode = size;

		Insets inset = getInsets();
		setPreferredSize(new java.awt.Dimension(PAINT_PANE_X[size] + MainCommandUI.MENU_PANE_X, PAINT_PANE_Y[size]));
		setSize(inset.left + inset.right + PAINT_PANE_X[size] + MainCommandUI.MENU_PANE_X,
				inset.top + inset.bottom + PAINT_PANE_Y[size]);
		setLocation(new java.awt.Point(100, 0));
		Translate.setCanvasSize(PAINT_PANE_X[size], PAINT_PANE_Y[size], fieldScaleData[scale], bufferSizeData[scale],
				fieldZoomRate[scale]);
		mypane.setPreferredSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMinimumSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMaximumSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
	}

	/**
	 * フルスクにする処理
	 * 
	 * @param scale 拡大倍率
	 */
	public void setFullScreenMode(int scale) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rect = env.getMaximumWindowBounds();
		// 縦横比を固定するため高さ基準に計算
		int h = rect.height;
		int w = (int) ((double) h * ASPECT);
		if ((w + MainCommandUI.MENU_PANE_X) > rect.width) {
			// 横にUIを足して画面からはみ出たら再計算
			w -= MainCommandUI.MENU_PANE_X;
			h = (int) ((double) w / ASPECT);
		}
		setSize(w + MainCommandUI.MENU_PANE_X, h);
		setLocation(new java.awt.Point(0, 0));
		Translate.setCanvasSize(w, h, fieldScaleData[scale], bufferSizeData[scale], fieldZoomRate[scale]);
		mypane.setPreferredSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMinimumSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
		mypane.setMaximumSize(new java.awt.Dimension(Translate.getCanvasW(), Translate.getCanvasH()));
	}

	/** 最初に出てくるウィンドウの作成 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void initTerrariumSize() {
		final String mess1 = GameText.read("terrarium_size");
		final String[] screen = new String[] { GameText.read("window_900_700"),
				GameText.read("window_1260_980"),
				GameText.read("full_screen") };
		final String mess2 = GameText.read("background_theme");
		final String mess3 = GameText.read("item_theme");
		final String mess4 = GameText.read("yukkuri_theme");
		final JComboBox windowModeCombo;
		final JComboBox fieldScaleCombo;
		final JComboBox bgModCombo;
		final JComboBox itemModCombo;
		final JComboBox yukkuriModCombo;
		final JRadioButton draw1;
		final JRadioButton draw2;
		final ButtonGroup drawGrp;

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1, 0, 0));
		mainPanel.setPreferredSize(new java.awt.Dimension(450, 220));
		JPanel winPanel = new JPanel();
		JPanel grpPanel = new JPanel();
		grpPanel.setLayout(new BoxLayout(grpPanel, BoxLayout.Y_AXIS));
		JPanel modPanel = new JPanel();
		modPanel.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel label = new JLabel(mess1);
		winPanel.add(label);
		windowModeCombo = new JComboBox(screen);
		windowModeCombo.setSelectedIndex(0);
		winPanel.add(windowModeCombo);
		fieldScaleCombo = new JComboBox(fieldScaleTbl);
		fieldScaleCombo.setSelectedIndex(1);
		winPanel.add(fieldScaleCombo);

		drawGrp = new ButtonGroup();
		draw1 = new JRadioButton(GameText.read("priority_speed"));
		draw1.setSelected(true);
		drawGrp.add(draw1);
		grpPanel.add(draw1);
		draw2 = new JRadioButton(GameText.read("priority_quality"));
		drawGrp.add(draw2);
		grpPanel.add(draw2);

		// --> うにょ版試験マージ
		JCheckBox checkboxDebug = new JCheckBox(GameText.read("unyo_on"));
		grpPanel.add(checkboxDebug);
		// <-- うにょ版試験マージ
		winPanel.add(grpPanel);

		bgModCombo = new JComboBox(ModLoader.getBackThemes());
		bgModCombo.setSelectedIndex(0);
		modPanel.add(new JLabel(mess2, JLabel.RIGHT));
		modPanel.add(bgModCombo);

		itemModCombo = new JComboBox(ModLoader.getItemThemes());
		itemModCombo.setSelectedIndex(0);
		modPanel.add(new JLabel(mess3, JLabel.RIGHT));
		modPanel.add(itemModCombo);

		yukkuriModCombo = new JComboBox(ModLoader.getYukkuriThemes());
		yukkuriModCombo.setSelectedIndex(0);
		modPanel.add(new JLabel(mess4, JLabel.RIGHT));
		modPanel.add(yukkuriModCombo);

		mainPanel.add(winPanel);
		mainPanel.add(modPanel);

		int res = JOptionPane.showConfirmDialog(this, mainPanel, SimYukkuri.TITLE, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (res != JOptionPane.OK_OPTION) {
			System.exit(0);
		}

		windowType = windowModeCombo.getSelectedIndex();
		scaleIndex = fieldScaleCombo.getSelectedIndex();

		if (windowType != 2) {
			setWindowMode(windowModeCombo.getSelectedIndex(), fieldScaleCombo.getSelectedIndex());
		} else {
			setFullScreenMode(fieldScaleCombo.getSelectedIndex());
		}

		if (draw1.isSelected()) {
			mypane.setRenderScale(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		} else {
			mypane.setRenderScale(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}

		if (checkboxDebug.isSelected()) {
			UNYO = true;
		} else {
			UNYO = false;
		}

		if (bgModCombo.getSelectedIndex() == 0) {
			ModLoader.setBackThemePath(null);
		} else {
			ModLoader.setBackThemePath(bgModCombo.getSelectedItem().toString());
		}

		if (itemModCombo.getSelectedIndex() == 0) {
			ModLoader.setItemThemePath(null);
		} else {
			ModLoader.setItemThemePath(itemModCombo.getSelectedItem().toString());
		}

		if (yukkuriModCombo.getSelectedIndex() == 0) {
			ModLoader.setYukkuriThemePath(null);
		} else {
			ModLoader.setYukkuriThemePath(yukkuriModCombo.getSelectedItem().toString());
		}
	}
}
