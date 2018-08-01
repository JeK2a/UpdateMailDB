package old;

import javax.mail.PasswordAuthentication;
import javax.swing.*;
import java.util.StringTokenizer;

public class PopupAuthenticator extends javax.mail.Authenticator {

    private String username;
    private String password;
    private String acc;

    public PopupAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {

        String result = JOptionPane.showInputDialog("Enter 'username,password'");

        StringTokenizer st = new StringTokenizer(result, ",");
        username = st.nextToken();
        password = st.nextToken();

        return new PasswordAuthentication(username, password);
    }

}