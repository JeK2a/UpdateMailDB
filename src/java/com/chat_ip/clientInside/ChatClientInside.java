package com.chat_ip.clientInside;

import com.chat_ip.entity.Message;
import com.chat_ip.inform.Information;
import com.service.SettingsChat;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

//import com.chat_ip.entity.Message;

public class ChatClientInside implements Runnable {

    private static String name = "mail"; // Стандартное имя

    private String status = "online";        // Статус текущего пользователя
    private String whoIm = "";               // Информация о текущем компьютере

    private ObjectOutputStream outputStream; // Исходящий поток

    @Override
    public void run() {
        try {
            InetAddress address = InetAddress.getByName(SettingsChat.getNameServerPC()); // получение адреса сервера в сети
            Socket socket = new Socket(address, SettingsChat.getPort());                 // открытия соета для связи с сервером

            whoIm = Information.getWhoIm();

            outputStream = new ObjectOutputStream(socket.getOutputStream()); // Создание потока для отправки сообщение на сервер

            new Thread(new ClientInInside(socket)).start();                  // Создание потока для входящих сообщений с сервера

            System.out.println(SettingsChat.getNameServerPC()); // вывод на экран название ПК сервера
            System.out.println(SettingsChat.getPort());         // вывод на экран порт ПК сервера
            System.out.println("address = " + address);         // вывод на экран адреса
            System.out.println("socket  = " + socket);          // вывод на экран сокета
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public boolean newMessage(String text) {
        try {
            if (outputStream == null) {
                System.out.println("outputStream null NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL NULL ");
                return false;
            }
            outputStream.writeObject(new Message(new Timestamp(new Date().getTime()), name, text, whoIm, status));
            outputStream.flush();   // проталкивание буфера вывода
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
