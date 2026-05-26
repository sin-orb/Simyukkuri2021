// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) ansi
// Source File Name:   Beltconveyor.java

package org.simyukkuri.entity.core.world.item;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.system.Cash;
import org.simyukkuri.ui.YukkuriFilterPanel;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * ベルコン2
 * <br>
 * 拡張、YukkuriFilterPanel
 */
public class BeltconveyorObj extends WorldEntity {

	private static final long serialVersionUID = -2840212904501204971L;
	public static final int hitCheckObjType = WorldEntity.YUKKURI + WorldEntity.SHIT + WorldEntity.FOOD
			+ WorldEntity.TOY + WorldEntity.VOMIT
			+ WorldEntity.STALK;
	private static final int images_num = 10;
	private static int[] AnimeImagesNum = {
			5, 5
	};
	private static BufferedImage[] images = new BufferedImage[images_num];
	private static Rectangle4y boundary = new Rectangle4y();
	static int hou_default = 0;
	static int obj_default = 0;
	static int move_default = 0;
	static int speed_default = 0;
	static int x_default = 50;
	static int y_default = 50;
	private int beltSpeed;
	private int houBefore = 0;
	private int objBefore = 0;
	private int moveBefore = 0;
	private int speedBefore = 0;
	private int targetType;
	private int cantmove = 0;
	private boolean moveOnce = false;
	protected List<Entity> bindObjList = new LinkedList<Entity>(); // ベルトコンベア上で移動不可能な状態になっているアイテムのリスト

	protected List<YukkuriType> selectedYukkuriType = new LinkedList<YukkuriType>(); // 処理対象のゆっくり
	protected static List<String> istrOptionList = new LinkedList<String>(); // 処理対象設定(オプション)
	protected List<Boolean> obOptionSelectionList = new LinkedList<Boolean>(); // 処理対象設定(オプション)の選択状態

	protected boolean filter = false;
	protected int fieldSx;
	protected int fieldSy;
	protected int fieldEx;
	protected int fieldEy;
	protected int firstX;
	protected int firstY;
	protected int[] polygonX = new int[4];
	protected int[] polygonY = new int[4];

	/**
	 * Action enum type.
	 */
	public static enum Action {
		YUKKURI_FILTER(GameText.read("item_filtersettings"), ""),
		;

		private String name;

		Action(String nameJ, String nameE) {
			this.name = nameJ;
		}

		/** アクション名を返す。 */
		public String toString() {
			return name;
		}
	}

	/** 画像リソースをロードする。 */
	public static void loadImages(ClassLoader loader, ImageObserver io)
			throws IOException {
		for (int i = 0; i < 10; i++) {
			images[i] = ModLoader.loadItemImage(loader,
					(new StringBuilder("beltconveyor/beltconveyor")).append(String.format("%03d", new Object[] {
							Integer.valueOf(i + 1)
					})).append(".png").toString());
		}

		boundary.setWidth(images[0].getWidth(io));
		boundary.setHeight(images[0].getHeight(io));
		boundary.setX(boundary.getWidth() >> 1);
		boundary.setY(boundary.getHeight() >> 1);

		// オプション
		istrOptionList.add(GameText.read("attitude_verynice"));
		istrOptionList.add(GameText.read("attitude_nice"));
		istrOptionList.add(GameText.read("attitude_normal"));
		istrOptionList.add(GameText.read("attitude_shithead"));
		istrOptionList.add(GameText.read("attitude_supershithead"));
		istrOptionList.add(GameText.read("intel_badge"));
		istrOptionList.add(GameText.read("intel_normal"));
		istrOptionList.add(GameText.read("intel_fool"));
		istrOptionList.add(GameText.read("item_onlydeadbody"));
	}

