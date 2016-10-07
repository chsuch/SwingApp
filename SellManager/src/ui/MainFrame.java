package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import common.Constants;
import common.DatabaseManager;


public class MainFrame extends JFrame implements ActionListener{
	
	private JMenuBar mb;
	private JMenuItem miInput, miSearchHistory, miSearchCustomer;
	private InputOrderJPanel mInputOrderPanel;
	private SearchSellHistoryPanel mSellHistorySearchPanel;
	private SearchCustomerPanel mCustomerSearchPanel;
	private EditCustomerInfoPanel mEditCustomerInfoPanel;
	private EditSellInfoPanel mEditSellInfoPanel;
	private JDialog loading;
	private JButton mDefaultBtn;
	private JMenuItem miEditCustomerInfo;
	private JMenuItem miEditSellInfo;
	private JMenuItem miInitDB;
	private JMenuItem miExportExcel;
	
	public static void main(String[] args) {
		new MainFrame();
	}
	
	public MainFrame() {
		init();
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		setSize(900, 700);
		setVisible(true);
		setResizable(false);
		setLocation( (Constants.SCREEN_SIZE.width-getSize().width)/2, (Constants.SCREEN_SIZE.height-getSize().height)/2 );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void init() {
		loading = new JDialog(this);
	    JPanel p1 = new JPanel(new BorderLayout());
	    p1.add(new JLabel("Please wait..."), BorderLayout.CENTER);
	    loading.setUndecorated(true);
	    loading.getContentPane().add(p1);
	    loading.pack();
	    loading.setLocationRelativeTo(this);
	    loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    loading.setModal(true);
		
		mInputOrderPanel = new InputOrderJPanel(this);
		mSellHistorySearchPanel = new SearchSellHistoryPanel(this);
		mCustomerSearchPanel = new SearchCustomerPanel(this);
		mEditCustomerInfoPanel = new EditCustomerInfoPanel(this);
		mEditSellInfoPanel = new EditSellInfoPanel(this);
		
		mDefaultBtn = new JButton();
		mDefaultBtn.setVisible(false);
		
		mb = new JMenuBar();
		JMenu searchMenu = new JMenu("�˻�");
		searchMenu.add(miSearchHistory = new JMenuItem("���Գ��� �˻�"));
		searchMenu.add(miSearchCustomer = new JMenuItem("�� �˻�"));
		
		JMenu inputMenu = new JMenu("�Է�");
		inputMenu.add(miInput = new JMenuItem("���Գ��� �Է�"));
		
		JMenu editMenu = new JMenu("����");
		editMenu.add(miEditCustomerInfo = new JMenuItem("������ ����"));
		editMenu.add(miEditSellInfo = new JMenuItem("���ų��� ����"));
		editMenu.add(new JSeparator());
		editMenu.add(miInitDB = new JMenuItem("DB �ʱ�ȭ"));
		editMenu.add(miExportExcel = new JMenuItem("��������"));
		
		miInput.addActionListener(this);
		miSearchHistory.addActionListener(this);
		miSearchCustomer.addActionListener(this);
		miEditCustomerInfo.addActionListener(this);
		miEditSellInfo.addActionListener(this);
		miInitDB.addActionListener(this);
		miExportExcel.addActionListener(this);
		
		mb.add(searchMenu);
		mb.add(inputMenu);
		mb.add(editMenu);
		
		add(mSellHistorySearchPanel);
		add(mInputOrderPanel);
		add(mCustomerSearchPanel);
		add(mEditCustomerInfoPanel);
		add(mEditSellInfoPanel);
		
		add(mDefaultBtn);
		
		setJMenuBar(mb);
		
		setTitle(miSearchHistory.getText());
		mSellHistorySearchPanel.setVisible(true);
//		mEditSellInfoPanel.setVisible(true);
	}

	public void doWork(Runnable r){
		new SwingWorker<Void, Void>() {

			@Override
			protected void done() {
				loading.dispose();
			}

			@Override
			protected Void doInBackground() throws Exception {
				r.run();
				return null;
			}
		}.execute();
		loading.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==miInitDB){
			int selectedOption = JOptionPane.showConfirmDialog(this, "DB�� �ʱ�ȭ �Ͻðڽ��ϱ�?", null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(selectedOption == JOptionPane.OK_OPTION){
				try {
					DatabaseManager.initDatabase(true);
					JOptionPane.showMessageDialog(this, "DB�� �ʱ�ȭ �߽��ϴ�.\n���α׷��� ����� �� �ּ���.");
					System.exit(0);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}else if(obj==miExportExcel){
			int selectedOption = JOptionPane.showConfirmDialog(this, "DB�� ����� �����͸� Excel���Ϸ� �����մϴ�.\n�����Ͻðڽ��ϱ�?", null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(selectedOption == JOptionPane.OK_OPTION){
				doWork(new Runnable() {
					public void run() {
						makeExcel(getSearchedData());
					}
				});
//				new SwingWorker<Void, Void>() {
//
//					@Override
//					protected void done() {
//						loading.dispose();
//					}
//
//					@Override
//					protected Void doInBackground() throws Exception {
//						makeExcel(getSearchedData());
//						return null;
//					}
//				}.execute();
//				loading.setVisible(true);
			}
		}else{
			setTitle( ((JMenuItem)obj).getText() );
			mInputOrderPanel.setVisible(obj==miInput);
			mSellHistorySearchPanel.setVisible(obj==miSearchHistory);
			mCustomerSearchPanel.setVisible(obj==miSearchCustomer);
			mEditCustomerInfoPanel.setVisible(obj==miEditCustomerInfo);
			mEditSellInfoPanel.setVisible(obj==miEditSellInfo);
			
		}
	}
	
	private void makeExcel(ResultSet rs){
		File dir = new File("backup");
		if(!dir.exists()){
			dir.mkdir();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		int count = 0;
		try {
			FileOutputStream fileOut = new FileOutputStream(new File(dir, (String.format("�Ǹ��̷�_%s.xls", format.format(new Date(System.currentTimeMillis()))))));
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("�Ǹ��̷�");
			createHeader(workbook, worksheet.createRow(0));
			
			HSSFRow row = null;
			while(rs.next()){
				row = worksheet.createRow(count+1);
				for(int i=0; i<10; i++){
					row.createCell(i).setCellValue(rs.getString(i+1));
				}
				count++;
			}
			if(count==0){
				JOptionPane.showMessageDialog(this, "Excel������ ���� �� �����Ͱ� �����ϴ�.");
			}else{
				workbook.write(fileOut);
				fileOut.flush();
				fileOut.close();
				JOptionPane.showMessageDialog(this, "Excel������ �����߽��ϴ�.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "������� Excel������ �ݰ� �ٽ� ������ �ּ���.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}
	
	private void createHeader(HSSFWorkbook workbook, HSSFRow header) {
		HSSFCellStyle rowStyle = workbook.createCellStyle();
		rowStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		header.setRowStyle(rowStyle);
		
		header.createCell(0).setCellValue("����ȣ");
		header.createCell(1).setCellValue("����Ƚ��");
		header.createCell(2).setCellValue("������");
		header.createCell(3).setCellValue("�ֹ���");
		header.createCell(4).setCellValue("������");
		header.createCell(5).setCellValue("��ȭ");
		header.createCell(6).setCellValue("�ֹ�����");
		header.createCell(7).setCellValue("�ּ�");
		header.createCell(8).setCellValue("�����ȣ");
		header.createCell(9).setCellValue("���");
		
	}

	private ResultSet getSearchedData(){
		String query;
		ResultSet rs = null;
		try {
			query = "SELECT "
					+ "C. CUSTOMER_NO, B.SELL_COUNT,A.BUY_DATE,C.NAME,A.RECIPIENT,C.PHONE_NO,A.JUMUN,C.ADDRESS,C.POST_CODE,A.ETC "
					+ "FROM "
					+ "SELL_LIST A, CUSTOMER C, (SELECT COUNT(CUSTOMER_NO) AS SELL_COUNT,CUSTOMER_NO FROM SELL_LIST WHERE DEL_YN != 'Y' GROUP BY CUSTOMER_NO) B "
					+ "WHERE "
					+ "A.CUSTOMER_NO = B.CUSTOMER_NO AND A.CUSTOMER_NO = C.CUSTOMER_NO "
					+ "ORDER BY A.BUY_DATE";
			DatabaseManager.initDatabase(false);
			rs = DatabaseManager.excuteQueryNoParams(query);
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
}
