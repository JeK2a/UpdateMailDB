import com.sun.mail.imap.IMAPFolder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;

public class MyTest {

    private void connectToMailAccount(User user) {
        MyProperties myProperties = new MyProperties(user); // Настройка подключение текущего пользователя

        new Settings();

        Session session = Session.getDefaultInstance(myProperties, null); // Создание сессии
        session.setDebug(Boolean.parseBoolean(Settings.getSession_debug()));          // Включение дебага

        try {
            Store store = session.getStore();
            store.connect(
                    user.getHost(),
                    user.getEmail(),
                    user.getPassword()
            );

            store.addFolderListener(new FolderListener() { // Подключение отслеживания действий с падками в текущем подключении пользователя

                @Override
                public void folderCreated(FolderEvent folderEvent) { // Действие при создании папки
                    StartMail.enterMessage("folder created");
                }

                @Override
                public void folderDeleted(FolderEvent folderEvent) { // Действие при удалении папки

                    try {
                        Message[] messages = folderEvent.getFolder().getMessages();

                        for (Message message : messages) {
                            System.out.println(message);

                        }
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    StartMail.enterMessage("folder deleted");
                }

                @Override
                public void folderRenamed(FolderEvent folderEvent) { // Действие при переименовании папки
                    try {
                        Message[] messages = folderEvent.getFolder().getMessages();

                        for (Message message : messages) {
                            System.out.println(message);
                        }
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    StartMail.enterMessage("folder renamed");
                }
            });

            store.addStoreListener(storeEvent ->
                    StartMail.enterMessage("store notification - " + storeEvent.getMessage()));

            IMAPFolder[] folders = (IMAPFolder[]) store.getDefaultFolder().list(); // Получение списка папок лоя текушего подключения
            for (IMAPFolder folder: folders) {

//                Thread myTreadAllMails = new Thread(new AddNewMessageThread(user, folder.getMessages())); // Создание потока для посинхронизации всего почтового ящика // TODO 1 all
//                myTreadAllMails.start(); // Запус потока

                Thread myThreadEvent = new Thread(new MailListenerThread(user, folder)); // Создание потока для отслеживания действий с определенной папкой // TODO 2 lsn
                myThreadEvent.start(); // Запус потока
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Вывод сообщения
    public static void enterMessage(String text) {
        System.out.println(text);       // На панель
    }

    public static void main(String[] args) {
        MyTest my_test = new MyTest(); //
        my_test.connectToMailAccount(
                new User(
                        1,
                        304,
                        "me@tdfort.ru",
                        "6Z8m5C8q",
                        true,
                        true,
                        "imap.yandex.ru",
                        993,
                        "me@tdfort.ru",
                        "JeK2a",
                        "utf-8",
                        "ssl",
                        1
                )
        ); // Подключение к почтовым аккаунтам

    }
}