// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) ansi
// Source File Name:   Beltconveyor.java

package src.item;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import src.enums.YukkuriType;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.system.Cash;
import src.system.ResourceUtil;
import src.system.YukkuriFilterPanel;
import src.util.YukkuriUtil;

/***************************************************
 * ベルコン2
 * <br>拡張、YukkuriFilterPanel
 */
public class BeltconveyorObj extends ObjEX implements java.io.Serializable {

	static final long serialVersionUID = 1L;
	public static final int hitCheckObjType = ObjEX.YUKKURI + ObjEX.SHIT + ObjEX.FOOD + ObjEX.TOY + ObjEX.VOMIT
			+ ObjEX.STALK;
	private static final int images_num = 10;
	private static int AnimeImagesNum[] = {
			5, 5
	};
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle boundary = new Rectangle();
	static int hou_default = 0;
	static int obj_default = 0;
	static int move_default = 0;
	static int speed_default = 0;
	static int x_default = 50;
	static int y_default = 50;
	private int beltSpeed;
	private int hou_before = 0;
	private int obj_before = 0;
	private int move_before = 0;
	private int speed_before = 0;
	private int targetType;
	private int cantmove = 0;
	private boolean bMoveOnce = false;
	protected List<Obj> bindObjList = new LinkedList<Obj>(); //　ベルトコンベア上で移動不可能な状態になっているアイテムのリスト

	protected List<YukkuriType> selectedYukkuriType = new LinkedList<YukkuriType>(); // 処理対象のゆっくり
	static protected List<String> istrOptionList = new LinkedList<String>(); // 処理対象設定(オプション)
	protected List<Boolean> obOptionSelectionList = new LinkedList<Boolean>(); // 処理対象設定(オプション)の選択状態

	protected boolean bFilter = false;
	protected int fieldSX;
	protected int fieldSY;
	protected int fieldEX;
	protected int fieldEY;
	protected int firstX;
	protected int firstY;
	protected int[] anPointX = new int[4];
	protected int[] anPointY = new int[4];

	public static enum Action {
		YUKKURI_FILTER(ResourceUtil.getInstance().read("item_filtersettings"), ""),
		;

		private String name;

		Action(String nameJ, String nameE) {
			this.name = nameJ;
		}

		public String toString() {
			return name;
		}
	}

	public static void loadImages(ClassLoader loader, ImageObserver io)
			throws IOException {
		for (int i = 0; i < 10; i++)
			images[i] = ModLoader.loadItemImage(loader,
					(new StringBuilder("beltconveyor/beltconveyor")).append(String.format("%03d", new Object[] {
							Integer.valueOf(i + 1)
					})).append(".png").toString());

		boundary.width = images[0].getWidth(io);
		boundary.height = images[0].getHeight(io);
		boundary.x = boundary.width >> 1;
		boundary.y = boundary.height >> 1;

		// オプション
		istrOptionList.add(ResourceUtil.getInstance().read("attitude_verynice"));
		istrOptionList.add(ResourceUtil.getInstance().read("attitude_nice"));
		istrOptionList.add(ResourceUtil.getInstance().read("attitude_normal"));
		istrOptionList.add(ResourceUtil.getInstance().read("attitude_shithead"));
		istrOptionList.add(ResourceUtil.getInstance().read("attitude_supershithead"));
		istrOptionList.add(ResourceUtil.getInstance().read("intel_badge"));
		istrOptionList.add(ResourceUtil.getInstance().read("intel_normal"));
		istrOptionList.add(ResourceUtil.getInstance().read("intel_fool"));
		istrOptionList.add(ResourceUtil.getInstance().read("item_onlydeadbody"));
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		int frame = 0;

		if (enabled)
			frame = (int) getAge();

		switch (option) { //楽にアニメ指定できるようにしたいが後で
		case 0:
		default:
			layer[0] = images[frame / 4 % AnimeImagesNum[0]]; //4フレームに1回画像更新
			break;
		case 1:
			layer[0] = images[4 - (frame / 4 % AnimeImagesNum[0])];
			break;
		case 2:
			layer[0] = images[9 - frame / 4 % AnimeImagesNum[1]];
			break;
		case 3:
			layer[0] = images[5 + (frame / 4 % AnimeImagesNum[1])];
			break;
		}

		return 1;
	}

