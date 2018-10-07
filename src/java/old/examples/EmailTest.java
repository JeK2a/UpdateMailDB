package old.examples;

public class EmailTest {
	public static void main(String[] args) {
		String email_to = "jek2ka@gmail.com";
		String thema    = "Test";
		String text     = "Test text";

		SendEmail.SMTP_SERVER    = "imap.gmail.com";
		SendEmail.SMTP_Port      = "993";
		SendEmail.EMAIL_FROM     = "jek2ka@gmail.com";
		SendEmail.SMTP_AUTH_USER = "jek2ka@gmail.com";
		SendEmail.SMTP_AUTH_PWD  = "pbnokia3510";
		SendEmail.REPLY_TO       = "jek2ka@gmail.com";

		SendEmail sendEmail = new SendEmail(email_to, thema);

		sendEmail.sendMessage(text);
		System.out.println ("Сообщение отправлено");
	}

}
