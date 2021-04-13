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


public class MapWindow extends JDialog implements ActionListener, WindowListener {

	private static final String TITLE = "マップ移動";

	// 現在のマップリスト
	public static enum MAP {
		MYROOM("自室", "myroom"),
		GARDEN("庭", "garden"),
		STREET("路上", "street"),
		PARK1("公園１", "park1"),
		PARK2("公園２", "park2"),
		FOREST1("森林１", "forest1"),
		FOREST2("森林２", "forest2"),
		PLANT1("加工所１", "plant1"),
		PLANT2("加工所２", "plant2"),
		DISPOSER("廃棄物処理場", "disposer"),
		;
		public String displayName;
		public String filePath;
		private MAP(String disp, String path) {
			this.displayName = disp;
			this.filePath = path;
		}
		public String toString() {
			return this.displayName;
		}
	}

	private JToggleButton[] butList;

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
			butList[i] = new JToggleButton(m.displayName);
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
		MyPane.selectBody = null;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		Point pos = SimYukkuri.simYukkuri.getLocation();
		setLocation(pos.x + Translate.canvasW - 200, pos.y + 400);
		butList[SimYukkuri.world.currentMap.mapIndex].setSelected(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		MainCommandUI.playerButton[ToolButtonLabel.MOVE.ordinal()].setSelected(false);
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

