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
		executorService = Executors.newFixedThreadPool( //ExecutorService ��ü�� ��� ���� �޼ҵ�
				Runtime.getRuntime().availableProcessors() //CPU �ھ��� ����ŭ �����带 ���鵵�� ��
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
					displayText("[���� ����]");
					btnStartStop.setText("stop");
				});
				
				while(true) {
					Socket socket = serverSocket.accept();
					String message = "[���� ����: "+ socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
					Platform.runLater(()->{
						displayText("[���� ����: " + connections.size() + "]");
					});
				}
			}
		};
		executorService.submit(runnable); //������Ǯ���� ó��. �ش� ��ü�� task�� �Ѱ��ָ� ������ Ǯ���� �˾Ƽ� task�� ������� �ֳ���.
	}
	
	void stopServer() {
		//�� �ݾ��ֱ�
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
							
							String message = "[��û ó��]";
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
