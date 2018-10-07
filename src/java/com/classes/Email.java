package com.classes;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.sql.Timestamp;
import java.util.Date;

public class Email {

    private int    id = 0;
    private String direction;
    private int    user_id;
    private int    client_id = 0;
    private long   uid;
    private String message_id;
    private int    msgno;
    private String from;
    private String to;

    private String cc;
    private String bcc;

    private String in_replay_to;
    private String references = "";
    private java.sql.Timestamp date;
    private int    size;
    private String subject;
    private String folder;
    private int    recent  = 0;
    private int    flagged = 0;
    private int    answred = 0;
    private int    deleted = 0;
    private int    seen    = 0;
    private int    draft   = 0;
    private java.sql.Timestamp update;

    public Email(User user, IMAPMessage imap_message, IMAPFolder imap_folder) {

        int client_id;

        try {

//            Address[] arr_cc  = message.getRecipients(Message.RecipientType.CC);
//            Address[] arr_bcc = message.getRecipients(Message.RecipientType.BCC);
//
//            System.err.println("------------------------ CC ------------------------");
//            if (arr_cc.length != 0) {
//                for (Address element : arr_cc) {
//                    System.err.println(element.toString());
//                }
//            }
//
//            System.err.println("------------------------ BCC ------------------------");
//            if (arr_bcc.length != 0) {
//                for (Address element : arr_bcc) {
//                    System.err.println(element.toString());
//                }
//            }
//
//            System.err.println();

            String to   = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.TO));
//            String cc   = InternetAddress.toString(message.getRecipients(Message.RecipientType.CC));
//            String bcc  = InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC));
            String from = InternetAddress.toString(imap_message.getFrom());

//            this.cc  = cc;
//            this.bcc = bcc;

            if (
                imap_message.getFrom() != null &&
                InternetAddress.toString(imap_message.getFrom()).contains(user.getEmail())
            ) {
                this.direction    = "out";
//                client_id = com.DB.getClientIDByAddress(to);
                from = user.getEmail(); // TODO
            } else {
                this.direction    = "in";
//                client_id = com.DB.getClientIDByAddress(from);
                to = user.getEmail();   // TODO
            }

            this.client_id = 0; // TODO client_id;

            if (!imap_folder.isOpen()) {
                try {
                    imap_folder.open(IMAPFolder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            IMAPFolder imap_folder_tmp = (IMAPFolder) imap_message.getFolder();

            this.uid          = imap_folder_tmp.getUID(imap_message);
            this.user_id      = user.getUser_id();
            this.message_id   = imap_message.getHeader("Message-ID")[0]
                                    .replace("<", "")
                                    .replace(">", "");
            this.msgno        = 0;
            this.from         = from;
            this.to           = (to == null ? "" : to);

            this.in_replay_to =  InternetAddress.toString(imap_message.getReplyTo());

            if (this.in_replay_to == null || this.in_replay_to.equals("")) {
                this.in_replay_to = " ";
            }

            this.date         = new java.sql.Timestamp(imap_message.getSentDate().getTime());
            this.size         = imap_message.getSize();

            this.subject      = removeBadChars(imap_message.getSubject());
            if (this.subject == null) { this.subject = " "; }

            this.folder       = imap_message.getFolder().getFullName();
            this.update       = new java.sql.Timestamp(new Date().getTime());

            if (imap_message.isSet(Flags.Flag.DELETED )) { this.deleted = 1; }
            if (imap_message.isSet(Flags.Flag.ANSWERED)) { this.answred = 1; }
            if (imap_message.isSet(Flags.Flag.DRAFT   )) { this.draft   = 1; }
            if (imap_message.isSet(Flags.Flag.FLAGGED )) { this.flagged = 1; }
            if (imap_message.isSet(Flags.Flag.RECENT  )) { this.recent  = 1; }
            if (imap_message.isSet(Flags.Flag.SEEN    )) { this.seen    = 1; }

//            String from = InternetAddress.toString(message.getFrom());
//            String reply-to =  InternetAddress.toString(message.getReplyTo());
//            String to = InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
//            String cc = InternetAddress.toString(message.getRecipients(Message.RecipientType.CC));
//            String bcc = InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC));
//            String Subject = message.getSubject();
//
//            Date sent = message.getSentDate();         // когда отправлено
//            Date received = message.getReceivedDate(); // когда получено

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "com.classes.Email {"                              + " \n" +
                "     id           = " + id           + ",\n" +
                "     direction    = " + direction    + ",\n" +
                "     user_id      = " + user_id      + ",\n" +
                "     client_id    = " + client_id    + ",\n" +
                "     uid          = " + uid          + ",\n" +
                "     message_id   = " + message_id   + ",\n" +
                "     msgno        = " + msgno        + ",\n" +
                "     from         = " + from         + ",\n" +
                "     to           = " + to           + ",\n" +
                "     in_replay_to = " + in_replay_to + ",\n" +
                "     references   = " + references   + ",\n" +
                "     date         = " + date         + ",\n" +
                "     size         = " + size         + ",\n" +
                "     subject      = " + subject      + ",\n" +
                "     folder       = " + folder       + ",\n" +
                "     recent       = " + recent       + ",\n" +
                "     flagged      = " + flagged      + ",\n" +
                "     answred      = " + answred      + ",\n" +
                "     deleted      = " + deleted      + ",\n" +
                "     seen         = " + seen         + ",\n" +
                "     draft        = " + draft        + ",\n" +
                "     update       = " + update       + " \n" +
                "}\n";
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public long getUid() {
        return uid;
    }

    public String getMessage_id() {
        return message_id;
    }

    public int getMsgno() {
        return msgno;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getIn_replay_to() {
        return in_replay_to;
    }

    public String getReferences() {
        return references;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getSize() {
        return size;
    }

    public String getSubject() {
        return subject;
    }

    public String getFolder() {
        return folder;
    }

    public int getRecent() {
        return recent;
    }

    public int getFlagged() {
        return flagged;
    }

    public int getAnswred() {
        return answred;
    }

    public int getDeleted() {
        return deleted;
    }

    public int getSeen() {
        return seen;
    }

    public int getDraft() {
        return draft;
    }

    public java.sql.Timestamp getUpdate() {
        return update;
    }

    public static String removeBadChars(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < s.length() ; i++){
            if (Character.isHighSurrogate(s.charAt(i))) continue;
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

}
