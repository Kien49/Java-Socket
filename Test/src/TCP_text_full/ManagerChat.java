package TCP_text_full;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTabbedPane;
import java.awt.Color;

public class ManagerChat extends JFrame implements Runnable{
//thread là 1 class độc lập, Runnable là 1 interface tạo ra class thread
	private JPanel contentPane;
	private JTextField txtServerPort;
	private JTabbedPane tabbedPane;
	
	ServerSocket srvSocket = null;
	BufferedReader bf =  null;
	Thread t;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManagerChat frame = new ManagerChat();
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
	public ManagerChat() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 725, 382);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(1, 2, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Manager Port: ");
		lblNewLabel.setForeground(Color.RED);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(lblNewLabel);
		
		txtServerPort = new JTextField();
		txtServerPort.setForeground(Color.RED);
		txtServerPort.setText("4919");
		txtServerPort.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(txtServerPort);
		txtServerPort.setColumns(10);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		tabbedPane.setToolTipText("");
		tabbedPane.setForeground(Color.RED);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		int serverPort = Integer.parseInt(txtServerPort.getText());// lấy port ra
		try {
			srvSocket = new ServerSocket(serverPort);
		}catch(Exception e){
			e.printStackTrace();
		}
		//cho tiến trình chạy
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		while (true) {//tiến trình chạy ngầm chạy liên tục
			try {
				//Tiến trình nhận socket
				Socket aStaffSocket = srvSocket.accept();// nhận socket
				if(aStaffSocket != null) {
					bf = new BufferedReader(new InputStreamReader(aStaffSocket.getInputStream()));//tạo bf để đọc
					String S = bf.readLine();// bf đọc 1 dòng có dạng là staff: .....
					
					//sử dụng phương pháp xử lý chuỗi để cắt nó ra
					int pos = S.indexOf(":");// tìm dau :
					String staffName = S.substring(pos+1);// cắt từ dấu : 
					
					//tạo ra 1 cửa sổ chat
					FormChatServer p = new FormChatServer(aStaffSocket, "Manager", staffName);//sender là mangaer, recervier là staffname
					tabbedPane.add(staffName, p);//hiển thị phần chat => thêm khung chat
					p.updateUI();
				}
				Thread.sleep(100);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
