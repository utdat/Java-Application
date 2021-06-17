package UngTienDat_18120314;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;


public class BTTH1 extends JFrame {

	private Connection conn;
	
	private JTabbedPane tabbedPane;
	
	private JPanel contentPane;
	private JPanel UpdatePanel;
	private JPanel InfoPanel;
	private JPanel NotiPanel;
	private JPanel SalaryPanel;
	private JPanel FindNamePanel;
	private JPanel GenderPanel;
	private JPanel FindPanel;
	
	private JScrollPane DeScrollPane;
	private JScrollPane EmScrollPane;
	private JTable SalaryTable;
	private JTable EmTable;
	private DefaultTableModel SalaryModel;
	private DefaultTableModel EmployeeModel;
	
	private JTextField IDEmployeeText;
	private JTextField RateText;
	
	private JRadioButton Biweekly;
	private JRadioButton Monthly;
	private JRadioButton Male;
	private JRadioButton Female;
	private ButtonGroup GroupPay;
	private ButtonGroup GroupGender;
	
	private JLabel EmployeeIDLabel;
	private JLabel DateOfChangeLabel;
	private JLabel RateLabel;
	private JLabel PayFrequencyLabel;
	private JLabel ShiftNameLabel;
	private JLabel DeNameLabel;
	private JLabel GenderLabel;
	private JLabel UpdateTitle;
	private JLabel SalaryTitle;
	private JLabel FindTitle;
	private JLabel UpdateMessageLabel;
	private JLabel FindMessageLabel;
	
	private JComboBox DeComboBox;
	private JComboBox ShiftComboBox;
	
	private JDateChooser DateChooser;
	
	private JButton UpdateSalaryButton;
	private JButton ResetButton;
	private JButton FindButton;
	private JButton UpdateButton;
	
	private Vector DepartmentName;
	private List<Integer> DepartmentID ;
	private Vector ShiftName;
	private List<Integer> ShiftID;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BTTH1 frame = new BTTH1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BTTH1() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 621, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------------------------------
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		tabbedPane.setBounds(10, 10, 592, 428);
		contentPane.add(tabbedPane);
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------------------------------
		
		FindPanel = new JPanel();
		
