import java.sql.*;
import java.util.ArrayList;

public class DB implements AutoCloseable {

    private static final String USER     = "root";
    private static final String PASSWORD = "root";
    private static final String URL      = "jdbc:mysql://localhost:8889/test";
    private static final String[] params = {
                                               "useSSL=false",
                                               "useUnicode=true",
                                               "characterEncoding=utf-8"
                                           };

    private static Connection con;
    private static Statement  stmt;
    private static ResultSet  rs;

    public DB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL + "?" + arrayToString(params), USER, PASSWORD); // JDBC подключение к MySQL
            System.err.println(URL + "?" + arrayToString(params));

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query
        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    public int addEmail(Email email) {
        String query = "" +
            "INSERT INTO `a_my_emails`(" +
                "`direction`,"   +
                "`user_id`,"     +
                "`client_id`,"   +
                "`uid`,"         +
                "`message_id`,"  +
                "`msgno`,"       +
                "`from`,"        +
                "`to`,"          +
                "`in_reply_to`," +
                "`references`,"  +
                "`date`,"        +
                "`size`,"        +
                "`subject`,"     +
                "`folder`,"      +
                "`recent`,"      +
                "`flagged`,"     +
                "`answered`,"    +
                "`deleted`,"     +
                "`seen`,"        +
                "`draft`,"       +
                "`udate`"        +
            ") "                 +
            "VALUES ("           +
                "'" + email.getDirection()    + "', " +
                "'" + email.getUser_id()      + "', " +
                "'" + email.getClient_id()    + "', " +
                "'" + email.getUid()          + "', " +
                "'" + email.getMessage_id()   + "', " +
                "'" + email.getMsgno()        + "', " +
                "'" + email.getFrom()         + "', " +
                "'" + email.getTo()           + "', " +
                "'" + email.getIn_replay_to() + "', " +
                "'" + email.getReferences()   + "', " +
                "'" + email.getDate()         + "', " +
                "'" + email.getSize()         + "', " +
                "'" + email.getSubject()      + "', " +
                "'" + email.getFolder()       + "', " +
                "'" + email.getRecent()       + "', " +
                "'" + email.getFlagged()      + "', " +
                "'" + email.getAnswred()      + "', " +
                "'" + email.getDeleted()      + "', " +
                "'" + email.getSeen()         + "', " +
                "'" + email.getDraft()        + "', " +
                "'" + email.getUpdate()       + "'" +
            ");";

        System.out.println(query);

        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int addNewEmail(Email email) {
        String query = "" +
            "INSERT INTO `a_my_emails`(" +
                "`direction`,"   +
                "`user_id`,"     +
                "`client_id`,"   +
                "`uid`,"         +
                "`message_id`,"  +
                "`msgno`,"       +
                "`from`,"        +
                "`to`,"          +
                "`in_reply_to`," +
                "`references`,"  +
                "`date`,"        +
                "`size`,"        +
                "`subject`,"     +
                "`folder`,"      +
                "`recent`,"      +
                "`flagged`,"     +
                "`answered`,"    +
                "`deleted`,"     +
                "`seen`,"        +
                "`draft`,"       +
                "`udate`"        +
            ") "                 +
            "VALUES(" +
                "'" + email.getDirection()    + "'" + ", " +
                "'" + email.getUser_id()      + "'" + ", " +
                "'" + email.getClient_id()    + "'" + ", " +
                "'" + email.getUid()          + "'" + ", " +
                "'" + email.getMessage_id()   + "'" + ", " +
                "'" + email.getMsgno()        + "'" + ", " +
                "'" + email.getFrom()         + "'" + ", " +
                "'" + email.getTo()           + "'" + ", " +
                "'" + email.getIn_replay_to() + "'" + ", " +
                "'" + email.getReferences()   + "'" + ", " +
                "'" + email.getDate()         + "'" + ", " +
                "'" + email.getSize()         + "'" + ", " +
                "'" + email.getSubject()      + "'" + ", " +
                "'" + email.getFolder()       + "'" + ", " +
                "'" + email.getRecent()       + "'" + ", " +
                "'" + email.getFlagged()      + "'" + ", " +
                "'" + email.getAnswred()      + "'" + ", " +
                "'" + email.getDeleted()      + "'" + ", " +
                "'" + email.getSeen()         + "'" + ", " +
                "'" + email.getDraft()        + "'" + ", " +
                "'" + email.getUpdate()       + "'" +
            ")" +
            "ON DUPLICATE KEY UPDATE " +
                "`message_id` = '" + email.getMessage_id() + "';";

        System.out.println(query);

        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int changeMessage(Email email) {
        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
//                  "`id`          = '" + email.getId()           + "', " +
                    "`direction`   = '" + email.getDirection()    + "', " +
                    "`user_id`     = '" + email.getUser_id()      + "', " +
                    "`client_id`   = '" + email.getClient_id()    + "', " +
                    "`uid`         = '" + email.getUid()          + "', " +
//                  "`message_id`  = '" + email.getMessage_id()   + "', " +
                    "`msgno`       = '" + email.getMsgno()        + "', " +
                    "`from`        = '" + email.getFrom()         + "', " +
                    "`to`          = '" + email.getTo()           + "', " +
                    "`in_reply_to` = '" + email.getIn_replay_to() + "', " +
                    "`references`  = '" + email.getReferences()   + "', " +
                    "`date`        = '" + email.getDate()         + "', " +
                    "`size`        = '" + email.getSize()         + "', " +
                    "`subject`     = '" + email.getSubject()      + "', " +
                    "`folder`      = '" + email.getFolder()       + "', " +
                    "`recent`      = '" + email.getRecent()       + "', " +
                    "`flagged`     = '" + email.getFlagged()      + "', " +
                    "`answered`    = '" + email.getFlagged()      + "', " +
                    "`deleted`     = '" + email.getDeleted()      + "', " +
                    "`seen`        = '" + email.getSeen()         + "', " +
                    "`draft`       = '" + email.getDraft()        + "', " +
                    "`udate`       = '" + email.getUpdate()       + "'  " +
                "WHERE `message_id` = '" + email.getMessage_id()  + "'; ";

        System.out.println(query);

        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public static int getClientIDByAddress(String address) { // TODO client_id from DB a_1c_client_emails/a_ex_client_emails
        String query = "" +
            "SELECT `client_id` " +
            "FROM `a_ex_client_emails` " +
            "WHERE `email` = '" + address + "'" +
            "LIMIT 1;";

        int id = 0;

        try {
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public ArrayList<User> getUsers() {
        String query = "" +
            "SELECT * " +
            "FROM `a_my_users_emails` " +
            "WHERE `is_monitoring` = 1";

        ArrayList<User> users = new ArrayList<>();

        try {
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(
                    new User(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        rs.getBoolean(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12)
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public int changeFolderName(Email email, int user_id, String new_folder_name) { // TODO изменить у сообщений имя папки
        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
                    "`folder` = '" + new_folder_name   + "', " +
                    "`udate`  = '" + email.getUpdate() + "'  " +
                "WHERE" +
                    "`folder`     = '" + user_id               + "' AND " +
                    "`message_id` = '" + email.getMessage_id() + "';";

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // TODO изменение флага сообщенией на удаленное

    private int useQuery(String query) { // TODO не видит запрос
        try {
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    @Override
    public void close() {
        try {
            assert con  != null; if (con  != null) con.close();
            assert stmt != null; if (stmt != null) stmt.close();
            assert rs   != null; if (rs   != null) rs.close();
        } catch(SQLException ignored) { }
    }

    public String arrayToString(String[] arr) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append("&");
            }
        }

        return str.toString();

    }

}
