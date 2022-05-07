package TCP_text_full;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class FormChatServer  extends JPanel {

	Socket socket = null; // socket để gửi và nhận
	BufferedReader bf = null; // đọc
	DataOutputStream os = null;// đối tượng để đẩy dữ liệu đi
	OutputThread t = null; //chạy nó để đọc thông tin cái luồng để nó ghi ra và nó sẽ cập nhật lên giao diên
	String sender;
	String receiver;
	JTextArea txtMessages;// tạo biến toàn cục
	JButton btnCall;
	/**
	 * Create the panel.
	 */
	public FormChatServer(Socket s, String sender, String receiver) {
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

		btnCall = new JButton("Call Voice");
		btnCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					init_audio();
				} catch (SocketException | LineUnavailableException e1) {
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
	public int port = 4919;
	
	public static AudioFormat getaudioformat() {
		float sampleRate = 8000.0F;
		int sampleSizeInbits = 16;
		int channel = 2;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}
	public SourceDataLine audio_out;
	
	public void init_audio() throws LineUnavailableException, SocketException {
		AudioFormat format = getaudioformat();
		DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info_out)) {
			System.out.println("Not support");
			System.exit(0);
		}
		audio_out = (SourceDataLine)AudioSystem.getLine(info_out) ;
		audio_out.open(format);
		audio_out.start();
		player_thread p = new player_thread();
		p.din = new DatagramSocket(port);
		p.audio_out = audio_out;
		Server_voice.calling = true;
		p.start();
		btnCall.setEnabled(false);
	}
		
}
