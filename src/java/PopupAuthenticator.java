import javax.mail.PasswordAuthentication;
import javax.swing.*;
import java.util.StringTokenizer;

public class PopupAuthenticator extends javax.mail.Authenticator {

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username;
        String password;

//        String result = JOptionPane.showInputDialog("Enter 'username,password'");
//
//        StringTokenizer st = new StringTokenizer(result, ",");
//        username = st.nextToken();
//        password = st.nextToken();

//        username = "vipjonpc@mail.ru";
//        password = "webmailnokia35101989";
        username = "jek2ka2016@yandex.ru";
        password = "Nokia3510!";

        return new PasswordAuthentication(username, password);
    }
}