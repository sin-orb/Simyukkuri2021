package src.item;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.SimYukkuri;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.ModLoader;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.ResourceUtil;

/***************************************************
 * とらんぽりん
 */
public class Trampoline extends ObjEX implements java.io.Serializable {
	static final long serialVersionUID = 1L;

	private static BufferedImage[] images = new BufferedImage[2];
	private static Rectangle4y boundary = new Rectangle4y();
	public int option;
	/**通常事故率*/
	public int accident1;
	/**餡子脳事故率*/
	public int accident2;
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	/**タイプ*/
	public static enum TrampolineType {
        NORMAL(ResourceUtil.getInstance().read("item_trampolinenorm")),
        EX(ResourceUtil.getInstance().read("item_trampolinedirection")),
       ;
        private String name;
        TrampolineType(String name) { this.name = name; }
        public String toString() { return name; }
	}
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {

        images[0] = ModLoader.loadItemImage(loader, "toy/trampoline.png");
        images[1] = ModLoader.loadItemImage(loader, "toy/shadow2.png");
        boundary.width = images[0].getWidth(io);
        boundary.height = images[0].getHeight(io);
        boundary.x = boundary.width >> 1;
        boundary.y = boundary.height - 1;
	}

	@Override
	public int getImageLayer(BufferedImage[] layer) {
		layer[0] = images[0];
		return 1;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	public BufferedImage getShadowImage() {
		return images[1];
	}

	@Override
	public void removeListData(){
		SimYukkuri.world.getCurrentMap().trampoline.remove(objId);
	}

	@Override
	public void grab() {
		grabbed = true;
	}

	@Override
	public void kick() {
		kick(0, -8, -4);
	}

	@Override
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}
	/**
	 * 当たり判定
	 * <br>ただし、これは特別に空中にいても当たり判定される
	 */
	public boolean checkHitObj(Obj o) {
		Rectangle tmpRect = new Rectangle();
		getCollisionRect(tmpRect);
		// 対象の座標をフィールド座標に変換
		Translate.translate(o.getX(), o.getY(), tmpPos);
		// 点が描画矩形に入ったかの判定
		if(tmpRect.contains(new java.awt.Point(tmpPos.x, tmpPos.y))) {
			return true;
		}

		return false;
	}

	@Override
	public boolean checkHitObj(Rectangle colRect, Obj o) {
		if( checkHitObj(o) )
		{
			return true;
		}

		return false;
	}

	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public Trampoline(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
        setBoundary(boundary);
        setCollisionSize(getPivotX(), getPivotY());
        SimYukkuri.world.getCurrentMap().trampoline.put(objId, this);
        objType = Type.OBJECT;
        objEXType = ObjEXType.TOY;
		boolean bRet = setupTrampoline(this);
		if( !bRet)
		{
			SimYukkuri.world.getCurrentMap().trampoline.remove(objId);
			return;
		}
        value = 500;
        cost = 0;
	}
	public Trampoline() {
		
	}

	/** 設定メニュー*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean setupTrampoline(Trampoline t) {
		t.accident1 = 0;
		t.accident2 = 0;
		JPanel mainPanel = new JPanel();
		JRadioButton but[] = new JRadioButton[TrampolineType.values().length];
		mainPanel.setLayout(new GridLayout(4, 1));
		mainPanel.setPreferredSize(new Dimension(150, 100));
		ButtonGroup bg = new ButtonGroup();
		for(int i = 0; i < but.length; i++){
			but[i] = new JRadioButton(TrampolineType.values()[i].toString());
			bg.add(but[i]);
			mainPanel.add(but[i]);
		}

		but[1].setSelected(true);
		String kagenmess[] = {
				"0", "1", "10", "20", "50", "100"
		};
		String jogenmess[] = {
				"0", "1", "10", "20", "50", "100"
		};
		JComboBox accident1Box = new JComboBox(kagenmess);
		JComboBox accident2Box = new JComboBox(jogenmess);
		accident1Box.setEditable(true);
		accident2Box.setEditable(true);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_trampolineacciaccurate")));
		mainPanel.add(accident1Box);
		mainPanel.add(new JLabel(ResourceUtil.getInstance().read("item_trampolineaccianko")));
		mainPanel.add(accident2Box);
		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel,
				ResourceUtil.getInstance().read("item_trampolinesettings"), 2, -1);
		if(dlgRet == 0){
			if(but[0].isSelected())
				t.option = 0;
			else
				t.option = 1;
			try{
				t.accident1 = Integer.parseInt(accident1Box.getSelectedItem().toString());
			}
			catch(NumberFormatException ne){
				t.accident1 = 0;
			}
			try{
				t.accident2 = Integer.parseInt(accident2Box.getSelectedItem().toString());
			}
			catch(NumberFormatException ne){
				t.accident2 = 0;
			}
			if(t.accident1 < 0)
				t.accident1 = 0;
			else
				if(t.accident1 > 100)
					t.accident1 = 100;
			if(t.accident2 < 0)
				t.accident2 = 0;
			else
				if(t.accident2 > 100)
					t.accident2 = 100;
		}
		else{
			return false;
		}

		return true;
	}
}



