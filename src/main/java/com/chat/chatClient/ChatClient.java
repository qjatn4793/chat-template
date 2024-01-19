package com.chat.chatClient;

import javax.swing.*;
import javax.swing.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.UUID;

public class ChatClient extends JFrame {
    private JTextField messageField;
    private JTextPane chatPane;
    private StyledDocument chatDocument;
    private Style userStyle;
    private Style opponentStyle;
    private JPanel inputPanel;
    private Socket socket;
    private PrintWriter out;
    private String clientId; // 클라이언트의 고유 ID

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    
    public ChatClient() {
    	// 클라이언트 고유 ID 생성
        clientId = UUID.randomUUID().toString();
    	
        setTitle("Chat Client");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        
        chatDocument = chatPane.getStyledDocument();

        StyleContext styleContext = new StyleContext();
        userStyle = styleContext.addStyle("UserStyle", null);
        StyleConstants.setAlignment(userStyle, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(userStyle, Color.BLUE);

        opponentStyle = styleContext.addStyle("OpponentStyle", null);
        StyleConstants.setAlignment(opponentStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(opponentStyle, Color.RED);

        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            // 서버에 연결
            socket = new Socket("localhost", 8080);
            // 출력 스트림 생성
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // 클라이언트의 clientId를 서버로 전송
            out.println(clientId);
            
            // 서버로부터 메시지를 비동기적으로 수신하는 별도 스레드 시작
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleReceivedMessage(clientId, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (ConnectException e) {
        	logger.info("연결 실패");
        } catch (SocketException e) {
        	logger.info(clientId + " 의 연결이 종료되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();

        if (!message.isEmpty()) {
            try {
                String sender = clientId + " : ";
                chatDocument.insertString(chatDocument.getLength(), sender, userStyle);
                chatDocument.insertString(chatDocument.getLength(), message + "\n", null);
                // 메시지와 클라이언트 ID를 서버로 전송
                out.println(clientId + "|" + message);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            messageField.setText("");
        }
    }

    private void handleReceivedMessage(String clientId, String message) {
        SwingUtilities.invokeLater(() -> {
            try {
            	
                if (!message.startsWith(clientId)) {
                    String sender = "익명의 사용자";
                    
                    chatDocument.insertString(chatDocument.getLength(), sender, opponentStyle);
                    chatDocument.insertString(chatDocument.getLength(), message + "\n", null);
                }
                
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatClient();
            }
        });
    }
}

