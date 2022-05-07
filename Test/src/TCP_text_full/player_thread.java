package TCP_text_full;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.SourceDataLine;


public class player_thread extends Thread{
	public DatagramSocket din;
	public SourceDataLine audio_out;
	byte[] buffer = new byte[512];
	@Override
	public void run() {
		int i = 0;
		
		while(Server_voice.calling) {
			audio_out.write(buffer, 0, buffer.length);
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			buffer = incoming.getData();
			System.out.println(i++);
			try {
				din.receive(incoming);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		audio_out.close();
		audio_out.drain();
		System.out.println("Stop");
	}
}