//package old;
//
//import com.sun.mail.imap.IMAPFolder;
//import javax.mail.*;
//import java.util.Properties;
//
//
//public class Fetch {
//
//    public static void main(String args[]) throws MessagingException {
//
//        // Получить системные свойства
//        Properties props = System.getProperties();
//        // Установить системные свойства
////        Properties props = new Properties();
//
//          // Mail.ru
////        props.put("mail.transport.protocol", "pop3");
////        props.put("mail.pop3.host", "pop.mail.ru");
////        props.put("mail.pop3.port", "995");
////        props.put("mail.pop3.auth", "true");
//
//        // Yandex
////        props.put("mail.transport.protocol", "imaps");
////        props.put("mail.imaps.host", "imap.yandex.ru");
////        props.put("mail.imaps.port", "993");
////        props.put("mail.imaps.auth", "true");
//
//        // Настроить аутентификацию, получить session
//        Authenticator auth = new PopupAuthenticator();
//
//        // Получить session
//        Session session = Session.getDefaultInstance(props, auth);
////        Session session = Session.getDefaultInstance(props, null);
//
//        // Получить store
//        Store store = session.getStore("imaps");
//        store.connect();
//        // Получить folder
////        Folder folder = store.getFolder("INBOX");
//
//        // Получить store
//        System.out.println(store.getDefaultFolder());
//
//        try (store) {
//            IMAPFolder imapFolder = (IMAPFolder) store.getFolder("INBOX");
////            imapFolder.open(Folder.READ_ONLY);
//
//            new Thread(new KeepAliveRunnable(imapFolder, 1)).start();
////            new Thread(new KeepAliveRunnable(imapFolder, 2)).start();
//
//            // Получить каталог
////            Message message[] = imapFolder.getMessages();
////            System.out.println("Новых сообщений: " + imapFolder.getNewMessageCount() + "/" + message.length);
////            System.out.println("Непрочитанных сообщений: " + imapFolder.getUnreadMessageCount() + "/" + message.length);
////            System.out.println(imapFolder.getURLName().toString());
////
////            // Закрыть соединение
////            imapFolder.close(false);
////            store.close();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.err.println(e);
//        }
//
//        System.out.println("End main");
//
////        System.exit(0);
//    }
//}
