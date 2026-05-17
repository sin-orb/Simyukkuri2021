package org.simyukkuri.ui;

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

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.ui.MainCommandUI.ToolButtonLabel;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameWorld;

/**
 * 持ち物ウィンドウの設計図クラス
 */
public class ItemWindow extends JDialog implements WindowListener, MouseListener, ActionListener, ListDataListener {
	private static final long serialVersionUID = 1359537638021473531L;

	private static final String TITLE = Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage())
			? "持ちもの"
			: "Belongings";

	@SuppressWarnings("rawtypes")
	private JList itemList;
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

		delButton = new JButton(GameText.read("system_throwaway"));
		// delButton.setPreferredSize(new Dimension(80, 30));
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
	/**
	 * Window opened.
	 *
	 * @param e イベント
	 */
	public void windowOpened(WindowEvent e) {
		itemList.setModel(GameWorld.get().getPlayer().getInventoryView());
		itemList.setSelectedIndex(-1);
		Point pos = SimYukkuri.simYukkuri.getLocation();
		setLocation(pos.x + Translate.getCanvasW() - 200, pos.y + 100);
	}

	@Override
	/**
	 * Window closing.
	 *
	 * @param e イベント
	 */
	public void windowClosing(WindowEvent e) {
		MainCommandUI.getPlayerButton()[ToolButtonLabel.BAG.ordinal()].setSelected(false);
		GameWorld.get().getPlayer().setHoldItem(null);
	}

	@Override
	/**
	 * Window closed.
	 *
	 * @param e イベント
	 */
	public void windowClosed(WindowEvent e) {
	}

	@Override
	/**
	 * Window iconified.
	 *
	 * @param e イベント
	 */
	public void windowIconified(WindowEvent e) {
	}

	@Override
	/**
	 * Window deiconified.
	 *
	 * @param e イベント
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	/**
	 * Window activated.
	 *
	 * @param e イベント
	 */
	public void windowActivated(WindowEvent e) {
	}

	@Override
	/**
	 * Window deactivated.
	 *
	 * @param e イベント
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	/**
	 * Mouse clicked.
	 *
	 * @param e イベント
	 */
	public void mouseClicked(MouseEvent e) {
		if (itemList.getSelectedIndices().length == 1) {
			int index = itemList.locationToIndex(e.getPoint());
			GameWorld.get().getPlayer().setHoldItem(GameWorld.get().getPlayer().getInventoryView().get(index));
		} else {
			GameWorld.get().getPlayer().setHoldItem(null);
		}
	}

	@Override
	/**
	 * Mouse pressed.
	 *
	 * @param e イベント
	 */
	public void mousePressed(MouseEvent e) {
	}

	@Override
	/**
	 * Mouse released.
	 *
	 * @param e イベント
	 */
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	/**
	 * Mouse entered.
	 *
	 * @param e イベント
	 */
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	/**
	 * Mouse exited.
	 *
	 * @param e イベント
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * 捨てるボタン
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (itemList.getSelectedIndex() == -1)
			return;

		int[] idx = itemList.getSelectedIndices();
		Entity[] obj = new Entity[idx.length];
		for (int i = 0; i < idx.length; i++) {
			obj[i] = GameWorld.get().getPlayer().getInventoryView().get(idx[i]);
			if (obj[i] != null)
				obj[i].remove();
		}
		for (int i = 0; i < obj.length; i++) {
			GameWorld.get().getPlayer().getInventoryView().removeElement(obj[i]);
		}
		itemList.setSelectedIndex(-1);
		GameWorld.get().getPlayer().setHoldItem(null);
	}

	@Override
	/**
	 * Interval added.
	 *
	 * @param e イベント
	 */
	public void intervalAdded(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}

	@Override
	/**
	 * Interval removed.
	 *
	 * @param e イベント
	 */
	public void intervalRemoved(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}

	@Override
	/**
	 * Contents changed.
	 *
	 * @param e イベント
	 */
	public void contentsChanged(ListDataEvent e) {
		itemList.setSelectedIndex(-1);
	}

	/** @return インベントリ表示用のリストコンポーネント */
	@SuppressWarnings("rawtypes")
	public JList getInventoryView() {
		return itemList;
	}
}
