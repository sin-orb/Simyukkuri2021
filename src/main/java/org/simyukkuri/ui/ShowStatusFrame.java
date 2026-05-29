package org.simyukkuri.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.simyukkuri.draw.MyPane;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.Attachment;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Pain;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameWorld;

/**
 * ゆっくりのステータスを表示するFrame.
 */
public class ShowStatusFrame extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = -5118988467314035506L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	private JTextField textField_16;
	private JTextField textField_17;
	private JTextField textField_18;
	private JTextField textField_20;
	private JTextField textField_21;
	private JTextField textField_22;
	private JTextField textField_19;
	private JTextField textField_23;
	private JTextField textField_24;
	private JTextField textField_25;
	private JTextField textField_26;

	private static final ShowStatusFrame instance = new ShowStatusFrame();

	/**
	 * コンストラクタ.
	 */
	private ShowStatusFrame() {
		setTitle(GameText.read("command_status_title"));
		setBounds(100, 100, 788, 464);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel idLabel = new JLabel(GameText.read("command_status_id"));
		idLabel.setBounds(12, 10, 78, 20);
		contentPane.add(idLabel);

		textField = new JTextField();
		textField.setBounds(102, 12, 96, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel nameLabel = new JLabel(GameText.read("command_status_name"));
		nameLabel.setBounds(241, 15, 48, 13);
		contentPane.add(nameLabel);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBounds(295, 10, 128, 19);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JButton updateButton = new JButton(GameText.read("command_status_new"));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ワールドのゆっくりリストから目的のゆっくりを探し出し、その情報を更新する。すでにいない場合は更新されない。
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
				} else {
					ShowStatusFrame.getInstance().giveYukkuriInfo(body);
				}
			}
		});

		JButton nextBodyButton = new JButton(GameText.read("command_status_nextid"));
		nextBodyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Yukkuri> sorted = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values())
						.stream()
						.sorted().collect(Collectors.toList());
				int target = -1;
				for (int i = 0; i < sorted.size(); i++) {
					Yukkuri body = sorted.get(i);
					if (body.getUniqueId() > id) {
						target = i;
						break;
					}
				}
				if (target < 0) {
					target = 0;
				}
				if (sorted.size() == 0) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveYukkuriInfo(sorted.get(target));
				}
			}
		});
		nextBodyButton.setBounds(665, 42, 91, 21);
		contentPane.add(nextBodyButton);
		JButton previousBodyButton = new JButton(GameText.read("command_status_beforeid"));
		previousBodyButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Yukkuri> sorted = (List<Yukkuri>) new LinkedList<Yukkuri>(
						GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values()).stream()
						.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				int target = -1;
				for (int i = 0; i < sorted.size(); i++) {
					Yukkuri body = sorted.get(i);
					if (body.getUniqueId() < id) {
						target = i;
						break;
					}
				}
				if (target < 0) {
					target = 0;
				}
				if (sorted.size() == 0) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveYukkuriInfo(sorted.get(target));
				}
			}
		});
		previousBodyButton.setBounds(665, 73, 91, 21);
		contentPane.add(previousBodyButton);
		updateButton.setBounds(665, 11, 91, 21);
		contentPane.add(updateButton);

		JButton firstBodyButton = new JButton(GameText.read("command_status_first"));
		firstBodyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Yukkuri> sorted = new LinkedList<Yukkuri>(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values())
						.stream()
						.sorted().collect(Collectors.toList());
				if (sorted.size() == 0) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveYukkuriInfo(sorted.get(0));
				}
			}
		});
		firstBodyButton.setBounds(665, 104, 91, 21);
		contentPane.add(firstBodyButton);

		JButton lastBodyButton = new JButton(GameText.read("command_status_last"));
		lastBodyButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				List<Yukkuri> sorted = (List<Yukkuri>) new LinkedList<>(
						GameWorld.get().getCurrentWorldState().getYukkuriRegistry().values()).stream()
						.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				if (sorted.size() == 0) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveYukkuriInfo(sorted.get(0));
				}
			}
		});
		lastBodyButton.setBounds(665, 135, 91, 21);
		contentPane.add(lastBodyButton);

		JButton randomBodyButton = new JButton(GameText.read("command_status_random"));
		randomBodyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (GameWorld.get().getCurrentWorldState().getYukkuriRegistry().size() == 0) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				}
				int random = GameRandom.nextInt(GameWorld.get().getCurrentWorldState().getYukkuriRegistry().size());
				List<Yukkuri> bodyList = new LinkedList<>();
				for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
					bodyList.add(entry.getValue());
				}
				ShowStatusFrame.getInstance().giveYukkuriInfo(bodyList.get(random));
			}
		});
		randomBodyButton.setBounds(665, 167, 91, 21);
		contentPane.add(randomBodyButton);

		JButton partnerButton = new JButton(GameText.read("command_status_partner"));
		partnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					Yukkuri partner = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner());
					if (partner == null) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveYukkuriInfo(partner);
					}
				}
			}
		});
		partnerButton.setBounds(665, 198, 91, 21);
		contentPane.add(partnerButton);

		JButton childButton = new JButton(GameText.read("command_status_child"));
		childButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Integer> children = body.getChildren();
					if (children.size() == 0) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance()
								.giveYukkuriInfo(org.simyukkuri.util.YukkuriLookup.getYukkuriById(children.get(0)));
					}
				}
			}
		});
		childButton.setBounds(665, 229, 91, 21);
		contentPane.add(childButton);

		JButton elderSisterButton = new JButton(GameText.read("command_status_eldersister"));
		elderSisterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Integer> elderSisters = body.getElderSisters();
					if (elderSisters.size() == 0) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						Yukkuri elderSister = org.simyukkuri.util.YukkuriLookup.getYukkuriById(elderSisters.get(0));
						ShowStatusFrame.getInstance().giveYukkuriInfo(elderSister);
					}
				}
			}
		});
		elderSisterButton.setBounds(665, 260, 91, 21);
		contentPane.add(elderSisterButton);

		JButton youngerSisterButton = new JButton(GameText.read("command_status_littlesister"));
		youngerSisterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Integer> sisters = body.getSisters();
					if (sisters.size() == 0) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance()
								.giveYukkuriInfo(org.simyukkuri.util.YukkuriLookup.getYukkuriById(sisters.get(0)));
					}
				}
			}
		});
		youngerSisterButton.setBounds(665, 291, 91, 21);
		contentPane.add(youngerSisterButton);

		JButton fatherButton = new JButton(GameText.read("command_status_father"));
		fatherButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					Yukkuri father = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getFather());
					if (father == null) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveYukkuriInfo(father);
					}
				}
			}
		});
		fatherButton.setBounds(665, 322, 91, 21);
		contentPane.add(fatherButton);

		JButton motherButton = new JButton(GameText.read("command_status_mother"));
		motherButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(GameText.read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				Yukkuri body = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(id);
				if (body == null) {
					showError(GameText.read("command_status_noexistyukkurierror"));
					return;
				} else {
					Yukkuri mother = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getMother());
					if (mother == null) {
						showError(GameText.read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveYukkuriInfo(mother);
					}
				}
			}
		});
		motherButton.setBounds(665, 351, 91, 21);
		contentPane.add(motherButton);

		JLabel ageLabel = new JLabel(GameText.read("command_status_age"));
		ageLabel.setBounds(494, 16, 37, 13);
		contentPane.add(ageLabel);

		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setBounds(552, 13, 96, 19);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		JLabel purposeLabel = new JLabel(GameText.read("command_status_purpose"));
		purposeLabel.setBounds(12, 50, 78, 13);
		contentPane.add(purposeLabel);

		textField_3 = new JTextField();
		textField_3.setEditable(false);
		textField_3.setBounds(60, 48, 154, 19);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		JLabel purposeCoordinateLabel = new JLabel(GameText.read("command_status_purposecoordinate"));
		purposeCoordinateLabel.setBounds(224, 50, 62, 13);
		contentPane.add(purposeCoordinateLabel);

		textField_4 = new JTextField();
		textField_4.setEditable(false);
		textField_4.setBounds(295, 46, 128, 19);
		contentPane.add(textField_4);
		textField_4.setColumns(10);

		JLabel eventLabel = new JLabel(GameText.read("command_status_event"));
		eventLabel.setBounds(427, 51, 62, 13);
		contentPane.add(eventLabel);

		textField_5 = new JTextField();
		textField_5.setEditable(false);
		textField_5.setBounds(479, 49, 169, 19);
		contentPane.add(textField_5);
		textField_5.setColumns(10);

		JLabel nydLabel = new JLabel(GameText.read("command_status_nyd"));
		nydLabel.setBounds(12, 85, 128, 13);
		contentPane.add(nydLabel);

		textField_6 = new JTextField();
		textField_6.setEditable(false);
		textField_6.setBounds(133, 83, 96, 19);
		contentPane.add(textField_6);
		textField_6.setColumns(10);

		JLabel hpLabel = new JLabel(GameText.read("command_status_hp"));
		hpLabel.setBounds(12, 120, 96, 13);
		contentPane.add(hpLabel);

		textField_7 = new JTextField();
		textField_7.setEditable(false);
		textField_7.setBounds(122, 118, 96, 19);
		contentPane.add(textField_7);
		textField_7.setColumns(10);

		JLabel stomachLabel = new JLabel(GameText.read("command_status_stomach"));
		stomachLabel.setBounds(389, 122, 140, 13);
		contentPane.add(stomachLabel);

		textField_8 = new JTextField();
		textField_8.setEditable(false);
		textField_8.setBounds(552, 119, 96, 19);
		contentPane.add(textField_8);
		textField_8.setColumns(10);

		JLabel namingLabel = new JLabel(GameText.read("command_status_naming"));
		namingLabel.setBounds(243, 85, 50, 13);
		contentPane.add(namingLabel);

		textField_9 = new JTextField();
		textField_9.setEditable(false);
		textField_9.setBounds(295, 82, 128, 19);
		contentPane.add(textField_9);
		textField_9.setColumns(10);

		JLabel moldLevelLabel = new JLabel(GameText.read("command_status_moldlevel"));
		moldLevelLabel.setBounds(12, 155, 96, 13);
		contentPane.add(moldLevelLabel);

		textField_10 = new JTextField();
		textField_10.setEditable(false);
		textField_10.setBounds(102, 153, 96, 19);
		contentPane.add(textField_10);
		textField_10.setColumns(10);

		JLabel weightLabel = new JLabel(GameText.read("command_status_weight"));
		weightLabel.setBounds(243, 155, 50, 13);
		contentPane.add(weightLabel);

		textField_11 = new JTextField();
		textField_11.setEditable(false);
		textField_11.setBounds(295, 151, 128, 19);
		contentPane.add(textField_11);
		textField_11.setColumns(10);

		JLabel quantityLabel = new JLabel(GameText.read("command_status_quantity"));
		quantityLabel.setBounds(479, 157, 61, 13);
		contentPane.add(quantityLabel);

		textField_12 = new JTextField();
		textField_12.setEditable(false);
		textField_12.setBounds(552, 154, 96, 19);
		contentPane.add(textField_12);
		textField_12.setColumns(10);

		JLabel footBakeLevelLabel = new JLabel(GameText.read("command_status_footbakelevel"));
		footBakeLevelLabel.setBounds(12, 188, 96, 13);
		contentPane.add(footBakeLevelLabel);

		textField_13 = new JTextField();
		textField_13.setEditable(false);
		textField_13.setBounds(102, 186, 96, 19);
		contentPane.add(textField_13);
		textField_13.setColumns(10);

		JLabel favoritesLabel = new JLabel(GameText.read("command_status_favs"));
		favoritesLabel.setBounds(427, 87, 140, 13);
		contentPane.add(favoritesLabel);

		textField_14 = new JTextField();
		textField_14.setEditable(false);
		textField_14.setBounds(552, 84, 96, 19);
		contentPane.add(textField_14);
		textField_14.setColumns(10);

		JLabel traumaLabel = new JLabel(GameText.read("command_status_trauma"));
		traumaLabel.setBounds(479, 187, 61, 13);
		contentPane.add(traumaLabel);

		textField_15 = new JTextField();
		textField_15.setEditable(false);
		textField_15.setBounds(552, 184, 96, 19);
		contentPane.add(textField_15);
		textField_15.setColumns(10);

		JLabel elderSisterCountLabel = new JLabel(GameText.read("command_status_eldersister"));
		elderSisterCountLabel.setBounds(12, 221, 50, 13);
		contentPane.add(elderSisterCountLabel);

		textField_16 = new JTextField();
		textField_16.setEditable(false);
		textField_16.setBounds(43, 215, 603, 19);
		contentPane.add(textField_16);
		textField_16.setColumns(10);

		JLabel youngerSisterCountLabel = new JLabel(GameText.read("command_status_littlesister"));
		youngerSisterCountLabel.setBounds(12, 247, 50, 13);
		contentPane.add(youngerSisterCountLabel);

		textField_17 = new JTextField();
		textField_17.setEditable(false);
		textField_17.setBounds(43, 244, 603, 19);
		contentPane.add(textField_17);
		textField_17.setColumns(10);

		JLabel childCountLabel = new JLabel(GameText.read("command_status_child"));
		childCountLabel.setBounds(12, 275, 50, 13);
		contentPane.add(childCountLabel);

		textField_18 = new JTextField();
		textField_18.setEditable(false);
		textField_18.setBounds(43, 272, 603, 19);
		contentPane.add(textField_18);
		textField_18.setColumns(10);

		JLabel equipmentLabel = new JLabel(GameText.read("command_status_equip"));
		equipmentLabel.setBounds(207, 309, 50, 13);
		contentPane.add(equipmentLabel);

		JLabel carriedItemLabel = new JLabel(GameText.read("command_status_conveyitem"));
		carriedItemLabel.setBounds(441, 341, 91, 13);
		contentPane.add(carriedItemLabel);

		textField_20 = new JTextField();
		textField_20.setEditable(false);
		textField_20.setBounds(552, 336, 96, 19);
		contentPane.add(textField_20);
		textField_20.setColumns(10);

		JLabel statusLabel = new JLabel(GameText.read("command_status_status"));
		statusLabel.setBounds(12, 393, 80, 13);
		contentPane.add(statusLabel);

		textField_21 = new JTextField();
		textField_21.setEditable(false);
		textField_21.setBounds(102, 389, 603, 19);
		contentPane.add(textField_21);
		textField_21.setColumns(10);

		JLabel partnerRelationLabel = new JLabel(GameText.read("command_status_partner"));
		partnerRelationLabel.setBounds(14, 307, 76, 13);
		contentPane.add(partnerRelationLabel);

		textField_22 = new JTextField();
		textField_22.setEditable(false);
		textField_22.setBounds(253, 305, 395, 19);
		contentPane.add(textField_22);
		textField_22.setColumns(10);

		textField_19 = new JTextField();
		textField_19.setEditable(false);
		textField_19.setBounds(99, 303, 96, 19);
		contentPane.add(textField_19);
		textField_19.setColumns(10);

		JLabel antsLabel = new JLabel(GameText.read("command_status_ants"));
		antsLabel.setBounds(14, 337, 181, 13);
		contentPane.add(antsLabel);

		textField_23 = new JTextField();
		textField_23.setEditable(false);
		textField_23.setBounds(170, 334, 96, 19);
		contentPane.add(textField_23);
		textField_23.setColumns(10);

		JLabel blockCountLabel = new JLabel(GameText.read("command_status_blockcount"));
		blockCountLabel.setBounds(14, 366, 164, 13);
		contentPane.add(blockCountLabel);

		textField_24 = new JTextField();
		textField_24.setEditable(false);
		textField_24.setBounds(170, 363, 96, 19);
		contentPane.add(textField_24);
		textField_24.setColumns(10);

		JLabel canMoveLabel = new JLabel(GameText.read("command_status_canmove"));
		canMoveLabel.setBounds(441, 369, 80, 13);
		contentPane.add(canMoveLabel);

		textField_25 = new JTextField();
		textField_25.setEditable(false);
		textField_25.setBounds(552, 365, 96, 19);
		contentPane.add(textField_25);
		textField_25.setColumns(10);

		JLabel attitudePointLabel = new JLabel(GameText.read("command_attitudepoint"));
		attitudePointLabel.setBounds(226, 188, 96, 13);
		contentPane.add(attitudePointLabel);

		textField_26 = new JTextField();
		textField_26.setEditable(false);
		textField_26.setBounds(315, 184, 128, 19);
		contentPane.add(textField_26);
		textField_26.setColumns(10);

	}

	/**
	 * ゆっくりの情報を表示する.
	 * 
	 * @param body ゆっくり
	 */
	public void giveYukkuriInfo(Yukkuri body) {
		textField.setText(String.valueOf(body.getUniqueId()));
		textField_1.setText(body.toString());
		textField_2.setText(String.valueOf(body.getAge()));
		String purpose = null;
		switch (body.getPurposeOfMoving()) {
			case BED:
				purpose = GameText.read("command_status_wantsleep");
				break;
			case FOOD:
				purpose = GameText.read("command_status_wanteat");
				break;
			case NONE:
				purpose = GameText.read("command_status_noespecially");
				break;
			case SHIT:
				purpose = GameText.read("command_status_wantunun");
				break;
			case STEAL:
				purpose = GameText.read("command_status_wantsteal");
				break;
			case SUKKIRI:
				purpose = GameText.read("command_status_wantsukkiri");
				break;
			case TAKEOUT:
				purpose = GameText.read("command_status_wanttakeout");
				break;
			default:
				purpose = GameText.read("command_status_wantyukkuri");
		}
		textField_3.setText(purpose);
		String moveTargetId = GameText.read("command_status_nothing");
		Entity targetObject = body.takeMappedObj(body.getMoveTargetId());
		if (targetObject != null) {
			moveTargetId = targetObject.getX() + "," + targetObject.getY();
		}
		textField_4.setText(moveTargetId);
		String event = GameText.read("command_status_nothing");
		if (body.getCurrentEvent() != null) {
			event = body.getCurrentEvent().toString();
		}
		textField_5.setText(event);

		textField_6.setText(String.valueOf(body.getNonYukkuriDiseaseTolerance()));
		textField_7.setText((body.getDamageLimit() - body.getDamage()) + " / " + body.getDamageLimit());
		textField_8.setText(body.getHungry() + " / " + body.getHungryLimitBase()[body.getAgeState().ordinal()]);
		textField_9.setText(body.getMyName());
		textField_10.setText(String.valueOf(body.getSickPeriod()));
		textField_11.setText(String.valueOf(body.getWeight()));
		textField_12.setText(String.valueOf(body.getAnkoAmount()));
		String footBake = null;
		switch (body.getFootBakeLevel()) {
			case CRITICAL:
				footBake = GameText.read("command_status_fatal");
				break;
			case MEDIUM:
				footBake = GameText.read("command_status_middle");
				break;
			default:
				footBake = GameText.read("command_status_nothing");
		}
		textField_13.setText(footBake);
		String fav = "";
		if (body.getFavoriteItems().size() != 0) {
			for (Map.Entry<FavItemType, Integer> entry : body.getFavoriteItems().entrySet()) {
				fav += entry.getKey() + ",";
			}
			fav = fav.substring(0, fav.length() - 1);
		} else {
			fav = GameText.read("command_status_nothing");
		}
		textField_14.setText(fav);
		String trauma = null;
		switch (body.getTrauma()) {
			case Factory:
				trauma = GameText.read("command_status_plant");
				break;
			case Ubuse:
				trauma = GameText.read("command_status_abuse");
				break;
			default:
				trauma = GameText.read("command_status_nothing");
		}
		textField_15.setText(trauma);
		String elder = "";
		if (body.getElderSisters().size() != 0) {
			for (int sisterId : body.getElderSisters()) {
				elder = elder += sisterId + ", ";
			}
			elder = elder.substring(0, elder.length() - 2);
		} else {
			elder = GameText.read("command_status_nothing");
		}
		textField_16.setText(elder);
		String sister = "";
		if (body.getSisters().size() != 0) {
			for (int sisterId : body.getSisters()) {
				sister += sisterId + ", ";
			}
			sister = sister.substring(0, sister.length() - 2);
		} else {
			sister = GameText.read("command_status_nothing");
		}
		textField_17.setText(sister);
		String child = "";
		if (body.getChildren().size() != 0) {
			for (int childId : body.getChildren()) {
				child += childId + ",";
			}
			child = child.substring(0, child.length() - 1);
		} else {
			child = GameText.read("command_status_nothing");
		}
		textField_18.setText(child);
		textField_19.setText(
				org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner()) != null ? String.valueOf(body.getPartner())
						: GameText.read("command_status_nothing"));
		String bring = "";
		if (body.getCarryItems().size() != 0) {
			for (TakeoutItemType item : body.getCarryItems().keySet()) {
				switch (item) {
					case FOOD:
						bring = GameText.read("command_status_food");
						break;
					case SHIT:
						bring = GameText.read("command_status_unun");
						break;
					case TOY:
						bring = GameText.read("command_status_toys");
						break;
					default:
						bring = GameText.read("command_status_children");
				}
			}
		} else {
			bring = GameText.read("command_status_nothing");
		}
		textField_20.setText(bring);
		String emo = "";
		if (body.isAngry()) {
			emo += GameText.read("command_status_anger");
		}
		if (body.isScare()) {
			emo += GameText.read("command_status_scare");
		}
		if (body.getPainState() != Pain.NONE) {
			emo += GameText.read("command_status_hurt");
		}
		if (body.isVerySad()) {
			emo += GameText.read("command_status_very_sad");
		} else if (body.isSad()) {
			emo += GameText.read("command_status_sad");
		}
		if (body.isBlind()) {
			emo += GameText.read("command_status_blind");
		}
		if (body.isPurupuru()) {
			emo += GameText.read("command_status_shake");
		}
		if (body.isDirty()) {
			emo += GameText.read("command_status_dirty");
		}
		if (body.isExciting()) {
			emo += GameText.read("command_status_hatsujou");
		}
		if (body.isHappy()) {
			emo += GameText.read("command_status_pleasure");
		}
		if (body.isMelt()) {
			emo += GameText.read("command_status_melt");
		}
		if (body.isOnlyAmaama()) {
			emo += GameText.read("command_status_tastedestruct");
		}
		if (body.isRelax()) {
			emo += GameText.read("command_status_doyukkuri");
		}
		if (body.isSilent()) {
			emo += GameText.read("command_status_silent");
		}
		if (body.isSleepy()) {
			emo += GameText.read("command_status_sleepy");
		}
		if (body.isStaying()) {
			emo += GameText.read("command_status_wait");
		}
		if (body.isVain()) {
			emo += GameText.read("command_status_vain");
		}
		if (body.isWet()) {
			emo += GameText.read("command_status_wet");
		}
		if (body.isYunnyaa()) {
			emo += GameText.read("command_status_yunyaa");
		}
		if (emo.length() == 0) {
			emo = GameText.read("command_status_nostate");
		} else {
			emo = emo.substring(0, emo.length() - 2);
		}
		textField_21.setText(emo);
		String attach = "";
		if (body.getAttach() != null && body.getAttach().size() != 0) {
			for (Attachment attachment : body.getAttach()) {
				attach = attach += attachment.toString() + " / ";
			}
		}
		if (attach.length() == 0) {
			attach = GameText.read("command_status_nothing");
		} else {
			attach = attach.substring(0, attach.length() - 2);
		}
		textField_22.setText(attach);

		textField_23.setText(String.valueOf(body.getAntCount()));
		textField_24.setText(String.valueOf(body.getBlockedTicks()));
		textField_25.setText(body.isLockmove() || body.isDead() ? GameText.read("command_status_cantmove")
				: GameText.read("command_status_canmove"));
		textField_26.setText(String.valueOf(body.getAttitudePoint()));
		MyPane.setSelectedYukkuri(body);
	}

	/**
	 * Shows error.
	 *
	 * @param s the s
	 */
	public static void showError(String s) {
		JLabel errorLabel = new JLabel(s);
		errorLabel.setForeground(Color.RED);
		JOptionPane.showMessageDialog(ShowStatusFrame.getInstance(), errorLabel);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
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
	public void actionPerformed(ActionEvent e) {
	}

	/**
	 * インスタンスを返す.
	 * 
	 * @return インスタンス
	 */
	public static ShowStatusFrame getInstance() {
		return instance;
	}
}
