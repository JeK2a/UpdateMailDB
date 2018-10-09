package com.threads;

import com.DB;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;
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
            int messages_count_mail = imap_folder.getMessageCount();
            int messages_count_db   = db.getCountMessages(emailAccount.getUser().getUser_id(), myFolder.getFolder_name());

            System.err.println("messages_count_mail = " + messages_count_mail);
            System.err.println("messages_count_db   = " + messages_count_db);


            if (messages_count_mail < 1) {
                myEx = imap_folder.getFullName() + " - message count = " + messages_count_mail;
                System.err.println(myEx);
                return;
            }

            int part_count = (int) (Math.sqrt(messages_count_db) / 2);

            ArrayList<MyMessage> myMessages = db.getRandomMessages(
                    emailAccount.getUser().getUser_id(),
                    myFolder.getFolder_name(),
                    part_count
            );
            long[] uids = new long[part_count];
            int i = 0;

            for (MyMessage myMessage : myMessages) {
                System.err.println("Test =====" + myMessage.getUid());
                if (myMessage.getUid() > 0) {
                    uids[i++] = myMessage.getUid();
                }
            }

            Message[] messages_tmp = new Message[0];

            if (messages_count_db > 0) {
                messages_tmp = imap_folder.getMessagesByUID(uids);
            }

            int check_count = 0;

            i = 0;

            for (MyMessage myMessage : myMessages) {
                if (myMessage.compare((IMAPMessage) messages_tmp[i++], imap_folder)) {
                    check_count++;
                }
            }

            String    what;
            Message[] messages;

            what = (check_count == part_count) ? "NEW" : "ALL";
            System.out.println(check_count + " /// " +  part_count);
            what = "NEW";


            System.err.println(what);

            switch (what) {
                case "ALL":
                    db.setFlags(emailAccount.getUser().getUser_id(), imap_folder.getFullName());

//                    ANSWERED
//                    DELETED
//                    DRAFT
//                    FLAGGED
//                    RECENT
//                    SEEN
//                    USER

//                    IMAPMessage messages_no_seen[] = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

//                    folder.getPermanentFlags().contains(Flags.Flag.USER)

                    IMAPMessage[] imes = (IMAPMessage[]) imap_folder.getMessages();

//                    long[] muids = imap_folder.getui

                    IMAPMessage messages_answered[] = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.ANSWERED), true));
                    System.out.println("messages_answered = " + messages_answered.length);
                    IMAPMessage messages_deleter[]  = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.DELETED),  true));
                    System.out.println("messages_deleter = " + messages_deleter.length);
                    IMAPMessage messages_draft[]    = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.DRAFT),    true));
                    System.out.println("messages_draft = " + messages_draft.length);
                    IMAPMessage messages_flagged[]  = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.FLAGGED),  true));
                    System.out.println("messages_flagged = " + messages_flagged.length);
                    IMAPMessage messages_recent[]   = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.RECENT),   true));
                    System.out.println("messages_recent = " + messages_recent.length);
                    IMAPMessage messages_no_seen[]  = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN),     false));
                    System.out.println("messages_no_seen = " + messages_no_seen.length);
                    IMAPMessage messages_user[]     = (IMAPMessage[]) imap_folder.search(new FlagTerm(new Flags(Flags.Flag.USER),     true));
                    System.out.println("messages_user = " + messages_user.length);

                    if (messages_answered.length > 0) {
                        String messages_uid_answered = String.valueOf(imap_folder.getUID(messages_answered[0]));
                        for (int n = 1; n < messages_answered.length; n++) {
                            messages_uid_answered += "," + String.valueOf(imap_folder.getUID(messages_answered[n]));
                        }
                        System.out.println(messages_uid_answered);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "answered", 1, messages_uid_answered);
                    }
                    if (messages_deleter.length > 0) {
                        String messages_uid_deleter = String.valueOf(imap_folder.getUID(messages_deleter[0]));
                        for (int n = 1; n < messages_deleter.length; n++) {
    //                        messages_uid_deleter[n] = messages_a_uid_answered[n].uid;
                            messages_uid_deleter += "," + String.valueOf(imap_folder.getUID(messages_deleter[n]));
                        }
                        System.out.println(messages_uid_deleter);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "deleted", 1, messages_uid_deleter);
                    }
                    if (messages_draft.length > 0) {
                        String messages_uid_draft = String.valueOf(imap_folder.getUID(messages_draft[0]));
                        for (int n = 1; n < messages_draft.length; n++) {
                            messages_uid_draft += "," + String.valueOf(imap_folder.getUID(messages_draft[n]));
                        }
                        System.out.println(messages_uid_draft);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "draft", 1, messages_uid_draft);
                    }

                    if (messages_flagged.length > 0) {
                        String messages_uid_flagged = String.valueOf(imap_folder.getUID(messages_flagged[0]));
                        for (int n = 1; n < messages_flagged.length; n++) {
                            messages_uid_flagged += "," + String.valueOf(imap_folder.getUID(messages_flagged[n]));
                        }
                        System.out.println(messages_uid_flagged);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "flagged", 1, messages_uid_flagged);
                    }
                    if (messages_recent.length > 0) {
                        String messages_uid_recent = String.valueOf(imap_folder.getUID(messages_recent[0]));
                        for (int n = 1; n < messages_recent.length; n++) {
                            messages_uid_recent += "," + String.valueOf(imap_folder.getUID(messages_recent[n]));
                        }
                        System.out.println(messages_uid_recent);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "recent", 1, messages_uid_recent);
                    }

                    if (messages_no_seen.length > 0) {
                        String messages_uid_no_seen = String.valueOf(imap_folder.getUID(messages_no_seen[0]));
                        for (int n = 1; n < messages_no_seen.length; n++) {
                            messages_uid_no_seen += "," + String.valueOf(imap_folder.getUID(messages_no_seen[n]));
                        }
                        System.out.println(messages_uid_no_seen);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "seen", 0, messages_uid_no_seen);
                    }
                    if (messages_user.length > 0) {
                        String messages_uid_user = String.valueOf(imap_folder.getUID(messages_user[0]));

                        for (int n = 1; n < messages_user.length; n++) {
                            messages_uid_user += "," + String.valueOf(imap_folder.getUID(messages_user[n]));
                        }
                        System.out.println(messages_uid_user);
                        db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "user", 1, messages_uid_user);
                    }


