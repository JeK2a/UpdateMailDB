package examples;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

class SendEmail
{
	private Message message      = null;
	static String SMTP_AUTH_USER = null;
    static String SMTP_AUTH_PWD  = null;
    static String EMAIL_FROM     = null;
    static String SMTP_SERVER    = null;
    static String SMTP_Port      = null;
    static String REPLY_TO       = null;
    private static String FILE_PATH      = null;

	SendEmail(final String emailTo, final String thema) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host"      , SMTP_SERVER);
		properties.put("mail.smtp.port"      , SMTP_Port);
		properties.put("mail.smtp.auth"      , "true");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		try {
			Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
			Session session = Session.getDefaultInstance(properties, auth);
			session.setDebug(true);
			
			InternetAddress email_from = new InternetAddress(EMAIL_FROM);
			InternetAddress email_to   = new InternetAddress(emailTo   );
			InternetAddress reply_to   = (REPLY_TO == null) ? null : new InternetAddress(REPLY_TO);
			message = new MimeMessage(session); 
			
			message.setFrom(email_from);
			message.setRecipient(Message.RecipientType.TO, email_to);
			message.setSubject(thema);
			if (reply_to != null)
				message.setReplyTo (new Address[] {reply_to});
		} catch (MessagingException e) {
			System.err.println(e.getMessage());
		}
	}

	public boolean sendMessage (final String text) {
		boolean result = false;
		try {
			// Содержимое сообщения
	        Multipart mmp = new MimeMultipart();
	        // Текст сообщения	
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(text, "text/plain; charset=utf-8");
	        mmp.addBodyPart(bodyPart);
	        // Вложение файла в сообщение
	        if (FILE_PATH != null) {
	        	MimeBodyPart mbr = createFileAttachment(FILE_PATH);
	        	mmp.addBodyPart(mbr);
	        }
			// Определение контента сообщения
	        message.setContent(mmp);
			// Отправка сообщения 
			Transport.send(message);
			result = true;
		} catch (MessagingException e){
			// Ошибка отправки сообщения
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Функция создания файлового вложения
	 * @param filepath путь к файлу
	 * @return MimeBodyPart
	 */
    private MimeBodyPart createFileAttachment(String filepath) {
        // Создание MimeBodyPart
        MimeBodyPart mbp = new MimeBodyPart();

        // Определение файла в качестве контента
        FileDataSource fds = new FileDataSource(filepath);
        try {
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(fds.getName());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mbp;
    }

}
