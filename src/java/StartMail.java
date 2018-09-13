import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class StartMail extends JFrame {

    private static DB db;
    private static JTextArea textArea; // Панель для вывода сообщения
    private ArrayList<ArrayList<Thread>> threadList = new ArrayList<>();

    private StartMail() {
        super("Mail reader");
        addWindow();
    }

    private void connectToMailAccount(User user) {
        ArrayList<Thread> tmpThreadList = new ArrayList<>();
        MyProperties myProperties       = new MyProperties(user);

        Session session = Session.getDefaultInstance(myProperties, null);
        session.setDebug(true);

        try {
            Store store = session.getStore();
            store.connect(
                user.getHost(),
                user.getEmail(),
                user.getPassword()
            );

            store.addFolderListener(new FolderListener() {
                @Override
                public void folderCreated(FolderEvent folderEvent) {
                    StartMail.enterMessage("folder created");
                }

                @Override
                public void folderDeleted(FolderEvent folderEvent) {

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
                public void folderRenamed(FolderEvent folderEvent) {
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

            System.err.println("_________________________________Folders_________________________________");
            IMAPFolder[] folders = (IMAPFolder[]) store.getDefaultFolder().list();
            for (IMAPFolder folder: folders) {
                StartMail.enterMessage("Connect to -> " + user.getEmail() + " -> " + folder.getFullName());
                Thread myThread = new Thread(new MailListenerThread(user, folder));
                myThread.start();
                tmpThreadList.add(myThread);
            }
            threadList.add(tmpThreadList);
            System.err.println("_________________________________folders_________________________________");

        } catch (MessagingException e) {
            System.err.println("Problems wish " + user.getEmail());
            e.printStackTrace();
        }
    }

    private void addWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Выходить из программы при закрытии основного окна

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        }); // Добавить событи для взаимодействия с основным окном

        textArea = new JTextArea(20, 30);         // Панель для вывода сообщения
//        textArea.setEnabled(false);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);                                  // Панель прокрутки
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);     // Вертикальная прокрутка
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Горизонтальная прокрутка
        setPreferredSize(new Dimension(600, 1000));                            // Установить размер панели прокрутки
        add(scrollPane);                                                                     // Добавить на окно панель прокрутки

        setVisible(true); // Сделать окно видимым
        pack();           // Сжать окно до минимума
    }

    // Вывод сообщения
    public static void enterMessage(String text) {
        System.err.println(text);     // На панель
        textArea.append(text + "\n"); // В коммандную строку
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
        ArrayList<User> users = db.getUsers();
        StartMail startMail = new StartMail();

        for (User user : users) {
            startMail.connectToMailAccount(user);
        }

        while (true) {
            for (ArrayList<Thread> threads : startMail.getThreadList()) {
                for (Thread thread : threads) {
                    enterMessage(thread.getName() + " / " + thread.isAlive() + " / " + thread.isDaemon());
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

}
