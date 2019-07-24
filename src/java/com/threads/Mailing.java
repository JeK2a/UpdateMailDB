package com.threads;

import com.Main;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.User;
import com.db.DB;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mailing implements Runnable {

    private DB db = Main.db;
    public volatile static ConcurrentHashMap<String, EmailAccount> emailAccounts = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try {
        ArrayList<User> users = db.getUsers(); // Получение списка пользователей

        for (User user : users) {

            EmailAccount emailAccount = new EmailAccount(user);

            addEmailAccount(emailAccount);

            while (true) {
                // разделить условия в println выводить revive try / close on error / wait
                if (
                    emailAccount.getStatus().equals("end")                  ||
                    emailAccount.getStatus().equals("stop")                 ||
                    emailAccount.getStatus().equals("error")                ||
                    emailAccount.getStatus().equals("AuthenticationFailed") ||
                    emailAccount.getStatus().equals("closed")               ||
                    emailAccount.getStatus().equals("close")
                ) {
                    break;
                }

                checkAccounts();

                Thread.sleep(30000);
            }
        }

        while (true) {
//            checkAccounts();
            Thread.sleep(300000);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAccounts() {
        for (Map.Entry<String, EmailAccount> accountEntry : emailAccounts.entrySet()) {

            EmailAccount emailAccount = accountEntry.getValue();
            MailingEmailAccountThread mailingEmailAccount_tmp = new MailingEmailAccountThread(emailAccount);

            ConcurrentHashMap<String, MyFolder> myFoldersMap_tmp = emailAccount.getMyFoldersMap();

            if (
                    !emailAccount.getStatus().equals("AuthenticationFailed") &&
                    emailAccount.getThread_problem() > 0 &&
                    emailAccount.getTime_reconnect() < (new Date().getTime() / 1000 - 240)
            ) {
                emailAccounts.remove(accountEntry.getKey());
//                addEmailAccount(emailAccount);
            }

            for (Map.Entry<String, MyFolder> folderEntry : myFoldersMap_tmp.entrySet()) {
                MyFolder myFolder = folderEntry.getValue();

                if (
                        myFolder.getThread_problem() > 0 &&
                        myFolder.getTime_last_noop() < (new Date().getTime() / 1000 - 120)
                ) {
                    System.err.println("Folder removed");
                    myFoldersMap_tmp.remove(folderEntry.getKey());
//                    mailingEmailAccount_tmp.addFolder(myFolder.getImap_folder());
                }
            }
        }
    }

    private void addEmailAccount(EmailAccount emailAccount) {
        Thread startMailThread = new Thread(new MailingEmailAccountThread(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
        emailAccounts.put(emailAccount.getEmailAddress(), emailAccount);
        startMailThread.setDaemon(true);
        startMailThread.start(); // Запус потока
    }

}
