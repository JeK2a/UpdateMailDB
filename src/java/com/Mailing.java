package com;

import com.classes.EmailAccount;
import com.classes.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Mailing implements Runnable {

    private DB db = Main.db;
    public static HashMap<Integer, EmailAccount> emailAccounts = new HashMap<>();

    @Override
    public void run() {

//        new SettingsMail();
//
//        Thread chatServerTread = new Thread(new ChatServer());
//        chatServerTread.start();
//

//
//        chatClient = new ChatClientInside();
//
//        chatClientTread = new Thread(chatClient);
//        chatClientTread.start();



        ArrayList<User> users = db.getUsers(); // Получение списка пользователей
//        MailingEmailAccount mailingEmailAccount = new MailingEmailAccount(); //

        int i = 0;

        for (User user : users) {

            EmailAccount emailAccount = new EmailAccount(user);
            emailAccounts.put(++i, emailAccount);
            Thread startMailThread = new Thread(new MailingEmailAccount(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
            startMailThread.start(); // Запус потока
            emailAccount.setThreadAccount(startMailThread);
//            mailingEmailAccount.connectToMailAccount(emailAccount); // Подключение к почтовым аккаунтам

            while (true) {
                if (
                    emailAccount.getStatus().equals("end_add_message_emailAccount") ||
                    !startMailThread.isAlive()
                ) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        System.out.println("----------------------------- end users");

//        ArrayList<User> users_update = db.getUsersUpdate(); // Получение списка пользователей

//        if (users_update == null) {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            for (User user : users_update) {
//                if (user.isIs_monitoring()) {
//                    EmailAccount emailAccount = new EmailAccount(user);
//                    emailAccounts.put(++i, emailAccount);
//                    mailingEmailAccount.connectToMailAccount(emailAccount);
//                } else {
//                    for (HashMap.Entry<Integer, EmailAccount> entry :  emailAccounts.entrySet()) {
//                        if (entry.getValue().getUser().getEmail().equals(user.getEmail())) {
//
//                        }
//                    }
//                }
//
//                EmailAccount emailAccount = new EmailAccount(user);
//
//                emailAccounts.put(++i, emailAccount);
//                mailingEmailAccount.connectToMailAccount(emailAccount); // Подключение к почтовым аккаунтам
//            }
//        }

//            MyPrint.printArrayList(emailAccounts);
//            System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
//        Console console = System.console();
    }
}
