import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MyReadEmail extends JFrame {

    private static JTextArea textArea; // Панель для вывода сообщения

    private MyReadEmail(String title) {
        super(title);

        addWindow();
    }

    private void connectToMailAccount(String acc) {
        MyProperties myProperties = new MyProperties(acc);
        Session session = Session.getDefaultInstance(myProperties, null);
        session.setDebug(true);

        try {
            Store store = session.getStore();
            store.connect(
                    myProperties.IMAP_Server,
                    myProperties.IMAP_AUTH_EMAIL,
                    myProperties.IMAP_AUTH_PWD
            );

            IMAPFolder folder = (IMAPFolder) store.getFolder("INBOX");
            new Thread(new MailListenerThread(folder)).start();
        } catch (MessagingException e) {
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
        setPreferredSize(new Dimension(450, 450));                             // Установить размер панели прокрутки
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
        String[] accs = {"GMail", "Mail.ru", "Yandex"};

		MyReadEmail myReadEmail = new MyReadEmail("Test SMTP Listener");

        for (String acc: accs) {
            System.out.println(acc);
            myReadEmail.connectToMailAccount(acc);
        }
	}

}
