package ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import common.DatabaseManager;


public class EditCustomerInfoPanel extends JPanel implements ActionListener{
	
	private MainFrame mMainFrame;
	private JButton btnSearch, btnModify;
	private JTextField tfSearchWord;
	private JComboBox cbSearch;
	private JTable mTable;
	
	public EditCustomerInfoPanel(MainFrame mainFrame) {
		mMainFrame = mainFrame;
		init();
		
		setLayout(new BorderLayout());
		setVisible(false);
		
		JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		top.add(cbSearch);
		top.add(tfSearchWord);
		top.add(btnSearch);
		top.add(btnModify);
		
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
		
		btnModify = new JButton("����");
		btnModify.setFocusable(false);
		
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
		header.addElement("����ȣ");
		header.addElement("����Ƚ��");
		header.addElement("����");
		header.addElement("��ȭ��ȣ");
		header.addElement("�ּ�");
		header.addElement("�����ȣ");
		header.addElement("�������");
		
		DefaultTableModel model = new DefaultTableModel(header, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column==2 ||column==3 || column==4 || column==5);
			}
		};

			
		mTable = new JTable(model){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {   
                Component result = super.prepareRenderer(renderer, row, column);   
                if (result instanceof JComponent) {  
                	((JComponent) result).setAlignmentY(CENTER_ALIGNMENT);
                    if(column==2 ||column==3 || column==4 || column==5){
                    	((JComponent) result).setBorder(new MatteBorder(1, 1, 1, 1, Color.MAGENTA));  
                    }
                }   
                return result;   
            }    
		};
		mTable.setFocusable(false);
		
		mTable.getColumnModel().getColumn(0).setPreferredWidth(70);//����ȣ
		mTable.getColumnModel().getColumn(1).setPreferredWidth(70);//����Ƚ��
		mTable.getColumnModel().getColumn(2).setPreferredWidth(80);//����
		mTable.getColumnModel().getColumn(3).setPreferredWidth(100);//��ȭ��ȣ
		mTable.getColumnModel().getColumn(4).setPreferredWidth(250);//�ּ�
		mTable.getColumnModel().getColumn(5).setPreferredWidth(80);//�����ȣ
		mTable.getColumnModel().getColumn(6).setPreferredWidth(150);//�������
		
		btnSearch.addActionListener(this);
		btnModify.addActionListener(this);
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
		}else if(obj == btnModify){
			if(mTable.getRowCount() == 0){
				JOptionPane.showMessageDialog(this, "������Ʈ �� �׸��� �����ϴ�.");
				return;
			}
			int selectedOption = JOptionPane.showConfirmDialog(this, "���� �� ������ ���� �Ͻðڽ��ϱ�?", null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(selectedOption == JOptionPane.OK_OPTION){
				saveModifiedDatas();
			}
		}
	}

	private void saveModifiedDatas(){
		mMainFrame.doWork(new Runnable() {
			
			@Override
			public void run() {
				String query = 
						"UPDATE CUSTOMER "
					+ 	"SET "
					+ 	"NAME = ?, "
					+ 	"PHONE_NO = ?, "
					+ 	"ADDRESS = ?, "
					+ 	"POST_CODE = ? "
					+ 	"WHERE "
					+ 	"CUSTOMER_NO = ?";
			try {
				DatabaseManager.initDatabase(false);
				DatabaseManager.beginTransaction();
				DatabaseManager.excuteUpdate(query, getUpdateDatas());
				DatabaseManager.commitTransaction();
				successUpdateDatas();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(EditCustomerInfoPanel.this, String.format("�Է� �� ������ �߻��߽��ϴ�.[%s]", e1.getMessage()));
				e1.printStackTrace();
				try {
					DatabaseManager.rollbackTransaction();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			} finally {
				try {
					DatabaseManager.releaseConnection(DatabaseManager.getConnection());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			}
		});
	}
	
	private ArrayList<ArrayList<String>> getUpdateDatas() {
		ArrayList<ArrayList<String>> datas = new ArrayList<ArrayList<String>>();
		ArrayList<String> rowData = null;
		for(int i=0; i<mTable.getRowCount(); i++){
			rowData = new ArrayList<String>();
			rowData.add(mTable.getValueAt(i, 2).toString());
			rowData.add(mTable.getValueAt(i, 3).toString());
			rowData.add(mTable.getValueAt(i, 4).toString());
			rowData.add(mTable.getValueAt(i, 5).toString());
			rowData.add(mTable.getValueAt(i, 0).toString());
			
			datas.add(rowData);
		}
		return datas;
	}
	
	private void successUpdateDatas() {
		mTable.clearSelection();
		JOptionPane.showMessageDialog(this, "�����͸� ���������� �����߽��ϴ�.");
		
		//������ ������ ����
		while(mTable.getRowCount() > 0){
			((DefaultTableModel)mTable.getModel()).removeRow(0);
		}
	}
	
	private ResultSet getSearchedData(int selectedIndex){
		String query;
		ResultSet rs = null;
		try {
			if(selectedIndex == 1){//���԰Ǽ�
				query = "SELECT "
						+ "A.CUSTOMER_NO, B.SELL_COUNT, A.NAME, A.PHONE_NO, A.ADDRESS, A.POST_CODE, A.UPDATE_TIME "
						+ "FROM "
						+ "CUSTOMER A, (SELECT COUNT(CUSTOMER_NO) AS SELL_COUNT,CUSTOMER_NO FROM SELL_LIST WHERE DEL_YN != 'Y' GROUP BY CUSTOMER_NO) B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.DEL_YN != 'Y' AND B.SELL_COUNT > ? "
						+ "ORDER BY A.CUSTOMER_NO";
			}else if(selectedIndex == 2){//����
				query = "SELECT "
						+ "A.CUSTOMER_NO, B.SELL_COUNT, A.NAME, A.PHONE_NO, A.ADDRESS, A.POST_CODE, A.UPDATE_TIME "
						+ "FROM "
						+ "CUSTOMER A, (SELECT COUNT(CUSTOMER_NO) AS SELL_COUNT,CUSTOMER_NO FROM SELL_LIST WHERE DEL_YN != 'Y' GROUP BY CUSTOMER_NO) B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.DEL_YN != 'Y' AND A.NAME LIKE ? "
						+ "ORDER BY A.CUSTOMER_NO";
			}else{//��ȭ��ȣ
				query = "SELECT "
						+ "A.CUSTOMER_NO, B.SELL_COUNT, A.NAME, A.PHONE_NO, A.ADDRESS, A.POST_CODE, A.UPDATE_TIME "
						+ "FROM "
						+ "CUSTOMER A, (SELECT COUNT(CUSTOMER_NO) AS SELL_COUNT,CUSTOMER_NO FROM SELL_LIST WHERE DEL_YN != 'Y' GROUP BY CUSTOMER_NO) B "
						+ "WHERE "
						+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.DEL_YN != 'Y' AND A.PHONE_NO LIKE ? "
						+ "ORDER BY A.CUSTOMER_NO";
			}
			DatabaseManager.initDatabase(false);
			rs = DatabaseManager.excuteQuery(query, new String[]{tfSearchWord.getText().trim()});
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
								rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)
							}
						);
					}
					if(count == 0){
						JOptionPane.showMessageDialog(EditCustomerInfoPanel.this, "�˻��� �����Ͱ� �����ϴ�.");
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
