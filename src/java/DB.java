import javax.mail.*;
import java.sql.*;
import java.util.ArrayList;

public class DB implements AutoCloseable {
    private static final String URL      = "jdbc:mysql://localhost:8889/test";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";

    private static Connection con;
    private static Statement  stmt;
    private static ResultSet  rs;

    public DB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query

        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    public void addEmail(Email email) {
        try {
            String query = "" +
                "INSERT INTO `a_my_emails`(" +
                    " `direction`," +
                    " `user_id`," +
                    " `client_id`," +
                    " `uid`," +
                    " `message_id`," +
                    " `msgno`," +
                    " `from`," +
                    " `to`," +
                    " `in_reply_to`," +
                    " `references`," +
                    " `date`," +
                    " `size`," +
                    " `subject`," +
                    " `folder`," +
                    " `recent`," +
                    " `flagged`," +
                    " `answered`," +
                    " `deleted`," +
                    " `seen`," +
                    " `draft`," +
                    " `udate`)" +
                " VALUES (" +
                     "\"" + email.getDirection() + "\"" +
                    ", " + "\"" + email.getUser_id() + "\"" +
                    ", " + "\"" + email.getClient_id() + "\"" +
                    ", " + "\"" + email.getUid() + "\"" +
                    ", " + "\"" + email.getMessage_id() + "\"" +
                    ", " + "\"" + email.getMsgno() + "\"" +
                    ", " + "\"" + email.getFrom() + "\"" +
                    ", " + "\"" + email.getTo() + "\"" +
                    ", " + "\"" + email.getIn_replay_to() + "\"" +
                    ", " + "\"" + email.getReferences() + "\"" +
                    ", " + "\"" + email.getDate() + "\"" +
                    ", " + "\"" + email.getSize() + "\"" +
                    ", " + "\"" + email.getSubject() + "\"" +
                    ", " + "\"" + email.getFolder() + "\"" +
                    ", " + "\"" + email.getRecent() + "\"" +
                    ", " + "\"" + email.getFlagged() + "\"" +
                    ", " + "\"" + email.getAnswred() + "\"" +
                    ", " + "\"" + email.getDeleted() + "\"" +
                    ", " + "\"" + email.getSeen() + "\"" +
                    ", " + "\"" + email.getDraft() + "\"" +
                    ", " + "\"" + email.getUpdate() + "\"" +
                    ")";
            System.out.println(query);
            stmt.executeUpdate(query); // Update(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeMessage(Email email) { // TODO

    }

    public ArrayList<User> getUsers() {
        String query = "" +
                "SELECT * " +
                "FROM a_my_users_emails " +
                "WHERE is_monitoring AND is_default";

        ArrayList<User> users = new ArrayList<User>();


        try {
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(new User(
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
                    rs.getString(12
                )));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    @Override
    public void close() throws Exception {
        try {
            assert con  != null; if (con  != null) con.close();
            assert stmt != null; if (stmt != null) stmt.close();
            assert rs   != null; if (rs   != null) rs.close();
        } catch(SQLException ignored) { }
    }
}
