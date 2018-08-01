package examples;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import javax.mail.Session;
import javax.mail.Store;

public class ReadEmail {

	public ReadEmail() 	{
		Properties properties = new Properties();
		properties.put("mail.debug"           , "true" );
		properties.put("mail.store.protocol"  , "imaps");
		properties.put("mail.imap.ssl.enable" , "true" );
		properties.put("mail.imap.port"       , "993"  );

		String IMAP_Server     = "imap.gmail.com";
		String IMAP_AUTH_EMAIL = "jek2ka@gmail.com";
		String IMAP_AUTH_PWD   = "pbnokia3510";
		Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);
		Session session = Session.getDefaultInstance(properties, auth);
		session.setDebug(false);
			
		try {
	        Store store = session.getStore();
	
	        // Подключение к почтовому серверу
			store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);

	        // Папка входящих сообщений
	        Folder inbox = store.getFolder("INBOX");
	
	        // Открываем папку в режиме только для чтения
	        inbox.open(Folder.READ_ONLY);
	
	        System.out.println("Количество сообщений : " + String.valueOf(inbox.getMessageCount()));
	        if (inbox.getMessageCount() == 0) {
				return;
			}
	        // Последнее сообщение; первое сообщение под номером 1
	        Message message = inbox.getMessage(inbox.getMessageCount());
	        Multipart messageContent = (Multipart) message.getContent();
	        // Вывод содержимого в консоль
	        for (int i = 0; i < messageContent.getCount(); i++) {
				BodyPart bp = messageContent.getBodyPart(i);
				if (bp.getFileName() == null) {
					System.out.println("    " + i + ".  сообщение : '" + bp.getContent() + "'");
				} else {
					System.out.println("    " + i + ". файл : '" + bp.getFileName() + "'");
				}
			}
		} catch (MessagingException | IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		new ReadEmail();
		System.exit(0);
	}

}
