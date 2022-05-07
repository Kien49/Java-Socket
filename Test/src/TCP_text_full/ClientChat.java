package TCP_text_full;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.border.EtchedBorder;

public class ClientChat extends JFrame {

	private JPanel contentPane;
	private JTextField txtStaff;
	private JTextField txtServerIP;
	private JTextField txtServerPort;
	
	Socket mngSocket = null;
	String mngIP = "";
	int mngPort = 0;
	String staffName = "";
	BufferedReader bf = null;
	DataOutputStream os = null;
	OutputThread t = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientChat frame = new ClientChat();
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
	public ClientChat() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 725, 382);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setForeground(new Color(255, 0, 0));
		panel.setBackground(Color.CYAN);
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Staff", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(255, 0, 0)));
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(1, 7, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Staff Name: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel);
		
		txtStaff = new JTextField();
		txtStaff.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtStaff.setForeground(Color.BLACK);
		panel.add(txtStaff);
		txtStaff.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Manage IP: ");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel_1);
		
		txtServerIP = new JTextField();
		txtServerIP.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(txtServerIP);
		txtServerIP.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Manager Port: ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel_2);
		
		txtServerPort = new JTextField();
		txtServerPort.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(txtServerPort);
		txtServerPort.setColumns(10);
		
		JFrame thisFrame = this;//tạo biến tạm thời
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// đầu tiên lấy các thông số trên giao diên
				mngIP = txtServerIP.getText();
				mngPort =  Integer.parseInt(txtServerPort.getText());//chuyển dữ liệu (chuỗi) thành số
				staffName = txtStaff.getText();
				
				try {
					mngSocket = new Socket(mngIP, mngPort);// mở ra cái socket ( mở kết nối tới server )
					if (mngSocket != null) {// nếu mà mở được
						FormChat p = new FormChat(mngSocket, staffName, "Manager");
						//mở ra cái formchat client..yêu cầu vào là 1 cái socket...sender là staffName.Người nhận là Manager
						
						//Tiếp theo là thêm form chat vào giao diện
						//đầu tiên là tạo ra biến tạm thời là thisframe( giao diện chính bên phía client)
						thisFrame.getContentPane().add(p);
						// thêm formchat Client
						p.getTxtMessages().append("Manager is running");
						p.updateUI();// Cập nhật giao diện
						
						//lấy cái luồng(dữ liệu) ra
						bf = new BufferedReader(new InputStreamReader(mngSocket.getInputStream()));
						os = new DataOutputStream(mngSocket.getOutputStream());
						
						//khi mà kết nốt thì chúng ta sẽ gửi về phía server các thông tin kết nối để server tạo ra các tab cửa sổ chat
						os.writeBytes("Staff: "+staffName);//tạo ra cái quy định tên gửi đi 
						//ghi dữ liệu vào
						os.write(13);
						os.write(10);
						os.flush();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		panel.add(btnConnect);
	}

}
