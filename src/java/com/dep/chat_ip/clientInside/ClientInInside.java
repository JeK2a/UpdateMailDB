package com.dep.chat_ip.clientInside;

import com.dep.chat_ip.entity.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

class ClientInInside implements Runnable {

    private Socket socket; // Сетевой сокет для пересылки сообщений

    ClientInInside(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() { // Запуск потока
        while (true) {  // Работать постоянно
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream()); // Создание входящего потока из сокета
                Message message = (Message) objectInputStream.readObject();                           // Получение входящего сообщения
//                System.out.println(message);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e);
            }
        }
    }
}
