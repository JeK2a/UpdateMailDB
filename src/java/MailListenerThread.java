import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.event.*;

public class MailListenerThread implements Runnable {

    private Folder folder;
    private final ThreadLocal<Integer> thread_sleep_time = ThreadLocal.withInitial(() -> 300);
    private DB db;
    private User user;

    public MailListenerThread(User user, Folder folder) {
        this.user = user;
        this.folder = folder;
        db = new DB();
    }

    @Override
    public void run() {
        try {
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        folder.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {
                MyReadEmail.enterMessage("Connection opened");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                try {
                    folder.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                MyReadEmail.enterMessage("Connection disconnected");
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                MyReadEmail.enterMessage("Connection closed");
            }
        });

        folder.addMessageChangedListener(new MessageChangedListener() {
            @Override
            public void messageChanged(MessageChangedEvent messageChangedEvent) {
                try {
                    String message_id = messageChangedEvent.getMessage().getHeader("Message-ID")[0].
                            replace("<", "").replace(">", "");

                    MyReadEmail.enterMessage(message_id);

                    MyReadEmail.enterMessage(new Email(user, messageChangedEvent.getMessage()).toString());

//                    db.addEmail(new Email(messageChangedEvent.getMessage()));

//                    Enumeration headers = messageChangedEvent.getMessage().getAllHeaders();
//                    while (headers.hasMoreElements()) {
//                        Header h = (Header) headers.nextElement();
//                        MyReadEmail.enterMessage(h.getName() + ": " + h.getValue());
//                    }

                    MyReadEmail.enterMessage("messageChanged");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });

        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                db.addEmail(new Email(user, messageCountEvent.getMessages()[0]));
                MyReadEmail.enterMessage(
                        "messagesAdded " +
                        messageCountEvent.getMessages().length +
                        " - " +
                        folder.getFullName()
                );
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                MyReadEmail.enterMessage(
                        "messagesRemoved " +
                        messageCountEvent.getMessages().length +
                        " - " +
                        folder.getFullName()
                );
            }
        });

        try {
            while (true) {
                if (!folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                    System.err.println("Folder close -> open");
                }
                Thread.sleep(thread_sleep_time.get());
            }
        } catch (InterruptedException | MessagingException e) {
            e.printStackTrace();
        }

    }
}
