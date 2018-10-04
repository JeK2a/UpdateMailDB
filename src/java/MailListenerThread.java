import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.*;
import java.util.Enumeration;

public class MailListenerThread implements Runnable {

    private Folder folder;
    private final ThreadLocal<Integer> thread_sleep_time = ThreadLocal.withInitial(() -> 1000);
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
            System.err.println("Problem with email " + user.getEmail() + " / folder " + folder.getFullName());
            e.printStackTrace();
        }

        folder.addConnectionListener(new ConnectionListener() {

            @Override
            public void opened(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection opened");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection disconnected");
                try {
                    folder.open(Folder.READ_ONLY);
                } catch (MessagingException e) {
                    System.err.println(
                            "Problem with email " + user.getEmail()
                                   + " / folder " + folder.getFullName()
                    );
                    e.printStackTrace();
                }
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                StartMail.enterMessage("Connection closed");
            }
        });


        folder.addMessageChangedListener(new MessageChangedListener() {
            @Override
            public void messageChanged(MessageChangedEvent messageChangedEvent) {
                Message message = messageChangedEvent.getMessage();
                Email email = new Email(user, message);


                try {
//                    Enumeration enumeration =  message.getAllHeaders();

//                    Enumeration enumeration1 = new Vec
//
//                    while (enumeration.hasMoreElements()) {
//                        System.out.println(enumeration.nextElement());
//                    }

//                    Vector v = new Vector();
//
//                    v.add("one");
//                    v.add("2");
//                    v.add("3");
//                    v.add("4");
//                    v.add("5");


            for (Enumeration e = message.getAllHeaders(); e.hasMoreElements();) {
                System.out.println(e.nextElement());
            }


//                    while (enumeration.hasMoreElements()) {
//                        String tmp = enumeration.nextElement().toString();
//                        System.out.println("Text ---- " + tmp);
//                    }
//
//                    Iterator iterator = enumeration.asIterator();
//
//                    while (iterator.hasNext()) {
//                        String tmp = (String) iterator.next();
//                        System.out.println("Text ---> " + tmp);
//
//                    }


                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                StartMail.enterMessage(email.toString());

                db.changeMessage(email);
                StartMail.enterMessage("messageChanged");

//                    Enumeration headers = messageChangedEvent.getMessage().getAllHeaders();
//                    while (headers.hasMoreElements()) {
//                        Header h = (Header) headers.nextElement();
//                        src.java.StartMail.enterMessage(h.getName() + ": " + h.getValue());
//                    }
            }
        });

        folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                for (Message message : messageCountEvent.getMessages()) {
                    db.addEmail(new Email(user, message));
                    StartMail.enterMessage("messagesAdded ");
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) { // TODO messagesRemoved
                for (Message message : messageCountEvent.getMessages()) {
                    db.changeMessage(new Email(user, message));
                    StartMail.enterMessage("messagesRemoved");
                }
            }
        });

//        try {
//            new Thread(new AddNewMessageThread(user, folder.getMessages())).start();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }

        try {
            while (true) {
                if (!folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                    System.err.println("Folder close -> open");
                }
                Thread.sleep(thread_sleep_time.get());
            }
        } catch (InterruptedException | MessagingException e) {
            System.err.println(
                    "Problem with email " + user.getEmail()
                           + " / folder " + folder.getFullName()
            );
            e.printStackTrace();
        }

    }
}
