import javax.activation.DataHandler;
import javax.mail.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Date;
import java.util.Enumeration;

public class DB {
    private static final String URL      = "jdbc:mysql://localhost:8889/test";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public DB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL

            if (con == null) {                       // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query

//            String query = "SELECT * FROM message";
//            rs = stmt.executeQuery(query);

            Message message = getMessage();

            for (int i = 0; i < 5000; i++) {
                addMessage(message);
                System.out.println(i);
            }

//            while (rs.next()) {
//                int count = rs.getInt(1);
//                System.out.println(count);
//            }

        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
        } finally {
            try {
                assert con  != null; if (con != null)  con.close();
                assert stmt != null; if (stmt != null) stmt.close();
                assert rs   != null; if (rs != null)   rs.close();
            } catch(SQLException ignored) {  }
        }
    }

    private Message getMessage() {
        return new Message() {
            @Override
            public Address[] getFrom() throws MessagingException {
                return new Address[0];
            }

            @Override
            public void setFrom() throws MessagingException {

            }

            @Override
            public void setFrom(Address address) throws MessagingException {

            }

            @Override
            public void addFrom(Address[] addresses) throws MessagingException {

            }

            @Override
            public Address[] getRecipients(RecipientType recipientType) throws MessagingException {
                return new Address[0];
            }

            @Override
            public void setRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {

            }

            @Override
            public void addRecipients(RecipientType recipientType, Address[] addresses) throws MessagingException {

            }

            @Override
            public String getSubject() throws MessagingException {
                return "Test Subject!!!";
            }

            @Override
            public void setSubject(String s) throws MessagingException {

            }

            @Override
            public Date getSentDate() throws MessagingException {
                return null;
            }

            @Override
            public void setSentDate(Date date) throws MessagingException {

            }

            @Override
            public Date getReceivedDate() throws MessagingException {
                return null;
            }

            @Override
            public Flags getFlags() throws MessagingException {
                return null;
            }

            @Override
            public void setFlags(Flags flags, boolean b) throws MessagingException {

            }

            @Override
            public Message reply(boolean b) throws MessagingException {
                return null;
            }

            @Override
            public void saveChanges() throws MessagingException {

            }

            @Override
            public int getSize() throws MessagingException {
                return 0;
            }

            @Override
            public int getLineCount() throws MessagingException {
                return 0;
            }

            @Override
            public String getContentType() throws MessagingException {
                return null;
            }

            @Override
            public boolean isMimeType(String s) throws MessagingException {
                return false;
            }

            @Override
            public String getDisposition() throws MessagingException {
                return null;
            }

            @Override
            public void setDisposition(String s) throws MessagingException {

            }

            @Override
            public String getDescription() throws MessagingException {
                return null;
            }

            @Override
            public void setDescription(String s) throws MessagingException {

            }

            @Override
            public String getFileName() throws MessagingException {
                return null;
            }

            @Override
            public void setFileName(String s) throws MessagingException {

            }

            @Override
            public InputStream getInputStream() throws IOException, MessagingException {
                return null;
            }

            @Override
            public DataHandler getDataHandler() throws MessagingException {
                return null;
            }

            @Override
            public Object getContent() throws IOException, MessagingException {
                return null;
            }

            @Override
            public void setDataHandler(DataHandler dataHandler) throws MessagingException {

            }

            @Override
            public void setContent(Object o, String s) throws MessagingException {

            }

            @Override
            public void setText(String s) throws MessagingException {

            }

            @Override
            public void setContent(Multipart multipart) throws MessagingException {

            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException, MessagingException {

            }

            @Override
            public String[] getHeader(String s) throws MessagingException {
                return new String[0];
            }

            @Override
            public void setHeader(String s, String s1) throws MessagingException {

            }

            @Override
            public void addHeader(String s, String s1) throws MessagingException {

            }

            @Override
            public void removeHeader(String s) throws MessagingException {

            }

            @Override
            public Enumeration getAllHeaders() throws MessagingException {
                return null;
            }

            @Override
            public Enumeration getMatchingHeaders(String[] strings) throws MessagingException {
                return null;
            }

            @Override
            public Enumeration getNonMatchingHeaders(String[] strings) throws MessagingException {
                return null;
            }
        };
    }

    private void addMessage(Message message) {
        try {
            String subject = message.getSubject();

            String query = "INSERT INTO test.`message` (`subject`) VALUES('"+ subject +"');";

            stmt.executeUpdate(query);
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
        }
    }

    private void changeMessage(Message message) {
        String query = "";

        try {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
