package src.system;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import src.SimYukkuri;
import src.draw.MyPane;
import src.draw.TerrainField;
import src.draw.Translate;
import src.system.MainCommandUI.ToolButtonLabel;

/**
 * マップ移動ウィンドウの作成クラス
 */
public class MapWindow extends JDialog implements ActionListener, WindowListener {

	private static final long serialVersionUID = -742083190961529494L;
	private static final String TITLE = ResourceUtil.getInstance().read("system_movemap");

	/** 現在のマップリスト */
	public static enum MAP {
		MYROOM(ResourceUtil.getInstance().read("system_myroom"), "myroom"),
		GARDEN(ResourceUtil.getInstance().read("system_yard"), "garden"),
		STREET(ResourceUtil.getInstance().read("system_road"), "street"),
		PARK1(ResourceUtil.getInstance().read("system_park1"), "park1"),
		PARK2(ResourceUtil.getInstance().read("system_park2"), "park2"),
		FOREST1(ResourceUtil.getInstance().read("system_forest1"), "forest1"),
		FOREST2(ResourceUtil.getInstance().read("system_forest2"), "forest2"),
		PLANT1(ResourceUtil.getInstance().read("system_plant1"), "plant1"),
		PLANT2(ResourceUtil.getInstance().read("system_plant2"), "plant2"),
		DISPOSER(ResourceUtil.getInstance().read("system_disposer"), "disposer"),
		;
		private final String displayName;
		private final String filePath;
		private MAP(String disp, String path) {
			this.displayName = disp;
			this.filePath = path;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getFilePath() {
			return filePath;
		}

		public String toString() {
			return this.displayName;
		}
	}

	private JToggleButton[] butList;
	/**
	 * コンストラクタ
	 * @param frame フレーム
	 */
	public MapWindow(Frame frame) {
		super(frame, TITLE, Dialog.ModalityType.MODELESS);
		addWindowListener(this);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		butList = new JToggleButton[MAP.values().length];
		ButtonGroup bg = new ButtonGroup();
		int i = 0;
		for(MAP m :MAP.values()) {
			butList[i] = new JToggleButton(m.getDisplayName());
			butList[i].addActionListener(this);
			bg.add(butList[i]);
			panel.add(butList[i]);
			i++;
		}

		Container base = getContentPane();
		base.add(panel);

		setPreferredSize(new Dimension(200, 300));
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int idx = -1;
		for(int i = 0; i < butList.length; i++) {
			if(butList[i] == e.getSource()) {
				idx = i;
				break;
			}
		}
		
		// 描画スレッドにロックをかける
		SimYukkuri.world.setNextMap(idx);
		// 行き先設定
		SimYukkuri.mypane.loadTerrainFile();
		Translate.createTransTable(TerrainField.isPers());
		SimYukkuri.world.changeMap();
		MyPane.setSelectBody(null);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		Point pos = SimYukkuri.simYukkuri.getLocation();
		setLocation(pos.x + Translate.getCanvasW() - 200, pos.y + 400);
		butList[SimYukkuri.world.getCurrentMap().getMapIndex()].setSelected(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		MainCommandUI.getPlayerButton()[ToolButtonLabel.MOVE.ordinal()].setSelected(false);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}



