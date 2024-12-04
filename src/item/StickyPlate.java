package src.item;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.Transient;
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
import src.enums.CriticalDamegeType;
import src.enums.ObjEXType;
import src.enums.Type;
import src.system.ResourceUtil;

/***************************************************
 * 粘着板
 */
public class StickyPlate extends ObjEX implements java.io.Serializable {

	private static final long serialVersionUID = -4372169494877309751L;

	/**どこをくっつけるか*/
	public static enum StickyType {
        UNDER(ResourceUtil.getInstance().read("item_footsticky")),
        BACK(ResourceUtil.getInstance().read("item_backsticky")),
		;
        private String name;
        StickyType(String name) { this.name = name; }
        public String toString() { return name; }
	}
	/**処理対象(ゆっくり)*/
	public static final int hitCheckObjType = ObjEX.YUKKURI;
	private static BufferedImage[] images = new BufferedImage[4];
	private static Rectangle4y boundary = new Rectangle4y();

	private Body bindBody = null;
	private boolean bFixBack = false;

	private ItemRank itemRank;
	/**画像ロード*/
	public static void loadImages (ClassLoader loader, ImageObserver io) throws IOException {
			images[0] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate.png");
			images[1] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate_off.png");
			images[2] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate" + ModLoader.YK_WORD_NORA + ".png");
			images[3] = ModLoader.loadItemImage(loader, "stickyplate" + File.separator + "stickyplate" + ModLoader.YK_WORD_NORA + "_off.png");
			boundary.width = images[0].getWidth(io);
			boundary.height = images[0].getHeight(io);
			boundary.x = boundary.width >> 1;
			boundary.y = boundary.height >> 1;
	}


	@Override
	public int getImageLayer(BufferedImage[] layer) {
		if(itemRank == ItemRank.HOUSE) {
			if(enabled) layer[0] = images[0];
			else layer[0] = images[1];
		} else {
			if(enabled) layer[0] = images[2];
			else layer[0] = images[3];
		}
		return 1;
	}

	@Override
	@Transient
	public BufferedImage getShadowImage() {
		return null;
	}
	/**境界線の取得*/
	public static Rectangle4y getBounding() {
		return boundary;
	}

	@Override
	@Transient
	public int getHitCheckObjType() {
		return hitCheckObjType;
	}

	@Override	
	public boolean enableHitCheck() {
		if(bindBody != null) return false;
		return true;
	}
	
	public Body getBindBody(){
		return bindBody;
	}

	@Override	
	public int objHitProcess( Obj o ) {
		if(((Body)o).isDead()) return 0;
		if(((Body)o).getCriticalDamegeType() == CriticalDamegeType.CUT) return 0;

		if( bindBody != (Body)o){
			// 入れ替える場合
			if(bindBody != null){
				bindBody.setPullAndPush(false);
				
				// 針が刺さっていない、死んでる
				if( !bindBody.isNeedled() || bindBody.isDead() ){
					bindBody.setLockmove(false);
					bindBody.setFixBack(false);
				}
			}
			bindBody = (Body)o;
			bindBody.clearActions();			
		}
		bindBody.setCalcX(x);
		bindBody.setCalcY(y);
		bindBody.setLockmove(true);
		if( !bFixBack || bindBody.isPealed()){
			bindBody.setPullAndPush(true);
		}
		else {
			bindBody.setFixBack(true);
		}
		return 0;
	}

	@Override
	public void upDate() {
		if(!enabled && bindBody != null){
			bindBody.setLockmove(false);
			return ;
		}
		if(bindBody != null) {
			if( bFixBack){
				// 針が刺さっていない
				if( !bindBody.isNeedled() && !bindBody.isSleeping()){
					if(SimYukkuri.RND.nextInt(10) == 0){
						bindBody.setFurifuri(true);
					}
				}
			}
			
			if(grabbed) {
				bindBody.setCalcX(x);
				bindBody.setCalcY(y);

			}
			else{
				// ぷるぷる以外が原因で座標がずれている、死んでいる場合は初期化
				if( ((bindBody.getX() != x || bindBody.getY() != y) && !bindBody.isPurupuru()) ||
					( bindBody.isRemoved() || bindBody.isDead()) )
				{
					bindBody.setPullAndPush(false);
					// 針が刺さっていない、死んでる
					if( !bindBody.isNeedled() || bindBody.isDead()  ){
						bindBody.setLockmove(false);
						bindBody.setFixBack(false);
					}
					bindBody = null;
				}
			}
		}
	}
	
	@Override
	public void removeListData(){
		if(bindBody != null) {
			bindBody.setLockmove(false);
			bindBody.setPullAndPush(false);
			bindBody = null;
		}
		SimYukkuri.world.getCurrentMap().stickyPlate.remove(objId);
	}

	/**
	 * コンストラクタ
	 * @param initX x座標
	 * @param initY y座標
	 * @param initOption 0:飼い用、1;野良用
	 */
	public StickyPlate(int initX, int initY, int initOption) {
		super(initX, initY, initOption);
		setBoundary(boundary);
		setCollisionSize(getPivotX(), getPivotY());
		SimYukkuri.world.getCurrentMap().stickyPlate.put(objId, this);
		objType = Type.PLATFORM;
		objEXType = ObjEXType.STICKYPLATE;
		interval = 5;
		if( !setupStickyPlate(this))
		{
			SimYukkuri.world.getCurrentMap().stickyPlate.remove(objId);
			return;
		}
		itemRank = ItemRank.values()[initOption];
		if(itemRank == ItemRank.HOUSE) {
			value = 2000;
			cost = 0;
		} else {
			value = 0;
			cost = 0;
		}
	}
	public StickyPlate() {
		
	}

	/** 設定メニュー*/
	public static boolean setupStickyPlate(StickyPlate s) {
		
		JPanel mainPanel = new JPanel();
		JRadioButton[] but = new JRadioButton[StickyType.values().length];
		boolean ret = false;
		
		mainPanel.setLayout(new GridLayout(2, 1));
		mainPanel.setPreferredSize(new Dimension(100, 100));
		ButtonGroup bg = new ButtonGroup();

		for(int i = 0; i < but.length; i++) {
			but[i] = new JRadioButton(StickyType.values()[i].toString());
			bg.add(but[i]);

			mainPanel.add(but[i]);
		}

		but[0].setSelected(true);

		int dlgRet = JOptionPane.showConfirmDialog(SimYukkuri.mypane, mainPanel, 
				ResourceUtil.getInstance().read("item_stickysettings"), 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if(dlgRet == JOptionPane.OK_OPTION) {
			if(but[0].isSelected()) s.bFixBack = false;
			if(but[1].isSelected()) s.bFixBack = true;
			ret = true;
		}
		return ret;
	}


	public boolean isbFixBack() {
		return bFixBack;
	}


	public void setbFixBack(boolean bFixBack) {
		this.bFixBack = bFixBack;
	}


	public ItemRank getItemRank() {
		return itemRank;
	}


	public void setItemRank(ItemRank itemRank) {
		this.itemRank = itemRank;
	}


	public void setBindBody(Body bindBody) {
		this.bindBody = bindBody;
	}
	
}


