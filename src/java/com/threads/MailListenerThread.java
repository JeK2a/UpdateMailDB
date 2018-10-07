package com.threads;

import com.DB;
import com.classes.Email;
import com.StartMail;
import com.classes.User;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.*;

public class MailListenerThread implements Runnable {

    private IMAPFolder imap_folder;
    private final ThreadLocal<Integer> thread_sleep_time = ThreadLocal.withInitial(() -> 1000);
    private DB db;
    private User user;

    public MailListenerThread(User user, IMAPFolder imap_folder) {
        this.user = user;
        this.imap_folder = imap_folder;
        db = new DB();
    }

    @Override
    public void run() {
        try {
            if (!imap_folder.isOpen()) {
                imap_folder.open(IMAPFolder.READ_ONLY);
            }
        } catch (MessagingException e) {
            System.err.println("Problem with email " + user.getEmail() + " / imap_folder " + imap_folder.getFullName());
            e.printStackTrace();
        }

        imap_folder.addConnectionListener(new ConnectionListener() {

            @Override
            public void opened(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection opened");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection disconnected");
                try {
                    imap_folder.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    System.err.println(
                            "Problem with email " + user.getEmail()
                                   + " / imap_folder " + imap_folder.getFullName()
                    );
                    e.printStackTrace();
                }
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection closed");
            }
        });


        imap_folder.addMessageChangedListener(new MessageChangedListener() {
            @Override
            public void messageChanged(MessageChangedEvent messageChangedEvent) {
                IMAPMessage imap_message = (IMAPMessage) messageChangedEvent.getMessage();
                Email email = new Email(user, imap_message, imap_folder);

                StartMail.enterMessage(email.toString());

                db.changeMessage(email);
                StartMail.enterMessage("messageChanged");
            }
        });

        imap_folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                for (Message message : messageCountEvent.getMessages()) {
                    db.addEmail(new Email(user, (IMAPMessage)  message, imap_folder));
                    StartMail.enterMessage("messagesAdded ");
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) { // TODO messagesRemoved
                for (Message message : messageCountEvent.getMessages()) {
                    db.changeMessage(new Email(user, (IMAPMessage) message, imap_folder));
                    StartMail.enterMessage("messagesRemoved");
                }
            }
        });

        try {
            while (true) {
                if (!imap_folder.isOpen()) {
                    imap_folder.open(Folder.READ_ONLY);
                    System.err.println("Folder close -> open");
                }
                Thread.sleep(thread_sleep_time.get());
            }
        } catch (InterruptedException | MessagingException e) {
            System.err.println("Problem with email " + user.getEmail() + " / imap_folder " + imap_folder.getFullName());
            e.printStackTrace();
        }

    }
}
