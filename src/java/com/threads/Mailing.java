package com.threads;

import com.DB;
import com.Main;
import com.classes.EmailAccount;
import com.classes.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Mailing implements Runnable {

    private DB db = Main.db;
    public static HashMap<Integer, EmailAccount> emailAccounts = new HashMap<>();

    @Override
    public void run() {
        try {
        ArrayList<User> users = db.getUsers(); // Получение списка пользователей
//        MailingEmailAccountThread mailingEmailAccount = new MailingEmailAccountThread(); //

        int i = 0;

            for (User user : users) {
                System.out.println(user);
//                for (HashMap.Entry<Integer, EmailAccount> entry :  emailAccounts.entrySet()) {
//                    if (!entry.getValue().getThreadAccount().isAlive()) {
//                        User user_tmp = entry.getValue().getUser();
//                        MailingEmailAccountThread mailingEmailAccount = new MailingEmailAccountThread(new EmailAccount(user_tmp));
//                        Thread thread = new Thread(mailingEmailAccount);
//                        thread.setDaemon(true);
//                        entry.getValue().setThreadAccount(thread);
//                        thread.start(); // Запус потока
//                    }
//                }


                EmailAccount emailAccount = new EmailAccount(user);
                emailAccounts.put(++i, emailAccount);
                Thread startMailThread = new Thread(new MailingEmailAccountThread(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
                startMailThread.setDaemon(true);

//                emailAccount.setThreadAccount(startMailThread);
                startMailThread.start(); // Запус потока

            while (true) {
                // разделить условия в println выводить revive try / close on error / wait
                if (
                    !startMailThread.isAlive() || // 5 try to revive
                    emailAccount.getStatus().equals("end_add_message_emailAccount")   ||
                    emailAccount.getStatus().equals("AuthenticationFailedException")  ||
                    emailAccount.getStatus().equals("stop")                           ||
                    emailAccount.getStatus().equals("error")                          ||
                    emailAccount.getStatus().equals("close")
                ) {
                    break;
                }

                Thread.sleep(10000);
            }
        }

        System.out.println("----------------------------- end users");

        while (true) {
            Thread.sleep(500);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

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
//            System.out.println(MyPrint.getStringFromEmailAccounts(emailAccounts));
//        Console console = System.console();
    }
}
