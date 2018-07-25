import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class MyReadEmail extends JFrame {

    private static DB db;
    private static JTextArea textArea; // Панель для вывода сообщения

    private MyReadEmail() {
        super("Mail reader");

        addWindow();
    }

    private void connectToMailAccount(User user, String folder_name) {
        MyProperties myProperties = new MyProperties(user);

        Session session = Session.getDefaultInstance(myProperties, null);
        session.setDebug(true);

        try {
            Store store = session.getStore();
            store.connect(
                    user.getHost(),
                    user.getEmail(),
                    user.getPassword()
            );

            IMAPFolder folder = (IMAPFolder) store.getFolder(folder_name);
            new Thread(new MailListenerThread(folder)).start();
        } catch (MessagingException e) {
            System.err.println(user.getEmail());
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
        System.out.println(text);     // На панель
        textArea.append(text + "\n"); // В коммандную строку
    }

	public static void main(String[] args) {

        db = new DB();
        ArrayList<User> users = db.getUsers();
        MyReadEmail myReadEmail = new MyReadEmail();

        for (User user : users) {
            myReadEmail.connectToMailAccount(user, "INBOX");
        }
	}

}
