package src.system;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import src.SimYukkuri;

/**
 * 読み込み時に出てくるウィンドウのクラス
 */
public class LoadWindow extends JDialog {
	private static final long serialVersionUID = 7062407460609428689L;
	private JLabel loading;
	private JTextArea log;
	private JScrollPane logBar;
	/** コンストラクタ. */
	public LoadWindow(Frame frame) {
		super(frame, SimYukkuri.TITLE, Dialog.ModalityType.MODELESS);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		loading = new JLabel("Loading...");
		log = new JTextArea();
		log.setEditable(false);
		
		logBar = new JScrollPane(log);

		Container base = getContentPane();

		mainPanel.add(loading, BorderLayout.NORTH);
		mainPanel.add(logBar, BorderLayout.CENTER);
		
		base.add(mainPanel);
		
		setPreferredSize(new Dimension(400, 300));
		pack();

		setLocationRelativeTo(null);
	}
	/**
	 * 読み込みウィンドウに文字列を加える.
	 * @param str 加える文字列
	 */
	public void addLine(String str) {
		log.append(str + "\r\n");
	}
	public JLabel getLoading() {
		return loading;
	}
	public void setLoading(JLabel loading) {
		this.loading = loading;
	}
	public JTextArea getLog() {
		return log;
	}
	public void setLog(JTextArea log) {
		this.log = log;
	}
	public JScrollPane getLogBar() {
		return logBar;
	}
	public void setLogBar(JScrollPane logBar) {
		this.logBar = logBar;
	}

}