	/** アイテム画像をレイヤー配列にセットし、使用レイヤー数を返す。 */
	@Override
	public int getImageLayer(BufferedImage[] layer) {
		int frame = 0;

		if (enabled) {
			frame = (int) getAge();
		}

		switch (option) { // 楽にアニメ指定できるようにしたいが後で
			case 0:
			default:
				layer[0] = images[frame / 4 % AnimeImagesNum[0]]; // 4フレームに1回画像更新
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

	/** ベルトコンベア面をテクスチャ塗りつぶして描画し、使用レイヤー数を返す。 */
	public int getImageLayer(Graphics2D g2, BufferedImage[] layer) {
		int[] basePolygonX = new int[2];
		int[] basePolygonY = new int[2];
		Translate.getMovedPoint(fieldSx, fieldSy, fieldEx, fieldEy, firstX, firstY, x, y, basePolygonX, basePolygonY);
		Translate.getPolygonPoint(basePolygonX[0], basePolygonY[0], basePolygonX[1], basePolygonY[1], polygonX,
				polygonY);
		TexturePaint texture = new TexturePaint(layer[0],
				new Rectangle2D.Float(0, 0, layer[0].getWidth() - 10, layer[0].getHeight() - 10));
		g2.setPaint(texture);
		g2.fillPolygon(polygonX, polygonY, 4);
		return 1;
	}

	/** 指定インデックスの画像を返す（現在は null）。 */
	public Image getImage(int idx) {
		return null;
	}

	/** 画像レイヤー数を返す。 */
	@Transient
	public int getImageLayerCount() {
		return 0;
	}

	/** アイテムの影画像を返す。 */
	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}

	/** ベルトコンベア配置前のプレビュー描画を行う。 */
	public static void drawPreview(Graphics2D g2, int sx, int sy, int ex, int ey) {
		int[] polygonX = new int[4];
		int[] polygonY = new int[4];
		Translate.getPolygonPoint(sx, sy, ex, ey, polygonX, polygonY);
		g2.drawPolygon(polygonX, polygonY, 4);
	}

	/** 境界ボックスを返す。 */
	public static Rectangle4y getBounding() {
		return boundary;
	}

	/** 衝突判定対象タイプを返す。 */
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	/** 指定座標がベルトコンベア上にあるかチェックする。 */
	public boolean checkContain(int inX, int inY, boolean isField) {
		int xc = inX;
		int yc = inY;
		if (isField) {
			Point4y pos = Translate.invertLimit(inX, inY);
			xc = pos.getX();
			yc = pos.getY();
		}

		Point4y posFirst = Translate.invertLimit(polygonX[0], polygonY[0]);
		Point4y posSecond = Translate.invertLimit(polygonX[2], polygonY[2]);
		if (posFirst != null && posSecond != null) {
			if (posFirst.getX() <= xc && xc <= posSecond.getX() && posFirst.getY() <= yc
					&& yc <= posSecond.getY()) {
				return true;
			}
		}
		return false;
	}

	/** 衝突判定を行い、範囲内のオブジェクトを搬送対象に追加する。 */
	public boolean checkHitObj(Rectangle colRect, Entity o) {
		if ((o instanceof Yukkuri) && ((Yukkuri) o).isLockmove()) {
			return false;
		}
		if (o.isRemoved()) {
			bindObjList.remove(o);
		}
		if (o.getZ() == 0) {
			if (checkContain(o.getX(), o.getY(), false)) {
				objHitProcess(o);
				return true;
			}
			if (bindObjList != null && bindObjList.contains(o)) {
				if (o instanceof Yukkuri) {
					((Yukkuri) o).setOnNonMovingConveyor(false);
				}
				bindObjList.remove(o);
			}
		}
		return false;
	}

	// 箱を斜めから見下ろしている形式なので奥のxと手前のxで同じ座標でも垂直にならない
	/** 衝突処理を行い、結果コードを返す。 */
	public int objHitProcess(Entity o) {
		final int objX = o.getX();
		final int objY = o.getY();
		final int objW = o.getW();
		final int objH = o.getH();
		int attr = 16;

		// フィルター有効時
		if (o instanceof Yukkuri && filter) {
			Yukkuri bodyTarget = (Yukkuri) o;
			YukkuriType type = YukkuriType.fromClassName(bodyTarget.getClass().getSimpleName());
			// ゆっくりタイプチェック
			if (selectedYukkuriType != null && selectedYukkuriType.contains(type)) {
				return 0;
			}
			if (obOptionSelectionList != null && obOptionSelectionList.size() == 9) {
				// 性格
				switch (bodyTarget.getAttitude()) {
					case VERY_NICE:
						if (!obOptionSelectionList.get(0)) {
							return 0;
						}
						break;
					case NICE:
						if (!obOptionSelectionList.get(1)) {
							return 0;
						}
						break;
					case AVERAGE:
						if (!obOptionSelectionList.get(2)) {
							return 0;
						}
						break;
					case SHITHEAD:
						if (!obOptionSelectionList.get(3)) {
							return 0;
						}
						break;
					case SUPER_SHITHEAD:
						if (!obOptionSelectionList.get(4)) {
							return 0;
						}
						break;
					default:
						break;
				}
				// 知性
				switch (bodyTarget.getIntelligence()) {
					case WISE:
						if (!obOptionSelectionList.get(5)) {
							return 0;
						}
						break;
					case AVERAGE:
						if (!obOptionSelectionList.get(6)) {
							return 0;
						}
						break;
					case FOOL:
						if (!obOptionSelectionList.get(7)) {
							return 0;
						}
						break;
					default:
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
					if (!(o instanceof Yukkuri)) {
						return 0;
					}
					break;
				case 2:
					if (!(o instanceof Shit || o instanceof Vomit)) {
						return 0;
					}
					break;
				case 3:
					if (!(o instanceof Food) || o instanceof Shit || o instanceof Vomit || o instanceof Yukkuri) {
						return 0;
					}
					break;
				case 4:
					if (!(o instanceof Stalk)) {
						return 0;
					}
					break;
				default:
					if (o instanceof Yukkuri) {
						return 0;
					}
					break;
			}
		}
		if (o instanceof Yukkuri) {
			attr = Barrier.BODY_BLOCK_FLAGS[((Yukkuri) o).getAgeState().ordinal()];
		}
		if (!Barrier.onBarrier(objX, objY, objW >> 1, objH >> 2, attr)) {
			boolean shouldMove = true;
			// 一体づつ流す設定の時
			if (moveOnce == true) {
				if ((bindObjList != null) && bindObjList.contains(o) == true) {
					for (Entity obind : bindObjList) {
						if (obind == null || obind.isRemoved()) {
							continue;
						}
						// 対象が優先度最大になったらスキップ
						if (obind == o) {
							break;
						}
						int attrBind = 16;
						if (obind instanceof Yukkuri) {
							attrBind = Barrier.BODY_BLOCK_FLAGS[((Yukkuri) obind).getAgeState().ordinal()];
						}
						// リスト上の優先データがフィールドにひかかっていないなら終了
						if (!Barrier.onBarrier(obind.getX(), obind.getY(), obind.getW() >> 1, obind.getH() >> 2,
								attrBind)) {
							shouldMove = false;
							break;
						}
					}
				}
			}
			if (shouldMove == true) {
				switch (option) {
					case 0: // '\0'
					default:
						o.setCalcY(objY - beltSpeed);
						break;
					case 1: // '\001'
						o.setCalcY(objY + beltSpeed);
						break;
					case 2: // '\002'
						o.setCalcX(objX + beltSpeed);
						break;
					case 3: // '\003'
						o.setCalcX(objX - beltSpeed);
						break;
				}
			}
		}

		if (cantmove != 0) {
			// 移動不可
			if (o instanceof Yukkuri) {
				((Yukkuri) o).setOnNonMovingConveyor(true);
			}
			if ((bindObjList != null) && !bindObjList.contains(o)) {
				bindObjList.add(o);
			}
		}
		return 0;
	}

	/** 維持コストを定期的に引く。 */
	public void upDate() {
		if (getAge() % 2400L == 0L) {
			Cash.addCash(-getCost());
		}
	}

	/** ベルト速度を返す。 */
	public int getBeltSpeed() {
		return beltSpeed;
	}

	/** ワールドからベルトコンベアオブジェクトを除去する。 */
	public void removeFromWorld() {
		if (bindObjList != null) {
			for (Entity o : bindObjList) {
				if (o == null) {
					continue;
				}
				if (o instanceof Yukkuri) {
					((Yukkuri) o).setOnNonMovingConveyor(false);
				}
			}
			bindObjList.clear();
		}

		GameWorld.get().getCurrentWorldState().getBeltconveyorObjects().remove(objId);
	}

	/** インターバルチェック（現在は常に true）。 */
	public boolean checkInterval(int cnt) {
		return true;
	}

	// 設定メニュー
	/** ベルトコンベアのセットアップを行い、成功かどうかを返す。 */
	public static boolean setupBeltconveyor(Beltconveyor target) {
		return true;
	}

	/** X/Y 座標で生成してワールドに追加するコンストラクタ。 */
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

		boolean setupSuccess = setBeltconveyors(this, false);
		if (!setupSuccess) {
			remove();
			return;
		}
		firstX = x;
		firstY = y;
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		GameWorld.get().getCurrentWorldState().getBeltconveyorObjects().put(objId, this);
		GameWorld.get().getCurrentWorldState().registerEntity(objId, this);
		objType = Type.PLATFORM;
		worldEntityType = WorldEntityKind.BELTCONVEYOR;
		value = 3000;
		cost = 25;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public BeltconveyorObj() {

	}

	/** ベルトコンベアの隣接関係を登録し、登録成功かどうかを返す。 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean setBeltconveyors(BeltconveyorObj belt, boolean init) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(5, 2));
		String[] houList = {
				GameText.read("inside"),
				GameText.read("outside"),
				GameText.read("right"),
				GameText.read("left")
		};
		JComboBox houBox = new JComboBox(houList);
		houBox.setSelectedIndex(hou_default);
		mainPanel.add(new JLabel(GameText.read("item_direction")));
		mainPanel.add(houBox);
		String[] objList = {
				GameText.read("all"),
				GameText.read("item_onlyyu"),
				GameText.read("item_onlyanko"),
				GameText.read("item_onlyfood"),
				GameText.read("item_onlystalk"),
				GameText.read("item_exceptyu")
		};
		JComboBox objBox = new JComboBox(objList);
		objBox.setSelectedIndex(obj_default);
		mainPanel.add(new JLabel(GameText.read("item_target")));
		mainPanel.add(objBox);
		String[] moveList = {
				GameText.read("item_movable"),
				GameText.read("item_immovable"),
				GameText.read("item_immovableonlyone")
		};
		JComboBox moveBox = new JComboBox(moveList);
		moveBox.setSelectedIndex(move_default);
		mainPanel.add(new JLabel(GameText.read("item_moveornot")));
		mainPanel.add(moveBox);
		String[] speedList = {
				"x1.00", "x2.00", "x3.00", "x4.00"
		};
		JComboBox speedBox = new JComboBox(speedList);
		speedBox.setSelectedIndex(speed_default);
		mainPanel.add(new JLabel(GameText.read("item_speed")));
		mainPanel.add(speedBox);

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
			houBox.setSelectedIndex(belt.houBefore);
			objBox.setSelectedIndex(belt.objBefore);
			moveBox.setSelectedIndex(belt.moveBefore);
			speedBox.setSelectedIndex(belt.speedBefore);
		}

		int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel,
				GameText.read("item_belconsettings"), 2, -1);
		if (dlgRet != 0) {
			return false;
		}

		int hou;
		belt.houBefore = hou_default = hou = houBox.getSelectedIndex();
		int obj;
		belt.objBefore = obj_default = obj = objBox.getSelectedIndex();
		int move;
		belt.moveBefore = move_default = move = moveBox.getSelectedIndex();
		int speed;
		belt.speedBefore = speed_default = speed = speedBox.getSelectedIndex();

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

		/*
		 * if(obj == 0){
		 * belt.targetType = 0;
		 * }
		 * else if(obj == 1){
		 * belt.targetType = 1;
		 * }
		 * else if(obj == 2){
		 * belt.targetType = 2;
		 * // belt.targetType2 = org.simyukkuri.Entity.Type.VOMIT;
		 * }
		 * else if(obj == 3){
		 * belt.targetType = 3;
		 * }
		 * else if(obj == 4){
		 * belt.targetType = 4;
		 * }
		 * else{
		 * belt.targetType = 5;
		 * }
		 */
		if (move == 0) {
			// 移動可能
			belt.cantmove = 0;
		} else if (move == 1) {
			belt.cantmove = 1;
			belt.moveOnce = false;
		} else {
			// 移動不可能
			belt.cantmove = 1;
			belt.moveOnce = true;
		}
		belt.beltSpeed = speed + 1;
		// ----------------------
		if (!init) {
			Point4y start = Translate.getFieldLimitForWorld(SimYukkuri.fieldSx, SimYukkuri.fieldSy);
			Point4y end = Translate.getFieldLimitForWorld(SimYukkuri.fieldEx, SimYukkuri.fieldEy);
			belt.fieldSx = start.getX();
			belt.fieldSy = start.getY();
			belt.fieldEx = end.getX();
			belt.fieldEy = end.getY();

			int[] basePolygonX = new int[2];
			int[] basePolygonY = new int[2];
			Translate.getMovedPoint(belt.fieldSx, belt.fieldSy, belt.fieldEx, belt.fieldEy, 0, 0, 0, 0, basePolygonX,
					basePolygonY);

			int centerX = basePolygonX[0] + Math.abs(basePolygonX[1] - basePolygonX[0]) / 2;
			int centerY = basePolygonY[0] + Math.abs(basePolygonY[1] - basePolygonY[0]) / 2;
			Point4y pos = Translate.invertLimit(centerX, centerY);
			BeltconveyorObj.x_default = pos.getX();
			BeltconveyorObj.y_default = pos.getY();
			belt.setCalcX(pos.getX());
			belt.setCalcY(pos.getY());

			BeltconveyorObj.boundary.setWidth(Math.abs(basePolygonX[1] - basePolygonX[0]));
			BeltconveyorObj.boundary.setHeight(Math.abs(SimYukkuri.fieldEy - SimYukkuri.fieldSy));
			BeltconveyorObj.boundary.setX(Math.abs(basePolygonX[1] - basePolygonX[0]) >> 1);
			BeltconveyorObj.boundary.setY(Math.abs(basePolygonY[1] - basePolygonY[0]) >> 1);
		}
		return true;
	}

	/** フィルター設定を適用する。 */
	public void applyFilterSetting(boolean filterEnabled) {
		filter = filterEnabled;
	}

	/** 種別フィルターで選択されたゆっくり種別リストを返す。 */
	public List<YukkuriType> getSelectedYukkuriTypes() {
		return selectedYukkuriType;
	}

	/** 種別フィルターで選択されたゆっくり種別リストをセットする。 */
	public void setSelectedYukkuriTypes(List<YukkuriType> selectedYukkuriTypes) {
		selectedYukkuriType = selectedYukkuriTypes;
	}

	/** フィルターオプションのラベルリストを返す。 */
	public List<String> getOptionLabels() {
		return istrOptionList;
	}

	/** フィルターオプションの選択状態リストを返す。 */
	public List<Boolean> getOptionSelections() {
		return obOptionSelectionList;
	}

	/** フィルターオプションの選択状態リストをセットする。 */
	public void setOptionSelections(List<Boolean> optionSelections) {
		obOptionSelectionList = optionSelections;
	}

	/** 前フレームの方向を返す。 */
	public int getHouBefore() {
		return houBefore;
	}

	/** 前フレームの方向をセットする。 */
	public void setHouBefore(int houBefore) {
		this.houBefore = houBefore;
	}

	/** 前フレームのオブジェクト ID を返す。 */
	public int getObjBefore() {
		return objBefore;
	}

	/** 前フレームのオブジェクト ID をセットする。 */
	public void setObjBefore(int objBefore) {
		this.objBefore = objBefore;
	}

	/** 前フレームの移動状態を返す。 */
	public int getMoveBefore() {
		return moveBefore;
	}

	/** 前フレームの移動状態をセットする。 */
	public void setMoveBefore(int moveBefore) {
		this.moveBefore = moveBefore;
	}

	/** 前フレームの移動速度を返す。 */
	public int getSpeedBefore() {
		return speedBefore;
	}

	/** 前フレームの移動速度をセットする。 */
	public void setSpeedBefore(int speedBefore) {
		this.speedBefore = speedBefore;
	}

	/** ターゲットの種別を返す。 */
	public int getTargetType() {
		return targetType;
	}

	/** ターゲットの種別をセットする。 */
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	/** 移動不可カウンタを返す。 */
	public int getCantmove() {
		return cantmove;
	}

	/** 移動不可カウンタをセットする。 */
	public void setCantmove(int cantmove) {
		this.cantmove = cantmove;
	}

	/** 一度だけ移動フラグを返す。 */
	public boolean isMoveOnce() {
		return moveOnce;
	}

	/** 一度だけ移動フラグをセットする。 */
	public void setMoveOnce(boolean moveOnce) {
		this.moveOnce = moveOnce;
	}

	/** 関連付けられているオブジェクト一覧を返す。 */
	public List<Entity> getBoundObjects() {
		return bindObjList;
	}

	/** 関連付けるオブジェクト一覧をセットする。 */
	public void setBoundObjects(List<Entity> boundObjects) {
		this.bindObjList = boundObjects;
	}

	/** フィルター有効かどうかを返す。 */
	public boolean isFilter() {
		return filter;
	}

	/** フィルター有効フラグをセットする。 */
	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	/** フィールド矩形の始点 X 座標を返す。 */
	public int getFieldSx() {
		return fieldSx;
	}

	/** フィールド矩形の始点 X 座標をセットする。 */
	public void setFieldSx(int fieldSx) {
		this.fieldSx = fieldSx;
	}

	/** フィールド矩形の始点 Y 座標を返す。 */
	public int getFieldSy() {
		return fieldSy;
	}

	/** フィールド矩形の始点 Y 座標をセットする。 */
	public void setFieldSy(int fieldSy) {
		this.fieldSy = fieldSy;
	}

	/** フィールド矩形の終点 X 座標を返す。 */
	public int getFieldEx() {
		return fieldEx;
	}

	/** フィールド矩形の終点 X 座標をセットする。 */
	public void setFieldEx(int fieldEx) {
		this.fieldEx = fieldEx;
	}

	/** フィールド矩形の終点 Y 座標を返す。 */
	public int getFieldEy() {
		return fieldEy;
	}

	/** フィールド矩形の終点 Y 座標をセットする。 */
	public void setFieldEy(int fieldEy) {
		this.fieldEy = fieldEy;
	}

	/** 配置時の初期 X 座標を返す。 */
	public int getFirstX() {
		return firstX;
	}

	/** 配置時の初期 X 座標をセットする。 */
	public void setFirstX(int firstX) {
		this.firstX = firstX;
	}

	/** 配置時の初期 Y 座標を返す。 */
	public int getFirstY() {
		return firstY;
	}

	/** 配置時の初期 Y 座標をセットする。 */
	public void setFirstY(int firstY) {
		this.firstY = firstY;
	}

	/** ポリゴン頂点の X 座標配列を返す。 */
	public int[] getPolygonX() {
		return polygonX;
	}

	/** ポリゴン頂点の X 座標配列をセットする。 */
	public void setPolygonX(int[] polygonX) {
		this.polygonX = polygonX;
	}

	/** ポリゴン頂点の Y 座標配列を返す。 */
	public int[] getPolygonY() {
		return polygonY;
	}

	/** ポリゴン頂点の Y 座標配列をセットする。 */
	public void setPolygonY(int[] polygonY) {
		this.polygonY = polygonY;
	}

	/** ベルト速度をセットする。 */
	public void setBeltSpeed(int beltSpeed) {
		this.beltSpeed = beltSpeed;
	}

	/**
	 * ButtonListener.
	 */
	public static class ButtonListener implements ActionListener {
		public BeltconveyorObj master = null;

		/** ボタンアクションを処理してフィルター設定を更新する。 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action select = Action.valueOf(command);
			if (master == null) {
				return;
			}
			switch (select) {
				case YUKKURI_FILTER:
					List<String> istrOptionList = master.getOptionLabels();
					List<Boolean> obOptionSelectionList = master.getOptionSelections();
					List<YukkuriType> arrayTemp = master.getSelectedYukkuriTypes();
					boolean filterEnabled = YukkuriFilterPanel.openFilterPanel(
							GameText.read("item_targetsettings"),
							GameText.read("item_explanation"),
							istrOptionList, arrayTemp, obOptionSelectionList);
					if (filterEnabled) {
						master.setFilter(filterEnabled);
						master.setSelectedYukkuriTypes(arrayTemp);
						master.setOptionSelections(obOptionSelectionList);
					}
					break;
				default:
					break;
			}
		}
	}
}
