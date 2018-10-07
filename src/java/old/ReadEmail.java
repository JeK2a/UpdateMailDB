package old;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IdleManager;
import old.examples.EmailAuthenticator;

import javax.mail.*;
import javax.mail.event.*;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadEmail {

	public ReadEmail() 	{
		Properties properties = new Properties();
		String acc = "Mail.ru";

        String IMAP_Server     = null;
        String IMAP_AUTH_EMAIL = null;
        String IMAP_AUTH_PWD    = null;

		switch (acc) {
            case "Gmail":
                properties.put("mail.debug"          , "true" );
                properties.put("mail.store.protocol" , "imaps");
                properties.put("mail.imap.ssl.enable", "true" );
                properties.put("mail.imap.port"      , "993"  );

                IMAP_Server     = "imap.gmail.com";
                IMAP_AUTH_EMAIL = "";
                IMAP_AUTH_PWD   = "";
                break;
            }

        System.out.println(acc);

		Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);
		Session session = Session.getDefaultInstance(properties, auth);
		session.setDebug(true);
			
		try {
	        Store store = session.getStore();
	
	        // Подключение к почтовому серверу
			store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);

			ExecutorService executorService = Executors.newCachedThreadPool();
			final IdleManager idleManager = new IdleManager(session, executorService);

            // the following should be done once...
//			Properties properties = session.getProperties();
            properties.put("mail.event.scope", "session"); // or "application"
            properties.put("mail.event.executor", executorService);

			// for Java 7
//			@Resource
//			ManagedExecutorService es;
//			final IdleManager idleManager = new IdleManager(session, es);

            IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");

            folder.open(Folder.READ_WRITE);

            folder.doCommand(protocol -> {
                try {
                    protocol.simpleCommand("NOOP", null);
                } catch (com.sun.mail.iap.ProtocolException e) {
                    e.printStackTrace();
                }
                return null;
            });


			folder.addMessageCountListener(new MessageCountAdapter() {
				public void messagesAdded(MessageCountEvent messageCountEvent) {
                    Folder folder = (Folder) messageCountEvent.getSource();

//                    try {
//                        folder.doCommand(protocol -> {
//                            try {
//                                protocol.simpleCommand("NOOP", null);
//                            } catch (com.sun.mail.iap.ProtocolException e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        });
//                    } catch (MessagingException e) {
//                        e.printStackTrace();
//                    }

                    Message[] msgs = messageCountEvent.getMessages();

					System.out.println(
					        "Folder: " + folder +
							" got " + msgs.length +
                            " new messages"
                    );

					try {
						// process new messages
                        System.err.println("Testing!!!");
						idleManager.watch(folder); // keep watching for new messages
					} catch (MessagingException mex) {
						// handle exception related to the Folder
                        mex.getStackTrace();
                        System.err.println(mex);
					}
				}
			});
            System.err.println("Testing!!!!!");
			idleManager.watch(folder);

		} catch (MessagingException e) {
            e.printStackTrace();
			System.err.println(e.getMessage());
		} catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		new ReadEmail();
		System.exit(0);
	}

}
