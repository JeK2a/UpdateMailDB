package com.threads;

import com.DB;
import com.StartMail;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.User;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.*;
import java.sql.Timestamp;
import java.util.Date;

public class MailListenerThread implements Runnable {

    private IMAPFolder imap_folder;
    private final ThreadLocal<Integer> thread_sleep_time = ThreadLocal.withInitial(() -> 1000);
    private DB db;
    private String email;

    private EmailAccount emailAccount;
    private User user;
    private MyFolder myFolder;

    public MailListenerThread(EmailAccount emailAccount, MyFolder myFolder) {
        db = new DB();
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
                    StartMail.enterMessage("Connection opened");
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection disconnected");
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
                    StartMail.enterMessage("Connection closed");
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }
        });


        imap_folder.addMessageChangedListener(new MessageChangedListener() {
            @Override
            public void messageChanged(MessageChangedEvent messageChangedEvent) {
                try {
                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();

                    IMAPMessage imap_message = (IMAPMessage) messageChangedEvent.getMessage();
                    Email email = new Email(user, imap_message, imap_folder);

                    StartMail.enterMessage(email.toString());

                    db.addEmail(email);
                    StartMail.enterMessage("messageChanged");
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }
        });

        imap_folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                try {
                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
                    myFolder.eventCountIncriminate();
                    StartMail.enterMessage("messagesAdded");

                    for (Message message : messageCountEvent.getMessages()) {
                        db.addEmail(new Email(user, (IMAPMessage) message, imap_folder));
                    }
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                //todo add removed to db
                try {
                    for (Message message : messageCountEvent.getMessages()) {
                        db.setDeleteFlag(user.getEmail(), imap_folder.getFullName(), message.getHeader("Message-ID")[0]);
                        StartMail.enterMessage("messagesRemoved");
                    }
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                }
            }
        });

        myFolder.setStatus("listening");

        try {
            while (true) {
                if (!imap_folder.isOpen()) {
                    imap_folder.open(Folder.READ_ONLY);
                    System.err.println("Folder close -> open");
                }
                Thread.sleep(thread_sleep_time.get());
            }
        } catch (InterruptedException | MessagingException e) {
            System.err.println("Problem with email " + email + " / imap_folder " + imap_folder.getFullName());
            myFolder.setStatus("error");
            myFolder.setException(e);
            e.printStackTrace();
        }

    }
}
