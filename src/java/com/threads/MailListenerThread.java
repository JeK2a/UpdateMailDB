package com.threads;

import com.DB;
import com.MailingEmailAccount;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.User;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.event.*;
import java.sql.Timestamp;
import java.util.Date;

public class MailListenerThread implements Runnable {

    private IMAPFolder imap_folder;
    private final ThreadLocal<Integer> thread_sleep_time = ThreadLocal.withInitial(() -> 1000);
    private static DB db;
    private String email;

    private EmailAccount emailAccount;
    private User user;
    private MyFolder myFolder;

    public MailListenerThread(EmailAccount emailAccount, MyFolder myFolder) {
        if (db == null) {
            db = new DB();
        }
        this.emailAccount = emailAccount;
        this.user         = emailAccount.getUser();
        this.email        = emailAccount.getUser().getEmail();
        this.myFolder     = myFolder;
        this.imap_folder  = myFolder.getImap_folder();
    }

    @Override
    public void run() {
        try {
            if (!imap_folder.isOpen()) {
                imap_folder.open(IMAPFolder.READ_ONLY);
                myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                myFolder.eventCountIncriminate();
            }
        } catch (MessagingException e) {
            System.err.println("Problem with email " + email + " / imap_folder " + imap_folder.getFullName());
            myFolder.setStatus("error");
            myFolder.setException(e);
            e.printStackTrace();
        }

        imap_folder.addConnectionListener(new ConnectionListener() {

            @Override
            public void opened(ConnectionEvent connectionEvent) {
                try {
                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();
                    MailingEmailAccount.enterMessage("Connection opened");
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                MailingEmailAccount.enterMessage("Connection disconnected");
                try {
                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();
                    imap_folder.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    System.err.println("Problem with email " + email + " / imap_folder " + imap_folder.getFullName());
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                try {
                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();
                    MailingEmailAccount.enterMessage("Connection closed");
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }
        });

        imap_folder.addMessageChangedListener(messageChangedEvent -> {
            try {

                int user_id          = emailAccount.getUser().getUser_id();
                String email_address = emailAccount.getEmailAddress();
                String folder_name   = myFolder.getFolder_name();

                myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                myFolder.eventCountIncriminate();

                IMAPMessage imap_message = (IMAPMessage) messageChangedEvent.getMessage();
//                    long uid = imap_folder.getUID(imap_message);
                Email email = new Email(user_id, email_address, imap_message, folder_name, 0, imap_folder);

                MailingEmailAccount.enterMessage(email.toString());

                if (db.addEmail(email)) {
                    db.updateFolderLastAddUID(email, email_address);
                }
                MailingEmailAccount.enterMessage("messageChanged");
            } catch (Exception e) {
                myFolder.setStatus("error");
                myFolder.setException(e);
                e.printStackTrace();
            }
        });

        imap_folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                try {

                    int user_id          = emailAccount.getUser().getUser_id();
                    String email_address = emailAccount.getEmailAddress();
                    String folder_name   = myFolder.getFolder_name();

                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();
                    MailingEmailAccount.enterMessage("messagesAdded");

                    for (Message message : messageCountEvent.getMessages()) {
                        Email email = new Email(user_id, email_address, message, folder_name, 0, imap_folder);

                        if (db.addEmail(email)) {
                            db.updateFolderLastAddUID(email, email_address);
                        }
                    }
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                //todo add removed to db TODO!!!!!
                System.out.println("messagesRemoved!!!");

//                try {
//                    for (Message message : messageCountEvent.getMessages()) {
//                        db.setDeleteFlag(user.getEmail(), imap_folder.getFullName(), message.getHeader("Message-ID")[0]);
//                        MailingEmailAccount.enterMessage("messagesRemoved");
//                    }
//                } catch (Exception e) {
//                    myFolder.setStatus("error");
//                    myFolder.setException(e);
//                    e.printStackTrace();
//                }
            }
        });


//        Message[] messages = new Message[0];
//        try {
//            messages = imap_folder.getMessages();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//        FetchProfile fp = new FetchProfile();
//        fp.add(FetchProfile.Item.ENVELOPE); // From, To, Cc, Bcc, ReplyTo, Subject and Date   // 31,57 // 132 // 0+59
//        fp.add(FetchProfile.Item.CONTENT_INFO); // ContentType, ContentDisposition, ContentDescription, Size and LineCount  // 206 (122 -flags) // + 91 // 0+79
////                    fp.add(FetchProfile.Item.SIZE); // Ограничение по объему предварительно загруженных писем  // count 3943
////                    fp.add(FetchProfile.Item.FLAGS); //   // 163 // +43 // 0+8
//        fp.add(UIDFolder.FetchProfileItem.UID); // 0+1
//        fp.add("Message-ID"); // 0+19
//        fp.add("X-Tdfid"); // 0+38
//
//        System.err.println("Fetch start");
//        long start = System.currentTimeMillis();
//        try {
//            imap_folder.fetch(messages, fp);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        long finish = System.currentTimeMillis();
//        System.err.println("Fetch end");
//        System.err.println("Test speed fetch - " + (finish - start));
//
////                    12:33:56
////                    13:41:21
//
//
//        if (!imap_folder.isOpen()) {
//            try {
//                imap_folder.open(IMAPFolder.READ_ONLY);
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//        }



//        myFolder.setStatus("listening");

        try {
//            while (true) {
//                if (!imap_folder.isOpen()) {
//                    imap_folder.open(Folder.READ_ONLY);
//                    System.err.println("Folder close -> open");
//                }
//                Thread.sleep(thread_sleep_time.get());
//            }

            Thread connectToFolderThread;

            while (true) {
                if (!imap_folder.isOpen()) {
                    connectToFolderThread = new Thread(new ConnectToFolder(imap_folder));
                    connectToFolderThread.start();
                    Thread.sleep(1000);

                    if (connectToFolderThread.isAlive()) {
                        connectToFolderThread.interrupt();
                    }
                } else {
                    Thread.sleep(thread_sleep_time.get());
                }

            }

        } catch (InterruptedException e) {
            System.err.println("Problem with email " + email + " / imap_folder " + imap_folder.getFullName());
            myFolder.setStatus("error");
            myFolder.setException(e);
            e.printStackTrace();
        }

    }
}
