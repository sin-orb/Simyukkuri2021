package src;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetAction;
import src.command.GadgetMenu;
import src.command.GadgetMenu.ActionControl;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.draw.ModLoader;
import src.draw.MyPane;
import src.draw.ObjDrawComp;
import src.draw.TerrainField;
import src.draw.Terrarium;
import src.draw.Translate;
import src.draw.World;
import src.game.Dna;
import src.game.Stalk;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.Farm;
import src.item.Pool;
import src.system.FieldShapeBase;
import src.system.IconPool;
import src.system.ItemMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.MessagePool;

/**
 * しむゆっくりメイン
 */
public class SimYukkuri extends JFrame {
	static final long serialVersionUID = 1L;
	/** アプリ名 */
	public static final String TITLE = "しむゆっくり/SimYukkuri";
	/** バージョン */
	public static final String VERSION = "Ver.2021/05/02リファクタリング版";
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
	public static final String[] fieldScaleTbl = { "x0.5", "x1", "x2" };//, "x4", "x8"};
	/** フィールド大きさ */
	public static final int[] fieldScaleData = { 50, 100, 200, 400, 800 };
	/** バッファ大きさ */
	public static final int[] bufferSizeData = { 50, 100, 200, 400, 200 };
	/** フィールド倍率レート */
	public static final float fieldZoomRate[][] = {
			{ 1.0f },
			{ 1.0f, 0.8f, 0.7f, 0.6f, 0.5f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.25f },
			{ 1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f, 0.125f },
	};

	/** 標準ウィンドウのテラリウム描画エリアのサイズ */
	public static final int[] PAINT_PANE_X = { 900, 1260 }, PAINT_PANE_Y = { 700, 980 };
	/** 標準ウィンドウのテラリウム描画エリアのアスペクト比 */
	public static final double ASPECT = 1.29;
	/** 標準ウィンドウの内部マップサイズ */
	public static final int[] DEFAULT_MAP_X = { 300, 500, 500 }, DEFAULT_MAP_Y = { 300, 500, 500 },
			DEFAULT_MAP_Z = { 100, 150, 150 };
	/** ペインのindex */
	public static int paintPaneMode = 0;

	private static int windowType, scaleIndex;
	/** 自オブジェクト */
	public static SimYukkuri simYukkuri = null;
	/** 世界 */
	public static World world = null;
	/** ペイン */
	public static MyPane mypane = new MyPane();
	/** スレッド */
	static Thread mythread;
	/** ドラッグ始点 */
	public static int fieldSX = -1, fieldSY = -1;
	/** ドラッグ終点 */
	public static int fieldEX = -1, fieldEY = -1;
	/** 「フィールド」オブジェクトのタイプ */
	public static int fieldType = 0;

	/** マウス処理の座標保存*/
	public static int mouseNewX = 0, mouseNewY = 0, mouseOldX = 0, mouseOldY = 0, mouseVX = 0, mouseVY = 0;
	/**マウススクロール処理の座標保存*/
	public static int scrollOldX = 0, scrollOldY = 0;
	/**マウスのポイント座標*/
	public static int[] fieldMousePos = new int[2];
	/**精子餡インスタンス*/
	public static Dna sperm = null;
	/** ランダム */
	public static Random RND = new Random();

	/**
	 * コンストラクタ
	 */
	public SimYukkuri() {

		super(TITLE + "  " + VERSION);

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
		JPanel ui = MainCommandUI.createInterface(PAINT_PANE_Y[paintPaneMode]);

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
		MessagePool.loadMessage(loader);
		mypane.loadImage(true, true, true, true, true, true);
		mypane.createBackBuffer();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(mainPane);
		setResizable(false);
		setVisible(true);

		// 初期設定

		NAGASI_MODE = ModLoader.loadBodyIniMapForInt(loader, ModLoader.DATA_WORLD_INI_DIR, "play", "NAGASI_MODE");
	}

