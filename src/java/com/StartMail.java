package com;

import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.User;
import com.service.MyPrint;
import com.service.Settings;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.threads.AddNewMessageThread;
import com.threads.MailListenerThread;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import java.util.ArrayList;
import java.util.HashMap;

public class StartMail {

    private static DB db;

    private static HashMap<String, HashMap<String, Thread>> threadMap = new HashMap<>();

    private static WSSChatClient wssChatClient;
    private static HashMap<Integer, EmailAccount> emailAccounts = new HashMap<>();

    private StartMail() {

    }

    private void connectToMailAccount(EmailAccount emailAccount) {
        MyProperties myProperties = new MyProperties(emailAccount.getUser()); // Настройка подключение текущего пользователя

        Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
        session.setDebug(Boolean.parseBoolean(Settings.getSession_debug()));          // Включение дебага

        try {
            Store store = session.getStore();
            store.connect(
                emailAccount.getUser().getHost(),
                emailAccount.getUser().getEmail(),
                emailAccount.getUser().getPassword()
            );

            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
            imapFolder.open(IMAPFolder.READ_ONLY);

            store.addFolderListener(new FolderListener() { // Подключение отслеживания действий с падками в текущем подключении пользователя
                @Override
                public void folderCreated(FolderEvent folderEvent) { // Действие при создании папки
                    try {
                        StartMail.enterMessage("folder created");
                    } catch (Exception e) {
                        emailAccount.setStatus("error");
                        emailAccount.setException(e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void folderDeleted(FolderEvent folderEvent) { // Действие при удалении папки

                    StartMail.enterMessage("folder deleted");

                    try {
                        IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                        for (IMAPMessage imap_message : messages) {
                            db.changeDeleteFlag(new Email(emailAccount.getUser(), imap_message, (IMAPFolder) folderEvent.getFolder()), emailAccount.getUser().getUser_id()); // TODO изменение флага сообщенией на удаленное (проверить)
                        }
                    } catch (MessagingException e) {
                        emailAccount.setStatus("error");
                        emailAccount.setException(e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void folderRenamed(FolderEvent folderEvent) { // Действие при переименовании папки
                    try {
                        String old_folder_name = folderEvent.getFolder().getFullName();
                        String new_folder_name = folderEvent.getNewFolder().getFullName();
                        int user_id = emailAccount.getUser().getUser_id();

                        IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                        for (IMAPMessage imap_message : messages) {
                            db.changeFolderName(new Email(emailAccount.getUser(), imap_message, (IMAPFolder) folderEvent.getFolder()), user_id, new_folder_name); // TODO проверить, добавить проверку результата
                        }

                    } catch (MessagingException e) {
                        emailAccount.setStatus("error");
                        emailAccount.setException(e);
                        e.printStackTrace();
                    }
                    StartMail.enterMessage("folder renamed");
                }
            });

            store.addStoreListener(storeEvent ->
                    StartMail.enterMessage("store notification - " + storeEvent.getMessage()));

            IMAPFolder[] imap_folders = (IMAPFolder[]) store.getDefaultFolder().list(); // Получение списка папок лоя текушего подключения
            for (IMAPFolder imap_folder: imap_folders) {

                MyFolder myFolder = new MyFolder(imap_folder);

                emailAccount.addMyFolder(myFolder);

                if (!imap_folder.isOpen()) {
                    try {
                        imap_folder.open(IMAPFolder.READ_ONLY);

                    } catch (MessagingException e) {
                        emailAccount.setStatus("error");
                        emailAccount.setException(e);
                        e.printStackTrace();
                    }
                }

                StartMail.enterMessage("Connect to -> " + emailAccount.getUser().getEmail() + " -> " + imap_folder.getFullName());

                Thread myThreadEvent = new Thread(new MailListenerThread(emailAccount, myFolder)); // Создание потока для отслеживания действий с определенной папкой // TODO 2 lsn
                myFolder.setThreadLisaningChangeMessage(myThreadEvent);
                myThreadEvent.start();

                Thread myTreadAllMails = new Thread(new AddNewMessageThread(emailAccount, myFolder)); // Создание потока для синхронизации всего почтового ящика // TODO 1 all
                myFolder.setThreadAddNewMessages(myTreadAllMails);
//                myTreadAllMails.start();

                while (true) {
                    if (myFolder.getStatus().equals("listening") || myFolder.getStatus().equals("stop")) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                myTreadAllMails.start(); // Запус потока



//                Thread myTreadAllMails = new Thread(new AddNewMessageThread(emailAccount, myFolder)); // Создание потока для посинхронизации всего почтового ящика // TODO 1 all
//                myFolder.setThreadAddNewMessages(myTreadAllMails);
//
//                Thread myThreadEvent = new Thread(new MailListenerThread(emailAccount, myFolder)); // Создание потока для отслеживания действий с определенной папкой // TODO 2 lsn
//                myFolder.setThreadLisaningChangeMessage(myThreadEvent);
//
//                if (true) {
//                    myFolder.setStatus("create new thread -> add new message");
//                    Thread myTreadAllMails = new Thread(new AddNewMessageThread(emailAccount, myFolder)); // Создание потока для посинхронизации всего почтового ящика // TODO 1 all
//                    myFolder.setThreadAddNewMessages(myTreadAllMails);
//                    myTreadAllMails.start(); // Запус потока
//                } else {
//                    myFolder.setStatus("stop");
//                }
//
//                while (true) {
//                    try {
//                        System.out.println("folder status = " + myFolder.getStatus());
//                        if (myFolder.getStatus() != "create new thread -> add new message") {
//                            Thread myThreadEvent = new Thread(new MailListenerThread(emailAccount, myFolder)); // Создание потока для отслеживания действий с определенной папкой // TODO 2 lsn
//                            myFolder.setThreadLisaningChangeMessage(myThreadEvent);
//                            myThreadEvent.start(); // Запус потока
//                            break;
//                        }
//
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            emailAccount.setStatus("listening");

        } catch (MessagingException e) {
            enterMessage("Problems wish "  + emailAccount.getUser().getEmail());
            emailAccount.setStatus("stop");
            emailAccount.setException(e);
            e.printStackTrace();
        }
    }

    // Вывод сообщения
    public static void enterMessage(String text) {
        System.out.println(text);
        wssChatClient.sendText(text);
    }

	public static void main(String[] args) {

        new Settings();

        wssChatClient = new WSSChatClient();
        db = new DB();
        ArrayList<User> users = db.getUsers(); // Получение списка пользователей
        StartMail startMail = new StartMail(); //

        int i = 0;

        for (User user : users) {
            EmailAccount emailAccount = new EmailAccount(user);
            emailAccounts.put(++i, emailAccount);
            startMail.connectToMailAccount(emailAccount); // Подключение к почтовым аккаунтам

            while (true) {
                if (emailAccount.getStatus().equals("listening") || emailAccount.getStatus().equals("stop")) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        while (true) {
            System.out.println("---------------------------------------------------------------------------------");
//            MyPrint.printArrayList(emailAccounts);
            System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
            System.out.println("---------------------------------------------------------------------------------");

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}
