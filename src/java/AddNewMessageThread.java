import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;

public class AddNewMessageThread implements Runnable {

    private DB         db;
    private User       user;
    private IMAPFolder imap_folder;

    public AddNewMessageThread(User user, IMAPFolder imap_folder) {
        db = new DB();
        this.user        = user;
        this.imap_folder = imap_folder;
    }

    @Override
    public void run() {
        try {
            int messages_count = imap_folder.getMessageCount();

            if (messages_count < 1) {
                System.err.println(imap_folder.getFullName() + " - message count = " + messages_count);
                return;
            }

            IMAPMessage last_imap_message = (IMAPMessage) imap_folder.getMessage(messages_count);
            long mail_last_uid = imap_folder.getUID(last_imap_message);
            long db_last_uid = db.getLastUID(user.getUser_id(), imap_folder.getFullName());

            Message[] messages = imap_folder.getMessagesByUID(db_last_uid + 1, mail_last_uid);

            System.out.println(messages.length + " из " + imap_folder.getMessages().length);

            for (Message message : messages) {
                if (!imap_folder.isOpen()) {
                    imap_folder.open(IMAPFolder.READ_ONLY);
                }
                db.addEmail(new Email(user, (IMAPMessage) message, imap_folder));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
