package serverStructure;

import back.handler.ChatServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer extends Thread {
    private final ServerSocket serversocket;
    private final ArrayList<ChatServerHandler> list = new ArrayList<>();

    public ChatServer(ServerSocket serversocket) {
        this.serversocket = serversocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("[채팅방 서버 열림] PORT : " + serversocket.getLocalPort());

            while (true) {
                Socket socket = serversocket.accept();
                System.out.println("[채팅방 입장] PORT : " + serversocket.getLocalPort() + " IP : " + socket.getLocalAddress());

                ChatServerHandler handler = new ChatServerHandler(socket, list);
                handler.start();

                list.add(handler);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                serversocket.close();
                System.out.println("[오류 발생, 채팅방 서버 닫힘] PORT : " + serversocket.getLocalPort());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}