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
		
		JFrame thisFrame = this;//t???o bi???n t???m th???i
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ?????u ti??n l???y c??c th??ng s??? tr??n giao di??n
				mngIP = txtServerIP.getText();
				mngPort =  Integer.parseInt(txtServerPort.getText());//chuy???n d??? li???u (chu???i) th??nh s???
				staffName = txtStaff.getText();
				
				try {
					mngSocket = new Socket(mngIP, mngPort);// m??? ra c??i socket ( m??? k???t n???i t???i server )
					if (mngSocket != null) {// n???u m?? m??? ???????c
						FormChat p = new FormChat(mngSocket, staffName, "Manager");
						//m??? ra c??i formchat client..y??u c???u v??o l?? 1 c??i socket...sender l????staffName.Ng?????i nh???n l?? Manager
						
						//Ti???p theo l?? th??m form chat v??o giao di???n
						//?????u ti??n l?? t???o ra bi???n t???m th???i l?? thisframe( giao di???n ch??nh b??n ph??a client)
						thisFrame.getContentPane().add(p);
						// th??m formchat Client
						p.getTxtMessages().append("Manager is running");
						p.updateUI();// C???p nh???t giao di???n
						
						//l???y c??i lu???ng(d??? li???u) ra
						bf = new BufferedReader(new InputStreamReader(mngSocket.getInputStream()));
						os = new DataOutputStream(mngSocket.getOutputStream());
						
						//khi m?? k???t n???t th?? ch??ng ta s??? g???i v??? ph??a server c??c th??ng tin k???t n???i ????? server t???o ra c??c tab c???a s??? chat
						os.writeBytes("Staff: "+staffName);//t???o ra c??i quy ?????nh t??n g???i ??i 
						//ghi d??? li???u v??o
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
