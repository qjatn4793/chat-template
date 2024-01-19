package com.chat.chatClient;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.template.controller.MainController;

public class ChatServer {
	private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
	
	private static Map<PrintWriter, String> clients = new HashMap<>();

    public static void main(String[] args) {
    	logger.info("채팅이 시작되었습니다.");
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String clientId; // 클라이언트의 고유 ID

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // 클라이언트의 clientId를 읽어옴
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientId = reader.readLine();

                out = new PrintWriter(socket.getOutputStream(), true);
                clients.put(out, clientId);
                
                logger.info("clientId : {}", clientId);
                handleClientJoin(clientId);

                String message;

                while ((message = reader.readLine()) != null) {
                    // 클라이언트 ID와 메시지 분리
                    int delimiterIndex = message.indexOf("|");
                    if (delimiterIndex != -1) {
                        String receivedClientId = message.substring(0, delimiterIndex);
                        String receivedMessage = message.substring(delimiterIndex + 1);

                        // 클라이언트 ID와 메시지를 모든 클라이언트에게 전송
                        broadcast(receivedClientId, receivedMessage);
                    }
                }
            } catch (SocketException e) {
                logger.info("사용자의 연결이 종료되었습니다.");
                handleClientLeave(clientId);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clients.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void broadcast(String clientId, String message) {
            for (PrintWriter client : clients.keySet()) {
                // 클라이언트 ID와 메시지를 모든 클라이언트에게 전송
                if (!clientId.equals(clients.get(client))) {
                	
                	logger.info("message : {}", message);
                	
                    client.println("(" + clientId + ") : " + message);
                }
            }
        }
        
        // 클라이언트 입장 시 호출
        private void handleClientJoin(String clientId) {
            String joinMessage = "님이 입장하셨습니다.";
            broadcast(clientId, joinMessage); // 모든 클라이언트에게 전송
        }

        // 클라이언트 퇴장 시 호출
        private void handleClientLeave(String clientId) {
            String leaveMessage = "님이 퇴장하셨습니다.";
            broadcast(clientId, leaveMessage); // 모든 클라이언트에게 전송
        }
        
    }
}