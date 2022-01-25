import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Server extends Application{
	ExecutorService executorService;
	ServerSocket serverSocket;
	List<Client> connections = new Vector<Client>();
	
	void startServer() {
		executorService = Executors.newFixedThreadPool( //ExecutorService 객체를 얻기 위한 메소드
				Runtime.getRuntime().availableProcessors() //CPU 코어의 수만큼 스레드를 만들도록 함
		);
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
		} catch (IOException e) {
			if(serverSocket.isClosed()) {
				stopServer();
				return;
			}
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Platform.runLater(()->{
					displayText("[서버 시작]");
					btnStartStop.setText("stop");
				});
				
				while(true) {
					Socket socket = serverSocket.accept();
					String message = "[연결 수락: "+ socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
					Platform.runLater(()->{
						displayText("[연결 개수: " + connections.size() + "]");
					});
				}
			}
		};
		executorService.submit(runnable); //스레드풀에서 처리. 해당 객체에 task를 넘겨주면 스레드 풀에서 알아서 task를 실행시켜 주나봄.
	}
	
	void stopServer() {
		//다 닫아주기
		executorService.shutdown();
	}
	
	class Client{
		Socket socket;
		
		Client(Socket socket){
			this.socket = socket;
			receive();
		}
		
		void send(String msg) {
			
		}
		
		void receive() {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while(true) {
							byte[] byteArr = new byte[100];
							InputStream inputStream = socket.getInputStream();
							
							int readByteCount = inputStream.read(byteArr);
							
							if(readByteCount == -1) {
								throw new IOException();
							}
							
							String message = "[요청 처리]";
							String data = new String(byteArr, 0, readByteCount, "UTF-8");
							
							for(Client client : connections) {
								client.send(data);
							}
						}
					}
					catch(Exception e) {
						
					}
				}
				
			};
			
			executorService.submit(runnable);
		}
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
