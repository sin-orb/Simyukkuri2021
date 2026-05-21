package org.simyukkuri.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;

/** ゆっくりの種別フィルターパネルを提供するクラス。 */
public class YukkuriFilterPanel {
	boolean[] beforeSelectedType;
	static YukkuriType[] yukkuriTypes = {
			// 通常種
			YukkuriType.MARISA,
			YukkuriType.MARISATSUMURI,
			YukkuriType.MARISAKOTATSUMURI,
			YukkuriType.DOSMARISA,
			YukkuriType.REIMU,
			YukkuriType.WASAREIMU,
			YukkuriType.DEIBU,
			YukkuriType.ALICE,
			YukkuriType.PATCH,
			YukkuriType.CHEN,
			YukkuriType.MYON,
			// 希少種
			YukkuriType.YURUSANAE,
			YukkuriType.AYAYA,
			YukkuriType.KIMEEMARU,
			YukkuriType.TENKO,
			YukkuriType.UDONGE,
			YukkuriType.MEIRIN,
			YukkuriType.SUWAKO,
			YukkuriType.CHIRUNO,
			YukkuriType.EIKI,
			YukkuriType.RAN,
			YukkuriType.NITORI,
			YukkuriType.YUUKA,
			YukkuriType.SAKUYA,
			// 捕食種
			YukkuriType.REMIRYA,
			YukkuriType.FRAN,
			YukkuriType.YUYUKO,
			// その他
			YukkuriType.TARINAI,
			YukkuriType.TARINAIREIMU,
			YukkuriType.MARISAREIMU,
			YukkuriType.REIMUMARISA,
			YukkuriType.HYBRIDYUKKURI,
	};

	/** 選択アクション */
	public static enum Action {
		SELECT_ALL(GameText.read("system_allselect"), ""),
		DSELECT_ALL(GameText.read("system_allselectoff"), ""),
		;

		private String name;

		Action(String nameJ, String nameE) {
			this.name = nameJ;
		}

		/** @return ボタンの表示名 */
		public String toString() {
			return name;
		}
	}

	/** コンストラクタ */
	public YukkuriFilterPanel() {
	}

