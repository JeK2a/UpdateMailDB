package com.threads;

import com.DB;
import com.Main;
import com.MyProperties;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.service.SettingsMail;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.wss.WSSChatClient;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MailingEmailAccountThread implements Runnable {

    private static DB db = Main.db;
//    private static HashMap<Integer, EmailAccount> emailAccounts = Mailing.emailAccounts;
//    private static HashMap<String, HashMap<String, Thread>> threadMap = new HashMap<>();

    private EmailAccount emailAccount;
    private static WSSChatClient wssChatClient = Main.wssChatClient;

    public MailingEmailAccountThread(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
//        Thread.sleep();
//        emailAccount.setThreadAccount();
    }

    @Override
    public void run() {

        changeAccountStatus("start");

        MyProperties myProperties = new MyProperties(emailAccount.getUser()); // Настройка подключение текущего пользователя

        Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
        session.setDebug(SettingsMail.getSession_debug());          // Включение дебага

        try {
            Store store = session.getStore("imap");

            for (int i = 0; i < 10; i++) {

                switch (connectToStore(store)) {
                    case -1:
                        changeAccountStatus("error");
                        if (i == 9) { return; } else { break; }
                    case  0:
//                        changeAccountStatus("AuthenticationFailedException");
                        changeAccountStatus("error");
                        changeAccountException(new AuthenticationFailedException());
                        return;
                    case  1:
                        changeAccountStatus("connect");
                        i = 10;
                        break;
                }

                Thread.sleep(3000);
            }

            addStoreListeners(store);

            IMAPFolder[] imap_folders = (IMAPFolder[]) store.getDefaultFolder().list("*"); // Получение списка папок лоя текушего подключения

            int i = 0;

//            System.out.println("---------------------------------------------------");

            for (IMAPFolder imap_folder: imap_folders) {

//                System.out.println(imap_folder.getFullName());

//                if (imap_folder == null || !imap_folder.exists()) {
//                    System.out.println("Invalid folder");
//                }

                int tmp_i = 0;

                ConnectToFolder connectToFolder = null;

                while ((connectToFolder == null || !connectToFolder.is_open) && ++tmp_i <= 3) {

                    connectToFolder = new ConnectToFolder(imap_folder, "MailingEmailAccountThread -> run");
                    Thread connectToFolderThread = new Thread(connectToFolder);
                    connectToFolderThread.start();

                    long start = System.currentTimeMillis();

                    while(!connectToFolder.is_open && System.currentTimeMillis() < start + 10000) {
                        Thread.sleep(50);
                    }

                    long stop = System.currentTimeMillis();

//                    System.out.println("timer " + (stop - start));

//                        connectToFolderThread.interrupt();
                    connectToFolderThread.stop();
                }

                if (connectToFolder.is_open ) {
                    wssChatClient.sendText(
                        emailAccount.getEmailAddress() + " - " + imap_folder.getFullName(),
                        "isOpen ok"
                    );
                    continue;
                } else {
                    wssChatClient.sendText(
                        emailAccount.getEmailAddress() + " - " + imap_folder.getFullName(),
                        "isOpen error"
                    );
                }

//                System.out.println("2 - ");

//                System.out.println(imap_folder.isOpen());

                String text = "folder_name - " + imap_folder.getFullName() + " -- " + imap_folder.getMessageCount();

//                System.out.println("\u001B[91m" + text + "\u001B[0m");
                i += imap_folder.getMessageCount();
            }

//            System.out.println("sum = " + i);
//            System.out.println("---------------------------------------------------");

//            int folders_count = 0;
//            int folders_count_all = imap_folders.length;

            for (IMAPFolder imap_folder: imap_folders) {
                MyFolder myFolder = new MyFolder(imap_folder);
                emailAccount.addMyFolder(myFolder);
//                MailingEmailAccountThread.enterMessage("Connect to -> " + emailAccount.getUser().getEmail() + " -> " + imap_folder.getFullName());
                Thread myTreadAllMails = new Thread(new AddNewMessageThread(emailAccount, myFolder, imap_folder)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
                myTreadAllMails.setDaemon(true);
                myFolder.setThreadAddNewMessages(myTreadAllMails);
                myTreadAllMails.start(); // Запус потока
//                System.out.println("\u001B[91m" +"Folders count = " + (++folders_count) + " / " + folders_count_all + "\u001B[0m");

                if (SettingsMail.getWaitFolder()) {
                    while (true) { // TODO
                        if (myFolder.getStatus().equals("end_add_message_folder") || myFolder.getStatus().equals("stop")) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            ConcurrentHashMap<String, MyFolder> myFolderMap_tmp = emailAccount.getFoldersMap();

            emailAccount.setStatus("wait");

            String tmp_str = "";

            if (SettingsMail.getWaitUser()) {
                while (!Thread.interrupted()) {
                    int n = 0;
                    StringBuffer out = new StringBuffer();

                    for (Map.Entry<String, MyFolder> entry : myFolderMap_tmp.entrySet()) {

                        entry.getValue().getThreadAddNewMessages();

                        MyFolder tmp_myFolder = entry.getValue();

                        String status = tmp_myFolder.getStatus();

                        if (
                            !(
                                !tmp_myFolder.getThreadAddNewMessages().isAlive() ||
                                status.contains("sleep")                          ||
                                status.equals("open")                             ||
                                status.equals("listening")                        ||
                                status.equals("end_add_message_folder")           ||
                                status.equals("close")
                            )
                        ) {
                            out.append(entry.getValue().getFolder_name()).append(" - ").append(status).append("\n");
                            n++;
                        }
                    }

                    String text = emailAccount.getEmailAddress() + " wait " + n;
                    out.append("\u001B[91m").append(text).append("\u001B[0m");

                    if (!out.toString().equals(tmp_str)) {
                        tmp_str = out.toString();
//                        System.out.println(out);
                    }

                    if (n == 0) { break; }

                    Thread.sleep(5000);
                }
            }

            emailAccount.setStatus("end");

            while (!Thread.interrupted()) {
                if (!store.isConnected()) {
//                    System.out.println("store restart start");
                    connectToStore(store);
//                    System.out.println("store restart end");
                    Thread.sleep(60000);
                }
//                if (!store.isConnected()) {
//                    System.out.println("store restart start");
//                    if (connectToStore(store)) {
//                        System.out.println("store restart true");
//                        for (Map.Entry<String, MyFolder> entry : myFolderMap_tmp.entrySet()) {
//                            Thread thread_tmp = entry.getValue().getThreadAddNewMessages();
//
//                            thread_tmp.interrupt();
//                            Thread.sleep(5000);
//                            thread_tmp.stop();
////                            thread_tmp. shutdownnow();
//
//                            Thread.sleep(5000);
//                            thread_tmp.setDaemon(true);
//                            thread_tmp.start();
//
////                            MyFolder tmp_myFolder = entry.getValue();
//////                            tmp_myFolder.getThreadAddNewMessages().stop();
////
////                            IMAPFolder imap_folder = tmp_myFolder.getImap_folder();
////                            thread_tmp = new Thread(new AddNewMessageThread(emailAccount, tmp_myFolder, imap_folder));
////                            thread_tmp.setDaemon(true);
////                            tmp_myFolder.setThreadAddNewMessages(thread_tmp);
////                            thread_tmp.start();
//                        }
//                    }
//                }
                Thread.sleep(30000);
            }

//            System.out.println("store restart close");
        } catch (Exception e) {
            e.printStackTrace();
            enterMessage(emailAccount.getEmailAddress(),"Problems wish "  + emailAccount.getUser().getEmail());
            emailAccount.setStatus("error");
            emailAccount.setException(e);
            db.updateAccountError(emailAccount.getUser().getId(), e.getMessage());
        } finally {
//            emailAccount.setStatus("end");
//            System.out.println("end");
        }

        emailAccount.setStatus("stop");
    }

    private void addStoreListeners(Store store) {
        store.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {
//                System.err.println(emailAccount.getEmailAddress() + " store opened");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
//                System.err.println(emailAccount.getEmailAddress() + " store disconnected");
                connectToStore(store);
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
//                System.err.println(emailAccount.getEmailAddress() + " store disconnected");
                connectToStore(store);
            }
        });


        store.addFolderListener(new FolderListener() { // Подключение отслеживания действий с папками в текущем подключении пользователя
            @Override
            public void folderCreated(FolderEvent folderEvent) { // Действие при создании папки
                try {
                    MailingEmailAccountThread.enterMessage(folderEvent.getNewFolder().getFullName(), "folder created");
                    IMAPFolder new_folder = (IMAPFolder) folderEvent.getFolder();

                    MyFolder myFolder = new MyFolder(new_folder);

                    emailAccount.addMyFolder(myFolder);

                    if (!new_folder.isOpen()) {
                        new_folder.open(IMAPFolder.READ_ONLY);
                    }

                    MailingEmailAccountThread.enterMessage(
                            emailAccount.getUser().getEmail() + " - " + new_folder.getFullName(),
                            "Connect"
                    );

                    // TODO
                    Thread myThreadEvent = new Thread(new AddNewMessageThread(emailAccount, myFolder, new_folder));
//                    myFolder.setThreadLisaningChangeMessage(myThreadEvent);
                    myThreadEvent.start();
                } catch (Exception e) {
                    emailAccount.setStatus("error");
                    emailAccount.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void folderDeleted(FolderEvent folderEvent) { // Действие при удалении папки
                try {
                    MailingEmailAccountThread.enterMessage(
                        emailAccount.getEmailAddress() + " " + folderEvent.getFolder().getFullName(),
                        "folder deleted"
                    );

                    IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                    for (IMAPMessage imap_message : messages) {
                        db.setDeleteFlag(emailAccount.getEmailAddress(), folderEvent.getFolder().getFullName(), imap_message.getHeader("Message-ID")[0]); // TODO изменение флага сообщенией на удаленное (проверить)
//                            db.setDeleteFlag(new Email(emailAccount.getUser(), imap_message, (IMAPFolder) folderEvent.getFolder()), (IMAPFolder) folderEvent.getFolder()); // TODO изменение флага сообщенией на удаленное (проверить)
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
                    IMAPFolder imap_folder = (IMAPFolder) folderEvent.getFolder();

                    String old_folder_name = folderEvent.getFolder().getFullName();
                    String new_folder_name = folderEvent.getNewFolder().getFullName();
                    int user_id = emailAccount.getUser().getUser_id();

                    String email_address = emailAccount.getEmailAddress();

                    IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                    for (IMAPMessage imap_message : messages) {

                        long uid = imap_folder.getUID(imap_message);
                        Email email = new Email(user_id, email_address, imap_message, old_folder_name, uid, imap_folder);

                        db.changeFolderName(email, new_folder_name); // TODO проверить, добавить проверку результата
                    }

                    MailingEmailAccountThread.enterMessage(
                            emailAccount.getEmailAddress() + " " + old_folder_name,
                            "folder renamed, new folder name " + new_folder_name
                    );
                } catch (MessagingException e) {
                    emailAccount.setStatus("error");
                    emailAccount.setException(e);
                    e.printStackTrace();
                }
            }
        });

        store.addStoreListener(storeEvent -> {
            MailingEmailAccountThread.enterMessage(
                emailAccount.getEmailAddress(),
                "store notification message - " + storeEvent.getMessage()
            );

            try {
//                store.close(); // TODO tesing
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            connectToStore(store);
        });

//        store.addConnectionListener(new ConnectionListener() {
//            @Override
//            public void opened(ConnectionEvent connectionEvent) {
//                MailingEmailAccountThread.enterMessage("store connect opened");
//            }
//
//            @Override
//            public void disconnected(ConnectionEvent connectionEvent) {
//                MailingEmailAccountThread.enterMessage("store connect disconnected, reconnect" + connectToStore(store));
//                restartAllFoldersThread();
//            }
//
//            @Override
//            public void closed(ConnectionEvent connectionEvent) {
//                MailingEmailAccountThread.enterMessage("store connect closed, reconnect" + connectToStore(store));
//                restartAllFoldersThread();
//            }
//        });

    } // Добавление слушалки на аккаунт

//    private void restartAllFoldersThread() {
//
//        HashMap<String, MyFolder> myFolderMap_tmp = emailAccount.getFoldersMap();
//
//        for (Map.Entry<String, MyFolder> entry : myFolderMap_tmp.entrySet()) {
//            Thread folder_thread = entry.getValue().getThreadAddNewMessages();
//
//            if (folder_thread.isAlive()) {
//                folder_thread.stop();
//            }
//
//            folder_thread.start();
//        }
//    }

    // Подключение к аккаунту
    private int connectToStore(Store store) {
//        System.err.println("Connect to store - " + emailAccount.getUser().getEmail());

        try {
//            System.out.println(store.isConnected());

            if (!store.isConnected()) {
                store.connect(
                    emailAccount.getUser().getHost(),
                    emailAccount.getUser().getEmail(),
                    emailAccount.getUser().getPassword()
                );
            }
//        } catch (javax.mail.AuthenticationFailedException | javax.mail.MessagingException e) {
        } catch (javax.mail.MessagingException e) {
            System.err.println("AuthenticationFailedException");
            db.updateSuccess(emailAccount.getUser().getEmail(), 0);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        db.updateSuccess(emailAccount.getUser().getEmail(), 1);

        return 1;
    }

    // Вывод сообщения
    public static void enterMessage(String text) {
        enterMessage("", text);
    }

    public static void enterMessage(String subject, String text) {
//        System.out.println(subject + " - " + text);
        wssChatClient.sendText(subject, text);
    }

    private void changeAccountStatus(String status) {
        emailAccount.setStatus(status);
        db.updateAccountStatus(emailAccount.getUser().getId(), status);
//        System.out.println(status);
    }

    private void changeAccountException(Exception exception) {
        String exception_text = exception.getMessage();

        emailAccount.setException(exception);
        db.updateAccountStatus(emailAccount.getUser().getId(), exception_text);
//        System.out.println(exception_text);
    }
}
