package old;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IdleManager;

import javax.mail.*;
import javax.mail.event.*;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMail {

    private static IMAPFolder folder;

    public static void main(String[] args) {

        // Настроить аутентификацию, получить session
//        Authenticator auth = new old.PopupAuthenticator();

        Properties props = System.getProperties();
        // Yandex
        props.put("mail.transport.protocol", "imaps");
        props.put("mail.imaps.host", "imap.yandex.ru");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.auth", "true");
        // Add
        props.put("mail.event.scope", "session"); // or "application"
//        props.put("mail.event.scope", "application"); // or "application"
        ExecutorService es = Executors.newCachedThreadPool();
        props.put("mail.event.executor", es);

        Session session = Session.getDefaultInstance(props, null);

        try {
            Store store = session.getStore("imaps");
            store.connect();
            folder = (IMAPFolder) store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            IdleManager finalIdleManager = new IdleManager(session, es);

            folder.addMessageChangedListener(new MessageChangedListener() {
                @Override
                public void messageChanged(MessageChangedEvent messageChangedEvent) {
                    folder = (IMAPFolder) messageChangedEvent.getSource();
                    System.out.println(folder.getFullName());
                    Message message = messageChangedEvent.getMessage();
                    System.out.println("Test change message");
                    try {
                        System.out.println(message.getSubject());
                        System.out.println("Test change");
                        finalIdleManager.watch(folder);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });

            folder.addMessageCountListener(new MessageCountListener() {
                @Override
                public void messagesAdded(MessageCountEvent messageCountEvent) {
                    System.out.println("Test1");
                }

                @Override
                public void messagesRemoved(MessageCountEvent messageCountEvent) {
                    System.out.println("Test2");
                }
            });

            folder.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent e) {
                    System.out.println("Test3");
                    super.messagesAdded(e);
                }

                @Override
                public void messagesRemoved(MessageCountEvent e) {
                    System.out.println("Test4");
                    super.messagesRemoved(e);
                }
            });

            folder.addFolderListener(new FolderListener() {
                @Override
                public void folderCreated(FolderEvent folderEvent) {
                    System.out.println("Test5");
                }

                @Override
                public void folderDeleted(FolderEvent folderEvent) {
                    System.out.println("Test6");
                }

                @Override
                public void folderRenamed(FolderEvent folderEvent) {
                    System.out.println("Test7");
                }
            });

            folder.addFolderListener(new FolderAdapter() {
                @Override
                public void folderCreated(FolderEvent e) {
                    System.out.println("Test8");
                    super.folderCreated(e);
                }

                @Override
                public void folderRenamed(FolderEvent e) {
                    System.out.println("Test9");
                    super.folderRenamed(e);
                }

                @Override
                public void folderDeleted(FolderEvent e) {
                    System.out.println("Test10");
                    super.folderDeleted(e);
                }
            });


//            folder.addMessageChangedListener(new MessageChangedListener() {
//                @Override
//                public void messageChanged(MessageChangedEvent messageChangedEvent) {
//                    folder = (IMAPFolder) messageChangedEvent.getSource();
//                    System.out.println(folder.getFullName());
//                    Message message = messageChangedEvent.getMessage();
//                    System.out.println("Test change message");
//                    try {
//                        System.out.println(message.getSubject());
//                        System.out.println("Test change");
//                        finalIdleManager.watch(folder);
//                    } catch (MessagingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

//            MessageCountAdapter() {
//                public void messagesAdded(MessageCountEvent ev) {
//                    Folder folder = (Folder) ev.getSource();
//                    Message[] msgs = ev.getMessages();
//                    System.out.println("Folder: " + folder + " got " + msgs.length + " new messages");
//                    try {
//                        // process new messages
//                        System.out.println("Test message");
//                        finalIdleManager.watch(folder); // keep watching for new messages
//                    } catch (MessagingException mex) {
//                        // handle exception related to the Folder
//                        mex.printStackTrace();
//                        System.err.println(mex);
//                    }
//                }
//            });

//            folder.addMessageChangedListener(messageChangedEvent -> {
//                System.out.println("messageChangedEvent!!!");
//            }
//            );


//            folder.addConnectionListener(new MessageChangedEvent() {}  );


//            finalIdleManager.watch(folder);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }

    }

}