	/**
	 * フィルターパネルを開く.
	 * 
	 * @param strHead            "対象設定"など
	 * @param strTop             トップに表示する文字列
	 * @param istrOptionList     フィルターするゆっくりの性質リスト
	 * @param ioResultSelectType 選ばれるタイプのリスト
	 * @param obOptionSelection  初期選択配列
	 * @return OKされたかどうか
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean openFilterPanel(String strHead, String strTop, List<String> istrOptionList,
			List<YukkuriType> ioResultSelectType, List<Boolean> obOptionSelection) {
		final List<YukkuriType> retSelectedType = new LinkedList<YukkuriType>();
		final int typeCount = yukkuriTypes.length;
		int optionCount = 0;
		if (istrOptionList != null) {
			optionCount = istrOptionList.size();
		}

		final JCheckBox[] checkBox = new JCheckBox[typeCount + optionCount];
		final JPanel mainPanel = new JPanel();
		final JPanel yukkuriPanel = new JPanel();
		final JPanel optionPanel = new JPanel();
		final JPanel buttonPanel = new JPanel();
		final JPanel centerPanel = new JPanel();
		final JPanel center2Panel = new JPanel();

		// レイアウト
		final GridBagLayout layout = new GridBagLayout();
		mainPanel.setLayout(layout);
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(center2Panel, constraints); // 制約の設定
		mainPanel.add(center2Panel);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(yukkuriPanel, constraints); // 制約の設定
		mainPanel.add(yukkuriPanel);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(optionPanel, constraints); // 制約の設定
		mainPanel.add(optionPanel);
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(buttonPanel, constraints); // 制約の設定
		mainPanel.add(buttonPanel);

		yukkuriPanel.setLayout(new GridLayout(9, 4));
		optionPanel.setLayout(new GridLayout(2, 5));
		buttonPanel.setLayout(new GridLayout(1, 5));

		// 枠線設定
		final LineBorder border = new LineBorder(Color.BLACK, 1, true);
		yukkuriPanel.setBorder(border);
		optionPanel.setBorder(border);
		// buttonPanel.setBorder(border);

		String[] names2 = new String[typeCount + optionCount];
		for (int k = 0; k < typeCount; k++) {
			String strTemp = yukkuriTypes[k].getNameJ();
			if (strTemp.length() != 0) {
				names2[k] = yukkuriTypes[k].getNameJ();
			} else {
				names2[k] = yukkuriTypes[k].toString();
			}
		}

		if (istrOptionList != null) {
			for (int k = typeCount; k < typeCount + optionCount; k++) {
				names2[k] = istrOptionList.get(k - typeCount);
			}
		}

		JLabel headLabel = new JLabel(strHead);
		centerPanel.add(headLabel);
		final JComboBox cb1 = new JComboBox(names2);
		cb1.setSelectedIndex(0);
		centerPanel.add(cb1);
		JLabel topLabel = new JLabel(strTop);
		center2Panel.add(topLabel);
		JLabel blankLabel = new JLabel("");

		center2Panel.add(blankLabel);
		for (int i = 0; i < typeCount; i++) {
			checkBox[i] = new JCheckBox(names2[i].toString());
			if (ioResultSelectType != null && ioResultSelectType.size() != 0) {
				if (ioResultSelectType.contains(yukkuriTypes[i])) {
					checkBox[i].setSelected(true);
				} else {
					checkBox[i].setSelected(false);
				}
			}
			yukkuriPanel.add(checkBox[i]);
		}

		for (int i = typeCount; i < typeCount + optionCount; i++) {
			checkBox[i] = new JCheckBox(names2[i].toString());
			if (obOptionSelection != null && optionCount == obOptionSelection.size()) {
				if (obOptionSelection.get(i - typeCount)) {
					checkBox[i].setSelected(true);
				} else {
					checkBox[i].setSelected(false);
				}
			}
			optionPanel.add(checkBox[i]);
		}

		final ButtonListener buttonListener = new ButtonListener();
		ButtonListener.setCheckbox(checkBox);

		final Action[] action = Action.values();
		for (int i = 0; i < action.length; i++) {
			JButton button = new JButton(action[i].toString());
			button.setActionCommand(action[i].name());
			button.addActionListener(buttonListener);
			buttonPanel.add(button);
		}

		final int dlgRet = JOptionPane.showConfirmDialog(GameView.getDialogParent(), mainPanel, strHead, 2, -1);
		if (dlgRet == 0) {
			for (int i = 0; i < typeCount; i++) {
				if (checkBox[i].isSelected()) {
					retSelectedType.add(yukkuriTypes[i]);
				}
			}
			ioResultSelectType.clear();
			ioResultSelectType.addAll(retSelectedType);

			if (obOptionSelection != null) {
				obOptionSelection.clear();
				for (int i = typeCount; i < typeCount + optionCount; i++) {
					if (checkBox[i].isSelected()) {
						obOptionSelection.add(true);
					} else {
						obOptionSelection.add(false);
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * ボタンリスナ
	 */
	public static class ButtonListener implements ActionListener {

		private static JCheckBox[] checkbox;

		/** @return チェックボックス配列 */
		public static JCheckBox[] getCheckbox() {
			return checkbox;
		}

		/** @param checkbox チェックボックス配列 */
		public static void setCheckbox(JCheckBox[] checkbox) {
			ButtonListener.checkbox = checkbox;
		}

		/** @param e アクションイベント */
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Action select = Action.valueOf(command);
			if (checkbox == null || checkbox.length == 0) {
				return;
			}

			switch (select) {
				case SELECT_ALL:
					for (JCheckBox cb : checkbox) {
						cb.setSelected(true);
					}
					break;
				case DSELECT_ALL:
					for (JCheckBox cb : checkbox) {
						cb.setSelected(false);
					}
					break;
				default:
					break;
			}
		}
	}
}
