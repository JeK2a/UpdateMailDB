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
import java.util.Scanner;

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
						//TODO ilya начать слушать эту папку
                        StartMail.enterMessage("folder created");
                        IMAPFolder new_folder = (IMAPFolder) folderEvent.getFolder();

                        MyFolder myFolder = new MyFolder(new_folder);

                        emailAccount.addMyFolder(myFolder);

                        if (!new_folder.isOpen()) {
                            new_folder.open(IMAPFolder.READ_ONLY);
                        }

                        StartMail.enterMessage("Connect to -> " + emailAccount.getUser().getEmail() + " -> " + new_folder.getFullName());

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

                    StartMail.enterMessage("folder deleted");

                    try {
                        IMAPMessage[] messages = (IMAPMessage[]) folderEvent.getFolder().getMessages();

                        for (IMAPMessage imap_message : messages) {
                            db.setDeleteFlag(new Email(emailAccount.getUser(), imap_message, (IMAPFolder) folderEvent.getFolder()), (IMAPFolder) folderEvent.getFolder()); // TODO изменение флага сообщенией на удаленное (проверить)
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

//        Console console = System.console();
        Scanner in = new Scanner(System.in);
        String str_in;

        while (true) {
            System.out.println("---------------------------------------------------------------------------------");
//            MyPrint.printArrayList(emailAccounts);
//            System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
            str_in = in.nextLine();

            String[] commands = str_in.split(" ");

            if (commands.length > 2) {

                switch (commands[0]) {
                    case "show":
                        switch (commands[1]) {
                            case "user":
                                switch (commands[2]) {
                                    case "all":
                                        System.out.println(users);
                                        break;
                                    case "id":
                                        int id = Integer.parseInt(commands[3]);
                                        for (User user : users) {
                                            if (user.getId() == id) {
                                                System.out.println(user);
                                            }
                                        }
                                        break;
                                    case "user_id":
                                        int user_id = Integer.parseInt(commands[3]);
                                        for (User user : users) {
                                            if (user.getUser_id() == user_id) {
                                                System.out.println(user);
                                            }
                                        }
                                        break;
                                    case "email":
                                        String email = commands[3];
                                        for (User user : users) {
                                            if (user.getEmail().equals(email)) {
                                                System.out.println(user);
                                            }
                                        }
                                        break;
                                    default:
                                        System.err.println("show user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                                        break;
                                    case "id":
                                        System.out.println(emailAccounts.get(commands[3]));
                                        break;
                                    default:
                                        System.err.println("show user error");
                                        break;
                                }
                                break;
                            default:
                                System.err.println("show error");
                                break;
                        }
//                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
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
                                        System.err.println("start user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.err.println("start account error");
                                        break;
                                }
                                break;
                            default:
                                System.err.println("start error");
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
                                        System.err.println("stop user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.err.println("stop account error");
                                        break;
                                }
                                break;
                            default:
                                System.err.println("stop error");
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
                                        System.err.println("add user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.err.println("add user error");
                                        break;
                                }
                                break;
                            default:
                                System.err.println("add error");
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
                                        System.err.println("delete user error");
                                        break;
                                }
                                break;
                            case "account":
                                switch (commands[2]) {
                                    case "all":
                                        break;
                                    default:
                                        System.err.println("delete user error");
                                        break;
                                }
                                break;
                            default:
                                System.err.println("delete error");
                                break;
                        }
//                        System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                        break;
                    default:
                        System.out.println(str_in);
                        break;
                }

            } else {
                System.err.println("Enter command");
            }



            System.out.println("---------------------------------------------------------------------------------");

//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
	}
}
