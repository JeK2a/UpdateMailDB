import java.util.Properties;

public class MyProperties extends Properties {

    public String IMAP_Server;
    public String IMAP_AUTH_EMAIL;
    public String IMAP_AUTH_PWD;

    public MyProperties(User user) {
        this.IMAP_Server = user.getHost();
        this.IMAP_AUTH_EMAIL = user.getEmail();
        this.IMAP_AUTH_PWD = user.getPassword();

        put("mail.debug"          , "false" );
        put("mail.store.protocol" , "imaps");
        if (user.getSecure().equals("ssl")) {
            put("mail.imap.ssl.enable", "true");
        }
        put("mail.imap.port"      , user.getPort());
    }

}
