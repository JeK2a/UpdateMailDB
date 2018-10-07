package com.threads;

import com.DB;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;

public class AddNewMessageThread implements Runnable {

    private DB db;
//    private User user;
    private EmailAccount emailAccount;
    private IMAPFolder imap_folder;
    private MyFolder myFolder;

    private String myEx;


    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder) {
        db = new DB();
        this.emailAccount = emailAccount;
        this.myFolder     = myFolder;
        this.imap_folder  = myFolder.getImap_folder();
    }

    @Override
    public void run() {
        try {
            int messages_count = imap_folder.getMessageCount();

            if (messages_count < 1) {
                myEx = imap_folder.getFullName() + " - message count = " + messages_count;
                System.err.println(myEx);
                return;
            }

            int part_count = (int) (Math.sqrt(messages_count) / 2);

            ArrayList<MyMessage> myMessages = db.getRandomMessages(emailAccount.getUser().getUser_id(),
                    myFolder.getFolder_name(), part_count);
            long[] uids = new long[part_count];
            int i = 0;

            System.out.println(" size = " +myMessages.size());

            for (MyMessage myMessage : myMessages) {
                uids[++i] = myMessage.getUid();
            }

            Message[] messages_tmp = imap_folder.getMessagesByUID(uids);

            int check_count = 0;

            i = 0;

            for (MyMessage myMessage : myMessages) {

                System.out.println(myMessages);

                if (myMessage.compare(messages_tmp[++i])) {
                    check_count++;
                }
            }

            String    what;
            Message[] messages;

            what = (check_count == part_count) ? "NEW" : "ALL";

            switch (what) {
                case "NEW":
                    IMAPMessage last_imap_message = (IMAPMessage) imap_folder.getMessage(messages_count);
                    long mail_last_uid = imap_folder.getUID(last_imap_message);
                    long db_last_uid   = db.getLastUID(emailAccount.getUser().getUser_id(), imap_folder.getFullName());

                    messages = imap_folder.getMessagesByUID(db_last_uid + 1, mail_last_uid);
//                    System.out.println(messages.length + " из " + imap_folder.getMessages().length);
                    break;
                case "ALL":
                    messages = imap_folder.getMessages();
                    break;
                default:
                    myFolder.setStatus("error");
                    return;
            }

            for (Message message : messages) {
                if (!imap_folder.isOpen()) {
                    imap_folder.open(IMAPFolder.READ_ONLY);
                }
                db.addEmail(new Email(emailAccount.getUser(), (IMAPMessage) message, imap_folder));
            }
        } catch (MessagingException e) {
            myFolder.setException(e);
            e.printStackTrace();
        }
    }
}
