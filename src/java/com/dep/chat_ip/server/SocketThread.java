package com.dep.chat_ip.server;

import com.dep.chat_ip.entity.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

class SocketThread implements Runnable {

    private final Socket SOCKET;                                       // Сокет
    private static LinkedList<Socket> listSocket = new LinkedList<>(); // Список всех сокетов клиентов, подключенных к серверу
    private static int clientsQuantity = 0;                            // Количество подключенных клиентов
    private ObjectInputStream inputStream = null;                      // Входящий потока

    SocketThread(Socket socket) { this.SOCKET = socket; }              // Конструктор

    // Получение количества клиентов
    static int getClientsQuantity() {
        return clientsQuantity;
    }

    @Override
    public void run() { // старт серверного потока

        Message message = null;

        try {
            clientsQuantity++;                                            // Увеличение количество клиентов
            listSocket.add(SOCKET);                                       // Добавление сокета в общий список
            inputStream = new ObjectInputStream(SOCKET.getInputStream()); // Создание постоянного одинночного входного потока
//            System.out.println("Клиент подключен");                       // Вывод сообщения, что клиент подключен
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println(ex);
        }

        // Отправка новому клиенту истории чата
        ObjectOutputStream outputStream;

        // Работа постоянного приема входящих сообщений с постоянного входящего потока
        while (true) {
            try {
                message = (Message) inputStream.readObject();           // Прием сообщение с постоянного входящего потока
                // Окончание работы потока
                if (message.getText().contains("END")) {                // Если во входящем сообщении есть END, отклють клиента
                    listSocket.remove(SOCKET);                          // Удалить из списка сокет клиента, который отключился от клиента
//                    System.out.println("Клиент отключен");              // Клиент отключен
                    clientsQuantity--;                                  // Уменьшение количество клиентов
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
                System.err.println(e);
                continue;
            }

            // Рассыл входящего сообщения по всем клиентам
            try {
                for (Socket s : listSocket) {                                   // Отсылка сообщения всем сокетам/клиентам
                    if (s == null) {
                        listSocket.remove(s);
                        continue;
                    }

                    outputStream = new ObjectOutputStream(s.getOutputStream()); // Создание из сокета исходящего потока
                    if (outputStream == null) {
                        listSocket.remove(s);
                        continue;
                    }
                    outputStream.writeObject(message);                          // Отправить сообщение по исходящему потоку
                    outputStream.flush();                                       // Протолкнуть сообщение
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println(ex);
            }
        }
    }
}