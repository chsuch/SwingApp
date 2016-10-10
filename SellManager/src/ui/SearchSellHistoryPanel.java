package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.sun.corba.se.spi.ior.MakeImmutable;

import common.DatabaseManager;


public class SearchSellHistoryPanel extends JPanel implements ActionListener{
	
	private MainFrame mMainFrame;
	private JButton btnSearch;
	private JTextField tfSearchWord;
	private JComboBox cbSearch;
	private JTable mTable;
	
	public SearchSellHistoryPanel(MainFrame mainFrame) {
		mMainFrame = mainFrame;
		init();
		
		setLayout(new BorderLayout());
		setVisible(false);
		
		JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.add(cbSearch);
		top.add(tfSearchWord);
		top.add(btnSearch);
		
		add(top, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(mTable);
		scroll.setPreferredSize(new Dimension(800, 550));
		add(scroll, BorderLayout.CENTER);
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		if(aFlag){
			tfSearchWord.requestFocus();
		}
	};
	
	private void init() {
		btnSearch = new JButton("�˻�");
		btnSearch.setFocusable(false);
		
		tfSearchWord = new JTextField(10);
		tfSearchWord.setToolTipText("�˻���");
		tfSearchWord.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					btnSearch.doClick();
				}
			}
		});
		
		cbSearch = new JComboBox(new String[]{"--�˻�����--","���԰Ǽ�","����","��ȭ��ȣ"});
		cbSearch.setFocusable(false);
		
		Vector<String> header = new Vector<String>();
		header.addElement("ID");
		header.addElement("������");
		header.addElement("����");
		header.addElement("������");
		header.addElement("��ȭ");
		header.addElement("�ֹ�����");
		header.addElement("�ּ�");
		header.addElement("�����ȣ");
		header.addElement("���");
		
		DefaultTableModel model = new DefaultTableModel(header, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		mTable = new JTable(model);
		mTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		mTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		mTable.getColumnModel().getColumn(2).setPreferredWidth(50);
		mTable.getColumnModel().getColumn(3).setPreferredWidth(50);
		mTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		mTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		mTable.getColumnModel().getColumn(6).setPreferredWidth(200);
		mTable.getColumnModel().getColumn(7).setPreferredWidth(60);
		mTable.getColumnModel().getColumn(8).setPreferredWidth(70);
		
		mTable.setFocusable(false);
		
		btnSearch.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if(obj == btnSearch){
			if(cbSearch.getSelectedIndex()==0){
				JOptionPane.showMessageDialog(this, "�˻� ������ �����ϼ���.");
				return;
			}else if(cbSearch.getSelectedIndex()==1){
				try {
					Integer.parseInt(tfSearchWord.getText().trim());
					setDataList(getSearchedData(cbSearch.getSelectedIndex()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(this, "�˻���� ���ڷ� �Է��ϼ���.");
				}
			}else if(tfSearchWord.getText().trim().equals("")){
				JOptionPane.showMessageDialog(this, "�˻�� �Է��ϼ���.");
				return;
			}else{//TABLE_SELL_LIST
				setDataList(getSearchedData(cbSearch.getSelectedIndex()));
			}
		}
	}

	private ResultSet getSearchedData(int selectedIndex){
		String query;
		ResultSet rs = null;
		try {
			if(selectedIndex == 1){//���԰Ǽ�
				query = "SELECT "
						+ "A.BUY_DATE,C.NAME,A.RECIPIENT,C.PHONE_NO,A.JUMUN,C.ADDRESS,C.POST_CODE,A.ETC,C.CUSTOMER_NO "
						+ "FROM "
						+ "SELL_LIST A, CUSTOMER C, (SELECT COUNT(CUSTOMER_NO) AS SELL_COUNT,CUSTOMER_NO FROM SELL_LIST WHERE DEL_YN != 'Y' GROUP BY CUSTOMER_NO) B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.CUSTOMER_NO = C.CUSTOMER_NO AND SELL_COUNT >= ? "
						+ "ORDER BY C.CUSTOMER_NO";
			}else if(selectedIndex == 2){//����
				query = "SELECT "
						+ "A.BUY_DATE,B.NAME,A.RECIPIENT,B.PHONE_NO,A.JUMUN,B.ADDRESS,B.POST_CODE,A.ETC,B.CUSTOMER_NO "
						+ "FROM SELL_LIST A, CUSTOMER B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.DEL_YN != 'Y' AND B.NAME LIKE ? "
						+ "ORDER BY B.CUSTOMER_NO";
			}else{//��ȭ��ȣ
				query = "SELECT "
						+ "A.BUY_DATE,B.NAME,A.RECIPIENT,B.PHONE_NO,A.JUMUN,B.ADDRESS,B.POST_CODE,A.ETC,B.CUSTOMER_NO "
						+ "FROM SELL_LIST A, CUSTOMER B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.DEL_YN != 'Y' AND B.PHONE_NO LIKE ? "
						+ "ORDER BY B.CUSTOMER_NO";
			}
			DatabaseManager.initDatabase(false);
			String searchWord = tfSearchWord.getText().trim();
			rs = DatabaseManager.excuteQuery(query, new String[]{selectedIndex == 1 ? searchWord : "%"+searchWord+"%"});
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				DatabaseManager.releaseConnection(DatabaseManager.getConnection());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rs;
	}
	
	
	private void setDataList(ResultSet rs){
		mMainFrame.doWork(new Runnable() {
			
			@Override
			public void run() {
				while(mTable.getRowCount() > 0){
					((DefaultTableModel)mTable.getModel()).removeRow(0);
				}
				int count = 0;
				try {
					while(rs.next()){
						count++;
						((DefaultTableModel)mTable.getModel()).addRow(new String[]
							{
								rs.getString(9),
								rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)
							}
						);
					}
					if(count == 0){
						JOptionPane.showMessageDialog(SearchSellHistoryPanel.this, "�˻��� �����Ͱ� �����ϴ�.");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		});
	}
	
	public void onClickSearchBtn(){
		btnSearch.doClick();
	}
}
