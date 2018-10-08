package com.classes;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.sql.Timestamp;
import java.util.Objects;

public class MyMessage {
    private int       id;
    private String    direction;
    private int       user_id;
    private int       client_id;
    private long      uid;
    private String    message_id;
    private int       msgno;
    private String    from;
    private String    to;
    private String    in_reply_to;
    private String    references;
    private Timestamp date;
    private int       size;
    private String    subject;
    private String    folder;
    private int       recent;
    private int       flagged;
    private int       answered;
    private int       deleted;
    private int       seen;
    private int       draft;
    private int       user;
    private Timestamp udate;

    public MyMessage(
            int id,
            String direction,
            int user_id,
            int client_id,
            long uid,
            String message_id,
            int msgno,
            String from,
            String to,
            String in_reply_to,
            String references,
            Timestamp date,
            int size,
            String subject,
            String folder,
            int recent,
            int flagged,
            int answered,
            int deleted,
            int seen,
            int draft,
            int user,
            Timestamp udate
    ) {
        this.id = id;
        this.direction   = direction;
        this.user_id     = user_id;
        this.client_id   = client_id;
        this.uid         = uid;
        this.message_id  = message_id;
        this.msgno       = msgno;
        this.from        = from;
        this.to          = to;
        this.in_reply_to = in_reply_to;
        this.references  = references;
        this.date        =  date;
        this.size        = size;
        this.subject     = subject;
        this.folder      = folder;
        this.recent      = recent;
        this.flagged     = flagged;
        this.answered    = answered;
        this.deleted     = deleted;
        this.seen        = seen;
        this.draft       = draft;
        this.user        = user;
        this.udate       = udate;
    }

    public MyMessage(Message message) {
//        this.id = id;
//        this.direction = direction;
//        this.user_id = user_id;
//        this.client_id = client_id;
//        this.uid = uid;
//        this.message_id = message_id;
//        this.msgno = msgno;
//        this.from = from;
//        this.to = to;
//        this.in_reply_to = in_reply_to;
//        this.references = references;
//        this.date = date;
//        this.size = size;
//        this.subject = subject;
//        this.folder = folder;
//        this.recent = recent;
//        this.flagged = flagged;
//        this.answered = answered;
//        this.deleted = deleted;
//        this.seen = seen;
//        this.draft = draft;
//        this.udate = udate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMessage myMessage = (MyMessage) o;
        return size == myMessage.size &&
                recent == myMessage.recent &&
                flagged == myMessage.flagged &&
                answered == myMessage.answered &&
                deleted == myMessage.deleted &&
                seen == myMessage.seen &&
                draft == myMessage.draft &&
                Objects.equals(message_id, myMessage.message_id) &&
                Objects.equals(from, myMessage.from) &&
                Objects.equals(to, myMessage.to) &&
                Objects.equals(in_reply_to, myMessage.in_reply_to) &&
                Objects.equals(references, myMessage.references) &&
                Objects.equals(date, myMessage.date) &&
                Objects.equals(subject, myMessage.subject) &&
                Objects.equals(folder, myMessage.folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message_id, from, to, in_reply_to, references, date,
                size, subject, folder, recent, flagged, answered, deleted, seen, draft);
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "id=" + id +
                ", direction='" + direction + '\'' +
                ", user_id=" + user_id +
                ", client_id=" + client_id +
                ", uid=" + uid +
                ", message_id='" + message_id + '\'' +
                ", msgno=" + msgno +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", in_reply_to='" + in_reply_to + '\'' +
                ", references='" + references + '\'' +
                ", date=" + date +
                ", size=" + size +
                ", subject='" + subject + '\'' +
                ", folder='" + folder + '\'' +
                ", recent=" + recent +
                ", flagged=" + flagged +
                ", answered=" + answered +
                ", deleted=" + deleted +
                ", seen=" + seen +
                ", draft=" + draft +
                ", user=" + user +
                ", udate=" + udate +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public int getMsgno() {
        return msgno;
    }

    public void setMsgno(int msgno) {
        this.msgno = msgno;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getIn_reply_to() {
        return in_reply_to;
    }

    public void setIn_reply_to(String in_reply_to) {
        this.in_reply_to = in_reply_to;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getRecent() {
        return recent;
    }

    public void setRecent(int recent) {
        this.recent = recent;
    }

    public int getFlagged() {
        return flagged;
    }

    public void setFlagged(int flagged) {
        this.flagged = flagged;
    }

    public int getAnswered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getDraft() {
        return draft;
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

    public Timestamp getUdate() {
        return udate;
    }

    public void setUdate(Timestamp udate) {
        this.udate = udate;
    }

    public boolean compare(IMAPMessage imap_message, IMAPFolder imapFolder) {
        try {
            if (
                this.getUid()    ==  imapFolder.getUID(imap_message) &&
                this.message_id  == imap_message.getMessageID() &&
                this.from        == InternetAddress.toString(imap_message.getFrom()) &&
                this.to          == InternetAddress.toString(imap_message.getRecipients(Message.RecipientType.TO)) &&
                this.in_reply_to == InternetAddress.toString(imap_message.getReplyTo()) &&
                this.size        == imap_message.getSize() &&
                this.subject     == imap_message.getSubject() &&
                this.folder      == imap_message.getFolder().getFullName()
            ) {
                return true;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
