import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.omg.CORBA_2_3.portable.OutputStream;

public class Server {

	ServerSocket serverSocket;

	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Server is running....!!!");
		try {
			// 1. 연결이 완성될 때까지 해당 라인에서 멈춘다.
			// 서버의 메인 thread는 클라이언트와의 연결만 담당하고,
			// 실제 Task는 sub thread(working thread)에서 처리되기 때문에 동시에 많은 요청을 처리할 수
			// 있음..
			Socket client = serverSocket.accept();
			processClient(client);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processClient(Socket client) {
		new Thread() {
			// 2. 연결된 socket에서 요청을 받는 stream을 열어서 통신준비를 한다.
			public void run() {
				InputStream is = null;
				OutputStream os = null;

				try {
					is = client.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String line = "";
					while (!(line = br.readLine()).startsWith("Accept-Language")) {
						System.out.println(line);
					}
					System.out.println("request done!");

				} catch (IOException o) {
					o.printStackTrace();
				}
				// 3. 응답을 하는 Stream을 열어서 응답을 완료한다.
				try {
					os = (OutputStream) client.getOutputStream();
					String message = "Response Completed!!!";
					// 헤더
					os.write("HTTP/1.0 200 OK \r\n".getBytes());
					os.write("Content-Type L text/html \r\n".getBytes());
					// 헤더와 바디 구분해주는 줄
					os.write(("Content-Length: " + message.getBytes().length + " \r\n").getBytes());
					// 실제 바디 메시지
					os.write(message.getBytes());
					os.flush();

				} catch (IOException e) {
					e.printStackTrace();

				} finally {
					try {
						os.close();
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				
			}
		}.start();
	}
}