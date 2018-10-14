package com.classes;

import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
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
    private int    user    = 0;

    private int    label1  = 0;
    private int    label2  = 0;
    private int    label3  = 0;
    private int    label4  = 0;
    private int    label5  = 0;
    private int    has_attachment = 0;

    private java.sql.Timestamp update;

    public Email(User user, IMAPMessage imap_message, IMAPFolder imap_folder) {

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

//            if (imap_message.getFrom() != null && InternetAddress.toString(imap_message.getFrom()).contains(user.getEmail())
            this.direction = (imap_folder.getFullName().equals("Исходящие") ? "out" : "in");

            //            String cc   = InternetAddress.toString(message.getRecipients(Message.RecipientType.CC));
            //            String bcc  = InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC));
            //            this.cc  = cc;
            //            this.bcc = bcc;
            this.from = InternetAddress.toString(imap_message.getFrom());
            this.to   = InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.TO));

            if (!imap_folder.isOpen()) {
                try {
                    imap_folder.open(IMAPFolder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            IMAPFolder imap_folder_tmp = (IMAPFolder) imap_message.getFolder();

            this.user_id      = user.getUser_id();
//            this.message_id   = imap_message.getHeader("Message-ID")[0].replace("<", "").replace(">", "");
            this.message_id = imap_message.getMessageID();
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

//            if (imap_message.isSet(Flags.Flag.DELETED )) { this.deleted = 1; }
//            if (imap_message.isSet(Flags.Flag.ANSWERED)) { this.answred = 1; }
//            if (imap_message.isSet(Flags.Flag.DRAFT   )) { this.draft   = 1; }
//            if (imap_message.isSet(Flags.Flag.FLAGGED )) { this.flagged = 1; }
//            if (imap_message.isSet(Flags.Flag.RECENT  )) { this.recent  = 1; }
//            if (imap_message.isSet(Flags.Flag.SEEN    )) { this.seen    = 1; }
//            if (imap_message.isSet(Flags.Flag.USER    )) { this.user    = 1; }

            String out = (String) imap_folder.doCommand(imapProtocol -> {
                Response[] responses = imapProtocol.command("FETCH " + imap_message.getMessageNumber() + " (FLAGS UID)", null);
                return responses[0].toString();
            });

            if (out.contains("\\Deleted"))     { this.deleted = 1; }
            if (out.contains("\\Answered"))    { this.answred = 1; }
            if (out.contains("\\Draft"))       { this.draft   = 1; }
            if (out.contains("\\Flagged"))     { this.flagged = 1; }
            if (out.contains("\\Recent"))      { this.recent  = 1; }
            if (out.contains("\\Seen"))        { this.seen    = 1; }
            if (out.contains("\\User"))        { this.user    = 1; }
            if (out.contains("$label1"))       { this.label1  = 1; }
            if (out.contains("$label2"))       { this.label2  = 1; }
            if (out.contains("$label3"))       { this.label3  = 1; }
            if (out.contains("$label4"))       { this.label4  = 1; }
            if (out.contains("$label5"))       { this.label5  = 1; }
            if (out.contains("HasAttachment")) { this.has_attachment = 1; }

            String[] out_str = out.split(" ");

//            if (out.length() > 3) {
                this.uid = Long.parseLong(out_str[4]);
//            } else {
//                this.uid = imap_folder.getUID(imap_message);
//            }

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
        return "Email {"                                  + " \n" +
                "     id             = " + id             + ",\n" +
                "     direction      = " + direction      + ",\n" +
                "     user_id        = " + user_id        + ",\n" +
                "     client_id      = " + client_id      + ",\n" +
                "     uid            = " + uid            + ",\n" +
                "     message_id     = " + message_id     + ",\n" +
                "     msgno          = " + msgno          + ",\n" +
                "     from           = " + from           + ",\n" +
                "     to             = " + to             + ",\n" +
                "     in_replay_to   = " + in_replay_to   + ",\n" +
                "     references     = " + references     + ",\n" +
                "     date           = " + date           + ",\n" +
                "     size           = " + size           + ",\n" +
                "     subject        = " + subject        + ",\n" +
                "     folder         = " + folder         + ",\n" +
                "     recent         = " + recent         + ",\n" +
                "     flagged        = " + flagged        + ",\n" +
                "     answred        = " + answred        + ",\n" +
                "     deleted        = " + deleted        + ",\n" +
                "     seen           = " + seen           + ",\n" +
                "     draft          = " + draft          + ",\n" +
                "     user           = " + user           + ",\n" +
                "     label1         = " + label1         + ",\n" +
                "     label2         = " + label2         + ",\n" +
                "     label3         = " + label3         + ",\n" +
                "     label4         = " + label4         + ",\n" +
                "     label5         = " + label5         + ",\n" +
                "     has_attachment = " + has_attachment + ",\n" +
                "     update         = " + update         + " \n" +
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

    public void setId(int id) {
        this.id = id;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setMsgno(int msgno) {
        this.msgno = msgno;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setIn_replay_to(String in_replay_to) {
        this.in_replay_to = in_replay_to;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public void setFlagged(int flagged) {
        this.flagged = flagged;
    }

    public void setAnswred(int answred) {
        this.answred = answred;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getLabel1() {
        return label1;
    }

    public void setLabel1(int label1) {
        this.label1 = label1;
    }

    public int getLabel2() {
        return label2;
    }

    public void setLabel2(int label2) {
        this.label2 = label2;
    }

    public int getLabel3() {
        return label3;
    }

    public void setLabel3(int label3) {
        this.label3 = label3;
    }

    public int getLabel4() {
        return label4;
    }

    public void setLabel4(int label4) {
        this.label4 = label4;
    }

    public int getLabel5() {
        return label5;
    }

    public void setLabel5(int label5) {
        this.label5 = label5;
    }

    public int getHas_attachment() {
        return has_attachment;
    }

    public void setHas_attachment(int has_attachment) {
        this.has_attachment = has_attachment;
    }

    public void setUpdate(Timestamp update) {
        this.update = update;
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
