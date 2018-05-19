package app.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import app.model.transaction.Transaction;
import app.start.util.GO;
import app.util.StringUtil;

public class TransactionsOverView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6905126641973068625L;
	public JButton refresh = new JButton("Refresh");
	private SimpleDateFormat smd = new SimpleDateFormat("dd.MM.YYYY HH:mm:SS");
	public TransactionsOverView() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.setBackground(Color.WHITE);
		JTable jTable1 = new javax.swing.JTable();
		jTable1.setBackground(Color.WHITE);

		jScrollPane1.setViewportView(jTable1);
		jScrollPane1.getViewport().setBackground(Color.WHITE);
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(GO.clientWalletLocal!= null){
					jTable1.setModel(new FinalTableModel(GO.clientWalletLocal.getTransactionHistory()));
				}
			}
		});
		add(refresh, BorderLayout.NORTH);
		add(jScrollPane1, BorderLayout.CENTER);
	}

	private class FinalTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -884388668168390337L;
		private LinkedList<Transaction> li = new LinkedList<Transaction>();
		private String[] columnNames = { "Date","TX-ID", "Sender", "Receiver", "Amount" };

		private FinalTableModel(LinkedList<Transaction> list) {
			this.li = list;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public int getRowCount() {
			return li.size();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Transaction si = li.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return smd.format(si.date);
			case 1:
				return si.transactionId;
			case 2:
				return StringUtil.getStringFromKey(si.sender);
			case 3:
				return StringUtil.getStringFromKey(si.reciepient);
			case 4:
				return si.value;
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return String.class;
			case 4:
				return Double.class;
			case 5:
				return Double.class;
			case 6:
				return String.class;
			}
			return null;
		}
	}
}
