package app.old;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

class ConsoleOutputView extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7566712233624286020L;
	private JTextArea consoleTextField = new JTextArea(31, 57);
	public ConsoleOutputView() {
		consoleTextField.setFont(consoleTextField.getFont().deriveFont(10f));
//		consoleTextField.setRows(45);
//		consoleTextField.setColumns(45);
		consoleTextField.setWrapStyleWord(true);
		consoleTextField.setLineWrap(true);
		consoleTextField.setEditable(false);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.setBackground(Color.WHITE);

		jScrollPane1.setViewportView(consoleTextField);
		jScrollPane1.getViewport().setBackground(Color.WHITE);
//		jScrollPane1.setBounds(10,60,600,600);
		jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(jScrollPane1, BorderLayout.CENTER);
	}
	
}
