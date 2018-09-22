import java.util.Properties;

public class MyProperties extends Properties {

    private String IMAP_server;
    private String IMAP_auth_email;
    private String IMAP_auth_password;

    public MyProperties(User user) {
        this.IMAP_server        = user.getHost();
        this.IMAP_auth_email    = user.getEmail();
        this.IMAP_auth_password = user.getPassword();

        Settings settings = new Settings();

        put("mail.debug"          , Settings.getMail_debug());
        put("mail.store.protocol" , "imaps");
        put("mail.imap.port"      , user.getPort());

        if (user.getSecure().equals("ssl") || user.getSecure().equals("tls") ||
            user.getSecure().equals("SSL") || user.getSecure().equals("TLS"))
        {
            put("mail.imap.ssl.enable", "true");
        }
//        put("mail.imap.statuscachetimeout", "500");
    }

    public String getIMAPServer() {
        return IMAP_server;
    }

    public void setIMAPServer(String IMAP_server) {
        this.IMAP_server = IMAP_server;
    }

    public String getIMAPAuthEmail() {
        return IMAP_auth_email;
    }

    public void setIMAPAuthEmail(String IMAP_auth_email) {
        this.IMAP_auth_email = IMAP_auth_email;
    }

    public String getIMAPAuthPassword() {
        return IMAP_auth_password;
    }

    public void setIMAPAuthPassword(String IMAP_auth_password) {
        this.IMAP_auth_password = IMAP_auth_password;
    }
}
