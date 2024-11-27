package src.system;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import src.SimYukkuri;
import src.base.Obj;
import src.draw.Translate;
import src.system.MainCommandUI.ToolButtonLabel;

/**
 * 持ち物ウィンドウの設計図クラス
 */
public class ItemWindow extends JDialog implements WindowListener, MouseListener, ActionListener, ListDataListener {
	private static final long serialVersionUID = 1359537638021473531L;

	private static final String TITLE = Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())? "持ちもの": "Belongings";

	@SuppressWarnings("rawtypes")
	public JList itemList;
	private JButton delButton;

	@SuppressWarnings("rawtypes")
	/** コンストラクタ */
	public ItemWindow(Frame frame) {
		super(frame, TITLE, Dialog.ModalityType.MODELESS);
		addWindowListener(this);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		itemList = new JList();
		itemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		itemList.addMouseListener(this);

		JScrollPane sc = new JScrollPane(itemList);

		panel.add(sc, BorderLayout.CENTER);

		JPanel bp = new JPanel();
		bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));

		delButton = new JButton(ResourceUtil.getInstance().read("system_throwaway"));
//		delButton.setPreferredSize(new Dimension(80, 30));
		delButton.addActionListener(this);
		bp.add(delButton);
		panel.add(bp, BorderLayout.SOUTH);

		Container base = getContentPane();
		base.add(panel);

		setPreferredSize(new Dimension(200, 300));
		pack();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void windowOpened(WindowEvent e) {
		itemList.setModel(SimYukkuri.world.player.getItemList());
		itemList.setSelectedIndex(-1);
		Point pos = SimYukkuri.simYukkuri.getLocation();
		setLocation(pos.x + Translate.canvasW - 200, pos.y + 100);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		MainCommandUI.playerButton[ToolButtonLabel.BAG.ordinal()].setSelected(false);
		SimYukkuri.world.player.setHoldItem(null);
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


	@Override
	public void mouseClicked(MouseEvent e) {
		if(itemList.getSelectedIndices().length == 1) {
			int index = itemList.locationToIndex(e.getPoint());
			SimYukkuri.world.player.setHoldItem(SimYukkuri.world.player.getItemList().get(index));
		} else {
			SimYukkuri.world.player.setHoldItem(null);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 *  捨てるボタン
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(itemList.getSelectedIndex() == -1) return;
		
		int[] idx = itemList.getSelectedIndices();
		Obj[] obj = new Obj[idx.length];
		for(int i = 0; i < idx.length; i++) {
			obj[i] = SimYukkuri.world.player.getItemList().get(idx[i]);
			if(obj[i] != null) obj[i].remove();
		}
		for(int i = 0; i < obj.length; i++) {
			SimYukkuri.world.player.getItemList().removeElement(obj[i]);
		}
		itemList.setSelectedIndex(-1);
		SimYukkuri.world.player.setHoldItem(null);
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}
}