//                    AppendUID[] messages_a_uid_answered = imap_folder.uid appendUIDMessages(messages_answered);
//                    System.out.println("messages_a_uid_answered = " + messages_a_uid_answered.length);
//                    AppendUID[] messages_a_uid_deleter  = imap_folder.appendUIDMessages(messages_deleter);
//                    System.out.println("messages_a_uid_deleter = " + messages_a_uid_deleter.length);
//                    AppendUID[] messages_a_uid_draft    = imap_folder.appendUIDMessages(messages_draft);
//                    System.out.println("messages_a_uid_draft = " + messages_a_uid_draft.length);
//                    AppendUID[] messages_a_uid_flagged  = imap_folder.appendUIDMessages(messages_flagged);
//                    System.out.println("messages_a_uid_flagged = " + messages_a_uid_flagged.length);
//                    AppendUID[] messages_a_uid_recent   = imap_folder.appendUIDMessages(messages_recent);
//                    System.out.println("messages_a_uid_recent = " + messages_a_uid_recent.length);
//                    AppendUID[] messages_a_uid_no_seen  = imap_folder.appendUIDMessages(messages_no_seen);
//                    System.out.println("messages_a_uid_no_seen = " + messages_a_uid_no_seen.length);
//                    AppendUID[] messages_a_uid_user     = imap_folder.appendUIDMessages(messages_user);
//                    System.out.println("messages_a_uid_user = " + messages_a_uid_user.length);


