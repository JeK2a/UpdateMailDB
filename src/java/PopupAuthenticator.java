import javax.mail.PasswordAuthentication;
import javax.swing.*;
import java.util.StringTokenizer;

public class PopupAuthenticator extends javax.mail.Authenticator {

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username;
        String password;

        String result = JOptionPane.showInputDialog("Enter 'username,password'");

        StringTokenizer st = new StringTokenizer(result, ",");
        username = st.nextToken();
        password = st.nextToken();

        return new PasswordAuthentication(username, password);
    }
}