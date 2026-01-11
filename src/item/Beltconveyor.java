package src.item;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.ModLoader;
import src.draw.Point4y;
import src.draw.Translate;
import src.enums.Numbering;
import src.game.Stalk;
import src.system.FieldShapeBase;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.system.MapPlaceData;
import src.system.ResourceUtil;

/***************************************************
 * ベルコン
 * <br>
 * これはほかのアイテムと違い、ObjEXを継承していないので注意。
 */
public class Beltconveyor extends FieldShapeBase implements Serializable {

	private static final long serialVersionUID = -4483279905064301375L;

	/** セットアップメニューの項目 */
	private static enum SetupMenu {
		DIRECT(ResourceUtil.getInstance().read("item_direction")),
		SPEED(ResourceUtil.getInstance().read("item_speed")),

		NORMAL_BABY(""), NORMAL_CHILD(""), NORMAL_ADULT(""),

		PREDATOR_BABY(""), PREDATOR_CHILD(""), PREDATOR_ADULT(""),

		RARE_BABY(""), RARE_CHILD(""), RARE_ADULT(""),

		IDIOT_BABY(""), IDIOT_CHILD(""), IDIOT_ADULT(""),

		HYBRID_BABY(""), HYBRID_CHILD(""), HYBRID_ADULT(""),

		SHIT(ResourceUtil.getInstance().read("command_status_unun")),
		VOMIT(ResourceUtil.getInstance().read("game_toan")),

		FOOD(ResourceUtil.getInstance().read("command_status_food")),
		STALK(ResourceUtil.getInstance().read("item_stalk")),
		;

		private final String caption;

		private SetupMenu(String cap) {
			this.caption = cap;
		}

		public String getCaption() {
			return caption;
		}

		@Override
		public String toString() {
			return this.caption;
		}
	}

	/** セットアップメニューのボタンの列挙 */
	private static enum SetupButton {
		NORMAL(ResourceUtil.getInstance().read("draw_normalsp"), SetupMenu.NORMAL_BABY, SetupMenu.NORMAL_CHILD,
				SetupMenu.NORMAL_ADULT),
		PREDATOR(ResourceUtil.getInstance().read("draw_predsp"), SetupMenu.PREDATOR_BABY, SetupMenu.PREDATOR_CHILD,
				SetupMenu.PREDATOR_ADULT),
		RARE(ResourceUtil.getInstance().read("draw_raresp"), SetupMenu.RARE_BABY, SetupMenu.RARE_CHILD,
				SetupMenu.RARE_ADULT),
		IDIOT(ResourceUtil.getInstance().read("item_tarinai"), SetupMenu.IDIOT_BABY, SetupMenu.IDIOT_CHILD,
				SetupMenu.IDIOT_ADULT),
		HYBRID(ResourceUtil.getInstance().read("enums_hybrid"), SetupMenu.HYBRID_BABY, SetupMenu.HYBRID_CHILD,
				SetupMenu.HYBRID_ADULT),
				;

		private final String caption;
		private final SetupMenu[] check;

		/** セットアップ用ボタン */
		private SetupButton(String cap, SetupMenu chk1, SetupMenu chk2, SetupMenu chk3) {
			this.caption = cap;
			this.check = new SetupMenu[3];
			this.check[0] = chk1;
			this.check[1] = chk2;
			this.check[2] = chk3;
		}

		public String getCaption() {
			return caption;
		}

		public SetupMenu[] getCheck() {
			return check;
		}

		@Override
		public String toString() {
			return this.caption;
		}
	}

	/** 方向のコンボボックスの定義 */
	private static enum DirectCombo {
		RIGHT(ResourceUtil.getInstance().read("right"), 0),
		UP(ResourceUtil.getInstance().read("inside"), 1),
		LEFT(ResourceUtil.getInstance().read("left"), 2),
		BOTTOM(ResourceUtil.getInstance().read("outside"), 3),
		;

		private final String caption;
		private final int direct;

		private DirectCombo(String cap, int dir) {
			this.caption = cap;
			this.direct = dir;
		}

		public String getCaption() {
			return caption;
		}

