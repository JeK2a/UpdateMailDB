package com;

import com.classes.Email;
import com.classes.MyMessage;
import com.classes.User;
import com.service.Settings;
import com.sun.mail.imap.IMAPFolder;

import java.sql.*;
import java.util.ArrayList;

public class DB implements AutoCloseable {

    private static final String[] params = {
                                               "useSSL=false",
                                               "useUnicode=true",
                                               "characterEncoding=utf-8"
                                           };
    private static Connection        con;
    private static Statement         stmt;
    private static PreparedStatement prep_stmt;
    private static ResultSet         rs;

    public DB() {
        new Settings();

        String USER     = Settings.getUser();
        String PASSWORD = Settings.getPassword();
        String HOST     = Settings.getHost();
        String PORT     = Settings.getPort();
        String SCHEMA   = Settings.getSchema();
        String URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

        params[0] = "useSSL="            + Settings.getUsessl();
        params[1] = "useUnicode="        + Settings.getUseunicode();
        params[2] = "characterEncoding=" + Settings.getCharacterencoding();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL + "?" + arrayToString(params, "&"), USER, PASSWORD); // JDBC подключение к MySQL
//            System.err.println(URL + "?" + arrayToString(params, "&"));

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query
        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

//    public boolean addEmail(Email email) {
//
//        String query = "" +
//            "INSERT INTO `" + Settings.getTable_emails() + "`(" +
//                "`direction`,"   +
//                "`user_id`,"     +
//                "`client_id`,"   +
//                "`uid`,"         +
//                "`message_id`,"  +
//                "`msgno`,"       +
//                "`from`,"        +
//                "`to`,"          +
//                "`in_reply_to`," +
//                "`references`,"  +
//                "`date`,"        +
//                "`size`,"        +
//                "`subject`,"     +
//                "`folder`,"      +
//                "`recent`,"      +
//                "`flagged`,"     +
//                "`answered`,"    +
//                "`deleted`,"     +
//                "`seen`,"        +
//                "`draft`,"       +
//                "`user`,"       +
//                "`udate`"        +
//            ") "                 +
//            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
//                "ON DUPLICATE KEY UPDATE `message_id` = VALUES(`message_id`);";
//
//        try {
//            prep_stmt = con.prepareStatement(query);
//
//            prep_stmt.setString(1, email.getDirection());
//            prep_stmt.setInt(2, email.getUser_id());
//            prep_stmt.setInt(3, email.getClient_id());
//            prep_stmt.setLong(4, email.getUid());
//            prep_stmt.setString(5, email.getMessage_id());
//            prep_stmt.setInt(6, email.getMsgno());
//            prep_stmt.setString(7, email.getFrom() );
//            prep_stmt.setString(8, email.getTo());
//            prep_stmt.setString(9, email.getIn_replay_to());
//            prep_stmt.setString(10,email.getReferences());
//            prep_stmt.setTimestamp(11, email.getDate());
//            prep_stmt.setInt(12, email.getSize());
//            prep_stmt.setString(13, email.getSubject());
//            prep_stmt.setString(14, email.getFolder());
//            prep_stmt.setInt(15, email.getRecent());
//            prep_stmt.setInt(16, email.getFlagged());
//            prep_stmt.setInt(17, email.getAnswred());
//            prep_stmt.setInt(18, email.getDeleted());
//            prep_stmt.setInt(19, email.getSeen());
//            prep_stmt.setInt(20, email.getDraft());
//            prep_stmt.setInt(21, email.getDraft());
//            prep_stmt.setTimestamp(22, email.getUpdate());
//
//            prep_stmt.executeLargeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }

