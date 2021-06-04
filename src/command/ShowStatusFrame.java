package src.command;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import src.SimYukkuri;
import src.base.Attachment;
import src.base.Body;
import src.base.Obj;
import src.enums.FavItemType;
import src.enums.Pain;
import src.enums.TakeoutItemType;
import src.system.ResourceUtil;

/**
 * ゆっくりのステータスを表示するFrame.
 */
public class ShowStatusFrame extends JFrame implements ActionListener, WindowListener {

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
		setTitle(ResourceUtil.getInstance().read("command_status_title"));
		setBounds(100, 100, 788, 464);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel(ResourceUtil.getInstance().read("command_status_id"));
		lblNewLabel.setBounds(12, 10, 78, 20);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(102, 12, 96, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel(ResourceUtil.getInstance().read("command_status_name"));
		lblNewLabel_1.setBounds(241, 15, 48, 13);
		contentPane.add(lblNewLabel_1);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBounds(295, 10, 128, 19);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JButton btnNewButton = new JButton(ResourceUtil.getInstance().read("command_status_new"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//ワールドのゆっくりリストから目的のゆっくりを探し出し、その情報を更新する。すでにいない場合は更新されない。
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
				} else {
					ShowStatusFrame.getInstance().giveBodyInfo(yukkuris.get(0));
				}
			}
		});

		JButton btnNewButton_1 = new JButton(ResourceUtil.getInstance().read("command_status_nextid"));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> sorted = SimYukkuri.world.getCurrentMap().body.stream()
						.sorted().collect(Collectors.toList());
				int target = -1;
				for (int i = 0; i < sorted.size(); i++) {
					Body b = sorted.get(i);
					if (b.getUniqueID() > id) {
						target = i;
						break;
					}
				}
				if (target < 0) {
					target = 0;
				}
				if (sorted.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveBodyInfo(sorted.get(target));
				}
			}
		});
		btnNewButton_1.setBounds(665, 42, 91, 21);
		contentPane.add(btnNewButton_1);
		JButton btnNewButton_2 = new JButton(ResourceUtil.getInstance().read("command_status_beforeid"));
		btnNewButton_2.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> sorted = (List<Body>) SimYukkuri.world.getCurrentMap().body.stream()
						.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				int target = -1;
				for (int i = 0; i < sorted.size(); i++) {
					Body b = sorted.get(i);
					if (b.getUniqueID() < id) {
						target = i;
						break;
					}
				}
				if (target < 0) {
					target = 0;
				}
				if (sorted.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveBodyInfo(sorted.get(target));
				}
			}
		});
		btnNewButton_2.setBounds(665, 73, 91, 21);
		contentPane.add(btnNewButton_2);
		btnNewButton.setBounds(665, 11, 91, 21);
		contentPane.add(btnNewButton);

		JButton btnNewButton_3 = new JButton(ResourceUtil.getInstance().read("command_status_first"));
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Body> sorted = SimYukkuri.world.getCurrentMap().body.stream()
						.sorted().collect(Collectors.toList());
				if (sorted.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveBodyInfo(sorted.get(0));
				}
			}
		});
		btnNewButton_3.setBounds(665, 104, 91, 21);
		contentPane.add(btnNewButton_3);

		JButton btnNewButton_4 = new JButton(ResourceUtil.getInstance().read("command_status_last"));
		btnNewButton_4.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				List<Body> sorted = (List<Body>) SimYukkuri.world.getCurrentMap().body.stream()
						.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
				if (sorted.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					ShowStatusFrame.getInstance().giveBodyInfo(sorted.get(0));
				}
			}
		});
		btnNewButton_4.setBounds(665, 135, 91, 21);
		contentPane.add(btnNewButton_4);

		JButton btnNewButton_5 = new JButton(ResourceUtil.getInstance().read("command_status_random"));
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int random = SimYukkuri.RND.nextInt(SimYukkuri.world.getCurrentMap().body.size());
				ShowStatusFrame.getInstance().giveBodyInfo(SimYukkuri.world.getCurrentMap().body.get(random));
			}
		});
		btnNewButton_5.setBounds(665, 167, 91, 21);
		contentPane.add(btnNewButton_5);

		JButton btnNewButton_6 = new JButton(ResourceUtil.getInstance().read("command_status_partner"));
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					Body partner = yukkuris.get(0).getPartner();
					if (partner == null) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(partner);
					}
				}
			}
		});
		btnNewButton_6.setBounds(665, 198, 91, 21);
		contentPane.add(btnNewButton_6);

		JButton btnNewButton_7 = new JButton(ResourceUtil.getInstance().read("command_status_child"));
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Body> children = yukkuris.get(0).getChildrenList();
					if (children.size() == 0) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(children.get(0));
					}
				}
			}
		});
		btnNewButton_7.setBounds(665, 229, 91, 21);
		contentPane.add(btnNewButton_7);

		JButton btnNewButton_8 = new JButton(ResourceUtil.getInstance().read("command_status_eldersister"));
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Body> elderSisters = yukkuris.get(0).getElderSisterList();
					if (elderSisters.size() == 0) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(elderSisters.get(0));
					}
				}
			}
		});
		btnNewButton_8.setBounds(665, 260, 91, 21);
		contentPane.add(btnNewButton_8);

		JButton btnNewButton_9 = new JButton(ResourceUtil.getInstance().read("command_status_littlesister"));
		btnNewButton_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					List<Body> sisters = yukkuris.get(0).getSisterList();
					if (sisters.size() == 0) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(sisters.get(0));
					}
				}
			}
		});
		btnNewButton_9.setBounds(665, 291, 91, 21);
		contentPane.add(btnNewButton_9);

		JButton btnNewButton_10 = new JButton(ResourceUtil.getInstance().read("command_status_father"));
		btnNewButton_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					Body target = yukkuris.get(0);
					Body father = target.getFather();
					if (father == null) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(father);
					}
				}
			}
		});
		btnNewButton_10.setBounds(665, 322, 91, 21);
		contentPane.add(btnNewButton_10);

		JButton btnNewButton_11 = new JButton(ResourceUtil.getInstance().read("command_status_mother"));
		btnNewButton_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idTemp = 0;
				try {
					idTemp = Integer.parseInt(textField.getText());
				} catch (Exception ex) {
					showError(ResourceUtil.getInstance().read("command_status_numericerror"));
					return;
				}
				final int id = idTemp;
				List<Body> yukkuris = SimYukkuri.world.getCurrentMap().body.stream()
						.filter(b -> b.getUniqueID() == id)
						.collect(Collectors.toList());
				if (yukkuris.size() == 0) {
					showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
					return;
				} else {
					Body target = yukkuris.get(0);
					Body mother = target.getMother();
					if (mother == null) {
						showError(ResourceUtil.getInstance().read("command_status_noexistyukkurierror"));
						return;
					} else {
						ShowStatusFrame.getInstance().giveBodyInfo(mother);
					}
				}
			}
		});
		btnNewButton_11.setBounds(665, 351, 91, 21);
		contentPane.add(btnNewButton_11);

		JLabel lblNewLabel_2 = new JLabel(ResourceUtil.getInstance().read("command_status_age"));
		lblNewLabel_2.setBounds(494, 16, 37, 13);
		contentPane.add(lblNewLabel_2);

		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setBounds(552, 13, 96, 19);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel(ResourceUtil.getInstance().read("command_status_purpose"));
		lblNewLabel_3.setBounds(12, 50, 78, 13);
		contentPane.add(lblNewLabel_3);

		textField_3 = new JTextField();
		textField_3.setEditable(false);
		textField_3.setBounds(60, 48, 154, 19);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel(ResourceUtil.getInstance().read("command_status_purposecoordinate"));
		lblNewLabel_4.setBounds(224, 50, 62, 13);
		contentPane.add(lblNewLabel_4);

		textField_4 = new JTextField();
		textField_4.setEditable(false);
		textField_4.setBounds(295, 46, 128, 19);
		contentPane.add(textField_4);
		textField_4.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel(ResourceUtil.getInstance().read("command_status_event"));
		lblNewLabel_5.setBounds(427, 51, 62, 13);
		contentPane.add(lblNewLabel_5);

		textField_5 = new JTextField();
		textField_5.setEditable(false);
		textField_5.setBounds(479, 49, 169, 19);
		contentPane.add(textField_5);
		textField_5.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel(ResourceUtil.getInstance().read("command_status_nyd"));
		lblNewLabel_6.setBounds(12, 85, 128, 13);
		contentPane.add(lblNewLabel_6);

		textField_6 = new JTextField();
		textField_6.setEditable(false);
		textField_6.setBounds(133, 83, 96, 19);
		contentPane.add(textField_6);
		textField_6.setColumns(10);

		JLabel lblNewLabel_7 = new JLabel(ResourceUtil.getInstance().read("command_status_hp"));
		lblNewLabel_7.setBounds(12, 120, 96, 13);
		contentPane.add(lblNewLabel_7);

		textField_7 = new JTextField();
		textField_7.setEditable(false);
		textField_7.setBounds(122, 118, 96, 19);
		contentPane.add(textField_7);
		textField_7.setColumns(10);

		JLabel lblNewLabel_8 = new JLabel(ResourceUtil.getInstance().read("command_status_stomach"));
		lblNewLabel_8.setBounds(389, 122, 140, 13);
		contentPane.add(lblNewLabel_8);

		textField_8 = new JTextField();
		textField_8.setEditable(false);
		textField_8.setBounds(552, 119, 96, 19);
		contentPane.add(textField_8);
		textField_8.setColumns(10);

		JLabel lblNewLabel_9 = new JLabel(ResourceUtil.getInstance().read("command_status_naming"));
		lblNewLabel_9.setBounds(243, 85, 50, 13);
		contentPane.add(lblNewLabel_9);

		textField_9 = new JTextField();
		textField_9.setEditable(false);
		textField_9.setBounds(295, 82, 128, 19);
		contentPane.add(textField_9);
		textField_9.setColumns(10);

		JLabel lblNewLabel_10 = new JLabel(ResourceUtil.getInstance().read("command_status_moldlevel"));
		lblNewLabel_10.setBounds(12, 155, 96, 13);
		contentPane.add(lblNewLabel_10);

		textField_10 = new JTextField();
		textField_10.setEditable(false);
		textField_10.setBounds(102, 153, 96, 19);
		contentPane.add(textField_10);
		textField_10.setColumns(10);

		JLabel lblNewLabel_11 = new JLabel(ResourceUtil.getInstance().read("command_status_weight"));
		lblNewLabel_11.setBounds(243, 155, 50, 13);
		contentPane.add(lblNewLabel_11);

		textField_11 = new JTextField();
		textField_11.setEditable(false);
		textField_11.setBounds(295, 151, 128, 19);
		contentPane.add(textField_11);
		textField_11.setColumns(10);

		JLabel lblNewLabel_12 = new JLabel(ResourceUtil.getInstance().read("command_status_quantity"));
		lblNewLabel_12.setBounds(479, 157, 61, 13);
		contentPane.add(lblNewLabel_12);

		textField_12 = new JTextField();
		textField_12.setEditable(false);
		textField_12.setBounds(552, 154, 96, 19);
		contentPane.add(textField_12);
		textField_12.setColumns(10);

		JLabel lblNewLabel_13 = new JLabel(ResourceUtil.getInstance().read("command_status_footbakelevel"));
		lblNewLabel_13.setBounds(12, 188, 96, 13);
		contentPane.add(lblNewLabel_13);

		textField_13 = new JTextField();
		textField_13.setEditable(false);
		textField_13.setBounds(102, 186, 96, 19);
		contentPane.add(textField_13);
		textField_13.setColumns(10);

		JLabel lblNewLabel_14 = new JLabel(ResourceUtil.getInstance().read("command_status_favs"));
		lblNewLabel_14.setBounds(427, 87, 140, 13);
		contentPane.add(lblNewLabel_14);

		textField_14 = new JTextField();
		textField_14.setEditable(false);
		textField_14.setBounds(552, 84, 96, 19);
		contentPane.add(textField_14);
		textField_14.setColumns(10);

		JLabel lblNewLabel_15 = new JLabel(ResourceUtil.getInstance().read("command_status_trauma"));
		lblNewLabel_15.setBounds(479, 187, 61, 13);
		contentPane.add(lblNewLabel_15);

		textField_15 = new JTextField();
		textField_15.setEditable(false);
		textField_15.setBounds(552, 184, 96, 19);
		contentPane.add(textField_15);
		textField_15.setColumns(10);

		JLabel lblNewLabel_16 = new JLabel(ResourceUtil.getInstance().read("command_status_eldersister"));
		lblNewLabel_16.setBounds(12, 221, 50, 13);
		contentPane.add(lblNewLabel_16);

		textField_16 = new JTextField();
		textField_16.setEditable(false);
		textField_16.setBounds(43, 215, 603, 19);
		contentPane.add(textField_16);
		textField_16.setColumns(10);

		JLabel lblNewLabel_17 = new JLabel(ResourceUtil.getInstance().read("command_status_littlesister"));
		lblNewLabel_17.setBounds(12, 247, 50, 13);
		contentPane.add(lblNewLabel_17);

		textField_17 = new JTextField();
		textField_17.setEditable(false);
		textField_17.setBounds(43, 244, 603, 19);
		contentPane.add(textField_17);
		textField_17.setColumns(10);

		JLabel lblNewLabel_18 = new JLabel(ResourceUtil.getInstance().read("command_status_child"));
		lblNewLabel_18.setBounds(12, 275, 50, 13);
		contentPane.add(lblNewLabel_18);

		textField_18 = new JTextField();
		textField_18.setEditable(false);
		textField_18.setBounds(43, 272, 603, 19);
		contentPane.add(textField_18);
		textField_18.setColumns(10);

		JLabel lblNewLabel_19 = new JLabel(ResourceUtil.getInstance().read("command_status_equip"));
		lblNewLabel_19.setBounds(207, 309, 50, 13);
		contentPane.add(lblNewLabel_19);

		JLabel lblNewLabel_20 = new JLabel(ResourceUtil.getInstance().read("command_status_conveyitem"));
		lblNewLabel_20.setBounds(441, 341, 91, 13);
		contentPane.add(lblNewLabel_20);

		textField_20 = new JTextField();
		textField_20.setEditable(false);
		textField_20.setBounds(552, 336, 96, 19);
		contentPane.add(textField_20);
		textField_20.setColumns(10);

		JLabel lblNewLabel_21 = new JLabel(ResourceUtil.getInstance().read("command_status_status"));
		lblNewLabel_21.setBounds(12, 393, 80, 13);
		contentPane.add(lblNewLabel_21);

		textField_21 = new JTextField();
		textField_21.setEditable(false);
		textField_21.setBounds(102, 389, 603, 19);
		contentPane.add(textField_21);
		textField_21.setColumns(10);

		JLabel lblNewLabel_22 = new JLabel(ResourceUtil.getInstance().read("command_status_partner"));
		lblNewLabel_22.setBounds(14, 307, 76, 13);
		contentPane.add(lblNewLabel_22);

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

		JLabel lblNewLabel_23 = new JLabel(ResourceUtil.getInstance().read("command_status_ants"));
		lblNewLabel_23.setBounds(14, 337, 181, 13);
		contentPane.add(lblNewLabel_23);

		textField_23 = new JTextField();
		textField_23.setEditable(false);
		textField_23.setBounds(170, 334, 96, 19);
		contentPane.add(textField_23);
		textField_23.setColumns(10);

		JLabel lblNewLabel_24 = new JLabel(ResourceUtil.getInstance().read("command_status_blockcount"));
		lblNewLabel_24.setBounds(14, 366, 164, 13);
		contentPane.add(lblNewLabel_24);

		textField_24 = new JTextField();
		textField_24.setEditable(false);
		textField_24.setBounds(170, 363, 96, 19);
		contentPane.add(textField_24);
		textField_24.setColumns(10);

		JLabel lblNewLabel_25 = new JLabel(ResourceUtil.getInstance().read("command_status_canmove"));
		lblNewLabel_25.setBounds(441, 369, 80, 13);
		contentPane.add(lblNewLabel_25);

		textField_25 = new JTextField();
		textField_25.setEditable(false);
		textField_25.setBounds(552, 365, 96, 19);
		contentPane.add(textField_25);
		textField_25.setColumns(10);
		
		JLabel lblNewLabel_26 = new JLabel(ResourceUtil.getInstance().read("command_attitudepoint"));
		lblNewLabel_26.setBounds(226, 188, 96, 13);
		contentPane.add(lblNewLabel_26);
		
		textField_26 = new JTextField();
		textField_26.setEditable(false);
		textField_26.setBounds(315, 184, 128, 19);
		contentPane.add(textField_26);
		textField_26.setColumns(10);
		
	}

	/**
	 * ゆっくりの情報を表示する.
	 * @param b ゆっくり
	 */
	public void giveBodyInfo(Body b) {
		textField.setText(String.valueOf(b.getUniqueID()));
		textField_1.setText(b.toString());
		textField_2.setText(String.valueOf(b.getAge()));
		String purpose = null;
		switch (b.getPurposeOfMoving()) {
		case BED:
			purpose = ResourceUtil.getInstance().read("command_status_wantsleep");
			break;
		case FOOD:
			purpose = ResourceUtil.getInstance().read("command_status_wanteat");
			break;
		case NONE:
			purpose = ResourceUtil.getInstance().read("command_status_noespecially");
			break;
		case SHIT:
			purpose = ResourceUtil.getInstance().read("command_status_wantunun");
			break;
		case STEAL:
			purpose = ResourceUtil.getInstance().read("command_status_wantsteal");
			break;
		case SUKKIRI:
			purpose = ResourceUtil.getInstance().read("command_status_wantsukkiri");
			break;
		case TAKEOUT:
			purpose = ResourceUtil.getInstance().read("command_status_wanttakeout");
			break;
		default:
			purpose = ResourceUtil.getInstance().read("command_status_wantyukkuri");
		}
		textField_3.setText(purpose);
		String moveTarget = ResourceUtil.getInstance().read("command_status_nothing");
		if (b.getMoveTarget() != null) {
			moveTarget = b.getMoveTarget().getX() + "," + b.getMoveTarget().getY();
		}
		textField_4.setText(moveTarget);
		String event = ResourceUtil.getInstance().read("command_status_nothing");
		if (b.getCurrentEvent() != null) {
			event = b.getCurrentEvent().toString();
		}
		textField_5.setText(event);

		textField_6.setText(String.valueOf(b.checkNonYukkuriDiseaseTolerance()));
		textField_7.setText((b.getDamageLimit() - b.getDamage()) + " / " + b.getDamageLimit());
		textField_8.setText(b.getHungry() + " / " + b.getHUNGRYLIMIT()[b.getBodyAgeState().ordinal()]);
		textField_9.setText(b.getMyName());
		textField_10.setText(String.valueOf(b.getSickPeriod()));
		textField_11.setText(String.valueOf(b.getWeight()));
		textField_12.setText(String.valueOf(b.getBodyAmount()));
		String footBake = null;
		switch (b.getFootBakeLevel()) {
		case CRITICAL:
			footBake = ResourceUtil.getInstance().read("command_status_fatal");
			break;
		case MIDIUM:
			footBake = ResourceUtil.getInstance().read("command_status_middle");
			break;
		default:
			footBake = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_13.setText(footBake);
		String fav = "";
		if (b.getFavItem().size() != 0) {
			for (Map.Entry<FavItemType, Obj> entry : b.getFavItem().entrySet()) {
				fav += entry.getKey() + ",";
			}
			fav = fav.substring(0, fav.length() - 1);
		} else {
			fav = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_14.setText(fav);
		String trauma = null;
		switch (b.getTrauma()) {
		case Factory:
			trauma = ResourceUtil.getInstance().read("command_status_plant");
			break;
		case Ubuse:
			trauma = ResourceUtil.getInstance().read("command_status_abuse");
			break;
		default:
			trauma = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_15.setText(trauma);
		String elder = "";
		if (b.getElderSisterList().size() != 0) {
			Set<Body> s = new TreeSet<>();
			for (Body y : b.getElderSisterList()) {
				s.add(y);
			}
			for (Body y : s) {
				elder = elder += y.getUniqueID() + ", ";
			}
			elder = elder.substring(0, elder.length() - 2);
		} else {
			elder = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_16.setText(elder);
		String sister = "";
		if (b.getSisterList().size() != 0) {
			Set<Body> s = new TreeSet<>();
			for (Body y : b.getSisterList()) {
				s.add(y);
			}
			for (Body y : s) {
				sister += y.getUniqueID() + ", ";
			}
			sister = sister.substring(0, sister.length() - 2);
		} else {
			sister = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_17.setText(sister);
		String child = "";
		if (b.getChildrenList().size() != 0) {
			Set<Body> s = new TreeSet<>();
			for (Body y : b.getChildrenList()) {
				s.add(y);
			}
			for (Body y : s) {
				child += y.getUniqueID() + ",";
			}
			child = child.substring(0, child.length() - 1);
		} else {
			child = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_18.setText(child);
		textField_19.setText(b.getPartner() != null ? String.valueOf(b.getPartner().getUniqueID()) : ResourceUtil.getInstance().read("command_status_nothing"));
		String bring = "";
		if (b.getTakeoutItem().size() != 0) {
			for (TakeoutItemType item : b.getTakeoutItem().keySet()) {
				switch (item) {
				case FOOD:
					bring = ResourceUtil.getInstance().read("command_status_food");
					break;
				case SHIT:
					bring = ResourceUtil.getInstance().read("command_status_unun");
					break;
				case TOY:
					bring = ResourceUtil.getInstance().read("command_status_toys");
					break;
				default:
					bring = ResourceUtil.getInstance().read("command_status_children");
				}
			}
		} else {
			bring = ResourceUtil.getInstance().read("command_status_nothing");
		}
		textField_20.setText(bring);
		String emo = "";
		if (b.isAngry()) {
			emo += ResourceUtil.getInstance().read("command_status_anger");
		}
		if (b.isScare()) {
			emo += ResourceUtil.getInstance().read("command_status_scare");
		}
		if (b.getPainState() != Pain.NONE) {
			emo += ResourceUtil.getInstance().read("command_status_hurt");
		}
		if (b.isSad()) {
			emo += ResourceUtil.getInstance().read("command_status_sad");
		}
		if (b.isBlind()) {
			emo += ResourceUtil.getInstance().read("command_status_blind");
		}
		if (b.isbPurupuru()) {
			emo += ResourceUtil.getInstance().read("command_status_shake");
		}
		if (b.isDirty()) {
			emo += ResourceUtil.getInstance().read("command_status_dirty");
		}
		if (b.isExciting()) {
			emo += ResourceUtil.getInstance().read("command_status_hatsujou");
		}
		if (b.isHappy()) {
			emo += ResourceUtil.getInstance().read("command_status_pleasure");
		}
		if (b.isMelt()) {
			emo += ResourceUtil.getInstance().read("command_status_melt");
		}
		if (b.isOnlyAmaama()) {
			emo += ResourceUtil.getInstance().read("command_status_tastedestruct");
		}
		if (b.isRelax()) {
			emo += ResourceUtil.getInstance().read("command_status_doyukkuri");
		}
		if (b.isSilent()) {
			emo += ResourceUtil.getInstance().read("command_status_silent");
		}
		if (b.isSleepy()) {
			emo += ResourceUtil.getInstance().read("command_status_sleepy");
		}
		if (b.isStaying()) {
			emo += ResourceUtil.getInstance().read("command_status_wait");
		}
		if (b.isVain()) {
			emo += ResourceUtil.getInstance().read("command_status_vain");
		}
		if (b.isWet()) {
			emo += ResourceUtil.getInstance().read("command_status_wet");
		}
		if (b.isYunnyaa()) {
			emo += ResourceUtil.getInstance().read("command_status_yunyaa");
		}
		if (emo.length() == 0) {
			emo = ResourceUtil.getInstance().read("command_status_nostate");
		} else {
			emo = emo.substring(0, emo.length() - 2);
		}
		textField_21.setText(emo);
		String attach = "";
		if (b.getAttach() != null && b.getAttach().size() != 0) {
			for (Attachment a : b.getAttach()) {
				attach = attach += a.toString() + " / ";
			}
		}
		if (attach.length() == 0) {
			attach = ResourceUtil.getInstance().read("command_status_nothing");
		} else {
			attach = attach.substring(0, attach.length() - 2);
		}
		textField_22.setText(attach);

		textField_23.setText(String.valueOf(b.getNumOfAnts()));
		textField_24.setText(String.valueOf(b.getBlockedCount()));
		textField_25.setText(b.isLockmove() ? ResourceUtil.getInstance().read("command_status_cantmove") : ResourceUtil.getInstance().read("command_status_canmove"));
		textField_26.setText(String.valueOf(b.getAttitudePoint()));
	}

	public static void showError(String s) {
		JLabel label = new JLabel(s);
		label.setForeground(Color.RED);
		JOptionPane.showMessageDialog(ShowStatusFrame.getInstance(), label);
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
	 * @return インスタンス
	 */
	public static ShowStatusFrame getInstance() {
		return instance;
	}
}
