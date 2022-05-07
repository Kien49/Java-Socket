package TCP_text_full;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.TargetDataLine;

public class record_thread extends Thread{
	public TargetDataLine audio_in = null;
	public DatagramSocket dout;
	byte byte_buff[] = new byte[512];
	public InetAddress server_ip;
	public int server_port;
	@Override
	public void run() {
		int i = 0;
		while(Client_voice.calling) {
				audio_in.read(byte_buff, 0, byte_buff.length);
				DatagramPacket data = new DatagramPacket(byte_buff, byte_buff.length, server_ip, server_port);
				System.out.println("send: " + i++);// đếm trình tự số gói gửi đi
					try {
						dout.send(data);// nó sẽ đại diện cho 1 socket là ddierm để gửi  1 dịch vụ phân phối gói,
						// mỗi gói sẽ gửi trên 1 dout và có địa chỉ và định tuyến riêng
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		audio_in.close();
		audio_in.drain();
		System.out.println("Thread stop");
	}
}