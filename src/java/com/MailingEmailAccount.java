package com;

import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.service.MyPrint;
import com.service.SettingsMail;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.threads.AddNewMessageThread;
import com.threads.ConnectToFolder;
import com.threads.MailListenerThread;
import com.wss.WSSChatClient;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MailingEmailAccount implements Runnable {

    private static DB db = Main.db;
    private static HashMap<Integer, EmailAccount> emailAccounts = Mailing.emailAccounts;
//    private static HashMap<String, HashMap<String, Thread>> threadMap = new HashMap<>();

    private EmailAccount emailAccount;
    private static WSSChatClient wssChatClient = Main.wssChatClient;

    public MailingEmailAccount(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    @Override
    public void run() {

        MyProperties myProperties = new MyProperties(emailAccount.getUser()); // Настройка подключение текущего пользователя

        Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
        session.setDebug(SettingsMail.getSession_debug());          // Включение дебага

        try {
//            Store store = session.getStore();
            Store store = session.getStore("imap");
            if (!connectToStore(store)) {
                return;
            }


            addStoreListeners(store);

            emailAccount.setStatus("end_add_message_emailAccount");
            System.out.println("end_add_message_emailAccount");

            IMAPFolder[] imap_folders = (IMAPFolder[]) store.getDefaultFolder().list("*"); // Получение списка папок лоя текушего подключения

            int i = 0;

            System.out.println("---------------------------------------------------");

            for (IMAPFolder imap_folder: imap_folders) {

//                if (imap_folder == null || !imap_folder.exists()) {
//                    System.out.println("Invalid folder");
//                }

//                System.out.println("1 - ");

                int tmp_i = 0;

//                System.out.println("1 - 1");

                ConnectToFolder connectToFolder = null;

                while ((connectToFolder == null || !connectToFolder.is_open) && tmp_i++ < 5) {
                    connectToFolder = new ConnectToFolder(imap_folder);
//                    System.out.println("1 - 2");

                    Thread connectToFolderThread = new Thread(connectToFolder);
//                    System.out.println("1 - 3");
                    connectToFolderThread.start();
//                    System.out.println("1 - 4");
                    Thread.sleep(1000);
//                    System.out.println("1 - 5");

                    if (connectToFolderThread.isAlive()) {
//                        System.out.println("1 - 6");
                        connectToFolderThread.interrupt();
                    }

//                    System.out.println("1 - 7");
                }

//                System.out.println("1 - 8");
//                System.out.println("2 - ");


                wssChatClient.sendText(imap_folder.getFullName());
                String text = "folder_name - " + imap_folder.getFullName() + " -- " + imap_folder.getMessageCount();

                System.out.println("\u001B[91m" + text + "\u001B[0m");
                i += imap_folder.getMessageCount();
            }

            System.out.println("sum = " + i);
            System.out.println("---------------------------------------------------");

            int folders_count = 0;
            int folders_count_all = imap_folders.length;

            for (IMAPFolder imap_folder: imap_folders) {
//                System.out.println("1");
                MyFolder myFolder = new MyFolder(imap_folder);
//                System.out.println("1");
                emailAccount.addMyFolder(myFolder);
//                System.out.println("3");
//                System.out.println("4");
                MailingEmailAccount.enterMessage("Connect to -> " + emailAccount.getUser().getEmail() + " -> " + imap_folder.getFullName());
//                System.out.println("5");
                Thread myTreadAllMails = new Thread(new AddNewMessageThread(emailAccount, myFolder)); // Создание потока для синхронизации всего почтового ящика // TODO old_messages
//                System.out.println("6");
                myFolder.setThreadAddNewMessages(myTreadAllMails);
//                System.out.println("7");
                myTreadAllMails.start(); // Запус потока
//                System.out.println("8");
                System.out.println("\u001B[91m" +"Folders count = " + (++folders_count) + " / " + folders_count_all + "\u001B[0m");
//                System.out.println("9");
//                if (SettingsMail.getWaitFolder()) {
//                    while (true) { // TODO
//                        if (myFolder.getStatus().equals("end_add_message_folder") || myFolder.getStatus().equals("stop")) {
//                            break;
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

//                Thread myThreadEvent = new Thread(new MailListenerThread(emailAccount, myFolder)); // Создание потока для отслеживания действий с определенной папкой // TODO listen
//                myFolder.setThreadLisaningChangeMessage(myThreadEvent);
//                myThreadEvent.start();
            }

//            emailAccount.setStatus("end_add_message_emailAccount");

            HashMap<String, MyFolder> myFolderMap_tmp = emailAccount.getFoldersMap();

            if (SettingsMail.getWaitUser()) {
                while (true) {
                    int n = 0;

                    for (Map.Entry<String, MyFolder> entry : myFolderMap_tmp.entrySet()) {
                        String status = entry.getValue().getStatus();
                        if (!(status.equals("listening") || status.equals("end_add_message_folder") || status.equals("close"))) {
                            System.out.println(entry.getValue().getFolder_name() + " - " + status);
                            n++;
                        }
                    }

                    String text = emailAccount.getEmailAddress() + " wait " + n;

                    System.out.println("\u001B[91m" + text + "\u001B[0m");

                    if (n == 0) {
                        break;
                    }
                    Thread.sleep(1000);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            enterMessage("Problems wish "  + emailAccount.getUser().getEmail());
            emailAccount.setStatus("stop");
            emailAccount.setException(e);
        } finally {
            emailAccount.setStatus("end_add_message_emailAccount");
            System.out.println("end_add_message_emailAccount");
        }
    }

    private void addStoreListeners(Store store) {
        store.addFolderListener(new FolderListener() { // Подключение отслеживания действий с папками в текущем подключении пользователя
            @Override
            public void folderCreated(FolderEvent folderEvent) { // Действие при создании папки
                try {
                    MailingEmailAccount.enterMessage("folder created");
                    IMAPFolder new_folder = (IMAPFolder) folderEvent.getFolder();

                    MyFolder myFolder = new MyFolder(new_folder);

                    emailAccount.addMyFolder(myFolder);

                    if (!new_folder.isOpen()) {
                        new_folder.open(IMAPFolder.READ_ONLY);
                    }

                    MailingEmailAccount.enterMessage("Connect to -> " + emailAccount.getUser().getEmail() + " -> " + new_folder.getFullName());

                    Thread myThreadEvent = new Thread(new MailListenerThread(emailAccount, myFolder));
                    myFolder.setThreadLisaningChangeMessage(myThreadEvent);
                    myThreadEvent.start();
                } catch (Exception e) {
                    emailAccount.setStatus("error");
                    emailAccount.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void folderDeleted(FolderEvent folderEvent) { // Действие при удалении папки

                MailingEmailAccount.enterMessage("folder deleted");

                try {
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

                } catch (MessagingException e) {
                    emailAccount.setStatus("error");
                    emailAccount.setException(e);
                    e.printStackTrace();
                }
                MailingEmailAccount.enterMessage("folder renamed");
            }
        });

        store.addStoreListener(storeEvent -> {
            MailingEmailAccount.enterMessage("store notification - " + storeEvent.getMessage());
            connectToStore(store);
        }); // TODO выжить любой ценой !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    } // Добавление слушалки на аккаунт

    private boolean connectToStore(Store store) {

        try {
            store.connect(
                emailAccount.getUser().getHost(),
                emailAccount.getUser().getEmail(),
                emailAccount.getUser().getPassword()
            );
        } catch (javax.mail.AuthenticationFailedException e) {
            db.updateSuccess(emailAccount.getUser().getEmail(), 0);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        db.updateSuccess(emailAccount.getUser().getEmail(), 1);

        return true;
    } // Подключение к аккаунту

    // Вывод сообщения
    public static void enterMessage(String text) {
        System.out.println(text);
        wssChatClient.sendText(text);
    }

	public void waitCommand() {
        Scanner in = new Scanner(System.in);
        String str_in;

        while (true) {
            System.out.println("---------------------------------------------------------------------------------");
            str_in = in.nextLine();

            String[] commands = str_in.split(" ");

            if (commands.length > 2) {

                switch (commands[0]) {
                    case "show":
                        switch (commands[1]) {
//                            case "user":
//                                switch (commands[2]) {
//                                    case "all":
//                                        System.out.println(users);
//                                        break;
//                                    case "id":
//                                        int id = Integer.parseInt(commands[3]);
//                                        for (User user : users) {
//                                            if (user.getId() == id) {
//                                                System.out.println(user);
//                                            }
//                                        }
//                                        break;
//                                    case "user_id":
//                                        int user_id = Integer.parseInt(commands[3]);
//                                        for (User user : users) {
//                                            if (user.getUser_id() == user_id) {
//                                                System.out.println(user);
//                                            }
//                                        }
//                                        break;
//                                    case "email":
//                                        String email = commands[3];
//                                        for (User user : users) {
//                                            if (user.getEmail().equals(email)) {
//                                                System.out.println(user);
//                                            }
//                                        }
//                                        break;
//                                    default:
//                                        System.out.println("show user error");
//                                        break;
//                                }
//                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                                        break;
                                    case "id":
                                        System.out.println(emailAccounts.get(commands[3]));
                                        break;
                                    default:
                                        System.out.println("show user error");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("show error");
                                break;
                        }
                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    case "start":
                        switch (commands[1]) {
                            case "user":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    case "id":
                                        break;
                                    case "user_id":
                                        break;
                                    case "email":
                                        break;
                                    default:
                                        System.out.println("start user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.out.println("start account error");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("start error");
                                break;
                        }
                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    case "stop":
                        switch (commands[1]) {
                            case "user":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    case "id":
                                        break;
                                    case "user_id":
                                        break;
                                    case "email":
                                        break;
                                    default:
                                        System.out.println("stop user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.out.println("stop account error");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("stop error");
                                break;
                        }
                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    case "add":
                        switch (commands[1]) {
                            case "user":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    case "id":
                                        break;
                                    case "user_id":
                                        break;
                                    case "email":
                                        break;
                                    default:
                                        System.out.println("add user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.out.println("add user error");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("add error");
                                break;
                        }
                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    case "delete":
                        switch (commands[1]) {
                            case "user":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    case "id":
                                        break;
                                    case "user_id":
                                        break;
                                    case "email":
                                        break;
                                    default:
                                        System.out.println("delete user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.out.println("delete user error");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("delete error");
                                break;
                        }
//                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    default:
                        System.out.println(str_in);
                        break;
                }

            } else {
                System.out.println("Enter command");
            }

            System.out.println("---------------------------------------------------------------------------------");

        }
    }

}
