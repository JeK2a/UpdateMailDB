import javax.mail.*;
import java.util.Properties;

public class Fetch {
    public static void main(String args[]) throws MessagingException {

        // Получить системные свойства
        Properties props = System.getProperties();
        // Установить системные свойства
//        Properties props = new Properties();

          // Mail.ru
//        props.put("mail.transport.protocol", "pop3");
//        props.put("mail.pop3.host", "pop.mail.ru");
//        props.put("mail.pop3.port", "995");
//        props.put("mail.pop3.auth", "true");

        // Yandex
        props.put("mail.transport.protocol", "imaps");
        props.put("mail.imaps.host", "imap.yandex.ru");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.auth", "true");

        // Настроить аутентификацию, получить session
        Authenticator auth = new PopupAuthenticator();

        // Получить session
        Session session = Session.getDefaultInstance(props, auth);
//        Session session = Session.getDefaultInstance(props, null);

        try {
            // Получить store
            Store store = session.getStore("imaps");
            store.connect();
//            store.connect(host, username, password);
            System.out.println(store.getDefaultFolder());

            // Получить folder
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            // Получить каталог
            Message message[] = folder.getMessages();
            System.out.println("Новых сообщений: " + folder.getNewMessageCount() + "/" + message.length);
            System.out.println("Непрочитанных сообщений: " + folder.getUnreadMessageCount() + "/" + message.length);
            System.out.println(folder.getURLName().toString());


            // Закрыть соединение
            folder.close(false);
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        System.exit(0);
    }
}
