import javax.mail.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private Statement statement = null;

    private static final String URL = "jdbc:mysql://localhost:8888";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public DB() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL

            if (connection == null) {                       // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            Statement statement = connection.createStatement(); // getting Statement object to execute query
        } catch(SQLException | ClassNotFoundException e){
            System.err.println(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    private void addMessage(Message message) {
        String query = "";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeMessage(Message message) {
        String query = "";

        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
