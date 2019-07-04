package com.threads;

import com.DB;
import com.Main;
import com.classes.EmailAccount;
import com.classes.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Mailing implements Runnable {

    private DB db = Main.db;
    public volatile static ConcurrentHashMap<Integer, EmailAccount> emailAccounts = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try {
        ArrayList<User> users = db.getUsers(); // Получение списка пользователей

        int i = 0;

        for (User user : users) {

            EmailAccount emailAccount = new EmailAccount(user);

            Thread startMailThread = new Thread(new MailingEmailAccountThread(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
//            emailAccount.setThreadAccount(startMailThread);
            emailAccounts.put(++i, emailAccount);
            startMailThread.setDaemon(true);
            startMailThread.start(); // Запус потока

            while (true) {
                // разделить условия в println выводить revive try / close on error / wait
                if (
//                    !startMailThread.isAlive()                                        || // 5 try to revive
                    emailAccount.getStatus().equals("end")                            ||
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

//        System.out.println("----------------------------- end users");

        while (true) {
            Thread.sleep(500);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
