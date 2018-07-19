import java.util.Properties;

public class MyProperties extends Properties {

    public String IMAP_Server;
    public String IMAP_AUTH_EMAIL;
    public String IMAP_AUTH_PWD;

    public MyProperties(String acc) {
        switch (acc) {
            case "GMail":
                put("mail.debug"          , "true" );
                put("mail.store.protocol" , "imaps");
                put("mail.imap.ssl.enable", "true" );
                put("mail.imap.port"      , "993"  );

                IMAP_Server     = "imap.gmail.com";
                IMAP_AUTH_EMAIL = "jek2ka@gmail.com";
                IMAP_AUTH_PWD   = "pbnokia3510";
                break;
            case "Mail.ru":
                put("mail.debug"          , "true" );
                put("mail.store.protocol" , "imaps");
                put("mail.imap.ssl.enable", "true" );
                put("mail.imap.port"      , "993"  );

                IMAP_Server     = "imap.mail.ru";
                IMAP_AUTH_EMAIL = "vipjonpc@mail.ru";
                IMAP_AUTH_PWD   = "webmailnokia35101989";
                break;
            case "Yandex":
                put("mail.debug"          , "true" );
                put("mail.store.protocol" , "imaps");
                put("mail.imap.ssl.enable", "true" );
                put("mail.imap.port"      , "993"  );

                IMAP_Server     = "imap.yandex.ru";
                IMAP_AUTH_EMAIL = "jek2ka2016@yandex.ru";
                IMAP_AUTH_PWD   = "Nokia3510!";
                break;
        }
    }

}
