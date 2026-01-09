package src.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import src.base.Body;
import src.game.Stalk;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

public class DebugFrame extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = -8472477224379296555L;
	private static final String[] COLUMN_NAMES = {ResourceUtil.getInstance().read("command_debug_property_name"),
			ResourceUtil.getInstance().read("command_debug_value")};
	private JPanel contentPane;
	private JTextField textField;
	private JScrollPane scrollPane;
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;
	private JButton btnNewButton_4;
	private JButton btnNewButton_5;
	private JButton btnNewButton_6;
	private JButton btnNewButton_7;
	private JButton btnNewButton_8;
	/**
	 * Create the frame.
	 */
	public DebugFrame() {
		setTitle(ResourceUtil.getInstance().read("command_debug_title"));
		setBounds(100, 100, 788, 464);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel(ResourceUtil.getInstance().read("command_debug_class"));
		lblNewLabel.setBounds(12, 10, 128, 20);
		contentPane.add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(170, 12, 400, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 80, 724, 321);
		contentPane.add(scrollPane);
		
		btnNewButton_2 = new JButton("father");
		btnNewButton_2.setBounds(22, 40, 91, 21);
		contentPane.add(btnNewButton_2);
		
		btnNewButton_3 = new JButton("mother");
		btnNewButton_3.setBounds(128, 40, 91, 21);
		contentPane.add(btnNewButton_3);
		
		btnNewButton_4 = new JButton("Stalk_1");
		btnNewButton_4.setBounds(231, 40, 91, 21);
		contentPane.add(btnNewButton_4);
		
		btnNewButton_5 = new JButton("child_1");
		btnNewButton_5.setBounds(334, 41, 91, 21);
		contentPane.add(btnNewButton_5);
		
		btnNewButton_6 = new JButton("Under Construction");
		btnNewButton_6.setBounds(439, 41, 91, 21);
		contentPane.add(btnNewButton_6);
		
		btnNewButton_7 = new JButton("Under Construction");
		btnNewButton_7.setBounds(542, 41, 91, 21);
		contentPane.add(btnNewButton_7);
		
		btnNewButton_8 = new JButton("Under Construction");
		btnNewButton_8.setBounds(645, 41, 91, 21);
		contentPane.add(btnNewButton_8);
	}
	
	public void setObjAndDisplay(Object o) {
		textField.setText(o.getClass().getCanonicalName());
		
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (o instanceof Body) {
					Body father = YukkuriUtil.getBodyInstance(((Body)o).getFather());
					if (father != null) {
						DebugFrame df = new DebugFrame();
						df.setObjAndDisplay(father);
						df.setVisible(true);
					}
				}
			}
		});
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (o instanceof Body) {
					Body mother = YukkuriUtil.getBodyInstance(((Body)o).getMother());
					if (mother != null) {
						DebugFrame df = new DebugFrame();
						df.setObjAndDisplay(mother);
						df.setVisible(true);
					}
				}
			}
		});
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (o instanceof Body) {
					Stalk s = ((Body)o).getStalks().get(0);
					if (s != null) {
						DebugFrame df = new DebugFrame();
						df.setObjAndDisplay(s);
						df.setVisible(true);
					}
				}
			}
		});
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (o instanceof Body) {
					Body firstChild = YukkuriUtil.getBodyInstance(((Body)o).getChildrenList().get(0));
					if (firstChild != null) {
						DebugFrame df = new DebugFrame();
						df.setObjAndDisplay(firstChild);
						df.setVisible(true);
					}
				}
			}
		});
		
		DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0);

		// Mapをアルファベット順にソート（TreeMapを使用）
		Map<String, Object> sortedMap = new TreeMap<>(getAllFields(o));

		// Mapの内容をテーブルモデルに追加
		for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
			tableModel.addRow(new Object[]{entry.getKey(), describe(entry.getValue())});
		}

		// JTableを作成
		JTable table = new JTable(tableModel);
		scrollPane.setViewportView(table);

		contentPane.revalidate();
        contentPane.repaint();
	}
	
	private static String describe(Object obj) {
		if (obj == null) {
			return "null";
		}

		StringBuilder result = new StringBuilder();

		// 1. 配列またはリストの場合
		if (obj.getClass().isArray()) {
			result.append("[");
			int length = java.lang.reflect.Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object element = java.lang.reflect.Array.get(obj, i);
				result.append(describe(element));
				if (i < length - 1) {
					result.append(", ");
				}
			}
			result.append("]");
			return result.toString();
		} else if (obj instanceof List) {
			List<?> list = (List<?>) obj;
			result.append("[");
			for (int i = 0; i < list.size(); i++) {
				result.append(describe(list.get(i)));
				if (i < list.size() - 1) {
					result.append(", ");
				}
			}
			result.append("]");
			return result.toString();
		}

		// 2. マップの場合
		if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			result.append("{");
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				result.append("KeyName: ").append(entry.getKey()).append(", Value: ");
				result.append(describe(entry.getValue()));
				result.append("; ");
			}
			if (!map.isEmpty()) {
				result.setLength(result.length() - 2); // 最後の "; " を削除
			}
			result.append("}");
			return result.toString();
		}

		// 3. クラス独自のtoStringメソッドを持つか確認
		if (hasCustomToString(obj)) {
			return obj.toString();
		}

		// 4. フィールドを取得して文字列化
		result.append("{");
		Class<?> clazz = obj.getClass();
		Map<String, String> fieldsMap = new TreeMap<>(); // フィールド名をアルファベット順に

		while (clazz != null) { // 親クラスも遡る
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue; // staticフィールドは無視
				}
				field.setAccessible(true);
				try {
					Object value = field.get(obj);
					fieldsMap.put(field.getName(), describe(value));
				} catch (IllegalAccessException e) {
					fieldsMap.put(field.getName(), "アクセス不可");
				}
			}
			clazz = clazz.getSuperclass();
		}

		fieldsMap.forEach((fieldName, value) -> result.append("FieldName: ").append(fieldName)
				.append(", Value: ").append(describe(value)).append("; "));
		if (!fieldsMap.isEmpty()) {
			result.setLength(result.length() - 2); // 最後の "; " を削除
		}
		result.append("}");
		return result.toString();
	}

	// クラス独自のtoStringメソッドを持つかどうか確認
	private static boolean hasCustomToString(Object obj) {
		try {
			Class<?> clazz = obj.getClass();
			return clazz.getMethod("toString").getDeclaringClass() != Object.class;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static Map<String, Object> getAllFields(Object obj) {
		Map<String, Object> fieldMap = new HashMap<>();
		Class<?> currentClass = obj.getClass();
		try {
			// クラス階層を辿る
			while (currentClass != null) {
				// 現在のクラスのフィールドを取得
				Field[] fields = currentClass.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true); // privateフィールドにもアクセスできるようにする
					fieldMap.put(field.getName(), field.get(obj));
				}
				// 親クラスへ移動
				currentClass = currentClass.getSuperclass();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fieldMap;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
