import java.util.Properties;

public class MyProperties extends Properties {

    private String IMAP_Server;
    private String IMAP_AUTH_EMAIL;
    private String IMAP_AUTH_PWD;

    public MyProperties(User user) {
        this.IMAP_Server     = user.getHost();
        this.IMAP_AUTH_EMAIL = user.getEmail();
        this.IMAP_AUTH_PWD   = user.getPassword();

        put("mail.debug"          , "true");
        put("mail.store.protocol" , "imaps");
        put("mail.imap.port"      , user.getPort());
        if (user.getSecure().equals("ssl") || user.getSecure().equals("tls") ||
            user.getSecure().equals("SSL") || user.getSecure().equals("TLS"))
        {
            put("mail.imap.ssl.enable", "true");
        }
//        put("mail.imap.statuscachetimeout", "500");
    }

    public String getIMAP_Server() {
        return IMAP_Server;
    }

    public void setIMAP_Server(String IMAP_Server) {
        this.IMAP_Server = IMAP_Server;
    }

    public String getIMAP_AUTH_EMAIL() {
        return IMAP_AUTH_EMAIL;
    }

    public void setIMAP_AUTH_EMAIL(String IMAP_AUTH_EMAIL) {
        this.IMAP_AUTH_EMAIL = IMAP_AUTH_EMAIL;
    }

    public String getIMAP_AUTH_PWD() {
        return IMAP_AUTH_PWD;
    }

    public void setIMAP_AUTH_PWD(String IMAP_AUTH_PWD) {
        this.IMAP_AUTH_PWD = IMAP_AUTH_PWD;
    }
}
