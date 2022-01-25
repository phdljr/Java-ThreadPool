# Java-ThreadPool

서버를 만들 때, 클라이언트마다 스레드를 하나씩 달아주는게 아니라, 제한된 스레드만을 사용해서 외부 요청을 다 해결하도록 구성해야 된다. 제한된 스레드를 모아둔 것을 스레드 풀이라고 한다.

스레드풀은 ExecutorService 클래스를 통해 만들 수 있다.

executorService = Executors.newFixedThreadPool( //ExecutorService 객체를 얻기 위한 메소드
				Runtime.getRuntime().availableProcessors() //CPU 코어의 수만큼 스레드를 만들도록 함. int형으로 반환됨
		);
    
-----------------------------------------------------------------------------------------------------------

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