	public int getImageLayer(Graphics2D g2, BufferedImage[] layer, Rectangle rect) {
		int[] anPointBaseX = new int[2];
		int[] anPointBaseY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, firstX, firstY, x, y, anPointBaseX, anPointBaseY);
		Translate.getPolygonPoint(anPointBaseX[0], anPointBaseY[0], anPointBaseX[1], anPointBaseY[1], anPointX,
				anPointY);
		//Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, anPointX, anPointY);
		TexturePaint texture = new TexturePaint(layer[0],
				new Rectangle2D.Float(0, 0, layer[0].getWidth() - 10, layer[0].getHeight() - 10));
		g2.setPaint(texture);
		g2.fillPolygon(anPointX, anPointY, 4);
		return 1;
	}

	public Image getImage(int idx) {
		return null;
	}

	public int getImageLayerCount() {
		return 0;
	}

	@Override
	public BufferedImage getShadowImage() {
		return null;
	}

	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, anPointX, anPointY);
		g2.drawPolygon(anPointX, anPointY, 4);
	}

	public static Rectangle getBounding() {
		return boundary;
	}

	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	public boolean checkContain(int inX, int inY, boolean bIsField) {
		int nX = inX;
		int nY = inY;
		if (bIsField) {
			Point pos = Translate.invertLimit(inX, inY);
			nX = pos.x;
			nY = pos.y;
		}

		Point posFirst = Translate.invertLimit(anPointX[0], anPointY[0]);
		Point posSecond = Translate.invertLimit(anPointX[2], anPointY[2]);
		if (posFirst != null && posSecond != null) {
			if (posFirst.x <= nX && nX <= posSecond.x && posFirst.y <= nY && nY <= posSecond.y) {
				return true;
			}
		}
		return false;
	}

	public boolean checkHitObj(Rectangle colRect, Obj o) {
		if ((o instanceof Body) && ((Body) o).isLockmove())
			return false;
		if (o.isRemoved()) {
			bindObjList.remove(o);
		}
		if (o.getZ() == 0) {
			if (checkContain(o.getX(), o.getY(), false)) {
				objHitProcess(o);
				return true;
			}
			if (bindObjList != null && bindObjList.contains(o)) {
				if (o instanceof Body) {
					((Body) o).setbOnDontMoveBeltconveyor(false);
				}
				bindObjList.remove(o);
			}
		}
		return false;
	}

	// 箱を斜めから見下ろしている形式なので奥のxと手前のxで同じ座標でも垂直にならない
	public int objHitProcess(Obj o) {
		int objX = o.getX();
		int objY = o.getY();
		int objW = o.getW();
		int objH = o.getH();
		int attr = 16;

		// フィルター有効時
		if (o instanceof Body && bFilter) {
			Body bodyTarget = (Body) o;
			YukkuriType type = YukkuriUtil.getYukkuriType(bodyTarget.getClass().getSimpleName());
			// ゆっくりタイプチェック
			if (selectedYukkuriType != null && selectedYukkuriType.contains(type)) {
				return 0;
			}
			if (obOptionSelectionList != null && obOptionSelectionList.size() == 9) {
				// 性格
				switch (bodyTarget.getAttitude()) {
				case VERY_NICE:
					if (!obOptionSelectionList.get(0))
						return 0;
					break;
				case NICE:
					if (!obOptionSelectionList.get(1))
						return 0;
					break;
				case AVERAGE:
					if (!obOptionSelectionList.get(2))
						return 0;
					break;
				case SHITHEAD:
					if (!obOptionSelectionList.get(3))
						return 0;
					break;
				case SUPER_SHITHEAD:
					if (!obOptionSelectionList.get(4))
						return 0;
					break;
				}
				// 知性
				switch (bodyTarget.getIntelligence()) {
				case WISE:
					if (!obOptionSelectionList.get(5))
						return 0;
					break;
				case AVERAGE:
					if (!obOptionSelectionList.get(6))
						return 0;
					break;
				case FOOL:
					if (!obOptionSelectionList.get(7))
						return 0;
					break;
				}
				// 死体のみチェック
				if (obOptionSelectionList.get(8)) {
					if (!bodyTarget.isDead()) {
						return 0;
					}
				}
			}
		}

		if (targetType != 0) {
			switch (targetType) {
			case 1:
				if (!(o instanceof Body))
					return 0;
				break;
			case 2:
				if (!(o instanceof Shit || o instanceof Vomit))
					return 0;
				break;
			case 3:
				if (!(o instanceof Food) || o instanceof Shit || o instanceof Vomit || o instanceof Body)
					return 0;
				break;
			case 4:
				if (!(o instanceof Stalk))
					return 0;
				break;
			default:
				if (o instanceof Body)
					return 0;
				break;
			}
		}
		if (o instanceof Body)
			attr = Barrier.MAP_BODY[((Body) o).getBodyAgeState().ordinal()];
		if (!Barrier.onBarrier(objX, objY, objW >> 1, objH >> 2, attr)) {
			boolean bMove = true;
			// 一体づつ流す設定の時
			if (bMoveOnce == true) {
				if ((bindObjList != null) && bindObjList.contains(o) == true) {
					for (Obj oBind : bindObjList) {
						if (oBind == null || oBind.isRemoved()) {
							continue;
						}
						// 対象が優先度最大になったらスキップ
						if (oBind == o) {
							break;
						}
						int attrBind = 16;
						if (oBind instanceof Body) {
							attrBind = Barrier.MAP_BODY[((Body) oBind).getBodyAgeState().ordinal()];
						}
						// リスト上の優先データがフィールドにひかかっていないなら終了
						if (!Barrier.onBarrier(oBind.getX(), oBind.getY(), oBind.getW() >> 1, oBind.getH() >> 2,
								attrBind)) {
							bMove = false;
							break;
						}
					}
				}
			}
			if (bMove == true) {
				switch (option) {
				case 0: // '\0'
				default:
					o.setY(objY - beltSpeed);
					break;
				case 1: // '\001'
					o.setY(objY + beltSpeed);
					break;
				case 2: // '\002'
					o.setX(objX + beltSpeed);
					break;
				case 3: // '\003'
					o.setX(objX - beltSpeed);
					break;
				}
			}
		}

		if (cantmove != 0) {
			// 移動不可
			if (o instanceof Body) {
				((Body) o).setbOnDontMoveBeltconveyor(true);
			}
			if ((bindObjList != null) && !bindObjList.contains(o)) {
				bindObjList.add(o);
			}
		}
		return 0;
	}

	public void upDate() {
		if (getAge() % 2400L == 0L)
			Cash.addCash(-getCost());
	}

	public int getBeltSpeed() {
		return beltSpeed;
	}

	public void removeListData() {
		if (bindObjList != null) {
			for (Obj o : bindObjList) {
				if (o == null) {
					continue;
				}
				if (o instanceof Body) {
					((Body) o).setbOnDontMoveBeltconveyor(false);
				}
			}
			bindObjList.clear();
		}

		SimYukkuri.world.getCurrentMap().beltconveyorObj.remove(this);
	}

	public boolean checkInterval(int cnt) {
		return true;
	}

	// 設定メニュー
	public static boolean setupBeltconveyor(Beltconveyor target) {
		return true;
	}

	public BeltconveyorObj(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		// 初期設定
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(true);
		obOptionSelectionList.add(false);

		boolean bRet = setBeltconveyor(this, false);
		if (!bRet) {
			remove();
			return;
		}
		firstX = x;
		firstY = y;
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().beltconveyorObj.add(this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.BELTCONVEYOR;
		value = 3000;
		cost = 25;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean setBeltconveyor(BeltconveyorObj belt, boolean init) {
		String HOU_LIST[] = {
				ResourceUtil.getInstance().read("inside"),
				ResourceUtil.getInstance().read("outside"),
				ResourceUtil.getInstance().read("right"),
				ResourceUtil.getInstance().read("left")
		};
		String OBJ_LIST[] = {
				ResourceUtil.getInstance().read("all"),
				ResourceUtil.getInstance().read("item_onlyyu"),
				ResourceUtil.getInstance().read("item_onlyanko"),
				ResourceUtil.getInstance().read("item_onlyfood"),
				ResourceUtil.getInstance().read("item_onlystalk"),
				ResourceUtil.getInstance().read("item_exceptyu")
		};
		String MOVE_LIST[] = {
				ResourceUtil.getInstance().read("item_movable"),
				ResourceUtil.getInstance().read("item_immovable"),
				ResourceUtil.getInstance().read("item_immovableonlyone")
		};
		String SPEED_LIST[] = {
				"x1.00", "x2.00", "x3.00", "x4.00"
		};

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 2));
		JComboBox hou_Box = new JComboBox(HOU_LIST);
		hou_Box.setSelectedIndex(hou_default);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_direction")));
		mainPanel.add(hou_Box);
		JComboBox obj_Box = new JComboBox(OBJ_LIST);
		obj_Box.setSelectedIndex(obj_default);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_target")));
		mainPanel.add(obj_Box);
		JComboBox move_Box = new JComboBox(MOVE_LIST);
		move_Box.setSelectedIndex(move_default);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_moveornot")));
		mainPanel.add(move_Box);
		JComboBox speed_Box = new JComboBox(SPEED_LIST);
		speed_Box.setSelectedIndex(speed_default);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_speed")));
		mainPanel.add(speed_Box);

		ButtonListener buttonListener = new ButtonListener();
		buttonListener.master = belt;

		Action[] action = Action.values();
		for (int i = 0; i < action.length; i++) {
			JButton but = new JButton(action[i].toString());
			but.setActionCommand(action[i].name());
			but.addActionListener(buttonListener);
			mainPanel.add(but);
		}

		if (init) {
			hou_Box.setSelectedIndex(belt.hou_before);
			obj_Box.setSelectedIndex(belt.obj_before);
			move_Box.setSelectedIndex(belt.move_before);
			speed_Box.setSelectedIndex(belt.speed_before);
		}

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, 
				ResourceUtil.getInstance().read("item_belconsettings"), 2, -1);
		if (dlgRet != 0) {
			return false;
		}

		int hou;
		belt.hou_before = hou_default = hou = hou_Box.getSelectedIndex();
		int obj;
		belt.obj_before = obj_default = obj = obj_Box.getSelectedIndex();
		int move;
		belt.move_before = move_default = move = move_Box.getSelectedIndex();
		int speed;
		belt.speed_before = speed_default = speed = speed_Box.getSelectedIndex();

		belt.setOption(hou);
		switch (obj) {
		case 0:
			belt.targetType = 0;
			break;
		case 1:
			belt.targetType = 1;
			break;
		case 2:
			belt.targetType = 2;
			break;
		case 3:
			belt.targetType = 3;
			break;
		case 4:
			belt.targetType = 4;
			break;
		default:
			belt.targetType = 5;
			break;
		}

		/*if(obj == 0){
			belt.targetType = 0;
		}
		else if(obj == 1){
			belt.targetType = 1;
		}
		else if(obj == 2){
			belt.targetType = 2;
		//			belt.targetType2 = src.Obj.Type.VOMIT;
		}
		else if(obj == 3){
			belt.targetType = 3;
		}
		else if(obj == 4){
			belt.targetType = 4;
		}
		else{
			belt.targetType = 5;
		}*/
		if (move == 0) {
			// 移動可能
			belt.cantmove = 0;
		} else if (move == 1) {
			belt.cantmove = 1;
			belt.bMoveOnce = false;
		} else {
			// 移動不可能
			belt.cantmove = 1;
			belt.bMoveOnce = true;
		}
		belt.beltSpeed = speed + 1;
		//----------------------
		if (!init) {
			Point pS = Translate.getFieldLimitForMap(SimYukkuri.fieldSX, SimYukkuri.fieldSY);
			Point pE = Translate.getFieldLimitForMap(SimYukkuri.fieldEX, SimYukkuri.fieldEY);
			belt.fieldSX = pS.x;
			belt.fieldSY = pS.y;
			belt.fieldEX = pE.x;
			belt.fieldEY = pE.y;

			int[] anPointBaseX = new int[2];
			int[] anPointBaseY = new int[2];
			Translate.getMovedPoint(belt.fieldSX, belt.fieldSY, belt.fieldEX, belt.fieldEY, 0, 0, 0, 0, anPointBaseX,
					anPointBaseY);

			int nTempX = anPointBaseX[0] + Math.abs(anPointBaseX[1] - anPointBaseX[0]) / 2;
			int nTempY = anPointBaseY[0] + Math.abs(anPointBaseY[1] - anPointBaseY[0]) / 2;
			Point pos = Translate.invertLimit(nTempX, nTempY);
			BeltconveyorObj.x_default = pos.x;
			BeltconveyorObj.y_default = pos.y;
			belt.setX(pos.x);
			belt.setY(pos.y);

			BeltconveyorObj.boundary.width = Math.abs(anPointBaseX[1] - anPointBaseX[0]);
			BeltconveyorObj.boundary.height = Math.abs(SimYukkuri.fieldEY - SimYukkuri.fieldSY);
			BeltconveyorObj.boundary.x = Math.abs(anPointBaseX[1] - anPointBaseX[0]) >> 1;
			BeltconveyorObj.boundary.y = Math.abs(anPointBaseY[1] - anPointBaseY[0]) >> 1;
		}
		return true;
	}

	public void setFilter(boolean bFlag) {
		bFilter = bFlag;
	}

	public List<YukkuriType> getYukkuriFilter() {
		return selectedYukkuriType;
	}

	public void setYukkuriFilter(List<YukkuriType> arrayTemp) {
		selectedYukkuriType = arrayTemp;
	}

	public List<String> getOptionFilter() {
		return istrOptionList;
	}

	public List<Boolean> getOptionResultFilter() {
		return obOptionSelectionList;
	}

	public void setOptionResultFilter(List<Boolean> arrayTemp) {
		obOptionSelectionList = arrayTemp;
	}

	public static class ButtonListener implements ActionListener {
		public BeltconveyorObj master = null;

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action select = Action.valueOf(command);
			if (master == null) {
				return;
			}
			switch (select) {
			case YUKKURI_FILTER:
				List<String> istrOptionList = master.getOptionFilter();
				List<Boolean> obOptionSelectionList = master.getOptionResultFilter();
				List<YukkuriType> arrayTemp = master.getYukkuriFilter();
				boolean bFilter = YukkuriFilterPanel.openFilterPanel(ResourceUtil.getInstance().read("item_targetsettings"),
						ResourceUtil.getInstance().read("item_explanation"),
						istrOptionList, arrayTemp, obOptionSelectionList);
				if (bFilter) {
					master.setFilter(bFilter);
					master.setYukkuriFilter(arrayTemp);
					master.setOptionResultFilter(obOptionSelectionList);
				}
			default:
				break;
			}
		}
	}
}
