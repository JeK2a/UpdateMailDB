package com.dep.chat_ip.server;

import com.service.SettingsChat;

import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer implements Runnable {
    public void run() {
        new ChatServer();

        try (ServerSocket serverSocket = new ServerSocket(SettingsChat.getPort())) {
//            System.out.println("Сервер запущен");

            while(true) {
                if (SocketThread.getClientsQuantity() <= SettingsChat.getSizeMaxClients()) {   // Если не привышено максималь
                    new Thread(new SocketThread(serverSocket.accept())).start();           // Созлание нового потока на сервере
                } else {
                    System.out.println("Превышено максимальное количество пользователей!"); // Вывести информационное сообщение
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}