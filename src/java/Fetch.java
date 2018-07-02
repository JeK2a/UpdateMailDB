import javax.mail.*;
import java.util.Properties;

public class Fetch {
    public static void main(String args[]) {

        String host = "imap.yandex.ru";
        String username = "jek2ka2016@yandex.ru";
        String password =  "Nokia3510!";

        // Получить системные свойства
//        Properties props = System.getProperties();
        // Установить системные свойства
        Properties props = new Properties();


          // Mail.ru
//        props.put("mail.transport.protocol", "pop3");
//        props.put("mail.pop3.host", "pop.mail.ru");
//        props.put("mail.pop3.port", "995");
//        props.put("mail.pop3.auth", "true");

        // Yandex
        props.put("mail.transport.protocol", "pop3");
        props.put("mail.pop3.host", "imap.yandex.ru");
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.auth", "true");

        // Настроить аутентификацию, получить session
        Authenticator auth = new PopupAuthenticator();

        // Получить session
//        Session session = Session.getDefaultInstance(props, auth);
        Session session = Session.getDefaultInstance(props, null);

//        System.out.println(session.toString());

        try {
            // Получить store
            Store store = session.getStore("imaps");
//            System.out.println(store.toString());
//            store.connect();
            store.connect(host, username, password);
            System.out.println(store.getDefaultFolder());
            // Получить folder
            Folder folder = store.getFolder("INBOX");
//            Folder folder = store.toString();
            System.out.println(store.getFolder("INBOX").getMessageCount());
            folder.open(Folder.READ_ONLY);

            // Получить каталог
//            System.out.println(folder.getMessageCount());
            Message message[] = folder.getMessages();
            System.err.println(message.length);

//            for (int i = 0, n = message.length; i < n; i++) {
//                System.out.println(i + ": " + message[i].getFrom()[0] + "\t"
//                        + message[i].getSubject());
//                String content = message[i].getContent().toString();
//                if (content.length() > 200) {
//                    content = content.substring(0, 200);
//                }
//                System.out.print(content);
//            }

            // Закрыть соединение
            folder.close(false);
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println(e);
        }


//        System.exit(0);
    }
}
