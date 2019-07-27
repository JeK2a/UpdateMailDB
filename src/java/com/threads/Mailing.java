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

    private static int index = 0;

    public static int getIndex() {
        return ++index;
    }

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

                    Thread.sleep(60000);
                }
            }

            while (true) {
                checkAccounts();
                Thread.sleep(120000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAccounts() {
        for (Map.Entry<String, EmailAccount> accountEntry : emailAccounts.entrySet()) {

            EmailAccount emailAccount = accountEntry.getValue();
            MailingEmailAccountThread mailingEmailAccount_tmp = new MailingEmailAccountThread(emailAccount);

            if (
                    !emailAccount.getStatus().equals("AuthenticationFailed") &&
                    emailAccount.getThread_problem() > 0 &&
                    emailAccount.getTime_reconnect() < (new Date().getTime() / 1000 - 240)
            ) {
                emailAccounts.remove(accountEntry.getKey());
                addEmailAccount(emailAccount);
            }

            for (Map.Entry<String, MyFolder> folderEntry : emailAccount.getMyFoldersMap().entrySet()) {
                MyFolder myFolder = folderEntry.getValue();

                if (
                        myFolder.getThread_problem() > 0 &&
                        myFolder.getTime_last_noop() < (new Date().getTime() / 1000 - 240)
                ) {
                    System.err.println("Folder removed");
                    emailAccount.getMyFoldersMap().remove(folderEntry.getKey());
                    mailingEmailAccount_tmp.addFolder(myFolder.getImap_folder());
                }
            }
        }
    }

    private void addEmailAccount(EmailAccount emailAccount) {
        Thread startMailThread = new Thread(new MailingEmailAccountThread(emailAccount)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
        startMailThread.setName("MailingEmailAccountThread " + MailingEmailAccountThread.getIndex());
        emailAccounts.put(emailAccount.getEmailAddress(), emailAccount);
        startMailThread.setDaemon(true);
        startMailThread.start(); // Запус потока
    }

}