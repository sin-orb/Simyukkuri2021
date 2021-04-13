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


public class LoadWindow extends JDialog {
	
	private JLabel loading;
	private JTextArea log;
	private JScrollPane logBar;

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
	
	public void addLine(String str) {
		log.append(str + "\r\n");
	}

}