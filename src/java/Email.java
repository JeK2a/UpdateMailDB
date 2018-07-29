import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.sql.Timestamp;
import java.util.Date;

public class Email {

    private int    id = 0;
    private String direction;
    private int    user_id;
    private int    client_id;
    private int    uid;
    private String message_id;
    private int    msgno;
    private String from;
    private String to;
    private String in_replay_to;
    private String references = "";
    private Timestamp date;
    private int    size    = 0;
    private String subject = "";
    private String folder  = "";
    private int    recent  = 0;
    private int    flagged = 0;
    private int    answred = 0;
    private int    deleted = 0;
    private int    seen    = 0;
    private int    draft   = 0;
    private Timestamp update;

    public Email(User user, Message message) {
        try {
            if (InternetAddress.toString(message.getFrom()).equals(user.getEmail())) {
                this.direction    = "out";
            } else {
                this.direction    = "in";
            }
            this.client_id    = 0; // TODO client_id from DB a_1c_client_emails or a_ex_client_emails
            this.uid          = 0; // TODO uid  ???
            this.user_id      = user.getUser_id();
            this.message_id   = message.getHeader("Message-ID")[0].
                                    replace("<", "").replace(">", "");
            this.from         = InternetAddress.toString(message.getFrom());
            this.to           = InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
            this.in_replay_to = InternetAddress.toString(message.getReplyTo());
            this.date         = new Timestamp(message.getSentDate().getTime());
            this.size         = message.getSize();
            this.subject      = message.getSubject();
            this.folder       = message.getFolder().getFullName();
            this.update       = new Timestamp(new Date().getTime());

            if (message.isSet(Flags.Flag.DELETED )) { this.deleted = 1; }
            if (message.isSet(Flags.Flag.ANSWERED)) { this.answred = 1; }
            if (message.isSet(Flags.Flag.DRAFT   )) { this.draft   = 1; }
            if (message.isSet(Flags.Flag.FLAGGED )) { this.flagged = 1; }
            if (message.isSet(Flags.Flag.RECENT  )) { this.recent  = 1; }
            if (message.isSet(Flags.Flag.SEEN    )) { this.seen    = 1; }

//            System.out.println("From: " + InternetAddress.toString(message.getFrom()));
//            System.out.println("Reply-to: " + InternetAddress.toString(message.getReplyTo()));
//            System.out.println("To: " + InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
//            System.out.println("Cc: " + InternetAddress.toString(message.getRecipients(Message.RecipientType.CC)));
//            System.out.println("Bcc: " + InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC)));
//            System.out.println("Subject: " + message.getSubject());
//
//            System.out.println("Sent: " + message.getSentDate());         // когда отправлено
//            System.out.println("Received: " + message.getReceivedDate()); // когда получено

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Email{"                            + " \n" +
                "id             = " + id           + ",\n" +
                ", direction    = " + direction    + ",\n" +
                ", user_id      = " + user_id      + ",\n" +
                ", client_id    = " + client_id    + ",\n" +
                ", uid          = " + uid          + ",\n" +
                ", message_id   = " + message_id   + ",\n" +
                ", msgno        = " + msgno        + ",\n" +
                ", from         = " + from         + ",\n" +
                ", to           = " + to           + ",\n" +
                ", in_replay_to = " + in_replay_to + ",\n" +
                ", references   = " + references   + ",\n" +
                ", date         = " + date         + ",\n" +
                ", size         = " + size         + ",\n" +
                ", subject      = " + subject      + ",\n" +
                ", folder       = " + folder       + ",\n" +
                ", recent       = " + recent       + ",\n" +
                ", flagged      = " + flagged      + ",\n" +
                ", answred      = " + answred      + ",\n" +
                ", deleted      = " + deleted      + ",\n" +
                ", seen         = " + seen         + ",\n" +
                ", draft        = " + draft        + ",\n" +
                ", update       = " + update       + " \n" +
                '}';
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

    public int getUid() {
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

    public Timestamp getUpdate() {
        return update;
    }

}