		public int getDirect() {
			return direct;
		}

		@Override
		public String toString() {
			return this.caption;
		}
	}

	/** スピードのコンボボックスの定義 */
	private static enum SpeedCombo {
		SLOW(ResourceUtil.getInstance().read("item_speedslow"), 1),
		MIDDLE(ResourceUtil.getInstance().read("item_speednorm"), 2),
		HIGH(ResourceUtil.getInstance().read("item_speedfast"), 4),
		;

		private final String caption;
		private final int speed;

		private SpeedCombo(String cap, int spd) {
			this.caption = cap;
			this.speed = spd;
		}

		public String getCaption() {
			return caption;
		}

		public int getSpeed() {
			return speed;
		}

		@Override
		public String toString() {
			return this.caption;
		}
	}

	/** ストローク定義 */
	public static final Stroke BELTCONVEYOR_STROKE = new BasicStroke(2.0f);
	/** 色定義 */
	public static final Color BELTCONVEYOR_COLOR = Color.BLACK;
	/** 最小サイズ */
	private static final int MIN_SIZE = 8;

	private static transient BufferedImage[] images = new BufferedImage[4];
	private static transient TexturePaint[] texture = new TexturePaint[4];

	private boolean[][] setting = new boolean[SetupMenu.values().length][3];// = ObjEX.YUKKURI | ObjEX.SHIT | ObjEX.FOOD
																			// | ObjEX.VOMIT | ObjEX.STALK;
	private DirectCombo direction;
	private SpeedCombo beltSpeed;

	private static JComboBox<DirectCombo> dirCombo;
	private static JComboBox<SpeedCombo> spdCombo;
	private static JCheckBox[][] targetCheck = new JCheckBox[SetupMenu.values().length][3];
	/** オブジェクトのユニークID */
	private int objId = 0;

	/** 画像ロード */
	public static void loadImages(ClassLoader loader, ImageObserver io) throws IOException {
		images[0] = ModLoader.loadItemImage(loader, "beltconveyor" + File.separator + "beltconveyor_r.png");
		images[1] = ModLoader.loadItemImage(loader, "beltconveyor" + File.separator + "beltconveyor_u.png");
		images[2] = ModLoader.loadItemImage(loader, "beltconveyor" + File.separator + "beltconveyor_l.png");
		images[3] = ModLoader.loadItemImage(loader, "beltconveyor" + File.separator + "beltconveyor_d.png");

		for (int i = 0; i < 4; i++) {
			texture[i] = new TexturePaint(images[i],
					new Rectangle2D.Float(0, 0, images[i].getWidth(), images[i].getHeight()));
		}
	}

	@Override
	public ShapeMenuTarget hasShapePopup() {
		return ShapeMenuTarget.BELT;
	}

	@Override
	public void executeShapePopup(ShapeMenu menu) {

		List<Beltconveyor> list = SimYukkuri.world.getCurrentMap().getBeltconveyor();
		int pos;

		switch (menu) {
			case SETUP:
				setupBelt(this);
				break;
			case TOP:
				list.remove(this);
				list.add(0, this);
				break;
			case UP:
				pos = list.indexOf(this);
				if (pos > 0) {
					list.remove(this);
					list.add(pos - 1, this);
				}
				break;
			case DOWN:
				pos = list.indexOf(this);
				if (pos < (list.size() - 1)) {
					list.remove(this);
					list.add(pos + 1, this);
				}
				break;
			case BOTTOM:
				list.remove(this);
				list.add(this);
				break;
			default:
				break;
		}
	}

	@Override
	@Transient
	public int getAttribute() {
		return FIELD_BELT;
	}

	@Override
	@Transient
	public int getMinimumSize() {
		return MIN_SIZE;
	}