		//tạo sự kiện click chuột cho FindPanel, khi click chuôt vào vùng trống thì bỏ dòng vừa chọn và ẩn button Update Salary
		FindPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UpdateButton.setEnabled(false);
				IDEmployeeText.setText("");
				EmTable.clearSelection();
				DeComboBox.setSelectedIndex(0);
				ShiftComboBox.setSelectedIndex(0);
				GroupGender.clearSelection();
			}
		});
		
		tabbedPane.addTab("Find Employee", null, FindPanel, null);
		FindPanel.setLayout(null);
		
		GenderPanel = new JPanel();
		GenderPanel.setBackground(new Color(152, 251, 152));
		GenderPanel.setBounds(474, 32, 113, 140);
		FindPanel.add(GenderPanel);
		GenderPanel.setLayout(null);
		
		GenderLabel = new JLabel("Gender");
		GenderLabel.setBounds(10, 0, 64, 18);
		GenderPanel.add(GenderLabel);
		GenderLabel.setLabelFor(GenderPanel);
		GenderLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GenderLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		Male = new JRadioButton("Male");
		Male.setBackground(new Color(255, 250, 205));
		Male.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		Male.setBounds(10, 49, 75, 25);
		GenderPanel.add(Male);
		
		Female = new JRadioButton("Female");
		Female.setBackground(new Color(255, 250, 205));
		Female.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		Female.setBounds(10, 96, 91, 25);
		GenderPanel.add(Female);
		
		GroupGender = new ButtonGroup();
		GroupGender.add(Male);
		GroupGender.add(Female);
		
		FindNamePanel = new JPanel();
		FindNamePanel.setBackground(new Color(175, 238, 238));
		FindNamePanel.setBounds(25, 70, 430, 102);
		FindPanel.add(FindNamePanel);
		FindNamePanel.setLayout(null);
		
		DeNameLabel = new JLabel("Department Name");
		DeNameLabel.setBounds(20, 10, 154, 31);
		FindNamePanel.add(DeNameLabel);
		DeNameLabel.setForeground(Color.BLACK);
		DeNameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		DeNameLabel.setBackground(SystemColor.menu);
		
		ShiftNameLabel = new JLabel("Shift Name");
		ShiftNameLabel.setBounds(20, 57, 114, 31);
		FindNamePanel.add(ShiftNameLabel);
		ShiftNameLabel.setForeground(Color.BLACK);
		ShiftNameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		ShiftNameLabel.setBackground(SystemColor.menu);
		
		DeComboBox = new JComboBox();
		DeComboBox.setBounds(182, 12, 219, 25);
		FindNamePanel.add(DeComboBox);
		DeComboBox.setEditable(true);
		DeComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		ShiftComboBox = new JComboBox();
		ShiftComboBox.setBounds(182, 59, 114, 25);
		FindNamePanel.add(ShiftComboBox);
		ShiftComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		ShiftComboBox.setEditable(true);
		
		FindTitle = new JLabel("Find Employee");
		FindTitle.setHorizontalAlignment(SwingConstants.CENTER);
		FindTitle.setForeground(Color.BLUE);
		FindTitle.setFont(new Font("Times New Roman", Font.BOLD, 30));
		FindTitle.setBackground(Color.CYAN);
		FindTitle.setBounds(5, 5, 582, 60);
		FindPanel.add(FindTitle);
		
		FindButton = new JButton("Find");
		
		//tạo sự kiện cho button Find, khi click chuột vào button thì sẽ tìm kiếm employee theo các condition đã chọn
		FindButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				String department_name = DeComboBox.getSelectedItem().toString();
				String shift_name = ShiftComboBox.getSelectedItem().toString();
				String gender = "";
				
				if (Male.isSelected()) {
					gender = "M";
				}
				else if (Female.isSelected()) {
					gender = "F";
				}
				
				if(!LoadEmployeeTable(DepartmentID.get(DeComboBox.getSelectedIndex()), ShiftID.get(ShiftComboBox.getSelectedIndex()), gender)) {
					return;
				}
			}
		});
		
		FindButton.setForeground(Color.BLACK);
		FindButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		FindButton.setBounds(311, 182, 119, 33);
		FindPanel.add(FindButton);
		
		EmScrollPane = new JScrollPane();
		EmScrollPane.setBounds(5, 257, 572, 140);
		FindPanel.add(EmScrollPane);
		
		EmTable = new JTable();
		
		//tạo sự kiện cho bảng kết quả employee tìm thấy, chọn 1 dòng dữ liệu, lưu lại Employee ID và button Update Salary sẽ hiện lên
		EmTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UpdateButton.setEnabled(true);
				int i = EmTable.getSelectedRow();
				IDEmployeeText.setText(EmployeeModel.getValueAt(i, 0).toString());
			}
		});
		EmTable.setBackground(new Color(255, 250, 205));
		EmTable.setModel(EmployeeModel = new DefaultTableModel(
																  new Object[][] {},
																  new String[] {"Employee ID", "Job Title", "Birth Date", "Hire Date", "Current Flag"}
															  ) {
																	Class[] columnTypes = new Class[] {
																		Integer.class, String.class, String.class, String.class, Integer.class
																	};
																	public Class getColumnClass(int columnIndex) {
																		return columnTypes[columnIndex];
																	}
																});
		
		EmTable.getColumnModel().getColumn(0).setPreferredWidth(71);
		EmTable.getColumnModel().getColumn(1).setPreferredWidth(199);
		EmTable.getColumnModel().getColumn(2).setPreferredWidth(79);
		EmTable.getColumnModel().getColumn(3).setPreferredWidth(78);
		EmTable.getColumnModel().getColumn(4).setPreferredWidth(71);
		EmTable.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		EmScrollPane.setViewportView(EmTable);
		
		FindMessageLabel = new JLabel("");
		FindMessageLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		FindMessageLabel.setBounds(24, 214, 525, 38);
		FindMessageLabel.setForeground(Color.RED);
		FindPanel.add(FindMessageLabel);
		
		UpdateButton = new JButton("Update Salary");
		
		//tạo sự kiện cho button Update Salary, khi đã chọn 1 dòng dữ liệu và click vào button sẽ chuyển sang tap Update Salary
		UpdateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane.setSelectedIndex(1);
			}
		});
		UpdateButton.setEnabled(false);
		UpdateButton.setForeground(Color.BLACK);
		UpdateButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		UpdateButton.setBounds(127, 182, 158, 33);
		FindPanel.add(UpdateButton);

		DepartmentName = new Vector();
		DepartmentName.add("");
		DepartmentID = new ArrayList<Integer>();
		DepartmentID.add(0);
		
		
		ShiftName = new Vector();
		ShiftName.add("");
		ShiftID = new ArrayList<Integer>();
		ShiftID.add(0);
		LoadComboBox();
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------------------------------
		
		UpdatePanel = new JPanel();
		tabbedPane.addTab("Update Salary", null, UpdatePanel, null);
		UpdatePanel.setLayout(null);
		
		InfoPanel = new JPanel();
		InfoPanel.setBackground(new Color(175, 238, 238));
		InfoPanel.setBounds(25, 71, 541, 197);
		UpdatePanel.add(InfoPanel);
		InfoPanel.setLayout(null);
		
		IDEmployeeText = new JTextField();
		IDEmployeeText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		IDEmployeeText.setForeground(new Color(0, 0, 0));
		IDEmployeeText.setBounds(166, 30, 127, 25);
		InfoPanel.add(IDEmployeeText);
		IDEmployeeText.setColumns(10);
		
		RateText = new JTextField();
		RateText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		RateText.setForeground(new Color(0, 0, 0));
		RateText.setBounds(382, 30, 127, 25);
		InfoPanel.add(RateText);
		RateText.setColumns(10);
		
		Biweekly = new JRadioButton("Bi-Weekly");
		Biweekly.setBackground(new Color(255, 250, 205));
		Biweekly.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		Biweekly.setForeground(new Color(0, 0, 0));
		Biweekly.setBounds(166, 151, 118, 25);
		InfoPanel.add(Biweekly);
		
		Monthly = new JRadioButton("Monthly");
		Monthly.setBackground(new Color(255, 250, 205));
		Monthly.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		Monthly.setForeground(new Color(0, 0, 0));
		Monthly.setBounds(328, 151, 103, 25);
		InfoPanel.add(Monthly);
		
		GroupPay = new ButtonGroup();
		GroupPay.add(Biweekly);
		GroupPay.add(Monthly);
		
		EmployeeIDLabel = new JLabel("Employee ID");
		EmployeeIDLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		EmployeeIDLabel.setForeground(new Color(0, 0, 0));
		EmployeeIDLabel.setBounds(21, 28, 113, 31);
		InfoPanel.add(EmployeeIDLabel);
		
		DateOfChangeLabel = new JLabel("Date Of Change");
		DateOfChangeLabel.setBackground(new Color(240, 240, 240));
		DateOfChangeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		DateOfChangeLabel.setForeground(new Color(0, 0, 0));
		DateOfChangeLabel.setBounds(21, 89, 135, 31);
		InfoPanel.add(DateOfChangeLabel);
		
		RateLabel = new JLabel("Rate");
		RateLabel.setForeground(new Color(0, 0, 0));
		RateLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		RateLabel.setBounds(328, 30, 51, 31);
		InfoPanel.add(RateLabel);
		
		PayFrequencyLabel = new JLabel("Pay Frequency");
		PayFrequencyLabel.setBackground(new Color(240, 240, 240));
		PayFrequencyLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		PayFrequencyLabel.setForeground(new Color(0, 0, 0));
		PayFrequencyLabel.setBounds(21, 148, 125, 31);
		InfoPanel.add(PayFrequencyLabel);
		
		DateChooser = new JDateChooser();
		DateChooser.getSpinner().setFont(new Font("Times New Roman", Font.PLAIN, 20));
		DateChooser.setBounds(166, 89, 195, 25);
		InfoPanel.add(DateChooser);
		
		UpdateTitle = new JLabel("Update Salary");
		UpdateTitle.setBackground(Color.CYAN);
		UpdateTitle.setForeground(Color.BLUE);
		UpdateTitle.setBounds(5, 5, 574, 68);
		UpdateTitle.setHorizontalAlignment(SwingConstants.CENTER);
		UpdateTitle.setFont(new Font("Times New Roman", Font.BOLD, 30));
		UpdatePanel.add(UpdateTitle);
		
		NotiPanel = new JPanel();
		NotiPanel.setBackground(SystemColor.control);
		NotiPanel.setBounds(25, 265, 541, 122);
		UpdatePanel.add(NotiPanel);
		NotiPanel.setLayout(null);
		
		UpdateSalaryButton = new JButton("Update");
		
		//tạo sự kiện cho button Update, khi click vào button sẽ cập nhật lại Rate mới cho Employee và bảng tổng lương mỗi phòng ban ở tap Salary Payment
			UpdateSalaryButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
						
					int id = 0;
					float rate = 0;
					Date temp_change = new Date();
					Calendar temp_mod = Calendar.getInstance();
					int pay_freq = 0;
					String date_change = "";
					UpdateMessageLabel.setForeground(Color.RED);
					UpdateMessageLabel.setText("");
						
					if(IDEmployeeText.getText().equalsIgnoreCase("")) {
						UpdateMessageLabel.setText("Please enter Employee ID!");
						IDEmployeeText.requestFocus();
						return;
					}
					if(RateText.getText().equalsIgnoreCase("")) {
						UpdateMessageLabel.setText("Please enter Rate!");
						RateText.requestFocus();
						return;
					}
					if (Biweekly.isSelected()) {
						pay_freq = 2;
					}
					else if (Monthly.isSelected()) {
						pay_freq = 1;
					}
					else {
						UpdateMessageLabel.setText("Please choose Pay Frequency!");
						PayFrequencyLabel.requestFocus();
						return;
					}
						
					try {
						id = Integer.parseInt(IDEmployeeText.getText());
						rate = Float.parseFloat(RateText.getText());
						if(rate < 6.5 || rate > 200) {
							UpdateMessageLabel.setText("Error! 6.5 <= Rate <= 200");
							RateText.requestFocus();
							return;
						}
						temp_change = (Date) DateChooser.getDate();
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
						date_change = sf.format(temp_change);
					} catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
						
					if(!SaveTable(id, date_change, rate, pay_freq))
						return;
						
					LoadDeSalaryTable();
					SalaryTable.setModel(SalaryModel);
					}
			});
		
		UpdateSalaryButton.setBounds(157, 51, 103, 33);
		UpdateSalaryButton.setForeground(Color.BLACK);
		UpdateSalaryButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		NotiPanel.add(UpdateSalaryButton);
		
		ResetButton = new JButton("Reset");
		
		//tạo sự kiện cho button Reset, khi click chuột vào button sẽ xóa hết các dữ liệu đã nhập và chọn
		ResetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UpdateMessageLabel.setForeground(Color.BLACK);
				UpdateMessageLabel.setText("Please fill in all information");
				IDEmployeeText.setText("");
				RateText.setText("");
				GroupPay.clearSelection();
			}
		});
		ResetButton.setForeground(Color.BLACK);
		ResetButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		ResetButton.setBounds(296, 50, 103, 35);
		NotiPanel.add(ResetButton);
		
		UpdateMessageLabel = new JLabel("");
		UpdateMessageLabel.setVerticalAlignment(SwingConstants.TOP);
		UpdateMessageLabel.setBounds(20, 3, 495, 38);
		NotiPanel.add(UpdateMessageLabel);
		UpdateMessageLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		//--------------------------------------------------------------------------------------------------------------------------------------
		//--------------------------------------------------------------------------------------------------------------------------------------
		
		SalaryPanel = new JPanel();
		tabbedPane.addTab("Salary Payment", null, SalaryPanel, null);
		SalaryPanel.setLayout(null);
		
		DeScrollPane = new JScrollPane();
		DeScrollPane.setBounds(10, 78, 569, 277);
		SalaryPanel.add(DeScrollPane);
		
		SalaryTable = new JTable();
		SalaryTable.setEnabled(false);
		SalaryTable.setBackground(new Color(255, 250, 205));
		SalaryTable.setToolTipText("");
		SalaryTable.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		SalaryTable.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		DeScrollPane.setViewportView(SalaryTable);
		SalaryTable.setModel(SalaryModel = new DefaultTableModel(
													  new Object[][] {},
													  new String[] {"ID", "Name", "Group Name", "Total Salary"}
													) {
															Class[] columnTypes = new Class[] {
																Integer.class, String.class, String.class, Float.class
															};
															public Class getColumnClass(int columnIndex) {
																return columnTypes[columnIndex];
															}
													   });
		
		SalaryTitle = new JLabel("Department Salary Payment Sheet");
		SalaryTitle.setBounds(0, 21, 587, 36);
		SalaryTitle.setForeground(Color.BLUE);
		SalaryTitle.setHorizontalAlignment(SwingConstants.CENTER);
		SalaryTitle.setFont(new Font("Times New Roman", Font.BOLD, 30));
		SalaryPanel.add(SalaryTitle);
		SalaryTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		SalaryTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		SalaryTable.getColumnModel().getColumn(2).setPreferredWidth(240);
		SalaryTable.getColumnModel().getColumn(3).setPreferredWidth(95);
		
		LoadDeSalaryTable();
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------------------------------------------------------------
	
	public void LoadComboBox() {
		try {
			conn = new SQLServerConnUtils().getSQLServerConnection();
			Object []rows = new Object[4];
			
			String sql = "SELECT Name, DepartmentID FROM Department";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				DepartmentName.add(rs.getString(1));
				DepartmentID.add(rs.getInt(2));
			}
			
			sql = "SELECT Name, ShiftID FROM Shift";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				ShiftName.add(rs.getString(1));
				ShiftID.add(rs.getInt(2));
			}
			
			DeComboBox.setModel(new DefaultComboBoxModel(DepartmentName));
			ShiftComboBox.setModel(new DefaultComboBoxModel(ShiftName));
			
			ps.close();
			rs.close();
			conn.close();
			
		} catch (SQLException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public boolean SaveTable(int id, String date_change, float rate, int pay_freq) {
		try {
			conn = new SQLServerConnUtils().getSQLServerConnection();
			String sql = "SELECT * FROM Employee WHERE BusinessEntityID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				UpdateMessageLabel.setText("Employee ID does not exist!");
				IDEmployeeText.requestFocus();
				rs.close();
				ps.close();
				return false;
			}
			
			//kiểm tra điều kiện khóa chính
			sql = "SELECT * FROM EmployeePayHistory WHERE BusinessEntityID = ? AND RateChangeDate = ?";
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1, id);
			ps.setString(2, date_change);
			
			rs = ps.executeQuery();
			if(rs.next()) {
				UpdateMessageLabel.setText("Rate change history exists!");
				IDEmployeeText.requestFocus();
				rs.close();
				ps.close();
				return false;
			}
			
			 //insert dữ liệu vào bảng EmployeePayHistory
			sql = "EXEC UpdateEmployeeSalary @_IDEm = ?, @_Rate = ?, @_ChangeDate = ?, @_PayFreq = ?;";
			ps = conn.prepareStatement(sql);
			
			ps.setInt(1, id);
			ps.setFloat(2, rate);
			ps.setString(3, date_change);
			ps.setInt(4, pay_freq);
			
			int result = ps.executeUpdate();
			
			if(result == 0) {
				JOptionPane.showMessageDialog(getParent(), "INSERT failed");
			} else {
				JOptionPane.showMessageDialog(getParent(), "INSERT successed");
			}

			ps.close();
			
			IDEmployeeText.setText("");
			RateText.setText("");
			GroupPay.clearSelection();
		} catch (SQLException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		return true;
	}
	
	public void LoadDeSalaryTable() {
		try {
			conn = new SQLServerConnUtils().getSQLServerConnection();
			Object []rows = new Object[4];
			
			String sql = "SELECT * FROM GetDepartmentSalary()";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			SalaryModel.setRowCount(0);
			
			while(rs.next()) {
				rows[0] = rs.getInt(1);
				rows[1] = rs.getString(2);
				rows[2] = rs.getString(3);
				rows[3] = rs.getFloat(4);
				SalaryModel.addRow(rows);
			}
			
			ps.close();
			rs.close();
			conn.close();
			
		} catch (SQLException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public boolean LoadEmployeeTable(int department_id,int shift_id, String gender) {
		try {
			conn = new SQLServerConnUtils().getSQLServerConnection();
			Object []rows = new Object[5];
			
			if (department_id == 0 && shift_id == 0 && gender.equalsIgnoreCase("")) {
				FindMessageLabel.setText("Please choose at least 1 condition!");
				return false;
			}
			
			String sql = "exec FindEmployee @_DepartmentID = ?, @_ShiftID = ?, @_Gender = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, department_id);
			ps.setInt(2, shift_id);
			ps.setString(3, "'"+gender+"'");
			
			ResultSet rs = ps.executeQuery();
			
			EmployeeModel.setRowCount(0);
			
			while(rs.next()) {
				rows[0] = rs.getInt(1);
				rows[1] = rs.getString(2);
				rows[2] = rs.getString(3);
				rows[3] = rs.getString(4);
				rows[4] = rs.getString(5);
				EmployeeModel.addRow(rows);
			}
			
			ps.close();
			rs.close();
			conn.close();
			
		} catch (SQLException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
		FindMessageLabel.setText("");
		return true;
	}
}
