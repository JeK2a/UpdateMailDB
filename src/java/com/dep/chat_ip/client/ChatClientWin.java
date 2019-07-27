package com.dep.chat_ip.client;

import com.dep.chat_ip.entity.Message;
import com.dep.chat_ip.inform.Information;
import com.service.SettingsChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

public class ChatClientWin {

    private static JTextArea textArea;     // Текстовое поле для отбажения текстовых сообщений
    private static JTextField textEnter;   // Текстовое поле для ввода ссообщения
    private static String name = "anonymous"; // Стандартное имя

    // Добавление сообщения в конец текстового поля
    static void addMessage(Message message) {
        textArea.append(String.valueOf(message) + "\n");                      // Добавление сообщения в конец текстового поля
        textArea.setCaretPosition(ChatClientWin.textArea.getText().length()); // Перемещение в конец текстового поля
    }

    private static class MainFrame extends JFrame {
        private ObjectOutputStream outputStream; // Исходящий поток
        private String status = "online";        // Статус текущего пользователя
        private String whoIm = "";               // Информация о текущем компьютере

        MainFrame() {
            super("IPChatClient V1.5");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  // Завершить работу программы, при закрытии окна

            try {
                InetAddress address = InetAddress.getByName(SettingsChat.getNameServerPC()); // получение адреса сервера в сети
                Socket socket = new Socket(address, SettingsChat.getPort());                 // открытия соета для связи с сервером

                whoIm = Information.getWhoIm();

                outputStream = new ObjectOutputStream(socket.getOutputStream()); // Создание потока для отправки сообщение на сервер
                new Thread(new ClientInWin(socket)).start();                     // Создание потока для входящих сообщений с сервера

//                System.out.println(SettingsChat.getNameServerPC()); // вывод на экран название ПК сервера
//                System.out.println(SettingsChat.getPort());         // вывод на экран порт ПК сервера
//                System.out.println("address = " + address);     // вывод на экран адреса
//                System.out.println("socket  = " + socket);      // вывод на экран сокета
            } catch (IOException e) {
                System.err.println(e);
            }

            // Создание графической части клиента
            textArea = new JTextArea(20, 30); // Поле для вывода сообщений
            textArea.setEditable(false);                     // Сделать неактивным поле для вывода сообщений

            JScrollPane scrollPane = new JScrollPane(textArea);                                  // Панель прокрутки
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);     // Вертикальная прокрутка
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Горизонтальная прокрутка
            setPreferredSize(new Dimension(450, 450));                             // Установить размер окна
            add(scrollPane, BorderLayout.NORTH);                                                 // Добавление к окну панель с прокруткой

            textEnter = new JTextField(50); // Поле для ввода сообщения
            add(textEnter, BorderLayout.SOUTH);     // Добавление к окну поля для ввода сообшения

            setVisible(true); // Сделать окно видимым

            textEnter.addActionListener(e -> {
                if (!textEnter.getText().equals("")) {
                    try {
                        // отправка сообщения на сервер
                        outputStream.writeObject(new Message(new Timestamp(new Date().getTime()),
                                name, textEnter.getText(), whoIm, status));
                        outputStream.flush();   // проталкивание буфера вывода
                        textEnter.setText("");  // обнуление строки для ввода текста
                    } catch (IOException e2) {
                        System.err.println(e2);
                    }
                }
            });

            pack();                                 // Упаковать окно
            textEnter.requestFocus(true); // Установить фокус на поле для ввода сообщений

            // События основного окна
            this.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) { }

                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        outputStream.writeObject(new Message(new Timestamp(new Date().getTime()),
                                name, "END", whoIm, "offline")); // отправка на сервер данных, что клиент отключился
                        outputStream.flush(); // проталкивание буфера
                    } catch (IOException e1) {
                        System.err.println(e1);
                    }
                    System.exit(0);
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
        }
    }

    public static void main(String[] args) {
//        System.out.println(Information.getWhoIm());
        new MainFrame();
    }
}
