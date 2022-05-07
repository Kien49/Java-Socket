package TCP_text_full;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;


import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class FormChat extends JPanel {

	Socket socket = null; // socket để gửi và nhận
	BufferedReader bf = null; // đọc
	DataOutputStream os = null;// đối tượng để đẩy dữ liệu đi
	OutputThread t = null; //chạy nó để đọc thông tin cái luồng để nó ghi ra và nó sẽ cập nhật lên giao diên
	String sender;
	String receiver;
	JTextArea txtMessages;// tạo biến toàn cục
	JButton btnCall;
	JButton btnStopCall;
	/**
	 * Create the panel.
	 */
	public FormChat(Socket s, String sender, String receiver) {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		JTextArea txtMessage = new JTextArea();
		txtMessage.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txtMessage.setLineWrap(true);
		scrollPane.setViewportView(txtMessage);
		
		JButton btnSend = new JButton("Send text");
		scrollPane.setRowHeaderView(btnSend);
		
		btnStopCall = new JButton("Stop Call");
		btnStopCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client_voice.calling = false;
				btnCall.setEnabled(true);
				btnStopCall.setEnabled(false);
			}
		});
		panel.add(btnStopCall, BorderLayout.SOUTH);
		
		btnCall = new JButton("Call Voice");
		btnCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					init_audio();
				} catch (UnknownHostException | SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnCall, BorderLayout.NORTH);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtMessage.getText().trim().length()==0) return;
				// lấy dữ liệu và kiểm tra
				try {
					os.writeBytes(txtMessage.getText());// ghi dữ liệu vào
					os.write(13);// kí tự xuống dòng
					os.write(10);// kí tự xuống dòng
					os.flush();// đẩy dữ liệu đi
					txtMessages.append("\n"+sender+ ": "+txtMessage.getText());
					// hiển thị dữ liệu lên trên
					txtMessage.setText("");//cho nó bằng rỗng luôn để viết cái khác
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		});
		
		JScrollPane scrollPane_1 = new JScrollPane();
		add(scrollPane_1, BorderLayout.CENTER);
		
		txtMessages = new JTextArea();
		txtMessages.setLineWrap(true);
		scrollPane_1.setViewportView(txtMessages);

		//-------------------chat text-------------------------//
		socket = s;
		this.sender = sender;
		this.receiver = receiver;
		try {
			bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//tạo bf là để đọc dữ liệu => làm việc với InputStream
			os = new DataOutputStream(socket.getOutputStream());
			// os là để ghi dữ liệu => làm việc với OutputStream
			t = new OutputThread(s, txtMessages, sender, receiver);
			// ghi thông tin vào txtMessages
			t.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public JTextArea getTxtMessages() {
		//trả các đối tượng mess về cho các thành phần khác(trả recevier) 
		return this.txtMessages;
	}
	
	//-------------------chat voice-------------------------//
	public int port_server = 4919;
	public String add_server = "127.0.0.1";
	public static AudioFormat getaudioformat() {
		float sampleRate = 8000.0F;
		int sampleSizeInbits = 16;
		int channel = 2;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}
	TargetDataLine audio_in;//chọn thiết bị đầu vào cụ thể, cung cấp 1 phương pháp đọc dữ liệu đã thu nhập từ bộ đềm bằng cách nhấp chuột ở đây là audio_in
	
	public void init_audio() throws UnknownHostException, SocketException {
		try {
			AudioFormat format = getaudioformat();// định dạng lại format theo như ban đầu định nghĩa
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			//cung cấp thông tin nó được thừa kế từ class getaudioformat ở trên  và cung cấp thêm các thông tin mặc định như định dạng âm thanh được hỗ trợ, kích thức tối thiểu và tối đa
			
			if(!AudioSystem.isLineSupported(info)) {
				System.out.println("Not Support");
				System.exit(0);
			}
			audio_in = (TargetDataLine) AudioSystem.getLine(info);
			audio_in.open(format);
			audio_in.start();
			
			record_thread r = new TCP_text_full.record_thread();
			InetAddress inet = InetAddress.getByName(add_server);
			r.audio_in = audio_in;
			r.dout = new DatagramSocket();
			r.server_ip = inet;
			r.server_port = port_server;
			Client_voice.calling = true;
			r.start();
			btnCall.setEnabled(false);
			btnStopCall.setEnabled(true);
			
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		
}
