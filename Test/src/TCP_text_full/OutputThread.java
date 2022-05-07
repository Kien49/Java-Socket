package TCP_text_full;
//Tiến trình để nhận dữ liệu từ chat để hiển thị
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JTextArea;

public class OutputThread extends Thread {
	Socket socket;
	JTextArea txt; // hiển thị cái nội dung để hiện thị
	BufferedReader bf; // đối tượng dùng cho việc đọc
	String sender; // hiển thị tên người gửi
	String receiver; // hiển thị tên người nhận
	
	public OutputThread(Socket socket, JTextArea txt, String sender, String receiver) {//tạo contrucstor
		super();
		this.socket = socket;
		this.txt = txt;
		this.sender = sender;
		this.receiver = receiver;
		try {
			bf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//tạo bf là  1 đối tượng để đọc dữ liệu từ 1 cái luồng
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() { // phương thức ngầm để cho nó chạy
		while (true) {
			try {
				if(socket != null) {
					String msg = ""; //gắn msg bằng rỗng
					if((msg = bf.readLine()) != null && msg.length() >0 ) {
						//đọc cái msg ra nếu nó khác rỗng và độ dài lớn hơn 0 => Hiển thị
						txt.append("\n" + receiver + ": "+msg);
					}
				}
				sleep(1000);//đọc liên tục sẽ làm chậm máy , đọc xong sẽ dừng 1 chút
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
}