	/**
	 * セーブする.
	 */
	public void doSave() {
		synchronized (lock) {
			final JFileChooser fc = new JFileChooser(ModLoader.getJarPath());
			int result = fc.showSaveDialog(SimYukkuri.this);
			if (result != JFileChooser.APPROVE_OPTION)
				return;
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
		synchronized (lock) {
			final JFileChooser fc = new JFileChooser(ModLoader.getJarPath());
			int result = fc.showOpenDialog(SimYukkuri.this);
			if (result != JFileChooser.APPROVE_OPTION)
				return;
			File file = fc.getSelectedFile();
			try {
				Terrarium.loadState(file);
			} catch (IOException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE,
						JOptionPane.ERROR_MESSAGE);
			} catch (ClassNotFoundException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(SimYukkuri.this, e.getLocalizedMessage(), SimYukkuri.TITLE,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * メインメソッド
	 * @param args 引数
	 */
	public static void main(String[] args) {

		try {
			ModLoader.setJarPath();

			if (simYukkuri == null) {
				simYukkuri = new SimYukkuri();
			}
			Translate.createTransTable(TerrainField.isPers());
			mypane.isRunning = true;
			mythread = new Thread(mypane);
			mythread.start();
		} catch (OutOfMemoryError e) {
			JOptionPane.showMessageDialog(null, "メモリ不足です");
			System.exit(0);
		}
	}

	/**
	 * キーリスナ
	 */
	public class MyKeyListener implements KeyListener {
		private int savedGameSpeed;

		/**
		 * キー押下
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				synchronized (SimYukkuri.lock) {
					if (MainCommandUI.selectedGameSpeed != 0) {
						savedGameSpeed = MainCommandUI.selectedGameSpeed;
						MainCommandUI.selectedGameSpeed = 0;
					} else {
						MainCommandUI.selectedGameSpeed = savedGameSpeed;
					}
					MainCommandUI.gameSpeedCombo.setSelectedIndex(MainCommandUI.selectedGameSpeed);
				}
				break;
			case KeyEvent.VK_DELETE:
				synchronized (SimYukkuri.lock) {
					GadgetAction.immediateEvaluate(GadgetList.ALL);
				}
				break;
			case KeyEvent.VK_Z:
				synchronized (SimYukkuri.lock) {
					if (Translate.addZoomRate(-1)) {
						Point mpos = getMousePosition();
						Translate.setBufferZoom();
						if (mpos != null) {
							Translate.transCanvasToField(mpos.x, mpos.x, fieldMousePos);
							Translate.setBufferCenterPos(fieldMousePos[0], fieldMousePos[1]);
						}
					}
				}
				break;
			case KeyEvent.VK_X:
				synchronized (SimYukkuri.lock) {
					if (Translate.addZoomRate(1)) {
						Point mpos = getMousePosition();
						Translate.setBufferZoom();
						if (mpos != null) {
							Translate.transCanvasToField(mpos.x, mpos.x, fieldMousePos);
							Translate.setBufferCenterPos(fieldMousePos[0], fieldMousePos[1]);
						}
					}
				}
				break;
			case KeyEvent.VK_C:
				synchronized (SimYukkuri.lock) {
					Translate.setZoomRate(0);
					Translate.setBufferPos(0, 0);
					Translate.setBufferZoom();
				}
				break;
			case KeyEvent.VK_W:
				synchronized (SimYukkuri.lock) {
					Translate.addBufferPos(0, -Translate.getDisplayArea().height / 3);
				}
				break;
			case KeyEvent.VK_S:
				synchronized (SimYukkuri.lock) {
					Translate.addBufferPos(0, Translate.getDisplayArea().height / 3);
				}
				break;
			case KeyEvent.VK_A:
				synchronized (SimYukkuri.lock) {
					Translate.addBufferPos(-Translate.getDisplayArea().width / 3, 0);
				}
				break;
			case KeyEvent.VK_D:
				synchronized (SimYukkuri.lock) {
					Translate.addBufferPos(Translate.getDisplayArea().width / 3, 0);
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}

	/**
	 * マウスリスナ
	 */
	public class MyMouseListener extends MouseAdapter {
		private Cursor cr = new Cursor(Cursor.HAND_CURSOR);
		private Cursor defCr = new Cursor(Cursor.DEFAULT_CURSOR);
		private Obj grabbedObj = null;
		private int startY = -1, startZ = -1;
		private int oX = 0, oY = 0;
		@SuppressWarnings("unused")
		private int altitude = 0;
		private Point translatePos = new Point();
		private Rectangle imageRect = new Rectangle();
		private List<Obj> list4sort = new LinkedList<Obj>();

		// マウス位置の最も手前にあるオブジェクトを取得
		private Obj getUpFront(int mx, int my, boolean stalkMode) {
			// Sort the objects according as Y position.
			list4sort.clear();
			list4sort.addAll(world.getYukkuriList());
			list4sort.addAll(world.getFixObjList());
			list4sort.addAll(world.getObjectList());

			Collections.sort(list4sort, ObjDrawComp.INSTANCE);
			// Check whether hit or not.
			Obj found = null;
			Obj parent = null;
			Body body = null;
			Stalk stalk = null;
			int num = list4sort.size() - 1;
			Obj o = null;
			for (int i = num; i >= 0; i--) {
				o = list4sort.get(i);

				// そもそも掴めないオブジェクトはスキップ
				if (!o.isCanGrab())
					continue;

				// 画面内での描画矩形内にカーソルがあるかチェック
				if (stalkMode && o instanceof Body) {
					// 茎ひっこぬきの場合はゆっくりにヒットしてもついてる茎を取得
					body = (Body) o;
					if (body.getStalks() != null && body.getStalks().size() > 0) {
						parent = body.getStalks().get(0);
					} else {
						parent = body;
					}
				} else if (!stalkMode && o instanceof Stalk) {
					// 茎ひっこぬき無効の場合は茎にヒットしても元のゆっくりを取得
					stalk = (Stalk) o;
					if (stalk.getPlantYukkuri() != null) {
						parent = stalk.getPlantYukkuri();
					} else {
						parent = stalk;
					}
				} else {
					parent = o;
				}

				Rectangle screenRect = parent.getScreenRect();
				if (screenRect.contains(mx, my)) {
					// ヒットしたら画像の原点からの位置を記録
					if (parent instanceof Body) {
						// ゆっくりの場合は膨らむので実サイズを取得
						((Body) parent).getExpandShape(imageRect);
					} else {
						parent.getBoundaryShape(imageRect);
					}
					found = parent;
					oX = screenRect.x + Translate.transSize(imageRect.x) - mx;
					oY = screenRect.y + Translate.transSize(imageRect.y) - my;
					break;
				}
			}

			if (found == null) {// platform has lowest priority.
				List<ObjEX> platformList = world.getPlatformList();
				for (Iterator<ObjEX> i = platformList.iterator(); i.hasNext();) {
					ObjEX oex = (ObjEX) i.next();
					Rectangle screenRect = oex.getScreenRect();
					oex.getBoundaryShape(imageRect);
					if (oex instanceof BeltconveyorObj) {
						if (((BeltconveyorObj) oex).checkContain(mx, my, true)) {
							found = oex;
							oX = screenRect.x + Translate.transSize(imageRect.x) - mx;
							oY = screenRect.y + Translate.transSize(imageRect.y) - my;
						}
					} else {
						if (screenRect.contains(mx, my)) {
							found = oex;
							oX = screenRect.x + Translate.transSize(imageRect.x) - mx;
							oY = screenRect.y + Translate.transSize(imageRect.y) - my;
						}
					}
				}
			}
			return found;
		}

		// マウス位置の最も手前にあるシェイプを取得
		private FieldShapeBase getShapeFront(int mx, int my) {

			Point pos = Translate.invert(mx, my);
			if (pos == null)
				return null;
			// フラグマップから大まかな判定取得
			MapPlaceData curMap = SimYukkuri.world.getCurrentMap();
			int flags = Translate.getCurrentFieldMapNum(pos.x, pos.y);
			// コンベア
			if ((flags & FieldShapeBase.FIELD_BELT) != 0) {
				int num = curMap.beltconveyor.size();
				for (int i = num - 1; i >= 0; i--) {
					Beltconveyor b = curMap.beltconveyor.get(i);
					if (b.mapContains(pos.x, pos.y)) {
						return b;
					}
				}
			}
			// 畑
			if ((flags & FieldShapeBase.FIELD_FARM) != 0) {
				int num = curMap.farm.size();
				for (int i = num - 1; i >= 0; i--) {
					Farm b = curMap.farm.get(i);
					if (b.mapContains(pos.x, pos.y)) {
						return b;
					}
				}
			}
			// 池
			if ((flags & FieldShapeBase.FIELD_POOL) != 0) {
				int num = curMap.pool.size();
				for (int i = num - 1; i >= 0; i--) {
					Pool b = curMap.pool.get(i);
					if (b.mapContains(pos.x, pos.y)) {
						return b;
					}
				}
			}
			return null;
		}

		/** マウスクリック */
		public void mouseClicked(MouseEvent e) {
			synchronized (lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				// 右クリック表示
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) {
					if (SwingUtilities.isRightMouseButton(e)) {
						// フィールド製作中はキャンセル動作
						if (fieldSX > -1 || fieldSY > -1) {
							fieldSX = -1;
							fieldSY = -1;
							fieldEX = -1;
							fieldEY = -1;
							ItemMenu.itemModeCancel(true);
							return;
						}

						// クリック対象を検索
						Obj found = getUpFront(fieldMousePos[0], fieldMousePos[1], false);
						if (found == null) {
							// シェイプの有無をチェック
							FieldShapeBase foundShape = getShapeFront(fieldMousePos[0], fieldMousePos[1]);
							if (foundShape != null && foundShape.hasShapePopup() != ShapeMenuTarget.NONE) {
								ItemMenu.setShapePopupMenu(foundShape);
								ItemMenu.shapePopup.show(mypane, e.getX() + 10, e.getY());
							} else {
								// 何も無いスペースを右クリック
								ItemMenu.itemModeCancel(true);
								// ガジェットメニュー表示
								if (GadgetMenu.popupDisplay) {
									GadgetMenu.popup.setVisible(false);
									GadgetMenu.popupDisplay = false;
								} else {
									GadgetMenu.popup.show(mypane, e.getX(), mypane.getY());
									GadgetMenu.popupDisplay = true;
								}
							}
						} else {
							// オブジェクトを右クリック
							if (SimYukkuri.world.player.holdItem == null) {
								// 手にアイテムを持っていない場合
								if (found.hasGetPopup() != ItemMenu.GetMenuTarget.NONE) {
									// アイテム取得メニュー表示
									ItemMenu.setGetPopupMenu(found);
									ItemMenu.getPopup.show(mypane, e.getX() + 10, e.getY());
								} else {
									ItemMenu.itemModeCancel(true);
									// 道具の場合スイッチ切り替え
									if (found instanceof ObjEX) {
										ObjEX oex = (ObjEX) found;
										oex.invertEnabled();
									}
								}
								return;
							} else {
								// 手に持っている場合
								if (found.hasUsePopup() != ItemMenu.UseMenuTarget.NONE) {
									// アイテム使用メニュー表示
									ItemMenu.usePopup.show(mypane, e.getX() + 10, e.getY());
								}
							}
						}
					}
					return;
				}
				GadgetMenu.popupDisplay = false;
				ItemMenu.itemModeCancel(false);

				// 左クリック処理
				if (SimYukkuri.world.player.holdItem != null) {
					// 手にアイテムを持っている場合は置く
					ItemMenu.dropItem(e);
					return;
				}
				// 選択コマンド取得
				GadgetList sel = GadgetMenu.getCurrentGadget();
				if (sel == null)
					return;

				// 即時発効型アクションはここで実行
				if (sel.getActionTarget() == ActionTarget.IMMEDIATE) {
					GadgetAction.immediateEvaluate(sel);
					return;
				}

				// マウスカーソル位置の最も手前にあるオブジェクトを取得
				boolean stalkMode = false;
				if (sel == GadgetList.STALK_UNPLUG) {
					stalkMode = true;
				}
				Obj found = getUpFront(fieldMousePos[0], fieldMousePos[1], stalkMode);
				ActionTarget foundType = ActionTarget.TERRAIN;

				if (found instanceof Body) {
					MainCommandUI.showStatus((Body) found);
					MyPane.selectBody = (Body) found;
					foundType = ActionTarget.BODY;
				} else if (found != null) {
					foundType = ActionTarget.GADGET;
				}

				// クリックした対象とコマンドが一致したら処理実行
				if (sel.getActionControl() == ActionControl.LEFT_CLICK) {
					if ((sel.getActionTarget().getFlag() & foundType.getFlag()) != 0
							|| sel.getActionTarget() == ActionTarget.WALL
							|| sel.getActionTarget() == ActionTarget.FIELD) {
						GadgetAction.leftClickEvaluate(sel, found, e, fieldMousePos);
					}
				} else if (sel.getActionControl() == ActionControl.LEFT_MULTI_CLICK) {
					if ((sel.getActionTarget().getFlag() & foundType.getFlag()) != 0) {
						GadgetAction.leftMultiClickEvaluate(sel, found, e, fieldMousePos);
					}
				}
			}
		}

		/** マウスボタンを押しただけ */
		public void mousePressed(MouseEvent e) {
			synchronized (lock) {
				ItemMenu.itemModeCancel(false);
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if (e.isShiftDown()) {
					scrollOldX = e.getX();
					scrollOldY = e.getY();
					return;
				}

				GadgetList sel = GadgetMenu.getCurrentGadget();
				// 左ボタンの場合は持ち上げツール,すりすりのみ使用
				if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
					if ((sel != GadgetList.PICKUP) && (sel != GadgetList.SURISURI)) {
						return;
					}
				}

				// ホイールクリックは無視
				if (SwingUtilities.isMiddleMouseButton(e)) {
					return;
				}

				if (grabbedObj != null) {
					return;
				}
				// マウスカーソル位置の最も手前にあるオブジェクトを取得
				boolean stalkMode = false;
				if (sel == GadgetList.STALK_UNPLUG) {
					stalkMode = true;
				}

				grabbedObj = getUpFront(fieldMousePos[0], fieldMousePos[1], stalkMode);
				if (grabbedObj != null) {
					// すりすり
					if (((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) && (sel == GadgetList.SURISURI)) {
						if (sel == GadgetList.SURISURI) {
							if (grabbedObj instanceof Body) {
								Body b = (Body) grabbedObj;
								b.setbSurisuriFromPlayer(true);
							}
						}
					} else {

						// yukkuri has been grabbed.
						startY = fieldMousePos[1];
						startZ = fieldMousePos[1] + Translate.transSize(grabbedObj.getZ() * 58 / 10);
						grabbedObj.grab();
						if (grabbedObj instanceof Body) {
							MainCommandUI.showStatus((Body) grabbedObj);
							MyPane.selectBody = (Body) grabbedObj;
						}
					}
				}
			}
		}

		/** マウスボタンを離す */
		public void mouseReleased(MouseEvent e) {
			synchronized (lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if ((e.getModifiers() & (MouseEvent.BUTTON1_MASK | MouseEvent.BUTTON3_MASK)) == 0) {
					return;
				}

				if (grabbedObj != null) {
					if (grabbedObj instanceof Body) {
						Body body = (Body) grabbedObj;
						if (body.isPullAndPush()) {
							body.releaseLockNobinobi();
						}

						// すりすり解除
						body.setbSurisuriFromPlayer(false);

						if (body.canflyCheck()) {
							if (grabbedObj.getZ() > 0) {
								grabbedObj.kick(0, 0, 0);
							}
						} else {
							if (grabbedObj.getZ() > 0) {
								grabbedObj.kick(SimYukkuri.mouseVX / 15, 0, SimYukkuri.mouseVY / 20);
							}
						}
					} else {
						if (grabbedObj.getZ() > 0) {
							grabbedObj.kick(SimYukkuri.mouseVX / 15, 0, SimYukkuri.mouseVY / 20);
						}
					}
					grabbedObj.release();
					grabbedObj = null;
					startY = -1;
					startZ = -1;
					altitude = 0;
				}
			}
		}

		/** マウスドラッグ中 */
		public void mouseDragged(MouseEvent e) {
			synchronized (lock) {
				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);

				if (e.isShiftDown()) {
					int dx = (int) ((float) (scrollOldX - e.getX()) * Translate.getCurrentZoomRate());
					int dy = (int) ((float) (scrollOldY - e.getY()) * Translate.getCurrentZoomRate());
					Translate.addBufferPos(dx, dy);
					scrollOldX = e.getX();
					scrollOldY = e.getY();
					return;
				}

				GadgetList sel = GadgetMenu.getCurrentGadget();

				if (grabbedObj != null) {
					int button = 1;
					if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
						button = 1;
					} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
						button = 2;
					} else {
						return;
					}

					if ((button == 1) && (sel == GadgetList.PICKUP)) {
						// 持ち上げ
						// 画面上のマウス移動量をマップ座標に変換
						int newX = fieldMousePos[0] + oX;
						int newY = startY;
						int newZ = startZ - fieldMousePos[1];
						int hitX;
						// ずらした位置をマップ座標に変換してオブジェクトに反映
						switch (grabbedObj.getObjType()) {
						case YUKKURI:
							Body b = (Body) grabbedObj;
							if (b.isPullAndPush() && !b.isDead()) {
								b.wakeup();
								if (b.getZ() <= 0)
									b.lockSetZ(newZ * Translate.mapZ / Translate.canvasH);
							} else {
								hitX = 4;
								altitude = startZ - fieldMousePos[1];
								Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
								grabbedObj.setX(translatePos.x);
								if (newZ > 0) {
									grabbedObj.setZ(translatePos.y);
								}
							}
							break;
						case SHIT:
						case VOMIT:
						case OBJECT:
						case FIX_OBJECT:
							hitX = grabbedObj.getPivotX();
							altitude = startZ - fieldMousePos[1];
							Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
							grabbedObj.setX(translatePos.x);
							grabbedObj.setZ(translatePos.y);
							break;
						case PLATFORM:
							hitX = grabbedObj.getPivotX();
							altitude = startZ - fieldMousePos[1];
							Translate.invertFlying(newX, newY, newZ, hitX, translatePos);
							grabbedObj.setX(translatePos.x);
							break;
						default:
							hitX = 1;
							break;
						}
					}

					else if (button == 2) {
						// 新しいマウス座標から原点計算
						int newX = fieldMousePos[0] + oX;
						int newY = fieldMousePos[1] + oY;
						int hitX;
						int hitY;
						switch (grabbedObj.getObjType()) {
						case YUKKURI:
							hitX = 4;
							hitY = 4;
							break;
						case SHIT:
						case VOMIT:
						case OBJECT:
						case FIX_OBJECT:
							hitX = grabbedObj.getPivotX();
							hitY = 4;
							break;
						case PLATFORM:
							if (grabbedObj instanceof BeltconveyorObj) {
								hitX = 4;
								hitY = grabbedObj.getPivotY();
							} else {
								hitX = grabbedObj.getPivotX();
								hitY = grabbedObj.getPivotY();
							}
							break;
						default:
							hitX = 1;
							hitY = 1;
							break;

						}
						// ずらした位置をマップ座標に変換してオブジェクトに反映
						Translate.invertGround(newX, newY, hitX, hitY, translatePos);
						grabbedObj.setX(translatePos.x);
						grabbedObj.setY(translatePos.y);
					}
					// すりすり
					if ((button == 1) && (sel == GadgetList.SURISURI)) {
						Obj found = getUpFront(fieldMousePos[0], fieldMousePos[1], false);
						// 解放
						boolean bOn = true;
						// 何もドラッグされない
						if (found == null) {
							bOn = false;
						} else {
							// ドラッグ対象が変更されたか
							if (grabbedObj != found) {
								bOn = false;
							}
						}

						// 解放
						if (!bOn) {
							if (grabbedObj instanceof Body) {
								Body b = (Body) grabbedObj;
								b.setbSurisuriFromPlayer(false);
								grabbedObj.release();
								grabbedObj = null;
							}
						}
					}
				}
				mouseNewX = fieldMousePos[0];
				mouseNewY = fieldMousePos[1];
			}
		}

		/** カーソル移動 */
		public void mouseMoved(MouseEvent e) {
			// ほぼ毎フレーム呼ばれるので処理は最小限に
			synchronized (lock) {
				GadgetList sel = GadgetMenu.getCurrentGadget();
				if (sel == null)
					return;

				// フィールドツール,ベルトコンベアの場合
				if ((sel.getGroup() != MainCategoryName.BARRIER && sel.getGroup() != MainCategoryName.CONVEYOR)
						|| sel.getInitOption() == 0)
					return;
				if (fieldSX < 0 || fieldSY < 0)
					return;

				Translate.transCanvasToField(e.getX(), e.getY(), fieldMousePos);
				fieldEX = fieldMousePos[0];
				fieldEY = fieldMousePos[1];
				mouseNewX = fieldMousePos[0];
				mouseNewY = fieldMousePos[1];
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(cr);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(defCr);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int select;

			select = MainCommandUI.gameSpeedCombo.getSelectedIndex();
			select += e.getWheelRotation();
			if (select < 0)
				select = 0;
			if (select >= MainCommandUI.gameSpeedCombo.getItemCount())
				select = MainCommandUI.gameSpeedCombo.getItemCount() - 1;
			MainCommandUI.gameSpeedCombo.setSelectedIndex(select);
		}
	}

	/**マウスの運動量取得*/
	public static void checkMouseVel() {
		mouseVX = (mouseNewX - mouseOldX) + mouseVX / 3 * 2;
		mouseVY = (mouseNewY - mouseOldY) + mouseVY / 3 * 2;
		mouseOldX = mouseNewX;
		mouseOldY = mouseNewY;
	}

	/**
	 * ウィンドウの大きさ指定
	 * @param size ウィンドウのサイズ
	 * @param scale 拡大倍率
	 */
	public void setWindowMode(int size, int scale) {
		paintPaneMode = size;

		Insets inset = getInsets();
		setPreferredSize(new Dimension(PAINT_PANE_X[size] + MainCommandUI.MENU_PANE_X, PAINT_PANE_Y[size]));
		setSize(inset.left + inset.right + PAINT_PANE_X[size] + MainCommandUI.MENU_PANE_X,
				inset.top + inset.bottom + PAINT_PANE_Y[size]);
		setLocation(new Point(100, 0));
		Translate.setCanvasSize(PAINT_PANE_X[size], PAINT_PANE_Y[size], fieldScaleData[scale], bufferSizeData[scale],
				fieldZoomRate[scale]);
		mypane.setPreferredSize(new Dimension(Translate.canvasW, Translate.canvasH));
		mypane.setMinimumSize(new Dimension(Translate.canvasW, Translate.canvasH));
		mypane.setMaximumSize(new Dimension(Translate.canvasW, Translate.canvasH));
	}

	/**
	 * フルスクにする処理
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
		setLocation(new Point(0, 0));
		Translate.setCanvasSize(w, h, fieldScaleData[scale], bufferSizeData[scale], fieldZoomRate[scale]);
		mypane.setPreferredSize(new Dimension(Translate.canvasW, Translate.canvasH));
		mypane.setMinimumSize(new Dimension(Translate.canvasW, Translate.canvasH));
		mypane.setMaximumSize(new Dimension(Translate.canvasW, Translate.canvasH));
	}

	/**最初に出てくるウィンドウの作成*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void initTerrariumSize() {
		String[] screen;
		String mess1, mess2, mess3, mess4;
		JComboBox windowModeCombo;
		JComboBox fieldScaleCombo;
		JComboBox bgModCombo;
		JComboBox itemModCombo;
		JComboBox yukkuriModCombo;
		JRadioButton draw1;
		JRadioButton draw2;
		ButtonGroup drawGrp;

		mess1 = "テラリウムサイズ";
		screen = new String[] { "ウィンドウ(900*700)", "ウィンドウ(1260*980)", "フルスクリーン" };

		mess2 = "背景テーマ";
		mess3 = "道具テーマ";
		mess4 = "ゆっくりテーマ";

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2, 1, 0, 0));
		mainPanel.setPreferredSize(new Dimension(450, 220));
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
		draw1 = new JRadioButton("速度優先");
		draw1.setSelected(true);
		drawGrp.add(draw1);
		grpPanel.add(draw1);
		draw2 = new JRadioButton("画質優先");
		drawGrp.add(draw2);
		grpPanel.add(draw2);

		// --> うにょ版試験マージ
		JCheckBox checkboxDebug = new JCheckBox("うにょON");
		grpPanel.add(checkboxDebug);
		// <-- うにょ版試験マージ
		winPanel.add(grpPanel);

		bgModCombo = new JComboBox(ModLoader.getBackThemeList());
		bgModCombo.setSelectedIndex(0);
		modPanel.add(new JLabel(mess2, JLabel.RIGHT));
		modPanel.add(bgModCombo);

		itemModCombo = new JComboBox(ModLoader.getItemThemeList());
		itemModCombo.setSelectedIndex(0);
		modPanel.add(new JLabel(mess3, JLabel.RIGHT));
		modPanel.add(itemModCombo);

		yukkuriModCombo = new JComboBox(ModLoader.getBodyThemeList());
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
			ModLoader.setBodyThemePath(null);
		} else {
			ModLoader.setBodyThemePath(yukkuriModCombo.getSelectedItem().toString());
		}
	}
}
