package com;

import com.classes.EmailAccount;
import com.classes.User;
import com.service.MyPrint;
import com.service.Settings;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Session;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.HashMap;

public class StartMailTest {

    private static DB db;

    private static HashMap<String, HashMap<String, Thread>> threadMap = new HashMap<>();

    private static WSSChatClient wssChatClient;
    private static HashMap<Integer, EmailAccount> emailAccounts = new HashMap<>();

    private StartMailTest() {

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
//
            imapFolder.open(IMAPFolder.READ_ONLY);
//
//            long start = System.nanoTime();

//            Message[] messages = imapFolder.getMessages();

            System.out.println("--------------------------------------Test--------------------------------------");
//            IMAPMessage messages_flagged[]  = (IMAPMessage[]) imapFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN),  true));
//            System.out.println("--------------------------------------Test--------------------------------------");
////            IMAPMessage messages_flagged[]  = (IMAPMessage[]) imapFolder.doCommand(imapProtocol ->  "SEARCH SEEN ALL");
////            IMAPMessage messages_flagged[]  = (IMAPMessage[]) imapFolder.si doCommand(imapProtocol ->  "SEARCH SEEN ALL");
//
//            imapFolder.doCommand(imapProtocol -> {
//                try {
//                    imapProtocol .simpleCommand("UID SEARCH 117100:117120 SEEN", null);
//                } catch (com.sun.mail.iap.ProtocolException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });
//
            System.out.println("--------------------------------------Test--------------------------------------");
//            imapFolder.doCommand(imapProtocol -> {
//                try {
////                    imapProtocol.simpleCommand("UID SEARCH UID 117100:117120 KEYWORD $label1", null);
//                    imapProtocol.simpleCommand("UID SEARCH UID 117100:117120 KEYWORD $label1", null);
//                } catch (com.sun.mail.iap.ProtocolException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });
            System.out.println("--------------------------------------Test--------------------------------------");
//            imapFolder.doCommand(imapProtocol -> {
//                try {
//                    imapProtocol.simpleCommand("UID SEARCH UID 117100:117120 FLAGGED", null);
//                } catch (com.sun.mail.iap.ProtocolException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });
//            System.out.println("--------------------------------------Test--------------------------------------");
//
//            imapFolder.doCommand(imapProtocol -> {
//                try {
//                    imapProtocol.simpleCommand("UID FETCH 117100:117120 FLAGS", null);
//                } catch (com.sun.mail.iap.ProtocolException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            });

            System.out.println("--------------------------------------Test--------------------------------------");



            String str = (String) imapFolder.doCommand(imapProtocol -> {
                Response[] responses = imapProtocol.command("FETCH 117101 (FLAGS UID)", null);
                return responses[0].toString();
            });


            System.out.println(str);
            System.exit(0);


            imapFolder.doCommand(imapProtocol -> {

//                Argument args = new Argument();

//                args.writeString("117101");
//                args.writeString("FLAGS");
//                args.writeString("UID");
//                args.writeString("UID");
//                args.writeString(Long.toString(117100) + ":" + Long.toString(117120));
//                args.writeString("KEYWORD $label1");
//                args.writeString("BODY[]");

//                Response[] responses = imapProtocol.command("UID SEARCH UID 117100:117120 KEYWORD $label1", null);
//                Response[] responses = imapProtocol.command("UID SEARCH UID 117100:117120 KEYWORD $label1", null);

                Response[] responses = imapProtocol.command("FETCH 117101 (FLAGS UID)", null);
//                String out = responses[0].toString();
//                System.out.println("Out: " + out);

                for (Response response : responses) {
                    System.out.println("Out: " + response);
                }

                return null;
            });

            System.out.println("--------------------------------------Test--------------------------------------");
//            System.out.println("messages_flagged = " + messages_flagged.length);

//            imapFolder.uid

//            start = System.nanoTime();


//            start = System.nanoTime();
//            AppendUID[] tmp = imapFolder.appendUIDMessages() appendUIDMessages(messages_flagged);
//            System.out.println("Time = " + (System.nanoTime() - start) / 1000);

//            UIDFolder uf = imapFolder;
//
//            long[] uid = new long[6];
//
//            start = System.nanoTime();
//            for (int i=0; i< 6; i++) {
//                 uid[i] = uf.getUID(messages_flagged[i]);
//            }
//            System.out.println("Time = " + (System.nanoTime() - start) / 1000);

            System.exit(0);

//            if (messages_flagged.length > 0) {

//            System.out.println(messages_flagged[0]);

//                for (int n = 1; n < messages_flagged.length; n++) {
//
//
//
////                    messages_uid_flagged += "," + String.valueOf(imap_folder.getUID(messages_flagged[n]));
//                }
//                System.out.println(messages_uid_flagged);
////                db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "flagged", 1, messages_uid_flagged);
////            }


//            System.out.println((System.nanoTime() - start) / 1000);


        } catch (Exception e) {
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
        StartMailTest startMail = new StartMailTest(); //

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
