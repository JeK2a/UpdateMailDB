import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StartMail {
//public class StartMail extends JFrame {

    private static DB db;
    //    private static JTextArea textArea; // Панель для вывода сообщения
    private ArrayList<ArrayList<Thread>> threadList = new ArrayList<>();
//    private static HashMap<User, HashMap<Folder, Thread>> threadMap = new HashMap<>();
    private static HashMap<String, HashMap<String, Thread>> threadMap = new HashMap<>();

    private StartMail() {
//        super("Mail reader");
//        addWindow();
    }

    private void connectToMailAccount(User user) {
        ArrayList<Thread> tmpThreadList = new ArrayList<>();
//        HashMap<Folder, Thread> tmpThreadMap = new HashMap<>();
        HashMap<String, Thread> tmpThreadMap = new HashMap<>();
        MyProperties myProperties       = new MyProperties(user); // Настройка подключение текущего пользователя

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
                            // TODO изменение флага сообщенией на удаленное (проверить)
                            db.changeDeleteFlag(new Email(user, message), user.getUser_id());
                        }
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    StartMail.enterMessage("folder deleted");
                }

                @Override
                public void folderRenamed(FolderEvent folderEvent) { // Действие при переименовании папки
                    try {
                        String old_folder_name = folderEvent.getFolder().getFullName();
                        String new_folder_name = folderEvent.getNewFolder().getFullName();
                        int user_id = user.getUser_id();

                        Message[] messages = folderEvent.getFolder().getMessages();

                        for (Message message : messages) {
                            db.changeFolderName(new Email(user, message), user_id, new_folder_name); // TODO проверить, добавить проверку результата
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
                StartMail.enterMessage("Connect to -> " + user.getEmail() + " -> " + folder.getFullName());
                Thread myThread = new Thread(new MailListenerThread(user, folder)); // Создание потока для отслеживания действий с определенной папкой
                myThread.start(); // Запус потока
                tmpThreadList.add(myThread); // Добавить потока в список
//                tmpThreadMap.put(folder, myThread);
                tmpThreadMap.put(folder.getFullName(), myThread);
            }

            threadList.add(tmpThreadList); // Добавить в список списк потоков с подключениями к папкам
//            threadMap.put(user, tmpThreadMap);
            threadMap.put(user.getEmail(), tmpThreadMap);

        } catch (MessagingException e) {
            System.err.println("Problems wish " + user.getEmail());
            e.printStackTrace();
        }
    }

//    private void addWindow() {
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Выходить из программы при закрытии основного окна
//
//        this.addWindowListener(new WindowListener() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//
//            @Override
//            public void windowOpened(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowClosed(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowIconified(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowDeiconified(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowActivated(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowDeactivated(WindowEvent e) {
//
//            }
//        }); // Добавить событи для взаимодействия с основным окном

//        textArea = new JTextArea(20, 30);         // Панель для вывода сообщения
////        textArea.setEnabled(false);
//        textArea.setEditable(false);
//
//        JScrollPane scrollPane = new JScrollPane(textArea);                                  // Панель прокрутки
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);     // Вертикальная прокрутка
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Горизонтальная прокрутка
//        setPreferredSize(new Dimension(600, 1000));                            // Установить размер панели прокрутки
//        add(scrollPane);                                                                     // Добавить на окно панель прокрутки
//
//        setVisible(true); // Сделать окно видимым
//        pack();           // Сжать окно до минимума
//    }

    // Вывод сообщения
    public static void enterMessage(String text) {
        System.out.println(text);     // На панель
//        textArea.append(text + "\n"); // В коммандную строку
    }

    private void showFolders(Folder[] folders) {
        String folder_name;
        for (Folder folder : folders) {
            StartMail.enterMessage(folder.getFullName());
        }
    }

    private ArrayList<ArrayList<Thread>> getThreadList() {
        return threadList;
    }

	public static void main(String[] args) {
        db = new DB();
        ArrayList<User> users = db.getUsers(); // Получение списка пользователей
        StartMail startMail = new StartMail(); //

        for (User user : users) {
            startMail.connectToMailAccount(user); // Подключение к почтовым аккаунтам
        }

        while (true) {

//            for (ArrayList<Thread> threads : startMail.getThreadList()) {
//                for (Thread thread : threads) {
//                    System.out.println(thread.getName() + " / " + thread.isAlive() + " / " + thread.isDaemon());
//                }
//            }

            System.err.println("Users count - " + threadMap.size());

//            for (HashMap.Entry<User, HashMap<Folder, Thread>> mapUsers : threadMap.entrySet()) {
//                System.err.println("user - " + mapUsers.getKey().getEmail());
//                HashMap<Folder, Thread> mapTmp = mapUsers.getValue();
//                for (HashMap.Entry<Folder, Thread> mapFolders : mapTmp.entrySet()) {
//                    Folder folder = mapFolders.getKey();
//                    Thread thread = mapFolders.getValue();
//                    if (!folder.isOpen()) {
//                        try {
//                            folder.open(Folder.READ_ONLY);
//                        } catch (MessagingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    System.err.println(folder.getFullName() + " / " +  thread.getName() + " / " + thread.isAlive());
//                }
//            }

            for (HashMap.Entry<String, HashMap<String, Thread>> mapUsers : threadMap.entrySet()) {
                System.err.println(mapUsers.getKey());
                HashMap<String, Thread> mapTmp = mapUsers.getValue();
                for (HashMap.Entry<String, Thread> mapFolders : mapTmp.entrySet()) {
                    String folder = mapFolders.getKey();
                    Thread thread = mapFolders.getValue();
                    System.err.println("          " + folder + " / " +  thread.getName() + " / " + thread.isAlive());
                }
            }


            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

}