package com.dep.chat_ip.client;

import com.dep.chat_ip.entity.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ClientInWin implements Runnable {

    private Socket socket; // Сетевой сокет для пересылки сообщений

    ClientInWin(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() { // Запуск потока
        while (true) {  // Работать постоянно
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream()); // Создание входящего потока из сокета
                Message message = (Message) objectInputStream.readObject();                           // Получение входящего сообщения
                ChatClientWin.addMessage(message);                                                    // Добавление полученного сообщения на основно окно
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e);
            }
        }
    }
}