	/** プレビューラインの描画 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, anPointX, anPointY);

		g2.drawPolygon(anPointX, anPointY, 4);
	}

	@Override
	public void drawShape(Graphics2D g2) {
		int[] anPointX = new int[4];
		int[] anPointY = new int[4];
		Translate.getPolygonPoint(fieldSX, fieldSY, fieldEX, fieldEY, anPointX, anPointY);

		g2.setPaint(texture[direction.getDirect()]);
		g2.fillPolygon(anPointX, anPointY, 4);
		// g2.setStroke(Beltconveyor.BELTCONVEYOR_STROKE);
		// g2.setColor(Beltconveyor.BELTCONVEYOR_COLOR);
		// g2.fillPolygon(anPointX, anPointY, 4 );
	}

	/**
	 * コンストラクタ
	 *
	 * @param fsx 設置起点のX座標
	 * @param fsy 設置起点のY座標
	 * @param fex 設置終点のX座標
	 * @param fey 設置終点のY座標
	 */
	public Beltconveyor(int fsx, int fsy, int fex, int fey) {
		objId = Numbering.INSTANCE.numberingObjId();
		Point4y pS = Translate.getFieldLimitForMap(fsx, fsy);
		Point4y pE = Translate.getFieldLimitForMap(fex, fey);
		fieldSX = pS.getX();
		fieldSY = pS.getY();
		fieldEX = pE.getX();
		fieldEY = pE.getY();

		int[] anPointBaseX = new int[2];
		int[] anPointBaseY = new int[2];
		Translate.getMovedPoint(fieldSX, fieldSY, fieldEX, fieldEY, 0, 0, 0, 0, anPointBaseX, anPointBaseY);

		// フィールド座標が渡ってくるのでマップ座標も計算しておく
		Point4y pos = Translate.invertLimit(anPointBaseX[0], anPointBaseY[0]);
		mapSX = Math.max(0, Math.min(pos.getX(), Translate.getMapW()));
		mapSY = Math.max(0, Math.min(pos.getY(), Translate.getMapH()));

		pos = Translate.invertLimit(anPointBaseX[1], anPointBaseY[1]);
		mapEX = Math.max(0, Math.min(pos.getX(), Translate.getMapW()));
		mapEY = Math.max(0, Math.min(pos.getY(), Translate.getMapH()));

		// 規定サイズと位置へ合わせる
		if ((mapEX - mapSX) < MIN_SIZE)
			mapEX = mapSX + MIN_SIZE;
		if ((mapEY - mapSY) < MIN_SIZE)
			mapEY = mapSY + MIN_SIZE;
		if (mapEX > Translate.getMapW()) {
			mapSX -= (mapEX - Translate.getMapW());
			mapEX -= (mapEX - Translate.getMapW());
		}
		if (mapEY > Translate.getMapH()) {
			mapSY -= (mapEY - Translate.getMapH());
			mapEY -= (mapEY - Translate.getMapH());
		}

		Point4y f = new Point4y();
		Translate.translate(mapSX, mapSY, f);
		fieldSX = f.getX();
		fieldSY = f.getY();
		Translate.translate(mapEX, mapEY, f);
		fieldEX = f.getX();
		fieldEY = f.getY();

		fieldW = fieldEX - fieldSX + 1;
		fieldH = fieldEY - fieldSY + 1;
		mapW = mapEX - mapSX + 1;
		mapH = mapEY - mapSY + 1;

		setting[2][0] = true;
		setting[2][1] = true;
		setting[2][2] = true;
		direction = DirectCombo.RIGHT;
		beltSpeed = SpeedCombo.MIDDLE;

		boolean ret = setupBelt(this);
		if (ret) {
			SimYukkuri.world.getCurrentMap().getBeltconveyor().add(this);
			MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().getFieldMap(), mapSX, mapSY, mapW, mapH, true,
					FIELD_BELT);
		}
	}

	public Beltconveyor() {

	}

	/** 処理する必要のあるオブジェクトか判定 */
	public boolean checkHitObj(Obj o) {

		boolean ret = false;

		switch (o.getObjType()) {
			case YUKKURI:
				Body b = (Body) o;
				int ageIdx = b.getBodyAgeState().ordinal();
				int bodyIdx;
				// settingのインデックスはSetupButton.ordinal() + SetupMenu.NORMAL_BABY.ordinal()
				// setupBeltで連続した行(2,3,4,5,6)に保存しているため
				if (b.isHybrid()) {
					// ハイブリッド
					bodyIdx = SetupButton.HYBRID.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
				} else if (b.isIdiot()) {
					// 足りない
					bodyIdx = SetupButton.IDIOT.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
				} else if (b.isRareType()) {
					// 希少種
					bodyIdx = SetupButton.RARE.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
				} else if (b.isPredatorType()) {
					// 捕食種
					bodyIdx = SetupButton.PREDATOR.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
				} else {
					// 通常種
					bodyIdx = SetupButton.NORMAL.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
				}
				if (setting[bodyIdx][ageIdx])
					ret = true;
				break;
			case SHIT:
				if (setting[SetupMenu.SHIT.ordinal()][0])
					ret = true;
				break;
			case OBJECT:
				if (o instanceof Food) {
					if (setting[SetupMenu.FOOD.ordinal()][0])
						ret = true;
				} else if (o instanceof Stalk) {
					if (setting[SetupMenu.STALK.ordinal()][0])
						ret = true;
				}
				break;
			case VOMIT:
				if (setting[SetupMenu.VOMIT.ordinal()][0])
					ret = true;
				break;
			default:
				break;
		}
		return ret;
	}

	/** ヒットしたオブジェクトの処理 */
	public void processHitObj(Obj o) {

		switch (direction) {
			case RIGHT:
				o.addBxyz(beltSpeed.getSpeed(), 0, 0);
				break;
			case UP:
				o.addBxyz(0, -beltSpeed.getSpeed(), 0);
				break;
			case LEFT:
				o.addBxyz(-beltSpeed.getSpeed(), 0, 0);
				break;
			case BOTTOM:
				o.addBxyz(0, beltSpeed.getSpeed(), 0);
				break;
		}
	}

	/** フィールド座標にあるシェイプ取得 */
	public static Beltconveyor getBeltconveyor(int fx, int fy) {

		for (Beltconveyor bc : SimYukkuri.world.getCurrentMap().getBeltconveyor()) {
			if (bc.fieldSX <= fx && fx <= bc.fieldEX
					&& bc.fieldSY <= fy && fy <= bc.fieldEY) {
				return bc;
			}
		}
		return null;
	}

	/** 削除 */
	public static void deleteBelt(Beltconveyor b) {
		MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().getFieldMap(), b.mapSX, b.mapSY, b.mapW, b.mapH, false,
				FIELD_BELT);
		SimYukkuri.world.getCurrentMap().getBeltconveyor().remove(b);
		// 重なってた部分の復元
		for (Beltconveyor bc : SimYukkuri.world.getCurrentMap().getBeltconveyor()) {
			MapPlaceData.setFiledFlag(SimYukkuri.world.getCurrentMap().getFieldMap(), bc.mapSX, bc.mapSY, bc.mapW, bc.mapH,
					true,
					FIELD_BELT);
		}
	}

	/** 設定メニュー */
	public static boolean setupBelt(Beltconveyor b) {

		JPanel mainPanel = new JPanel();
		JPanel northPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		boolean ret = false;

		// パネル全体
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(300, 300));
		// 上部ラベル
		northPanel.setLayout(new GridLayout(1, 3));
		// 左側ボタン列
		westPanel.setLayout(new GridLayout(6, 1));
		westPanel.setPreferredSize(new Dimension(120, 300));
		// 中央チェックボックス
		centerPanel.setLayout(new GridLayout(6, 3));
		// 下部その他設定
		southPanel.setLayout(new GridLayout(2, 2));

		ButtonListener butAction = new ButtonListener();

		// 上
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel label = new JLabel(SetupMenu.DIRECT.toString());
		panel.add(label);
		dirCombo = new JComboBox<Beltconveyor.DirectCombo>(DirectCombo.values());
		dirCombo.setSelectedIndex(b.direction.ordinal());
		dirCombo.setActionCommand(SetupMenu.SPEED.name());
		panel.add(dirCombo);

		panel.add(new JLabel("     "));

		label = new JLabel(SetupMenu.SPEED.toString());
		panel.add(label);
		spdCombo = new JComboBox<Beltconveyor.SpeedCombo>(SpeedCombo.values());
		spdCombo.setSelectedIndex(b.beltSpeed.ordinal());
		spdCombo.setActionCommand(SetupMenu.SPEED.name());
		panel.add(spdCombo);

		northPanel.add(panel);

		// 左
		westPanel.add(new JLabel(""));
		for (SetupButton bp : SetupButton.values()) {
			JButton but = new JButton(bp.toString());
			but.addActionListener(butAction);
			but.setActionCommand(bp.name());
			westPanel.add(but);
		}

		// 中
		label = new JLabel("赤ゆ");
		centerPanel.add(label);
		label = new JLabel("子ゆ");
		centerPanel.add(label);
		label = new JLabel("成ゆ");
		centerPanel.add(label);
		SetupMenu[] body = SetupMenu.values();
		int row = SetupMenu.NORMAL_BABY.ordinal();
		int col = 0;
		for (int i = SetupMenu.NORMAL_BABY.ordinal(); i <= SetupMenu.HYBRID_ADULT.ordinal(); i++) {
			targetCheck[row][col] = new JCheckBox("");
			targetCheck[row][col].setSelected(b.setting[row][col]);
			targetCheck[row][col].setActionCommand(body[i].name());
			centerPanel.add(targetCheck[row][col]);
			col++;
			if (col == 3) {
				col = 0;
				row++;
			}
		}

		// 下
		row = SetupMenu.HYBRID_ADULT.ordinal() + 1;
		for (int i = SetupMenu.SHIT.ordinal(); i <= SetupMenu.STALK.ordinal(); i++) {
			targetCheck[row][0] = new JCheckBox(body[i].toString());
			targetCheck[row][0].setSelected(b.setting[row][0]);
			targetCheck[row][0].setActionCommand(body[i].name());
			southPanel.add(targetCheck[row][0]);
			row++;
		}

		mainPanel.add(BorderLayout.NORTH, northPanel);
		mainPanel.add(BorderLayout.WEST, westPanel);
		mainPanel.add(BorderLayout.CENTER, centerPanel);
		mainPanel.add(BorderLayout.SOUTH, southPanel);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel,
				ResourceUtil.getInstance().read("item_coveyersettings"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (dlgRet == JOptionPane.OK_OPTION) {
			b.direction = DirectCombo.values()[dirCombo.getSelectedIndex()];
			b.beltSpeed = SpeedCombo.values()[spdCombo.getSelectedIndex()];

			row = SetupMenu.NORMAL_BABY.ordinal();
			col = 0;
			for (int i = SetupMenu.NORMAL_BABY.ordinal(); i <= SetupMenu.HYBRID_ADULT.ordinal(); i++) {
				b.setting[row][col] = targetCheck[row][col].isSelected();
				col++;
				if (col == 3) {
					col = 0;
					row++;
				}
			}

			row = SetupMenu.HYBRID_ADULT.ordinal() + 1;
			for (int i = SetupMenu.SHIT.ordinal(); i <= SetupMenu.STALK.ordinal(); i++) {
				b.setting[row][0] = targetCheck[row][0].isSelected();
				row++;
			}
			ret = true;
		}
		return ret;
	}

	/** ボタン操作の反映作業 */
	public static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			SetupButton but = SetupButton.valueOf(command);
			int row = but.ordinal() + SetupMenu.NORMAL_BABY.ordinal();
			if (targetCheck[row][0].isSelected()) {
				targetCheck[row][0].setSelected(false);
				targetCheck[row][1].setSelected(false);
				targetCheck[row][2].setSelected(false);
			} else {
				targetCheck[row][0].setSelected(true);
				targetCheck[row][1].setSelected(true);
				targetCheck[row][2].setSelected(true);
			}
		}
	}

	public boolean[][] getSetting() {
		return setting;
	}

	public void setSetting(boolean[][] setting) {
		this.setting = setting;
	}

	public DirectCombo getDirection() {
		return direction;
	}

	public void setDirection(DirectCombo direction) {
		this.direction = direction;
	}

	public SpeedCombo getBeltSpeed() {
		return beltSpeed;
	}

	public void setBeltSpeed(SpeedCombo beltSpeed) {
		this.beltSpeed = beltSpeed;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

}