    public int changeMessage(Email email) {
        int result = 0;

        String query = "" +
            "UPDATE `" + Settings.getTable_emails() + "` " +
            "SET " +
//              "`id`             = '" + email.getId()           + "', " +
                "`direction`      = '" + email.getDirection()    + "', " +
                "`user_id`        = '" + email.getUser_id()      + "', " +
                "`client_id`      = '" + email.getClient_id()    + "', " +
                "`uid`            = '" + email.getUid()          + "', " +
//              "`message_id`     = '" + email.getMessage_id()   + "', " +
                "`msgno`          = '" + email.getMsgno()        + "', " +
                "`from`           = '" + email.getFrom()         + "', " +
                "`to`             = '" + email.getTo()           + "', " +
                "`in_reply_to`    = '" + email.getIn_replay_to() + "', " +
                "`references`     = '" + email.getReferences()   + "', " +
                "`date`           = '" + email.getDate()         + "', " +
                "`size`           = '" + email.getSize()         + "', " +
                "`subject`        = ?                                , " +
                "`folder`         = '" + email.getFolder()       + "', " +
                "`recent`         = '" + email.getRecent()       + "', " +
                "`flagged`        = '" + email.getFlagged()      + "', " +
                "`answered`       = '" + email.getFlagged()      + "', " +
                "`deleted`        = '" + email.getDeleted()      + "', " +
                "`seen`           = '" + email.getSeen()         + "', " +
                "`draft`          = '" + email.getDraft()        + "', " +
                "`user`           = '" + email.getUser()         + "', " +
                "`label1`         = '" + email.getLabel1()       + "', " +
                "`label2`         = '" + email.getLabel2()       + "', " +
                "`label3`         = '" + email.getLabel3()       + "', " +
                "`label4`         = '" + email.getLabel4()       + "', " +
                "`label5`         = '" + email.getLabel5()       + "', " +
                "`has_attachment` = '" + email.getHas_attachment() + "', " +
                "`udate`          = '" + email.getUpdate()       + "'  " +
            "WHERE `message_id`   = '" + email.getMessage_id()   + "'; ";

//        System.out.println(query);

        try {

            prep_stmt = con.prepareStatement(query);
            prep_stmt.setString(1, email.getSubject());
            result = prep_stmt.executeUpdate();
//            return stmt.executeUpdate(query); // old
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

//    public static int getClientIDByAddress(String address) { // TODO client_id from src.java.com.DB a_1c_client_emails/a_ex_client_emails
//        String query = "" +
//            "SELECT `client_id` " +
//            "FROM `a_ex_client_emails` " +
//            "WHERE `email` = '" + address + "'" +
//            "LIMIT 1;";
//
//        int id = 0;
//
//        try {
//            rs = stmt.executeQuery(query);
//
//            if (rs.next()) {
//                id = rs.getInt(1);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return id;
//    }

    public ArrayList<User> getUsers() {

        String query = "" +
            "SELECT " +
                " `id`, "            +
                " `user_id`, "       +
                " `email`, "         +
                " `password`, "      +
                " `is_monitoring`, " +
                " `is_default`, "    +
                " `host`, "          +
                " `port`, "          +
                " `login`, "         +
                " `name_from`, "     +
                " `charset`, "       +
                " `secure`, "        +
                " `success` "        +
            "FROM `" + Settings.getTable_users() + "` " +
            "WHERE " +
                "`is_monitoring` = 1 AND " +
                "`success` = 1";

        ArrayList<User> users = new ArrayList<>();

        try {
            stmt = con.createStatement();
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
                        rs.getString(12),
                        rs.getInt(13)
                    )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public long getLastUID(int user_id, String folder_name) {

        String query = "" +
                "SELECT MAX(`uid`) "            +
                "FROM `" + Settings.getTable_emails() + "` " +
                "WHERE " +
                "    `user_id` = '"+user_id+"' AND " +
                "    `folder` = '"+folder_name+"' ";

        long last_uid = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) { last_uid = rs.getLong(1); }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return last_uid;
    }

    public int getCountMessages(int user_id, String folder_name) {

        String query = "" +
                "SELECT COUNT(`uid`) "            +
                "FROM `" + Settings.getTable_emails() + "` " +
                "WHERE " +
                "    `user_id` = '"+user_id+"' AND " +
                "    `folder` = '"+folder_name+"' ";

        int count_messages = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) { count_messages = rs.getInt(1); }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count_messages;
    }


    public int changeFolderName(Email email, int user_id, String new_folder_name) { // TODO изменить у сообщений имя папки (проверить)
        String query = "" +
                "UPDATE `" + Settings.getTable_emails() + "` " +
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

    public String arrayToString(String[] arr, String symbol) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append(symbol);
            }
        }