////                    long[] messages_uid_answered = new long[messages_a_uid_answered.length];
//                    StringBuilder messages_uid_answered = new StringBuilder(String.valueOf(messages_a_uid_answered[0].uid));
//                    for (int n = 1; n < messages_a_uid_answered.length; n++) {
////                        messages_uid_answered[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_answered.append(",").append(String.valueOf(messages_a_uid_answered[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "answered", 1, messages_uid_answered.toString());
//
////                    long[] messages_uid_deleter = new long[messages_a_uid_deleter.length];
//                    StringBuilder messages_uid_deleter = new StringBuilder(String.valueOf(messages_a_uid_deleter[0].uid));
//                    for (int n = 1; n < messages_a_uid_deleter.length; n++) {
////                        messages_uid_deleter[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_deleter.append(",").append(String.valueOf(messages_a_uid_deleter[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "deleted", 1, messages_uid_deleter.toString());
//
////                    long[] messages_uid_draft = new long[messages_a_uid_draft.length];
//                    StringBuilder messages_uid_draft = new StringBuilder(String.valueOf(messages_a_uid_draft[0].uid));
//                    for (int n = 1; n < messages_a_uid_draft.length; n++) {
////                        messages_uid_draft[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_draft.append(",").append(String.valueOf(messages_a_uid_draft[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "draft", 1, messages_uid_draft.toString());
//
////                    long[] messages_uid_flagged = new long[messages_a_uid_flagged.length];
//                    StringBuilder messages_uid_flagged = new StringBuilder(String.valueOf(messages_a_uid_flagged[0].uid));
//                    for (int n = 1; n < messages_a_uid_flagged.length; n++) {
////                        messages_uid_flagged[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_flagged.append(",").append(String.valueOf(messages_a_uid_flagged[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "flagged", 1, messages_uid_flagged.toString());
//
////                    long[] messages_uid_recent = new long[messages_a_uid_recent.length];
//                    StringBuilder messages_uid_recent = new StringBuilder(String.valueOf(messages_a_uid_recent[0].uid));
//                    for (int n = 1; n < messages_a_uid_recent.length; n++) {
////                        messages_uid_recent[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_recent.append(",").append(String.valueOf(messages_a_uid_recent[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "recent", 1, messages_uid_recent.toString());
//
////                    long[] messages_uid_no_seen = new long[messages_a_uid_no_seen.length];
//                    StringBuilder messages_uid_no_seen = new StringBuilder(String.valueOf(messages_a_uid_no_seen[0].uid));
//                    for (int n = 1; n < messages_a_uid_no_seen.length; n++) {
////                        messages_uid_no_seen[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_no_seen.append(",").append(String.valueOf(messages_a_uid_no_seen[n].uid));
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "seen", 0, messages_uid_no_seen.toString());
//
////                    long[] messages_uid_user = new long[messages_a_uid_user.length];
//                    String messages_uid_user = String.valueOf(messages_a_uid_user[0].uid);
//                    for (int n = 1; n < messages_a_uid_user.length; n++) {
////                        messages_uid_user[n] = messages_a_uid_answered[n].uid;
//                        messages_uid_user += "," + String.valueOf(messages_a_uid_user[n].uid);
//                    }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "user", 1, messages_uid_user.toString());

                    messages = imap_folder.getMessages();

                    break;
                case "NEW":

                    if (messages_count_db > 0) {

                        IMAPMessage last_imap_message = (IMAPMessage) imap_folder.getMessage(messages_count_mail);
                        long mail_last_uid = imap_folder.getUID(last_imap_message);
                        long db_last_uid = db.getLastUID(emailAccount.getUser().getUser_id(), imap_folder.getFullName());

                        messages = imap_folder.getMessagesByUID(db_last_uid + 1, mail_last_uid);
//                    System.out.println(messages.length + " из " + imap_folder.getMessages().length);
                    } else {
                        messages = imap_folder.getMessages();
                    }
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
