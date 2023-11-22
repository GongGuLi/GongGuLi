package back;

import back.handler.ChatServer_Handler;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer extends Thread {
    private Socket socket = null;
    private ServerSocket serversocket = null;
    private ChatServer_Handler handler = null;
    private ArrayList<ChatServer_Handler> list = new ArrayList<>();

    public ChatServer(ServerSocket serversocket) {
        this.serversocket = serversocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("[클라이언트 접속 대기중] port : " + serversocket.getLocalPort());
                socket = serversocket.accept();
                System.out.println("Client connect: " + socket.getLocalAddress());

                handler = new ChatServer_Handler(socket, list);
                handler.start();

                list.add(handler);
            }
        } catch (Exception exception) {
            stopServer();
            System.out.println("서버 종료, 포트 번호: " + serversocket.getLocalPort());
        } finally {
            try {
                stopServer();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void stopServer() {
        try {
            serversocket.close();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }
}