        return str.toString();

    }

    public int changeDeleteFlag(Email email, IMAPFolder imapFolder) { // TODO изменение флага сообщенией на удаленное (проверить)
        String query = "" +
                "UPDATE `" + Settings.getTable_emails() + "` " +
                "SET " +
                    "`deleted` = 1, " +
                    "`udate`  = '" + email.getUpdate() + "'  " +
                "WHERE" +
                    "`user_id`     = '" + email.getUser_id() + "' AND " +
                    "`folder` = '" + imapFolder.getFullName() + "';";

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int setFlags() {
        return this.setFlags(0, null);
    }

    public int setFlags(int user_id) {
        return this.setFlags(user_id, null);
    }

    public int setFlags(int user_id, String folder_name) {
        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
                "    `recent`   = 0, " +
                "    `flagged`  = 0, " +
                "    `answered` = 0, " +
                "    `deleted`  = 0, " +
                "    `seen`     = 1, " +
                "    `draft`    = 0, " +
                "    `user`     = 0, " +
                "    `label1`   = 0, " +
                "    `label2`   = 0, " +
                "    `label3`   = 0, " +
                "    `label4`   = 0, " +
                "    `label5`   = 0, " +
                "    `has_attachment` = 0 " +
                "WHERE TRUE ";
        if (user_id != 0) {
            query += String.format(" AND `user_id` = '%d' ", user_id);
        }
        if (folder_name != null) {
            query += String.format(" AND `folder` = '%s' ", folder_name);
        }

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int setFlags(int user_id, String folder_name, String flag_name, byte flag_value) {
        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET `"+flag_name+"` = "+flag_value+" " +
                "WHERE  " +
                "   `user_id` = '"+user_id+"' AND" +
                "   `folder` = '"+flag_name+"';";

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int setFlags(int user_id, String folder_name, String flag_name, int flag_value, String uids) {
        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET `"+flag_name+"` = "+flag_value+" " +
                "WHERE  " +
                "   `user_id` = '"+user_id+"' AND" +
                "   `folder` = '"+flag_name+"' AND " +
                "   `uid` IN ("+uids+");";

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<MyMessage> getRandomMessages(int user_id, String folder_name, int count) {

        String query = "" +
                "SELECT" +
                "    `id`, "          +
                "    `direction`, "   +
                "    `user_id`, "     +
                "    `client_id`, "   +
                "    `uid`,"          +
                "    `message_id`, "  +
                "    `msgno`, "       +
                "    `from`, "        +
                "    `to`, "          +
                "    `in_reply_to`, " +
                "    `references`, "  +
                "    `date`, "        +
                "    `size`, "        +
                "    `subject`, "     +
                "    `folder`, "      +
                "    `recent`, "      +
                "    `flagged`, "     +
                "    `answered`, "    +
                "    `deleted`, "     +
                "    `seen`, "        +
                "    `draft`, "       +
                "    `user`, "        +
                "    `label1`, "      +
                "    `label2`, "      +
                "    `label3`, "      +
                "    `label4`, "      +
                "    `label5`, "      +
                "    `label1`, "      +
                "    `has_attachment` " +
                "FROM `a_my_emails` " +
                "WHERE " +
                "    `user_id` = '"+user_id +"' AND " +
                "    `folder` = '"+folder_name+"' " +
                "ORDER BY RAND() LIMIT "+count+";";

        ArrayList<MyMessage> myMessages = new ArrayList<>();

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                myMessages.add(
                        new MyMessage(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(4),
                                rs.getLong(5),
                                rs.getString(6),
                                rs.getInt(7),
                                rs.getString(8),
                                rs.getString(9),
                                rs.getString(10),
                                rs.getString(11),
                                rs.getTimestamp(12),
                                rs.getInt(13),
                                rs.getString(14),
                                rs.getString(15),
                                rs.getInt(16),
                                rs.getInt(17),
                                rs.getInt(18),
                                rs.getInt(19),
                                rs.getInt(20),
                                rs.getInt(21),
                                rs.getInt(22),

                                rs.getInt(23),
                                rs.getInt(24),
                                rs.getInt(25),
                                rs.getInt(26),
                                rs.getInt(27),
                                rs.getInt(28),

                                rs.getTimestamp(29)
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return myMessages;
    }

    public int checkDelete(int user_id, String folder_name, int uid_start, int uid_end, String uids) {
        String query = "" +
                "DELETE `a_my_emails` " +

                "WHERE  " +
                "   `uid` >= '"+uid_start+"' AND '"+uid_start+"' <= `uid`  AND" +
                "   `folder` = '"+folder_name+"' AND `user_id` = '"+user_id+"' AND " +
                "   `uid` NOT IN ("+uids+");";

//        int result = 0;
//
//        try {
//            result = stmt.executeUpdate(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return 1;
    }



}
