import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.event.*;
import java.util.Arrays;

public class MailListenerThread implements Runnable {

    private Folder folder;
    private int thread_sleep_time = 1000;

    public MailListenerThread(Folder folder) {
        this.folder = folder;
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
                MyReadEmail.enterMessage("messageChanged " + folder.getFullName());
                try {
                    MyReadEmail.enterMessage(String.valueOf(messageChangedEvent.getMessage().getMessageNumber()));
                    MyReadEmail.enterMessage(messageChangedEvent.getMessage().getSubject());
                    MyReadEmail.enterMessage(messageChangedEvent.getMessage().getFlags().toString());
                    MyReadEmail.enterMessage(Arrays.toString(messageChangedEvent.getMessage().getFrom()));
                    MyReadEmail.enterMessage(Arrays.toString(messageChangedEvent.getMessage().getReplyTo()));
                    MyReadEmail.enterMessage(messageChangedEvent.getMessage().getSentDate().toString());
                    MyReadEmail.enterMessage(messageChangedEvent.getMessage().getReceivedDate().toString());
                    MyReadEmail.enterMessage(messageChangedEvent.getMessage().getDataHandler().toString());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
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
                Thread.sleep(thread_sleep_time);
            }
        } catch (InterruptedException | MessagingException e) {
            e.printStackTrace();
        }

    }
}
